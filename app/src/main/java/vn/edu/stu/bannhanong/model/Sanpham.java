package vn.edu.stu.bannhanong.model;

import java.io.Serializable;
import java.sql.Array;
import java.util.List;
import com.google.firebase.firestore.PropertyName;

public class Sanpham implements Serializable {
    private String documentID;
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
    private String maDocumentuser;

    public Sanpham(String documentID, String tensp, String mota, double gia, String donvitinh, List<String> anh, int idloaisp, String maDocumentuser) {
        this.documentID = documentID;
        this.tensp = tensp;
        this.mota = mota;
        this.gia = gia;
        this.donvitinh = donvitinh;
        this.anh = anh;
        this.idloaisp = idloaisp;
        this.maDocumentuser = maDocumentuser;
    }

    public Sanpham(String tensp, String mota, double gia, String donvitinh, List<String> anh, int idloaisp, String maDocumentuser) {
        this.tensp = tensp;
        this.mota = mota;
        this.gia = gia;
        this.donvitinh = donvitinh;
        this.anh = anh;
        this.idloaisp = idloaisp;
        this.maDocumentuser = maDocumentuser;
    }

    public Sanpham() {
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
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

    public String getMaDocumentuser() {
        return maDocumentuser;
    }

    public void setMaDocumentuser(String maDocumentuser) {
        this.maDocumentuser = maDocumentuser;
    }
}