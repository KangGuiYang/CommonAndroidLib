package com.upbest.base;


import com.upbest.rxlibrary.RxLife;
import com.upbest.util.GsonUtil;
import com.upbest.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * <pre>
 * 文件名：	BasePresenter
 * 作　者：	jqli
 * 时　间：	2018/3/13 11:34
 * 描　述：
 * @author
 * </pre>
 */
public class BasePresenter<V> {

    private static final String TAG = BasePresenter.class.getSimpleName();

    public RequestBody getRequestBody2Json(HashMap<String, Object> commitHashMap) {
        String json = GsonUtil.parseBeanToJson(commitHashMap);
        LogUtil.d(TAG, "[parseJson] : " + json);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    protected WeakReference<V> mViewRef;
    private RxLife rxLife;

    /**
     * 界面创建，Presenter与界面取得联系（创建View的弱引用）
     *
     * @param view
     */
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
        rxLife = RxLife.create();
    }

    /**
     * 获取界面的引用
     *
     * @return
     */
    public V getView() {
        return mViewRef == null ? null : mViewRef.get();
    }

    public RxLife getRxLife() {
        return rxLife;
    }

    public void addToRxLife(Disposable disposable) {
        if (rxLife != null) {
            rxLife.add(disposable);
        }
    }

    /**
     * 判断界面是否销毁
     *
     * @return
     */
    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 界面销毁，Presenter与界面断开联系
     */
    public void detachView() {
        LogUtil.d(TAG, "[BasePresenter detachView]");
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        rxLife.destroy();
        rxLife = null;
    }
}