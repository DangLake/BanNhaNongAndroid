package vn.edu.stu.bannhanong;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.stu.bannhanong.adapter.AdapterSanPhamHDND;
import vn.edu.stu.bannhanong.dao.DBHelperHopDong;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.databinding.ActivityChoNongSanBinding;
import vn.edu.stu.bannhanong.databinding.ActivityTaoHopDongBinding;
import vn.edu.stu.bannhanong.model.HopDong;
import vn.edu.stu.bannhanong.model.Sanpham;

public class TaoHopDong extends AppCompatActivity {
    EditText edtTenDN, edtThue, edtDaidien, edtNgaykt, edtGiaohang, edtDiachi,edtsdtDN,edtMailDN;
    AutoCompleteTextView edtTenND;
    DBHelperHopDong dbHelperHopDong;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;
    RecyclerView recyclerView;
    List<Sanpham> dsSP;
    AdapterSanPhamHDND adapterSanPhamHDND;
    private Map<String, String> tenNongDanToIdUser = new HashMap<>();
    Toolbar toolbar;
    ActivityTaoHopDongBinding binding;
    Button btnLayds, btnTaohd;
    List<Pair<Sanpham, Integer>> selectedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tao_hop_dong);
        binding = ActivityTaoHopDongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        getdata();
        loadTenNongdan();
        addEvents();
    }

    private void addEvents() {
        btnTaohd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenDN = edtTenDN.getText().toString().trim();
                String maThue = edtThue.getText().toString().trim();
                String diachi = edtDiachi.getText().toString().trim();
                String nguoiDD = edtDaidien.getText().toString().trim();
                String tenND = edtTenND.getText().toString().trim();
                String ngayKT = edtNgaykt.getText().toString().trim();
                String giaohang = edtGiaohang.getText().toString().trim();
                String sdt=edtsdtDN.getText().toString().trim();
                String mail=edtMailDN.getText().toString().trim();
                if (tenDN.isEmpty() || maThue.isEmpty() || diachi.isEmpty() || nguoiDD.isEmpty() || tenND.isEmpty() || ngayKT.isEmpty() || giaohang.isEmpty()||sdt.isEmpty()||mail.isEmpty()) {
                    Toast.makeText(TaoHopDong.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedProducts = adapterSanPhamHDND.getSelectedProductsWithQuantities();
                for (Pair<Sanpham, Integer> entry : selectedProducts) {
                    Sanpham sp = entry.first;
                    int quantity = entry.second;
                    Log.d("SelectedProduct", "Tên: " + sp.getTensp() + ", Số lượng: " + quantity);
                }
                HopDong hopdong=new HopDong();
                hopdong.setTenDN(tenDN);
                hopdong.setMathue(maThue);
                hopdong.setDiachiDN(diachi);
                hopdong.setNguoiDaidien(nguoiDD);
                hopdong.setTenND(tenND);
                hopdong.setNgayKT(ngayKT);
                hopdong.setSdtDN(sdt);
                hopdong.setEmailDN(mail);
                hopdong.setDanhSachSanPham(selectedProducts);
                hopdong.setTrangthai(0);
                dbHelperHopDong.luuHopdong(hopdong, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(TaoHopDong.this, "Hợp đồng đã được gửi", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(TaoHopDong.this, "Lỗi khi gửi hợp đồng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        edtNgaykt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.add(Calendar.MONTH, 1);
                long minDate = calendar.getTimeInMillis();
                DatePickerDialog dialog = new DatePickerDialog(
                        TaoHopDong.this,
                        (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                            edtNgaykt.setText(selectedDate);
                        },
                        year, month, day
                );
                dialog.getDatePicker().setMinDate(minDate);
                dialog.show();
            }
        });
        btnLayds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenND = edtTenND.getText().toString().trim();
                if (tenND.isEmpty()) {
                    Toast.makeText(TaoHopDong.this, "Vui lòng nhập tên nông dân", Toast.LENGTH_LONG).show();
                    return;
                }

                String id = tenNongDanToIdUser.get(tenND);
                if (id == null) {
                    Toast.makeText(TaoHopDong.this, "Không tìm thấy nông dân với tên này", Toast.LENGTH_LONG).show();
                    return;
                }

                dbHelperHopDong.getSanPhamByUser(id, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        dsSP.clear(); // Xóa danh sách sản phẩm cũ
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idSanPham = document.getId();
                            String tenSanPham = document.getString("tensp");
                            double giaSanPham = document.getDouble("gia");
                            String donViTinh = document.getString("donvitinh");

                            Sanpham sp = new Sanpham();
                            sp.setTensp(tenSanPham);
                            sp.setGia(giaSanPham);
                            sp.setIduser(id);
                            sp.setDocumentId(idSanPham);
                            sp.setDonvitinh(donViTinh);

                            dsSP.add(sp);
                        }
                        adapterSanPhamHDND.updateData(dsSP);
                        // Kiểm tra xem danh sách sản phẩm có rỗng không
                        if (dsSP.isEmpty()) {
                            Toast.makeText(TaoHopDong.this, "Nông dân này không có sản phẩm", Toast.LENGTH_LONG).show();
                        }
                        adapterSanPhamHDND.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TaoHopDong.this, "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void loadTenNongdan() {
        dbHelperHopDong.getTenNongdan(task -> {
            if (task.isSuccessful()) {
                List<String> tenNongDanList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String tenNongDan = document.getString("tenuser");
                    String iduser = document.getId(); // Lấy ID user
                    if (tenNongDan != null) {
                        tenNongDanList.add(tenNongDan);
                        tenNongDanToIdUser.put(tenNongDan, iduser);
                    }
                }
                updateAutoComplete(tenNongDanList);
            } else {
                Toast.makeText(this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAutoComplete(List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, data);
        edtTenND.setAdapter(adapter);
        edtTenND.setThreshold(1);
    }

    private void getdata() {
        Intent intent = getIntent();
        SharedPreferences sharedPreferences = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String ten = sharedPreferences.getString("user_name", "");
        String diachi = sharedPreferences.getString("diachi", "");
        String sdt = sharedPreferences.getString("user_phone", "");
        String tinh = sharedPreferences.getString("tinh", "");
        String quan = sharedPreferences.getString("quan", "");
        edtDiachi.setText(diachi + " ," + quan + " ," + tinh);
        edtTenDN.setText(ten);
        edtsdtDN.setText(sdt);
        if (intent.hasExtra("SP")) {
            Sanpham sp = (Sanpham) intent.getSerializableExtra("SP");
            if (sp != null) {
                dbHelperHopDong.getUserName(sp.getIduser(), edtTenND);
            }
        }
    }

    private void addControls() {
        dbHelperHopDong = new DBHelperHopDong();
        dbHelperSanPhamBuy = new DBHelperSanPhamBuy();
        edtGiaohang = findViewById(R.id.edtGiaohang);
        edtTenDN = findViewById(R.id.edtTenDN);
        edtThue = findViewById(R.id.edtThue);
        edtsdtDN=findViewById(R.id.edtsdtDN);
        edtDaidien = findViewById(R.id.edtDaidien);
        edtNgaykt = findViewById(R.id.edtNgaykt);
        edtTenND = findViewById(R.id.autoCompleteNongDan);
        edtDiachi = findViewById(R.id.edtDiachi);
        edtMailDN=findViewById(R.id.edtMailDN);
        dsSP = new ArrayList<>();
        recyclerView = findViewById(R.id.dsSanphamHD);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterSanPhamHDND = new AdapterSanPhamHDND(TaoHopDong.this, R.layout.item_sanpham_hd, dsSP);
        recyclerView.setAdapter(adapterSanPhamHDND);

        btnLayds = findViewById(R.id.btnLayds);
        btnTaohd = findViewById(R.id.btnTaohd);

    }
}