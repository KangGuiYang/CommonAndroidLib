package com.upbest.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upbest.rxlibrary.R;
import com.upbest.util.GsonUtil;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * <pre>
 * 文件名：	MvpBaseActivity
 * 作　者：	k
 * 时　间：	2018/3/13 11:54
 * 描　述：
 * </pre>
 *
 * @author kang gui yang
 */
public abstract class MvpBaseActivity<P extends BasePresenter> extends BaseAppCompatActivity {

    protected RelativeLayout mLlTitleBarLayout;
    protected P mPresenter;
    protected LinearLayout mLlMoreLayout;
    protected TextView mTvTitle, mTvRightText;
    protected ImageView mIvMoreRight;
    protected LinearLayout mLlLeft;

    @Override
    protected void onCreate(Bundle arg) {
        mPresenter = createPresenter();
        //presenter取得与界面的联系
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initTitle();
        super.onCreate(arg);

    }

    /**
     * 初始化标题栏
     *
     * @param rightVisible
     * @param title
     */
    public void setUpTitleBar(boolean rightVisible, String title) {
        mLlTitleBarLayout = findViewById(R.id.titleBar);
        mLlMoreLayout = findViewById(R.id.more_lay);
        mTvTitle = findViewById(R.id.tv_title);
        mIvMoreRight = findViewById(R.id.more_img);
        mLlLeft = findViewById(R.id.ll_left);
        mTvRightText = findViewById(R.id.tv_right_text);
        if (rightVisible) {
            mLlMoreLayout.setVisibility(View.VISIBLE);
        } else {
            mLlMoreLayout.setVisibility(View.INVISIBLE);
        }
        if (title != null) {
            mTvTitle.setText(title);
        }

    }

    /**
     * 设置标题栏右边图片
     *
     * @param drawableId
     */
    protected void setRightImg(int drawableId) {
        if (mLlMoreLayout != null) {
            mLlMoreLayout.setVisibility(View.VISIBLE);
        }
        if (mIvMoreRight != null) {
            mIvMoreRight.setImageDrawable(getResources().getDrawable(drawableId));
        }
    }


    /**
     * 注册标题栏的返回事件.只有用了include的界面才可以使用.否则抛出异常.
     */
    protected void registerBackClickEvent() {
        findViewById(R.id.ll_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 通用标题栏，按钮点击效果处理
     */
    protected void initTitle() {
        final RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.titleBar);
        if (titleBar != null) {
            titleBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    titleBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LinearLayout ll_left = (LinearLayout) findViewById(R.id.ll_left);
                    LinearLayout more_lay = (LinearLayout) findViewById(R.id.more_lay);

                    // 设置左侧按钮宽高
                    RelativeLayout.LayoutParams lpLeft = (RelativeLayout.LayoutParams) ll_left.getLayoutParams();
                    lpLeft.width = titleBar.getHeight();
                    lpLeft.height = lpLeft.width;
                    ll_left.setLayoutParams(lpLeft);

                    // 设置右侧按钮宽高
                    RelativeLayout.LayoutParams lpMore = (RelativeLayout.LayoutParams) more_lay.getLayoutParams();
                    lpMore.width = titleBar.getHeight();
                    lpMore.height = lpMore.width;
                    more_lay.setLayoutParams(lpMore);
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //presenter断开与界面的联系
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 创建Presenter
     *
     * @return
     */
    protected abstract P createPresenter();

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {

        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(300);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {

        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(300);
        return mHiddenAction;
    }


    /**
     * 提交Post接口 HASH_MAP - JSON
     *
     * @param commitHashMap
     * @return
     */
    public RequestBody getRequestBody2Json(HashMap<String, Object> commitHashMap) {
        String json = GsonUtil.parseBeanToJson(commitHashMap);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }


}