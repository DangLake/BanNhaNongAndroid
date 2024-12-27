package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;

public class DoiMatKhau extends AppCompatActivity {

    EditText edtMatkhau,edtCheckMatkhau;
    Button btnCapnhat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doi_mat_khau);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnCapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyDoiMatKhau();
            }
        });
    }

    private void xulyDoiMatKhau() {
        String password = edtMatkhau.getText().toString().trim();
        String checkPassword = edtCheckMatkhau.getText().toString().trim();
        String phoneNumber = getIntent().getStringExtra("phone_number");

        // Kiểm tra các trường thông tin
        if (password.isEmpty() || checkPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.dk_mk_null), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(checkPassword)) {
            Toast.makeText(this, getString(R.string.dk_mk_khop), Toast.LENGTH_SHORT).show();
            return;
        }
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        // Gọi hàm doiMatKhau từ DBHelperUsers
        DBHelperUsers dbHelper = new DBHelperUsers();
        dbHelper.doiMatKhau(phoneNumber, hashedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DoiMatKhau.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DoiMatKhau.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(DoiMatKhau.this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(DoiMatKhau.this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show());
    }
    private String hashPassword(String password) {
        try {
            // Tạo đối tượng MessageDigest với thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Chuyển mật khẩu thành mảng byte và băm nó
            byte[] hash = md.digest(password.getBytes());

            // Chuyển kết quả băm thành chuỗi hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void addControls() {
        edtMatkhau=findViewById(R.id.edtMatkhau);
        edtCheckMatkhau=findViewById(R.id.edtCheckMatkhau);
        btnCapnhat=findViewById(R.id.btnCapnhat);
    }

}