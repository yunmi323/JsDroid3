package com.jsdroid.script;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityThread;
import android.app.Application;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.genymobile.scrcpy.wrappers.ServiceManager;
import com.genymobile.scrcpy.wrappers.SurfaceControl;
import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.annotations.FieldName;
import com.jsdroid.api.annotations.MethodDoc;
import com.jsdroid.findimg.FindImg;
import com.jsdroid.findpic.FindPic;
import com.jsdroid.ipc.call.SyncRunnable;
import com.jsdroid.sdk.apps.Apps;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.directions.Directions;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.files.Files;
import com.jsdroid.sdk.gestures.Gestures;
import com.jsdroid.sdk.https.Https;
import com.jsdroid.sdk.libs.Libs;
import com.jsdroid.sdk.logs.Logs;
import com.jsdroid.sdk.nodes.Node;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.nodes.Store;
import com.jsdroid.sdk.nodes.UiAutomationService;
import com.jsdroid.sdk.points.Points;
import com.jsdroid.sdk.rects.Rects;
import com.jsdroid.sdk.screens.Screens;
import com.jsdroid.sdk.scripts.Scripts;
import com.jsdroid.sdk.shells.Shells;
import com.jsdroid.sdk.sockets.Sockets;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class JsDroidScript extends Script {
    private IJsDroidApp app;
    private String pkg;
    private Files files;

    static {
        setFetchNotImportantNodeEnable(true);
        setFetchWebNodeEnable(true);
    }

    @MethodDoc("获取UiAutomation,不知者不推荐使用")
    public static UiAutomation getUiAutomation() {
        try {
            Field uiAutomationField = UiAutomationService.class.getDeclaredField("uiAutomation");
            uiAutomationField.setAccessible(true);
            return (UiAutomation) uiAutomationField.get(UiAutomationService.getInstance());
        } catch (Exception e) {
        }
        return null;
    }

    @MethodDoc("设置是否获取不重要节点")
    public static void setFetchNotImportantNodeEnable(boolean enable) {
        if (enable) {
            addNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        } else {
            removeNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        }
    }


    @MethodDoc("设置是否开启web增强模式")
    public static void setFetchWebNodeEnable(boolean enable) {
        if (enable) {
            addNodeFlag(AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY);
        } else {
            removeNodeFlag(AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY);
        }
    }

    /**
     * @param flag
     * @see AccessibilityServiceInfo#FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
     * @see AccessibilityServiceInfo#FLAG_REQUEST_TOUCH_EXPLORATION_MODE
     * @see AccessibilityServiceInfo#FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
     * @see AccessibilityServiceInfo#FLAG_REQUEST_FILTER_KEY_EVENTS
     * @see AccessibilityServiceInfo#FLAG_REPORT_VIEW_IDS
     * @see AccessibilityServiceInfo#FLAG_RETRIEVE_INTERACTIVE_WINDOWS
     * @see AccessibilityServiceInfo#FLAG_ENABLE_ACCESSIBILITY_VOLUME
     * @see AccessibilityServiceInfo#FLAG_REQUEST_ACCESSIBILITY_BUTTON
     */
    @MethodDoc("添加节点标志")
    public static void addNodeFlag(int flag) {
        try {
            UiAutomation uiAutomation = getUiAutomation();
            assert uiAutomation != null;
            AccessibilityServiceInfo serviceInfo = uiAutomation.getServiceInfo();
            serviceInfo.flags |= flag;
            uiAutomation.setServiceInfo(serviceInfo);
        } catch (Throwable e) {
        }
    }

    @MethodDoc("删除节点标志")
    public static void removeNodeFlag(int flag) {
        try {
            UiAutomation uiAutomation = getUiAutomation();
            assert uiAutomation != null;
            AccessibilityServiceInfo serviceInfo = uiAutomation.getServiceInfo();
            serviceInfo.flags &= ~flag;
            uiAutomation.setServiceInfo(serviceInfo);
        } catch (Throwable e) {
        }
    }

    public JsDroidScript() {
        files = new Files(this);
    }

    public JsDroidScript(Binding binding) {
        super(binding);
        files = new Files(this);

    }

    public Object load(String name) throws InterruptedException {
        return Scripts.getInstance(pkg).load(this, name);
    }

    public Files getGFile() {
        return files;
    }

    public Logs getGLog() {
        return Logs.getInstance();
    }

    public Https getGHttp() {
        return Https.getInstance();
    }

    public Sockets getGSocket() {
        return Sockets.getInstance();
    }

    public Devices getGDevice() {
        return Devices.getInstance();
    }

    public Directions getGDirection() {
        return Directions.getInstance();
    }

    public Events getGEvent() {
        return Events.getInstance();
    }

    public Gestures getGGesture() {
        return Gestures.getInstance();
    }

    public Nodes getGNode() {
        return Nodes.getInstance();
    }

    public Points getGPoint() {
        return Points.getInstance();
    }

    public Rects getGRect() {
        return Rects.getInstance();
    }

    public Screens getGScreen() {
        return Screens.getInstance();
    }

    public Shells getGShell() {
        return Shells.getInstance();
    }

    public Apps getGApp() {
        return Apps.getInstance(pkg);
    }

    public void setApp(String pkg, IJsDroidApp app) {
        this.pkg = pkg;
        this.app = app;
        setProperty("out", getGApp());
    }

    @MethodDoc("弹出toast")
    public void toast(Object text) {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            runnerApp.toast(text);
        }
    }

    @MethodDoc("输入文字")
    public void inputText(
            @FieldName("文字") String text) {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.input(text);
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("清除文字")
    public void clearText(@FieldName("光标前位数") int before, @FieldName("光标后位数") int after) {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.clear(before, after);
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入go")
    public void inputGo() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputGo();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入结束")
    public void inputDone() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputDone();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入下一步")
    public void inputNext() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputNext();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入搜索动作")
    public void inputSearch() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputSearch();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入发送动作")
    public void inputSend() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputSend();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("输入未指定的动作")
    public void inputUnspecified() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                input.inputUnspecified();
            } catch (Exception e) {
            }
        }
    }

    @MethodDoc("手指按下")
    public void touchDown(@FieldName("x") int x, @FieldName("y") int y) {
        getGEvent().touchDown(x, y);
    }

    @MethodDoc("手指弹起")
    public void touchUp(int x, int y) {
        getGEvent().touchUp(x, y);
    }

    @MethodDoc("手指移动")
    public void touchMove(@FieldName("x") int x, @FieldName("y") int y) {
        getGEvent().touchMove(x, y);
    }

    @MethodDoc("点击屏幕")
    public void click(@FieldName("x") int x, @FieldName("y") int y) {
        getGEvent().tap(x, y);
    }

    @MethodDoc("点击屏幕")
    public void tap(@FieldName("x") int x, @FieldName("y") int y) {
        getGEvent().tap(x, y);
    }

    @MethodDoc("手指滑动")
    public void swipe(@FieldName("x1") int x1, @FieldName("y1") int y1, @FieldName("x2") int x2,
                      @FieldName("y2") int y2) {
        getGEvent().swipe(x1, y1, x2, y2, 20);
    }

    @MethodDoc("手指滑动")
    public void swipe(@FieldName("x1") int x1, @FieldName("y1") int y1, @FieldName("x2") int x2,
                      @FieldName("y2") int y2, @FieldName("补间数量") int steps) {
        getGEvent().swipe(x1, y1, x2, y2, steps);
    }

    @MethodDoc("模拟按键")
    public void keyPress(@FieldName("按键码") int code) {
        getGEvent().pressKeyCode(code);
    }

    @MethodDoc("查找单个节点")
    public Node findNode(@FieldName("正则表达式") Pattern pattern) {
        final Store<Node> nodeStore = new Store<>();
        getGNode().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                try {
                    if (pattern.matcher(node.getText()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getRes()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getDesc()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
        return nodeStore.get();

    }

    @MethodDoc("查找所有节点")
    public List<Node> findNodeAll(@FieldName("正则表达式") Pattern pattern) {
        final List<Node> nodes = new ArrayList<>();
        getGNode().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                try {
                    if (pattern.matcher(node.getText()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getRes()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getDesc()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
        return nodes;
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("png文件路径") String pngFile,
                                @FieldName("左") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        Bitmap image;
        try {
            image = BitmapFactory.decodeStream(getGFile().openRes(pngFile));
        } catch (Exception e) {
            return null;
        }
        return findImg(image,
                left,
                top,
                right,
                bottom,
                offset,
                sim);
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("内存图片") Bitmap image,
                                @FieldName("左") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        Bitmap screen;
        try {
            screen = getGScreen().capture();
        } catch (Exception e) {
            return null;
        }
        return findImg(screen,
                image,
                left,
                top,
                right,
                bottom,
                offset,
                sim);
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("被找内存图") Bitmap screen,
                                @FieldName("要找内存图") Bitmap image,
                                @FieldName("做") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        int distance = 1;
        int level = 8;
        if (sim > 0.7) {
            distance = 2;
            level = 16;
        }
        if (image == null) {
            return null;
        }
        return FindImg.findImg(screen,
                image,
                level,
                left,
                top,
                right,
                bottom,
                offset,
                distance,
                sim);
    }


    @MethodDoc("普通找图")
    public Point findPic(@FieldName("png文件") String pngFile,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        Bitmap image;
        try {
            image = BitmapFactory.decodeStream(getGFile().openRes(pngFile));
        } catch (Exception e) {
            return null;
        }
        return findPic(image,
                (int) left,
                (int) top,
                (int) right,
                (int) bottom,
                (int) offset,
                (float) sim);
    }

    @MethodDoc("普通找图")
    public Point findPic(@FieldName("内存图") Bitmap image,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        Bitmap screen;
        try {
            screen = getGScreen().capture();
        } catch (Exception e) {
            return null;
        }
        return findPic(screen,
                image,
                (int) left,
                (int) top,
                (int) right,
                (int) bottom,
                (int) offset,
                (float) sim);
    }

    @MethodDoc("普通找图")
    public Point findPic(@FieldName("被找内存图") Bitmap screen,
                         @FieldName("要找内存图") Bitmap image,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        try {
            if (image == null) {
                return null;
            }
            return FindPic.findPic(screen,
                    image,
                    (int) left,
                    (int) top,
                    (int) right,
                    (int) bottom,
                    (int) offset,
                    (float) sim);
        } catch (Exception e) {
            return null;
        }
    }


    @MethodDoc("图片转bytes")
    public byte[] bitmapToJpg(@FieldName("内存图") Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                80,
                out);
        return out.toByteArray();
    }

    @MethodDoc("图片转bytes")
    public byte[] bitmapToPng(@FieldName("内存图") Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,
                80,
                out);
        return out.toByteArray();
    }

    @MethodDoc("时间戳")
    public long time() {
        return System.currentTimeMillis();
    }

    @MethodDoc("安卓版本号")
    public int getSdk() {
        return Libs.SDK_INT;
    }

    @MethodDoc("读取配置")
    public String readConfig(@FieldName("key") String key,
                             @FieldName("默认值") String defaultValue) {
        return getGApp().readConfig(key, defaultValue);
    }

    @MethodDoc("读取配置")
    public String readConfig(@FieldName("key") String key) {
        return readConfig(key, null);
    }

    @MethodDoc("执行shell命令")
    public String exec(@FieldName("shell命令") String shell) {
        return Shells.getInstance().exec(shell);
    }

    @MethodDoc("并发执行,等待结束")
    public void multiThread(@FieldName("闭包") Closure... closures) {
        List<SyncRunnable> syncRunnableList = new ArrayList<>();
        for (Closure closure : closures) {
            SyncRunnable runnable = new SyncRunnable(new Runnable() {
                @Override
                public void run() {
                    closure.call();
                }
            });
            syncRunnableList.add(runnable);
            new Thread(runnable).start();
        }
        for (SyncRunnable runnable : syncRunnableList) {
            try {
                runnable.sync();
            } catch (Throwable throwable) {
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    throwable.printStackTrace(pw);
                }
                print(sw.toString());
            }
        }
    }

    @MethodDoc("发送http请求")
    public String httpGet(@FieldName("链接") String url) {
        return getGHttp().get(url);
    }

    @MethodDoc("发送http请求")
    public String httpGet(@FieldName("链接") String url,
                          @FieldName("数据") Map params) {
        return getGHttp().get(url, params);
    }

    @MethodDoc("发送http请求")
    public String httpGet(@FieldName("链接") String url,
                          @FieldName("请求头") Map headers,
                          @FieldName("数据") Map params) {
        return getGHttp().get(url, headers, params);
    }

    @MethodDoc("发送http请求")
    public String httpPost(@FieldName("链接") String url,
                           @FieldName("数据") Map params) {
        return getGHttp().post(url, params);
    }

    @MethodDoc("发送http请求")
    public String httpPost(@FieldName("链接") String url,
                           @FieldName("请求头") Map headers,
                           @FieldName("数据") Map params) {
        return getGHttp().post(url, headers, params);
    }

    @MethodDoc("杀死app进程")
    public void killApp(@FieldName("包名") String pkg) {
        exec("am force-stop " + pkg);
    }

    @MethodDoc("启动app")
    public void runApp(@FieldName("包名或者应用名") String pkgOrName) {
        String appPkg = getAppPkg(pkgOrName);
        if (appPkg != null) {
            Application application = ActivityThread.currentApplication();
            PackageManager pm = application.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(appPkg);
            if (intent != null) {
                ComponentName component = intent.getComponent();
                if (component != null) {
                    String am = component.flattenToShortString();
                    exec("am start -n " + am);
                }
            }
        }

    }

    @MethodDoc("获取app包名")
    public String getAppPkg(@FieldName("包名或者应用名") String pkgOrName) {
        Application application = ActivityThread.currentApplication();
        PackageManager pm = application.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo aPackage : packages) {
            if (aPackage.packageName.equals(pkgOrName)) {
                return pkgOrName;
            }
            CharSequence name = pm.getApplicationLabel(aPackage.applicationInfo);
            if (pkgOrName.equals(name.toString())) {
                return aPackage.packageName;
            }
        }
        return null;
    }

    @MethodDoc("设置屏幕亮度,设置范围0-255")
    public void setScreenBrightness(int brightness) {
        exec("settings put system screen_brightness_mode 0\n" +
                "settings put system screen_brightness " + brightness + "\n");
    }

    @MethodDoc("读取屏幕亮度，如果读取失败，则返回-1")
    public int getScreenBrightness() {
        try {
            String str = exec("settings get system screen_brightness");
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return -1;
    }

    //息屏运行模式
    @MethodDoc("屏幕显示模式(0:息屏 2:普通)")
    public void setScreenPowerMode(int mode) {
        IBinder d = SurfaceControl.getBuiltInDisplay();
        if (d == null) {
            return;
        }
        SurfaceControl.setDisplayPowerMode(d, mode);
    }

    private ServiceManager serviceManager = new ServiceManager();

    @MethodDoc("设置剪切板")
    public void setClipboardText(String text) {
        serviceManager.getClipboardManager().setText(text);
    }

    @MethodDoc("读取剪切板")
    public String getClipboardText() {
        try {
            return serviceManager.getClipboardManager().getText().toString();
        } catch (Exception e) {
        }
        return null;
    }

    @MethodDoc("读取prop")
    public String getProp(String key) {
        try {
            return exec("getprop " + key);
        } catch (Exception e) {
        }
        return null;
    }

    @MethodDoc("获取imei")
    public String getIMEI() {
        try {
            ActivityThread activityThread = ActivityThread.currentActivityThread();
            Application application = activityThread.getApplication();
            TelephonyManager tm = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
        }
        return null;
    }

    @MethodDoc("获取adb序列号")
    public String getSerial() {
        return getProp("ro.serialno");
    }

    @MethodDoc("检查jsd输入法是否打开")
    public boolean checkInput() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                return input != null;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
