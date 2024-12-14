package vn.edu.stu.bannhanong.dao;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.model.LoaiSp;
import vn.edu.stu.bannhanong.model.Sanpham;
import vn.edu.stu.bannhanong.model.Users;

public class DBHelperSanPham {
    private FirebaseFirestore db;

    public DBHelperSanPham() {
        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
    }

//    // Lấy sản phẩm theo userId
//    public void getSanPhamByUser(int userId, final FirestoreCallback callback) {
//        db.collection("sanpham")
//                .whereEqualTo("iduser", userId)  // Lọc theo iduser
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<Sanpham> sanphamList = new ArrayList<>();
//                        QuerySnapshot querySnapshot = task.getResult();
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            Sanpham sanpham = document.toObject(Sanpham.class);
//                            // Sau khi lấy thông tin sản phẩm, ta cần lấy thêm thông tin loại sản phẩm và người dùng.
//                            getLoaiSpAndUser(sanpham, callback, sanphamList);
//                        }
//                    } else {
//                        callback.onCallback(null);  // Trường hợp truy vấn thất bại
//                    }
//                });
//    }

    // Lấy loại sản phẩm và thông tin người dùng
//    private void getLoaiSpAndUser(Sanpham sanpham, final FirestoreCallback callback, List<Sanpham> sanphamList) {
//        // Lấy thông tin loại sản phẩm
//        db.collection("loaisanpham")
//                .document(String.valueOf(sanpham.getLoaiSp().getMaloai()))
//                .get()
//                .addOnSuccessListener(document -> {
//                    if (document.exists()) {
//                        LoaiSp loaiSp = document.toObject(LoaiSp.class);
//                        sanpham.setLoaiSp(loaiSp);
//
//                        // Lấy thông tin người dùng
//                        db.collection("users")
//                                .document(String.valueOf(sanpham.getMauser().getId()))
//                                .get()
//                                .addOnSuccessListener(userDoc -> {
//                                    if (userDoc.exists()) {
//                                        Users user = userDoc.toObject(Users.class);
//                                        sanpham.setMauser(user);
//                                        sanphamList.add(sanpham);
//                                        if (sanphamList.size() == 10) {
//                                            callback.onCallback(sanphamList);
//                                        }
//                                    }
//                                });
//                    }
//                });
//    }
//
//    // Lấy sản phẩm theo ID
//    public void getSanPhamById(int id, final FirestoreCallback callback) {
//        db.collection("sanpham")
//                .document(String.valueOf(id))
//                .get()
//                .addOnSuccessListener(document -> {
//                    if (document.exists()) {
//                        Sanpham sanpham = document.toObject(Sanpham.class);
//                        // Lấy thông tin loại sản phẩm và người dùng
//                        getLoaiSpAndUser(sanpham, callback, new ArrayList<>());
//                    } else {
//                        callback.onCallback(null);  // Trường hợp không tìm thấy sản phẩm
//                    }
//                });
//    }

    // Callback interface để trả về kết quả bất đồng bộ
    public interface FirestoreCallback {
        void onCallback(List<Sanpham> sanphamList);
    }
}
