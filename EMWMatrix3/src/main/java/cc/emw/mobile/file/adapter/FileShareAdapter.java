package cc.emw.mobile.file.adapter;

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

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * 权限/分享到Talker Adapter
 * @author shaobo.zhuang
 */
public class FileShareAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<UserFilePower> mDataList;
	private DisplayImageOptions options;

	public FileShareAdapter(Context context,
							ArrayList<UserFilePower> dataList) {
		this.mContext = context;
		this.mDataList = dataList;

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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_fileshare, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_fileshare_head);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_fileshare_name);
			vh.deleteTv = (IconTextView) convertView.findViewById(R.id.itv_fileshare_del);
			vh.dividerView = convertView.findViewById(R.id.view_fileshare_divider);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		UserFilePower filePower = mDataList.get(position);

		if (EMWApplication.personMap != null && EMWApplication.personMap.get(filePower.ID) != null) {
			String image = EMWApplication.personMap.get(filePower.ID).Image;
			String companyCode = EMWApplication.personMap.get(filePower.ID).CompanyCode;
			String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(companyCode) ? PrefsUtil.readUserInfo().CompanyCode : companyCode, image);
			ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		}

		vh.nameTv.setText(filePower.Name);
		vh.deleteTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDataList.remove(position);
				notifyDataSetChanged();
			}
		});
		vh.dividerView.setVisibility(getCount() - 1 == position ? View.INVISIBLE:View.VISIBLE);
		
		convertView.setTag(R.id.tag_second, filePower);
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		TextView deleteTv;
		View dividerView;
	}
	
}
