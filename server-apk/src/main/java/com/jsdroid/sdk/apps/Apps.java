package com.jsdroid.sdk.apps;

import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.commons.ShellUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Apps {
    private static Map<String, Apps> instanceMap = new HashMap<>();

    private static Apps runnerApp;

    public static Apps getInstance(String pkg) {
        return instanceMap.get(pkg);
    }

    //获取运行app
    public static Apps getRunnerApp() {
        return runnerApp;
    }

    public static Apps getDebugApp() {
        return getInstance("com.jsdroid.app");
    }

    public synchronized static void putApp(String pkg, IJsDroidApp app) {
        Apps apps = instanceMap.get(pkg);
        if (apps == null) {
            apps = new Apps(pkg, app);
        } else {
            apps.setApp(app);
        }
        String app_pkg = System.getenv("app_pkg");
        if (pkg.equals(app_pkg)) {
            runnerApp = apps;
            apps.isRunnerApp = true;
        }
        instanceMap.put(pkg, apps);
    }

    private String pkg;
    private IJsDroidApp app;
    private boolean isRunnerApp;

    public Apps(String pkg, IJsDroidApp app) {
        this.pkg = pkg;
        this.app = app;
    }

    public String getPkg() {
        return pkg;
    }

    public static void loadScript(String file) throws InterruptedException {
        Apps defaultApp = getDebugApp();
        if (defaultApp != null) {
            new File(file).setExecutable(true, false);
            new File(file).setReadable(true, false);
            new File(file).setWritable(true, false);
            defaultApp.app.loadScript(file);
        }
    }

    public String readConfig(String key, String defaultValue) {
        if ("jsd.exe".equals(pkg)) {
            Apps defaultApp = getDebugApp();
            if (defaultApp == null) {
                return defaultValue;
            }
            return defaultApp.readConfig(key, defaultValue);
        }
        try {
            return app.readConfig(key, defaultValue);
        } catch (InterruptedException e) {
        }
        return null;
    }

    public IInput getInput() throws InterruptedException {
        return app.getInput();
    }

    public void println() {
        doPrint("");
    }

    public void print(Object value) {
        doPrint("" + value);
    }

    public void toast(Object value) {
        try {
            app.toast("" + value);
        } catch (Exception e) {
            startAppService();
        }
    }

    //连接断开，重启app服务
    public void startAppService() {
        if (isRunnerApp) {
            ShellUtil.exec("am startservice -n " + pkg + "/com.jsdroid.runner.JsDroidService");
        }
    }


    private void doPrint(String text) {
        try {
            app.print(text);
        } catch (Exception e) {
            startAppService();
        }
        if ("jsd.exe".equals(pkg)) {
            Apps defaultApp = getDebugApp();
            if (defaultApp != null) {
                defaultApp.doPrint(text);
            }
        }
    }

    public void println(Object value) {
        doPrint("" + value);

    }

    public void printf(String format, Object value) {
        printf(format, value);
    }

    public void printf(String format, Object... values) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(out);
        ) {
            pw.printf(format, values);
            pw.flush();
            doPrint(out.toString());
        } catch (IOException e) {
        }
    }

    public IJsDroidApp getApp() {
        return app;
    }

    public void setApp(IJsDroidApp app) {
        this.app = app;
    }
}
