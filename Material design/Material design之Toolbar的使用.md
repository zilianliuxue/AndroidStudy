ToolBar是Android 5.0（API Level 21）之后用来取代ActionBar的

## ToolBar的优势：

Toolbar本身是一个 ViewGroup(而Actionbar直接继承自object)相比Actionbar可以更加灵活的配置使用。

## 引入方式：

在布局文件中引入support V7包下的ToolBar控件：

```java
 <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/blue"
        android:theme="@style/Theme.ToolBar">
 
    </android.support.v7.widget.Toolbar>
```

在代码中像使用其他控件一样拿到ToolBar的引用:

```java
 Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
```

设置右上角的menu菜单：调用Toolbar的inflateMenu()方法，传入一个Menu的xml文件

```java
toolbar.inflateMenu(R.menu.menu);
```

或者覆盖onCreateOptionsMenu()方法

```java
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
```

###  简单使用：

在Activity中添加代码即可使用ToolBar；

```java
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolTitle = (TextView) findViewById(R.id.toolbar_title);
        tvToolTitle.setText("测试");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
```

![这里写图片描述](http://img.blog.csdn.net/20160520144010087)

ToolBar中的菜单布局：

```java
<menu xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:app="http://schemas.android.com/apk/res-auto"  
    xmlns:tools="http://schemas.android.com/tools"  
    tools:context=".MainActivity" >   
    <item  
        android:id="@+id/action_settings"  
        android:orderInCategory="100"  
        android:title="action_settings"  
        app:showAsAction="never"/>  

</menu>  

```

```java
mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {  
    @Override  
    public boolean onMenuItemClick(MenuItem item) {  
        switch (item.getItemId()) {  
        case R.id.action_settings:  
            break;  
        default:  
            break;  
        }  
        return true;  
    }  
});  
```



## ToolBar的配置：

代码方式：

配置主标题：标题内容和颜色

```java
 toolbar.setTitle("Title");//设置主标题
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));//设置主标题颜色
        toolbar.setTitleTextAppearance(this, R.style.Theme_ToolBar_Base_Title);//修改主标题的外观，包括文字颜色，文字大小等
```

```java
  <style name="Theme.ToolBar.Base.Title" parent="@style/TextAppearance.Widget.AppCompat.Toolbar.Title">
        <item name="android:textSize">20sp</item>
        <item name="android:textColor">@color/color_red</item>
    </style>
```

配置副标题：

```java
toolbar.setSubtitle("Subtitle");//设置子标题
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.darker_gray));//设置子标题颜色
        toolbar.setSubtitleTextAppearance(this, R.style.Theme_ToolBar_Base_Subtitle);//设置子标题的外观，包括文字颜色，文字大小等
```

```java
 <style name="Theme.ToolBar.Base.Subtitle" parent="@style/TextAppearance.Widget.AppCompat.Toolbar.Subtitle">
        <item name="android:textSize">15sp</item>
        <item name="android:textColor">@color/color_43d28d</item>
    </style>
```

配置Home导航图标

```java
toolbar.setNavigationIcon(R.mipmap.home);
```

配置Logo：

```java
toolbar.setLogo(R.mipmap.ic_logo);
```

配置ToolBar背景色：

方式一：布局文件中设置android:background属性

```java
android:background="@color/blue"
```

方式二：代码中调用toolbar的setBackgroundColor()方法

```java
 toolbar.setBackgroundColor(getResources().getColor(R.color.color_red));
```

响应Menu菜单的点击事件：

```java
toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    //do something
 
                } 
                return true;
            }
        });
```

或者覆盖onOptionsItemSelected()方法

```java
 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
 
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
```



> 如果是在fragment使用menu，需要在onCreate方法里调用 setHasOptionsMenu(true);

```java
 @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }
```

### 防止没有Menu按键的手机不显示Menu：

该特性由ViewConfiguration这个类中一个叫做sHasPermanentMenuKey的静态变量控制，系统根据这个变量的值来判断手机有没有物理Menu键的。当然这是一个内部变量，无法直接访问它，可以通过反射的方式修改它的值，让它为false。

```java
 private void setMenuAlwaysShow() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

此处参考：Android ActionBar完全解析，使用官方推荐的最佳导航栏(上) - 郭霖的专栏 - 博客频道 - CSDN.NET

[http://blog.csdn.net/guolin_blog/article/details/18234477](http://blog.csdn.net/guolin_blog/article/details/18234477)

### 配置Menu的icon图标：

在ToolBar的主题中配置name="actionOverflowButtonStyle"属性，创建一个主题继承自android:style/Widget.Holo.Light.ActionButton.Overflow，配置name="android:src"属性：

```java
<style name="Theme.ToolBar.Base" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:textSize">20sp</item>
        <item name="android:textColorPrimary">@color/black_424242</item>
        <!--s设置Menu菜单的背景色-->
        <item name="android:itemBackground">@color/blue_600</item>
        <!--设置menu菜单不遮挡actionbar-->
        <item name="actionOverflowMenuStyle">@style/OverflowMenu</item>
        <!--配置Menu的图标-->
        <item name="actionOverflowButtonStyle">@style/ToolBar.ActionButton.Overflow</item>
    </style>
```

```java
<style name="ToolBar.ActionButton.Overflow" parent="android:style/Widget.Holo.Light.ActionButton.Overflow">
        <item name="android:src">@mipmap/ic_menu_more_overflow</item>
    </style>
```

### 配置Menu菜单的OvweFlow的action：

基本和ActionBar相同

### 配置OvweFlow中的action文字颜色：

在ToolBar的主题中配置android:textColorPrimary属性

```java
<style name="Theme.ToolBar.Base" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:textSize">20sp</item>
        <item name="android:textColorPrimary">@color/color_red</item>
    </style>
```

或者在Toolbar的 app:popupTheme="@style/AppTheme.PopupOverlay"主题中配置android:textColorPrimary属性：

```java
 app:popupTheme="@style/AppTheme.PopupOverlay"
```

```java
 <style name="AppTheme.PopupOverlay" parent="ThemeOverlay.AppCompat.Light">>
        <item name="android:textColorPrimary">#fff</item>
    </style>
```

### 配置OvweFlow中的action背景：

在ToolBar的主题中配置android:itemBackground属性

```java
 <!--s设置Menu菜单的背景色-->
        <item name="android:itemBackground">@color/black_light</item>
```

### 配置OvweFlow中的action的宽度：

在Toolbar的popupTheme中设置android:width属性，但是注意Item的宽度有最大最小的限制，超过该限制后显示效果是最大宽度和最小宽度。

```java
  <style name="AppTheme.PopupOverlay" parent="ThemeOverlay.AppCompat.Light">
        <!--Item的宽度-->
        <item name="android:width">400dp</item>
        <item name="android:textColorPrimary">#fff</item>
    </style>
```

### 配置OvweFlow中的action显示icon：

默认情况下OverFlow中只显示文字，但这样不够美观。在Toolbar的popupTheme中设置android:drawableRight  android:drawableLeft  android:drawableTop  android:drawableBottom属性，注意这个设置对所有Item统一生效的，这样也就没有太大的意义。

```java
<!--<item name="android:drawableLeft">@drawable/ic_action_search</item>-->
        <item name="android:drawableRight">@drawable/ic_home_gray_36dp</item>
        <item name="android:drawableTop">@drawable/ic_textsms_gray_36dp</item>
```

是否显示Icon是由MenuBuilder这个类的setOptionalIconsVisible方法来决定的，该方法不能直接调用，可以反射之。重写boolean onCreatePanelMenu(int featureId, Menu menu)方法，在menu创建的时候调用之。

```java
  @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onCreatePanelMenu(featureId, menu);
    }
