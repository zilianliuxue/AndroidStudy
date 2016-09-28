package com.losileeya.appupdate.retrofit;


import com.losileeya.appupdate.bean.UpdateAppInfo;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ApiService {
    //实际开发过程可能的接口方式
     @GET("update")
    Observable<UpdateAppInfo> getUpdateInfo(@Query("appname") String appname, @Query("serverVersion") String appVersion);
    //以下方便版本更新接口测试
    @GET("update")
    Observable<UpdateAppInfo> getUpdateInfo();
}
