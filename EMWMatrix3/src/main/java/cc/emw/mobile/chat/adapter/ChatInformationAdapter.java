package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.ChatDynamicDetailActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.task.activity.TaskDetailActivity;

/**
 * Created by sunny.du on 2017/5/16.
 */

public class ChatInformationAdapter extends RecyclerView.Adapter<ChatInformationAdapter.MyHolder> {
    private Context mContext;
    private List<UserNote> mUserNoteList;
    private LayoutInflater mLayoutInflater;
    private int selectorPosition = -1;
    private int userNoteType = -1;
    private UserNote userNote;
    private boolean isEdit;

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener listener) {
        onSelectListener = listener;
    }

    public UserNote getUserNote() {
        return userNote;
    }

    public void setUserNote(UserNote userNote) {
        this.userNote = userNote;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public boolean getIsEdit() {
        return isEdit;
    }

    public void setSelectorPosition(int selectorPosition) {
        this.selectorPosition = -1;
    }

    public int getUserNoteType() {
        return userNoteType;
    }


    public void setmUserNoteList(List<UserNote> userNoteList) {
        this.mUserNoteList = userNoteList;
    }

    public ChatInformationAdapter(Context context) {
        mUserNoteList = new ArrayList<>();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(mLayoutInflater.inflate(R.layout.chat_information_item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final UserNote userNote = mUserNoteList.get(position);
        StringBuilder sb = new StringBuilder();
        holder.itemView.setOnClickListener(null);
        holder.mTvChatInformationItemTitle.setText("");
        holder.mTvChatInformationItemTime.setText("");
        holder.mTvChatInformationItemDes.setText("");
        switch (userNote.Type) {
            case ApiEnum.UserNoteAddTypes.Appoint: // 约会
                holder.mIvChatInformationItemType.setImageResource(R.drawable.icon_yue_hui);
                if (userNote.info.schedule != null && userNote.info.schedule.size() != 0) {
                    sb.append(userNote.info.schedule.get(0).StartTime);
                    sb.append(" / ");
                    sb.append(userNote.info.schedule.get(0).OverTime);
                    holder.mTvChatInformationItemTitle.setText(userNote.info.schedule.get(0).Title);
                    holder.mTvChatInformationItemTime.setText(sb.toString());
                    holder.mTvChatInformationItemDes.setText(userNote.info.schedule.get(0).Remark);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {//查看任务详情
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ChatDynamicDetailActivity.class);
                            userNote.info.schedule.get(0).ID = userNote.ID;
                            intent.putExtra("userNote", userNote.info.schedule.get(0));
                            intent.putExtra("userNoteID", userNote.TypeId);
                            intent.putExtra("start_anim", false);
                            mContext.startActivity(intent);
                        }
                    });
                }
                break;
            case ApiEnum.UserNoteAddTypes.Email: // 邮件
                holder.mIvChatInformationItemType.setImageResource(R.drawable.icon_mail);
                if (userNote.info.schedule != null && userNote.info.schedule.size() != 0) {
                    sb.append(userNote.info.schedule.get(0).StartTime);
                    sb.append(" / ");
                    sb.append(userNote.info.schedule.get(0).OverTime);
                    holder.mTvChatInformationItemTitle.setText(userNote.info.schedule.get(0).Title);
                    holder.mTvChatInformationItemTime.setText(sb.toString());
                    holder.mTvChatInformationItemDes.setText(userNote.info.schedule.get(0).Remark);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {//查看任务详情
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, CalendarInfoActivity2.class);
                            intent.putExtra("calendarId", userNote.TypeId);
                            intent.putExtra("start_anim", false);
                            mContext.startActivity(intent);
                        }
                    });
                }
                break;
            case ApiEnum.UserNoteAddTypes.Task: // 工作分派
                holder.mIvChatInformationItemType.setImageResource(R.drawable.icon_task);
                if (userNote.info.task != null && userNote.info.task.size() != 0) {
                    sb.append(userNote.info.task.get(0).StartTime);
                    sb.append(" / ");
                    sb.append(userNote.info.task.get(0).FinishTime);
                    holder.mTvChatInformationItemTitle.setText(userNote.info.task.get(0).Title);
                    holder.mTvChatInformationItemTime.setText(sb.toString());
                    holder.mTvChatInformationItemDes.setText(TextUtils.isEmpty(userNote.info.task.get(0).Mark) ? ""
                            : Html.fromHtml(userNote.info.task.get(0).Mark));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {//查看任务详情
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, TaskDetailActivity.class);
                            intent.putExtra(TaskDetailActivity.TASK_ID, userNote.info.task.get(0).ID);
                            intent.putExtra("start_anim", false);
                            mContext.startActivity(intent);
                        }
                    });
                }
                break;
        }

        holder.mIvChatInformationItemType.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        holder.mCheckBoxInformation.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);

        if (selectorPosition == position) {
            holder.mCheckBoxInformation.setChecked(true);
        } else {
            holder.mCheckBoxInformation.setChecked(false);
        }

        holder.mRlCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    if (selectorPosition == position) {
                        return;
                    }
                    selectorPosition = position;
                    userNoteType = userNote.Type;
                    ChatInformationAdapter.this.userNote = userNote;

                    if (onSelectListener != null) {
                        onSelectListener.onSelect(selectorPosition);
                    }
                } else {
                    isEdit = true;
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserNoteList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView mIvChatInformationItemType;
        TextView mTvChatInformationItemTitle;
        TextView mTvChatInformationItemDes;
        CheckBox mCheckBoxInformation;
        RelativeLayout mRlCheck;
        //        LinearLayout mLlChatInformationRoot;
        TextView mTvChatInformationItemTime;

        public MyHolder(View itemView) {
            super(itemView);
            mIvChatInformationItemType = (ImageView) itemView.findViewById(R.id.iv_chat_information_item_type);
            mTvChatInformationItemTitle = (TextView) itemView.findViewById(R.id.tv_chat_information_item_title);
            mTvChatInformationItemDes = (TextView) itemView.findViewById(R.id.tv_chat_information_item_des);
            mRlCheck = (RelativeLayout) itemView.findViewById(R.id.rl_check_layout);
//            mLlChatInformationRoot = (LinearLayout) itemView.findViewById(R.id.ll_chat_information_root);
            mCheckBoxInformation = (CheckBox) itemView.findViewById(R.id.checkbox_information);
            mTvChatInformationItemTime = (TextView) itemView.findViewById(R.id.tv_chat_information_item_time);
        }
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }
}
