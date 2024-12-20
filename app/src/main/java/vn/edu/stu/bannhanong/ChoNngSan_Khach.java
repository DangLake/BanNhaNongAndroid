package vn.edu.stu.bannhanong;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

import vn.edu.stu.bannhanong.databinding.ActivityChoNongSanBinding;

public class ChoNngSan_Khach extends AppCompatActivity {
    Toolbar toolbar;
    ActivityChoNongSanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_cho_nng_san_khach);
        binding = ActivityChoNongSanBinding.inflate(getLayoutInflater());
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
        replaceFrag(new SanPhamFragment());
        addEvents();
    }

    private void addControls() {

    }

    private void addEvents() {
        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.sanpham) {
                    replaceFrag(new SanPhamFragment());
                } else if (id == R.id.giohang) {
                    replaceFrag(new GiohangFragment());
                } else if (id == R.id.tinnhan) {
                    replaceFrag(new MessFragment());
                } else if (id == R.id.donhang) {
                    replaceFrag(new DonHang_Khach_Fragment());
                } else if (id == R.id.taikhoan) {
                    replaceFrag(new AccountFragment());
                }
                return true;
            }
        });
    }

    private void replaceFrag(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransition = fragmentManager.beginTransaction();
        fragmentTransition.replace(R.id.frmMain, fragment);
        fragmentTransition.commit();
    }
}