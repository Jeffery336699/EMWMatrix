package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;

public class ResponserAdapter extends BaseAdapter{
	private static final String TAG = "ResponserAdapter";
	private Context mContext;
	private ArrayList<UserInfo> mResponser;
	private DisplayImageOptions options;
	//是否显示可删除按钮
	private Boolean mCanDel = false;
	public ResponserAdapter(Context context){
		this.mContext = context;
		mResponser = new ArrayList<UserInfo>();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
		.showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//		.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
		.build(); // 创建配置过得DisplayImageOption对象
	}
	
	public void setArrayUser(List<UserInfo> users,Boolean canDel) {
		if (users != null) {
			this.mCanDel = canDel;
			this.mResponser.clear();
			this.mResponser.addAll(users);
		}
	}
	
	public ArrayList<UserInfo> getArrayUser(){
		return mResponser;
	}
	
	@Override
	public int getCount() {
		return mResponser != null ? mResponser.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mResponser != null ? mResponser.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		ViewHolder vh;
		if(convertView == null){
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.listitem_project_responser, parent,false);
			vh.portrait = (ImageView)convertView
					.findViewById(R.id.iv_responser_portrait);
			vh.name = (TextView)convertView
					.findViewById(R.id.tv_responser_name);
			vh.delBtn = (ImageView)convertView
					.findViewById(R.id.iv_responser_del);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}
		final UserInfo user = mResponser.get(position);
		Log.d(TAG, position+"");
		if(user != null){
			String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, user.Image);
			ImageLoader.getInstance().displayImage(uri, vh.portrait, options);
			vh.name.setText(user.Name);
			vh.delBtn.setVisibility(mCanDel?View.VISIBLE:View.INVISIBLE);
			if(mCanDel){
				vh.delBtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mResponser.remove(user);
						notifyDataSetChanged();
						TaskUtils.setListViewHeightBasedOnChildren((ListView)parent);
					}
				});
			}
		}
		return convertView;
	}
	class ViewHolder{
		ImageView portrait;
		TextView name;
		ImageView delBtn;
	}
}
