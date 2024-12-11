package vn.edu.stu.bannhanong.model;

import java.io.Serializable;
import java.util.List;

public class Sanpham implements Serializable {
    private int ma;
    private String ten;
    private String mota;
    private double gia;
    private String donvitinh;
    private int soluong;
    private LoaiSp loaiSp;
    private Users mauser;

    public Sanpham() {
    }

    public Sanpham(int ma, String ten, String mota, double gia, String donvitinh, int soluong, LoaiSp loaiSp, Users mauser) {
        this.ma = ma;
        this.ten = ten;
        this.mota = mota;
        this.gia = gia;
        this.donvitinh = donvitinh;
        this.soluong = soluong;
        this.loaiSp = loaiSp;
        this.mauser = mauser;
    }

    public Sanpham(String ten, String mota, double gia, String donvitinh, int soluong, LoaiSp loaiSp, Users mauser) {
        this.ten = ten;
        this.mota = mota;
        this.gia = gia;
        this.donvitinh = donvitinh;
        this.soluong = soluong;
        this.loaiSp = loaiSp;
        this.mauser = mauser;
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
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

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public LoaiSp getLoaiSp() {
        return loaiSp;
    }

    public void setLoaiSp(LoaiSp loaiSp) {
        this.loaiSp = loaiSp;
    }

    public Users getMauser() {
        return mauser;
    }

    public void setMauser(Users mauser) {
        this.mauser = mauser;
    }
}
