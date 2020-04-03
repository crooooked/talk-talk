package com.rdc.p2p.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rdc.p2p.R;
import com.rdc.p2p.app.App;
import com.rdc.p2p.base.BaseActivity;
import com.rdc.p2p.base.BasePresenter;
import com.rdc.p2p.bean.ImageBean;
import com.rdc.p2p.bean.MessageBean;
import com.rdc.p2p.bean.UserBean;
import com.rdc.p2p.database.DBOpenHelper;
import com.rdc.p2p.fragment.ScanDeviceFragment;
import com.rdc.p2p.fragment.SelectImageFragment;
import com.rdc.p2p.util.ImageUtil;
import com.rdc.p2p.util.NetUtil;
import com.rdc.p2p.util.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends BaseActivity {
    public static final String TAG = "Register";

    @BindView(R.id.et_nickname_act_register)
    EditText mEtNickname;
    @BindView(R.id.et_password_act_register)
    EditText mEtPassword;
    @BindView(R.id.btn_register_act_register)
    Button mBtnLogin;
    @BindView(R.id.switch_to_login)
    TextView mTvSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPermission(this);
        LitePal.getDatabase();
        DataSupport.deleteAll(MessageBean.class);
    }
    /**
     * 获取储存权限
     * @param activity
     * @return
     */

    public void getPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            finish();
                            showToast("Deny authorization, cannot use this application!");
                        }
                    }
                }else {
                    showToast("Deny authorization, cannot use this application!");
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public BasePresenter getInstance() {
        return null;
    }
    @Override
    protected int setLayoutResID() {
        return R.layout.activity_register;
    }
    @Override
    protected void initData() {

    }
    @Override
    protected void initView() {

    }
    @Override
    protected void initListener() {

        mTvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEtNickname.getText())&&!TextUtils.isEmpty(mEtPassword.getText())){
                    showToast("Connecting database...");
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();

                            if (NetUtil.isWifi(RegisterActivity.this)){
                                Connection conn=null;
                                conn=(Connection) DBOpenHelper.getConn();
                                if(conn==null)
                                {
                                    showToast("Database connection error");
                                }
                                else {
                                    int flag = 0;
                                    showToast("Registering...");
                                    flag = DBOpenHelper.regist(conn, mEtNickname.getText().toString(), mEtPassword.getText().toString());
                                    if (flag == 1) {

                                        System.out.println("Successful registration");
                                        showToast("Successful registration！Return to login page！");
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else
                                    {
                                        showToast("Registration failure");
                                        System.out.println("Registration failure");
                                    }
                                }
                            }else {
                                showToast("Please connect WIFI!");
                            }Looper.loop();
                        }
                    }.start();
                }else {
                    showToast("account/passwords cannot be empty!");
                }

            }
        });
    }

}

