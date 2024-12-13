//package vn.edu.stu.bannhanong.dao;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import vn.edu.stu.bannhanong.model.Anh_SP;
//import vn.edu.stu.bannhanong.model.Sanpham;
//
//public class DBHelperAnhSP {
//    private FirebaseFirestore db;
//
//    public DBHelperAnhSP() {
//        // Khởi tạo Firestore
//        db = FirebaseFirestore.getInstance();
//    }
//
//    // Lấy ảnh theo ID sản phẩm
//    public void getAnhByIDSanPham(int sanpham_id, final FirestoreCallback callback) {
//        // Truy vấn Firestore để lấy ảnh sản phẩm dựa trên ID sản phẩm
//        db.collection("anh_sp")
//                .whereEqualTo("idsanpham", sanpham_id)  // Lọc theo ID sản phẩm
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<Anh_SP> dsAnh = new ArrayList<>();
//                        QuerySnapshot querySnapshot = task.getResult();
//
//                        // Duyệt qua các tài liệu trong kết quả
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            int maAnh = document.getLong("id").intValue();
//                            byte[] hinhAnh = document.getBlob("hinhanh").toBytes();
//                            int maSP = document.getLong("idsanpham").intValue();
//
//                            // Lấy thông tin sản phẩm từ Firestore
//                            db.collection("sanpham")
//                                    .document(String.valueOf(maSP))
//                                    .get()
//                                    .addOnSuccessListener(productDoc -> {
//                                        if (productDoc.exists()) {
//                                            String tenSP = productDoc.getString("tensp");
//                                            String moTa = productDoc.getString("mota");
//                                            double gia = productDoc.getDouble("gia");
//                                            String donViTinh = productDoc.getString("donvitinh");
//                                            int soLuong = productDoc.getLong("soluong").intValue();
//
//                                            // Tạo đối tượng Sanpham
//                                            Sanpham sanpham = new Sanpham(maSP, tenSP, moTa, gia, donViTinh, soLuong, null, null);
//
//                                            // Tạo đối tượng Anh_SP và thêm vào danh sách
//                                            Anh_SP anhSP = new Anh_SP(maAnh, hinhAnh, sanpham);
//                                            dsAnh.add(anhSP);
//
//                                            // Gọi callback khi hoàn thành
//                                            if (dsAnh.size() == querySnapshot.size()) {
//                                                callback.onCallback(dsAnh);
//                                            }
//                                        }
//                                    });
//                        }
//                    } else {
//                        callback.onCallback(null);  // Nếu truy vấn thất bại
//                    }
//                });
//    }
//
//    // Callback interface
//    public interface FirestoreCallback {
//        void onCallback(List<Anh_SP> dsAnh);
//    }
//}
