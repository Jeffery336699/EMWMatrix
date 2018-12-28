package cc.emw.mobile.file.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.file.FileDetailActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * 知识库列表Adapter
 * @author shaobo.zhuang
 */
public class FileOtherAdapter extends RecyclerView.Adapter<FileOtherAdapter.MyViewHolder> {

	private Context mContext;
	private ArrayList<Files> mDataList;

	public FileOtherAdapter(Context context, ArrayList<Files> dataList) {
		this.mContext = context;
		this.mDataList = dataList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filelist, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		final Files file = mDataList.get(position);
		holder.rootView.setTag(R.id.tag_second, file);

		holder.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
		holder.nameTv.setText(file.Name);
		holder.timeTv.setText(file.UpdateTime);
		holder.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
		holder.sizeTv.setVisibility(View.VISIBLE);
		String localPath = EMWApplication.filePath + FileUtil.getFileName(file.Url);
		holder.existItv.setTextColor(FileUtil.hasFile(localPath, file.Length) ? Color.parseColor("#4A90E2"):Color.parseColor("#CBCBCB"));
		holder.projectTv.setVisibility(View.GONE);
		holder.moreLayout.setVisibility(View.GONE);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mDataList != null ? mDataList.size() : 0;
	}


	class MyViewHolder extends RecyclerView.ViewHolder {
		public View rootView;
		public ImageView iconIv;
		public TextView nameTv;
		public TextView timeTv;
		public TextView sizeTv;
		public TextView projectTv;
		public IconTextView moreBtn;
		public LinearLayout moreLayout;
		public IconTextView existItv;
		public int position;

		public MyViewHolder(View itemView) {
			super(itemView);
			iconIv = (ImageView) itemView.findViewById(R.id.iv_filelist_icon);
			nameTv = (TextView) itemView.findViewById(R.id.tv_filelist_name);
			timeTv = (TextView) itemView.findViewById(R.id.tv_filelist_time);
			sizeTv = (TextView) itemView.findViewById(R.id.tv_filelist_size);
			projectTv = (TextView) itemView.findViewById(R.id.tv_filelist_project);
			moreBtn = (IconTextView) itemView.findViewById(R.id.itv_filelist_more);
			moreLayout = (LinearLayout) itemView.findViewById(R.id.ll_filelist_more);

			existItv = (IconTextView) itemView.findViewById(R.id.itv_filelist_exist);
			rootView = itemView.findViewById(R.id.item_root);
			rootView.setOnClickListener(new NoDoubleClickListener() {
				@Override
				public void onNoDoubleClick(View v) {
					ApiEntity.Files noteFile = (ApiEntity.Files) v.getTag(R.id.tag_second);
					Intent intent = new Intent(mContext, FileDetailActivity.class);
					intent.putExtra("file_info", noteFile);
					intent.putExtra("start_anim", false);
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					intent.putExtra("click_pos_y", location[1]);
					mContext.startActivity(intent);
				}
			});
		}
	}
}
