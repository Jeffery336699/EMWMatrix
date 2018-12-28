package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatWebActivity;
import cc.emw.mobile.chat.model.bean.AiMsgBean;
import cc.emw.mobile.chat.factory.ImageLoadFactory;

/**
 * Created by sunny.du on 2017/4/5.
 * 地图引导
 */
public class ChatAIListMapFoodsAdapter extends RecyclerView.Adapter<ChatAIListMapFoodsAdapter.MyHolder> {
    private Context mContext;
    private AiMsgBean mImagesAndUrl;

    /**
     * 模拟数据  msgList user传入空值
     */
    public ChatAIListMapFoodsAdapter(Context context, AiMsgBean imagesAndUrl) {
        this.mContext = context;
        this.mImagesAndUrl = imagesAndUrl;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_foods_map_item_0, parent, false));
        } else {
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_image_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        if (position < mImagesAndUrl.getFoods().size()) {
            DisplayImageOptions optionesMsgImage = ImageLoadFactory.getChatApdaterImage();
            ImageLoader.getInstance().displayImage(mImagesAndUrl.getFoods().get(position).getDefaultPic(), holder.mChatAiItemIvFootPic, optionesMsgImage);
            holder.mChatAiItemFootName.setText(mImagesAndUrl.getFoods().get(position).getName());
            holder.mChatAiItemFootMoney.setText(mImagesAndUrl.getFoods().get(position).getPriceText());
            final int positions=position;
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatWebActivity.class);
                    intent.putExtra("load_more_web_url", mImagesAndUrl.getFoods().get(positions).getUrl());
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO跳转WebView
                    Intent intent = new Intent(mContext, ChatWebActivity.class);
                    intent.putExtra("load_more_web_url", mImagesAndUrl.getUrl());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImagesAndUrl.getFoods().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mImagesAndUrl.getFoods().size()) {
            return 0;
        } else {
            return 1;
        }
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView mChatAiItemIvFootPic;
        TextView mChatAiItemFootName;
        TextView mChatAiItemFootMoney;

        public MyHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mChatAiItemIvFootPic = (ImageView) itemView.findViewById(R.id.chat_ai_item_iv_foot_pic);
            mChatAiItemFootName = (TextView) itemView.findViewById(R.id.chat_ai_item_foot_name);
            mChatAiItemFootMoney = (TextView) itemView.findViewById(R.id.chat_ai_item_foot_money);
        }
    }
}
