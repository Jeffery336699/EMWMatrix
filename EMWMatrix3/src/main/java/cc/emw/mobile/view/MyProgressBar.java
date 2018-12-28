package cc.emw.mobile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cc.emw.mobile.R;

/**
 * Created by jven.wu on 2016/8/2.
 */
public class MyProgressBar extends View {
    /** 进度条 */
    private Paint mProgressPaint;
    /** 回删 */
    private Paint mRemovePaint;
    /** 最长时长 */
    private int mMax;
    /** 进度*/
    private int mProgress;
    private boolean isRemove;
    public MyProgressBar(Context Context, AttributeSet Attr) {
        super(Context, Attr);
        init();
    }
    private void init() {
        mProgressPaint = new Paint();
        mRemovePaint = new Paint();
        setBackgroundColor(getResources().getColor(R.color.transparent));
        mProgressPaint.setColor(Color.GREEN);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mRemovePaint.setColor(getResources().getColor(
                R.color.title_back));
        mRemovePaint.setStyle(Paint.Style.FILL);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        final int width = getMeasuredWidth(), height = getMeasuredHeight();
        int progressLength = (int) ((mProgress / (mMax * 1.0f)) * (width / 2));
        canvas.drawRect(progressLength, 0, width - progressLength, height, isRemove ? mRemovePaint : mProgressPaint);
        canvas.restore();
    }
    public void setMax(int max){
        this.mMax = max;
    }
    public void setProgress(int progress){
        this.mProgress = progress;
    }
    public void setRemove(boolean isRemove){
        this.isRemove = isRemove;
    }
}