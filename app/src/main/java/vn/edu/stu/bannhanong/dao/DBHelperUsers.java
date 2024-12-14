package vn.edu.stu.bannhanong.dao;

import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;

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
    public Task<String> getDocumentIdByPhoneNumber(String phoneNumber) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        taskCompletionSource.setResult(documentId);
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

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

        firestore.collection("users")
                .whereEqualTo("sdt", phoneNumber)
                .whereEqualTo("matkhau", password)
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


    public Task<Void> updateUser(String documentId, String userName, String userPhone, String userAddress, String quan, String tinh) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        firestore.collection("users")
                .document(documentId)
                .update(
                        "tenuser", userName,
                        "sdt", userPhone,
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
                });

        return taskCompletionSource.getTask();
    }




}