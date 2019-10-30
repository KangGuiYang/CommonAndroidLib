package com.upbest.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * 本地化储存 偏好设置工具类
 *
 * @author kang gui yang
 */
public class PreUtils {

    /**
     * 用户登录信息
     */
    public static final String LOGIN_USER_INFO_KEY = "LOGIN_USER_INFO_KEY";

    /**
     * token效验
     */
    public static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";

    /**
     * 刷新token
     */
    public static final String REFRESH_ACCESS_TOKEN_KEY = "REFRESH_ACCESS_TOKEN_KEY";

    /**
     * 上次退出的时候存的 lat
     */
    public static final String LAST_LAT = "lastLat";
    /**
     * 上次退出的时候存的 lng
     */
    public static final String LAST_LNG = "lastLng";

    /**
     * 上下线
     */
    public static final String OFFLINE_ONLINE = "OFF_ONLINE";


    private static SharedPreferences getSharedPreferences(final Context context) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 判断是否是第一次启动APP
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isFirstTime(Context context, String key) {
        if (getBoolean(context, key, false)) {
            return false;
        } else {
            putBoolean(context, key, true);
            return true;
        }
    }

    public static boolean contains(Context context, String key) {
        return PreUtils.getSharedPreferences(context).contains(key);
    }

    public static int getInt(final Context context, final String key, final int defaultValue) {
        return PreUtils.getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static boolean putInt(final Context context, final String key, final int pValue) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        editor.putInt(key, pValue);

        return editor.commit();
    }

    public static long getLong(final Context context, final String key, final long defaultValue) {
        return PreUtils.getSharedPreferences(context).getLong(key, defaultValue);
    }

    public static Long getLong(final Context context, final String key, final Long defaultValue) {
        if (PreUtils.getSharedPreferences(context).contains(key)) {
            return PreUtils.getSharedPreferences(context).getLong(key, 0);
        } else {
            return null;
        }
    }


    public static boolean putLong(final Context context, final String key, final long pValue) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        editor.putLong(key, pValue);

        return editor.commit();
    }

    public static boolean getBoolean(final Context context, final String key, final boolean defaultValue) {
        return PreUtils.getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static boolean putBoolean(final Context context, final String key, final boolean pValue) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        editor.putBoolean(key, pValue);

        return editor.commit();
    }

    public static String getString(final Context context, final String key, final String defaultValue) {
        return PreUtils.getSharedPreferences(context).getString(key, defaultValue);
    }

    public static boolean putString(final Context context, final String key, final String pValue) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        editor.putString(key, pValue);

        return editor.commit();
    }


    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param objValue
     */
    public static void putObj(final Context context, final String key, final Object objValue) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(objValue);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));

            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T getObject(final Context context, String key, Class<T> clazz) {
        final SharedPreferences sp = PreUtils.getSharedPreferences(context);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static boolean remove(final Context context, final String key) {
        final SharedPreferences.Editor editor = PreUtils.getSharedPreferences(context).edit();

        editor.remove(key);

        return editor.commit();
    }

}