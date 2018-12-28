package cc.emw.mobile.contact.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupSelectAdapter extends BaseAdapter {

	private DisplayImageOptions options;

	private Context mContext;

	private ArrayList<GroupInfo> mDataList;

	private int lastPosition = -1;

	private GroupInfo targetG = new GroupInfo();

	public GroupSelectAdapter(Context context) {
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

	public GroupInfo getTargetG() {
		return targetG;
	}

	public void setData(ArrayList<GroupInfo> mDataList) {
		this.mDataList = mDataList;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return mDataList.get(arg0).ID;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup arg2) {
		View view = null;
		if (contentView != null) {
			view = contentView;
		} else {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_contact_groups_select, null);
		}
		ViewHolder vh = (ViewHolder) view.getTag();
		if (vh == null) {
			vh = new ViewHolder();
			vh.img = (ImageView) view.findViewById(R.id.iv_group_head);
			vh.tv = (TextView) view.findViewById(R.id.tv_group_name);
			vh.rb = (CheckBox) view.findViewById(R.id.rb_groups_select);
			view.setTag(vh);
		}

		GroupInfo gInfo = mDataList.get(position);

		final String uri = String.format(Const.DOWN_ICON_URL,
				PrefsUtil.readUserInfo().CompanyCode, gInfo.Image);

		ImageLoader.getInstance().displayImage(uri, vh.img, options);

		vh.tv.setText(gInfo.Name);

		vh.rb.setChecked(lastPosition == position ? true : false);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (lastPosition == position) {
					lastPosition = -1;
				} else {
					lastPosition = position;
					targetG = mDataList.get(position);
				}
				notifyDataSetChanged();
			}
		});

		return view;
	}

	class ViewHolder {
		ImageView img;
		TextView tv;
		CheckBox rb;
	}

}
