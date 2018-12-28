package cc.emw.mobile.contact;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;

/**
 * Created by ${zrjt} on 2016/5/31.
 */
public class ContactSideBar extends View {

    // 触摸事件
    private OnTouchChangeListener onTouchChangeListener;
    private List<String> lists = new ArrayList<>();
    private Paint paint = new Paint();
    private int choose = -1;    //选中状态
    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    public ContactSideBar(Context context) {
        super(context);
    }

    public ContactSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContactSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        if (lists.size() > 0) {
            int singleHeight = height / lists.size();
            for (int i = 0; i < lists.size(); i++) {
                paint.setColor(Color.rgb(192, 192, 192));
                paint.setAntiAlias(true);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(20);
                if (i == choose) {
                    paint.setColor(Color.parseColor("#3399ff"));
                    paint.setFakeBoldText(true);
                }
                // x坐标等于中间-字符串宽度的一半.
                float xPos = width / 2 - paint.measureText(lists.get(i)) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(lists.get(i), xPos, yPos, paint);
                paint.reset();// 重置画笔
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchChangeListener listener = onTouchChangeListener;
        final int c = (int) (y / getHeight() * lists.size());// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < lists.size()) {
                        if (listener != null) {
                            listener.onTouchChanged(lists.get(c));
                        }
                        if (mTextDialog != null && !lists.get(c).equals("")) {
                            mTextDialog.setText(lists.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchChangeListener
     */
    public void setOnTouchingChangedListener(
            OnTouchChangeListener onTouchChangeListener) {
        this.onTouchChangeListener = onTouchChangeListener;
    }

    public interface OnTouchChangeListener {
        public void onTouchChanged(String s);
    }
}
