TextInputLayout是用来增强EditText的，使用的时候也是在EditText包裹一层布局，如：

```
<android.support.design.widget.TextInputLayout
    android:id="@+id/til_username"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="10dp">

    <EditText
        android:id="@+id/et_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="UserName"/>
</android.support.design.widget.TextInputLayout>123456789101112
```

#### 1>主要功能特色：

①当里面的EditText获取焦点后，EditText的hint文字，会移动到EditText的上面做Label，提示用户。。

②具有错误提示机制，当用户输入的内容不符合要求时，可以提示用户，以前我们都是用toast的方式，体验很差。在布局中设置app:errorEnabled=”true” 在代码中调用textInputLayout.setError(tip)方法，就可以在EditText的下方显示错误内容。

③具有字数统计功能，很多情况下输入文字都有文字字数限制，比如输入反馈、描述等。以前我们都是自己去实现，现在好了，有系统控件帮我们实现了。通过下面的配置启用该功能

```
app:counterEnabled="true"
app:counterMaxLength="50"
app:errorEnabled="true"123
```

#### 2> 具体的使用详解

由于篇幅的原因，下面的讲解，我就不每个属性值都去改下，然后贴出图片，这样也没有任何必要，希望需要的同学，自己去试试。

> 纸上得来终觉浅，绝知此事要躬行

①如何更改EditText的下方的横线的颜色。如下图所示：

![这里写图片描述](http://img.blog.csdn.net/20160715152318883)

这个颜色的控制是在样式文件里设置的，通过

```
<item name="colorAccent">@color/colorAccent</item>1
```

② 如何更改获取焦点后，上面Label的颜色/大小等。如下图所示：

![这里写图片描述](http://img.blog.csdn.net/20160715152451555)

这个颜色大小等属性修改通过

```java
app:hintTextAppearance="@style/HintAppearance"
```

本工程里的样式是这样的：

```java
<style name="HintAppearance" parent="TextAppearance.AppCompat">
    <item name="android:textSize">14sp</item>
    <item name="android:textColor">#8bc34a</item>
</style>
```

③如何修改错误提示的颜色，如下图所示：

![这里写图片描述](http://img.blog.csdn.net/20160715152359352)

错误的样式通过如下方式修改：

```java
app:errorTextAppearance="@style/ErrorAppearance"1
```

```java
<style name="ErrorAppearance" parent="TextAppearance.AppCompat">
    <item name="android:textSize">14sp</item>
    <item name="android:textColor">#a2ced1</item>
</style>
12345
```

需要注意的是，该属性不止改变的是错误文字的颜色、大小，还修改了EditText的的那条横线的颜色。

如果不想在编写TextInputLayout布局的时候都去设置，还可以通过style文件去统一设置，如：

```java
<item name="textColorError">@color/textColorError</item>
```

当然，如果你设置了errorTextAppearance同时又设置了textColorError，TextInputLayout会优先使用errorTextAppearance配置的颜色。

④ 字数统计功能，修改错误的有颜色和上面是一样。如何修改`统计字数`的样式。如下图：

![这里写图片描述](http://img.blog.csdn.net/20160715152549871)

这里分两种情况，一种是没有超过的情况，另一种是超过字数的情况。

```java
//没有超过最大字数
app:counterTextAppearance="@style/CounterAppearance"
//超过最大字数
app:counterOverflowTextAppearance="@style/CounterOverflowAppearance"
```

#### 使用TextInputLayout遇到的一些坑：

① 如果加上字数统计需要在style里加上textErrorColor，否则超过字数会后会闪退。

② 如果不需要字数统计，且启用错误机制（setErrorEnabled(true)）, 不需要加上textErrorColor（不会闪退）系统会提供一个默认的error color。 
当然可以通过textErrorColor来自定义错误颜色(error color). 
可以使用更为强大的errorTextAppearance来定义错误颜色，字体大小等属性。如果TextInputLayout 同时 设置了textErrorColor和errorTextAppearance ，只有errorTextAppearance生效.

③ 如果加上字数统计，且同时设置了textErrorColor和errorTextAppearance。 
这个时候回出现奇怪的效果，`Label`和`统计`的颜色为textErrorColor的颜色。 
EditText的`横线` 和 `错误文字提示`为errorTextAppearance设置的效果。所以为什么不加上textErrorColor会闪退，因为超过字数后TextInputLayout需要textErrorColor属性设置的颜色。