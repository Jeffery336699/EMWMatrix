package cc.emw.mobile.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import cc.emw.mobile.util.DisplayUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 监听RecyclerView滚动到顶部或者底部做相关事件拦截
 */
public class SwipeBackRecyclerView extends RecyclerView {

    private boolean isScrolledToTop = true; // 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    //在刷新RecyclerView数据时,会执行onScroll(),标志这个属性状态避免在ViewPager中各界面相互影响，不是第一个显示的界面，初始化都需要调用setEnableGesture(false)
    //再通过onPageSelected(int position)，发送各自广播调用setEnableGesture(boolean enable)，设置相应的值
    private boolean mEnable = true;
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    public SwipeBackRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (context instanceof SwipeBackActivity) {
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(context) - 100);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            /**
             * addOnScrollListener调用后，当前页面滑动到顶部再向上滑动会导致退出页面及应用。暂时弃用该效果
             */
            //addOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        }
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

    public void notifyScrollChangedListeners() {
        if (!mEnable) {
            return;
        }
        if (isScrolledToTop) {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            }
        } else if (isScrolledToBottom) {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(true);
            }
        } else {
            if (getContext() instanceof SwipeBackActivity) {
                ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            }
        }
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }


    private class PauseOnScrollListener extends RecyclerView.OnScrollListener {

        private ImageLoader imageLoader;
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;

        public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.imageLoader = imageLoader;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!mEnable) {
                return;
            }
            Log.d("canscroll", recyclerView.toString()+":"+recyclerView.canScrollVertically(1));
            updateSwipeBackState();
        }

        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；由于用户的操作，屏幕产生惯性滑动时为2
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            //根据newState状态做处理
            if (imageLoader != null) {
                switch (newState) {
                    case 0:
                        imageLoader.resume();
                        break;
                    case 1:
                        if (pauseOnScroll) {
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;
                    case 2:
                        if (pauseOnFling) {
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;
                }
            }
        }
    }
}
