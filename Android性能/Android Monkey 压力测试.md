Monkey 是Android SDK提供的一个命令行工具， 可以简单，方便地运行在任何版本的Android模拟器和实体设备上。 Monkey会发送伪随机的用户事件流，适合对app做压力测试。

# Monkey的使用

一、Monkey测试简介

Monkey测试是Android平台自动化测试的一种手段，通过Monkey程序模拟用户触摸屏幕、滑动Trackball、按键等操作来对设备上的程序进行压力测试，检测程序多久的时间会发生异常。

二、Monkey程序介绍

1) Monkey程序由Android系统自带，使用Java语言写成，在Android文件系统中的存放路径是：/system/framework/monkey.jar；

2) 
Monkey.jar程序是由一个名为“monkey”的Shell脚本来启动执行，shell脚本在Android文件系统中的存放路径是：/system/bin/monkey；这样就可以通过在CMD窗口中执行:adb
 shell monkey ｛+命令参数｝来进行Monkey测试了。


三、Monkey命令的简单帮助

要获取Monkey命令自带的简单帮助，在CMD中执行命令： 

* adb shell monkey –help

四、Monkey命令参数介绍

![](http://images2015.cnblogs.com/blog/263119/201605/263119-20160505230123154-1071480837.png)

![这里写图片描述](http://img.blog.csdn.net/20161019163821260)

1) 参数： -p

参数-p用于约束限制，用此参数指定一个或多个包（Package，即App）。指定

包之后，Monkey将只允许系统启动指定的APP。如果不指定包，Monkey将允许系统启动设备中的所有APP。

* 指定一个包：adb shell monkey -p com.idreamsky.yqwan 100

说明：com.idreamsky.yqwan为包名，100是事件计数（即让Monkey程序模拟100次随机用户事件）。

* 指定多个包：adbshell monkey -p com.idreamsky.yqwan –p com.htc.pdfreader -pcom.htc.photo.widgets 100

* 不指定包：adbshell monkey 100

　说明：Monkey随机启动APP并发送100个随机事件。

* 查看设备运行app的包

  ```java
  adb shell
  logcat | grep START
  ```

  如图：

  ![这里写图片描述](http://img.blog.csdn.net/20161019163651384)


* 要查看设备中所有的包，在CMD窗口中执行以下命令：

>adb shell
>
>cd data/data
>
>ls

如下图：

![这里写图片描述](http://img.blog.csdn.net/20161019163726382)

2) 参数:-v

用于指定反馈信息级别（信息级别就是日志的详细程度），总共分3个级别，分别对应的参数如下表所示：

日志级别 Level 0 

示例 adb shell monkey -pcom.htc.Weather –v 100

说明 缺省值，仅提供启动提示、测试完成和最终结果等少量信息



日志级别 Level 1

示例 adb shell monkey -pcom.htc.Weather –v -v 100

说明 提供较为详细的日志，包括每个发送到Activity的事件信息 



日志级别 Level 2

示例 adb shell monkey -pcom.htc.Weather –v -v –v 100

说明 最详细的日志，包括了测试中选中/未选中的Activity信息



3)参数： -s

用于指定伪随机数生成器的seed值，如果seed相同，则两次Monkey测试所产生的事件序列也相同的。

* 示例：

Monkey测试1：adbshell monkey -p com.htc.Weather –s 10 100

Monkey 测试2：adbshell monkey -p com.htc.Weather –s 10 100

两次测试的效果是相同的，因为模拟的用户操作序列（每次操作按照一定的先后顺序所组成的一系列操作，即一个序列）是一样的。操作序列虽 然是随机生成的，但是只要我们指定了相同的Seed值，就可以保证两次测试产生的随机操作序列是完全相同的，所以这个操作序列伪随机的；



4) 参数：--throttle <毫秒>

用于指定用户操作（即事件）间的时延，单位是毫秒；

* 示例：adbshell monkey -p com.htc.Weather –throttle 3000 100



5) 参数：--ignore-crashes

用于指定当应用程序崩溃时（Force & Close错误），Monkey是否停止运行。如果使用此参数，即使应用程序崩溃，Monkey依然会发送事件，直到事件计数完成。

* 示例1：adbshell monkey -p com.htc.Weather --ignore-crashes 1000

测试过程中即使Weather程序崩溃，Monkey依然会继续发送事件直到事件数目达到1000为止；

* 示例2：adbshell monkey -p com.htc.Weather 1000

测试过程中，如果Weather程序崩溃，Monkey将会停止运行。

