package com.upbest.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


public class ImageLoader {


    public static void load(Context context, Drawable drawable, ImageView mIv) {
        Glide.with(context).load(drawable).into(mIv);
    }


    /**
     * 加载网络大图
     *
     * @param context
     * @param url
     * @param iv
     */
    public static void load(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(iv);
    }

    /**
     * 加载大图 无占位图
     *
     * @param context
     * @param url
     * @param iv
     */
    public static void loadBigImg(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(iv);
    }

    /**
     * 加载网络大图 带展位图
     *
     * @param context
     * @param url
     * @param iv
     */
    public static void load(Context context, String url, ImageView iv, int defaultImgId) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defaultImgId)
                .crossFade()
                .into(iv);
    }

    /**
     * 加载本地大图
     *
     * @param context
     * @param resId
     * @param iv
     */
    public static void load(Context context, int resId, ImageView iv) {
        Glide.with(context)
                .load(resId)
                .crossFade()
                .into(iv);
    }


    /**
     * 加载本地圆形图片
     *
     * @param context 上下文对象
     * @param resId   本地资源文件
     * @param iv      ImageView
     */
    public static void loadCircle(Context context, int resId, ImageView iv) {
        Glide.with(context)
                .load(resId)
                .crossFade()
                .transform(new CircleTransform(context))
                .into(iv);
    }

    /**
     * 加载网络圆形图片
     *
     * @param context
     * @param url
     * @param iv
     */
    public static void loadCircle(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .crossFade()
                .transform(new CircleTransform(context))
                .into(iv);
    }

    /**
     * 加载网络圆形图片
     *
     * @param context
     * @param url
     * @param iv
     * @param error
     * @param defaultImg
     */
    public static void loadCircle(Context context, String url, ImageView iv, int error, int defaultImg) {
        Glide.with(context)
                .load(url)
                .placeholder(defaultImg)
                .error(error)
                .crossFade()
                .transform(new CircleTransform(context))
                .into(iv);
    }

    /**
     * LinearLayout 等View设置背景
     *
     * @param activity
     * @param img
     * @param view
     */
    public static void loadImgToBackground(Activity activity, Object img, final View view) {
        Glide.with(activity)
                .load(img)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<?
                            super GlideDrawable> glideAnimation) {
                        view.setBackgroundDrawable(resource);
                    }
                });
    }

}
