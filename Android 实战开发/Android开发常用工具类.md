# Android开发常用工具类



> Android开发过程中，我们需要的很多代码都是重复多次使用的，写成工具类是一个比较好的做法，下面是我常用的几个工具类，也希望对你有所帮助。

### 身份证校验

```
package com.zy.utils;
import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.GregorianCalendar;  
import java.util.Hashtable;  
import java.util.Scanner;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  

/** 
 * 身份证号码的格式：610821-20061222-612-X 
 * 由18位数字组成：前6位为地址码，第7至14位为出生日期码，第15至17位为顺序码， 
 * 第18位为校验码。检验码分别是0-10共11个数字，当检验码为“10”时，为了保证公民身份证号码18位，所以用“X”表示。虽然校验码为“X”不能更换，但若需全用数字表示，只需将18位公民身份号码转换成15位居民身份证号码，去掉第7至8位和最后1位3个数码。  
 * 当今的身份证号码有15位和18位之分。1985年我国实行居民身份证制度，当时签发的身份证号码是15位的，1999年签发的身份证由于年份的扩展（由两位变为四位）和末尾加了效验码，就成了18位。 
 * （1）前1、2位数字表示：所在省份的代码；  
 * （2）第3、4位数字表示：所在城市的代码； 
 * （3）第5、6位数字表示：所在区县的代码； 
 * （4）第7~14位数字表示：出生年、月、日； 
 * （5）第15、16位数字表示：所在地的派出所的代码；  
 * （6）第17位数字表示性别：奇数表示男性，偶数表示女性 
 * （7）第18位数字是校检码：根据一定算法生成 
 * 
 */ 
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CheckIdCardUtil {  


    public static String IDCardValidate(String IDStr) throws ParseException {          
        String tipInfo = "该身份证有效！";// 记录错误信息  
        String Ai = "";  
        // 判断号码的长度 15位或18位  
        if (IDStr.length() != 15 && IDStr.length() != 18) {  
            tipInfo = "身份证号码长度应该为15位或18位。";  
            return tipInfo;  
        }  


        // 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字  
        if (IDStr.length() == 18) {  
            Ai = IDStr.substring(0, 17);  
        } else if (IDStr.length() == 15) {  
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);  
        }  
        if (isNumeric(Ai) == false) {  
            tipInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";  
            return tipInfo;  
        }  


        // 判断出生年月是否有效   
        String strYear = Ai.substring(6, 10);// 年份  
        String strMonth = Ai.substring(10, 12);// 月份  
        String strDay = Ai.substring(12, 14);// 日期  
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {  
            tipInfo = "身份证出生日期无效。";  
            return tipInfo;  
        }  
        GregorianCalendar gc = new GregorianCalendar();  
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
        try {  
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 
                    || (gc.getTime().getTime() - s.parse(  
                            strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {  
                tipInfo = "身份证生日不在有效范围。";  
                return tipInfo;  
            }  
        } catch (NumberFormatException e) {  
            e.printStackTrace();  
        } catch (java.text.ParseException e) {  
            e.printStackTrace();  
        }  
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {  
            tipInfo = "身份证月份无效";  
            return tipInfo;  
        }  
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {  
            tipInfo = "身份证日期无效";  
            return tipInfo;  
        }  


        // 判断地区码是否有效   
        Hashtable areacode = GetAreaCode();  
        //如果身份证前两位的地区码不在Hashtable，则地区码有误  
        if (areacode.get(Ai.substring(0, 2)) == null) {  
            tipInfo = "身份证地区编码错误。";  
            return tipInfo;  
        }  

        if(isVarifyCode(Ai,IDStr)==false){  
            tipInfo = "身份证校验码无效，不是合法的身份证号码";  
            return tipInfo;  
        }  


        return tipInfo;  
    }  


     /* 
      * 判断第18位校验码是否正确 
     * 第18位校验码的计算方式：  
        　　1. 对前17位数字本体码加权求和  
        　　公式为：S = Sum(Ai * Wi), i = 0, ... , 16  
        　　其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2  
        　　2. 用11对计算结果取模  
        　　Y = mod(S, 11)  
        　　3. 根据模的值得到对应的校验码  
        　　对应关系为：  
        　　 Y值：     0  1  2  3  4  5  6  7  8  9  10  
        　　校验码： 1  0  X  9  8  7  6  5  4  3   2 
     */  
    private static boolean isVarifyCode(String Ai,String IDStr) {  
         String[] VarifyCode = { "1", "0", "X", "9", "8", "7", "6", "5", "4","3", "2" };  
         String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7","9", "10", "5", "8", "4", "2" };  
         int sum = 0;  
         for (int i = 0; i < 17; i++) {  
            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);  
         }  
         int modValue = sum % 11;  
         String strVerifyCode = VarifyCode[modValue];  
         Ai = Ai + strVerifyCode;  
         if (IDStr.length() == 18) {  
             if (Ai.equals(IDStr) == false) {  
                 return false;  

             }  
         }   
         return true;  
    }  


    /** 
     * 将所有地址编码保存在一个Hashtable中     
     * @return Hashtable 对象 
     */  


    private static Hashtable GetAreaCode() {  
        Hashtable hashtable = new Hashtable();  
        hashtable.put("11", "北京");  
        hashtable.put("12", "天津");  
        hashtable.put("13", "河北");  
        hashtable.put("14", "山西");  
        hashtable.put("15", "内蒙古");  
        hashtable.put("21", "辽宁");  
        hashtable.put("22", "吉林");  
        hashtable.put("23", "黑龙江");  
        hashtable.put("31", "上海");  
        hashtable.put("32", "江苏");  
        hashtable.put("33", "浙江");  
        hashtable.put("34", "安徽");  
        hashtable.put("35", "福建");  
        hashtable.put("36", "江西");  
        hashtable.put("37", "山东");  
        hashtable.put("41", "河南");  
        hashtable.put("42", "湖北");  
        hashtable.put("43", "湖南");  
        hashtable.put("44", "广东");  
        hashtable.put("45", "广西");  
        hashtable.put("46", "海南");  
        hashtable.put("50", "重庆");  
        hashtable.put("51", "四川");  
        hashtable.put("52", "贵州");  
        hashtable.put("53", "云南");  
        hashtable.put("54", "西藏");  
        hashtable.put("61", "陕西");  
        hashtable.put("62", "甘肃");  
        hashtable.put("63", "青海");  
        hashtable.put("64", "宁夏");  
        hashtable.put("65", "新疆");  
        hashtable.put("71", "台湾");  
        hashtable.put("81", "香港");  
        hashtable.put("82", "澳门");  
        hashtable.put("91", "国外");  
        return hashtable;  
    }  

    /** 
     * 判断字符串是否为数字,0-9重复0次或者多次    
     * @param strnum 
     * @return 
     */  
    private static boolean isNumeric(String strnum) {  
        Pattern pattern = Pattern.compile("[0-9]*");  
        Matcher isNum = pattern.matcher(strnum);  
        if (isNum.matches()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  

    /** 
     * 功能：判断字符串出生日期是否符合正则表达式：包括年月日，闰年、平年和每月31天、30天和闰月的28天或者29天 
     *  
     * @param string 
     * @return 
     */ 
    public static boolean isDate(String strDate) {  

        Pattern pattern = Pattern  
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");  
        Matcher m = pattern.matcher(strDate);  
        if (m.matches()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  
}
```

