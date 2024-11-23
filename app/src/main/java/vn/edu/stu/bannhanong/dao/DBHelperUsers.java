package vn.edu.stu.bannhanong.dao;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelperUsers extends SQLiteOpenHelper {

    private static final String DB_NAME="bannhanong.sqlite";
    private static final int DB_VERSION=1;

    public DBHelperUsers(Activity context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public class TABLE implements BaseColumns {
        private static final String TABLE_NAME="users";
        private static final String COL_MA="id";
        private static final String COL_TEN="tenuser";
        private static final String COL_SDT="sdt";
        private static final String COL_MATKHAU="matkhau";
        private static final String COL_DIACHI="diachi";
        private static final String COL_LOAI="maloai";

    }
    public boolean isPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE.TABLE_NAME + " WHERE " + TABLE.COL_SDT + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
