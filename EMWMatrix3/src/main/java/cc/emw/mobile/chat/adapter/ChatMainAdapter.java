package cc.emw.mobile.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatDynamicDetailActivity;
import cc.emw.mobile.chat.PosInfoActivity;
import cc.emw.mobile.chat.SharePosActivity;
import cc.emw.mobile.chat.TestVideoActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.chat.model.IChatMsgDBModel;
import cc.emw.mobile.chat.model.bean.AiMsgBean;
import cc.emw.mobile.chat.model.bean.Audios;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.model.bean.Follow;
import cc.emw.mobile.chat.model.bean.ImprotanceMessage;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.chat.model.bean.MessageChatAITest;
import cc.emw.mobile.chat.model.bean.Task;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.util.ActionSheet;
import cc.emw.mobile.chat.utils.DateUtil;
import cc.emw.mobile.chat.utils.MediaPlayerManger;
import cc.emw.mobile.chat.utils.VideoPlayer;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.chat.view.EventRecyclerView;
import cc.emw.mobile.chat.view.MKLoader;
import cc.emw.mobile.chat.view.MySeekBar;
import cc.emw.mobile.chat.view.RecyclerViewLinearLayoutManager;
import cc.emw.mobile.chat.view.ScrollToRecyclerView;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.entity.CallInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.form.FormDetailActivity;
import cc.emw.mobile.form.FormWebActivity;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import cc.emw.mobile.view.SectorProgressView;
import cc.emw.mobile.view.VideoPlayerView;


/**
 * 聊天列表适配器
 */
public class ChatMainAdapter extends RecyclerView.Adapter<ChatMainAdapter.MyHolder> {
    private final IChatMsgDBModel mChatMsgDao;
    private List<ChatMsgBean> mMessageList;//消息列表
    private Context mContext;//上下文

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format; //HH:mm
    private DisplayImageOptions optiones;//头像
    private StringBuffer sb;
    private Files files;
    private ListDialog mAddDialog;
    private String tempPath = "";
    private MediaPlayerManger mMediaPlayerMangers;
    private boolean isPlay = true;
    private ListDialog mAudioDeviceList;
    private AudioManager audioManager;
    private Map<Integer, Boolean> isVideoPlay;
    private Map<Integer, Boolean> isVideoLoading;
    private int imageLayoutWidth = -1;
    private LayoutInflater mLayoutInflater;
    private ListDialog textDialog;
    private ChatMsgBean mChatMsg;//当前条目的bean   用于在onCreateViewHolder()方法中new Holder传递bean做消息类型判断
    private Bundle savedInstanceState;
    private int UserOrGroupType;
    private int noteType;//动态消息type专用   邮件、任务、约会
    ////////////////////////////////////////代码修改///////////////////////////////////////////////////////
    /**
     * 高德地图
     */
    private AMap aMap;

    private ScrollToRecyclerView mRecycleView;

    private ActionSheet.ActionSheetListener listener;

    public void setListener(ActionSheet.ActionSheetListener listener) {
        this.listener = listener;
    }

    public void setRecycleView(ScrollToRecyclerView mRecycleView) {
        this.mRecycleView = mRecycleView;
    }

    public void setSavedInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
    }

    private int countDown = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    boolean isGroup = false;

    public ChatMainAdapter(Context context, int type, IChatMsgDBModel chatMsgDao) {
        this.mContext = context;
        this.UserOrGroupType = type;
        if (type == 2) {
            isGroup = true;
        }
        this.mChatMsgDao = chatMsgDao;
        mLayoutInflater = LayoutInflater.from(mContext);
        isVideoPlay = new HashMap<>();
        isVideoLoading = new HashMap<>();
        mImprotanceMsgList = new ArrayList<>();
        mMessageList = new LinkedList<>();
        mMediaPlayerMangers = MediaPlayerManger.getInstance();
        format = new SimpleDateFormat("HH:mm", Locale.getDefault());//yyyy-MM-dd
        optiones = ImageLoadFactory.getChatOptiones();//图片load
        sb = new StringBuffer();
        initListDialog(context);
        initSensor();
        PrefsUtil.setAudioDevice(AudioManager.MODE_NORMAL);
    }

    /**
     * 時間和連接符的判斷方法
     */
    public int postions;

    private void initVisibility(int postion, ChatMsgBean chatMsgBean, ChatMsgBean lastChatMsgBean, MyHolder holder) {
        postions = postion;
        int selectTimeFlag = -1;//-1表示不赋值   0表示赋值几月几日  1表示赋值时间
        /**********************************************
         *消息头部时间
         */
        if (holder.mTvMsgTime != null) {
            if (postion == 0 || !(DateUtil.isSameDate(DateUtil.getDate("yyyy-MM-dd", lastChatMsgBean.getCreateTime()), DateUtil.getDate("yyyy-MM-dd", chatMsgBean.getCreateTime())))) {//同一天
                holder.mTvMsgTime.setVisibility(View.VISIBLE);
                selectTimeFlag = 0;
            } else {
                if (postion != mMessageList.size() - 1) {
                    long chatMsgBeanTimeLongNext = mMessageList.get(postion + 1).getCreateTimeLong();//除了最后一条消息以外的下一条消息实体类
                    long chatMsgBeanTimeLongNow = chatMsgBean.getCreateTimeLong();
                    if (!(DateUtil.isSameMinutes(chatMsgBeanTimeLongNext, chatMsgBeanTimeLongNow, chatMsgBean.getCreateTime(), mMessageList.get(postion + 1).getCreateTime()))) {//非同三分钟
                        holder.mTvMsgTime.setVisibility(View.VISIBLE);
                        selectTimeFlag = 1;
                    } else {
                        //判断显示/隐藏
                        holder.mTvMsgTime.setVisibility(View.GONE);
                        holder.mTvMsgTime.setText("");
                        selectTimeFlag = -1;
                    }
                }
            }
            //若显示  则赋值
            if (holder.mTvMsgTime.getVisibility() == View.VISIBLE) {
                if (chatMsgBean.getCreateTime() != null) {
                    if (selectTimeFlag == 0 || selectTimeFlag == 1) {
                        holder.mTvMsgTime.setText(StringUtils.friendly_time3(chatMsgBean.getCreateTime()));
                        selectTimeFlag = -1;
                        /*holder.mTvMsgTime.setText(DateUtil.getStringTime("MM月dd日", DateUtil.getDate("yyyy-MM-dd HH:mm:ss", chatMsgBean.getCreateTime())));
                    } else if (selectTimeFlag == 1) {
                        holder.mTvMsgTime.setText(chatMsgBean.getCreateTime() == null ? format.format(new Date()) : format.format(StringUtils.toDate(chatMsgBean.getCreateTime())));
                        selectTimeFlag = -1;*/
                    } else {
                        holder.mTvMsgTime.setVisibility(View.GONE);
                        holder.mTvMsgTime.setText("");
                    }
                }
            }
        }
    }


    /**
     * 绑定视图
     */
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final ChatMsgBean mChatMSG = mMessageList.get(position);//当前条目的信息
        int type = getItemViewMsgType(position);//当前条目的接收发送状态：接受者/发送者
        /**********************************************
         *  增加机器人判断  非机器人消息才进入UI设置
         */
        ChatMsgBean lastMsg = position == 0 ? mChatMSG : mMessageList.get(position - 1);//上一条消息的bean
        initVisibility(position, mChatMSG, lastMsg, holder);

        if (mChatMSG.getContent() != null) {
            initReceUserHeadImageView(position, holder, type);
        }
        //点击重新发送
        final int positioned = position;
        if (holder.mItvChatMsgStateError != null && holder.mItvChatMsgStateError.getVisibility() == View.VISIBLE && mChatMSG.getIsSendFlag() != ChatContent.SEND_MSG_SUCCESS) {
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(null);
                    builder.setMessage("重新发送");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ChatContent.loopSendMessageBean);
                            intent.putExtra("msg_content", positioned);
                            mContext.sendBroadcast(intent);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            });
        }

        /**********************************************
         *绑定UI数据
         */
        if (mChatMSG.getContent() != null) {
            try {
                initItemViewByMsgType(position, holder, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (holder.mTvChatMsgContentRece != null) {
            copeText(holder.mTvChatMsgContentRece, mChatMSG, null, UserOrGroupType);
        }
        if (holder.mTvChatMsgContentSend != null) {
            copeText(holder.mTvChatMsgContentSend, mChatMSG, null, UserOrGroupType);
        }
    }


    private void copeText(final TextView textView, final ChatMsgBean msg, final String msgStr, final int userOrGroupType) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.copeText(textView, msg, msgStr, userOrGroupType);
                return true;
            }
        });
