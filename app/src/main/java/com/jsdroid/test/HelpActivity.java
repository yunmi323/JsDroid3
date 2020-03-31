package com.jsdroid.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jsdroid.test.widget.ScriptHelpView;
import com.qmuiteam.qmui.widget.QMUITopBar;

public class HelpActivity extends AppCompatActivity {

    private QMUITopBar topBar;
    private ScriptHelpView helpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initView();
        topBar.setTitle("帮助");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topBar.setShadowElevation(0);
        helpView.loadHelp();

    }

    private void initView() {
        topBar = (QMUITopBar) findViewById(R.id.topBar);
        helpView = (ScriptHelpView) findViewById(R.id.helpView);
    }
}