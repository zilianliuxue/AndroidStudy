package com.losileeya.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-22
 * Time: 13:03
 * 类描述：
 *
 * @version :
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private List<String> list;
    private List<? extends Fragment> fs;
    private int[] imageResId;

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<String> list, List<? extends Fragment> fs, int[] imageResId, Context context) {
        super(fm);
        this.list = list;
        this.fs = fs;
        this.imageResId = imageResId;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fs.get(position);
    }
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv= (TextView) view.findViewById(R.id.textView);
        tv.setText(list.get(position));
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(imageResId[position]);
        return view;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + list.get(position));
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
