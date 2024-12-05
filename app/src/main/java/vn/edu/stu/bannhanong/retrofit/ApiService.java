package vn.edu.stu.bannhanong.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.edu.stu.bannhanong.model.Province;

import java.util.List;

public interface ApiService {
    // Lấy danh sách tỉnh/thành phố
    @GET("p/")
    Call<List<Province>> getProvinces();

    // Lấy danh sách quận/huyện theo tỉnh/thành phố
    @GET("p/{province_id}?depth=2")
    Call<Province> getDistricts(@Path("province_id") int provinceId);
}
