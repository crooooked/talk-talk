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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rdc.p2p.app.App;
import com.rdc.p2p.bean.MessageBean;
import com.rdc.p2p.bean.UserBean;
import com.rdc.p2p.fragment.ScanDeviceFragment;
import com.rdc.p2p.fragment.SelectImageFragment;
import com.rdc.p2p.R;
import com.rdc.p2p.base.BaseActivity;
import com.rdc.p2p.base.BasePresenter;
import com.rdc.p2p.bean.ImageBean;
import com.rdc.p2p.util.ImageUtil;
import com.rdc.p2p.util.NetUtil;
import com.rdc.p2p.util.UserUtil;
import com.rdc.p2p.database.DBOpenHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "Login";
    @BindView(R.id.civ_user_image_act_login)
    CircleImageView mCivUserImage;
    @BindView(R.id.et_nickname_act_login)
    EditText mEtNickname;
    @BindView(R.id.et_password_act_login)
    EditText mEtPassword;
    @BindView(R.id.btn_login_act_login)
    Button mBtnLogin;
    @BindView(R.id.switch_to_register)
    TextView mTvSwitch;
    private List<ImageBean> mImageList;
    private int mSelectedImageId;

    @Override
    public BasePresenter getInstance() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        getPermission(this);
        LitePal.getDatabase();
        DataSupport.deleteAll(MessageBean.class);
        mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        mSelectedImageId = 0;
        mImageList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            ImageBean imageBean = new ImageBean();
            imageBean.setImageId(i);
            mImageList.add(imageBean);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        mCivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImageFragment selectImageFragment = new SelectImageFragment();
                selectImageFragment.show(getSupportFragmentManager(),"DialogFragment");
            }
        });
        mTvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
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
                            if (NetUtil.isWifi(LoginActivity.this)){
                                Connection conn=null;
                                conn=(Connection)DBOpenHelper.getConn();
                                if(conn==null)
                                {
                                    showToast("Database connection error");
                                }
                                else {
                                    int flag = 0;
                                    int flag1 =0;
                                    showToast("Verifying...");
                                    flag = DBOpenHelper.login(conn, mEtNickname.getText().toString(), mEtPassword.getText().toString());
                                    if (flag == 1) {
                                        UserBean userBean = new UserBean();
                                        userBean.setNickName(getString(mEtNickname));
                                        userBean.setUserImageId(mSelectedImageId);
                                        UserUtil.saveUser(userBean);
                                        App.setUserBean(userBean);
                                        Connection conn1=null;
                                        conn1=(Connection)DBOpenHelper.getConn();
                                        flag1=DBOpenHelper.update(conn1,mEtNickname.getText().toString(),mSelectedImageId);
                                        if(flag1==0)showToast("Update avatar failure");
                                        System.out.println("Correct account password");
                                        showToast("Successful landing");
                                        ScanDeviceFragment scanDeviceFragment = new ScanDeviceFragment();
                                        scanDeviceFragment.setCancelable(false);
                                        scanDeviceFragment.show(getSupportFragmentManager(), "progressFragment");

                                    } else
                                    {
                                        showToast("account / password error.");
                                        System.out.println("account/password error");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ScanDeviceFinished(List<String> ipList){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putStringArrayListExtra("ipList", (ArrayList<String>) ipList);
        startActivity(intent);
        finish();
    }
    public void setImageId(int imageId){
        mSelectedImageId = imageId;
        Glide.with(this).load(ImageUtil.getImageResId(imageId)).into(mCivUserImage);
    }
}
