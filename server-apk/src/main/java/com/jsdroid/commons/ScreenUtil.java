package com.jsdroid.commons;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Surface;

import com.jsdroid.api.annotations.MethodDoc;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.events.Events;


public class ScreenUtil {


    public static float getScale(float MAX_WIDTH) {
        int naturalWidth = getNaturalWidth();
        int naturalHeight = getNaturalHeight();
        if (naturalHeight > naturalWidth) {
            return MAX_WIDTH / naturalHeight;
        } else {
            return MAX_WIDTH / naturalWidth;

        }
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param rotation {@link Surface}
     * @return
     */
    public static Bitmap rotate(Bitmap bitmap, int rotation) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int screenshotWidth;
        int screenshotHeight;
        float degree;
        switch (rotation) {

            default: {
                screenshotWidth = imageWidth;
                screenshotHeight = imageHeight;
                degree = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degree = 360f - 90f;
                screenshotWidth = imageHeight;
                screenshotHeight = imageWidth;
                break;
            }
            case Surface.ROTATION_180: {
                degree = 360f - 180f;
                screenshotWidth = imageWidth;
                screenshotHeight = imageHeight;
                break;
            }
            case Surface.ROTATION_270: {
                degree = 360f - 270f;
                screenshotWidth = imageHeight;
                screenshotHeight = imageWidth;
            }
        }

        if (rotation != Surface.ROTATION_0) {
            Bitmap unrotatedScreenShot = Bitmap.createBitmap(screenshotWidth,
                    screenshotHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(unrotatedScreenShot);
            canvas.translate(screenshotWidth / 2,
                    screenshotHeight / 2);
            canvas.rotate(degree);
            canvas.translate(-imageWidth / 2, -imageHeight / 2);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.setBitmap(null);
            bitmap.recycle();
            bitmap = unrotatedScreenShot;
        }
        return bitmap;
    }


    public static int getNaturalWidth() {
        return Devices.getInstance().getNaturalWidth();
    }

    public static int getNaturalHeight() {
        return Devices.getInstance().getNaturalHeight();
    }

    public static int getDpi() {
        return Devices.getInstance().getDpi();
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getScreenHeight(Context context) {
//        if (sPoint == null) {
//            sPoint = new Point();
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            wm.getDefaultDisplay().getSize(sPoint);
//        }
//        return sPoint.y;
        //屏幕方向变化大小也会变化
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getRotation() {
        return Devices.getInstance().getRotation();
    }

    public static void unlock(Context context) {
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

    public static void wakeup(Context context) {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    @MethodDoc("锁定屏幕")
    public static void lockDevice(Context context) {
        if (isScreenOn(context)) {
            Events.getInstance().pressKeyCode(KeyEvent.KEYCODE_POWER);
        }
    }

    /**
     * 解锁屏幕
     */
    @MethodDoc("解锁屏幕")
    public static void unlockDevice(Context context) {
        try {
            wakeup(context);
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.newKeyguardLock("unLock").disableKeyguard();
        } catch (Throwable e) {
        }

    }
}