6) 参数：--ignore-timeouts

用于指定当应用程序发生ANR（ApplicationNo Responding）错误时，Monkey是否停止运行。如果使用此参数，即使应用程序发生ANR错误，Monkey依然会发送事件，直到事件计数完成。

7) 参数：--ignore-security-exceptions

用于指定当应用程序发生许可错误时（如证书许可，网络许可等），Monkey是否停止运行。如果使用此参数，即使应用程序发生许可错误，Monkey依然会发送事件，直到事件计数完成。

8) 参数：--kill-process-after-error

用于指定当应用程序发生错误时，是否停止其运行。如果指定此参数，当应用程序发生错误时，应用程序停止运行并保持在当前状态（注意：应用程序仅是静止在发生错误时的状态，系统并不会结束该应用程序的进程）。

9) 参数：--monitor-native-crashes

用于指定是否监视并报告应用程序发生崩溃的本地代码。

10) 参数：--pct-｛+事件类别｝ ｛+事件类别百分比｝

用于指定每种类别事件的数目百分比（在Monkey事件序列中，该类事件数目占总事件数目的百分比）

--pct-touch ｛+百分比｝

调整触摸事件的百分比(触摸事件是一个down-up事件，它发生在屏幕上的某单一位置)

* adb shell monkey -p com.htc.Weather --pct-touch 101000



--pct-motion ｛+百分比｝

调整动作事件的百分比(动作事件由屏幕上某处的一个down事件、一系列的伪随机事件和一个up事件组成)

* adbshell monkey -p com.htc.Weather --pct-motion 20 1000



--pct-trackball ｛+百分比｝

调整轨迹事件的百分比(轨迹事件由一个或几个随机的移动组成，有时还伴随有点击)

* adb shell monkey -p com.htc.Weather --pct-trackball 301000

--pct-nav ｛+百分比｝



调整“基本”导航事件的百分比(导航事件由来自方向输入设备的up/down/left/right组成)

* adb shell monkey -p com.htc.Weather --pct-nav 40 1000



--pct-majornav ｛+百分比｝

调整“主要”导航事件的百分比(这些导航事件通常引发图形界面中的动作，如：5-way键盘的中间按键、回退按键、菜单按键)

* adb shell monkey -p com.htc.Weather --pct-majornav 501000



--pct-syskeys ｛+百分比｝

调整“系统”按键事件的百分比(这些按键通常被保留，由系统使用，如Home、Back、StartCall、End Call及音量控制键)

* adb shell monkey -p com.htc.Weather --pct-syskeys 601000



--pct-appswitch ｛+百分比｝

调整启动Activity的百分比。在随机间隔里，Monkey将执行一个startActivity()调用，作为最大程度覆盖包中全部Activity的一种方法

* adb shell monkey -p com.htc.Weather --pct-appswitch 701000



--pct-anyevent ｛+百分比｝

调整其它类型事件的百分比。它包罗了所有其它类型的事件，如：按键、其它不常用的设备按钮、等等

* adb shell monkey -p com.htc.Weather



--pct -anyevent 100 1000* 指定多个类型事件的百分比： 

* adb shell monkey -p com.htc.Weather --pct-anyevent 50--pct-appswitch 50 1000


注意：各事件类型的百分比总数不能超过100%；

###  Monkey Script 的使用

1. 什么是monkey script

Monkey script是按照一定的语法规则编写有序的用户事件流并适用于monkey命令工具的脚本。

