package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.util.HelpUtil;


/**
 * 工作分派项任务列表Adapter
 * @author shaobo.zhuang
 */
public class DynamicAllotAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<UserFenPai> mDataList;
	private UserNote mUserNote;
	private DisplayImageOptions options;
	
	public DynamicAllotAdapter(Context context,
			UserNote un) {
		this.mContext = context;
		this.mDataList = un.info.task;
		this.mUserNote = un;
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//		.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
		.build(); // 创建配置过得DisplayImageOption对象
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
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dynamic_item_allot_listitem, null);
			vh.tagIv = (ImageView) convertView.findViewById(R.id.iv_dynamicallot_tag);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_dynamicallot_name);
			vh.progressBar = (CircleProgressView) convertView.findViewById(R.id.cpv_dynamicallot_progress);
			vh.taskNameTv = (TextView) convertView.findViewById(R.id.tv_dynamicallot_taskname);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_dynamicallot_finishtime);
//			vh.personImgLayout = (LinearLayout) convertView.findViewById(R.id.task_ll_personimg);
//			vh.mainpersonImgLayout = (LinearLayout) convertView.findViewById(R.id.task_ll_mainpersonimg);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
//		vh.personImgLayout.removeAllViews();
//		vh.mainpersonImgLayout.removeAllViews();
		final UserFenPai task = mDataList.get(position);
//		task.id = task.taskid;
		vh.tagIv.setImageResource(HelpUtil.getTaskResId(task.Yxj));
		vh.taskNameTv.setText(task.Title);
		
		if (!TextUtils.isEmpty(task.MainUser)) {
			String[] mains = task.MainUser.split(",");
			if (!TextUtils.isEmpty(mains[0]) && TextUtils.isDigitsOnly(mains[0]) && EMWApplication.personMap != null) {
				UserInfo user = EMWApplication.personMap.get(Integer.valueOf(mains[0]));
				if (user != null)
					vh.nameTv.setText(user.Name);
			}
		} else {
			vh.nameTv.setText(R.string.unkonw);
		}

//		vh.progressBar.setValue(CommonUtil.getProgress(task.StartTime, task.FinishTime, mContext.getString(R.string.timeformat2)));
		/*StringBuffer nameBuffer = new StringBuffer();
		for (int i = 0; i<task.user.size(); i++) {
			if (EMWApplication.personList != null && EMWApplication.personList.size() > 0) {
				for (SimpleUser user : EMWApplication.personList) {
					if (task.user.get(i) == user.getID()) {
						nameBuffer.append(user.getName());
						if (i < task.user.size() - 1) {
							nameBuffer.append("，");
						}
						CircleImageView imgview = new CircleImageView(mContext);
						imgview.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
						imgview.setBorderColorResource(R.color.gray_4);
						LayoutParams params = new LayoutParams(DisplayUtil.dip2pxForTypedValue(mContext, 40), DisplayUtil.dip2pxForTypedValue(mContext, 40));
						params.rightMargin = DisplayUtil.dip2pxForTypedValue(mContext, 5);
						imgview.setLayoutParams(params);
						String uri = String.format(HttpConstant.ICON_URL, PrefsUtil.readUserInfo().getCompanyCode(), user.getImage());
						ImageLoader.getInstance().displayImage(uri, imgview, options);
						vh.personImgLayout.addView(imgview);
						break;
					}
				}
			}
		}
		
		if (task.mainuser != null && task.mainuser.size() > 0) {
			if (EMWApplication.personList != null && EMWApplication.personList.size() > 0) {
				for (SimpleUser user : EMWApplication.personList) {
					if (task.mainuser.get(0) == user.getID()) {
						CircleImageView imgview = new CircleImageView(mContext);
						imgview.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
						imgview.setBorderColorResource(R.color.gray_4);
						LayoutParams params = new LayoutParams(DisplayUtil.dip2pxForTypedValue(mContext, 40), DisplayUtil.dip2pxForTypedValue(mContext, 40));
						imgview.setLayoutParams(params);
						String uri = String.format(HttpConstant.ICON_URL, PrefsUtil.readUserInfo().getCompanyCode(), user.getImage());
						ImageLoader.getInstance().displayImage(uri, imgview, options);
						vh.mainpersonImgLayout.addView(imgview);
						break;
					}
				}
			}
		}
		vh.personTv.setText(nameBuffer.toString());*/
		vh.timeTv.setText(task.FinishTime);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TaskDetailActivity.class);
				intent.putExtra(TaskDetailActivity.TASK_ID, task.ID);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}	
	
	static class ViewHolder {
		ImageView tagIv;
		TextView nameTv;
		CircleProgressView progressBar;
		TextView taskNameTv;
		TextView timeTv;
	}
	
	
}
