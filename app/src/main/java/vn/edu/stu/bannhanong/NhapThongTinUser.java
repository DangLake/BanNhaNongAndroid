package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;

public class NhapThongTinUser extends AppCompatActivity {
    EditText edtName,edtMatkhau,edtCheckMatkhau;
    RadioButton rdoNguoimua,rdoDoanhnghiep,rdoNongdan;
    Button btnDangky2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhap_thong_tin_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnDangky2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyDangKy();
            }
        });
    }

    private void xulyDangKy() {
        // Lấy thông tin từ các trường nhập liệu
        String name = edtName.getText().toString().trim();
        String password = edtMatkhau.getText().toString().trim();
        String checkPassword = edtCheckMatkhau.getText().toString().trim();

        // Lấy số điện thoại từ Intent (đã được gửi từ OTP)
        String phoneNumber = getIntent().getStringExtra("phone_number");

        // Kiểm tra tính hợp lệ của các trường dữ liệu
        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.dk_ten_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || checkPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.dk_mk_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(checkPassword)) {
            Toast.makeText(this, getString(R.string.dk_mk_khop), Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác định loại người dùng
        int userType; // Giá trị mặc định cho Nông dân
        if (rdoNguoimua.isChecked()) {
            userType = 0; // Người mua
        } else if (rdoDoanhnghiep.isChecked()) {
            userType = 1; // Doanh nghiệp
        } else {
            userType = 2;
        }

        // Kiểm tra nếu số điện thoại đã tồn tại trong Firestore
        DBHelperUsers dbHelper = new DBHelperUsers();
        dbHelper.isPhoneNumberExists(phoneNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()) {
                        // Nếu số điện thoại đã tồn tại, thông báo lỗi
                        Toast.makeText(this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        // Nếu số điện thoại chưa tồn tại, thêm người dùng vào Firestore
                        dbHelper.insertUser(name, phoneNumber, password,null,null,null, userType, new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    // Đăng ký thành công
                                    Toast.makeText(NhapThongTinUser.this, getString(R.string.signin_success), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NhapThongTinUser.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Thêm người dùng thất bại
                                    Toast.makeText(NhapThongTinUser.this, getString(R.string.signin_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi kiểm tra số điện thoại
                    Toast.makeText(this, "Kiểm tra số điện thoại lỗi", Toast.LENGTH_SHORT).show();
                });
    }

    private void addControls() {
        edtName=findViewById(R.id.edtName);
        edtMatkhau=findViewById(R.id.edtMatkhau);
        edtCheckMatkhau=findViewById(R.id.edtCheckMatkhau);
        rdoNguoimua=findViewById(R.id.rdoNguoimua);
        rdoDoanhnghiep=findViewById(R.id.rdoDoanhnghiep);
        rdoNongdan=findViewById(R.id.rdoNongdan);
        btnDangky2=findViewById(R.id.btnDangky2);
    }
}