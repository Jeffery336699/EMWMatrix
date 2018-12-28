package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.chat.util.ItemTouchHelperAdapter;
import cc.emw.mobile.chat.util.ItemTouchHelperViewHolder;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.entity.CallInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.bean.JoinDataBean;
import cc.emw.mobile.util.CacheUtils;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import io.socket.client.Socket;

/**
 * Created by ${zrjt} on 2017/3/3. 圈子中聊天记录 在获取聊天记录中预先获取到聊天集合
 */
public class ChatHistoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    public enum ITEM_TYPE {
        ITEM_TYPE_VerListHead,
        ITEM_TYPE_VerList,
        ITEM_TYPE_HorList
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<HistoryMessage> mDatas;
    private List<Integer> onLineList;
    private List<UserInfo> mFollowLists = new ArrayList<>();
    private String name;
    private DisplayImageOptions option, options;

    public ChatHistoriesAdapter(Context mContext, List<HistoryMessage> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
        initOptions();
    }

    public void setData(List<HistoryMessage> mDatas) {
        if (mDatas != null) {
            this.mDatas.clear();
            this.mDatas = mDatas;
        }
    }

    private void initOptions() {
        option = new DisplayImageOptions.Builder()
                //                .showImageOnLoading(R.drawable.cm_img_head)
                //                .showImageForEmptyUri(R.drawable.cm_img_head)
                //                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();
        options = new DisplayImageOptions.Builder()
                //                .showImageOnLoading(R.drawable.cm_img_grouphead)
                //                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                //                .showImageOnFail(R.drawable.cm_img_grouphead)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_HorList.ordinal()) {
            return new MyHorViewHolder(mInflater.inflate(R.layout.listitem_chat_my_follow, parent, false)); //关注列表
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_VerList.ordinal()) {
            return new MyViewHolder(mInflater.inflate(R.layout.listitem_chat_histories, parent, false));    //列表
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_VerListHead.ordinal()) {
            return new MyHeadViewHolder(mInflater.inflate(R.layout.listitem_chat_histories_head, parent, false)); //头部
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).getType() == -1) {
            return ITEM_TYPE.ITEM_TYPE_VerListHead.ordinal();
        } else if (mDatas.get(position).getType() == -2) {
            return ITEM_TYPE.ITEM_TYPE_HorList.ordinal();
        } else {
            return ITEM_TYPE.ITEM_TYPE_VerList.ordinal();
        }
    }

    /**
     * 设置在线人员列表
     *
     * @param onLineList
     */
    public void setOnLineList(List<Integer> onLineList) {
        this.onLineList = onLineList;
    }

    public void setMyFollowingList(List<UserInfo> mFollowLists) {
        this.mFollowLists = mFollowLists;
    }

    @Override
    public void onItemDismiss(final int position) {
        final int sendId = mDatas.get(position).getReceiverID();
        final int type = mDatas.get(position).getType();
        new AlertDialog(mContext).builder().setMsg("确认删除该条聊天记录？")
                .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text)).setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.confirm),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatas.remove(position);
                                notifyDataSetChanged();
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(0, getItemCount());
                                delete(sendId, type, position);
                            }
                        })
                .setNegativeButton(mContext.getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notifyDataSetChanged();
                            }
                        }).show();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof MyHorViewHolder) {
            initFollowListItem(mContext, (MyHorViewHolder) vh);
        } else if (vh instanceof MyViewHolder) {
            initListItem((MyViewHolder) vh, position);
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog(mContext).builder().setMsg("确认删除该条聊天记录")
                                    .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                                    .setPositiveButton(mContext.getString(R.string.confirm),
                                            new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    delete(mDatas.get(position).getReceiverID(), mDatas.get(position).getType(), position);
                                                    mDatas.remove(position);
                                                    notifyDataSetChanged();
                                                    if (EMWApplication.mChatHistory != null) {
                                                        EMWApplication.mChatHistory.remove(position);
                                                    }
                                                }
                                            })
                                    .setNegativeButton(mContext.getString(R.string.cancel),
                                            new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();
                        }
                    }, 300);
                    return false;
                }
            });
        } else if (vh instanceof MyHeadViewHolder) {
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    /**
     * 列表
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView mTvName, mTvTime, mTvContent, mTvCount;

        CircleImageView mCImage;

        ImageView mOnline;

        View itemLayout, mLine; //横线

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvContent = (TextView) itemView.findViewById(R.id.content);
            mTvName = (TextView) itemView.findViewById(R.id.name);
            mTvTime = (TextView) itemView.findViewById(R.id.time);
            mTvCount = (TextView) itemView.findViewById(R.id.count);
            mCImage = (CircleImageView) itemView.findViewById(R.id.head);
            mOnline = (ImageView) itemView.findViewById(R.id.online);
            mLine = itemView.findViewById(R.id.view_line_chat_item);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }

        @Override
        public void onItemClear(Context context) {
            itemLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            mTvName.setTextColor(context.getResources().getColor(R.color.textview_color));
            mTvContent.setTextColor(context.getResources().getColor(R.color.textview_color2));
            mLine.setVisibility(View.VISIBLE);
        }

        @Override
        public void onItemSelected(Context context) {
            itemLayout.setBackgroundColor(context.getResources().getColor(R.color.colorAccents));
            mTvName.setTextColor(context.getResources().getColor(R.color.white));
            mTvContent.setTextColor(context.getResources().getColor(R.color.white));
            mLine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 头部
     */
    public static class MyHeadViewHolder extends RecyclerView.ViewHolder {

        TextView mTvRecent;

        public MyHeadViewHolder(View itemView) {
            super(itemView);
            mTvRecent = (TextView) itemView.findViewById(R.id.tv_chat_list_recent);  //最近联系文本
        }
    }

    /**
     * 水平滑动的view
     */
    public static class MyHorViewHolder extends RecyclerView.ViewHolder {

        HorizontalScrollView mFollowScroll;

        LinearLayout mFollowLayout;

        View line1, line2, line3;

        TextView tvMyFollow, tvLongChat;

        public MyHorViewHolder(View itemView) {
            super(itemView);
            mFollowLayout = (LinearLayout) itemView.findViewById(R.id.ll_chat_follow_contain);
            mFollowScroll = (HorizontalScrollView) itemView.findViewById(R.id.hscr_my_follow);
            line1 = itemView.findViewById(R.id.line_follow_1);
            line2 = itemView.findViewById(R.id.line_follow_1);
            line3 = itemView.findViewById(R.id.line_follow_1);
            tvMyFollow = (TextView) itemView.findViewById(R.id.tv_my_follow);
            tvLongChat = (TextView) itemView.findViewById(R.id.tv_long_chat);
        }
    }

    /////////////////////////////////////

    /**
     * 初始化我关注的人员列表
     *
     * @param mContext
     * @param vh
     */
    private void initFollowListItem(final Context mContext, MyHorViewHolder vh) {
        if (mFollowLists != null && mFollowLists.size() > 0) {
            vh.mFollowLayout.removeAllViews();
            vh.mFollowScroll.setVisibility(View.VISIBLE);
            vh.line1.setVisibility(View.VISIBLE);
            vh.line2.setVisibility(View.VISIBLE);
            for (final ApiEntity.UserInfo user : mFollowLists) {
                if (user != null) {
                    final View view = mInflater.inflate(R.layout.fragment_head_layout, null);
                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.head);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    ImageView online = (ImageView) view.findViewById(R.id.online);
                    if (onLineList != null && onLineList.contains(user.ID))
                        online.setImageResource(R.drawable.circle_is_online);
                    else
                        online.setImageResource(R.drawable.circle_is_not_online);
                    name.setText(user.Name);
                    String uri = String.format(Const.DOWN_ICON_URL,
                            PrefsUtil.readUserInfo().CompanyCode, user.Image);
                    imageView.setTvBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
                    ImageLoader.getInstance().displayImage(uri, new ImageViewAware(imageView), option,
                            new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                    //                    Picasso.with(mContext)
                    //                            .load(uri)
                    //                            .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                    //                            .centerCrop()
                    //                            .config(Bitmap.Config.ALPHA_8)
                    //                            .placeholder(R.color.trans)
                    //                            .error(R.color.trans)
                    //                            .into(imageView);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    view.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            Intent intent1 = new Intent(mContext, ChatActivity.class);
                            intent1.putExtra("type", 1);
                            intent1.putExtra("name", user.Name);
                            intent1.putExtra("SenderID", user.ID);
                            intent1.putExtra("start_anim", false);
                            int[] location = new int[2];
                            v.getLocationInWindow(location);
                            intent1.putExtra("click_pos_y", location[1]);
                            mContext.startActivity(intent1);
                        }
                    });
                    vh.mFollowLayout.addView(view, params);
                }
            }
        } else {
            vh.mFollowLayout.removeAllViews();
            vh.mFollowScroll.setVisibility(View.GONE);
            vh.line1.setVisibility(View.GONE);
            vh.line2.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化item
     *
     * @param vh
     * @param position
     */

    private void initListItem(final MyViewHolder vh, int position) {
        final HistoryMessage msg = mDatas.get(position);
        vh.mLine.setVisibility(position == mDatas.size() - 1 ? View.GONE : View.VISIBLE);
        vh.mTvTime.setText(StringUtils.friendly_time((msg.getMessage().getCreateTime())));
        vh.mTvCount.setVisibility(msg.getUnReadCount() == 0 ? View.INVISIBLE : View.VISIBLE);
        vh.mTvCount.setText(msg.getUnReadCount() + "");
        //            个人消息
        if (msg.getType() == 1) {
            vh.mOnline.setVisibility(View.VISIBLE);
            if (msg.Receiver != null) {
                name = msg.Receiver.Name;
                String image = msg.Receiver.Image;
                vh.mOnline.setImageResource(onLineList != null && onLineList.contains(msg.Receiver.ID) ? R.drawable.circle_is_online :
                        R.drawable.circle_is_not_online);
                vh.mCImage.setTvBg(EMWApplication.getIconColor(msg.Receiver.ID), name, 40);
                String uriPerson = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                        image);
                ImageLoader.getInstance().displayImage(uriPerson, new ImageViewAware(vh.mCImage), option,
                        new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                //                    Picasso.with(mContext)
                //                            .load(uriPerson)
                //                            .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                //                            .centerCrop()
                //                            .config(Bitmap.Config.ALPHA_8)
                ////                            .placeholder(R.drawable.cm_img_head)
                ////                            .error(R.drawable.cm_img_head)
                //                            .into(vh.mCImage);
            } else {
                vh.mCImage.setTvBg(EMWApplication.getIconColor(msg.Receiver.ID), name, 40);
            }
        } else {
            vh.mOnline.setVisibility(View.GONE);
            if (msg.getMessage().Group != null) {
                name = msg.getMessage().Group.Name;
                String image = msg.getMessage().Group.Image;
                String uriGroup = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                        image);
                vh.mCImage.setImageResource(R.drawable.cm_img_grouphead);
                ImageLoader.getInstance().displayImage(uriGroup, new ImageViewAware(vh.mCImage), options,
                        new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
                //                    Picasso.with(mContext)
                //                            .load(uriGroup)
                //                            .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                //                            .centerCrop()
                ////                            .placeholder(R.drawable.cm_img_grouphead)
                //                            .error(R.drawable.cm_img_grouphead)
                //                            .config(Bitmap.Config.ALPHA_8)
                //                            .into(vh.mCImage);
            }
        }

        vh.mTvName.setText(TextUtils.isEmpty(name) ? "" : name);

        switch (msg.getMessage().getType()) {
            case ApiEnum.MessageType.Audio:
                vh.mTvContent.setText(R.string.audio);
                break;
            case ApiEnum.MessageType.Video:
                vh.mTvContent.setText(R.string.video);
                break;
            case ApiEnum.MessageType.Image:
                vh.mTvContent.setText(R.string.photoes);
                break;
            case ApiEnum.MessageType.Attach:
                vh.mTvContent.setText(R.string.doc);
                break;
            case ApiEnum.MessageType.Share:
                vh.mTvContent.setText(R.string.task_attachment_share);
                break;
            case ApiEnum.MessageType.Task:
                vh.mTvContent.setText("[任务]");
                break;
            case ApiEnum.MessageType.Flow:
                vh.mTvContent.setText(R.string.follow_mes);
                break;
            case ApiEnum.MessageType.Robot:
            case ApiEnum.MessageType.RobotSchedule:
                vh.mTvContent.setText("[智能聊天回复]");
                break;
            case ApiEnum.MessageType.CHAT_LOCATION:
                vh.mTvContent.setText("[位置]");
                break;
            case ChatContent.DYNAMIC:
                vh.mTvContent.setText("[分享消息]");
                break;
            case ChatContent.CHAT_SHARE_LOCATION:
                vh.mTvContent.setText("[位置共享]");
                break;
            //            case 0:
            case ApiEnum.MessageType.PhoneStateMsg:
                try {
                    CallInfo callInfo = new Gson().fromJson(msg.getMessage().getContent(), CallInfo.class);
                    vh.mTvContent.setText(callInfo.type == 1 ? "[语音通话]" : "[视频通话]");
                } catch (Exception e) {
                }
                break;
            default:
                if (msg.getMessage().getContent().contains("FlowID")) {
                    vh.mTvContent.setText(R.string.follow_mes);
                } else if (msg.getMessage().getContent().contains("m.amap")) {
                    vh.mTvContent.setText(R.string.road_way);
                } else if (msg.getMessage().getContent().contains("ControlType")) {
                    vh.mTvContent.setText("[通知]");
                } else {
                    ChatUtils.spannableEmoticonFilter(vh.mTvContent, msg.getMessage().getContent());
                }
                break;
        }
        //跳转聊天窗口
        vh.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Log.e("ChatHistoriesAdapter", "---setOnClickListener-----");
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("unReadNum", msg.getUnReadCount());
                intent.putExtra("SenderID", msg.getReceiverID());
                intent.putExtra("type", msg.getType());
                intent.putExtra("GroupID", msg.getMessage().getGroupID());
                // Log.e("ChatHistoriesAdapter","---setOnClickListener-----SenderID="+msg.getReceiverID()+" GroupID = "+msg.getMessage().getGroupID()+" userid = "+msg.getUserID());
                //JoinDataBean joinDataBean = new JoinDataBean(msg.getUserID(),msg.getReceiverID(),msg.getMessage().getGroupID());
                //Log.e("ChatHistoriesAdapter","---join data ok -===="+new Gson().toJson(joinDataBean));
                //socketSumitJoin(new JoinDataBean(msg.getUserID(),msg.getReceiverID(),msg.getMessage().getGroupID()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                intent.putExtra("click_pos_y", location[1]);
                //                HelpUtil.hideSoftInput(mContext, v);
                mContext.startActivity(intent);
                vh.mTvCount.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void socketSumitJoin(final JoinDataBean joinDataBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMWApplication emw = (EMWApplication) mContext.getApplicationContext();
                    //Socket socketIO = SingleIOSocket.getIoSocket();
                    Socket socketIO = emw.getAppIOSocket();
                    if (!socketIO.connected()) {
                        socketIO.connect();
                    }
                    socketIO.emit("join", new Gson().toJson(joinDataBean));
                    Log.e("ChatHistoriesAdapter", "---join data -=" + new Gson().toJson(joinDataBean));
                } catch (Exception ex) {

                }
            }
        }).start();
    }

    /**
     * 删除聊天记录
     */
    private void delete(int sendID, final int type, final int position) {
        API.Message.RemoveChatRecord(sendID, type, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(mContext, "删除失败！");
            }

            @Override
            public void onSuccess(String result) {
                if (mDatas.size() == 2) {
                    Intent intent = new Intent();
                    intent.setAction(ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                    mContext.sendBroadcast(intent);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<HistoryMessage> mDatas = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "mDatas",
                                new TypeToken<List<HistoryMessage>>() {
                                }.getType());
                        mDatas.remove(position);
                        Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                        mContext.sendBroadcast(intent);
                    }
                }).start();
                ToastUtil.showToast(mContext, R.string.delete_success,
                        R.drawable.tishi_ico_gougou);
            }
        });
    }
}
