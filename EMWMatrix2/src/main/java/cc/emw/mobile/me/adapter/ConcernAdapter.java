package cc.emw.mobile.me.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.PersonActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author zrjt
 * @version 2016-3-11 下午7:26:01
 */
public class ConcernAdapter extends BaseAdapter {

	private Context mContext;
	private List<UserInfo> mDataList;
	private DisplayImageOptions options;

	public ConcernAdapter(Context context) {
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

	public void setData(List<UserInfo> dataList) {
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
					R.layout.listitem_concern, null);
			vh.headIv = (CircleImageView) convertView
					.findViewById(R.id.iv_concern_head);
			vh.nameTv = (TextView) convertView
					.findViewById(R.id.tv_concern_name);
			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		final UserInfo user = mDataList.get(position);
		vh.nameTv.setText(user.Name);
		final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
				.readUserInfo().CompanyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, vh.headIv, options);

		convertView.setTag(R.id.tag_second, user);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PersonActivity.class);
				intent.putExtra("simple_user",(UserInfo) v.getTag(R.id.tag_second));
				((Activity) mContext).startActivityForResult(intent, 10);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
	}

}
