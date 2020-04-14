package com.jsdroid.sdk.scripts;

import android.util.Log;

import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.JsDroidEnv;
import com.jsdroid.commons.LibraryUtil;
import com.jsdroid.groovy.AndroidGroovyScriptLoader;
import com.jsdroid.script.JsDroidScript;
import com.jsdroid.sdk.files.Files;
import com.jsdroid.sdk.libs.Libs;
import com.jsdroid.sdk.zips.FileEach;
import com.jsdroid.sdk.zips.ZipInput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dalvik.system.DexClassLoader;
import groovy.lang.Binding;
import groovy.lang.Script;

public class Scripts {
    private static Map<String, Scripts> scriptsMap = new HashMap<>();

    public synchronized static Scripts getInstance(String pkg) {

        if (scriptsMap.containsKey(pkg)) {
            return scriptsMap.get(pkg);
        }
        Scripts scripts = new Scripts(pkg);
        scriptsMap.put(pkg, scripts);
        return scripts;
    }

    private String pkg;
    private IJsDroidApp app;

    private Scripts(String pkg) {
        this.pkg = pkg;
    }

    public void setApp(IJsDroidApp app) {
        this.app = app;
    }

    public String getPkg() {
        return pkg;
    }

    /**
     * load xxx.groovy
     * load xxx.jsd
     * load xxx
     *
     * @param parent
     * @param name
     * @return
     */
    public Object load(Script parent, String name) {
        if (name.endsWith(".groovy")) {
            try {
                Script script = createGroovyScriptFromSource(name);
                if (script != null) {
                    loadScript(parent, script);
                    return script;
                }
            } catch (Exception ignored) {
            }
        } else if (name.endsWith(".jsd")) {
            try {
                Script script = createGroovyScriptFromJsd(name);
                if (script != null) {
                    loadScript(parent, script);
                    return script;
                }
            } catch (Exception ignored) {
            }
        } else {
            try {
                Class<?> aClass = parent.getClass().getClassLoader().loadClass(name);
                Script script = (Script) aClass.newInstance();
                loadScript(parent, script);
                return script;
            } catch (Exception e) {
                Log.e("JsDroid", "load error.", e);
            }
        }
        return null;
    }

    public Object eval(Script parent, String content) {
        try {
            Script script = createGroovyScriptFromCode(parent, content);
            if (script != null) {
                script.run();
                return script;
            }
        } catch (Throwable e) {
            Log.e("JsDroid", "eval: ", e);
            return e;
        }
        return null;
    }

    private void loadScript(Script parent, Script script) {
        if (parent != null) {
            Binding binding = parent.getBinding();
            if (binding.hasVariable("global")) {
                script.setProperty("global", binding.getProperty("global"));
            } else {
                script.setProperty("global", parent);
            }
            if (parent.getProperty("out") != null) {
                script.setProperty("out", parent.getProperty("out"));
            }
        }
        script.run();
    }


    public Script createGroovyScriptFromSource(String file) throws IOException {
        return createGroovyScriptFromSource(null, file);
    }

    public Script createGroovyScriptFromSource(Script parent, String file) throws IOException {
        AndroidGroovyScriptLoader androidGroovyScriptLoader =
                new AndroidGroovyScriptLoader(JsDroidEnv.optDir,
                        JsDroidEnv.classesDir,
                        JsDroidScript.class.getName());
        String source = FileUtils.readFileToString(new File(file));
        Script script = androidGroovyScriptLoader.loadScript(parent,
                source, file);
        if (script != null) {
            script.setProperty("app", app);
        }
        return script;
    }

    public Script createGroovyScriptFromCode(String code) throws Exception {
        return createGroovyScriptFromCode(null, code);
    }

    public Script createGroovyScriptFromCode(Script parent, String code) throws Exception {
        AndroidGroovyScriptLoader androidGroovyScriptLoader =
                new AndroidGroovyScriptLoader(JsDroidEnv.optDir,
                        JsDroidEnv.classesDir,
                        JsDroidScript.class.getName());
        Script script = androidGroovyScriptLoader.loadScript(parent,
                code, "script" + System.currentTimeMillis() + ".groovy");
        if (script != null) {
            script.setProperty("app", app);
        }
        return script;
    }

    public Script createGroovyScriptFromJsd(String file) throws Exception {
        //如果脚本文件更新，则将里面的plugin解压出来
        if (Files.isPluginUpdate(new File(file))) {
            new ZipInput(file).unzipFileToDir("plugin", JsDroidEnv.pluginDir);
            File pluginDir = new File(JsDroidEnv.pluginDir, "plugin");
            File[] pluginFiles = pluginDir.listFiles();
            if (pluginFiles != null) {
                for (File pluginFile : pluginFiles) {
                    if (pluginFile.isFile()) {
                        if (pluginFile.getName().endsWith(".apk")) {
                            try {
                                Libs.extractLibFile(pluginFile.getPath(), JsDroidEnv.libDir);
                            } catch (Exception e) {
                            }
                            try {
                                PluginClassLoader.getInstance().add(pluginFile.getPath());
                            } catch (IOException e) {
                            }
                        }

                    }
                }
            }
        }

        File apk = new File("/data/local/tmp/" + getCachePrefix() + UUID.randomUUID() + ".apk");
        FileUtils.copyFile(new File(file), apk);
        ScriptClassLoader classLoader = new ScriptClassLoader(apk.getPath(), JsDroidEnv.optDir, JsDroidEnv.libDir);
        ScriptInfo scriptInfo = getScriptInfo(classLoader);
        String mainClassName = (scriptInfo.getMainScript());
        Class<?> aClass = classLoader.loadClass(mainClassName);
        return (Script) aClass.newInstance();


    }

    private String getCachePrefix() {
        return pkg + ".cache.";
    }

    public void deleteScriptCacheFile() {
        final String preName = getCachePrefix();
        FileEach.eachFile(new File("/data/local/tmp/"), new FileEach() {
            @Override
            public boolean each(File file, int depth) {
                String name = file.getName();
                if (name.startsWith(preName)
                        || name.startsWith(AndroidGroovyScriptLoader.PREFIX)) {
                    if (name.endsWith(".dex") || name.endsWith(".apk") || name.endsWith(".odex") || name.endsWith(".vdex")) {
                        file.delete();
                    }
                }
                return false;
            }
        });
    }

    public ScriptInfo getScriptInfo(DexClassLoader classLoader) throws Exception {
        try (InputStream input = classLoader.getResourceAsStream("config.json")) {
            ScriptInfo scriptInfo = new ScriptInfo();
            String body = IOUtils.toString(input, "utf-8");
            try {
                JSONObject json = new JSONObject(body);
                scriptInfo.setVersion(json.getString("version"));
                scriptInfo.setVersion(json.getString("pkg"));
                scriptInfo.setName(json.getString("name"));
                scriptInfo.setMainScript(json.getString("mainScript"));
                return scriptInfo;
            } catch (Exception e) {
                throw new Exception("无法解析config.json,内容：" + body, e);
            }
        }
    }
}