```

### 配置OvweFlow中的action间的分割线：

在Toolbar的popupTheme中设置android:dividerHeight属性，设置高度大于0就能显示分割线，分割线的默认颜色是白色。

```java
<item name="android:dividerHeight">1dp</item>
```

### 配置ToolBar的Menu不遮挡ToolBar：

Actionbar中的配置方式是：设置actionOverflowMenuStyle的android:overlapAnchor属性为false.这里同样,不过注意是在ToolBar的主题中而不是Activity的主题。

```java
<style name="Theme.ToolBar.Base" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:textSize">20sp</item>
        <item name="android:textColorPrimary">@color/color_red</item>
        <!--s设置Menu菜单的背景色-->
        <item name="android:itemBackground">@color/gray</item>
        <!--设置menu菜单不遮挡actionbar-->
        <item name="actionOverflowMenuStyle">@style/OverflowMenu</item>
 
    </style>
 
    <style name="OverflowMenu" parent="Widget.AppCompat.PopupMenu.Overflow">
        <!--兼容Api 21之前的版本 -->
        <item name="overlapAnchor">false</item>
        <!-- Api 21-->
        <item name="android:overlapAnchor">false</item>
    </style>
```

## 配置ToolBar的Home图标左侧间隙

在布局中Toolbar控件中配置属性 app:contentInsetStart="3dp"

```java
 app:contentInsetStart="3dp"
