package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.UserNote.UserNoteVote;


/**
 * 分享投票项列表Adapter
 * @author shaobo.zhuang
 */
public class ShareVoteAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<UserNoteVote> mDataList;
	
	public ShareVoteAdapter(Context context,
			ArrayList<UserNoteVote> dataList) {
		this.mContext = context;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.share_tab_vote_item, null);
			vh.delBtn = (ImageButton) convertView.findViewById(R.id.btn_sharevote_del);
			vh.delBtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {
                	int position=(Integer) holder.contentEt.getTag();
					mDataList.remove(position);
					notifyDataSetChanged();
                }
            });
			vh.optionTv = (TextView) convertView.findViewById(R.id.tv_sharevote_index);
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
		vote.ID = position;
		vh.optionTv.setText("选项 "+ (vote.ID+1));
		vh.contentEt.setText(vote.Text);
		vh.delBtn.setVisibility(position > 1 ? View.VISIBLE : View.GONE);
		
		return convertView;
	}

	static class ViewHolder {
		ImageButton delBtn;
		TextView optionTv;
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
