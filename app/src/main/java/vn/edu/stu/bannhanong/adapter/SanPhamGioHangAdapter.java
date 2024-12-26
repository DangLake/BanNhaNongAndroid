package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.dao.DBHelperGiohang;
import vn.edu.stu.bannhanong.model.GiohangItem;

public class SanPhamGioHangAdapter extends RecyclerView.Adapter<SanPhamGioHangAdapter.ProductViewHolder> {
    private List<GiohangItem> products;
    private Context context;
    private OnQuantityChangeListener quantityChangeListener;
    private String userId;

    public SanPhamGioHangAdapter(Context context, List<GiohangItem> products,String userId,OnQuantityChangeListener listener) {
        this.context = context;
        this.products = products;
        this.quantityChangeListener = listener;
        this.userId=userId;
    }
    public interface OnQuantityChangeListener {
        void onIncrease(int position, int quantity); // Xử lý khi nhấn nút "+"
        void onDecrease(int position, int quantity);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang_sp, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        GiohangItem product = products.get(position);

        // Truy vấn sản phẩm từ Firestore (giữ nguyên phần lấy tên, giá, ảnh)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("sanpham").document(product.getDocumentIdSanpham());

        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String productName = documentSnapshot.getString("tensp");
                Long giaLong = documentSnapshot.getLong("gia");
                String dvt = documentSnapshot.getString("donvitinh");

                holder.tvProductName.setText(productName);
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(giaLong);
                holder.tvProductPrice.setText(formattedPrice + " /" + dvt);
                holder.tv_product_quantity.setText(String.valueOf(product.getSoluong()));

                List<String> imageUrls = (List<String>) documentSnapshot.get("anh");
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    Glide.with(context)
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(holder.imgSanPham);
                } else {
                    holder.imgSanPham.setImageResource(R.drawable.logo);
                }
            } else {
                holder.tvProductName.setText("Không tìm thấy sản phẩm");
            }
        }).addOnFailureListener(e -> holder.tvProductName.setText("Lỗi truy vấn"));

        // Thêm DBHelperGiohang để tăng/giảm số lượng
        DBHelperGiohang dbHelper = new DBHelperGiohang();

        holder.btnTang.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí mới nhất
            if (currentPosition != RecyclerView.NO_POSITION) { // Kiểm tra vị trí hợp lệ
                GiohangItem currentItem = products.get(currentPosition);
                dbHelper.onIncrease(userId, currentItem.getDocumentIdSanpham(), currentItem.getSoluong(), new DBHelperGiohang.OnCartUpdateListener() {
                    @Override
                    public void onSuccess(int newQuantity) {
                        currentItem.setSoluong(newQuantity);
                        holder.tv_product_quantity.setText(String.valueOf(newQuantity));
                        if (quantityChangeListener != null) {
                            quantityChangeListener.onIncrease(currentPosition, newQuantity);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.btnGiam.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí mới nhất
            if (currentPosition != RecyclerView.NO_POSITION) { // Kiểm tra vị trí hợp lệ
                GiohangItem currentItem = products.get(currentPosition);
                dbHelper.onDecrease(userId, currentItem.getDocumentIdSanpham(), currentItem.getSoluong(), new DBHelperGiohang.OnCartUpdateListener() {
                    @Override
                    public void onSuccess(int newQuantity) {
                        currentItem.setSoluong(newQuantity);
                        holder.tv_product_quantity.setText(String.valueOf(newQuantity));
                        if (quantityChangeListener != null) {
                            quantityChangeListener.onDecrease(currentPosition, newQuantity);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Lỗi khi giảm số lượng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice,tv_product_quantity;
        ImageView imgSanPham;
        Button btnTang, btnGiam;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvTenSanPham);
            tvProductPrice = itemView.findViewById(R.id.tvGiaSanPham);
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
            tv_product_quantity=itemView.findViewById(R.id.tv_product_quantity);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
        }
    }
}
