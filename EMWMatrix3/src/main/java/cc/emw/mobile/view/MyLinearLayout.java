package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by jven.wu on 2016/8/3.
 */
public class MyLinearLayout extends LinearLayout {
    public static final String TAG = "MyLinearLayout";

    private OnEventUPListener mOnEventUPListener;
    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnEventUPListener(OnEventUPListener listener){
        this.mOnEventUPListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        switch (action)
//        {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "myll onTouchEvent ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "myll onTouchEvent ACTION_MOVE");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "myll onTouchEvent ACTION_UP");
//                break;
//            default:
//                break;
//        }
        if(mOnEventUPListener != null){
            mOnEventUPListener.onEventUp(event);
        }
        requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public interface OnEventUPListener{
        void onEventUp(MotionEvent event);
    }
}
