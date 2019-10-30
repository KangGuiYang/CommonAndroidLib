package com.upbest.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.upbest.rxlibrary.R;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * @author 图片预览界面
 */
public class ImageScaleActivity extends Activity {

    private static final String BUNDLE_KEY_URL_LIST = "URL_LIST";

    private static final String BUNDLE_KET_IMG_POSITION = "IMG_POSITION";

    /**
     * 滑动viewPager
     */
    private PreviewViewPager viewPager;
    /**
     * 显示当前页面数字
     */
    private TextView pageNumber;

    private ImageView mIvBack;

    /**
     * 传入的Img List
     */
    private ArrayList<String> urlList;

    /**
     * 传入的当前选中的index
     */
    private int currentPosition;

    protected int mPreviousPos = -1;

    private ImageScaleAdapter mAdapter;

    /**
     * 跳转传值
     *
     * @param context
     * @param urlList  当前图片集合
     * @param position 当前图片在集合中选中的位置
     */
    public static void startPreviewActivity(Context context, ArrayList<String> urlList, int position) {
        Intent intent = new Intent(context, ImageScaleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_URL_LIST, urlList);
        bundle.putInt(BUNDLE_KET_IMG_POSITION, position);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scale);

        viewPager = this.findViewById(R.id.viewPager);
        pageNumber = this.findViewById(R.id.page_number);
        mIvBack = this.findViewById(R.id.image_scale_back);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //1.获取数据
        if (getIntent().getExtras() != null) {
            urlList = (ArrayList<String>) getIntent().getExtras().getSerializable(BUNDLE_KEY_URL_LIST);
            currentPosition = getIntent().getExtras().getInt(BUNDLE_KET_IMG_POSITION);
        }

        mAdapter = new ImageScaleAdapter(this, urlList);
        viewPager.setAdapter(mAdapter);

        if (urlList != null) {
            pageNumber.setText(1 + "/" + urlList.size());
        }
        //设置viewPager监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageNumber.setText((position + 1) + "/" + urlList.size());
                if (mPreviousPos != -1 && mPreviousPos != position) {
                    ImageViewTouch iv = (ImageViewTouch) mAdapter.instantiateItem(viewPager, mPreviousPos);
                    if (iv != null) {
                        iv.resetMatrix();
                    }
                }
                mPreviousPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(currentPosition);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
