package cc.emw.mobile.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.MyStarMsgActivity;
import cc.emw.mobile.chat.model.bean.ImprotanceMessage;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.DisplayUtil;


/**
 * Created by sunny.du on 2017/5/10.
 */

public class ChatImportanceMessageAdapter extends RecyclerView.Adapter<ChatImportanceMessageAdapter.MyHolder> {
    private Context mContext;
    private List<ImprotanceMessage> importanceMsgList;
    private LayoutInflater mLayoutInflater;
    private Set<Integer> selectorMessageList;
    private boolean isStartAnim;
    private MyStarMsgActivity.GetMessageListener listener;

    public void setListener(MyStarMsgActivity.GetMessageListener listener) {
        this.listener = listener;
    }

    public void setStartAnim(boolean isStartAnim) {
        this.isStartAnim = isStartAnim;
    }

    public Set<Integer> getSelectorMessageList() {
        return selectorMessageList;
    }

    public void setSelectorMessageList(Set<Integer> selectorMessageList) {
        this.selectorMessageList = selectorMessageList;
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener listener) {
        onSelectListener = listener;
    }

    public void getAllSelector() {
        if (importanceMsgList.size() == selectorMessageList.size()) {
            for (int i = 0; i < importanceMsgList.size(); i++) {
                ImprotanceMessage improtanceMessage = importanceMsgList.get(i);
                improtanceMessage.setIsSelector(false);
                importanceMsgList.set(i, improtanceMessage);
                selectorMessageList.remove(improtanceMessage.getID());
            }
        } else {
            for (int i = 0; i < importanceMsgList.size(); i++) {
                ImprotanceMessage improtanceMessage = importanceMsgList.get(i);
                improtanceMessage.setIsSelector(true);
                importanceMsgList.set(i, improtanceMessage);
                selectorMessageList.add(improtanceMessage.getID());
            }
        }
        if (onSelectListener != null) {
            onSelectListener.onSelect(selectorMessageList.size());
        }
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(mLayoutInflater.inflate(R.layout.chat_importance_message_item, parent, false));
    }

    boolean isChangeMsgFlag = false;

    public boolean getIsChangeMsgFlag() {
        return isChangeMsgFlag;
    }

    public void setIsChangeMsgFalg(boolean isChangeMsgFlag) {
        this.isChangeMsgFlag = isChangeMsgFlag;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final ImprotanceMessage message = importanceMsgList.get(position);
        UserInfo userinfo = EMWApplication.personMap.get(message.getSenderID());
        if (userinfo != null) {
            holder.mTvImportanceName.setText(userinfo.Name);
        } else {
            holder.mTvImportanceName.setText("已停用账号");
        }
        ChatUtils.spannableEmoticonFilter(holder.mTvImportanceContent, message.getContent());
        holder.mTvImportanceTimeInfo.setText(message.getCreateTime());
        if (message.getIsSelector()) {
            holder.mCheckBox.setChecked(true);
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setChecked(false);
            holder.mCheckBox.setVisibility(View.GONE);
        }

        if (isChangeMsgFlag) {
            TranslateAnimation animation1 = new TranslateAnimation(0, DisplayUtil.dip2px(mContext, 30), 0, 0);
            animation1.setDuration(isStartAnim ? 400 : 0);//设置动画持续时间
            animation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            holder.mCheckBox.setAnimation(animation1);
            holder.mTvImportanceContent.setAnimation(animation1);
            holder.mTvImportanceName.setAnimation(animation1);
        } else {
            TranslateAnimation animation1 = new TranslateAnimation(DisplayUtil.dip2px(mContext, 30), 0, 0, 0);
            animation1.setDuration(isStartAnim ? 400 : 0);//设置动画持续时间
            animation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            holder.mCheckBox.setAnimation(animation1);
            holder.mTvImportanceContent.setAnimation(animation1);
            holder.mTvImportanceName.setAnimation(animation1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChangeMsgFlag) {
                    isStartAnim = false;
                    boolean isChecked = !holder.mCheckBox.isChecked();
                    message.setIsSelector(isChecked);
                    importanceMsgList.set(position, message);
                    if (isChecked) {
                        selectorMessageList.add(message.getID());
                    } else {
                        selectorMessageList.remove(message.getID());
                    }
                    if (onSelectListener != null) {
                        onSelectListener.onSelect(selectorMessageList.size());
                    }
                    notifyItemChanged(position);
                }else {
                    listener.GetMessageIndex(message);
                }
            }
        });
//        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                message.setIsSelector(isChecked);
//                importanceMsgList.set(position, message);
//                if (isChecked) {
//                    selectorMessageList.add(message.getID());
//                } else {
//                    selectorMessageList.remove(message.getID());
//                }
//                if (onSelectListener != null) {
//                    onSelectListener.onSelect(selectorMessageList.size());
//                }
//            }
//        });
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView mTvImportanceName;
        TextView mTvImportanceContent;
        TextView mTvImportanceTimeInfo;
        android.widget.CheckBox mCheckBox;
        View mItemView;

        public MyHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mTvImportanceName = (TextView) itemView.findViewById(R.id.tv_importance_name);
            mTvImportanceContent = (TextView) itemView.findViewById(R.id.tv_importance_content);
            mTvImportanceTimeInfo = (TextView) itemView.findViewById(R.id.tv_importance_time_info);
            mCheckBox = (android.widget.CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @Override
    public int getItemCount() {
        return importanceMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setData(List<ImprotanceMessage> data) {
        this.importanceMsgList = data;
    }

    public ChatImportanceMessageAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        importanceMsgList = new ArrayList<>();
        selectorMessageList = new HashSet<>();
    }

    public interface OnSelectListener {
        void onSelect(int size);
    }
}
