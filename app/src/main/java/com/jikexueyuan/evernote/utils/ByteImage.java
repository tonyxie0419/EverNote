package com.jikexueyuan.evernote.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by admin on 2016/12/22.
 */

public class ByteImage {

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//100表示和原图大小一样
        return baos.toByteArray();
    }

    public static Bitmap getBmp(Map<String, Bitmap> map, String imgTag) {
        return map.get(imgTag);
    }
}
