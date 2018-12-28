package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;


/**
 * 动态·新建工作分派Adapter
 * @author shaobo.zhuang
 */
public class AllotAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<UserInfo> mDataList;
	private DisplayImageOptions options;
	
	public AllotAdapter(Context context,
			ArrayList<UserInfo> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
		
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}
	
	public void setDataList(ArrayList<UserInfo> dataList) {
		this.mDataList = dataList;
	}
	
	public ArrayList<UserInfo> getDataList() {
		if (mDataList == null) 
			mDataList = new ArrayList<UserInfo>();
		return mDataList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_allot, null);
	    	vh.headIv = (CircleImageView) convertView.findViewById(R.id.iv_allotitem_head);
	    	vh.nameTv = (TextView) convertView.findViewById(R.id.tv_allotitem_name);
	    	vh.delBtn = (ImageButton) convertView.findViewById(R.id.btn_allotitem_del);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		UserInfo user = mDataList.get(position);
		if (EMWApplication.personMap != null && EMWApplication.personMap.get(user.ID) != null) {
			String image = EMWApplication.personMap.get(user.ID).Image;
			String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
			ImageLoader.getInstance().displayImage(uri, vh.headIv, options);
		}
        vh.nameTv.setText(user.Name);
        vh.delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDataList.remove(position);
				notifyDataSetChanged();
			}
		});
		
		return convertView;
	}

	static class ViewHolder {
		CircleImageView headIv;
		TextView nameTv;
		ImageButton delBtn;
	}
}
