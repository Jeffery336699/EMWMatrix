package cc.emw.mobile.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 仿微信添加触摸图片阴影效果
 */
public class ShadowImageViews extends ImageView {
    public ShadowImageViews(Context context) {
        super(context);
    }

    public ShadowImageViews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowImageViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShadowImageViews(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setColorFilter(Color.parseColor("#77000000"));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                setColorFilter(null);
                break;
        }


        return super.onTouchEvent(event);
    }
}
