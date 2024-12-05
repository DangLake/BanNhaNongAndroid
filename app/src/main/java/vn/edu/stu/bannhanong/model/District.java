package vn.edu.stu.bannhanong.model;

import com.google.gson.annotations.SerializedName;

public class District {
    @SerializedName("code")
    private int code;

    @SerializedName("name")
    private String name;

    // Getters
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
