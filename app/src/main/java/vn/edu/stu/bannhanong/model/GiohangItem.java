package vn.edu.stu.bannhanong.model;

public class GiohangItem {
    private String documentIdSanpham;
    private int soluong;

    public GiohangItem(String documentIdSanpham, int soluong) {
        this.documentIdSanpham = documentIdSanpham;
        this.soluong = soluong;
    }

    public GiohangItem() {
    }

    public String getDocumentIdSanpham() {
        return documentIdSanpham;
    }

    public void setDocumentIdSanpham(String documentIdSanpham) {
        this.documentIdSanpham = documentIdSanpham;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
