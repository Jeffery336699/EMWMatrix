package cc.emw.mobile.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.view.FlowLayout.LayoutParams;

/**
 * 此类为人员选择样式类
 */
public class SalesChartTextView extends TextView {
	protected static final String TAG = "PersonTextView";

	public SalesChartTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SalesChartTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SalesChartTextView(Context context, int BgID, boolean canDelete){
		super(context);
		this.setTextAppearance(getContext(), R.style.tv_select_person);
		if(canDelete){
			Drawable drawable=context.getResources().getDrawable(R.drawable.presonview_img_del);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			this.setCompoundDrawables(null, null, drawable, null);
		}
		this.setBackgroundResource(BgID);
		this.setPadding(10, 6, 10, 6);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 3;
		params.bottomMargin = 3;
		params.leftMargin =3;
		params.rightMargin = 3;
		this.setLayoutParams(params);
	}
	public SalesChartTextView(Context context) {
		super(context);
		this.setTextAppearance(getContext(), R.style.tv_sales_chart);
		Drawable drawable=context.getResources().getDrawable(R.drawable.presonview_img_del);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		this.setCompoundDrawables(drawable, null, null, null);
//		this.setBackgroundResource(R.drawable.persontext_bg);
		this.setPadding(10, 6, 10, 6);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 3;
		params.bottomMargin = 3;
		params.leftMargin =3;
		params.rightMargin = 3;
		this.setLayoutParams(params);
	}

}
