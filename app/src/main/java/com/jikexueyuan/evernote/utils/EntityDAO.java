package com.jikexueyuan.evernote.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jikexueyuan.evernote.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/14.
 */

public class EntityDAO {

    private SQLiteHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public EntityDAO(Context context) {
        helper = new SQLiteHelper(context, "my.db", null, 1);
        this.context = context;
    }

    public long add(Entity entity) {
        System.out.println("add");
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", entity.getUsername());
        cv.put("title", entity.getTitle());
        cv.put("content", entity.getContent());
        cv.put("date", entity.getDate());
        return db.insert("userData", null, cv);
    }

    public void addImg(Entity entity) {
        System.out.println("addImg");
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < entity.getBitmaps().size(); i++) {
            cv.put("uid", entity.getId());
            cv.put("image", ByteImage.bitmapToByte(entity.getBitmaps().get(i)));
            cv.put("imgTag", entity.getImgTags().get(i));
            db.insert("userImg", null, cv);
        }
    }

    public void updateImg(Entity entity) {
        List<String> imgTags = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        System.out.println("update img");

        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from userImg where uid='" + entity.getId() + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                imgTags.add(cursor.getString(cursor.getColumnIndex("imgTag")));
                byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
                bitmaps.add(BitmapFactory.decodeByteArray(in, 0, in.length));
            } while (cursor.moveToNext());
            System.out.println("entity: " + entity.getImgTags());
            System.out.println(imgTags);
            for (int i = 0; i < entity.getImgTags().size(); i++) {
                for (int j = 0; j < imgTags.size(); j++) {
                    if (entity.getImgTags().get(i).equals(imgTags.get(j))) {
                        entity.getImgTags().remove(i);
                        entity.getBitmaps().remove(i);
                        imgTags.remove(j);
                        bitmaps.remove(j);
                        i--;
                    }
                }
            }
            cursor.close();
        }
        System.out.println("entity: " + entity.getImgTags());
        System.out.println(imgTags);
        if (entity.getImgTags().size() > 0) {
            addImg(entity);
        }
        if (imgTags.size() > 0) {
            deleteImgTag(imgTags);
        }
    }

    public void update(Entity entity) {
        System.out.println("update");
        db = helper.getWritableDatabase();
        db.execSQL("update userData set title='" + entity.getTitle() + "' where _id ='" + entity.getId() + "'");
        db.execSQL("update userData set content='" + entity.getContent() + "' where _id ='" + entity.getId() + "'");
        db.execSQL("update userData set date='" + entity.getDate() + "' where _id ='" + entity.getId() + "'");
    }

    public Entity find(int id) {
        String title = null;
        String content = null;
        String date = null;
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from userData where  _id='" + id + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            cursor.close();
        }
        return new Entity(title, content, date, id);
    }

    public List<Entity> findAll(String username) {
        String title;
        String content;
        String date;
        int id;
        List<Entity> list = new ArrayList<>();

        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from userData where username='" + username + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                title = cursor.getString(cursor.getColumnIndex("title"));
                date = cursor.getString(cursor.getColumnIndex("date"));
                content = cursor.getString(cursor.getColumnIndex("content"));
                id = cursor.getInt(cursor.getColumnIndex("_id"));
                list.add(new Entity(title, content, date, id));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Bitmap> findImg(int uid) {

        List<Bitmap> bitmaps = new ArrayList<>();

        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from userImg where uid='" + uid + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                byte[] in = cursor.getBlob(cursor.getColumnIndex("image"));
                bitmaps.add(BitmapFactory.decodeByteArray(in, 0, in.length));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return bitmaps;
    }

    public List<String> findImgTag(int uid) {

        List<String> imgTags = new ArrayList<>();

        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from userImg where uid='" + uid + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                imgTags.add(cursor.getString(cursor.getColumnIndex("imgTag")));
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return imgTags;
    }

    /**
     * 删除图片数据库中的数据
     * @param imgTags
     */
    public void deleteImgTag(List<String> imgTags) {

        db = helper.getWritableDatabase();

        for (int i = 0; i < imgTags.size(); i++) {
            db.execSQL("delete from userImg where imgTag='" + imgTags.get(i) + "'");
        }
    }

    /**
     * 删除列表项
     * @param id
     */
    public void delete(int id) {

        db = helper.getWritableDatabase();

        db.execSQL("delete from userData where _id='" + id + "'");
        List<String> imgTags = findImgTag(id);
        if (imgTags.size() > 0) {
            System.out.println("deleteImg");
            db.execSQL("delete from userImg where uid='" + id + "'");
        }
    }
}
