package cc.emw.mobile.chat.view;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by sunny.du on 2016/10/25.
 */
public class MyCollapsingToolbarLayout extends CollapsingToolbarLayout {

    public MyCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setExpandedTitleColor(int color) {
        super.setExpandedTitleColor(color);
    }

    @Override
    public void setCollapsedTitleTextColor(int color) {
        super.setCollapsedTitleTextColor(color);
    }
}
