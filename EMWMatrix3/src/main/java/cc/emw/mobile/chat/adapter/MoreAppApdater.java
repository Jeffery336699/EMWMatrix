package cc.emw.mobile.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatInformationActivity;
import cc.emw.mobile.chat.ChatPOIActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.map.activity.ShareLocationActivity;
import cc.emw.mobile.chat.model.bean.MapBean;
import cc.emw.mobile.util.ToastUtil;

/**
 * Created by sunny.du on 2017/4/27.
 */

public class MoreAppApdater extends RecyclerView.Adapter<MoreAppApdater.MyHolder> {
    private Context mContext;
    private final int appCount=7;
    private MapBean mMapBean;
    public void setMapBean(MapBean mapBean){
        this.mMapBean=mapBean;
    }
    public MoreAppApdater(Context context){
        this.mContext=context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder( LayoutInflater.from(mContext).inflate(R.layout.chat_more_app_item, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final Intent intent=new Intent();
        intent.setAction(ChatContent.REFRESH_CHAT_COLSE_MORE_VIEW);
        switch (position){
            case 0://搜索
                holder.mIvChatMoreAppItem.setImageResource(R.drawable.chat_nearby_button);
                holder.mIvChatMoreAppItemName.setText("搜索");
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ToastUtil.showToast(mContext,"玩命开发中。。。");
                        if(mMapBean!=null) {
                            mContext.sendBroadcast(intent);
                            Intent intent = new Intent(mContext, ChatPOIActivity.class);
                            intent.putExtra("longitude", mMapBean.longitude);
                            intent.putExtra("latitude", mMapBean.latitude);
                            intent.putExtra("cityCode", mMapBean.cityCode);
                            intent.putExtra("start_anim", false);
                            ((Activity) mContext).startActivityForResult(intent, 544);
                        }else{
                            ToastUtil.showToast(mContext,"未获取到地里位置信息，请检查网络或GPS");
                        }
                    }
                });
                break;
            case 1://视频聊天
                holder.mIvChatMoreAppItem.setImageResource(R.drawable.open_video_chat);
                holder.mIvChatMoreAppItemName.setText("视频通话");
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setAction(ChatContent.OPEN_VIDEO_CHAT);
                        mContext.sendBroadcast(intent);

                    }
                });
                break;
            case 2://定位
                holder.mIvChatMoreAppItem.setImageResource(R.drawable.chat_icon_location);
                holder.mIvChatMoreAppItemName.setText("位置");
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(ChatContent.REFRESH_CHAT_OPEN_MAP);
                        mContext.sendBroadcast(intent);
                    }
                });
                break;
            case 4://文件
                holder.mIvChatMoreAppItem.setImageResource(R.drawable.chat_icon_search);
                holder.mIvChatMoreAppItemName.setText("我的文件");
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(ChatContent.REFRESH_CHAT_OPEN_FILE);
                        mContext.sendBroadcast(intent);
                    }
                });
                break;
            case 6://发布
                holder.mIvChatMoreAppItem.setImageResource(R.drawable.chat_release_msg);
                holder.mIvChatMoreAppItemName.setText("发布");
                holder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.sendBroadcast(intent);
                       Intent intent=new Intent(mContext, ChatInformationActivity.class);
                        intent.putExtra("start_anim", false);
                        ((Activity)mContext).startActivityForResult(intent,543);
//                        ToastUtil.showToast(mContext,"玩命开发中。。。");
                    }
                });
                break;
        }
    }

    private void startLocation() {
        if(mMapBean!=null) {
            Intent intent = new Intent();
            intent.putExtra("longitude", mMapBean.longitude);
            intent.putExtra("latitude", mMapBean.latitude);
            intent.putExtra("cityCode", mMapBean.cityCode);
            intent.setClass(mContext, ShareLocationActivity.class);
            ((Activity) mContext).startActivityForResult(intent, 9821);
        }else{
            ToastUtil.showToast(mContext,"未获取到地里位置信息，请检查网络或GPS");
        }
    }

    @Override
    public int getItemCount() {
        return appCount;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        Context mContext;
        ImageView mIvChatMoreAppItem;
        TextView mIvChatMoreAppItemName;
        View mItemView;
        public MyHolder(View itemView, Context context) {
            super(itemView);
            this.mItemView=itemView;
            this.mContext=context;
            mIvChatMoreAppItem= (ImageView) itemView.findViewById(R.id.iv_chat_more_app_item);
           mIvChatMoreAppItemName= (TextView) itemView.findViewById(R.id.tv_chat_more_app_item_name);
        }
    }

}
