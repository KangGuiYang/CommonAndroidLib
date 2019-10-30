package com.upbest.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.upbest.entity.EventCenter;
import com.upbest.util.LogUtil;
import com.upbest.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 父类Fragment
 *
 * @author
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "Base";

    /**
     * context
     */
    protected Context mContext = null;

    private boolean isFirstResume = true;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;
    public View mRootView;

    /**
     * 管理RxJava2生命周期
     */
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Unbinder mUnBinder;

    private Bundle savedInstanceState;

    /**
     * 当EventBus回调
     *
     * @param eventCenter
     */
    protected abstract void onEventComing(EventCenter eventCenter);

    /**
     * 设置Fragment布局界面
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 是否注册EventBus
     *
     * @return
     */
    protected abstract boolean isBindEventBusHere();

    /**
     * 懒加载用 (第一次用户可见)
     */
    protected abstract void onFirstUserVisible();

    /**
     * 用户可见(onResume)
     */
    protected abstract void onUserVisible();

    /**
     * 第一次用户不可见 不操作
     */
    private void onFirstUserInvisible() {
    }

    /**
     * 当用户不可见时(onPause)
     */
    protected abstract void onUserInvisible();

    /**
     * 初始化布局以及监听事件
     */
    protected abstract void initViews();


    /**
     * 初始化业务操作
     */
    protected abstract void initData();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断是否注册EventBus
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 根View 赋值
        mRootView = view;
        if (mRootView != null) {
            mUnBinder = ButterKnife.bind(this, mRootView);
        }
        this.savedInstanceState = savedInstanceState;
        // 获取宽高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        initViews();
        initData();
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 设置Fragment布局
        if (getContentViewLayoutID() != 0) {
            return inflater.inflate(getContentViewLayoutID(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initPrepare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }


    protected FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }


    public void addCompositeDisposable(Disposable disposable) {
        if (disposable != null) {
            compositeDisposable.add(disposable);
        }
    }

    /**
     * 停止RxJava2事件订阅
     *
     * @param disposable
     */
    public void stopCompositeDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 如果注册 则反注册
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        // 解除RxJava2的订阅
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    protected void showToast(String message) {
        ToastUtil.showShotToast(mContext.getApplicationContext(), message);
    }

    protected void showToast(int msgId) {
        ToastUtil.showShotToast(mContext.getApplicationContext(), msgId);
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    /**
     * EventBus回调
     *
     * @param eventCenter
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            LogUtil.i(TAG, "onEventMainThread() : " + "界面：" + this.getClass().getSimpleName()
                    + "有EventBus数据回调 code = " + eventCenter.getEventCode());
            onEventComing(eventCenter);
        }
    }
}
