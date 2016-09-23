package com.losileeya.coordinatorlayout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-23
 * Time: 00:31
 * 类描述：
 *
 * @version :
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {
    private final String[] titles;
    private Context context;
    private List<Fragment> fragments;

    public TabFragmentAdapter(List<Fragment> fragments, String[] titles, FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
