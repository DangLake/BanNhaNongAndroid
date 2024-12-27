package vn.edu.stu.bannhanong;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import vn.edu.stu.bannhanong.dao.DBHelperUsers;

public class SignIn extends AppCompatActivity {

    Button btnDangnhap2,btnDangky2;
    EditText edtSDT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        requestSmsPermission();
        addEvents();
    }

    private void addEvents() {
        btnDangnhap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDangky2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyDangky();
            }
        });
    }

    private void xulyDangky() {
        String phoneNumber = edtSDT.getText().toString().trim();

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(SignIn.this, getString(R.string.sdt_error), Toast.LENGTH_SHORT).show();
        } else {
            // Kiểm tra số điện thoại trong Firestore một cách bất đồng bộ
            DBHelperUsers dbHelper = new DBHelperUsers();
            dbHelper.isPhoneNumberExists(phoneNumber).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean exists = task.getResult();
                    if (exists) {
                        Toast.makeText(SignIn.this, getString(R.string.sdt_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        sendOtp(phoneNumber);
                    }
                } else {
                    // Xử lý lỗi khi không thể kiểm tra số điện thoại
                    Toast.makeText(SignIn.this, "Lỗi khi kiểm tra số điện thoại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void sendOtp(String phoneNumber) {
        // Kiểm tra quyền gửi SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            // Nếu đã có quyền, tiến hành gửi OTP
            int otp = (int) (Math.random() * 900000) + 100000;
            String message = getString(R.string.otp_you) + otp;
            Log.d("SignIn", message);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, getString(R.string.opt_sent_success) + phoneNumber, Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình OTP
                Intent intent = new Intent(SignIn.this, OTP.class);
                intent.putExtra("otp", otp);
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.otp_sent_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^0\\d{9}$");
    }

    private void addControls() {
        btnDangnhap2=findViewById(R.id.btnDangnhap2);
        btnDangky2=findViewById(R.id.btnDangky2);
        edtSDT=findViewById(R.id.edtSDT);
    }
    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS
            }, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            boolean sendSmsGranted = false;
            boolean receiveSmsGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.SEND_SMS)) {
                    sendSmsGranted = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
                if (permissions[i].equals(Manifest.permission.RECEIVE_SMS)) {
                    receiveSmsGranted = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (sendSmsGranted && receiveSmsGranted) {
                Toast.makeText(this, "Đã cấp quyền", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền chưa đuợc cấp", Toast.LENGTH_SHORT).show();
            }
        }
    }

}