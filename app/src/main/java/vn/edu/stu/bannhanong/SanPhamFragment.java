package vn.edu.stu.bannhanong;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.stu.bannhanong.adapter.AdapterSanPhamBuy;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.dao.DBHelperUsers;
import vn.edu.stu.bannhanong.model.District;
import vn.edu.stu.bannhanong.model.LoaiSp;
import vn.edu.stu.bannhanong.model.Province;
import vn.edu.stu.bannhanong.model.Sanpham;
import vn.edu.stu.bannhanong.retrofit.ApiService;
import vn.edu.stu.bannhanong.retrofit.RetrofitClient;

public class SanPhamFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    MaterialAutoCompleteTextView inputLoai;
    TextInputLayout inputLayout;
    AdapterSanPhamBuy adapterSanPhamBuy;
    List<Sanpham> dsSanPham;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;
    ArrayList<LoaiSp> dsLoai;
    ArrayAdapter<String> adapter;
    ListView lvSanPhamBuy;
    private FirebaseFirestore db;
    FirebaseFirestore firestore;
    ApiService apiService;
    Retrofit retrofit;
    private List<Province> provinces;
    private List<District> districts;
    EditText edtTinhThanh;
    Sanpham chon=null;


    public SanPhamFragment() {

    }

    public static SanPhamFragment newInstance(String param1, String param2) {
        SanPhamFragment fragment = new SanPhamFragment();
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
        firestore = FirebaseFirestore.getInstance();
        retrofit = RetrofitClient.getClient("https://provinces.open-api.vn/api/");
        apiService = retrofit.create(ApiService.class);
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
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_san_pham, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvSanPhamBuy=view.findViewById(R.id.lvSanPhamBuy);
        dbHelperSanPhamBuy=new DBHelperSanPhamBuy();
        dsSanPham=new ArrayList<>();
        adapterSanPhamBuy=new AdapterSanPhamBuy(getActivity(),R.layout.item_sanpham_khach,dsSanPham);
        lvSanPhamBuy.setAdapter(adapterSanPhamBuy);
        inputLoai=view.findViewById(R.id.inputLoai);
        inputLayout=view.findViewById(R.id.inputLayout);
        edtTinhThanh=view.findViewById(R.id.edtTinhthanh);
        dsLoai = new ArrayList<>();

        loadSanphamBuy();
        loadLoaiSanpham();
        loadProvinces();
        addEvents();
    }

    private void addEvents() {
        lvSanPhamBuy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chon=dsSanPham.get(i);
                Intent intent=new Intent(getActivity(),ThongTinSanPham.class);
                intent.putExtra("SANPHAM",chon);
                startActivityForResult(intent,1);
            }
        });
    }

    private void loadLoaiSanpham() {
        db = FirebaseFirestore.getInstance();
        dsLoai.clear(); // Xóa danh sách cũ trước khi tải mới

        db.collection("loaisanpham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int maloai = document.getLong("id").intValue();
                            String tenloai = document.getString("tenloaisp");
                            dsLoai.add(new LoaiSp(maloai, tenloai));
                        }
                        // Tạo danh sách tên loại để đưa vào Adapter
                        List<String> tenLoaiList = new ArrayList<>();
                        for (LoaiSp loaiSp : dsLoai) {
                            tenLoaiList.add(loaiSp.getTenloai());
                        }

                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, tenLoaiList);
                        inputLoai.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "Lỗi khi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadSanphamBuy() {
        dbHelperSanPhamBuy.getAllProducts(new DBHelperSanPhamBuy.ProductCallback() {
            @Override
            public void onSuccess(List<Sanpham> productList) {
                dsSanPham.clear();
                dsSanPham.addAll(productList);
                adapterSanPhamBuy.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Sản phẩm trống", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadSanphamBuy();
    }
}