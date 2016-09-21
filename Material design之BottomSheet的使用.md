

在Android Support Library 23.2版本推出之后，我们可以看到一些新的特性,例如AppCompat DayNight主题，BottomSheet等，其中BottomSheet控件用于从屏幕底部向上滑动，以显示更多的内容。本篇文章是想总结一下我在学习BottomSheet过程中的一些笔记以及一些需要注意的地方。



![这里写图片描述](http://img.blog.csdn.net/20160921160614108)



效果的实现很简单我们只需要给这个可滑动的view添加BottomSheetBehavior 表现行为即可，把这个behavior指定为android.support.design.widget.BottomSheetBehavior就可以达到这种效果了，来看看代码吧

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:onClick="fuck"
        android:text="点我啊" />
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:onClick="simplebottomsheetdialog"
        android:text="simplebottomsheetdialog"/>
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:onClick="recycleView"
        android:text="recycleView"/>
    <android.support.design.widget.CoordinatorLayout
        android:id="@+id/cl"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    <android.support.v4.widget.NestedScrollView
        android:id="@+id/bottom_sheet"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_behavior="@string/bottom_sheet_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:background="#00ffff"
                android:text="Hello World!" />
        </LinearLayout>

    </android.support.v4.widget.NestedScrollView>
</android.support.design.widget.CoordinatorLayout>
</LinearLayout>

```

我们也看到了，我们需要CoordinatorLayout才有 layout_behavior属性。

代码也很简单，首先我们通过BottomSheetBehavior.from()方法来获取到它的Behavior，然后通过getState方法来判断现在的状态，如果是展开的状态，我们就让它收缩起来；反之，则展开。 
其中BottomSheet 有这么几种状态：

- STATE_COLLAPSED ：被折叠的状态
- STATE_SETTING ：沉降，我的理解是视图从脱离手指自由滑动到最终停下的这一小段时间
- STATE_EXPANDED ：完全展开的状态
- STATE_HIDDEN ：被隐藏的状态，默认是false，可通过app:behavior_hideable属性设置
- STATE_DRAGGING ：被拖拽的状态

```java
  bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变回调
                //newState 有四个状态 ：
                //展开 BottomSheetBehavior.STATE_EXPANDED
                //收起 BottomSheetBehavior.STATE_COLLAPSED
                //拖动 BottomSheetBehavior.STATE_DRAGGING
                //下滑 BottomSheetBehavior.STATE_SETTLING
                Log.d("MainActivity", "newState:" + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，slideOffset为0-1 完全收起为0 完全展开为1
                Log.d("MainActivity", "slideOffset:" + slideOffset);
            }
        });
```

实现隐藏和展开：

```java
  Toast.makeText(this, "我日", Toast.LENGTH_SHORT).show();
          if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
             bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }else {
              bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          }
```

接下来我们来使用BottomSheetDialog ：

Support Library 23.2中还提供了BottomSheetDialog和BottomSheetDialogFragment。BottomSheetDialog的用法与普通的dialog差不多，显示的内容可以是列表，也可以是简单的对话，当显示的内容一滑入屏幕时，屏幕的其余部分会变暗，以衬托Dialog重点。下面看下BottomSheetDialog的简单使用：

```java
BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();
```

下面我们来使用简单的BottomSheetDialog：

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <LinearLayout
        android:id="@+id/share_qq"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:orientation="horizontal">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:src="@mipmap/ic_launcher"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="分享到QQ"/>
    </LinearLayout>
    <LinearLayout
        android:id="@+id/share_wx"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:orientation="horizontal">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:src="@mipmap/ic_launcher"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="分享到微信"/>
    </LinearLayout>
    <LinearLayout
        android:id="@+id/share_sina"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:orientation="horizontal">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:src="@mipmap/ic_launcher"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="分享到微博"/>
    </LinearLayout>

</LinearLayout>
```

代码实现：

```java
 final    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this,R.layout.share,null);
        View qq = inflate.findViewById(R.id.share_qq);
        View wx = inflate.findViewById(R.id.share_wx);
        View sina = inflate.findViewById(R.id.share_sina);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到qq", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到wx", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "分享到sina", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
```

最后我们用RecyclerView来实现获取安卓手机应用列表：

先来写个item布局：

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="wrap_content"
    android:gravity="center"
    android:layout_height="wrap_content">
     <ImageView
         android:id="@+id/icon"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content" />
    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>
```

具体代码实现：

```java
package com.losileeya.bottomsheetmaster;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-21
 * Time: 13:56
 * 类描述：
 *
 * @version :
 */
public class AppInfo {
    private String appLabel;
    private Drawable appIcon;
    private Intent intent ;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}

```

我们的RecyclerView的adapter的实现：

```java
package com.losileeya.bottomsheetmaster;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-21
 * Time: 14:01
 * 类描述：
 *
 * @version :
 */
public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<AppInfo> datas;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public RecyclerAdapter(List<AppInfo> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        holder.tv.setText(datas.get(position).getAppLabel());
        holder.icon.setImageDrawable(datas.get(position).getAppIcon());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClickListener(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size()>0?datas.size():0;
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder{
         TextView tv;
         ImageView icon;
         public MyViewHolder(View itemView) {
             super(itemView);
             tv= (TextView) itemView.findViewById(R.id.tv);
             icon= (ImageView) itemView.findViewById(R.id.icon);
         }
     }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClickListener(View item, int position);
    }
}

```

获取到系统app的信息：

```java
 // 获得所有启动Activity的信息，类似于Launch界面
    public void queryAppInfo() {
        mlistAppInfo = new ArrayList<AppInfo>();
        PackageManager pm = this.getPackageManager(); //获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                mlistAppInfo.add(appInfo); // 添加至列表中
            }
        }
    }
```

具体使用RecyclerView：

```java
   queryAppInfo();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        //创建recyclerView
        RecyclerView recyclerView = new RecyclerView(this);
        GridLayoutManager layoutManager= new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mlistAppInfo,this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View item, int position) {
                Toast.makeText(MainActivity.this,mlistAppInfo.get(position).getAppLabel(), Toast.LENGTH_SHORT).show();
                Intent intent = mlistAppInfo.get(position).getIntent();
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(recyclerView);
        bottomSheetDialog.show();
```



好了，本篇文章就这样子啦，存在总结不到位的地方还望指导，感谢^_^










