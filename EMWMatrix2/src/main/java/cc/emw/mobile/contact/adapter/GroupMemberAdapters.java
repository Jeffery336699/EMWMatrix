package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;

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
import cc.emw.mobile.contact.PersonActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author zrjt
 */
public class GroupMemberAdapters extends BaseAdapter {

	private Context mContext;
	private ArrayList<UserInfo> mDataList;
	private ArrayList<GroupInfo> mFollowList;
	private DisplayImageOptions options;
	private GroupInfo info;
	private int type;

	public GroupMemberAdapters(Context context, GroupInfo info) {
		this.mContext = context;
		this.info = info;
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

	public void setData(ArrayList<UserInfo> dataList) {
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

		final UserInfo user = mDataList.get(position);

		vh.deptTv.setVisibility(View.GONE);
		if (position > 0 && mDataList.get(position - 1).isTag == user.isTag) {
			vh.deptTv.setVisibility(View.GONE);
		} else {
			vh.deptTv.setVisibility(View.VISIBLE);
			vh.deptTv.setText(user.isTag ? "群主" : "成员");
		}
		vh.nameTv.setText(user.Name);

		// final String uri =
		// "http://pic5.nipic.com/20091224/3471190_042508021383_2.jpg";
		final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
				.readUserInfo().CompanyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		convertView.setTag(R.id.tag_second, user);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PersonActivity.class);
				Bundle args = new Bundle();
//				UserInfo simpleUser = new UserInfo(user.getID(), user
//						.getName(), user.getJob(), user.getDeptName(), user
//						.isIsFollow(), user.getDeptID(), user.isIsOnline(),
//						user.getEmail(), user.getCode(), user.getImage(), user
//								.getVoip_Code(), user.getPhone());
				args.putSerializable("simple_user", user);
				intent.putExtras(args);
				mContext.startActivity(intent);
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
