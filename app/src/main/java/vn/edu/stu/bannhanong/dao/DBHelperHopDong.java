package vn.edu.stu.bannhanong.dao;


import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.model.HopDong;
import vn.edu.stu.bannhanong.model.Sanpham;


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
    public void luuHopdong(HopDong hopdong,OnCompleteListener<Void> listener){
        DocumentReference contractRef=db.collection("hopdong").document();
        String documentID=contractRef.getId();
        hopdong.setDocumentID(documentID);
        Map<String, Object> contractData = new HashMap<>();
        contractData.put("tenDN", hopdong.getTenDN() != null ? hopdong.getTenDN() : null);
        contractData.put("mathue", hopdong.getMathue() != null ? hopdong.getMathue() : null);
        contractData.put("diachiDN", hopdong.getDiachiDN() != null ? hopdong.getDiachiDN() : null);
        contractData.put("nguoiDaidien", hopdong.getNguoiDaidien() != null ? hopdong.getNguoiDaidien() : null);
        contractData.put("tenND", hopdong.getTenND() != null ? hopdong.getTenND() : null);
        contractData.put("ngayKT", hopdong.getNgayKT() != null ? hopdong.getNgayKT() : null);
        contractData.put("sdtDN", hopdong.getSdtDN() != null ? hopdong.getSdtDN() : null);
        contractData.put("giaohang", hopdong.getGiaohang() != null ? hopdong.getGiaohang() : null);
        contractData.put("CMND", hopdong.getCMND() != null ? hopdong.getCMND() : null);
        contractData.put("emailND", hopdong.getEmailND() != null ? hopdong.getEmailND() : null);
        contractData.put("emailDN", hopdong.getEmailDN() != null ? hopdong.getEmailDN() : null);
        contractData.put("diachiND", hopdong.getDiachiND() != null ? hopdong.getDiachiND() : null);
        contractData.put("sdtND", hopdong.getSdtND() != null ? hopdong.getSdtND() : null);
        contractData.put("trangthai", hopdong.getTrangthai());
        List<Map<String,Object>> productList=new ArrayList<>();
        if(hopdong.getDanhSachSanPham()!=null){
            for(Pair<Sanpham,Integer> product:hopdong.getDanhSachSanPham()){
                Map<String,Object> productData=new HashMap<>();
                productData.put("sanpham",product.first.getTensp());
                productData.put("soluong",product.second);
                productList.add(productData);
            }
        }
        contractData.put("danhsachsanpham",productList);
        contractRef.set(contractData)
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Loi luu hop dong " + e.getMessage());
                });
    }

}
