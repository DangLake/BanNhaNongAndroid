package vn.edu.stu.bannhanong.model;

import java.util.List;

public class LoaiSp {
    private int maloai;
    private String tenloai;
    private List<Sanpham> dsSP;

    public LoaiSp() {
    }

    public LoaiSp(int maloai, String tenloai, List<Sanpham> dsSP) {
        this.maloai = maloai;
        this.tenloai = tenloai;
        this.dsSP = dsSP;
    }

    public int getMaloai() {
        return maloai;
    }

    public void setMaloai(int maloai) {
        this.maloai = maloai;
    }

    public String getTenloai() {
        return tenloai;
    }

    public void setTenloai(String tenloai) {
        this.tenloai = tenloai;
    }

    public List<Sanpham> getDsSP() {
        return dsSP;
    }

    public void setDsSP(List<Sanpham> dsSP) {
        this.dsSP = dsSP;
    }
}
