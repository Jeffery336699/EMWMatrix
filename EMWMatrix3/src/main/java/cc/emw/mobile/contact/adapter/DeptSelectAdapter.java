package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.Dept;

public class DeptSelectAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Dept> mDataList;
	private int lastPosition = -1;

	private Dept targetG = new Dept();

	public DeptSelectAdapter(Context context) {
		this.mContext = context;
	}

	public Dept getTargetG() {
		return targetG;
	}

	public void setData(ArrayList<Dept> mDataList) {
		this.mDataList = mDataList;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDataList.get(position).ID;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup arg2) {
		ViewHolder vh;
		if (contentView == null) {
			vh = new ViewHolder();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_contact_dept_select, null);
			vh.headIv = (ImageView) contentView.findViewById(R.id.iv_deptselect_head);
			vh.nameTv = (TextView) contentView.findViewById(R.id.tv_deptselect_name);
			vh.checkBox = (CheckBox) contentView.findViewById(R.id.rb_groups_select);
			contentView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) contentView.getTag(R.id.tag_first);
		}

		Dept dept = mDataList.get(position);

		vh.nameTv.setText(dept.Name);

		vh.checkBox.setChecked(lastPosition == position ? true : false);
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lastPosition == position) {
					lastPosition = -1;
				} else {
					lastPosition = position;
					targetG = mDataList.get(position);
				}
				notifyDataSetChanged();
			}
		});

		return contentView;
	}

	class ViewHolder {
		ImageView headIv;
		TextView nameTv;
		CheckBox checkBox;
	}

}
