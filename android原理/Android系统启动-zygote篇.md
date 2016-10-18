转自[Android系统启动-zygote篇](http://gityuan.com/2016/02/13/android-zygote/)

> 基于Android 6.0的源码剖析， 分析Android启动过程的Zygote进程

```java
/frameworks/base/cmds/app_process/App_main.cpp （内含AppRuntime类）
/frameworks/base/core/jni/AndroidRuntime.cpp
/frameworks/base/core/java/com/android/internal/os/ZygoteInit.java
/frameworks/base/core/java/com/android/internal/os/Zygote.java
/frameworks/base/core/java/android/net/LocalServerSocket.java
```

## 一、启动调用栈

Zygote启动函数调用类的栈关系：

```java
App_main.main
    AndroidRuntime.start
        startVm
        startReg
        ZygoteInit.main
            registerZygoteSocket
            preload
            startSystemServer
            runSelectLoop
```

## 二、App_main

Zygote是由[init进程](http://gityuan.com/2016/01/16/android-init//#zygote)通过解析init.zygote.rc文件而创建的，zygote所对应的可执行程序app_process，所对应的源文件是App_main.cpp，进程名为zygote。

传到main()的参数为 `-Xzygote /system/bin --zygote --start-system-server`

[–>App_main.cpp]

```java
int main(int argc, char* const argv[])
{
    AppRuntime runtime(argv[0], computeArgBlockSize(argc, argv));

    //忽略第一个参数
    argc--;
    argv++;

    int i;
    for (i = 0; i < argc; i++) {
        if (argv[i][0] != '-') {
            break;
        }
        if (argv[i][1] == '-' && argv[i][2] == 0) {
            ++i;
            break;
        }
        runtime.addOption(strdup(argv[i]));
    }
    //参数解析
    bool zygote = false;
    bool startSystemServer = false;
    bool application = false;
    String8 niceName;
    String8 className;
    ++i;
    while (i < argc) {
        const char* arg = argv[i++];
        if (strcmp(arg, "--zygote") == 0) {
            zygote = true;
            niceName = ZYGOTE_NICE_NAME; //对于64位系统nice_name为zygote64,32位系统为zygote
        } else if (strcmp(arg, "--start-system-server") == 0) {
            startSystemServer = true;
        } else if (strcmp(arg, "--application") == 0) {
            application = true;
        } else if (strncmp(arg, "--nice-name=", 12) == 0) {
            niceName.setTo(arg + 12);
        } else if (strncmp(arg, "--", 2) != 0) {
            className.setTo(arg);
            break;
        } else {
            --i;
            break;
        }
    }
    Vector<String8> args;
    if (!className.isEmpty()) {
        // 运行application或tool程序
        args.add(application ? String8("application") : String8("tool"));
        runtime.setClassNameAndArgs(className, argc - i, argv + i);
    } else {
        //进入zygote模式，创建 /data/dalvik-cache路径
        maybeCreateDalvikCache();
        if (startSystemServer) {
            args.add(String8("start-system-server"));
        }
        char prop[PROP_VALUE_MAX];
        if (property_get(ABI_LIST_PROPERTY, prop, NULL) == 0) {
            return 11;
        }
        String8 abiFlag("--abi-list=");
        abiFlag.append(prop);
        args.add(abiFlag);

        for (; i < argc; ++i) {
            args.add(String8(argv[i]));
        }
    }

    //设置进程名
    if (!niceName.isEmpty()) {
        runtime.setArgv0(niceName.string());
        set_process_name(niceName.string());
    }
    if (zygote) {
        // 启动AppRuntime 【见小节3】
        runtime.start("com.android.internal.os.ZygoteInit", args, zygote);
    } else if (className) {
        runtime.start("com.android.internal.os.RuntimeInit", args, zygote);
    } else {
        //没有指定类名或zygote，参数错误
        return 10;
    }
}
```

采用cmd命令，是通过fork进程来执行相应的类：

```java
 app_process [可选参数] 命令所在路径 启动的类名 [可选参数]
```

## 三、AndroidRuntime

AndroidRuntime.cpp

```java
void AndroidRuntime::start(const char* className, const Vector<String8>& options, bool zygote)
{
    static const String8 startSystemServer("start-system-server");

    for (size_t i = 0; i < options.size(); ++i) {
        if (options[i] == startSystemServer) {
           const int LOG_BOOT_PROGRESS_START = 3000;
        }
    }
    const char* rootDir = getenv("ANDROID_ROOT");
    if (rootDir == NULL) {
        rootDir = "/system";
        if (!hasDir("/system")) {
            return;
        }
        setenv("ANDROID_ROOT", rootDir, 1);
    }
    JniInvocation jni_invocation;
    jni_invocation.Init(NULL);
    JNIEnv* env;
    // 虚拟机创建【见小节3.1】
    if (startVm(&mJavaVM, &env, zygote) != 0) {
        return;
    }
    onVmCreated(env);
    // JNI方法注册【见小节3.2】
    if (startReg(env) < 0) {
        ALOGE("Unable to register all android natives\n");
        return;
    }

    jclass stringClass;
    jobjectArray strArray;
    jstring classNameStr;

    //等价 strArray= new String[options.size() + 1];
    stringClass = env->FindClass("java/lang/String");
    strArray = env->NewObjectArray(options.size() + 1, stringClass, NULL);

    //等价 strArray[0] = "com.android.internal.os.ZygoteInit"
    classNameStr = env->NewStringUTF(className);
    env->SetObjectArrayElement(strArray, 0, classNameStr);

    //等价 strArray[1] = "start-system-server"；
    //   strArray[2] = "--abi-list=xxx"；其中xxx为系统响应的cpu架构类型，比如arm64-v8a.
    for (size_t i = 0; i < options.size(); ++i) {
        jstring optionsStr = env->NewStringUTF(options.itemAt(i).string());
        env->SetObjectArrayElement(strArray, i + 1, optionsStr);
    }

    //将"com.android.internal.os.ZygoteInit"转换为"com/android/internal/os/ZygoteInit"
    char* slashClassName = toSlashClassName(className);
    jclass startClass = env->FindClass(slashClassName);
    if (startClass == NULL) {
        ALOGE("JavaVM unable to locate class '%s'\n", slashClassName);
    } else {
        jmethodID startMeth = env->GetStaticMethodID(startClass, "main",
            "([Ljava/lang/String;)V");
        if (startMeth == NULL) {
            ALOGE("JavaVM unable to find main() in '%s'\n", className);
        } else {
            // 调用ZygoteInit.main()方法【见小节4.0】
            env->CallStaticVoidMethod(startClass, startMeth, strArray);
        }
    }
    free(slashClassName); //释放相应对象的内存空间
    if (mJavaVM->DetachCurrentThread() != JNI_OK)
        ALOGW("Warning: unable to detach main thread\n");
    if (mJavaVM->DestroyJavaVM() != 0)
        ALOGW("Warning: VM did not shut down cleanly\n");
}
```

### 3.1 虚拟机创建startVm

[–>AndroidRuntime.cpp]

创建Java虚拟机方法的主要篇幅是关于虚拟机参数的设置，下面只列举部分在调试优化过程中常用参数。

```java
int AndroidRuntime::startVm(JavaVM** pJavaVM, JNIEnv** pEnv, bool zygote)
{
    // JNI检测功能，用于native层调用jni函数时进行常规检测，比较弱字符串格式是否符合要求，资源是否正确释放。该功能一般用于早期系统调试或手机Eng版，对于User版往往不会开启，引用该功能比较消耗系统CPU资源，降低系统性能。
    bool checkJni = false;
    property_get("dalvik.vm.checkjni", propBuf, "");
    if (strcmp(propBuf, "true") == 0) {
        checkJni = true;
    } else if (strcmp(propBuf, "false") != 0) {
        property_get("ro.kernel.android.checkjni", propBuf, "");
        if (propBuf[0] == '1') {
            checkJni = true;
        }
    }
    if (checkJni) {
        addOption("-Xcheck:jni");
    }

    //虚拟机产生的trace文件，主要用于分析系统问题，路径默认为/data/anr/traces.txt
    parseRuntimeOption("dalvik.vm.stack-trace-file", stackTraceFileBuf, "-Xstacktracefile:");

    //对于不同的软硬件环境，这些参数往往需要调整、优化，从而使系统达到最佳性能
    parseRuntimeOption("dalvik.vm.heapstartsize", heapstartsizeOptsBuf, "-Xms", "4m");
    parseRuntimeOption("dalvik.vm.heapsize", heapsizeOptsBuf, "-Xmx", "16m");
    parseRuntimeOption("dalvik.vm.heapgrowthlimit", heapgrowthlimitOptsBuf, "-XX:HeapGrowthLimit=");
    parseRuntimeOption("dalvik.vm.heapminfree", heapminfreeOptsBuf, "-XX:HeapMinFree=");
    parseRuntimeOption("dalvik.vm.heapmaxfree", heapmaxfreeOptsBuf, "-XX:HeapMaxFree=");
    parseRuntimeOption("dalvik.vm.heaptargetutilization",
                       heaptargetutilizationOptsBuf, "-XX:HeapTargetUtilization=");

    //preloaded-classes文件内容是由WritePreloadedClassFile.java生成的，在ZygoteInit类中会预加载工作将其中的classes提前加载到内存，以提高系统性能
    if (!hasFile("/system/etc/preloaded-classes")) {
        return -1;
    }

    //创建虚拟机
    if (JNI_CreateJavaVM(pJavaVM, pEnv, &initArgs) < 0) {
        ALOGE("JNI_CreateJavaVM failed\n");
        return -1;
    }
}
```

### 3.2 JNI函数注册startReg

[–>AndroidRuntime.cpp]

```java
int AndroidRuntime::startReg(JNIEnv* env)
{
    //设置线程创建方法为javaCreateThreadEtc
    androidSetCreateThreadFunc((android_create_thread_fn) javaCreateThreadEtc);

    env->PushLocalFrame(200);
    //进程NI方法的注册
    if (register_jni_procs(gRegJNI, NELEM(gRegJNI), env) < 0) {
        env->PopLocalFrame(NULL);
        return -1;
    }
    env->PopLocalFrame(NULL);
    return 0;
}
```

**register_jni_procs**

```java
static int register_jni_procs(const RegJNIRec array[], size_t count, JNIEnv* env)
{
    for (size_t i = 0; i < count; i++) {
        if (array[i].mProc(env) < 0) { 【见下文】
            return -1;
        }
    }
    return 0;
}
```

**gRegJNI**

gRegJNI是一个数组,有100多个成员

```java
static const RegJNIRec gRegJNI[] = {
    REG_JNI(register_com_android_internal_os_RuntimeInit),
    REG_JNI(register_android_os_Binder)，
    ...
}；
```

**REG_JNI**

```java
#define REG_JNI(name)      { name }
struct RegJNIRec {
    int (*mProc)(JNIEnv*);
};
```

调用mProc，那么gRegJNI数组的其中之一REG_JNI(register_com_android_internal_os_RuntimeInit)，等价于调用下面方法：

```java
int register_com_android_internal_os_RuntimeInit(JNIEnv* env)
{
    return jniRegisterNativeMethods(env, "com/android/internal/os/RuntimeInit",
        gMethods, NELEM(gMethods));
}

//gMethods：java层方法名与jni层的方法的一一映射关系
static JNINativeMethod gMethods[] = {
    { "nativeFinishInit", "()V",
        (void*) com_android_internal_os_RuntimeInit_nativeFinishInit },
    { "nativeZygoteInit", "()V",
        (void*) com_android_internal_os_RuntimeInit_nativeZygoteInit },
    { "nativeSetExitWithoutCleanup", "(Z)V",
        (void*) com_android_internal_os_RuntimeInit_nativeSetExitWithoutCleanup },
};
```

## 四 ZygoteInit

[–>ZygoteInit.java]

```java
public static void main(String argv[]) {
    try {
        RuntimeInit.enableDdms(); //开启DDMS功能
        SamplingProfilerIntegration.start();
        boolean startSystemServer = false;
        String socketName = "zygote";
        String abiList = null;
        for (int i = 1; i < argv.length; i++) {
            if ("start-system-server".equals(argv[i])) {
                startSystemServer = true;
            } else if (argv[i].startsWith(ABI_LIST_ARG)) {
                abiList = argv[i].substring(ABI_LIST_ARG.length());
            } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
                socketName = argv[i].substring(SOCKET_NAME_ARG.length());
            } else {
                throw new RuntimeException("Unknown command line argument: " + argv[i]);
            }
        }
        if (abiList == null) {
            throw new RuntimeException("No ABI list supplied.");
        }
        registerZygoteSocket(socketName); //为Zygote注册socket【见小节4.1】
        preload(); // 预加载类和资源【见小节4.2】
        SamplingProfilerIntegration.writeZygoteSnapshot();
        gcAndFinalize(); //GC操作
        if (startSystemServer) {
            startSystemServer(abiList, socketName);//启动system_server【见小节4.3】
        }
        runSelectLoop(abiList); //进入循环模式【见小节4.4】
        closeServerSocket();
    } catch (MethodAndArgsCaller caller) {
        caller.run(); //启动system_server中会讲到。
    } catch (RuntimeException ex) {
        closeServerSocket();
        throw ex;
    }
}
```

在异常捕获后调用的方法caller.run()，会在后续的system_server文章会讲到。

### 4.1 registerZygoteSocket

```java
private static void registerZygoteSocket(String socketName) {
    if (sServerSocket == null) {
        int fileDesc;
        final String fullSocketName = ANDROID_SOCKET_PREFIX + socketName;
        try {
            String env = System.getenv(fullSocketName);
            fileDesc = Integer.parseInt(env);
        } catch (RuntimeException ex) {
            throw new RuntimeException(fullSocketName + " unset or invalid", ex);
        }

        try {
            FileDescriptor fd = new FileDescriptor();
            fd.setInt$(fileDesc); //设置文件描述符
            sServerSocket = new LocalServerSocket(fd); //创建Socket的本地服务端
        } catch (IOException ex) {
            throw new RuntimeException("Error binding to local socket '" + fileDesc + "'", ex);
        }
    }
}
```

### 4.2 preload

执行Zygote进程的初始化

```java
static void preload() {
    //预加载位于/system/etc/preloaded-classes文件中的类
    preloadClasses();

    //预加载资源，包含drawable和color资源
    preloadResources();

    //预加载OpenGL
    preloadOpenGL();

    //通过System.loadLibrary()方法，
    //预加载"android","compiler_rt","jnigraphics"这3个共享库
    preloadSharedLibraries();

    //预加载  文本连接符资源
    preloadTextResources();

    //仅用于zygote进程，用于内存共享的进程
    WebViewFactory.prepareWebViewInZygote();
}
```

对于类加载，采用反射机制Class.forName()方法来加载。对于资源加载，主要是 com.android.internal.R.array.preloaded_drawables和com.android.internal.R.array.preloaded_color_state_lists，在应用程序中以com.android.internal.R.xxx开头的资源，便是此时由Zygote加载到内存的。

zygote进程内加载了preload()方法中的所有资源，当需要fork新进程时，采用copy on write技术，如下：

![zygote_fork](http://gityuan.com/images/boot/zygote/zygote_fork.jpg)

### 4.3 startSystemServer

ZygoteInit.java

```java
private static boolean startSystemServer(String abiList, String socketName)
        throws MethodAndArgsCaller, RuntimeException {
    long capabilities = posixCapabilitiesAsBits(
        OsConstants.CAP_BLOCK_SUSPEND,
        OsConstants.CAP_KILL,
        OsConstants.CAP_NET_ADMIN,
        OsConstants.CAP_NET_BIND_SERVICE,
        OsConstants.CAP_NET_BROADCAST,
        OsConstants.CAP_NET_RAW,
        OsConstants.CAP_SYS_MODULE,
        OsConstants.CAP_SYS_NICE,
        OsConstants.CAP_SYS_RESOURCE,
        OsConstants.CAP_SYS_TIME,
        OsConstants.CAP_SYS_TTY_CONFIG
    );
    //参数准备
    String args[] = {
        "--setuid=1000",
        "--setgid=1000",
        "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1021,1032,3001,3002,3003,3006,3007",
        "--capabilities=" + capabilities + "," + capabilities,
        "--nice-name=system_server",
        "--runtime-args",
        "com.android.server.SystemServer",
    };

    ZygoteConnection.Arguments parsedArgs = null;
    int pid;
    try {
        //用于解析参数，生成目标格式
        parsedArgs = new ZygoteConnection.Arguments(args);
        ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
        ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);

        // fork子进程，用于运行system_server
        pid = Zygote.forkSystemServer(
                parsedArgs.uid, parsedArgs.gid,
                parsedArgs.gids,
                parsedArgs.debugFlags,
                null,
                parsedArgs.permittedCapabilities,
                parsedArgs.effectiveCapabilities);
    } catch (IllegalArgumentException ex) {
        throw new RuntimeException(ex);
    }

    //进入子进程system_server
    if (pid == 0) {
        if (hasSecondZygote(abiList)) {
            waitForSecondaryZygote(socketName);
        }
        // 完成system_server进程剩余的工作
        handleSystemServerProcess(parsedArgs);
    }
    return true;
}
```

准备参数并fork新进程，从上面可以看出system server进程参数信息为uid=1000,gid=1000,进程名为sytem_server，从zygote进程fork新进程后，需要关闭zygote原有的socket。另外，对于有两个zygote进程情况，需等待第2个zygote创建完成。更多详情见[Android系统启动-systemServer上篇](http://gityuan.com/2016/02/13/android-zygote/)。

### 4.4 runSelectLoop

```java
private static void runSelectLoop(String abiList) throws MethodAndArgsCaller {
    ArrayList<FileDescriptor> fds = new ArrayList<FileDescriptor>();
    ArrayList<ZygoteConnection> peers = new ArrayList<ZygoteConnection>();
    //sServerSocket是socket通信中的服务端，即zygote进程
    fds.add(sServerSocket.getFileDescriptor());
    peers.add(null);

    while (true) {
        StructPollfd[] pollFds = new StructPollfd[fds.size()];
        for (int i = 0; i < pollFds.length; ++i) {
            pollFds[i] = new StructPollfd();
            pollFds[i].fd = fds.get(i);
            pollFds[i].events = (short) POLLIN;
        }
        try {
            Os.poll(pollFds, -1);
        } catch (ErrnoException ex) {
            throw new RuntimeException("poll failed", ex);
        }
        for (int i = pollFds.length - 1; i >= 0; --i) {
            //采用I/O多路复用机制，当客户端发出连接请求或者数据处理请求时，跳过continue，执行后面的代码
            if ((pollFds[i].revents & POLLIN) == 0) {
                continue;
            }
            if (i == 0) {
                //创建客户端连接
                ZygoteConnection newPeer = acceptCommandPeer(abiList);
                peers.add(newPeer);
                fds.add(newPeer.getFileDesciptor());
            } else {
                //处理客户端数据事务
                boolean done = peers.get(i).runOnce();
                if (done) {
                    peers.remove(i);
                    fds.remove(i);
                }
            }
        }
    }
}
```

Zygote采用高效的I/O多路复用机制，保证在没有客户端连接请求或数据处理时休眠，否则响应客户端的请求。

## 五、总结

Zygote启动过程的调用流程图：

![startup_process](http://gityuan.com/images/boot/zygote/zygote_process.jpg)

1. 解析init.zygote.rc中的参数，创建AppRuntime并调用AppRuntime.start()方法；
2. 调用AndroidRuntime的startVM()方法创建虚拟机，再调用startReg()注册JNI函数；
3. 通过JNI方式调用ZygoteInit.main()，第一次进入Java世界；
4. registerZygoteSocket()建立socket通道，zygote作为通信的服务端，用于响应客户端请求；
5. preload()预加载通用类、drawable和color资源、openGL以及共享库以及WebView，用于提高ap启动效率；
6. zygote完毕大部分工作，接下来再通过startSystemServer()，fork得力帮手system_server进程，也是上层framework的运行载体。
7. zygote功成身退，调用runSelectLoop()，随时待命，当接收到请求创建新进程请求时立即唤醒并执行相应工作。