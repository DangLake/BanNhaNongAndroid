package vn.edu.stu.bannhanong.model;

import java.io.Serializable;

public class Users implements Serializable {
    private int id;
    private String tenuser;
    private String sdt;
    private String matkhau;
    private String diachi;
    private LoaiUSers loaiUSers;

    public Users() {
    }

    public Users(int id, String tenuser, String sdt, String matkhau, String diachi, LoaiUSers loaiUSers) {
        this.id = id;
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.loaiUSers = loaiUSers;
    }

    public Users(String tenuser, String sdt, String matkhau, String diachi, LoaiUSers loaiUSers) {
        this.tenuser = tenuser;
        this.sdt = sdt;
        this.matkhau = matkhau;
        this.diachi = diachi;
        this.loaiUSers = loaiUSers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public LoaiUSers getLoaiUSers() {
        return loaiUSers;
    }

    public void setLoaiUSers(LoaiUSers loaiUSers) {
        this.loaiUSers = loaiUSers;
    }
}
