package cc.emw.mobile.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ${zrjt} on 2016/11/3.
 */
public class MyRecyclerView extends RecyclerView implements View.OnTouchListener{

    private int x, y;
    private CollapsingColor collapsingColor;

    public void setCollapsingColor(CollapsingColor collapsingColor) {
        this.collapsingColor = collapsingColor;
    }

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下y
                x = (int) ev.getX();
                y = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE://移动
                int new_x = (int) ev.getX();
                int new_y = (int) ev.getY();

                //判断有上下滑动的意向（用于字VIew是上下，parent是水平的）
                int move_x = new_x - x;//x轴滑动的距离
                int move_y = new_y - y;//y轴滑动的距离
                if (move_y > (move_x + 200))//10的偏移量
                    collapsingColor.isCollapsing(true);
                else if (move_y > -(move_x + 200))
                    collapsingColor.isCollapsing(false);
                break;
        }
        return false;
    }
}
