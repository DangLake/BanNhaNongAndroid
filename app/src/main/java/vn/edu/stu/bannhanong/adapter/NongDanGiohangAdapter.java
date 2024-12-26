package vn.edu.stu.bannhanong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.GiohangItem;
import vn.edu.stu.bannhanong.model.GiohangNongdan;

public class NongDanGiohangAdapter extends RecyclerView.Adapter<NongDanGiohangAdapter.FarmerViewHolder> {
    private List<GiohangNongdan> farmers;
    private String userId;
    private SanPhamGioHangAdapter.OnQuantityChangeListener quantityChangeListener;

    public NongDanGiohangAdapter(List<GiohangNongdan> farmers, String userId, SanPhamGioHangAdapter.OnQuantityChangeListener quantityChangeListener) {
        this.farmers = farmers;
        this.userId = userId;
        this.quantityChangeListener = quantityChangeListener;
    }

    @Override
    public FarmerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang_nongdan, parent, false);
        return new FarmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FarmerViewHolder holder, int position) {
        GiohangNongdan farmer = farmers.get(position);
        holder.tvFarmerName.setText(farmer.getFarmerName());

        // Hiển thị danh sách sản phẩm của nông dân
        SanPhamGioHangAdapter productAdapter = new SanPhamGioHangAdapter(
                holder.itemView.getContext(),
                farmer.getProductList(),
                userId,
                quantityChangeListener
        );
        holder.rvProductList.setAdapter(productAdapter);

    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    public static class FarmerViewHolder extends RecyclerView.ViewHolder {
        TextView tvFarmerName;
        RecyclerView rvProductList;

        public FarmerViewHolder(View itemView) {
            super(itemView);
            tvFarmerName = itemView.findViewById(R.id.tv_farmer_name);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
