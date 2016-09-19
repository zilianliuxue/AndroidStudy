侧拉菜单作为常见的导航交互控件，最开始在没有没有android官方控件时，很多时候都是使用开源的SlidingMenu。使用DrawerLayout可以轻松的实现抽屉效果。DrawerLayout 在android.support.v4.widget.DrawerLayout这个包里。

先看简单的页面效果

![里写图片描述](http://img.blog.csdn.net/20160920012346463)

​                          内容部分

![这里写图片描述](http://img.blog.csdn.net/20160920012402776)

​                         菜单部分

##   DrawerLayout 的使用

DrawerLayout布局分两部分，第一用户内容，就是非菜单部分。第二是菜单。

> DrawerLayout可以轻松的实现抽屉效果、在DrawerLayout中，第一个子View必须是显示内容的view，第二个view是抽屉view,设置属性layout_gravity=”left|right”,表示是从左边滑出还是右边滑出。 

先添加依赖：

```java
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:24.2.1'
    compile 'com.android.support:support-v4:24.2.1'
    compile 'com.android.support:design:24.2.1'
    compile 'com.jakewharton:butterknife:8.4.0'
    apt 'com.jakewharton:butterknife-compiler:8.4.0'
}
```

我们知道DrawerLayout必须依赖v4包，然后我们一般DrawerLayout搭配NavigationView使用效果更好，所以得添加design的依赖。

先看主布局：activity_main.xml

```java
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start"
    >
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#E1BEE7"
        android:orientation="vertical">

        <android.support.v7.widget.Toolbar
            android:id="@+id/id_toolbar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="?attr/colorPrimary"
            android:minHeight="?attr/actionBarSize"
            android:popupTheme="@style/ThemeOverlay.AppCompat.Light"
            app:theme="@style/ThemeOverlay.AppCompat.ActionBar"
            app:title="Losileeya" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:text="this content"
            android:gravity="center"/>
    </LinearLayout>
    <android.support.design.widget.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/header_layout"
        app:menu="@menu/left_menu" />
</android.support.v4.widget.DrawerLayout>
```

从上面的布局代码中我们就看出来了，DrawerLayout包含NavigationView和内容布局,NavigationView就是我们左边栏的菜单抽屉。从上面的布局可以看出NavigationView 分两部分，一个是header，一个是menu。



背景是蓝色的，menu的按钮和文字是黑色的，怎么修改呢？

```java
app:itemIconTint="#2196f3"  给icon着色
app:itemTextColor="#009688" menu文字颜色
app:itemBackground="@drawable/my_ripple" 设置menu item的背景
```



如何修改 Toolbar 左边`汉堡图标`的颜色 ?

通过一个样式就可以修改了：

```java
<style name="DrawerArrowStyle" parent="@style/Widget.AppCompat.DrawerArrowToggle">
    <item name="spinBars">true</item>
    <item name="color">@android:color/white</item> //custom color
</style>

//然后在style中加上就可以了
<item name="drawerArrowStyle">@style/DrawerArrowStyle</item>
```



### NavigationView

NavigationView分为两部分，一部分是headerLayout，一部分是menu。headerLayout就是对应菜单的顶部部分，一般用来显示用户信息什么的，menu则对应实际的菜单选项。我们从上面的布局代码中可以看出分别对应的就是 app:headerLayout和app:menu。

```java
  app:headerLayout="@layout/header_layout"
  app:menu="@menu/left_menu" 
```

### headerLayout(头部布局)

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp"
    android:theme="@style/ThemeOverlay.AppCompat.Dark"
    android:background="?attr/colorPrimaryDark"
    android:gravity="center_vertical"
    >
    <ImageView
        android:id="@+id/head_iv"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_marginTop="30dp"
        android:src="@drawable/head" />

    <TextView
        android:text="Losileeya"
        android:layout_marginTop="10dp"
        android:textColor="#ffffff"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>
```

很简单就是图片中的logo加上文字。

###  menu(菜单编写)

```java
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:checkableBehavior="single"
        >
        <item
            android:id="@+id/nav_home"
            android:icon="@drawable/nav_icon_home"
            android:title="Home" />
        <item
            android:id="@+id/nav_favorite"
            android:icon="@drawable/nav_icon_favorite"
            android:title="收藏" />
        <item
            android:id="@+id/nav_followers"
            android:icon="@drawable/nav_icon_followers"
            android:title="群组" />
        <item
            android:id="@+id/nav_settings"
            android:icon="@drawable/nav_icon_settings"
            android:title="设置" />
    </group>

    <item android:title="分享和反馈">
        <menu >
            <item
                android:id="@+id/nav_share"
                android:icon="@drawable/nav_icon_my_shares"
                android:title="分享" />

            <item
                android:id="@+id/nav_feedback"
                android:icon="@drawable/nav_icon_feedback"
                android:title="意见反馈" />
        </menu>
    </item>
</menu>
```



修改menu里的 SubHeader 颜色 ?

```java
从上面的图我们发现SubHeader是黑色的，怎么修改颜色, 加上下面的配置即可：

<item name="android:textColorSecondary">#ffffff</item>
```

###  activity的代码实现

```java
package com.losileeya.drawerlayout;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

public class MainActivity extends AppCompatActivity {

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
        setupDrawerContent(navView);
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

    private void setupDrawerContent(NavigationView navView) {

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                return false;
            }
        });
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
}

```

上面主要对DrawerLayout和NavigationView进行了声明和初始化，以及实现了NavigationView.OnNavigationItemSelectedListener这个监听事件，然后在实现的监听方法里判断点击事件。记得实现了监听，别忘了设置监听：navigationView.setNavigationItemSelectedListener(this);

### headerLayout上的控件实现

如果要实现headerLayout上的控件的点击，那就得这样做了，如下：

```
View navHeaderView = navView.inflateHeaderView(R.layout.header_layout);

ImageView headIv = (ImageView) navHeaderView.findViewById(R.id.head_iv);

headIv.setOnClickListener(new View.OnClickListener() {

        @Override

        public void onClick(View view) {

            Toast.makeText(MainActivity.this, "点击我的头像", Toast.LENGTH_SHORT).show();

        }

});
```

但是这样做了之后，就相当于在navigationView上又添加了一个headerlayou布局，所以这时，我们需要在布局文件中把

```
app:headerLayout="@layout/header_layout"
```

这行代码去掉，否则会重复的。

### 主题和配色

style:

```java
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!--状态栏颜色-->
        <item name="colorPrimaryDark">@color/Indigo_colorPrimaryDark</item>
        <!--Toolbar颜色-->
        <item name="colorPrimary">@color/Indigo_colorPrimary</item>
        <!--返回键样式-->
        <item name="drawerArrowStyle">@style/AppTheme.DrawerArrowToggle</item>
    </style>
    <style name="AppTheme.DrawerArrowToggle" parent="Base.Widget.AppCompat.DrawerArrowToggle">
        <item name="color">@android:color/white</item>
    </style>

    <style name="AppTheme.NoActionBar">
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>

    </style>
    <style name="AppTheme.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar" />

    <style name="AppTheme.PopupOverlay" parent="ThemeOverlay.AppCompat.Light" />

</resources>
```

color:

```java&#39;
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#3F51B5</color>
    <color name="colorPrimaryDark">#303F9F</color>
    <color name="colorAccent">#FF4081</color>
    <color name="Indigo_colorPrimaryDark">#303f9f</color>
    <color name="Indigo_colorPrimary">#3f51b5</color>
    <color name="Indigo_nav_color">#4675FF</color>
</resources>
```



好了，该说的都说完了。






























































































