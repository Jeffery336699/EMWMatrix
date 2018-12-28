package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * ScrollView嵌套ListView中导致ListView高度计算不正确，只显示一行。
 */
public class ExListView extends ListView {
	protected static final String TAG = "ExListView";

	public ExListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExListView(Context context) {
		super(context);
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int newhei=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
    	super.onMeasure(widthMeasureSpec, newhei);
    }
}
