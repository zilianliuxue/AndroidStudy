package com.losileeya.layout;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ExampleActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<String>tabs= asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6");
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
             Fragment f1=new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", "TAB"+i);
            f1.setArguments(bundle);
            fragmentList.add(f1);
        }
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(),tabs,fragmentList);
        viewPager.setAdapter(adapter);
      //  tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<String> list;
        private List<? extends Fragment> fs;

        public ViewPagerAdapter(FragmentManager fm, List<String> list, List<? extends Fragment> fs) {
            super(fm);
            this.list = list;
            this.fs = fs;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Fragment getItem(int position) {
            return fs.get(position);
        }
        @Override

        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position);
        }
    }
}
