package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.Sanpham;

public class AdapterSanPhamHDND extends RecyclerView.Adapter<AdapterSanPhamHDND.SanPhamViewHolder> {
    private Context context;
    private int resource;
    private List<Sanpham> objects;
    private List<Boolean> isSelected;
    private List<Integer> quantities;


    public AdapterSanPhamHDND(Context context, int resource, List<Sanpham> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects != null ? objects : new ArrayList<>(); // Tránh null
        // Cập nhật lại isSelected và quantities sao cho phù hợp với kích thước của objects
        this.isSelected = new ArrayList<>(Collections.nCopies(this.objects.size(), false));
        this.quantities = new ArrayList<>(Collections.nCopies(this.objects.size(), 1));
    }

    @NonNull
    @Override
    public SanPhamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resource, parent, false);
        return new SanPhamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SanPhamViewHolder holder, int position) {
        // Lấy vị trí hiện tại của item
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) {
            return; // Nếu vị trí không hợp lệ, thoát sớm
        }

        Sanpham sp = objects.get(currentPosition);

        if (holder.tvTen != null) {
            holder.tvTen.setText(sp.getTensp());
        }

        if (holder.tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sp.getGia());
            holder.tvGia.setText("Giá: " + formattedPrice + " VND/" + sp.getDonvitinh());
        }

        // Đảm bảo không gây lỗi khi truy cập vào isSelected
        holder.checkBox.setOnCheckedChangeListener(null); // Hủy sự kiện cũ
        holder.checkBox.setChecked(isSelected.get(currentPosition)); // Cập nhật trạng thái checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> isSelected.set(currentPosition, isChecked));

        holder.tvSoLuong.setText(String.valueOf(quantities.get(currentPosition)));

        // Thêm TextWatcher chỉ một lần duy nhất cho mỗi item
        holder.tvSoLuong.removeTextChangedListener(holder.tvSoLuong.getTag() != null ? (TextWatcher) holder.tvSoLuong.getTag() : null); // Xóa TextWatcher cũ
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                try {
                    // Cập nhật số lượng trong quantities khi người dùng nhập
                    int newQuantity = Integer.parseInt(charSequence.toString());
                    if (newQuantity >= 1) {
                        quantities.set(currentPosition, newQuantity); // Cập nhật lại số lượng
                    }
                } catch (NumberFormatException e) {
                    // Nếu không phải số hợp lệ, giữ nguyên số lượng cũ
                    quantities.set(currentPosition, 1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        // Lưu TextWatcher vào tag của EditText để tránh thêm lại TextWatcher nhiều lần
        holder.tvSoLuong.setTag(textWatcher);
        holder.tvSoLuong.addTextChangedListener(textWatcher);

        holder.btnTang.setOnClickListener(v -> {
            int currentQuantity = quantities.get(currentPosition);
            quantities.set(currentPosition, currentQuantity + 1);
            holder.tvSoLuong.setText(String.valueOf(quantities.get(currentPosition)));
            notifyItemChanged(currentPosition);
        });

        holder.btnGiam.setOnClickListener(v -> {
            int currentQuantity = quantities.get(currentPosition);
            if (currentQuantity > 1) { // Không giảm xuống dưới 1
                quantities.set(currentPosition, currentQuantity - 1);
                holder.tvSoLuong.setText(String.valueOf(quantities.get(currentPosition)));
                notifyItemChanged(currentPosition);
            }
        });
    }


    public List<Pair<Sanpham, Integer>> getSelectedProductsWithQuantities() {
        List<Pair<Sanpham, Integer>> selectedProducts = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            if (isSelected.get(i)) {
                selectedProducts.add(new Pair<>(objects.get(i), quantities.get(i)));
            }
        }
        return selectedProducts;
    }

    public void updateData(List<Sanpham> newObjects) {
        this.objects = newObjects != null ? newObjects : new ArrayList<>();
        this.quantities = new ArrayList<>(Collections.nCopies(this.objects.size(), 1));
        this.isSelected = new ArrayList<>(Collections.nCopies(this.objects.size(), false));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    // ViewHolder class
    public static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;
        CheckBox checkBox;
        EditText tvSoLuong;
        Button btnTang, btnGiam;

        public SanPhamViewHolder(View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenSanPham);
            tvGia = itemView.findViewById(R.id.tvGiaSanPham);
            checkBox = itemView.findViewById(R.id.checkbox_product);
            tvSoLuong = itemView.findViewById(R.id.tvSoluong);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
        }
    }
}
