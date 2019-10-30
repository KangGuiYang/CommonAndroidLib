package com.upbest.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.upbest.rxlibrary.R;

/**
 * 底部 弹出的操作栏dialog
 *
 * @author
 */
public class MapAppMenuDialog extends Dialog implements View.OnClickListener {

    private Button mBtnGd;
    private Button mBtnTx;
    private Button mBtnBaiDu;
    private Button cancelBtn;

    private View.OnClickListener gdListener;
    private View.OnClickListener cancelListener;
    private View.OnClickListener txListener;
    private View.OnClickListener baiduListener;


    /**
     * @param context
     */
    public MapAppMenuDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    /**
     * @param context
     * @param theme
     */
    public MapAppMenuDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map_dialog_bottom);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //设置动画
        window.setWindowAnimations(R.style.dialogWindowAnim);

        mBtnBaiDu = (Button) findViewById(R.id.btn_map_baidu);
        mBtnGd = (Button) findViewById(R.id.btn_map_gao_de);
        mBtnTx = (Button) findViewById(R.id.btn_map_tx);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(this);
        mBtnGd.setOnClickListener(this);
        mBtnBaiDu.setOnClickListener(this);
        mBtnTx.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_map_gao_de) {
            if (gdListener != null) {
                gdListener.onClick(v);
            }
            return;
        }
        if (id == R.id.btn_map_baidu) {
            if (baiduListener != null) {
                baiduListener.onClick(v);
            }
            return;
        }
        if (id == R.id.btn_map_tx) {
            if (txListener != null) {
                txListener.onClick(v);
            }
            return;
        }
        if (id == R.id.cancelBtn) {
            if (cancelListener != null) {
                cancelListener.onClick(v);
            }
            dismiss();
            return;
        }
    }

    public void setGdListener(View.OnClickListener gdListener) {
        this.gdListener = gdListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setTxListener(View.OnClickListener txListener) {
        this.txListener = txListener;
    }

    public void setBaiduListener(View.OnClickListener baiduListener) {
        this.baiduListener = baiduListener;
    }
}