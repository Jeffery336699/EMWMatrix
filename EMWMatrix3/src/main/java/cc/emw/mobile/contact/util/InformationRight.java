package cc.emw.mobile.contact.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.NoticeCommentAdapter;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.Dynamic;
import cc.emw.mobile.chat.model.bean.Follow;
import cc.emw.mobile.chat.model.bean.GroupMessage;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.chat.model.bean.Information;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.entity.CallInfo;
import cc.emw.mobile.entity.DynamicEnjoy;
import cc.emw.mobile.entity.NoticeComment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.task.adapter.CommentAdapter;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by tao.zhou on 2017/4/26.
 */

public class InformationRight {

    private Context context;

    private SlidingMenu mSlidingMenu;

    private boolean isMainActivity;
    private MyListView mListView_common;
    private LinearLayout llSum;

    public InformationRight(Context context, SlidingMenu mSlidingMenu, boolean isMainActivity) {
        this.context = context;
        this.mSlidingMenu = mSlidingMenu;
        this.isMainActivity = isMainActivity;
    }

    private PtrFrameLayout mPtrFrameLayout;
    private LinearLayout.LayoutParams params;
    private DisplayImageOptions options;
    private DisplayImageOptions optionsGroup;
    private ScrollView scrollView;
    private View mBlankView;   //空白内容状态
    private TextView mTvTalker;
    private MyListView mListView_talker;
    private MyListView mListView_work;
    private MyListView mListView_information;
    private MyListView mCommentLv;
    private MyListView mEnjoyLv;
    private ChatAdapter adapterChat = new ChatAdapter();
    private ChatAdapters adapterWork = new ChatAdapters();
    private ChatAdapterss adapterInfomation = new ChatAdapterss();
    private CommentAdapter commentAdapter = new CommentAdapter();
    //    private EnjoyAdapter enjoyAdapter = new EnjoyAdapter();   //动态收藏
    private View mLineTalkerTop, mLineTalkerBottom, mLineWorkBottom, mLineInfoBottom;
    public static ArrayList<HistoryMessage> m1 = new ArrayList<>();//Takler
    public static ArrayList<ApiEntity.Message> m2 = new ArrayList<>();//Work
    public static ArrayList<ApiEntity.Message> m3 = new ArrayList<>();//Information
    public static List<UserNote> mEnjoyList = new ArrayList<>();
    /////////////////////////////// 评论通知 ///////////////////////
    private List<ApiEntity.Message> comments = new ArrayList<>();
    public static List<NoticeComment> noticeComments;
    private int topId;  //标记是否为同一条动态
    //    private List<ApiEntity.Message> enjoys = new ArrayList<>();
    /////////////////////////////// 收藏通知 ///////////////////////
    private int topIds;  //标记是否为同一条动态
    private Map<Integer, List<String>> mEnjoyHeadMap = new HashMap<>();
    ////////////////////////////////////////////////////////////////
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //                case 111:   //Talker
                //                    try {
                //                        if (m1 != null && m1.size() > 0) {
                //                            scrollView.setVisibility(View.VISIBLE);
                //                            mBlankView.setVisibility(View.GONE);
                //                            adapterChat.setData(m1);
                //                            mListView_talker.requestLayout();
                //                            adapterChat.notifyDataSetChanged();
                //                        }
                //                    } catch (Exception e) {
                //                        Log.d("zrjt", "m1" + e.toString());
                //                    }
                //                    break;
                //                case 112:   //work
                //                    try {
                //                        if (m2 != null && m2.size() >= 0) {
                //                            scrollView.setVisibility(View.VISIBLE);
                //                            mBlankView.setVisibility(View.GONE);
                //                            mLineWorkBottom.setVisibility(View.VISIBLE);
                //                            adapterWork.setData(m2);
                //                            mListView_work.requestLayout();
                //                            adapterWork.notifyDataSetChanged();
                //                            mLineWorkBottom.setVisibility(m2.size() > 0 ? View.VISIBLE : View.GONE);
                //                        }
                //                    } catch (Exception e) {
                //                    }
                //                    break;
                case 113:   //notice
                    try {
                        adapterInfomation.setData(m3);
                        commentAdapter.setData((ArrayList<NoticeComment>) noticeComments);
                        mListView_information.requestLayout();
                        adapterInfomation.notifyDataSetChanged();
                        commentAdapter.notifyDataSetChanged();
                        mLineInfoBottom.setVisibility(m3 != null && m3.size() > 0 ? View.VISIBLE : View.GONE);
                        if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                            scrollView.setVisibility(View.VISIBLE);
                            mBlankView.setVisibility(View.GONE);
                        } else {
                            mBlankView.setVisibility(View.VISIBLE);
                            scrollView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };

    /**
     * 初始化右侧导航栏
     */
    public void initRightMenu() {
        mPtrFrameLayout = (PtrFrameLayout) ((BaseActivity) context).findViewById(R.id.load_more_list_view_ptr_frame);
        // header
        final MaterialHeader header = new MaterialHeader(context);
        int[] colors = context.getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(context, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setToggleMenu(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);

        params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30));
        mTvTalker = (TextView) ((BaseActivity) context).findViewById(R.id.tv_right_talker);
        mLineTalkerTop = ((BaseActivity) context).findViewById(R.id.line_talker_top);
        mLineTalkerBottom = ((BaseActivity) context).findViewById(R.id.line_talker_bottom);
        mLineWorkBottom = ((BaseActivity) context).findViewById(R.id.line_work_bottom);
        mLineInfoBottom = ((BaseActivity) context).findViewById(R.id.line_info_bottom);
        mCommentLv = (MyListView) ((BaseActivity) context).findViewById(R.id.mlv_comment);
        mEnjoyLv = (MyListView) ((BaseActivity) context).findViewById(R.id.mlv_enjoy);
        mListView_talker = (MyListView) ((BaseActivity) context).findViewById(R.id.list_takler);
        mListView_work = (MyListView) ((BaseActivity) context).findViewById(R.id.list_work);
        mListView_information = (MyListView) ((BaseActivity) context).findViewById(R.id.list_information);
        mListView_common = (MyListView) ((BaseActivity) context).findViewById(R.id.list_common);
        mBlankView = ((BaseActivity) context).findViewById(R.id.kongbai);
        scrollView = (ScrollView) ((BaseActivity) context).findViewById(R.id.scroll);
        llSum = (LinearLayout) ((BaseActivity) context).findViewById(R.id.ll_sum);

