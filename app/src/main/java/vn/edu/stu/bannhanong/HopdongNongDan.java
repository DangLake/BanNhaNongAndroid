package vn.edu.stu.bannhanong;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.databinding.ActivityChoNongSanBinding;
import vn.edu.stu.bannhanong.databinding.ActivityHopdongNongDanBinding;
import vn.edu.stu.bannhanong.model.HopDong;

public class HopdongNongDan extends AppCompatActivity {

    Toolbar toolbar;
    ActivityHopdongNongDanBinding binding;
    ListView lvHopdong;
    ArrayAdapter<HopDong> adapter;
    List<HopDong> dsHopdong;
    Button btnDangtienhanh,btnChoxacnhan,btnDaketthuc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_hopdong_nong_dan);
        binding = ActivityHopdongNongDanBinding.inflate(getLayoutInflater());
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
    }

    private void addControls() {
        btnDaketthuc=findViewById(R.id.btnDaketthuc);
        btnDangtienhanh=findViewById(R.id.btnDangtienhanh);
        btnChoxacnhan=findViewById(R.id.btnChoxacnhan);
        lvHopdong=findViewById(R.id.lvHopdong);
        dsHopdong=new ArrayList<>();
        adapter=new ArrayAdapter<>(HopdongNongDan.this, android.R.layout.simple_list_item_1,dsHopdong);
        lvHopdong.setAdapter(adapter);

    }
}