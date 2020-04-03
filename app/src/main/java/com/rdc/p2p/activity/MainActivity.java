package com.rdc.p2p.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.p2p.R;
import com.rdc.p2p.base.BaseActivity;
import com.rdc.p2p.base.BasePresenter;
import com.rdc.p2p.database.DBOpenHelper;
import com.rdc.p2p.fragment.PeerListFragment;
import com.rdc.p2p.fragment.ScanDeviceFragment;
import com.rdc.p2p.manager.SocketManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.vp_act_main)
    ViewPager mVpContent;

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private boolean checking = true;// true 选中聊天列表 , false 选中 聊天室
    private static final String TAG = "conversation";
    private static final int BROADCAST_PORT = 3000;
    private static final String BROADCAST_IP = "239.0.0.3";
    private MulticastSocket mSocket;
    private InetAddress mAddress;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
//                    mTvShow.append("\n"+message.obj.toString());
                    break;
            }
            return true;
        }
    });
    private PeerListFragment mPeerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
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
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initView() {
        initToolbar();
//        ActionBarDrawerToggle mDrawerToggle =
//                new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mPeerListFragment = new PeerListFragment();
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return mPeerListFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public void restoreState(Parcelable state, ClassLoader loader) {
                super.restoreState(state, loader);
            }

            @Override
            public Parcelable saveState() {
                return super.saveState();
            }
        };
        mVpContent.setAdapter(mFragmentPagerAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolbar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                if (mPeerListFragment.isServerSocketConnected()){
                    ScanDeviceFragment mScanDeviceFragment = new ScanDeviceFragment();
                    mScanDeviceFragment.setCancelable(false);
                    mScanDeviceFragment.show(getSupportFragmentManager(),"scanDevice");
                }else {
                    showToast("ServerSocket isn't connected. Check WIFI!");
                }
                break;
        }
        return true;
    }

    @Override
    protected void initListener() {
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_bu_2:
                                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                                startActivity(intent);
                                finish();break;
                            case R.id.nav_bu_3:
                                Intent intent1 = new Intent(MainActivity.this, MyActivity.class);
                                startActivity(intent1);
                                finish();break;


                        }
                        return true;
                    }
                });

    }

}
