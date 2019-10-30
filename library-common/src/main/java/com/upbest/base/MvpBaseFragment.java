package com.upbest.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * <pre>
 * 文件名：	MvpBaseFragment
 * 作　者：	gykang
 * 时　间：	2018/3/14 15:38
 * 描　述：   MVPFragment通用父类
 *
 * </pre>
 */

public abstract class MvpBaseFragment<P extends BasePresenter> extends BaseFragment {

    protected P mPresenter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mPresenter = createPresenter();
        //presenter取得与界面的联系
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroy() {
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
}
