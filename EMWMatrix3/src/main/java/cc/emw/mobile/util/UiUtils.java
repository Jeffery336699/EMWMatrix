package cc.emw.mobile.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

/**
 * Created by sunny.du on 2016/12/5.
 * 工具类整合了便捷操作UI显示、隐藏等功能
 */
public class UiUtils {

    /**
     * 批量操作控件隐藏
     */
    public static void executeViewGONE(View... goneViews) {
        for (View v : goneViews) {
            if (v != null)
                v.setVisibility(View.GONE);
        }
    }

    /**
     * 批量操作控件显示
     */
    public static void executeViewVISIBLE(View... visibleViews) {
        for (View v : visibleViews) {
            v.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 批量操作控件不取消焦点隐藏
     */
    public static void executeViewINVISIBLE(View... invisibleViews) {
        for (View v : invisibleViews) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 放大用户按钮交互的作用范围工具类
     *
     * @param view             需要放大的组件
     * @param expandTouchWidth 放大的范围(PX)
     */
    public static void setTouchDelegate(final View view, final int expandTouchWidth) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                final Rect rect = new Rect();
                view.getHitRect(rect);
                rect.top -= expandTouchWidth;
                rect.bottom += expandTouchWidth;
                rect.left -= expandTouchWidth;
                rect.right += expandTouchWidth;
                TouchDelegate touchDelegate = new TouchDelegate(rect, view);
                parentView.setTouchDelegate(touchDelegate);
            }
        });
    }
}
