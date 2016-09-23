package com.losileeya.coordinatorlayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-23
 * Time: 00:28
 * 类描述：
 *
 * @version :
 */
public class ViewPagerActivity  extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        String[] titles = {"最新","赣州","社会","订阅","娱乐","NBA","搞笑","科技","创业"};
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("viewpager");

        setSupportActionBar(toolbar);//替换系统的actionBar
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { //返回按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 0; i < titles.length; i++) {
            Fragment fragment = new MyFragment();
            Bundle bundle = new Bundle();
            bundle.putString("text",titles[i]);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewPager.setAdapter(new TabFragmentAdapter(fragments, titles, getSupportFragmentManager(), this));
// 初始化
        TabLayout tablayout = (TabLayout) findViewById(R.id.tablayout);
// 将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(viewPager);
// 设置tab文本的没有选中（第一个参数）和选中（第二个参数）的颜色
        tablayout.setTabTextColors(Color.GRAY, Color.WHITE);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view,"FAB",Snackbar.LENGTH_LONG)
                        .setAction("cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //这里的单击事件代表点击消除Action后的响应事件

                            }
                        })
                        .show();
            }
        });
    }
}
