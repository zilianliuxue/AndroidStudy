内存泄露，是Android开发者最头疼的事。可能一处小小的内存泄露，都可能是毁于千里之堤的蚁穴。怎么才能检测内存泄露呢？网上教程非常多，不过很多都是使用Eclipse检测的, 其实1.3版本以后的Android Studio 检测内存非常方便, 如果结合上MAT工具,LeakCanary插件,一切就变得so easy了。

# 熟悉Android Studio界面工欲善其事,必先利其器。

我们接下来先来熟悉下Android Studio的界面

内存分析界面

一般分析内存泄露, 首先运行程序,打开日志控制台,有一个标签Memory ,我们可以在这个界面分析当前程序使用的内存情况, 一目了然, 我们再也不需要苦苦的在logcat中寻找内存的日志了。

**图中蓝色区域，就是程序使用的内存， 灰色区域就是空闲内存，**

当然，Android内存分配机制是对每个应用程序逐步增加, 比如你程序当前使用30M内存, 系统可能会给你分配40M, 当前就有10M空闲, 如果程序使用了50M了,系统会紧接着给当前程序增加一部分,比如达到了80M， 当前你的空闲内存就是30M了。 当然,系统如果不能再给你分配额外的内存,程序自然就会OOM(内存溢出)了。 每个应用程序最高可以申请的内存和手机密切相关，比如我当前使用的华为Mate7,极限大概是200M,算比较高的了, 一般128M 就是极限了, 甚至有的手机只有可怜的16M或者32M，这样的手机相对于内存溢出的概率非常大了。

## 我们怎么检测内存泄露呢

首先需要明白一个概念, 内存泄露就是指,本应该回收的内存,还驻留在内存中。一般情况下,高密度的手机,一个页面大概就会消耗20M内存,如果发现退出界面,程序内存迟迟不降低的话,可能就发生了严重的内存泄露。我们可以反复进入该界面，然后点击dump java heap 这个按钮,然后Android Studio就开始干活了,下面的图就是正在dump

