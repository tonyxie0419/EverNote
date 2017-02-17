package com.jikexueyuan.evernote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.jikexueyuan.evernote.controller.EditActivity;
import com.jikexueyuan.evernote.controller.SetupActivity;
import com.jikexueyuan.evernote.model.Entity;
import com.jikexueyuan.evernote.utils.EntityDAO;
import com.jikexueyuan.evernote.view.HomeAdapter;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private SwipeMenuRecyclerView recyclerView;
    private HomeAdapter mAdapter;
    private List<Entity> list;
    private List<BmobObject> bmobObjectList;
    private MyApplication appApplication;
    EntityDAO entityDAO;
    private HomeAdapter.OnItemClickListener mOnItemClickListener;
    private static final int EDIT_RESULT = 10086;
    private static final int LOGIN_RESULT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        appApplication = (MyApplication) getApplication();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //初始化Bmob
        Bmob.initialize(this, "b719c1ce1df429c45d1842740daa4129");
        //设置BmobConfig,设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId("b719c1ce1df429c45d1842740daa4129")
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);

        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.lv_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int width = getResources().getDimensionPixelSize(R.dimen.item_height);
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                {
                    SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                            .setBackgroundDrawable(R.color.colorRed)
                            .setText("删除") // 文字。
                            .setTextColor(R.color.colorWhite) // 文字颜色。
                            .setTextSize(16)// 文字大小。
                            .setWidth(width)
                            .setHeight(height);
                    swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
                }
            }
        });
        //菜单点击监听
        recyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
                closeable.smoothCloseMenu();
                if (menuPosition == 0) {// 删除按钮被点击。
                    entityDAO.delete(list.get(adapterPosition).getId());
                    list.remove(adapterPosition);
                    mAdapter.notifyItemRemoved(adapterPosition);
                }
            }
        });

        entityDAO = new EntityDAO(getApplicationContext());

        list = entityDAO.findAll(appApplication.sharedPreferences.getString("username", "default"));
        bmobObjectList = new ArrayList<>();

        mOnItemClickListener = new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivityForResult(intent, EDIT_RESULT);
            }
        };

        setOnClick();
    }

    public void setOnClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "这是浮动按钮", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id", -1);
                startActivity(intent);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "这是返回按钮", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivityForResult(intent, LOGIN_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync) {

//            Toast.makeText(MainActivity.this, "这是同步按钮", Toast.LENGTH_SHORT).show();
//            UserSaveData userSaveData = new UserSaveData(list, appApplication.sharedPreferences.getString("username", "default"));
//            userSaveData.execute();

            //批量添加数据
            new BmobBatch().insertBatch(bmobObjectList).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            BatchResult result = list.get(i);
                            BmobException ex = result.getError();
                            if (ex == null) {
                                System.out.println("第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                            } else {
                                System.out.println("第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                            }
                        }
                    } else {
                        System.out.println("失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_RESULT:
                switch (resultCode) {
                    case RESULT_OK:
                        list.clear();
                        list = entityDAO.findAll(appApplication.sharedPreferences.getString("username", "default"));
                        break;
                }
                break;
            case LOGIN_RESULT:
                switch (resultCode) {
                    case RESULT_OK:
                        list.clear();
                        list = entityDAO.findAll(appApplication.sharedPreferences.getString("username", "default"));
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        list = entityDAO.findAll(appApplication.sharedPreferences.getString("username", "default"));
        for (int i = 0; i < list.size(); i++) {
            bmobObjectList.add(list.get(i));
        }
        mAdapter = new HomeAdapter(this, list);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        Collections.reverse(list);
        recyclerView.setAdapter(mAdapter);
        super.onResume();
    }
}
