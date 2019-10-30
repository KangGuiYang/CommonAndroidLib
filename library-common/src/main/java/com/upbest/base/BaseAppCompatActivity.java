package com.upbest.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.upbest.entity.EventCenter;
import com.upbest.util.BaseAppManager;
import com.upbest.util.LogUtil;
import com.upbest.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 项目通用父类
 *
 * @author
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = "Base";

    /**
     * 上下文对象
     */
    protected Context mContext = null;

    private Unbinder mUnBinder;

    private Bundle savedInstanceState;
    /**
     * 管理RxJava2生命周期
     */
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * 是否绑定EventBus
     *
     * @return
     */
    protected abstract boolean isBindEventBusHere();

    /**
     * 设置布局ID
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 初始化布局以及监听事件
     */
    protected abstract void initViews();

    /**
     * 初始化业务操作
     */
    protected abstract void initData();

    /**
     * EventBus 回调方法
     *
     * @param eventCenter
     */
    protected abstract void onEventComing(EventCenter eventCenter);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "[onCreate()] : " + this.getClass().getName());
        this.savedInstanceState = savedInstanceState;
        //设置透明状态栏
        //fullScreen(BaseAppCompatActivity.this);
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        BaseAppManager.getInstance().addActivity(this);
        //是否绑定了EventBus
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        mUnBinder = ButterKnife.bind(this);
        initViews();
        initData();

    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }


    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                //window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                //attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    protected void addCompositeDisposable(Disposable disposable) {
        if (disposable != null) {
            compositeDisposable.add(disposable);
        }
    }

    /**
     * 停止RxJava2事件订阅
     *
     * @param disposable
     */
    protected void stopCompositeDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    /**
     * 当前Activity Or Fragment 注册 如有EventBus回调 则会回调到此方法
     *
     * @param eventCenter
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            LogUtil.i(TAG, "[onEventMainThread()] : " + "界面：" + this.getLocalClassName()
                    + "有EventBus数据回调 code = " + eventCenter.getEventCode());
            onEventComing(eventCenter);
        }
    }

    @Override
    public void finish() {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果当前Activity注册了EventBus 则取消注册
        LogUtil.i(TAG, "[onDestroy()]");
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        // 解除黄油刀
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        //  解除RxJava2的订阅
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
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
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
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


}
