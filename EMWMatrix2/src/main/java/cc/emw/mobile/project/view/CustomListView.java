package cc.emw.mobile.project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by jven.wu on 2016/5/20.
 */
public class CustomListView extends ListView {
    private OnScrollYListener listener;

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null){
            listener.onScroll(t);
        }
    }

    /**
     * 滚动回调接口
     */
    public interface OnScrollYListener{
        /**
         * 回调方法，返回Y方向滑动距离
         * @param scrollY 垂直滚动值
         */
        void onScroll(int scrollY);
    }

    /**
     * 设置监听垂直滚动接口
     * @param listener
     */
    public void setOnScrollYListener(OnScrollYListener listener){
        this.listener = listener;
    }
}
