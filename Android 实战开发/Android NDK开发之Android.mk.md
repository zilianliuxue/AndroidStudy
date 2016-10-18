转自[http://blog.csdn.net/mynameishuangshuai/article/details/52577228](http://blog.csdn.net/mynameishuangshuai/article/details/52577228)

       [Android](http://lib.csdn.net/base/android).mk是一个向Android NDK构建系统描述NDK项目的GUN Makefile片段。它是每一个NDK项目的必备组件，构建系统希望它出现在jni子目录中。

![Android.mk](http://img.blog.csdn.net/20160918192715184)

上图就是Android.mk的基本文件格式，我们来一起学习下Android.mk的编写语法。

## （一）基本使用

（一）Android.mk文件首先必须要指定LOCAL_PATH变量，用于查找源文件。由于一般情况下 
Android.mk和需要编译的源文件在同一目录下，所以定义成如下形式：

```java
LOCAL_PATH:=$(call my-dir)11
```

上面的语句的意思是将LOCAL_PATH变量定义成本文件所在目录路径。

（二）Android.mk中可以定义多个编译模块，每个编译模块都是

```java
以include $(CLEAR_VARS)开始，以include $(BUILD_XXX)结束
```

       CLEAR_VARS由编译系统提供，指定让GNU 
MAKEFILE为你清除除LOCAL_PATH以外的所有LOCAL_XXX变量，清楚它们可以避免冲突，每一个原生组件被称为一个模块。如LOCAL_MODULE，LOCAL_SRC_FILES，LOCAL_SHARED_LIBRARIES，LOCAL_STATIC_LIBRARIES等。
​      ● **LOCAL_MODULE变量用来给这些模块设定一个唯一的名称，例如：**

```java
LOCAL_MODULE ：= hello-jni11
```

**● LOCAL_SRC_FILES变量定义用来建立和组装这个模块的源文件列表，例如：**

```java
LOCAL_SRC_FILES：= hello-jni.c11
```

## （二）常用构建系统变量讲解

### 构建共享库

       为了建立可供主应用程序使用的模块，必须将该模块变成共享库。Android NDK构建系统将BUILD_STATIC_LIBRARY变量设置成build-shared-library.mk文件的保存位置。

```java
include $(BUILD_STATIC_LIBRARY)
```

### 构建多个共享库

       基于不同应用程序的体系结构，一个单独的Android.mk文件可能产生多个共享库模块，为了达到这个目的，需要在Android.mk文档中定义多个模块。例如：

```java
LOCAL_PATH := $(call my-dir)

#模块1

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni1
LOCAL_SRC_FILES := hello-jni1.c

include $(BUILD_SHARED_LIBRARY)

#模块2

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni2
LOCAL_SRC_FILES := hello-jni2.c

include $(BUILD_SHARED_LIBRARY)
```

       在处理完这个Android.mk构建文档之后，Android NDK构建系统会产生libhello-jni1.so和libhello-jni2.so两个共享库。

### 构建静态库

       Android NDK构建系统也支持静态库，静态库可以用来构建共享库，例如，在将第三方代码添加到现有原生项目中，不用直接将第三方源代码包括在原生项目中，而是将第三方代码编译成静态库，然后并入共享库。

```java
LOCAL_PATH := $(call my-dir)

#第三方AVI库
include $(CLEAR_VARS)

LOCAL_MODULE    := avilib
LOCAL_SRC_FILES := avilib.c

include $(BUILD_SHARED_LIBRARY)

#原生模块
include $(CLEAR_VARS)

LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := hello-jni.c

LOCAL_SHARED_LIBRARIES：=avilib
include $(BUILD_SHARED_LIBRARY)
```

       将第三方代码模块生成静态库后，共享库就可以通过将它的模块名称添加到LOCAL_SHARED_LIBRARIES变量中来使用该模块。

### 用公共库共享通用模块

       静态库可以保证源代码模块化，但是，当静态库与共享库相连接时，就变成了共享库的一部分。在多个共享库的情况下，多个共享库与同一个静态库连接时，需要将通用模块的多个副本与不同共享库重复连接，这样就增加了应用程序的大小，在这种情况下，我们不用构建静态库，而是将通用模块作为共享库建立起来，而动态连接依赖模块以便消除重复的副本。

```java
LOCAL_PATH := $(call my-dir)

#第三方AVI库
include $(CLEAR_VARS)

LOCAL_MODULE    := avilib
LOCAL_SRC_FILES := avilib.c
include $(BUILD_SHARED_LIBRARY)

#原生模块1

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni1
LOCAL_SRC_FILES := hello-jni1.c
LOCAL_SHARED_LIBRARIES：=avilib

include $(BUILD_SHARED_LIBRARY)

#原生模块2

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni2
LOCAL_SRC_FILES := hello-jni2.c
LOCAL_SHARED_LIBRARIES：=avilib

include $(BUILD_SHARED_LIBRARY)
```

### 在多个NDK项目间共享模块

       同时使用静态库和共享库时，可以在模块间共享通用模块。但是要注意的是，所有这些模块必须属于同一个NDK项目。 
1.首先将avilib源代码移动到当前NDK项目以外的位置，如：D:\shared-moudles\transcode\avilib 
2.作为共享模块，avilib需要自己的Android.mk文件，具体配置如下：

```
LOCAL_PATH := $(call my-dir)

#第三方AVI库
include $(CLEAR_VARS)

LOCAL_MODULE    := avilib
LOCAL_SRC_FILES := avilib.c
include $(BUILD_SHARED_LIBRARY)1234567812345678
```

3.现在可以将avilib模块从当前NDK项目的Android.mk文件中移除。通常，为了避免冲突，我们将以transcode/avilib为参数调用函数宏import-module不分添加在构建文档的末尾。

```
#原生模块
include $(CLEAR_VARS)

LOCAL_MODULE    := hello-jni1
LOCAL_SRC_FILES := hello-jni1.c
LOCAL_SHARED_LIBRARIES：=avilib

include $(BUILD_SHARED_LIBRARY)

$(call import-module,transcode/avilib)1234567891012345678910
```

4.import-module函数宏需要先定位共享模块，然后将它导入到NDK项目中。默认情况下，import-module函数宏只搜索/sources目录。为了搜索D:\shared-moudles目录，定义一个名为NDK_MODULE_PATH的新环境变量并将它设置成共享模块的根目录，例如D:\shared-moudles。

### 用Prebuilt库

prebulit库有两大作用： 
1.想在不发布源代码的情况下将你的模块发布给他人 
2.想使用共享模块的预建版来加速构建过程

```
LOCAL_PATH := $(call my-dir)

#第三方AVI库
include $(CLEAR_VARS)

LOCAL_MODULE    := avilib
LOCAL_SRC_FILES := libavilib.so
include $(PREBUILT_SHARED_LIBRARY)1234567812345678
```

       其中，LOCAL_SRC_FILES 变量指向的不是源文件，而是实际Prebuild库相对于LOCAL_PATH的位置。

### 构建独立的可执行文件

       有时候为了方便快速测试和原型设计，我们可能会需要Android NDK构建独立的可执行文件，它们不用打包成APK文件既可以赋值到android设备上的常规[Linux](http://lib.csdn.net/base/linux)应用程序，而且它们可以直接执行，而不通过[Java](http://lib.csdn.net/base/javaee)应用程序加载。生成独立的可以执行文件需要再Android.mk构建文档中导入BUILD_EXECUTABLE变量。

```java
#独立可执行的原生模块
include $(CLEAR_VARS)

LOCAL_MODULE    := moudles
LOCAL_SRC_FILES := moudles.c
LOCAL_SHARED_LIBRARIES：=avilib

include $(BUILD_EXECUTABLE)
```

       独立的可执行文件以与模块相同的名称放在libs/目录下。

### 构建系统宏函数

**my-dir:**返回当前 Android.mk 所在的目录的路径，相对于 NDK 编译系统的顶层。这是有用的，在 Android.mk 文件的开头如此定义： 

```java
    LOCAL_PATH := $(call my-dir) 
```

**all-subdir-makefiles:** 返回一个位于当前’my-dir’路径的子目录中的所有Android.mk的列表。  
​       例如，某一子项目的目录层次如下： 

```java
     src/foo/Android.mk 
     src/foo/lib1/Android.mk 
     src/foo/lib2/Android.mk 
```

```java
  如果 src/foo/Android.mk 包含一行： 
       include $(call all-subdir-makefiles) 
  那么它就会自动包含 src/foo/lib1/Android.mk 和 src/foo/lib2/Android.mk。 
  这项功能用于向编译系统提供深层次嵌套的代码目录层次。 
  注意，在默认情况下，NDK 将会只搜索在 src/*/Android.mk 中的文件。 
```

**this-makefile:**  返回当前Makefile 的路径(即这个函数调用的地方)  
   **parent-makefile:**  返回调用树中父 Makefile 路径。即包含当前Makefile的Makefile 路径。  
   **grand-parent-makefile:**返回调用树中父Makefile的父Makefile的路径 

### 定义新变量

       开发人员可以定义其他新变量来简化他们的构建文件。以LOCAL_和NDK_前缀开头的名称预留给Android NDK构建系统使用，建议大家使用MY_开头。例如： 
MY_SRC_FILIES:=avilib.c

### 条件操作

       Android.mk构建文件可以包含某些关于这些变量的条件操作，例如，在某个体系结构中包含一个不同的源文件集：

```java
……
ifeq($(TARGET_ARCH),arm)
LOCAL_SRC_FILES += armonly.c
else
LOCAL_SRC_FILES += generic.c
endif
```

### 其他构建系统变量列表

**TARGET_ARCH:** 目标 CPU平台的名字  
**TARGET_PLATFORM:** Android.mk 解析的时候，目标 Android 平台的名字

```java
       android-3 -> Official Android 1.5 system images 
       android-4 -> Official Android 1.6 system images 
       android-5 -> Official Android 2.0 system images
```

**TARGET_ARCH_ABI:**  暂时只支持两个 value，armeabi 和 armeabi-v7a。。  
**TARGET_ABI:** 目标平台和 ABI 的组合，  
**LOCAL_PATH:**  这个变量用于给出当前文件的路径。  
​       必须在 Android.mk 的开头定义，可以这样使用：

```java
    LOCAL_PATH := $(call my-dir) 
```

```java
   如当前目录下有个文件夹名称 src，则可以这样写 $(call src)，那么就会得到 src 目录的完整路径 
   这个变量不会被$(CLEAR_VARS)清除，因此每个 Android.mk 只需要定义一次(即使在一个文件中定义了几个模块的情况下)。 
```

**LOCAL_MODULE:** 这是模块的名字，它必须是唯一的，而且不能包含空格。  
​       必须在包含任一的$(BUILD_XXXX)脚本之前定义它。模块的名字决定了生成文件的名字。  
**LOCAL_SRC_FILES:**  这是要编译的源代码文件列表。  
​       只要列出要传递给编译器的文件，因为编译系统自动计算依赖。注意源代码文件名称都是相对于 LOCAL_PATH的，你可以使用路径部分，例如： 

```java
        LOCAL_SRC_FILES := foo.c toto/bar.c\ 
        Hello.c 
   文件之间可以用空格或Tab键进行分割,换行请用"\" 
   如果是追加源代码文件的话，请用LOCAL_SRC_FILES += 
   注意：可以LOCAL_SRC_FILES := $(call all-subdir-java-files)这种形式来包含local_path目录下的所有java文件。 
```

**LOCAL_C_INCLUDES:**  可选变量，表示头文件的搜索路径。  
​        默认的头文件的搜索路径是LOCAL_PATH目录。  
**LOCAL_STATIC_LIBRARIES:** 表示该模块需要使用哪些静态库，以便在编译时进行链接。  
**LOCAL_SHARED_LIBRARIES:**  表示模块在运行时要依赖的共享库（动态库），在链接时就需要，以便在生成文件时嵌入其相应的信息。  
​       注意：它不会附加列出的模块到编译图，也就是仍然需要在Application.mk 中把它们添加到程序要求的模块中。  
**LOCAL_LDLIBS:**  编译模块时要使用的附加的链接器选项。这对于使用‘-l’前缀传递指定库的名字是有用的。  
​       例如，LOCAL_LDLIBS := -lz表示告诉链接器生成的模块要在加载时刻链接到/system/lib/libz.so  
​       可查看 docs/STABLE-APIS.TXT 获取使用 NDK发行版能链接到的开放的系统库列表。  
**LOCAL_MODULE_PATH 和 LOCAL_UNSTRIPPED_PATH:** 
​       在 Android.mk 文件中， 还可以用LOCAL_MODULE_PATH 和LOCAL_UNSTRIPPED_PATH指定最后的目标安装路径.  
​       不同的文件系统路径用以下的宏进行选择：  
​       TARGET_ROOT_OUT：表示根文件系统。  
​       TARGET_OUT：表示 system文件系统。  
​       TARGET_OUT_DATA：表示 data文件系统。  
​       用法如：LOCAL_MODULE_PATH :=$(TARGET_ROOT_OUT)  
​       至于LOCAL_MODULE_PATH 和LOCAL_UNSTRIPPED_PATH的区别，暂时还不清楚。  
**LOCAL_JNI_SHARED_LIBRARIES：**定义了要包含的so库文件的名字，如果程序没有采用jni，不需要  
​        LOCAL_JNI_SHARED_LIBRARIES := libxxx 这样在编译的时候，NDK自动会把这个libxxx打包进apk； 放在youapk/lib/目录下 