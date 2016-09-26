#### 开机自动启动app的实现是比较简单的，监听一个开机广播即可。当监听到开机广播后打开想要启动的app即可，具体实现如下：

##### 1. 创建广播接收器：BootBroadcastReceiver。

```
package com.losileeya.bootstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-26
 * Time: 21:14
 * 类描述：
 *
 * @version :
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(ACTION)){
            // final  Intent mainactivity= new Intent(context,MainActivity.class);//启动具体的activity
          final   Intent mainactivity = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());//通过包名的Launch启动
           mainactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             final  Context mContext = context;
           new Handler().postDelayed(new Runnable(){
               public void run() {
                   mContext.startActivity(mainactivity);
               }
           }, 10000);
           //也可以启动服务
             //后边的XXX.class就是要启动的服务  
          ///Intent service = new Intent(context,XXXclass);  
          // context.startService(service);  
       }
    }

}
```

##### 2. 在application声明Receiver。

```java
         <receiver android:name=".BootBroadcastReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                   <category android:name="android.intent.category.LAUNCHER" />  
            </intent-filter>
        </receiver>
```

##### 3. 声明权限。

```java
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"></uses-permission>
```

##### 4. 特殊说明：

1） Apk需要设置默认安装到手机内存，外设SD卡是接收不到开机广播，这里只针对安装位置为手机内存的app。

2）小米手机收不到开机广播的处理办法：系统与安全文件夹--->安全中心--->授权管理--->自启动管理--->对本App添加自启动授权