> 同样我们也来验证一下结果：结果是完全正确。 
> **情况一：** 
> ![18位数](http://img.blog.csdn.net/20160131124421511) 
> 18位数的身份证 
> **情况二：** 
> ![15位](http://img.blog.csdn.net/20160131124754938) 
> 15位数的身份证 
> **情况三：** 
> ![带X的](http://img.blog.csdn.net/20160131125037489) 

### 日期工具

```java
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.richerpay.ryshop.log.KLog;

/**
 * 时间日期格式化工具类
 * 
 */
public class ToolDateTime {

	/** 日期格式：yyyy-MM-dd HH:mm:ss **/
	public static final String DF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	/** 日期格式：yyyy-MM-dd HH:mm **/
	public static final String DF_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

	/** 日期格式：yyyy-MM-dd **/
	public static final String DF_YYYY_MM_DD = "yyyy-MM-dd";

	/** 日期格式：HH:mm:ss **/
	public static final String DF_HH_MM_SS = "HH:mm:ss";

	/** 日期格式：HH:mm **/
	public static final String DF_HH_MM = "HH:mm";

	private final static long MINUTE = 60 * 1000;// 1分钟
	private final static long HOUR = 60 * MINUTE;// 1小时
	private final static long DAY = 24 * HOUR;// 1天
	private final static long MONTH = 31 * DAY;// 月
	private final static long YEAR = 12 * MONTH;// 年

	/** Log输出标识 **/
	private static final String TAG = ToolDateTime.class.getSimpleName();

	/**
	 * 将日期格式化成友好的字符串：几分钟前、几小时前、几天前、几月前、几年前、刚刚
	 * 
	 * @param date
	 * @return
	 */
	public static String formatFriendly(Date date) {
		if (date == null) {
			return null;
		}
		long diff = new Date().getTime() - date.getTime();
		long r = 0;
		if (diff > YEAR) {
			r = (diff / YEAR);
			return r + "年前";
		}
		if (diff > MONTH) {
			r = (diff / MONTH);
			return r + "个月前";
		}
		if (diff > DAY) {
			r = (diff / DAY);
			return r + "天前";
		}
		if (diff > HOUR) {
			r = (diff / HOUR);
			return r + "个小时前";
		}
		if (diff > MINUTE) {
			r = (diff / MINUTE);
			return r + "分钟前";
		}
		return "刚刚";
	}

	/**
	 * 将日期以yyyy-MM-dd HH:mm:ss格式化
	 * 
	 * @param dateL
	 *            日期
	 * @return
	 */
	public static String formatDateTime(long dateL) {
		SimpleDateFormat sdf = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS);
		Date date = new Date(dateL);
		return sdf.format(date);
	}

	/**
	 * 将日期以yyyy-MM-dd HH:mm:ss格式化
	 * 
	 * @param dateL
	 *            日期
	 * @return
	 */
	public static String formatDateTime(long dateL, String formater) {
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
		return sdf.format(new Date(dateL));
	}

	/**
	 * 将日期以yyyy-MM-dd HH:mm:ss格式化
	 * 
	 * @param dateL
	 *            日期
	 * @return
	 */
	public static String formatDateTime(Date date, String formater) {
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
		return sdf.format(date);
	}

	/**
	 * 将日期字符串转成日期
	 * 
	 * @param strDate
	 *            字符串日期
	 * @return java.util.date日期类型
	 */

	public static Date parseDate(String strDate) {
		DateFormat dateFormat = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS);
		Date returnDate = null;
		try {
			returnDate = dateFormat.parse(strDate);
		} catch (ParseException e) {
			KLog.v(TAG, "parseDate failed !");

		}
		return returnDate;

	}

	/**
	 * 获取系统当前日期
	 * 
	 * @return
	 */
	public static Date gainCurrentDate() {
		return new Date();
	}

	/**
	 * 验证日期是否比当前日期早
	 * 
	 * @param target1
	 *            比较时间1
	 * @param target2
	 *            比较时间2
	 * @return true 则代表target1比target2晚或等于target2，否则比target2早
	 */
	public static boolean compareDate(Date target1, Date target2) {
		boolean flag = false;
		try {
			String target1DateTime = ToolDateTime.formatDateTime(target1,
					DF_YYYY_MM_DD_HH_MM_SS);
			String target2DateTime = ToolDateTime.formatDateTime(target2,
					DF_YYYY_MM_DD_HH_MM_SS);
			if (target1DateTime.compareTo(target2DateTime) <= 0) {
				flag = true;
			}
		} catch (Exception e1) {
			KLog.e("比较失败，原因：" + e1.getMessage());
		}
		return flag;
	}

	/**
	 * 对日期进行增加操作
	 * 
	 * @param target
	 *            需要进行运算的日期
	 * @param hour
	 *            小时
	 * @return
	 */
	public static Date addDateTime(Date target, double hour) {
		if (null == target || hour < 0) {
			return target;
		}

		return new Date(target.getTime() + (long) (hour * 60 * 60 * 1000));
	}

	/**
	 * 对日期进行相减操作
	 * 
	 * @param target
	 *            需要进行运算的日期
	 * @param hour
	 *            小时
	 * @return
	 */
	public static Date subDateTime(Date target, double hour) {
		if (null == target || hour < 0) {
			return target;
		}

		return new Date(target.getTime() - (long) (hour * 60 * 60 * 1000));
	}
 private static SimpleDateFormat second = new SimpleDateFormat(
            "yy-MM-dd hh:mm:ss");

    private static SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat detailDay = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static SimpleDateFormat tempTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat excelDate = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 格式化excel中的时间
     * @param date
     * @return
     */
    public static String formatDateForExcelDate(Date date) {
        return excelDate.format(date);
    }

    /**
     * 将日期格式化作为文件名
     * @param date
     * @return
     */
    public static String formatDateForFileName(Date date) {
        return fileName.format(date);
    }

    /**
     * 格式化日期(精确到秒)
     *
     * @param date
     * @return
     */
    public static String formatDateSecond(Date date) {
        return second.format(date);
    }

    /**
     * 格式化日期(精确到秒)
     *
     * @param date
     * @return
     */
    public static String tempDateSecond(Date date) {
        return tempTime.format(date);
    }

    public static Date tempDateSecond(String str) {
        try {
            return tempTime.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    /**
     * 格式化日期(精确到天)
     *
     * @param date
     * @return
     */
    public static String formatDateDay(Date date) {
        return day.format(date);
    }

    /**
     * 格式化日期(精确到天)
     *
     * @param date
     * @return
     */
    public static String formatDateDetailDay(Date date) {
        return detailDay.format(date);
    }

    /**
     * 将double类型的数字保留两位小数（四舍五入）
     *
     * @param number
     * @return
     */
    public static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("#0.00");
        return df.format(number);
    }

    /**
     * 将字符串转换成日期
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date formateDate(String date) throws Exception {
        return day.parse(date);
    }

    /**
     * 将字符日期转换成Date
     * @param date
     * @return
     * @throws Exception
     */
    public static Date parseStringToDate(String date) throws Exception {
        return day.parse(date);
    }

    public static String formatDoubleNumber(double number) {
        DecimalFormat df = new DecimalFormat("#");
        return df.format(number);
    }
}

```

### 转换图片颜色工具

```java
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

public class ConvertImageColor {
    Activity activity;

    public ConvertImageColor(Activity activity) {
        this.activity = activity;
    }

    // 改变图标的颜色
    public  Drawable changeImageColor(int drawable,int color){
        Drawable myIcon = activity.getResources().getDrawable(drawable);
        myIcon.setColorFilter(activity.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        return  myIcon;
    }
}
```

### Intent 工具

```
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 基本功能：Intent工具
 */
public class IntentUtil {
    private static final String TAG = IntentUtil.class.getSimpleName();
	private static Intent intent;
	private static final Object lock = new Object();

	/**
	 * 
	 * @Title: startActivity
	 */
	public static <T> void doAction(Activity activity, Class<T> class1) {
		synchronized (lock) {
			try {
				intent = new Intent(activity, class1);
				activity.startActivity(intent);
				// activity.overridePendingTransition(R.anim.zoom_enter,
				// R.anim.zoom_exit);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @Title: startActivity
	 */
	public static <T> void doAction(Activity activity, Class<T> class1,
			String key, Object object) {
		synchronized (lock) {
			try {
				intent = new Intent(activity, class1);
				if (object != null && object instanceof String) {
					intent.putExtra(key, (String) object);
				} else if (object != null && object instanceof Integer) {
					intent.putExtra(key, (Integer) object);
				}
				activity.startActivity(intent);
				// activity.overridePendingTransition(R.anim.zoom_enter,
				// R.anim.zoom_exit);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @Title: startActivity
	 */
	public static <T> void doAction(Activity activity, Class<T> class1,
			HashMap<String, Serializable> map) {
		synchronized (lock) {
			try {
				intent = new Intent(activity, class1);
				if (null != map) {
					Set<String> keys = map.keySet();
					for (String key : keys) {
						intent.putExtra(key, map.get(key));
					}
					activity.startActivity(intent);
					// activity.overridePendingTransition(R.anim.zoom_enter,
					// R.anim.zoom_exit);
				}
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @Title: startActivity
	 */
	public static <T> void doAction(Activity activity, Class<T> class1,
			Bundle bundle) {
		synchronized (lock) {
			try {
				intent = new Intent(activity, class1);
				if (bundle != null) {
					intent.putExtras(bundle);
				}

				activity.startActivity(intent);
				// activity.overridePendingTransition(R.anim.zoom_enter,
				// R.anim.zoom_exit);

			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}

	}
    public static Intent getLauncherIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        return intent;
    }
    public static void logIntent(String tag, Intent intent) {
        if (intent == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\nAction:" + intent.getAction());
        sb.append("\nData:" + intent.getData());
        sb.append("\nDataStr:" + intent.getDataString());
        sb.append("\nScheme:" + intent.getScheme());
        sb.append("\nType:" + intent.getType());
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                sb.append("\nEXTRA: {" + key + "::" + value + "}");
            }
        } else {
            sb.append("\nNO EXTRAS");
        }
        Log.i(tag, sb.toString());
    }

    public static int sdkVersion() {
        return new Integer(Build.VERSION.SDK).intValue();
    }

    public static void startDialer(Context context, String phoneNumber) {
        try {
            Intent dial = new Intent();
            dial.setAction(Intent.ACTION_DIAL);
            dial.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(dial);
        } catch (Exception ex) {
            Log.e(TAG, "Error starting phone dialer intent.", ex);
            Toast.makeText(context,
                    "Sorry, we couldn't find any app to place a phone call!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void startSmsIntent(Context context, String phoneNumber) {
        try {
            Uri uri = Uri.parse("sms:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra("address", phoneNumber);
            intent.setType("vnd.android-dir/mms-sms");
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Error starting sms intent.", ex);
            Toast.makeText(context,
                    "Sorry, we couldn't find any app to send an SMS!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void startEmailIntent(Context context, String emailAddress) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{emailAddress});
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Error starting email intent.", ex);
            Toast.makeText(context,
                    "Sorry, we couldn't find any app for sending emails!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void startWebIntent(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Error starting url intent.", ex);
            Toast.makeText(context,
                    "Sorry, we couldn't find any app for viewing this url!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
```

###  手机相关操作API

```java
package com.richerpay.ryshop.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.richerpay.ryshop.log.KLog;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 手机相关操作API
 * 
 * @version 1.0
 * 
 */
public class ToolPhone {

	/**
	 * 直接呼叫指定的号码(需要<uses-permission
	 * android:name="android.permission.CALL_PHONE"/>权限)
	 * 
	 * @param mContext
	 *            上下文Context
	 * @param phoneNumber
	 *            需要呼叫的手机号码
	 */
	public static void callPhone(Context mContext, String phoneNumber) {
		Uri uri = Uri.parse("tel:" + phoneNumber);
		Intent call = new Intent(Intent.ACTION_CALL, uri);
		mContext.startActivity(call);
	}

	/**
	 * 跳转至拨号界面
	 * 
	 * @param mContext
	 *            上下文Context
	 * @param phoneNumber
	 *            需要呼叫的手机号码
	 */
	public static void toCallPhoneActivity(Context mContext, String phoneNumber) {
		Uri uri = Uri.parse("tel:" + phoneNumber);
		Intent call = new Intent(Intent.ACTION_DIAL, uri);
		mContext.startActivity(call);
	}

	/**
	 * 直接调用短信API发送信息(设置监听发送和接收状态)
	 * 
	 * @param strPhone
	 *            手机号码
	 * @param strMsgContext
	 *            短信内容
	 */
	public static void sendMessage(final Context mContext,
			final String strPhone, final String strMsgContext) {

		// 处理返回的发送状态
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sendIntent = PendingIntent.getBroadcast(mContext, 0,
				sentIntent, 0);
		// register the Broadcast Receivers
		mContext.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(mContext, "短信发送成功", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));

		// 处理返回的接收状态
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent backIntent = PendingIntent.getBroadcast(mContext, 0,
				deliverIntent, 0);
		mContext.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				Toast.makeText(mContext, strPhone + "已经成功接收",
						Toast.LENGTH_SHORT).show();
			}
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		// 拆分短信内容（手机短信长度限制）
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> msgList = smsManager.divideMessage(strMsgContext);
		for (String text : msgList) {
			smsManager.sendTextMessage(strPhone, null, text, sendIntent,
					backIntent);
		}
	}

	/**
	 * 跳转至发送短信界面(自动设置接收方的号码)
	 * 
	 * @param mActivity
	 *            Activity
	 * @param strPhone
	 *            手机号码
	 * @param strMsgContext
	 *            短信内容
	 */
	public static void toSendMessageActivity(Context mContext, String strPhone,
			String strMsgContext) {
		if (PhoneNumberUtils.isGlobalPhoneNumber(strPhone)) {
			Uri uri = Uri.parse("smsto:" + strPhone);
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
			sendIntent.putExtra("sms_body", strMsgContext);
			mContext.startActivity(sendIntent);
		}
	}

	/**
	 * 跳转至联系人选择界面
	 * 
	 * @param mContext
	 *            上下文
	 * @param requestCode
	 *            请求返回区分代码
	 */
	public static void toChooseContactsList(Activity mContext, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		mContext.startActivityForResult(intent, requestCode);
	}

	/**
	 * 获取选择的联系人的手机号码
	 * 
	 * @param mContext
	 *            上下文
	 * @param resultCode
	 *            请求返回Result状态区分代码
	 * @param data
	 *            onActivityResult返回的Intent
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getChoosedPhoneNumber(Activity mContext,
			int resultCode, Intent data) {
		// 返回结果
		String phoneResult = "";
		if (Activity.RESULT_OK == resultCode) {
			Uri uri = data.getData();
			Cursor mCursor = mContext.managedQuery(uri, null, null, null, null);
			mCursor.moveToFirst();

			int phoneColumn = mCursor
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			int phoneNum = mCursor.getInt(phoneColumn);
			if (phoneNum > 0) {
				// 获得联系人的ID号
				int idColumn = mCursor
						.getColumnIndex(ContactsContract.Contacts._ID);
				String contactId = mCursor.getString(idColumn);
				// 获得联系人的电话号码的cursor;
				Cursor phones = mContext.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				if (phones.moveToFirst()) {
					// 遍历所有的电话号码
					for (; !phones.isAfterLast(); phones.moveToNext()) {
						int index = phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
						int typeindex = phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
						int phone_type = phones.getInt(typeindex);
						String phoneNumber = phones.getString(index);
						if (phone_type == 2) {
							phoneResult = phoneNumber;
						}
					}
					if (!phones.isClosed()) {
						phones.close();
					}
				}
			}
			// 关闭游标
			mCursor.close();
		}

		return phoneResult;
	}

	/**
	 * 跳转至拍照程序界面
	 * 
	 * @param mContext
	 *            上下文
	 * @param requestCode
	 *            请求返回Result区分代码
	 */
	public static void toCameraActivity(Activity mContext, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mContext.startActivityForResult(intent, requestCode);
	}

	/**
	 * 跳转至相册选择界面
	 * 
	 * @param mContext
	 *            上下文
	 * @param requestCode
	 */
	public static void toImagePickerActivity(Activity mContext, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		mContext.startActivityForResult(intent, requestCode);
	}

	/**
	 * 获得选中相册的图片
	 * 
	 * @param mContext
	 *            上下文
	 * @param data
	 *            onActivityResult返回的Intent
	 * @return
	 */

	@SuppressWarnings({ "deprecation", "unused" })
	public static Bitmap getChoosedImage(Activity mContext, Intent data) {
		if (data == null) {
			return null;
		}

		Bitmap bm = null;

		// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		ContentResolver resolver = mContext.getContentResolver();

		// 此处的用于判断接收的Activity是不是你想要的那个
		try {
			Uri originalUri = data.getData(); // 获得图片的uri
			bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
			// 这里开始的第二部分，获取图片的路径：
			String[] proj = { MediaStore.Images.Media.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = mContext.managedQuery(originalUri, proj, null,
					null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// 将光标移至开头 ，这个很重要，不小心很容易引起越界
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			String path = cursor.getString(column_index);
			// 不用了关闭游标
			cursor.close();
		} catch (Exception e) {
			KLog.e("ToolPhone", e.getMessage());
		}

		return bm;
	}

	/**
	 * 调用本地浏览器打开一个网页
	 * 
	 * @param mContext
	 *            上下文
	 * @param strSiteUrl
	 *            网页地址
	 */
	public static void openWebSite(Context mContext, String strSiteUrl) {
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strSiteUrl));
		mContext.startActivity(webIntent);
	}

	/**
	 * 跳转至系统设置界面
	 * 
	 * @param mContext
	 *            上下文
	 */
	public static void toSettingActivity(Context mContext) {
		Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
		mContext.startActivity(settingsIntent);
	}

	/**
	 * 跳转至WIFI设置界面
	 * 
	 * @param mContext
	 *            上下文
	 */
	public static void toWIFISettingActivity(Context mContext) {
		Intent wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		mContext.startActivity(wifiSettingsIntent);
	}

	/**
	 * 启动本地应用打开PDF
	 * 
	 * @param mContext
	 *            上下文
	 * @param filePath
	 *            文件路径
	 */
	public static void openPDFFile(Context mContext, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				Uri path = Uri.fromFile(file);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(path, "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(intent);
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "未检测到可打开PDF相关软件", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 启动本地应用打开PDF
	 * 
	 * @param mContext
	 *            上下文
	 * @param filePath
	 *            文件路径
	 */
	public static void openWordFile(Context mContext, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				Uri path = Uri.fromFile(file);
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(path, "application/msword");
				mContext.startActivity(intent);
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "未检测到可打开Word文档相关软件", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 调用WPS打开office文档 http://bbs.wps.cn/thread-22349340-1-1.html
	 * 
	 * @param mContext
	 *            上下文
	 * @param filePath
	 *            文件路径
	 */
	public static void openOfficeByWPS(Context mContext, String filePath) {

		try {

			// 文件存在性检查
			File file = new File(filePath);
			if (!file.exists()) {
				Toast.makeText(mContext, filePath + "文件路径不存在",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// 检查是否安装WPS
			String wpsPackageEng = "cn.wps.moffice_eng";// 普通版与英文版一样
			// String wpsActivity =
			// "cn.wps.moffice.documentmanager.PreStartActivity";
			String wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2";// 默认第三方程序启动

			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setClassName(wpsPackageEng, wpsActivity2);

			Uri uri = Uri.fromFile(new File(filePath));
			intent.setData(uri);
			mContext.startActivity(intent);

		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, "本地未安装WPS", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(mContext, "打开文档失败", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 判断是否安装指定包名的APP
	 * 
	 * @param mContext
	 *            上下文
	 * @param packageName
	 *            包路径
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean isInstalledApp(Context mContext, String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}

		try {
			ApplicationInfo info = mContext.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 判断是否存在指定的Activity
	 * 
	 * @param mContext
	 *            上下文
	 * @param packageName
	 *            包名
	 * @param className
	 *            activity全路径类名
	 * @return
	 */
	public static boolean isExistActivity(Context mContext, String packageName,
			String className) {

		Boolean result = true;
		Intent intent = new Intent();
		intent.setClassName(packageName, className);

		if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
			result = false;
		} else if (intent.resolveActivity(mContext.getPackageManager()) == null) {
			result = false;
		} else {
			List<ResolveInfo> list = mContext.getPackageManager()
					.queryIntentActivities(intent, 0);
			if (list.size() == 0) {
				result = false;
			}
		}

		return result;
	}

}
```



### 手机信息采集工具

```java
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基本功能：手机信息采集工具
 */
public class MobileUtil {

    /**
     * Print telephone info.
     */
    public static String printMobileInfo(Context context) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("系统时间：").append(time).append("\n");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = tm.getSubscriberId();
        //IMSI前面三位460是国家号码，其次的两位是运营商代号，00、02是中国移动，01是联通，03是电信。
        String providerName = null;
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providerName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                providerName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                providerName = "中国电信";
            }
        }
        sb.append(providerName).append("\n").append(getNativePhoneNumber(context)).append("\n网络模式：").append(getNetType(context)).append("\nIMSI是：").append(IMSI);
        sb.append("\nDeviceID(IMEI)       :").append(tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion:").append(tm.getDeviceSoftwareVersion());
        sb.append("\ngetLine1Number       :").append(tm.getLine1Number());
        sb.append("\nNetworkCountryIso    :").append(tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator      :").append(tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName  :").append(tm.getNetworkOperatorName());
        sb.append("\nNetworkType          :").append(tm.getNetworkType());
        sb.append("\nPhoneType            :").append(tm.getPhoneType());
        sb.append("\nSimCountryIso        :").append(tm.getSimCountryIso());
        sb.append("\nSimOperator          :").append(tm.getSimOperator());
        sb.append("\nSimOperatorName      :").append(tm.getSimOperatorName());
        sb.append("\nSimSerialNumber      :").append(tm.getSimSerialNumber());
        sb.append("\ngetSimState          :").append(tm.getSimState());
        sb.append("\nSubscriberId         :").append(tm.getSubscriberId());
        sb.append("\nVoiceMailNumber      :").append(tm.getVoiceMailNumber());

        return sb.toString();
    }


    /**
     * 打印系统信息
     * @return
     */
    public static String printSystemInfo() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("_______  系统信息  ").append(time).append(" ______________");
        sb.append("\nID                 :").append(Build.ID);
        sb.append("\nBRAND              :").append(Build.BRAND);
        sb.append("\nMODEL              :").append(Build.MODEL);
        sb.append("\nRELEASE            :").append(Build.VERSION.RELEASE);
        sb.append("\nSDK                :").append(Build.VERSION.SDK);

        sb.append("\n_______ OTHER _______");
        sb.append("\nBOARD              :").append(Build.BOARD);
        sb.append("\nPRODUCT            :").append(Build.PRODUCT);
        sb.append("\nDEVICE             :").append(Build.DEVICE);
        sb.append("\nFINGERPRINT        :").append(Build.FINGERPRINT);
        sb.append("\nHOST               :").append(Build.HOST);
        sb.append("\nTAGS               :").append(Build.TAGS);
        sb.append("\nTYPE               :").append(Build.TYPE);
        sb.append("\nTIME               :").append(Build.TIME);
        sb.append("\nINCREMENTAL        :").append(Build.VERSION.INCREMENTAL);

        sb.append("\n_______ CUPCAKE-3 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sb.append("\nDISPLAY            :").append(Build.DISPLAY);
        }

        sb.append("\n_______ DONUT-4 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            sb.append("\nSDK_INT            :").append(Build.VERSION.SDK_INT);
            sb.append("\nMANUFACTURER       :").append(Build.MANUFACTURER);
            sb.append("\nBOOTLOADER         :").append(Build.BOOTLOADER);
            sb.append("\nCPU_ABI            :").append(Build.CPU_ABI);
            sb.append("\nCPU_ABI2           :").append(Build.CPU_ABI2);
            sb.append("\nHARDWARE           :").append(Build.HARDWARE);
            sb.append("\nUNKNOWN            :").append(Build.UNKNOWN);
            sb.append("\nCODENAME           :").append(Build.VERSION.CODENAME);
        }

        sb.append("\n_______ GINGERBREAD-9 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("\nSERIAL             :").append(Build.SERIAL);
        }
        return sb.toString();
    }

    /****
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetType(Context context) {
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info == null) {
                return "";
            }
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
                    return "CDMA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                    return "EDGE";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                    return "EVDO0";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                    return "EVDOA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                    return "GPRS";
                }
                /*
                 * else if(info.getSubtype() ==
                 * TelephonyManager.NETWORK_TYPE_HSDPA){ return "HSDPA"; }else
                 * if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA){
                 * return "HSPA"; }else if(info.getSubtype() ==
                 * TelephonyManager.NETWORK_TYPE_HSUPA){ return "HSUPA"; }
                 */
                else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    return "UMTS";
                } else {
                    return "3G";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * 获取当前设置的电话号码
     */
    public static String getNativePhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String NativePhoneNumber = null;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return String.format("手机号: %s", NativePhoneNumber);
    }
    /**
     * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
     * IMSI共有15位，其结构如下：
     * MCC+MNC+MIN
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;
     * MNC:Mobile NetworkCode，移动网络码，共2位
     * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
     * 合起来就是（也是Android手机中APN配置文件中的代码）：
     * 中国移动：46000 46002
     * 中国联通：46001
     * 中国电信：46003
     * 举例，一个典型的IMSI号码为460030912121001
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        return IMSI;
    }

    /**
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
     * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的
     * 其组成为：
     * 1. 前6位数(TAC)是”型号核准号码”，一般代表机型
     * 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地
     * 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号
     * 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = telephonyManager.getDeviceId();
        return IMEI;
    }



    /////_________________ 双卡双待系统IMEI和IMSI方案（see more on http://benson37.iteye.com/blog/1923946）

    /**
     * 双卡双待神机IMSI、IMSI、PhoneType信息
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static class TeleInfo {
        public String imsi_1;
        public String imsi_2;
        public String imei_1;
        public String imei_2;
        public int phoneType_1;
        public int phoneType_2;

        @Override
        public String toString() {
            return "TeleInfo{" +
                    "imsi_1='" + imsi_1 + '\'' +
                    ", imsi_2='" + imsi_2 + '\'' +
                    ", imei_1='" + imei_1 + '\'' +
                    ", imei_2='" + imei_2 + '\'' +
                    ", phoneType_1=" + phoneType_1 +
                    ", phoneType_2=" + phoneType_2 +
                    '}';
        }
    }

    /**
     * MTK Phone.
     *
     * 获取 MTK 神机的双卡 IMSI、IMSI 信息
     */
    public static TeleInfo getMtkTeleInfo(Context context) {
        TeleInfo teleInfo = new TeleInfo();
        try {
            Class<?> phone = Class.forName("com.android.internal.telephony.Phone");

            Field fields1 = phone.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            int simId_1 = (Integer) fields1.get(null);

            Field fields2 = phone.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            int simId_2 = (Integer) fields2.get(null);

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getSubscriberIdGemini = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
            String imsi_1 = (String) getSubscriberIdGemini.invoke(tm, simId_1);
            String imsi_2 = (String) getSubscriberIdGemini.invoke(tm, simId_2);
            teleInfo.imsi_1 = imsi_1;
            teleInfo.imsi_2 = imsi_2;

            Method getDeviceIdGemini = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
            String imei_1 = (String) getDeviceIdGemini.invoke(tm, simId_1);
            String imei_2 = (String) getDeviceIdGemini.invoke(tm, simId_2);

            teleInfo.imei_1 = imei_1;
            teleInfo.imei_2 = imei_2;

            Method getPhoneTypeGemini = TelephonyManager.class.getDeclaredMethod("getPhoneTypeGemini", int.class);
            int phoneType_1 = (Integer) getPhoneTypeGemini.invoke(tm, simId_1);
            int phoneType_2 = (Integer) getPhoneTypeGemini.invoke(tm, simId_2);
            teleInfo.phoneType_1 = phoneType_1;
            teleInfo.phoneType_2 = phoneType_2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teleInfo;
    }

    /**
     * MTK Phone.
     *
     * 获取 MTK 神机的双卡 IMSI、IMSI 信息
     */
    public static TeleInfo getMtkTeleInfo2(Context context) {
        TeleInfo teleInfo = new TeleInfo();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> phone = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = phone.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            int simId_1 = (Integer) fields1.get(null);
            Field fields2 = phone.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            int simId_2 = (Integer) fields2.get(null);

            Method getDefault = TelephonyManager.class.getMethod("getDefault", int.class);
            TelephonyManager tm1 = (TelephonyManager) getDefault.invoke(tm, simId_1);
            TelephonyManager tm2 = (TelephonyManager) getDefault.invoke(tm, simId_2);

            String imsi_1 = tm1.getSubscriberId();
            String imsi_2 = tm2.getSubscriberId();
            teleInfo.imsi_1 = imsi_1;
            teleInfo.imsi_2 = imsi_2;

            String imei_1 = tm1.getDeviceId();
            String imei_2 = tm2.getDeviceId();
            teleInfo.imei_1 = imei_1;
            teleInfo.imei_2 = imei_2;

            int phoneType_1 = tm1.getPhoneType();
            int phoneType_2 = tm2.getPhoneType();
            teleInfo.phoneType_1 = phoneType_1;
            teleInfo.phoneType_2 = phoneType_2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teleInfo;
    }

    /**
     * Qualcomm Phone.
     * 获取 高通 神机的双卡 IMSI、IMSI 信息
     */
    public static TeleInfo getQualcommTeleInfo(Context context) {
        TeleInfo teleInfo = new TeleInfo();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> simTMclass = Class.forName("android.telephony.MSimTelephonyManager");
            // Object sim = context.getSystemService("phone_msim");
            Object sim = context.getSystemService(Context.TELEPHONY_SERVICE);
            int simId_1 = 0;
            int simId_2 = 1;

            Method getSubscriberId = simTMclass.getMethod("getSubscriberId", int.class);
            String imsi_1 = (String) getSubscriberId.invoke(sim, simId_1);
            String imsi_2 = (String) getSubscriberId.invoke(sim, simId_2);
            teleInfo.imsi_1 = imsi_1;
            teleInfo.imsi_2 = imsi_2;

            Method getDeviceId = simTMclass.getMethod("getDeviceId", int.class);
            String imei_1 = (String) getDeviceId.invoke(sim, simId_1);
            String imei_2 = (String) getDeviceId.invoke(sim, simId_2);
            teleInfo.imei_1 = imei_1;
            teleInfo.imei_2 = imei_2;

            Method getDataState = simTMclass.getMethod("getDataState");
            int phoneType_1 = tm.getDataState();
            int phoneType_2 = (Integer) getDataState.invoke(sim);
            teleInfo.phoneType_1 = phoneType_1;
            teleInfo.phoneType_2 = phoneType_2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teleInfo;
    }

    /**
     * Spreadtrum Phone.
     *
     * 获取 展讯 神机的双卡 IMSI、IMSI 信息
     */
    public static TeleInfo getSpreadtrumTeleInfo(Context context) {
        TeleInfo teleInfo = new TeleInfo();
        try {

            TelephonyManager tm1 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi_1 = tm1.getSubscriberId();
            String imei_1 = tm1.getDeviceId();
            int phoneType_1 = tm1.getPhoneType();
            teleInfo.imsi_1 = imsi_1;
            teleInfo.imei_1 = imei_1;
            teleInfo.phoneType_1 = phoneType_1;

            Class<?> phoneFactory = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method getServiceName = phoneFactory.getMethod("getServiceName", String.class, int.class);
            getServiceName.setAccessible(true);
            TelephonyManager tm2 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi_2 = tm2.getSubscriberId();
            String imei_2 = tm2.getDeviceId();
            int phoneType_2 = tm2.getPhoneType();
            teleInfo.imsi_2 = imsi_2;
            teleInfo.imei_2 = imei_2;
            teleInfo.phoneType_2 = phoneType_2;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return teleInfo;
    }

    /**
     * 获取 MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */
    public static String getMacAddress(Context context) {
        //wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();

        return mac;
    }

    /**
     * 获取 开机时间
     */
    public static String getBootTimeString() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        int h = (int) ((ut / 3600));
        int m = (int) ((ut / 60) % 60);

        return h + ":" + m;
    }


}
```

### 正则表达式工具

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基本功能：正则表达式工具类
 */
public class RegexUtil {
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }


    public static boolean checkIdCard(String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex,idCard);
    }


    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }


    public static boolean checkPhone(String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }


    public static boolean checkDigit(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return Pattern.matches(regex,digit);
    }


    public static boolean checkDecimals(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return Pattern.matches(regex, decimals);
    }


    public static boolean checkBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex,blankSpace);
    }


    public static boolean checkChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex,chinese);
    }


    public static boolean checkBirthday(String birthday) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex,birthday);
    }


    public static boolean checkURL(String url) {
//        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        String regex = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        return Pattern.matches(regex, url);
    }

    public static String getDomain(String url) {
        Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        // 获取完整的域名
        // Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher.find();
        return matcher.group();
    }

    public static boolean checkPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }


    public static boolean checkIpAddress(String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }
}
```

### SD卡工具

```java
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 基本功能：SD卡片工具
 */
