package com.losileeya.appupdate.retrofit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-27
 * Time: 15:27
 * 类描述：
 *
 * @version :
 */
public class ServiceFactory {
    private static final String BASEURL="http://192.168.191.1:8081/";
    public static <T> T createServiceFrom(final Class<T> serviceClass) {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .build();
        return adapter.create(serviceClass);
    }
}
