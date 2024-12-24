package vn.edu.stu.bannhanong;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.adapter.AdapterSanPhamcuaNongDan;
import vn.edu.stu.bannhanong.adapter.NongDanGiohangAdapter;
import vn.edu.stu.bannhanong.dao.DBHelperGiohang;
import vn.edu.stu.bannhanong.databinding.ActivityChoNongSanBinding;
import vn.edu.stu.bannhanong.databinding.ActivityGioHangBinding;
import vn.edu.stu.bannhanong.model.GiohangNongdan;

public class GioHang extends AppCompatActivity {
    RecyclerView recyclerView;
    NongDanGiohangAdapter adapter;
    List<GiohangNongdan> dsNongdan;
    Toolbar toolbar;
    ActivityGioHangBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_gio_hang);
        binding = ActivityGioHangBinding.inflate(getLayoutInflater());
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

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String documentIDUser = sharedPreferences.getString("documentID", "Guest");
        getProductsInCart(documentIDUser);
    }

    private void getProductsInCart(String documentIDUser) {
        DBHelperGiohang dbHelperGiohang=new DBHelperGiohang();
        dbHelperGiohang.getProductsInCart(documentIDUser, new DBHelperGiohang.OnGetCartProductsListener() {
            @Override
            public void onSuccess(List<GiohangNongdan> farmersList) {
                dsNongdan.clear();
                dsNongdan.addAll(farmersList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                // Khi có lỗi xảy ra, hiển thị thông báo lỗi
                Toast.makeText(GioHang.this, "Lỗi khi lấy giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addControls() {
        recyclerView = findViewById(R.id.rv_farmer_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        dsNongdan = new ArrayList<>();
        adapter = new NongDanGiohangAdapter(dsNongdan);
        recyclerView.setAdapter(adapter);
    }
}