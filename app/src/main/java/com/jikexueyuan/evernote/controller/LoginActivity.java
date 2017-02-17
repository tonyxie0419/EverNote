package com.jikexueyuan.evernote.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.evernote.utils.PwdDAO;
import com.jikexueyuan.evernote.R;
import com.jikexueyuan.evernote.model.UserInfo;

/**
 * 用户名/密码登录界面
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private static final int RESULT_CODE_OK = 0;
    private static final int RESULT_CODE_NO_USER = 1;
    private static final int RESULT_CODE_WRONG_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mUsernameLoginButton = (Button) findViewById(R.id.username_login_button);
        Button mUsernameRegButton = (Button) findViewById(R.id.username_reg_button);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mUsernameLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mUsernameRegButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegist();
            }
        });
    }

    /**
     * 注册
     */
    private void attemptRegist() {

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_username_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }else{
            PwdDAO pwdDao = new PwdDAO(LoginActivity.this);
            pwdDao.add(new UserInfo(username, password));
        }
    }

    /**
     * 登录
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_username_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_password_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: 可以设置更复杂的逻辑
        return username.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        //TODO: 可以设置更复杂的逻辑
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            UserInfo userInfo = new UserInfo(mUsername, mPassword);
            PwdDAO pwdDAO = new PwdDAO(LoginActivity.this);
            return pwdDAO.find(userInfo);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mAuthTask = null;
            switch (integer) {
                case RESULT_CODE_OK:
                    Intent intent = new Intent();
                    intent.putExtra("username", mUsername);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case RESULT_CODE_WRONG_PASSWORD:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.setText("");
                    mPasswordView.requestFocus();
                    break;
                case RESULT_CODE_NO_USER:
                    Toast.makeText(LoginActivity.this, "用户名不存在，请注册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled(Integer integer) {
            mAuthTask = null;
        }
    }
}

