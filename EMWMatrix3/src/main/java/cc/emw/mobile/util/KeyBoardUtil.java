package cc.emw.mobile.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ${zrjt} on 2016/12/26.
 */
public class KeyBoardUtil {

    private static InputMethodManager imm;

    /****
     * 强制关闭键盘(无论是否打开或者关闭)
     */
    public static boolean closeKeyboard(Context context) {
        try {
            Activity activity=(Activity)context;
            View viewKeyboard = activity.getCurrentFocus();
            assert viewKeyboard != null;
            imm = (InputMethodManager) viewKeyboard.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            viewKeyboard.clearFocus();
            imm.hideSoftInputFromWindow(viewKeyboard.getWindowToken(), 0);

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭或者打开软键盘
     */
    public static boolean openOrCloseSoftInput(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 这里分两组情况 一：如果开启，就隐藏 二：如果隐藏，就打开
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
        return true;
    }

    /**
     * 关闭或者打开软键盘
     */
    public static void colseSoftInput(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 这里分两组情况 一：如果开启，就隐藏 二：如果隐藏，就打开
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    0);

        }
    }

    /**
     * 关闭或者打开软键盘
     */
    public static void openSoftInput(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 这里分两组情况 一：如果开启，就隐藏 二：如果隐藏，就打开
            imm.toggleSoftInput(0,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    /**
     * 隐藏输入法
     *
     * @param view 接受软键盘输入的视图
     */
    public static void hideSoftInput(View view, Context context) {
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
