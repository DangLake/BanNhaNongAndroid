package vn.edu.stu.bannhanong;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.bannhanong.adapter.AdapterSanPham;
import vn.edu.stu.bannhanong.dao.DBHelperSanPham;
import vn.edu.stu.bannhanong.model.Sanpham;

public class ProductFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btnPostProduct;
    ListView lvSanpham;
    AdapterSanPham adapterSanPham;
    List<Sanpham> dsSP;
    DBHelperSanPham dbHelperSanPham;
    private String mParam1;
    private String mParam2;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPostProduct=view.findViewById(R.id.btnPostProduct);
        lvSanpham=view.findViewById(R.id.lvSanpham);
        dsSP=new ArrayList<>();
        adapterSanPham=new AdapterSanPham(getActivity(),R.layout.item_sanpham,dsSP);
        lvSanpham.setAdapter(adapterSanPham);
        dbHelperSanPham=new DBHelperSanPham();

        addEvents();
        loadSanpham();
    }

    private void loadSanpham() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String documentIDuser = sharedPreferences.getString("documentID", "");

        dbHelperSanPham.getSanphamByUserID(documentIDuser, new DBHelperSanPham.FirestoreCallback() {
            @Override
            public void onCallback(List<Sanpham> sanphamList) {
                dsSP.clear();
                dsSP.addAll(sanphamList);
                adapterSanPham.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Sản phẩm trống", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void addEvents() {
        btnPostProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), DangBanSP.class);
                startActivity(intent);
            }
        });
        lvSanpham.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sanpham sp=dsSP.get(i);
                xulyXoa(sp);
                return true;
            }
        });
    }

    private void xulyXoa(Sanpham sp) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Xóa?")
                .setMessage("Có chắc xóa sản phẩm này?")
                .setCancelable(false)
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelperSanPham.deleteSanpham(sp.getDocumentId(), new DBHelperSanPham.FirestoreCallback() {
                            @Override
                            public void onCallback(List<Sanpham> sanphamList) {
                                // Cập nhật UI sau khi xóa thành công
                                Toast.makeText(getActivity(), "Sản phẩm đã được xóa!", Toast.LENGTH_SHORT).show();
                                // Cập nhật lại danh sách sản phẩm, ví dụ:
                                dsSP.remove(sp); // Xóa sản phẩm khỏi danh sách
                                adapterSanPham.notifyDataSetChanged(); // Thông báo cho adapter cập nhật lại UI
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Xử lý khi xóa thất bại
                                Toast.makeText(getActivity(), "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Không",null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSanpham();
    }
}