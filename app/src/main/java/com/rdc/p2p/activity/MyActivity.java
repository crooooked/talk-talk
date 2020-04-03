package com.rdc.p2p.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.p2p.R;
import com.rdc.p2p.app.App;
import com.rdc.p2p.base.BaseActivity;
import com.rdc.p2p.base.BasePresenter;
import com.rdc.p2p.bean.MessageBean;
import com.rdc.p2p.bean.UserBean;
import com.rdc.p2p.config.Constant;
import com.rdc.p2p.config.Protocol;
import com.rdc.p2p.database.DBOpenHelper;
import com.rdc.p2p.event.RecentMsgEvent;
import com.rdc.p2p.fragment.SelectImageFragment;
import com.rdc.p2p.util.SDUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.sql.Connection;

import butterknife.BindView;

public class MyActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    int imageID;
    String nickname;
    @BindView(R.id.my_image)
    ImageView mImage;
    @BindView(R.id.my_name)
    TextView mName;
    @BindView(R.id.change_background)
    TextView mChangeBg;
    @BindView(R.id.logged_out)
    TextView mExit;
    @BindView(R.id.about_us)
    TextView mAboutUs;
    @BindView(R.id.change_password)
    TextView mChangePwd;
    @BindView(R.id.modify_pwd)
    EditText mModifyPwd;
    @BindView(R.id.ensure_modify_pwd)
    Button mBtnModifyPwd;
    private int[] image_type={R.drawable.iv_0,R.drawable.iv_1,R.drawable.iv_2,R.drawable.iv_3,R.drawable.iv_4,R.drawable.iv_5,R.drawable.iv_6,R.drawable.iv_7,R.drawable.iv_8,R.drawable.iv_9,R.drawable.iv_10,R.drawable.iv_11,R.drawable.iv_12,R.drawable.iv_13,R.drawable.iv_14,R.drawable.iv_15,R.drawable.iv_16,R.drawable.iv_17};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserBean userBean = new UserBean();
        userBean=App.getUserBean();
        imageID=userBean.getUserImageId();
        nickname=userBean.getNickName();
        mImage.setImageResource(image_type[imageID]);
        mName.setText(nickname);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public BasePresenter getInstance() {
        return null;
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initView() {
        initToolbar();

    }

    private void initToolbar() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void initListener() {
        mChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //访问相册
                Intent intent=new Intent();
                intent.setType("image/*");
                //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
                //类型的内容给你选择
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
                startActivityForResult(intent, 1);

         }
        });
        mChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mModifyPwd.setVisibility(View.VISIBLE);
                mBtnModifyPwd.setVisibility(View.VISIBLE);
                mBtnModifyPwd.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(mModifyPwd.getText()))
                        {
                            new Thread() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    Connection conn = null;
                                    conn = (Connection) DBOpenHelper.getConn();
                                    int flag = 0;
                                    flag = DBOpenHelper.modify_pwd(conn, nickname, mModifyPwd.getText().toString());
                                    if (flag == 0) showToast("fail to modify password!");
                                    else showToast("modify password successfully!");
                                    Looper.loop();
                                }

                            }.start();
                            mModifyPwd.setVisibility(View.INVISIBLE);
                            mBtnModifyPwd.setVisibility(View.INVISIBLE);
                        }
                        else{
                            showToast("please input new password");
                        }
                    }
                });

            }
        });
        mAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, AboutUsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_mine);
        bnv.getMenu().getItem(2).setChecked(true);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_bu_1:
                                Intent intent = new Intent(MyActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();break;

                            case R.id.nav_bu_2:
                                Intent intent1 = new Intent(MyActivity.this, FriendActivity.class);
                                startActivity(intent1);
                                finish();break;


                        }
                        return true;
                    }
                });
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                App.setBg(bitmap);
                System.out.println("APP inserts pictures");
//                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else {
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
