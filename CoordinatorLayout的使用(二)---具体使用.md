## CoordinatorLayout有什么作用

CoordinatorLayout作为“super-powered FrameLayout”基本实现两个功能： 
1、作为顶层布局 
2、调度协调子布局

```
CoordinatorLayout使用新的思路通过协调调度子布局的形式实现触摸影响布局的形式产生动画效果。CoordinatorLayout通过设置子View的 Behaviors来调度子View。系统（Support V7）提供了AppBarLayout.Behavior, AppBarLayout.ScrollingViewBehavior, FloatingActionButton.Behavior, SwipeDismissBehavior<V extends View> 等。
```

使用CoordinatorLayout需要在Gradle加入Support Design Library：

```java
  compile fileTree(include: ['*.jar'], dir: 'libs')
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:24.2.1'
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:design:24.2.1'
    compile 'com.android.support:support-v4:24.2.1'
    compile 'com.android.support:cardview-v7:24.2.1'
```

说到底CoordinatorLayout最重要的一个概念就是加入了Behavior。

## CoordinatorLayout与AppBarLayout

> AppBarLayout是一个实现了很多材料设计特性的垂直的LinearLayout，它能响应滑动事件。必须在它的子view上设置app:layout_scrollFlags属性或者是在代码中调用setScrollFlags()设置这个属性。这个类的特性强烈依赖于它是否是一个CoordinatorLayout的直接子view，如果不是，那么它的很多特性不能够使用。AppBarLayout需要一个具有滑动属性的兄弟节点view，并且在这个兄弟节点View中指定behavior属性为AppBarLayout.ScrollingViewBehavior的类实例，可以使用一个内置的string表示这个默认的实例@string/appbar_scrolling_view_behavior.



AppBarLayout 是继承LinerLayout实现的一个ViewGroup容器组件，它是为了Material Design设计的App Bar，支持手势滑动操作。

默认的AppBarLayout是垂直方向的，它的作用是把AppBarLayout包裹的内容都作为AppBar。

**注意：** AppBarLayout必须作为Toolbar的父布局容器,AppBarLayout是支持手势滑动效果的，不过的跟CoordinatorLayout配合使用.

```java
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout
    android:id="@+id/main_content"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >


    <android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <!--标题栏-->
        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:layout_scrollFlags="scroll|enterAlways">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_gravity="center"
                android:gravity="center"
                android:text="自定义标题"/>
        </android.support.v7.widget.Toolbar>
        <!--大图-->
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:background="@drawable/title_bg"/>
        <!--选项卡-->
        <android.support.design.widget.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#80ffffff"
            app:tabIndicatorColor="@color/colorAccent"
            app:tabMode="scrollable"
            app:tabSelectedTextColor="@color/colorAccent"
            app:tabTextColor="@android:color/black"/>
    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:overScrollMode="never"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:id="@+id/ll_sc_content"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">
        </LinearLayout>
    </android.support.v4.widget.NestedScrollView>
</android.support.design.widget.CoordinatorLayout>
```

为了达到上面效果图的手势动画效果，我们必须做如下设置，通过app:layout_scrollFlags=”scroll|enterAlways” 属性来确定哪个组件是可滑动的

设置的layout_scrollFlags有如下几种选项：

> - scroll: 所有想滚动出屏幕的view都需要设置这个flag- 没有设置这个flag的view将被固定在屏幕顶部。
>
> - enterAlways: 这个flag让任意向下的滚动都会导致该view变为可见，启用快速“返回模式”。
>
> - enterAlwaysCollapsed: 当你的视图已经设置minHeight属性又使用此标志时，你的视图只能已最小高度进入，只有当滚动视图到达顶部时才扩大到完整高度。
>
> - exitUntilCollapsed: 滚动退出屏幕，最后折叠在顶端。
>
>   ​

```java
package com.losileeya.appbarlayout;

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
```

效果是这样的：

