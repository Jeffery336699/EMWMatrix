package cc.emw.mobile.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.czt.mp3recorder.MP3Recorder;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mingle.headsUp.HeadsUpManager;
import com.nononsenseapps.filepicker.BackHandlingFilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.AlertDialog;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.R;
import cc.emw.mobile.asr.UploadFile;
import cc.emw.mobile.asr.asrfinishjson.AsrFinishJsonData;
import cc.emw.mobile.asr.asrpartialjson.AsrPartialJsonData;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.calendar.RelationFileActivity;
import cc.emw.mobile.chat.adapter.ChatEmoticonsAdapter;
import cc.emw.mobile.chat.adapter.ChatFaceGVAdapter;
import cc.emw.mobile.chat.adapter.ChatGroupSelectAdapter;
import cc.emw.mobile.chat.adapter.ChatImportanceMessageAdapter;
import cc.emw.mobile.chat.adapter.ChatMainAdapter;
import cc.emw.mobile.chat.adapter.ChatPhotoAndCameraAdapter;
import cc.emw.mobile.chat.adapter.MoreAppApdater;
import cc.emw.mobile.chat.base.ChatBaseActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.imagepicker.ImagePicker;
import cc.emw.mobile.chat.imagepicker.bean.ImageItem;
import cc.emw.mobile.chat.imagepicker.ui.ImageGridActivity;
import cc.emw.mobile.chat.map.activity.ShareLocationActivity;
import cc.emw.mobile.chat.model.IChatMsgDBModel;
import cc.emw.mobile.chat.model.bean.Audios;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.model.bean.ImprotanceMessage;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.chat.model.bean.MapBean;
import cc.emw.mobile.chat.model.bean.MessageInfo;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.model.impl.ChatMsgDBModelImpl;
import cc.emw.mobile.chat.model.impl.PutUserInfoAllServiceConnection;
import cc.emw.mobile.chat.persenter.impl.ChatNetPersenterImpl;
import cc.emw.mobile.chat.ui.IChatView;
import cc.emw.mobile.chat.util.ActionSheet;
import cc.emw.mobile.chat.utils.DefQqEmoticons;
import cc.emw.mobile.chat.utils.MediaPlayerManger;
import cc.emw.mobile.chat.utils.PhotoUtil;
import cc.emw.mobile.chat.utils.VideoPlayer;
import cc.emw.mobile.chat.view.ChatButtonRoundnessLayout;
import cc.emw.mobile.chat.view.ChatButtonRoundnessMenuLayout;
import cc.emw.mobile.chat.view.ChatButtonRoundnessMenuLayout2;
import cc.emw.mobile.chat.view.EmoticonsEditText;
import cc.emw.mobile.chat.view.EventRecyclerView;
import cc.emw.mobile.chat.view.HintPopupWindow;
import cc.emw.mobile.chat.view.ScrollToRecyclerView;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.adapter.FileSelectAdapter;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.map.AMapUtil;
import cc.emw.mobile.map.Locations;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.bean.JoinDataBean;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.Prefs;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import cc.emw.mobile.view.SectorProgressView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import io.rong.callkit.CallSelectMemberActivity;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallCommon;
import io.rong.imlib.model.Conversation;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static cc.emw.mobile.net.Const.BASE_URL;
import static cc.emw.mobile.util.PrefsUtil.KEY_USERINFO;


