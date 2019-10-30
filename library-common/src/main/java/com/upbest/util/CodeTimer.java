package com.upbest.util;

import android.os.Handler;
import android.widget.TextView;

/**
 * 倒计时的CodeTimer
 *
 * @author k
 */
public class CodeTimer {
    private int time = 60;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView codeTv;

    public CodeTimer(TextView codeTv) {
        super();
        this.codeTv = codeTv;
        initTimer();
    }

    private void initTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                time--;
                if (time == 0) {
                    timerHandler.removeCallbacks(timerRunnable);
                    codeTv.setEnabled(true);
                    time = 60;
                    codeTv.setText("获取验证码");
                } else {
                    codeTv.setText(time + "秒后重新获取");
                    timerHandler.postDelayed(timerRunnable, 1000);
                }
            }
        };
    }

    public void startTimer() {
        codeTv.setEnabled(false);
        codeTv.setText(time + "秒后重新获取");
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    public void stopTimer() {
        time = 60;
        codeTv.setEnabled(true);
        codeTv.setText("获取手机验证码");
        timerHandler.removeCallbacks(timerRunnable);
    }

}
