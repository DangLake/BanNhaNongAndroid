package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.Sanpham;

public class AdapterSanPhamcuaNongDan extends RecyclerView.Adapter<AdapterSanPhamcuaNongDan.ImageViewHolder> {

    private Context context;
    private List<Sanpham> sanPhamList; // Danh sách sản phẩm

    public AdapterSanPhamcuaNongDan(Context context, List<Sanpham> sanPhamList) {
        this.context = context;
        this.sanPhamList = sanPhamList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout của item sản phẩm
        View view = LayoutInflater.from(context).inflate(R.layout.item_sanpham_cua_nd, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Sanpham sanPham = sanPhamList.get(position);

        if (sanPham != null) {
            holder.tvTenSp.setText(sanPham.getTensp() != null ? sanPham.getTensp() : "Tên không xác định");

            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sanPham.getGia());
            holder.tvGia.setText(formattedPrice + " /" + (sanPham.getDonvitinh() != null ? sanPham.getDonvitinh() : "N/A"));

            if (sanPham.getAnh() != null && !sanPham.getAnh().isEmpty()) {
                String imageUri = sanPham.getAnh().get(0); // URL ảnh đầu tiên
                Glide.with(context)
                        .load(imageUri)
                        .into(holder.imgAnh);
            } else {
                Glide.with(context)
                        .load(R.drawable.logo) // Ảnh mặc định
                        .into(holder.imgAnh);
            }
        }
    }


    @Override
    public int getItemCount() {
        return sanPhamList.size(); // Trả về số lượng sản phẩm
    }

    // Định nghĩa lớp ViewHolder
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSp, tvGia, tvDanhGia;
        ImageView imgAnh;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tham chiếu đến các View trong layout item_sanpham
            tvTenSp = itemView.findViewById(R.id.tvTenSp);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvDanhGia = itemView.findViewById(R.id.tvDanhgia);
            imgAnh = itemView.findViewById(R.id.imgAnh);
        }
    }
}

