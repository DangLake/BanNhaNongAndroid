package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import vn.edu.stu.bannhanong.adapter.AdapterImageSlide;
import vn.edu.stu.bannhanong.adapter.AdapterSanPhamcuaNongDan;
import vn.edu.stu.bannhanong.adapter.ImageAdapter;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.databinding.ActivityThongTinSanPhamBinding;
import vn.edu.stu.bannhanong.model.Image;
import vn.edu.stu.bannhanong.model.Sanpham;

public class ThongTinSanPham extends AppCompatActivity {
    Toolbar toolbar;
    ActivityThongTinSanPhamBinding binding;
    private ViewPager viewPager;
    private AdapterImageSlide adapterImageSlide;
    TextView tvTen,tvGia,tvMota,tvTenND,tvDiachi;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;
    private RecyclerView recyclerView;
    private AdapterSanPhamcuaNongDan adapterSanPhamcuaNongDan;
    private List<Sanpham> listSP;

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
        hhienSanPhamND();
    }

    private void hhienSanPhamND() {

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
                Log.d("Image URLs", "Danh sách URL: " + sanpham.getAnh());
                loadProductImages(sanpham.getDocumentId());
                dbHelperSanPhamBuy.getAllProductsExcluding(sanpham.getIduser(), sanpham.getDocumentId(), new DBHelperSanPhamBuy.ProductCallback() {
                    @Override
                    public void onSuccess(List<Sanpham> productList) {
                        listSP.clear();
                        listSP.addAll(productList);
                        adapterSanPhamcuaNongDan.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ThongTinSanPham", "Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
                    }
                });
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
                        // Lấy danh sách URL ảnh từ Firestore
                        List<String> listImages = (List<String>) documentSnapshot.get("anh");

                        if (listImages != null && !listImages.isEmpty()) {
                            List<String> imageObjects = new ArrayList<>();
                            for (String imageUrl : listImages) {
                                imageObjects.add(imageUrl); // Mỗi URL là một đối tượng Image
                            }
                            adapterImageSlide.setImages(imageObjects); // Cập nhật Adapter
                        } else {
                            setDefaultImage();
                        }
                    } else {
                        Log.e("ThongTinSanPham", "Không tìm thấy sản phẩm");
                        setDefaultImage();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ThongTinSanPham", "Lỗi khi tải sản phẩm: " + e.getMessage());
                    setDefaultImage();
                });
    }


    private void setDefaultImage() {
        List<String> defaultImage = new ArrayList<>();
        defaultImage.add("android.resource://" + getPackageName() + "/" + R.drawable.logo);
        adapterImageSlide.setImages(defaultImage);
    }


    private void addControls() {
        viewPager=findViewById(R.id.viewPager);
        adapterImageSlide =new AdapterImageSlide(this,getListphoto());
        viewPager.setAdapter(adapterImageSlide);
        tvTen=findViewById(R.id.tvTen);
        tvGia=findViewById(R.id.tvGia);
        tvMota=findViewById(R.id.tvMota);
        tvTenND=findViewById(R.id.tvTenND);
        tvDiachi=findViewById(R.id.tvDiachi);
        dbHelperSanPhamBuy=new DBHelperSanPhamBuy();
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        listSP = new ArrayList<>();
        adapterSanPhamcuaNongDan = new AdapterSanPhamcuaNongDan(this, listSP);
        recyclerView.setAdapter(adapterSanPhamcuaNongDan);

    }

    private List<String> getListphoto() {
        List<String> list = new ArrayList<>();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.chiase) {

        }else if (item.getItemId()==R.id.menu_cart) {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String documentIDUser = sharedPreferences.getString("documentID", "Guest");
            Intent intent=new Intent(ThongTinSanPham.this, GiohangFragment.class);
            intent.putExtra("documentIDUser",documentIDUser);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}