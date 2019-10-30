package com.upbest.retrofit;

import com.upbest.Constant;
import com.upbest.rxlibrary.BuildConfig;
import com.upbest.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by kGod on 2017/2/28.
 * Email 18252032703@163.com
 * Thank you for watching my code
 * Description: RetrofitHelper
 *
 * @author kang gui yang
 */

public class RetrofitHelper {

    /**
     * 默认连接时间 单位秒
     */
    private static final int DEFAULT_CONNECT_TIME = 40;
    /**
     * 默认读超时时间
     */
    private static final int DEFAULT_READ_TIME_OUT = 40;
    /**
     * 默认写超时时间
     */
    private static final int DEFAULT_WRITE_TIME_OUT = 40;

    private static Apis mApiService;

    /**
     * 返回纯String
     */
    private static Apis mApiServiceSingleStr;


    /**
     * 单例
     *
     * @return
     */
    public static RetrofitHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static class SingletonHolder {
        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    }

    /**
     * 私有构造方法
     */
    private RetrofitHelper() {
        mApiService = create();
        mApiServiceSingleStr = create1();
    }

    public Apis getApiService() {
        return mApiService;
    }

    public Apis getApiServiceSingleStr() {
        return mApiServiceSingleStr;
    }

    /**
     * 创建RetroFit 实例
     *
     * @return
     */
    private Apis create() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(Apis.class);
    }

    /**
     * 返回纯String
     *
     * @return
     */
    private Apis create1() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(Apis.class);
    }

    /**
     * Retrofit是基于OkHttpClient的，可以创建一个OkHttpClient进行一些配置
     * 拦截器的执行顺序是根据添加的顺序.
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {

        SSLInterceptor.SSLParams sslParams = null;
        try {
            sslParams = SSLInterceptor.getSslSocketFactory(new InputStream[]{Utils.
                    getAppContext().getAssets()
                    .open("www.patternstack.cn.pem")}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 配置超时拦截器
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 配置连接超时
        builder.connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS);
        // 写入超时
        builder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
        // 读取超时
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        // 添加日志拦截器
        if (BuildConfig.DEBUG) {
            // host拦截器
            builder.addInterceptor(new InterceptorUtil.HostInterceptor());
            builder.addNetworkInterceptor(InterceptorUtil.logInterceptor());
        }
        // 添加token拦截器
        builder.addInterceptor(InterceptorUtil.headerInterceptor());
        //SSL验证
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)  ;
        return builder.build();
    }


}
