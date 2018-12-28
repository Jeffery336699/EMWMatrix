package cc.emw.mobile.task.adapter;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.TaskUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DevideAttachmentAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Files> mDataList;
	//是否显示可删除按钮
	private Boolean mCanDel = false;
	
	public DevideAttachmentAdapter(Context context){
		this.mContext = context;
		mDataList = new ArrayList<Files>();
	}
	
	public void setArrayData(List<Files> dataList,Boolean canDel) {
		if (dataList != null) {
			this.mCanDel = canDel;
			this.mDataList.clear();
			this.mDataList.addAll(dataList);
		}
	}
	
	public ArrayList<Files> getArrayData(){
		return mDataList;
	}
	
	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}
	@Override
	public Object getItem(int position) {
		return mDataList != null ? mDataList.get(position) : null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.listitem_task_devideattachment, parent,false);
			vh.icon = (ImageView)convertView
					.findViewById(R.id.iv_task_devide_icon);
			vh.name = (TextView)convertView
					.findViewById(R.id.tv_task_devide_name);
			vh.delBtn = (ImageView)convertView
					.findViewById(R.id.iv_task_devide_del);
			vh.size=(TextView) convertView.findViewById(R.id.tv_task_devide_size);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}
		final Files data = mDataList.get(position);
		if(data != null){
			vh.icon.setImageResource(FileUtil.getResIconId(data.Name));
			vh.size.setText(FileUtil.getReadableFileSize(data.Length));// 将Long转换成文件大小
			vh.name.setText(data.Name);
			vh.delBtn.setVisibility(mCanDel?View.VISIBLE:View.INVISIBLE);
			if(mCanDel){
				vh.delBtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mDataList.remove(data);
						notifyDataSetChanged();
						TaskUtils.setListViewHeightBasedOnChildren((ListView)parent);
					}
				});
			}
		}
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView icon;
		TextView name;
		ImageView delBtn;
		TextView size;
	}
}
