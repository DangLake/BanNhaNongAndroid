package vn.edu.stu.bannhanong.model;

import java.io.Serializable;

public class Users implements Serializable {
    private String documentID;
    private String tenuser;
    private String sdt;
    private String matkhau;
    private String diachi;
    private String quanhuyen;
    private String tinh;
    private int maloai;

    public Users() {
    }


    public Users(String documentID, String tenuser, String sdt, String matkhau, String diachi, String quanhuyen, String tinh, int maloai) {
        this.documentID = documentID;
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.quanhuyen = quanhuyen;
        this.tinh = tinh;
        this.maloai = maloai;
    }

    public Users(String tenuser, String sdt, String matkhau, String diachi, String quanhuyen, String tinh, int maloai) {
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.quanhuyen = quanhuyen;
        this.tinh = tinh;
        this.maloai = maloai;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTenuser() {
        return tenuser;
    }

    public void setTenuser(String tenuser) {
        this.tenuser = tenuser;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getQuanhuyen() {
        return quanhuyen;
    }

    public void setQuanhuyen(String quanhuyen) {
        this.quanhuyen = quanhuyen;
    }

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public int getMaloai() {
        return maloai;
    }

    public void setMaloai(int maloai) {
        this.maloai = maloai;
    }
}
