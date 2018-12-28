package com.jeek.calendar.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by ${zrjt} on 2017/3/18.
 */
public class MyListViewScrollTops extends ListView {
    public MyListViewScrollTops(Context context) {
        super(context);
    }

    public MyListViewScrollTops(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrollTop() {
        return computeVerticalScrollOffset() == 0;
    }

    public boolean isScrollBottom() {
        return getLastVisiblePosition() == getCount() - 1;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);
        }
    }
}
