因为这些都比较简单，我就放在一起说了。

#### 1> SnackBar 使用方法很简单[和Toast差不多]，如下所示：

```java
//带按钮的
Snackbar.make(container, "Snackbar with action", Snackbar.LENGTH_SHORT)
        .setAction("Action", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(v.getContext(), "Snackbar Action pressed",Toast.LENGTH_SHORT).show();
            }
        }).show();



//纯文本的
Snackbar.make(container, "This is Snackbar", Snackbar.LENGTH_SHORT).show();
```

#### 2> FloatingActionButton

使用方法：

```java
<android.support.design.widget.FloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:clickable="true"
    android:src="@null"
    app:backgroundTint="#0091eb"
    app:fabSize="normal"/>
```

通过android:src修改图片 
通过app:backgroundTint修改背景颜色 
通过app:fabSize设置大小，只有两个选项normal和mini

你也可以参考[

[FloatingActionButton 完全解析](http://blog.csdn.net/lmj623565791/article/details/46678867)

[android悬浮按钮（Floating action button）的两种实现方法](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1028/1857.html)

#### 3> Shadows

在布局中设置阴影

```java
android:elevation="2dp"
app:elevation="2dp" //如果在Toolbar等控件，一定要加上这句，否则设置无效12
```

#### 4> Ripple效果

在res目录下新建drawable-v21. 然后建一个资源文件 如下所示：

```java
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="#dcdcdc"> //android:color 按下去的效果
    <item> //默认效果
        <shape>
            <solid android:color="#0091eb" />
            <corners android:radius="2dp" />
        </shape>
    </item>
</ripple>
```

需要注意的是，需要在drawable目录下新建同样名称的资源文件，否则在低版本上运行去闪退，因为找不到该文件，drawable-v21只在android5.0或以上系统有效。