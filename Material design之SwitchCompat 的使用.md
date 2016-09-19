 在Android 5.0 中 Switch 更新了样式 变得 比较好用了 
但是在5.0 以下的版本 还是老样子 不实用 因此 就有了 SwitchCompat 来兼容 它是v7 包中的 因此可兼容到 2.1

使用时 写一下布局即可。。

```java
<android.support.v7.widget.SwitchCompat
        android:id="@+id/switch1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" /> 
```

这个switch 默认是 粉红色的 有的时候我们需要设置成和我们的主题颜色一致 就需要更改 switch的颜色 
一种方式是 设置 thumb(拇指按钮) 和 track(轨迹) 颜色

```java
switch1 = (SwitchCompat) view.findViewById(R.id.switch1);
        switch1.setTrackResource();
        switch1.setThumbResource();
```

如果有适合的图片 可以这样设置 但是通常这样设置是比较麻烦的 需要合适的图片 
因此就有了下面的简便的方法 ：

在style 中设置

```java
  <!-- Active thumb color & Active track color(30% transparency) switch 打开时的拇指按钮的颜色 轨迹颜色默认为30%这个颜色 -->  
            <item name="colorControlActivated">@color/theme_color_green</item>
            <!-- Inactive thumb color switch关闭时的拇指按钮的颜色 -->
            <item name="colorSwitchThumbNormal">@color/colorAccent</item>
            <!-- Inactive track color(30% transparency) switch关闭时的轨迹的颜色  30%这个颜色 -->
            <item name="android:colorForeground">@color/colorPrimaryDark</item>
```

这样就完成了

这个控件使用也非常简单。下面就说一下其他相关用法：

```java
//SwitchCompat被竖线隔开
switchCompat.setSplitTrack(false);

//SwitchCompat右边会出现错误提示
switchCompat.setError("error");

//是否显示文字[默认为 开启/关闭](当然也可以自定义文字)
switchCompat.setShowText(true);

//自定义文字
switchCompat.setTextOff("Off");
switchCompat.setTextOn("On");

//设置左边文字和右边按钮的距离
switchCompat.setSwitchPadding(20);

//设置关闭和开启
switchCompat.setChecked(true/false);


//监听switchCompat开启和关闭变化
switchCompat.setOnCheckedChangeListener();

//设置Track图标
switchCompat.setTrackResource(R.mipmap.ic_back_gray);

//switchCompat设置指示图标[但是开启和关闭都是一个图标,可以在setOnCheckedChangeListener里动态设置]
switchCompat.setThumbResource(R.mipmap.ic_back_gray);
```

切换按钮的效果这里就不贴了，自己体会。