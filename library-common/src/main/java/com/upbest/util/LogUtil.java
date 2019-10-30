package com.upbest.util;

import android.util.Log;

/**
 * <pre>
 * 文件名：	LogUtil.java
 * @author： kang gui yang
 * 描　述：	日志工具类（统一日志开关）
 * </pre>
 */
public class LogUtil {

    /**
     * 日志开关
     */
    private static boolean logFlag = true;

    public static void closeLog() {
        logFlag = false;
    }

    public static void openLog() {
        logFlag = true;
    }

    /**
     * Log.d(tag, msg);
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (logFlag) {
            if (null == msg) {
                Log.d(tag, "null");
            } else {
                Log.d(tag, msg);
            }
        }
    }

    /**
     * Log.i(tag, msg);
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (logFlag) {
            if (null == msg) {
                Log.i(tag, "null");
            } else {
                Log.i(tag, msg);
            }
        }
    }

    /**
     * Log.e(tag, msg);
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (logFlag) {
            if (null == msg) {
                Log.e(tag, "null");
            } else {
                Log.e(tag, msg);
            }
        }
    }

    /**
     * System.out.println
     *
     * @param tag
     * @param msg
     */
    public static void println(String tag, String msg) {
        if (logFlag) {
            if (null == msg) {
                System.out.println(tag + "  :  null");
            } else {
                System.out.println(tag + "  :  " + msg);
            }
        }
    }
}
