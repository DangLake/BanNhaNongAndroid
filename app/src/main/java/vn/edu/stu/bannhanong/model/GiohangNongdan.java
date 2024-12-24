package vn.edu.stu.bannhanong.model;

import java.util.List;

public class GiohangNongdan {
    private String farmerName;
    private List<GiohangItem> productList;

    public GiohangNongdan(String farmerName, List<GiohangItem> productList) {
        this.farmerName = farmerName;
        this.productList = productList;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public List<GiohangItem> getProductList() {
        return productList;
    }

    public void setProductList(List<GiohangItem> productList) {
        this.productList = productList;
    }
}