public class SdCardUtil {

    /**
     * is sd card available.
     * @return true if available
     */
    public boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Get {@link android.os.StatFs}.
     */
    public static StatFs getStatFs(String path) {
        return new StatFs(path);
    }

    /**
     * Get phone data path.
     */
    public static String getDataPath() {
        return Environment.getDataDirectory().getPath();

    }

    /**
     * Get SD card path.
     */
    public static String getNormalSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * Get SD card path by CMD.
     */
    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
        String sdcard = null;
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedReader bufferedReader = null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
            String lineStr;
            while ((lineStr = bufferedReader.readLine()) != null) {
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray.length >= 5) {
                        sdcard = strArray[1].replace("/.android_secure", "");
                        return sdcard;
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sdcard = Environment.getExternalStorageDirectory().getPath();
        return sdcard;
    }

    /**
     * Get SD card path list.
     */
    public static ArrayList<String> getSDCardPathEx() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) {
                    continue;
                }
                if (line.contains("asec")) {
                    continue;
                }

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        list.add("*" + columns[1]);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        list.add(columns[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get available size of SD card.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableSize(String path) {
        try {
            File base = new File(path);
            StatFs stat = new StatFs(base.getPath());
            return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get SD card info detail.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static SDCardInfo getSDCardInfo() {
        SDCardInfo sd = new SDCardInfo();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sd.isExist = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());

                sd.totalBlocks = sf.getBlockCountLong();
                sd.blockByteSize = sf.getBlockSizeLong();

                sd.availableBlocks = sf.getAvailableBlocksLong();
                sd.availableBytes = sf.getAvailableBytes();

                sd.freeBlocks = sf.getFreeBlocksLong();
                sd.freeBytes = sf.getFreeBytes();

                sd.totalBytes = sf.getTotalBytes();
            }
        }
        return sd;
    }


    /**
     * see more {@link android.os.StatFs}
     */
    public static class SDCardInfo {
        public boolean isExist;
        public long totalBlocks;
        public long freeBlocks;
        public long availableBlocks;

        public long blockByteSize;

        public long totalBytes;
        public long freeBytes;
        public long availableBytes;

        @Override
        public String toString() {
            return "SDCardInfo{" +
                    "isExist=" + isExist +
                    ", totalBlocks=" + totalBlocks +
                    ", freeBlocks=" + freeBlocks +
                    ", availableBlocks=" + availableBlocks +
                    ", blockByteSize=" + blockByteSize +
                    ", totalBytes=" + totalBytes +
                    ", freeBytes=" + freeBytes +
                    ", availableBytes=" + availableBytes +
                    '}';
        }
    }
}
```

### Toast信息工具

```java
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 基本功能：Toast信息工具
 */
