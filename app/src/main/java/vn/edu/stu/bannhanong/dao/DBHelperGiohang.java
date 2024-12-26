package vn.edu.stu.bannhanong.dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.model.GiohangItem;
import vn.edu.stu.bannhanong.model.GiohangNongdan;

public class DBHelperGiohang {
    private FirebaseFirestore db;

    public DBHelperGiohang() {
        db = FirebaseFirestore.getInstance();
    }

    public void getProductsInCart(String userId, final OnGetCartProductsListener listener) {
        DocumentReference cartRef = db.collection("giohang").document(userId);
        cartRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<GiohangItem> cartItems = new ArrayList<>();
                        List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("items");
                        if (products != null) {
                            for (Map<String, Object> productData : products) {
                                String productId = (String) productData.get("documentIdSanpham");
                                int quantity = ((Long) productData.get("soluong")).intValue();
                                GiohangItem cartItem = new GiohangItem();
                                cartItem.setDocumentIdSanpham(productId);
                                cartItem.setSoluong(quantity);
                                cartItems.add(cartItem);
                            }
                        }

                        // Lấy tên nông dân (user) qua documentIdSanpham
                        Map<String, List<GiohangItem>> groupedByFarmer = new HashMap<>();
                        for (GiohangItem item : cartItems) {
                            // Truy vấn người bán từ documentIdSanpham
                            String productId = item.getDocumentIdSanpham();
                            db.collection("sanpham").document(productId).get()
                                    .addOnSuccessListener(productDoc -> {
                                        if (productDoc.exists()) {
                                            // Lấy userId (id người bán) từ sản phẩm
                                            String farmerUserId = productDoc.getString("iduser");

                                            // Truy vấn thông tin người bán (nông dân)
                                            db.collection("users").document(farmerUserId).get()
                                                    .addOnSuccessListener(userDoc -> {
                                                        if (userDoc.exists()) {
                                                            // Lấy tên nông dân
                                                            String farmerName = userDoc.getString("tenuser");
                                                            // Thêm sản phẩm vào nhóm nông dân theo tên
                                                            groupedByFarmer.putIfAbsent(farmerName, new ArrayList<>());
                                                            groupedByFarmer.get(farmerName).add(item);

                                                            // Sau khi nhóm sản phẩm xong, trả kết quả
                                                            List<GiohangNongdan> farmersList = new ArrayList<>();
                                                            for (Map.Entry<String, List<GiohangItem>> entry : groupedByFarmer.entrySet()) {
                                                                farmersList.add(new GiohangNongdan(entry.getKey(), entry.getValue()));
                                                            }
                                                            listener.onSuccess(farmersList);
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> listener.onFailure(e));
                                        }
                                    })
                                    .addOnFailureListener(e -> listener.onFailure(e));
                        }
                    } else {
                        listener.onFailure(new Exception("Giỏ hàng không tồn tại"));
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e));
    }


    // Interface để trả kết quả lấy giỏ hàng
    public interface OnGetCartProductsListener {
        void onSuccess(List<GiohangNongdan> cartItems);

        void onFailure(Exception e);
    }

    // Hàm tăng số lượng sản phẩm
    public void onIncrease(String userId, String productId, int currentQuantity, final OnCartUpdateListener listener) {
        int newQuantity = currentQuantity + 1;

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        DocumentReference cartRef = db.collection("giohang").document(userId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("items");
                if (products != null) {
                    for (Map<String, Object> productData : products) {
                        if (productId.equals(productData.get("documentIdSanpham"))) {
                            productData.put("soluong", newQuantity); // Cập nhật số lượng mới
                            break;
                        }
                    }

                    // Lưu lại thay đổi
                    cartRef.update("items", products)
                            .addOnSuccessListener(aVoid -> listener.onSuccess(newQuantity))
                            .addOnFailureListener(listener::onFailure);
                } else {
                    listener.onFailure(new Exception("Giỏ hàng rỗng"));
                }
            } else {
                listener.onFailure(new Exception("Giỏ hàng không tồn tại"));
            }
        }).addOnFailureListener(listener::onFailure);
    }

    // Hàm giảm số lượng sản phẩm
    public void onDecrease(String userId, String productId, int currentQuantity, final OnCartUpdateListener listener) {
        if (currentQuantity <= 1) {
            listener.onFailure(new Exception("Số lượng không thể nhỏ hơn 1"));
            return;
        }
        int newQuantity = currentQuantity - 1;

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        DocumentReference cartRef = db.collection("giohang").document(userId);
        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("items");
                if (products != null) {
                    for (Map<String, Object> productData : products) {
                        if (productId.equals(productData.get("documentIdSanpham"))) {
                            productData.put("soluong", newQuantity); // Cập nhật số lượng mới
                            break;
                        }
                    }

                    // Lưu lại thay đổi
                    cartRef.update("items", products)
                            .addOnSuccessListener(aVoid -> listener.onSuccess(newQuantity))
                            .addOnFailureListener(listener::onFailure);
                } else {
                    listener.onFailure(new Exception("Giỏ hàng rỗng"));
                }
            } else {
                listener.onFailure(new Exception("Giỏ hàng không tồn tại"));
            }
        }).addOnFailureListener(listener::onFailure);
    }

    // Interface để trả kết quả cập nhật giỏ hàng
    public interface OnCartUpdateListener {
        void onSuccess(int newQuantity);
        void onFailure(Exception e);
    }
}

