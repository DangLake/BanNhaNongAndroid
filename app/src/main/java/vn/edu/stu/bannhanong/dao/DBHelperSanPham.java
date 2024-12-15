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
        db = FirebaseFirestore.getInstance();
    }
    public interface FirestoreCallback {
        void onCallback(List<Sanpham> sanphamList);
        void onCallback(Sanpham sanpham);
        void onFailure(Exception e);
    }
    public void getSanphamByDocumentID(String documentID, FirestoreCallback callback) {
        db.collection("sanpham") // Tên collection trong Firestore
                .document(documentID)  // Truy cập document theo ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chuyển đổi dữ liệu Firestore thành đối tượng Sanpham
                        Sanpham sanpham = documentSnapshot.toObject(Sanpham.class);
                        sanpham.setDocumentID(documentSnapshot.getId());
                        callback.onCallback(sanpham);
                    } else {
                        callback.onFailure(new Exception("Document không tồn tại"));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e));
    }


}
