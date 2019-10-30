package com.upbest.retrofit;

import android.text.TextUtils;

import com.upbest.Constant;
import com.upbest.rxlibrary.ApiException;
import com.upbest.util.GsonUtil;
import com.upbest.util.LogUtil;
import com.upbest.util.PreUtils;
import com.upbest.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.upbest.Constant.BASE_URL;

/**
 * <pre>
 * 文件名：	InterceptorUtil
 * 作　者：	gykang
 * 时　间：	2018/7/19 10:47
 * 描　述：  Retrofit2 拦截器工具类
 * @author kang gui yang
 * </pre>
 */
public class InterceptorUtil {

    private static final String TAG = InterceptorUtil.class.getSimpleName();
    private static String token;

    /**
     * 日志拦截器
     *
     * @return
     */
    public static HttpLoggingInterceptor logInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            try {
                LogUtil.e(TAG, message);
            } catch (Exception e) {
                LogUtil.e(TAG, "[拦截异常了] : " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * BaseUrl拦截器
     */
    public static class HostInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            //重新赋值
            HttpUrl newHost = HttpUrl.parse(BASE_URL);
            LogUtil.e(TAG, "------------------------------Host拦截器------------------------------");
            LogUtil.e(TAG, "[ROOT_URL] : " + newHost);
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();

            HttpUrl oldHttpUrl = request.url();
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newHost.scheme())
                    .host(newHost.host())
                    .port(newHost.port())
                    .build();
            LogUtil.e(TAG, "[拦截转url] : " + newFullUrl.url().toString());
            return chain.proceed(builder.url(newFullUrl).build());
        }
    }


    /**
     * Header拦截器 token拦截器
     * 第一次进去默认 token Basic dXBiZXN0OnVwYmVzdA== (有空格)
     *
     * @return
     */
    public static Interceptor headerInterceptor() {
        return chain -> {
            //在这里你可以做一些想做的事,比如token失效时,重新获取token
            LogUtil.e(TAG, "----------header拦截器------------------------------");
            LogUtil.d(TAG, "[headerInterceptor  intercept]");
            token = PreUtils.getString(Utils.getAppContext(), PreUtils.ACCESS_TOKEN_KEY,
                    "");
            if (TextUtils.isEmpty(token)) {
                Request mRequest =
                        chain.request()
                                .newBuilder()
                                .build();
                Response response = chain.proceed(mRequest);
                return response;
            }

            Request mRequest =
                    chain.request()
                            .newBuilder()
                            .addHeader("Authorization", token)
                            .build();
            LogUtil.d(TAG, "[拦截器中获取到的token] : " + token);
            Response response = chain.proceed(mRequest);

            if (isTokenExpired(response)) {
                // 拿到新的token
                String newToken = getNewToken();
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", newToken)
                        .build();
                return chain.proceed(newRequest);
            }

            return response;
        };

    }

    /**
     * 根据Response判断Token是否失效
     * 401 表示已过期
     *
     * @param response
     * @return
     */
    private static boolean isTokenExpired(Response response) {
        LogUtil.d(TAG, "[token code ] = " + response.code());
        if (response.code() == ApiException.REQUEST_TOKEN_FAILED) {
            return true;
        }
        return false;

    }

    /**
     * 获取新的token
     *
     * @return
     * @throws IOException
     */
    private static String getNewToken() throws IOException {
        PreUtils.putString(Utils.getAppContext(), PreUtils.ACCESS_TOKEN_KEY, "");
        Map<String, String> map = new HashMap<>();
        String refreshToken = PreUtils.getString(Utils.getAppContext(), PreUtils.REFRESH_ACCESS_TOKEN_KEY, Constant.BASIC_TOKEN);
        map.put("refresh_token", refreshToken);
        map.put("grant_type", "refresh_token");

        String requestJson = GsonUtil.parseBeanToJson(map);
        LogUtil.d(TAG, "[获取新的token传参Json] = " + requestJson);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GsonUtil.parseBeanToJson(map));

        /*RxManager.getInstance().doSubscribe1(RetrofitHelper.getInstance().getApiServiceSingleStr().refreshToken(body),
                new HttpResultSubscriber<String>() {
                    @Override
                    protected void _onNext(String s) {
                        if (TextUtils.isEmpty(s)) {
                            return;
                        }
                        LogUtil.d(TAG, "[刷新token接口返回] : " + s);

                    }

                    @Override
                    protected void _onError(String message) {
                        // 错误码 就走 登录页面
                        return;
                    }
                }
        );*/

        LogUtil.d(TAG, "[刷新获取新的token返回] ： " + token);
        return token;
    }


}
