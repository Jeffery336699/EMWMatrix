package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupIntoActivity;
import cc.emw.mobile.contact.PersonActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

public class GroupEditAdapter extends BaseAdapter {

	DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private Context mContext;
	private List<UserInfo> mDataList;
	private Dialog mLoadingDialog; // 加载框
	private boolean isEdit;
	private GroupInfo mGroupInfo;

	public GroupEditAdapter(Context context, Dialog loadingDialog) {
		mContext = context;
		mLoadingDialog = loadingDialog;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
	}

	public int getMemberCount() {
		return mDataList.size();
	}

	public void setData(List<UserInfo> dataList) {
		mDataList = dataList;
	}

	public void setEditEnabled(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public void setGroupInfo(GroupInfo groupInfo) {
		mGroupInfo = groupInfo;
	}

	@Override
	public int getCount() {

		if (mDataList == null)
			return 0;
		else if (mDataList.size() + 1 > 50)
			return 50;
		else
			return mDataList.size() + 1;

		// return mDataList != null ? mDataList.size() + 1 : 0;
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
					R.layout.item_group_edit_grid, null, false);
			vh.headIv = (CircleImageView) convertView
					.findViewById(R.id.group_create_iv_head);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.group_create_tv_name);
			vh.delTagImg = (ImageView) convertView
					.findViewById(R.id.ico_del_red);
			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		if (position == getCount() - 1) {
			vh.headIv.setImageResource(R.drawable.btn_add_people);
			vh.nameTv.setText("添加");
			vh.delTagImg.setVisibility(View.GONE);
			convertView.setVisibility(View.VISIBLE);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							ContactSelectActivity.class);
					intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
							ContactSelectActivity.MULTI_SELECT);
					((Activity) mContext).startActivityForResult(intent, 1);
				}
			});
		} else {
			final UserInfo role = mDataList.get(position);
			String uri = String.format(Const.DOWN_ICON_URL,
					PrefsUtil.readUserInfo().CompanyCode, role.Image);
			ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
			vh.nameTv.setText(role.Name);
			if (isEdit) {
				if (role.ID != PrefsUtil.readUserInfo().ID) {
					vh.delTagImg.setVisibility(View.VISIBLE);
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							new AlertDialog(mContext)
									.builder()
									.setMsg("确认要删除该成员？")
									.setPositiveButton("确定",
											new OnClickListener() {
												@Override
												public void onClick(View v) {
													ArrayList<UserInfo> roleList = new ArrayList<UserInfo>();
													roleList.add(role);
													delGroupRoles(
															mGroupInfo.ID,
															role.ID, position);
												}
											})
									.setNegativeButton("取消",
											new OnClickListener() {
												@Override
												public void onClick(View v) {
												}
											}).show();
						}
					});
				} else {
					vh.delTagImg.setVisibility(View.GONE);
				}
			} else {
				vh.delTagImg.setVisibility(View.GONE);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext,
								PersonActivity.class);
						Bundle args = new Bundle();
						// SimpleUser simpleUser = new SimpleUser(role.getID(),
						// role.getName(), role.getJob(), role
						// .getDeptName(), role.isIsFollow(), role
						// .getDeptID(), role.isIsOnline(), role
						// .getEmail(), role.getCode(), role
						// .getImage(), role.getVoip_Code(), role
						// .getPhone());
						args.putSerializable("simple_user", role);
						intent.putExtras(args);
						mContext.startActivity(intent);
					}
				});
			}
		}

		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		ImageView delTagImg;
	}

	private void delGroupRoles(int gid, int userid, final int postion) {
		API.TalkerAPI.DelGroupUser(gid, userid, new RequestCallback<String>(
				String.class) {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Toast.makeText(mContext, "服务器异常", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(String arg0) {
				if (Integer.valueOf(arg0) == 0) {
					Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
				} else if (Integer.valueOf(arg0) == 1) {
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
					mDataList.remove(postion);
					Intent intent = new Intent();
					intent.setAction(GroupIntoActivity.ACTION_REFRESH_COUNT_GROUP);
					mContext.sendBroadcast(intent);
					notifyDataSetChanged();
				}
			}
		});
	}

}
