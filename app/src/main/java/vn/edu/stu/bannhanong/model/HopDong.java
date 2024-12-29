package vn.edu.stu.bannhanong.model;

import android.util.Pair;

import java.util.List;

public class HopDong {
    private String documentID;
    private String tenDN;
    private String mathue;
    private String diachiDN;
    private String nguoiDaidien;
    private String tenND;
    private String ngayBD;
    private String ngayKT;
    private String sdtDN;
    private String giaohang;
    private List<Pair<Sanpham, Integer>> danhSachSanPham;

    private String CMND;
    private String emailND;
    private String emailDN;
    private String diachiND;
    private String sdtND;
    private int trangthai;




    public HopDong() {
    }

    public String getSdtDN() {
        return sdtDN;
    }

    public void setSdtDN(String sdtDN) {
        this.sdtDN = sdtDN;
    }

    public String getSdtND() {
        return sdtND;
    }

    public void setSdtND(String sdtND) {
        this.sdtND = sdtND;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTenDN() {
        return tenDN;
    }

    public void setTenDN(String tenDN) {
        this.tenDN = tenDN;
    }

    public String getMathue() {
        return mathue;
    }

    public void setMathue(String mathue) {
        this.mathue = mathue;
    }

    public String getDiachiDN() {
        return diachiDN;
    }

    public void setDiachiDN(String diachiDN) {
        this.diachiDN = diachiDN;
    }

    public String getNguoiDaidien() {
        return nguoiDaidien;
    }

    public void setNguoiDaidien(String nguoiDaidien) {
        this.nguoiDaidien = nguoiDaidien;
    }

    public String getTenND() {
        return tenND;
    }

    public void setTenND(String tenND) {
        this.tenND = tenND;
    }

    public String getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(String ngayBD) {
        this.ngayBD = ngayBD;
    }

    public String getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(String ngayKT) {
        this.ngayKT = ngayKT;
    }

    public String getGiaohang() {
        return giaohang;
    }

    public void setGiaohang(String giaohang) {
        this.giaohang = giaohang;
    }

    public List<Pair<Sanpham, Integer>> getDanhSachSanPham() {
        return danhSachSanPham;
    }

    public void setDanhSachSanPham(List<Pair<Sanpham, Integer>> danhSachSanPham) {
        this.danhSachSanPham = danhSachSanPham;
    }

    public String getCMND() {
        return CMND;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
    }

    public String getEmailND() {
        return emailND;
    }

    public void setEmailND(String emailND) {
        this.emailND = emailND;
    }

    public String getEmailDN() {
        return emailDN;
    }

    public void setEmailDN(String emailDN) {
        this.emailDN = emailDN;
    }

    public String getDiachiND() {
        return diachiND;
    }

    public void setDiachiND(String diachiND) {
        this.diachiND = diachiND;
    }

    public int getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(int trangthai) {
        this.trangthai = trangthai;
    }
}
