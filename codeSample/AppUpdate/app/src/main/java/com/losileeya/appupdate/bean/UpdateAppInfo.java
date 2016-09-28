package com.losileeya.appupdate.bean;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-27
 * Time: 11:20
 * 类描述：
 *
 * @version :
 */
public class UpdateAppInfo  {
    public UpdateInfo data; // 信息
    public Integer error_code; // 错误代码
    public String error_msg; // 错误信息
    public static class UpdateInfo{
        // app名字
        public String appname;
        //服务器版本
        public String serverVersion;
        //服务器标志
        public String serverFlag;
        //强制升级
        public String lastForce;
        //app最新版本地址
        public String updateurl;
        //升级信息
        public String upgradeinfo;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getServerVersion() {
            return serverVersion;
        }

        public void setServerVersion(String serverVersion) {
            this.serverVersion = serverVersion;
        }

        public String getServerFlag() {
            return serverFlag;
        }

        public void setServerFlag(String serverFlag) {
            this.serverFlag = serverFlag;
        }

        public String getLastForce() {
            return lastForce;
        }

        public void setLastForce(String lastForce) {
            this.lastForce = lastForce;
        }

        public String getUpdateurl() {
            return updateurl;
        }

        public void setUpdateurl(String updateurl) {
            this.updateurl = updateurl;
        }

        public String getUpgradeinfo() {
            return upgradeinfo;
        }

        public void setUpgradeinfo(String upgradeinfo) {
            this.upgradeinfo = upgradeinfo;
        }
    }

    public UpdateInfo getData() {
        return data;
    }

    public void setData(UpdateInfo data) {
        this.data = data;
    }

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
