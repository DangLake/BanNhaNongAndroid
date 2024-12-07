package vn.edu.stu.bannhanong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.stu.bannhanong.dao.DBHelperUsers;

public class OTPCapNhat extends AppCompatActivity {

    EditText otp_1, otp_2, otp_3, otp_4, otp_5, otp_6;
    TextView resend_otp;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpcap_nhat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private String getEnteredOtp() {
        return otp_1.getText().toString().trim() +
                otp_2.getText().toString().trim() +
                otp_3.getText().toString().trim() +
                otp_4.getText().toString().trim() +
                otp_5.getText().toString().trim() +
                otp_6.getText().toString().trim();
    }

    private void addEvents() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtp();
            }
        });
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
            }
        });
    }

    private void resendOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        String phoneNumber = getIntent().getStringExtra("phone_number"); // Số mới

        if (phoneNumber != null) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, getString(R.string.otp_you) + otp, null, null);
                Toast.makeText(this, getString(R.string.otp_new_resend), Toast.LENGTH_SHORT).show();
                Log.d("OTP Update", "Sent OTP: " + otp + " to " + phoneNumber);
                getIntent().putExtra("otp", otp);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.otp_dont_new_resend) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.otp_find_sdt), Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyOtp() {
        String enteredOtp = getEnteredOtp();
        int sentOtp = getIntent().getIntExtra("otp", -1);
        String phoneNumber = getIntent().getStringExtra("phone_number"); // Số mới
        String userName = getIntent().getStringExtra("ten");
        String userAddress = getIntent().getStringExtra("diachi");
        String quan = getIntent().getStringExtra("quan");
        String tinh = getIntent().getStringExtra("tinh");
        int userId = getIntent().getIntExtra("userId", -1);

        if (enteredOtp.isEmpty() || enteredOtp.length() < 6) {
            Toast.makeText(this, getString(R.string.otp_input), Toast.LENGTH_SHORT).show();
            return;
        }

        if (String.valueOf(sentOtp).equals(enteredOtp)) {
            Toast.makeText(this, getString(R.string.otp_success), Toast.LENGTH_SHORT).show();

            // Cập nhật thông tin người dùng với số mới
            DBHelperUsers dbHelperUsers = new DBHelperUsers(this);
            dbHelperUsers.updateUser(userId, userName, phoneNumber, userAddress,quan,tinh);

            // Gửi kết quả về Fragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("userId", userId);
            resultIntent.putExtra("newPhone", phoneNumber);
            resultIntent.putExtra("ten", userName);
            resultIntent.putExtra("diachi", userAddress);
            resultIntent.putExtra("quan", quan);
            resultIntent.putExtra("tinh", tinh);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.otp_failed), Toast.LENGTH_SHORT).show();
        }
    }


    private void addControls() {
        otp_1 = findViewById(R.id.otp_1);
        otp_2 = findViewById(R.id.otp_2);
        otp_3 = findViewById(R.id.otp_3);
        otp_4 = findViewById(R.id.otp_4);
        otp_5 = findViewById(R.id.otp_5);
        otp_6 = findViewById(R.id.otp_6);
        resend_otp = findViewById(R.id.resend_otp);
        btn_submit = findViewById(R.id.btn_submit);
        setupOtpAutoFocus();
    }

    private void setupOtpAutoFocus() {
        otp_1.addTextChangedListener(new GenericTextWatcher(otp_1, otp_2));
        otp_2.addTextChangedListener(new GenericTextWatcher(otp_2, otp_3));
        otp_3.addTextChangedListener(new GenericTextWatcher(otp_3, otp_4));
        otp_4.addTextChangedListener(new GenericTextWatcher(otp_4, otp_5));
        otp_5.addTextChangedListener(new GenericTextWatcher(otp_5, otp_6));
        otp_6.addTextChangedListener(new GenericTextWatcher(otp_6, null));
    }
}
