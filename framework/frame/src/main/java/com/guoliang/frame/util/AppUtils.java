package com.guoliang.frame.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @Description:
 * @Author: zhangguoliang
 * @CreateTime: 2020/5/27 10:13
 */
public class AppUtils {

    /**

     * 获取应用程序名称

     */

    public static synchronized String getAppName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            int labelRes = packageInfo.applicationInfo.labelRes;

            return context.getResources().getString(labelRes);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }



    /**

     * [获取应用程序版本名称信息]

     * @param context

     * @return 当前应用的版本名称

     */

    public static synchronized String getVersionName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.versionName;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return "";

    }





    /**

     * [获取应用程序版本名称信息]

     * @param context

     * @return 当前应用的版本名称

     */

    public static synchronized int getVersionCode(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return (int) packageInfo.versionCode;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;

    }





    /**

     * [获取应用程序版本名称信息]

     * @param context

     * @return 当前应用的版本名称

     */

    public static synchronized String getPackageName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.packageName;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return "";

    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }



    /**

     * 获取图标 bitmap

     * @param context

     */

    public static synchronized Bitmap getBitmap(Context context) {

        PackageManager packageManager = null;

        ApplicationInfo applicationInfo = null;

        try {

            packageManager = context.getApplicationContext()

                    .getPackageManager();

            applicationInfo = packageManager.getApplicationInfo(

                    context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;

        }

        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable

        BitmapDrawable bd = (BitmapDrawable) d;


        return bd.getBitmap();

    }

    /**
     * 获取手机IMEI号
     *
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(@NotNull Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public static void showInput(Context context,final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
