##  TabLayout简单使用

TabLayout属于design支持包下，所以需要在gradle加载相关配置：

```java
compile 'com.android.support:design:24.2.0'
```

TabLayout常用的方法如下： 

> \- addTab(TabLayout.Tab tab, int position, boolean setSelected) 增加选项卡到 layout 中 
> \- addTab(TabLayout.Tab tab, boolean setSelected) 同上 
> \- addTab(TabLayout.Tab tab) 同上 
> \- getTabAt(int index) 得到选项卡 
> \- getTabCount() 得到选项卡的总个数 
> \- getTabGravity() 得到 tab 的 Gravity 
> \- getTabMode() 得到 tab 的模式 
> \- getTabTextColors() 得到 tab 中文本的颜色 
> \- newTab() 新建个 tab 
> \- removeAllTabs() 移除所有的 tab 
> \- removeTab(TabLayout.Tab tab) 移除指定的 tab 
> \- removeTabAt(int position) 移除指定位置的 tab 
> \- setOnTabSelectedListener(TabLayout.OnTabSelectedListener onTabSelectedListener) 为每个 tab 增加选择监听器 
> \- setScrollPosition(int position, float positionOffset, boolean updateSelectedText) 设置滚动位置 
> \- setTabGravity(int gravity) 设置 Gravity 
> \- setTabMode(int mode) 设置 Mode,有两种值：TabLayout.MODE_SCROLLABLE和TabLayout.MODE_FIXED分别表示当tab的内容超过屏幕宽度是否支持横向水平滑动，第一种支持滑动，第二种不支持，默认不支持水平滑动。 
> \- setTabTextColors(ColorStateList textColor) 设置 tab 中文本的颜色 
> \- setTabTextColors(int normalColor, int selectedColor) 设置 tab 中文本的颜色 默认 选中 
> \- setTabsFromPagerAdapter(PagerAdapter adapter) 设置 PagerAdapter 
> \- setupWithViewPager(ViewPager viewPager) 和 ViewPager 联动

一般TabLayout都是和ViewPager共同使用才发挥它的优势，现在我们通过代码来看看以上方法的使用。

```java
<android.support.design.widget.TabLayout
    android:id="@+id/tab_layout"
    style="@style/TabLayoutStyle"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/colorPrimary"
    app:tabContentStart="50dp"
    app:tabMode="scrollable"/>

<android.support.v4.view.ViewPager
    android:id="@+id/view_pager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

使用其实非常简单，关键就一个方法：

```java
tabLayout.setupWithViewPager(viewPager);
```

完整代码如下所示：

```java
viewPager.setAdapter(new ViewPagerAdapter(
        Arrays.asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6"),
        Arrays.asList(new RecyclerViewFragment(), new RecyclerViewFragment(),
                new RecyclerViewFragment(), new RecyclerViewFragment(),
                new RecyclerViewFragment(), new RecyclerViewFragment()

        )));
tabLayout.setupWithViewPager(viewPager);
```

TabLayout还可以设置滚动和屏幕填充。

通过以下两个方法设置即可：

```java
tabLayout.setTabMode(TabLayout.MODE_FIXED);
tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
```

效果图我就不贴出来了。

在TabLayout.MODE_SCROLLABLE模式下还可以设置左边的padding： 
app:tabContentStart=”50dp”

下面是`Adapter`代码

```java
class ViewPagerAdapter extends FragmentStatePagerAdapter {
private List<String> list;
private List<? extends Fragment> fs;
public ViewPagerAdapter(List<String> list, List<? extends Fragment> fs) {
    super(getChildFragmentManager());
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
```

效果图：



![这里写图片描述](http://img.blog.csdn.net/20160922135131496)





# 定义TabLayout的样式

默认的情况下，TabLayout的tab indicator的颜色是Material Design中的accent color(#009688)，我们可以稍作修改：

```java
<style name="MyCustomTabLayout" parent="Widget.Design.TabLayout">
    <item name="tabIndicatorColor">#0000FF</item>
</style>
```

在布局中使用：

```java
<android.support.design.widget.TabLayout
       android:id="@+id/sliding_tabs"
       style="@style/MyCustomTabLayout"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
        />
```

还有一些其他的样式可供选择：

```java
<style name="MyCustomTabLayout" parent="Widget.Design.TabLayout">
    <item name="tabMaxWidth">@dimen/tab_max_width</item>
    <item name="tabIndicatorColor">?attr/colorAccent</item>
    <item name="tabIndicatorHeight">2dp</item>
    <item name="tabPaddingStart">12dp</item>
    <item name="tabPaddingEnd">12dp</item>
    <item name="tabBackground">?attr/selectableItemBackground</item>
    <item name="tabTextAppearance">@style/MyCustomTabTextAppearance</item>
    <item name="tabSelectedTextColor">?android:textColorPrimary</item>
</style>
<style name="MyCustomTabTextAppearance" parent="TextAppearance.Design.Tab">
    <item name="android:textSize">14sp</item>
    <item name="android:textColor">?android:textColorSecondary</item>
    <item name="textAllCaps">true</item>
</style>
```

# 添加icon到tab

当前的TabLayout没有方法让我们去添加icon，我们可以使用SpannableString结合ImageSpan来实现，在SimpleFragmentPagerAdapter中：

```java
private int[] imageResId = {
        R.drawable.ic_one,
        R.drawable.ic_two,
        R.drawable.ic_three
};
 
// ...
 
@Override
public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    // return tabTitles[position];
    Drawable image = context.getResources().getDrawable(imageResId[position]);
    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
    SpannableString sb = new SpannableString(" ");
    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return sb;
}
```

运行，发现没有显示，这是因为TabLayout创建的tab默认设置textAllCaps属性为true，这阻止了ImageSpan被渲染出来，可以通过下面的样式文件定义来改变：

```java
<style name="MyCustomTabLayout" parent="Widget.Design.TabLayout">
      <item name="tabTextAppearance">@style/MyCustomTextAppearance</item>
</style>
 
<style name="MyCustomTextAppearance" parent="TextAppearance.Design.Tab">
      <item name="textAllCaps">false</item>
</style>
```

现在运行，效果就出来了。

# 添加icon和text到tab

```java
@Override
public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    Drawable image = context.getResources().getDrawable(imageResId[position]);
    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
    // Replace blank spaces with image icon
    SpannableString sb = new SpannableString("   " + tabTitles[position]);
    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return sb;
}
```

我们看到在实例化SpannableString的时候，我在tabTitles[position]前面加了几个空格，这些空格的位置是用来放置icon的。

# 添加自定义的view到tab

适配器中增加getTabView(...)方法：

```java
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

import com.losileeya.bottomsheetmaster.R;

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

```

简单的布局：

```java
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    >
 
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/imageView"
        android:layout_centerVertical="true"
        />
 
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toRightOf="@id/imageView"
        android:layout_centerVertical="true"
        android:id="@+id/textView"
        android:layout_marginLeft="3dp"
        />
 
</RelativeLayout>
```

使用：

```java
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
```

# 处理配置改变

当屏幕旋转或者配置改变的时候，我们需要保存当前的状态。

```java
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
```

需要注意的是getSelectedTabPosition()方法是最新的design support library才有的。
最后的效果如下：



![这里写图片描述](http://img.blog.csdn.net/20160922135154980)



demo传送门：[tablayout](https://github.com/zilianliuxue/AndroidStudy/tree/master/codeSample/tablayout)