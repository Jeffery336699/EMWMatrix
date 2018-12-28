package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.chat.utils.DateUtil;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by sunny.du on 2017/1/16.
 * 日程专用
 */
public class ChatAIListScheduleAdapter extends RecyclerView.Adapter<ChatAIListScheduleAdapter.MyHolder> {
    private Context mContext;
    private List<ApiEntity.UserSchedule> msgList;
    private int viewType;

    /**
     * 模拟数据  msgList user传入空值
     */
    public ChatAIListScheduleAdapter(Context context, List<ApiEntity.UserSchedule> msgList) {
        this.mContext = context;
        this.msgList = msgList;
    }

    @Override
    public ChatAIListScheduleAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.viewType = viewType;
        if (viewType == ChatContent.ITEM_TITLE) {//title
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_schedule_item_title, parent, false), this);
        } else {//内容条目
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_ai_list_schedule_item, parent, false), this);
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (position == ChatContent.ITEM_TITLE) {
            String startTime = msgList.get(0).StartTime;
            if (startTime != null && !("".equals(startTime))) {
                holder.mTvSheduleDayInfo.setText(DateUtil.getStringTime("yyyy-MM-dd", DateUtil.getDate("yyyy-MM-dd", startTime)));//当前时间设置
            }
        } else {
            if (msgList.size() == position) {
                holder.mViewChatAiListLine.setVisibility(View.GONE);
            } else {
                holder.mViewChatAiListLine.setVisibility(View.VISIBLE);
            }
            final ApiEntity.UserSchedule scheduleInfo = msgList.get(position - 1);
            Date dateStart = DateUtil.getDate("yyyy-MM-dd HH:mm:ss", scheduleInfo.StartTime);
            holder.mTvChatAiScheduleTile.setText(scheduleInfo.Title);
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, scheduleInfo.T_CreatorImage);
            ImageLoader.getInstance().displayImage(uri, holder.mCivAiChatScheduleHeader, ImageLoadFactory.getChatOptiones());
            int hhStart = Integer.parseInt(DateUtil.getStringTime("HH", dateStart));//日程开启时间的小时int值
            if (hhStart >= 12) {
                holder.mTvChatAiScheduleTime.setText("下午 " + (hhStart - 12) + ":" + DateUtil.getStringTime("mm", dateStart));
            } else {
                holder.mTvChatAiScheduleTime.setText("上午 " + hhStart + ":" + DateUtil.getStringTime("mm", dateStart));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2017-02-10 00:00:00
            long time = 0;
            try {
                Date start = simpleDateFormat.parse(scheduleInfo.StartTime);
                Date over = simpleDateFormat.parse(scheduleInfo.OverTime);
                long timeStart = start.getTime();
                long timeOver = over.getTime();
                time = (timeOver - timeStart) / 1000 / 60L;//日程持续的分钟数
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (time / 60 == 0) {//小时
                holder.mTvChatAiScheduleStartTime.setText(time + "");
            } else if (time / 60 <= 24) {
                holder.mTvChatAiScheduleStartTime.setText(time / 60 + "小时");
            } else if (time / 60 / 24 >= 1) {
                holder.mTvChatAiScheduleStartTime.setText(time / 60 / 24 + "天");
            }

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CalendarInfoActivity2.class);
                    intent.putExtra(CalendarInfoActivity2.CALENDARID, scheduleInfo.ID);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ChatContent.ITEM_TITLE;
        }
        return ChatContent.ITEM_STATIC;
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView mTvSheduleDayInfo;//日程時間
        TextView mTvChatAiScheduleTile;//日程標題
        TextView mTvChatAiScheduleTime;//日程几点开始
        TextView mTvChatAiScheduleStartTime;//距离日程开始的事件 小时分钟
        CircleImageView mCivAiChatScheduleHeader;//日程相关人员的头像
        View mItemView;
        View mViewChatAiListLine;

        public MyHolder(View itemView, ChatAIListScheduleAdapter adapter) {
            super(itemView);
            this.mItemView = itemView;
            if (adapter.viewType == ChatContent.ITEM_TITLE) {
                mTvSheduleDayInfo = (TextView) itemView.findViewById(R.id.tv_schedule_day_info);
            } else {
                mTvChatAiScheduleTile = (TextView) itemView.findViewById(R.id.tv_chat_ai_schedule_title);
                mTvChatAiScheduleTime = (TextView) itemView.findViewById(R.id.tv_chat_ai_schedule_time);
                mTvChatAiScheduleStartTime = (TextView) itemView.findViewById(R.id.tv_chat_ai_schedule_start_time);
                mCivAiChatScheduleHeader = (CircleImageView) itemView.findViewById(R.id.civ_ai_chat_schedule_header);
                mViewChatAiListLine = itemView.findViewById(R.id.view_chat_ai_list_line);
            }
        }
    }
}
