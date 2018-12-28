package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.factory.ImageLoadFactory;

/**
 * Created by sunny.du on 2017/5/26.
 * 搜索
 */

public class ChatPoiAdapter extends RecyclerView.Adapter<ChatPoiAdapter.MyHolder> {
    private Context mContext;
    private List<PoiItem> date;
    private DisplayImageOptions optionesMsgImage;
    private int selectorPosition = -1;
    private PoiItem chatPoiBean;

    public PoiItem getChatPoiBean() {
        return chatPoiBean;
    }

    public ChatPoiAdapter(Context context) {
        optionesMsgImage = ImageLoadFactory.getChatApdaterImage();
        date = new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chat_poi, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final PoiItem chatPoiBean = date.get(position);
        holder.mTvPoiTitle.setText(chatPoiBean.getTitle());
        holder.mTvPoiDes.setText(chatPoiBean.getTypeDes());
        holder.mTvPoiDess.setText(chatPoiBean.getSnippet());
        if(chatPoiBean.getTel() != null &&!("".equals(chatPoiBean.getTel().trim()))) {
            holder.mTvPoiTel.setText(chatPoiBean.getTel());
        }else{
            holder.mTvPoiTel.setText("暂无电话信息");
        }
        if (chatPoiBean.getPhotos() != null && chatPoiBean.getPhotos().size() != 0 && chatPoiBean.getPhotos().get(0) != null) {
            ImageLoader.getInstance().displayImage(chatPoiBean.getPhotos().get(0).getUrl(), holder.mIvPoiImage, optionesMsgImage);
        } else {
            holder.mIvPoiImage.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        if (selectorPosition == position) {
            holder.mCheckBoxPoi.setChecked(true);
        } else {
            holder.mCheckBoxPoi.setChecked(false);
        }
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectorPosition == position) {
                    return;
                }
                selectorPosition = position;
                ChatPoiAdapter.this.chatPoiBean=chatPoiBean;
                notifyDataSetChanged();

                if (onSelectListener != null) {
                    onSelectListener.onSelect(selectorPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return date.size();
    }

    public void setDate(List<PoiItem> date) {
        this.date = date;
    }
    private ChatInformationAdapter.OnSelectListener onSelectListener;

    public void setOnSelectListener(ChatInformationAdapter.OnSelectListener listener) {
        onSelectListener = listener;
    }
    public interface OnSelectListener {
        void onSelect(int position);
    }
    public class MyHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBoxPoi;
        TextView mTvPoiTitle;
        TextView mTvPoiDes;
        TextView mTvPoiDess;
        TextView mTvPoiTel;
        ImageView mIvPoiImage;
        View mItemView;
        public MyHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mCheckBoxPoi = (CheckBox) itemView.findViewById(R.id.checkbox_poi);
            mTvPoiTitle = (TextView) itemView.findViewById(R.id.tv_poi_title);
            mTvPoiDes = (TextView) itemView.findViewById(R.id.tv_poi_des);
            mTvPoiDess = (TextView) itemView.findViewById(R.id.tv_poi_dress);
            mTvPoiTel = (TextView) itemView.findViewById(R.id.tv_poi_tel);
            mIvPoiImage = (ImageView) itemView.findViewById(R.id.iv_poi_image);
        }
    }
}
