package vn.edu.stu.bannhanong.cloudinary;


import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    public static void uploadImage(String imageUri, UploadCallback callback) {
        MediaManager.get().upload(imageUri)
                .unsigned("sanpham_upload") // Tùy chọn, nếu bạn sử dụng unsigned preset
                .callback(callback)
                .dispatch();
    }
    public interface CloudinaryCallback {
        void onSuccess();
        void onError(String error);
    }

    // Xóa ảnh trên Cloudinary
    public static void deleteImage(String publicId, CloudinaryCallback callback) {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "duthhwipq");
        config.put("api_key", "721938926416681");
        config.put("api_secret", "ozvYw0n11KYF9LLGs4pb1muHhsI");
        Cloudinary cloudinary = new Cloudinary(config);
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if (result != null && result.get("result").equals("ok")) {
                callback.onSuccess();
            } else {
                callback.onError("Không thể xóa ảnh: " + publicId);
            }
        } catch (Exception e) {
            callback.onError("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
}
