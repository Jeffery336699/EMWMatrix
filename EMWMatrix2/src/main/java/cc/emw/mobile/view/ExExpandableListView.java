package cc.emw.mobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;
import android.widget.ListView;

/**
 * ScrollView嵌套ExpandableListView中导致ExpandableListView高度计算不正确，只显示一行。
 */
public class ExExpandableListView extends ExpandableListView {

	public ExExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExExpandableListView(Context context) {
		super(context);
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int newhei=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
    	super.onMeasure(widthMeasureSpec, newhei);
    }
}
