package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import vn.edu.stu.bannhanong.adapter.AdapterImageSlide;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.databinding.ActivityThongTinSanPhamBinding;
import vn.edu.stu.bannhanong.model.Image;
import vn.edu.stu.bannhanong.model.Sanpham;

public class ThongTinSanPham extends AppCompatActivity {
    Toolbar toolbar;
    ActivityThongTinSanPhamBinding binding;
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private AdapterImageSlide adapterImageSlide;
    TextView tvTen,tvGia,tvMota,tvTenND,tvDiachi;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_thong_tin_san_pham);
        binding = ActivityThongTinSanPhamBinding.inflate(getLayoutInflater());
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
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("SANPHAM")) {
            Sanpham sanpham = (Sanpham) intent.getSerializableExtra("SANPHAM");
            if (sanpham != null) {
                dbHelperSanPhamBuy.getUserName(sanpham.getIduser(),tvTenND,tvDiachi);
                tvTen.setText(sanpham.getTensp());
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(sanpham.getGia());
                tvGia.setText("Giá: " + formattedPrice + " VND/"+sanpham.getDonvitinh());
                tvMota.setText("Mô tả sản phẩm"+"\n"+"\n"+sanpham.getMota());
                loadProductImages(sanpham.getDocumentId());
            }
        }
    }

    private void loadProductImages(String idSanPham) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sanpham")
                .document(idSanPham)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy danh sách URL ảnh từ trường "anh"
                        List<String> listImages = (List<String>) documentSnapshot.get("anh");

                        if (listImages != null && !listImages.isEmpty()) {
                            // Chuyển đổi danh sách String sang danh sách Image
                            List<Image> imageObjects = new ArrayList<>();
                            for (String imageUrl : listImages) {
                                imageObjects.add(new Image(imageUrl));
                            }

                            // Cập nhật Adapter
                            adapterImageSlide.setImages(imageObjects);
                        } else {
                            // Nếu không có ảnh, thêm ảnh mặc định
                            List<Image> defaultImage = new ArrayList<>();
                            defaultImage.add(new Image(String.valueOf(R.drawable.logo))); // Ảnh mặc định
                            adapterImageSlide.setImages(defaultImage);
                        }
                    } else {
                        Log.e("ThongTinSanPham", "Không tìm thấy sản phẩm");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ThongTinSanPham", "Lỗi khi tải sản phẩm: " + e.getMessage());
                    // Xử lý lỗi và thêm ảnh mặc định
                    List<Image> defaultImage = new ArrayList<>();
                    defaultImage.add(new Image(String.valueOf(R.drawable.logo))); // Ảnh mặc định
                    adapterImageSlide.setImages(defaultImage);
                });
    }


    private void addControls() {
        viewPager=findViewById(R.id.viewPager);
        indicator=findViewById(R.id.circle);
        adapterImageSlide =new AdapterImageSlide(this,getListphoto());
        viewPager.setAdapter(adapterImageSlide);
        indicator.setViewPager(viewPager);
        adapterImageSlide.registerDataSetObserver(indicator.getDataSetObserver());
        tvTen=findViewById(R.id.tvTen);
        tvGia=findViewById(R.id.tvGia);
        tvMota=findViewById(R.id.tvMota);
        tvTenND=findViewById(R.id.tvTenND);
        tvDiachi=findViewById(R.id.tvDiachi);
        dbHelperSanPhamBuy=new DBHelperSanPhamBuy();
    }

    private List<Image> getListphoto() {
        List<Image> list = new ArrayList<>();
        // Thêm một ảnh mặc định ban đầu (có thể là logo hoặc ảnh trống)
        list.add(new Image(String.valueOf(R.drawable.logo)));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_giohang, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            menuItem.setTitle(s);
        }
        return true;
    }
}