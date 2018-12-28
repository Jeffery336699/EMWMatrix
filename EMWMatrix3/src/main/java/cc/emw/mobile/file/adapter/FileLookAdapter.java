package cc.emw.mobile.file.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
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
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 分享查看Adapter
 * @author shaobo.zhuang
 */
public class FileLookAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<UserFilePower> mDataList;
	private DisplayImageOptions options;

	public FileLookAdapter(Context context,
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
		return mDataList != null ? mDataList.size()-1 : 0;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filelook, null);
			vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_filelook_head);
			vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filelook_name);
			vh.powerTv = (TextView) convertView.findViewById(R.id.tv_filelook_power);
			vh.dividerView = convertView.findViewById(R.id.view_filelook_divider);

			convertView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) convertView.getTag(R.id.tag_first);
		}

		UserFilePower filePower = null;
		try {
			filePower = mDataList.get(position + 1);

			if (EMWApplication.personMap != null && EMWApplication.personMap.get(filePower.ID) != null) {
				String image = EMWApplication.personMap.get(filePower.ID).Image;
				String companyCode = EMWApplication.personMap.get(filePower.ID).CompanyCode;
				String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(companyCode) ? PrefsUtil.readUserInfo().CompanyCode : companyCode, image);
				ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
			}

			vh.nameTv.setText(filePower.Name);
			vh.powerTv.setText(HelpUtil.getFilePowerNumber(Integer.toBinaryString(filePower.Power))); //十进制转二进制
			vh.dividerView.setVisibility(getCount() - 1 == position ? View.INVISIBLE:View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		convertView.setTag(R.id.tag_second, filePower);
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		TextView powerTv;
		View dividerView;
	}
	
}
