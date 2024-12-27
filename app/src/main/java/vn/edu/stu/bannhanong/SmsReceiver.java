package vn.edu.stu.bannhanong;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    StringBuilder smsContent = new StringBuilder();
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        smsContent.append(smsMessage.getMessageBody());
                    }

                    // Hiển thị OTP trong Toast hoặc Notification
                    String receivedMessage = smsContent.toString();
                    showNotification(context, receivedMessage);

                    // Nếu cần xử lý OTP, bạn có thể truyền dữ liệu qua Intent đến Activity
                    Intent otpIntent = new Intent(context, OTP.class);
                    otpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    otpIntent.putExtra("receivedOtp", receivedMessage);
                    context.startActivity(otpIntent);
                }
            }
        }
    }
    private void showNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "otp_channel";
        String channelName = "OTP Notifications";

        // Tạo NotificationChannel nếu chạy trên Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH // Đảm bảo mức quan trọng cao
            );
            channel.setDescription("Channel for OTP notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo Notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon hiển thị
                .setContentTitle("OTP Received")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Mức độ ưu tiên cao
                .setAutoCancel(true) // Đóng thông báo sau khi người dùng nhấn vào
                .build();

        notificationManager.notify(1, notification);
    }

}