@ContentView(R.layout.activity_chat)
public class ChatActivity extends ChatBaseActivity<IChatView, ChatNetPersenterImpl<IChatView>> implements IChatView, AMapLocationListener, LocationSource, ActionSheet.ActionSheetListener, EventListener {
    @Override
    public void finishActivity() {
        if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
            mChatButtonRoundnessLayout.setVisibility(View.GONE);
        }
        finish();
    }

    @Override
    public void groupUserSearchGone() {
        if (mChatGroupSelectUserList.getVisibility() == View.VISIBLE) {
            mChatGroupSelectUserList.setVisibility(View.GONE);
        }
        mChatGroupSelectAdapter.setData(null);
        mChatGroupSelectAdapter.notifyDataSetChanged();
    }

    @Override
    public void groupUserSearchVisible(List<UserInfo> parserList) {
        if (mChatGroupSelectUserList.getVisibility() == View.GONE) {
            mChatGroupSelectUserList.setVisibility(View.VISIBLE);
        }
        mChatGroupSelectAdapter.setData(parserList);
        mChatGroupSelectAdapter.notifyDataSetChanged();
    }


    @Override
    public void showToastPro(String name, int id) {
        ToastUtil.showToast(ChatActivity.this, name, id);
    }

    @Override
    public void showDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }

    @Override
    public void dismissDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showDialogError() {
        AlertDialog dialog = new AlertDialog(ChatActivity.this).builder();
        dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
        dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                    mChatButtonRoundnessLayout.setVisibility(View.GONE);
                }
                finish();
            }
        }).show();
    }

    @Override
    public void modifyMsgSateView(List<ChatMsgBean> msgList, boolean isR) {
/*//        if (isR) {
//            adapater.setData(msgList);
//            adapater.notifyItemChanged(msgList.size() - 1);
//            mListview.scrollToPosition(msgList.size() - 1);
//        } else {
        adapater.setData(msgList);
//        adapater.notifyItemChanged(msgList.size() - 1);
        adapater.notifyDataSetChanged();
        mListview.scrollToPosition(msgList.size() - 1);
//        }*/

        if (isR) {
            adapater.setData(msgList);
            adapater.notifyDataSetChanged();
            mListview.scrollToPosition(msgList.size() - 1);
        }
    }


    @Override
    public void sendBroadCast(String broadCastName, String action, int id, String broadCastAction) {
        if (broadCastName == null || action == null || id == 0) {
            sendBroadCasts(broadCastAction);
        } else {
            Intent intent = new Intent();
            intent.setAction(broadCastName);
            intent.putExtra(action, id);
            sendBroadcast(intent);
        }
    }

    @Override
    public void sendHandlerView1(android.os.Message msg) {
        handler.sendMessage(msg);
    }

    @Override
    public void showRefreshView() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.refreshComplete();
        }
    }

    @Override
    public void refreshListView(final List<ChatMsgBean> dataList, int pageIndex, int pageSize) {
        adapater.setData(dataList);
        adapater.notifyDataSetChanged();
        if (pageIndex == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListview.scrollToPosition(dataList.size() - 1);
                }
            }, 25);
        } else {
            MoveToPosition(mLayoutManager, mListview, pageSize - 1);//停留在刷新的位置
        }
    }

    @Override
    public void showToast(String name) {
        ToastUtil.showToast(ChatActivity.this, name);
    }

    @Override
    public void sendBroadCast4saveGroupInfo(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void initVideoPlayer(Videos videos) {
        try {
            //给视频截图
            VideoPlayer player = new VideoPlayer(ChatActivity.this);
            player.setVideo(videos);
            player.getFirstVideoFrame(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTitleNameTextView(String name) {
        mTvHeaderTvTitle.setText(!TextUtils.isEmpty(name) ? name : "");
    }


    /**
     * 数据库管理
     */
    private IChatMsgDBModel mChatMsgDao;
    /**************************************************
     * 初始化view控件
     **************************************************/
    @ViewInject(R.id.cm_header_tv_title)//头像栏目--名字
    private TextView mTvHeaderTvTitle;
    @ViewInject(R.id.load_more_list_view_ptr_frame)//全局-刷新控件
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.chat_lv_message)//全局-聊天内容控件
    private ScrollToRecyclerView mListview;//setSelection
    @ViewInject(R.id.rl_chat_show_edit)//聊天栏-文本栏
    private LinearLayout mRlChatShowEdit;
    @ViewInject(R.id.itv_chat_content_app)//聊天栏-切换功能栏按钮
    private ImageView mItvChatContentApp;
    @ViewInject(R.id.eet_chat_add_text)//聊天栏-文本输入框
    private EmoticonsEditText mEetChatAddText;
    @ViewInject(R.id.itv_chat_add_emoticon)//聊天栏--表情按钮
    private ImageView mItvChatAddEmoticon;
    @ViewInject(R.id.itv_chat_send_msg)//聊天栏--发送消息的按钮
    private ImageView mItvChatSendMsg;
    @ViewInject(R.id.rl_line)   //相机 相册布局
    private RelativeLayout mRlLine;
    @ViewInject(R.id.rv_show_photo) //显示
    private RecyclerView mRvShowPhoto;
    @ViewInject(R.id.rl_expression_root) //表情
    private RelativeLayout mRlExpressionRoot;
    @ViewInject(R.id.tv_open_chat_ai_button)//输入框EMW
    private TextView mTvOpenChatAiButton;
    @ViewInject(R.id.tv_open_chat_ai_button2)
    private TextView mTvOpenChatAiButton2; //EMW输入文字信息
    @ViewInject(R.id.root_view)
    private LinearLayout mLlChatRoot;
    @ViewInject(R.id.ll_bottom_layout)
    private LinearLayout mBottomLayout;
    //    @ViewInject(R.id.rv_show_more_app)
    //    private RecyclerView mRvShowMoreApp;
    @ViewInject(R.id.itv_chat_file_back)
    private IconTextView mItvChatFileBack;
    @ViewInject(R.id.et_filelist_search)
    private EditText mEtFileListSearch;
    @ViewInject(R.id.rl_expression_root3)
    private RelativeLayout mRlFileViewGroup;
    @ViewInject(R.id.rl_chatmsg_root)
    private RelativeLayout mRlChatMsgRoot;
    @ViewInject(R.id.rl_file_root)
    private RelativeLayout mRlFileRoot;
    @ViewInject(R.id.itv_chat_find_file_state_start)
    private ProgressBar mRb;
    @ViewInject(R.id.rl_expression_root4)
    private RelativeLayout mRlRoot4;       //更多功能
    @ViewInject(R.id.rl_expression_root5)
    private RelativeLayout mRlRoot5;    //共享位置
    @ViewInject(R.id.iv_chat_more_msg)
    private ImageView mIvChatMoreMsg;
    @ViewInject(R.id.id_menulayout)
    private ChatButtonRoundnessLayout mChatButtonRoundnessLayout;

    @ViewInject(R.id.rl_root6)
    private RelativeLayout mRlRoot6;//root
    @ViewInject(R.id.tv_type1)
    private TextView mTvTypeI;//按住说话
    @ViewInject(R.id.iv_down)
    private ImageView mIvDown;//按钮
    @ViewInject(R.id.tv_type2)
    private TextView mTvTypeII;//左右滑动取消
    @ViewInject(R.id.iv_begin_icon_a)
    private ImageView mIvBeginIconI;//左图标
    @ViewInject(R.id.iv_begin_icon_b)
    private ImageView mIvBeginIconII;//右图标
    @ViewInject(R.id.chronometer_type1i)
    private Chronometer mChronometerType;
    @ViewInject(R.id.chat_button_menu)
    private ChatButtonRoundnessMenuLayout mChatButtonMenu;//视频通话
    @ViewInject(R.id.chat_button_menu2)
    private ChatButtonRoundnessMenuLayout2 mChatButtonMenu2;//拍照
    @ViewInject(R.id.chat_group_select_user_list)
    private RecyclerView mChatGroupSelectUserList;
    private MyHandler handler;//ChatActivity
    private String send_msg, name;
    private ListDialog mAddDialog, mAddDialog2;
    private int normal_mes = 4, audio_mes = 8, image_mes = 7, video_mes = 9;// 4 普通消息 7图片消息 6附件消息 8音频信息,10分享 信息
    private ChatMainAdapter adapater;
    private ChatGroupSelectAdapter mChatGroupSelectAdapter;
    private ChatImportanceMessageAdapter importanceMsgAdapter;
    private ChatPhotoAndCameraAdapter photoAndCameraAdapter;
    private MoreAppApdater mMoreAppAdapter;
    private boolean isFinish;
    private List<ChatMsgBean> dataList;
    private UserInfo user;
    private MP3Recorder mRecorder;//语音录制类
    private int SenderID, pageIndex, pageSize, type, groupId;// 发送者ID,消息页数,消息类型
    /***********
     * 表情
     ***************************/
    private List<String> mEmotioconsList;
    private LinearLayout mDotsLayout;
    private int columns = 6;
    private int rows = 4;
    /**
     * 高德地图
     */
    @ViewInject(R.id.map_chat_position)
    private TextureMapView mShareMap;
    @ViewInject(R.id.ll_share_ing_pos)
    private LinearLayout mLayoutSendPosIng;
    @ViewInject(R.id.tv_share_pos_ing_tag)
    private TextView mSharePosIngTv;
    @ViewInject(R.id.btn_send_pos)
    private ImageButton mBtnSendPos;
    @ViewInject(R.id.spv)
    private SectorProgressView mSectorProgressView;
    @ViewInject(R.id.tv_end_time)
    private TextView mEndTime;
    private AMap aMap;
    private AMapLocationClientOption mLocationOption = null; //定位参数
    private AMapLocationClient mlocationClient;
    private AMapLocation mAMapLocation;
    private Timer timer;    //发送者定时更新自己的经纬度
    private Timer timerReceive; //接收者定时取对方的位置
    private boolean isSharePosIng;
    private Handler mHandler = new Handler() {  //实时共享位置
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 555:
                    countDown++;
                    if (countDown == 3600) {
                        countDown = 0;
                        currentTime = 0;
                        mLayoutSendPosIng.setTag(0);
                        isSharePosIng = false;
                        mBtnSendPos.setVisibility(View.VISIBLE);
                        mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                        mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                        mSharePosIngTv.setText("共享实时位置");
                        if (timer != null) {
                            timer.cancel();
                            timer.purge();
                            timer = null;
                        }

                        //倒计时
                        mSectorProgressView.setVisibility(View.GONE);
                        if (timer2 != null) {
                            timer2.cancel();
                            timer2.purge();
                            timer2 = null;
                        }
                    }
                    mSectorProgressView.setPercent(countDown / 36);
                    break;
                case 444:
                    initLocation();
                    break;
                case 445:
                    List<Integer> userids = new ArrayList<>();
                    if (type == 1) { //个人聊天
                        userids.add(SenderID);
                    }
                    getOthersPos(userids);
                    break;
            }
        }
    };

    // 发送者定位时间倒计时
    private Timer timer2;
    private TimerTask task2;
    private int countDown = 0;
    private long currentTime;
    /////////////////////////////////////////////////////////////////

    private boolean keybordStateFlag = false;//用于标记键盘的打开或者关闭  true打开   false关闭

    private LinearLayoutManager mLayoutManager;//聊天列表
    private LinearLayoutManager mLayoutManager2;//相机相册列表
    //    private GridLayoutManager mLayoutManager3;//更多功能列表
    private LinearLayoutManager mLayoutManager4;//更多功能列表
    private ArrayList<UserInfo> noteRoles;//添加人员集合
    private List<Integer> userids;//群组用户ID保存
    private ArrayList<UserInfo> sUsers;
    private int selectTeamUserPosition;
    private Dialog mLoadingDialog; // 加载框
    private GroupInfo groupInfo;
    private boolean isAddMsgFlag = false;
    private int reviseIdex = 0;
    private Intent intent;
    private int mainUserId;

    private LinearLayout blankLayout;
    private FileSelectAdapter mFileAdapter; // 文件adapter
    private ArrayList<ApiEntity.Files> mDataList; // 文件列表数据
    private Stack<ArrayList<ApiEntity.Files>> mDataListBackStack = new Stack<>(); //列表数据缓存，快速返回
    private int fileID = -10; //当前文件夹ID

    private Bundle savedInstanceState;

    @ViewInject(R.id.itv_chat_asr)//实时语音识别
    private ImageView itv_chat_asr;
    private EventManager asr;
    private String final_result;
    private View cover;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);//允许Activity右滑退出
        Intent intent = new Intent();
        intent.setAction("cc.emw.mobile.person_server");
        intent.setPackage("cc.emw.mobile");
        bindService(intent, PutUserInfoAllServiceConnection.getConnection(), Context.BIND_AUTO_CREATE);
        this.savedInstanceState = savedInstanceState;
        //        Eyes.setStatusBarLightMode(this, Color.WHITE);
        initBroadcastReceiver();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initSocketOn();//IO socket初始化工作
                initData();
                initView();
                //                initAMap(savedInstanceState);
                initEditView();
                initEvent();
                initFileView();
                initListDilog();
                initPtrFrameLayout();
                initExpression();
                getIntentDatas();


            }
        }, 500);
        initPermission();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法
    }

    private Socket mSocket;
    private final static String TAG = "ChatActivity";

    //初始化IO Socket
    public void initSocketOn() {
        try {
            EMWApplication emwSocket = (EMWApplication) getApplication();
            LogLongUtil.e(TAG, " -------initIOSocketOn----EMW------");
            mSocket = emwSocket.getAppIOSocket();
            if (mSocket == null) {
                try {
                    mSocket = IO.socket(Const.SOCKET_URL);
                } catch (URISyntaxException e) {
                    //LogLongUtil.e(TAG," mSocket ="+e.getMessage());
                }
            }
            LogLongUtil.e(TAG, " -------initIOSocketOn---start------" + mSocket.connected());
            //on监听 消息方法
            mSocket.on(EMWApplication.SOCKET_EVENT_MSG, onNewMessage);
            if (!mSocket.connected()) {
                mSocket.connect();
            }
            LogLongUtil.e(TAG, " -------initIOSocketOn--connect------" + mSocket.connected());
        } catch (Exception ex) {

        }


    }

    TextView textView;
    ChatMsgBean msg;
    String msgStr;
    int userOrGroupType;
    String[] otherText = new String[5];

    @Override
    public void copeText(final TextView textView, final ChatMsgBean msg, final String msgStr, final int userOrGroupType) {
        this.textView = textView;
        this.msg = msg;
        this.msgStr = msgStr;
        this.userOrGroupType = userOrGroupType;
        otherText[0] = "复制";
        if (msg.getOptions() == 2) {
            otherText[1] = "取消收藏";
        } else {
            otherText[1] = "收藏";
        }
        if (msg.getOptions() == 1) {
            otherText[2] = "取消重要消息标记";
        } else {
            otherText[2] = "标记重要消息";
        }
        otherText[3] = "添加日程";
        otherText[4] = "添加任务";
        setTheme(R.style.ActionSheetStyleiOS7);
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(otherText)
                .setCancelableOnTouchOutside(true).setListener(this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (index == 0) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (TextUtils.isEmpty(msgStr)) {
                cm.setText(msg.getContent().toString());
            } else {
                cm.setText(msgStr);
            }
            ToastUtil.showToast(this, "复制成功");
        } else if (index == 1) {
            Intent intent = new Intent();
            intent.setAction(ChatContent.REFRESH_CHAT_UPDATE_MSG);
            intent.putExtra("msg_id", msg.getID() + "");
            if (msg.getOptions() == 2) {
                intent.putExtra("state", 0);
            } else {
                intent.putExtra("state", 2);

            }
            sendBroadcast(intent);
        } else if (index == 2) {
            Intent intent = new Intent();
            intent.setAction(ChatContent.REFRESH_CHAT_CHANGE_MSG);
            intent.putExtra("msg_id", msg.getID() + "");
            if (msg.getOptions() == 1) {
                intent.putExtra("state", 0);
            } else {
                intent.putExtra("state", 1);
            }
            sendBroadcast(intent);
        } else if (index == 3) {
            Intent scheduleIntent = new Intent(this, CalendarCreateActivitys.class);
            scheduleIntent.putExtra("enter_flag", 1);
            scheduleIntent.putExtra("chat_desc", textView.getText().toString());
            scheduleIntent.putExtra("start_anim", false);
            startActivity(scheduleIntent);
        } else if (index == 4) {
            Intent scheduleIntent = new Intent(this, AddTasksWebActivity.class);
            scheduleIntent.putExtra("content", msg.getContent().toString());
            scheduleIntent.putExtra("type", 1);
            startActivity(scheduleIntent);
        }
    }

    //监听消息接收
    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            try {
                //要UI主线程刷新界面
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = (JSONObject) args[0];
                        Message message = new Gson().fromJson(json.toString(), Message.class);
                        //添加消息，并更新界面
                        if (groupId > 0) {
                            if (message.getGroupID() == groupId && message.getSenderID() != mainUserId) {
                                addMsg(message);
                            }
                        } else if (message.getSenderID() == SenderID && message.getGroupID() == groupId) {
                            addMsg(message);
                        }
                        LogLongUtil.e(TAG, "----message = " + message.toString());
                    }
                });
            } catch (Exception ex) {
                LogLongUtil.e(TAG, "--socket Exception ex = " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    };

    //释放Socket绑定信息
    public void destroyIOSocket() {
        LogLongUtil.e(TAG, " -------initIOSocketOff--------");
        try {
            mSocket.off(EMWApplication.SOCKET_EVENT_MSG, onNewMessage);
        } catch (Exception ex) {

        }

    }

    @Override
    protected void startAnimEnd(Bundle savedInstanceState) {
        /*initData();
        initView();
//                initAMap(savedInstanceState);
        initEditView();
        initEvent();
        initFileView();
        initListDilog();
        initPtrFrameLayout();
        initExpression();
        getIntentDatas();*/
    }

    /**
     * 高德地图(实时共享位置)
     *
     * @param savedInstanceState
     */
    private void initAMap(Bundle savedInstanceState) {
        mShareMap.onCreate(savedInstanceState);
        //show my location
        if (aMap == null) {
            aMap = mShareMap.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        mLayoutSendPosIng.setTag(0);
        mBtnSendPos.setTag(0);
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getLongitude() != 0.0 && aMapLocation.getLatitude() != 0.0
                && aMapLocation.getAddress() != null && !TextUtils.isEmpty(aMapLocation.getAddress())) {
            // 停止定位
            mAMapLocation = aMapLocation;
            deactivate();
            if (!isSharePosIng) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude())), 13f));
            }

            for (int i = 0; i < aMap.getMapScreenMarkers().size(); i++) {
                aMap.getMapScreenMarkers().get(i).destroy();
            }
            aMap.addMarker(new MarkerOptions()
                    .position(AMapUtil.convertToLatLng(new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude())))
                    .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(isSharePosIng ? PrefsUtil.readUserInfo().ID : 0))).draggable(true));
            //            sendLocation();
            //            if (isSharePosIng) {
            //                String lola = aMapLocation.getLatitude() + ","
            //                        + aMapLocation.getLongitude();
            //                sendMyPos(lola);
            //            }
        }
    }

    //将view转换为bitmap
    public Bitmap convertViewToBitmap(int id) {
        if (id == 0) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(R.drawable.my_location);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            return bitmap;
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.item_user_map_head, null);
            CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.civ_map_head);
            if (EMWApplication.personMap != null && EMWApplication.personMap.get(id) != null) {
                UserInfo userInfo = EMWApplication.personMap.get(id);
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                        userInfo.Image);
                Picasso.with(this)
                        .load(uri)
                        .resize(DisplayUtil.dip2px(this, 30), DisplayUtil.dip2px(this, 30))
                        .centerCrop()
                        .placeholder(R.drawable.cm_img_head)
                        .error(R.drawable.cm_img_head)
                        .config(Bitmap.Config.ALPHA_8)
                        .into(circleImageView);
            }
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, DisplayUtil.dip2px(this, 30), DisplayUtil.dip2px(this, 30));
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            return bitmap;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 开启定位
            mlocationClient.startLocation();
        }
    }

    /**
     * 更新我的位置
     */
    private void sendMyPos(final String lola) {
        API.UserAPI.ModifyUserAxisById(lola, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    Toast.makeText(ChatActivity.this, "更新位置成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取对方的位置
     */
    private void getOthersPos(final List<Integer> userids) {
        API.UserAPI.GetUserAxisList(null, new RequestCallback<Locations>(Locations.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(ChatActivity.this, "获取位置失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<Locations> respList) {
                for (Locations locationss : respList) {
                    if (locationss.getAxis() != null && !TextUtils.isEmpty(locationss.getAxis())
                            && !locationss.getAxis().equals("0.0,0.0")) {
                        String[] data = locationss.getAxis().split(",");
                        if (data.length == 2) {
                            String lat = data[0];
                            String log = data[1];
                            if (locationss.getAxis() != null) {
                                aMap.clear();
                                for (int i = 0; i < userids.size(); i++) {
                                    MarkerOptions options = new MarkerOptions().position(
                                            AMapUtil.convertToLatLng(new LatLonPoint(
                                                    Double.valueOf(lat), Double
                                                    .valueOf(log)))).title(locationss.getName()).infoWindowEnable(false)
                                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(userids.get(0)))).draggable(false);
                                    aMap.addMarker(options);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //    private void sendLocation() {
    //        if (mAMapLocation != null) {
    //            final LocationBean lb = new LocationBean();
    //            lb.addressName = mAMapLocation.getAddress();
    //            lb.title = mAMapLocation.getPoiName();
    //            lb.longitude = mAMapLocation.getLongitude();
    //            lb.latitude = mAMapLocation.getLatitude();
    //            if (isSharePosIng) {
    //                lb.isShareLocation = true;
    //            } else {
    //                lb.isShareLocation = false;
    //            }
    //
    //            aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
    //                @Override
    //                public void onMapScreenShot(Bitmap bitmap) {
    //                    Log.d("zrjtsss", "---------->in");
    //                    long time = System.currentTimeMillis();
    //                    writeImage(bitmap, time);
    //                    lb.url = EMWApplication.tempPath + time + ".png";
    //                    mPersenter.uploadMapImagePersenter(lb);
    //                    bitmap.recycle();
    //                }
    //
    //                @Override
    //                public void onMapScreenShot(Bitmap bitmap, int i) {
    //                }
    //            });
    //        }
    //    }
    //
    //    public void writeImage(Bitmap bitmap, long timeName) {
    //        OutputStream outputStream = null;
    //        try {
    //            File dir = new File(EMWApplication.tempPath);
    //            if (!dir.exists()) {
    //                dir.mkdirs();
    //            }
    //            File file = new File(EMWApplication.tempPath, timeName + ".png");
    //            boolean falg = file.createNewFile();
    //            if (falg) {
    //                outputStream = new FileOutputStream(file);
    //                if (bitmap != null) {
    //                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
    //                }
    //            } else {
    //                file.delete();
    //                file.createNewFile();
    //                outputStream = new FileOutputStream(file);
    //                if (bitmap != null) {
    //                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
    //                }
    //            }
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        } finally {
    //            if (outputStream != null) {
    //                try {
    //                    outputStream.close();
    //                    bitmap.recycle();
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }
    //    }

    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected ChatNetPersenterImpl<IChatView> createPresent() {
        return new ChatNetPersenterImpl<>(this);
    }

    private void socketEmitJoin(final JoinDataBean joinDataBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null) {
                    if (!mSocket.connected()) {
                        mSocket.connect();
                    }
                    mSocket.emit(EMWApplication.SOCKET_EVENT_JOIN, new Gson().toJson(joinDataBean));
                }
            }
        }).start();
    }

    private void initData() {
        mKeyBoradView = mBottomLayout;
        intent = getIntent();
        mainUserId = new Gson().fromJson(Prefs.getString(KEY_USERINFO, ""), UserInfo.class).ID;
        groupId = intent.getIntExtra("GroupID", 0);
        type = intent.getIntExtra("type", 1);
        SenderID = intent.getIntExtra("SenderID", -1);
        if (SenderID > 0) {
            if (EMWApplication.personMap != null && EMWApplication.personMap.get(SenderID) == null) {
                getUserInfo(SenderID);
            }
        }
        int unReadNum = intent.getIntExtra("unReadNum", 0);
        if (mPersenter == null) {
            mPersenter = createPresent();
        }
        //IO socket 提交join
        socketEmitJoin(new JoinDataBean(mainUserId, SenderID, groupId));
        mPersenter.obtainUserInfo(mainUserId, groupId, type, SenderID, unReadNum, mSocket);
        //Log.e(TAG,"ChatActivity---userInfo mainUserId="+mainUserId+"  groupId ="+groupId+" senderId = "+SenderID);


        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();
        mChatMsgDao = new ChatMsgDBModelImpl();//聊天信息
        dataList = new LinkedList<>();
        handler = new MyHandler(this);
        adapater = new ChatMainAdapter(this, type, mChatMsgDao);//聊天本地聊天记录适配器
        adapater.setListener(this);
        photoAndCameraAdapter = new ChatPhotoAndCameraAdapter(this);//聊天图片
        importanceMsgAdapter = new ChatImportanceMessageAdapter(this);//聊天重要信息
        mRecorder = new MP3Recorder(new File(EMWApplication.audioPath, "voice_" + System.currentTimeMillis() + Math.round(Math.random() * 1000.0D) + ".mp3"));//语音
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager2 = new LinearLayoutManager(this);
        //        mLayoutManager3 = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        mLayoutManager4 = new LinearLayoutManager(this);
        pageIndex = 0;
        pageSize = 20;
        mMediaPlayerManger = MediaPlayerManger.getInstance();
        mLoadingDialog = createLoadingDialog("正在处理...");
        if (groupId != 0) {
            mPersenter.initDatas();
        }
        if (SenderID > 0) {
            if (unReadNum > 0) {
                mPersenter.removeNewMessageBySenderIdPersenter(true);
            }
            HeadsUpManager.getInstant(this).cancel(SenderID);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(SenderID);
                }
            }, 3000);
        }
        screenHeight = ChatActivity.this.getWindow().getDecorView().getRootView().getHeight();
    }

    private void openVodeo(int uid) {
        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
            ToastUtil.showToast(ChatActivity.this, "你暂未开通视频通话服务，请联系管理员申请开通。");
            return;
        }
        if (user != null && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(user.VoipCode)) {
            Intent intentVideo = new Intent(ChatActivity.this, VideoConverseActivity.class);
            intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intentVideo.putExtra("userName", name);
            intentVideo.putExtra("userId", user.VoipCode);
            intentVideo.putExtra("call_phone", user.VoipCode);
            intentVideo.putExtra("call_position", "");
            startActivity(intentVideo);
            send_msg = "[视频通话]";
            Message msg = createMessageBean(normal_mes, send_msg);
            final ChatMsgBean chatMsgBean = addMsg(msg);
            final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
            ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                    PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
            mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
        } else {
            ToastUtil.showToast(ChatActivity.this, "对方暂未开通视频通话服务，请联系管理员申请开通。");
        }*/
        //融云语音视频验证
        if (TextUtils.isEmpty(PrefsUtil.readUserInfo().RongYunToken)) {
            ToastUtil.showToast(ChatActivity.this, "你暂未开通语音通话服务，请联系管理员申请开通。");
            return;
        }
        /*RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(ChatActivity.this, getString(R.string.rc_voip_call_start_fail), Toast.LENGTH_SHORT).show();
            return;
        }*/
        //网络判断
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(ChatActivity.this, getString(R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        user = EMWApplication.personMap.get(uid);
        if (user != null && !TextUtils.isEmpty(user.RongYunToken)) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            intent.putExtra("targetId", String.valueOf(user.ID));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(getPackageName());
            startActivity(intent);
        } else {
            ToastUtil.showToast(ChatActivity.this, "对方暂未开通视频通话服务，请联系管理员申请开通。");
        }
    }

    private void openMultiVideo(boolean isTemp) {
        Intent intent = new Intent(this, CallSelectMemberActivity.class);
        ArrayList<String> allMembers = new ArrayList<>();
        if (!isTemp) {
            GroupInfo group = EMWApplication.groupMap.get(groupId);
            if (group != null && group.Users != null && group.Users.size() > 0) {
                for (ApiEntity.UserInfo user : group.Users) {
                    if (user != null) {
                        allMembers.add(Integer.toString(user.ID));
                    }
                }
            }
        } else {
            intent.putExtra(CallSelectMemberActivity.EXTRA_INTO_FLAG, CallSelectMemberActivity.FLAG_CHAT_TEMP);
        }
        intent.putStringArrayListExtra("allMembers", allMembers);
        String myId = Integer.toString(PrefsUtil.readUserInfo().ID);
        ArrayList<String> invited = new ArrayList<>();
        invited.add(myId);
        intent.putStringArrayListExtra("invitedMembers", invited);
        intent.putExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
        if (!isTemp) {
            startActivityForResult(intent, 111);
        } else {
            startActivity(intent);
        }
    }

    private void openCall(int uid) {
        /*if (TextUtils.isEmpty(PrefsUtil.readUserInfo().VoipCode)) {
            ToastUtil.showToast(ChatActivity.this, "你暂未开通语音通话服务，请联系管理员申请开通。");
        }
        if (user != null && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(user.VoipCode)) {
            Intent intentVoice = new Intent(ChatActivity.this, AudioConverseActivity.class);
            intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intentVoice.putExtra("userName", name);
            intentVoice.putExtra("userId", user.VoipCode);
            intentVoice.putExtra("call_phone", user.VoipCode);
            intentVoice.putExtra("call_type", 1);//免费电话
            intentVoice.putExtra("call_head", user.Image);
            intentVoice.putExtra("call_phones", user.Phone);
            startActivity(intentVoice);
            send_msg = "[语音通话]";
            Message msg = createMessageBean(normal_mes, send_msg);
            final ChatMsgBean chatMsgBean = addMsg(msg);
            final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
            ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                    PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
            mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
        } else {
            ToastUtil.showToast(ChatActivity.this, "对方暂未开通语音通话服务，请联系管理员申请开通。");
        }*/

        if (TextUtils.isEmpty(PrefsUtil.readUserInfo().RongYunToken)) {
            ToastUtil.showToast(ChatActivity.this, "你暂未开通语音通话服务，请联系管理员申请开通。");
            return;
        }
        /*RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(ChatActivity.this, getString(R.string.rc_voip_call_start_fail), Toast.LENGTH_SHORT).show();
            return;
        }*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(ChatActivity.this, getString(R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        user = EMWApplication.personMap.get(uid);
        if (user != null && !TextUtils.isEmpty(user.RongYunToken)) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            intent.putExtra("targetId", String.valueOf(user.ID));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(getPackageName());
            startActivity(intent);
        } else {
            ToastUtil.showToast(ChatActivity.this, "对方暂未开通语音通话服务，请联系管理员申请开通。");
        }
    }

    private void openMultiAudio(boolean isTemp) {
        Intent intent = new Intent(this, CallSelectMemberActivity.class);
        ArrayList<String> allMembers = new ArrayList<>();
        if (!isTemp) {
            GroupInfo group = EMWApplication.groupMap.get(groupId);
            if (group != null && group.Users != null && group.Users.size() > 0) {
                for (ApiEntity.UserInfo user : group.Users) {
                    if (user != null) {
                        allMembers.add(Integer.toString(user.ID));
                    }
                }
            }
        } else {
            intent.putExtra(CallSelectMemberActivity.EXTRA_INTO_FLAG, CallSelectMemberActivity.FLAG_CHAT_TEMP);
        }
        intent.putStringArrayListExtra("allMembers", allMembers);
        String myId = Integer.toString(PrefsUtil.readUserInfo().ID);
        ArrayList<String> invited = new ArrayList<>();
        invited.add(myId);
        intent.putStringArrayListExtra("invitedMembers", invited);
        intent.putExtra("mediaType", RongCallCommon.CallMediaType.AUDIO.getValue());
        if (!isTemp) {
            startActivityForResult(intent, 110);
        } else {
            startActivity(intent);
        }
    }

    /**
     * 视频聊天语音聊天子菜单选择界面以及选择关闭动画
     */
    private void clickContentAppMenuAnim(int pos) {
        closeContentAppAnim(1001);
        switch (pos) {
            case 0:
                if (groupId > 0) {
                    openMultiAudio(false);
                } else {
                    openMultiAudio(true);
                }
                break;
            case 1:
                if (groupId > 0) {
                    Intent intent = new Intent(this, CallSelectMemberActivity.class);
                    ArrayList<String> allMembers = new ArrayList<>();
                    GroupInfo group = EMWApplication.groupMap.get(groupId);
                    if (group != null && group.Users != null && group.Users.size() > 0) {
                        for (ApiEntity.UserInfo user : group.Users) {
                            if (user != null) {
                                allMembers.add(Integer.toString(user.ID));
                            }
                        }
                    }
                    intent.putStringArrayListExtra("allMembers", allMembers);
                    String myId = Integer.toString(PrefsUtil.readUserInfo().ID);
                    ArrayList<String> invited = new ArrayList<>();
                    invited.add(myId);
                    intent.putStringArrayListExtra("invitedMembers", invited);
                    intent.putExtra("mediaType", RongCallCommon.CallMediaType.AUDIO.getValue());
                    intent.putExtra(CallSelectMemberActivity.EXTRA_SELECT_TYPE, CallSelectMemberActivity.RADIO_SELECT);
                    startActivityForResult(intent, 1100);
                } else {
                    openCall(SenderID);
                }
                break;
            case 2:
                if (groupId > 0) {
                    openMultiVideo(false);
                } else {
                    openMultiVideo(true);
                }
                break;
            case 3:
                if (groupId > 0) {
                    Intent intent = new Intent(this, CallSelectMemberActivity.class);
                    ArrayList<String> allMembers = new ArrayList<>();
                    GroupInfo group = EMWApplication.groupMap.get(groupId);
                    if (group != null && group.Users != null && group.Users.size() > 0) {
                        for (ApiEntity.UserInfo user : group.Users) {
                            if (user != null) {
                                allMembers.add(Integer.toString(user.ID));
                            }
                        }
                    }
                    intent.putStringArrayListExtra("allMembers", allMembers);
                    String myId = Integer.toString(PrefsUtil.readUserInfo().ID);
                    ArrayList<String> invited = new ArrayList<>();
                    invited.add(myId);
                    intent.putStringArrayListExtra("invitedMembers", invited);
                    intent.putExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
                    intent.putExtra(CallSelectMemberActivity.EXTRA_SELECT_TYPE, CallSelectMemberActivity.RADIO_SELECT);
                    startActivityForResult(intent, 1110);
                } else {
                    openVodeo(SenderID);
                }
                break;
        }
    }

    private void openPhoto() {
        Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    private void openCamera() {
        Intent intent = new Intent(ChatActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CHAT);
        startActivity(intent);
    }

    /**
     * 照片、视频、拍照子菜单选择界面以及选择关闭动画
     */
    private void clickContentAppMenuAnim2(int pos) {
        switch (pos) {
            case 0://拍照
                openCamera();
                break;
            case 1://我的照片
                openPhoto();
                break;
            case 2://视频
                openCamera();
                break;
            case 3://我的视频
                openPhoto();
                break;
        }
    }

    /**
     * 主转盘
     */
    private boolean closeContentAppAnimFlag = false;

    private void closeContentAppAnim(final int pos) {
        if (pos == 0 || pos == 1) {
            openFunction(pos);
        } else {
            if (mChatButtonMenu.getVisibility() == View.VISIBLE) {
                closeContentAppMenuAnim2();
            }
            if (mChatButtonMenu2.getVisibility() == View.VISIBLE) {
                closeContentAppMenuAnim();
            }
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator animator_xi = ObjectAnimator.ofFloat(mChatButtonRoundnessLayout, "scaleX", 1f, 1.1f, 1.2f, 1.3f, 1.4f, 1f, 0.7f, 0.4f, 0.2f, 0f);
            animator_xi.setDuration(300);
            animator_xi.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mChatButtonRoundnessLayout.setVisibility(View.GONE);
                }
            });
            ObjectAnimator animator_yi = ObjectAnimator.ofFloat(mChatButtonRoundnessLayout, "scaleY", 1f, 1.1f, 1.2f, 1.3f, 1.4f, 1f, 0.7f, 0.4f, 0.2f, 0f);
            animator_yi.setDuration(300);


            ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mItvChatContentApp, "scaleX", 1f, 0.6f, 1f);
            animator_xii.setDuration(300);
            animator_xii.setStartDelay(300);

            ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mItvChatContentApp, "scaleY", 1f, 0.6f, 1f);
            animator_yii.setDuration(300);
            animator_yii.setStartDelay(300);
            set.play(animator_xi).with(animator_yi).with(animator_xii).with(animator_yii);
            set.setDuration(600);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    openFunction(pos);
                    closeContentAppAnimFlag = false;
                }
            });
            set.start();
        }
    }

    private boolean openContentAppAnimFlag = false;

    private void openContentAppAnim() {
        if (!openContentAppAnimFlag) {
            openContentAppAnimFlag = true;
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator animator_xi = ObjectAnimator.ofFloat(mItvChatContentApp, "scaleX", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f, 0f, 1f);
            animator_xi.setDuration(200);
            ObjectAnimator animator_yi = ObjectAnimator.ofFloat(mItvChatContentApp, "scaleY", 1.5f, 1.2f, 1f, 0.5f, 0.7f, 1f, 0f, 1f);
            animator_yi.setDuration(200);


            ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mChatButtonRoundnessLayout, "scaleX", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_xii.setStartDelay(200);
            animator_xii.setDuration(300);
            ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mChatButtonRoundnessLayout, "scaleY", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_yii.setStartDelay(200);
            animator_yii.setDuration(300);


            animator_xii.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mChatButtonRoundnessLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    openContentAppAnimFlag = false;
                }
            });
            set.play(animator_xi).with(animator_yi).with(animator_xii).with(animator_yii);
            set.setDuration(500);
            set.start();
        }
    }

    private boolean openContentAppMenuAnimflag = false;

    private void openContentAppMenuAnim() {
        if (!openContentAppMenuAnimflag) {
            openContentAppMenuAnimflag = true;
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mChatButtonMenu2, "scaleX", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_xii.setDuration(600);
            ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mChatButtonMenu2, "scaleY", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_yii.setDuration(600);
            animator_xii.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mChatButtonMenu2.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    openContentAppMenuAnimflag = false;
                }
            });
            set.play(animator_xii).with(animator_yii);
            set.setDuration(600);
            set.start();
        }
    }

    private boolean openContentAppMenuAnim2Flag = false;

    private void openContentAppMenuAnim2() {
        if (!openContentAppMenuAnim2Flag) {
            openContentAppMenuAnim2Flag = true;
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mChatButtonMenu, "scaleX", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_xii.setDuration(600);
            ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mChatButtonMenu, "scaleY", 0f, 0.5f, 0.8f, 1f, 1.1f, 1);
            animator_yii.setDuration(600);
            animator_xii.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mChatButtonMenu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    openContentAppMenuAnim2Flag = false;
                }
            });
            set.play(animator_xii).with(animator_yii);
            set.setDuration(600);
            set.start();
        }
    }

    private void closeContentAppMenuAnim() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mChatButtonMenu2, "scaleX", 1f, 1.1f, 1f, 0.8f, 0.5f, 0f);
        animator_xii.setDuration(300);
        ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mChatButtonMenu2, "scaleY", 1f, 1.1f, 1f, 0.8f, 0.5f, 0f);
        animator_yii.setDuration(300);
        animator_xii.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mChatButtonMenu2.setVisibility(View.GONE);
            }
        });
        set.play(animator_xii).with(animator_yii);
        set.setDuration(300);
        set.start();
    }

    private void closeContentAppMenuAnim2() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator_xii = ObjectAnimator.ofFloat(mChatButtonMenu, "scaleX", 1f, 1.1f, 1f, 0.8f, 0.5f, 0f);
        animator_xii.setDuration(300);
        ObjectAnimator animator_yii = ObjectAnimator.ofFloat(mChatButtonMenu, "scaleY", 1f, 1.1f, 1f, 0.8f, 0.5f, 0f);
        animator_yii.setDuration(300);
        animator_xii.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mChatButtonMenu.setVisibility(View.GONE);
            }
        });
        set.play(animator_xii).with(animator_yii);
        set.setDuration(300);
        set.start();
    }

    private void initView() {
        /**********************************************
         *设置组件显示或者隐藏*/
        mRlChatShowEdit.setVisibility(View.VISIBLE);
        mEetChatAddText.setVisibility(View.VISIBLE);
        mItvChatContentApp.setVisibility(View.VISIBLE);
        mTvOpenChatAiButton2.setVisibility(View.GONE);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListview.setItemAnimator(new DefaultItemAnimator());
        mListview.setLayoutManager(mLayoutManager);
        adapater.setRecycleView(mListview);
        adapater.setSavedInstanceState(savedInstanceState);
        mListview.setAdapter(adapater);

        mLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvShowPhoto.setItemAnimator(new DefaultItemAnimator());
        mRvShowPhoto.setLayoutManager(mLayoutManager2);
        mRvShowPhoto.setAdapter(photoAndCameraAdapter);
        mMoreAppAdapter = new MoreAppApdater(this);
        //        mLayoutManager3.setOrientation(GridLayoutManager.HORIZONTAL);
        //        mRvShowMoreApp.setItemAnimator(new DefaultItemAnimator());
        //        mRvShowMoreApp.setLayoutManager(mLayoutManager3);
        //        mRvShowMoreApp.setAdapter(mMoreAppAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mChatGroupSelectUserList.setLayoutManager(layoutManager);
        mChatGroupSelectAdapter = new ChatGroupSelectAdapter(this);
        mChatGroupSelectUserList.setAdapter(mChatGroupSelectAdapter);

        mChatButtonMenu.setOnMenuItemClickListener(new ChatButtonRoundnessMenuLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                clickContentAppMenuAnim(pos);
            }
        });
        mChatButtonMenu2.setOnMenuItemClickListener(new ChatButtonRoundnessMenuLayout2.OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                clickContentAppMenuAnim2(pos);
            }
        });

        mChatButtonRoundnessLayout.setOnMenuItemClickListener(new ChatButtonRoundnessLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(View view, int pos) {
                closeContentAppAnim(pos);
            }

            @Override
            public void itemCenterClick(View view) {
                closeContentAppAnim(1001);
            }
        });
        ////        //获取定位信息
        //        MapUtils mapUtils = new MapUtils();
        //        mapUtils.getLonLat(this, new MyLonLatListener());
        showPhotoWindow();
        showImprotanceMessage();
    }


    private void initListDilog2() {
        mAddDialog2 = new ListDialog(this, false);
        mAddDialog2.addItem("发送位置", ChatContent.CHAT_LOCATION);
        mAddDialog2.addItem("共享位置", ChatContent.CHAT_SHARE_LOCATION);
        mAddDialog2.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ChatContent.CHAT_LOCATION:
                        Intent intent = new Intent(ChatActivity.this, ShareLocationActivity.class);
                        intent.putExtra("start_anim", false);
                        startActivity(intent);
                        break;
                    case ChatContent.CHAT_SHARE_LOCATION:
                        Intent sharePosIntent = new Intent(ChatActivity.this, SharePosActivity.class);
                        sharePosIntent.putExtra("start_anim", false);
                        sharePosIntent.putExtra("type", type);
                        startActivity(sharePosIntent);
                        break;
                }
            }
        });
    }

    private void openFunction(int pos) {
        switch (pos) {
            case 0://相机
                closeContentAppAnim(1001);
                mRlLine.setVisibility(View.VISIBLE);
                /*if (mChatButtonMenu2.getVisibility() == View.GONE) {
                    openContentAppMenuAnim();
                    if (mChatButtonMenu.getVisibility() == View.VISIBLE) {
                        closeContentAppMenuAnim2();
                    }
                } else {
                    closeContentAppMenuAnim();
                }*/
                break;
            case 1://视频聊天
                if (mChatButtonMenu.getVisibility() == View.GONE) {
                    if (mChatButtonMenu2.getVisibility() == View.VISIBLE) {
                        closeContentAppMenuAnim();
                    }
                    openContentAppMenuAnim2();
                } else {
                    closeContentAppMenuAnim2();
                }

                break;
            case 2://定位
                if (type == 1) {
                    Intent sharePosIntent = new Intent(ChatActivity.this, SharePosActivity.class);
                    sharePosIntent.putExtra("start_anim", false);
                    sharePosIntent.putExtra("type", type);
                    startActivity(sharePosIntent);
                } else {
                    Intent intent = new Intent(this, ShareLocationActivity.class);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                }
                //                mAddDialog2.show();
                //                        initLocation();
                //                        boolean isShow4 = false;
                //                        if (mRlExpressionRoot.getVisibility() == View.VISIBLE || mRlLine.getVisibility() == View.VISIBLE
                //                                || mRlRoot4.getVisibility() == View.VISIBLE || mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                //                            isShow4 = true;
                //                        }
                //                        if (mRlLine.getVisibility() == View.VISIBLE) {
                //                            mRlLine.setVisibility(View.GONE);
                //                        }
                //                        if (mRlRoot4.getVisibility() == View.VISIBLE) {
                //                            mRlRoot4.setVisibility(View.GONE);
                //                        }
                //                        if (mRlRoot6.getVisibility() == View.VISIBLE) {
                //                            mRlRoot6.setVisibility(View.GONE);
                //                        }
                //                        mIvAddMore.animate().setDuration(300).rotation(90);
                //                        if (isShow4) {
                //                            mRlRoot5.setVisibility(View.VISIBLE);
                //                            isShow4 = false;
                //                        } else {
                //                            openRootView(mRlRoot5, false, false);
                //                        }
                break;
            case 3://语音
                boolean isShow6 = false;
                if (mRlExpressionRoot.getVisibility() == View.VISIBLE || mRlRoot5.getVisibility() == View.VISIBLE || mRlLine.getVisibility() == View.VISIBLE
                        || mRlRoot4.getVisibility() == View.VISIBLE || mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                    isShow6 = true;
                }
                if (keybordStateFlag) {//关闭键盘
                    keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);
                    SystemClock.sleep(100);
                }
                if (mRlRoot4.getVisibility() == View.VISIBLE) {
                    mRlRoot4.setVisibility(View.GONE);
                }
                if (mRlLine.getVisibility() == View.VISIBLE) {
                    mRlLine.setVisibility(View.GONE);
                }
                if (mRlRoot5.getVisibility() == View.VISIBLE) {
                    mRlRoot5.setVisibility(View.GONE);
                }
                if (mRlRoot6.getVisibility() == View.VISIBLE) {
                    mRlRoot6.setVisibility(View.GONE);
                }
                if (mRlLine.getVisibility() == View.GONE) {
                    if (isShow6) {
                        mRlRoot6.setVisibility(View.VISIBLE);
                        isShow6 = false;
                    } else {
                        openRootView(mRlRoot6, false, false);
                    }
                } else {
                    mRlRoot6.setVisibility(View.GONE);
                }
                break;
            case 4://POI附近店面搜索
                //                if (mMapBean != null) {
                Intent intentPOI = new Intent(ChatActivity.this, ChatPOISelectorActivity.class);
                //                    intent.putExtra("longitude", mMapBean.longitude);
                //                    intent.putExtra("latitude", mMapBean.latitude);
                //                    intent.putExtra("cityCode", mMapBean.cityCode);
                intentPOI.putExtra("start_anim", false);
                startActivityForResult(intentPOI, 544);
                //                } else {
                //                    ToastUtil.showToast(ChatActivity.this, "未获取到地里位置信息，请检查网络或GPS");
                //                }
                break;
            case 5://发布消息
                closeContentAppAnim(1001);
                Intent intent = new Intent(ChatActivity.this, ChatInformationActivity.class);
                intent.putExtra("start_anim", false);
                startActivityForResult(intent, 543);
                break;
            case 6://文件
                boolean isShow5 = false;
                if (mRlExpressionRoot.getVisibility() == View.VISIBLE || mRlLine.getVisibility() == View.VISIBLE
                        || mRlRoot4.getVisibility() == View.VISIBLE || mRlRoot5.getVisibility() == View.VISIBLE) {
                    isShow5 = true;
                }
                if (mRlLine.getVisibility() == View.VISIBLE) {
                    mRlLine.setVisibility(View.GONE);
                }
                if (mRlRoot4.getVisibility() == View.VISIBLE) {
                    mRlRoot4.setVisibility(View.GONE);
                }
                if (mRlRoot5.getVisibility() == View.VISIBLE) {
                    mRlRoot5.setVisibility(View.GONE);
                }
                if (mRlRoot6.getVisibility() == View.VISIBLE) {
                    mRlRoot6.setVisibility(View.GONE);
                }
                if (isShow5) {
                    mRlFileViewGroup.setVisibility(View.VISIBLE);
                    isShow5 = false;
                } else {
                    openRootView(mRlFileViewGroup, false, false);
                }
                //                mRlChatMsgRoot.setVisibility(View.GONE);
                //                mRlFileRoot.setVisibility(View.VISIBLE);
                break;
        }
    }


    private PopupWindow popImprotanceView;

    private void showImprotanceMessage() {
        View contentView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.chat_importance_message_list_view_layout, null);
        popImprotanceView = new PopupWindow(this);
        //转移焦点，可以在弹框内进行操作
        popImprotanceView.setFocusable(true);
        //设置popupWindow以外的区域可以相应触摸事件
        popImprotanceView.setOutsideTouchable(true);
        //当点击popupWindow以外的区域，则会消失
        popImprotanceView.setBackgroundDrawable(new ColorDrawable());
        popImprotanceView.setContentView(contentView);
        popImprotanceView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        int height = DisplayUtil.getDisplayHeight(this) - DisplayUtil.dip2px(this, 110) - DisplayUtil.getStatusBarHeight(this);
        if (DisplayUtil.navigationBarExist2(this)) {
            popImprotanceView.setHeight(height - DisplayUtil.getNavigationBarHeight(this));
        }
        popImprotanceView.setHeight(height);
        final LinearLayout mLlImprotanceMsgChangeRoot = (LinearLayout) contentView.findViewById(R.id.ll_improtance_msg_change_root);
        TextView mTvImprotanceAllIn = (TextView) contentView.findViewById(R.id.tv_improtance_all_in);
        TextView mTvImprotanceDelete = (TextView) contentView.findViewById(R.id.tv_improtance_delete);
        EventRecyclerView mRvChatImportanceMessage = (EventRecyclerView) contentView.findViewById(R.id.rv_chat_importance_messages);
        ImageView mIvDeleteImprotanceMsgList = (ImageView) contentView.findViewById(R.id.iv_delete_improtance_msg_list);
        final ImageView mIvEditImprotanceMsgList = (ImageView) contentView.findViewById(R.id.iv_edit_improtance_msg_list);
        final TextView mTvRemoveEditImprotanceMsgList = (TextView) contentView.findViewById(R.id.tv_remove_edit_improtance_msg_list);
        mLlImprotanceMsgChangeRoot.setVisibility(View.GONE);
        mTvRemoveEditImprotanceMsgList.setVisibility(View.GONE);

        mRvChatImportanceMessage.setItemAnimator(new DefaultItemAnimator());
        mRvChatImportanceMessage.setLayoutManager(mLayoutManager4);
        mRvChatImportanceMessage.setAdapter(importanceMsgAdapter);
        popImprotanceView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mLlImprotanceMsgChangeRoot.setVisibility(View.GONE);
                mTvRemoveEditImprotanceMsgList.setVisibility(View.GONE);
                mIvEditImprotanceMsgList.setVisibility(View.VISIBLE);
                importanceMsgAdapter.setIsChangeMsgFalg(false);
            }
        });
        mIvDeleteImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popImprotanceView.dismiss();
                importanceMsgAdapter.setIsChangeMsgFalg(false);
                importanceMsgAdapter.notifyDataSetChanged();
            }
        });
        mIvEditImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlImprotanceMsgChangeRoot.setVisibility(View.VISIBLE);
                mIvEditImprotanceMsgList.setVisibility(View.GONE);
                mTvRemoveEditImprotanceMsgList.setVisibility(View.VISIBLE);
                importanceMsgAdapter.setIsChangeMsgFalg(true);
                importanceMsgAdapter.notifyDataSetChanged();
            }
        });
        mTvRemoveEditImprotanceMsgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlImprotanceMsgChangeRoot.setVisibility(View.GONE);
                mIvEditImprotanceMsgList.setVisibility(View.VISIBLE);
                mTvRemoveEditImprotanceMsgList.setVisibility(View.GONE);
                importanceMsgAdapter.setIsChangeMsgFalg(false);
                importanceMsgAdapter.notifyDataSetChanged();
            }
        });
        mTvImprotanceDelete.setOnClickListener(new View.OnClickListener() {
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
                sendBroadcast(intent);
                selectorMessageList.clear();
                importanceMsgAdapter.setSelectorMessageList(selectorMessageList);
            }
        });
        mTvImprotanceAllIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importanceMsgAdapter.getAllSelector();
            }
        });
    }

    private void initEditView() {
        mEetChatAddText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                ChatActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (heightDifferenceed != heightDifference) {
                    try {
                        MoveToPosition(mLayoutManager, mListview, mPersenter.getMessageList().size() - 1);
                    } catch (Exception e) {

                    }
                }
                heightDifferenceed = heightDifference;
                mListview.getChildCount();
            }
        });
        //聊天信息　发送按钮显示出来
        mEetChatAddText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {
                if (str == null || "".equals(str) || str.length() == 0) {
                    mItvChatSendMsg.setVisibility(View.GONE);
                } else {
                    mItvChatSendMsg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                if (str == null || "".equals(str) || str.length() == 0) {
                    mItvChatSendMsg.setVisibility(View.GONE);
                    if (mChatGroupSelectUserList.getVisibility() == View.VISIBLE) {
                        mPersenter.clearParserListPersenter();
                    }
                } else {
                    mItvChatSendMsg.setVisibility(View.VISIBLE);

                    if (!str.toString().contains(" ")) {
                        if (str.toString().startsWith("@")) {//搜索
                            mPersenter.editTextParser(str.toString());
                        }
                    } else {
                        if (mChatGroupSelectUserList.getVisibility() == View.VISIBLE) {
                            mPersenter.clearParserListPersenter();
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable str) {
                if (str == null || "".equals(str) || str.length() == 0) {
                    mItvChatSendMsg.setVisibility(View.GONE);
                } else {
                    mItvChatSendMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        mEetChatAddText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && TextUtils.isEmpty(mEetChatAddText.getText().toString())) {
                    if (mTvOpenChatAiButton2.getVisibility() == View.VISIBLE) {
                        mTvOpenChatAiButton2.setVisibility(View.GONE);
                        mItvChatContentApp.setVisibility(View.VISIBLE);
                        mTvOpenChatAiButton.setVisibility(View.VISIBLE);
                        mItvChatAddEmoticon.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });

        mEetChatAddText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEetChatAddText.setFocusable(true);
                mEetChatAddText.setFocusableInTouchMode(true);//EditText与软键盘建立连接关系
                keybordStateFlag = true;
                if (mRlRoot4.getVisibility() == View.VISIBLE) {
                    mRlRoot4.setVisibility(View.GONE);
                }
                if (mRlRoot6.getVisibility() == View.VISIBLE) {
                    mRlRoot6.setVisibility(View.GONE);
                }
                if (mRlRoot5.getVisibility() == View.VISIBLE) {
                    mRlRoot5.setVisibility(View.GONE);
                }
                if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                    closeContentAppAnim(1001);
                }
                if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {
                    FrameLayout.LayoutParams llParams1 = (FrameLayout.LayoutParams) mRlExpressionRoot.getLayoutParams();
                    llParams1.bottomMargin = -(DisplayUtil.dip2px(ChatActivity.this, 220));
                    mRlExpressionRoot.setLayoutParams(llParams1);
                }
                if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                    mEtFileListSearch.setText("");
                    mRlChatMsgRoot.setVisibility(View.VISIBLE);
                    mRlFileRoot.setVisibility(View.GONE);
                    FrameLayout.LayoutParams llParams1 = (FrameLayout.LayoutParams) mRlFileViewGroup.getLayoutParams();
                    llParams1.bottomMargin = -(DisplayUtil.dip2px(ChatActivity.this, 220));
                    mRlFileViewGroup.setLayoutParams(llParams1);
                }
                if (mRlLine.getVisibility() == View.VISIBLE && photoAndCameraAdapter != null && photoAndCameraAdapter.getPreSurfaceView() != null) {
                    FrameLayout.LayoutParams llParams1 = (FrameLayout.LayoutParams) mRlLine.getLayoutParams();
                    llParams1.bottomMargin = -(DisplayUtil.dip2px(ChatActivity.this, 220));
                    mRlLine.setLayoutParams(llParams1);
                }
                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                    mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
                }
                return false;
            }
        });

        mListview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mRlRoot4.getVisibility() == View.VISIBLE) {
                    mRlRoot4.setVisibility(View.GONE);
                }
                if (mRlRoot5.getVisibility() == View.VISIBLE) {
                    mRlRoot5.setVisibility(View.GONE);
                }
                if (mRlRoot6.getVisibility() == View.VISIBLE) {
                    mRlRoot6.setVisibility(View.GONE);
                }
                if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {
                    mRlExpressionRoot.setVisibility(View.GONE);
                }
                if (mRlLine.getVisibility() == View.VISIBLE && photoAndCameraAdapter != null) {
                    mRlLine.setVisibility(View.GONE);
                }
                if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                    mEtFileListSearch.setText("");
                    mRlFileViewGroup.setVisibility(View.GONE);
                    mRlChatMsgRoot.setVisibility(View.VISIBLE);
                    mRlFileRoot.setVisibility(View.GONE);
                }
                if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                    closeContentAppAnim(1001);
                }
                return false;
            }
        });
        mLlChatRoot.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = mLlChatRoot.getRootView().getHeight() - mLlChatRoot.getHeight();
                        if (heightDiff > 100) { // 说明键盘是弹出状态
                            if (!keybordStateFlag) {
                                keybordStateFlag = true;
                            }
                        } else {
                            if (keybordStateFlag) {
                                keybordStateFlag = false;
                            }
                        }
                        //TODO   整体动画
                        /*Log.e("keyboard", "" + heightDiff);
                        if (heightDiff > 300) {
                            ((LinearLayout) mLlChatTop.getParent()).setBackgroundColor(getResources().getColor(R.color.cm_dialog_bg));
                            ScaleAnimation animation = new ScaleAnimation(1.0f, 0.97f, 1.0f, 0.97f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
                            animation.setDuration(400);//设置动画持续时间
                            animation.setFillAfter(true);
                            mLlChatTop.setAnimation(animation);

                        } else {
                            ((LinearLayout) mLlChatTop.getParent()).setBackgroundColor(Color.WHITE);
                            ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setDuration(400);//设置动画持续时间
                            animation.setFillAfter(true);
                            mLlChatTop.setAnimation(animation);
                        }*/
                    }
                });
    }

    //语音控制
    private void initEvent() {
        mIvDown.setOnTouchListener(touchListener);
        if (type == 2) {//群组专用监听
            mChatGroupSelectAdapter.setOnItemCilckListener(new ChatGroupSelectAdapter.OnItemCilckListener() {
                @Override
                public void itemClick(TextView tv) {
                    mEetChatAddText.setText("@" + tv.getText().toString() + " ");
                }
            });
        }
    }

    private void initFileView() {
        mRb.setVisibility(View.GONE);
        View view = LayoutInflater.from(this).inflate(R.layout.chat_item_file_window, null);
        mRb = (ProgressBar) view.findViewById(R.id.itv_chat_find_file_state_start);
        View backView = view.findViewById(R.id.itv_chat_file_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItvChatFileBack.performClick();
            }
        });
        View sendView = view.findViewById(R.id.itv_chat_send_msg2);
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                ArrayList<ApiEntity.Files> mmfiles = new ArrayList<>();
                mmfiles.addAll(mFileAdapter.getSelectList());
                data.putExtra("select_list", mmfiles);
                onActivityResult(21, Activity.RESULT_OK, data);
                mFileAdapter.setSelectList();
                mFileAdapter.notifyDataSetChanged();
            }
        });
        final ListView listView = (ListView) view.findViewById(R.id.load_more_small_image_list_view);
        blankLayout = (LinearLayout) view.findViewById(R.id.ll_file_blank); // 空视图
        /*//设置搜索监听
        mEtFileListSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 && fileID != -10) {
                    String keyword = mEtFileListSearch.getText().toString().trim();
                    if (fileID == -10) {
                        initFileData();
                    } else if (fileID == -1) {
                        getOtherFileList(keyword, 4, mRb, blankLayout);
                    } else if (fileID == -2) {
                        getOtherFileList(keyword, 6, mRb, blankLayout);
                    } else if (fileID == -3) {
                        getOtherFileList(keyword, 5, mRb, blankLayout);
                    } else {
                        getFileList(keyword, 3, mRb, fileID, blankLayout);
                    }
                }
            }
        });
        mEtFileListSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    if (fileID == -10) {
                        Toast.makeText(ChatActivity.this, "当前目录查找不到文件", Toast.LENGTH_SHORT).show();
                    } else {
                        String keyword = mEtFileListSearch.getText().toString().trim();

                        if (fileID == -10) {
                            initFileData();
                        } else if (fileID == -1) {
                            getOtherFileList(keyword, 4, mRb, blankLayout);
                        } else if (fileID == -2) {
                            getOtherFileList(keyword, 6, mRb, blankLayout);
                        } else if (fileID == -3) {
                            getOtherFileList(keyword, 5, mRb, blankLayout);
                        } else {
                            getFileList(keyword, 3, mRb, fileID, blankLayout);
                        }
                    }
                    return true;
                }
                return false;
            }
        });*/
        mDataList = new ArrayList<>();
        mFileAdapter = new FileSelectAdapter(this, mDataList);
        listView.setAdapter(mFileAdapter);
        mItvChatFileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataListBackStack.size() > 0) { //返回，加载缓存数据
                    blankLayout.setVisibility(View.GONE);
                    mDataList.clear();
                    mDataList.addAll(mDataListBackStack.pop());
                    mFileAdapter.notifyDataSetChanged();
                    if (mDataList.size() > 0) {
                        fileID = mDataList.get(0).ParentID;
                    } else {
                        fileID = -10;
                    }
                }
            }
        });
        initFileData();
        mRlFileViewGroup.addView(view);
    }

    private void initListDilog() {
        mAddDialog = new ListDialog(this, false);
        mAddDialog.addItem("语音通话", ApiEnum.UserNoteAddTypes.Notice);
        mAddDialog.addItem("直拨电话", ApiEnum.UserNoteAddTypes.Share);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ApiEnum.UserNoteAddTypes.Share://直播电话
                        if (user != null && !TextUtils.isEmpty(user.Phone)) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.Phone));
                            startActivity(intent);
                        } else {
                            ToastUtil.showToast(ChatActivity.this, "未能获取到对方手机号码");
                        }
                        break;
                    case ApiEnum.UserNoteAddTypes.Notice://语音电话
                        if (HelpUtil.startVoice(ChatActivity.this, user)) {
                            /*send_msg = "[语音通话]";
                            Message msg = createMessageBean(normal_mes, send_msg);
                            final ChatMsgBean chatMsgBean = addMsg(msg);
                            final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
                            ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                                    PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                            mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);*/
                        }
                        break;
                }
            }
        });
    }

    private void initPtrFrameLayout() {
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean check = PtrDefaultHandler.checkContentCanBePulledDown(frame, mListview, header);
                return check;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageIndex++;
                mPersenter.refreshMessagePersenter();
            }
        });
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
    }

    private void initExpression() {
        List<View> views = new ArrayList<>();
        mEmotioconsList = DefQqEmoticons.sQqEmoticonKeyList;
        View viewPager = View.inflate(ChatActivity.this, R.layout.view_emoticons, null);//设置弹窗的布局
        ViewPager gvPupEmoticons = (ViewPager) viewPager.findViewById(R.id.gv_emoticons);
        gvPupEmoticons.setOnPageChangeListener(new PageChange());
        mDotsLayout = (LinearLayout) viewPager.findViewById(R.id.face_dots_container);//设置小圆点
        // 获取页数view
        for (int i = 0; i < getPagerCount(mEmotioconsList); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        if (mDotsLayout.getChildAt(0) != null) {
            mDotsLayout.getChildAt(0).setSelected(true);
        }
        ChatEmoticonsAdapter emoticonsAdapter = new ChatEmoticonsAdapter(views);
        gvPupEmoticons.setAdapter(emoticonsAdapter);

        mRlExpressionRoot.addView(viewPager);
        mRlExpressionRoot.setVisibility(View.GONE);
    }

    /**********************************************
     * 第一次加载聊天界面的时候初始化聊天信息以及聊天数据列表
     */
    private void getIntentDatas() {
        Log.e("ChatMsgDBModelImpl", "============getChatRecord=========================");
        mPersenter.setTitleNameTextViewPersenter();
        //        mPersenter.initIdInfoPersenter();
        mPersenter.initMessageListPersenter();//获取聊天记录
        /**********************************************
         *初始化群组相关消息
         */
        if (EMWApplication.groupMap != null && EMWApplication.groupMap.get(groupId) != null) {
            mPersenter.getGroupMemberPersenter();
        }
        if (type == 2 && EMWApplication.groupMap.get(groupId) != null && EMWApplication.groupMap.get(groupId).CreateUser == 0) {
            mPersenter.getGroupsByIDPersenter();
        }
        try {
            if (groupId != 0) {//群组设置参数
                EMWApplication.currentChatUid = groupId;
                PutUserInfoAllServiceConnection.getManager().setCurChatID(groupId);
            } else if (SenderID != -1) {
                EMWApplication.currentChatUid = SenderID;
                PutUserInfoAllServiceConnection.getManager().setCurChatID(SenderID);
            }
        } catch (Exception e) {

        }
        sendOtherMessage(intent);
    }

    private void startAnim(boolean isStart) {
        mTvTypeII.setText("左右滑动取消");
        if (isStart) {
            mIvDown.setBackground(getResources().getDrawable(R.drawable.chat_aduio_button));
            mTvTypeI.setVisibility(View.GONE);
            mChronometerType.setVisibility(View.VISIBLE);
            mChronometerType.setBase(SystemClock.elapsedRealtime());
            mChronometerType.setFormat("%S");
            mChronometerType.start();
            isAnimOpen = true;
            voiceStart();
        }
    }

    private void stopAnim() {
        mIvDown.setBackground(getResources().getDrawable(R.drawable.chat_aduio_button_bg));
        mChronometerType.stop();
        mChronometerType.setVisibility(View.GONE);
        mTvTypeI.setVisibility(View.VISIBLE);
        mTvTypeII.setText("左右滑动取消");
        isAnimOpen = false;
    }

    private void moveAnim() {
        mTvTypeII.setText("松开手,取消发送");
    }

    private boolean isCancel;
    private float downX = 0;
    private int[] imgResI = {
            R.drawable.voice_a1
            , R.drawable.voice_a2,
            R.drawable.voice_a3
            , R.drawable.voice_a4,
            R.drawable.voice_a5
            , R.drawable.voice_a6
            , R.drawable.voice_a7
            , R.drawable.voice_a8
            , R.drawable.voice_a9
            , R.drawable.voice_a10
            , R.drawable.voice_a11
            , R.drawable.voice_a12
            , R.drawable.voice_a13
            , R.drawable.chat_bg_null};//图片控件的ID

    private int[] imgResII = {
            R.drawable.voice_b1
            , R.drawable.voice_b2
            , R.drawable.voice_b3
            , R.drawable.voice_b4
            , R.drawable.voice_b5
            , R.drawable.voice_b6
            , R.drawable.voice_b7
            , R.drawable.voice_b8
            , R.drawable.voice_b9
            , R.drawable.voice_b10
            , R.drawable.voice_b11
            , R.drawable.voice_b12
            , R.drawable.voice_b13
            , R.drawable.chat_bg_null};//图片控件的ID
    private boolean isAnimOpen = false;

    private void voiceStart() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 13);
        animator.setDuration(1400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mIvBeginIconI.setBackground(getResources().getDrawable(imgResI[value]));
                mIvBeginIconII.setBackground(getResources().getDrawable(imgResII[value]));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIvBeginIconI.setVisibility(View.VISIBLE);
                mIvBeginIconII.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimOpen) {
                    voiceStart();
                } else {
                    mIvBeginIconI.setVisibility(View.GONE);
                    mIvBeginIconII.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean ret = false;
            int action = event.getAction();
            switch (v.getId()) {
                case R.id.iv_down:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            ret = true;
                            if (mMediaPlayerManger.getIsPlay()) {
                                mMediaPlayerManger.pause();
                            }
                            startAnim(true);
                            initRecorder();/**开始启动语音录制*/
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentX = event.getX();
                            float v1 = downX - currentX;
                            float abs = Math.abs(v1);
                            if (abs > 250) {
                                moveAnim();
                                isCancel = true;
                            } else {
                                isCancel = false;
                                startAnim(false);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            stopAnim();
                            if (isCancel) {
                                isCancel = false;
                                mRecorder.cancel();
                            } else {
                                int duration = getDuration(mChronometerType.getText().toString());
                                switch (duration) {
                                    case -1:
                                        break;
                                    case -2:
                                        break;
                                    default:
                                        mRecorder.stop();
                                        break;
                                }
                            }
                            break;
                    }
                    break;
            }
            return ret;
        }
    };

    private int getDuration(String str) {
        String a = str.substring(0, 1);
        String b = str.substring(1, 2);
        String c = str.substring(3, 4);
        String d = str.substring(4);
        if (a.equals("0") && b.equals("0")) {
            if (c.equals("0") && Integer.valueOf(d) < 1) {
                return -2;
            } else if (c.equals("0") && Integer.valueOf(d) > 1) {
                return Integer.valueOf(d);
            } else {
                return Integer.valueOf(c + d);
            }
        } else {
            return -1;
        }
    }

    private void openRootView(final RelativeLayout view1, final boolean isCloseKeybFlag, final boolean keybFlag) {
        SpringConfig defaultConfig = SpringConfig.fromOrigamiTensionAndFriction(70, 7);
        SpringSystem mSpringSystem = SpringSystem.create();
        Spring mSpring = mSpringSystem.createSpring();
        mSpring.setSpringConfig(defaultConfig);
        mSpring.addListener(new SpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float moveTo = DisplayUtil.dip2px(ChatActivity.this, 220);
                float scale1 = -moveTo + (value * moveTo);
                FrameLayout.LayoutParams llParams1 = (FrameLayout.LayoutParams) view1.getLayoutParams();
                llParams1.bottomMargin = (int) scale1;
                view1.setLayoutParams(llParams1);

            }

            @Override
            public void onSpringAtRest(Spring spring) {
                if (keybFlag) {
                    if (isCloseKeybFlag) {
                        keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);//关闭键盘
                    } else {
                        if (!keybordStateFlag) {
                            keybordStateFlag = KeyBoardUtil.openOrCloseSoftInput(ChatActivity.this);
                        }
                    }
                }
                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                    mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
                }
            }

            @Override
            public void onSpringActivate(Spring spring) {
                view1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSpringEndStateChange(Spring spring) {
            }
        });
        mSpring.setEndValue(1);
    }

    @SuppressWarnings("unused")
    @Event({R.id.itv_chat_add_emoticon, R.id.tv_open_chat_ai_button, R.id.tv_open_chat_ai_button2, R.id.cm_header_btn_left,
            R.id.cm_header_tv_title, R.id.itv_chat_send_msg, R.id.itv_chat_content_app,
            R.id.rl_chat_ai_head_section,
            R.id.iv_chat_more_msg, R.id.itv_chat_send_msg2, R.id.itv_chat_asr})
    private void onclick(View v) {
        switch (v.getId()) {
            case R.id.itv_chat_send_msg2:
                Intent data = new Intent();
                ArrayList<ApiEntity.Files> mmfiles = new ArrayList<>();
                mmfiles.addAll(mFileAdapter.getSelectList());
                data.putExtra("select_list", mmfiles);
                onActivityResult(21, Activity.RESULT_OK, data);
                mFileAdapter.setSelectList();
                mFileAdapter.notifyDataSetChanged();
                break;
            case R.id.ll_share_ing_pos:
                if (mLayoutSendPosIng.getTag().equals(0)) {
                    currentTime = System.currentTimeMillis();
                    mLayoutSendPosIng.setTag(1);
                    isSharePosIng = true;
                    mBtnSendPos.setTag(1);
                    mEndTime.setVisibility(View.GONE);
                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.white));
                    mSharePosIngTv.setTextColor(Color.parseColor("#E50909"));
                    mSharePosIngTv.setText("停止共享");
                    aMap.getMapScreenMarkers().get(0).destroy();
                    aMap.addMarker(new MarkerOptions()
                            .position(AMapUtil.convertToLatLng(new LatLonPoint(mAMapLocation.getLatitude(), mAMapLocation.getLongitude())))
                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(0))).draggable(true));
                    //                    sendLocation();
                    TimerTask task = new TimerTask() {
                        public void run() {
                            mHandler.sendEmptyMessage(444);
                        }
                    };
                    timer = new Timer();
                    timer.schedule(task, 0, 30000); //延时10000ms后执行，10000ms执行一次

                    //倒计时
                    mSectorProgressView.setVisibility(View.VISIBLE);
                    timer2 = new Timer();
                    task2 = new TimerTask() {
                        public void run() {
                            mHandler.sendEmptyMessage(555);
                        }
                    };
                    timer2.schedule(task2, 0, 1000);
                } else if (mLayoutSendPosIng.getTag().equals(1)) {
                    mLayoutSendPosIng.setTag(0);
                    currentTime = 0;
                    isSharePosIng = false;
                    mBtnSendPos.setTag(0);
                    mEndTime.setVisibility(View.VISIBLE);
                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                    mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                    mSharePosIngTv.setText("共享实时位置");
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }

                    //倒计时
                    mSectorProgressView.setPercent(0);
                    mSectorProgressView.setVisibility(View.GONE);
                    if (timer2 != null) {
                        timer2.cancel();
                        timer2.purge();
                        timer2 = null;
                    }
                }
                break;
            case R.id.btn_send_pos:
                if (mBtnSendPos.getTag().equals(0)) {
                    if (mAMapLocation != null) {
                        Intent intent = new Intent();
                        intent.putExtra("longitude", mAMapLocation.getLongitude());
                        intent.putExtra("latitude", mAMapLocation.getLatitude());
                        intent.putExtra("cityCode", mAMapLocation.getCityCode());
                        intent.putExtra("start_anim", false);
                        intent.setClass(this, ShareLocationActivity.class);
                        startActivityForResult(intent, 9821);
                        //                    closeRootView(mRlRoot5);
                        mRlRoot5.setVisibility(View.GONE);
                    } else {
                        ToastUtil.showToast(this, "未获取到位置信息，请检查网络或GPS");
                    }
                } else if (mBtnSendPos.getTag().equals(1)) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            AMapUtil.convertToLatLng(new LatLonPoint(mAMapLocation.getLatitude(), mAMapLocation.getLongitude())), 13f));
                }
                break;
            case R.id.iv_chat_more_msg:
                List<String> pupItemName = new ArrayList<>();
                List<View.OnClickListener> pupItemEvent = new ArrayList<>();
                if (type == 2) {
                    pupItemName.add("提到我的");
                    pupItemName.add("群组详情");
                }
                pupItemName.add("重要消息");
                pupItemName.add("我的收藏");
                if (type == 2) {
                    View.OnClickListener callListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.this, CallMsgActivity.class);
                            intent.putExtra("group_id", groupId);
                            startActivityForResult(intent, 1111);
                        }
                    };
                    pupItemEvent.add(callListener);
                    View.OnClickListener goalDesListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showGoalDes(v);
                        }
                    };
                    pupItemEvent.add(goalDesListener);
                }
                View.OnClickListener importanceMessagesListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, ImportantMsgActivity.class);
                        intent.putExtra("group_id", groupId);
                        intent.putExtra("important_send_id", SenderID);
                        intent.putExtra("start_anim", false);
                        startActivityForResult(intent, 1111);
                    }
                };
                pupItemEvent.add(importanceMessagesListener);
                View.OnClickListener MyStarMessagesListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, MyStarMsgActivity.class);
                        intent.putExtra("group_id", groupId);
                        intent.putExtra("important_send_id", SenderID);
                        intent.putExtra("start_anim", false);
                        startActivityForResult(intent, 1111);
                    }
                };
                pupItemEvent.add(MyStarMessagesListener);
                HintPopupWindow pop = new HintPopupWindow(this, pupItemName, pupItemEvent);
                pop.showPopupWindow(mIvChatMoreMsg);
                pupItemName.clear();
                pupItemEvent.clear();
                break;
            case R.id.itv_chat_add_emoticon://添加表情
                chatAddEmoticon();
                break;
            case R.id.tv_open_chat_ai_button://开启机器人聊天模式
                openAiChat();
                break;
            case R.id.tv_open_chat_ai_button2://关闭机器人聊天模式
                cloaseAiChat();
                break;
            case R.id.itv_chat_content_app://切换功能菜单
                changeContentApp();        //+加号多个功能请求
                break;
            case R.id.itv_chat_send_msg://发送消息
                //                if (mRlChatMsgRoot.getVisibility() == View.VISIBLE && mRlFileRoot.getVisibility() == View.GONE) {
                if (mTvOpenChatAiButton2.getVisibility() == View.GONE) {
                    OnSendBtnClick(mEetChatAddText.getText().toString());
                    mEetChatAddText.setText("");
                } else {
                    String msgSend = mEetChatAddText.getText().toString();
                    if (" ".equals(msgSend) || "".equals(msgSend)) {
                        ToastUtil.showToast(ChatActivity.this, "发送消息不能为空");
                    } else {
                        OnSendBtnClick(ChatContent.CONSULT_EMW + mEetChatAddText.getText().toString());
                        mEetChatAddText.setText(" ");
                        mEetChatAddText.setSelection(mEetChatAddText.getText().length());
                    }
                }
                //                } else {
                //                    Intent data = new Intent();
                //                    ArrayList<ApiEntity.Files> mmfiles = new ArrayList<>();
                //                    mmfiles.addAll(mFileAdapter.getSelectList());
                //                    data.putExtra("select_list", mmfiles);
                //                    onActivityResult(21, Activity.RESULT_OK, data);
                //                    mFileAdapter.setSelectList();
                //                    mFileAdapter.notifyDataSetChanged();
                //                }
                break;
            case R.id.cm_header_btn_left://关闭聊天对话框
                if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                    mChatButtonRoundnessLayout.setVisibility(View.GONE);
                }
                onBackPressed();
                break;
            case R.id.cm_header_tv_title://打开成员详情或者群组详情
                showGoalDes(v);
                break;
            //            case R.id.itv_chat_add_talk://电话聊天
            //                mAddDialog.show();
            //                break;
            case R.id.itv_chat_asr://语音识别文字
                start();
                break;
        }
    }


    private void changeContentApp() {
        if (mTvOpenChatAiButton2.getVisibility() == View.VISIBLE) {
            mTvOpenChatAiButton2.setVisibility(View.GONE);
            mTvOpenChatAiButton.setVisibility(View.VISIBLE);
            mItvChatAddEmoticon.setVisibility(View.VISIBLE);
        }
        if (mRlRoot6.getVisibility() == View.VISIBLE) {
            mRlRoot6.setVisibility(View.GONE);
        }
        if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
            mEetChatAddText.setText("");
            mEtFileListSearch.setText("");
            mRlFileViewGroup.setVisibility(View.GONE);
            mRlChatMsgRoot.setVisibility(View.VISIBLE);
            mRlFileRoot.setVisibility(View.GONE);
        }
        if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {//关闭表情
            mRlExpressionRoot.setVisibility(View.GONE);
        }
        if (mRlLine.getVisibility() == View.VISIBLE && photoAndCameraAdapter != null && photoAndCameraAdapter.getPreSurfaceView() != null) {//关闭相机
            mRlLine.setVisibility(View.GONE);
        }
        if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {//关闭文件
            mRlFileViewGroup.setVisibility(View.GONE);
        }
        if (mRlRoot5.getVisibility() == View.VISIBLE) {//关闭共享位置
            mRlRoot5.setVisibility(View.GONE);
        }
        android.os.Message msgOs = android.os.Message.obtain();
        msgOs.arg1 = 1004;
        handler.sendMessageDelayed(msgOs, 500);
    }

    @Override
    public void onBackPressed() {
        if (isSharePosIng) {
            new AlertDialog(this).builder().setMsg("退出该页面,将停止共享位置功能\n确认退出?")
                    .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text)).setCancelable(false)
                    .setPositiveButton(getString(R.string.confirm),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLayoutSendPosIng.setTag(0);
                                    currentTime = 0;
                                    isSharePosIng = false;
                                    mBtnSendPos.setVisibility(View.VISIBLE);
                                    mEndTime.setVisibility(View.VISIBLE);
                                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                                    mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                                    mSharePosIngTv.setText("共享实时位置");
                                    if (timer != null) {
                                        timer.cancel();
                                        timer.purge();
                                        timer = null;
                                    }

                                    //倒计时
                                    mSectorProgressView.setVisibility(View.GONE);
                                    if (timer2 != null) {
                                        timer2.cancel();
                                        timer2.purge();
                                        timer2 = null;
                                    }
                                    if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                                        mChatButtonRoundnessLayout.setVisibility(View.GONE);
                                    }
                                    onBackPressed();
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
        } else {
            super.onBackPressed();
        }
    }

    /**********************************************
     * 发送消息按钮监听消息方法封装，发送消息
     */
    private void OnSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg.trim())) {
            send_msg = msg;
            Message msgbean = createMessageBean(normal_mes, send_msg);
            if (msgbean.getType() == normal_mes)
                adapater.setIsNewMessage(true);
            final ChatMsgBean chatMsgBean = addMsg(msgbean);
            final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
            ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                    PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
            //  sendSocketChatMessage(message);
            mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, false);//发送聊天给服务器中
            scrollToBottom();//聊天消息
        }
    }

    private void initRecorder() {
        try {
            mRecorder.setRecorderListener(new MP3Recorder.MP3RecorderListener() {
                @Override
                public void recorderCancel() {
                }

                @Override
                public void recorderShort() {
                    ToastUtil.showToast(ChatActivity.this, "录音时间小于1s，请重新录音.");
                }

                @Override
                public void recorderTime(int second) {
                }

                @Override
                public void recorderPath(String path) {
                    mPersenter.uploadAudiosPersenter(path, FileUtil.getMediaLength(path));
                }
            });
            if (mRecorder != null)
                mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**********************************************
     * 关闭机器人聊天模式
     */
    private void cloaseAiChat() {
        mTvOpenChatAiButton2.setVisibility(View.GONE);
        mTvOpenChatAiButton.setVisibility(View.VISIBLE);
        mItvChatAddEmoticon.setVisibility(View.VISIBLE);//打开表情按钮
        mEetChatAddText.setText("");
    }

    /**********************************************
     * 开启机器人聊天模式
     */
    private void openAiChat() {
        mItvChatContentApp.setVisibility(View.VISIBLE);
        mEetChatAddText.setText(" ");
        mEetChatAddText.setSelection(mEetChatAddText.getText().length());
        mItvChatAddEmoticon.setVisibility(View.GONE);//隐藏表情按钮
        if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {//关闭表情
            //            closeRootView(mRlExpressionRoot);
            mRlExpressionRoot.setVisibility(View.GONE);
        }
        if (mRlRoot4.getVisibility() == View.VISIBLE) {
            //            closeRootView(mRlRoot4);
            mRlRoot4.setVisibility(View.GONE);
        }
        if (mRlRoot4.getVisibility() == View.VISIBLE) {
            mRlRoot5.setVisibility(View.GONE);
        }
        if (mRlLine.getVisibility() == View.VISIBLE) {
            mRlLine.setVisibility(View.GONE);
        }
        if (mRlRoot6.getVisibility() == View.VISIBLE) {
            mRlRoot6.setVisibility(View.GONE);
        }

        mTvOpenChatAiButton2.setVisibility(View.VISIBLE);//打开机器人左侧图标
        mTvOpenChatAiButton.setVisibility(View.GONE);//关闭机器人右侧图标
        android.os.Message msg = android.os.Message.obtain();
        msg.arg1 = 1003;
        handler.sendMessageDelayed(msg, 500);

        mEetChatAddText.setFocusable(true);
        mEetChatAddText.setFocusableInTouchMode(true);
        mEetChatAddText.requestFocus();
        mEetChatAddText.findFocus();
        if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
            mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
        }
    }

    /**
     * 再次进入重现数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        //使用此方法可以 保证用户在点击notification时只出现一个聊天页面
        int id = intent.getIntExtra("SenderID", -1);
        int gid = intent.getIntExtra("GroupID", 0);
        if (EMWApplication.groupMap.get(gid) != null && EMWApplication.groupMap.get(gid).CreateUser == 0) {
            mPersenter.getGroupsByIDPersenter();
        }
        if (gid > 0 && gid != EMWApplication.currentChatUid || gid == 0 && id != EMWApplication.currentChatUid) {
            isFinish = true;
            if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                mChatButtonRoundnessLayout.setVisibility(View.GONE);
            }
            finish();
            startActivity(intent);
        } else {
            super.onNewIntent(intent);
        }
    }


    /**********************************************
     * activity返回数据处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 543 && resultCode == RESULT_OK) {//发送动态消息
            ApiEntity.UserNote userNote = (ApiEntity.UserNote) data.getSerializableExtra("user_note");
            //TODO   删除相同消息   后续稳定后考虑封装抽取  与接收消息的去重逻辑并用
            for (int i = mPersenter.getMessageList().size() - 1; i >= 0; i--) {//根据功能特征，做列表倒序循环
                if (mPersenter.getMessageList().get(i).getType() == ChatContent.DYNAMIC) {
                    UserNote userNoteOld = new Gson().fromJson(mPersenter.getMessageList().get(i).getContent(), UserNote.class);
                    if (userNoteOld.ID == userNote.ID) {
                        mChatMsgDao.deleteMsgItem(mPersenter.getMessageList().get(i));
                        mPersenter.removeMessageListItem(i);
                        adapater.setData(mPersenter.getMessageList());
                        adapater.notifyItemRemoved(i);
                        //                        adapater.notifyDataSetChanged();
                        adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
                        break;
                    }
                }
            }
            String sendDynamic = new Gson().toJson(userNote);
            if (sendDynamic != null && !("".equals(sendDynamic))) {
                Message msg = createMessageBean(ChatContent.DYNAMIC, sendDynamic);
                ChatMsgBean chatMsgBean = addMsg(msg);
                int dataListIndex = mPersenter.getMessageList().size() - 1;
                ApiEntity.Message message = new MessageInfo(0, sendDynamic, PrefsUtil.readUserInfo().ID,
                        PrefsUtil.readUserInfo().ID, SenderID, ChatContent.DYNAMIC, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
            }

        }
        if (requestCode == 100 && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //noinspection unchecked
            ArrayList<ImageItem> imageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (imageList != null && imageList.size() > 0 && imageList.get(0) != null) {
                String pathStr = imageList.get(0).path;
                Intent intentImage = new Intent(ChatActivity.this, ShowPhotoActivity.class);
                intentImage.putExtra("photo_uri", pathStr);
                startActivity(intentImage);
            }
        }
        if (requestCode == 21 && resultCode == RESULT_OK) {
            //noinspection unchecked
            ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
            for (ApiEntity.Files userNoteFile : fileList) {
                Files files = new Files();
                files.setLength(userNoteFile.Length);
                files.setName(userNoteFile.Name);
                files.setUrl(userNoteFile.Url);
                files.setID(userNoteFile.ID);

                send_msg = new Gson().toJson(files);
                int attach_mes = 6;
                Message msg = createMessageBean(attach_mes, send_msg);
                final ChatMsgBean chatMsgBean = addMsg(msg);
                final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
                ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                        PrefsUtil.readUserInfo().ID, SenderID, attach_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
            }
        } else if (resultCode == RESULT_OK && requestCode == 22) {
            ArrayList<String> paths = data.getStringArrayListExtra(FilePickerActivity.EXTRA_PATHS);
            if (paths != null) {
                mPersenter.upLoadFilesPersenter(paths);
            }
        }
        if (requestCode == ChatContent.VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            String vPath = data.getStringExtra("videoPath");
            int vTime = data.getIntExtra("videoTime", 0);
            mPersenter.uploadVideoPersenter(vPath, vTime);
        }
        /**
         * 返回人员选择
         */
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            sUsers.clear();
            //noinspection unchecked
            sUsers = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
            if (sUsers.size() > 0) {
                userids.clear();
                for (int i = 0; i < sUsers.size(); i++) {
                    userids.add(sUsers.get(i).ID);
                }
                mPersenter.saveGroupInfoPersenter();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            selectTeamUserPosition = data.getIntExtra("count", -1);
            if (selectTeamUserPosition != -1) {
                new AlertDialog(ChatActivity.this).builder().setMsg("确定要转让群管理权和退出群吗？").setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPersenter.outChatterGroupByCreatorPersenter(groupId, sUsers.get(selectTeamUserPosition).ID);
                        if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                            mChatButtonRoundnessLayout.setVisibility(View.GONE);
                        }
                        finish();
                    }
                }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        }
        if (requestCode == ChatContent.SYSTEM_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
        }

        if (requestCode == 9821 && resultCode == RESULT_OK) {
            LocationBean vPath = data.getParcelableExtra("latLon_point");
            mPersenter.uploadMapImagePersenter(vPath);
        }

        if (requestCode == 110 && resultCode == RESULT_OK) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIAUDIO);
            ArrayList<String> userIds = data.getStringArrayListExtra("invited");
            userIds.add(Integer.toString(PrefsUtil.readUserInfo().ID));
            intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getName().toLowerCase());
            intent.putExtra("targetId", Integer.toString(groupId));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.putStringArrayListExtra("invitedUsers", userIds);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(getPackageName());
            getApplicationContext().startActivity(intent);
        } else if (requestCode == 111 && resultCode == RESULT_OK) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIVIDEO);
            ArrayList<String> userIds = data.getStringArrayListExtra("invited");
            userIds.add(Integer.toString(PrefsUtil.readUserInfo().ID));
            intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getName().toLowerCase());
            intent.putExtra("targetId", Integer.toString(groupId));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.putStringArrayListExtra("invitedUsers", userIds);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(getPackageName());
            getApplicationContext().startActivity(intent);
        } else if (requestCode == 1100 && resultCode == RESULT_OK) {
            ArrayList<String> userIds = data.getStringArrayListExtra("invited");
            if (userIds != null && userIds.size() > 0) {
                openCall(Integer.valueOf(userIds.get(0)));
            }
        } else if (requestCode == 1110 && resultCode == RESULT_OK) {
            ArrayList<String> userIds = data.getStringArrayListExtra("invited");
            if (userIds != null && userIds.size() > 0) {
                openVodeo(Integer.valueOf(userIds.get(0)));
            }
        } else if (requestCode == 1111 && resultCode == RESULT_OK) {
            //MoveToPosition(mLayoutManager, mListview, mPersenter.getMessageList().size() - 1);
            //mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
            ImprotanceMessage message = (ImprotanceMessage) data.getSerializableExtra("message");
            for (int i = mPersenter.getMessageList().size() - 1; i >= 0; i--) {
                ChatMsgBean chatmeg = mPersenter.getMessageList().get(i);
                if (chatmeg.getSenderID() == message.getSenderID() && chatmeg.getContent().equals(message.getContent()) && chatmeg.getCreateTime().equals(message.getCreateTime())) {
                    //MoveToPosition(mLayoutManager, mListview, i);
                    mListview.smoothScrollToPosition(i);
                    return;
                }
            }
            ToastUtil.showToast(this, "该消息太过久远，请手动查找聊天记录");
        }
    }


    private void sendShareMessage(String sendMsg) {
        Message msg = createMessageBean(ApiEnum.MessageType.Share, sendMsg);
        final ChatMsgBean chatMsgBean = addMsg(msg);
        final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
        ApiEntity.Message message = new MessageInfo(0, sendMsg, PrefsUtil.readUserInfo().ID,
                PrefsUtil.readUserInfo().ID, SenderID, ApiEnum.MessageType.Share, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
        // sendSocketChatMessage(message);
        mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
    }

    //    public void sendSocketChatMessage(final ApiEntity.Message message){
    //        new Thread(new Runnable() {
    //            @Override
    //            public void run() {
    //                //Socket socket = SingleIOSocket.getIoSocket();
    //                // Socket socket =
    //                if (!mSocket.connected()){
    //                    mSocket.connect();
    //                }
    //                String messageStr = new Gson().toJson(message);
    //                mSocket.send(messageStr);
    //            }
    //        }).start();
    //    }

    @Override
    public void onDestroy() {
        unbindService(PutUserInfoAllServiceConnection.getConnection());
        if (mOpenVideoChatBroadcastReceiver != null) {
            unregisterReceiver(mOpenVideoChatBroadcastReceiver);
            mOpenVideoChatBroadcastReceiver = null;
        }
        if (mMyFileBroadcastReceiver != null) {
            unregisterReceiver(mMyFileBroadcastReceiver);
            mMyFileBroadcastReceiver = null;
        }
        if (mColseMoreViewBroadcastReceiver != null) {
            unregisterReceiver(mColseMoreViewBroadcastReceiver);
            mColseMoreViewBroadcastReceiver = null;
        }
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
            mMyBroadcastReceiver = null;
        }
        if (mMyImageBroadcastReceiver != null) {
            unregisterReceiver(mMyImageBroadcastReceiver);
            mMyImageBroadcastReceiver = null;
        }
        if (mMyVideoBroadcastReceiver != null) {
            unregisterReceiver(mMyVideoBroadcastReceiver);
            mMyVideoBroadcastReceiver = null;
        }
        if (mMyPhotoBroadcastReceiver != null) {
            unregisterReceiver(mMyPhotoBroadcastReceiver);
            mMyPhotoBroadcastReceiver = null;
        }
        if (mMyCamareBroadcastReceiver != null) {
            unregisterReceiver(mMyCamareBroadcastReceiver);
            mMyCamareBroadcastReceiver = null;
        }
        if (mLoopSendMessageBroadcastReceiver != null) {
            unregisterReceiver(mLoopSendMessageBroadcastReceiver);
            mLoopSendMessageBroadcastReceiver = null;
        }
        if (mMyCamareNewBroadcastReceiver != null) {
            unregisterReceiver(mMyCamareNewBroadcastReceiver);
            mMyCamareNewBroadcastReceiver = null;
        }
        if (mMyVideoNewBroadcastReceiver != null) {
            unregisterReceiver(mMyVideoNewBroadcastReceiver);
            mMyVideoNewBroadcastReceiver = null;
        }
        if (mMyColseRlLinePhotoBroadcastReceiver != null) {
            unregisterReceiver(mMyColseRlLinePhotoBroadcastReceiver);
            mMyColseRlLinePhotoBroadcastReceiver = null;
        }
        if (mMyOpenFileDirBroadcastReceiver != null) {
            unregisterReceiver(mMyOpenFileDirBroadcastReceiver);
            mMyOpenFileDirBroadcastReceiver = null;
        }
        if (mChangeMessageStateBroadcastReceiver != null) {
            unregisterReceiver(mChangeMessageStateBroadcastReceiver);
            mChangeMessageStateBroadcastReceiver = null;
        }
        if (mUpdateMessageStateBroadcastReceiver != null) {
            unregisterReceiver(mUpdateMessageStateBroadcastReceiver);
            mUpdateMessageStateBroadcastReceiver = null;
        }
        if (mColseimprotanceMsgBroadcastReceiver != null) {
            unregisterReceiver(mColseimprotanceMsgBroadcastReceiver);
            mColseimprotanceMsgBroadcastReceiver = null;
        }
        if (mMessageReceivedBroadcastReceiver != null) {
            unregisterReceiver(mMessageReceivedBroadcastReceiver);
            mMessageReceivedBroadcastReceiver = null;
        }
        if (mMyAddUserToNoteFBroadcastReceiver != null) {
            unregisterReceiver(mMyAddUserToNoteFBroadcastReceiver);
            mMyAddUserToNoteFBroadcastReceiver = null;
        }

        if (!isFinish) {
            EMWApplication.currentChatUid = -2;
            try {
                PutUserInfoAllServiceConnection.getManager().setCurChatID(-2);
            } catch (Exception e) {

            }
        }

        isFinish = false;
        MediaPlayerManger.getInstance().pause();
        super.onDestroy();
        //销毁IO_socket资源
        destroyIOSocket();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    //TODO  处理关闭相机、文件、语音功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mRlLine.getVisibility() == View.VISIBLE) {
                mRlLine.setVisibility(View.GONE);
                return false;
            }
            if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                mEetChatAddText.setText("");
                mEtFileListSearch.setText("");
                mRlFileViewGroup.setVisibility(View.GONE);
                mRlRoot5.setVisibility(View.GONE);
                mRlChatMsgRoot.setVisibility(View.VISIBLE);
                mRlFileRoot.setVisibility(View.GONE);
                return false;
            }
            if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                mChatButtonRoundnessLayout.setVisibility(View.GONE);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager       设置RecyclerView对应的manager
     * @param mRecyclerView 当前的RecyclerView
     * @param n             要跳转的位置
     */
    private void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else {
            mRecyclerView.scrollToPosition(n);
        }
    }


    /***********************************************************************************************************************************/
    /***********************************************************************************************************************************/


    /*****************************************************************************************************************************************************************************************/

    private void sendBroadCasts(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private MapBean mapBean;

    //    /**
    //     * 处理新的动态消息
    //     */
    //    @Override
    //    public void onChangeDynamicMessageMoreUserSate(String messageBean) {
    //        if (!"".equals(messageBean)) {  // 如果是当前页面的聊天直接展示
    //            try {
    //                Message msg = new Gson().fromJson(messageBean, Message.class);
    //                deleteOldDynamicMessage(msg);
    //                msg.setCreateTime(msg.getCreateTime());
    //                if (!"".equals(msg.getContent())) {
    //                    if (msg.getGroupID() == EMWApplication.currentChatUid
    //                            || msg.getGroupID() == 0
    //                            && msg.getSenderID() == EMWApplication.currentChatUid) {
    //                        closeImprotanceMessageView();
    //                        ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, 0, false);//将数据添加到数据库  默认消息状态为成功
    //                        mPersenter.getMessageList().add(chatMsgBean);
    //                        adapater.setData(mPersenter.getMessageList());
    //                        adapater.notifyDataSetChanged();
    //                        if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
    //                            mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
    //                        }
    //                        RemoveNewMessageBySenderID(false);
    //                        sendBroadCasts(MainActivity.ACTION_REFRESH_COUNT);
    //                    }
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }


    //    class MyLonLatListener implements MapUtils.LonLatListener {
    //
    //        @Override
    //        public void getLonLat(AMapLocation amapLocation) {
    //            if (amapLocation != null) {
    //                if (amapLocation.getErrorCode() == 0) {
    //                    //定位成功回调信息，设置相关消息
    //                    mapBean = new MapBean();
    //                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
    //                    mapBean.latitude = amapLocation.getLatitude();//获取纬度
    //                    mapBean.longitude = amapLocation.getLongitude();//获取经度
    //                    mapBean.cityCode = amapLocation.getCityCode();
    //                    mMoreAppAdapter.setMapBean(mapBean);
    //                    ChatActivity.this.mMapBean = mapBean;
    //                    amapLocation.getAccuracy();//获取精度信息
    //                } else {
    //                    Toast.makeText(ChatActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //        }
    //    }


    /**
     * 相册展示界面
     */
    private void showPhotoWindow() {
        new Thread() {
            public void run() {
                final List<String> listimage = PhotoUtil.getAllPhoto(getContentResolver());
                handler.post(new Runnable() {
                    public void run() {
                        photoAndCameraAdapter.setData(listimage);
                        photoAndCameraAdapter.notifyDataSetChanged();
                        mListview.smoothScrollToPosition(1);
                    }
                });
            }
        }.start();
    }


    /***************************************************************************************/
    /***************************************************************************************/
    //    private AnimationSet getKeyAnimation() {
    //        AlphaAnimation aaGONE = new AlphaAnimation(0, 1);//设置透明度；设置动画终止的透明度
    //        aaGONE.setDuration(500);//设置动画效果
    //        aaGONE.setFillAfter(true);
    //        ScaleAnimation sa = new ScaleAnimation(1, 1.3f, 1, 1.3f, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
    //        sa.setDuration(1000);//设置动画效果
    //        sa.setRepeatMode(Animation.REVERSE);
    //        sa.setRepeatCount(10000);
    //        AnimationSet set = new AnimationSet(true);
    //        set.addAnimation(aaGONE);
    //        set.addAnimation(sa);
    //        sa.setRepeatMode(Animation.REVERSE);
    //        set.setRepeatCount(1000);
    //        return set;
    //    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private int heightDifferenceed = 0;
    //获取屏幕的高度
    private int screenHeight;


    private void scrollToBottom() {
        mListview.requestLayout();
        mListview.post(new Runnable() {
            @Override
            public void run() {
                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                    mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
                }
            }
        });
    }


    /***********************************************************************************************************************************/
    /***********************************************************************************************************************************/
    /***********************************************************************************************************************************/
    /***********************************************************************************************************************************/
    //Expression
    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.chat_face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        final List<String> subList = new ArrayList<>();
        subList.addAll(mEmotioconsList.subList(position * (columns * rows - 1), (columns * rows - 1) * (position + 1) > mEmotioconsList.size() ? mEmotioconsList.size() : (columns * rows - 1) * (position + 1)));
        if (subList.size() < columns * rows - 1) {
            int remainCount = (columns * rows - 1) - subList.size();
            for (int i = 0; i < remainCount; i++) {
                subList.add("CURSOR");
            }
        }
        subList.add("emotion_del_normal");//末尾添加删除图标   对应的id是：R.id.emotion_del_normal
        ChatFaceGVAdapter mGvAdapter = new ChatFaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!subList.get(position).equals("emotion_del_normal") && !(subList.get(position).equals("CURSOR"))) {//如果不是删除按键则设置
                    insert(getFace(subList.get(position)));
                } else if (subList.get(position).equals("emotion_del_normal")) {
                    delete();
                }
            }
        });

        return gridview;
    }


    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }

    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.chat_dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((mEetChatAddText.getText()));
        int iCursorEnd = Selection.getSelectionEnd((mEetChatAddText.getText()));
        if (iCursorStart != iCursorEnd) {
            (mEetChatAddText.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((mEetChatAddText.getText()));
        (mEetChatAddText.getText()).insert(iCursor, text);
    }


    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            sb.append(png);
            sb.setSpan(
                    new ImageSpan(ChatActivity.this, BitmapFactory.decodeResource(getResources(), DefQqEmoticons.sQqEmoticonHashMap.get(png)))
                    , sb.length() - png.length()
                    , sb.length()
                    , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private int emoticonsFlag = -1;//用于标记是12位的还是13位的，每次使用完毕要回执到-1    0代表12位  1代表13位

    private void delete() {
        if (mEetChatAddText.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(mEetChatAddText.getText());
            int iCursorStart = Selection.getSelectionStart(mEetChatAddText.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        if (emoticonsFlag == 0) {
                            String st1 = "[face:5.png]";
                            (mEetChatAddText.getText()).delete(iCursorEnd - st1.length(), iCursorEnd);
                            emoticonsFlag = -1;
                        } else if (emoticonsFlag == 1) {
                            String st2 = "[face:05.png]";
                            (mEetChatAddText.getText()).delete(iCursorEnd - st2.length(), iCursorEnd);
                            emoticonsFlag = -1;
                        }
                    } else {
                        (mEetChatAddText.getText()).delete(iCursorEnd - 1, iCursorEnd);
                    }
                } else {
                    (mEetChatAddText.getText()).delete(iCursorStart, iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st12 = "[face:5.png]";
        String st13 = "[face:05.png]";
        String content = mEetChatAddText.getText().toString().substring(0, cursor);
        if (content.length() >= st12.length()) {
            String checkStr12 = content.substring(content.length() - st12.length(), content.length());
            String regex12 = "\\[face:[0-9]+\\.png\\]";
            Pattern p1 = Pattern.compile(regex12);
            Matcher m1 = p1.matcher(checkStr12);
            if (m1.matches()) {
                emoticonsFlag = 0;
                return m1.matches();
            } else {
                String checkStr13 = content.substring(content.length() - st13.length(), content.length());
                String regex13 = "\\[face:[0-9][0-9]+\\.png\\]";
                Pattern p2 = Pattern.compile(regex13);
                Matcher m2 = p2.matcher(checkStr13);
                emoticonsFlag = 1;
                return m2.matches();
            }
        }
        return false;
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     */
    private int getPagerCount(List<String> mViews) {
        int count = mViews.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1) : count / (columns * rows - 1) + 1;
    }


    private void sendOtherMessage(Intent intent) {
        // 分享 地图 路径
        if (intent.hasExtra("url_mes")) {
            send_msg = getIntent().getStringExtra("url_mes");
            mPersenter.sendOtherMessagePersenter(send_msg, ChatContent.NORMAL_MSG);
        }
        // 分享动态消息
        if (intent.hasExtra("share")) {
            send_msg = getIntent().getStringExtra("share");
            mPersenter.sendOtherMessagePersenter(send_msg, ApiEnum.MessageType.Share);
        }
        // 转发图片信息
        if (intent.hasExtra("url_image")) {
            send_msg = getIntent().getStringExtra("url_image");
            mPersenter.sendOtherMessagePersenter(send_msg, ChatContent.NORMAL_MSG);
        }
    }

    /**********************************************
     * 用于处理更多的加载更多数据的handler
     */
    static class MyHandler extends Handler {
        WeakReference<ChatActivity> mActivityReference;

        MyHandler(ChatActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            final ChatActivity activity = mActivityReference.get();
            if (activity != null) {
                int arg1 = msg.arg1;
                if (arg1 == 1) {
                    activity.mPersenter.refreshMessagePersenter();
                } else if (arg1 == 1001) {
                    activity.openRootView(activity.mRlExpressionRoot, false, false);
                } else if (arg1 == 1002) {
                    activity.mRlExpressionRoot.setVisibility(View.GONE);
                } else if (arg1 == 1003) {
                    if (!activity.keybordStateFlag) {
                        activity.keybordStateFlag = KeyBoardUtil.openOrCloseSoftInput(activity);
                    }
                } else if (arg1 == 1004) {//开启转盘
                    KeyBoardUtil.closeKeyboard(activity);
                    activity.openContentAppAnim();
                } else if (arg1 == 1005) {
                    activity.mRlExpressionRoot.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private Message createMessageBean(int msgType, String content) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat temporaryTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式yyyy-MM-dd HH:mm:ss
        Message msg = new Message();
        msg.setID(0);
        msg.setContent(content);
        msg.setUserID(PrefsUtil.readUserInfo().ID);
        msg.setSenderID(PrefsUtil.readUserInfo().ID);
        msg.setReceiverID(SenderID);
        msg.setType(msgType);
        msg.setCompanyCode(PrefsUtil.readUserInfo().CompanyCode);
        msg.setCreateTime(temporaryTimeFormat.format(new Date()));
        msg.setGroupID(groupId);
        return msg;
    }

    private ChatMsgBean addMsg(Message msg) {
        closeImprotanceMessageView();
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_SUCCESS, false);
        mPersenter.addMessageListItem(chatMsgBean);
        adapater.setData(mPersenter.getMessageList());
        adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
        if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
            mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
        }
        return chatMsgBean;
    }

    //打开群组详情或者个人主页
    private void showGoalDes(final View v) {
        boolean flag = false;
        if (!TextUtils.isEmpty(name)) {
            flag = true;
        }
        Intent intent1;
        if (type == 1 && flag) {
            intent1 = new Intent(this, PersonInfoActivity.class);
            intent1.putExtra("UserInfo", user);
            intent1.putExtra("start_anim", false);
            int[] location = new int[2];
            v.getLocationInWindow(location);
            intent.putExtra("click_pos_y", location[1]);
            startActivity(intent1);
        } else if (type == 2) {
            if (EMWApplication.groupMap != null && EMWApplication.groupMap.get(groupId) != null) {
                groupInfo = EMWApplication.groupMap.get(groupId);
                ActionSheetDialog dialog = new ActionSheetDialog(
                        ChatActivity.this).builder();
                dialog.addSheetItem("圈子详细信息", null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(ChatActivity.this, ChatTeamInfoActivity3.class);
                                intent.putExtra("groupInfo", groupInfo);
                                intent.putExtra("start_anim", false);
                                startActivity(intent);
                            }
                        });
                if (EMWApplication.groupMap.get(groupId).CreateUser == mainUserId) {
                    dialog.addSheetItem("添加成员", null,
                            new ActionSheetDialog.OnSheetItemClickListener() {

                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(ChatActivity.this, ContactSelectActivity.class);
                                    intent.putExtra("select_list", noteRoles);
                                    intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                                    intent.putExtra("start_anim", false);
                                    intent.putExtra("chat_add_group_user", 1001);//解决进程通信问题，添加成员放到主进程ContactSelectActivity去做，
                                    intent.putExtra("chat_add_group_info", EMWApplication.groupMap.get(groupId));
                                    ChatActivity.this.startActivity(intent);
                                }
                            });
                }
                dialog.addSheetItem("附件", null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent relationIntent = new Intent(ChatActivity.this, RelationFileActivity.class);
                                relationIntent.putExtra("group_info", groupInfo);
                                relationIntent.putExtra("start_anim", false);
                                startActivity(relationIntent);
                            }
                        });
                dialog.addSheetItem("退出并删除聊天记录", ActionSheetDialog.SheetItemColor.Red,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {//群管理员解散群并且退出
                                    if (sUsers.size() != 1) {
                                        Intent selectUserIntent = new Intent(ChatActivity.this, ChatTeamPersonSelectActivity.class);
                                        selectUserIntent.putExtra("userinfo", sUsers);
                                        selectUserIntent.putExtra("groupinfo", groupInfo);
                                        startActivityForResult(selectUserIntent, 2);
                                    } else {
                                        mPersenter.delGroupPersenter(groupInfo.ID);
                                        if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                                            mChatButtonRoundnessLayout.setVisibility(View.GONE);
                                        }
                                        finish();
                                    }

                                } else {//普通群成员退出群
                                    new AlertDialog(ChatActivity.this).builder().setMsg("确认退出群" + groupInfo.Name).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPersenter.delGroupRolesPersenter(groupInfo.ID, PrefsUtil.readUserInfo().ID);
                                            if (mChatButtonRoundnessLayout.getVisibility() == View.VISIBLE) {
                                                mChatButtonRoundnessLayout.setVisibility(View.GONE);
                                            }
                                            finish();
                                        }
                                    }).show();
                                }
                            }
                        });
                dialog.show();
            }
        }
    }

    /**
     * 表情画布打开与关闭
     */
    private void chatAddEmoticon() {
        boolean isShow3 = false;
        /**
         * mRlLine  相机
         mRlRoot5 共享位置
         mRlRoot4更多功能
         mRlExpressionRoot表情
         mRlFileViewGroup文件
         */
        if (mRlLine.getVisibility() == View.VISIBLE || mRlRoot5.getVisibility() == View.VISIBLE
                || mRlRoot4.getVisibility() == View.VISIBLE || mRlFileViewGroup.getVisibility() == View.VISIBLE) {
            isShow3 = true;
        }

        keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);//关闭键盘
        if (mRlRoot4.getVisibility() == View.VISIBLE) {
            mRlRoot4.setVisibility(View.GONE);
        }
        if (mRlRoot5.getVisibility() == View.VISIBLE) {
            mRlRoot5.setVisibility(View.GONE);
        }
        if (mRlRoot6.getVisibility() == View.VISIBLE) {
            mRlRoot6.setVisibility(View.GONE);
        }
        if (mRlLine.getVisibility() == View.VISIBLE) {
            mRlLine.setVisibility(View.GONE);
        }
        if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
            mRlFileViewGroup.setVisibility(View.GONE);
        }
        if (mRlExpressionRoot.getVisibility() == View.GONE) {//如果当前表情画布为隐藏状态则打开
            if (isShow3) {
                mRlExpressionRoot.setVisibility(View.VISIBLE);
                isShow3 = false;
            } else {
                android.os.Message msg = android.os.Message.obtain();
                msg.arg1 = 1001;
                handler.sendMessageDelayed(msg, 200);
            }

        } else if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {//如果当前表情画布为显示状态则隐藏
            //打开多媒体功能栏
            android.os.Message msg = android.os.Message.obtain();
            msg.arg1 = 1002;
            handler.sendMessageDelayed(msg, 200);

        }
        mEetChatAddText.setFocusable(true);
        mEetChatAddText.setFocusableInTouchMode(true);
        mEetChatAddText.requestFocus();
        mEetChatAddText.findFocus();
    }

    //===================================↓ 半屏显示文件选择 ↓=====================================


    private void initFileData() {
        mDataList.clear();
        ApiEntity.Files file1 = new ApiEntity.Files();
        file1.ID = 0;
        file1.Type = 1;
        file1.Name = getString(R.string.file_myfile);
        ApiEntity.Files file2 = new ApiEntity.Files();
        file2.ID = -1;
        file2.Type = 1;
        file2.Name = getString(R.string.file_sharefile);
        ApiEntity.Files file3 = new ApiEntity.Files();
        file3.ID = -2;
        file3.Type = 1;
        file3.Name = getString(R.string.file_mysharefile);
        ApiEntity.Files file4 = new ApiEntity.Files();
        file4.ID = -3;
        file4.Type = 1;
        file4.Name = getString(R.string.file_cancelfile);
        ApiEntity.Files file5 = new ApiEntity.Files();
        file5.ID = -4;
        file5.Type = 1;
        file5.Name = "本地文件";
        mDataList.add(file1);
        mDataList.add(file2);
        mDataList.add(file3);
        mDataList.add(file4);
        mDataList.add(file5);
        mFileAdapter.notifyDataSetChanged();
        mPtrFrameLayout.refreshComplete();
    }

    private void getFileList(String keyword, int type, final ProgressBar mRb, int foldId, final LinearLayout blankLayout) {
        mRb.setVisibility(View.VISIBLE);
        API.UserData.GetFileInfoByFolderId(keyword, type, foldId, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mRb.setVisibility(View.GONE);
                blankLayout.setVisibility(View.GONE);
                if (ex instanceof ConnectException) {
                } else {
                    ToastUtil.showToast(ChatActivity.this, R.string.filelist_list_error);
                }
                if (mDataListBackStack.size() > 0) {
                    mDataListBackStack.pop();
                }
            }

            @Override
            public void onStarted() {
                HelpUtil.hideSoftInput(ChatActivity.this, mEtFileListSearch);
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                mRb.setVisibility(View.GONE);
                blankLayout.setVisibility(respList.size() == 0 ? View.VISIBLE : View.GONE);
                mDataList.clear();
                mDataList.addAll(respList);
                mFileAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getOtherFileList(String keyword, int type, final ProgressBar mRb, final LinearLayout blankLayout) {
        mRb.setVisibility(View.VISIBLE);
        API.UserData.GetNewFilesList(keyword, type, 0, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mRb.setVisibility(View.GONE);
                blankLayout.setVisibility(View.GONE);
                if (!(ex instanceof ConnectException)) {
                    ToastUtil.showToast(ChatActivity.this, R.string.filelist_list_error);
                }
                if (mDataListBackStack.size() > 0) {
                    mDataListBackStack.pop();
                }
            }

            @Override
            public void onStarted() {
                HelpUtil.hideSoftInput(ChatActivity.this, mEtFileListSearch);
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                mRb.setVisibility(View.GONE);
                blankLayout.setVisibility(respList.size() == 0 ? View.VISIBLE : View.GONE);
                mDataList.clear();
                mDataList.addAll(respList);
                mFileAdapter.notifyDataSetChanged();
            }
        });
    }


    public void closeImprotanceMessageView() {
        if (mPersenter.getMessageList().size() != 0) {
            if (mPersenter.getMessageList().get(mPersenter.getMessageList().size() - 1).getType() == ChatContent.IMPROTANCE_MSG) {
                mPersenter.removeMessageListItem(mPersenter.getMessageList().size() - 1);
                adapater.setData(mPersenter.getMessageList());
                //                adapater.notifyDataSetChanged();
                adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                    MoveToPosition(mLayoutManager, mListview, mPersenter.getMessageList().size() - 1);//停留在刷新的位置
                }
            }
        }
    }

    private MediaPlayerManger mMediaPlayerManger;//语音播放工具类，用于判断是否语音正在播放以及对语音播放控件进行操作
    private MyVideoBroadcastReceiver mMyVideoBroadcastReceiver;
    private MyFileBroadcastReceiver mMyFileBroadcastReceiver;
    private MyImageBroadcastReceiver mMyImageBroadcastReceiver;
    private MyPhotoBroadcastReceiver mMyPhotoBroadcastReceiver;
    private MyBroadcastReceiver mMyBroadcastReceiver;
    private MyCamareBroadcastReceiver mMyCamareBroadcastReceiver;
    private LoopSendMessageBroadcastReceiver mLoopSendMessageBroadcastReceiver;
    private MyCamareNewBroadcastReceiver mMyCamareNewBroadcastReceiver;
    private MyVideoNewBroadcastReceiver mMyVideoNewBroadcastReceiver;
    private MyColseRlLinePhotoBroadcastReceiver mMyColseRlLinePhotoBroadcastReceiver;
    private MyOpenFileDirBroadcastReceiver mMyOpenFileDirBroadcastReceiver;
    private ChangeMessageStateBroadcastReceiver mChangeMessageStateBroadcastReceiver;
    private UpdateMessageStateBroadcastReceiver mUpdateMessageStateBroadcastReceiver;
    private ColseimprotanceMsgBroadcastReceiver mColseimprotanceMsgBroadcastReceiver;
    private MessageReceivedBroadcastReceiver mMessageReceivedBroadcastReceiver;
    private ColseMoreViewBroadcastReceiver mColseMoreViewBroadcastReceiver;

    private void initBroadcastReceiver() {
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatContent.REFRESH_CHAT_TEAM_INFO);
        this.registerReceiver(mMyBroadcastReceiver, filter);
        mMyVideoBroadcastReceiver = new MyVideoBroadcastReceiver();
        IntentFilter filterVideo = new IntentFilter();
        filterVideo.addAction(ChatContent.REFRESH_CHAT_VIDEO_INFO);
        this.registerReceiver(mMyVideoBroadcastReceiver, filterVideo);
        mMyImageBroadcastReceiver = new MyImageBroadcastReceiver();
        IntentFilter filterImage = new IntentFilter();
        filterImage.addAction(ChatContent.REFRESH_CHAT_IMAGE_INFO);
        this.registerReceiver(mMyImageBroadcastReceiver, filterImage);
        mMyPhotoBroadcastReceiver = new MyPhotoBroadcastReceiver();
        IntentFilter filterPhoto = new IntentFilter();
        filterPhoto.addAction(ChatContent.REFRESH_CHAT_PHOTO_INFO);
        this.registerReceiver(mMyPhotoBroadcastReceiver, filterPhoto);
        mMyCamareBroadcastReceiver = new MyCamareBroadcastReceiver();
        IntentFilter filterCamare = new IntentFilter();
        filterCamare.addAction(ChatContent.REFRESH_CHAT_CAMARE_INFO);

        this.registerReceiver(mMyCamareBroadcastReceiver, filterCamare);
        mLoopSendMessageBroadcastReceiver = new LoopSendMessageBroadcastReceiver();
        IntentFilter loopSendMsgFilter = new IntentFilter();
        loopSendMsgFilter.addAction(ChatContent.loopSendMessageBean);
        this.registerReceiver(mLoopSendMessageBroadcastReceiver, loopSendMsgFilter);
        mMyCamareNewBroadcastReceiver = new MyCamareNewBroadcastReceiver();
        IntentFilter camareNewFilter = new IntentFilter();
        camareNewFilter.addAction(ChatContent.REFRESH_CHAT_CAMARE_NEW_INFO);
        this.registerReceiver(mMyCamareNewBroadcastReceiver, camareNewFilter);
        mMyColseRlLinePhotoBroadcastReceiver = new MyColseRlLinePhotoBroadcastReceiver();
        IntentFilter openRlLinePhotoFilter = new IntentFilter();
        openRlLinePhotoFilter.addAction(ChatContent.REFRESH_CHAT_COLSE_PHOTO_INFO);
        this.registerReceiver(mMyColseRlLinePhotoBroadcastReceiver, openRlLinePhotoFilter);
        mMyVideoNewBroadcastReceiver = new MyVideoNewBroadcastReceiver();
        IntentFilter videoNewFilter = new IntentFilter();
        videoNewFilter.addAction(ChatContent.REFRESH_CHAT_VIDEO_NEW_INFO);
        this.registerReceiver(mMyVideoNewBroadcastReceiver, videoNewFilter);
        mMyOpenFileDirBroadcastReceiver = new MyOpenFileDirBroadcastReceiver();
        IntentFilter openFileDirFilter = new IntentFilter();
        openFileDirFilter.addAction(ChatContent.REFRESH_CHAT_OPEN_FILE_DIR_INFO);
        this.registerReceiver(mMyOpenFileDirBroadcastReceiver, openFileDirFilter);
        mMyFileBroadcastReceiver = new MyFileBroadcastReceiver();
        IntentFilter openFileFilter = new IntentFilter();
        openFileFilter.addAction(ChatContent.REFRESH_CHAT_OPEN_FILE);
        openFileFilter.addAction(ChatContent.REFRESH_CHAT_OPEN_MAP);    //taozrjt 打开共享位置布局
        openFileFilter.addAction(ChatContent.SEND_SHRRE_POS_MESSAGE);    //taozrjt 共享位置
        openFileFilter.addAction(ChatContent.SEND_UNSHARE_POS_MESSAGE);    //taozrjt 取消共享位置
        this.registerReceiver(mMyFileBroadcastReceiver, openFileFilter);


        mChangeMessageStateBroadcastReceiver = new ChangeMessageStateBroadcastReceiver();
        IntentFilter changeMsgFilter = new IntentFilter();
        changeMsgFilter.addAction(ChatContent.REFRESH_CHAT_CHANGE_MSG);
        this.registerReceiver(mChangeMessageStateBroadcastReceiver, changeMsgFilter);

        mUpdateMessageStateBroadcastReceiver = new UpdateMessageStateBroadcastReceiver();
        IntentFilter updateMsgFilter = new IntentFilter();
        updateMsgFilter.addAction(ChatContent.REFRESH_CHAT_UPDATE_MSG);
        this.registerReceiver(mUpdateMessageStateBroadcastReceiver, updateMsgFilter);

        mColseimprotanceMsgBroadcastReceiver = new ColseimprotanceMsgBroadcastReceiver();
        IntentFilter ColseimprotanceMsgFilter = new IntentFilter();
        ColseimprotanceMsgFilter.addAction(ChatContent.REFRESH_CHAT_COLSE_IMPROTANCE_MSG);
        this.registerReceiver(mColseimprotanceMsgBroadcastReceiver, ColseimprotanceMsgFilter);
        mMessageReceivedBroadcastReceiver = new MessageReceivedBroadcastReceiver();
        IntentFilter messageReceivedMsgFilter = new IntentFilter();
        messageReceivedMsgFilter.addAction(ChatContent.REFRESH_CHAT_RECEIVED_MSG);
        this.registerReceiver(mMessageReceivedBroadcastReceiver, messageReceivedMsgFilter);


        mColseMoreViewBroadcastReceiver = new ColseMoreViewBroadcastReceiver();
        IntentFilter colseMoreViewFilter = new IntentFilter();
        colseMoreViewFilter.addAction(ChatContent.REFRESH_CHAT_COLSE_MORE_VIEW);
        this.registerReceiver(mColseMoreViewBroadcastReceiver, colseMoreViewFilter);

        mOpenVideoChatBroadcastReceiver = new OpenVideoChatBroadcastReceiver();
        IntentFilter OpenVideoChatFilter = new IntentFilter();
        OpenVideoChatFilter.addAction(ChatContent.OPEN_VIDEO_CHAT);
        this.registerReceiver(mOpenVideoChatBroadcastReceiver, OpenVideoChatFilter);

        mMyAddUserToNoteFBroadcastReceiver = new MyAddUserToNoteFBroadcastReceiver();
        IntentFilter addUserToNoteFilter = new IntentFilter();
        addUserToNoteFilter.addAction(ChatContent.ADD_USER_TO_NOTE);
        this.registerReceiver(mMyAddUserToNoteFBroadcastReceiver, addUserToNoteFilter);
    }

    private OpenVideoChatBroadcastReceiver mOpenVideoChatBroadcastReceiver;
    private MyAddUserToNoteFBroadcastReceiver mMyAddUserToNoteFBroadcastReceiver;

    class MyAddUserToNoteFBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapater.addUserToMessageInOf(adapater.getHolder(), intent.getIntExtra("user_note_id", -1), intent.getIntExtra("mesage_id", -1), intent.getBooleanExtra("is_group", false));
        }
    }

    class OpenVideoChatBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (HelpUtil.startVideo(ChatActivity.this, user)) {
                /*send_msg = "[视频通话]";
                Message msg = createMessageBean(normal_mes, send_msg);
                final ChatMsgBean chatMsgBean = addMsg(msg);
                final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
                ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                        PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);*/
            }
        }
    }

    class ColseMoreViewBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);//关闭键盘
            //            closeRootView(mRlRoot4);
            mRlRoot4.setVisibility(View.GONE);
        }
    }
    /*****************************************************************************************************************************************************************************************/
    /**********************************************/
    //接收消息广播
    class MessageReceivedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("msg_json");
            if (!"".equals(message)) {  // 如果是当前页面的聊天直接展示
                try {
                    Message msg = new Gson().fromJson(message, Message.class);
                    if (!"".equals(msg.getContent())) {
                        if (msg.getType() == ChatContent.DYNAMIC) {//对DYNAMIC类型的消息做特殊处理：删除原有的MESSAGE缓存 用新的DYNAMIC做消息替代
                            UserNote userNoteNew = new Gson().fromJson(msg.getContent(), UserNote.class);
                            for (int i = mPersenter.getMessageList().size() - 1; i >= 0; i--) {//根据功能特征，做列表倒序循环
                                if (mPersenter.getMessageList().get(i).getType() == ChatContent.DYNAMIC) {
                                    UserNote userNote = new Gson().fromJson(mPersenter.getMessageList().get(i).getContent()
                                            , UserNote.class);
                                    if (userNote.ID == userNoteNew.ID) {
                                        mChatMsgDao.deleteMsgItem(mPersenter.getMessageList().get(i));
                                        mPersenter.removeMessageListItem(i);
                                        adapater.setData(mPersenter.getMessageList());
                                        //                                        adapater.notifyDataSetChanged();
                                        adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
                                        break;
                                    }
                                }
                            }
                        }
                        if (msg.getType() == ChatContent.NORMAL_MSG) {
                            adapater.setIsNewReceMessage(true);
                        }
                        //共享地理位置
                        // TODO 收到共享位置的消息(判断是开始,还是结束,是个人还是群组)
                        //                        if (msg.getType() == ChatContent.CHAT_SHARE_LOCATION) {
                        //                            LocationBean locationBeanNew = new Gson().fromJson(msg.getContent(), LocationBean.class);
                        //                            if (type == 1) {  //个人聊天
                        //                                if (locationBeanNew.isShareLocation) {   //正在共享位置
                        //                                    aMap.addMarker(new MarkerOptions()
                        //                                            .position(AMapUtil.convertToLatLng(new LatLonPoint(locationBeanNew.latitude, locationBeanNew.longitude)))
                        //                                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(isSharePosIng ? PrefsUtil.readUserInfo().ID : 0))).draggable(true));
                        //                                    TimerTask task = new TimerTask() {
                        //                                        public void run() {
                        //                                            mHandler.sendEmptyMessage(445);
                        //                                        }
                        //                                    };
                        //                                    timerReceive = new Timer();
                        //                                    timerReceive.schedule(task, 0, 30000); //延时10000ms后执行，10000ms执行一次
                        //                                }
                        //                            }
                        //                        }
                        if (msg.getGroupID() == EMWApplication.currentChatUid
                                || msg.getGroupID() == 0
                                && msg.getSenderID() == EMWApplication.currentChatUid
                                || msg.getGroupID() == 0
                                && msg.getUserID() == EMWApplication.currentChatUid) {
                            closeImprotanceMessageView();
                            ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, 0, false);//将数据添加到数据库  默认消息状态为成功
                            mPersenter.addMessageListItem(chatMsgBean);
                            adapater.setData(mPersenter.getMessageList());
                            //                            adapater.notifyDataSetChanged();
                            adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
                            if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                                mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
                            }
                            mPersenter.removeNewMessageBySenderIdPersenter(false);
                            sendBroadCasts(MainActivity.ACTION_REFRESH_COUNT);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sunny----->", "e=" + e);
                }
            }
        }
    }

    class ColseimprotanceMsgBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeImprotanceMessageView();

        }
    }


    class ChangeMessageStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgId = intent.getStringExtra("msg_id");
            int state = intent.getIntExtra("state", 1);
            if (msgId != null && !("".equals(msgId))) {
                mPersenter.updateImportanceMessagePersenter(msgId + "", state, new UpdateAdapter() {
                    @Override
                    public void updateAdapter(int state) {
                        List<ChatMsgBean> list = adapater.getData();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getContent().equals(msg.getContent()) && list.get(i).getCreateTime().equals(msg.getCreateTime())) {
                                list.get(i).setOptions(state);
                                adapater.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                });
            } else {
                ToastUtil.showToast(ChatActivity.this, "服务器异常，请稍后尝试");
            }
        }
    }

    class UpdateMessageStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgId = intent.getStringExtra("msg_id");
            int state = intent.getIntExtra("state", 2);
            if (msgId != null && !("".equals(msgId))) {
                mPersenter.updateImportanceMessagePersenter(msgId + "", state, new UpdateAdapter() {
                    @Override
                    public void updateAdapter(int state) {
                        List<ChatMsgBean> list = adapater.getData();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getContent().equals(msg.getContent()) && list.get(i).getCreateTime().equals(msg.getCreateTime())) {
                                list.get(i).setOptions(state);
                                adapater.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                });
            } else {
                ToastUtil.showToast(ChatActivity.this, "服务器异常，请稍后尝试");
            }
        }
    }

    public interface UpdateAdapter {
        void updateAdapter(int state);
    }

    /**
     * 触发上传图片的广播
     */
    class MyPhotoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String uri = intent.getStringExtra("send_photo_uri");
            mPersenter.uploadImagePersenter(uri);
        }
    }

    class MyVideoNewBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String videoUri = intent.getStringExtra("video_url");
            mPersenter.uploadVideoPersenter(videoUri, 0);
        }
    }

    class MyCamareNewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra("filePath");
            mPersenter.uploadImagePersenter(filePath);
        }
    }

    class MyOpenFileDirBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ApiEntity.Files file = (ApiEntity.Files) intent.getSerializableExtra("fileObject");
            if (mRb.getVisibility() == View.GONE) {
                if (file.Type == 1) { //点击文件夹
                    if (file.ID == -4) { //选择本地文件
                        Intent i = new Intent(ChatActivity.this, BackHandlingFilePickerActivity.class);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                        startActivityForResult(i, 22);
                    } else {
                        ArrayList<ApiEntity.Files> dataList = new ArrayList<>();
                        dataList.addAll(mDataList);
                        mDataListBackStack.push(dataList);
                        fileID = file.ID;
                        String keyword = mEtFileListSearch.getText().toString().trim();
                        if (fileID == -10) {
                            initFileData();
                        } else if (fileID == -1) {
                            getOtherFileList(keyword, 4, mRb, blankLayout);
                        } else if (fileID == -2) {
                            getOtherFileList(keyword, 6, mRb, blankLayout);
                        } else if (fileID == -3) {
                            getOtherFileList(keyword, 5, mRb, blankLayout);
                        } else {
                            getFileList(keyword, 3, mRb, fileID, blankLayout);
                        }
                    }
                } else { //选中或取消文件
                    List<ApiEntity.Files> selectList = mFileAdapter.getSelectList();
                    SparseBooleanArray selectMap = mFileAdapter.getSelectMap();
                    Boolean isSelect = selectMap.get(file.ID);
                    if (isSelect != null) {
                        boolean curSelect = !isSelect;
                        selectMap.put(file.ID, curSelect);
                        if (curSelect) {
                            selectList.add(file);
                        } else {
                            for (int i = 0, size = selectList.size(); i < size; i++) {
                                if (file.ID == selectList.get(i).ID) {
                                    selectList.remove(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        selectMap.put(file.ID, true);
                        selectList.add(file);
                    }
                    mFileAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    class MyColseRlLinePhotoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //            closeRootView(mRlLine);
            mRlLine.setVisibility(View.GONE);
            //            mIvChatAddPhoto.setBackground(getResources().getDrawable(R.drawable.chat_icon_camera));
        }
    }

    /**
     * 刷新数据的广播
     */
    class LoopSendMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeImprotanceMessageView();
            int position = intent.getIntExtra("msg_content", -1);
            if (position != -1) {
                ChatMsgBean chatMsgBean = mPersenter.removeMessageListItem(position);//删除发送消息失败的条目并且提取相应条目的实体类
                mChatMsgDao.deleteMsgItem(chatMsgBean);
                adapater.setData(mPersenter.getMessageList());
                adapater.notifyItemChanged(position);
                adapater.notifyItemChanged(mPersenter.getMessageList().size() - 1);
                if (adapater.getData().size() > 0 && adapater.getData().size() == mPersenter.getMessageList().size()) {
                    mListview.smoothScrollToPosition(mPersenter.getMessageList().size() - 1);
                }
                /**********************************************
                 *重新发送消息向服务器请求
                 */
                send_msg = chatMsgBean.getContent();
                switch (chatMsgBean.getType()) {
                    case ApiEnum.MessageType.Message:
                        OnSendBtnClick(chatMsgBean.getContent());
                        break;
                    case ApiEnum.MessageType.Image:
                        mPersenter.uploadImagePersenter(chatMsgBean.getContent());
                        break;
                    case ApiEnum.MessageType.Audio:
                        Type types = new TypeToken<Audios>() {
                        }.getType();
                        Audios audios = new Gson().fromJson(chatMsgBean.getContent(), types);
                        mPersenter.uploadAudiosPersenter(audios.getUrl(), Integer.parseInt(audios.getLength()));
                        break;
                    case ApiEnum.MessageType.Video:
                        Videos videos = new Gson().fromJson(chatMsgBean.getContent(), Videos.class);
                        mPersenter.uploadVideoPersenter(videos.getUrl(), Integer.parseInt(videos.getLength()));
                        break;
                    case ApiEnum.MessageType.Share:
                        sendShareMessage(chatMsgBean.getContent());
                        break;
                }
            }
        }
    }

    //TODO
    class MyCamareBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //            photoAndCameraAdapter.getPreSurfaceView().openCamera();
        }
    }

    /**
     * 刷新数据的广播
     */
    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("msg_content")) {
                send_msg = intent.getStringExtra("msg_content");
                Message msg = createMessageBean(normal_mes, send_msg);
                final ChatMsgBean chatMsgBean = addMsg(msg);
                final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
                ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID,
                        PrefsUtil.readUserInfo().ID, SenderID, normal_mes, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
            } else if (intent.hasExtra("show_select")) {
                if (mAddDialog != null)
                    mAddDialog.show();
            } else {
                getIntentDatas();
            }
        }
    }

    /**
     * 开启文件
     */
    class MyFileBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ChatContent.REFRESH_CHAT_OPEN_FILE:
                    boolean isShow5 = false;
                    if (mRlExpressionRoot.getVisibility() == View.VISIBLE || mRlLine.getVisibility() == View.VISIBLE
                            || mRlRoot4.getVisibility() == View.VISIBLE || mRlRoot5.getVisibility() == View.VISIBLE) {
                        isShow5 = true;
                    }
                    if (mRlLine.getVisibility() == View.VISIBLE) {
                        //                        closeRootView(mRlLine);
                        mRlLine.setVisibility(View.GONE);
                    }
                    if (mRlRoot4.getVisibility() == View.VISIBLE) {
                        //                        closeRootView(mRlRoot4);
                        mRlRoot4.setVisibility(View.GONE);
                    }
                    if (mRlRoot5.getVisibility() == View.VISIBLE) {
                        //                        closeRootView(mRlRoot5);
                        mRlRoot5.setVisibility(View.GONE);
                    }
                    if (isShow5) {
                        mRlFileViewGroup.setVisibility(View.VISIBLE);
                        isShow5 = false;
                    } else {
                        openRootView(mRlFileViewGroup, false, false);
                    }
                    mRlChatMsgRoot.setVisibility(View.GONE);
                    mRlFileRoot.setVisibility(View.VISIBLE);
                    break;
                case ChatContent.REFRESH_CHAT_OPEN_MAP:
                    //                    initLocation();
                    //                    boolean isShow4 = false;
                    //                    if (mRlExpressionRoot.getVisibility() == View.VISIBLE || mRlLine.getVisibility() == View.VISIBLE
                    //                            || mRlRoot4.getVisibility() == View.VISIBLE || mRlFileViewGroup.getVisibility() == View.VISIBLE) {
                    //                        isShow4 = true;
                    //                    }
                    //                    if (mRlLine.getVisibility() == View.VISIBLE) {
                    ////                        closeRootView(mRlLine);
                    //                        mRlLine.setVisibility(View.GONE);
                    //                    }
                    //                    if (mRlRoot4.getVisibility() == View.VISIBLE) {
                    ////                        closeRootView(mRlRoot4);
                    //                        mRlRoot4.setVisibility(View.GONE);
                    //                    }
                    //                    mIvAddMore.animate().setDuration(300).rotation(90);
                    //                    if (isShow4) {
                    //
                    //                        mRlRoot5.setVisibility(View.VISIBLE);
                    //                        isShow4 = false;
                    //                    } else {
                    //                        openRootView(mRlRoot5, false, false);
                    //                    }
                    break;
                case ChatContent.SEND_SHRRE_POS_MESSAGE:
                    LocationBean lb = intent.getParcelableExtra("lb");
                    mPersenter.uploadMapImagePersenter(lb);
                    break;
                case ChatContent.SEND_UNSHARE_POS_MESSAGE:
                    isSharePosIng = false;
                    Message msg = createMessageBean(ChatContent.CHAT_UNSHARE_LOCATION, "取消了位置共享");
                    final ChatMsgBean chatMsgBean = addMsg(msg);
                    final int dataListIndex = mPersenter.getMessageList().size() - 1;//获取插入数据的位置便于更新状态
                    ApiEntity.Message message = new MessageInfo(0, "取消了位置共享", PrefsUtil.readUserInfo().ID,
                            PrefsUtil.readUserInfo().ID, SenderID, ChatContent.CHAT_UNSHARE_LOCATION, PrefsUtil.readUserInfo().CompanyCode, null, groupId);
                    //  sendSocketChatMessage(message);
                    mPersenter.sendMessagePersenter(type, message, chatMsgBean, dataListIndex, true);
                    break;
            }
        }
    }

    /**
     * 刷新视频数据的广播
     */
    class MyVideoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String vPath = intent.getStringExtra("videoPath");
            int vTime = intent.getIntExtra("videoTime", 0);
            mPersenter.uploadVideoPersenter(vPath, vTime);
        }
    }

    /**
     * 刷新图片数据的广播 SABAI
     */
    class MyImageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(ChatActivity.this);
        }
    }

    private void getUserInfo(int uid) {
        API.UserPubAPI.GetUserInfoByID(uid, new RequestCallback<UserInfo>() {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onParseSuccess(UserInfo respInfo) {
                if (EMWApplication.personMap == null) {
                    EMWApplication.personMap = new SparseArray<>();
                }
                EMWApplication.personMap.put(respInfo.ID, respInfo);
                setTitleNameTextView(respInfo.Name);
            }
        });
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    /*
    * EventListener  回调方法
    * name:回调事件
    * params: JSON数据，其格式如下：
    *
    * */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String result = "";

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            result += "引擎准备就绪，可以开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            result += "检测到用户的已经开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            result += "检测到用户的已经停止说话";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            result += "识别临时识别结果";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            //            Log.d(TAG, "Temp Params:"+params);
            parseAsrPartialJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            result += "识别结束";
            itv_chat_asr.setEnabled(true);
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            Log.d(TAG, "Result Params:" + params);
            parseAsrFinishJsonData(params);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {

        }
        printResult(result);
    }

    private void printResult(String text) {
        //mEetChatAddText.append(text + "\n");
    }

    Button cancel_btn, confirm_btn;
    EmoticonsEditText tv_dialog_content;
    int state;//1为说完了，2为发送

    private void start() {
        if (null == cover) {
            cover = getLayoutInflater().inflate(R.layout.asr_dialog, null);
            cancel_btn = (Button) cover.findViewById(R.id.cancel_btn);
            confirm_btn = (Button) cover.findViewById(R.id.confirm_btn);
            tv_dialog_content = (EmoticonsEditText) cover.findViewById(R.id.tv_dialog_content);
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                    if (null != cover) {
                        FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                        root.removeView(cover);
                        cover = null;
                        itv_chat_asr.setEnabled(true);
                        state = 1;
                    }
                }
            });
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 1) {
                        stop();
                    } else {
                        String str = tv_dialog_content.getText().toString();
                        if (!"".equals(str) && !str.trim().equals("")) {
                            OnSendBtnClick(tv_dialog_content.getText().toString());
                        } else {
                            ToastUtil.showToast(ChatActivity.this, "未识别到有效语音");
                        }
                        if (null != cover) {
                            FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                            root.removeView(cover);
                            cover = null;
                            itv_chat_asr.setEnabled(true);
                        }
                        state = 1;
                    }
                }
            });
            FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
            root.addView(cover);
            state = 1;
        }
        itv_chat_asr.setEnabled(false);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;
        params.put(SpeechConstant.PID, 15362); // 默认1536
        params.put(SpeechConstant.DECODER, 0); // 纯在线(默认)
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN); // 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 3000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);// 是否需要语音音频数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调
        params.put(SpeechConstant.OUT_FILE, "/storage/emulated/0/baiduASR/outfile.pcm");// 音频存放地址

        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        printResult("输入参数：" + json);
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    private void cancel() {
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    private void parseAsrPartialJsonData(String data) {
        Log.d(TAG, "parseAsrPartialJsonData data:" + data);
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(data, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        Log.d(TAG, "resultType:" + resultType);
        if (resultType != null && resultType.equals("final_result")) {
            //只有识别结束才会进入此if
            final_result = jsonData.getBest_result();
        } else if (null != jsonData.getBest_result() && !jsonData.getBest_result().equals("")) {
            tv_dialog_content.setText(jsonData.getBest_result());
        }
    }

    private void parseAsrFinishJsonData(String data) {
        Log.d(TAG, "parseAsrFinishJsonData data:" + data);
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(data, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        if (desc != null && desc.equals("Speech Recognize success.")) {
            tv_dialog_content.setText(final_result);
            state = 2;
            confirm_btn.setText("发送");
            tv_dialog_content.setFocusable(true);
            tv_dialog_content.setFocusableInTouchMode(true);


            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetInfo = connectMgr.getActiveNetworkInfo();
            int type = ConnectivityManager.TYPE_DUMMY;
            if (wifiNetInfo != null) {
                type = wifiNetInfo.getType();
            }
            if (type == ConnectivityManager.TYPE_WIFI) {
                final File file = new File("/storage/emulated/0/baiduASR/outfile.pcm");
                if (file.exists()) {
                    //ToastUtil.showToast(this, "文件存在");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = BASE_URL + "/UploadFileByExport";
                            File files = file;
                            new Thread(UploadFile.uploadFile(url, "1",
                                    "0", "0", files, handlerzhuce)
                            ).start();

                        }
                    }).start();
                }
            }
        } else {
            String errorCode = "\n错误码:" + jsonData.getError();
            String errorSubCode = "\n错误子码:" + jsonData.getSub_error();
            String errorResult = errorCode + errorSubCode;
            //mEetChatAddText.setText("解析错误,原因是:" + desc + "\n" + errorResult);
            tv_dialog_content.setText("");
            ToastUtil.showToast(this, "未识别到有效语音");
            if (null != cover) {
                FrameLayout root = (FrameLayout) findViewById(R.id.asr_main);
                root.removeView(cover);
                cover = null;
                itv_chat_asr.setEnabled(true);
            }
            state = 1;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (state == 2) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                onUserInteraction();
            }
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
            return onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    // 上传和返回结果处理
    final Handler handlerzhuce = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String info = (String) msg.obj;
            //ToastUtil.showToast(ChatActivity.this, "请求结果为：" + info);
        }
    };
}
