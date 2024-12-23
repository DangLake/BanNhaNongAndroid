package vn.edu.stu.bannhanong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.stu.bannhanong.R;
import vn.edu.stu.bannhanong.model.Image;

public class AdapterImageSlide extends PagerAdapter {
    private Context context;
    private List<Image> dsPhoto;

    public AdapterImageSlide(Context context, List<Image> listphoto) {
        this.context=context;
        this.dsPhoto=listphoto;
    }

    public void setImages(List<Image> images) {
        this.dsPhoto = images;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo, container, false);
        ImageView img = view.findViewById(R.id.imgSP);
        Image anh = dsPhoto.get(position);

        if (anh != null && anh.getAnh() != null) {
            Glide.with(context)
                    .load(anh.getAnh())
                    .into(img);
        } else {
            Glide.with(context)
                    .load(R.drawable.logo) // Ảnh mặc định
                    .into(img);
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(dsPhoto!=null){
            return dsPhoto.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);

    }
}
