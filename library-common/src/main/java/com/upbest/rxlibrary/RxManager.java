package com.upbest.rxlibrary;


import android.text.TextUtils;

import com.upbest.entity.HttpResult;
import com.upbest.util.LogUtil;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 * 文件名：	RxManager
 * 作　者：	gykang
 * 时　间：	2018/5/29 15:03
 * 描　述：  RxJava2 封装
 * @author kang gui yang
 * </pre>
 */
public class RxManager {

    /**
     * 私有构造方法 禁止new
     */
    private RxManager() {
    }

    public static RxManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RxManager INSTANCE = new RxManager();
    }

    public <T> DisposableObserver<T> doSubscribe1(Observable<T> observable, DisposableObserver<T> observer) {
        return observable
                .compose(RxManager.<T>rxSchedulerHelper())
                .subscribeWith(observer);
    }

    /**
     * 订阅请求
     *
     * @param observable
     * @param observer
     * @param <T>
     */
    public <T> DisposableObserver<T> doSubscribe(Observable<HttpResult<T>> observable, DisposableObserver<T> observer) {
        return observable.compose(RxManager.<T>handleResult())
                .compose(RxManager.<T>rxSchedulerHelper())
                .subscribeWith(observer);
    }


    /**
     * 当业务接口多的时候  简化代码
     * 统一线程处理 compose 操作符 简化线程
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * FlowableTransformer 背压用
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> rxSchedulerHelperForFlowable() {

        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 处理请求结果
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<HttpResult<T>, T> handleResult() {
        return upstream -> upstream.flatMap((Function<HttpResult<T>, ObservableSource<T>>) tHttpResult -> {

            if (tHttpResult.getCode() == ApiException.REQUEST_OK) {
                // 返回对应数据 根据项目情况而定
                return Observable.create((ObservableOnSubscribe<T>) emitter -> {
                    emitter.onNext(tHttpResult.getData() == null ? (T) "null" : tHttpResult.getData());
                    emitter.onComplete();
                });
            } else {
                LogUtil.d("TEST","HANDLE_RESULT = " + tHttpResult.toString());
                if (!TextUtils.isEmpty(tHttpResult.getMsg())) {
                    return Observable.error(new ApiException(tHttpResult.getMsg()));
                } else {
                    return Observable.error(new ApiException(tHttpResult.getCode()));
                }

            }
        });
    }


}


