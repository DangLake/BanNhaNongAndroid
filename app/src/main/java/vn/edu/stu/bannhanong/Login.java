package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;
import vn.edu.stu.bannhanong.model.Users;

public class Login extends AppCompatActivity {
    Button btnDangky, btnQuenmk, btnDangnhap;
    EditText edtSDT, edtMatkhau;
    FirebaseFirestore firestore;
    FusedLocationProviderClient fusedLocationClient;
    static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestore = FirebaseFirestore.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyDangKy();
            }
        });
        btnQuenmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyQuenMK();
            }
        });
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyDangNhap();
            }
        });

    }

    private void xulyDangNhap() {
        String phoneNumber = edtSDT.getText().toString().trim();
        String password = edtMatkhau.getText().toString().trim();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelperUsers dbHelper = new DBHelperUsers();
        dbHelper.isValidLogin(phoneNumber, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()) {
                        // Nếu đăng nhập thành công
                        firestore.collection("users")
                                .whereEqualTo("sdt", phoneNumber)
                                .whereEqualTo("matkhau", password)
                                .get()
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful() && !innerTask.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : innerTask.getResult()) {
                                            Users user = document.toObject(Users.class);
                                            saveUserInfoToPreferences(user);
                                            checkAndRequestLocationPermission();
                                        }
                                    } else {
                                        Toast.makeText(Login.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Login.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Login.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show());
    }

    private void saveUserInfoToPreferences(Users user) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("location_permission_granted", false);
        editor.putString("user_id", user.getId());
        editor.putString("user_name", user.getTenuser());
        editor.putString("user_phone", user.getSdt());
        editor.putInt("user_type", user.getLoaiUSers());
        editor.apply();
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocationAndProceed();
        }
    }

    private void fetchLocationAndProceed() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("LoginActivity", "Latitude: " + latitude + ", Longitude: " + longitude);

                Geocoder geocoder = new Geocoder(Login.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String fullAddress = address.getAddressLine(0);

                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("location_permission_granted", true);
                        editor.putFloat("last_latitude", (float) latitude);
                        editor.putFloat("last_longitude", (float) longitude);
                        editor.putString("user_address", fullAddress);
                        editor.apply();

                        int userType = sharedPreferences.getInt("user_type", -1);
                        Intent intent = userType == 2 ? new Intent(Login.this, TrangchuNongDan.class) : new Intent(Login.this, TrangChuDoanhNghiep.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.location_failed), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.location_failed_mess) + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.location_failed), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, getString(R.string.location_failed_mess) + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void xulyQuenMK() {
        Intent intent = new Intent(Login.this, XacThucDienThoai.class);
        startActivity(intent);
    }

    private void xulyDangKy() {
        Intent intent = new Intent(Login.this, SignIn.class);
        startActivity(intent);
    }

    private void addControls() {
        btnDangky = findViewById(R.id.btnDangky);
        btnQuenmk = findViewById(R.id.btnQuenmk);
        btnDangnhap = findViewById(R.id.btnDangnhap);
        edtSDT = findViewById(R.id.edtSDT);
        edtMatkhau = findViewById(R.id.edtMatkhau);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndProceed();
            } else {
                Toast.makeText(this, getString(R.string.location_failed_per), Toast.LENGTH_SHORT).show();
            }
        }
    }
}