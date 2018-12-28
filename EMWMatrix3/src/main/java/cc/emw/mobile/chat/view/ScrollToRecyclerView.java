package cc.emw.mobile.chat.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by sunny.du on 2017/4/18.
 */

public class ScrollToRecyclerView extends RecyclerView {
    private boolean isScrollFlag = false;

    public void setScrollFlag(boolean scrollFlag) {
        isScrollFlag = scrollFlag;
    }

    public boolean getScrollFlag() {
        return isScrollFlag;
    }

    public ScrollToRecyclerView(Context context) {
        this(context, null);
    }

    public ScrollToRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollToRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isScrollFlag = false;
        }
    };

    @Override
    public void smoothScrollToPosition(int position) {
        isScrollFlag = true;
        super.smoothScrollToPosition(position);
        handler.sendMessageDelayed(Message.obtain(), 1000);
    }

    boolean isEvent=true;
    public void setIsEvent(boolean isevent){
        this.isEvent=isevent;
    }
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        //TODO   isEvent
//        return false;
//    }
}
