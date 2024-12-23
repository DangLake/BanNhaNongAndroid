package vn.edu.stu.bannhanong;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.adapter.AdapterSanPhamBuy;
import vn.edu.stu.bannhanong.dao.DBHelperSanPhamBuy;
import vn.edu.stu.bannhanong.model.Sanpham;

public class SanPhamFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    AdapterSanPhamBuy adapterSanPhamBuy;
    List<Sanpham> dsSanPham;
    DBHelperSanPhamBuy dbHelperSanPhamBuy;
    ListView lvSanPhamBuy;


    public SanPhamFragment() {

    }

    public static SanPhamFragment newInstance(String param1, String param2) {
        SanPhamFragment fragment = new SanPhamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_san_pham, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvSanPhamBuy=view.findViewById(R.id.lvSanPhamBuy);
        dbHelperSanPhamBuy=new DBHelperSanPhamBuy();
        dsSanPham=new ArrayList<>();
        adapterSanPhamBuy=new AdapterSanPhamBuy(getActivity(),R.layout.item_sanpham_khach,dsSanPham);
        lvSanPhamBuy.setAdapter(adapterSanPhamBuy);
        loadSanphamBuy();
    }

    private void loadSanphamBuy() {
        dbHelperSanPhamBuy.getAllProducts(new DBHelperSanPhamBuy.ProductCallback() {
            @Override
            public void onSuccess(List<Sanpham> productList) {
                dsSanPham.clear();
                dsSanPham.addAll(productList);
                adapterSanPhamBuy.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Sản phẩm trống", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadSanphamBuy();
    }
}