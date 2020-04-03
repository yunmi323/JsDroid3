package com.jsdroid.test;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.runner.JsDroidApplication;
import com.jsdroid.test.widget.ScriptOptionView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yhao.floatwindow.SdkVersion;

public class MainActivity extends AppCompatActivity implements UiMessageUtils.UiMessageCallback {

    private Toolbar toolBar;
    private AccountHeader accountHeader;
    private Drawer drawer;
    private ScriptOptionView optionView;
    boolean pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiMessageUtils.getInstance().addListener(this);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolBar);
        Drawable overflowIcon = toolBar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(0xffffffff,
                    PorterDuff.Mode.SRC_IN);
        }
        if (SdkVersion.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolBar.setElevation(QMUIDisplayHelper.dp2px(this, 10));
        }
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withCloseOnClick(false)
                .withHeaderDivider(true)
                .withAccountHeader(accountHeader, true)
                .withToolbar(toolBar)
                .withActionBarDrawerToggleAnimated(true)
                .build();
        drawer.getActionBarDrawerToggle().getDrawerArrowDrawable().setColor(0xffffffff);
        JsdApp jsdApp = JsdApp.getInstance(JsdApp.class);
        drawer.addItem(new SwitchDrawerItem().withIdentifier(1).withName("悬浮窗").withChecked(jsdApp.isShowFloatView()).withSelectable(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                JsdApp jsdApp = JsdApp.getInstance();
                jsdApp.switchFloatWindowState(isChecked);
            }
        }));
        drawer.addItem(new SwitchDrawerItem().withIdentifier(2).withName("音量键控制").withChecked(jsdApp.isVolumeControl()).withSelectable(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                JsdApp jsdApp = JsdApp.getInstance();
                jsdApp.switchVolumeControlState(isChecked);
            }
        }));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(3).withName("重启服务")
                .withSelectable(false).withIsExpanded(true).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        JsdApp jsdApp = JsdApp.getInstance();
                        jsdApp.stopScript();
                        return false;
                    }
                }));

        if (pro) {
            drawer.addItem(new PrimaryDrawerItem().withIdentifier(4).withName("自定义su").withSelectable(false)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            QMUIDialog.EditTextDialogBuilder editTextDialogBuilder = new QMUIDialog.EditTextDialogBuilder(MainActivity.this);
                            editTextDialogBuilder.setDefaultText(JsDroidApplication.getInstance().getSu());
                            editTextDialogBuilder.setPlaceholder("请输入自定义su");
                            editTextDialogBuilder.setTitle("请输入自定义su");
                            editTextDialogBuilder.addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            });
                            editTextDialogBuilder.addAction("确定", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    EditText editText = editTextDialogBuilder.getEditText();
                                    String su = editText.getText().toString();
                                    String trim = su.trim();
                                    if (trim.length() > 0) {
                                        JsDroidApplication.getInstance().setSu(trim);
                                    }

                                }
                            });

                            QMUIDialog qmuiDialog = editTextDialogBuilder.create();
                            qmuiDialog.show();


                            return false;
                        }
                    })
            );
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("帮助").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                return false;
            }
        }).setIcon(R.drawable.ic_help).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("日志").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, LogActivity.class));
                return false;
            }
        }).setIcon(R.drawable.ic_log).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("运行").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                JsdApp.getInstance(JsdApp.class).showFloatMenu();
                return false;
            }
        }).setIcon(R.drawable.ic_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        optionView = (ScriptOptionView) findViewById(R.id.optionView);
    }

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.OPTION_CHANGED:
                optionView.loadOptions(localMessage.getObject());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiMessageUtils.getInstance().removeListener(this);
        optionView.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
