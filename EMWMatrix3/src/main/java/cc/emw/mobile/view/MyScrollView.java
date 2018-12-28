package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private int x, y;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下y
                x = (int) ev.getX();
                y = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE://移动
                int new_x = (int) ev.getX();
                int new_y = (int) ev.getY();

                int move_x = Math.abs(new_x - x);//x轴滑动的距离
                int move_y = Math.abs(new_y - y);//y轴滑动的距离

                //判断有水平滑动的意向
//                if (move_x > (move_y + 10))//10的偏移量
//                {
//                    return false;//传递给字View
//                }

                //判断有上下滑动的意向（用于字VIew是上下，parent是水平的）
                if (move_y > (move_x + 10))//10的偏移量
                {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);

    }
}