public class ToastUtil {

    /**
     * 将最长使用的显示方法单独提出来，方便使用。
     * 屏幕中心位置短时间显示Toast。
     *
     * @param context
     * @param message
     */
    public static void show(Context context, String message) {
        ToastShortCenter(context,message);
    }

    /**
     * 屏幕底部中间位置显示短时间Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortBottomCenter(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 屏幕底部左边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortBottomLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕底部右边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortBottomRight(Context context, String message) {

        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortCenter(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心左边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortCenterLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心右边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortCenterRight(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部中心位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortTopCenter(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部左边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortTopLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部右边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastShortTopRight(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕底部中间位置显示长时间Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongBottomCenter(Context context, String message) {
        if (context != null) {

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 屏幕底部左边位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongBottomLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕底部右边位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongBottomRight(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongCenter(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心左边位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongCenterLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕中心右边位置短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongCenterRight(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部中心位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongTopCenter(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部左边位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongTopLeft(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    /**
     * 屏幕顶部右边位置长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void ToastLongTopRight(Context context, String message) {
        if (context != null) {

            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
            toast.show();
        }
    }

}
```

### 类型转换工具

```java

/**
 * 基本功能：类型转换工具

 */
public class TypeConvertUtil {

    public static String nullOfString(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    public static byte stringToByte(String str) {
        byte b = 0;
        if (str != null) {
            try {
                b = Byte.parseByte(str);
            } catch (Exception e) {

            }
        }
        return b;
    }

    public static boolean stringToBoolean(String str) {
        if (str == null) {
            return false;
        } else {
            if (str.equals("1")) {
                return true;
            } else if (str.equals("0")) {
                return false;
            } else {
                try {
                    return Boolean.parseBoolean(str);
                } catch (Exception e) {
                    return false;
                }
            }
        }
    }

    public static int stringToInt(String str) {
        int i = 0;
        if (str != null) {
            try {
                i = Integer.parseInt(str.trim());
            } catch (Exception e) {
                i = 0;
            }

        } else {
            i = 0;
        }
        return i;
    }

    public static short stringToShort(String str) {
        short i = 0;
        if (str != null) {
            try {
                i = Short.parseShort(str.trim());
            } catch (Exception e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        return i;
    }


    public static double stringToDouble(String str) {
        double i = 0;
        if (str != null) {
            try {
                i = Double.parseDouble(str.trim());
            } catch (Exception e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        return i;
    }

    public static String intToString(int i) {
        String str = "";
        try {
            str = String.valueOf(i);
        } catch (Exception e) {
            str = "";
        }
        return str;
    }


    public static long doubleToLong(double d) {
        long lo = 0;
        try {
//double转换成long前要过滤掉double类型小数点后数据
            lo = Long.parseLong(String.valueOf(d).substring(0, String.valueOf(d).lastIndexOf(".")));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static int doubleToInt(double d) {
        int i = 0;
        try {
//double转换成long前要过滤掉double类型小数点后数据
            i = Integer.parseInt(String.valueOf(d).substring(0, String.valueOf(d).lastIndexOf(".")));
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }

    public static double longToDouble(long d) {
        double lo = 0;
        try {
            lo = Double.parseDouble(String.valueOf(d));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static int longToInt(long d) {
        int lo = 0;
        try {
            lo = Integer.parseInt(String.valueOf(d));
        } catch (Exception e) {
            lo = 0;
        }
        return lo;
    }

    public static long stringToLong(String str) {
        Long li = new Long(0);
        try {
            li = Long.valueOf(str);
        } catch (Exception e) {
        //li = new Long(0);
        }
        return li.longValue();
    }

    public static String longToString(long li) {
        String str = "";
        try {
            str = String.valueOf(li);
        } catch (Exception e) {

        }
        return str;
    }

}
```

### app版本工具

```java
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 基本功能：app版本工具
 */
public class VersionUtil {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 版本比较
     *
     * @param nowVersion    app版本
     * @param serverVersion 服务器版本
     * @return
     */
    public static boolean compareVersion(String nowVersion, String serverVersion) {

        if (nowVersion != null && serverVersion != null) {
            String[] nowVersions = nowVersion.split("\\.");
            String[] serverVersions = serverVersion.split("\\.");
            if (nowVersions != null && serverVersions != null && nowVersions.length > 1 && serverVersions.length > 1) {
                int nowVersionsFirst = Integer.parseInt(nowVersions[0]);
                int serverVersionFirst = Integer.parseInt(serverVersions[0]);
                int nowVersionsSecond = Integer.parseInt(nowVersions[1]);
                int serverVersionSecond = Integer.parseInt(serverVersions[1]);
                if (nowVersionsFirst < serverVersionFirst) {
                    return true;
                } else if (nowVersionsFirst == serverVersionFirst && nowVersionsSecond < serverVersionSecond) {
                    return true;
                }
            }
        }
        return false;
    }
}
```

本文有的来自于我的csdn: 

[android开发工具类（草鸡好用）](http://blog.csdn.net/u013278099/article/details/50614439)

更多的工具类请去我的github:

[**AndroidUtils**](https://github.com/zilianliuxue/AndroidStudy/tree/master/codeSample/AndroidUtils)