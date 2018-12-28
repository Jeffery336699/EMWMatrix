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
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 历史版本Adapter
 * @author shaobo.zhuang
 */
public class FileHistoryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Files> mDataList;
	private DisplayImageOptions options;

	public FileHistoryAdapter(Context context,
							  ArrayList<Files> dataList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filehistory, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_filehistory_head);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filehistory_name);
			vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filehistory_time);
			vh.descTv = (TextView) convertView.findViewById(R.id.tv_filehistory_desc);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}
		Files file = mDataList.get(position);

		if (EMWApplication.personMap != null && EMWApplication.personMap.get(file.Creator) != null) {
			String image = EMWApplication.personMap.get(file.Creator).Image;
			String companyCode = EMWApplication.personMap.get(file.Creator).CompanyCode;
			String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(companyCode) ? PrefsUtil.readUserInfo().CompanyCode : companyCode, image);
			ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		}

		vh.nameTv.setText(file.T_Creator);
		vh.timeTv.setText(file.UpdateTime);
		vh.descTv.setText(file.Content);
		
		convertView.setTag(R.id.tag_second, file);
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		TextView timeTv;
		TextView descTv;
	}
	
}
