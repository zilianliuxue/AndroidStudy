package com.richerpay.ryshop.util;

import com.richerpay.ryshop.config.SysEnv;

import android.util.DisplayMetrics;

/**
 * 尺寸工具类
 * 
 */
public class ToolUnit {

	/** 设备显示材质 **/
	private static DisplayMetrics mDisplayMetrics = SysEnv.getDisplayMetrics();

	/**
	 * sp转换px
	 * 
	 * @param spValue
	 *            sp数值
	 * @return px数值
	 */
	public static int spTopx(float spValue) {
		return (int) (spValue * mDisplayMetrics.scaledDensity + 0.5f);
	}

	/**
	 * px转换sp
	 * 
	 * @param pxValue
	 *            px数值
	 * @return sp数值
	 */
	public static int pxTosp(float pxValue) {
		return (int) (pxValue / mDisplayMetrics.scaledDensity + 0.5f);
	}

	/**
	 * dip转换px
	 * 
	 * @param dipValue
	 *            dip数值
	 * @return px数值
	 */
	public static int dipTopx(int dipValue) {
		return (int) (dipValue * mDisplayMetrics.density + 0.5f);
	}

	/**
	 * px转换dip
	 * 
	 * @param pxValue
	 *            px数值
	 * @return dip数值
	 */
	public static int pxTodip(float pxValue) {
		return (int) (pxValue / mDisplayMetrics.density + 0.5f);
	}
}
