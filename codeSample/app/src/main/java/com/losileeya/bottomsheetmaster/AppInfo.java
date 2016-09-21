package com.losileeya.bottomsheetmaster;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-21
 * Time: 13:56
 * 类描述：
 *
 * @version :
 */
public class AppInfo {
    private String appLabel;
    private Drawable appIcon;
    private Intent intent ;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
