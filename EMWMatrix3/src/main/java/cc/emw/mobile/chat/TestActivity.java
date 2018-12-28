package cc.emw.mobile.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.AiMsgBean;
import cc.emw.mobile.chat.model.bean.Dynamic;
import cc.emw.mobile.chat.model.bean.Follow;
import cc.emw.mobile.chat.model.bean.GroupMessage;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.chat.model.bean.Information;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.entity.NoticeComment;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.form.FormDetailActivity;
import cc.emw.mobile.form.FormWebActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.CacheUtils;
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

import static cc.emw.mobile.chat.base.ChatContent.ENJOY;
import static cc.emw.mobile.chat.base.ChatContent.M1;


/**
 * Created by xiang.peng on 2016/6/27.
 */

public class TestActivity extends BaseActivity implements View.OnClickListener, CIMEventListener {

    private PtrFrameLayout mPtrFrameLayout;
    private LinearLayout.LayoutParams params;
    private DisplayImageOptions options;
    private DisplayImageOptions optionsGroup;
    private MyBroadcastReceiver receiver;
    private ScrollView scrollView;
    private MyListView mListView_takler;
    private MyListView mListView_work;
    private MyListView mListView_information;
    private MyListView mCommentLv;
    private MyListView mEnjoyLv;
    private LinearLayout concernLayout; //关注列表Layout
    private LinearLayout concernList;   //关注列表
    private TextView concernNum;    //关注数量
    private ChatAdapter adapter = new ChatAdapter();
    private ChatAdapters adapters = new ChatAdapters();
    private ChatAdapterss adapterss = new ChatAdapterss();
    private CommentAdapter commentAdapter = new CommentAdapter(this);
    private EnjoyAdapter enjoyAdapter = new EnjoyAdapter(this);
    private View mLineWorkBottom, mLineInfoBottom;
    private ArrayList<HistoryMessage> m1 = new ArrayList<>();//Takler
    private ArrayList<ApiEntity.Message> m2 = new ArrayList<>();//Work
    private ArrayList<ApiEntity.Message> m3 = new ArrayList<>();//Information
    private View kongbai;   //空白内容状态
    private List<UserNote> mEnjoyList = new ArrayList<>();
    /////////////////////////////// 评论通知 //////////////////////////////
    private List<ApiEntity.Message> comments = new ArrayList<>();
    private List<NoticeComment.CommentInfo> commentInfos;
    private List<NoticeComment> noticeComments;
    private int topId;  //标记是否为同一条动态
    //////////////////////////////////////////////////////////////////////
    public boolean isRefreshAll = true;

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ChatContent.ACTION_CHATS)) {
                if (intent.hasExtra("work"))
                    getMessageWork(true);
                else if (intent.hasExtra("information"))
                    getMessageNotice(true);
                else
                    getChatList(false);
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.arg1 == 1) {
                isRefreshAll = false;
                scrollView.setVisibility(View.VISIBLE);
                kongbai.setVisibility(View.GONE);
                if (EMWApplication.onMessageReceive == 1) {
                    getChatList(false);
                    getMyFans();
                } else {
                    getMyFans();
                }
            }
            if (isRefreshAll) {
                isRefreshAll = false;
                mPtrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.autoRefresh(false);
                    }
                }, 100);
                scrollView.setVisibility(View.GONE);
                kongbai.setVisibility(View.VISIBLE);
            }
            EMWApplication.onMessageReceive = 0;
            switch (msg.what) {
                case M1:
                    if (msg.obj != null) {
                        adapter.setData((List<HistoryMessage>) msg.obj);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case ChatContent.M2:
                    if (msg.obj != null) {
                        if (((List<ApiEntity.Message>) msg.obj).size() > 0) {
                            mLineWorkBottom.setVisibility(View.VISIBLE);
                            adapters.setData((List<ApiEntity.Message>) msg.obj);
                            adapters.notifyDataSetChanged();
                        } else {
                            mLineWorkBottom.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ChatContent.M3:
                    if (msg.obj != null) {
                        if (((List<ApiEntity.Message>) msg.obj).size() > 0) {
                            mLineInfoBottom.setVisibility(View.VISIBLE);
                            adapterss.setData((List<ApiEntity.Message>) msg.obj);
                            adapterss.notifyDataSetChanged();
                        } else {
                            mLineInfoBottom.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ChatContent.COMMENT:
                    if (msg.obj != null) {
                        commentAdapter.setData((ArrayList<NoticeComment>) msg.obj);
                        commentAdapter.notifyDataSetChanged();
                    }
                    break;
                case ChatContent.ENJOY:
                    if (msg.obj != null) {
                        enjoyAdapter.setData((ArrayList<UserNote>) msg.obj);
                        enjoyAdapter.notifyDataSetChanged();
                    }
                    break;
                case 111:   //Talker
                    try {
                        if (m1 != null && m1.size() > 0) {
                            scrollView.setVisibility(View.VISIBLE);
                            kongbai.setVisibility(View.GONE);
                            adapter.setData(m1);
                            adapter.notifyDataSetChanged();
                        }
                        Intent intentBroadCast = new Intent();
                        intentBroadCast.setAction(ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                        sendBroadcast(intentBroadCast);
                    } catch (Exception e) {
                        Log.d("zrjt", "m1" + e.toString());
                    }
                    break;
                case 112:   //work
                    if (m2 != null && m2.size() > 0) {
                        scrollView.setVisibility(View.VISIBLE);
                        kongbai.setVisibility(View.GONE);
                        mLineWorkBottom.setVisibility(View.VISIBLE);
                        adapters.setData(m2);
                        adapters.notifyDataSetChanged();
                    } else {
                        mLineWorkBottom.setVisibility(View.GONE);
                    }
                    break;
                case 113:   //notice
                    adapterss.setData(m3);
                    commentAdapter.setData((ArrayList<NoticeComment>) noticeComments);
                    adapterss.notifyDataSetChanged();
                    commentAdapter.notifyDataSetChanged();
                    mLineInfoBottom.setVisibility(m3 != null && m3.size() > 0 ? View.VISIBLE : View.GONE);
                    if (m1.size() > 0 || m2.size() > 0 || m3.size() > 0) {
                        scrollView.setVisibility(View.VISIBLE);
                        kongbai.setVisibility(View.GONE);
                    } else {
                        kongbai.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_information);
        getSwipeBackLayout().setScrimColor(Color.TRANSPARENT);
        CIMListenerManager.registerMessageListener(this, this);
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
        sendBroadcast(intent);
        initView();
    }

    @Override
    public void onMessageReceived(String message) {
        if (message != null && !TextUtils.isEmpty(message)) {
            try {
                Message msg = new Gson().fromJson(message, Message.class);
                switch (msg.getBusType()) {
                    case 0:
                        getChatList(true);
                        break;
                    case 1:
                        getMessageWork(true);
                        break;
                    case 2:
                        getMessageNotice(true);
                        break;
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onReplyReceived(String replybody) {
    }

    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {
    }

    @Override
    public void onConnectionStatus(boolean isConnected) {
    }

    @Override
    public void onCIMConnectionSucceed() {
    }

    @Override
    public void onCIMConnectionClosed() {
    }

    private void initView() {
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setToggleMenu(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);

        params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(this, 30), DisplayUtil.dip2px(this, 30));

        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatContent.ACTION_CHATS);
        registerReceiver(receiver, filter);
        mLineWorkBottom = findViewById(R.id.line_work_bottom);
        mLineInfoBottom = findViewById(R.id.line_info_bottom);
//        mLineComment = findViewById(R.id.line_comment_bottom);
        concernLayout = (LinearLayout) findViewById(R.id.ll_notification_concern_layout);
        concernList = (LinearLayout) findViewById(R.id.ll_notification_concern_list);
        concernNum = (TextView) findViewById(R.id.iv_more_concern_num);
        mCommentLv = (MyListView) findViewById(R.id.mlv_comment);
        mEnjoyLv = (MyListView) findViewById(R.id.mlv_enjoy);
        mListView_takler = (MyListView) findViewById(R.id.list_takler);
        mListView_work = (MyListView) findViewById(R.id.list_work);
        mListView_information = (MyListView) findViewById(R.id.list_information);
        kongbai = findViewById(R.id.kongbai);
        kongbai.setVisibility(View.VISIBLE);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        ImageView dismiss = (ImageView) findViewById(R.id.dismiss);
        dismiss.setOnClickListener(this);
        TextView delete = (TextView) findViewById(R.id.delete);
        kongbai.setVisibility(View.GONE);
        delete.setOnClickListener(this);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();

        optionsGroup = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead)
                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                .showImageOnFail(R.drawable.cm_img_grouphead)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true).build();

        //set
        mListView_takler.setAdapter(adapter);
        mListView_work.setAdapter(adapters);
        mListView_information.setAdapter(adapterss);
        mCommentLv.setAdapter(commentAdapter);
        mEnjoyLv.setAdapter(enjoyAdapter);

        concernLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, ConcernActivity.class);
                startActivity(intent);
            }
        });
        readCache();
        refresh();
    }

    /**
     * 读取缓存信息
     */
    private void readCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HistoryMessage> m1 = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "m1", new TypeToken<List<HistoryMessage>>() {
                }.getType());
                List<ApiEntity.Message> m2 = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "m2", new TypeToken<List<ApiEntity.Message>>() {
                }.getType());
                List<ApiEntity.Message> m3 = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "m3", new TypeToken<List<ApiEntity.Message>>() {
                }.getType());
                List<NoticeComment> noticeComments = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "noticeComments", new TypeToken<List<NoticeComment>>() {
                }.getType());
                List<UserNote> enjoyLists = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "mEnjoyList", new TypeToken<List<UserNote>>() {
                }.getType());
                android.os.Message msg1 = mHandler.obtainMessage();
                android.os.Message msg2 = mHandler.obtainMessage();
                android.os.Message msg3 = mHandler.obtainMessage();
                android.os.Message msg4 = mHandler.obtainMessage();
                android.os.Message msg5 = mHandler.obtainMessage();
                msg1.what = ChatContent.M1;
                msg1.obj = m1;
                msg2.what = ChatContent.M2;
                msg2.obj = m2;
                msg3.what = ChatContent.M3;
                msg3.obj = m3;
                msg4.what = ChatContent.COMMENT;
                msg4.obj = noticeComments;
                msg5.what = ENJOY;
                msg5.obj = enjoyLists;
                if (m1 != null && m1.size() > 0 || m2 != null && m2.size() > 0 || m3 != null && m3.size() > 0
                        || noticeComments != null && noticeComments.size() > 0 || enjoyLists != null && enjoyLists.size() > 0) {
                    msg1.arg1 = 1;
                    msg2.arg1 = 1;
                    msg3.arg1 = 1;
                    msg4.arg1 = 1;
                    msg5.arg1 = 1;
                }
                mHandler.sendMessage(msg1);
                mHandler.sendMessage(msg2);
                mHandler.sendMessage(msg3);
                mHandler.sendMessage(msg4);
                mHandler.sendMessage(msg5);
            }
        }).start();
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
                getMyFans();
                Intent intent = new Intent();
                intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismiss:
                onBackPressed();
                break;
            case R.id.delete:
                clearMessageCounts();
                break;
        }
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
            final ViewHolder vh;
            if (null == convertView) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(TestActivity.this).inflate(R.layout.listitem_chats, null);
                vh.content = (TextView) convertView.findViewById(R.id.content);
                vh.head = (CircleImageView) convertView.findViewById(R.id.head);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.v1 = convertView.findViewById(R.id.bace);
                vh.v2 = convertView.findViewById(R.id.liucheng);
                vh.liucheng_content = (TextView) convertView.findViewById(R.id.liucheng_content);
                vh.liucheng_time = (TextView) convertView.findViewById(R.id.liucheng_time);
                vh.talker1 = convertView.findViewById(R.id.view_message_tag_talker_bace);
                vh.talker2 = convertView.findViewById(R.id.view_message_tag_talker_liucheng);
                convertView.setTag(R.id.tag_first, vh);
            } else {
                vh = (ViewHolder) convertView.getTag(R.id.tag_first);
            }

            if (msg.getUnReadCount() != 0) {
                vh.talker1.setVisibility(View.VISIBLE);
                vh.talker2.setVisibility(View.VISIBLE);
            } else {
                vh.talker1.setVisibility(View.INVISIBLE);
                vh.talker2.setVisibility(View.INVISIBLE);
            }

            try {
                if (msg.getMessage().getContent().contains("FlowID")) {
                    Follow data = new Gson().fromJson(msg.getMessage().getContent(), Follow.class);
                    vh.v2.setVisibility(View.VISIBLE);
                    vh.v1.setVisibility(View.GONE);
                    if (data.Content != null && !TextUtils.isEmpty(data.Content))
                        vh.liucheng_content.setText(data.Content);
                    else
                        vh.liucheng_content.setText("你收到一个流程");
                    vh.liucheng_time.setText(StringUtils.friendly_time(msg.getMessage().getCreateTime()));

                    try {
                        final Follow follow = new Gson().fromJson(msg.getMessage().getContent(), Follow.class);
                        vh.liucheng_content.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent;
                                if (PrefsUtil.isFormWeb()) {
                                    intent = new Intent(TestActivity.this, FormWebActivity.class);
                                    intent.putExtra(FormWebActivity.PAGE_ID, follow.Page);
                                    intent.putExtra(FormWebActivity.ROW_ID, follow.RID);
                                } else {
                                    intent = new Intent(TestActivity.this, FormDetailActivity.class);
                                    intent.putExtra("page_id", String.valueOf(follow.Page));
                                    intent.putExtra("row_id", String.valueOf(follow.RID));
                                }

                                TestActivity.this.startActivity(intent);
                                if (vh.talker2.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.getMessage().SenderID);
                                    sendBroadcast(intent);
                                    getChatList(true);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    vh.v1.setVisibility(View.VISIBLE);
                    vh.v2.setVisibility(View.GONE);
                    switch (msg.getMessage().getType()) {
                        case ApiEnum.MessageType.Video:
                            vh.content.setText("[视频]");
                            break;
                        case ApiEnum.MessageType.Audio:
                            vh.content.setText("[语音]");
                            break;
                        case ApiEnum.MessageType.Image:
                            vh.content.setText("[照片]");
                            break;
                        case ApiEnum.MessageType.Attach:
                            vh.content.setText("[文件]");
                            break;
                        case ApiEnum.MessageType.Share:
                            Information data = new Gson().fromJson(msg.getMessage().getContent(), Information.class);
                            String base = "[Takler分享] ";
                            SpannableString spanStr = new SpannableString(base + data.Content);
                            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.dynamicreply_name_text));
                            spanStr.setSpan(colorSpan1, 0, base.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            vh.content.setText(spanStr);
                            break;
                        case ApiEnum.MessageType.Task:
                            vh.content.setText("[任务]");
                            break;
                        case ApiEnum.MessageType.Flow:
                            vh.content.setText("流程消息");
                            break;
                        case ApiEnum.MessageType.RevTalker:
                            vh.content.setText("你的动态收到一条评论");
                            break;
                        case ApiEnum.MessageType.RobotSchedule:
                            vh.content.setText("[日程]");
                            break;
                        default:
                            if (msg.getMessage().getContent().contains("FlowID")) {
                                vh.content.setText("[流程消息]");
                            } else if (msg.getMessage().getContent().contains("m.amap")) {
                                vh.content.setText(R.string.road_way);
                            } else {
                                ChatUtils.spannableEmoticonFilter(vh.content, msg.getMessage().getContent());
                            }
                            break;
                    }
                    String uri;
                    // 群组消息
                    if (m1.get(arg0).getType() == 2) {
                        uri = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, msg.getMessage().Group.Image);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.head), optionsGroup,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                    } else {
                        uri = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, msg.Receiver.Image);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.head), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                    }
                    vh.time.setText(StringUtils.friendly_time(msg.getMessage().getCreateTime()));
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                        intent.putExtra("SenderID", msg.getReceiverID());
                        intent.putExtra("type", msg.getType());
                        intent.putExtra("GroupID", msg.getMessage().GroupID);
                        startActivity(intent);
                        if (vh.talker1.getVisibility() == View.VISIBLE) {
                            intent = new Intent();
                            intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                            intent.putExtra("projectSendId", msg.getMessage().SenderID);
                            sendBroadcast(intent);
                            getChatList(true);
                        }
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
                convertView = LayoutInflater.from(TestActivity.this).inflate(R.layout.list_item_work, null);
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
                vh.work1 = convertView.findViewById(R.id.view_message_tag_work_jiaqun);
                vh.work2 = convertView.findViewById(R.id.view_message_tag_work_task);
                vh.work3 = convertView.findViewById(R.id.view_message_tag_xiangmu);
                vh.work4 = convertView.findViewById(R.id.view_message_tag_quanzi);
                // 申请加群操作
                vh.tvAgree = (TextView) convertView.findViewById(R.id.jiaqun_sure);
                vh.tvRefuse = (TextView) convertView.findViewById(R.id.jiaqun_cancel);
                vh.tvGroupWhat = (TextView) convertView.findViewById(R.id.tv_works_group_what);
                vh.liucheng_time = (TextView) convertView.findViewById(R.id.jianqun_times);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(R.id.tag_second, 0);
            vh.inforIcon.setVisibility(View.VISIBLE);
            vh.ivCalendarNotice.setVisibility(View.GONE);
            vh.cImgProjectCreate.setVisibility(View.GONE);
            if (msg.IsNew == 1) {
                vh.work1.setVisibility(View.VISIBLE);
                vh.work2.setVisibility(View.VISIBLE);
                vh.work3.setVisibility(View.VISIBLE);
                vh.work4.setVisibility(View.VISIBLE);
            } else {
                vh.work1.setVisibility(View.INVISIBLE);
                vh.work2.setVisibility(View.INVISIBLE);
                vh.work3.setVisibility(View.INVISIBLE);
                vh.work4.setVisibility(View.INVISIBLE);
            }

            try {
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
                                    ToastUtil.showToast(TestActivity.this, "群組加入成功!", R.drawable.tishi_ico_gougou);
                                    vh.tvAgree.setText("已同意");
                                    vh.tvAgree.setVisibility(View.VISIBLE);
                                    vh.tvRefuse.setVisibility(View.GONE);
                                    Intent intent = new Intent(TestActivity.this, GroupFragment.class);
                                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                                    TestActivity.this.sendBroadcast(intent);
                                    if (vh.work1.getVisibility() == View.VISIBLE) {
                                        intent = new Intent();
                                        intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                        intent.putExtra("projectSendId", msg.SenderID);
                                        intent.putExtra("work", "work");
                                        sendBroadcast(intent);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    ToastUtil.showToast(TestActivity.this, "群組加入失敗");
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
                                if (vh.work1.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                ToastUtil.showToast(TestActivity.this, "处理失败。");
                            }
                        });
                    }
                });

                vh.header2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (msg.SenderID != 0) {
                            if (EMWApplication.personMap != null && EMWApplication.personMap.get(msg.SenderID) != null) {
                                Intent intent = new Intent(TestActivity.this, PersonInfoActivity.class);
                                intent.putExtra("UserInfo", EMWApplication.personMap.get(msg.SenderID));
                                startActivity(intent);
                            }
                        }
                    }
                });

                switch (msg.Type) {
                    case 25:
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.VISIBLE);
                        vh.tvAgree.setVisibility(View.GONE);
                        vh.tvRefuse.setVisibility(View.GONE);
                        vh.liucheng_time.setVisibility(View.VISIBLE);
                        String uri = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, msg.Sender.Image);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(vh.header2), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        vh.tvGroupWhat.setText(data.State == 1 ? "同意您加入" : "拒绝您加入");
                        vh.liucheng_content.setText(data.GroupName);
                        vh.liucheng_time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TestActivity.this, GroupInActivity.class);
                                intent.putExtra("GroupID", data.GroupID);
                                startActivity(intent);
                                if (vh.work1.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                        break;
                    case 19:
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.VISIBLE);
                        vh.tvAgree.setVisibility(View.VISIBLE);
                        vh.liucheng_time.setVisibility(View.GONE);
                        if (data.State == 0) {
                            vh.tvAgree.setText("同意");
                            vh.tvRefuse.setVisibility(View.VISIBLE);
                            vh.tvAgree.setTag(1);
                        } else {
                            vh.tvAgree.setText(data.State == 1 ? "已同意" : "已拒绝");
                            vh.tvRefuse.setVisibility(View.GONE);
                            vh.tvAgree.setTag(0);
                        }
                        vh.tvGroupWhat.setText("申请加入: ");
                        vh.liucheng_content.setText(data.GroupName);
                        String uris = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(uris, new ImageViewAware(vh.header2), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TestActivity.this, GroupInActivity.class);
                                intent.putExtra("GroupID", data.GroupID);
                                startActivity(intent);
                                if (vh.work1.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                        break;
                    case 16:
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
                                Intent intent = new Intent(TestActivity.this, TaskDetailActivity.class);
//                                intent.putExtra("sendId", msg.SenderID);
                                intent.putExtra(TaskDetailActivity.TASK_ID, data.TaskID);
                                startActivity(intent);
                                if (vh.work2.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                        break;
                    case 17:
                        vh.v2.setVisibility(View.VISIBLE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v3.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.inforIcon.setVisibility(View.GONE);
                        vh.ivCalendarNotice.setVisibility(View.GONE);
                        vh.cImgProjectCreate.setVisibility(View.VISIBLE);
                        String b1 = "创建了项目组:";
                        String b2 = "并关联了你";
                        SpannableString sp = new SpannableString(b1 + data.ProjectName + b2);
                        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.dynamicreply_name_text));
                        sp.setSpan(colorSpan1, b1.length(), data.ProjectName.length() + b1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        Log.d("zrjt", sp.toString());
                        vh.name.setText(sp);
                        String urissProject = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(urissProject, new ImageViewAware(vh.cImgProjectCreate), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                                if (vh.work3.getVisibility() == View.VISIBLE) {
                                    Intent intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                                TestActivity.this.finish();
                            }
                        });
                        break;
                    case 18:
                        vh.v3.setVisibility(View.VISIBLE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        String base1 = "创建了圈子：";
                        String base2 = "并添加了你";
                        SpannableString spanStr = new SpannableString(base1 + data.GroupName + base2);
                        ForegroundColorSpan color = new ForegroundColorSpan(getResources().getColor(R.color.dynamicreply_name_text));
                        spanStr.setSpan(color, base1.length(), base1.length() + data.GroupName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        Log.d("zrjt", spanStr.toString());
//                        String[] strss = spanStr.toString().split("\\|\\|");
//                        if (strss.length == 2) {
                        vh.qunzhus.setText(spanStr);
//                        }
                        String uriss = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
                        ImageLoader.getInstance().displayImage(uriss, new ImageViewAware(vh.header1), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        vh.qunzhushijian.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TestActivity.this, GroupInActivity.class);
                                intent.putExtra("GroupID", data.GroupID);
                                startActivity(intent);
                                if (vh.work4.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
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
                                Intent intent = new Intent(TestActivity.this, CalendarInfoActivity2.class);
                                intent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(strss[1]));
                                intent.putExtra("start_anim", false);
                                startActivity(intent);
                                if (vh.work3.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                    } else {
                        String images = "";
                        if (EMWApplication.personMap.size() > 0) {
                            if (EMWApplication.personMap.get(msg.ReceiverID) != null) {
                                ApiEntity.UserInfo user = EMWApplication.personMap.get(m2.get(
                                        arg0).ReceiverID);
                                if (user != null) {
                                    images = user.Image;
                                }
                            }
                        }
                        String uris = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, images);
                        ImageLoader.getInstance().displayImage(uris, new ImageViewAware(vh.header2), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TestActivity.this, TaskDetailActivity.class);
                                intent.putExtra("sendId", msg.SenderID);
                                intent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(strss[1]));
                                intent.putExtra("start_anim", false);
                                startActivity(intent);
                                if (vh.work3.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("work", "work");
                                    sendBroadcast(intent);
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
                convertView = LayoutInflater.from(TestActivity.this).inflate(R.layout.list_item_information, null);
                vh.content = (TextView) convertView.findViewById(R.id.dongtainierong);
                vh.time = (TextView) convertView.findViewById(R.id.times);
                vh.name = (TextView) convertView.findViewById(R.id.dongtaiming);
                vh.header2 = (CircleImageView) convertView.findViewById(R.id.heads);
                vh.head = (CircleImageView) convertView.findViewById(R.id.head);
                vh.v1 = convertView.findViewById(R.id.dongtai);//收藏动态
                vh.v2 = convertView.findViewById(R.id.dongtai_pl); //动态评论
                vh.v4 = convertView.findViewById(R.id.bace);//关注我
                vh.v5 = convertView.findViewById(R.id.ll_exit_group);   //移除圈子通知
                vh.information4 = convertView.findViewById(R.id.view_message_tag_group_exit);
                vh.exitGroupContent = (TextView) convertView.findViewById(R.id.tv_message_info_exit_group);
                vh.exitGroupTime = (TextView) convertView.findViewById(R.id.tv_time_exit_group);
                vh.enjoy = (TextView) convertView.findViewById(R.id.tv_message_enjoy);

                // 动态评论
                vh.information3 = convertView.findViewById(R.id.view_message_tag_talker_dongtai);
                vh.talkerDtName = (TextView) convertView.findViewById(R.id.dongtaiming_talker);
                vh.qunzhumingzi = (TextView) convertView.findViewById(R.id.times_pl);
                vh.qunzhushijian = (TextView) convertView.findViewById(R.id.dongtainierong_pl);

                vh.liucheng_content = (TextView) convertView.findViewById(R.id.content);
                vh.information1 = convertView.findViewById(R.id.view_message_tag_information_dongtai);
                vh.information2 = convertView.findViewById(R.id.view_message_tag_information_concern);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            if (msg.IsNew == 1) {
                vh.information1.setVisibility(View.VISIBLE);
                vh.information2.setVisibility(View.VISIBLE);
                vh.information3.setVisibility(View.VISIBLE);
                vh.information4.setVisibility(View.VISIBLE);
            } else {
                vh.information1.setVisibility(View.INVISIBLE);
                vh.information2.setVisibility(View.INVISIBLE);
                vh.information3.setVisibility(View.INVISIBLE);
                vh.information4.setVisibility(View.INVISIBLE);
            }

            try {
                if (msg.Content.contains("ControlType")) {

                    vh.talkerDtName.setText(PrefsUtil.readUserInfo().Name + ": "); //动态当前用户发布取其姓名

                    Dynamic data = new Gson().fromJson(msg.Content, Dynamic.class);

                    if (data.ControlType == 0) {
                        //动态评论
                        vh.v2.setVisibility(View.GONE); //显示
                        vh.v1.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
//                        String uri = "";
//                        if (data.NewContent != null && !TextUtils.isEmpty(data.NewContent)) {
//                            NoticeComment noticeComment = new NoticeComment();
//                            noticeComment.newContent = data.NewContent;
//                            noticeComment.urls = data.UserImage;
//                            noticeComments.add(noticeComment);
//                        }
//                        ChatUtils.spannableEmoticonFilter(vh.qunzhushijian, data.OldContent);
//                        if (data.NewContent != null) {
//                            ChatUtils.spannableEmoticonFilter(vh.qun, data.NewContent);
//                        }
//                        if (data.UserImage != null) {
//                            uri = String.format(Const.DOWN_ICON_URL,
//                                    PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
//                        }
//                        ImageLoader.getInstance().displayImage(uri, vh.header1, options);
//                        vh.qunzhumingzi.setText(msg.CreateTime);
                    }
                }

                switch (msg.Type) {
                    case ApiEnum.MessageType.LeaveGroup:
                        vh.v5.setVisibility(View.VISIBLE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
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
                                Intent intent = new Intent(TestActivity.this, GroupInActivity.class);
                                intent.putExtra("GroupID", msg.GroupID);
                                intent.putExtra("start_anim", false);
                                int[] location = new int[2];
                                v.getLocationInWindow(location);
                                intent.putExtra("click_pos_y", location[1]);
                                startActivity(intent);
                                if (vh.information4.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("information", "information");
                                    sendBroadcast(intent);
                                }
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
                        String base2 = "向你发来一个分享 ";
                        SpannableString spanStr2 = new SpannableString(base2 + data.Content);
                        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.dynamicreply_name_text));
                        spanStr2.setSpan(colorSpan2, base2.length(), spanStr2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        vh.liucheng_content.setText(spanStr2);
                        String uriss2;
                        if (msg.Sender == null || msg.Sender.Image == null || TextUtils.isEmpty(msg.Sender.Image))
                            uriss2 = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode, "test");
                        else
                            uriss2 = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode, msg.Sender.Image);
                        ImageLoader.getInstance().displayImage(uriss2, new ImageViewAware(vh.head), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        vh.time.setText(StringUtils.friendly_time(msg.CreateTime));
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TestActivity.this, DynamicDetailActivity.class);
                                intent.putExtra("note_id", data.TalkerID);
                                startActivity(intent);
                                if (vh.information2.getVisibility() == View.VISIBLE) {
                                    intent = new Intent();
                                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                                    intent.putExtra("projectSendId", msg.SenderID);
                                    intent.putExtra("information", "information");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                        break;
                    case ApiEnum.MessageType.EnjoyTalker:
                        vh.v1.setVisibility(View.GONE); //显示
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
//                        String uriEnjoy = "";
//                        ChatUtils.spannableEmoticonFilter(vh.content, data.OldContent);
//                        if (data.ControlValue == 0) {
//                            vh.enjoy.setText("他们收藏了你的动态");
//                        } else {
//                            vh.enjoy.setText("他们取消收藏了你的动态");
//                        }
//                        vh.name.setText(PrefsUtil.readUserInfo().Name + ": ");
//                        if (data.UserImage != null) {
//                            uriEnjoy = String.format(Const.DOWN_ICON_URL,
//                                    PrefsUtil.readUserInfo().CompanyCode, data.UserImage);
//                        }
//                        ImageLoader.getInstance().displayImage(uriEnjoy, vh.header2, options);
//                        vh.time.setText(msg.CreateTime + "");
                        break;
//                    case 2://关注我
                    case ApiEnum.MessageType.FollowMe:
                        vh.v1.setVisibility(View.GONE);
                        vh.v2.setVisibility(View.GONE);
                        vh.v4.setVisibility(View.GONE);
                        vh.v5.setVisibility(View.GONE);
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
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TestActivity", "notice:" + new Gson().toJson(msg));
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
        View v1, v2, v3, v4, v5, talker1, talker2, work1, work2, work3, work4, information1, information2, information3, information4;
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

    // 获取消息记录
    private void getChatList(final boolean isFinish) {
        API.Message.GetChatRecords(new RequestCallback<HistoryMessage>(
                HistoryMessage.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(TestActivity.this, "加载失败，请重试...");
            }

            @Override
            public void onParseSuccess(final List<HistoryMessage> respList) {
                if (respList.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            m1.clear();
                            for (HistoryMessage historyMessage : respList) {
                                if (historyMessage.getMessage() != null) {
                                    AiMsgBean aiMsgBean = null;
                                    try {
                                        aiMsgBean = new Gson().fromJson(historyMessage.getMessage().getContent(), AiMsgBean.class);
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                    switch (historyMessage.getMessage().BusType) {
                                        case 0:
                                            if (historyMessage.getType() == 2 && EMWApplication.groupMap != null &&
                                                    EMWApplication.groupMap.get(historyMessage.getMessage().getGroupID()) != null) {
                                                if (aiMsgBean != null && aiMsgBean.getText() != null)
                                                    historyMessage.getMessage().setContent(aiMsgBean.getText());
                                                m1.add(historyMessage);
                                            } else if (historyMessage.getType() == 1) {
                                                if (aiMsgBean != null && aiMsgBean.getText() != null)
                                                    historyMessage.getMessage().setContent(aiMsgBean.getText());
                                                m1.add(historyMessage);
                                            }
                                            break;
                                    }
                                }
                            }
                            mHandler.sendEmptyMessage(111); // Talker
                            // 写入缓存
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "m1", m1);
                        }
                    }).start();
                    if (!isFinish)
                        getMessageWork(false);
                }
            }
        });
    }

    private void getMessageWork(final boolean isFinish) {
        API.Message.GetUserWorkMessages(1, 20, new RequestCallback<ApiEntity.Message>(ApiEntity.Message.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("zrjt", "WorkError");
            }

            @Override
            public void onParseSuccess(final List<ApiEntity.Message> respList) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        m2.clear();
                        m2.addAll(respList);
                        mHandler.sendEmptyMessage(112);  //work
                        CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "m2", respList);
                    }
                }).start();
                if (!isFinish)
                    getMessageNotice(false);
            }
        });
    }

    private void getMessageNotice(final boolean isFinish) {
        API.Message.GetUserNoticeMessages(1, 20, new RequestCallback<ApiEntity.Message>(ApiEntity.Message.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("zrjt", "NoticeError");
            }

            @Override
            public void onParseSuccess(final List<ApiEntity.Message> respList) {
//                messageEnjoyList = new ArrayList<ApiEntity.Message>();
//                messageConcernList = new ArrayList<ApiEntity.Message>();
                if (!isFinish)
                    getEnjoyNoteList();
                if (respList != null && respList.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            m3.clear();
                            comments.clear();
                            noticeComments = new ArrayList<>();//评论消息的集合
                            commentInfos = new ArrayList<NoticeComment.CommentInfo>();
                            for (int i = 0; i < respList.size(); i++) {
                                ApiEntity.Message message = respList.get(i);
                                if (message.Type == 13) {    //评论
                                    Dynamic datas = new Gson().fromJson(message.Content, Dynamic.class);
                                    message.topIdOut = datas.TopID;
                                    comments.add(message);
                                }
                                if (message.Type == 23 || message.Type == 30) {
                                    m3.add(message);
                                }
                            }
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
                                        noticeComment.commentInfos.add(commentInfo);
                                    }
                                    noticeComment.topId = data.TopID;
                                    noticeComment.userId = respList.get(i).UserID;
                                    noticeComment.isNew = respList.get(i).IsNew;
                                    noticeComment.oldContent = data.OldContent;
                                    noticeComment.sendID = respList.get(i).SenderID;
                                    noticeComments.add(noticeComment);
                                }
                            }
                            mHandler.sendEmptyMessage(113);     //notice
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "noticeComments", noticeComments);
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "m3", m3);
                        }
                    }).start();
                }
            }
        });
    }

    private void getEnjoyNoteList() {
        API.TalkerAPI.GetEnjoyNoteList(5, 1, new RequestCallback<UserNote>(UserNote.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("zrjt", "NoticeEnjoyError");
                mPtrFrameLayout.refreshComplete();
            }

            @Override
            public void onParseSuccess(List<UserNote> respList) {
                mPtrFrameLayout.refreshComplete();
                mEnjoyList.clear();
                mEnjoyList.addAll(respList);
                CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "mEnjoyList", mEnjoyList);
                enjoyAdapter.setData((ArrayList<UserNote>) mEnjoyList);
                enjoyAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 动态评论adapter
     */
    class CommentAdapter extends BaseAdapter {

        private Context mContext;
        private List<NoticeComment> mDataList;
        private DisplayImageOptions options;

        public CommentAdapter(Context context) {
            this.mContext = context;
            this.mDataList = new ArrayList<>();
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                    .bitmapConfig(Bitmap.Config.RGB_565)
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
            final ViewHolder vh;
            if (contentView == null) {
                vh = new ViewHolder();
                contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_info_comment, null);
                vh.tagView = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
                vh.nameTv = (TextView) contentView.findViewById(R.id.tv_infocomment_name);
                vh.timeTv = (TextView) contentView.findViewById(R.id.tv_infocomment_time);
                vh.contentTv = (TextView) contentView.findViewById(R.id.tv_infocomment_content);
                vh.listView = (MyListView) contentView.findViewById(R.id.lv_comment);
                vh.view = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
                contentView.setTag(R.id.tag_first, vh);
            } else {
                vh = (ViewHolder) contentView.getTag(R.id.tag_first);
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

            NoticeCommentAdapter noticeCommentAdapter = new NoticeCommentAdapter(mContext, un.commentInfos);
            vh.listView.setAdapter(noticeCommentAdapter);

            contentView.setTag(R.id.tag_second, un.topId);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", Integer.valueOf(v.getTag(R.id.tag_second).toString()));
                    mContext.startActivity(intent);
                    intent = new Intent();
                    intent.setAction(MainActivity.ACTION_REFRESH_COUNT);
                    intent.putExtra("projectSendId", un.sendID);
                    sendBroadcast(intent);
                    if (vh.view.getVisibility() == View.VISIBLE)
                        getMessageNotice(true);
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

    class EnjoyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<UserNote> mDataList;
        private DisplayImageOptions options;

        public EnjoyAdapter(Context context) {
            this.mContext = context;
            this.mDataList = new ArrayList<>();

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .build(); // 创建配置过得DisplayImageOption对象
        }

        public void setData(ArrayList<UserNote> dataList) {
            this.mDataList = dataList;
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
            return mDataList.get(position).ID;
        }

        @Override
        public View getView(final int position, View contentView, ViewGroup parent) {
            ViewHolder vh;
            if (contentView == null) {
                vh = new ViewHolder();
                contentView = LayoutInflater.from(mContext).inflate(R.layout.listitem_info_enjoy, null);
                vh.tagView = contentView.findViewById(R.id.view_message_tag_talker_dongtai);
                vh.nameTv = (TextView) contentView.findViewById(R.id.tv_infocomment_name);
                vh.timeTv = (TextView) contentView.findViewById(R.id.tv_infocomment_time);
                vh.contentTv = (TextView) contentView.findViewById(R.id.tv_infocomment_content);
                vh.headLayout = (LinearLayout) contentView.findViewById(R.id.ll_infoenjoy_head);
                contentView.setTag(R.id.tag_first, vh);
            } else {
                vh = (ViewHolder) contentView.getTag(R.id.tag_first);
            }
            final UserNote un = mDataList.get(position);
            vh.nameTv.setText(PrefsUtil.readUserInfo().Name + ": ");
            vh.headLayout.removeAllViews();
//            String createTime = un.CreateTime;
//            vh.timeTv.setText(StringUtils.friendly_time(createTime));
            String content = "";
            if (!TextUtils.isEmpty(un.Content)) {
                content = Html.fromHtml(un.Content).toString();
            }
            vh.contentTv.setText(content);

            if (un.EnjoyList != null && un.EnjoyList.size() > 0) {
                contentView.setVisibility(View.VISIBLE);
                for (int i = 0; i < un.EnjoyList.size(); i++) {
                    if (un.EnjoyList.size() - i - 5 > 0) //超过5个只需显示最后5个
                        continue;
                    ApiEntity.TalkerUserInfo user = un.EnjoyList.get(i);
                    final CircleImageView imgview = new CircleImageView(mContext);
                    imgview.setBorderWidth(DisplayUtil.dip2px(mContext, 1));
                    imgview.setBorderColorResource(R.color.cm_headimg_border);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 30), DisplayUtil.dip2px(mContext, 30));
                    String url = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, user.Image);
                    ImageLoader.getInstance().displayImage(url, new ImageViewAware(imgview), options, new ImageSize(DisplayUtil.dip2px(mContext, 30), DisplayUtil.dip2px(mContext, 30)), null, null);
                    if (vh.headLayout.getChildCount() != 0)
                        params.leftMargin = DisplayUtil.dip2px(mContext, 5);
                    vh.headLayout.addView(imgview, params);
                }
            } else {
                contentView.setVisibility(View.GONE);
            }
            contentView.setTag(R.id.tag_second, un.ID);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicDetailActivity.class);
                    intent.putExtra("note_id", Integer.valueOf(v.getTag(R.id.tag_second).toString()));
                    mContext.startActivity(intent);
                }
            });

            return contentView;
        }

        class ViewHolder {
            View tagView;
            TextView nameTv;
            TextView timeTv;
            TextView contentTv;
            LinearLayout headLayout;
        }
    }

    /**
     * 获取我的粉丝列表
     */
    private void getMyFans() {

        API.TalkerAPI.GetMyFans(PrefsUtil.readUserInfo().ID, new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    concernLayout.setVisibility(View.VISIBLE);
                    concernList.removeAllViews();
                    for (int i = 0; i < respList.size(); i++) {
                        if (i > 5)
                            continue;
                        CircleImageView circleImageView = new CircleImageView(TestActivity.this);
                        String uri;
                        if (i != 0) {
                            params.leftMargin = DisplayUtil.dip2px(TestActivity.this, 10);
                        }
                        uri = String.format(Const.DOWN_ICON_URL,
                                PrefsUtil.readUserInfo().CompanyCode, respList.get(i).Image);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(circleImageView), options,
                                new ImageSize(DisplayUtil.dip2px(TestActivity.this, 30), DisplayUtil.dip2px(TestActivity.this, 30)), null, null);
                        concernList.addView(circleImageView, params);
                    }
                    if (respList.size() > 5) {
                        concernNum.setVisibility(View.VISIBLE);
                        concernNum.setText(respList.size() + "");
                    } else {
                        concernNum.setVisibility(View.INVISIBLE);
                    }
                } else {
                    concernLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }
        });
    }

    /**
     * 标记未读消息记录
     */
    private void clearMessageCounts() {
        API.Message.ClearUnReadByBusType(-1, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onSuccess(String result) {
                ToastUtil.showToast(TestActivity.this, "标记成功", R.drawable.tishi_ico_gougou);
                getChatList(false);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(TestActivity.this, "网络异常,请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CIMListenerManager.removeMessageListener(this);
        if (receiver != null)
            unregisterReceiver(receiver);
    }
}
