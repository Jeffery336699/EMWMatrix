package cc.emw.mobile.calendar.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.CalendarInfo;

/**
 * @author zrjt
 */
public class CalendarMonthAdapter extends BaseAdapter {

	private List<CalendarInfo> mDataList;

	private Context mContext;

	public CalendarMonthAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void setData(List<CalendarInfo> mDataList) {
		this.mDataList = mDataList;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_calendar_scan_info, null);
		}
		ViewHolder vh = (ViewHolder) view.getTag();
		if (vh == null) {
			vh = new ViewHolder();
			vh.textView = (TextView) view
					.findViewById(R.id.tv_calendar_scan_info);
			view.setTag(vh);
		}

		CalendarInfo calInfo = mDataList.get(position);

		vh.textView.setText(calInfo.Title);

		int colorId = calInfo.Color;
		switch (colorId) {
		case 0:
			vh.textView.setBackgroundResource(R.color.cal_color0);
			break;
		case 1:
			vh.textView.setBackgroundResource(R.color.cal_color1);
			break;
		case 2:
			vh.textView.setBackgroundResource(R.color.cal_color2);
			break;
		case 3:
			vh.textView.setBackgroundResource(R.color.cal_color3);
			break;
		case 4:
			vh.textView.setBackgroundResource(R.color.cal_color4);
			break;
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
			}
		});
		return view;
	}

	private class ViewHolder {
		TextView textView;
	}

}
