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
        if (password.isEmpty() || checkPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.dk_mk_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(checkPassword)) {
            Toast.makeText(this, getString(R.string.dk_mk_khop), Toast.LENGTH_SHORT).show();
            return;
        }
        DBHelperUsers dbHelper = new DBHelperUsers(this);
        boolean success=dbHelper.doiMatKhau(phoneNumber,password);
        if (success) {
            Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DoiMatKhau.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void addControls() {
        edtMatkhau=findViewById(R.id.edtMatkhau);
        edtCheckMatkhau=findViewById(R.id.edtCheckMatkhau);
        btnCapnhat=findViewById(R.id.btnCapnhat);
    }

}