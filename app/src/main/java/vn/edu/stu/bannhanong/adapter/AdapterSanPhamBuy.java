package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.model.Sanpham;

public class AdapterSanPhamBuy extends ArrayAdapter<Sanpham> {
    Context context;
    int resource;
    List<Sanpham> dsSanPham;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;
    public AdapterSanPhamBuy(@NonNull Context context, int resource, @NonNull List<Sanpham> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.dsSanPham = objects;
        this.dbHelperSanPhamBuy = new DBHelperSanPhamBuy();
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
        TextView tvDiachi=rowView.findViewById(R.id.tvDiachi);

        Sanpham sp=dsSanPham.get(position);
        if(tvTen!=null){
            tvTen.setText(sp.getTensp());
        }
        if (tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sp.getGia());
            tvGia.setText("Giá: " + formattedPrice + " VND/"+sp.getDonvitinh());
        }
        if(tvDVT!=null){
            tvDVT.setText("Đơn vị tính:  " +sp.getDonvitinh());
        }
        if (tvDiachi != null && sp.getIduser() != null) {
            dbHelperSanPhamBuy.getUserAddress(sp.getIduser(), tvDiachi);
        }

        if (sp.getAnh() != null && !sp.getAnh().isEmpty()) {
            // Lấy URL ảnh từ danh sách URL đã lưu trong Firestore (URL từ Cloudinary)
            String imageUri = sp.getAnh().get(0);  // Lấy URL ảnh đầu tiên
            Glide.with(context)
                    .load(imageUri)
                    .into(img);
        } else {
            // Nếu không có ảnh, load ảnh mặc định
            Glide.with(context)
                    .load(R.drawable.logo)
                    .into(img);
        }
        return rowView;
    }
}
