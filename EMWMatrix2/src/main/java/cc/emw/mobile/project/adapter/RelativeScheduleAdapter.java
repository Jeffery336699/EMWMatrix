package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.util.DisplayUtil;

public class RelativeScheduleAdapter extends BaseAdapter {
	private static final String TAG = "RelativeScheduleAdapter";
	private Context mContext;
	private ArrayList<CalendarInfo> userSchedules;
	private ArrayList<CalendarInfo> mScheduleRet;
	private Map<Integer, Integer> colorMap;
	private Map<Integer, Boolean> selMap;
	public RelativeScheduleAdapter(Context context){
		this.mContext = context;
		userSchedules = new ArrayList<CalendarInfo>();
		mScheduleRet = new ArrayList<CalendarInfo>();
		colorMap = new HashMap<Integer, Integer>();
		colorMap.put(0, R.drawable.round_icon_blue);
		colorMap.put(1, R.drawable.round_icon_blue);
		colorMap.put(2, R.drawable.round_icon_orange);
		colorMap.put(3, R.drawable.round_icon_red);
		colorMap.put(4, R.drawable.round_icon_red);
		colorMap.put(5, R.drawable.round_icon_red);
		colorMap.put(6, R.drawable.round_icon_red);
	}
	
	public void setArraySchedules(List<CalendarInfo> schedules) {
		if (schedules != null) {
			this.userSchedules.addAll(schedules);
			selMap = new HashMap<Integer, Boolean>();
			for(int i = 0;i<schedules.size();i++){
				selMap.put(i, false);
			}
		}
	}
	
	//设置项目,若项目中有任务，则显示时让任务打勾
	public void setSchedules(String scheduString){
		if(scheduString != null 
				&& !scheduString.equals("")
				&& !scheduString.equals("[]")){
			String[] strings;
        	String scheduleString = scheduString;
        	scheduleString =scheduleString.substring(1,scheduleString.length()-1);
    		strings = scheduleString.split(",");
    		for(int i = 0;i<userSchedules.size();i++){
    			selMap.put(i, false);
    			for(int j=0;j<strings.length;j++){
            		if(userSchedules.get(i).ID == Integer.valueOf(strings[j]).intValue()){
            			selMap.put(i, true);
            			break;
            		}
            	}
    		}
		}
	}
	
	public ArrayList<CalendarInfo> getSelSchedule(){
		for(int i = 0;i<userSchedules.size();i++){
			if(selMap.get(i)){
				mScheduleRet.add(userSchedules.get(i));
			}
		}
		return this.mScheduleRet;
	}
	
	@Override
	public int getCount() {
		return userSchedules != null ? userSchedules.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return userSchedules != null ? userSchedules.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(
					R.layout.listitem_project_relativetask, parent, false);
			vh.name = (TextView) convertView
					.findViewById(R.id.tv_summarytask_name);
			vh.description = (TextView) convertView
					.findViewById(R.id.tv_summarytask_description);	
			vh.selImg = (ImageView)convertView
					.findViewById(R.id.iv_relativetask_select);
			vh.arrowImg = (ImageView)convertView
					.findViewById(R.id.iv_summarytask_arrow);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		CalendarInfo schedule = userSchedules.get(position);
		Log.d(TAG, schedule.Color+"");
		vh.arrowImg.setImageResource(colorMap.get(schedule.Color>=0?schedule.Color:0));
		vh.arrowImg.setLayoutParams(
				new RelativeLayout.LayoutParams(DisplayUtil.dip2px(mContext, 8), DisplayUtil.dip2px(mContext, 8)));
		vh.name.setText(schedule.Title);
		vh.description.setText(schedule.StartTime);
		vh.description.setTextColor(
				parent.getContext().getResources().getColor(R.color.blue_19));
		vh.selImg.setImageResource(
				selMap.get(position)?R.drawable.ico_duoxuan:R.drawable.cm_multi_select_nor);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selMap.put(position, selMap.get(position) ? false : true);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	class ViewHolder {
		TextView name;
		TextView description;
		ImageView arrowImg;
		ImageView selImg;
	}
}
