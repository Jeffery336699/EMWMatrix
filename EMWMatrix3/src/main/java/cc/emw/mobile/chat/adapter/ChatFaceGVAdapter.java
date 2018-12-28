package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.DefQqEmoticons;


public class ChatFaceGVAdapter extends BaseAdapter {
	private static final String TAG = "FaceGVAdapter";
	private List<String> list;
	private Context mContext;

	public ChatFaceGVAdapter(List<String> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler hodler;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_face_image, null);
			hodler.iv = (ImageView) convertView.findViewById(R.id.face_img);
			hodler.tv = (TextView) convertView.findViewById(R.id.face_text);
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		if(!(list.get(position).equals("emotion_del_normal")) && !(list.get(position).equals("CURSOR"))){
			hodler.iv.setImageDrawable(mContext.getResources().getDrawable(DefQqEmoticons.sQqEmoticonHashMap.get(list.get(position))));
			hodler.tv.setText(list.get(position)+"");
		}else if(list.get(position).equals("emotion_del_normal")){
			hodler.iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.emotion_del_normal));
			hodler.tv.setText("emotion_del_normal");
		}
		return convertView;
	}

	class ViewHodler {
		ImageView iv;
		TextView tv;
	}
}
