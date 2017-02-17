package com.jikexueyuan.evernote;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2016/12/15.
 */

public class MyApplication extends Application {

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        sharedPreferences = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        super.onCreate();
    }
}
