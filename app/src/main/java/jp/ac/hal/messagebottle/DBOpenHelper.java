package jp.ac.hal.messagebottle;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by muto.masakazu on 2017/09/09.
 */



public class DBOpenHelper extends SQLiteOpenHelper {

    private Context m_context;
    public static final String TAG = "DBOpenHelper";
    private static final String DB_NAME = "android_sqlite_demo";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.m_context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブルの作成
        String sql = "create table userfile(" +
                "_id integer primary key autoincrement," +
                "filenum integer not null)";

        db.execSQL(sql);

        ContentValues values = new ContentValues();
        values.put("filenum",0);
        db.insert("filenum",null,values);
        values.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
