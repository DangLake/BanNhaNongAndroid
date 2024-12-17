package vn.edu.stu.bannhanong;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.adapter.ImageAdapter;
import vn.edu.stu.bannhanong.cloudinary.CloudinaryManager;
import vn.edu.stu.bannhanong.dao.DBHelperSanPham;
import vn.edu.stu.bannhanong.model.LoaiSp;
import vn.edu.stu.bannhanong.model.Sanpham;
import vn.edu.stu.bannhanong.util.FileUtils;

public class DangBanSP extends AppCompatActivity {
    EditText edtTen, edtGia, edtDVT, edtMota;
    Button btnLuu, btnThoat, btnChonanh;
    ArrayList<LoaiSp> dsLoai;
    ArrayAdapter<String> adapter;
    MaterialAutoCompleteTextView inputLoai;
    TextInputLayout inputLayout;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imageList;
    private FirebaseFirestore db;
    int requescode = 1;
    DBHelperSanPham dbHelperSanPham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ban_sp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControls();
        addEvents();
        loadLoaiSanpham();
    }
    private void loadLoaiSanpham() {
        db = FirebaseFirestore.getInstance();
        dsLoai.clear(); // Xóa danh sách cũ trước khi tải mới

        db.collection("loaisanpham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int maloai = document.getLong("id").intValue();
                            String tenloai = document.getString("tenloaisp");
                            dsLoai.add(new LoaiSp(maloai, tenloai));
                        }

                        // Tạo danh sách tên loại để đưa vào Adapter
                        List<String> tenLoaiList = new ArrayList<>();
                        for (LoaiSp loaiSp : dsLoai) {
                            tenLoaiList.add(loaiSp.getTenloai());
                        }

                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tenLoaiList);
                        inputLoai.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void addEvents() {
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnChonanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyLuu();
            }
        });
    }

    private void xulyLuu() {
        String tensp = edtTen.getText().toString().trim();
        String giastr = edtGia.getText().toString().trim();
        String donvitinh = edtDVT.getText().toString().trim();
        String mota = edtMota.getText().toString().trim();
        String tenLoai = inputLoai.getText().toString();
        if (tensp.isEmpty() || giastr.isEmpty() || donvitinh.isEmpty() || mota.isEmpty() || tenLoai.isEmpty() || imageList.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        double gia = Double.parseDouble(giastr);
        uploadImagesAndSaveProduct(tensp, mota, gia, donvitinh, tenLoai);
    }

    private void uploadImagesAndSaveProduct(String tensp, String mota, double gia, String donvitinh, String tenLoai) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String documentId = sharedPreferences.getString("documentID", "");

        List<String> uploadImg = new ArrayList<>();
        int soluong = imageList.size();
        if (soluong == 0) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelperSanPham.getIdLoaiSpByName(tenLoai, new DBHelperSanPham.LoaiSpCallback() {
            @Override
            public void onSuccess(int idLoai) {
                final int[] uploadedImagesCount = {0}; // Biến đếm số ảnh đã tải lên

                for (int i = 0; i < soluong; i++) {
                    Uri imageUri = imageList.get(i);
                    uploadImageToStorage(imageUri, new OnImageUploadCompleteListener() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            uploadImg.add(imageUrl);
                            uploadedImagesCount[0]++; // Tăng số ảnh đã tải lên

                            // Log đường dẫn ảnh khi upload thành công
                            Log.d("Upload", "Đường dẫn ảnh upload thành công: " + imageUrl);

                            // Kiểm tra nếu tất cả ảnh đã được tải lên
                            if (uploadedImagesCount[0] == soluong) {
                                // Log danh sách đường dẫn ảnh đã tải lên
                                Log.d("Upload", "Danh sách đường dẫn ảnh: " + uploadImg);

                                // Khi tất cả ảnh đã được tải lên, tạo đối tượng Sanpham
                                Sanpham sanpham = new Sanpham();
                                sanpham.setTensp(tensp);
                                sanpham.setMota(mota);
                                sanpham.setGia(gia);
                                sanpham.setDonvitinh(donvitinh);
                                sanpham.setAnh(uploadImg);  // Lưu danh sách URL ảnh
                                sanpham.setIdloaisp(idLoai);  // Dùng ID loại sản phẩm
                                sanpham.setIduser(documentId);

                                // Lưu sản phẩm vào Firestore
                                dbHelperSanPham.saveSanphamToFirestore(sanpham, new DBHelperSanPham.FirestoreCallback() {
                                    @Override
                                    public void onCallback(List<Sanpham> sanphamList) {
                                        // Xử lý khi thêm sản phẩm thành công
                                        Toast.makeText(DangBanSP.this, "Sản phẩm đã được lưu!", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý khi có lỗi
                                        Toast.makeText(DangBanSP.this, "Lỗi khi lưu sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onUploadFailure(Exception e) {
                            // Log lỗi nếu tải ảnh lên thất bại
                            Log.e("Upload", "Lỗi khi tải ảnh lên: " + e.getMessage());
                            Toast.makeText(DangBanSP.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Xử lý khi không tìm thấy loại sản phẩm
                Toast.makeText(DangBanSP.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void uploadImageToStorage(Uri imageUri, final OnImageUploadCompleteListener listener) {
        String filePath = getRealPathFromUri(this, imageUri);
        if (filePath == null) {
            listener.onUploadFailure(new Exception("Không thể lấy đường dẫn tệp"));
            return;
        }

        Log.d("Upload", "Đường dẫn tệp: " + filePath);

        // Cấu hình Cloudinary
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "duthhwipq");
        config.put("api_key", "721938926416681");
        config.put("api_secret", "ozvYw0n11KYF9LLGs4pb1muHhsI");
        Cloudinary cloudinary = new Cloudinary(config);

        new Thread(() -> {
            try {
                Map result = cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap());
                Log.d("Upload", "Kết quả upload: " + result);

                String imageUrl = (String) result.get("secure_url");
                if (imageUrl != null) {
                    runOnUiThread(() -> listener.onUploadSuccess(imageUrl));
                } else {
                    runOnUiThread(() -> listener.onUploadFailure(new Exception("Không tìm thấy secure_url")));
                }
            } catch (Exception e) {
                runOnUiThread(() -> listener.onUploadFailure(e));
                Log.e("Upload", "Lỗi khi tải ảnh lên: ", e);
            }
        }).start();
    }
    public String getRealPathFromUri(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        return result;
    }


    public interface OnImageUploadCompleteListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e);
    }



    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 trở lên
            if (Environment.isExternalStorageManager()) {
                openImagePicker();
            } else {
                try {
                    // Yêu cầu quyền truy cập tất cả file
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Không thể mở cài đặt quyền. Hãy cấp quyền thủ công.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            // Android 10 trở xuống
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                // Yêu cầu quyền nếu chưa có
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA},
                        1);
            } else {
                openImagePicker();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều ảnh
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), requescode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requescode && resultCode == RESULT_OK) {
            if (data != null) {
                // Nếu người dùng chọn nhiều ảnh
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageList.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageList.add(imageUri); // Thêm URI vào danh sách
                }

                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addControls() {
        edtTen = findViewById(R.id.edtTen);
        edtGia = findViewById(R.id.edtGia);
        edtDVT = findViewById(R.id.edtDVT);
        edtMota = findViewById(R.id.edtMota);
        btnLuu = findViewById(R.id.btnLuu);
        btnThoat = findViewById(R.id.btnThoat);
        btnChonanh = findViewById(R.id.btnChonanh);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);
        inputLayout = findViewById(R.id.inputLayout);
        inputLoai = findViewById(R.id.inputLoai);
        dsLoai = new ArrayList<>();
        dbHelperSanPham=new DBHelperSanPham();
    }
}