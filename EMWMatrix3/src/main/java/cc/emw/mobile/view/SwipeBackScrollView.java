package cc.emw.mobile.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 监听ScrollView滚动到顶部或者底部做相关事件拦截
 */
public class SwipeBackScrollView extends ScrollView {

    private boolean isScrolledToTop = true; // 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    private boolean mEnable = true;
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    public SwipeBackScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.SwipeBack, 0, 0);
        mEnable = attr.getBoolean(R.styleable.SwipeBack_enable_gesture, true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (context instanceof SwipeBackActivity) {
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(context) / 2 - 10);
            //防止已滑动到底部，再展开某视图(标签/重复/提醒)不能往上滑动问题
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    Logger.w("scrolls", "left:"+left+", top:"+top+", right:"+right+", bottom:"+bottom+", oldLeft:"+oldLeft+", oldTop:"+oldTop+", oldRight:"+oldRight+", oldBottom:"+oldBottom);
                    if (!mEnable) {
                        return;
                    }
                    if (!canScroll()) {
                        isScrolledToTop = true;
                        isScrolledToBottom = true;
                    } else {
                        isScrolledToBottom = false;
                    }
                    notifyScrollChangedListeners();
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!mEnable) {
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!canScroll()) {
                    isScrolledToBottom = true;
                    notifyScrollChangedListeners();
                }
            }
        }, 1000);

    }

    public void updateSwipeBackState() {
        if (!canScrollVertically(1)) {
            isScrolledToTop = false;
            isScrolledToBottom = true;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        notifyScrollChangedListeners();
    }

    //能否滑动
    private boolean canScroll() {
        View child = getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            Logger.w("scrolls", "Height:"+ getHeight() + ", childHeight:" + childHeight + ", canScroll:"+(getHeight() < childHeight));
            return getHeight() < childHeight + getPaddingTop() + getPaddingBottom();
        }
        return false;
    }

    private ISmartScrollChangedListener mSmartScrollChangedListener;

    /** 定义监听接口 */
    public interface ISmartScrollChangedListener {
        void onScrolledToBottom();
        void onScrolledToTop();
        void onScrolledToCenter();
    }

    public void setSmartScrollChangedListener(ISmartScrollChangedListener smartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int newScrollY = scrollY + deltaY;
        final int bottom = maxOverScrollY + scrollRangeY;
        final int top = -maxOverScrollY;
        if (newScrollY > bottom) {
            Logger.w("scrolls", "滑动到底端");
            isScrolledToTop = false;
            isScrolledToBottom = true;
        } else if (newScrollY < top) {
            Logger.w("scrolls", "滑动到顶端");
            isScrolledToTop = true;
            isScrolledToBottom = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        Logger.w("scrolls", "newY:"+newScrollY+", top:"+top+", bottom:"+bottom);
        notifyScrollChangedListeners();
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    private void notifyScrollChangedListeners() {
        if (isScrolledToTop && isScrolledToBottom) {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(true);
            }
        } else if (isScrolledToTop) {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(true);
            }
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(true);
            }
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToBottom();
            }
        } else {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            }
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToCenter();
            }
        }
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }
}
