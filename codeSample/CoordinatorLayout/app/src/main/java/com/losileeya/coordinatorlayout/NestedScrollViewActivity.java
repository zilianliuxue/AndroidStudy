package com.losileeya.coordinatorlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-22
 * Time: 23:58
 * 类描述：
 *
 * @version :
 */
public class NestedScrollViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private AppBarLayout appbar;
    private LinearLayout ll_sc_content;
    private CoordinatorLayout main_content;
    List<String> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nestedscrolview_layout);
        initData(1);
        initView();

    }

    private void initData(int pager) {
        mData = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            mData.add("pager" + pager + " 第" + i + "个item");
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        ll_sc_content = (LinearLayout) findViewById(R.id.ll_sc_content);
        main_content = (CoordinatorLayout) findViewById(R.id.main_content);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);//替换系统的actionBar
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { //返回按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        for (int i = 1; i < 20; i++) {
            tabLayout.addTab(tabLayout.newTab().setText("TAB" + i));
        }
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //TabLayout的切换监听
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                initData(tab.getPosition() + 1);
                setScrollViewContent();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setScrollViewContent();
    }
    /**
     * 刷新ScrollView的内容
     */
    private void setScrollViewContent() {
        //NestedScrollView下的LinearLayout
        ll_sc_content.removeAllViews();
        for (int i = 0; i < mData.size(); i++) {
            View view = View.inflate(NestedScrollViewActivity.this, R.layout.item_layout, null);
            ((TextView) view.findViewById(R.id.tv_info)).setText(mData.get(i));
            //动态添加 子View
            ll_sc_content.addView(view, i);
        }
    }

}
