package com.jsdroid.test;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.UiMessageUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.jsdroid.runner.JsDroidApplication;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class JsdApp extends JsDroidApplication {
    private String scriptFile;
    private boolean volumeControl;
    private boolean showFloatView;

    @Override
    public void onCreate() {
        super.onCreate();
        readFloatWindowState();
        readVolumeControlState();
        FloatLogo.getInstance().init(this);
        FloatMenu.getInstance().init(this);
        if (showFloatView) {
            FloatLogo.getInstance().show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FloatLogo.getInstance().onConfigureChanged();
        FloatMenu.getInstance().hide();
    }

    @Override
    public void onJsDroidConnected() {
        super.onJsDroidConnected();
        UiMessageUtils.getInstance().send(UiMessage.JSDROID_CONNECT);
        JsdLog.print("服务连接");
        UiMessageUtils.getInstance().send(UiMessage.PRINT, "服务连接.");
        //判断是否运行
        if (isRunning()) {
            UiMessageUtils.getInstance().send(UiMessage.SRIPT_HAS_START);
        }
    }

    public void print(String text) {
        JsdLog.print(text + "\n");
        UiMessageUtils.getInstance().send(UiMessage.PRINT, text + "\n");
    }

    @Override
    public void onScriptStart() {
        super.onScriptStart();
        UiMessageUtils.getInstance().send(UiMessage.SRIPT_START);
    }

    @Override
    public void onScriptStop(String result) {
        super.onScriptStop(result);
        if (result != null) {
            JsdLog.print(result);
            UiMessageUtils.getInstance().send(UiMessage.PRINT, result);
            UiMessageUtils.getInstance().send(UiMessage.SRIPT_STOP, result);
        } else {
            UiMessageUtils.getInstance().send(UiMessage.SRIPT_STOP);
        }
    }

    @Override
    public void onJsDroidServerStop() {
        super.onJsDroidServerStop();
    }

    @Override
    public void onJsDroidDisconnected() {
        super.onJsDroidDisconnected();
        UiMessageUtils.getInstance().send(UiMessage.JSDROID_DISCONNECT);
        JsdLog.print("服务中断脚本停止");
        UiMessageUtils.getInstance().send(UiMessage.PRINT, "服务中断脚本停止");
        UiMessageUtils.getInstance().send(UiMessage.SRIPT_STOP, "服务中断脚本停止");
    }

    @Override
    public void onWaitAdbPort() {
        super.onWaitAdbPort();
        JsdLog.print("等待免root服务.");
        UiMessageUtils.getInstance().send(UiMessage.PRINT, "等待免root服务.");
    }

    @Override
    public void onScriptPrint(String text) {
        super.onScriptPrint(text);
        JsdLog.print(text);
        UiMessageUtils.getInstance().send(UiMessage.PRINT, text);
    }

    private void readFloatWindowState() {
        showFloatView = Boolean.parseBoolean(readConfig("jsd_float_menu", "false"));
    }

    private void readVolumeControlState() {
        volumeControl = Boolean.parseBoolean(readConfig("jsd_volume_control", "false"));
    }

    public void switchFloatWindowState(boolean state) {
        showFloatView = state;
        saveConfig("jsd_float_menu", Boolean.toString(state));
        if (state) {
            FloatLogo.getInstance().show();
        } else {
            FloatLogo.getInstance().hide();
            FloatMenu.getInstance().hide();
        }
    }


    public void switchVolumeControlState(boolean state) {
        volumeControl = state;
        saveConfig("jsd_volume_control", Boolean.toString(state));
    }

    @Override
    public void onVolumeDown(boolean running) {
        super.onVolumeDown(running);
        if (volumeControl) {
            if (running) {
                stopScript();
            } else {
                startScript();
            }
        }
    }

    @Override
    public void onLoadScript(final String file) {
        super.onLoadScript(file);
        scriptFile = file;
        ThreadUtils.executeByCached(new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                FileUtils.deleteAllInDir(getScriptDir());
                ZipUtils.unzipFile(new File(file), getScriptDir());
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                UiMessageUtils.getInstance().send(UiMessage.LOAD_SCRIPT, file);
                UiMessageUtils.getInstance().send(UiMessage.OPTION_CHANGED, file);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {
                Log.e("JsDroid", "onFail: ", t);
            }
        });
    }

    public File getScriptDir() {
        return getDir("script_dir", 0);
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public boolean isShowFloatView() {
        return showFloatView;
    }

    public boolean isVolumeControl() {
        return volumeControl;
    }

    @JavascriptInterface
    @Override
    public void startScript() {
        super.startScript();
    }

    @JavascriptInterface
    @Override
    public void stopScript() {
        super.stopScript();
    }

    @JavascriptInterface
    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @JavascriptInterface
    public void showFloatMenu() {
        Log.d("JsDroid", "showFloatMenu: ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switchFloatWindowState(true);
            }
        });

    }

    @JavascriptInterface
    public void hideFloatMenu() {
        Log.d("JsDroid", "hideFloatMenu: ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switchFloatWindowState(false);
            }
        });
    }

}
