package vn.edu.stu.bannhanong.model;

import java.io.Serializable;

public class LoaiUSers implements Serializable {
    private int maloai;
    private String tenloai;

    public LoaiUSers() {
    }

    public LoaiUSers(int maloai, String tenloai) {
        this.maloai = maloai;
        this.tenloai = tenloai;
    }

    public LoaiUSers(String tenloai) {
        this.tenloai = tenloai;
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
}
