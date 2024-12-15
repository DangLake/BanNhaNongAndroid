package vn.edu.stu.bannhanong.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.Sanpham;

public class AdapterSanPham extends ArrayAdapter<Sanpham> {

    Context context;
    int resource;
    List<Sanpham> dsSanPham;
    public AdapterSanPham(@NonNull Context context, int resource, @NonNull List<Sanpham> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.dsSanPham = objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(resource, parent, false);
        }
        ImageView img=rowView.findViewById(R.id.imgSanPham);
        TextView tvTen=rowView.findViewById(R.id.tvTenSanPham);
        TextView tvGia=rowView.findViewById(R.id.tvGiaSanPham);
        TextView tvDVT=rowView.findViewById(R.id.tvDonViTinh);

        Sanpham sp=dsSanPham.get(position);
        if(tvTen!=null){
            tvTen.setText(sp.getTensp());
            Log.d("SanPham", "Tên sản phẩm: " + sp.getTensp());
        }
        if (tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sp.getGia());
            tvGia.setText("Giá: " + formattedPrice + " VND /KG");
        }
        if(tvDVT!=null){
            tvDVT.setText("Đơn vị tính:  " +sp.getDonvitinh());
        }
        if (sp.getAnh() != null && !sp.getAnh().isEmpty()) {
            // Lấy URL ảnh từ Firebase Storage
            String imagePath = sp.getAnh().get(0);  // Giả sử chỉ lấy ảnh đầu tiên trong danh sách
            Log.d("ImagePath", "Đường dẫn ảnh: " + imagePath);

            // Firebase Storage reference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Tạo đường dẫn tới ảnh từ Firestore
            StorageReference imageRef = storageRef.child(imagePath); // "Pictures/taox" hoặc đường dẫn khác

            // Lấy URL của ảnh và tải ảnh bằng Glide
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("ImageURL", "URL ảnh: " + uri.toString());
                // Tải ảnh từ URL
                Glide.with(context)
                        .load(uri)
                        .into(img);
            }).addOnFailureListener(e -> {
                Log.e("ImageError", "Lỗi khi lấy URL ảnh", e);
                // Nếu không lấy được ảnh, sử dụng ảnh mặc định
                Glide.with(context)
                        .load(R.drawable.logo)  // Ảnh mặc định nếu có lỗi
                        .into(img);
            });
        } else {
            // Nếu không có ảnh, sử dụng ảnh mặc định
            Glide.with(context)
                    .load(R.drawable.logo)  // Ảnh mặc định
                    .into(img);
        }

        return rowView;
    }

}
