package cc.emw.mobile.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.LinearLayout;

/**
 * Created by jven.wu on 2016/8/16.
 */
public class AnimUtil {
    public static void setHideShowAnim(int transVal, int time, LinearLayout linearLayout,ValueAnimator.AnimatorUpdateListener listener) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(linearLayout, "translationY", transVal);
        if(listener != null && true) {
            oa.addUpdateListener(listener);
        }
        oa.setDuration(time);
        oa.start();
    }

    public static void setHideShowAnim(int transVal, int time, LinearLayout linearLayout){
        setHideShowAnim(transVal,time,linearLayout,null);
    }
}
