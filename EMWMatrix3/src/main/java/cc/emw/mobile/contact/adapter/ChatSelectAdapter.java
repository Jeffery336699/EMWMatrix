package cc.emw.mobile.contact.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

public class ChatSelectAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private ArrayList<UserInfo> mDataList;

	private DisplayImageOptions options;

	public ChatSelectAdapter(Context context) {
		this.mContext = context;

		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
	}


	public void setData(ArrayList<UserInfo> mDataList) {
		this.mDataList = mDataList;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDataList.get(position).ID;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup arg2) {
		ViewHolder vh;
		if (contentView == null) {
			vh = new ViewHolder();
			contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_contact_person_select, null);
			vh.headIv = (CircleImageView) contentView.findViewById(R.id.iv_personselect_head);
			vh.okItv = (IconTextView) contentView.findViewById(R.id.itv_personselect_ok);
			vh.nameTv = (TextView) contentView.findViewById(R.id.tv_personselect_name);
			contentView.setTag(R.id.tag_first, vh);
		} else {
			vh = (ViewHolder) contentView.getTag(R.id.tag_first);
		}

		final UserInfo user = mDataList.get(position);
		vh.headIv.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
		String uri = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(user.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : user.CompanyCode, user.Image);
		ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.headIv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
		vh.nameTv.setText(user.Name);

		contentView.setTag(R.id.tag_second, user);
		return contentView;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mDataList.size(); i++) {
			String sortStr = mDataList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		int i = 0, m = 0;
		for (int j = 0; j < mDataList.size(); j++) {
			if (i == position) {
				m = mDataList.get(j).getSortLetters().charAt(0);
				break;
			}
		}
		i++;
		return m;
	}

	class ViewHolder {
		CircleImageView headIv;
		IconTextView okItv;
		TextView nameTv;
	}

}
