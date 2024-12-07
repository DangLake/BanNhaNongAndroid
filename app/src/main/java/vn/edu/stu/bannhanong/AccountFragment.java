package vn.edu.stu.bannhanong;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.stu.bannhanong.dao.DBHelperUsers;
import vn.edu.stu.bannhanong.model.District;
import vn.edu.stu.bannhanong.model.Province;
import vn.edu.stu.bannhanong.model.Users;
import vn.edu.stu.bannhanong.retrofit.ApiService;
import vn.edu.stu.bannhanong.retrofit.RetrofitClient;

public class AccountFragment extends Fragment {
    ApiService apiService;
    Retrofit retrofit;
    Button btnCapNhatuser;
    EditText edtTinhThanh, edtQuanHuyen,edtTen,edtSDT,edtDiachi;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private List<Province> provinces;
    DBHelperUsers dbHelperUsers;
    private List<District> districts;
    public AccountFragment() {
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        retrofit = RetrofitClient.getClient("https://provinces.open-api.vn/api/");
        apiService = retrofit.create(ApiService.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
    private void loadProvinces() {
        apiService.getProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinces = response.body();
                    // Đảm bảo là tỉnh thành có dữ liệu
                    edtTinhThanh.setOnClickListener(v -> showProvinceDialog());
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching provinces", t);
            }
        });
    }

    private void loadDistricts(int provinceId) {
        apiService.getDistricts(provinceId).enqueue(new Callback<Province>() {
            @Override
            public void onResponse(Call<Province> call, Response<Province> response) {
                if (response.isSuccessful() && response.body() != null) {
                    districts = response.body().getDistricts();
                    // Hiển thị danh sách quận huyện khi chọn tỉnh
                    edtQuanHuyen.setOnClickListener(v -> showDistrictDialog());
                }
            }

            @Override
            public void onFailure(Call<Province> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching districts", t);
            }
        });
    }

    private void showProvinceDialog() {
        List<String> provinceNames = new ArrayList<>();
        provinceNames.add("Chọn Tỉnh/Thành phố");
        for (Province province : provinces) {
            provinceNames.add(province.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn Tỉnh/Thành phố")
                .setItems(provinceNames.toArray(new String[0]), (dialog, which) -> {
                    if (which > 0) {
                        edtTinhThanh.setText(provinceNames.get(which));
                        loadDistricts(provinces.get(which - 1).getCode());  // Tải quận huyện theo tỉnh đã chọn
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDistrictDialog() {
        if (districts == null || districts.isEmpty()) {
            return; // Nếu chưa có dữ liệu quận huyện, không hiển thị dialog
        }

        List<String> districtNames = new ArrayList<>();
        districtNames.add("Chọn Quận/Huyện");
        for (District district : districts) {
            districtNames.add(district.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn Quận/Huyện")
                .setItems(districtNames.toArray(new String[0]), (dialog, which) -> {
                    if (which > 0) {
                        edtQuanHuyen.setText(districtNames.get(which));
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtTinhThanh = view.findViewById(R.id.edtTinhthanh);
        edtQuanHuyen = view.findViewById(R.id.edtQuanhuyen);
        edtTen=view.findViewById(R.id.edtTen);
        edtSDT=view.findViewById(R.id.edtSDT);
        edtDiachi=view.findViewById(R.id.edtDiachi);
        btnCapNhatuser=view.findViewById(R.id.btnCapNhatuser);
        loadUserInfo();
        loadProvinces();
        addEvents();
        dbHelperUsers=new DBHelperUsers(getContext());
    }

    private void addEvents() {
        btnCapNhatuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = edtTen.getText().toString();
                String sdt = edtSDT.getText().toString();
                String diachi = edtDiachi.getText().toString();
                String quan=edtQuanHuyen.getText().toString();
                String tinh=edtTinhThanh.getText().toString();

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);
                String oldPhone = sharedPreferences.getString("user_phone", "");

                if (userId == -1) {
                    Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!sdt.equals(oldPhone)) { // Số điện thoại thay đổi
                    Intent intent = new Intent(getActivity(), OTPCapNhat.class);
                    intent.putExtra("phone_number", sdt); // Gửi số điện thoại mới
                    intent.putExtra("userId", userId);
                    intent.putExtra("ten", ten);
                    intent.putExtra("diachi", diachi);
                    intent.putExtra("quan", quan);
                    intent.putExtra("tinh", tinh);
                    startActivityForResult(intent, 100);
                } else {
                    // Nếu số điện thoại không thay đổi, cập nhật trực tiếp
                    dbHelperUsers.updateUser(userId, ten, sdt, diachi,quan,tinh);
                    sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", ten);
                    editor.putString("diachi",diachi);
                    editor.putString("quan",quan);
                    editor.putString("tinh",tinh);
                    editor.apply();
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "");
        String userPhone = sharedPreferences.getString("user_phone", "");
        String diachi = sharedPreferences.getString("diachi", "");
        String quan = sharedPreferences.getString("quan", "");
        String tinh = sharedPreferences.getString("tinh", "");
        edtTen.setText(userName);
        edtSDT.setText(userPhone);
        edtDiachi.setText(diachi);
        edtQuanHuyen.setText(quan);
        edtTinhThanh.setText(tinh);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            int userId = data.getIntExtra("userId", -1);
            String newPhone = data.getStringExtra("newPhone");
            String ten = data.getStringExtra("ten");
            String diachi = data.getStringExtra("diachi");
            String quan = data.getStringExtra("quan");
            String tinh = data.getStringExtra("tinh");
            if (userId != -1) {
                dbHelperUsers.updateUser(userId, ten, newPhone, diachi,quan,tinh);
                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_phone", newPhone);
                editor.putString("user_name", ten);
                editor.putString("diachi",diachi);
                editor.putString("quan",quan);
                editor.putString("tinh",tinh);
                editor.apply();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }
}