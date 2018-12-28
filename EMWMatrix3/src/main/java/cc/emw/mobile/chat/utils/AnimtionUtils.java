package cc.emw.mobile.chat.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by sunny.du on 2017/6/27.
 * 动画工具类
 */

public class AnimtionUtils {
    private  static AnimatorSet set = new AnimatorSet();
    public static final AnimtionUtils animUtil = new AnimtionUtils();
    private AnimtionUtils(){}
    public static AnimtionUtils getanimUtil(){
        return animUtil;
    }
    /**
     * 设置缩放动画
     */
    public static void setScalse(View view) {
        ObjectAnimator animator_x = ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f);
        ObjectAnimator animator_y = ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f);
        set.play(animator_x).with(animator_y);
        set.setDuration(500);
        set.start();
    }

    public void showPupoWindodAmin(final View view, float start, final float end, final int duation, final boolean isWhile){
        ValueAnimator va=ValueAnimator.ofFloat(start,end).setDuration(duation);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                view.setPivotX(view.getWidth());
                view.setPivotY(0);
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(isWhile){
                    showPupoWindodAmin(view,end,0.95f,duation/3,false);
                }
            }
        });
    }

}
