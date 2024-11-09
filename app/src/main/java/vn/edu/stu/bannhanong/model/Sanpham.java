package vn.edu.stu.bannhanong.model;

public class Sanpham {
    private int ma;
    private String ten;
    private String anh;
    private double gia;
    private String mota;

    private LoaiSp loaiSp;

    public Sanpham() {
    }

    public Sanpham(int ma, String ten, String anh, double gia, String mota, LoaiSp loaiSp) {
        this.ma = ma;
        this.ten = ten;
        this.anh = anh;
        this.gia = gia;
        this.mota = mota;
        this.loaiSp = loaiSp;
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

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public LoaiSp getLoaiSp() {
        return loaiSp;
    }

    public void setLoaiSp(LoaiSp loaiSp) {
        this.loaiSp = loaiSp;
    }
}