![](https://github.com/zilianliuxue/AndroidStudy/blob/master/codeSample/CoordinatorLayout/img/GIF.gif)

这里用到了TabLayout+NestedScrollView，tablayout 就是实现了选项卡的功能一般和viewpager搭配使用。NestedScrollView它的作用类似于android.widget.ScrollView,不同点在于NestedScrollView支持嵌套滑动.

接下来我们放弃使用NestedScrollView，用viewpager来代替：

```java
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"

    android:layout_width="match_parent"
    android:layout_height="match_parent"
  >

    <!-- AppBarLayout，作为CoordinatorLayout的子类 -->
    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:fitsSystemWindows="true"
        android:layout_height="wrap_content"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        >

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
            app:layout_scrollFlags="scroll|enterAlways"/>

        <android.support.design.widget.TabLayout
            android:id="@+id/tablayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_scrollFlags="scroll|enterAlways"
            app:tabIndicatorColor="@android:color/transparent"
            app:tabMode="scrollable">
        </android.support.design.widget.TabLayout>

    </android.support.design.widget.AppBarLayout>

    <!-- Your scrolling content -->
    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >
    </android.support.v4.view.ViewPager>
    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end|bottom"
        android:layout_margin="@dimen/fab_margin"
        android:src="@drawable/ic_done" />

</android.support.design.widget.CoordinatorLayout>
```

item的布局：

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">
   <TextView
       android:id="@+id/tv_info"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:gravity="center"/>
</LinearLayout>
```

代码实现：

```java
package com.losileeya.appbarlayout;

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

```

fragment的写法：

```java
public class MyFragment extends Fragment {
    private String mText;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if(getArguments()!=null){
            mText = getArguments().getString("text");
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mText);
        return textView;
    }
}
```

activity的实现：

```java
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

```

效果如下：



![](https://github.com/zilianliuxue/AndroidStudy/blob/master/codeSample/CoordinatorLayout/img/GIF1.gif)



哈哈，看见了吗，viewpager支持左右滑动，但是不能上下滑动是不是很不爽，上面提到NestedScrollView可以实现嵌套滑动。

```java
   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       NestedScrollView scrollableView = new NestedScrollView(getActivity());
        TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mText);
       scrollableView.addView(textView);
        return scrollableView;
    }
```

这样既可以左右滚动还可以上下滚动了。

效果如下：



![](https://github.com/zilianliuxue/AndroidStudy/blob/master/codeSample/CoordinatorLayout/img/GIF2.gif)



NestedScrolling机制可以参考hongyang的：

[Android NestedScrolling机制完全解析 带你玩转嵌套滑动](http://blog.csdn.net/lmj623565791/article/details/52204039)

### AppBarLayout嵌套CollapsingToolbarLayout

CollapsingToolbarLayout包裹 Toolbar 的时候提供一个可折叠的 Toolbar，一般作为AppbarLayout的子视图使用。

CollapsingToolbarLayout 提供以下属性和方法是用：

> 1. Collapsing title：ToolBar的标题，当CollapsingToolbarLayout全屏没有折叠时，title显示的是大字体，在折叠的过程中，title不断变小到一定大小的效果。你可以调用setTitle(CharSequence)方法设置title。
> 2. Content scrim：ToolBar被折叠到顶部固定时候的背景，你可以调用setContentScrim(Drawable)方法改变背景或者 在属性中使用 app:contentScrim=”?attr/colorPrimary”来改变背景。
> 3. Status bar scrim：状态栏的背景，调用方法setStatusBarScrim(Drawable)。还没研究明白，不过这个只能在Android5.0以上系统有效果。
> 4. Parallax scrolling children：CollapsingToolbarLayout滑动时，子视图的视觉差，可以通过属性app:layout_collapseParallaxMultiplier=”0.6”改变。
> 5. CollapseMode ：子视图的折叠模式，有两种“pin”：固定模式，在折叠的时候最后固定在顶端；“parallax”：视差模式，在折叠的时候会有个视差折叠的效果。我们可以在布局中使用属性app:layout_collapseMode=”parallax”来改变。



```java
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main_content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="256dp"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        android:fitsSystemWindows="true">

        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            android:fitsSystemWindows="true"
            app:contentScrim="?attr/colorPrimary"
            app:expandedTitleMarginStart="48dp"
            app:expandedTitleMarginEnd="64dp">

            <ImageView
                android:id="@+id/backdrop"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:fitsSystemWindows="true"
                android:src="@drawable/title_bg"
                app:layout_collapseMode="parallax"
                />

            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
                app:layout_collapseMode="pin" />

        </android.support.design.widget.CollapsingToolbarLayout>

    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:paddingTop="24dp">

            <android.support.v7.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="CardView"
                        android:textAppearance="@style/TextAppearance.AppCompat.Title" />

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="尤其在对网络的封锁上，不仅朝鲜老百姓很难能上网，很多国外黑客之前想要黑进朝鲜内网“窥探”最后也失败了." />

                </LinearLayout>

            </android.support.v7.widget.CardView>
            <android.support.v7.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="CardView"
                        android:textAppearance="@style/TextAppearance.AppCompat.Title" />

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="他发现朝鲜竟然意外开放了一个DNS服务器，他写了一段程序开始跟踪，复制到了DNS数据....." />

                </LinearLayout>

            </android.support.v7.widget.CardView>
            <android.support.v7.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="CardView"
                        android:textAppearance="@style/TextAppearance.AppCompat.Title" />

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="他把获取的数据和信息都发布到了GitHub上，很快，这事儿就传到了Reddit..." />

                </LinearLayout>

            </android.support.v7.widget.CardView>
            <android.support.v7.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="16dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="CardView"
                        android:textAppearance="@style/TextAppearance.AppCompat.Title" />

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="国外网友发现朝鲜的网站可以被公开访问以后也是挺嗨的，纷纷开始研究这些网站.......然而没多久朝鲜就意识到自己的疏忽，全部又给封堵上了" />

                </LinearLayout>

            </android.support.v7.widget.CardView>
        </LinearLayout>

    </android.support.v4.widget.NestedScrollView>

    <android.support.design.widget.FloatingActionButton
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        app:layout_anchor="@id/appbar"
        app:layout_anchorGravity="bottom|right|end"
        app:backgroundTint="#FF1793"
        app:rippleColor="#FF1793"
        app:fabSize="normal"
        android:src="@drawable/ic_done"
        android:clickable="true"/>

