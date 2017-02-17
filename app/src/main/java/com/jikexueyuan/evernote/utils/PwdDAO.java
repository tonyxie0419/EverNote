package com.jikexueyuan.evernote.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.jikexueyuan.evernote.model.UserInfo;

/**
 * Created by admin on 2016/12/13.
 */

public class PwdDAO {

    private SQLiteHelper helper;
    private SQLiteDatabase db;
    private Context context;
    private static final int RESULT_CODE_OK = 0;
    private static final int RESULT_CODE_NO_USER = 1;
    private static final int RESULT_CODE_WRONG_PASSWORD = 2;

    public PwdDAO(Context context) {
        helper = new SQLiteHelper(context, "my.db", null, 1);
        this.context = context;
    }

    public void add(UserInfo userInfo) {
        db = helper.getWritableDatabase();
        String username = userInfo.getUsername();
        Cursor cursor = db.rawQuery("select username from user where username='" + username + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            Toast.makeText(context, "用户名已存在", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put("username", userInfo.getUsername());
            cv.put("password", userInfo.getPassword());
            long id = db.insert("user", null, cv);
            if (id != -1) {
                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        db.close();
    }

    public int find(UserInfo userInfo) {
        int result;
        db = helper.getWritableDatabase();
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        Cursor cursorName = db.rawQuery("select username from user where username='" + username + "'", null);
        if (cursorName != null && cursorName.getCount() > 0) {
            Cursor cursor = db.rawQuery("select password from user where username='" + username + "'", null);
            cursor.moveToFirst();
            if (password.equals(cursor.getString(cursor.getColumnIndex("password")))) {
                cursor.close();
                result = RESULT_CODE_OK;
            } else {
                cursor.close();
                result = RESULT_CODE_WRONG_PASSWORD;
            }
            cursor.close();
        } else {
            result = RESULT_CODE_NO_USER;
        }
        cursorName.close();
        db.close();
        return result;
    }
}
