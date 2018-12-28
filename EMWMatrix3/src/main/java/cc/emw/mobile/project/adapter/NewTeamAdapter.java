package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * 编辑圈子信息成员Adapter
 * @author shaobo.zhuang
 */
public class NewTeamAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ApiEntity.UserInfo> mDataList;
	private DisplayImageOptions options;
	private int type;
	private OnSetUpdateListener onSetUpdateListener;
	public void setOnSetUpdateListener(OnSetUpdateListener onSetUpdateListener) {
		this.onSetUpdateListener = onSetUpdateListener;
	}

	public NewTeamAdapter(Context context, ArrayList<ApiEntity.UserInfo> dataList, int type) {
		this.mContext = context;
		this.mDataList = dataList;
		this.type = type;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
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
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_newteam, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_newteam_head);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_newteam_name);
			vh.setTv = (TextView) convertView.findViewById(R.id.tv_newteam_set);
			vh.deleteTv = (IconTextView) convertView.findViewById(R.id.itv_newteam_del);
			vh.dividerView = convertView.findViewById(R.id.view_newteam_divider);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		final ApiEntity.UserInfo user = mDataList.get(position);
		String companyCode = user.CompanyCode;
		String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(companyCode) ? PrefsUtil.readUserInfo().CompanyCode : companyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

		vh.nameTv.setText(user.Name);

		convertView.setTag(R.id.tag_second, user);

		vh.setTv.setText(type == 0 ? "设为普通成员" : "设为负责人");
		vh.setTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onSetUpdateListener != null)
					onSetUpdateListener.onSetUpdate(user, type);
			}
		});
		vh.deleteTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDataList.remove(position);
				notifyDataSetChanged();
			}
		});
		vh.dividerView.setVisibility(getCount() - 1 == position ? View.INVISIBLE:View.VISIBLE);
		
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		TextView setTv;
		TextView deleteTv;
		View dividerView;
	}

	public interface OnSetUpdateListener {
		void onSetUpdate(ApiEntity.UserInfo user, int type);
	}
}
