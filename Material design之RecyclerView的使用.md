

抛弃ListView, 想试一试RecyclerView, 在用的过程中，遇到了一些问题，比如：如何为RecyclerView添加Header和Footer? 如何为RecyclerView添加分割线？如何为RecyclerView添加下拉刷新和上拉加载？ 在今后的一段时间里，我会针对这几个问题，通过写简书的方式一一讲述， 今天为大家带来的是第一个问题的解决方法，如何为RecyclerView添加Header和Footer？在这之前，我想分享一下我对RecyclerView的认识：

##   RecyclerView 的认识

RecyclerView 是ListView的替代者，使用方法也相似，只不过方法名都替我们规范好了。

RecyclerView功能方面比ListView更加强大。比如动画、横向滚动、瀑布流等。

从使用角度上讲，RecyclerView不仅要设置adapter，还要设置layoutManager，layoutManager也就是相对于listView强大的地方。

- RecyclerView的越来越引起了我们Android开发人员的注意，甚至很多人都说：ListView， GridView已经逐渐被RecyclerView替代， 最主要的原因就是RecyclerView的灵活性， 还有性能上的提升。那么也许有很多人会问：RecyclerView和ListView, GridView到底是什么关系呢？通过Android官方文档的一组截图告诉你：

  ![](http://upload-images.jianshu.io/upload_images/2068191-b2b479053034376a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

  ​                    ListView的家族谱（继承结构）

  ![](http://upload-images.jianshu.io/upload_images/2068191-f1c73555d7ddcc81.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

​                               RecyclerView的家族谱（继承结构）

- 通过上面的两个图可以发现， ListView继承自：AbsListView。(GirdView也是)， RecyclerView直接继承了ViewGroup , 最后得出结论：RecyclerView是ListView的爷爷辈， 也就是RecyclerView是ListView的二爷， 所以从封装的层次上得出了为什么RecyclerView性能比ListView更好的原因， 因为封装的层次越高，查询执行的速度相对较慢，当然还有一个原因，RecyclerView中自带的Adapter中加入了Holder，强制要求开发人员使用，在ListView中，很多人都不懂使用Holder, 这也导致了使用ListView性能比较差。这也是RecyclerView性能提升的一个主要原因, 当然，封装的层越高越灵活，相对使用起来也相对难，很多方法都是通过自己去封装的，比如ListView中有addHeaderView(View view), addFooterView(View view)去添加自己的Header 和Footer, 但是在RecyclerView中没有。

系统内置的LayoutManager有：

`LinearLayoutManager`、`GridLayoutManager`、`StaggeredGridLayoutManager`

### 简单使用

RecyclerView的使用和ListView的使用差不多，无非就是那三步：

第一，初始化RecyclerView；

第二，初始化数据，并且将数据通过Adapter装在到View中；

第三，通过setAdapter方法，将Adapter绑定到RecyclerView中。

```java
recyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());

linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

recyclerView.setHasFixedSize(true);

recyclerView.setLayoutManager(linearLayoutManager);

recyclerView.setAdapter(new RecyclerAdapter(list));
```

下面是 `adapter`

```java
public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<String> list;
    public RecyclerAdapter(List<String> list) {
        this.list = list;
    }
    //创建Holder和ListView中一样。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_view, parent, false));
    }
    //绑定数据
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.tvTitle.setText(list.get(position));
        itemViewHolder.tvDesc.setText(list.get(position) + " this is description");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_vibrant);
            tvDesc = (TextView) itemView.findViewById(R.id.title_desc);
        }
    }
}
```

需要说明的地方已经注释了，就不赘述了！

### 为RecyclerView添加Header和Footer

 **先看ListView实现方案**

    既然我们是看ListView提供了这个方法才决定对RecyclerView进行改造的，那么ListView中是怎么处理的呢？

```java
public void addHeaderView(View v) {  
    addHeaderView(v, null, true);  
} 
```

在addHeaderView(View v)方法中调用了一个三参数的addHeaderView方法：

```java
public void addHeaderView(View v, Object data, boolean isSelectable) {  
    final FixedViewInfo info = new FixedViewInfo();  
    info.view = v;  
    info.data = data;  
    info.isSelectable = isSelectable;  
    mHeaderViewInfos.add(info);  
    mAreAllItemsSelectable &= isSelectable;  
  
    // Wrap the adapter if it wasn't already wrapped.  
    if (mAdapter != null) {  
        if (!(mAdapter instanceof HeaderViewListAdapter)) {  
            mAdapter = new HeaderViewListAdapter(mHeaderViewInfos, mFooterViewInfos, mAdapter);  
        }  
  
        // In the case of re-adding a header view, or adding one later on,  
        // we need to notify the observer.  
        if (mDataSetObserver != null) {  
            mDataSetObserver.onChanged();  
        }  
    }  
} 
```

可以看到这里使用了一个叫 HeaderViewListAdapter的包装类，并且使用了一个FixedViewInfo的内部类来保存添加的信息。既然是一个ListView的Adapter那我们自然最关心的就是该Adapter中的getCount()、getView()等方法：

```java
public int getCount() {  
    if (mAdapter != null) {  
        return getFootersCount() + getHeadersCount() + mAdapter.getCount();  
    } else {  
        return getFootersCount() + getHeadersCount();  
    }  
} 
```

```java
public View getView(int position, View convertView, ViewGroup parent) {  
    // Header (negative positions will throw an IndexOutOfBoundsException)  
    int numHeaders = getHeadersCount();  
    if (position < numHeaders) {  
        return mHeaderViewInfos.get(position).view;  
    }  
  
    // Adapter  
    final int adjPosition = position - numHeaders;  
    int adapterCount = 0;  
    if (mAdapter != null) {  
        adapterCount = mAdapter.getCount();  
        if (adjPosition < adapterCount) {  
            return mAdapter.getView(adjPosition, convertView, parent);  
        }  
    }  
  
    // Footer (off-limits positions will throw an IndexOutOfBoundsException)  
    return mFooterViewInfos.get(adjPosition - adapterCount).view;  
}  
```

通过以上简单的分析，我们基本就知道了ListView是如何实现添加头部和添加尾部的，就是给ListView的Adapter使用了一个包装类，对头部和尾部条目做了处理，其余还是使用的被包装类的方法。

**recycleView实现头部和尾部**

 既然了解了ListView添加头部和尾部的实现方案，那么我们就按照该方案来实现自己的Recycler.Adapter的包装类吧。

    我们知道ListView的Adapter中我们通常关系的是getCount()，getView()等方法，那么Recycler.Adapter中我们关心哪些方法呢？没错就是onCreateViewHolder()、onBindViewHolder()、getItemCount()、getItemViewType()，那么只需要对这几个方法包装下就好了。



HeaderView和FooterView的布局文件，也是一个TextView,  这里只贴出了HeaderView的布局，FooterView的布局文件和FooterView的一样：

HeaderView的布局文件： header.xml:

```java
<?xml version="1.0" encoding="utf-8"?>   
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"             
        android:orientation="vertical"    
        android:layout_width="match_parent"    
        android:layout_height="100dp">  
        <TextView         
                android:id="@+id/header"  
                android:layout_width="match_parent"  
                android:layout_height="match_parent"  
                android:text="我是Header"  
                android:textSize="30sp"  
                android:textColor="#fde70b0b"  
                android:background="#f9777979"   
                android:gravity="center"/>  
</LinearLayout>
```

好了， 布局文件到这里我们已经弄好了， 下面我们直接看MainActivity中的内容：
（4） MainActivity.java中的内容为：

```java
import android.app.Activity;import android.os.Bundle;   
import android.support.v7.widget.LinearLayoutManager;  
import android.support.v7.widget.RecyclerView;  
import android.view.LayoutInflater;  
import android.view.View;  
import java.util.ArrayList;import java.util.List;   

public class MainActivity extends Activity {      
     private RecyclerView mRecyclerView;      
     private MyAdapter mMyAdapter;      
     private List<String> mList;      

     @Override       
     protected void onCreate(Bundle savedInstanceState) {  
         super.onCreate(savedInstanceState);  
         setContentView(R.layout.activity_main);   

         //RecyclerView三部曲+LayoutManager         
         mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);  
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);  
         mRecyclerView.setLayoutManager(linearLayoutManager);  
         initData();  
         mMyAdapter = new MyAdapter(mList);   
         mRecyclerView.setAdapter(mMyAdapter);     

        //为RecyclerView添加HeaderView和FooterView   
         setHeaderView(mRecyclerView);  
         setFooterView(mRecyclerView);   
    }        

    //初始化RecyclerView中每个item的数据  
    private void initData(){  
        mList = new ArrayList<String>();  
        for (int i = 0; i < 20; i++){   
            mList.add("item" + i);   
        }    
    }     

    private void setHeaderView(RecyclerView view){   
        View header = LayoutInflater.from(this).inflate(R.layout.header, view, false);  
         mMyAdapter.setHeaderView(header);  
    }       

    private void setFooterView(RecyclerView view){   
        View footer = LayoutInflater.from(this).inflate(R.layout.footer, view, false);          
        mMyAdapter.setFooterView(footer);   
    }  
}
```

- 从上面的代码中，我们可以看到，我们在MainActivity中做了两件事，一个是初始化RecyclerView相关的View, Adapter, data； 另一个是通过我们自定义的Adapter的setHeaderView()和setFooterView()方法为RecyclerView添加HeaderView和FooterView, 到这里，我们已经迫不及待的想知道MyAdapter中到底有什么东西, 直接上代码

   MyAdapter.java的代码

```java
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
public class MyAdapter extendsRecyclerView.Adapter<RecyclerView.ViewHolder> {    
    public static final int TYPE_HEADER = 0;  //说明是带有Header的   
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的   
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    //获取从Activity中传递过来每个item的数据集合   
    private List<String> mDatas;
    //HeaderView, FooterView    
    private View mHeaderView;    
    private View mFooterView;    

    //构造函数    
    public MyAdapter(List<String> list){        
        this.mDatas = list;    
    }    

    //HeaderView和FooterView的get和set函数    
    public View getHeaderView() {        
        return mHeaderView;    
    }    
    public void setHeaderView(View headerView) {        
        mHeaderView = headerView;        
        notifyItemInserted(0);    
    }    
    public View getFooterView() {        
        return mFooterView;    
    }    
    public void setFooterView(View footerView) {        
        mFooterView = footerView;        
        notifyItemInserted(getItemCount()-1);    
    }   

    /** 重写这个方法，很重要，是加入Header和Footer的关键，我们通过判断item的类型，从而绑定不同的view    * */   
    @Override
    public int getItemViewType(int position) {         
        if (mHeaderView == null && mFooterView == null){            
            return TYPE_NORMAL;       
        }        
        if (position == 0){            
            //第一个item应该加载Header            
            return TYPE_HEADER;       
        }       
        if (position == getItemCount()-1){           
            //最后一个,应该加载Footer           
            return TYPE_FOOTER;       
        }        
        return TYPE_NORMAL;   
    }    

     //创建View，如果是HeaderView或者是FooterView，直接在Holder中返回           
     @Override    
     public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        
        if(mHeaderView != null && viewType == TYPE_HEADER) {            
            return new ListHolder(mHeaderView);       
        }        
        if(mFooterView != null && viewType == TYPE_FOOTER){            
            return new ListHolder(mFooterView);       
        }        
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);        
        return new ListHolder(layout);    
    }    

    //绑定View，这里是根据返回的这个position的类型，从而进行绑定的，   HeaderView和FooterView, 就不同绑定了    
    @Override    
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        
        if(getItemViewType(position) == TYPE_NORMAL){            
            if(holder instanceof ListHolder) {                
                //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了                
                ((ListHolder) holder).tv.setText(mDatas.get(position-1));                
                return;            
            }            
            return;        
        }else if(getItemViewType(position) == TYPE_HEADER){            
            return;            
        }else{           
           return;       
         }   
      }    

    //在这里面加载ListView中的每个item的布局    
    class ListHolder extends RecyclerView.ViewHolder{        
        TextView tv;        
        public ListHolder(View itemView) {            
            super(itemView);            
            //如果是headerview或者是footerview,直接返回            
            if (itemView == mHeaderView){                
                return;            
            }            
            if (itemView == mFooterView){                
                return;            
            }            
            tv = (TextView)itemView.findViewById(R.id.item);        
         }    
    }    

    //返回View中Item的个数，这个时候，总的个数应该是ListView中Item的个数加上HeaderView和FooterView    
    @Override    
    public int getItemCount() {        
        if(mHeaderView == null && mFooterView == null){            
            return mDatas.size();        
        }else if(mHeaderView == null && mFooterView != null){            
            return mDatas.size() + 1;        
        }else if (mHeaderView != null && mFooterView == null){            
            return mDatas.size() + 1;        
        }else {            
            return mDatas.size() + 2;        
        }    
    }
}
```

- 从上面的MyAdapter类中，有setHeaderView()和setFooterView()两个方法，我们就是通过这两个方法从Activity将headerView和footerView传递过来的， 在Adapter中的onCreateViewHolder（）方法中，利用getItemViewType()返回Item的类型（你这个Item是不是Header家的？还是Footer家的？或者是ListView家的？）根据不同的类型，我们创建不同的Item的View。大概的思路就是这样子。

### 为RecyclerView添加分隔线

```java
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class MyDecoration extends RecyclerView.ItemDecoration{

    private Context mContext;
    private Drawable mDivider;
    private int mOrientation;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    //我们通过获取系统属性中的listDivider来添加，在系统中的AppTheme中设置
    public static final int[] ATRRS  = new int[]{
            android.R.attr.listDivider
    };

    public MyDecoration(Context context, int orientation) {
        this.mContext = context;
        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
        this.mDivider = ta.getDrawable(0);
        ta.recycle();
        setOrientation(orientation);
    }

    //设置屏幕的方向
    public void setOrientation(int orientation){
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");        }        mOrientation = orientation;
    } 

   @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST){
            drawVerticalLine(c, parent, state);
        }else {
            drawHorizontalLine(c, parent, state);
        }
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    public void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state){
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
            //Log.d("wnw", left + " " + top + " "+right+"   "+bottom+" "+i);
        }
    }

    //画竖线
    public void drawVerticalLine(Canvas c, RecyclerView parent, RecyclerView.State state){
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i); 

           //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL_LIST){
            //画横线，就是往下偏移一个分割线的高度
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }else {
            //画竖线，就是往右偏移一个分割线的宽度
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
```

从上面的代码中，我们还通过系统属性来适应屏幕的横屏和竖屏，然后确定画横的，还是竖的Divider，其实在里面我们做了三件事，

第一件事：获取到系统中的listDivider， 我们就是通过它在主题中去设置的,

第二件事：就是找到我们需要添加Divider的位置，从onDraw方法中去找到，并将Divider添加进去。

第三件事：得到Item的偏移量。

看看我们的MainActivity.java

```java
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    //定义RecyclerView
    private RecyclerView mRecyclerView = null;

    //定义一个List集合，用于存放RecyclerView中的每一个数据
    private List<String> mData = null;

    //定义一个Adapter
    private MyAdapter mAdapter; 

   //定义一个LinearLayoutManager
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView三步曲+LayoutManager
        initView();
        initData();
        mAdapter = new MyAdapter(this,mData);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter); 

        //这句就是添加我们自定义的分隔线
        mRecyclerView.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST));
    }

    //初始化View
    private void initView(){
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
    }

    //初始化加载到RecyclerView中的数据, 我这里只是给每一个Item添加了String类型的数据
    private void initData(){
        mData = new ArrayList<String>();
        for (int i = 0; i < 20; i++){
            mData.add("Item" + i);
        }
    }
}
```

分隔线Divider的drawable文件：divider.xml

```java
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#7b7a7a"/>
    <size android:height="1dp"/>
</shape>
```

画了一个：rectangle, 给它填充颜色，还有高度，这样就搞定了，高度小，显示出来也是一条线：其实线的本质就是长方形。这里可以根据个人需要，画不同类型的divider.

在styles.xml的AppTheme中，设置listDivider为我们的divider.xml文件：

```java
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="android:listDivider">@drawable/divider</item>
</style>
```

这样，我们将系统的listDivider设置成我们自定义的divider.  还记得我们在MyDecoration中获取系统的listDivider这个属性吗，这样通过这个属性，我们就可以将我们的divider.xml文件和MyDecoration.java进行关联了。

你也可以参考：

[Android RecyclerView 使用完全解析 体验艺术般的控件](http://blog.csdn.net/lmj623565791/article/details/45059587)

[Android RecyclerView添加头部和尾部](http://blog.csdn.net/xuehuayous/article/details/50498345)





