2.    Monkey script编写及运行在development/cmds/monkey/src/com/android/commands/monkey/MonkeySourceScript.java源码下有一段注释规定了monkey script的基本规则，如下

      ```java
      #Start Script
      type = user
      count = 49
      speed = 1.0
      start data >>
      LaunchActivity(com.android.browser,com.android.browser.BrowserActivity)
      UserWait(5000)
      #open renren
      captureDispatchPointer(5109520,5109520,0,1150,330,0,0,0,0,0,0,0);
      captureDispatchPointer(5109521,5109521,1,1150,330,0,0,0,0,0,0,0);
      UserWait(3000)//1150,330 区域
      #close browser
      captureDispatchPointer(5109520,5109520,0,205,31,0,0,0,0,0,0,0);//0代表down
      captureDispatchPointer(5109521,5109521,1,205,31,0,0,0,0,0,0,0);//1代表up
      UserWait(2000)
      ```

      可供使用的api

      ```java

      DispatchPointer(long downTime, long eventTime, int action,

         float x, float y, float pressure, float size, int metaState,

         float xPrecision, float yPrecision, int device, int edgeFlags)
      DispatchTrackball(long downTime, long eventTime, int action,

         float x, float y, float pressure, float size, int metaState,

         float xPrecision, float yPrecision, int device, int edgeFlags)

      DispatchKey(long downTime, long eventTime, int action, int code,

         int repeat, int metaState, int device, int scancode)
      DispatchFlip(boolean keyboardOpen)
      DispatchPress(int keyCode)  //按下哪个键
      LaunchActivity(String pkg_name, String cl_name) //启动launch
      LaunchInstrumentation(String test_name, String runner_name)
      UserWait(long sleeptime)  //用户等待时间
      LongPress(int keyCode)  //长按哪个键
      PowerLog(String power_log_type)  
      PowerLog(String power_log_type, String test_case_status)
      WriteLog: write power log to sdcard
      RunCmd(String cmd)://执行cmd命令
      ```

      具体的操作步骤为:

      1.      将上述脚本复制到browser.script中

      2.      将browser.script  push 到sd卡指定目录

      3.      adb shell monkey –v –v –v –f /sdcard/browser.script –throttle 1500 100 > monkey.txt

      脚本会按照browser.script发送的指令序列每隔1.5s执行一个指令，执行100遍，并将log存在monkey.txt文件中。

### Monkeyrunner

Monkeyrunner API

主要包括三个模块

1、MonkeyRunner:这个类提供了用于连接monkeyrunner和设备或模拟器的方法，它还提供了用于创建用户界面显示提供了方法。

2、MonkeyDevice:代表一个设备或模拟器。这个类为安装和卸载包、开启Activity、发送按键和触摸事件、运行测试包等提供了方法。

3、MonkeyImage:这个类提供了捕捉屏幕的方法。这个类为截图、将位图转换成各种格式、对比两个MonkeyImage对象、将image保存到文件等提供了方法。

这里需要先安装Python

