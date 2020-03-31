package com.jsdroid.test.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.ConsoleMessage;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.api.annotations.Doc;
import com.jsdroid.test.JsdApp;
import com.jsdroid.test.R;
import com.jsdroid.test.UiMessage;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Iterator;

@Doc("脚本配置界面")
public class ScriptOptionView extends LinearLayout {


    public ScriptOptionView(Context context) {
        super(context);
        loadOptions(null);
    }

    public ScriptOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadOptions(null);
    }

    public void loadOptions(Object obj) {
        if (equals(obj)) {
            return;
        }
        removeAllViews();
        //将配置解析出来
        JsdApp jsd = JsdApp.getInstance();
        File scriptDir = jsd.getScriptDir();
        File optionJsonFile = new File(scriptDir, "option.json");
        File optionHtmlFile = new File(scriptDir, "index.html");
        Context context = getContext();
        if (context instanceof Activity) {
            if (optionHtmlFile.exists()) {
                showOptionHtml(optionHtmlFile);
            } else if (optionJsonFile.exists()) {
                showOptionJson(optionJsonFile);
            } else {
                showReadMe();
            }
        } else {
            showOptionJson(optionJsonFile);
        }
    }

    private void showReadMe() {
        addView(new ScriptHelpView(getContext()), -1, -1);
    }

    private void showOptionJson(File optionFile) {
        LayoutInflater.from(getContext()).inflate(R.layout.jsd_tab_script_option, this);
        LinearLayout contentView = findViewById(R.id.content);
        try {
            String optionContent = FileUtils.readFileToString(optionFile);
            JSONObject json = new JSONObject(optionContent);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject optionItem = json.getJSONObject(key);
                String name = null;
                if (optionItem.has("name")) {
                    name = optionItem.getString("name");
                }
                String value = null;
                if (optionItem.has("value")) {
                    value = optionItem.getString("value");
                }
                addOption(contentView, key, name, value);
            }
        } catch (Exception e) {
            Log.d("JsDroid", "loadOptions: ", e);
        }
    }

    private void showOptionHtml(File optionFile) {
        Context context = getContext();
        if (context instanceof Activity) {

            AgentWeb.with((Activity) context).setAgentWebParent(this,
                    new LayoutParams(-1, -1)
            ).useDefaultIndicator(getResources().getColor(R.color.primary))
                    .setWebChromeClient(new OptionChromeClient()).addJavascriptInterface("JsDroid", JsdApp.getInstance())
                    .setWebViewClient(new WebViewClient() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                            return shouldInterceptRequest(view, request.getUrl().toString());
                        }


                        @Override
                        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                            JsdApp jsd = JsdApp.getInstance();
                            File scriptDir = jsd.getScriptDir();
                            if (url.startsWith("option://jsdroid.com/")) {
                                try {
                                    String name = url.substring("option://jsdroid.com/".length());

                                    File file = new File(scriptDir, URLDecoder.decode(name));
                                    String type = file.toURL().openConnection().getContentType();
                                    Log.d("JsDroid", "shouldInterceptRequest: " + url + " type:" + type);
                                    return new WebResourceResponse(type, "UTF-8", new FileInputStream(file));
                                } catch (Exception e) {
                                    Log.d("JsDroid", "shouldInterceptRequest: ", e);
                                }
                            } else {
                                Log.d("JsDroid", "shouldInterceptRequest?: " + url);
                            }
                            return super.shouldInterceptRequest(view, url);
                        }

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            return shouldOverrideUrlLoading(view, request.getUrl().toString());
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.d("JsDroid", "shouldOverrideUrlLoading: " + url);
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }).createAgentWeb().go("option://jsdroid.com/" + optionFile.getName());

        }

    }

    private void addOption(LinearLayout contentView, final String key, String name, String defaultValue) {
        MaterialEditText optionView = new MaterialEditText(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(-1, -2);
        int dp10 = QMUIDisplayHelper.dp2px(getContext(), 10);
        params.setMargins(dp10, dp10, dp10, dp10);
        optionView.setLayoutParams(params);
        optionView.setHint(name);
        final JsdApp jsdApp = JsdApp.getInstance();
        String value = jsdApp.readConfig(key, defaultValue);
        optionView.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL);
        optionView.setFloatingLabelText(name);
        optionView.setText(value);
        optionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                jsdApp.saveConfig(key, text.toString());
                UiMessageUtils.getInstance().send(UiMessage.OPTION_CHANGED, ScriptOptionView.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        contentView.addView(optionView);
    }
}