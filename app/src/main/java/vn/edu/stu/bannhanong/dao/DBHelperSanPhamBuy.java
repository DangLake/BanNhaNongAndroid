package vn.edu.stu.bannhanong.dao;

import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.model.Sanpham;

public class DBHelperSanPhamBuy {
    private FirebaseFirestore db;
    public DBHelperSanPhamBuy() {
        db = FirebaseFirestore.getInstance();
    }

    public void getUserAddress(String userID, TextView tvDiachi) {
        db.collection("users") // Tên collection chứa thông tin user
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String diachi = documentSnapshot.getString("tinh");
                        tvDiachi.setText("Địa chỉ: " + diachi);
                    } else {
                        tvDiachi.setText("Địa chỉ: Không có thông tin");
                    }
                })
                .addOnFailureListener(e -> {
                    tvDiachi.setText("Địa chỉ: Không tải được");
                });
    }
    public void getAllProducts(ProductCallback callback) {
        db.collection("sanpham") // Tên collection chứa thông tin sản phẩm
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Sanpham> productList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Map document fields to Sanpham object
                        Sanpham product = document.toObject(Sanpham.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    callback.onSuccess(productList); // Trả về danh sách sản phẩm qua callback
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e); // Trả về lỗi qua callback
                });
    }

    public interface ProductCallback {
        void onSuccess(List<Sanpham> productList); // Khi dữ liệu được tải thành công
        void onFailure(Exception e);              // Khi có lỗi xảy ra
    }

}
