package cc.emw.mobile.contact.util;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by tao.zhou on 2017/4/22.
 */

public class AnimUtils {

    //添加中间展开动画
    public static void showExpandAnim(Bundle savedInstanceState, final View rootView, final View childView, final int height, final Animator.AnimatorListener listener) {
        if (savedInstanceState == null) {
            rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startRootAnim(rootView, height, listener);
                    startChildAnim(childView);
                    return true;
                }
            });
        }
    }

    public static void showExpandAnim(Bundle savedInstanceState, final View rootView, final View childView, final int height) {
        showExpandAnim(savedInstanceState, rootView, childView, height, null);
    }

    //缩放动画
    private static void startRootAnim(View rootView, int height, Animator.AnimatorListener listener) {
        rootView.setScaleY(0.1f);
        rootView.setPivotY(height);
        rootView.animate()
                .scaleY(1.0f)
                .setDuration(400)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(listener)
                .start();
    }

    //渐变动画
    private static void startChildAnim(View childView) {
        childView.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setStartDelay(400)
//                        .setListener(listener)
                .start();
    }

    public static void closeAnim(View rootView, View childView, int height, Animator.AnimatorListener listener) {
//        rootView.setScaleY(0.1f);
        childView.setAlpha(0);
        rootView.setPivotY(height);
        rootView.animate()
                .scaleY(0.0f)
                .setDuration(400)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(listener)
                .start();
    }
}
