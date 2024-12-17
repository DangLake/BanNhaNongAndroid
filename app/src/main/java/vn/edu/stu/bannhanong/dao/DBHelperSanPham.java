package vn.edu.stu.bannhanong.dao;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.cloudinary.CloudinaryManager;
import vn.edu.stu.bannhanong.model.LoaiSp;
import vn.edu.stu.bannhanong.model.Sanpham;
import vn.edu.stu.bannhanong.model.Users;

public class DBHelperSanPham {
    private Context context;

    public DBHelperSanPham(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }
    private FirebaseFirestore db;

    public DBHelperSanPham() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface callback cho danh sách sản phẩm
    public interface FirestoreCallback {
        void onCallback(List<Sanpham> sanphamList);  // Callback với danh sách sản phẩm
        void onFailure(Exception e);  // Callback lỗi
    }

    // Interface callback cho ID loại sản phẩm
    public interface LoaiSpCallback {
        void onSuccess(int idLoai); // Callback trả về ID loại sản phẩm
        void onFailure(Exception e); // Callback lỗi
    }

    // Lấy sản phẩm của người dùng theo ID
    public void getSanphamByUserID(String documentuserID, FirestoreCallback callback) {
        db.collection("sanpham")
                .whereEqualTo("iduser", documentuserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Sanpham> sanphamList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Sanpham sanpham = documentSnapshot.toObject(Sanpham.class);
                            Log.d("Sanpham", "Sản phẩm: " + sanpham.getTensp() + ", Giá: " + sanpham.getGia() + ", Ảnh: " + sanpham.getAnh() + ", DVT: " + sanpham.getDonvitinh());
                            sanphamList.add(sanpham);
                        }
                        callback.onCallback(sanphamList);
                    } else {
                        callback.onFailure(new Exception("Không có sản phẩm nào cho người dùng này"));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void addSanphamWithImage(Sanpham sanpham, List<String> imageUrls, FirestoreCallback callback) {
        List<String> uploadedUrls = new ArrayList<>();

        for (String imageUrl : imageUrls) {
            // Upload hình ảnh với đường dẫn từ Cloudinary (sử dụng URL trực tiếp nếu đã có URL)
            CloudinaryManager.uploadImage(imageUrl, new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d("CLOUDINARY", "Upload started");
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String secureUrl = resultData.get("secure_url").toString();
                    uploadedUrls.add(secureUrl);

                    // Log đường dẫn ảnh từ Cloudinary
                    Log.d("CLOUDINARY", "Đường dẫn ảnh từ Cloudinary: " + secureUrl);

                    // Kiểm tra nếu tất cả ảnh đã được upload
                    if (uploadedUrls.size() == imageUrls.size()) {
                        sanpham.setAnh(uploadedUrls);  // Gán danh sách URL đã upload vào sanpham
                        saveSanphamToFirestore(sanpham, callback);  // Lưu sản phẩm vào Firestore
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    callback.onFailure(new Exception("Upload error: " + error.getDescription()));
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    callback.onFailure(new Exception("Upload rescheduled: " + error.getDescription()));
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d("CLOUDINARY", "Uploading... " + bytes + "/" + totalBytes);
                }
            });
        }
    }

    public void saveSanphamToFirestore(Sanpham sanpham, FirestoreCallback callback) {
        // Thêm sản phẩm vào Firestore và lấy documentID tự động
        db.collection("sanpham")
                .add(sanpham)
                .addOnSuccessListener(documentReference -> {
                    List<Sanpham> sanphamList = new ArrayList<>();
                    sanphamList.add(sanpham);
                    callback.onCallback(sanphamList); // Gọi callback với danh sách sản phẩm
                })
                .addOnFailureListener(e -> callback.onFailure(e)); // Nếu thất bại, gọi callback với lỗi
    }

    // Lấy ID loại sản phẩm từ tên loại
    public void getIdLoaiSpByName(String tenLoai, final LoaiSpCallback callback) {
        db.collection("loaisanpham")
                .whereEqualTo("tenloaisp", tenLoai) // Tìm theo tên loại sản phẩm
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0); // Lấy kết quả đầu tiên
                        int maloai = document.getLong("id").intValue(); // Lấy ID loại sản phẩm
                        callback.onSuccess(maloai);
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy loại sản phẩm"));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e));
    }
}