```

## ToolBar的状态栏沉浸效果：

配置ToolBar和系统状态栏背景色一致：必须配置不使用半透明状态栏才可以设置状态栏颜色，需要android4.4（Api Level 19）及以上

```java
  <style name="Theme.MyActivity" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <!--配置是否使用半透明状态栏-->
        <item name="android:windowTranslucentStatus">false</item>
        <item name="android:windowTranslucentNavigation">true</item>
        <!--状态栏颜色-->
        <item name="android:statusBarColor">@color/color_0176da</item>
    </style>
```

## 设置Toolbar 浮动：

ToolBar 没有提供显示和隐藏的方法，首先调用 setSupportActionBar(toolbar);方法把ToolBar设置为ActionBar，再使用ActionBar的方式显示隐藏

点击屏幕后显示隐藏ToolBar;

```java
 @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (actionBar == null) {
            actionBar = getSupportActionBar();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
 
                if (actionBar != null) {
                    if (actionBar.isShowing()) {
                        actionBar.hide();
                    } else {
                        actionBar.show();
                    }
                }
                break;
        }
 
        return super.onTouchEvent(event);
    }
```

但是这样设置setSupportActionBar(toolbar);之后，如果是直接在ToolBar上设置的Menu的方式;toolbar.inflateMenu(R.menu.base_toolbar_menu);ToolBar上的Menu菜单会消失,这时候需要使用复写方式添加Menu

```java
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_toolbar_menu, menu);
        return true;
    }
```

ToolBar和一个普通控件是一样的，所以浮动效果应该通过布局搞定，设置Toolbar的根布局为相对布局，同时添加一个移动动画

设置Activity的主题：

```java
 <style name="ToolbarActivityTheme" parent="AppTheme">
        <!--启用ActionBar的叠加模式(不生效)-->
        <item name="windowActionBarOverlay">true</item>
        <!--状态栏半透明-->
        <item name="android:windowTranslucentStatus">true</item>
        <!--内容填充到导航栏-->
        <item name="android:windowTranslucentNavigation">true</item>
        <!--设置全屏-->
        <item name="android:windowFullscreen">true</item>
    </style>
```

添加动画和隐藏效果：

```java
 @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (actionBar == null) {
            actionBar = getSupportActionBar();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
 
                if (actionBar != null) {
                    if (actionBar.isShowing()) {
                        int height = toolbar.getMeasuredHeight();
                        height = height <= 0 ? 112 : height;
                        startAnimation(-height, new Runnable() {
                            @Override
                            public void run() {
                                actionBar.hide();
                            }
                        });
 
                    } else {
                        actionBar.show();
                        startAnimation(0, new Runnable() {
                            @Override
                            public void run() {
 
                            }
                        });
 
                    }
                }
                break;
        }
 
        return super.onTouchEvent(event);
    }
 
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startAnimation(int transY, final Runnable end) {
 
        ViewPropertyAnimator animator = toolbar.animate().translationY(transY).setDuration(600);
        animator.start();
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
 
            }
 
            @Override
            public void onAnimationEnd(Animator animation) {
                end.run();
            }
 
            @Override
            public void onAnimationCancel(Animator animation) {
 
            }
 
            @Override
            public void onAnimationRepeat(Animator animation) {
 
            }
        });
    }
```

## 配置Activity内容填充到系统状态栏：

这样Activity的整个内容会冲到屏幕顶部，不是导航栏透明

```java
<item name="android:windowTranslucentNavigation">true</item>
```

###  ToolBar的navigationIcon一直居上不居中的异常：

排查原因竟然是在Activity中设置ToolBar的主题......哪怕这个主题中什么都不写，这个图标依然会居上，不明白什么原因

```java
 <style name="ToolBarActivityTheme" parent="Theme.AppCompat.Light">
        <!--使用toolbar取消默认的actionbar-->
        <item name="android:windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
 
        <item name="android:actionBarStyle">@style/TranslucentActionBar</item>
        <!--设置ActionBar的主题-->
        <!--<item name="android:toolbarStyle">@style/ToolBarActivityToolbarTheme</item>-->
        <item name="toolbarStyle">@style/ToolBarActivityToolbarTheme</item>
    </style>
```