转自[Java中System.loadLibrary() 的执行过程](https://my.oschina.net/wolfcs/blog/129696)

 *System.loadLibrary()*是我们在使用Java的JNI机制时，会用到的一个非常重要的函数，它的作用即是把实现了我们在Java code中声明的native方法的那个libraryload进来，或者load其他什么动态连接库。 

 算是处于好奇吧，我们可以看一下这个方法它的实现，即执行流程。(下面分析的那些code，来自于android 4.2.2 aosp版。)先看一下这个方法的code(在libcore/luni/src/main/java/java/lang/System.java这个文件中)： 

```java
/**
     * Loads and links the library with the specified name. The mapping of the
     * specified library name to the full path for loading the library is
     * implementation-dependent.
     *
     * @param libName
     *            the name of the library to load.
     * @throws UnsatisfiedLinkError
     *             if the library could not be loaded.
     */
    public static void loadLibrary(String libName) {
        Runtime.getRuntime().loadLibrary(libName, VMStack.getCallingClassLoader());
    }
```

 由上面的那段code，可以看到，它的实现非常简单，就只是先调用*VMStack.getCallingClassLoader()*获取到ClassLoader，然后再把实际要做的事情委托给了Runtime来做而已。接下来我们再看一下*Runtime.loadLibrary()*的实现(在libcore/luni/src/main/java/java/lang/Runtime.java这个文件中): 

```java
/*
     * Loads and links a library without security checks.
     */
    void loadLibrary(String libraryName, ClassLoader loader) {
        if (loader != null) {
            String filename = loader.findLibrary(libraryName);
            if (filename == null) {
                throw new UnsatisfiedLinkError("Couldn't load " + libraryName
                                               + " from loader " + loader
                                               + ": findLibrary returned null");
            }
            String error = nativeLoad(filename, loader);
            if (error != null) {
                throw new UnsatisfiedLinkError(error);
            }
            return;
        }

        String filename = System.mapLibraryName(libraryName);
        List<String> candidates = new ArrayList<String>();
        String lastError = null;
        for (String directory : mLibPaths) {
            String candidate = directory + filename;
            candidates.add(candidate);
            if (new File(candidate).exists()) {
                String error = nativeLoad(candidate, loader);
                if (error == null) {
                    return; // We successfully loaded the library. Job done.
                }
                lastError = error;
            }
        }

        if (lastError != null) {
            throw new UnsatisfiedLinkError(lastError);
        }
        throw new UnsatisfiedLinkError("Library " + libraryName + " not found; tried " + candidates);
    }
```

 由上面的那段code，我们看到，*loadLibrary()*可以被看作是一个2步走的过程： 

1.  获取到library path。对于这一点，上面的那个函数，依据于所传递的ClassLoader的不同，会有两种不同的方法。如果ClassLoader非空，则会利用ClassLoader的*******findLibrary()*方法来获取library的path。而如果ClassLoader为空，则会首先依据传递进来的library name，获取到library file的name，比如传递“hello”进来，它的library file name，经过*System.mapLibraryName(libraryName)*将会是“libhello.so”；然后再在一个path list(即上面那段code中的*mLibPaths*)中查找到这个library file，并最终确定library 的path。 
2.  调用nativeLoad()这个native方法来load library 

 这段code，又牵出几个问题，首先，可用的library path都是哪些，这实际上也决定了，我们的so文件放在哪些folder下，才可以被真正load起来？其次，在native层load library的过程，又实际做了什么事情？下面会对这两个问题，一一的作出解答。 

##  **系统的library path** 

 我们由简单到复杂的来看这个问题。先来看一下，在传入的ClassLoader为空的情况(尽管我们知道，在*System.loadLibrary()*这个case下不会发生)，前面*Runtime.loadLibrary()*的实现中那个*mLibPaths*的初始化的过程，在Runtime的构造函数中，如下： 

   

```java
/**
     * Prevent this class from being instantiated.
     */
    private Runtime(){
        String pathList = System.getProperty("java.library.path", ".");
        String pathSep = System.getProperty("path.separator", ":");
        String fileSep = System.getProperty("file.separator", "/");

        mLibPaths = pathList.split(pathSep);

        // Add a '/' to the end so we don't have to do the property lookup
        // and concatenation later.
        for (int i = 0; i < mLibPaths.length; i++) {
            if (!mLibPaths[i].endsWith(fileSep)) {
                mLibPaths[i] += fileSep;
            }
        }
    }
```

 可以看到，那个library path list实际上读取自一个system property。那在android系统中，这个system property的实际内容又是什么呢？dump这些内容出来，就像下面这样： 

```java
05-11 07:51:40.974: V/QRCodeActivity(11081): pathList = /vendor/lib:/system/lib
05-11 07:51:40.974: V/QRCodeActivity(11081): pathSep = :
05-11 07:51:40.974: V/QRCodeActivity(11081): fileSep = /
```

 然后是传入的ClassLoader非空的情况，ClassLoader的*******findLibrary()*方法的执行过程。首先看一下它的实现(在libcore/luni/src/main/java/java/lang/ClassLoader.java这个文件中)： 

```java
/**
     * Returns the absolute path of the native library with the specified name,
     * or {@code null}. If this method returns {@code null} then the virtual
     * machine searches the directories specified by the system property
     * "java.library.path".
     * <p>
     * This implementation always returns {@code null}.
     * </p>
     *
     * @param libName
     *            the name of the library to find.
     * @return the absolute path of the library.
     */
    protected String findLibrary(String libName) {
        return null;
    }
```

 竟然是一个空函数。那系统中实际运行的ClassLoader就是这个吗？我们可以做一个小小的实验，打印系统中实际运行的ClassLoader的String： 

   

```java
ClassLoader classLoader = getClassLoader();
        Log.v(TAG, "classLoader = " + classLoader.toString());
```

在Galaxy Nexus上执行的结果如下：

   

```java
05-11 08:18:57.857: V/QRCodeActivity(11556): classLoader = dalvik.system.PathClassLoader[dexPath=/data/app/com.qrcode.qrcode-1.apk,libraryPath=/data/app-lib/com.qrcode.qrcode-1]
```

看到了吧，android系统中的

ClassLoader真正的实现

在dalvik的*dalvik.system.PathClassLoader*。打开libcore/dalvik/src/main/java/dalvik/system/PathClassLoader.java来看

PathClassLoader这个class

的实现，可以看到，就只是简单的继承

BaseDexClassLoader而已，没有任何实际的内容

。接下来我们就来看一下

BaseDexClassLoader中

那个

*findLibrary()*

真正的实现(

在libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java这个文件中

)：

```java
@Override
    public String findLibrary(String name) {
        return pathList.findLibrary(name);
    }
```

 这个方法看上去倒挺简单，不用多做解释。然后来看那个*pathList*的初始化的过程，在BaseDexClassLoader的构造函数里： 

```java
/**
     * Constructs an instance.
     *
     * @param dexPath the list of jar/apk files containing classes and
     * resources, delimited by {@code File.pathSeparator}, which
     * defaults to {@code ":"} on Android
     * @param optimizedDirectory directory where optimized dex files
     * should be written; may be {@code null}
     * @param libraryPath the list of directories containing native
     * libraries, delimited by {@code File.pathSeparator}; may be
     * {@code null}
     * @param parent the parent class loader
     */
    public BaseDexClassLoader(String dexPath, File optimizedDirectory,
            String libraryPath, ClassLoader parent) {
        super(parent);

        this.originalPath = dexPath;
        this.originalLibraryPath = libraryPath;
        this.pathList =
            new DexPathList(this, dexPath, libraryPath, optimizedDirectory);
    }
```

 BaseDexClassLoader的构造函数也不用多做解释吧。然后是DexPathList的构造函数： 

```java
/**
     * Constructs an instance.
     *
     * @param definingContext the context in which any as-yet unresolved
     * classes should be defined
     * @param dexPath list of dex/resource path elements, separated by
     * {@code File.pathSeparator}
     * @param libraryPath list of native library directory path elements,
     * separated by {@code File.pathSeparator}
     * @param optimizedDirectory directory where optimized {@code .dex} files
     * should be found and written to, or {@code null} to use the default
     * system directory for same
     */
    public DexPathList(ClassLoader definingContext, String dexPath,
            String libraryPath, File optimizedDirectory) {
        if (definingContext == null) {
            throw new NullPointerException("definingContext == null");
        }

        if (dexPath == null) {
            throw new NullPointerException("dexPath == null");
        }

        if (optimizedDirectory != null) {
            if (!optimizedDirectory.exists())  {
                throw new IllegalArgumentException(
                        "optimizedDirectory doesn't exist: "
                        + optimizedDirectory);
            }

            if (!(optimizedDirectory.canRead()
                            && optimizedDirectory.canWrite())) {
                throw new IllegalArgumentException(
                        "optimizedDirectory not readable/writable: "
                        + optimizedDirectory);
            }
        }

        this.definingContext = definingContext;
        this.dexElements =
            makeDexElements(splitDexPath(dexPath), optimizedDirectory);
        this.nativeLibraryDirectories = splitLibraryPath(libraryPath);
    }
```

 关于我们的library path的问题，可以只关注最后的那个splitLibraryPath()，这个地方，实际上即是把传进来的libraryPath 又丢给splitLibraryPath来获取library path 的list。可以看一下DexPathList.splitLibraryPath()的实现: 

```java
/**
     * Splits the given library directory path string into elements
     * using the path separator ({@code File.pathSeparator}, which
     * defaults to {@code ":"} on Android, appending on the elements
     * from the system library path, and pruning out any elements that
     * do not refer to existing and readable directories.
     */
    private static File[] splitLibraryPath(String path) {
        /*
         * Native libraries may exist in both the system and
         * application library paths, and we use this search order:
         *
         *   1. this class loader's library path for application
         *      libraries
         *   2. the VM's library path from the system
         *      property for system libraries
         *
         * This order was reversed prior to Gingerbread; see http://b/2933456.
         */
        ArrayList<File> result = splitPaths(
                path, System.getProperty("java.library.path", "."), true);
        return result.toArray(new File[result.size()]);
    }
```

 这个地方，是在用两个部分的library path list来由splitPaths构造最终的那个path list，一个部分是，传进来的library path，另外一个部分是，像我们前面看到的那个，是system property。然后再来看一下DexPathList.splitPaths()的实现: 

```java
/**
     * Splits the given path strings into file elements using the path
     * separator, combining the results and filtering out elements
     * that don't exist, aren't readable, or aren't either a regular
     * file or a directory (as specified). Either string may be empty
     * or {@code null}, in which case it is ignored. If both strings
     * are empty or {@code null}, or all elements get pruned out, then
     * this returns a zero-element list.
     */
    private static ArrayList<File> splitPaths(String path1, String path2,
            boolean wantDirectories) {
        ArrayList<File> result = new ArrayList<File>();

        splitAndAdd(path1, wantDirectories, result);
        splitAndAdd(path2, wantDirectories, result);
        return result;
    }
```

 总结一下，ClassLoader的那个findLibrary()实际上会在两个部分的folder中去寻找System.loadLibrary()要load的那个library，一个部分是，构造ClassLoader时，传进来的那个library path，即是app folder，另外一个部分是system property。在android系统中，查找要load的library，实际上会在如下3个folder中进行： 

1.  /vendor/lib 
2.  /system/lib 
3.  /data/app-lib/com.qrcode.qrcode-1 

  

 上面第3个item只是一个例子，每一个app，它的那个app library path的最后一个部分都会是特定于那个app的。至于说，构造BaseDexClassLoader时的那个libraryPath 到底是怎么来的，那可能就会牵扯到android本身更复杂的一些过程了，在此不再做更详细的说明。 

##  Native 层load library的过程 

 然后来看一下native层，把so文件load起的过程，先来一下nativeLoad()这个函数的实现(在JellyBean/dalvik/vm/native/java_lang_Runtime.cpp这个文件中)： 

```java
/*
 * static String nativeLoad(String filename, ClassLoader loader)
 *
 * Load the specified full path as a dynamic library filled with
 * JNI-compatible methods. Returns null on success, or a failure
 * message on failure.
 */
static void Dalvik_java_lang_Runtime_nativeLoad(const u4* args,
    JValue* pResult)
{
    StringObject* fileNameObj = (StringObject*) args[0];
    Object* classLoader = (Object*) args[1];
    char* fileName = NULL;
    StringObject* result = NULL;
    char* reason = NULL;
    bool success;

    assert(fileNameObj != NULL);
    fileName = dvmCreateCstrFromString(fileNameObj);

    success = dvmLoadNativeCode(fileName, classLoader, &reason);
    if (!success) {
        const char* msg = (reason != NULL) ? reason : "unknown failure";
        result = dvmCreateStringFromCstr(msg);
        dvmReleaseTrackedAlloc((Object*) result, NULL);
    }

    free(reason);
    free(fileName);
    RETURN_PTR(result);
}
```

 可以看到，*nativeLoad()*实际上只是完成了两件事情，第一，是调用*dvmCreateCstrFromString()*将Java 的library path String 转换到native的String，然后将这个path传给*dvmLoadNativeCode()*做load，*dvmLoadNativeCode()*这个函数的实现在dalvik/vm/Native.cpp中，如下： 

```java
/*
 * Load native code from the specified absolute pathname.  Per the spec,
 * if we've already loaded a library with the specified pathname, we
 * return without doing anything.
 *
 * TODO? for better results we should absolutify the pathname.  For fully
 * correct results we should stat to get the inode and compare that.  The
 * existing implementation is fine so long as everybody is using
 * System.loadLibrary.
 *
 * The library will be associated with the specified class loader.  The JNI
 * spec says we can't load the same library into more than one class loader.
 *
 * Returns "true" on success. On failure, sets *detail to a
 * human-readable description of the error or NULL if no detail is
 * available; ownership of the string is transferred to the caller.
 */
bool dvmLoadNativeCode(const char* pathName, Object* classLoader,
        char** detail)
{
    SharedLib* pEntry;
    void* handle;
    bool verbose;

    /* reduce noise by not chattering about system libraries */
    verbose = !!strncmp(pathName, "/system", sizeof("/system")-1);
    verbose = verbose && !!strncmp(pathName, "/vendor", sizeof("/vendor")-1);

    if (verbose)
        ALOGD("Trying to load lib %s %p", pathName, classLoader);

    *detail = NULL;

    /*
     * See if we've already loaded it.  If we have, and the class loader
     * matches, return successfully without doing anything.
     */
    pEntry = findSharedLibEntry(pathName);
    if (pEntry != NULL) {
        if (pEntry->classLoader != classLoader) {
            ALOGW("Shared lib '%s' already opened by CL %p; can't open in %p",
                pathName, pEntry->classLoader, classLoader);
            return false;
        }
        if (verbose) {
            ALOGD("Shared lib '%s' already loaded in same CL %p",
                pathName, classLoader);
        }
        if (!checkOnLoadResult(pEntry))
            return false;
        return true;
    }

    /*
     * Open the shared library.  Because we're using a full path, the system
     * doesn't have to search through LD_LIBRARY_PATH.  (It may do so to
     * resolve this library's dependencies though.)
     *
     * Failures here are expected when java.library.path has several entries
     * and we have to hunt for the lib.
     *
     * The current version of the dynamic linker prints detailed information
     * about dlopen() failures.  Some things to check if the message is
     * cryptic:
     *   - make sure the library exists on the device
     *   - verify that the right path is being opened (the debug log message
     *     above can help with that)
     *   - check to see if the library is valid (e.g. not zero bytes long)
     *   - check config/prelink-linux-arm.map to ensure that the library
     *     is listed and is not being overrun by the previous entry (if
     *     loading suddenly stops working on a prelinked library, this is
     *     a good one to check)
     *   - write a trivial app that calls sleep() then dlopen(), attach
     *     to it with "strace -p <pid>" while it sleeps, and watch for
     *     attempts to open nonexistent dependent shared libs
     *
     * This can execute slowly for a large library on a busy system, so we
     * want to switch from RUNNING to VMWAIT while it executes.  This allows
     * the GC to ignore us.
     */
    Thread* self = dvmThreadSelf();
    ThreadStatus oldStatus = dvmChangeStatus(self, THREAD_VMWAIT);
    handle = dlopen(pathName, RTLD_LAZY);
    dvmChangeStatus(self, oldStatus);

    if (handle == NULL) {
        *detail = strdup(dlerror());
        ALOGE("dlopen(\"%s\") failed: %s", pathName, *detail);
        return false;
    }

    /* create a new entry */
    SharedLib* pNewEntry;
    pNewEntry = (SharedLib*) calloc(1, sizeof(SharedLib));
    pNewEntry->pathName = strdup(pathName);
    pNewEntry->handle = handle;
    pNewEntry->classLoader = classLoader;
    dvmInitMutex(&pNewEntry->onLoadLock);
    pthread_cond_init(&pNewEntry->onLoadCond, NULL);
    pNewEntry->onLoadThreadId = self->threadId;

    /* try to add it to the list */
    SharedLib* pActualEntry = addSharedLibEntry(pNewEntry);

    if (pNewEntry != pActualEntry) {
        ALOGI("WOW: we lost a race to add a shared lib (%s CL=%p)",
            pathName, classLoader);
        freeSharedLibEntry(pNewEntry);
        return checkOnLoadResult(pActualEntry);
    } else {
        if (verbose)
            ALOGD("Added shared lib %s %p", pathName, classLoader);

        bool result = true;
        void* vonLoad;
        int version;

        vonLoad = dlsym(handle, "JNI_OnLoad");
        if (vonLoad == NULL) {
            ALOGD("No JNI_OnLoad found in %s %p, skipping init",
                pathName, classLoader);
        } else {
            /*
             * Call JNI_OnLoad.  We have to override the current class
             * loader, which will always be "null" since the stuff at the
             * top of the stack is around Runtime.loadLibrary().  (See
             * the comments in the JNI FindClass function.)
             */
            OnLoadFunc func = (OnLoadFunc)vonLoad;
            Object* prevOverride = self->classLoaderOverride;

            self->classLoaderOverride = classLoader;
            oldStatus = dvmChangeStatus(self, THREAD_NATIVE);
            if (gDvm.verboseJni) {
                ALOGI("[Calling JNI_OnLoad for \"%s\"]", pathName);
            }
            version = (*func)(gDvmJni.jniVm, NULL);
            dvmChangeStatus(self, oldStatus);
            self->classLoaderOverride = prevOverride;

            if (version != JNI_VERSION_1_2 && version != JNI_VERSION_1_4 &&
                version != JNI_VERSION_1_6)
            {
                ALOGW("JNI_OnLoad returned bad version (%d) in %s %p",
                    version, pathName, classLoader);
                /*
                 * It's unwise to call dlclose() here, but we can mark it
                 * as bad and ensure that future load attempts will fail.
                 *
                 * We don't know how far JNI_OnLoad got, so there could
                 * be some partially-initialized stuff accessible through
                 * newly-registered native method calls.  We could try to
                 * unregister them, but that doesn't seem worthwhile.
                 */
                result = false;
            } else {
                if (gDvm.verboseJni) {
                    ALOGI("[Returned from JNI_OnLoad for \"%s\"]", pathName);
                }
            }
        }

        if (result)
            pNewEntry->onLoadResult = kOnLoadOkay;
        else
            pNewEntry->onLoadResult = kOnLoadFailed;

        pNewEntry->onLoadThreadId = 0;

        /*
         * Broadcast a wakeup to anybody sleeping on the condition variable.
         */
        dvmLockMutex(&pNewEntry->onLoadLock);
        pthread_cond_broadcast(&pNewEntry->onLoadCond);
        dvmUnlockMutex(&pNewEntry->onLoadLock);
        return result;
    }
}
```

 哇塞，*dvmLoadNativeCode()*这个函数还真的是有点复杂，那就挑那些跟我们的JNI比较紧密相关的逻辑来看吧。可以认为这个函数做了下面的这样一些事情： 

1.  调用*dlopen() *打开一个so文件，创建一个handle。 
2.  调用*dlsym()*函数，查找到so文件中的*JNI_OnLoad()*这个函数的函数指针。 
3.  执行上一步找到的那个*JNI_OnLoad()*函数。 

 至此，大体可以结束*System.loadLibrary()*的执行过程的分析。 