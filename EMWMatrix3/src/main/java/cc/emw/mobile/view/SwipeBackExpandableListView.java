package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;

import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 监听ExpandableListView滚动到顶部或者底部做相关事件拦截
 */
public class SwipeBackExpandableListView extends FloatingGroupExpandableListView {

    private boolean isScrolledToTop = true; // 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    private boolean mEnable = true;
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    public SwipeBackExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (context instanceof SwipeBackActivity) {
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(context) - 100);
            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
            setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    switch (scrollState) {
                        case OnScrollListener.SCROLL_STATE_IDLE: //停止滚动
                            // 判断滚动到底部
                            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                                view.setSelection(view.getLastVisiblePosition()); //完全显示最后的item
                            }
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Logger.w("sss", "onScroll:"+mEnable);
                    if (!mEnable) {
                        if (getContext() instanceof SwipeBackActivity) {
                            ((SwipeBackActivity)getContext()).getSwipeBackLayout().setEnableGesture(false);
                        }
                        return;
                    }

                    //滚动条滚到最底部
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        isScrolledToTop = false;
                        isScrolledToBottom = true;
                    } else {
                        isScrolledToTop = false;
                        isScrolledToBottom = false;
                    }
                    notifyScrollChangedListeners();
                }
            });
        }
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
