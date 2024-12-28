package vn.edu.stu.bannhanong;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    Button btnThemgiohang, btnTaoHopDong;
    Sanpham sanpham;

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
        addEvents();
    }

    private void addEvents() {
        btnThemgiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                if (intent.hasExtra("SANPHAM")) {
                    Sanpham sanpham = (Sanpham) intent.getSerializableExtra("SANPHAM");
                    xuLyThemGioHang(sanpham);
                }
            }
        });
        btnTaoHopDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ThongTinSanPham.this, TaoHopDong.class);
                intent.putExtra("SP",sanpham);
                startActivityForResult(intent,1);
            }
        });
    }

    private void xuLyThemGioHang(Sanpham sanpham) {
        // Tạo một Dialog để chọn số lượng
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn số lượng");

        // Tạo Layout để chứa các nút tăng và giảm số lượng
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // Tạo một TextView để hiển thị số lượng
        final TextView tvSoLuong = new TextView(this);
        tvSoLuong.setText("1"); // Mặc định số lượng là 1
        tvSoLuong.setPadding(20, 0, 20, 0);
        tvSoLuong.setTextSize(18);
        tvSoLuong.setGravity(Gravity.CENTER);

        // Tạo nút giảm số lượng
        Button btnGiam = new Button(this);
        btnGiam.setText("-");
        btnGiam.setTextSize(18);

        // Tạo nút tăng số lượng
        Button btnTang = new Button(this);
        btnTang.setText("+");
        btnTang.setTextSize(18);

        // Thêm các phần tử vào layout
        layout.addView(btnGiam);
        layout.addView(tvSoLuong);
        layout.addView(btnTang);

        // Cài đặt Layout vào Dialog
        builder.setView(layout);

        // Sự kiện khi nhấn nút giảm số lượng
        btnGiam.setOnClickListener(v -> {
            int soLuong = Integer.parseInt(tvSoLuong.getText().toString());
            if (soLuong > 1) {
                soLuong--; // Giảm số lượng
                tvSoLuong.setText(String.valueOf(soLuong));
            }
        });

        // Sự kiện khi nhấn nút tăng số lượng
        btnTang.setOnClickListener(v -> {
            int soLuong = Integer.parseInt(tvSoLuong.getText().toString());
            soLuong++; // Tăng số lượng
            tvSoLuong.setText(String.valueOf(soLuong));
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String documentIdUser = sharedPreferences.getString("documentID", "Guest");
        // Cài đặt nút "OK" để thêm sản phẩm vào giỏ hàng
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int soLuong = Integer.parseInt(tvSoLuong.getText().toString());
                // Gọi hàm thêm sản phẩm vào giỏ hàng với số lượng đã chọn
                dbHelperSanPhamBuy.addToCart(documentIdUser, sanpham, soLuong, new DBHelperSanPhamBuy.CartCallback() {
                    @Override
                    public void onSuccess(String documentId) {
                        // Thành công, có thể thông báo cho người dùng
                        Toast.makeText(getApplicationContext(), "Sản phẩm đã được thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Xử lý lỗi nếu có
                        Toast.makeText(getApplicationContext(), "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Cài đặt nút "Hủy" để đóng Dialog
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng Dialog nếu người dùng bấm Hủy
            }
        });

        // Hiển thị Dialog
        builder.show();
    }


    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("SANPHAM")) {
            sanpham = (Sanpham) intent.getSerializableExtra("SANPHAM");
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
        btnThemgiohang=findViewById(R.id.btnThemgiohang);
        btnTaoHopDong = findViewById(R.id.btnTaohopdong); // Button tạo hợp đồng

        // Kiểm tra loại người dùng (ví dụ từ SharedPreferences)
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userType = sharedPreferences.getInt("user_type", 0);

        // Nếu người dùng là doanh nghiệp, thay thế nút "Thêm giỏ hàng" bằng nút "Tạo hợp đồng"
        if (userType==1) {
            btnThemgiohang.setVisibility(View.GONE); // Ẩn nút "Thêm giỏ hàng"
            btnTaoHopDong.setVisibility(View.VISIBLE); // Hiển thị nút "Tạo hợp đồng"
        } else {
            btnThemgiohang.setVisibility(View.VISIBLE); // Hiển thị nút "Thêm giỏ hàng"
            btnTaoHopDong.setVisibility(View.GONE); // Ẩn nút "Tạo hợp đồng"
        }

    }

    private List<String> getListphoto() {
        List<String> list = new ArrayList<>();
        return list;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_giohang, menu);

        // Lấy thông tin loại người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userType = sharedPreferences.getInt("user_type", 0);

        // Kiểm tra loại người dùng và ẩn giỏ hàng nếu là doanh nghiệp
        if (userType==1) {
            MenuItem cartMenuItem = menu.findItem(R.id.menu_cart);
            if (cartMenuItem != null) {
                cartMenuItem.setVisible(false); // Ẩn giỏ hàng
            }
        }

        // Thay đổi màu sắc cho menu item
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
        // Xử lý khi người dùng chọn một item trong menu
        if (item.getItemId() == R.id.chiase) {
            // Xử lý chia sẻ
        } else if (item.getItemId() == R.id.menu_cart) {
            // Chuyển đến giỏ hàng nếu không phải doanh nghiệp
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String documentIDUser = sharedPreferences.getString("documentID", "Guest");
            Intent intent = new Intent(ThongTinSanPham.this, GioHang.class);
            intent.putExtra("documentIDUser", documentIDUser);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}