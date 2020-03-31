package com.jsdroid.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.test.widget.LogView;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LogActivity extends AppCompatActivity implements UiMessageUtils.UiMessageCallback {

    private QMUITopBar topBar;
    private LogView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initView();
        topBar.setTitle("日志");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topBar.addRightImageButton(R.drawable.ic_baseline_delete_24, 1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLog();
            }
        });
        UiMessageUtils.getInstance().addListener(this);
        loadLog();
    }

    private void clearLog() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                JsdLog.clear();
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                logView.clear();
            }
        });
    }

    private void loadLog() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                return JsdLog.read();
            }

            @Override
            public void onSuccess(String result) {
                logView.setText(result);
            }
        });
    }

    private void initView() {
        topBar = (QMUITopBar) findViewById(R.id.topBar);
        logView = (LogView) findViewById(R.id.logView);
    }

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.PRINT:
                logView.appendText(localMessage.getObject() + "\n");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiMessageUtils.getInstance().removeListener(this);
    }
}