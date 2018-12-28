package cc.emw.mobile.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by tao.zhou on 2017/5/22.
 */

public class MyFixRecycleView extends RecyclerView {

    public MyFixRecycleView(Context context) {
        super(context);
    }

    public MyFixRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFixRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
