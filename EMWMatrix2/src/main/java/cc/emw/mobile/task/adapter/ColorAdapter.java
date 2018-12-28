package cc.emw.mobile.task.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.constant.TaskConstant;

public class ColorAdapter extends BaseAdapter {
	private ArrayList<Integer> mDatas = new ArrayList<Integer>();
	private Context mContext;
	// private int mPosition = -1;// 记录选择的位置
	public static final String[] colorStr = {
			TaskConstant.TaskEmergencyState.NORMAL,
			TaskConstant.TaskEmergencyState.EMERGENCY,
			TaskConstant.TaskEmergencyState.VERY_EMERGENCY };
	public static final int[] colorIcon = { R.drawable.shape_ico_blue,
			R.drawable.shape_ico_orange, R.drawable.shape_ico_red };

	public ColorAdapter(Context context) {
		mContext = context;
		// 初始化数据
		for (int i = 0; i < 3; i++) {
			mDatas.add(i);
		}
	}

	@Override
	public int getCount() {
		return mDatas != null ? mDatas.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mDatas != null ? mDatas.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.listitem_task_modify_color, null);
			vh = new ViewHolder();
			vh.ivIcon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
			vh.ivIsSelected = (ImageView) convertView
					.findViewById(R.id.iv_task_select);
			vh.tvEmergency = (TextView) convertView
					.findViewById(R.id.tv_task_emergency);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		// 设置数据
		int data = mDatas.get(position);
		vh.ivIcon.setBackgroundResource(colorIcon[data]);
		vh.tvEmergency.setText(colorStr[data]);
		// vh.ivIsSelected.setVisibility(mPosition==position?View.VISIBLE:View.GONE);
		/*
		 * convertView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (mPosition==position) {
		 * mPosition=-1; }else{ mPosition=position; } notifyDataSetChanged(); }
		 * });
		 */
		return convertView;
	}

	class ViewHolder {
		TextView tvEmergency;
		ImageView ivIcon;
		ImageView ivIsSelected;
	}
}
