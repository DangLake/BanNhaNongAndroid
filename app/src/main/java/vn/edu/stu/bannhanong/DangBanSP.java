package vn.edu.stu.bannhanong;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.adapter.ImageAdapter;
import vn.edu.stu.bannhanong.dao.DBHelperSanPham;
import vn.edu.stu.bannhanong.model.LoaiSp;
import vn.edu.stu.bannhanong.model.Sanpham;

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

        // Lấy ID loại sản phẩm từ DBHelperSanPham
        dbHelperSanPham.getIdLoaiSpByName(tenLoai, new DBHelperSanPham.LoaiSpCallback() {
            @Override
            public void onSuccess(int idLoai) {
                // Nếu có ID loại sản phẩm, tải ảnh và lưu sản phẩm
                for (int i = 0; i < soluong; i++) {
                    Uri imageUri = imageList.get(i);
                    uploadImageToStorage(imageUri, new OnImageUploadCompleteListener() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            uploadImg.add(imageUrl);
                            if (uploadImg.size() == soluong) {
                                // Sau khi tải lên tất cả ảnh, tạo đối tượng sản phẩm và lưu
                                Sanpham sanpham = new Sanpham();
                                sanpham.setTensp(tensp);
                                sanpham.setMota(mota);
                                sanpham.setGia(gia);
                                sanpham.setDonvitinh(donvitinh);
                                sanpham.setAnh(uploadImg);  // Lưu danh sách URL ảnh
                                sanpham.setIdloaisp(idLoai);  // Dùng ID loại sản phẩm
                                sanpham.setMaDocumentuser(documentId);
                                dbHelperSanPham.addSanphamWithImage(sanpham, uploadImg, new DBHelperSanPham.FirestoreCallback() {
                                    @Override
                                    public void onCallback(List<Sanpham> sanphamList) {
                                        // Xử lý khi thêm sản phẩm thành công
                                        Toast.makeText(DangBanSP.this, "Sản phẩm đã được lưu!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý khi có lỗi
                                        Toast.makeText(DangBanSP.this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onUploadFailure(Exception e) {
                            // Xử lý khi tải ảnh thất bại
                            Toast.makeText(DangBanSP.this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
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
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("anh");
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("Upload Image", "Image uploaded successfully: " + uri.toString());
                        listener.onUploadSuccess(uri.toString());
                    }).addOnFailureListener(e -> {
                        Log.e("Upload Image", "Error getting download URL: " + e.getMessage());
                        listener.onUploadFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Upload Image", "Error uploading image: " + e.getMessage());
                    listener.onUploadFailure(e);
                });
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