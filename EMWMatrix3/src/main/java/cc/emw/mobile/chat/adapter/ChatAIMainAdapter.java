package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.ChatAIBean;
import cc.emw.mobile.chat.model.bean.MessageChatAITest;
import cc.emw.mobile.chat.view.RecyclerViewLinearLayoutManager;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.util.DisplayUtil;

/**
 * Created by sunny.du on 2017/1/10.
 *
 */
public class ChatAIMainAdapter extends RecyclerView.Adapter<ChatAIMainAdapter.MyHolder> {
    private Context mContext;
    private List<Message> mMsgList;
    private Message messageChatAI;
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat format; //HH:mm

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * 根据消息类型创建不同的view
         */
        switch (viewType) {
            case ChatContent.WEB_MSG://网页信息消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_ai_web_item, parent, false), this);
            case ChatContent.MAP_MSG://地图消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_ai_map_item, parent, false), this);
            case ChatContent.SCHEDULE_MSG://日程相关消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_ai_list_item, parent, false), this);
            case ChatContent.WEATHER_MSG://天气预报
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_ai_weather_item, parent, false), this);
            default://普通消息和其他消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_ai_default_item, parent, false), this);
        }
    }

    /**
     * 给每个条目赋值
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        /**
         * 根据不同视图绑定相应的样式
         */
        Message message = mMsgList.get(position);
        switch (message.getType()) {
            case ChatContent.WEB_MSG://网页信息消息
                break;
            case ChatContent.MAP_MSG://地图消息
                break;
            case ChatContent.SCHEDULE_MSG://日程相关消息
                if ("找不到相关数据".equals(message.getContent().trim())) {
                    staticBindText(holder.mTvAIChatMsgContentRece, message.getContent(),null);
                    holder.mCvMsg.setVisibility(View.GONE);
                } else {
                    holder.mCvMsg.setVisibility(View.VISIBLE);
                    staticBindText(holder.mTvAIChatMsgContentRece, "以下是查询到的结果",null);
                    List<ApiEntity.UserSchedule> scheduleList =new Gson().fromJson(message.getContent(), new TypeToken<List<ApiEntity.UserSchedule>>() {}.getType());
                    switchListMsg(holder, scheduleList, message.getType(), null);//进入子链表方法
                }
                break;
            case ChatContent.WEATHER_MSG://天气预报
                break;
            default://普通消息和其他消息
                if (message.getSenderID() == -1) {//机器人发送消息
                    holder.mRlChatAIRece.setVisibility(View.VISIBLE);
                    holder.mRlChatAISend.setVisibility(View.GONE);
                    holder.mRlAiChatTimeMsg.setVisibility(View.GONE);
                    final ChatAIBean aiBean = new Gson().fromJson(message.getContent(), ChatAIBean.class);
                    staticBindText(holder.mTvAIChatMsgContentRece, aiBean.getText(),aiBean.getUrl());
                } else {//发送人消息
                    holder.mRlChatAIRece.setVisibility(View.GONE);
                    holder.mRlChatAISend.setVisibility(View.VISIBLE);
                    holder.mRlAiChatTimeMsg.setVisibility(View.VISIBLE);
                    holder.mTvAiChatTimeMsg.setText(format.format(new Date()));
                    staticBindText(holder.mTvAiChatMsgContentSend, message.getContent(),null);

                }

                break;
        }
        if (mMsgList.size() == position + 1) {
            holder.mItemView.setPadding(0, 0, 0, DisplayUtil.dip2px(mContext, 56));//设置底部高度，始终使最后一条消息在输入框上面
        } else {
            holder.mItemView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * 绑定文本消息的公共方法
     */
    private void staticBindText(TextView contentView, String msg,String url) {
        contentView.setText(msg);
        if(url != null && (!"".equals(url.trim()))){
            String webLink = "<a href=\'"+url+"\'>点击查看</a>";
            contentView.append(Html.fromHtml(webLink));
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
    static class MyHolder extends RecyclerView.ViewHolder {
        public MyHolder(View itemView, ChatAIMainAdapter adapter) {
            super(itemView);
            initView(itemView, adapter);
        }

        /**
         * view实例区**************************************************************
         */
        View mItemView;
        RecyclerView mChatRvMessage;
        RelativeLayout mRlChatAIRece;
        RelativeLayout mRlChatAISend;
        RelativeLayout mRlAiChatTimeMsg;
        TextView mTvAiChatMsgContentSend;
        TextView mTvAIChatMsgContentRece;
        TextView mTvAiChatTimeMsg;
        CardView mCvMsg;
        /**
         * 绑定所有视图信息
         */
        private void initView(View itemView, ChatAIMainAdapter adapter) {
            this.mItemView = itemView;
            mTvAIChatMsgContentRece = (TextView) itemView.findViewById(R.id.tv_ai_chat_msg_content_rece);
            mRlChatAIRece = (RelativeLayout) itemView.findViewById(R.id.rl_ai_msg_text_rece_all);
            mCvMsg = (CardView) itemView.findViewById(R.id.cv_msg);
            switch (adapter.messageChatAI.getType()) {
                case ChatContent.WEB_MSG://网页信息消息
                    break;
                case ChatContent.MAP_MSG://地图消息
                    break;
                case ChatContent.SCHEDULE_MSG://日程相关消息
                    mChatRvMessage = (RecyclerView) itemView.findViewById(R.id.chat_ai_lv_message2);
                    break;
                case ChatContent.WEATHER_MSG://天气预报
                    break;
                default://普通消息和其他消息
                    mRlChatAISend = (RelativeLayout) itemView.findViewById(R.id.rl_ai_msg_text_send_all);
                    mRlAiChatTimeMsg = (RelativeLayout) itemView.findViewById(R.id.rl_ai_chat_time_msg);
                    mTvAiChatMsgContentSend = (TextView) itemView.findViewById(R.id.tv_ai_chat_msg_content_send);
                    mTvAiChatTimeMsg = (TextView) itemView.findViewById(R.id.tv_ai_chat_time_msg);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    /**
     * 设置消息类型
     * 暂时模拟数据  后续需要修改
     */
    @Override
    public int getItemViewType(int position) {
        messageChatAI = mMsgList.get(position);

        return messageChatAI.getType();
    }

    public ChatAIMainAdapter(Context context, List<Message> msgList) {
        this.mContext = context;
        this.mMsgList = msgList;
        mLayoutInflater = LayoutInflater.from(mContext);
        format = new SimpleDateFormat("HH:mm", Locale.getDefault());//yyyy-MM-dd
    }

    /**
     * @des 初始化子列表信息  绑定数据
     * @cal
     */
    private void switchListMsg(MyHolder holder,  List<ApiEntity.UserSchedule> userScheduleList, int msgSunType, MessageChatAITest msg) {
        switch (msgSunType) {
            case ChatContent.SCHEDULE_MSG://日程展示列表
                holder.mChatRvMessage.setItemAnimator(new DefaultItemAnimator());
                holder.mChatRvMessage.setAdapter(new ChatAIListScheduleAdapter(mContext, userScheduleList));
                holder.mChatRvMessage.setLayoutManager(new RecyclerViewLinearLayoutManager(mContext));
                break;
            default:
                break;
        }
    }



    public void setData(List<Message> list) {
        this.mMsgList = list;
    }
}