package vn.edu.stu.bannhanong.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.Sanpham;

public class AdapterSanPhamHDND extends RecyclerView.Adapter<AdapterSanPhamHDND.SanPhamViewHolder> {
    private Context context;
    private int resource;
    private List<Sanpham> objects;

    public AdapterSanPhamHDND(Context context, int resource, List<Sanpham> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
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
        Sanpham sp = objects.get(position);
        if (holder.tvTen != null) {
            holder.tvTen.setText(sp.getTensp());
        }
        if (holder.tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sp.getGia());
            holder.tvGia.setText("Giá: " + formattedPrice + " VND/" + sp.getDonvitinh());
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    // ViewHolder class
    public static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;

        public SanPhamViewHolder(View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenSanPham);
            tvGia = itemView.findViewById(R.id.tvGiaSanPham);
        }
    }
}