/*        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add("复制");
        popupMenuItemList.add("收藏");
        popupMenuItemList.add("标记重要消息");
        popupMenuItemList.add("添加日程");
        PopupList normalViewPopupList = new PopupList(mContext);
        normalViewPopupList.bind(textView, popupMenuItemList, new PopupList.PopupListListener() {
            @Override
            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {

                return msg.getType() != ApiEnum.MessageType.PhoneStateMsg;
            }

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                if (position == 0) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (TextUtils.isEmpty(msgStr)) {
                        cm.setText(msg.getContent().toString());
                    } else {
                        cm.setText(msgStr);
                    }
                    ToastUtil.showToast(mContext, "复制成功");
                } else if (position == 1) {
                    Intent intent = new Intent();
                    intent.setAction(ChatContent.REFRESH_CHAT_UPDATE_MSG);
                    intent.putExtra("msg_id", msg.getID() + "");
                    mContext.sendBroadcast(intent);
                } else if (position == 2) {
                    Intent intent = new Intent();
                    intent.setAction(ChatContent.REFRESH_CHAT_CHANGE_MSG);
                    intent.putExtra("msg_id", msg.getID() + "");
                    mContext.sendBroadcast(intent);
                } else if (position == 3) {
                    Intent scheduleIntent = new Intent(mContext, CalendarCreateActivitys.class);
                    scheduleIntent.putExtra("enter_flag", 1);
                    scheduleIntent.putExtra("chat_desc", textView.getText().toString());
                    scheduleIntent.putExtra("start_anim", false);
                    mContext.startActivity(scheduleIntent);
                }
            }
        });*/
    }

    private void initReceUserHeadImageView(int position, MyHolder holder, int type) {
        ChatMsgBean mChatMSG = mMessageList.get(position);
        if (holder.mCivChatMsgHead != null) {
            switch (type) {
                case ChatContent.TYPE_ITEM_RECE:
                    holder.mCivChatMsgHead.setVisibility(View.VISIBLE);
                    initUserHeadImage(mChatMSG, holder.mCivChatMsgHead);
                    break;
                case ChatContent.TYPE_ITEM_SEND:
                    holder.mCivChatMsgHead.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private boolean isNewReceMessage = false;//用于标识是否是一条新的消息，如果true则加载动画，否则不显示动画加载过程
    private boolean isNewMessage = false;//用于标识是否是一条新的消息，如果true则加载动画，否则不显示动画加载过程
    private boolean isStartAnim = false;
    private boolean isStartReceAnim = false;

    public void setIsNewMessage(boolean flag) {
        this.isNewMessage = flag;
    }

    public void setIsNewReceMessage(boolean flag) {
        this.isNewReceMessage = flag;
    }

    private void initItemViewByMsgType(int position, final MyHolder holder, final int type) {
        final ChatMsgBean mChatMSG = mMessageList.get(position);
        if (mChatMSG.getIsSendFlag() == ChatContent.SEND_MSG_ERROR) {
            holder.mItvChatMsgStateError.setVisibility(View.VISIBLE);
        } else {
            holder.mItvChatMsgStateError.setVisibility(View.GONE);
        }
        switch (mMessageList.get(position).getType()) {
            case ChatContent.DEFAULT_MSG://机器人普通消息
                bindAiMsg(holder, mChatMSG);
                break;
            case ChatContent.SCHEDULE_MSG://日程
                aiSchedule(mChatMSG, holder);
                break;
            /**
             * 设置图片消息类型
             */
            case ApiEnum.MessageType.Image://图片消息
                ImageMessage(holder, mChatMSG);
                break;
            /**
             * 设置音频消息类型
             */
            case ApiEnum.MessageType.Audio://音频消息
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                        holder.mRlMsgVoiceReceAll.setVisibility(View.VISIBLE);
                        holder.mRlMsgVoiceSendAll.setVisibility(View.GONE);
                        holder.mItvChatVoiceShowRece.setVisibility(View.GONE);
                        initVideo(holder);
                        /**
                         * 设置SeekBar进度滑块主题和进度值，如果在XML中直接设置会导致部分屏幕出现滑块和进度条错误的问题 sunnydu
                         */
                        holder.mSBChatVoiceRece.setThumb(mContext.getResources().getDrawable(R.drawable.chat_icon_seek_bar_sender));
                        holder.mSBChatVoiceRece.setProgress(0);
                        AudioMessage(mChatMSG, holder.mTvChatMsgVoiceRece, holder.mRlMsgVoiceRece, holder.mItvChatVoiceShowRece, holder.mItvChatVoiceOpenRece, holder.mSBChatVoiceRece);
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mRlMsgVoiceSendAll.setVisibility(View.VISIBLE);
                        holder.mRlMsgVoiceReceAll.setVisibility(View.GONE);
                        holder.mItvChatVoiceShowSend.setVisibility(View.GONE);
                        holder.mSBChatVoiceSend.setThumb(mContext.getResources().getDrawable(R.drawable.chat_seekbar_oval));
                        holder.mSBChatVoiceSend.setProgress(0);
                        AudioMessage(mChatMSG, holder.mTvChatMsgVoiceSend, holder.mRlMsgVoiceSend, holder.mItvChatVoiceShowSend, holder.mItvChatVoiceOpenSend, holder.mSBChatVoiceSend);
                        break;
                }
                break;
            /**
             * 视频消息
             */
            case ApiEnum.MessageType.Video://视频消息
                VideoPlayer player = new VideoPlayer(mContext);
                videoMessage(mChatMSG, holder, position, player);
                break;
            case ApiEnum.MessageType.Attach://附件消息
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                        holder.mRlChatMsgShareReceAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareSendAll.setVisibility(View.GONE);
                        AttachMessage(mChatMSG, holder.mIvChatShareImgRece, holder.mTvChatShareNameRece, holder.mTvChatShareFileSizeRece, holder.mRlMsgTextShowRece);
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mRlChatMsgShareSendAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareReceAll.setVisibility(View.GONE);
                        AttachMessage(mChatMSG, holder.mIvChatShareImgSend, holder.mTvChatShareNameSend, holder.mTvChatShareFileSizeSend, holder.mRlMsgTextShowSend);
                        break;
                }
                break;
            case ApiEnum.MessageType.Flow://流程消息
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                        holder.mRlChatMsgShareReceAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareSendAll.setVisibility(View.GONE);
                        flowMessage(mChatMSG, holder.mIvChatShareImgRece, holder.mTvChatShareNameRece, holder.mTvChatShareFileSizeRece, holder.mRlMsgTextShowRece);
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mRlChatMsgShareSendAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareReceAll.setVisibility(View.GONE);
                        flowMessage(mChatMSG, holder.mIvChatShareImgSend, holder.mTvChatShareNameSend, holder.mTvChatShareFileSizeSend, holder.mRlMsgTextShowSend);
                        break;
                }
                break;
            case ApiEnum.MessageType.Task://任务消息
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                        holder.mRlChatMsgShareReceAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareSendAll.setVisibility(View.GONE);
                        taskMessage(mChatMSG, holder.mIvChatShareImgRece, holder.mTvChatShareNameRece, holder.mTvChatShareFileSizeRece, holder.mRlMsgTextShowRece);
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mRlChatMsgShareSendAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareReceAll.setVisibility(View.GONE);
                        taskMessage(mChatMSG, holder.mIvChatShareImgSend, holder.mTvChatShareNameSend, holder.mTvChatShareFileSizeSend, holder.mRlMsgTextShowSend);
                        break;
                }
                break;
            case ApiEnum.MessageType.Share://分享消息
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                        holder.mRlChatMsgShareReceAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareSendAll.setVisibility(View.GONE);
                        sharMessage(mChatMSG, holder.mIvChatShareImgRece, holder.mTvChatShareNameRece, holder.mTvChatShareFileSizeRece, holder.mRlMsgTextShowRece);
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mRlChatMsgShareSendAll.setVisibility(View.VISIBLE);
                        holder.mRlChatMsgShareReceAll.setVisibility(View.GONE);
                        sharMessage(mChatMSG, holder.mIvChatShareImgSend, holder.mTvChatShareNameSend, holder.mTvChatShareFileSizeSend, holder.mRlMsgTextShowSend);
                        break;
                }
                break;
            case ChatContent.CHAT_LOCATION:
                try {
                    final LocationBean locationBean = new Gson().fromJson(mChatMSG.getContent(), LocationBean.class);
                    holder.mTvLocationInfo.setText(locationBean.addressName);
                    DisplayImageOptions optionesMsgImage = ImageLoadFactory.getChatApdaterImage();
                    if (mChatMSG.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
                        final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, locationBean.url);
                        ImageLoader.getInstance().displayImage(uri, holder.mIvMapImage, optionesMsgImage);
                        holder.mIvMapImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent posIntent = new Intent(mContext, PosInfoActivity.class);
                                posIntent.putExtra("start_anim", false);
                                posIntent.putExtra("lb", locationBean);
                                mContext.startActivity(posIntent);
                            }
                        });
                    } else {
                        ImageLoader.getInstance().displayImage("file://" + locationBean.url, holder.mIvMapImage, optionesMsgImage);
                    }
                } catch (Exception x) {
                    Log.d("sunny----->", "错误");
                }
                break;
            case ChatContent.CHAT_SHARE_LOCATION:
                try {
                    final LocationBean locationBean = new Gson().fromJson(mChatMSG.getContent(), LocationBean.class);
                    DisplayImageOptions optionsMsgImage = ImageLoadFactory.getChatApdaterImage();
                    switch (type) {
                        case ChatContent.TYPE_ITEM_RECE:
                            holder.mRlLocationReceive.setVisibility(View.VISIBLE);
                            holder.mRlLocationSend.setVisibility(View.GONE);
                            //                    holder.mLlSharePosTiming.setVisibility(locationBean.isShareLocation ? View.VISIBLE : View.GONE);
                            holder.mTvLocationInfo.setText(locationBean.addressName);
                            //                            final Timer timer = new Timer();
                            //                            holder.mSpv.setTag(mChatMSG.getID());
                            //                            final Handler mHandler = new Handler() {  //实时共享位置
                            //                                @Override
                            //                                public void handleMessage(android.os.Message msg) {
                            //                                    switch (msg.what) {
                            //                                        case 555:
                            //                                            if (holder.mSpv.getTag().equals(mChatMSG.getID())) {
                            //                                                countDown++;
                            //                                                if (countDown == 3600) {
                            //                                                    countDown = 0;
                            //                                                    //倒计时
                            //                                                    holder.mSpv.setVisibility(View.GONE);
                            //                                                    if (timer != null) {
                            //                                                        timer.cancel();
                            //                                                        timer.purge();
                            //                                                    }
                            //                                                    holder.mSpv.setPercent(0);
                            //                                                }
                            //                                                holder.mSpv.setPercent(countDown / 36);
                            //                                            }
                            //                                            break;
                            //                                    }
                            //                                }
                            //                            };
                            //
                            //                            TimerTask task = new TimerTask() {
                            //                                public void run() {
                            //                                    mHandler.sendEmptyMessage(555);
                            //                                }
                            //                            };
                            //                            timer.schedule(task, 0, 1000);

                            if (mChatMSG.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
                                final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, locationBean.url);
                                ImageLoader.getInstance().displayImage(uri, holder.mIvMapImage, optionsMsgImage);
                                //                        if (mMessageList.get(position).getType() == ChatContent.CHAT_LOCATION) {
                                if (locationBean.isShareLocation) {
                                    holder.mIvMapImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent sharePosIntent = new Intent(mContext, SharePosActivity.class);
                                            sharePosIntent.putExtra("start_anim", false);
                                            sharePosIntent.putExtra("type", type);
                                            sharePosIntent.putExtra("SenderId", mChatMSG.getSenderID());
                                            mContext.startActivity(sharePosIntent);
                                        }
                                    });
                                }
                            } else {
                                ImageLoader.getInstance().displayImage("file://" + locationBean.url, holder.mIvMapImage, optionsMsgImage);
                            }
                            break;
                        case ChatContent.TYPE_ITEM_SEND:
                            holder.mRlLocationReceive.setVisibility(View.GONE);
                            holder.mRlLocationSend.setVisibility(View.VISIBLE);
                            //                    holder.mLlSharePosTiming.setVisibility(locationBean.isShareLocation ? View.VISIBLE : View.GONE);
                            holder.mTvLocationInfo2.setText(locationBean.addressName);

                            //                            final Timer timer = new Timer();
                            //                            holder.mSpv.setTag(mChatMSG.getID());
                            //                            final Handler mHandler = new Handler() {  //实时共享位置
                            //                                @Override
                            //                                public void handleMessage(android.os.Message msg) {
                            //                                    switch (msg.what) {
                            //                                        case 555:
                            //                                            if (holder.mSpv.getTag().equals(mChatMSG.getID())) {
                            //                                                countDown++;
                            //                                                if (countDown == 3600) {
                            //                                                    countDown = 0;
                            //                                                    //倒计时
                            //                                                    holder.mSpv.setVisibility(View.GONE);
                            //                                                    if (timer != null) {
                            //                                                        timer.cancel();
                            //                                                        timer.purge();
                            //                                                    }
                            //                                                    holder.mSpv.setPercent(0);
                            //                                                }
                            //                                                holder.mSpv.setPercent(countDown / 36);
                            //                                            }
                            //                                            break;
                            //                                    }
                            //                                }
                            //                            };
                            //
                            //                            TimerTask task = new TimerTask() {
                            //                                public void run() {
                            //                                    mHandler.sendEmptyMessage(555);
                            //                                }
                            //                            };
                            //                            timer.schedule(task, 0, 1000);

                            if (mChatMSG.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
                                final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, locationBean.url);
                                ImageLoader.getInstance().displayImage(uri, holder.mIvMapImage2, optionsMsgImage);
                                //                        if (mMessageList.get(position).getType() == ChatContent.CHAT_LOCATION) {
                                //                                if (locationBean.isShareLocation) {
                                //                                    holder.mIvMapImage2.setOnClickListener(new View.OnClickListener() {
                                //                                        @Override
                                //                                        public void onClick(View v) {
                                //                                            Intent sharePosIntent = new Intent(mContext, SharePosActivity.class);
                                //                                            sharePosIntent.putExtra("start_anim", false);
                                //                                            sharePosIntent.putExtra("type", type);
                                //                                            sharePosIntent.putExtra("SenderId", mChatMSG.getSenderID());
                                //                                            mContext.startActivity(sharePosIntent);
                                //                                        }
                                //                                    });
                                //                                }
                            } else {
                                ImageLoader.getInstance().displayImage("file://" + locationBean.url, holder.mIvMapImage2, optionsMsgImage);
                            }
                            break;
                    }
                } catch (Exception x) {
                    Log.d("sunny----->", "错误");
                }
                break;
            case ChatContent.IMPROTANCE_MSG:
                LinearLayoutManager mLayoutManager4 = new LinearLayoutManager(mContext);
                mLayoutManager4.setOrientation(LinearLayoutManager.VERTICAL);
                holder.mRvChatImportanceMessage.setItemAnimator(new DefaultItemAnimator());
                holder.mRvChatImportanceMessage.setLayoutManager(mLayoutManager4);
                final ChatImportanceMessageAdapter importanceMsgAdapter = new ChatImportanceMessageAdapter(mContext);
                importanceMsgAdapter.setData(mImprotanceMsgList);
                holder.mRvChatImportanceMessage.setAdapter(importanceMsgAdapter);
                holder.mIvDeleteImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(ChatContent.REFRESH_CHAT_COLSE_IMPROTANCE_MSG);
                        mContext.sendBroadcast(intent);
                    }
                });
                holder.mIvEditImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mLlImprotanceMsgChangeRoot.setVisibility(View.VISIBLE);
                        holder.mIvEditImprotanceMsgList.setVisibility(View.GONE);
                        holder.mTvRemoveEditImprotanceMsgList.setVisibility(View.VISIBLE);
                    }
                });
                holder.mTvRemoveEditImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mLlImprotanceMsgChangeRoot.setVisibility(View.GONE);
                        holder.mIvEditImprotanceMsgList.setVisibility(View.VISIBLE);
                        holder.mTvRemoveEditImprotanceMsgList.setVisibility(View.GONE);
                    }
                });
                holder.mTvImprotanceDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Set<Integer> selectorMessageList = importanceMsgAdapter.getSelectorMessageList();
                        StringBuilder sb = new StringBuilder();
                        int i = 0;
                        for (Integer id : selectorMessageList) {
                            if (i++ == 0) {
                                sb.append(id);
                            } else {
                                sb.append("," + id);
                            }
                        }
                        i = 0;
                        Intent intent = new Intent();
                        intent.setAction(ChatContent.REFRESH_CHAT_CHANGE_MSG);
                        intent.putExtra("msg_id", (Serializable) sb);
                        intent.putExtra("state", 0);
                        mContext.sendBroadcast(intent);
                    }
                });
                holder.mTvImprotanceAllIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        importanceMsgAdapter.getAllSelector();
                    }
                });
                break;
            case ChatContent.DYNAMIC://动态消息绑定
                try {
                    UserNote userNote = new Gson().fromJson(mChatMSG.getContent(), UserNote.class);
                    RequestCallback.convertUserNote(userNote);
                    //                String userName = "";
                    String imagePath = "";
                    String dynamicTitleStr = null;
                    String dynamicTime = null;
                    List<cc.emw.mobile.net.ApiEntity.UserInfo> userNum = new ArrayList<>();
                    ApiEntity.UserSchedule scheduleBean = null;
                    ApiEntity.UserFenPai fenpaiBean = null;
                    switch (userNote.Type) {
                        case ApiEnum.UserNoteAddTypes.Appoint:
                        case ApiEnum.UserNoteAddTypes.Email:
                            if (userNote.info.schedule != null && userNote.info.schedule.size() != 0) {
                                scheduleBean = userNote.info.schedule.get(0);
                            }
                            break;
                        case ApiEnum.UserNoteAddTypes.Task:
                            if (userNote.info.task != null && userNote.info.task.size() != 0) {
                                fenpaiBean = userNote.info.task.get(0);
                            }
                            break;
                    }
                    if (scheduleBean != null) {
                        if (scheduleBean.ImagePath != null) {
                            imagePath = scheduleBean.ImagePath;
                        }
                        dynamicTitleStr = scheduleBean.Title;
                        dynamicTime = scheduleBean.StartTime + "/" + scheduleBean.OverTime;
                        if (scheduleBean.MustActor != null && !("(null)".equals(scheduleBean.MustActor)) && (!"".equals(scheduleBean.MustActor))) {
                            userNum = TaskUtils.getUsers(scheduleBean.MustActor);
                        }
                    } else if (fenpaiBean != null) {
                        dynamicTitleStr = fenpaiBean.Title;
                        dynamicTime = fenpaiBean.StartTime + "/" + fenpaiBean.FinishTime;
                        if (fenpaiBean.MoreUser != null && !("(null)".equals(fenpaiBean.MoreUser)) && (!"".equals(fenpaiBean.MoreUser))) {
                            userNum = TaskUtils.getUsers(fenpaiBean.MoreUser);
                        }
                    }
                    this.showInformmationMsg(holder, imagePath, userNote, dynamicTitleStr, dynamicTime, userNum, mChatMSG.getID(), scheduleBean);
                } catch (Exception x) {
                    Log.d("sunny----->", "x=" + x.toString());
                }
                break;
            default://普通消息和通知类消息通道
                switch (type) {
                    case ChatContent.TYPE_ITEM_RECE:
                    case ChatContent.CHAT_UNSHARE_LOCATION:
                        holder.mRlMsgTextReceAll.setVisibility(View.VISIBLE);
                        holder.mRlMsgTextSendAll.setVisibility(View.GONE);
                        if (mChatMSG.getOptions() == 0) {
                            holder.iv_rece_sc.setVisibility(View.GONE);
                            holder.iv_rece_bj.setVisibility(View.GONE);
                        } else if (mChatMSG.getOptions() == 1) {
                            holder.iv_rece_sc.setVisibility(View.GONE);
                            holder.iv_rece_bj.setVisibility(View.VISIBLE);
                        } else if (mChatMSG.getOptions() == 2) {
                            holder.iv_rece_sc.setVisibility(View.VISIBLE);
                            holder.iv_rece_bj.setVisibility(View.GONE);
                        }
                        if (isNewReceMessage) {
                            isNewReceMessage = false;
                            holder.mTvChatMsgContentRece.setVisibility(View.GONE);
                            holder.mrlReceTextMsg.setVisibility(View.VISIBLE);
                            holder.mrlReceTextMsg.setOnAnimEndListener(new MKLoader.OnAnimEndListener() {
                                @Override
                                public void doEnd() {
                                    holder.mTvChatMsgContentRece.setVisibility(View.VISIBLE);
                                    holder.mrlReceTextMsg.setVisibility(View.GONE);
                                    baceMessage(mChatMSG, holder.mTvChatMsgContentRece);
                                    chatItemAnim(holder.mTvChatMsgContentRece);
                                }
                            });
                        } else {
                            baceMessage(mChatMSG, holder.mTvChatMsgContentRece);
                        }
                        break;
                    case ChatContent.TYPE_ITEM_SEND:
                        holder.mTvChatAiMsgContentSendHeader.setVisibility(View.GONE);
                        holder.mRlMsgTextSendAll.setVisibility(View.VISIBLE);
                        holder.mRlMsgTextReceAll.setVisibility(View.GONE);
                        if (mChatMSG.getOptions() == 0) {
                            holder.iv_send_sc.setVisibility(View.GONE);
                            holder.iv_send_sc.setVisibility(View.GONE);
                        } else if (mChatMSG.getOptions() == 1) {
                            holder.iv_send_sc.setVisibility(View.GONE);
                            holder.iv_send_bj.setVisibility(View.VISIBLE);
                        } else if (mChatMSG.getOptions() == 2) {
                            holder.iv_send_sc.setVisibility(View.VISIBLE);
                            holder.iv_send_bj.setVisibility(View.GONE);
                        }
                        //TODO   发送人的消息不走动画流程，解决消息状态无法及时转变
                        //                        if (isNewMessage) {
                        //                            isNewMessage = false;
                        //                            holder.loaderSend.setVisibility(View.VISIBLE);
                        //                            holder.mrlSendTextMSG.setVisibility(View.GONE);
                        //                            holder.loaderSend.setOnAnimEndListener(new MKLoader.OnAnimEndListener() {
                        //                                @Override
                        //                                public void doEnd() {
                        //                                    holder.loaderSend.setVisibility(View.GONE);
                        //                                    holder.mrlSendTextMSG.setVisibility(View.VISIBLE);
                        //                                    if (mChatMSG.getContent().contains("@EMW")) {
                        //                                        String msg = mChatMSG.getContent().substring(4, mChatMSG.getContent().length());
                        //                                        holder.mTvChatAiMsgContentSendHeader.setVisibility(View.VISIBLE);
                        //                                        ChatUtils.spannableEmoticonFilter(holder.mTvChatMsgContentSend, msg);
                        //                                    } else {
                        //                                        holder.mTvChatAiMsgContentSendHeader.setVisibility(View.GONE);
                        //                                        baceMessage(mChatMSG, holder.mTvChatMsgContentSend);
                        //                                        chatItemAnim(holder.mRlChatItemSendRoot);
                        //                                    }
                        //                                }
                        //                            });
                        //                        } else {
                        if (mChatMSG.getContent().contains("@EMW")) {
                            String msg = mChatMSG.getContent().substring(4, mChatMSG.getContent().length());
                            holder.mTvChatAiMsgContentSendHeader.setVisibility(View.VISIBLE);
                            ChatUtils.spannableEmoticonFilter(holder.mTvChatMsgContentSend, msg);
                        } else {
                            holder.mTvChatAiMsgContentSendHeader.setVisibility(View.GONE);
                            baceMessage(mChatMSG, holder.mTvChatMsgContentSend);
                        }
                        //                        }
                        break;
                }
                break;
        }
    }

    private void showInformmationMsg(final MyHolder holder, String dynamicIconPath, final UserNote userNote, String dynamicTitle,
                                     String dynamicTime, List<cc.emw.mobile.net.ApiEntity.UserInfo> userHeadIcons, final int mesageId, final ApiEntity.UserSchedule scheduleBean) {
        //        holder.mTvChatItemMsgDynamicUserName.setText(userName + "发布了一个动态");

        if (dynamicIconPath != null && !("".equals(dynamicIconPath))) {
            holder.mIvChatItemMsgDynamicIcon.setVisibility(View.VISIBLE);
            //TODO 网络请求图片
        }
        switch (userNote.Type) {
            case ApiEnum.UserNoteAddTypes.Appoint:
                holder.mIvChatItemMsgDynamicIcon.setImageResource(R.drawable.icon_chat_date_title);
                holder.mIvChatItemMsgDynamicTitleIcon.setImageResource(R.drawable.icon_chat_appointment);
                break;
            case ApiEnum.UserNoteAddTypes.Email:
                holder.mIvChatItemMsgDynamicIcon.setImageResource(R.drawable.icon_chat_mail_title);
                holder.mIvChatItemMsgDynamicTitleIcon.setImageResource(R.drawable.icon_chat_schedule);
                break;
            default:
                holder.mIvChatItemMsgDynamicIcon.setImageResource(R.drawable.icon_chat_task_title);
                holder.mIvChatItemMsgDynamicTitleIcon.setImageResource(R.drawable.icon_chat_project);
                break;
        }
        holder.mTvChatItemMsgDynamicTitle.setText("主题：" + dynamicTitle);
        holder.mTvChatItemMsgDynamicTime.setText(dynamicTime);
        //        holder.mLlChatItemMsgDynamicHeadIcon.removeAllViews(); TODO  sunnydu 2017年7月13日17:38:01
        //        if (userHeadIcons != null && userHeadIcons.size() != 0) {TODO  sunnydu 2017年7月13日17:38:01
        //            holder.mLlChatItemMsgDynamicHeadIcon.setVisibility(View.VISIBLE);TODO  sunnydu 2017年7月13日17:38:01
        //            holder.mTvChatItemMsgDynamicUserNum.setText("(" + userHeadIcons.size() + "人）");TODO  sunnydu 2017年7月13日17:38:01
        //            for (int i = 0; i < userHeadIcons.size(); i++) {TODO  sunnydu 2017年7月13日17:38:01
        //                ApiEntity.UserInfo userInfo = userHeadIcons.get(i);TODO  sunnydu 2017年7月13日17:38:01
        //                CircleImageView circleImageView = new CircleImageView(mContext);TODO  sunnydu 2017年7月13日17:38:01
        //                circleImageView.setBorderWidth(DisplayUtil.dip2px(mContext, 1));TODO  sunnydu 2017年7月13日17:38:01
        //                circleImageView.setBorderColorResource(R.color.cm_headimg_border);TODO  sunnydu 2017年7月13日17:38:01
        //                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 24), DisplayUtil.dip2px(mContext, 24));TODO  sunnydu 2017年7月13日17:38:01
        //                if (holder.mLlChatItemMsgDynamicHeadIcon.getChildCount() != 0) {TODO  sunnydu 2017年7月13日17:38:01
        //                    params.leftMargin = -DisplayUtil.dip2px(mContext, 4);TODO  sunnydu 2017年7月13日17:38:01
        //                }TODO  sunnydu 2017年7月13日17:38:01
        //                if (userInfo != null && userInfo.Image != null) {TODO  sunnydu 2017年7月13日17:38:01
        //                    ImageLoader.getInstance().displayImage(getHeadImageUri(userInfo.Image),TODO  sunnydu 2017年7月13日17:38:01
        //                            circleImageView, options);TODO  sunnydu 2017年7月13日17:38:01
        //                    holder.mLlChatItemMsgDynamicHeadIcon.addView(circleImageView, params);TODO  sunnydu 2017年7月13日17:38:01
        //                } else {TODO  sunnydu 2017年7月13日17:38:01
        //                    circleImageView.setImageResource(R.drawable.cm_img_head);TODO  sunnydu 2017年7月13日17:38:01
        //                    holder.mLlChatItemMsgDynamicHeadIcon.addView(circleImageView, params);TODO  sunnydu 2017年7月13日17:38:01
        //                }TODO  sunnydu 2017年7月13日17:38:01
        //                if (i == 5) {TODO  sunnydu 2017年7月13日17:38:01
        //                    break;TODO  sunnydu 2017年7月13日17:38:01
        //                }TODO  sunnydu 2017年7月13日17:38:01
        //            }TODO  sunnydu 2017年7月13日17:38:01
        //        } else {TODO  sunnydu 2017年7月13日17:38:01
        //            //展示没有执行人 TODO  sunnydu 2017年7月13日17:38:01
        //            holder.mTvChatItemMsgDynamicUserNum.setText("暂无");TODO  sunnydu 2017年7月13日17:38:01
        //            holder.mLlChatItemMsgDynamicHeadIcon.setVisibility(View.GONE);TODO  sunnydu 2017年7月13日17:38:01
        //        }TODO  sunnydu 2017年7月13日17:38:01
        if (scheduleBean.AppointPayType == 0) {
            holder.mTvChatItemPayType.setText("请客");
            holder.mRlChatItemPayMoney.setVisibility(View.GONE);
        } else if (scheduleBean.AppointPayType == 1) {
            holder.mTvChatItemPayType.setText("AA制");
            holder.mRlChatItemPayMoney.setVisibility(View.VISIBLE);
            holder.mTvChatItemPayMoney.setText(scheduleBean.AppointPayVal);
        }
        if (userHeadIcons != null && userHeadIcons.size() != 0) {
            for (cc.emw.mobile.net.ApiEntity.UserInfo user : userHeadIcons) {
                if (user.ID == PrefsUtil.readUserInfo().ID) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            if (flag) {
                holder.mButChatItemMsgDynamicAttend.setBackground(null);
                holder.mButChatItemMsgDynamicAttend.setTextColor(Color.parseColor("#999999"));
            } else {
                holder.mButChatItemMsgDynamicAttend.setBackground(null);
                holder.mButChatItemMsgDynamicAttend.setTextColor(Color.parseColor("#EE3102"));
                holder.mButChatItemMsgDynamicAttend.setOnClickListener(new View.OnClickListener() {//我要参加按钮事件监听
                    @Override
                    public void onClick(View v) {
                        if (!flag) {
                            switch (userNote.Type) {
                                case ApiEnum.UserNoteAddTypes.Appoint://约会
                                    //                                    Intent intent = new Intent(mContext, DoPayActivity.class);
                                    //                                    intent.putExtra("user_note_id", userNote.ID);
                                    //                                    intent.putExtra("mesage_id", mesageId);
                                    //                                    intent.putExtra("is_group", isGroup);
                                    //                                    intent.putExtra("schedule_bean", scheduleBean);
                                    //                                    mContext.startActivity(intent);
                                    //                                    ChatMainAdapter.this.holder = holder;TODO   暂时关闭支付接口
                                    addUserToMessageInOf(holder, userNote.ID, mesageId, isGroup);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Email:
                                    addUserToMessageInOf(holder, userNote.ID, mesageId, isGroup);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Task:
                                    addUserToMessageInOf(holder, userNote.TypeId, mesageId, isGroup);
                                    break;
                            }
                        }
                    }
                });
            }
            flag = false;
        }
        holder.mLlDynamiInfoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (userNote.Type) {
                    case ApiEnum.UserNoteAddTypes.Appoint:
                        if (userNote.info.schedule.get(0) != null) {
                            Intent intentNote = new Intent(mContext, ChatDynamicDetailActivity.class);
                            userNote.info.schedule.get(0).ID = userNote.ID;
                            intentNote.putExtra("userNote", userNote.info.schedule.get(0));
                            intentNote.putExtra("userNoteID", userNote.TypeId);
                            mContext.startActivity(intentNote);
                        } else {
                            ToastUtil.showToast(mContext, "网络异常，请稍后尝试");
                        }
                        break;
                    case ApiEnum.UserNoteAddTypes.Task:
                        if (userNote.info.task.get(0) != null) {
                            Intent intentTask = new Intent(mContext, TaskDetailActivity.class);
                            intentTask.putExtra(TaskDetailActivity.TASK_ID, userNote.info.task.get(0).ID);
                            mContext.startActivity(intentTask);
                        } else {
                            ToastUtil.showToast(mContext, "网络异常，请稍后尝试");
                        }
                        break;
                }
            }
        });

    }

    private MyHolder holder;//addUserToMessageInOf专用。其他类型的条目不建议使用

    public MyHolder getHolder() {
        return holder;
    }

    private boolean flag = false;

    public void addUserToMessageInOf(final MyHolder holder, int noteid, int mesageId, boolean isGroup) {
        API.TalkerAPI.AddUserToMessageByNoteID(noteid, mesageId, isGroup, new RequestCallback<ApiEntity.Message>(ApiEntity.Message.class) {
            @Override
            public void onParseSuccess(List<ApiEntity.Message> respInfo) {
                holder.mButChatItemMsgDynamicAttend.setBackground(null);
                holder.mButChatItemMsgDynamicAttend.setTextColor(Color.parseColor("#999999"));
                ToastUtil.showToast(mContext, "参与成功");
                if (respInfo != null && respInfo.size() != 0) {
                    Intent intent = new Intent();
                    intent.setAction(ChatContent.REFRESH_CHAT_RECEIVED_MSG);
                    intent.putExtra("msg_json", new Gson().toJson((respInfo.get(0))));
                    mContext.sendBroadcast(intent);
                } else {
                    ToastUtil.showToast(mContext, "添加失败,请检查网络稍后尝试");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(mContext, "添加失败,请检查网络稍后尝试");
            }
        });
    }


    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.ALPHA_8)
            .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
            .build(); // 创建配置过得DisplayImageOption对象

    private void aiSchedule(ChatMsgBean message, MyHolder holder) {
        if ("找不到相关数据".equals(message.getContent().trim())) {
            staticBindText(holder.mTvAIChatMsgContentRece, message.getContent(), null);
            holder.mCvMsg.setVisibility(View.GONE);
        } else {
            holder.mCvMsg.setVisibility(View.VISIBLE);
            staticBindText(holder.mTvAIChatMsgContentRece, "以下是查询到的结果", null);
            List<ApiEntity.UserSchedule> scheduleList = new Gson().fromJson(message.getContent(), new TypeToken<List<ApiEntity.UserSchedule>>() {
            }.getType());
            switchListMsg(message, holder, scheduleList, message.getType(), null);//进入子链表方法
        }
    }

    /**
     * 绑定文本消息的公共方法
     */
    private void staticBindText(TextView contentView, String msg, String url) {
        contentView.setText(msg);
        if (url != null && (!"".equals(url.trim()))) {
            String webLink = "<a href=\'" + url + "\'>点击查看</a>";
            contentView.append(Html.fromHtml(webLink));
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * @des 初始化子列表信息  绑定数据
     * @cal
     */
    private void switchListMsg(ChatMsgBean message, MyHolder holder, List<ApiEntity.UserSchedule> userScheduleList, int msgSunType, MessageChatAITest msg) {
        switch (msgSunType) {
            case ChatContent.SCHEDULE_MSG://日程展示列表
                holder.mChatRvMessage.setItemAnimator(new DefaultItemAnimator());
                holder.mChatRvMessage.setAdapter(new ChatAIListScheduleAdapter(mContext, userScheduleList));
                holder.mChatRvMessage.setLayoutManager(new RecyclerViewLinearLayoutManager(mContext));
                //                holder.aiTime.setText(message.getCreateTime() == null ? format.format(new Date()) : format.format(StringUtils.toDate(message.getCreateTime())));
                break;
            default:
                break;
        }
    }


    private void bindAiMsg(MyHolder holder, ChatMsgBean mChatMSG) {
        AiMsgBean msg = new Gson().fromJson(mChatMSG.getContent(), AiMsgBean.class);
        if (msg.getType() != null) {
            switch (Integer.valueOf(msg.getType())) {
                case ChatContent.ITEM_MSG://机器人普通消息
                    holder.mRlImageListRoot.setVisibility(View.GONE);
                    holder.mChatAiLvMessage.setVisibility(View.GONE);
                    holder.aiMsg.setText(msg.getText());
                    if (holder.aiMsg != null) {
                        copeText(holder.aiMsg, mChatMSG, msg.getText(), UserOrGroupType);
                    }
                    break;
                case ChatContent.ITEM_IMAGE_MSG://图片列表
                    holder.mRlImageListRoot.setVisibility(View.VISIBLE);
                    holder.mChatAiLvMessage.setVisibility(View.VISIBLE);
                    holder.aiMsg.setText("以下是查询到的结果");
                    holder.mChatAiLvMessage.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.mChatAiLvMessage.setItemAnimator(new DefaultItemAnimator());
                    holder.mChatAiLvMessage.setAdapter(new ChatAIListImageAdapter(mContext, msg));
                    holder.mChatAiLvMessage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                    holder.mChatAiLvMessage.setItemAnimator(new DefaultItemAnimator());
                    break;
                case ChatContent.MAP_MSG://地图指引
                    holder.mRlImageListRoot.setVisibility(View.VISIBLE);
                    holder.mChatAiLvMessage.setVisibility(View.VISIBLE);
                    holder.aiMsg.setText("以下是查询到的结果");
                    holder.mChatAiLvMessage.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.mChatAiLvMessage.setItemAnimator(new DefaultItemAnimator());
                    holder.mChatAiLvMessage.setAdapter(new ChatAIListMapFoodsAdapter(mContext, msg));
                    holder.mChatAiLvMessage.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                    holder.mChatAiLvMessage.setItemAnimator(new DefaultItemAnimator());
                    break;
            }
        } else {
            holder.mRlImageListRoot.setVisibility(View.GONE);
            holder.mChatAiLvMessage.setVisibility(View.GONE);
            holder.aiMsg.setText(mChatMSG.getContent());
            if (holder.aiMsg != null) {
                copeText(holder.aiMsg, mChatMSG, "", UserOrGroupType);
            }
        }
    }


    /**
     * 获取消息列表
     * 第一次加载或者消息发生变化的时候
     */
    public void setData(List<ChatMsgBean> dataList) {
        this.mMessageList = dataList;
    }

    private List<ImprotanceMessage> mImprotanceMsgList;

    public List<ChatMsgBean> getData() {
        return this.mMessageList;
    }

    /**
     * 重置语音多媒体功能
     */
    private void initVideo(MyHolder holder) {
        if (holder.player != null) {
            holder.player.stopVideo();
        }
    }

    /**
     * 初始化视图
     */
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ApiEnum.MessageType.Image://图片消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_image, parent, false), this, mChatMsg);
            case ApiEnum.MessageType.Audio://音频消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_voice, parent, false), this, mChatMsg);
            case ApiEnum.MessageType.Video://视频消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_video, parent, false), this, mChatMsg);
            case ChatContent.DEFAULT_MSG://机器人--普通消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_ai_default_item, parent, false), this, mChatMsg);
            case ChatContent.SCHEDULE_MSG://机器人--日程消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_ai_schedule_list_item, parent, false), this, mChatMsg);
            case ChatContent.DYNAMIC://邮件、任务、约会
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_dynamic, parent, false), this, mChatMsg);
            case ApiEnum.MessageType.Attach://附件消息
            case ApiEnum.MessageType.Flow://流程消息
            case ApiEnum.MessageType.Task://任务消息
            case ApiEnum.MessageType.Share://分享消息
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_share_info, parent, false), this, mChatMsg);
            case ChatContent.CHAT_LOCATION:
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_map, parent, false), this, mChatMsg);
            case ChatContent.CHAT_SHARE_LOCATION:
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_map_ing, parent, false), this, mChatMsg);
            case ChatContent.IMPROTANCE_MSG:
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_importance_message_list_view_layout, parent, false), this, mChatMsg);
            default://普通消息和通知类消息通道
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_msg_text, parent, false), this, mChatMsg);
        }
    }

    public void chatItemAnim(View view) {
        isStartAnim = false;
        AnimationSet set = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(0, 1);//设置透明度；设置动画终止的透明度
        ScaleAnimation sa = new ScaleAnimation(0, 1, 1, 1);
        set.addAnimation(aa);
        set.addAnimation(sa);
        set.setDuration(200);
        set.setRepeatCount(1);
        view.startAnimation(set);
    }

    /**
     * 普通消息
     *
     * @param msg              当前条目实体类
     * @param tvChatMsgContent 文本显示控件
     */
    private void baceMessage(ChatMsgBean msg, TextView tvChatMsgContent) {
        if (msg.getType() == ApiEnum.MessageType.PhoneStateMsg) {
            try {
                CallInfo callInfo = new Gson().fromJson(msg.getContent(), CallInfo.class);
                Drawable left = mContext.getResources().getDrawable(callInfo.type == 1 ? R.drawable.chat_ic_audio : R.drawable.chat_ic_video);
                left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());// 必须设置图片大小，否则不显示
                tvChatMsgContent.setCompoundDrawables(left, null, null, null);
                tvChatMsgContent.setCompoundDrawablePadding(DisplayUtil.dip2px(mContext, 5));
                tvChatMsgContent.setText(callInfo.content);
            } catch (Exception e) {

            }
        } else if ("[语音通话]".equals(msg.getContent()) || "[视频通话]".equals(msg.getContent())) {
            tvChatMsgContent.setText(msg.getContent());
        } else {
            ChatUtils.spannableEmoticonFilter(tvChatMsgContent, msg.getContent());
        }
    }

    /**
     * 分享消息
     *
     * @param msg                 当前条目实体类
     * @param ivChatShareImg      分享信息图标
     * @param tvChatShareName     分享信息名字
     * @param tvChatShareFileSize 分享信息描述
     * @param rlShareInfo         分享信息组件容器
     */
    private void sharMessage(ChatMsgBean msg, ImageView ivChatShareImg, TextView tvChatShareName, TextView tvChatShareFileSize, RelativeLayout rlShareInfo) {
        try {
            final UserNote.UserNoteShareTo share = new Gson().fromJson(msg.getContent(), UserNote.UserNoteShareTo.class);
            ivChatShareImg.setBackgroundResource(R.drawable.index_ico_share);
            tvChatShareName.setText(share.UserName);
            tvChatShareFileSize.setText(share.Content);
            //                  TODO  setLayouts(type, vh);
            rlShareInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", share.NoteID);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ChatMainAdapter", "share:" + new Gson().toJson(msg));
        }
    }

    /**
     * 任务消息
     *
     * @param msg                 当前条目实体类
     * @param ivChatShareImg      分享信息图标
     * @param tvChatShareName     分享信息名字
     * @param tvChatShareFileSize 分享信息描述
     * @param rlShareInfo         分享信息组件容器
     */
    private void taskMessage(ChatMsgBean msg, ImageView ivChatShareImg, TextView tvChatShareName, TextView tvChatShareFileSize, RelativeLayout rlShareInfo) {
        final Task task = new Gson().fromJson(msg.getContent(), Task.class);
        ivChatShareImg.setBackgroundResource(R.drawable.index_ico_share);
        tvChatShareName.setText(R.string.new_task);
        tvChatShareFileSize.setText(task.getTitle());
        rlShareInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_ID, task.getID());
                intent.putExtra("start_anim", false);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 流程消息
     *
     * @param msg                 当前条目实体类
     * @param ivChatShareImg      分享信息图标
     * @param tvChatShareName     分享信息名字
     * @param tvChatShareFileSize 分享信息描述
     * @param rlShareInfo         分享信息组件容器
     */
    private void flowMessage(ChatMsgBean msg, ImageView ivChatShareImg, TextView tvChatShareName, TextView tvChatShareFileSize, RelativeLayout rlShareInfo) {
        try {
            final Follow follow = new Gson().fromJson(msg.getContent(), Follow.class);
            ivChatShareImg.setBackgroundResource(R.drawable.index_ico_gongzuoliu);
            tvChatShareName.setText(follow.Title);
            tvChatShareFileSize.setText(TextUtils.isEmpty(follow.Content) ? "你收到一个流程消息" : follow.Content);
            rlShareInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (PrefsUtil.isFormWeb()) {
                        intent = new Intent(mContext, FormWebActivity.class);
                        intent.putExtra(FormWebActivity.PAGE_ID, follow.Page);
                        intent.putExtra(FormWebActivity.ROW_ID, follow.RID);
                    } else {
                        intent = new Intent(mContext, FormDetailActivity.class);
                        intent.putExtra("page_id", String.valueOf(follow.Page));
                        intent.putExtra("row_id", String.valueOf(follow.RID));
                    }
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 附件消息
     *
     * @param msg                 当前条目实体类
     * @param ivChatShareImg      分享信息图标
     * @param tvChatShareName     分享信息名字
     * @param tvChatShareFileSize 分享信息描述
     * @param rlShareInfo         分享信息组件容器
     */
    private void AttachMessage(ChatMsgBean msg, ImageView ivChatShareImg, TextView tvChatShareName, TextView tvChatShareFileSize, RelativeLayout rlShareInfo) {
        if (msg.getContent().startsWith("{")) {
            Type types = new TypeToken<Files>() {
            }.getType();
            files = new Gson().fromJson(msg.getContent(), types);
            int id = FileUtil.getResIconId(files.getName());
            ivChatShareImg.setBackgroundResource(id);
            tvChatShareName.setText(files.getName());
            tvChatShareFileSize.setText(FileUtil.getReadableFileSize(files.getLength())); // 设置文件的大小和文件名
            rlShareInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddDialog.show();
                }
            });
        }
    }

    /**
     * 视频消息
     */
    private void videoMessage(final ChatMsgBean msg, final MyHolder vh, final int position, final VideoPlayer player) {
        Videos videos = null;
        if (msg.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
            final Files video = new Gson().fromJson(msg.getContent(), Files.class);
            videos = new Videos();
            videos.setThumbFileName(video.getName());
            videos.setLength(video.getLength() + "");
            videos.setUrl(video.getUrl());
            vh.mIvChatMsgVideoThumb.setTag(videos.getUrl());
            final Videos videoCahe = videos;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VideoPlayer tempPlayer = new VideoPlayer(mContext);
                    tempPlayer.setVideo(videoCahe);
                    final Bitmap bitmap = tempPlayer.getFirstVideoFrame(true);
                    vh.mIvChatMsgVideoThumb.post(new Runnable() {
                        @Override
                        public void run() {
                            if (videoCahe.getUrl().equals(vh.mIvChatMsgVideoThumb.getTag())) {
                                if (bitmap != null) {
                                    vh.mIvChatMsgVideoThumb.setImageBitmap(bitmap);
                                }
                            }
                        }
                    });
                }
            }).start();
        } else {
            videos = new Gson().fromJson(msg.getContent(), Videos.class);
            vh.mIvChatMsgVideoThumb.setTag(videos.getUrl());
            final Videos videoCahe = videos;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VideoPlayer tempPlayer = new VideoPlayer(mContext);
                    tempPlayer.setVideo(videoCahe, false);
                    final Bitmap bitmap = tempPlayer.getFirstVideoFrame(videoCahe.getUrl());
                    vh.mIvChatMsgVideoThumb.post(new Runnable() {
                        @Override
                        public void run() {
                            if (videoCahe.getUrl().equals(vh.mIvChatMsgVideoThumb.getTag())) {
                                if (bitmap != null) {
                                    vh.mIvChatMsgVideoThumb.setImageBitmap(bitmap);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
        if (msg.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
            player.setVideo(videos);
        } else {
            player.setVideo(videos, false);
        }
        player.setSurfaceRatio(9f / 11);

        /**********************************/
        isVideoPlay.put(position, false);
        isVideoLoading.put(position, false);
        final Videos videoCahe = videos;
        vh.mRlChatMsgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoPlay.get(position)) {//点击全屏播放视频
                    Intent intent = new Intent(mContext, TestVideoActivity.class);
                    intent.putExtra("videos", msg.getContent());
                    mContext.startActivity(intent);
                } else if (!isVideoLoading.get(position) && !isVideoPlay.get(position)) {
                    vh.mIvChatMsgVideoPlay.setVisibility(View.GONE);
                    vh.mPbChatMsgVideoProgress.setVisibility(View.VISIBLE);
                    if (msg.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
                        player.init(EMWApplication.videoPath, vh.mVpvChatMsgVideoSf, vh.mRlChatMsgVideo, vh.mPbChatMsgVideoProgress, true);
                    } else {
                        player.init(videoCahe.getUrl(), vh.mVpvChatMsgVideoSf, vh.mRlChatMsgVideo, vh.mPbChatMsgVideoProgress, true);
                    }
                    player.downLoadVideo();
                    isVideoLoading.put(position, true);
                    player.setOnClose(new VideoPlayer.OnClose() {
                        @Override
                        public void showBut() {
                            vh.mIvChatMsgVideoPlay.setVisibility(View.VISIBLE);
                            isVideoPlay.put(position, false);
                        }
                    });
                    player.setOnPlayListener(new VideoPlayer.OnPlayListener() {
                        @Override
                        public void onPlay() {
                            isVideoLoading.put(position, false);
                            isVideoPlay.put(position, true);
                        }
                    });
                }
            }
        });
        vh.mRlChatMsgVideo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ListDialog listDialog = new ListDialog(mContext, false);
                listDialog.addItem("收藏", 1);
                listDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                    @Override
                    public void selected(View view, ListDialog.Item item, int position) {
                        try {
                            String url = vh.mIvChatMsgVideoThumb.getTag().toString();
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            File file = new File(EMWApplication.videoPath + fileName);
                            if (file.exists()) {
                                String destPath = EMWApplication.filePath + fileName;
                                FileUtils.copyFile(file, new File(destPath));
                                ToastUtil.showToast(mContext, "已收藏(" + destPath + ")");
                            }
                        } catch (Exception e) {
                            ToastUtil.showToast(mContext, "收藏失败");
                        }
                    }
                });
                listDialog.show();
                return false;
            }
        });
        /***/
        if (imageLayoutWidth == -1 || true) {
            vh.mIvChatMsgVideoThumb.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imageLayoutWidth = vh.mIvChatMsgVideoThumb.getWidth();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mIvChatMsgVideoThumb.getLayoutParams();
                    params.width = imageLayoutWidth;
                    params.height = (9 * imageLayoutWidth) / 11;
                    vh.mIvChatMsgVideoThumb.setLayoutParams(params);
                    vh.mIvChatMsgVideoThumb.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mIvChatMsgVideoThumb.getLayoutParams();
            params.width = imageLayoutWidth;
            params.height = (9 * imageLayoutWidth) / 11;
            vh.mIvChatMsgVideoThumb.setLayoutParams(params);
        }
    }

    /**
     * 音频消息
     *
     * @param msg              当前条目实体类
     * @param tvChatMsgVoice   语音时间
     * @param rlMsgVoice       语音消息组件容器
     * @param itvChatVoiceShow 语音信息停止播放两条杠图标
     * @param itvChatVoiceOpen 语音信息三角播放图标
     */
    private void AudioMessage(ChatMsgBean msg, final TextView tvChatMsgVoice, final RelativeLayout rlMsgVoice
            , final IconTextView itvChatVoiceShow, final IconTextView itvChatVoiceOpen, final MySeekBar seekBarChatVoice) {
        Type types = new TypeToken<Audios>() {
        }.getType();
        Audios audios = new Gson().fromJson(msg.getContent(), types);
        String paths;
        if (msg.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
            final String path = Const.BASE_URL + "/" + audios.getUrl();
            paths = path.replace("\"", "");
        } else {
            paths = audios.getUrl();
        }
        final String times;
        String time = null;
        //判断时间格式是否是xx:xx
        if (audios.getLength().contains(":")) {
            tvChatMsgVoice.setText(audios.getLength());
            time = audios.getLength();
        } else {
            try {
                Format f0 = new SimpleDateFormat("ss");
                SimpleDateFormat f1 = new SimpleDateFormat("mm:ss");
                Date d = (Date) f0.parseObject(audios.getLength());
                time = f1.format(d);
                tvChatMsgVoice.setText(f1.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        times = time;
        final String finalPaths = paths;
        rlMsgVoice.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (!finalPaths.equals(tempPath)) {
                    mMediaPlayerMangers.pause();
                    isPlay = true;
                }
                if (isPlay) {
                    mMediaPlayerMangers.playSound(itvChatVoiceShow, itvChatVoiceOpen, seekBarChatVoice, finalPaths, tvChatMsgVoice, times, rlMsgVoice, mContext, ChatMainAdapter.this);
                } else {
                    mMediaPlayerMangers.pause();
                }
                isPlay = !isPlay;
                tempPath = finalPaths;
            }
        });


        rlMsgVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                switch (PrefsUtil.getAudioDevice()) {
                    case AudioManager.MODE_IN_COMMUNICATION:
                        //音频设备列表
                        mAudioDeviceList = new ListDialog(mContext, false);
                        mAudioDeviceList.addItem("使用扬声器模式", ChatContent.AUDIO_DEVICE_SPEEKER);
                        mAudioDeviceList.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                            @Override
                            public void selected(View view, ListDialog.Item item, int position) {
                                audioManager.setSpeakerphoneOn(true);
                                audioManager.setMode(AudioManager.MODE_NORMAL);
                                PrefsUtil.setAudioDevice(AudioManager.MODE_NORMAL);
                                ToastUtil.showToast(mContext, "已切换为扬声器播放模式");
                            }
                        });
                        break;
                    case AudioManager.MODE_NORMAL:
                        mAudioDeviceList = new ListDialog(mContext, false);
                        mAudioDeviceList.addItem("使用听筒模式", ChatContent.AUDIO_DEVICE_EAR);
                        mAudioDeviceList.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                            @Override
                            public void selected(View view, ListDialog.Item item, int position) {
                                audioManager.setSpeakerphoneOn(false);//关闭扬声器
                                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                                PrefsUtil.setAudioDevice(AudioManager.MODE_IN_COMMUNICATION);
                                ToastUtil.showToast(mContext, "已切换为听筒模式");
                            }
                        });
                        break;
                    default:
                        return true;
                }
                mAudioDeviceList.show();
                return true;
            }
        });
    }

    //照片消息
    private void ImageMessage(MyHolder holder, final ChatMsgBean chatMSG) {
        DisplayImageOptions optionesMsgImage = ImageLoadFactory.getChatApdaterImage();
        if (chatMSG.getIsSendFlag() == ChatContent.SEND_MSG_SUCCESS) {
            Files image = new Gson().fromJson(chatMSG.getContent(), Files.class);
            final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image.getUrl());
            ImageLoader.getInstance().displayImage(uri, holder.mItvChatMsgImge, optionesMsgImage);
            holder.mItvChatMsgImge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Files image = new Gson().fromJson(chatMSG.getContent(), Files.class);
                    final String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image.getUrl());
                    Intent intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_POSITION, 0);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, uri);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_ISFORMAT, false);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.image_activity_in, R.anim.image_activity_out);
                    sb.delete(0, sb.length());
                }
            });
            holder.mItvChatMsgImge.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ListDialog listDialog = new ListDialog(mContext, false);
                    listDialog.addItem("收藏", 1);
                    listDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                        @Override
                        public void selected(View view, ListDialog.Item item, int position) {
                            try {
                                File file = ImageLoader.getInstance().getDiskCache().get(uri);
                                if (file != null && file.exists()) {
                                    String fileName = uri.substring(uri.lastIndexOf("/") + 1);
                                    String destPath = EMWApplication.filePath + fileName;
                                    FileUtils.copyFile(file, new File(destPath));
                                    ToastUtil.showToast(mContext, "已收藏(" + destPath + ")");
                                }
                            } catch (Exception e) {
                                ToastUtil.showToast(mContext, "收藏失败");
                            }
                        }
                    });
                    listDialog.show();
                    return false;
                }
            });
        } else {
            ImageLoader.getInstance().displayImage("file://" + chatMSG.getContent(), holder.mItvChatMsgImge, optionesMsgImage);
        }
    }


    /**
     * 初始化设置用户头像以及设置监听
     */
    private void initUserHeadImage(ChatMsgBean data, CircleImageView civChatMsgHead) {
        String image = "";
        final UserInfo user = EMWApplication.personMap.get(data.getSenderID());
        if (user != null) {
            image = user.Image;

            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
            civChatMsgHead.setTextBg(EMWApplication.getIconColor(user.ID), user.Name, 40);
            ImageLoader.getInstance().displayImage(uri, civChatMsgHead, optiones);
            civChatMsgHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(mContext, PersonInfoActivity.class);
                    intent1.putExtra("intoTag", 1);
                    intent1.putExtra("UserInfo", user);
                    intent1.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent1.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent1);
                }
            });
        } else {
            civChatMsgHead.setTextBg(EMWApplication.getIconColor(data.getSenderID()), "", 40);
        }
    }

    /**
     * @des 针对附件信息的打开方式
     * @cal
     */
    private void initListDialog(Context context) {
        mAddDialog = new ListDialog(context, false);
        mAddDialog.addItem("在线查看", ApiEnum.UserNoteAddTypes.Share);
        mAddDialog.addItem("在线下载", ApiEnum.UserNoteAddTypes.File);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ApiEnum.UserNoteAddTypes.Share:
                        if (files.getName() != null) {
                            if (!FileUtil.hasFile(EMWApplication.filePath + files.getUrl())) {
                                ToastUtil.showToast(mContext, "你未下载该文件，请先下载");
                            } else {
                                FileUtil.openFile(mContext, EMWApplication.filePath + files.getUrl());
                            }
                        }
                        break;
                    case ApiEnum.UserNoteAddTypes.File:
                        if (files.getName() != null) {
                            if (!FileUtil.hasFile(EMWApplication.filePath + files.getUrl())) {
                                String fileUrl = Const.DOWN_FILE_URL2 + files.getUrl();
                                NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath);
                            } else {
                                FileUtil.openFile(mContext, EMWApplication.filePath + files.getUrl());
                            }
                        }
                        break;
                }
            }
        });
    }

    /**
     * 初始化加速传感器
     */
    private SensorManager sensorManager;
    private Sensor sensor;

    private void initSensor() {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        new SensorListener();
        sensorManager.getSensorList(Sensor.TYPE_ALL);
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void GoneChikBox() {

    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    //距离传感器监听
    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            int audioDevice = PrefsUtil.getAudioDevice();
            if (PrefsUtil.getAudioDevice() == -1) {
                PrefsUtil.setAudioDevice(AudioManager.MODE_NORMAL);
            }
            if (audioDevice == AudioManager.MODE_NORMAL) {
                float proxiVal = sensorEvent.values[0];
                if (proxiVal == sensor.getMaximumRange()) {
                    audioManager.setSpeakerphoneOn(true);
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                } else {
                    audioManager.setSpeakerphoneOn(false);//关闭扬声器
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //传感器精度变化时使用
        }
    }


    /**
     * 缓存
     */
    public static class MyHolder extends RecyclerView.ViewHolder {
        /*******************************************************************
         * default
         */
        View mItemView;//条目全局view
        TextView mTvMsgTime;//消息头部时间
        CircleImageView mCivChatMsgHead;//接受者消息头像
        //        RelativeLayout mRlChatTimeMsg;//消息底部时间容器
        //        TextView mTvChatTimeMsg;//消息底部时间
        //        IconTextView mItvChatMsgStateSuccess;//消息底部图标--成功状态
        IconTextView mItvChatMsgStateError;//消息底部图标--失败状态
        //        ProgressBar mItvChatMsgStateStart;//消息底部图标--加载状态
        //        RelativeLayout mRlChatItemLinkAll;//聊天底部状态图标和时间的容器组件
        /*******************************************************************
         * 消息连接符
         */
        /*******************************************************************
         * 图片消息
         */
        ImageView mItvChatMsgImge;

        /*******************************************************************
         * 语音消息
         */
        RelativeLayout mRlMsgVoiceRece;//语音组件容器
        IconTextView mItvChatVoiceOpenRece;//停止播放状态 三角
        IconTextView mItvChatVoiceShowRece;//播放状态 两竖
        MySeekBar mSBChatVoiceRece;//播放进度条
        TextView mTvChatMsgVoiceRece;//语音时间
        IconTextView mItvChatAudioBtRece;//语音图标

        RelativeLayout mRlMsgVoiceSend;//语音组件容器
        IconTextView mItvChatVoiceOpenSend;//停止播放状态 三角
        IconTextView mItvChatVoiceShowSend;//播放状态 两竖
        MySeekBar mSBChatVoiceSend;//播放进度条
        TextView mTvChatMsgVoiceSend;//语音时间
        IconTextView mItvChatAudioBtSend;//语音图标

        RelativeLayout mRlMsgVoiceReceAll;
        RelativeLayout mRlMsgVoiceSendAll;
        /*******************************************************************
         * 视频消息
         */
        RelativeLayout mRlChatMsgVideo;//视频组件容器
        ImageView mIvChatMsgVideoThumb;//视频第一帧图片显示组件
        VideoPlayerView mVpvChatMsgVideoSf;//视频播放组件
        ImageView mIvChatMsgVideoPlay;//视频播放按钮图标
        ProgressBar mPbChatMsgVideoProgress;//视频加载状态显示组件
        VideoPlayer player;//媒体播放类
        /*******************************************************************
         * //附件消息   //流程消息  //任务消息  //分享消息
         */
        RelativeLayout mRlMsgTextShowRece;//接收分享消息组件容器
        ImageView mIvChatShareImgRece;//接收分享消息图标
        TextView mTvChatShareNameRece;//接收分享消息标题
        TextView mTvChatShareFileSizeRece;//接收分享消息描述
        RelativeLayout mRlMsgTextShowSend;//发送分享消息组件容器
        ImageView mIvChatShareImgSend;//发送分享消息图标
        TextView mTvChatShareNameSend;//发送分享消息标题
        TextView mTvChatShareFileSizeSend;//发送分享消息描述

        RelativeLayout mRlChatMsgShareReceAll;
        RelativeLayout mRlChatMsgShareSendAll;
        /*******************************************************************
         * 普通消息和通知类消息通道
         */
        RelativeLayout mRlChatItemMsgTextRece;//接收消息文本组件
        TextView mTvChatMsgContentRece;//接收普通消息文本
        RelativeLayout mRlChatItemSendRoot;
        TextView mTvChatMsgContentSend;//发送普通消息文本
        RelativeLayout mRlMsgTextReceAll;
        RelativeLayout mRlMsgTextSendAll;
        ImageView iv_rece_sc;
        ImageView iv_rece_bj;
        ImageView iv_send_sc;
        ImageView iv_send_bj;

        TextView mTvChatAiMsgContentSendHeader;
        /**********************************************
         * AI
         */
        TextView aiMsg;
        //        TextView aiTime;
        RecyclerView mChatRvMessage;
        TextView mTvAIChatMsgContentRece;
        CardView mCvMsg;
        //        RelativeLayout mRlAiChatTimeMsg;
        RelativeLayout mRlImageListRoot;
        RecyclerView mChatAiLvMessage;

        ImageView mIvMapImage, mIvMapImage2;
        LinearLayout mLlSharePosTiming, mLlSharePosTiming2;
        SectorProgressView mSpv, mSpv2;
        TextView mTvLocationInfo, mTvLocationInfo2;
        RelativeLayout mRlLocationReceive, mRlLocationSend;

        EventRecyclerView mRvChatImportanceMessage;
        ImageView mIvDeleteImprotanceMsgList;
        LinearLayout mLlImprotanceMsgChangeRoot;
        TextView mTvImprotanceAllIn;
        TextView mTvImprotanceDelete;
        ImageView mIvEditImprotanceMsgList;
        TextView mTvRemoveEditImprotanceMsgList;


        //        TextView mTvChatItemMsgDynamicUserName;//XXX发布了一个动态
        //        TextView mTvChatItemMsgDynamicUserNum;//动态参加人数  (1人)
        //        LinearLayout mLlChatItemMsgDynamicHeadIcon;//动态人员头像ROOT
        ImageView mIvChatItemMsgDynamicIcon;//动态图片   可隐藏
        ImageView mIvChatItemMsgDynamicTitleIcon;//动态类型图标标识
        TextView mTvChatItemMsgDynamicTitle;//动态标题
        TextView mTvChatItemMsgDynamicTime;//动态时间信息
        Button mButChatItemMsgDynamicAttend;//参加动态按钮
        LinearLayout mLlDynamiInfoRoot;//点击详情ROOT
        RelativeLayout mRlChatItemPayType;
        TextView mTvChatItemPayType;
        RelativeLayout mRlChatItemPayMoney;
        TextView mTvChatItemPayMoney;

        MKLoader loaderSend;
        RelativeLayout mrlSendTextMSG;
        MKLoader mrlReceTextMsg;

        //        CircleImageView mCircleImageView;
        public MyHolder(View itemView, ChatMainAdapter adapter, ChatMsgBean msg) {
            super(itemView);
            initView(itemView, msg, adapter);
        }

        private void initView(View itemView, ChatMsgBean msg, ChatMainAdapter adapter) {
            this.mItemView = itemView;
            initDefaultView(itemView);
            if (msg != null) {
                switch (msg.getType()) {
                    case ChatContent.DYNAMIC:
                        //                        mTvChatItemMsgDynamicUserName = (TextView) itemView.findViewById(R.id.tv_chat_item_msg_dynamic_username);
                        //                        mCircleImageView= (CircleImageView) itemView.findViewById(R.id.civ_chat_msg_head);
                        mRlChatItemPayType = (RelativeLayout) itemView.findViewById(R.id.rl_chat_item_pay_type);
                        mTvChatItemPayType = (TextView) itemView.findViewById(R.id.tv_chat_item_pay_type);
                        mRlChatItemPayMoney = (RelativeLayout) itemView.findViewById(R.id.rl_chat_item_pay_money);
                        mTvChatItemPayMoney = (TextView) itemView.findViewById(R.id.tv_chat_item_pay_money);


                        mIvChatItemMsgDynamicIcon = (ImageView) itemView.findViewById(R.id.iv_chat_item_msg_dynamic_icon);
                        mIvChatItemMsgDynamicTitleIcon = (ImageView) itemView.findViewById(R.id.iv_chat_item_msg_dynamic_title_icon);
                        mTvChatItemMsgDynamicTitle = (TextView) itemView.findViewById(R.id.tv_chat_item_msg_dynamic_title);
                        mTvChatItemMsgDynamicTime = (TextView) itemView.findViewById(R.id.tv_chat_item_msg_dynamic_time);
                        mButChatItemMsgDynamicAttend = (Button) itemView.findViewById(R.id.but_chat_item_msg_dynamic_attend);
                        mLlDynamiInfoRoot = (LinearLayout) itemView.findViewById(R.id.ll_dynamic_info_root);
                        //                        mTvChatItemMsgDynamicUserNum = (TextView) itemView.findViewById(R.id.tv_chat_item_msg_dynamic_user_num);
                        //                        mLlChatItemMsgDynamicHeadIcon = (LinearLayout) itemView.findViewById(R.id.ll_chat_item_msg_dynamic_headicon);


                        break;
                    case ApiEnum.MessageType.Image://图片消息
                        mItvChatMsgImge = (ImageView) itemView.findViewById(R.id.iv_chat_msg_img);
                        break;
                    case ApiEnum.MessageType.Audio://音频消息
                        mRlMsgVoiceRece = (RelativeLayout) itemView.findViewById(R.id.rl_msg_voice_rece);//语音组件容器
                        mItvChatVoiceOpenRece = (IconTextView) itemView.findViewById(R.id.itv_chat_voice_open_rece);//停止播放状态 三角
                        mItvChatVoiceShowRece = (IconTextView) itemView.findViewById(R.id.itv_chat_voice_show_rece);//播放状态 两竖
                        mSBChatVoiceRece = (MySeekBar) itemView.findViewById(R.id.seekbar_chat_voice_rece);//播放进度条
                        mTvChatMsgVoiceRece = (TextView) itemView.findViewById(R.id.tv_chat_msg_voice_rece);//语音时间
                        mItvChatAudioBtRece = (IconTextView) itemView.findViewById(R.id.itv_chat_audio_bt_rece);//语音图标

                        mRlMsgVoiceSend = (RelativeLayout) itemView.findViewById(R.id.rl_msg_voice_send);//语音组件容器
                        mItvChatVoiceOpenSend = (IconTextView) itemView.findViewById(R.id.itv_chat_voice_open_send);//停止播放状态 三角
                        mItvChatVoiceShowSend = (IconTextView) itemView.findViewById(R.id.itv_chat_voice_show_send);//播放状态 两竖
                        mSBChatVoiceSend = (MySeekBar) itemView.findViewById(R.id.seekbar_chat_voice_send);//播放进度条
                        mTvChatMsgVoiceSend = (TextView) itemView.findViewById(R.id.tv_chat_msg_voice_send);//语音时间
                        mItvChatAudioBtSend = (IconTextView) itemView.findViewById(R.id.itv_chat_audio_bt_send);//语音图标

                        mRlMsgVoiceReceAll = (RelativeLayout) itemView.findViewById(R.id.rl_msg_voice_rece_all);
                        mRlMsgVoiceSendAll = (RelativeLayout) itemView.findViewById(R.id.rl_msg_voice_send_all);
                        break;
                    case ApiEnum.MessageType.Video://视频消息
                        mRlChatMsgVideo = (RelativeLayout) itemView.findViewById(R.id.rl_chat_msg_video);//视频组件容器
                        mIvChatMsgVideoThumb = (ImageView) itemView.findViewById(R.id.iv_chat_msg_video_thumb);//视频第一帧图片显示组件
                        mVpvChatMsgVideoSf = (VideoPlayerView) itemView.findViewById(R.id.vpv_chat_msg_video_sf);//视频播放组件
                        mIvChatMsgVideoPlay = (ImageView) itemView.findViewById(R.id.iv_chat_msg_video_play);//视频播放按钮图标
                        mPbChatMsgVideoProgress = (ProgressBar) itemView.findViewById(R.id.pb_chat_msg_video_progress);//视频加载状态显示组件
                        break;
                    case ChatContent.DEFAULT_MSG://机器人-普通消息
                        mTvMsgTime = (TextView) itemView.findViewById(R.id.tv_msg_time);
                        aiMsg = (TextView) itemView.findViewById(R.id.tv_ai_chat_msg_content_rece);
                        //                        aiTime = (TextView) itemView.findViewById(R.id.tv_ai_chat_time_msg);
                        //                        mRlAiChatTimeMsg = (RelativeLayout) itemView.findViewById(R.id.rl_ai_chat_time_msg);
                        mRlImageListRoot = (RelativeLayout) itemView.findViewById(R.id.rl_image_list_root);
                        mChatAiLvMessage = (RecyclerView) itemView.findViewById(R.id.chat_ai_lv_message);
                        break;
                    case ChatContent.SCHEDULE_MSG://机器人-日程
                        mTvMsgTime = (TextView) itemView.findViewById(R.id.tv_msg_time);
                        mChatRvMessage = (RecyclerView) itemView.findViewById(R.id.chat_ai_lv_message2);
                        //                        aiTime = (TextView) itemView.findViewById(R.id.tv_ai_chat_time_msg);
                        mTvAIChatMsgContentRece = (TextView) itemView.findViewById(R.id.tv_ai_chat_msg_content_rece);
                        mCvMsg = (CardView) itemView.findViewById(R.id.cv_msg);
                        break;
                    case ChatContent.IMPROTANCE_MSG:
                        mTvMsgTime = new TextView(adapter.mContext);
                        mCivChatMsgHead = new CircleImageView(adapter.mContext);
                        //                        mRlChatTimeMsg = new RelativeLayout(adapter.mContext);
                        //                        mTvChatTimeMsg = new TextView(adapter.mContext);
                        //                        mItvChatMsgStateSuccess = new IconTextView(adapter.mContext);
                        mItvChatMsgStateError = new IconTextView(adapter.mContext);
                        //                        mItvChatMsgStateStart = new ProgressBar(adapter.mContext);
                        //                        mRlChatItemLinkAll = new RelativeLayout(adapter.mContext);
                        mLlImprotanceMsgChangeRoot = (LinearLayout) itemView.findViewById(R.id.ll_improtance_msg_change_root);
                        mTvImprotanceAllIn = (TextView) itemView.findViewById(R.id.tv_improtance_all_in);
                        mTvImprotanceDelete = (TextView) itemView.findViewById(R.id.tv_improtance_delete);
                        mRvChatImportanceMessage = (EventRecyclerView) itemView.findViewById(R.id.rv_chat_importance_messages);
                        mIvDeleteImprotanceMsgList = (ImageView) itemView.findViewById(R.id.iv_delete_improtance_msg_list);
                        mIvEditImprotanceMsgList = (ImageView) itemView.findViewById(R.id.iv_edit_improtance_msg_list);
                        mTvRemoveEditImprotanceMsgList = (TextView) itemView.findViewById(R.id.tv_remove_edit_improtance_msg_list);
                        break;
                    case ApiEnum.MessageType.Attach://附件消息
                    case ApiEnum.MessageType.Flow://流程消息
                    case ApiEnum.MessageType.Task://任务消息
                    case ApiEnum.MessageType.Share://分享消息
                        mRlMsgTextShowRece = (RelativeLayout) itemView.findViewById(R.id.rl_msg_text_show_rece);//接收分享消息组件容器
                        mIvChatShareImgRece = (ImageView) itemView.findViewById(R.id.iv_chat_share_img_rece);//接收分享消息图标
                        mTvChatShareNameRece = (TextView) itemView.findViewById(R.id.tv_chat_share_name_rece);//接收分享消息标题
                        mTvChatShareFileSizeRece = (TextView) itemView.findViewById(R.id.tv_chat_share_filesize_rece);//接收分享消息描述
                        mRlMsgTextShowSend = (RelativeLayout) itemView.findViewById(R.id.rl_share_info_send);//发送分享消息组件容器
                        mIvChatShareImgSend = (ImageView) itemView.findViewById(R.id.iv_chat_share_img_send);//发送分享消息图标
                        mTvChatShareNameSend = (TextView) itemView.findViewById(R.id.tv_chat_share_name_send);//发送分享消息标题
                        mTvChatShareFileSizeSend = (TextView) itemView.findViewById(R.id.tv_chat_share_filesize_send);//发送分享消息描述

                        mRlChatMsgShareReceAll = (RelativeLayout) itemView.findViewById(R.id.rl_chat_msg_share_rece_all);
                        mRlChatMsgShareSendAll = (RelativeLayout) itemView.findViewById(R.id.rl_chat_msg_share_send_all);
                        break;
                    case ChatContent.CHAT_LOCATION:
                        mIvMapImage = (ImageView) itemView.findViewById(R.id.iv_map_image);
                        mTvLocationInfo = (TextView) itemView.findViewById(R.id.tv_location_info);
                        break;
                    case ChatContent.CHAT_SHARE_LOCATION:
                        mIvMapImage = (ImageView) itemView.findViewById(R.id.iv_map_image);
                        mTvLocationInfo = (TextView) itemView.findViewById(R.id.tv_location_info);
                        mLlSharePosTiming = (LinearLayout) itemView.findViewById(R.id.ll_share_pos_timing);
                        mSpv = (SectorProgressView) itemView.findViewById(R.id.spv);
                        mIvMapImage2 = (ImageView) itemView.findViewById(R.id.iv_map_image2);
                        mTvLocationInfo2 = (TextView) itemView.findViewById(R.id.tv_location_info2);
                        mLlSharePosTiming2 = (LinearLayout) itemView.findViewById(R.id.ll_share_pos_timing2);
                        mSpv2 = (SectorProgressView) itemView.findViewById(R.id.spv2);
                        mRlLocationReceive = (RelativeLayout) itemView.findViewById(R.id.rl_location_receive);
                        mRlLocationSend = (RelativeLayout) itemView.findViewById(R.id.rl_location_send);
                        break;
                    default://普通消息和通知类消息通道
                        mRlChatItemMsgTextRece = (RelativeLayout) itemView.findViewById(R.id.rl_chat_item_msg_text_rece);//接收消息文本组件
                        mTvChatMsgContentRece = (TextView) itemView.findViewById(R.id.tv_chat_msg_content_rece);//接收普通消息文本
                        mRlChatItemSendRoot = (RelativeLayout) itemView.findViewById(R.id.rl_chat_item_send_root);
                        mTvChatMsgContentSend = (TextView) itemView.findViewById(R.id.tv_chat_msg_content_send);//发送普通消息文本
                        mRlMsgTextReceAll = (RelativeLayout) itemView.findViewById(R.id.rl_msg_text_rece_all);
                        mRlMsgTextSendAll = (RelativeLayout) itemView.findViewById(R.id.rl_msg_text_send_all);
                        mTvChatAiMsgContentSendHeader = (TextView) itemView.findViewById(R.id.tv_chat_ai_msg_content_send_header);//发送消息给机器人的标识
                        loaderSend = (MKLoader) itemView.findViewById(R.id.chat_loader_send);
                        mrlSendTextMSG = (RelativeLayout) itemView.findViewById(R.id.rl_chat_send_textmsg);
                        mrlReceTextMsg = (MKLoader) itemView.findViewById(R.id.chat_loader_rece);
                        iv_rece_sc = (ImageView) itemView.findViewById(R.id.iv_rece_sc);
                        iv_rece_bj = (ImageView) itemView.findViewById(R.id.iv_rece_bj);
                        iv_send_sc = (ImageView) itemView.findViewById(R.id.iv_send_sc);
                        iv_send_bj = (ImageView) itemView.findViewById(R.id.iv_send_bj);


                        break;
                }
            }
        }

        /**
         * 初始化默认控件
         *
         * @param itemView 不同条目类型的视图实例
         */
        private void initDefaultView(View itemView) {
            mTvMsgTime = (TextView) itemView.findViewById(R.id.tv_msg_time);
            mCivChatMsgHead = (CircleImageView) itemView.findViewById(R.id.civ_chat_msg_head);
            //            mRlChatTimeMsg = (RelativeLayout) itemView.findViewById(R.id.rl_chat_time_msg);
            //            mTvChatTimeMsg = (TextView) itemView.findViewById(R.id.tv_chat_time_msg);
            //            mItvChatMsgStateSuccess = (IconTextView) itemView.findViewById(R.id.itv_chat_msg_state_success);
            mItvChatMsgStateError = (IconTextView) itemView.findViewById(R.id.itv_chat_msg_state_error);
            //            mItvChatMsgStateStart = (ProgressBar) itemView.findViewById(R.id.itv_chat_msg_state_start);
            //            mRlChatItemLinkAll = (RelativeLayout) itemView.findViewById(R.id.rl_chat_item_link_all);
        }
    }

    /**
     * 设置条目数量
     */
    @Override
    public int getItemCount() {
        if (mMessageList != null || mMessageList.size() > 0) {
            return mMessageList.size();
        } else {
            return 0;
        }
    }

    /**
     * 条目类型
     * 普通消息/语音/视频/图片/。。。。。
     */
    @Override
    public int getItemViewType(int position) {
        mChatMsg = mMessageList.get(position);
        return mMessageList.get(position).getType();
    }

    /**********************************************
     * 获取当前条目的接收发送状态
     * 接收者/发送者
     */
    private int getItemViewMsgType(int position) {
        UserInfo user = PrefsUtil.readUserInfo();// 用户
        return mMessageList.get(position).getSenderID() == user.ID ? ChatContent.TYPE_ITEM_SEND : ChatContent.TYPE_ITEM_RECE;
    }

}