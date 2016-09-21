package com.losileeya.bottomsheetmaster;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private List<AppInfo> mlistAppInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomSheet();


    }
    // 获得所有启动Activity的信息，类似于Launch界面
    public void queryAppInfo() {
        mlistAppInfo = new ArrayList<AppInfo>();
        PackageManager pm = this.getPackageManager(); //获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                mlistAppInfo.add(appInfo); // 添加至列表中
            }
        }
    }


    private void bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变回调
                //newState 有四个状态 ：
                //展开 BottomSheetBehavior.STATE_EXPANDED
                //收起 BottomSheetBehavior.STATE_COLLAPSED
                //拖动 BottomSheetBehavior.STATE_DRAGGING
                //下滑 BottomSheetBehavior.STATE_SETTLING
                Log.d("MainActivity", "newState:" + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，slideOffset为0-1 完全收起为0 完全展开为1
                Log.d("MainActivity", "slideOffset:" + slideOffset);
            }
        });
    }

    public void fuck(View view) {
         Toast.makeText(this, "我日", Toast.LENGTH_SHORT).show();
          if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
             bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }else {
              bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          }
    }
    public  void simplebottomsheetdialog(View view){
     final    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this,R.layout.share,null);
        View qq = inflate.findViewById(R.id.share_qq);
        View wx = inflate.findViewById(R.id.share_wx);
        View sina = inflate.findViewById(R.id.share_sina);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到qq", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到wx", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到sina", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }
    public void  recycleView(View view){
        queryAppInfo();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        //创建recyclerView
        RecyclerView recyclerView = new RecyclerView(this);
        GridLayoutManager layoutManager= new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mlistAppInfo,this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View item, int position) {
                Toast.makeText(MainActivity.this,mlistAppInfo.get(position).getAppLabel(), Toast.LENGTH_SHORT).show();
                Intent intent = mlistAppInfo.get(position).getIntent();
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(recyclerView);
        bottomSheetDialog.show();
    }
}
