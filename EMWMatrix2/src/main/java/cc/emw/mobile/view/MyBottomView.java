package cc.emw.mobile.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by xiang.peng on 2016/5/26.
 */
public class MyBottomView extends RelativeLayout {

    private static final int SNAP_VELOCITY = 800;

    private View mContentView;


    private int bottomFlexHeight = 0;
    private int mMaxtranslationY;
    private int touchHeight = 80;

    private boolean isDeal = true;

    private VelocityTracker velocityTracker;
    private ScrollTopListener mTopListener;
    private ScrollBottomListener mBottomListener;

    public MyBottomView(Context context) {
        this(context, null);
    }

    public MyBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);
        mMaxtranslationY = mContentView.getHeight() - bottomFlexHeight;
        mContentView.setY(mMaxtranslationY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);
        addVelocityTracker(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                isDeal = !isInRect(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDeal) {
                    mContentView.setY(event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int velocityY = getScrollYVelocity();
                if (isDeal) {
                    if (mContentView.getTranslationY() < (mMaxtranslationY / 2)) {
                        if (velocityY > SNAP_VELOCITY) {
                            scrollToBottom();
                        } else {
                            scrollToTop();
                        }
                    } else {
                        if (velocityY <= -SNAP_VELOCITY) {
                            scrollToTop();
                        } else {
                            scrollToBottom();
                        }
                    }
                }
                isDeal = false;
                recycleVelocityTracker();

                break;
        }
        return true;
    }

    /**
     * 添加用的速度跟踪器
     */

    private void addVelocityTracker(MotionEvent event){
        if (velocityTracker==null){
            velocityTracker=VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {

        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 根据监听滑动到顶部
     */

    private void scrollToTop() {
        ObjectAnimator topAnimation = ObjectAnimator.ofFloat(mContentView,
                "translationY", mContentView.getTranslationY(), 0);
        if (mTopListener != null) {
            topAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mTopListener.onScrollTop();
                }
            });
        }
        topAnimation.start();

    }

    /**
     * 设置底部漏出的高度
     *
     */
    public void setBottomFlexHeight(int height) {
        bottomFlexHeight = height;
    }


    public void scrollToBottom() {
        ObjectAnimator bottomAnimation = ObjectAnimator.ofFloat(mContentView,
                "translationY", mContentView.getTranslationY(),
                (mMaxtranslationY));
        if (mBottomListener != null) {
            bottomAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBottomListener.onScrollBottom();
                }
            });
        }
        bottomAnimation.start();

    }

    /**
     * 判断按下的点是否在指定区域内,不在区域内不做处理
     */
    private boolean isInRect(float downX, float downY) {
        if (downX > mContentView.getLeft() && downX < mContentView.getRight()) {
            if (downY >= mContentView.getTranslationY()
                    && downY <= mContentView.getTranslationY() + touchHeight) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取Y方向的滑动速度
     */
    private int getScrollYVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        return (int) velocityTracker.getYVelocity();
    }

    /**
     * @description 滑动到底部事件监听
     */
    public interface ScrollBottomListener {
        void onScrollBottom();
    }

    /**
     * @description 滑动到顶部事件监听
     */
    public interface ScrollTopListener {
        void onScrollTop();
    }
}