package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by jven.wu on 2016/8/3.
 */
public class MyButtonButton extends Button {

    private static final String TAG = "MyButtonButton";

    public MyButtonButton(Context context) {
        this(context,null);
    }

    public MyButtonButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyButtonButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "mybtn onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "mybtn onTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "mybtn onTouchEvent ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "mybtn onTouchEvent ACTION_CANCEL");
                break;
            default:
                break;
        }
        return false;
    }
}
