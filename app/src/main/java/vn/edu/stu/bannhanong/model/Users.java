package vn.edu.stu.bannhanong.model;

import java.io.Serializable;

public class Users implements Serializable {
    private String id;
    private String tenuser;
    private String sdt;
    private String matkhau;
    private String diachi;
    private String quanhuyen;
    private String tinh;
    private int loaiUSers;

    public Users() {
    }

    public Users(String id, String tenuser, String sdt, String matkhau, String diachi, String quanhuyen, String tinh, int loaiUSers) {
        this.id = id;
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.quanhuyen = quanhuyen;
        this.tinh = tinh;
        this.loaiUSers = loaiUSers;
    }

    public Users(String tenuser, String sdt, String matkhau, String diachi, String quanhuyen, String tinh, int loaiUSers) {
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.quanhuyen = quanhuyen;
        this.tinh = tinh;
        this.loaiUSers = loaiUSers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getLoaiUSers() {
        return loaiUSers;
    }

    public void setLoaiUSers(int loaiUSers) {
        this.loaiUSers = loaiUSers;
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
}
