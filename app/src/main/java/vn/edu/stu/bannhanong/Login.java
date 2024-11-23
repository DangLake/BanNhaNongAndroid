package vn.edu.stu.bannhanong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    Button btnDangky,btnQuenmk;
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
    }

    private void xulyQuenMK() {
        Intent intent=new Intent(Login.this, SignIn.class);
        startActivity(intent);
    }

    private void xulyDangKy() {
        Intent intent=new Intent(Login.this, SignIn.class);
        startActivity(intent);
    }

    private void addControls() {
        btnDangky=findViewById(R.id.btnDangky);
        btnQuenmk=findViewById(R.id.btnQuenmk);
    }
}