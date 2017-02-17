package com.jikexueyuan.evernote.controller;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jikexueyuan.evernote.MyApplication;
import com.jikexueyuan.evernote.model.Entity;
import com.jikexueyuan.evernote.R;
import com.jikexueyuan.evernote.utils.EntityDAO;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Toolbar etToolbar;
    private EditText et_etTitle;
    private EditText et_etContent;
    private ImageButton btnPhoto;
    private ImageButton btnCamera;
    private MyApplication appApplication;
    int _id;
    EntityDAO entityDAO;
    private static final int PICK_PIC = 10010;
    private static final int PICK_CAMERA = 10011;
    private static final int PICK_VIDEO = 10012;
    private List<String> imgTags;
    private List<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        setOnClick();
    }

    public void init() {
        appApplication = (MyApplication) getApplication();
        etToolbar = (Toolbar) findViewById(R.id.et_toolbar);
        et_etTitle = (EditText) findViewById(R.id.et_etTitle);
        et_etContent = (EditText) findViewById(R.id.et_etContent);
        btnPhoto = (ImageButton) findViewById(R.id.et_btnPhoto);
        btnCamera = (ImageButton) findViewById(R.id.et_btnCamera);
        EditTag.setEditTag(false);
        imgTags = new ArrayList<>();
        bitmaps = new ArrayList<>();

        _id = getIntent().getIntExtra("id", -1);
        entityDAO = new EntityDAO(getApplicationContext());

        //显示详情
        if (_id != -1) {
            Entity entity = entityDAO.find(_id);
            String content = entity.getContent();
            et_etTitle.setText(entity.getTitle());

            /**
             * 实现图文混排
             */
            List<Bitmap> bitmapTemps = entityDAO.findImg(_id);
            List<String> imgTagTemps = entityDAO.findImgTag(_id);

            SpannableStringBuilder builder = new SpannableStringBuilder(content);

            if (bitmapTemps.size() > 0) {
                for (int i = 0; i < imgTagTemps.size(); i++) {
                    //需要替换的字符
                    String imgTag = imgTagTemps.get(i);
                    Bitmap bitmap = bitmapTemps.get(i);
                    Pattern pattern = Pattern.compile(imgTag);
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        imgTags.add(imgTag);
                        bitmaps.add(bitmap);
                        ImageSpan imageSpan = new ImageSpan(this, bitmap);
                        builder.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            et_etContent.setText(builder);
        }

        setSupportActionBar(etToolbar);
    }

    public void setOnClick() {
        et_etTitle.setOnClickListener(this);
        et_etContent.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        etToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    /**
     * 返回按钮
     */
    public void back() {
        final String title = et_etTitle.getText().toString();
        final String content = et_etContent.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date currentDate = new Date();
        final String date = sdf.format(currentDate);
        final String username = appApplication.sharedPreferences.getString("username", "default");
        if (EditTag.isEditTag()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(EditActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("是否保存？");
            dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (title.equals("")) {
                        Toast.makeText(EditActivity.this, "请填写标题", Toast.LENGTH_SHORT).show();
                    } else {
                        if (_id != -1) {
                            entityDAO.update(new Entity(title, content, date, _id));
                        } else {
                            _id = (int) entityDAO.add(new Entity(title, date, content, username));
                        }
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            });
            dialog.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.create();
            dialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_etTitle:
                et_etTitle.setCursorVisible(true);
                et_etTitle.addTextChangedListener(this);
                break;
            case R.id.et_etContent:
                et_etContent.setCursorVisible(true);
                et_etContent.addTextChangedListener(this);
                break;
            case R.id.et_btnPhoto:
                final CharSequence[] items = {"手机相册", "相机拍摄"};
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle("选择图片")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    Intent getImageByCameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    startActivityForResult(getImageByCameraIntent, PICK_CAMERA);
                                } else {
                                    Intent getImage = new Intent(Intent.ACTION_PICK);
                                    getImage.setType("image/*");
                                    startActivityForResult(getImage, PICK_PIC);
                                }
                            }
                        }).create();
                dlg.show();
                break;
            case R.id.et_btnCamera:
//                Toast.makeText(EditActivity.this, "点击了视频", Toast.LENGTH_SHORT).show();
                Intent getVideoByCameraIntent = new Intent("android.media.action.VIDEO_CAPTURE");
                startActivityForResult(getVideoByCameraIntent, PICK_VIDEO);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
//            Toast.makeText(EditActivity.this, "这是保存按钮", Toast.LENGTH_SHORT).show();
            String title = et_etTitle.getText().toString().trim();
            if (title.equals("")) {
                Toast.makeText(EditActivity.this, "请填写标题", Toast.LENGTH_SHORT).show();
            } else {
                String content = et_etContent.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                Date currentDate = new Date();
                String date = sdf.format(currentDate);
                String username = appApplication.sharedPreferences.getString("username", "default");

                EntityDAO entityDAO = new EntityDAO(getApplicationContext());
                if (_id != -1) {
                    System.out.println("_id:" + _id);
                    entityDAO.update(new Entity(title, content, date, _id));
                    if (imgTags.size() > 0) {
                        entityDAO.updateImg(new Entity(_id, bitmaps, imgTags));
                    }
                } else {
                    _id = (int) entityDAO.add(new Entity(title, content, date, username));
                    if (!imgTags.isEmpty()) {
                        entityDAO.addImg(new Entity(_id, bitmaps, imgTags));
                    }
                    System.out.println("_id:" + _id);
                }
                EditTag.setEditTag(false);
                Toast.makeText(EditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditTag.setEditTag(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private static class EditTag {
        private static boolean isEdit = false;

        static boolean isEditTag() {
            return isEdit;
        }

        static void setEditTag(boolean editTag) {
            isEdit = editTag;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getContentResolver();
        Bitmap bitmap;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_CAMERA:
                    Bundle extras = data.getExtras();
                    Bitmap bitmapCamera = (Bitmap) extras.get("data");
                    if (bitmapCamera != null) {
                        bitmap = resizeImage(bitmapCamera, et_etContent.getWidth());
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String imgTag = "img" + System.currentTimeMillis();
                        SpannableString spannableString = new SpannableString(imgTag);
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, imgTag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        System.out.println(imgTag);
                        //将选择的图片追加到EditText中光标所在位置
                        int index = et_etContent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = et_etContent.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                        } else {
                            edit_text.insert(index, spannableString);
                        }
                        imgTags.add(imgTag);
                        bitmaps.add(bitmapCamera);
                    } else {
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PICK_PIC:
                    //获得图片的uri
                    Uri originalUri = data.getData();
                    Bitmap originalBitmap = null;
                    try {
                        originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = resizeImage(originalBitmap, et_etContent.getWidth());
                    if (bitmap != null) {
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String imgTag = "img" + System.currentTimeMillis();
                        SpannableString spannableString = new SpannableString(imgTag);
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, imgTag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置
                        int index = et_etContent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = et_etContent.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                        } else {
                            edit_text.insert(index, spannableString);
                        }
                        imgTags.add(imgTag);
                        bitmaps.add(bitmap);
                    } else {
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PICK_VIDEO:
                    Toast.makeText(this, "录制了视频", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 图片缩放
     *
     * @param originalBitmap 原始的Bitmap
     * @param newWidth       自定义宽度
     * @return 缩放后的Bitmap
     */
    private Bitmap resizeImage(Bitmap originalBitmap, float newWidth) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        if (width >= newWidth) {
            //计算宽、高缩放率
            float scale = newWidth / width;
            //创建操作图片用的matrix对象 Matrix
            Matrix matrix = new Matrix();
            // 缩放图片动作
            matrix.setScale(scale, scale);
            return Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
        }
        return originalBitmap;
    }

}
