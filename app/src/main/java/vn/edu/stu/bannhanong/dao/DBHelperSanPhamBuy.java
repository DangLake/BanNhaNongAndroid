package vn.edu.stu.bannhanong.dao;

import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.model.GiohangItem;
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
    public void getUserName(String userID, TextView tvTenND,TextView tvDiachi) {
        db.collection("users") // Tên collection chứa thông tin user
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String diachi = documentSnapshot.getString("tinh");
                        String ten = documentSnapshot.getString("tenuser");
                        tvTenND.setText(ten);
                        tvDiachi.setText(diachi);
                    } else {
                        tvDiachi.setText("Địa chỉ: Không có thông tin");
                        tvTenND.setText("null");
                    }
                })
                .addOnFailureListener(e -> {
                    tvDiachi.setText("Địa chỉ: Không tải được");
                    tvTenND.setText("khong tai duoc");
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
    public void getAllProductsExcluding(String userID, String excludedDocumentId, ProductCallback callback) {
        db.collection("sanpham") // Collection chứa thông tin sản phẩm
                .whereEqualTo("iduser", userID) // Lọc theo userID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Sanpham> productList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        if (!document.getId().equals(excludedDocumentId)) { // Loại trừ documentId cụ thể
                            Sanpham product = document.toObject(Sanpham.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                    }
                    callback.onSuccess(productList); // Trả về danh sách sản phẩm qua callback
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e); // Trả về lỗi qua callback
                });
    }

    public void addToCart(String userID, Sanpham sanpham, int soLuong, CartCallback callback) {
        // Kiểm tra xem giỏ hàng đã tồn tại chưa
        db.collection("giohang")
                .document(userID)  // Mỗi người dùng có tài liệu giỏ hàng riêng biệt với ID là userID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy danh sách items và chuyển đổi thành danh sách GiohangItem
                        List<Map<String, Object>> cartItemsMap = (List<Map<String, Object>>) documentSnapshot.get("items");
                        List<GiohangItem> cartItems = new ArrayList<>();
                        if (cartItemsMap != null) {
                            for (Map<String, Object> itemMap : cartItemsMap) {
                                GiohangItem item = convertMapToGiohangItem(itemMap);
                                cartItems.add(item);
                            }
                        }
                        boolean productExists = false;

                        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
                        for (GiohangItem item : cartItems) {
                            if (item.getDocumentIdSanpham().equals(sanpham.getDocumentId())) {
                                // Cập nhật số lượng sản phẩm trong giỏ hàng
                                item.setSoluong(item.getSoluong() + soLuong);
                                productExists = true;
                                break;
                            }
                        }

                        // Thêm sản phẩm mới nếu chưa có
                        if (!productExists) {
                            GiohangItem newItem = new GiohangItem(sanpham.getDocumentId(), soLuong);
                            cartItems.add(newItem);
                        }

                        // Cập nhật lại giỏ hàng trong Firestore
                        Map<String, Object> cartData = new HashMap<>();
                        cartData.put("items", cartItems);
                        db.collection("giohang")
                                .document(userID)
                                .update(cartData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(userID))
                                .addOnFailureListener(callback::onFailure);
                    } else {
                        // Giỏ hàng chưa có, tạo mới
                        List<GiohangItem> cartItems = new ArrayList<>();
                        GiohangItem newItem = new GiohangItem(sanpham.getDocumentId(), soLuong);
                        cartItems.add(newItem);
                        Map<String, Object> cartData = new HashMap<>();
                        cartData.put("items", cartItems);

                        db.collection("giohang")
                                .document(userID)
                                .set(cartData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(userID))
                                .addOnFailureListener(callback::onFailure);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Phương thức chuyển đổi Map thành GiohangItem
    private GiohangItem convertMapToGiohangItem(Map<String, Object> itemMap) {
        String documentId = (String) itemMap.get("documentIdSanpham");
        Long soluong = (Long) itemMap.get("soluong");
        return new GiohangItem(documentId, soluong.intValue());
    }

    public interface CartCallback {
        void onSuccess(String documentId);  // Trả về ID của đối tượng giỏ hàng mới được tạo trong Firestore
        void onFailure(Exception e);        // Xử lý khi có lỗi
    }

    public interface ProductCallback {
        void onSuccess(List<Sanpham> productList); // Khi dữ liệu được tải thành công
        void onFailure(Exception e);              // Khi có lỗi xảy ra
    }

}
