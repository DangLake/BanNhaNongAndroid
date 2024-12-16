package vn.edu.stu.bannhanong.cloudinary;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    public static void uploadImage(Uri imageUri, UploadCallback callback) {
        MediaManager.get().upload(imageUri)
                .unsigned("sanpham_upload") // Tùy chọn, nếu bạn sử dụng unsigned preset
                .callback(callback)
                .dispatch();
    }
}
