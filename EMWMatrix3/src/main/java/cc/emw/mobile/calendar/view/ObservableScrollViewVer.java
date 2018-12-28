package cc.emw.mobile.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import cc.emw.mobile.calendar.inter.ScrollViewListener;

/**
 * Created by ${zrjt} on 2016/11/11.
 */
public class ObservableScrollViewVer extends ScrollView{

    private ScrollViewListener scrollViewListener = null;

    private View mView;

    public ObservableScrollViewVer(Context context) {
        super(context);
    }

    public ObservableScrollViewVer(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollViewVer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChangedVer(this, x, y, oldx, oldy);
        }
        if(mView!=null){
            mView.scrollTo(mView.getScrollX(), y);
        }
    }

    public void setScrollView(View view){
        mView = view;
    }

}