</android.support.design.widget.CoordinatorLayout>
```

**总结：** CollapsingToolbarLayout主要是提供一个可折叠的Toolbar容器，对容器中的不同视图设置layout_collapseMode折叠模式，来达到不同的折叠效果。

1.Toolbar 的高度layout_height必须固定，不能 “wrap_content”，否则Toolbar不会滑动，也没有折叠效果。 
2.为了能让FloatingActionButton也能折叠且消失出现，我们必须给FAB设置锚点属性

```java
app:layout_anchor="@id/appbar"
```

意思是FAB浮动按钮显示在哪个布局区域。 
且设置当前锚点的位置

app:layout_anchorGravity=”bottom|end|right”

意思FAB浮动按钮在这个布局区域的具体位置。 
两个属性共同作用才是的FAB 浮动按钮也能折叠消失，出现。

3.给需要有折叠效果的组件设置 layout_collapseMode属性。

activity实现的代码：

```java
package com.losileeya.appbarlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-23
 * Time: 12:58
 * 类描述：
 *
 * @version :
 */
public class CollasingActivity extends AppCompatActivity {
    private ImageView backdrop;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;
    private AppBarLayout appbar;
    private CoordinatorLayout main_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapsing_layout);
        initView();
    }

    private void initView() {
        backdrop = (ImageView) findViewById(R.id.backdrop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        main_content = (CoordinatorLayout) findViewById(R.id.main_content);
        // Title
        collapsing_toolbar.setTitle("collapsing");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { //返回按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    msg += "Click edit";
                    break;
                case R.id.action_share:
                    msg += "Click share";
                    break;
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(CollasingActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}

```

效果如下：



![](https://github.com/zilianliuxue/AndroidStudy/blob/master/codeSample/CoordinatorLayout/img/GIF3.gif)



demo传送门：[CoordinatorLayout](https://github.com/zilianliuxue/AndroidStudy/tree/master/codeSample/CoordinatorLayout)



综上，基本覆盖了CoordinatorLayout的使用方式。



##### CoordinatorLayout可以去参考：

[CoordinatorLayout简述](http://www.open-open.com/lib/view/open1453021356261.html)

##### 自定义Behavior可以去参考：

1. [Material Design系列，Behavior之BottomSheetBehavior与BottomSheetDialog](http://blog.csdn.net/yanzhenjie1003/article/details/51938400) 
2. [Material Design系列，Behavior之SwipeDismissBehavior](http://blog.csdn.net/yanzhenjie1003/article/details/51938425) 
3. [Material Design系列，自定义Behavior之上滑显示返回顶部按钮](http://blog.csdn.net/yanzhenjie1003/article/details/51941288) 
4. [Material Design系列，自定义Behavior实现Android知乎首页](http://blog.csdn.net/yanzhenjie1003/article/details/51946749) 
5. [Material Design系列，自定义Behavior支持所有 View](http://blog.csdn.net/yanzhenjie1003/article/details/52205665)


