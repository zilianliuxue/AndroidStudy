package com.losileeya.drawerlayout;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener{

    @BindView(R.id.id_toolbar)
    Toolbar idToolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(idToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
       // getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示title
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, idToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
//        idToolbar.setNavigationOnClickListener(new View.OnClickListener() { //返回按钮的点击事件
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        View navHeaderView = navView.inflateHeaderView(R.layout.header_layout);
//
//        ImageView headIv = (ImageView) navHeaderView.findViewById(R.id.head_iv);
//
//        headIv.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//
//            public void onClick(View view) {
//
//                Toast.makeText(MainActivity.this, "点击我的头像", Toast.LENGTH_SHORT).show();
//
//            }
//
//        });
    }


    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {

            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                doExitApp();
            }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_favorite:
                Toast.makeText(MainActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_followers:
                Toast.makeText(MainActivity.this, "followers", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(MainActivity.this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_share:
                Toast.makeText(MainActivity.this, "share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_feedback:
                Toast.makeText(MainActivity.this, "feedback", Toast.LENGTH_SHORT).show();
                break;
        }
        menuItem.setChecked(true);
        //      drawerLayout.closeDrawer(GravityCompat.START);
        drawerLayout.closeDrawers();
        return true;
    }
}
