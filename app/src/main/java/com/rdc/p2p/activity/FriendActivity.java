package com.rdc.p2p.activity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rdc.p2p.R;
import com.rdc.p2p.base.BaseActivity;
import com.rdc.p2p.base.BasePresenter;
import com.rdc.p2p.database.DBOpenHelper;
import com.rdc.p2p.fragment.PeerListFragment;
import com.rdc.p2p.fragment.ScanDeviceFragment;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FriendActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private int[] image_type={R.drawable.iv_0,R.drawable.iv_1,R.drawable.iv_2,R.drawable.iv_3,R.drawable.iv_4,R.drawable.iv_5,R.drawable.iv_6,R.drawable.iv_7,R.drawable.iv_8,R.drawable.iv_9,R.drawable.iv_10,R.drawable.iv_11,R.drawable.iv_12,R.drawable.iv_13,R.drawable.iv_14,R.drawable.iv_15,R.drawable.iv_16,R.drawable.iv_17};
    List<String> name_list = new ArrayList<String>();
    List<String> image_list = new ArrayList<String>();
    int imageID=0;
    int size=0;
    int flag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread() {
            @Override
            public void run() {
                Connection conn=null;
                conn=(Connection) DBOpenHelper.getConn();
                DBOpenHelper.query_friend(conn);
                name_list=DBOpenHelper.query_friend(conn);
                size=name_list.size();
                if(size!=0) {
                    for (String s : name_list) {
                        imageID = DBOpenHelper.query_image(conn, s);
                        image_list.add(String.valueOf(imageID));
                    }
                }
                try{
                    conn.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        try {
            showToast("Looking for recent contacts...");
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        ListView listView=findViewById(R.id.vp_act_main);
        String[] name = name_list.toArray(new String[size]);
        String[] image = image_list.toArray(new String[size]);
        if(name.length!=0) {
            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return name.length;
                }

                @Override
                public Object getItem(int position) {
                    return name[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View layout = View.inflate(FriendActivity.this, R.layout.fragment_friend, null);
                    ImageView imageView = (ImageView) layout.findViewById(R.id.item_image);
                    TextView textView = (TextView) layout.findViewById(R.id.item_text);
                    imageView.setImageResource(image_type[Integer.parseInt(image[position])]);
                    textView.setText(name[position]);
                    return layout;
                }
            };
            listView.setAdapter(baseAdapter);
        }
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
        return R.layout.activity_friend;
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
        final BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation_friend);
        bnv.getMenu().getItem(1).setChecked(true);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_bu_1:
                                Intent intent = new Intent(FriendActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();break;

                            case R.id.nav_bu_3:
                                Intent intent1 = new Intent(FriendActivity.this, MyActivity.class);
                                startActivity(intent1);
                                finish();break;

                        }
                        return true;
                    }
                });
    }

}
