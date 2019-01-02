package cc.emw.mobile.main;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.farsunset.cim.client.android.CIMCacheTools;
import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.android.CIMPushManager;
import com.farsunset.cim.client.model.IdentityInfo;
import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.asr.AsrActivity;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.chat.CaptureTestActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.PhoneBookActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.model.bean.Connections;
import cc.emw.mobile.chat.model.bean.EmojiBean;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.chat.model.bean.Information;
import cc.emw.mobile.chat.model.bean.MessageInfo;
import cc.emw.mobile.chat.model.bean.Task;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.ChatSelectActivity;
import cc.emw.mobile.contact.util.InformationRight;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.dynamic.fragment.DynamicFragment;
import cc.emw.mobile.entity.CallInfo;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.LoginActivity2;
import cc.emw.mobile.login.ScannerLoginActivity;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.main.adapter.TemplatesAdapter;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.me.MyInfoActivity;
import cc.emw.mobile.me.PendingEventActivity;
import cc.emw.mobile.me.SettingActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.ApiEntity.Template;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayer;
import cc.emw.mobile.service.StartChatProgressService;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.Prefs;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.DiscussEmoticonsKeyBoard;
import cc.emw.mobile.view.ExViewPager;
import io.rong.callkit.CallSelectContactActivity;
import io.rong.callkit.TerminalLoginDialogActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.socket.client.Socket;
import me.leolin.shortcutbadger.ShortcutBadger;
import q.rorbin.badgeview.QBadgeView;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;

/**
 * 主界面，在用
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements CIMEventListener, LoginView, AMapLocationListener, LocationSource, SwipeRefreshLayout.OnRefreshListener {

    private ExViewPager mViewPager;
    private SlidingMenu mSlidingMenu;
    private ExpandableListView mNavigateElv;
    ///////////////////////////////////////////////////////////////////////右侧导航
    private DisplayImageOptions options;
    //    public static InformationRight informationRight;
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_REFRESH_MAIN = "cc.emw.mobile.refresh_main"; //弹出左侧菜单
    public static final String ACTION_REFRESH_MAIN_RIGHT = "cc.emw.mobile.refresh_main_right"; //弹出右侧菜单
    public static final String ACTION_REFRESH_LEFTMENU = "cc.emw.mobile.refresh_leftmenu"; //刷新左侧菜单
    public static final String ACTION_REFRESH_COUNT = "cc.emw.mobile.refresh_count"; //刷新数量的action
    public static final String ACTION_TALKER_COMMENT = "cc.emw.mobile.talker_comment"; //
    public static final String ACTION_RIGHT_REFRESH = "cc.emw.mobile.right_refresh";  //刷新右侧菜单
    public static final String MESSAGE_ID = "MessageId";
    public static final String MESSAGE_TYPE = "MessageType";
    public static final String ACTION_UNREAD_COUNT = "cc.emw.mobile.unread_count"; //刷新顶部右上角未读数的action
    public static final String ACTION_FINISH_ACTIVITY = "cc.emw.mobile.finish_activity"; //关闭Activity的action
    private MyBroadcastReceive mReceive;
    private SparseArray<Fragment> fragmentMap;
    private boolean isLoginRY; //是否可以登录融云
    private static View view = null; //消息条根View
    public static int count; //未读数量
    private static long TIME = 5000;
    private TemplatesAdapter adapter; //左侧导航adapter
    public static List<Template> templates; //导航分组数据
    public static List<Navigation> navigations; //导航组中数据

    private AMapLocationClientOption mLocationOption = null; //定位参数
    private AMapLocationClient mlocationClient;

    private LoginPresenter loginPresenter;

    //    @ViewInject(R.id.ek_bar)
    public static DiscussEmoticonsKeyBoard ekBar;
    private EMWApplication emwApplication;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    //private SlidingUpPanelLayout slidLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightForSlidingMenu(this);
        try {
            loginPresenter = new LoginPresenter(this);
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            loginPresenter.checkVersion(packInfo.versionCode); //检查版本
            //如果刚从登录界面跳转，无需再次登录获取用户信息
            if (getIntent().getBooleanExtra("isRelogin", true)) {
                String username = Prefs.getString(PrefsUtil.KEY_USERNAME, "");
                String password = Prefs.getString(PrefsUtil.KEY_PASSWORD, "");
                String comcode = Prefs.getString(PrefsUtil.KEY_COMCODE, "");
                loginPresenter.login(username, password, comcode); //登录
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PrefsUtil.setSwitch(false);
        setSwipeBackEnable(false);
        fragmentMap = new SparseArray<>();
        isLoginRY = getIntent().getBooleanExtra("isLoginRY", false);

        initView();
        //        initMsgTipsView();
        getUnreadCount();
        loginRY();
        startPushServer();
        //从服务器获取列表清单数据
        getLeftNavigateList();
        getAllGroupList();
        initLocation();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_MAIN);
        intentFilter.addAction(ACTION_REFRESH_LEFTMENU);
        intentFilter.addAction(ACTION_REFRESH_COUNT);
        intentFilter.addAction(ACTION_REFRESH_MAIN_RIGHT);
        intentFilter.addAction(ACTION_TALKER_COMMENT);
        intentFilter.addAction(ACTION_RIGHT_REFRESH);
        intentFilter.addAction(ACTION_FINISH_ACTIVITY);
        intentFilter.addAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听

        Intent openchatservice = new Intent(this, StartChatProgressService.class);
        openchatservice.setAction("com.tencent.mobileqq.openprocess");
        startService(openchatservice); //先启动与聊天同个进程,让application先初始化
        stopService(openchatservice);

        emwApplication = (EMWApplication) this.getApplication();
        emwApplication.doSocketOnAction();
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

    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        //
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT, true);
        mSlidingMenu.setMenu(R.layout.main_left_menu3);
        mSlidingMenu.setSecondaryMenu(R.layout.right_information_new);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0设置顶部view与状态栏高度一致，不会与状态栏重叠
            int statusbarHeight = DisplayUtil.getStatusBarHeight(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, statusbarHeight);
            findViewById(R.id.content_toppadding).setLayoutParams(lp);
            mSlidingMenu.getMenu().findViewById(R.id.leftmenu_toppadding).setLayoutParams(lp);
            mSlidingMenu.getSecondaryMenu().findViewById(R.id.rightmenu_toppadding).setLayoutParams(lp);
        }
        swipeRefreshLayout = mSlidingMenu.getSecondaryMenu().findViewById(R.id.srl);
        // 设定下拉圆圈的背景
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = mSlidingMenu.getSecondaryMenu().findViewById(R.id.ry);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mSlidingMenu.getSecondaryMenu().findViewById(R.id.rl_type_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopwindows(view);
            }
        });

        mViewPager = (ExViewPager) findViewById(R.id.tab_pager);
        // mViewPager.setPagingEnabled(false); // 禁止左右滑动
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));


        //获取多级菜单控件，显示出来
        mNavigateElv = (ExpandableListView) findViewById(R.id.elv_mainmenu_navigate);
        adapter = new TemplatesAdapter(MainActivity.this);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        mNavigateElv.setAdapter(wrapperAdapter);
        /*mNavigateElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (v.getTag(R.id.tag_second) != null) {
                    Template temp = (Template) v.getTag(R.id.tag_second);
                    switch (temp.ID) {
                        case -1: //Talker
                            sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 0));
                            mSlidingMenu.toggle();
                            break;
                        case -2: //圈子
                            sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 2));
                            mSlidingMenu.toggle();
                            break;
                        case -3: //Time Tracking
                            sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                            mSlidingMenu.toggle();
                            break;
                        case -4: //扫码登录
                            Intent scanIntent = new Intent(MainActivity.this, ScannerLoginActivity.class);
                            startActivity(scanIntent);
                            break;
                        case -5: //路径
                            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                            startActivity(intent);
                            break;
                        case -6: //零售
                            Intent saleIntent = new Intent(MainActivity.this, RetailActivity.class);
                            startActivity(saleIntent);
                            break;
                    }
                }
                return false;
            }
        });*/

        EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    ChatUtils.delClick(ekBar.getEtChat());
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == 2) {
                        if (o instanceof EmoticonEntity) {
                            //						OnSendImage(((EmoticonEntity) o).getIconUri());
                        }
                    } else {
                        String content = null;
                        if (o instanceof EmojiBean) {
                            content = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content = ((EmoticonEntity) o).getContent();
                        }
                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        int index = ekBar.getEtChat().getSelectionStart();
                        Editable editable = ekBar.getEtChat().getText();
                        editable.insert(index, content);
                    }
                }
            }
        };
        ekBar = (DiscussEmoticonsKeyBoard) findViewById(R.id.ek_bar);
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.getBtnSend().setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String content = ekBar.getEtChat().getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(MainActivity.this, R.string.empty_content_tips);
                } else {
                    reply(content, Integer.valueOf(v.getTag(R.id.tag_first).toString())
                            , Boolean.valueOf(v.getTag(R.id.tag_second).toString()), Integer.valueOf(v.getTag(R.id.tag_third).toString()));
                }
            }
        });


        //初始化右侧导航
