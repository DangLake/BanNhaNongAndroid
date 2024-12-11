package vn.edu.stu.bannhanong.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            tvTen.setText(sp.getTen());
        }
        if (tvGia != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedPrice = decimalFormat.format(sp.getGia());
            tvGia.setText("Giá: " + formattedPrice + " VND /KG");
        }
        if(tvDVT!=null){
            tvDVT.setText("Đơn vị tính:  " +sp.getDonvitinh());
        }

        return rowView;
    }
}