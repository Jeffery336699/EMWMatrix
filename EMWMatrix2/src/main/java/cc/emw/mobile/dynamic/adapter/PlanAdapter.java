package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserPlan;


/**
 * 新建工作计划Adapter
 * @author shaobo.zhuang
 */
public class PlanAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<UserPlan> mDataList;
	
	public PlanAdapter(Context context,
			ArrayList<UserPlan> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
	}
	
	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_plan, null);
	    	vh.delBtn = (ImageButton) convertView.findViewById(R.id.workplan_btn_itemdel);
	    	vh.delBtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {
                	int position=(Integer) holder.contentEt.getTag();
					mDataList.remove(position);
					notifyDataSetChanged();
                }
            });
			vh.nameTv = (TextView) convertView.findViewById(R.id.workallot_tv_itemtask);
	    	vh.contentEt = (EditText) convertView.findViewById(R.id.workplan_et_itemcontent);
	    	vh.contentEt.setTag(position);
	    	vh.contentEt.addTextChangedListener(new MyTextWatcher(vh) {
	            @Override
	            public void afterTextChanged(Editable s, ViewHolder holder) {
	                int position=(Integer) holder.contentEt.getTag();
	                UserPlan p = mDataList.get(position);
	                p.Name = s.toString();
	                mDataList.set(position, p);
	            }
	        });
	    	vh.timeEt = (TextView) convertView.findViewById(R.id.workplan_et_itemtime);
	    	vh.timeLayout = (LinearLayout) convertView.findViewById(R.id.workplan_ll_itemtime);
//	    	vh.timeSelectBtn = (Button) convertView.findViewById(R.id.workplan_btn_timeselect);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
			vh.contentEt.setTag(position);
		}
		final UserPlan plan = mDataList.get(position);
		vh.nameTv.setText("计划" + (position + 1));
		vh.contentEt.setText(plan.Name);
		vh.contentEt.setHint("计划" + (position + 1));
		vh.timeEt.setText(plan.EndTime);
		final TimePickerView pwTime = new TimePickerView(mContext, Type.YEAR_MONTH_DAY);//时间选择器
		pwTime.setTitle("开始时间");
		pwTime.setCancelable(true);
        pwTime.setOnTimeSelectListener(new OnTimeSelectListener() { //时间选择后回调

            @Override
            public void onTimeSelect(Date date) {
//            	CalendarUtil calendarUtil = new CalendarUtil();
//            	if (calendarUtil.isTimeInner(getTime(date))) {
            		plan.EndTime = getTime(date);
            		vh.timeEt.setText(getTime(date));
//            	} else {
//            		Toast.makeText(mContext, "不在日期范围内，请重新选择！", Toast.LENGTH_SHORT).show();
//            	}
            }
        });
        vh.timeLayout.setOnClickListener(new OnClickListener() {//弹出时间选择器

            @Override
            public void onClick(View v) {
//                pwTime.showAtLocation(v, Gravity.BOTTOM, 0, 0,new Date());
            	pwTime.show();
            }
        });
        
		return convertView;
	}

	static class ViewHolder {
		ImageButton delBtn;
		TextView nameTv;
		EditText contentEt;
		TextView timeEt;
		LinearLayout timeLayout;
//		Button timeSelectBtn;
	}
	
	public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    } 
	
	
	private abstract class MyTextWatcher implements TextWatcher{
        private ViewHolder mHolder;
        
        public MyTextWatcher(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, mHolder);
        }
        public abstract void afterTextChanged(Editable s,ViewHolder holder);
    }
	private abstract class MyOnClickListener implements OnClickListener{
        
        private ViewHolder mHolder;
        
        public MyOnClickListener(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }
        public abstract void onClick(View v,ViewHolder holder);
        
    }
}
