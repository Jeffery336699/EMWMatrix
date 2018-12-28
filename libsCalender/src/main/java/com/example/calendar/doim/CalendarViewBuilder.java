package com.example.calendar.doim;

import android.content.Context;

import com.example.calendar.widget.CalendarView;
import com.example.calendar.widget.CalendarView.CallBack;

/**
 * CalendarView�ĸ�����
 * 
 * @author huang
 * 
 */
public class CalendarViewBuilder {
	private CalendarView[] calendarViews;

	/**
	 * �����CalendarView
	 * 
	 * @param context
	 * @param count
	 * @param style
	 * @param callBack
	 * @return
	 */
	public CalendarView[] createMassCalendarViews(Context context, int count,
			int style, CallBack callBack) {
		calendarViews = new CalendarView[count];
		for (int i = 0; i < count; i++) {
			calendarViews[i] = new CalendarView(context, style, callBack);
		}
		return calendarViews;
	}

	public CalendarView[] createMassCalendarViews(Context context, int count,
			CallBack callBack) {

		return createMassCalendarViews(context, count,
				CalendarView.MONTH_STYLE, callBack);
	}

	/**
	 * @param style
	 */
	public void swtichCalendarViewsStyle(int style) {
		if (calendarViews != null)
			for (int i = 0; i < calendarViews.length; i++) {
				calendarViews[i].switchStyle(style);
			}
	}

	/**
	 * CandlendarView�ص���ǰ����
	 */

	public void backTodayCalendarViews() {
		if (calendarViews != null)
			for (int i = 0; i < calendarViews.length; i++) {
				calendarViews[i].backToday();
			}
	}
}
