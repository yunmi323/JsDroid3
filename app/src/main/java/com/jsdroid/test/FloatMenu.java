package com.jsdroid.test;

import android.animation.Animator;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.test.widget.ScriptOptionView;
import com.jsdroid.test.widget.ShadowDrawable;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.ViewStateListenerAdapter;

public class FloatMenu implements UiMessageUtils.UiMessageCallback {

    public static final String TAG = "jsd_float_menu";

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.OPTION_CHANGED:
                if (scriptOptionView != null) {
                    scriptOptionView.loadOptions(localMessage.getObject());
                }
                break;
            case UiMessage.SRIPT_START:
            case UiMessage.SRIPT_HAS_START:
                hide();
                FloatLogo.getInstance().show();
                break;
        }
    }

    private static class Single {
        static FloatMenu single = new FloatMenu();
    }

    public static FloatMenu getInstance() {
        return Single.single;
    }

    private JsdApp jsdApp;
    private View view;
    private ScriptOptionView scriptOptionView;

    private FloatMenu() {
    }

    public void init(final JsdApp jsdApp) {
        this.jsdApp = jsdApp;

    }

    private void initView() {
        FloatWindow.with(jsdApp)
                .setTag(TAG)
                .setMoveType(MoveType.inactive)
                .setView(R.layout.jsd_float_menu)
                .setWindowFlag(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                .setDesktopShow(true)
                .setViewStateListener(new ViewStateListenerAdapter() {
                    @Override
                    public void onShow(final IFloatWindow floatWindow) {
                        WindowManager.LayoutParams params = floatWindow.getParams();
//                        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                        floatWindow.updateParams();
                        View view = floatWindow.getView();
                        view.setAlpha(0);
                        view.animate().setDuration(300).alpha(1).start();
                        toCenter(floatWindow);

                    }


                })
                .build();
        IFloatWindow floatWindow = FloatWindow.get(TAG);
        if (floatWindow != null) {
            view = floatWindow.getView();
            view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.animate().setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            hide();
                            FloatLogo.getInstance().show();
                            view.animate().setListener(null);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).alpha(0).setDuration(300).start();

                }
            });
            view.findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jsdApp.startScript();
                }
            });
            int dp4 = QMUIDisplayHelper.dp2px(view.getContext(), 8);
            ShadowDrawable drawable = new ShadowDrawable.Builder()
                    .setShapeRadius(dp4)
                    .setShadowColor(0xff333333)
                    .setShadowRadius(dp4)
                    .setBgColor(0xffffffff)
                    .builder();
            drawable.setAlpha(100);
            view.findViewById(R.id.rootView).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            view.findViewById(R.id.rootView).setBackground(drawable);
            scriptOptionView = new ScriptOptionView(view.getContext());
            LinearLayout content = view.findViewById(R.id.content);
            content.addView(scriptOptionView);
            floatWindow.show();
        }
        UiMessageUtils.getInstance().addListener(this);
    }


    public void show() {
        final IFloatWindow floatWindow = FloatWindow.get(TAG);
        if (floatWindow != null) {
            floatWindow.show();
            toCenter(floatWindow);
        } else {
            initView();
        }

    }

    private void toCenter(final IFloatWindow floatWindow) {
        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (floatWindow.isShowing()) {
                        int x = QMUIDisplayHelper.getScreenWidth(jsdApp) - view.getWidth();
                        int y = QMUIDisplayHelper.getScreenHeight(jsdApp) - view.getHeight();
                        floatWindow.updateX(x / 2);
                        floatWindow.updateY(y / 2);
                    }

                }
            });
        }
    }

    public void hide() {
        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    IFloatWindow floatWindow = FloatWindow.get(TAG);
                    if (floatWindow != null) {
                        floatWindow.hide();
                    }
                }
            });
        }

    }
}