//        informationRight = new InformationRight(this, mSlidingMenu, true);
//        informationRight.initRightMenu();

        try {

            /*slidLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0 偏移量需要再加上状态栏高度
                int statusbarHeight = DisplayUtil.getStatusBarHeight(this);
                slidLayout.setParallaxOffset(DisplayUtil.dip2px(this, 65) + statusbarHeight);
            }*/
            final CircleImageView headView = (CircleImageView) findViewById(R.id.civ_mainmenu_head);
            final TextView nameView = (TextView) findViewById(R.id.tv_mainmenu_name);
            final View menuLayout = findViewById(R.id.ll_mainmenu_root);
            //final View arrowView = findViewById(R.id.itv_mainmenu_arrow);

            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
            headView.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 60);
            ImageLoader.getInstance().displayImage(uri, headView, options);
            headView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    /*if (slidLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        slidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        Intent meIntent = new Intent(MainActivity.this, MyInfoActivity.class);
                        startActivity(meIntent); //个人中心
                        //                        sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 5));
                        //                        mSlidingMenu.toggle();
                    }*/
                    Intent meIntent = new Intent(MainActivity.this, MyInfoActivity.class);
                    startActivity(meIntent); //个人中心
                }
            });
            nameView.setText(PrefsUtil.readUserInfo().Name);

            final int minWH = DisplayUtil.dip2px(MainActivity.this, 30);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mSlidingMenu.setOnScrollDistanceListener(new SlidingMenu.OnScrollDistanceListener() {
                @Override
                public void onPageScrolled(float positionOffset, int positionOffsetPixels) {
                    params.width = (int) ((positionOffset) * minWH * 2.5);
                    params.height = (int) ((positionOffset) * minWH * 2.5);
                    headView.setLayoutParams(params);
                    nameView.animate().setDuration(0).scaleX(positionOffset).scaleY(positionOffset);
                    nameView.animate().setDuration(0).alpha(positionOffset * 2);
                    menuLayout.animate().setDuration(0).alpha(positionOffset);
                    Intent intent = new Intent();
                    intent.setAction("refresh.head");
                    intent.putExtra("positionOffset", (int) ((0.84 - positionOffset) * minWH) * 4 / 3);
                    sendBroadcast(intent);//发送普通广播
                }
            });
            /*slidLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    Log.i("panel_slid", "onPanelSlide " + slideOffset);
                    params.width = (int) ((1 - slideOffset) * minWH) + minWH;
                    params.height = (int) ((1 - slideOffset) * minWH) + minWH;
                    headView.setLayoutParams(params);

                    nameView.animate().setDuration(0).scaleX(1 - slideOffset).scaleY(1 - slideOffset);
                    nameView.animate().setDuration(0).alpha(1 - slideOffset * 2);
                    menuLayout.animate().setDuration(0).alpha(1 - slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    Log.i("slid", "onPanelStateChanged " + newState);
                    if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        arrowView.animate().setDuration(300).rotation(180);
                    } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        arrowView.animate().setDuration(300).rotation(360);
                    }
                }
            });*/
            findViewById(R.id.ll_mainmenu_talker).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 0));
                    mSlidingMenu.toggle();
                }
            });
            findViewById(R.id.ll_mainmenu_calendar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 1));
                    mSlidingMenu.toggle();
                }
            });
            findViewById(R.id.ll_mainmenu_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 4));
                    mSlidingMenu.toggle();
                }
            });
            findViewById(R.id.ll_mainmenu_group).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 2));
                    mSlidingMenu.toggle();
                    //                    CircularAnim.fullActivity(MainActivity.this, v)
                    //                            .colorOrImageRes(R.color.white)
                    //                            .go(new CircularAnim.OnAnimationEndListener() {
                    //                                @Override
                    //                                public void onAnimationEnd() {
                    //                                    Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                    //                                    startActivity(intent);
                    //                                    mSlidingMenu.toggle();
                    //                                }
                    //                            });
                }
            });
            //个人用户不显示time tracking
            findViewById(R.id.ll_mainmenu_time).setVisibility("PUB".equalsIgnoreCase(PrefsUtil.readUserInfo().CompanyCode) ? View.GONE : View.VISIBLE);
            findViewById(R.id.ll_mainmenu_time).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                    mSlidingMenu.toggle();
                    /*Intent intent = new Intent(MainActivity.this, FormWebActivity.class);
                    intent.putExtra("url", "/Web_App/new_task/index.html");
                    intent.putExtra(FormWebActivity.HAS_PROGRESSBAR, false);
                    startActivity(intent);*/
                }
            });
            findViewById(R.id.ll_mainmenu_scanner).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent scanIntent = new Intent(MainActivity.this, ScannerLoginActivity.class);
                    startActivity(scanIntent);
                }
            });
            //二维码生成测试项
            findViewById(R.id.ll_insert_scanner).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CaptureTestActivity.class);
                    startActivity(intent);
                }
            });
            //语音识别测试项
            findViewById(R.id.ll_insert_asr).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AsrActivity.class);
                    startActivity(intent);
                }
            });
            //通讯录测试项
            findViewById(R.id.ll_insert_phoneBook).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent(MainActivity.this, PhoneBookActivity.class);
                    startActivity(intent);
                }
            });
            //待办工作
            findViewById(R.id.ll_pending_event).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent pendingIntent = new Intent(MainActivity.this, PendingEventActivity.class);
                    startActivity(pendingIntent);
                }
            });
            findViewById(R.id.ll_mainmenu_me).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent meIntent = new Intent(MainActivity.this, MyInfoActivity.class);
                    startActivity(meIntent); //个人中心
                }
            });
            findViewById(R.id.ll_mainmenu_setting).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent setIntent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(setIntent); //设置
                }
            });

        } catch (Exception e) {

        }
    }

    private void showPopwindows(View v) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_circle_type, null);
        final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupView.findViewById(R.id.tv_newst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast(MainActivity.this, "最新");
                window.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_talker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast(MainActivity.this, "talker");
                getChatList();
                window.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast(MainActivity.this, "工作");
                window.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast(MainActivity.this, "通知");
                window.dismiss();
            }
        });

        window.setOutsideTouchable(true);
        window.setFocusable(true);
        window.setAnimationStyle(R.style.popup_more_anim_new);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.showAsDropDown(v, 0, 0);
    }

    public static ArrayList<HistoryMessage> m1 = new ArrayList<>();//Takler

    // 获取聊天消息
    public void getChatList() {
        API.Message.GetChatRecords(new RequestCallback<HistoryMessage>(
                HistoryMessage.class) {

            @Override
            public void onStarted() {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(MainActivity.this, "加载失败，请重试...");
            }

            @Override
            public void onParseSuccess(final List<HistoryMessage> respList) {
                if (respList != null) {
                    m1.clear();
                    for (HistoryMessage historyMessage : respList) {
                        if (historyMessage.getMessage() != null) {
                            m1.add(historyMessage);
                        }
                    }
                    recyclerView.setAdapter(new CommonAdapter<HistoryMessage>(MainActivity.this, R.layout.item_section_char_content, m1) {
                        @Override
                        protected void convert(ViewHolder holder, HistoryMessage msg, int position) {
                            if (msg.getUnReadCount() != 0) {
                                new QBadgeView(holder.itemView.getContext()).bindTarget(holder.getView(R.id.ivHead)).setBadgeNumber(5).setBadgeBackgroundColor(0xffff4141).setBadgeTextColor(0xffFFFFFF)
                                        .stroke(0xffffffff, 1, true).setBadgeTextSize(8, true)
                                        .setGravityOffset(4, 2, true);
                            } else {
                                new QBadgeView(holder.itemView.getContext()).bindTarget(holder.getView(R.id.ivHead)).setBadgeBackgroundColor(0x00000000);
                            }

                            switch (msg.getMessage().getType()) {
                                case 4://4
                                    ChatUtils.spannableEmoticonFilter((TextView) holder.getView(R.id.tvContent), msg.getMessage().getContent());
                                    break;
                                case 9://9
//                                    mvh.tvContent.setText("[视频]");
                                    holder.setText(R.id.tvContent, "[视频]");
                                    break;
                                case 42://取消任务共享
//                                    mvh.tvContent.setText("[取消任务共享]");
                                    holder.setText(R.id.tvContent, "[取消任务共享]");
                                    break;
                                case ApiEnum.MessageType.Audio://8
//                                    mvh.tvContent.setText("[语音]");
                                    holder.setText(R.id.tvContent, "[语音]");
                                    break;
                                case ApiEnum.MessageType.Image://7
//                                    mvh.tvContent.setText("[照片]");
                                    holder.setText(R.id.tvContent, "[照片]");
                                    break;
                                case ApiEnum.MessageType.Attach://6
//                                    mvh.tvContent.setText("[附件]");
                                    holder.setText(R.id.tvContent, "[附件]");
                                    break;
                                case ApiEnum.MessageType.Share://10
                                    Information data = new Gson().fromJson(msg.getMessage().getContent(), Information.class);
                                    String base = "[Talker分享] ";
                                    SpannableString spanStr = new SpannableString(base + data.Content);
                                    ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.dynamicreply_name_text));
                                    spanStr.setSpan(colorSpan1, 0, base.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                                    mvh.tvContent.setText(spanStr);
                                    ((TextView) holder.getView(R.id.tvContent)).setText(spanStr);
                                    break;
                                case ApiEnum.MessageType.Robot://36
//                                    mvh.tvContent.setText("[智能聊天回复]");
                                    holder.setText(R.id.tvContent, "[智能聊天回复]");
                                    break;
                                case ApiEnum.MessageType.CHAT_LOCATION://38
//                                    mvh.tvContent.setText("[位置]");
                                    holder.setText(R.id.tvContent, "[位置]");
                                    break;
                                case ChatContent.DYNAMIC://39
//                                    mvh.tvContent.setText("[分享消息]");
                                    holder.setText(R.id.tvContent, "[分享消息]");
                                    break;
                                case ChatContent.CHAT_SHARE_LOCATION://41
//                                    mvh.tvContent.setText("[位置共享]");
                                    holder.setText(R.id.tvContent, "[位置共享]");

                                    break;
                                //                        case 0:
                                case ApiEnum.MessageType.PhoneStateMsg://43
                                    CallInfo callInfo = new Gson().fromJson(msg.getMessage().getContent(), CallInfo.class);
//                                    mvh.tvContent.setText(callInfo.type == 1 ? "[语音通话]" : "[视频通话]");
                                    holder.setText(R.id.tvContent, callInfo.type == 1 ? "[语音通话]" : "[视频通话]");
                                    break;
                            }

                            // 群组消息
                            CircleImageView imageView = (CircleImageView) holder.getView(R.id.ivHead);
                            if (msg.getType() == 2) {
                                holder.setText(R.id.tvName,"群聊");
                                (imageView).setImageResource(R.drawable.cm_img_grouphead);
                                String uriGroup = String.format(Const.DOWN_ICON_URL,
                                        PrefsUtil.readUserInfo().CompanyCode, msg.getMessage().Group.Image);
                                DisplayImageOptions optionsGroup = new DisplayImageOptions.Builder()
                                        //                .showImageOnLoading(R.drawable.cm_img_grouphead)
                                        //                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                                        //                .showImageOnFail(R.drawable.cm_img_grouphead)
                                        .cacheInMemory(true)
                                        .bitmapConfig(Bitmap.Config.ALPHA_8)
                                        .imageScaleType(ImageScaleType.EXACTLY)
                                        .cacheOnDisk(true).build();
                                ImageLoader.getInstance().displayImage(uriGroup, new ImageViewAware(imageView), optionsGroup,
                                        new ImageSize(DisplayUtil.dip2px(holder.itemView.getContext(), 30), DisplayUtil.dip2px(holder.itemView.getContext(), 30)), null, null);
                                //                        Picasso.with(context)
                                //                                .load(uriGroup)
                                //                                .resize(DisplayUtil.dip2px(context, 40), DisplayUtil.dip2px(context, 40))
                                //                                .centerCrop()
                                //                                .config(Bitmap.Config.ALPHA_8)
                                //                                .placeholder(R.drawable.cm_img_grouphead)
                                //                                .error(R.drawable.cm_img_grouphead)
                                //                                .into(vh.head);
                            } else {//TODO:都是走这里
//                                mvh.tvName.setText(msg.Receiver.Name);
                                holder.setText(R.id.tvName, msg.Receiver.Name);
                                imageView.setTvBg(EMWApplication.getIconColor(msg.Receiver.ID), msg.Receiver.Name, 30);
                                String uriUser = String.format(Const.DOWN_ICON_URL,
                                        PrefsUtil.readUserInfo().CompanyCode, msg.Receiver.Image);
                                ImageLoader.getInstance().displayImage(uriUser, new ImageViewAware(imageView), options,
                                        new ImageSize(DisplayUtil.dip2px(holder.itemView.getContext(), 30), DisplayUtil.dip2px(holder.itemView.getContext(), 30)), null, null);
                            }
//                            mvh.tvTime.setText(StringUtils.friendly_time(msg.getMessage().getCreateTime()));
                            holder.setText(R.id.tvTextTime, StringUtils.friendly_time(msg.getMessage().getCreateTime()));
                        }
                    });
                }
            }
        });
    }

    /*@Event(R.id.itv_dynamicbottom_send)
    private void onSendClick(View v) {
        String content = mCommentContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(this, R.string.empty_content_tips);
        } else {
            reply(content, Integer.valueOf(v.getTag(R.id.tag_first).toString()), Boolean.valueOf(v.getTag(R.id.tag_second).toString()));
        }
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra("isRelogin")) {
            finish();
            String ryToken = PrefsUtil.readUserInfo().RongYunToken;
            if (!TextUtils.isEmpty(ryToken)) {
                isLoginRY = true;
            }
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra("isLoginRY", isLoginRY);
            mainIntent.putExtra("isRelogin", false);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(mainIntent);
        }
    }

    private boolean isRefresh = false;//是否刷新中

    @Override
    public void onRefresh() {
        //检查是否处于刷新状态
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    //显示或隐藏刷新进度条
                    swipeRefreshLayout.setRefreshing(false);
                    //修改adapter的数据
                    getChatList();
                    isRefresh = false;
                }
            }, 1000);
        }
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_FINISH_ACTIVITY.equals(action)) {
                RongIM.setConnectionStatusListener(null);
                MainActivity.this.finish();
            }
            if (ACTION_REFRESH_MAIN.equals(action) && mSlidingMenu != null) {
                mSlidingMenu.showMenu();
            }
            if (ACTION_REFRESH_MAIN_RIGHT.equals(action) && mSlidingMenu != null) {
                mSlidingMenu.showSecondaryMenu();
            }
            if (ACTION_REFRESH_LEFTMENU.equals(action)) {
                getLeftNavigateList();
            }
            if (ACTION_RIGHT_REFRESH.equals(action)) {
                int id = intent.getIntExtra(MESSAGE_ID, 0);
                int tag = intent.getIntExtra(MESSAGE_TYPE, 0);
                if (tag == 0) {
//                    informationRight.getChatList(true);
                } else if (tag == 1) {
                    RemoveAllMessageByID(id, tag);
                } else if (tag == 2) {
                    RemoveAllMessageByID(id, tag);
                }
            }
            if (ACTION_REFRESH_COUNT.equals(action)) {
                getUnreadCount();
            }
            if (ACTION_TALKER_COMMENT.equals(action)) { //Talker列表直接评论发过来的广播
                boolean isShow = intent.getBooleanExtra("is_show", false);
                boolean isSubrev = intent.getBooleanExtra("is_subrev", false);
                int noteID = intent.getIntExtra("note_id", 0);
                int enterFlag = intent.getIntExtra("enter_flag", 0);
                ekBar.getBtnSend().setTag(R.id.tag_third, enterFlag);
                ApiEntity.UserNote un = (ApiEntity.UserNote) intent.getSerializableExtra("user_note");
                if (isShow) {
                    ekBar.getRootView().setVisibility(View.VISIBLE);
                    ekBar.getEtChat().requestFocusFromTouch();
                    ekBar.getEtChat().setTag(un);
                    ekBar.getBtnSend().setTag(R.id.tag_first, noteID);
                    ekBar.getBtnSend().setTag(R.id.tag_second, isSubrev);
                    if (un != null && isSubrev) {
                        String name = "";
                        if (EMWApplication.personMap != null && EMWApplication.personMap.get(un.UserID) != null) {
                            name = EMWApplication.personMap.get(un.UserID).Name;
                        }
                        ekBar.getEtChat().setHint("回复 " + name);
                    } else {
                        ekBar.getEtChat().setHint(R.string.dynamicdetail_edittext_hint);
                    }
                    HelpUtil.showSoftInput(ekBar.getEtChat().getContext(), ekBar.getEtChat());
                } else {
                    ekBar.getEtChat().clearFocus();
                    ekBar.getRootView().setVisibility(View.GONE);
                    HelpUtil.hideSoftInput(ekBar.getEtChat().getContext(), ekBar.getEtChat());
                }
            }
            if (DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
                CircleImageView headView = (CircleImageView) findViewById(R.id.civ_mainmenu_head);
                TextView nameView = (TextView) findViewById(R.id.tv_mainmenu_name);
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
                headView.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 60);
                ImageLoader.getInstance().displayImage(uri, headView, options);
                nameView.setText(PrefsUtil.readUserInfo().Name);
            }
            /*if (ACTION_TALKER_COMMENT.equals(action)) { //Talker列表直接评论发过来的广播
                boolean isShow = intent.getBooleanExtra("is_show", false);
                boolean isSubrev = intent.getBooleanExtra("is_subrev", false);
                int noteID = intent.getIntExtra("note_id", 0);
                ApiEntity.UserNote un = (ApiEntity.UserNote) intent.getSerializableExtra("user_note");
                if (isShow) {
                    mCommentLayout.setVisibility(View.VISIBLE);
                    mCommentContentEt.requestFocus();
                    mCommentContentEt.setTag(un);
                    mCommentSendItv.setTag(R.id.tag_first, noteID);
                    mCommentSendItv.setTag(R.id.tag_second, isSubrev);
                    if (un != null && isSubrev) {
                        String name = "";
                        if (EMWApplication.personMap != null && EMWApplication.personMap.get(un.UserID) != null) {
                            name = EMWApplication.personMap.get(un.UserID).Name;
                        }
                        mCommentContentEt.setHint("回复 " + name);
                    } else {
                        mCommentContentEt.setHint(R.string.dynamicdetail_edittext_hint);
                    }
                    HelpUtil.showSoftInput(mCommentContentEt.getContext(), mCommentContentEt);
                } else {
                    mCommentLayout.setVisibility(View.GONE);
                    HelpUtil.hideSoftInput(mCommentContentEt.getContext(), mCommentContentEt);
                }
            }*/
        }
    }

    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    private Fragment getFragment(int position) {
        Fragment fragment = fragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new TalkerFragment();
                    break;
                //                case 1:
                //                    fragment = new WorkerFragment();
                //                    break;
            }
            fragmentMap.put(position, fragment);
        }
        return fragment;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (CalendarFragmentView.guide != null)
            CalendarFragmentView.guide.dismiss();
        if (JCVideoPlayer.backPress()) {
            return;
        }
        if (mSlidingMenu.isMenuShowing() || mSlidingMenu.isSecondaryMenuShowing()) {
            mSlidingMenu.toggle();
        } else if (DynamicFragment.mActionMenu != null && DynamicFragment.mActionMenu.isOpened()) {
            DynamicFragment.mActionMenu.close(true);
        } else if (TimeTrackingWebFragment.mActionMenu != null && TimeTrackingWebFragment.mActionMenu.isOpened()) {
            TimeTrackingWebFragment.mActionMenu.close(true);
        } /*else if (mCommentLayout != null && mCommentLayout.getVisibility() == View.VISIBLE) {
            mCommentLayout.setVisibility(View.GONE);
        } */ else if (ekBar.getRootView() != null && ekBar.getRootView().getVisibility() == View.VISIBLE) {
            ekBar.getEtChat().clearFocus();
            ekBar.getRootView().setVisibility(View.GONE);
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.main_back_tips, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (mReceive != null) {
                unregisterReceiver(mReceive);
            }
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.cancelAll();
            RongIM.getInstance().logout(); //断开与融云服务器的连接，并且不再接收 Push 消息。
            CIMPushManager.destory(this);
            CIMListenerManager.removeMessageListener(this);
            PrefsUtil.cleanUserInfo();
            AppManager.AppExit(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        Log.e("MainActivity", "main_onDestroy()");
        if (mReceive != null) {
            unregisterReceiver(mReceive);
        }
        RongIM.getInstance().logout();

        //ioSocket 断开连接
        try {
            emwApplication.doSocketOffAction();
            Socket mySocket = getIOSocket();
            if (mySocket != null) {
                getIOSocket().disconnect();
            }
        } catch (Exception ex) {

        }
        super.onDestroy();
        CIMListenerManager.removeMessageListener(this);
    }

    //初始消息条视图
    private void initMsgTipsView() {
        WindowManager windowManger = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = LayoutParams.TYPE_SYSTEM_ALERT;

        lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
        lp.x = 0;
        lp.y = DisplayUtil.dip2px(this, 50);
        lp.width = getWindowManager().getDefaultDisplay().getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER | Gravity.TOP;
        lp.format = PixelFormat.TRANSPARENT;

        if (view == null) {
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_msg, null);
            view.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        view.setVisibility(View.GONE);
                        Message message = (Message) v.getTag();
                        if (message != null) {
                            int type = message.getType();
                            if (type == MessageType.Task) {
                                Task task = new Gson().fromJson(message.getContent(), Task.class);
                                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                                intent.putExtra(TaskDetailActivity.TASK_ID, task.getID());
                                intent.putExtra("start_anim", false);
                                startActivity(intent);
                            } else if (type == MessageType.Message || type == MessageType.JoinGroup || type == MessageType.Attach || type == MessageType.Audio || type == MessageType.RevTalker || type == MessageType.Flow || type == MessageType.Image || type == MessageType.FollowMe) {
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                intent.putExtra("SenderID", message.getSenderID());
                                intent.putExtra("type", message.getGroupID() > 0 ? 2 : 1);
                                intent.putExtra("GroupID", message.getGroupID());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            windowManger.addView(view, lp);
        }
    }

    //===CIMEventListener ↓===
    @Override
    public void onCIMConnectionSucceed() {
        LoginResp cookie = PrefsUtil.readLoginCookie();
        if (cookie != null) {
            IdentityInfo idinfo = new IdentityInfo();
            idinfo.setCompanyCode(cookie.c);
            idinfo.setKey(cookie.s);
            idinfo.setPublicKey(cookie.p);
            idinfo.setUserID(cookie.u);

            String contents = new Gson().toJson(idinfo);
            MessageInfo newmsg = new MessageInfo();
            newmsg.setContent(contents);
            newmsg.setSenderID(100);
            newmsg.setType(1);
            Logger.d("pxss", "onCIMConnectionSucceed!验证体：" + new Gson().toJson(newmsg));
            CIMPushManager.sendRequest(this, new Gson().toJson(newmsg));
        }
    }

    @Override
    public void onMessageReceived(String message) {
        EMWApplication.onMessageReceive = 1;
        try {
            Message msg = new Gson().fromJson(message, Message.class);
            switch (msg.getBusType()) {
                case 0:
//                    if (informationRight != null)
//                        informationRight.getChatList(true);
                    break;
                case 1:
//                    if (informationRight != null)
//                        informationRight.getMessageWork(true);
                    break;
                case 2:
//                    if (informationRight != null)
//                        informationRight.getMessageNotice(true);
                    break;
            }

            if (EMWApplication.currentChatUid == -2) {
                if (msg.getType() != MessageType.FollowMe)
                    count++;
                ShortcutBadger.applyCount(this, count); //设置桌面图标未读数
                Intent intent = new Intent(ACTION_UNREAD_COUNT);
                intent.putExtra("unread_count", count);
                sendBroadcast(intent); //刷新右上角未读数
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("px", "MainActivity-onMessageReceived-解析异常！" + e.toString());
        }
    }

    @Override
    public void onReplyReceived(String replybody) {

    }

    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {
        if (networkinfo != null && networkinfo.isAvailable() && networkinfo.isConnected()) {
            if (EMWApplication.personMap == null || EMWApplication.personMap.size() == 0) {
                loginPresenter.getPersonList();
            }
            if (EMWApplication.groupMap == null || EMWApplication.groupMap.size() == 0) {
                getAllGroupList();
            }
            String username = Prefs.getString(PrefsUtil.KEY_USERNAME, "");
            String password = Prefs.getString(PrefsUtil.KEY_PASSWORD, "");
            String comcode = Prefs.getString(PrefsUtil.KEY_COMCODE, "");
            loginPresenter.login(username, password, comcode); //登录
        }
    }

    @Override
    public void onConnectionStatus(boolean isConnected) {
        Logger.d("px", "isConnected" + isConnected);
    }

    @Override
    public void onCIMConnectionClosed() {
        Logger.d("px", "onCIMConnectionClosed");
    }
    //===CIMEventListener ↑===

    //===LoginView ↓===
    @Override
    public void saveLoginInfo(LoginResp respInfo) {
        Logger.d("MainActivity", "saveLoginInfo----------save" + new Gson().toJson(respInfo));
        PrefsUtil.saveLoginCookie(respInfo);
        PrefsUtil.saveUserInfo(respInfo.User);


    }

    @Override
    public void navigateToIndex() {
    }

    @Override
    public void showTipDialog(String tip) {
        Logger.d("main", tip);
        if (isFinishing())
            return;
        if ("用户名、密码或企业代码错误!".equals(tip)) {
            PrefsUtil.setSwitch(true);
            new AlertDialog(this).builder()
                    .setMsg("登录状态过期，请重新登录！").setCancelable(false)
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public void showTipToast(String tip) {
    }

    @Override
    public void showVersionDialog(Version ver) {
        DialogUtil.showVersionDialog(this, ver);
    }

    @Override
    public Socket getIOSocket() {
        EMWApplication emw = (EMWApplication) getApplication();
        Socket mSocket = emw.getAppIOSocket();
        return mSocket;
    }
    //===LoginView ↑===

    //===LocationSource ↓===
    // 激活定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
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

    // 停止定位
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    //===LocationSource ↑===

    //===AMapLocationListener ↓===
    //自动上传人员定位
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getLongitude() != 0.0 && amapLocation.getLatitude() != 0.0
                && amapLocation.getAddress() != null && !TextUtils.isEmpty(amapLocation.getAddress())) {
            // 停止定位
            deactivate();
            // 向服务器返回当前用户的经纬度
            final String lola = amapLocation.getLatitude() + ","
                    + amapLocation.getLongitude();
            //                    + "," + amapLocation.getAoiName();
            API.UserAPI.ModifyUserAxisById(lola, new RequestCallback<String>(String.class) {
                @Override
                public void onError(Throwable throwable, boolean b) {
                }

                @Override
                public void onSuccess(String result) {
                    Logger.d("zzzz", result + "");
                    Log.d("zzzz", lola);
                    if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                        //                        Toast.makeText(MainActivity.this, "更新位置成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //===AMapLocationListener ↑===


    private boolean isA = false;
    private boolean isB = false;

    /**
     * 获取左侧导航列表,获取左侧列表数据，包括生产管理，仓库管理，生产计划
     */
    private void getLeftNavigateList() {
        API.TemplateAPI.LoadTemplates(new RequestCallback<Template>(
                Template.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onParseSuccess(List<Template> respList) {
                if (respList != null && respList.size() > 0) {
                    templates = respList;
                    isA = true;
                    if (isB && isA) {

                        adapter.setData(templates, navigations);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        API.TemplateAPI.GetIndexNavigation(new RequestCallback<Navigation>(
                Navigation.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onParseSuccess(List<Navigation> respList) {
                if (respList != null && respList.size() > 0) {
                    navigations = respList;
                    isB = true;
                    if (isB && isA) {
                        adapter.setData(templates, navigations);
                        adapter.notifyDataSetChanged();
                        Intent intent = new Intent();
                        intent.putExtra("left", "left");
                        sendBroadcast(intent);
                    }
                }
            }
        });
    }

    /**
     * 登录融云平台
     */
    private void loginRY() {
        if (isLoginRY) {
            RongIM.connect(PrefsUtil.readUserInfo().RongYunToken, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                            2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.e("connect", "onTokenIncorrect");
                    //                    reGetToken();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    //                    Toast.makeText(MainActivity.this, "连接融云成功", Toast.LENGTH_SHORT).show();
                    //                    connectResultId = s;
                    Log.e("connect", "onSuccess userid:" + userid);
                    //                    editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                    //                    editor.apply();
                    //                    SealUserInfoManager.getInstance().openDB();
                    //                    request(SYNC_USER_INFO, true);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e("connect", "onError errorcode:" + errorCode.getValue());
                }
            });

            RongIM.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
                @Override
                public void onChanged(ConnectionStatus connectionStatus) {
                    if (!isFinishing() && connectionStatus == ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                        CIMPushManager.destory(MainActivity.this);
                        EMWApplication.mChatHistory.clear();

                        PrefsUtil.cleanLoginCookie();
                        PrefsUtil.setSwitch(true);

                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), TerminalLoginDialogActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("reason", connectionStatus.getValue());
                        intent.putExtra("start_anim", false);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /**
     * 获取圈子列表
     */
    private void getAllGroupList() {
        if (EMWApplication.groupMap == null) {
            EMWApplication.groupMap = new SparseArray<GroupInfo>();
        }
        EMWApplication.groupMap.clear();

        API.TalkerAPI.LoadGroups("", false, -1, false, new RequestCallback<GroupInfo>(GroupInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onParseSuccess(List<GroupInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    EMWApplication.groupAIDLStr = new Gson().toJson(respList);
                    for (GroupInfo groupInfo : respList) {
                        EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    }
                }
            }
        });
    }

    // 获取消息服务器IP与端口
    private void startPushServer() {
        CIMListenerManager.registerMessageListener(this, this);
        API.Message.AssignEndPoint(new RequestCallback<Connections>(
                Connections.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Logger.w("px", "获取消息服务器IP与端口失败");
            }

            @Override
            public void onParseSuccess(final Connections respInfo) {
                Logger.d("px", "获取消息服务器IP与端口成功 " + respInfo.getHostName() + ":" + respInfo.getPort() + " " + respInfo.getProtol());
                CIMCacheTools.putBoolean(MainActivity.this, CIMCacheTools.KEY_CIM_IS_SSL, "wss".equals(respInfo.getProtol()));
                //连接消息服务器
                CIMPushManager.init(MainActivity.this, respInfo.getHostName(), respInfo.getPort());
            }
        });
    }

    //获取消息未读数量
    private void getUnreadCount() {
        API.Message.GetNewMessageCount(new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                count = Integer.valueOf(result);
                ShortcutBadger.applyCount(MainActivity.this, count); //设置桌面图标未读数
                Intent intent = new Intent(ACTION_UNREAD_COUNT);
                intent.putExtra("unread_count", count);
                sendBroadcast(intent); //刷新右上角未读数
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    /**
     * 根据发送者的ID清除未读消息
     *
     * @param SenderID
     */
    private void removeNewMessageBySenderID(int SenderID) {
        API.Message.RemoveNewMessageBySenderID(SenderID, 1, new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                if (result.equals("true")) {
                    getUnreadCount();
//                    informationRight.getMessageNotice(false);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Logger.d("zrjtsss", "removeCountFail");
            }
        });
    }

    /**
     * 根据消息 ID 清除未读消息
     *
     * @param id
     */
    public void RemoveAllMessageByID(int id, final int type) {
        API.Message.RemoveAllMessageByID(id, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (result.equals("true")) {
//                    if (type == 1)
//                        informationRight.getMessageWork(true);
//                    else if (type == 2)
//                        informationRight.getMessageNotice(true);
                    Intent intent = new Intent(MainActivity.ACTION_REFRESH_COUNT);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(MainActivity.this, "消息标记失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Dialog mLoadingDialog;

    private void reply(String content, final int noteID, boolean isSubrev, final int enterFlag) {
        ekBar.getEtChat().clearFocus();
        ekBar.getRootView().setVisibility(View.GONE);
        HelpUtil.hideSoftInput(ekBar.getEtChat().getContext(), ekBar.getEtChat());
        final UserNote rev = new UserNote();
        rev.ID = 0;
        rev.Content = content;
        rev.PID = noteID;
        rev.TopId = noteID;
        if (ekBar.getEtChat() != null && ekBar.getEtChat().getTag() instanceof ApiEntity.UserNote) { //是否回复对方
            ApiEntity.UserNote subrev = (ApiEntity.UserNote) ekBar.getEtChat().getTag();
            rev.PID = subrev.ToUserId > 0 ? subrev.PID : subrev.ID;
            rev.ToUserId = subrev.UserID;

            rev.ToUserIdInfo = new ApiEntity.TalkerUserInfo(); //回复的对方信息
            rev.ToUserIdInfo.ID = subrev.UserID;
            rev.ToUserIdInfo.Name = subrev.UserIdInfo != null ? subrev.UserIdInfo.Name : "";
        }

        API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    //                    ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    if (enterFlag == 1) {
                        intent = new Intent(TimeTrackingWebFragment.ACTION_TIME_DYNAMICDISCUSS);
                    }
                    intent.putExtra("note_id", noteID);
                    rev.ID = Integer.valueOf(result);
                    rev.UserID = PrefsUtil.readUserInfo().ID;
                    rev.UserIdInfo = HelpUtil.userInfo2TalkerUserInfo(PrefsUtil.readUserInfo());
                    rev.CreateTime = StringUtils.getCurTimeStr();
                    Gson gson = new Gson();
                    intent.putExtra("rev_note", gson.fromJson(gson.toJson(rev), ApiEntity.UserNote.class));
                    sendBroadcast(intent); //刷新Talker列表
                    ekBar.getEtChat().setText("");
                    ekBar.getEtChat().setTag(null);
                    ekBar.getEtChat().setHint(R.string.dynamicdetail_edittext_hint);
                    ekBar.reset();
                } else {
                    ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_error);
                }
            }
        });
    }

    /*@ViewInject(R.id.ll_dynamicbottom_discuss)
    private LinearLayout mCommentLayout;
    @ViewInject(R.id.et_dynamicbottom_content)
    private EditText mCommentContentEt;
    @ViewInject(R.id.itv_dynamicbottom_send)
    private IconTextView mCommentSendItv;
    private Dialog mLoadingDialog;

    private void reply(String content, final int noteID, boolean isSubrev) {
        mCommentLayout.setVisibility(View.GONE);
        HelpUtil.hideSoftInput(this, mCommentContentEt);
        final UserNote rev = new UserNote();
        rev.ID = 0;
        rev.Content = content;
        if (mCommentContentEt != null && mCommentContentEt.getTag() instanceof ApiEntity.UserNote) {
            ApiEntity.UserNote subrev = (ApiEntity.UserNote) mCommentContentEt.getTag();
            rev.PID = subrev.PID > 0 ? subrev.PID : subrev.ID;
            rev.TopId = subrev.ID;
            if (isSubrev) {
                rev.ToUserId = subrev.UserID;
            }
        }

        API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("note_id", noteID);
                    rev.ID = Integer.valueOf(result);
                    rev.UserID = PrefsUtil.readUserInfo().ID;
                    Gson gson = new Gson();
                    intent.putExtra("rev_note", gson.fromJson(gson.toJson(rev), ApiEntity.UserNote.class));
                    sendBroadcast(intent); //刷新Talker列表
                    mCommentContentEt.setText("");
                    mCommentContentEt.setTag(null);
                    mCommentContentEt.setHint(R.string.dynamicdetail_edittext_hint);
                } else {
                    ToastUtil.showToast(MainActivity.this, R.string.dynamicdetail_comment_error);
                }
            }
        });
    }*/
}
