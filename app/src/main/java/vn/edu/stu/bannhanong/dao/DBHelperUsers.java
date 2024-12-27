package vn.edu.stu.bannhanong.dao;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import vn.edu.stu.bannhanong.model.Users;

public class DBHelperUsers {
    private FirebaseFirestore firestore;

    public DBHelperUsers() {
        firestore = FirebaseFirestore.getInstance();
    }

    public Task<Boolean> isPhoneNumberExists(String phoneNumber) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        taskCompletionSource.setResult(exists);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }


    public void insertUser(String name, String phoneNumber, String password,String diachi,String quanhuyen,String tinh, int userType, OnCompleteListener<DocumentReference> listener) {
        Map<String, Object> user = new HashMap<>();
        user.put("tenuser", name);
        user.put("sdt", phoneNumber);
        user.put("matkhau", password);
        user.put("maloai", userType);
        user.put("diachi", diachi);
        user.put("tinh", tinh);
        user.put("quanhuyen", quanhuyen);

        // Thêm người dùng vào Firestore và lấy tài liệu đã thêm
        firestore.collection("users").add(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference documentReference = task.getResult();
                listener.onComplete(task);  // Gọi listener với task đã hoàn thành
            } else {
                listener.onComplete(task);  // Gọi listener nếu có lỗi
            }
        });
    }
    public Task<String> getUserDocumentIdByPhoneNumber(String phoneNumber) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber) // Tìm document có số điện thoại khớp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Lấy document đầu tiên khớp với số điện thoại
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        taskCompletionSource.setResult(documentId);
                    } else {
                        // Không tìm thấy user hoặc có lỗi xảy ra
                        taskCompletionSource.setException(new Exception("Không tìm thấy người dùng với số điện thoại: " + phoneNumber));
                    }
                })
                .addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }




    public Task<Void> doiMatKhau(String phoneNumber, String newPassword) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            firestore.collection("users").document(document.getId())
                                    .update("matkhau", newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            taskCompletionSource.setResult(null);
                                        } else {
                                            taskCompletionSource.setException(updateTask.getException());
                                        }
                                    });
                        }
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }


    public Task<Boolean> isValidLogin(String phoneNumber, String password) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        String hashPassword = hashPassword(password);
        if (hashPassword == null) {
            return Tasks.forException(new Exception("Lỗi mã hóa mật khẩu"));
        }

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .whereEqualTo("matkhau", hashPassword)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean valid = !task.getResult().isEmpty();
                        taskCompletionSource.setResult(valid);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }
    private String hashPassword(String password) {
        try {
            // Tạo đối tượng MessageDigest với thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Chuyển mật khẩu thành mảng byte và băm nó
            byte[] hash = md.digest(password.getBytes());

            // Chuyển kết quả băm thành chuỗi hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Task<Users> getUserByPhoneNumber(String phoneNumber) {
        TaskCompletionSource<Users> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot doc = task.getResult().iterator().next();
                        Users user = new Users(
                                doc.getString("tenuser"),
                                doc.getString("sdt"),
                                doc.getString("matkhau"),
                                doc.getString("diachi"),
                                doc.getString("quanhuyen"),
                                doc.getString("tinh"),
                                doc.getLong("maloai").intValue()
                        );
                        taskCompletionSource.setResult(user);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }


    public Task<Void> updateUser(String documentId, String userName, String phoneNumber, String userAddress, String quan, String tinh) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        // Cập nhật thông tin trực tiếp qua documentId
        firestore.collection("users")
                .document(documentId)
                .update(
                        "tenuser", userName,
                        "sdt", phoneNumber,
                        "diachi", userAddress,
                        "quanhuyen", quan,
                        "tinh", tinh
                )
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        taskCompletionSource.setResult(null);
                    } else {
                        taskCompletionSource.setException(updateTask.getException());
                    }
                })
                .addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }



}