        ImageView dismiss = (ImageView) ((BaseActivity) context).findViewById(R.id.dismiss);    //关闭右侧菜单
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggle();
            }
        });

        TextView delete = (TextView) ((BaseActivity) context).findViewById(R.id.delete);    //标记为已读
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMessageCounts(-1);
            }
        });

        mBlankView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        options = new DisplayImageOptions.Builder()
                //                .showImageOnLoading(R.drawable.cm_img_head)
                //                .showImageForEmptyUri(R.drawable.cm_img_head)
                //                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();

        optionsGroup = new DisplayImageOptions.Builder()
                //                .showImageOnLoading(R.drawable.cm_img_grouphead)
                //                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                //                .showImageOnFail(R.drawable.cm_img_grouphead)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();

        //是否是在MainActivity进入
        if (isMainActivity) {
            //            mLineTalkerTop.setVisibility(View.VISIBLE);
            //            mTvTalker.setVisibility(View.VISIBLE);
            getChatList(false);
        } else {
            adapterChat.setData(m1);
            adapterWork.setData(m2);
            adapterInfomation.setData(m3);
            commentAdapter.setData((ArrayList<NoticeComment>) noticeComments);
            //            enjoyAdapter.setData((ArrayList<UserNote>) mEnjoyList);
            //            mLineTalkerTop.setVisibility(View.GONE);
            //            mTvTalker.setVisibility(View.GONE);
            if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                scrollView.setVisibility(View.VISIBLE);
                mBlankView.setVisibility(View.GONE);
            } else {
                mBlankView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }
        }
        mLineTalkerBottom.setVisibility(m1.size() > 0 ? View.VISIBLE : View.GONE);
        mLineWorkBottom.setVisibility(m2.size() > 0 ? View.VISIBLE : View.GONE);
        mLineInfoBottom.setVisibility(m3.size() > 0 ? View.VISIBLE : View.GONE);
        //set
        mListView_talker.setAdapter(adapterChat);
        mListView_work.setAdapter(adapterWork);
        mListView_information.setAdapter(adapterInfomation);
        mCommentLv.setAdapter(commentAdapter);

        //        mEnjoyLv.setAdapter(enjoyAdapter);
        //        getMyFans();

        refresh();

    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        scrollView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getChatList(false);
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                context.sendBroadcast(intent);
            }
        });
    }

    /**
     * Talker
     */
    class ChatAdapter extends BaseAdapter {

        private List<HistoryMessage> m1 = new ArrayList<>();

        public void setData(List<HistoryMessage> m1) {
            this.m1 = m1;
        }

        @Override
        public int getCount() {
            return m1.size() == 0 ? 0 : m1.size();
        }

        @Override
        public Object getItem(int arg0) {
            return m1.get(arg0).getMessage() == null ? null : m1.get(arg0).getMessage();
        }

        @Override
        public long getItemId(int arg0) {
            return m1.get(arg0).getReceiverID() == 0 ? 0 : m1.get(arg0).getReceiverID();
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            final HistoryMessage msg = m1.get(arg0);
//            final ViewHolder vh = null;
            final MyViewHolder mvh;
            if (null == convertView) {
                mvh = new MyViewHolder();
//                convertView = LayoutInflater.from(context).inflate(R.layout.listitem_chats, null);
//                vh.content = (TextView) convertView.findViewById(R.id.content);
//                vh.head = (CircleImageView) convertView.findViewById(R.id.head);
//                vh.time = (TextView) convertView.findViewById(R.id.time);
//                vh.v1 = convertView.findViewById(R.id.bace);
//                vh.v2 = convertView.findViewById(R.id.liucheng);
//                vh.liucheng_content = (TextView) convertView.findViewById(R.id.liucheng_content);
//                vh.liucheng_time = (TextView) convertView.findViewById(R.id.liucheng_time);
//                vh.dot1 = convertView.findViewById(R.id.view_message_tag_talker_bace);
//                vh.dot2 = convertView.findViewById(R.id.view_message_tag_talker_liucheng);
                convertView = LayoutInflater.from(context).inflate(R.layout.item_section_char_content, null);
                mvh.ivHead=convertView.findViewById(R.id.ivHead);
                mvh.tvName=convertView.findViewById(R.id.tvName);
                mvh.tvTime=convertView.findViewById(R.id.tvTextTime);
                mvh.tvContent=convertView.findViewById(R.id.tvContent);
                convertView.setTag(R.id.tag_first, mvh);
            } else {
                mvh = (MyViewHolder) convertView.getTag(R.id.tag_first);
            }

            try {
                if (msg.getUnReadCount() != 0) {
                    new QBadgeView(context).bindTarget(mvh.ivHead).setBadgeNumber(5).setBadgeBackgroundColor(0xffff4141).setBadgeTextColor(0xffFFFFFF)
                            .stroke(0xffffffff,1,true).setBadgeTextSize(8,true)
                            .setGravityOffset(4,2,true);
                } else {
                    new QBadgeView(context).bindTarget(mvh.ivHead).setBadgeBackgroundColor(0x00000000);
                }

                if (msg.getMessage().getContent().contains("FlowID")) {
                    Follow data = new Gson().fromJson(msg.getMessage().getContent(), Follow.class);
//                    vh.v2.setVisibility(View.VISIBLE);
//                    vh.v1.setVisibility(View.GONE);
//                    if (data.Title != null && !TextUtils.isEmpty(data.Title))
//                        vh.liucheng_content.setText(data.Title);
//                    else
//                        vh.liucheng_content.setText("你收到一个流程");
//                    vh.liucheng_time.setText(StringUtils.friendly_time(msg.getMessage().getCreateTime()));

                    /****************************************************/
                    //                  流程消息点击事件
                    //                    try {
                    //                        final Follow follow = new Gson().fromJson(msg.getMessage().getContent(), Follow.class);
                    //                        vh.liucheng_content.setOnClickListener(new View.OnClickListener() {
                    //                            @Override
                    //                            public void onClick(View v) {
                    //                                Intent intent = new Intent(context, FormDetailActivity.class);
                    //                                intent.putExtra("start_anim", false);
                    //                                intent.putExtra("page_id", String.valueOf(follow.Page));
                    //                                intent.putExtra("row_id", String.valueOf(follow.RID));
                    //                                context.startActivity(intent);
                    //                                if (vh.dot2.getVisibility() == View.VISIBLE) {
                    //                                    intent = new Intent();
                    //                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                    //                                    intent.putExtra("chat", msg.getMessage().SenderID);
                    //                                    context.sendBroadcast(intent);
                    //                                }
                    //                            }
                    //                        });
                    //                    } catch (Exception e) {
                    //                        e.printStackTrace();
                    //                    }
                } else {//TODO:都是走这里
//                    vh.v1.setVisibility(View.VISIBLE);
//                    vh.v2.setVisibility(View.GONE);
                    switch (msg.getMessage().getType()) {
                        case 4://4
                            ChatUtils.spannableEmoticonFilter(mvh.tvContent, msg.getMessage().getContent());
                            break;
                        case 9://9
                            mvh.tvContent.setText("[视频]");
                            break;
//                        case 10://任务创建
//                            vh.content.setText("[任务创建]");
//                            break;
                        case 42://取消任务共享
                            mvh.tvContent.setText("[取消任务共享]");
                            break;
                        case ApiEnum.MessageType.Audio://8
                            mvh.tvContent.setText("[语音]");
                            break;
                        case ApiEnum.MessageType.Image://7
                            mvh.tvContent.setText("[照片]");
                            break;
                        case ApiEnum.MessageType.Attach://6
                            mvh.tvContent.setText("[附件]");
                            break;
                        case ApiEnum.MessageType.Share://10
                            Information data = new Gson().fromJson(msg.getMessage().getContent(), Information.class);
                            String base = "[Talker分享] ";
                            SpannableString spanStr = new SpannableString(base + data.Content);
                            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                            spanStr.setSpan(colorSpan1, 0, base.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            mvh.tvContent.setText(spanStr);
                            break;
//                        case ApiEnum.MessageType.Task://11
//                            vh.content.setText("[任务]");
//                            break;
//                        case ApiEnum.MessageType.Flow://5
//                            vh.content.setText("[流程消息]");
//                            break;
//                        case ApiEnum.MessageType.RevTalker://13
//                            vh.content.setText("你的动态收到一条评论");
//                            break;
                        case ApiEnum.MessageType.Robot://36
//                        case ApiEnum.MessageType.RobotSchedule://37
                            mvh.tvContent.setText("[智能聊天回复]");
                            break;
                        case ApiEnum.MessageType.CHAT_LOCATION://38
                            mvh.tvContent.setText("[位置]");
                            break;
                        case ChatContent.DYNAMIC://39
                            mvh.tvContent.setText("[分享消息]");
                            break;
                        case ChatContent.CHAT_SHARE_LOCATION://41
                            mvh.tvContent.setText("[位置共享]");
                            break;
                        //                        case 0:
                        case ApiEnum.MessageType.PhoneStateMsg://43
                            CallInfo callInfo = new Gson().fromJson(msg.getMessage().getContent(), CallInfo.class);
                            mvh.tvContent.setText(callInfo.type == 1 ? "[语音通话]" : "[视频通话]");
                            break;
//                        default:
//                            if (msg.getMessage().getContent().contains("FlowID")) {
//                                vh.content.setText("[流程消息]");
//                            } else if (msg.getMessage().getContent().contains("m.amap")) {
//                                vh.content.setText(R.string.road_way);
//                            } else {//普通消息
//                                ChatUtils.spannableEmoticonFilter(vh.content, msg.getMessage().getContent());
//                            }
//                            break;
                    }
                    // 群组消息
                    if (m1.get(arg0).getType() == 2) {
                        mvh.ivHead.setImageResource(R.drawable.cm_img_grouphead);
                        String uriGroup = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, msg.getMessage().Group.Image);
                        ImageLoader.getInstance().displayImage(uriGroup, new ImageViewAware(mvh.ivHead), optionsGroup,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        //                        Picasso.with(context)
                        //                                .load(uriGroup)
                        //                                .resize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40))
                        //                                .centerCrop()
                        //                                .config(Bitmap.Config.ALPHA_8)
                        //                                .placeholder(R.drawable.cm_img_grouphead)
                        //                                .error(R.drawable.cm_img_grouphead)
                        //                                .into(vh.head);
                    } else {//TODO:都是走这里
                        mvh.tvName.setText(msg.Receiver.Name);
                        mvh.ivHead.setTvBg(EMWApplication.getIconColor(msg.Receiver.ID), msg.Receiver.Name, 30);
                        String uriUser = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, msg.Receiver.Image);
                        ImageLoader.getInstance().displayImage(uriUser, new ImageViewAware(mvh.ivHead), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        //                        Picasso.with(context)
                        //                                .load(uriUser)
                        //                                .resize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40))
                        //                                .centerCrop()
                        //                                .config(Bitmap.Config.ALPHA_8)
                        //                                .placeholder(R.drawable.cm_img_head)
                        //                                .error(R.drawable.cm_img_head)
                        //                                .into(vh.head);
                    }
                    mvh.tvTime.setText(StringUtils.friendly_time(msg.getMessage().getCreateTime()));
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("unReadNum", msg.getUnReadCount());
                        intent.putExtra("SenderID", msg.getReceiverID());
                        intent.putExtra("type", msg.getType());
                        intent.putExtra("GroupID", msg.getMessage().GroupID);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        intent.putExtra("click_pos_y", location[1]);
                        context.startActivity(intent);
//                        vh.dot1.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    /**
     * Work
     */
    class ChatAdapters extends BaseAdapter {

        private List<ApiEntity.Message> m2 = new ArrayList<>();

        public void setData(List<ApiEntity.Message> m2) {
            this.m2 = m2;
        }

        @Override
        public int getCount() {
            return m2.size() == 0 ? 0 : m2.size();
        }

        @Override
        public Object getItem(int arg0) {
            return m2.get(arg0) == null ? null : m2.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return m2.get(arg0).ReceiverID == 0 ? 0 : m2.get(arg0).ReceiverID;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            final ApiEntity.Message msg = m2.get(arg0);
            final ViewHolder vh;
            if (null == convertView) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_work, null);
                vh.content = (TextView) convertView.findViewById(R.id.task_name);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.name = (TextView) convertView.findViewById(R.id.project_name);
                vh.qunzhumingzi = (TextView) convertView.findViewById(R.id.task_name);
                vh.qun = (TextView) convertView.findViewById(R.id.project_time);
                vh.liucheng_content = (TextView) convertView.findViewById(R.id.jiaqun_name);
                vh.header2 = (CircleImageView) convertView.findViewById(R.id.jiaqun_head);
                vh.ivCalendarNotice = (ImageView) convertView.findViewById(R.id.iv_calendar_notice);
                vh.inforIcon = (IconTextView) convertView.findViewById(R.id.head);
                vh.v1 = convertView.findViewById(R.id.task);//任务
                vh.v2 = convertView.findViewById(R.id.project);//项目
                vh.v3 = convertView.findViewById(R.id.projects);//新建群组
                vh.v4 = convertView.findViewById(R.id.jiaqun);//加入群
                vh.cImgProjectCreate = (CircleImageView) convertView.findViewById(R.id.cimg_project_create);
                vh.header1 = (CircleImageView) convertView.findViewById(R.id.heads);
                vh.qunzhushijian = (TextView) convertView.findViewById(R.id.times);
                vh.qunzhus = (TextView) convertView.findViewById(R.id.project_names);
                vh.dot1 = convertView.findViewById(R.id.view_message_tag_work_jiaqun);
                vh.dot2 = convertView.findViewById(R.id.view_message_tag_work_task);
                vh.dot3 = convertView.findViewById(R.id.view_message_tag_xiangmu);
                vh.dot4 = convertView.findViewById(R.id.view_message_tag_quanzi);
                // 申请加群操作
                vh.tvAgree = (TextView) convertView.findViewById(R.id.jiaqun_sure);
                vh.tvRefuse = (TextView) convertView.findViewById(R.id.jiaqun_cancel);
                vh.tvGroupWhat = (TextView) convertView.findViewById(R.id.tv_works_group_what);
                vh.liucheng_time = (TextView) convertView.findViewById(R.id.jianqun_times);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            try {
                convertView.setTag(R.id.tag_second, 0);
                vh.inforIcon.setVisibility(View.VISIBLE);
                vh.ivCalendarNotice.setVisibility(View.GONE);
                vh.cImgProjectCreate.setVisibility(View.GONE);
                if (msg.IsNew == 1) {
                    vh.dot1.setVisibility(View.VISIBLE);
                    vh.dot2.setVisibility(View.VISIBLE);
                    vh.dot3.setVisibility(View.VISIBLE);
                    vh.dot4.setVisibility(View.VISIBLE);
                } else {
                    vh.dot1.setVisibility(View.INVISIBLE);
                    vh.dot2.setVisibility(View.INVISIBLE);
                    vh.dot3.setVisibility(View.INVISIBLE);
                    vh.dot4.setVisibility(View.INVISIBLE);
                }

                final Information data = new Gson().fromJson(msg.Content, Information.class);

                // 同意加入群组
                vh.tvAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vh.tvAgree.getTag().equals(1)) {
                            GroupMessage newdata = new GroupMessage();
                            newdata.UserID = data.UserID;
                            newdata.State = 1;
                            newdata.GroupID = data.GroupID;
                            newdata.ControlType = data.ControlType;
                            newdata.GroupName = data.GroupName;
                            newdata.UserImage = data.UserImage;
                            newdata.UserName = data.UserName;
                            newdata.Type = data.Type;
                            newdata.DisAgreeText = data.DisAgreeText;
                            API.TalkerAPI.DoJoinGroupUser(data.GroupID, data.UserID, 1, new Gson().toJson(newdata), msg.ID, data.GroupName, new RequestCallback<String>(String.class) {
                                @Override
                                public void onSuccess(String result) {
                                    ToastUtil.showToast(context, "处理成功!", R.drawable.tishi_ico_gougou);
                                    vh.tvAgree.setText("已同意");
                                    vh.tvAgree.setVisibility(View.VISIBLE);
                                    vh.tvRefuse.setVisibility(View.GONE);
                                    Intent intent = new Intent(context, GroupFragment.class);
                                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                                    context.sendBroadcast(intent);
                                    if (vh.dot4.getVisibility() == View.VISIBLE) {
                                        intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                        intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                        intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                        context.sendBroadcast(intent);
                                        vh.dot4.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    ToastUtil.showToast(context, "处理失敗");
                                }
                            });
                        }
                    }
                });

                // 拒绝加入群组
                vh.tvRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupMessage newdata = new GroupMessage();
                        newdata.UserID = data.UserID;
                        newdata.State = 2;
                        newdata.GroupID = data.GroupID;
                        newdata.ControlType = data.ControlType;
                        newdata.GroupName = data.GroupName;
                        newdata.UserImage = data.UserImage;
                        newdata.UserName = data.UserName;
                        newdata.Type = data.Type;
                        newdata.DisAgreeText = data.DisAgreeText;
                        API.TalkerAPI.DoJoinGroupUser(data.GroupID, data.UserID, 2, new Gson().toJson(newdata), msg.ID, data.GroupName, new RequestCallback<String>(String.class) {
                            @Override
                            public void onSuccess(String result) {
                                vh.tvAgree.setText("已拒绝");
                                vh.tvAgree.setVisibility(View.VISIBLE);
                                vh.tvRefuse.setVisibility(View.GONE);
                                if (vh.dot4.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot4.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                ToastUtil.showToast(context, "处理失败。");
                            }
                        });
                    }
                });

                vh.header2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (msg.SenderID != 0) {
                            if (EMWApplication.personMap != null && EMWApplication.personMap.get(msg.SenderID) != null) {
                                Intent intent = new Intent(context, PersonInfoActivity.class);
                                intent.putExtra("UserInfo", EMWApplication.personMap.get(msg.SenderID));
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                context.startActivity(intent);
                            }
                        }
                    }
                });

                switch (msg.Type) {
//                    case 25://拒绝加入群组通知
//                        vh.v1.setVisibility(View.GONE);
//                        vh.v2.setVisibility(View.GONE);
//                        vh.v3.setVisibility(View.GONE);
//                        vh.v4.setVisibility(View.VISIBLE);
//                        vh.tvAgree.setVisibility(View.GONE);
//                        vh.tvRefuse.setVisibility(View.GONE);
//                        vh.liucheng_time.setVisibility(View.VISIBLE);
//                        vh.header2.setTvBg(msg.Sender.ID, msg.Sender.Name, 30);
//                        String uri = String.format(Const.DOWN_ICON_URL,
//                                PrefsUtil.readUserInfo().CompanyCode, msg.Sender.Image);
//                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.header2), options,
//                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
//                        vh.tvGroupWhat.setText(data.State == 1 ? "同意您加入" : "拒绝您加入");
//                        vh.liucheng_content.setText(data.GroupName);
//                        vh.liucheng_time.setText(StringUtils.friendly_time(msg.CreateTime));
//                        convertView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(context, GroupInActivity.class);
//                                intent.putExtra("start_anim", false);
//                                intent.putExtra("GroupID", data.GroupID);
//                                int[] location = new int[2];
//                                v.getLocationInWindow(location);
//                                intent.putExtra("click_pos_y", location[1]);
//                                context.startActivity(intent);
//                                if (vh.dot1.getVisibility() == View.VISIBLE) {
//                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
//                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
//                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
//                                    context.sendBroadcast(intent);
//                                    vh.dot1.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        });
//                        break;
//                    case 19://申请加入群组通知
//                        vh.v1.setVisibility(View.GONE);
//                        vh.v2.setVisibility(View.GONE);
//                        vh.v3.setVisibility(View.GONE);
//                        vh.v4.setVisibility(View.VISIBLE);
//                        vh.tvAgree.setVisibility(View.VISIBLE);
//                        vh.liucheng_time.setVisibility(View.GONE);
//                        if (data.State == 0) {
//                            vh.tvAgree.setText("同意");
//                            vh.tvRefuse.setVisibility(View.VISIBLE);
//                            vh.tvAgree.setTag(1);
//                        } else {
//                            vh.tvAgree.setText(data.State == 1 ? "已同意" : "已拒绝");
//                            vh.tvRefuse.setVisibility(View.GONE);
//                            vh.tvAgree.setTag(0);
//                        }
//                        vh.tvGroupWhat.setText("申请加入: ");
//                        vh.liucheng_content.setText(data.GroupName);
//                        vh.header2.setTvBg(msg.Sender.ID, msg.Sender.Name, 30);
//                        String uris = String.format(Const.DOWN_ICON_URL,
//                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
//                        ImageLoader.getInstance().displayImage(uris, new ImageViewAware(vh.header2), options,
//                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
//                        convertView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(context, GroupInActivity.class);
//                                intent.putExtra("GroupID", data.GroupID);
//                                intent.putExtra("start_anim", false);
//                                int[] location = new int[2];
//                                v.getLocationInWindow(location);
//                                intent.putExtra("click_pos_y", location[1]);
//                                context.startActivity(intent);
//                                if (vh.dot1.getVisibility() == View.VISIBLE) {
//                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
//                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
//                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
//                                    context.sendBroadcast(intent);
//                                    vh.dot1.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        });
//                        break;
                    case 16://任务分配
                        vh.v1.setVisibility(View.VISIBLE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        Log.d("zrjtsss", msg.Content);
                        vh.qunzhumingzi.setText(data.Name);
                        vh.qun.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, TaskDetailActivity.class);
                                //                                intent.putExtra("sendId", msg.SenderID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                intent.putExtra(TaskDetailActivity.TASK_ID, data.TaskID);
                                context.startActivity(intent);
                                if (vh.dot2.getVisibility() == View.VISIBLE) {
                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot2.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        break;
                    case 17://新建项目通知
                        vh.v2.setVisibility(View.VISIBLE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.inforIcon.setVisibility(View.GONE);
                        vh.ivCalendarNotice.setVisibility(View.GONE);
                        vh.cImgProjectCreate.setVisibility(View.VISIBLE);
                        String b1 = "创建了项目组: ";
                        String b2 = "并关联了你";
                        SpannableString sp = new SpannableString(b1 + data.ProjectName + b2);
                        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                        sp.setSpan(colorSpan1, b1.length(), data.ProjectName.length() + b1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        Log.d("zrjt", sp.toString());
                        vh.name.setText(sp);
                        vh.cImgProjectCreate.setTvBg(EMWApplication.getIconColor(data.UserID), data.UserName, 30);
                        String urissProject = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(urissProject, new ImageViewAware(vh.cImgProjectCreate), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                                if (vh.dot3.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot3.setVisibility(View.INVISIBLE);
                                }
                                mSlidingMenu.toggle();
                            }
                        });
                        break;

                    case 5://流程审批
                        vh.v2.setVisibility(View.VISIBLE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.inforIcon.setVisibility(View.GONE);
                        vh.ivCalendarNotice.setVisibility(View.GONE);
                        vh.cImgProjectCreate.setVisibility(View.VISIBLE);
//                        String b1 = "创建了项目组: ";
//                        String b2 = "并关联了你";
//                        SpannableString sp = new SpannableString(b1 + data.ProjectName + b2);
//                        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
//                        sp.setSpan(colorSpan1, b1.length(), data.ProjectName.length() + b1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                        Log.d("zrjt", sp.toString());
                        vh.name.setText("[审批流程]");
                        vh.cImgProjectCreate.setTvBg(EMWApplication.getIconColor(data.UserID), data.UserName, 30);
                        String urissProject1 = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(urissProject1, new ImageViewAware(vh.cImgProjectCreate), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                                if (vh.dot3.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot3.setVisibility(View.INVISIBLE);
                                }
                                mSlidingMenu.toggle();
                            }
                        });
                        break;
                    case 18://加入新建项目组通知
                        vh.v3.setVisibility(View.VISIBLE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        String base1 = "创建了圈子：";
                        String base2 = "并添加了你";
                        SpannableString spanStr = new SpannableString(base1 + data.GroupName + base2);
                        ForegroundColorSpan color = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                        spanStr.setSpan(color, base1.length(), base1.length() + data.GroupName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        Log.d("zrjt", spanStr.toString());
                        //                        String[] strss = spanStr.toString().split("\\|\\|");
                        //                        if (strss.length == 2) {
                        vh.qunzhus.setText(spanStr);
                        //                        }
                        vh.header1.setTvBg(EMWApplication.getIconColor(data.UserID), data.UserName, 30);
                        String uriss = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(uriss, new ImageViewAware(vh.header1), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.qunzhushijian.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, GroupInActivity.class);
                                intent.putExtra("GroupID", data.GroupID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                context.startActivity(intent);
                                if (vh.dot4.getVisibility() == View.VISIBLE) {
                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot4.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        break;
                }
            } catch (JsonParseException e) {
                e.printStackTrace();
                vh.v2.setVisibility(View.VISIBLE);
                vh.v3.setVisibility(View.GONE);
                vh.v1.setVisibility(View.GONE);
                vh.v4.setVisibility(View.GONE);
                if (msg != null && msg != null) {
                    final String[] strss = msg.Content.toString().split("\\|\\|");
                    Log.d("zrjt", msg.Content + "异常");
                    if (strss.length == 2) {
                        vh.name.setText(strss[0]);
                    } else {
                        vh.name.setText(msg.Content);
                    }
                    /**
                     * 日程提醒
                     */
                    if (msg.Type == ApiEnum.MessageType.CallSchedule) {
                        vh.inforIcon.setVisibility(View.GONE);
                        vh.cImgProjectCreate.setVisibility(View.GONE);
                        vh.ivCalendarNotice.setVisibility(View.VISIBLE);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, CalendarInfoActivity2.class);
                                intent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(strss[1]));
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                context.startActivity(intent);
                                if (vh.dot3.getVisibility() == View.VISIBLE) {
                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot3.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        String images = "";
                        String name = "";
                        int id = 0;
                        if (EMWApplication.personMap.size() > 0) {
                            if (EMWApplication.personMap.get(msg.ReceiverID) != null) {
                                ApiEntity.UserInfo user = EMWApplication.personMap.get(m2.get(
                                        arg0).ReceiverID);
                                if (user != null) {
                                    id = user.ID;
                                    name = user.Name;
                                    images = user.Image;
                                }
                            }
                        }
                        vh.header2.setTvBg(EMWApplication.getIconColor(id), name, 30);
                        String uris = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, images);
                        ImageLoader.getInstance().displayImage(uris, new ImageViewAware(vh.header2), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, TaskDetailActivity.class);
                                intent.putExtra("sendId", msg.SenderID);
                                intent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(strss[1]));
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                context.startActivity(intent);
                                if (vh.dot3.getVisibility() == View.VISIBLE) {
                                    intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
                                    context.sendBroadcast(intent);
                                    vh.dot3.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }

            } catch (Exception e) {
                Log.d("zrjt", msg.Content + "异常");
            }
            return convertView;
        }
    }

    /**
     * information
     */
    class ChatAdapterss extends BaseAdapter {

        private List<ApiEntity.Message> m3 = new ArrayList<>();

        public void setData(List<ApiEntity.Message> m3) {
            this.m3 = m3;
        }

        @Override
        public int getCount() {
            return m3.size() == 0 ? 0 : m3.size();
        }

        @Override
        public Object getItem(int arg0) {
            return m3.get(arg0) == null ? null : m3.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return m3.get(arg0).ReceiverID == 0 ? 0 : m3.get(arg0).ReceiverID;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            final ApiEntity.Message msg = m3.get(arg0);
            final ViewHolder vh;
            if (null == convertView) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_information, null);
                vh.content = (TextView) convertView.findViewById(R.id.dongtainierong);
                vh.time = (TextView) convertView.findViewById(R.id.times);
                vh.name = (TextView) convertView.findViewById(R.id.dongtaiming);
                vh.header2 = (CircleImageView) convertView.findViewById(R.id.heads);
                vh.head = (CircleImageView) convertView.findViewById(R.id.head);
                vh.header1 = (CircleImageView) convertView.findViewById(R.id.add_friend_head);  //好友申请头像
                vh.tvAgree = (TextView) convertView.findViewById(R.id.add_friend_sure); //同意加好友申请
                vh.tvRefuse = (TextView) convertView.findViewById(R.id.add_friend_cancel);     //拒绝加好友申请
                vh.v1 = convertView.findViewById(R.id.dongtai);//收藏动态
                vh.v2 = convertView.findViewById(R.id.dongtai_pl); //动态评论
                vh.v3 = convertView.findViewById(R.id.ll_apply_friend); //申请添加好友
                vh.v4 = convertView.findViewById(R.id.bace);//关注我
                vh.v5 = convertView.findViewById(R.id.ll_exit_group);   //移除圈子通知
                vh.dot4 = convertView.findViewById(R.id.view_message_tag_group_exit);
                vh.exitGroupContent = (TextView) convertView.findViewById(R.id.tv_message_info_exit_group);
                vh.exitGroupTime = (TextView) convertView.findViewById(R.id.tv_time_exit_group);
                vh.enjoy = (TextView) convertView.findViewById(R.id.tv_message_enjoy);

                // 动态评论
                vh.dot3 = convertView.findViewById(R.id.view_message_tag_talker_dongtai);
                vh.talkerDtName = (TextView) convertView.findViewById(R.id.dongtaiming_talker);
                vh.qunzhumingzi = (TextView) convertView.findViewById(R.id.times_pl);
                vh.qunzhushijian = (TextView) convertView.findViewById(R.id.dongtainierong_pl);
                vh.liucheng_content = (TextView) convertView.findViewById(R.id.content);
                vh.dot1 = convertView.findViewById(R.id.view_message_tag_information_dongtai);
                vh.dot2 = convertView.findViewById(R.id.view_message_tag_information_concern);
                //添加好友
                vh.dot5 = convertView.findViewById(R.id.view_message_tag_information_add_friend);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            try {
                if (msg.IsNew == 1) {
                    vh.dot1.setVisibility(View.VISIBLE);
                    vh.dot2.setVisibility(View.VISIBLE);
                    vh.dot3.setVisibility(View.VISIBLE);
                    vh.dot4.setVisibility(View.VISIBLE);
                    vh.dot5.setVisibility(View.VISIBLE);
                } else {
                    vh.dot1.setVisibility(View.INVISIBLE);
                    vh.dot2.setVisibility(View.INVISIBLE);
                    vh.dot3.setVisibility(View.INVISIBLE);
                    vh.dot4.setVisibility(View.INVISIBLE);
                    vh.dot5.setVisibility(View.INVISIBLE);
                }

                if (msg.Content.contains("ControlType")) {
                    vh.talkerDtName.setText(PrefsUtil.readUserInfo().Name + ": "); //动态当前用户发布取其姓名
                    Dynamic data = new Gson().fromJson(msg.Content, Dynamic.class);
                    if (data.ControlType == 0) {    //动态评论
                        vh.v2.setVisibility(View.GONE); //显示
                        vh.v1.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                    }
                    //                    if (data.ControlType == 1) {     //动态收藏
                    //                        vh.v1.setVisibility(View.GONE);
                    //                        vh.v2.setVisibility(View.GONE);
                    //                        vh.v4.setVisibility(View.GONE);
                    //                        vh.v5.setVisibility(View.GONE);
                    //                        if (mEnjoyHeadMap.get(data.TopID) != null) {
                    //                            if (data.ControlValue == 0) {
                    //                                mEnjoyHeadMap.get(data.TopID).add(data.UserImage);
                    //                            } else if (data.ControlValue == 1) {
                    //                                mEnjoyHeadMap.get(data.TopID).add(data.UserImage);
                    //                            }
                    //                        } else {
                    //                            List<String> mEnjoyHeads = new ArrayList<>();
                    //                            mEnjoyHeads.add(data.UserImage);
                    //                            mEnjoyHeadMap.put(data.TopID, mEnjoyHeads);
                    //                        }
                    //                        if (mEnjoyHeadMap.get(data.TopID).size() > 0) {
                    //                            vh.v1.setVisibility(View.VISIBLE);
                    //                            vh.mEnjoyListLayout.removeAllViews();
                    //                            for (int i = 0; i < mEnjoyHeadMap.get(data.TopID).size(); i++) {
                    //                                if (i > 5)
                    //                                    continue;
                    //                                CircleImageView circleImageView = new CircleImageView(context);
                    //                                String uri;
                    //                                if (i != 0) {
                    //                                    params.leftMargin = DisplayUtil.dip2px(context, 10);
                    //                                }
                    //                                uri = String.format(Const.DOWN_ICON_URL,
                    //                                        PrefsUtil.readUserInfo().CompanyCode, mEnjoyHeadMap.get(data.TopID).get(i));
                    //                                ImageLoader.getInstance().displayImage(uri, new ImageViewAware(circleImageView), options,
                    //                                        new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                    //                                vh.mEnjoyListLayout.addView(circleImageView, params);
                    //                            }
                    //                        } else {
                    //                            vh.v1.setVisibility(View.GONE);
                    //                        }
                    //                    }
                }

                switch (msg.Type) {
                    case ApiEnum.MessageType.LeaveGroup:
                        vh.v5.setVisibility(View.VISIBLE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.exitGroupTime.setText(StringUtils.friendly_time(msg.CreateTime));
                        String[] strss = msg.Content.toString().split("\\|\\|");
                        if (strss.length == 2) {
                            vh.exitGroupContent.setText(strss[0]);
                            msg.GroupID = Integer.valueOf(strss[1]);
                        } else {
                            vh.exitGroupContent.setText(msg.Content);
                        }
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, GroupInActivity.class);
                                intent.putExtra("GroupID", msg.GroupID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);

                                if (vh.dot4.getVisibility() == View.VISIBLE) {
                                    intent.putExtra("msg_id", msg.ID);
                                    vh.dot4.setVisibility(View.INVISIBLE);
                                }
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case ApiEnum.MessageType.TellFromTalker: //向你发来一个分享
                        String str = msg.Content.replaceAll("\u0027", "\"");
                        final Dynamic data = new Gson().fromJson(str, Dynamic.class);
                        vh.v4.setVisibility(View.VISIBLE);
                        vh.v5.setVisibility(View.GONE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        String base2 = "向你发来一个分享 ";
                        SpannableString spanStr2 = new SpannableString(base2 + data.Content);
                        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                        spanStr2.setSpan(colorSpan2, base2.length(), spanStr2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        vh.liucheng_content.setText(spanStr2);
                        String uriss2;
                        if (msg.Sender == null || msg.Sender.Image == null || TextUtils.isEmpty(msg.Sender.Image))
                            uriss2 = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode, "test");
                        else
                            uriss2 = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode, msg.Sender.Image);
                        vh.head.setTvBg(EMWApplication.getIconColor(msg.Sender.ID), msg.Sender.Name, 30);
                        ImageLoader.getInstance().displayImage(uriss2, new ImageViewAware(vh.head), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, DynamicDetailActivity.class);
                                intent.putExtra("note_id", data.TalkerID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);

                                if (vh.dot2.getVisibility() == View.VISIBLE) {
                                    intent.putExtra("msg_id", msg.ID);
                                    vh.dot2.setVisibility(View.INVISIBLE);
                                }
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case ApiEnum.MessageType.EnjoyTalker:
                        String strs = msg.Content.replaceAll("\u0027", "\"");
                        final DynamicEnjoy enjoy = new Gson().fromJson(strs, DynamicEnjoy.class);
                        vh.v1.setVisibility(View.VISIBLE); //显示
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        String uriEnjoy = "";
                        ChatUtils.spannableEmoticonFilter(vh.content, enjoy.OldContent);
                        if (enjoy.ControlValue == 0) {
                            String b1 = enjoy.UserName;
                            String b2 = "收藏了你的动态";
                            SpannableString sp = new SpannableString(b1 + " " + b2);
                            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                            sp.setSpan(colorSpan1, 0, b1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            vh.enjoy.setText(sp);
                        } else {
                            String b1 = enjoy.UserName;
                            String b2 = "取消收藏了你的动态";
                            SpannableString sp = new SpannableString(b1 + " " + b2);
                            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.dynamicreply_name_text));
                            sp.setSpan(colorSpan1, 0, b1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            vh.enjoy.setText(sp);
                        }
                        vh.name.setText(PrefsUtil.readUserInfo().Name + ": ");
                        if (enjoy.UserImage != null) {
                            uriEnjoy = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode, enjoy.UserImage);
                        }
                        vh.header2.setTvBg(EMWApplication.getIconColor(0), enjoy.UserName, 30);
                        ImageLoader.getInstance().displayImage(uriEnjoy, new ImageViewAware(vh.header2), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        vh.time.setText(msg.CreateTime + "");
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, DynamicDetailActivity.class);
                                intent.putExtra("note_id", enjoy.TopID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                if (vh.dot1.getVisibility() == View.VISIBLE) {
                                    intent.putExtra("msg_id", msg.ID);
                                    vh.dot1.setVisibility(View.INVISIBLE);
                                }
                                context.startActivity(intent);
                            }
                        });
                        break;
                    //                    case 2://关注我
                    case ApiEnum.MessageType.FollowMe:
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.VISIBLE);
                        //                        String imagess = "";
                        //                        String base1 = "";
                        //                        if (data.UserImage != null) {
                        //                            imagess = String.format(Const.DOWN_ICON_URL,
                        //                                    PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        //                        }
                        //                        if (data.ControlValue == 0)
                        //                            base1 = "关注了你。";
                        //                        else
                        //                            base1 = "取消关注了你。";
                        //                        vh.liucheng_content.setText(base1);
                        //                        ImageLoader.getInstance().displayImage(imagess, vh.head, options);
                        //                        vh.qunzhushijian.setText(StringUtils.friendly_time(msg.CreateTime));
                        break;
                    case ApiEnum.MessageType.CallUserApply:
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.VISIBLE);
                        vh.header1.setTvBg(0, msg.Sender.Name, 30);
                        vh.header1.setTvBg(EMWApplication.getIconColor(msg.Sender.ID), msg.Sender.Name, 30);
                        String applyerImgHead = String.format(Const.DOWN_ICON_URL,
                                TextUtils.isEmpty(msg.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : msg.CompanyCode, msg.Sender.Image);
                        ImageLoader.getInstance().displayImage(applyerImgHead, new ImageViewAware(vh.header1), options,
                                new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
                        //                        Picasso.with(context)
                        //                                .load(applyerImgHead)
                        //                                .resize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40))
                        //                                .centerCrop()
                        //                                .config(Bitmap.Config.ALPHA_8)
                        //                                .placeholder(R.drawable.cm_img_head)
                        //                                .error(R.drawable.cm_img_head)
                        //                                .into(vh.header1);
                        //同意
                        vh.tvAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doApplyFriend(msg.SenderID, 1);
                                if (vh.dot5.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 2);
                                    context.sendBroadcast(intent);
                                    vh.dot5.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        //拒绝
                        vh.tvRefuse.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doApplyFriend(msg.SenderID, 2);
                                if (vh.dot5.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
                                    intent.putExtra(MainActivity.MESSAGE_ID, msg.ID);
                                    intent.putExtra(MainActivity.MESSAGE_TYPE, 2);
                                    context.sendBroadcast(intent);
                                    vh.dot5.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, PersonInfoActivity.class);
                                intent.putExtra("UserInfo", EMWApplication.personMap.get(msg.SenderID));
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                context.startActivity(intent);
                            }
                        });
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MainActivity", "notice:" + new Gson().toJson(msg));
            }
            return convertView;
        }
    }


    static class ViewHolder {
        TextView tvAgree, tvRefuse;
        CircleImageView head;
        TextView enjoy;
        IconTextView inforIcon;
        TextView name, content, time, qun, qunzhumingzi, qunzhushijian, liucheng_content, liucheng_time, qunzhus, talkerDtName, exitGroupContent, exitGroupTime, tvGroupWhat;
        CircleImageView header1, header2, cImgProjectCreate;
        ImageView ivCalendarNotice;
        AnimationSet set;
        LinearLayout mEnjoyListLayout;
        View v1, v2, v3, v4, v5, dot1, dot2, dot3, dot4, dot5;
    }

    static class MyViewHolder {
       TextView tvName;
       TextView tvTime;
       TextView tvContent;
        CircleImageView ivHead;
    }

    private void doApplyFriend(final int conId, int state) {
        API.UserPubAPI.DoPubConApply(conId, state, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (EMWApplication.personMap != null && EMWApplication.personMap.get(conId) != null) {
                        EMWApplication.personMap.get(conId).IsFollow = true;
                    }
                    Toast.makeText(context, "处理成功!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, "处理失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AnimationSet anmi() {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scale = new ScaleAnimation(0.7f, 1, 0.7f, 1, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scale.setDuration(800);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(300);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }

    // 获取聊天消息
    public void getChatList(final boolean isFinish) {
        API.Message.GetChatRecords(new RequestCallback<HistoryMessage>(
                HistoryMessage.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(context, "加载失败，请重试...");
                mPtrFrameLayout.refreshComplete();
            }

            @Override
            public void onParseSuccess(final List<HistoryMessage> respList) {
                if (respList != null) {
                    m1.clear();
                    for (HistoryMessage historyMessage : respList) {
                        if (historyMessage.getMessage() != null) {
                            m1.add(historyMessage);
                        }
//                                        m1.add(historyMessage);
                    }
                    //mHandler.sendEmptyMessage(111); // Talker
                    adapterChat.setData(m1);
                    adapterChat.notifyDataSetChanged();
                    mLineTalkerBottom.setVisibility(m1.size() > 0 ? View.VISIBLE : View.GONE);
                    if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                        scrollView.setVisibility(View.VISIBLE);
                        mBlankView.setVisibility(View.GONE);
                    } else {
                        mBlankView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                }
                if (!isFinish)
                    getMessageWork(false);
            }
        });
    }

    // 获取工作消息
    public void getMessageWork(final boolean isFinish) {
        API.Message.GetUserWorkMessages(1, 20, new RequestCallback<ApiEntity.Message>(ApiEntity.Message.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                Log.d("zrjt", "WorkError");
            }

            @Override
            public void onParseSuccess(final List<ApiEntity.Message> respList) {
                if (m2 != null) {
                    m2.clear();
                    m2.addAll(respList);
                    //                mHandler.sendEmptyMessage(112);  //work
                    adapterWork.setData(m2);
                    adapterWork.notifyDataSetChanged();
                    mLineWorkBottom.setVisibility(m2.size() > 0 ? View.VISIBLE : View.GONE);
                    if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                        scrollView.setVisibility(View.VISIBLE);
                        mBlankView.setVisibility(View.GONE);
                    } else {
                        mBlankView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                }
                if (!isFinish)
                    getMessageNotice(false);
            }
        });
    }

    // 获取通知消息
    public void getMessageNotice(final boolean isFinish) {
        API.Message.GetUserNoticeMessages(1, 20, new RequestCallback<ApiEntity.Message>(ApiEntity.Message.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                Log.d("zrjt", "NoticeError");
            }

            @Override
            public void onParseSuccess(final List<ApiEntity.Message> respList) {
                //                if (!isFinish)
                //                    getEnjoyNoteList();
                mPtrFrameLayout.refreshComplete();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (respList != null) {
                            m3.clear();
                            comments.clear();
                            noticeComments = new ArrayList<>();//评论消息的集合
                            for (int i = 0; i < respList.size(); i++) {
                                ApiEntity.Message message = respList.get(i);
                                /**
                                 * 23新发布Talker通知
                                 * 30离开群组通知
                                 * 14talker回复通知
                                 * 35 CallUserApply
                                 * 需要改：
                                 * 11talker分享
                                 * 12群组邀请
                                 * 14talker回复通知
                                 * 13talker收藏
                                 */
//                                if (message.Type == 23 || message.Type == 30 || message.Type == 14 || message.Type == 35) {//原来
                                if (message.Type == 23 || message.Type == 30 || message.Type == 14 || message.Type == 35|| message.Type == 15) {//测试15关注通知
                                    m3.add(message);
                                }
                                if (message.Type == 13) {    //评论
                                    Dynamic datas = new Gson().fromJson(message.Content, Dynamic.class);
                                    message.topIdOut = datas.TopID;
                                    comments.add(message);
                                }
                            }
                            // 评论消息
                            Collections.sort(comments);
                            topId = 0;
                            for (int i = 0; i < comments.size(); i++) {
                                ApiEntity.Message message = comments.get(i);
                                String content = message.Content;
                                Dynamic data = new Gson().fromJson(content, Dynamic.class);
                                if (topId == data.TopID) {
                                    if (data.NewContent != null && !TextUtils.isEmpty(data.NewContent)) {
                                        NoticeComment.CommentInfo commentInfo = new NoticeComment.CommentInfo();
                                        commentInfo.newContent = data.NewContent;
                                        commentInfo.urls = data.UserImage;
                                        commentInfo.isNew = message.IsNew;
                                        commentInfo.userID = data.UserID;
                                        commentInfo.userName = data.UserName;
                                        if (noticeComments != null && noticeComments.size() > 0)
                                            noticeComments.get(noticeComments.size() - 1).commentInfos.add(commentInfo);
                                    }
                                } else {
                                    topId = data.TopID;
                                    NoticeComment noticeComment = new NoticeComment();
                                    noticeComment.commentInfos = new ArrayList<>();
                                    if (data.NewContent != null && !TextUtils.isEmpty(data.NewContent)) {
                                        NoticeComment.CommentInfo commentInfo = new NoticeComment.CommentInfo();
                                        commentInfo.newContent = data.NewContent;
                                        commentInfo.urls = data.UserImage;
                                        commentInfo.isNew = message.IsNew;
                                        commentInfo.userID = data.UserID;
                                        commentInfo.userName = data.UserName;
                                        noticeComment.commentInfos.add(commentInfo);
                                    }
                                    noticeComment.ID = data.ID;
                                    noticeComment.topId = data.TopID;
                                    noticeComment.userId = respList.get(i).UserID;
                                    noticeComment.isNew = respList.get(i).IsNew;
                                    noticeComment.oldContent = data.OldContent;
                                    noticeComment.sendID = respList.get(i).SenderID;
                                    noticeComments.add(noticeComment);
                                }
                            }
                            mHandler.sendEmptyMessage(113);     //notice
                            if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                                scrollView.setVisibility(View.VISIBLE);
                                mBlankView.setVisibility(View.GONE);
                            } else {
                                mBlankView.setVisibility(View.VISIBLE);
                                scrollView.setVisibility(View.GONE);
                            }
                        }
                    }
                }).start();
            }
        });
    }

    //    private void getEnjoyNoteList() {
    //        API.TalkerAPI.GetEnjoyNoteList(5, 1, new RequestCallback<UserNote>(UserNote.class) {
    //            @Override
    //            public void onError(Throwable throwable, boolean b) {
    //                Log.d("zrjt", "NoticeEnjoyError");
    //                mPtrFrameLayout.refreshComplete();
    //            }
    //
    //            @Override
    //            public void onParseSuccess(List<UserNote> respList) {
    //                mPtrFrameLayout.refreshComplete();
    //                mEnjoyList.clear();
    //                mEnjoyList.addAll(respList);
    //                enjoyAdapter.setData((ArrayList<UserNote>) mEnjoyList);
    //                enjoyAdapter.notifyDataSetChanged();
    //            }
    //        });
    //    }

    /**
     * 动态评论adapter
     */
    class CommentAdapter extends BaseAdapter {

        private List<NoticeComment> mDataList;
        private DisplayImageOptions options;

        public CommentAdapter() {
            this.mDataList = new ArrayList<>();
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                    .bitmapConfig(Bitmap.Config.ALPHA_8)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .build(); // 创建配置过得DisplayImageOption对象
        }

        public void setData(ArrayList<NoticeComment> mDataList) {
            this.mDataList = mDataList;
        }

        @Override
        public int getCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mDataList.get(position).topId;
        }

        @Override
        public View getView(final int position, View contentView, ViewGroup parent) {
            final CommentAdapter.ViewHolder vh;
            if (contentView == null) {
                vh = new CommentAdapter.ViewHolder();
                contentView = LayoutInflater.from(context).inflate(R.layout.listitem_info_comment, null);
                vh.tagView = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
                vh.nameTv = (TextView) contentView.findViewById(R.id.tv_infocomment_name);
                vh.timeTv = (TextView) contentView.findViewById(R.id.tv_infocomment_time);
                vh.contentTv = (TextView) contentView.findViewById(R.id.tv_infocomment_content);
                vh.listView = (MyListView) contentView.findViewById(R.id.lv_comment);
                vh.view = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
                contentView.setTag(R.id.tag_first, vh);
            } else {
                vh = (CommentAdapter.ViewHolder) contentView.getTag(R.id.tag_first);
            }

            final NoticeComment un = mDataList.get(position);
            if (EMWApplication.personMap.get(un.userId) != null)
                vh.nameTv.setText(EMWApplication.personMap.get(un.userId).Name + ": ");
            //            String createTime = un.CreateTime;
            //            vh.timeTv.setText(StringUtils.friendly_time(createTime));
            String content = "";
            if (!TextUtils.isEmpty(un.oldContent)) {
                content = Html.fromHtml(un.oldContent).toString();
            }
            vh.contentTv.setText(content);

            for (int i = 0; i < un.commentInfos.size(); i++) {
                if (un.commentInfos.get(i).isNew == 1) {
                    vh.view.setVisibility(View.VISIBLE);
                    break;
                } else {
                    vh.view.setVisibility(View.INVISIBLE);
                }
            }

            NoticeCommentAdapter noticeCommentAdapter = new NoticeCommentAdapter(context, un.commentInfos);
            vh.listView.setAdapter(noticeCommentAdapter);

            contentView.setTag(R.id.tag_second, un.topId);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DynamicDetailActivity.class);
                    intent.putExtra("note_id", Integer.valueOf(v.getTag(R.id.tag_second).toString()));
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    intent.putExtra("msg_id", un.ID);
                    if (vh.view.getVisibility() == View.VISIBLE) {
                        //                         TODO 优化
                        //                        Intent intent2 = new Intent(MainActivity.ACTION_REFRESH_COUNT);
                        //                        intent2.putExtra("information", un.sendID);
                        //                        context.sendBroadcast(intent2);
                        clearMessageCounts(2);
                        //                        RemoveAllMessageByID(un.ID, 2);
                    }
                    context.startActivity(intent);
                }
            });
            return contentView;
        }

        class ViewHolder {
            View tagView;
            TextView nameTv;
            TextView timeTv;
            TextView contentTv;
            MyListView listView;
            View view;
        }
    }

    //    /**
    //     * 动态收藏adapter
    //     */
    //    class EnjoyAdapter extends BaseAdapter {
    //
    //        private ArrayList<UserNote> mDataList;
    //        private DisplayImageOptions options;
    //
    //        public EnjoyAdapter() {
    //            this.mDataList = new ArrayList<>();
    //
    //            options = new DisplayImageOptions.Builder()
    //                    .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
    //                    .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
    //                    .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
    //                    .bitmapConfig(Bitmap.Config.ALPHA_8)
    //                    .imageScaleType(ImageScaleType.EXACTLY)
    //                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
    //                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
    //                    .build(); // 创建配置过得DisplayImageOption对象
    //        }
    //
    //        public void setData(ArrayList<UserNote> dataList) {
    //            this.mDataList = dataList;
    //        }
    //
    //        @Override
    //        public int getCount() {
    //            return mDataList == null ? 0 : mDataList.size();
    //        }
    //
    //        @Override
    //        public Object getItem(int position) {
    //            return mDataList.get(position);
    //        }
    //
    //        @Override
    //        public long getItemId(int position) {
    //            return mDataList.get(position).ID;
    //        }
    //
    //        @Override
    //        public View getView(final int position, View contentView, ViewGroup parent) {
    //            EnjoyAdapter.ViewHolder vh;
    //            if (contentView == null) {
    //                vh = new EnjoyAdapter.ViewHolder();
    //                contentView = LayoutInflater.from(context).inflate(R.layout.listitem_info_enjoy, null);
    //                vh.tagView = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
    //                vh.nameTv = (TextView) contentView.findViewById(R.id.tv_infocomment_name);
    //                vh.timeTv = (TextView) contentView.findViewById(R.id.tv_infocomment_time);
    //                vh.contentTv = (TextView) contentView.findViewById(R.id.tv_infocomment_content);
    //                vh.headLayout = (LinearLayout) contentView.findViewById(R.id.ll_infoenjoy_head);
    //                contentView.setTag(R.id.tag_first, vh);
    //            } else {
    //                vh = (EnjoyAdapter.ViewHolder) contentView.getTag(R.id.tag_first);
    //            }
    //            final UserNote un = mDataList.get(position);
    //            vh.nameTv.setText(PrefsUtil.readUserInfo().Name + ": ");
    //            vh.headLayout.removeAllViews();
    ////            String createTime = un.CreateTime;
    ////            vh.timeTv.setText(StringUtils.friendly_time(createTime));
    //            String content = "";
    //            if (!TextUtils.isEmpty(un.Content)) {
    //                content = Html.fromHtml(un.Content).toString();
    //            }
    //            vh.contentTv.setText(content);
    //
    //            if (un.EnjoyList != null && un.EnjoyList.size() > 0) {
    //                contentView.setVisibility(View.VISIBLE);
    //                for (int i = 0; i < un.EnjoyList.size(); i++) {
    //                    if (un.EnjoyList.size() - i - 5 > 0) //超过5个只需显示最后5个
    //                        continue;
    //                    ApiEntity.TalkerUserInfo user = un.EnjoyList.get(i);
    //                    final CircleImageView imgview = new CircleImageView(context);
    //                    imgview.setBorderWidth(DisplayUtil.dip2px(context, 1));
    //                    imgview.setBorderColorResource(R.color.cm_headimg_border);
    //                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30));
    //                    String url = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, user.Image);
    //                    ImageLoader.getInstance().displayImage(url, new ImageViewAware(imgview), options, new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
    //                    if (vh.headLayout.getChildCount() != 0)
    //                        params.leftMargin = DisplayUtil.dip2px(context, 5);
    //                    vh.headLayout.addView(imgview, params);
    //                }
    //            } else {
    //                contentView.setVisibility(View.GONE);
    //            }
    //            contentView.setTag(R.id.tag_second, un.ID);
    //            contentView.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    Intent intent = new Intent(context, DynamicDetailActivity.class);
    //                    intent.putExtra("note_id", Integer.valueOf(v.getTag(R.id.tag_second).toString()));
    //                    intent.putExtra("start_anim", false);
    //                    int[] location = new int[2];
    //                    v.getLocationInWindow(location);
    //                    intent.putExtra("click_pos_y", location[1]);
    //                    context.startActivity(intent);
    //                }
    //            });
    //
    //            return contentView;
    //        }
    //
    //        class ViewHolder {
    //            View tagView;
    //            TextView nameTv;
    //            TextView timeTv;
    //            TextView contentTv;
    //            LinearLayout headLayout;
    //        }
    //    }

    /**
     * 标记未读消息记录
     */
    private void clearMessageCounts(final int tag) {
        API.Message.ClearUnReadByBusType(tag, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                context.sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_COUNT));
                if (tag == -1) {
                    ToastUtil.showToast(context, "标记成功", R.drawable.tishi_ico_gougou);
                    getChatList(false);
                } else {
                    getMessageNotice(true);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (tag == -1) {
                    Toast.makeText(context, "网络异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
