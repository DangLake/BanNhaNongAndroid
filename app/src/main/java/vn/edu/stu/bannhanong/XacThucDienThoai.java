package vn.edu.stu.bannhanong;

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

public class XacThucDienThoai extends AppCompatActivity {

    EditText edtSDT;
    Button btnSendOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xac_thuc_dien_thoai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xulyQuenMK();
            }
        });
    }

    private void xulyQuenMK() {
        String phoneNumber = edtSDT.getText().toString().trim();

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(XacThucDienThoai.this, getString(R.string.sdt_error), Toast.LENGTH_SHORT).show();
        } else {
            // Kiểm tra số điện thoại trong Firestore
            DBHelperUsers dbHelper = new DBHelperUsers();
            dbHelper.isPhoneNumberExists(phoneNumber).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean exists = task.getResult();
                    if (!exists) {
                        Toast.makeText(XacThucDienThoai.this, getString(R.string.sdt_noexists), Toast.LENGTH_SHORT).show();
                    } else {
                        sendOtp(phoneNumber);
                    }
                } else {
                    Toast.makeText(XacThucDienThoai.this, "Lỗi khi kiểm tra số điện thoại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^0\\d{9}$");
    }
    private void sendOtp(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
        } else {
            int otp = (int) (Math.random() * 900000) + 100000;
            String message = getString(R.string.otp_you) + otp;
            Log.d("SignIn", getString(R.string.otp_you) + otp);
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, getString(R.string.opt_sent_success) + phoneNumber, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(XacThucDienThoai.this, OTP2.class);
                intent.putExtra("otp", otp);
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.otp_sent_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addControls() {
        edtSDT=findViewById(R.id.edtSDT);
        btnSendOTP=findViewById(R.id.btnSendOTP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = edtSDT.getText().toString().trim();
                if (isValidPhoneNumber(phoneNumber)) {
                    sendOtp(phoneNumber);
                } else {
                    Toast.makeText(this, getString(R.string.sdt_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.permission_send_otp), Toast.LENGTH_SHORT).show();
            }
        }
    }
}