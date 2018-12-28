package cc.emw.mobile.dynamic.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.dynamic.ShareActivity;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.file.FileSelectActivity2;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.RoundedImageView;


/**
 * 分享图片投票项列表Adapter
 * @author shaobo.zhuang
 */
public class ShareImgVoteAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<UserNoteVote> mDataList;
	private DisplayImageOptions options;

	public ShareImgVoteAdapter(Context context,
							   ArrayList<UserNoteVote> dataList) {
		this.mContext = context;
		UserNoteVote vote1 = new UserNoteVote();
		UserNoteVote vote2 = new UserNoteVote();
		vote1.TP = 1;
		vote2.TP = 1;
		dataList.add(vote1);
		dataList.add(vote2);
		this.mDataList = dataList;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_1)
//		.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
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
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.share_tab_imgvote_item, null);
			vh.delTv = (IconTextView) convertView.findViewById(R.id.tv_sharevote_del);
			vh.delTv.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {
                	int position=(Integer) holder.contentEt.getTag();
					mDataList.remove(position);
					notifyDataSetChanged();
                }
            });
			vh.imgIv = (RoundedImageView) convertView.findViewById(R.id.iv_sharevote_img);
			vh.contentEt = (EditText) convertView.findViewById(R.id.et_sharevote_content);
			vh.contentEt.setTag(position);
	    	vh.contentEt.addTextChangedListener(new MyTextWatcher(vh) {
	            @Override
	            public void afterTextChanged(Editable s, ViewHolder holder) {
	                int position=(Integer) holder.contentEt.getTag();
	                UserNoteVote v = mDataList.get(position);
	                v.Text = s.toString();
	                mDataList.set(position, v);
	            }
	        });
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
			vh.contentEt.setTag(position);
		}
		UserNoteVote vote = mDataList.get(position);
		vote.ID = position + 1;
		vh.contentEt.setText(vote.Text);

		if (!TextUtils.isEmpty(vote.Url)) {
			String url = HelpUtil.getFileURL(vote);
			ImageLoader.getInstance().displayImage(url, vh.imgIv, options);
		} else {
			vh.imgIv.setImageResource(R.drawable.share_img_add);
		}

		vh.imgIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FileSelectActivity2.class);
				intent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, FileSelectActivity2.RADIO_SELECT);
				intent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
				intent.putExtra(FileSelectActivity2.EXTRA_POSITION, position);
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				intent.putExtra("click_pos_y", location[1]);
				((Activity)mContext).startActivityForResult(intent, ShareActivity.CHOSE_IMGVOTE_CODE);
			}
		});
		
		return convertView;
	}

	static class ViewHolder {
		IconTextView delTv;
		RoundedImageView imgIv;
		EditText contentEt;
	}
	
	
	private abstract class MyTextWatcher implements TextWatcher{
        private ViewHolder mHolder;
        
        public MyTextWatcher(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, mHolder);
        }
        public abstract void afterTextChanged(Editable s,ViewHolder holder);
    }
	private abstract class MyOnClickListener implements OnClickListener{
        
        private ViewHolder mHolder;
        
        public MyOnClickListener(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }
        public abstract void onClick(View v,ViewHolder holder);
        
    }
}
