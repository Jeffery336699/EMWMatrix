package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatWebActivity;
import cc.emw.mobile.chat.model.bean.AiMsgBean;
import cc.emw.mobile.chat.factory.ImageLoadFactory;

/**
 * Created by sunny.du on 2017/1/16.
 * 日程专用
 */
public class ChatAIListImageAdapter extends RecyclerView.Adapter<ChatAIListImageAdapter.MyHolder> {
    private Context mContext;
    private AiMsgBean mImagesAndUrl;
    /**
     * 模拟数据  msgList user传入空值
     */
    public ChatAIListImageAdapter(Context context, AiMsgBean imagesAndUrl) {
        this.mContext = context;
        this.mImagesAndUrl=imagesAndUrl;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_image_item_0, parent, false));
        }else{
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_image_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if(position < mImagesAndUrl.getImages().length) {
            DisplayImageOptions optionesMsgImage = ImageLoadFactory.getChatApdaterImage();
            ImageLoader.getInstance().displayImage(mImagesAndUrl.getImages()[position], holder.mIvChatAiListImageItem, optionesMsgImage);
        }else{
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO跳转WebView
                    Intent intent=new Intent(mContext, ChatWebActivity.class);
                    intent.putExtra("load_more_web_url",mImagesAndUrl.getUrl());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImagesAndUrl.getImages().length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < mImagesAndUrl.getImages().length) {
            return 0;
        }else{
            return 1;
        }
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView mIvChatAiListImageItem;
        View mItemView;
        public MyHolder(View itemView) {
            super(itemView);
            this.mItemView=itemView;
            mIvChatAiListImageItem= (ImageView) itemView.findViewById(R.id.iv_chat_ai_list_image_item);
        }
    }
}
