package vn.edu.stu.bannhanong.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Province {
    @SerializedName("code")
    private int code;

    @SerializedName("name")
    private String name;

    @SerializedName("districts")
    private List<District> districts;

    // Getters
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<District> getDistricts() {
        return districts;
    }
}

