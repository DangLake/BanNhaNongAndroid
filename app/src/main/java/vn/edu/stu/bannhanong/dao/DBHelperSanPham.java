package vn.edu.stu.bannhanong.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelperSanPham extends SQLiteOpenHelper {
    private static final String DB_NAME="bannhanong.sqlite";
    private static final int DB_VERSION=1;
    public DBHelperSanPham(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
