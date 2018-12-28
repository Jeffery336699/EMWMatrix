package cc.emw.mobile.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 监听GridView滚动到顶部或者底部做相关事件拦截
 */
public class SwipeBackGridView extends GridView {

    private boolean isScrolledToTop = true; // 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    //在刷新GridView数据时,会执行onScroll(),标志这个属性状态避免在ViewPager中各界面相互影响，不是第一个显示的界面，初始化都需要调用setEnableGesture(false)
    //再通过onPageSelected(int position)，发送各自广播调用setEnableGesture(boolean enable)，设置相应的值
    private boolean mEnable = true;
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    public SwipeBackGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (context instanceof SwipeBackActivity) {
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(context) - 100);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true){
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    if (!mEnable) {
                        return;
                    }
                    Log.d("canscroll", view.toString()+":"+view.canScrollVertically(1));
                    updateSwipeBackState();
                }
            });
        }
    }

    public void updateSwipeBackState() {
        //滚动条滚到最底部
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
}
