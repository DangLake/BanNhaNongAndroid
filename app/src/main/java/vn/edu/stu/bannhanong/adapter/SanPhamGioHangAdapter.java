package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.GiohangItem;

public class SanPhamGioHangAdapter extends RecyclerView.Adapter<SanPhamGioHangAdapter.ProductViewHolder> {
    private List<GiohangItem> products;
    private Context context;

    public SanPhamGioHangAdapter(Context context, List<GiohangItem> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang_sp, parent, false);
        return new ProductViewHolder(view);
    }

    public void onBindViewHolder(ProductViewHolder holder, int position) {
        GiohangItem product = products.get(position);

        // Truy vấn tên sản phẩm và ảnh từ Firestore (hoặc từ API)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("sanpham").document(product.getDocumentIdSanpham());

        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Lấy thông tin sản phẩm từ Firestore
                String productName = documentSnapshot.getString("tensp");  // Tên trường phải trùng với trường trong Firestore
                Long giaLong = documentSnapshot.getLong("gia");
                String dvt = documentSnapshot.getString("donvitinh");

                // Hiển thị tên sản phẩm và giá
                holder.tvProductName.setText(productName);
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(giaLong);
                holder.tvProductPrice.setText(formattedPrice + " /" + dvt);
                holder.tv_product_quantity.setText(product.getSoluong()+"");

                List<String> imageUrls = (List<String>) documentSnapshot.get("anh");  // Lấy mảng ảnh
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    String imageUri = imageUrls.get(0);  // Lấy URL ảnh đầu tiên
                    Glide.with(context)
                            .load(imageUri)
                            .placeholder(R.drawable.logo)  // Ảnh placeholder
                            .error(R.drawable.logo)  // Ảnh khi tải thất bại
                            .into(holder.imgSanPham);
                } else {
                    // Nếu không có ảnh, hiển thị ảnh mặc định
                    holder.imgSanPham.setImageResource(R.drawable.logo);
                }

            } else {
                holder.tvProductName.setText("Không tìm thấy sản phẩm");
            }
        }).addOnFailureListener(e -> holder.tvProductName.setText("Lỗi truy vấn"));
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice,tv_product_quantity;
        ImageView imgSanPham;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvTenSanPham);
            tvProductPrice = itemView.findViewById(R.id.tvGiaSanPham);
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
            tv_product_quantity=itemView.findViewById(R.id.tv_product_quantity);
        }
    }
}
