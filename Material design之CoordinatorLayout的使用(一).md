[CoordinatorLayout](https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html) 实现了多种Material Design中提到的[滚动效果](http://www.google.com/design/spec/patterns/scrolling-techniques.html)。目前这个框架提供了几种不用写动画代码就能工作的方法，这些效果包括：

- 让浮动操作按钮上下滑动，为Snackbar留出空间。

  ​

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0543932-0.gif)

- 扩展或者缩小Toolbar或者头部，让主内容区域有更多的空间。

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0543025-1.gif)

- 控制哪个view应该扩展还是收缩，以及其显示大小比例，包括[视差滚动效果](https://ihatetomatoes.net/demos/parallax-scroll-effect/)动画。

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0542Y3-2.gif)

## 设置

首先确保遵循了[Design Support Library](https://guides.codepath.com/android/Design-Support-Library)的使用说明。

## 浮动操作按钮与Snackbar

CoordinatorLayout可以用来配合浮动操作按钮的 layout_anchor 和 layout_gravity属性创造出浮动效果，详情请参见[浮动操作按钮](https://guides.codepath.com/android/Floating-Action-Buttons)指南。

当[Snackbar](https://guides.codepath.com/android/Displaying-the-Snackbar)在显示的时候，往往出现在屏幕的底部。为了给Snackbar留出空间，浮动操作按钮需要向上移动。

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0543932-0.gif)

只要使用CoordinatorLayout作为基本布局，将自动产生向上移动的动画。浮动操作按钮有一个 [默认的 behavior](https://developer.android.com/reference/android/support/design/widget/FloatingActionButton.Behavior.html)来检测Snackbar的添加并让按钮在Snackbar之上呈现上移与Snackbar等高的动画。

```java
<android.support.design.widget.CoordinatorLayout
        android:id="@+id/main_content"
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
 
   <android.support.v7.widget.RecyclerView
         android:id="@+id/rvToDoList"
         android:layout_width="match_parent"
         android:layout_height="match_parent">
   </android.support.v7.widget.RecyclerView>
 
   <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|right"
        android:layout_margin="16dp"
        android:src="@mipmap/ic_launcher"
        app:layout_anchor="@id/rvToDoList"
        app:layout_anchorGravity="bottom|right|end"/>
 </android.support.design.widget.CoordinatorLayout>
```

## Toolbar的扩展与收缩

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0543025-1.gif)

首先需要确保你不是使用已经过时的ActionBar。务必遵循 [使用ToolBar作为actionbar](http://guides.codepath.com/android/Defining-The-ActionBar#using-toolbar-as-actionbar)这篇文章的指南。同样，这里也需要CoordinatorLayout作为主布局容器。

```java
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
 xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main_content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">
 
      <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
 
</android.support.design.widget.CoordinatorLayout>
```

### 响应滚动事件

接下来，我们必须使用一个容器布局：[AppBarLayout](http://developer.android.com/reference/android/support/design/widget/AppBarLayout.html)来让Toolbar响应滚动事件。

```java
<android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/detail_backdrop_height"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        android:fitsSystemWindows="true">
 
  <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
 
 </android.support.design.widget.AppBarLayout>
```

注意：根据官方的[谷歌文档](http://developer.android.com/reference/android/support/design/widget/AppBarLayout.html)，AppBarLayout目前必须是第一个嵌套在CoordinatorLayout里面的子view（现在貌似不是这样子，正确的理解是：AppBarLayout必须是CoordinatorLayout的直接子View）。

然后，我们需要定义AppBarLayout与滚动视图之间的联系。在RecyclerView或者任意支持嵌套滚动的view比如[NestedScrollView](http://stackoverflow.com/questions/25136481/what-are-the-new-nested-scrolling-apis-for-android-l)上添加app:layout_behavior。support library包含了一个特殊的字符串资源@string/appbar_scrolling_view_behavior，它和[AppBarLayout.ScrollingViewBehavior](https://developer.android.com/reference/android/support/design/widget/AppBarLayout.ScrollingViewBehavior.html)相匹配，用来通知AppBarLayout 这个特殊的view何时发生了滚动事件，这个behavior需要设置在触发事件（滚动）的view之上。

```java
<android.support.v7.widget.RecyclerView
        android:id="@+id/rvToDoList"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
```

当CoordinatorLayout发现RecyclerView中定义了这个属性，它会搜索自己所包含的其他view，看看是否有view与这个behavior相关联。AppBarLayout.ScrollingViewBehavior描述了RecyclerView与AppBarLayout之间的依赖关系。RecyclerView的任意滚动事件都将触发AppBarLayout或者AppBarLayout里面view的改变。

AppBarLayout里面定义的view只要设置了app:layout_scrollFlags属性，就可以在RecyclerView滚动事件发生的时候被触发：

```java
<android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fitsSystemWindows="true"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">
 
            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_scrollFlags="scroll|enterAlways"/>
 
 </android.support.design.widget.AppBarLayout>
```

app:layout_scrollFlags属性里面必须至少启用scroll这个flag，这样这个view才会滚动出屏幕，否则它将一直固定在顶部。可以使用的其他flag有：

- enterAlways: 一旦向上滚动这个view就可见。
- enterAlwaysCollapsed: 顾名思义，这个flag定义的是何时进入（已经消失之后何时再次显示）。假设你定义了一个最小高度（minHeight）同时enterAlways也定义了，那么view将在到达这个最小高度的时候开始显示，并且从这个时候开始慢慢展开，当滚动到顶部的时候展开完。
- exitUntilCollapsed: 同样顾名思义，这个flag时定义何时退出，当你定义了一个minHeight，这个view将在滚动到达这个最小高度的时候消失。

记住，要把带有scroll flag的view放在前面，这样收回的view才能让正常退出，而固定的view继续留在顶部。

此时，你应该注意到我们的Toolbar能够响应滚动事件了。

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G054F13-5.gif)

### 制造折叠效果

如果想制造toolbar的折叠效果，我们必须把Toolbar放在CollapsingToolbarLayout中：

```java
<android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fitsSystemWindows="true"
            app:contentScrim="?attr/colorPrimary"
            app:expandedTitleMarginEnd="64dp"
            app:expandedTitleMarginStart="48dp"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">
            
            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_scrollFlags="scroll|enterAlways"></android.support.v7.widget.Toolbar>
        </android.support.design.widget.CollapsingToolbarLayout>
```

现在效果就成了：

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0543025-1.gif)

通常，我们我们都是设置Toolbar的title，而现在，我们需要把title设置在CollapsingToolBarLayout上，而不是Toolbar。

```java
CollapsingToolbarLayout collapsingToolbar =
              (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
      collapsingToolbar.setTitle("Title");
```

### 制造视差效果

CollapsingToolbarLayout还能让我们做出更高级的动画，比如在里面放一个ImageView，然后在它折叠的时候渐渐淡出。同时在用户滚动的时候title的高度也会随着改变。

![img](http://www.jcodecraeer.com/uploads/allimg/150717/1G0541510-7.gif)

为了制造出这种效果，我们添加一个定义了app:layout_collapseMode="parallax" 属性的ImageView。

```java
<android.support.design.widget.CollapsingToolbarLayout
          android:id="@+id/collapsing_toolbar"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:fitsSystemWindows="true"
          app:contentScrim="?attr/colorPrimary"
          app:expandedTitleMarginEnd="64dp"
          app:expandedTitleMarginStart="48dp"
          app:layout_scrollFlags="scroll|exitUntilCollapsed">
 
          <android.support.v7.widget.Toolbar
              android:id="@+id/toolbar"
              android:layout_width="match_parent"
              android:layout_height="?attr/actionBarSize"
              app:layout_scrollFlags="scroll|enterAlways"></android.support.v7.widget.Toolbar>
          <ImageView
              android:src="@drawable/cheese_1"
              app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:scaleType="centerCrop"
              app:layout_collapseMode="parallax"
              android:minHeight="100dp"/>
 
      </android.support.design.widget.CollapsingToolbarLayout>
```

## 自定义Behavior

在[CoordinatorLayout 与浮动操作按钮](http://guides.codepath.com/android/Floating-Action-Buttons#using-coordinatorlayout)中我们讨论了一个自定义behavior的例子。注： 译文[http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0718/3197.html](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0718/3197.html)  。

CoordinatorLayout的工作原理是搜索定义了[CoordinatorLayout Behavior](http://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.Behavior.html) 的子view，不管是通过在xml中使用app:layout_behavior标签还是通过在代码中对view类使用@DefaultBehavior修饰符来添加注解。当滚动发生的时候，CoordinatorLayout会尝试触发那些声明了依赖的子view。

要自己定义CoordinatorLayout Behavior，你需要实现layoutDependsOn() 和onDependentViewChanged()两个方法。比如AppBarLayout.Behavior 就定义了这两个关键方法。这个behavior用于当滚动发生的时候让AppBarLayout发生改变。

```java
public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
    return dependency instanceof AppBarLayout;
}
 
public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
    // check the behavior triggered
    android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
    if (behavior instanceof AppBarLayout.Behavior) {
        // do stuff here
    }
}
```

以上内容来自泡网。