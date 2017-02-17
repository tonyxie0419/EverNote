package com.jikexueyuan.evernote.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.evernote.MyApplication;
import com.jikexueyuan.evernote.R;

public class SetupActivity extends AppCompatActivity {

    private MyApplication appApplication;
    private ImageView userImage;
    private TextView usernameShow;
    private LinearLayout userInfoShow;
    private Toolbar toolbar;

    private static final int LOGIN_CODE = 10086;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        init();

        if (!appApplication.sharedPreferences.getString("username", "default").equals("default")) {
            usernameShow.setText(appApplication.sharedPreferences.getString("username", null));
        }
    }

    private void init() {
        appApplication = (MyApplication) getApplication();
        userImage = (ImageView) findViewById(R.id.userImage);
        usernameShow = (TextView) findViewById(R.id.username_show);
        userInfoShow = (LinearLayout) findViewById(R.id.userInfo_show);
        toolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        userInfoShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Login.isLogin()) {
                    Intent intent = new Intent(SetupActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_CODE);
                } else {
                    Toast.makeText(SetupActivity.this, "以后会增加更多功能……", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String username = data.getStringExtra("username");
                usernameShow.setText(username);
                appApplication.editor.clear();
                appApplication.editor.putString("username", username);
                appApplication.editor.apply();
                Toast.makeText(SetupActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Login.setLogin(true);
                break;
            case RESULT_CANCELED:
                Login.setLogin(false);
                Toast.makeText(SetupActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private static class Login {

        private static boolean isLogin = false;

        static boolean isLogin() {
            return isLogin;
        }

        static void setLogin(boolean login) {
            isLogin = login;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Login.setLogin(false);
            appApplication.editor.clear().putString("username","default").apply();
            usernameShow.setText(getString(R.string.login_hint));
            userImage.setImageResource(R.mipmap.ic_launcher);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
