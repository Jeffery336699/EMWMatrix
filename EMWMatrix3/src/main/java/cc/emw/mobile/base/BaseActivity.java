package cc.emw.mobile.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.x;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.util.AnimUtils;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.util.DisplayUtil;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Activity基类
 *
 * @author shaobo.zhuang
 */
public class BaseActivity extends SwipeBackActivity {

    private boolean isBackTip; //是否退出提示
    private boolean isStartAnim = true; //是否跳转动画
    private boolean isForceKilled;
    private ViewGroup rootView;
    private int height;
    protected View mKeyBoradView; //在软键盘显示时，点击该View区域之外会隐藏软键盘

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.addActivity(this);
        x.view().inject(this);
        try {
            if (getIntent().getBooleanExtra("expand_anim", true)) {
                rootView = (ViewGroup) findViewById(R.id.root_view);
                View childView = rootView.getChildAt(0);
                childView.setAlpha(0);
                height = getIntent().getIntExtra("click_pos_y", DisplayUtil.getDisplayHeight(this) / 2);
                AnimUtils.showExpandAnim(savedInstanceState, rootView, childView, height, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimEnd(savedInstanceState);
                    }
                });
            } else {
                startAnimEnd(savedInstanceState);
            }
        } catch (Exception e) {

        }

//        getSwipeBackLayout().setScrimColor(Color.TRANSPARENT);
    }

    protected void startAnimEnd(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        isStartAnim = true;
        super.onDestroy();
        AppManager.finishActivity(this);
    }

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isForceKilled = true;
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
//		if (isBackTip) {
//			showDialogTip();
//		} else {
        if (rootView != null) {
//            finish();
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            AnimUtils.closeAnim(rootView, rootView.getChildAt(0), height, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isTaskRoot()) {
                                System.exit(0);
                            }else {
                                finish();
                            }

                        }
                    }, 100);
                }
            });
        } else {
            scrollToFinishActivity();
        }
//		}
    }

    private void handleRootView() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
//        final View rootView = getWindow().getDecorView();
//        /*rootView.setPivotX(0);
//        rootView.setPivotY(0);
//        rootView.animate()
//                .scaleX(0.98f)
//                .scaleY(0.98f)
//                .setDuration(300)
//                .start();*/
//        ObjectAnimator setPivotX = ObjectAnimator.ofFloat(rootView, "pivotX", 0f);
//        ObjectAnimator setPivotY = ObjectAnimator.ofFloat(rootView, "pivotY", 0f);
//        ObjectAnimator setScaleY = ObjectAnimator.ofFloat(rootView, "scaleY", 1f, 0.98f);
//        ObjectAnimator setScaleX = ObjectAnimator.ofFloat(rootView, "scaleX", 1f, 0.98f);
//        AnimatorSet set = new AnimatorSet();
//        set.play(setPivotX).with(setPivotY).with(setScaleX).with(setScaleY);
//        set.setDuration(400);
//        set.start();
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, "scaleY", 0.98f, 1f);
//                ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, "scaleX", 0.98f, 1f);
//                AnimatorSet set = new AnimatorSet();
//                set.play(scaleX).with(scaleY);
//                set.setDuration(0);
//                set.setStartDelay(1000);
//                set.start();
//            }
//        });
    }

    @Override
    public void startActivity(Intent intent) {
        if (!intent.hasExtra("start_anim") || intent.getBooleanExtra("start_anim", false)) {
            super.startActivity(intent);
            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } catch (Exception e) {
                    super.startActivity(intent);
                }
            } else {
                super.startActivity(intent);
            }
            if (intent.getBooleanExtra("scale_anim", false)) {
                handleRootView();
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (!intent.hasExtra("start_anim") || intent.getBooleanExtra("start_anim", false)) {
            super.startActivityForResult(intent, requestCode);
            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    super.startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } catch (Exception e) {
                    super.startActivityForResult(intent, requestCode);
                }
            } else {
                super.startActivityForResult(intent, requestCode);
            }
            if (intent.getBooleanExtra("scale_anim", false)) {
                handleRootView();
            }
        }
    }


    /**
     * 加载对话框
     *
     * @param resId 字符串资源ID
     * @return
     */
    public Dialog createLoadingDialog(int resId) {
        return createLoadingDialog(getString(resId));
    }

    /**
     * 加载对话框
     *
     * @param msg 提示信息
     * @return
     */
    public Dialog createLoadingDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    public void setBackTip(boolean isBackTip) {
        this.isBackTip = isBackTip;
    }

    public void showDialogTip() {
        new AlertDialog(this).builder().setMsg("退出此次编辑？")
                .setPositiveButton("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollToFinishActivity();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    public void setStartAnim(boolean isStartAnim) {
        this.isStartAnim = isStartAnim;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getIntent().getBooleanExtra("isHideKeyboard", true)) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                if (MainActivity.ekBar != null && MainActivity.ekBar.getRootView() != null
                        && MainActivity.ekBar.getRootView().getVisibility() == View.VISIBLE) {
                    //如果Talker首页显示评论条，点击外部隐藏
                    if (isShouldHideKeyboard(MainActivity.ekBar.getRootView(), ev)) {
                        hideKeyboard(MainActivity.ekBar.getEtChat().getWindowToken());
                        MainActivity.ekBar.getRootView().setVisibility(View.GONE);
                        MainActivity.ekBar.getFuncLayout().hideAllFuncView();
                    }
                } else {
                    View v = getCurrentFocus();
                    if (v != null && isShouldHideKeyboard(mKeyBoradView != null ? mKeyBoradView : v, ev)) {
                        hideKeyboard(v.getWindowToken());
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                if (v instanceof EditText && v.hasFocusable()) {
                    v.clearFocus();
                }
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    public void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏软键盘
     * hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 弹出输入法窗口
     */
    public void showSoftInputView(final EditText et) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) et.getContext().getSystemService(Service.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }
}
