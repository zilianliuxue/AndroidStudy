CardView是Android5.0之后为新增的控件，说白了就是一个viewGroup，可以带圆角阴影和水波纹效果的控件。

必须先添加依赖：

```java
compile 'com.android.support:cardview-v7:24.2.0'  
```

### 基本使用

CardView是一个新增加的UI控件，我们先在代码中定义一个CardView的变量，然后查看源码。

```java
public class CardView extends FrameLayout implements CardViewDelegate {  
            ...  
    } 
```

从源码看，CardView继承FrameLayout，所以CardView是一个ViewGroup，我们可以在里面添加一些控件进行布局。既然CardView继承FrameLayout，而且Android中早已有了FrameLayout布局，为什么还有使用CardView这个布局控件呢？我们先来看看官网对此类的注释

A FrameLayout  with  a rounded corner background and shadow.

这个FrameLayout特殊点就是有rounded corner（圆角）和shadow（阴影），这个就是它的特殊之处，回首往日，我们需要自定义shape文件进行实现圆角和阴影的设计，现在google的大牛已经把它设计为CardView的属性供我们设置进行使用。下面我们看看CardView新增了哪些属性：

- CardView_cardBackgroundColor 设置背景色
- CardView_cardCornerRadius 设置圆角大小
- CardView_cardElevation 设置z轴阴影
- CardView_cardMaxElevation 设置z轴最大高度值
- CardView_cardUseCompatPadding 是否使用CompadPadding
- CardView_cardPreventCornerOverlap 是否使用PreventCornerOverlap
- CardView_contentPadding 内容的padding
- CardView_contentPaddingLeft 内容的左padding
- CardView_contentPaddingTop 内容的上padding
- CardView_contentPaddingRight 内容的右padding
- CardView_contentPaddingBottom 内容的底padding

card_view:cardUseCompatPadding 设置内边距，V21+的版本和之前的版本仍旧具有一样的计算方式

card_view:cardPreventConrerOverlap 在V20和之前的版本中添加内边距，这个属性为了防止内容和边角的重叠

下面开始简单的进行使用：
1、普通使用效果

```java
<android.support.v7.widget.CardView  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content">  
        <TextView  
            android:layout_width="match_parent"  
            android:layout_height="70dp"  
            android:text="正常使用效果"  
            android:gravity="center_horizontal|center_vertical"  
            android:textColor="#000000"  
            android:textSize="20sp"  
            android:padding="10dp"  
            android:layout_margin="10dp"/>  
    </android.support.v7.widget.CardView>  
```

效果图： 
![nomal](http://img.blog.csdn.net/20151003105553715)

2、增加背景色和圆角的效果，注意我们此时使用background属性是没效果的：

```java
<android.support.v7.widget.CardView  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content"  
        app:cardBackgroundColor="#669900"  
        app:cardCornerRadius="10dp">  
        ...  
    </android.support.v7.widget.CardView>  
```

效果图： 
![back](http://img.blog.csdn.net/20151003105635460)

3、设置z轴阴影

```java
<android.support.v7.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="#669900"
    app:cardElevation="20dp"
    app:cardCornerRadius="10dp">
    ...
</android.support.v7.widget.CardView>
```

效果图： 
![elave](http://img.blog.csdn.net/20151003105659657)

通过上面的演示，我们发现CardView的卡片布局效果很不错，如果用在ListView、RecyclerView中一定也有不错的效果，那么现在我们来使用下。

定义RecyclerView的item的布局：

```java
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"  
        xmlns:app="http://schemas.android.com/apk/res-auto"  
        app:cardBackgroundColor="#80cbc4"  
        app:cardCornerRadius="10dp"  
        app:cardPreventCornerOverlap="true"  
        app:cardUseCompatPadding="true"  
        android:layout_width="match_parent"  
        android:layout_height="wrap_content">  
        <RelativeLayout  
            android:layout_width="match_parent"  
            android:layout_height="100dp"  
            android:padding="5dp">  
            <ImageView  
                android:id="@+id/picture"  
                android:layout_width="match_parent"  
                android:layout_height="match_parent"  
                android:layout_centerInParent="true"  
                android:scaleType="centerCrop" />  
            <TextView  
                android:clickable="true"  
                android:id="@+id/name"  
                android:layout_width="match_parent"  
                android:layout_height="match_parent"  
                android:layout_marginBottom="10dp"  
                android:layout_marginRight="10dp"  
                android:gravity="right|bottom"  
                android:textColor="@android:color/white"  
                android:textSize="24sp" />  
        </RelativeLayout>  
  
    </android.support.v7.widget.CardView>  
```

其实没有什么好讲的。