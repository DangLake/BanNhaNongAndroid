package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;

public class Login extends AppCompatActivity {
    Button btnDangky,btnQuenmk,btnDangnhap;
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    String DATABASE_NAME = "bannhanong.sqlite";
    EditText edtSDT, edtMatkhau;
    DBHelperUsers dbHelperUsers;

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
        addControls();
        addEvents();
        processCopy();
        dbHelperUsers = new DBHelperUsers(this);
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
            Toast.makeText(Login.this, getString(R.string.login_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelperUsers.isValidLogin(phoneNumber, password)) {
            Toast.makeText(Login.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, Trangchu.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(Login.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void xulyQuenMK() {
        Intent intent=new Intent(Login.this, XacThucDienThoai.class);
        startActivity(intent);
    }

    private void xulyDangKy() {
        Intent intent=new Intent(Login.this, SignIn.class);
        startActivity(intent);
    }

    private void addControls() {
        btnDangky=findViewById(R.id.btnDangky);
        btnQuenmk=findViewById(R.id.btnQuenmk);
        btnDangnhap=findViewById(R.id.btnDangnhap);
        edtSDT=findViewById(R.id.edtSDT);
        edtMatkhau=findViewById(R.id.edtMatkhau);
    }
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}