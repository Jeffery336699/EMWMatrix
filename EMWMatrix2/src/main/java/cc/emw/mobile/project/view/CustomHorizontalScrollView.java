package cc.emw.mobile.project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 自定义可放置ListView的横向滑动控件
 * @author jven.wu
 *
 */
public class CustomHorizontalScrollView extends HorizontalScrollView{
    public static final String TAG = "HorizontalScrollView";
    private OnScrollListener onScrollListener;
    private GestureDetector mGestureDetector;  

    public CustomHorizontalScrollView(Context context) {  
        super(context);  
        mGestureDetector = new GestureDetector(context, new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
  
    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        mGestureDetector = new GestureDetector(context, new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
  
    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        mGestureDetector = new GestureDetector(context, new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }  
  
    /**  
     * 如果竖向滑动距离<横向距离，执行横向滑动，否则竖向。如果是ScrollView，则'<'换成'>'
     */  
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {  
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {  
            if (Math.abs(distanceY) < Math.abs(distanceX)) {  
                return true;  
            }  
            return false;  
        }  
    }

    /**
     * 设置滚动接口
     * @param onScrollListener 滚动监听类
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onScrollListener != null){
            onScrollListener.onScroll(l);
        }
    }

    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener{
        /**
         * 回调方法， 返回MyScrollView滑动的X方向距离
         * @param scrollX 横行滚动值
         */
        void onScroll(int scrollX);
    }
}
