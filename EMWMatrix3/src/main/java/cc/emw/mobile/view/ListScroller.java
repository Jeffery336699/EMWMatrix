package cc.emw.mobile.view;

import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

/**
 * Created by tao.zhou on 2017/7/13.
 */

public class ListScroller implements AbsListView.OnScrollListener {
    Context context;

    public ListScroller(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState ) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            //如果在暂停或者触摸的情况下完成重置
            picasso.resumeTag(context);
        } else {
            //停止更新
            picasso.pauseTag(context);
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


}
