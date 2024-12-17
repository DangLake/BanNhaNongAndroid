package vn.edu.stu.bannhanong.model;

import android.net.Uri;

import java.io.Serializable;
import java.sql.Array;
import java.util.List;

import com.google.firebase.firestore.PropertyName;

public class Sanpham implements Serializable {
    @PropertyName("tensp")
    private String tensp;

    @PropertyName("mota")
    private String mota;

    private double gia;
    private String donvitinh;

    @PropertyName("anh")
    private List<String> anh;

    @PropertyName("idloaisp")
    private int idloaisp;

    @PropertyName("iduser")
    private String iduser;

    public Sanpham(String tensp, String mota, double gia, String donvitinh, List<String> anh, int idloaisp, String iduser) {
        this.tensp = tensp;
        this.mota = mota;
        this.gia = gia;
        this.donvitinh = donvitinh;
        this.anh = anh;
        this.idloaisp = idloaisp;
        this.iduser = iduser;
    }

    public Sanpham() {
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getDonvitinh() {
        return donvitinh;
    }

    public void setDonvitinh(String donvitinh) {
        this.donvitinh = donvitinh;
    }

    public List<String> getAnh() {
        return anh;
    }

    public void setAnh(List<String> anh) {
        this.anh = anh;
    }

    public int getIdloaisp() {
        return idloaisp;
    }

    public void setIdloaisp(int idloaisp) {
        this.idloaisp = idloaisp;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }
}