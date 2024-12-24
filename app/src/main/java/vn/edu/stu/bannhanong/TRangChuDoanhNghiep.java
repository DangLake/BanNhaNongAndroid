package vn.edu.stu.bannhanong;

import static vn.edu.stu.bannhanong.R.id.toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class TRangChuDoanhNghiep extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView tvTenUser;
    View headerView;
    Button btnChonongsan,btnThoitiet,btnBao,btnDiendan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu_doanh_nghiep);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setTint(Color.WHITE);
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
        loadUser();
    }
    private void loadUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Guest");
        tvTenUser.setText(userName);
    }

    private void addEvents() {
        btnChonongsan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TRangChuDoanhNghiep.this, ChoNongSan_DN.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        tvTenUser = headerView.findViewById(R.id.tvTenUser);
        btnChonongsan=findViewById(R.id.btnChonongsan);
        btnThoitiet=findViewById(R.id.btnThoitiet);
        btnBao=findViewById(R.id.btnBao);
        btnDiendan=findViewById(R.id.btnDiendan);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_tuongtoi) {

        } else if (id == R.id.nav_hopdong) {

        } else if (id == R.id.nav_lienhe) {

        } else if (id == R.id.nav_caidat) {

        } else if (id == R.id.nav_dangxuat) {
            Intent intent=new Intent(TRangChuDoanhNghiep.this,Login.class);
            startActivity(intent);
            finish();
        } else if (id==R.id.nav_yeuthich) {
            
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }
}