package vn.edu.stu.bannhanong.model;

public class Anh_SP {
    private int id;
    private byte[] hinhAnh;
    private Sanpham idSP;

    public Anh_SP(int id, byte[] hinhAnh, Sanpham idSP) {
        this.id = id;
        this.hinhAnh = hinhAnh;
        this.idSP = idSP;
    }

    public Anh_SP(byte[] hinhAnh, Sanpham idSP) {
        this.hinhAnh = hinhAnh;
        this.idSP = idSP;
    }

    public Anh_SP() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Sanpham getIdSP() {
        return idSP;
    }

    public void setIdSP(Sanpham idSP) {
        this.idSP = idSP;
    }
}