![这里写图片描述](http://img.blog.csdn.net/20161019163803713)

**运行monkeyrunner**

方式一：在CMD命令窗口直接运行monkeyrunner

方式二：使用Python编写测试代码文件，在CMD中执行monkeyrunner Findyou.py运行

不论使用哪种方式，您都需要调用SDK目录的tools子目录下的monkeyrunner命令。

注意：在运行monkeyrunner之前必须先运行相应的模拟器或连接真机，否则monkeyrunner无法连接到设备

测试demo.py

```java
from com.android.monkeyrunner import MonkeyRunner,MonkeyDevice,MonkeyImage  
#MonkeyRunner.alert('hello','title','OK')
#device=MonkeyRunner.waitForConnection() #连接手机设备

#等待主机与android设备连接
device = MonkeyRunner.waitForConnection()
MonkeyRunner.sleep(3)
#杀掉待测程序，通常用于排除干扰
device.shell('am force-stop com.tencent.mobileqq')
MonkeyRunner.sleep(3)
device.startActivity(component="com.tencent.mobileqq/com.tencent.mobileqq.activity.SplashActivity")  
#开始发送按键
MonkeyRunner.sleep(10)
device.press('KEYCODE_DPAD_UP','DOWN_AND_UP')
MonkeyRunner.sleep(2)
device.press('KEYCODE_DPAD_RIGHT','DOWN_AND_UP')
MonkeyRunner.sleep(5)
device.press("KEYCODE_DPAD_CENTER","DOWN_AND_UP")
MonkeyRunner.sleep(10)
device.press('KEYCODE_DPAD_DOWN','DOWN_AND_UP')
MonkeyRunner.sleep(3)
device.press('KEYCODE_BACK ','DOWN_AND_UP')
MonkeyRunner.sleep(3)
device.touch(300,300,'DOWN_AND_UP')
#device.type('hello')#向编辑区域输入文本'hello'
#屏幕抓图
result = device.takeSnapshot()
#文件将写入主机上,脚本所在目录
result.writeToFile('./pic001.png','png')
device.reboot() #手机设备重启
```

直接运行

![这里写图片描述](http://img.blog.csdn.net/20161019163742101)

引入程序所用的模块

```java
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice, MonkeyImage 
from com.android.monkeyrunner import MonkeyRunner as mr
from com.android.monkeyrunner import MonkeyDevice as md
from com.android.monkeyrunner import MonkeyImage as mi
```

如果给导入的模块起了别名，就必须使用别名，否则会出现错误。
比如连接设备或模拟器，起了以上别名后，命令应该如下：

> device=mr.waitForConnection()



连接到设备或模拟器

参数1：超时时间，单位秒，浮点数。默认是无限期地等待。
参数2：串deviceid，指定的设备名称。默认为当前设备（手机优先，比如手机通过USB线连接到PC、其次为模拟器）。
默认连接：

> device = MonkeyRunner.waitForConnection()

参数连接：

> device = MonkeyRunner.waitForConnection(1.0,'4df74b8XXXXXXX') 

\#向设备或模拟器安装APK 

以下两种方式都是对的

```java
device.installPackage('E:/JAVA/monkeyrunner/Test1/ThinkDrive_new.apk')

device.installPackage('E:\JAVA\monkeyrunner\Test1\ThinkDrive_new.apk')
```

参数可以为绝对路径，也可为相对路径

卸载设备或模拟器中的APK 

> device.removePackage('cn.richinfo.thinkdrive') 

启动任意的Activity

>  device.startActivity(component="包名/启动Activity")

以下两种都OK

```java
device.startActivity(component="cn.richinfo.thinkdrive/cn.richinfo.thinkdrive.ui.activities.NavigateActivity")

device.startActivity(component="cn.richinfo.thinkdrive/.ui.activities.NavigateActivity")
```

手机截图 

获取设备的屏蔽缓冲区，产生了整个显示器的屏蔽捕获。（截图）

> result=device.takeSnapshot()

返回一个MonkeyImage对象（点阵图包装），我们可以用以下命令将图保存到文件

> result.writeToFile('E:\\JAVA\\monkeyrunner\\Test1\\Test1_001.png','png')

暂停 

暂停目前正在运行的程序指定的秒数

> MonkeyRunner.sleep(5)

字符串发送到键盘 

> device.type('Findyou')

 唤醒设备屏幕

锁屏后,屏幕关闭，可以用下命令唤醒

> device.wake()

重启手机

> device.reboot()

 模拟滑动

> device.drag(X,Y,D,S)
>
> X 开始坐标
>
> Y 结束坐标
>
> D 拖动持续时间(以秒为单位)，默认1.0秒
>
> S 插值点时要采取的步骤。默认值是10
>
> device.drag((100,1053),(520,1053),0.1,10)

在指定位置发送触摸事件

> device.touch(x,y,触摸事件类型)
>
> x,y的单位为像素
> 触摸事件类型，请见下文中Findyou对device.press描述
> device.touch(520,520,'DOWN_AND_UP')

发送指定类型指定键码的事件

```java
#device.press(参数1:键码,参数2:触摸事件类型)
#参数1：见android.view.KeyEvent
#参数2，如有TouchPressType()返回的类型－触摸事件类型，有三种。
#1、DOWN 发送一个DOWN事件。指定DOWN事件类型发送到设备，对应的按一个键或触摸屏幕上。
#2、UP 发送一个UP事件。指定UP事件类型发送到设备，对应释放一个键或从屏幕上抬起。
#3、DOWN_AND_UP 发送一个DOWN事件，然后一个UP事件。对应于输入键或点击屏幕。

以上三种事件做为press()参数或touch()参数

#按下HOME键
device.press('KEYCODE_HOME',MonkeyDevice.DOWN_AND_UP) 
#按下BACK键
device.press('KEYCODE_BACK',MonkeyDevice.DOWN_AND_UP) 
#按下下导航键
device.press('KEYCODE_DPAD_DOWN',MonkeyDevice.DOWN_AND_UP) 
#按下上导航键
device.press('KEYCODE_DPAD_UP',MonkeyDevice.DOWN_AND_UP) 
#按下OK键
device.press('KEYCODE_DPAD_CENTER',MonkeyDevice.DOWN_AND_UP) 
```

KeyCode: 

home键 KEYCODE_HOME 

back键 KEYCODE_BACK 

send键 KEYCODE_CALL 

end键 KEYCODE_ENDCALL 

上导航键 KEYCODE_DPAD_UP 

下导航键 KEYCODE_DPAD_DOWN 

左导航 KEYCODE_DPAD_LEFT 

右导航键 KEYCODE_DPAD_RIGHT  

ok键 KEYCODE_DPAD_CENTER 

上音量键 KEYCODE_VOLUME_UP  

下音量键 KEYCODE_VOLUME_DOWN 

power键 KEYCODE_POWER 

camera键 KEYCODE_CAMERA 

menu键 KEYCODE_MENU

