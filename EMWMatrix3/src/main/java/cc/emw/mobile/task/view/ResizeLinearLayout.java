package cc.emw.mobile.task.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLinearLayout extends LinearLayout {

	private OnSizeChangeListener mOnSizeChangeListener;


	public void setmOnSizeChangeListener(OnSizeChangeListener mOnSizeChangeListener) {
		this.mOnSizeChangeListener = mOnSizeChangeListener;
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs,
							  int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(oldh != 0){
			if(mOnSizeChangeListener != null){
				mOnSizeChangeListener.onSizeChange(h, oldh);
			}
		}
	}
	public interface OnSizeChangeListener{
		void onSizeChange(int h, int oldh);
	}
}
