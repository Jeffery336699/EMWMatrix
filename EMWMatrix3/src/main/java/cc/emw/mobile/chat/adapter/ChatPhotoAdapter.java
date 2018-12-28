package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.utils.ImageLoader;

/**
 * Created by sunny.du on 2016/11/23.
 * 展示图片的适配器
 */
public class ChatPhotoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> photoList;
    private int windowWidth;
    private ImageLoader mImageLoader;
    @Override
    public int getCount() {
        if (photoList.size() != 0) {
            return photoList.size();
        }
        return 0;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_chat_show_photo, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        mImageLoader.loadImage(photoList.get(position).toString(),holder.mImageView);
        return convertView;
    }


    /**
     * 缓存Holder
     */
    public class ViewHolder {
        ImageView mImageView;

        public ViewHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.iv_photo);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            params.height = windowWidth;
            params.width = windowWidth;
            mImageView.setLayoutParams(params);
        }
    }

    /**
     * 初始化
     *
     * @param context   ↑↓文
     * @param photoList 保存图片地址的string地址列表
     */
    public ChatPhotoAdapter(Context context, List<String> photoList, int windowWidth) {
        mImageLoader = ImageLoader.getInstance(3 , ImageLoader.Type.LIFO);
        this.mContext = context;
        this.photoList = photoList;
        this.windowWidth = windowWidth;
    }
}
