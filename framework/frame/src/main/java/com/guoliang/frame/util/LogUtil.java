package com.guoliang.frame.util;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: log工具类
 * @Author: zhangguoliang
 * @CreateTime: 2020/5/26 18:05
 */
public class LogUtil {


    private static boolean IS_DEBUG= true;
    public static void v(String TAG, String msg) {
        if (IS_DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (IS_DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (IS_DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (IS_DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (IS_DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void setIsDebug(boolean isDebug) {
        IS_DEBUG = isDebug;
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }
}
