## lint 基本配置

几乎所有的开发团队的代码规范里面都有这么一条：不允许在布局文件中进行`hardcode`，原因参加见：[stackoverflow](http://stackoverflow.com/questions/8743349/hardcoded-string-row-three-should-use-string-resource)

为了达到上面的目的，我们可以通过设置AS的`code inspections`来设置静态代码检查的规则,找到`hardcode`的配置：

![](http://upload-images.jianshu.io/upload_images/699911-767eeaf119badde7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到默认的`hardcode`配置的`severity`是警告，所以我们在xml中直接写字符串时，将光标放到去可以看到警告提示：

![](http://upload-images.jianshu.io/upload_images/699911-1b24425b6a1d809a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

但是这个提示也太弱了吧，我们将'severity'提升到`error`试试：

![](http://upload-images.jianshu.io/upload_images/699911-669051d05e619fb3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**注意，提升这个地方的等级不会对代码和其他静态分析工具如lint产生影响，也不会对运行产生影响，它的作用域仅仅是IDE展示**

同样的，我们还可以设置很多其他的IDE静态代码检查，通过改变其`severity`达到更直观提示的作用，可以让开发者有一个直观的认识，哪些代码是**合法但不合规**的。如果有人不按照这个约束进行开发，那么代码中到处都是红色的错误(额，希望他不是个处女座..)。如果说通过IDE的`code inspections`是进行自律的话(实际上这个配置也是个人的行为)，那么`Android`提供的另外一个静态代码工具`lint`就是一种对别人的约束了。

lint是Android提供的一个静态代码检查的工具，我们可以在`gradle`的构建`task`中加入`link`检查。具体的使用请移步到goole文档。`link`能够检查的东西很多，参考 [所有check issue](http://tools.android.com/tips/lint-checks)。
还是上面的场景，如果我的需求是代码中存在`hardcode`,那么所有人的代码都编译不通过(现实中不可能这么变态)。

`lint`工具可以通过一个xml文件来配置，它可以用来修改某些`check issue`是否忽略(典型的例子是第三方库里面存在问题)，同时可以修改某些`issue`的默认等级。
`HardcodedText`的默认等级是警告，我们升级成`error`，并在配置文件中增加
`lintOptions {    lintConfig file("lint.xml")    abortOnError true}`，这样在我们构建项目的时候如果发生错误，将直接中断构建。

![](http://upload-images.jianshu.io/upload_images/699911-6cccc645757e0699.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

需要注意的是，点击AS上面的绿色的运行按钮是不会触发`lint`检查的，如果你想每次点击运行都进行lint检查，可以如下设置：

![](http://upload-images.jianshu.io/upload_images/699911-7bdcbe633f7fccce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](http://upload-images.jianshu.io/upload_images/699911-f8162be66c622e4c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

添加 run gradle task

![](http://upload-images.jianshu.io/upload_images/699911-f8a18d0db3ff0c1e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

task填lint即可，这样在点击AS上的运行时，会自动执行`lint`，不过这样会导致每次运行时都会变慢。

## Android lint 自动检测并删除无用资源

使用到Android 提供的 **Lint** 检查工具。安装包从原来的 *8M* 减小大现在的 *5M* 左右，主要就是一些不再使用资源文件，layout ,drawable 下的图片，asset 里面的资源。先说优化过程，后面再解释原理。

**检测过程**

#### 1.配置build 文件

在终端输入命令gradle link,强烈建议在build.gradle做好如下配置 

```
// android 选项 下面 添加lint 检测设置

lintOptions {
    //build release 版本 时 开启lint 检测
    checkReleaseBuilds true
    //lint 遇到 error 时继续 构建
    abortOnError false

}

```

#### 2.lint检测

在 Android Studio 终端选项下 执行 命令

```
gradle lint

```

在 yoru_project_dirctory/build/outputs/ 会生成 两个文件 **lint-result.xml**, lint-result.html 和文件夹 lint-result-files. 最重要的是 *lint-result.xml* 文件，里面包含了我们要解析的信息，包含项目中不再使用的资源文件信息。

命令执行过长比较长，执行成功后可以到build/outputs/lint-results.xml查看结果，当然打开html也是一样的效果，这里可以看到扫描的全部错误，在这里只关注UnusedResources错误 
![这里写图片描述](http://img.blog.csdn.net/20160404185346542) 

#### 3.执行 命令

```
android-resource-remover --xml lint-result.xml 

```

android-resource-remover 安装和使用， 请查考github 上的说明。

执行完这个命令,项目中不再使用的资源文件，包含 string ，color ,value等，全都被删除掉，是不是感觉超级方便。

#### 引入第三方jar的问题(非常重要)

一般的项目，这样使用都是没问题的，但是**如果你的项目中第三方jar ,jar中有使用到资源文件，这些文件也会被一起删除。**有没有办法解决这个问题呢 ？ 当然有，方法如下。

找到 在jar 中 调用的layout 文件 在根目录 下添加如下属性

```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="fill_parent"
            android:layout_height="wrap_content"
            <--最关键的一条属性-->
            tools:ignore="all"
>
</RelativeLayout>
```

最重要的一个属性 **tools:ignore = “all”** 其中 all 也可以是其他的lint check id 

这里的tools:ignore表示忽略警告，不过更建议使用UnUsedResource代替all，UnUsedResource表示未使用资源文件警告。tools:ignore在全部的res资源下均可以使用，添加后不会再被link扫描出来。



