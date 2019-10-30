package com.upbest.preview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.upbest.util.ImageLoader;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;


/**
 * @author 查看大图适配器
 */
public class ImageScaleAdapter extends PagerAdapter {

    private ArrayList<String> urlList;
    private Context mContext;

    public ImageScaleAdapter(Context context, ArrayList<String> urlList) {
        super();
        this.mContext = context;
        this.urlList = urlList;
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageViewTouch imageView = new ImageViewTouch(mContext);
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        // 待优化
        String currentUrl = urlList.get(position);
        if (!TextUtils.isEmpty(currentUrl)) {
            ImageLoader.load(mContext, currentUrl, imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
