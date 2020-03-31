package com.jsdroid.sdk.devices;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityThread;
import android.app.Service;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Build;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.jsdroid.commons.ContextUtil;
import com.jsdroid.commons.ScreenUtil;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.nodes.UiAutomationService;

public class Devices {
    private static class Single {
        private static Devices instance = new Devices();
    }

    private WindowManager windowManager;
    private PowerManager powerManager;

    private Devices() {
        Context context = getSystemContext();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        powerManager = (PowerManager) context.getSystemService(Service.POWER_SERVICE);
    }

    public static Devices getInstance() {
        return Single.instance;
    }


    public int getWidth() {
        return getDefaultDisplay().getWidth();
    }

    public int getHeight() {
        return getDefaultDisplay().getHeight();
    }

    public int getDpi() {
        DisplayMetrics metrics = new DisplayMetrics();
        getDefaultDisplay().getRealMetrics(metrics);
        return metrics.densityDpi;
    }

    public Display getDefaultDisplay() {
        return windowManager.getDefaultDisplay();
    }

    private Context getSystemContext() {
        return ActivityThread.currentActivityThread().getSystemContext();
    }

    public int getRotation() {
        return getDefaultDisplay().getRotation();
    }

    public int getNaturalWidth() {
        return getDisplayInfo().getNaturalWidth();
    }

    public int getNaturalHeight() {
        return getDisplayInfo().getNaturalHeight();
    }

    public DisplayInfo getDisplayInfo() {
        return DisplayManagerGlobal.getInstance().getDisplayInfo(Display.DEFAULT_DISPLAY);
    }

    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public String getVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getBrand() {
        return Build.BRAND;
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getAbi() {
        return Build.CPU_ABI;
    }

    public String getProductName() {
        return Build.PRODUCT;
    }

    public boolean isScreenOn() {
        return powerManager.isScreenOn();
    }

    public boolean wakeDevice() throws RemoteException {
        if (!isScreenOn()) {
            Events.getInstance().pressKeyCode(KeyEvent.KEYCODE_POWER);
            return true;
        }
        return false;
    }

    public boolean setRotation(int rotation) {
        return UiAutomationService.getInstance().setRotation(rotation);
    }

    public boolean pressMenu() {
        return Events.getInstance().pressMenu();
    }


    public boolean pressBack() {
        return Events.getInstance().pressBack();
    }

    public boolean pressHome() {
        return Events.getInstance().pressHome();
    }

    public boolean pressSearch() {
        return pressKeyCode(KeyEvent.KEYCODE_SEARCH);
    }

    public boolean pressDPadCenter() {
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_CENTER);
    }

    public boolean pressDPadDown() {
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
    }

    public boolean pressDPadUp() {
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_UP);
    }

    public boolean pressDPadLeft() {
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
    }

    public boolean pressDPadRight() {
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
    }

    public boolean pressDelete() {
        return pressKeyCode(KeyEvent.KEYCODE_DEL);
    }

    public boolean pressEnter() {
        return pressKeyCode(KeyEvent.KEYCODE_ENTER);
    }

    public boolean pressKeyCode(int keyCode) {
        return Events.getInstance().pressKeyCode(keyCode);
    }


    public boolean pressRecentApps() throws RemoteException {
        return Events.getInstance().toggleRecentApps();
    }

    public boolean openNotification() {
        return Events.getInstance().openNotification();
    }

    public boolean openQuickSettings() {
        return Events.getInstance().openQuickSettings();
    }

    public int getDisplayWidth() {
        try {
            return ScreenUtil.getScreenWidth(ContextUtil.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Point getDisplayRealSize() {
        Display display = Devices.getInstance().getDefaultDisplay();
        Point p = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(p);
        }
        return p;
    }

    public int getDisplayHeight() {
        try {
            return ScreenUtil.getScreenHeight(ContextUtil.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean click(int x, int y) {
        return Events.getInstance().tap(x, y);
    }


    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {

        return Events.getInstance().swipe(startX, startY, endX, endY, steps);
    }

    public String getCurrentActivityName() {
        return null;
    }

    public String getCurrentPackageName() {
        try {
            return Nodes.getInstance().getUiAutomation().getRootInActiveWindow().getPackageName().toString();
        } catch (Exception e) {
        }
        return "";
    }

    public void freezeRotation() {
        setRotation(UiAutomation.ROTATION_FREEZE_CURRENT);
    }

    public void unfreezeRotation() {
        setRotation(UiAutomation.ROTATION_UNFREEZE);
    }
    public boolean sleepDevice() throws RemoteException {
        if (isScreenOn()) {
            Events.getInstance().pressKeyCode(KeyEvent.KEYCODE_POWER);
            return true;
        }
        return false;
    }
    public boolean toggleRecentApps() {
        return Events.getInstance().toggleRecentApps();
    }
    
}
