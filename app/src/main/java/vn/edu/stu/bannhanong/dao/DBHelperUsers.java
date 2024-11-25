package vn.edu.stu.bannhanong.dao;

import android.app.Activity;
import android.content.ContentValues;
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
    public boolean insertUser(String name, String phoneNumber, String password, int userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE.COL_TEN, name);
        values.put(TABLE.COL_SDT, phoneNumber);
        values.put(TABLE.COL_MATKHAU, password);
        values.put(TABLE.COL_LOAI, userType);

        long result = db.insert(TABLE.TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }
    public boolean isValidLogin(String phoneNumber, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE.TABLE_NAME + " WHERE " +
                TABLE.COL_SDT + " = ? AND " + TABLE.COL_MATKHAU + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber, password});

        boolean valid = cursor.moveToFirst();
        cursor.close();
        db.close();
        return valid;
    }






    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE.TABLE_NAME + " (" +
                TABLE.COL_MA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TABLE.COL_TEN + " TEXT, " +
                TABLE.COL_SDT + " TEXT, " +
                TABLE.COL_MATKHAU + " TEXT, " +
                TABLE.COL_DIACHI + " TEXT, " +
                TABLE.COL_LOAI + " INTEGER)";
        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
