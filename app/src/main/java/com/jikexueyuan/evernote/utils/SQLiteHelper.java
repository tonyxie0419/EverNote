package com.jikexueyuan.evernote.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2016/12/13.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//        db.execSQL("DROP TABLE userData");
//        db.execSQL("DROP TABLE userImg");
//        String dataSql = "CREATE TABLE userData"
//                + "(_id INTEGER PRIMARY KEY,"
//                + " username TEXT  NOT NULL,"
//                + " title TEXT ,"
//                + " date TEXT NOT NULL,"
//                + " content TEXT)";
//        db.execSQL(dataSql);
//
//        String imgSql = "CREATE TABLE userImg"
//                + "(_id INTEGER PRIMARY KEY,"
//                + " uid INTEGER NOT NULL,"
//                + " image BLOB,"
//                + " imgTag TEXT)";
//        db.execSQL(imgSql);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String userSql = "CREATE TABLE user"
                + "(_id INTEGER PRIMARY KEY,"
                + " username TEXT  NOT NULL ,"
                + " password VARCHAR(10))";

        String dataSql = "CREATE TABLE userData"
                + "(_id INTEGER PRIMARY KEY,"
                + " username TEXT  NOT NULL,"
                + " title TEXT ,"
                + " date TEXT NOT NULL,"
                + " content TEXT)";

        String imgSql = "CREATE TABLE userImg"
                + "(_id INTEGER PRIMARY KEY,"
                + " uid INTEGER NOT NULL,"
                + " image BLOB,"
                + " imgTag TEXT)";

        sqLiteDatabase.execSQL(userSql);
        sqLiteDatabase.execSQL(dataSql);
        sqLiteDatabase.execSQL(imgSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
