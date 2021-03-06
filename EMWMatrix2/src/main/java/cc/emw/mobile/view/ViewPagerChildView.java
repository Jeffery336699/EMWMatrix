package cc.emw.mobile.view;

import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

/**
 * 解决当photoview嵌套在viewpager中的时候就会出现缩小崩溃问题。
 * 
 * @author shaobo.zhuang
 * 
 */
public class ViewPagerChildView extends ViewPager {

	public ViewPagerChildView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public ViewPagerChildView(Context context) {
		super(context);
	
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
