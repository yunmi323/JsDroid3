package com.jsdroid.script;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityThread;
import android.app.Application;
import android.app.Service;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import com.genymobile.scrcpy.wrappers.ServiceManager;
import com.genymobile.scrcpy.wrappers.SurfaceControl;
import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.annotations.FieldName;
import com.jsdroid.api.annotations.MethodDoc;
import com.jsdroid.commons.ActivityUtil;
import com.jsdroid.commons.BitmapUtil;
import com.jsdroid.commons.ContextUtil;
import com.jsdroid.commons.FileUtil;
import com.jsdroid.commons.Http;
import com.jsdroid.commons.ScreenUtil;
import com.jsdroid.findimg.FindImg;
import com.jsdroid.findpic.FindPic;
import com.jsdroid.ipc.call.SyncRunnable;
import com.jsdroid.sdk.apps.Apps;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.directions.Directions;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.files.Files;
import com.jsdroid.sdk.gestures.Gestures;
import com.jsdroid.sdk.gestures.PointerGesture;
import com.jsdroid.sdk.https.Https;
import com.jsdroid.sdk.inputs.Inputs;
import com.jsdroid.sdk.libs.Libs;
import com.jsdroid.sdk.logs.Logs;
import com.jsdroid.sdk.nodes.Node;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.nodes.Store;
import com.jsdroid.sdk.play.SinglePlayer;
import com.jsdroid.sdk.points.Points;
import com.jsdroid.sdk.rects.Rects;
import com.jsdroid.sdk.screens.Screens;
import com.jsdroid.sdk.scripts.Scripts;
import com.jsdroid.sdk.shells.Shells;
import com.jsdroid.sdk.sockets.Sockets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class JsDroidScript extends Script {
    public IJsDroidApp app;
    public String pkg;
    public Files files = new Files(this);
    public Devices device = Devices.getInstance();
    public Events events = Events.getInstance();
    public Nodes nodes = Nodes.getInstance();

    {
        try {
            app = Apps.getRunnerApp().getApp();
            pkg = Apps.getRunnerApp().getPkg();
        } catch (Throwable e) {
        }
    }

    @MethodDoc("获取UiAutomation,不知者不推荐使用")
    public static UiAutomation getUiAutomation() {
        return Nodes.getInstance().getUiAutomation();
    }

    @MethodDoc("设置是否获取不重要节点")
    public void setFetchNotImportantNodeEnable(boolean enable) {
        if (enable) {
            addNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        } else {
            removeNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        }
    }

    @MethodDoc("设置是否开启web增强模式")
    public void setFetchWebNodeEnable(boolean enable) {
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
    public void addNodeFlag(int flag) {
        try {
            try {
                Nodes.getInstance().addNodeFlag(flag);
            } catch (Throwable e) {
            }
        } catch (Throwable e) {
        }
    }

    @MethodDoc("删除节点标志")
    public void removeNodeFlag(int flag) {
        try {
            Nodes.getInstance().removeNodeFlag(flag);
        } catch (Throwable e) {
        }
    }

    public JsDroidScript() {
    }

    public JsDroidScript(Binding binding) {
        super(binding);
    }

    public Object load(String name) throws InterruptedException {
        return Scripts.getInstance(pkg).load(this, name);
    }

    public Files getGFile() {
        return files;
    }

    public Files getFiles() {
        return files;
    }

    public Logs getGLog() {
        return Logs.getInstance();
    }

    public Logs getgLog() {
        return Logs.getInstance();
    }


    public Https getGHttp() {
        return Https.getInstance();
    }

    public Https getgHttp() {
        return Https.getInstance();
    }

    public Sockets getGSocket() {
        return Sockets.getInstance();
    }

    public Sockets getgSocket() {
        return Sockets.getInstance();
    }

    public Devices getGDevice() {
        return Devices.getInstance();
    }

    public Devices getgDevice() {
        return Devices.getInstance();
    }

    public Directions getGDirection() {
        return Directions.getInstance();
    }

    public Directions getgDirection() {
        return Directions.getInstance();
    }

    public Events getGEvent() {
        return Events.getInstance();
    }

    public Events getgEvent() {
        return Events.getInstance();
    }

    public Gestures getGGesture() {
        return Gestures.getInstance();
    }

    public Gestures getgGesture() {
        return Gestures.getInstance();
    }

    public Nodes getGNode() {
        return Nodes.getInstance();
    }

    public Nodes getgNode() {
        return Nodes.getInstance();
    }

    public Points getGPoint() {
        return Points.getInstance();
    }

    public Points getgPoint() {
        return Points.getInstance();
    }

    public Rects getGRect() {
        return Rects.getInstance();
    }

    public Rects getgRect() {
        return Rects.getInstance();
    }

    public Screens getGScreen() {
        return Screens.getInstance();
    }

    public Screens getgScreen() {
        return Screens.getInstance();
    }

    public Shells getGShell() {
        return Shells.getInstance();
    }

    public Shells getgShell() {
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

    @MethodDoc("判断输入法是否打开")
    public boolean hasInputOpen() {
        Apps runnerApp = Apps.getRunnerApp();
        if (runnerApp != null) {
            try {
                IInput input = runnerApp.getInput();
                return input.hasOpen();
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 打开输入法
     */
    @MethodDoc("打开输入法")
    public void openInputMethod() throws InterruptedException {
        try {
            Inputs.getInstance().openInputMethod();
        } catch (Exception e) {
        }
    }

    /**
     * 关闭输入法
     */
    @MethodDoc("关闭输入法")
    public void closeInputMethod() {
        try {
            Inputs.getInstance().closeInputMethod();
        } catch (Exception e) {
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
                    if (pattern.matcher(node.getClazz()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
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

    @MethodDoc("查找单个节点")
    public Node findNode(@FieldName("map") Map map) {
        try {
            return Nodes.getInstance().map(map).findOne();
        } catch (Throwable e) {
            return null;
        }
    }

    @MethodDoc("查找所有节点")
    public List<Node> findNodeAll(@FieldName("正则表达式") Pattern pattern) {
        final List<Node> nodes = new ArrayList<>();
        getGNode().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                try {
                    if (pattern.matcher(node.getClazz()).matches()) {
                        nodes.add(node);
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getText()).matches()) {
                        nodes.add(node);
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getRes()).matches()) {
                        nodes.add(node);
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getDesc()).matches()) {
                        nodes.add(node);
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
        return nodes;
    }

    @MethodDoc("查找全部节点")
    public List<Node> findNodeAll(@FieldName("map") Map map) {
        try {
            return Nodes.getInstance().map(map).findAll();
        } catch (Throwable e) {
            return new LinkedList<>();
        }
    }


    @MethodDoc("查找所有节点")
    public List<Node> findNodes(@FieldName("正则表达式") Pattern pattern) {
        return findNodeAll(pattern);
    }

    @MethodDoc("查找全部节点")
    public List<Node> findNodes(@FieldName("map") Map map) {
        return findNodeAll(map);
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
        try {
            return getGApp().readConfig(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
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
    public boolean checkJsdInput() {
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

    @MethodDoc("手机震动")
    public void vibrate(int ms) {
        try {
            Vibrator vibrator =
                    (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
        } catch (Throwable e) {
        }
    }

    @MethodDoc("播放音乐,返回音乐时长")
    public int playMusic(@FieldName("file") String file) {

        return SinglePlayer.play(file);

    }

    @MethodDoc("停止播放音乐")
    public void stopMusic() {
        SinglePlayer.stop();
    }

    @MethodDoc("get链")
    public Http get(@FieldName("url") String url) {
        return Http.get(url);
    }

    @MethodDoc("post链")
    public Http post(@FieldName("url") String url) {
        return Http.post(url);
    }


    public Context getContext() {
        try {
            return ContextUtil.getContext();
        } catch (Throwable e) {
            return null;
        }
    }


    @MethodDoc("点击节点")
    public boolean click(@FieldName("map") Map map) throws InterruptedException {
        try {
            Node node = findNode(map);
            if (node != null) {
                node.click();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @MethodDoc("手势操作")
    public void gestures(@FieldName("pointerGestures") PointerGesture... pointerGestures) {
        try {
            getGEvent().performGestures(pointerGestures);
        } catch (Exception e) {
        }
    }

    @MethodDoc("输出日志")
    public void log(@FieldName(value = "content", note = "输出的内容") Object content) {
        print(content);
    }

    @MethodDoc("读取res文件")
    public String readRes(String file) {
        try {
            return getGFile().readResToString(file);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("读取res文件")
    public String readRes(String file, String encode) {
        try {
            return getGFile().readResToString(file, encode);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("读取res文件")
    public byte[] readResBytes(String file) {
        try {
            return getGFile().readRes(file);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("读取文件为字符串")
    public String read(@FieldName("file") String file) {
        try {
            return new String(getGFile().readFile(file));
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("读取文件为字符串")
    public String read(@FieldName("file") String file, @FieldName("encode") String encode) {
        try {
            return new String(getGFile().readFile(file), encode);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("写入内容到文件")
    public void write(@FieldName("file") String file,
                      @FieldName("content") String content) {
        try {
            FileUtil.write(file, content);
        } catch (Exception e) {
        }
    }

    @MethodDoc("向文件追加一行内容")
    public void append(@FieldName("file") String file,
                       @FieldName("content") String content) {
        try {
            FileUtil.append(file, content);
        } catch (Exception e) {
        }
    }

    @MethodDoc("移动文件到文件")
    public void moveFileToFile(@FieldName("from") String from,
                               @FieldName("to") String to) {
        try {
            File fromFile = new File(from);
            FileUtils.copyFile(fromFile,
                    new File(to));
        } catch (Exception e) {
        }
    }

    @MethodDoc("移动文件到文件夹")
    public void moveFileToDir(@FieldName("from") String from,
                              @FieldName("dir") String dir) {
        try {
            File fromFile = new File(from);
            File toFile = new File(dir);
            FileUtils.copyFileToDirectory(fromFile,
                    toFile);
            fromFile.delete();
        } catch (Exception e) {
        }

    }

    @MethodDoc("复制文件到文件")
    public void copyFileToFile(@FieldName(value = "from") String from,
                               @FieldName("to") String to) {
        try {
            FileUtils.copyFile(new File(from), new File(to));
        } catch (Exception e) {
        }
    }

    @MethodDoc("复制文件到文件夹")
    public void copyFileToDir(@FieldName("from") String from,
                              @FieldName("dir") String dir) {
        try {
            FileUtils.copyFileToDirectory(new File(from), new File(dir));
        } catch (Exception e) {
        }

    }

    @MethodDoc("删除文件")
    public void deleteFile(@FieldName(value = "file", note = "文件路径") String file) {
        try {
            new File(file).delete();
        } catch (Exception e) {
        }
    }

    @MethodDoc("创建文件")
    public void createFile(@FieldName(value = "file", note = "文件路径") String file) {
        try {
            new File(file).createNewFile();
        } catch (Exception e) {
        }
    }

    @MethodDoc("创建文件夹")
    public void mkdir(@FieldName(value = "dir", note = "文件路径") String file) {
        try {
            new File(file).mkdir();
        } catch (Exception e) {
        }
    }

    @MethodDoc("创建文件夹，多层递归创建")
    public void mkdirs(@FieldName(value = "dir", note = "文件路径") String file) {
        try {
            new File(file).mkdirs();
        } catch (Exception e) {
        }
    }

    /**
     * 获取当前activity组件名
     *
     * @return
     */
    @MethodDoc("获取当前activity")
    public String getActivity() {
        try {
            String result = exec("dumpsys activity activities|grep mResumedActivity");
            String[] split = result.split(" ");
            for (String sp : split) {
                if (sp.contains("/")) {
                    return sp;
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    @MethodDoc("线程睡眠")
    public void delay(@FieldName("time") long time) throws InterruptedException {
        Thread.sleep(time);
    }

    @MethodDoc("锁定屏幕")
    public void lockDevice() {
        try {
            ScreenUtil.lockDevice(getContext());
        } catch (Exception e) {
        }
    }

    /**
     * 解锁屏幕
     */
    @MethodDoc("解锁屏幕")
    public void unlockDevice() {
        try {
            ScreenUtil.unlockDevice(getContext());
        } catch (Throwable e) {
        }

    }

    @MethodDoc("启动activity")
    public void startActivity(@FieldName("intent") Intent intent) {
        try {
            ActivityUtil.startActivity(intent);
        } catch (Exception e) {
        }
    }

    @MethodDoc("获取屏幕宽度")
    public int getScreenWidth() {
        return ScreenUtil.getScreenWidth(getContext());
    }

    @MethodDoc("获取屏幕高度")
    public int getScreenHeight() {
        return ScreenUtil.getScreenHeight(getContext());
    }

    @MethodDoc("获取屏幕旋转方向")
    public int getRotation() {
        return ScreenUtil.getRotation();
    }

    @MethodDoc("获取屏幕未旋转的宽度")
    public int getNaturalWidth() {
        return ScreenUtil.getNaturalWidth();
    }

    @MethodDoc("获取屏幕未旋转的高度")
    public int getNaturalHeight() {
        return ScreenUtil.getNaturalHeight();
    }

    public Bitmap cap() {
        try {
            return Screens.getInstance().capture();
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * 截图
     *
     * @param file    保存的文件为准
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param quality
     * @param type    保存的类型：png/jpg
     */
    @MethodDoc("截屏到文件")
    public void screenshot(@FieldName("saveFile") String file,
                           @FieldName("left") int left,
                           @FieldName("top") int top,
                           @FieldName("right") int right,
                           @FieldName("bottom") int bottom,
                           @FieldName("quality") int quality,
                           @FieldName("type") String type) {

        FileOutputStream out = null;
        try {
            Bitmap bitmap = Screens.getInstance().capture();
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,
                    left,
                    top,
                    right - left,
                    bottom - top);
            out = new FileOutputStream(file);
            bitmap1.compress(type.equals("png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                    quality,
                    out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 截图
     *
     * @param file
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @MethodDoc("截屏到文件")
    public void screenshot(@FieldName("file") String file,
                           @FieldName("left") int left,
                           @FieldName("top") int top,
                           @FieldName("right") int right,
                           @FieldName("bottom") int bottom) {
        screenshot(file,
                left,
                top,
                right,
                bottom,
                100);
    }

    /**
     * 截图
     *
     * @param file
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param quality
     */
    @MethodDoc("截屏到文件")
    public void screenshot(@FieldName("file") String file,
                           @FieldName("left") int left,
                           @FieldName("top") int top,
                           @FieldName("right") int right,
                           @FieldName("bottom") int bottom,
                           @FieldName("quality") int quality) {
        screenshot(file,
                left,
                top,
                right,
                bottom,
                quality,
                "png");
    }

    @MethodDoc("读取图片")
    public Bitmap readBitmap(@FieldName("file") String file) {
        try {
            byte[] data = FileUtil.readBytes(getGFile().openRes(file));
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("保存图片")
    public void saveBitmap(@FieldName("file") String file,
                           @FieldName("bitmap") Bitmap image) {
        try {
            BitmapUtil.save(file,
                    image);
        } catch (Exception e) {
        }
    }

    @MethodDoc("截屏")
    public Bitmap cap(@FieldName("left") int left,
                      @FieldName("top") int top,
                      @FieldName("right") int right,
                      @FieldName("bottom") int bottom) {
        try {
            Bitmap bitmap = cap();
            return Bitmap.createBitmap(bitmap,
                    left,
                    top,
                    right - left,
                    bottom - top);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("获取颜色的红色分量")
    public int red(@FieldName("color") int color) {
        return Color.red(color);
    }

    @MethodDoc("获取颜色的绿色分量")
    public int green(@FieldName("color") int color) {
        return Color.green(color);
    }

    @MethodDoc("获取颜色的蓝色分量")
    public int blue(@FieldName("color") int color) {
        return Color.blue(color);
    }

    @MethodDoc("获取颜色")
    public int getColor(@FieldName("x") int x,
                        @FieldName("y") int y) {
        try {

            return cap().getPixel(x,
                    y);
        } catch (Exception e) {
            return 0;
        }
    }

    @MethodDoc("截取图片")
    public Bitmap screenshot() {
        try {
            return cap();
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc("执行groovy代码")
    public Object eval(@FieldName("code") String code) throws Exception {
        return Scripts.getInstance(pkg).eval(this, code);
    }

    @MethodDoc("执行groovy代码")
    public Object runCode(@FieldName("code") String code) throws Exception {
        return Scripts.getInstance(pkg).eval(this, code);
    }


    @MethodDoc("释放文件")
    public void releaseFile(String resName, String path) {
        try (
                FileOutputStream out = new FileOutputStream(path);
                InputStream input = files.openRes(resName)
        ) {
            IOUtils.copy(input, out);
        } catch (Exception err) {

        }
    }

}
