package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;
import vn.edu.stu.bannhanong.model.Users;

public class Login extends AppCompatActivity {
    Button btnDangky, btnQuenmk, btnDangnhap;
    EditText edtSDT, edtMatkhau;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firestore = FirebaseFirestore.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDangky.setOnClickListener(view -> xulyDangKy());
        btnQuenmk.setOnClickListener(view -> xulyQuenMK());
        btnDangnhap.setOnClickListener(view -> xulyDangNhap());
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
                        firestore.collection("users")
                                .whereEqualTo("sdt", phoneNumber)
                                .whereEqualTo("matkhau", password)
                                .get()
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful() && !innerTask.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : innerTask.getResult()) {
                                            Users user = document.toObject(Users.class);

                                            String documentId = String.valueOf(dbHelper.getDocumentIdByPhoneNumber(phoneNumber));
                                            saveUserInfoToPreferences(user, documentId);

                                            int userType = user.getLoaiUSers();
                                            if (userType == 0 || userType == 1) {
                                                Intent intent = new Intent(Login.this, TrangchuNongDan.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(Login.this, TrangChuDoanhNghiep.class);
                                                startActivity(intent);
                                                finish();
                                            }
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

    private void saveUserInfoToPreferences(Users user, String documentId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("documentID", documentId);
        editor.putString("user_name", user.getTenuser());
        editor.putString("user_phone", user.getSdt());
        editor.putInt("user_type", user.getLoaiUSers());
        editor.apply();
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
}
