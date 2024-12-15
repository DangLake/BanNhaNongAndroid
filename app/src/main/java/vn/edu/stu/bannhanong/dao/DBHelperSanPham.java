package vn.edu.stu.bannhanong.dao;
import android.util.Log;

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
        db = FirebaseFirestore.getInstance();
    }
    public interface FirestoreCallback {
        void onCallback(List<Sanpham> sanphamList);  // Callback với danh sách sản phẩm
        void onFailure(Exception e);  // Callback lỗi
    }

    public void getSanphamByUserID(String documentuserID, FirestoreCallback callback) {
        db.collection("sanpham")
                .whereEqualTo("iduser", documentuserID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Sanpham> sanphamList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Sanpham sanpham = documentSnapshot.toObject(Sanpham.class);
                            sanpham.setDocumentID(documentSnapshot.getId());

                            // Log dữ liệu để kiểm tra
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


}