![](http://upload-images.jianshu.io/upload_images/1132780-7b6085de6f082879.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

​                                         正在dump

dump成功后会自动打开 hprof文件,文件以Snapshot+时间来命名

![](http://upload-images.jianshu.io/upload_images/1132780-65b2ff097ceda16e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

​                                                内存分析结果

通过Android Studio自带的界面,查看内存泄露还不是很智能,我们可以借助第三方工具,常见的工具就是MAT了,下载地址 [http://eclipse.org/mat/downloads.php](http://eclipse.org/mat/downloads.php) ,这里我们需要下载独立版的MAT. 下图是MAT一开始打开的界面, 这里需要提醒大家的是，MAT并不会准确地告诉我们哪里发生了内存泄漏，而是会提供一大堆的数据和线索，我们需要自己去分析这些数据来去判断到底是不是真的发生了内存泄漏。

![](http://upload-images.jianshu.io/upload_images/1132780-f4974d8f4aa75267.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

​                                                MAT主界面

接下来我们需要用MAT打开内存分析的文件, 上文给大家介绍了使用Android Studio生成了 hprof文件, 这个文件在呢, 在Android Studio中的Captrues这个目录中,可以找到hprof目录；

注意,这个文件不能直接交给MAT, MAT是不识别的, 我们需要右键点击这个文件,转换成MAT识别的。 导出标准的hprof

然后用MAT打开导出的hprof(File->Open heap dump) MAT会帮我们分析内存泄露的原因

  ![](http://upload-images.jianshu.io/upload_images/1132780-6c9c8639bb131edb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



​                                                         打开标准的hprof



![自动分析内存泄露的原因](http://upload-images.jianshu.io/upload_images/1132780-10748547ba059f3e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



​                                                   自动分析内存泄露的原因

使用请参考:[MAT使用入门](http://www.jianshu.com/p/d8e247b1e7b2)

## LeakCanary

之前碰到的OOM问题，终于很直白的呈现在我的眼前：我尝试了MAT，但是发现不怎么会用。直到今天终于发现了这个新工具：

当我们的App中存在内存泄露时会在通知栏弹出通知：

![这里写图片描述](http://img.blog.csdn.net/20151029231843172)

当点击该通知时，会跳转到具体的页面，展示出Leak的引用路径，如下图所示：

![这里写图片描述](http://img.blog.csdn.net/20151029231938265)

LeakCanary 可以用更加直白的方式将内存泄露展现在我们的面前。



工程包括：

1. LeakCanary库代码
2. LeakCanaryDemo示例代码

使用步骤：

1. 将LeakCanary import 入自己的工程

2. 添加依赖：

   ```java
   dependencies {
      debugCompile 'com.squareup.leakcanary:leakcanary-android:1.4'
      releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.4'
      testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.4'
    }
   ```

   ​

3. 在Application中进行配置

   ```
   public class ExampleApplication extends Application {

     ......
     //在自己的Application中添加如下代码
   public static RefWatcher getRefWatcher(Context context) {
       ExampleApplication application = (ExampleApplication) context
               .getApplicationContext();
       return application.refWatcher;
   }

     //在自己的Application中添加如下代码
   private RefWatcher refWatcher;

   @Override
   public void onCreate() {
       super.onCreate();
       ......
           //在自己的Application中添加如下代码
       refWatcher = LeakCanary.install(this);
       ......
   }

   .....
   }

   ```

4. 在Activity中进行配置

```
public class MainActivity extends AppCompatActivity {

    ......
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //在自己的应用初始Activity中加入如下两行代码
        RefWatcher refWatcher = ExampleApplication.getRefWatcher(this);
        refWatcher.watch(this);

        textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsyncTask();
            }
        });

    }

    private void async() {

        startAsyncTask();
    }

    private void startAsyncTask() {
        // This async task is an anonymous class and therefore has a hidden reference to the outer
        // class MainActivity. If the activity gets destroyed before the task finishes (e.g. rotation),
        // the activity instance will leak.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Do some slow work in background
                SystemClock.sleep(20000);
                return null;
            }
        }.execute();
    }


}

```

1. 在AndroidMainfest.xml 中进行配置,添加如下代码

```
        <service
            android:name="com.squareup.leakcanary.internal.HeapAnalyzerService"
            android:enabled="false"
            android:process=":leakcanary" />
        <service
            android:name="com.squareup.leakcanary.DisplayLeakService"
            android:enabled="false" />

        <activity
            android:name="com.squareup.leakcanary.internal.DisplayLeakActivity"
            android:enabled="false"
            android:icon="@drawable/__leak_canary_icon"
            android:label="@string/__leak_canary_display_activity_label"
            android:taskAffinity="com.squareup.leakcanary"
            android:theme="@style/__LeakCanary.Base" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```

以下是我找到的学习资料，写的非常棒： 
1、[LeakCanary: 让内存泄露无所遁形](http://www.liaohuqiu.net/cn/posts/leak-canary/) 
2、[LeakCanary 中文使用说明](http://www.liaohuqiu.net/cn/posts/leak-canary-read-me/)

**AndroidStudio** (官方)上使用LeakCanary 请移步： 
[https://github.com/square/leakcanary](https://github.com/square/leakcanary)

**Eclipse** 上使用LeakCanary 请移步我的： 
[https://github.com/SOFTPOWER1991/LeakcanarySample-Eclipse](https://github.com/SOFTPOWER1991/LeakcanarySample-Eclipse)

**android studio** （自己弄的）上使用LeakCanary也可以看这个：

[leakcanarySample_androidStudio](https://github.com/SOFTPOWER1991/leakcanarySample_androidStudio)

## 追踪内存分配

如果我们想了解内存分配更详细的情况,可以使用Allocation Traker来查看内存到底被什么占用了。用法很简单：

![](http://upload-images.jianshu.io/upload_images/1132780-0244bebf7437f309.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

点一下是追踪， 再点一下是停止追踪， 停止追踪后 .alloc文件会自动打开,打开后界面如下:

![](http://upload-images.jianshu.io/upload_images/1132780-01804a7ff4472f96.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

当你想查看某个方法的源码时,右键选择的方法,点击Jump to source就可以了

## 查询方法执行的时间

Android Studio 功能越来越强大了, 我们可以借助AS观测各种性能,如下图:

![](http://upload-images.jianshu.io/upload_images/1132780-b8e2a2b6826ffe10.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

Android studio2.1 界面发生了变化，功能没有太大区别：

![](http://upload-images.jianshu.io/upload_images/1132780-d4013b5b63fb7cae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果我们要观测方法执行的时间,就需要来到CPU界面

![](http://upload-images.jianshu.io/upload_images/1132780-9cdfc9784f6a27db.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

点击Start Method Tracking, 一段时间后再点击一次, trace文件被自动打开,

![](http://upload-images.jianshu.io/upload_images/1132780-a66064f4a41d08fa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**非独占时间:** 某函数占用的CPU时间,包含内部调用其它函数的CPU时间。
**独占时间:** 某函数占用CPU时间,但不含内部调用其它函数所占用的CPU时间。

### 我们如何判断可能有问题的方法？

通过方法的调用次数和独占时间来查看，通常判断方法是：

1. 如果方法调用次数不多，但每次调用却需要花费很长的时间的函数，可能会有问题。
2. 如果自身占用时间不长，但调用却非常频繁的函数也可能会有问题。

