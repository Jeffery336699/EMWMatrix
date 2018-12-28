package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.GroupIntoActivity;
import cc.emw.mobile.contact.GroupUnIntoActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author zrjt
 */
public class GroupAdapters extends BaseAdapter {

	private Context mContext;
	private ArrayList<GroupInfo> mDataList;
	private ArrayList<GroupInfo> mFollowList;
	private DisplayImageOptions options;
	private int type;

	public GroupAdapters(Context context) {
		this.mContext = context;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setData(ArrayList<GroupInfo> dataList) {
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
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_groups, null);
			vh.deptTv = (TextView) convertView
					.findViewById(R.id.personnel_tv_dept);
			vh.headIv = (CircleImageView) convertView
					.findViewById(R.id.personnel_iv_head);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.personnel_tv_name);
			vh.tagTv = (TextView) convertView
					.findViewById(R.id.right_menu_tv_time);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}

		final GroupInfo user = mDataList.get(position);

		vh.deptTv.setVisibility(View.GONE);
		if (position > 0 && mDataList.get(position - 1).tag == user.tag) {
			vh.deptTv.setVisibility(View.GONE);
		} else {
			vh.deptTv.setVisibility(View.VISIBLE);
			vh.deptTv.setText(user.tag ? "加入的" : "所有的");
		}
		if (user.CreateUser == PrefsUtil.readUserInfo().ID) {
			vh.tagTv.setVisibility(View.VISIBLE);
			vh.tagTv.setText("群主");
		} else {
			vh.tagTv.setVisibility(View.GONE);
		}
		vh.nameTv.setText(user.Name);
		// final String uri =
		// "http://pic5.nipic.com/20091224/3471190_042508021383_2.jpg";
		final String uri = Const.BASE_URL + user.Image;
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		convertView.setTag(R.id.tag_second, user);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (user.IsAddIn) {
					intent = new Intent(mContext, GroupIntoActivity.class);
					Bundle args = new Bundle();
					args.putSerializable("group_info", user);
					intent.putExtras(args);
					((Activity) mContext).startActivity(intent);
				} else {
					intent = new Intent(mContext, GroupUnIntoActivity.class);
					Bundle args = new Bundle();
					args.putSerializable("group_info", user);
					intent.putExtras(args);
					((Activity) mContext).startActivity(intent);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView deptTv;
		CircleImageView headIv;
		TextView nameTv;
		TextView tagTv;
	}

}
