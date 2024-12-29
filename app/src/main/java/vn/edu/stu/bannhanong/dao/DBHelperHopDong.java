package vn.edu.stu.bannhanong.dao;


import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class DBHelperHopDong {
    private FirebaseFirestore db;

    public DBHelperHopDong() {
        db = FirebaseFirestore.getInstance();
    }
    public void getTenNongdan(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("users")
                .whereEqualTo("maloai", 2)
                .get()
                .addOnCompleteListener(listener);
    }
    public void getSanPhamByUser(String userID, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("sanpham") // Tên collection chứa sản phẩm
                .whereEqualTo("iduser", userID) // Điều kiện: sản phẩm thuộc về nông dân này
                .get()
                .addOnCompleteListener(listener);
    }

    public void getUserName(String userID, TextView tvTenND) {
        db.collection("users") // Tên collection chứa thông tin user
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String ten = documentSnapshot.getString("tenuser");
                        tvTenND.setText(ten);
                    } else {
                        tvTenND.setText("null");
                    }
                })
                .addOnFailureListener(e -> {
                    tvTenND.setText("khong tai duoc");
                });
    }

}
