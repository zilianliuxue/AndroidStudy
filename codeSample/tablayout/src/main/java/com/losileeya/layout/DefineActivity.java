package com.losileeya.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-22
 * Time: 13:02
 * 类描述：
 *
 * @version :
 */
public class DefineActivity extends AppCompatActivity{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public final String POSITION="tab_position";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<String>tabs= asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6");
        List<Fragment> fragmentList = new ArrayList<>();
        int []imgs={R.drawable.nav_icon_favorite,R.drawable.nav_icon_feedback,R.drawable.nav_icon_followers,R.drawable.nav_icon_following,R.drawable.nav_icon_gift,R.drawable.nav_icon_home};
        for (int i = 0; i < tabs.size(); i++) {
            Fragment f1=new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", "TAB"+(i+1));
            f1.setArguments(bundle);
            fragmentList.add(f1);
        }
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),tabs,fragmentList,imgs,this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }
}
