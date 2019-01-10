package cc.emw.mobile.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * Created by liuwei on 2017/10/13.
 */

public class UiUtil {
    /**
     * 获取图片资源
     */
    public static int[] getIcons(Context context, int arraysId) {
        TypedArray category_icons = context.getResources().obtainTypedArray(arraysId);
        int len = category_icons.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            resIds[i] = category_icons.getResourceId(i, 0);
        }
        category_icons.recycle();
        return resIds;
    }

    /**
     * 设置margin
     *
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Convert px to sp
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2sp(Context context, float px) {
        return (int) (pxToSp(context, px) + 0.5f);
    }

    /**
     * Convert px to sp
     *
     * @param context
     * @param px
     * @return
     */
    private static float pxToSp(Context context, float px) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return px / fontScale;
    }

    /**
     * Convert sp to px
     *
     * @param context
     * @param sp
     * @return
     */
    public static int sp2px(Context context, float sp) {
        return (int) (spToPx(context, sp) + 0.5f);
    }

    /**
     * Convert sp to px
     *
     * @param context
     * @param sp
     * @return
     */
    private static float spToPx(Context context, float sp) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * fontScale;
    }

    /**
     * @param srcString hint中的文字
     * @param size      hint的字体大小
     * @param editText  edittext
     */
    public static void changeTextHintSize(String srcString, int size, EditText editText) {
        SpannableString s = new SpannableString(srcString);
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(size, true);
        s.setSpan(textSize, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(s);

    }

    /**
     * 更改edittext的hint字体大小
     *
     * @param size
     * @param editText
     */
    public static void changeTextHintSize(int size, EditText editText) {
        if (TextUtils.isEmpty(editText.getHint())) {
            return;
        }
        String hint = editText.getHint().toString();
        SpannableString s = new SpannableString(hint);
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(size, true);
        s.setSpan(textSize, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(s);
    }

    /**
     * 移动光标到末尾
     *
     * @param et   EditText
     * @param text 字符串
     */
    public static void moveCursorToEnd(EditText et, String text) {
        int size = text.length();
        if (size > 0) {
            et.setSelection(size);
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        //TODO：
//        int resourceId = PcbApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = PcbApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
//        }
        return result;
    }

    /**
     * 获取toolbar的高度
     */
    public static int getToolbarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
//        if ( PcbApplication.getInstance().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,  PcbApplication.getInstance().getResources().getDisplayMetrics());
//        }
        return actionBarHeight;
    }

}
