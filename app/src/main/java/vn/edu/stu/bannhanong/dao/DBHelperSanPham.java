package vn.edu.stu.bannhanong.dao;
import android.content.Context;
import android.util.Log;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
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
                            sanpham.setDocumentId(documentSnapshot.getId());
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
        db.collection("sanpham")
                .add(sanpham)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    sanpham.setDocumentId(documentId); // Gán documentId
                    List<Sanpham> sanphamList = new ArrayList<>();
                    sanphamList.add(sanpham);
                    callback.onCallback(sanphamList); // Trả về danh sách sản phẩm đã lưu
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lưu sản phẩm: " + e.getMessage());
                    callback.onFailure(e); // Nếu thất bại, trả về callback lỗi
                });
    }
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
    public void deleteSanpham(String documentId, FirestoreCallback callback) {
        // Lấy sản phẩm từ Firestore trước khi xóa
        db.collection("sanpham")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Sanpham sanpham = documentSnapshot.toObject(Sanpham.class);
                        if (sanpham != null && sanpham.getAnh() != null) {
                            // Xóa ảnh trên Cloudinary nếu có URL ảnh
                            for (String imageUrl : sanpham.getAnh()) {
                                String publicId = extractPublicIdFromUrl(imageUrl);
                                if (publicId != null) {
                                    Log.d("PublicID", "Publicanh " + publicId);
                                    CloudinaryManager.deleteImage(publicId, new CloudinaryManager.CloudinaryCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("Cloudinary", "Xóa ảnh thành công: " + imageUrl);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e("Cloudinary", "Lỗi khi xóa ảnh: " + error);
                                        }
                                    });
                                }
                            }
                        }

                        // Xóa sản phẩm trong Firestore
                        db.collection("sanpham")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Xóa sản phẩm thành công với documentId: " + documentId);
                                    callback.onCallback(Collections.emptyList()); // Trả về danh sách trống
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Lỗi khi xóa sản phẩm: " + e.getMessage());
                                    callback.onFailure(e); // Trả về callback lỗi
                                });
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy sản phẩm để xóa"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy sản phẩm từ Firestore: " + e.getMessage());
                    callback.onFailure(e);
                });
    }
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Kiểm tra xem URL có chứa /upload/ không
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                // Lấy phần trước dấu . là public ID
                String[] publicIdParts = parts[1].split("\\."); // Chia phần sau /upload/ tại dấu .
                String publicId = publicIdParts[0];  // Lấy phần trước dấu chấm

                // Tiếp tục tách phần publicId nếu có dấu '/' (để lấy phần sau dấu '/')
                String[] publicIdSubParts = publicId.split("/");
                return publicIdSubParts[1];  // Lấy phần sau dấu '/'
            } else {
                Log.e("Cloudinary", "URL không hợp lệ: " + imageUrl);
            }
        } catch (Exception e) {
            Log.e("Cloudinary", "Lỗi khi trích xuất public ID từ URL: " + imageUrl);
        }
        return null;  // Nếu không trích xuất được, trả về null
    }


}
