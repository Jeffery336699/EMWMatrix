package cc.emw.mobile.main;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.android.CIMPushManager;
import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsErrorCode;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.chat.bean.Connections;
import cc.emw.mobile.chat.bean.IdentityInfo;
import cc.emw.mobile.chat.bean.SendMessage;
import cc.emw.mobile.chat.bean.Task;
import cc.emw.mobile.contact.model.ContactModelImple;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.main.adapter.TemplatesAdapter;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.main.fragment.WorkerFragment;
import cc.emw.mobile.map.RouteActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.ApiEntity.Template;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.sale.RetailActivity;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.ExViewPager;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements CIMEventListener {

    @ViewInject(R.id.tab_pager)
    private ExViewPager mViewPager;
    private SlidingMenu mSlidingMenu;
    private ExpandableListView mNavigateElv;

    public static final String ACTION_REFRESH_MAIN = "cc.emw.mobile.refresh_main"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private BroadcastReceiver br;
    private SparseArray<Fragment> fragmentMap;
    private boolean isLoginYZX; // 是否可以登录云之讯
    private static View view = null;
    private static long TIME = 3000;
    private TemplatesAdapter adapter;
    private List<Template> templates;
    private List<Navigation> navigations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        fragmentMap = new SparseArray<Fragment>();
        isLoginYZX = getIntent().getBooleanExtra("isLoginYZX", false);
        initView();
        loginYZX();
        loadingServer();
        loadingNavigate();

        new ContactModelImple().getGroupList(null);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_MAIN);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听

    }

    @Override
    protected void onDestroy() {
        if (mReceive != null) {
            unregisterReceiver(mReceive);
        }
        if (br != null) {
            unregisterReceiver(br);
        }
        super.onDestroy();
    }

    private void initView() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        // mSlidingMenu.setShadowDrawable(R.drawable.shadowleft);
        // mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.main_left_menu);
        // mSlidingMenu.setSecondaryMenu(R.layout.main_right_menu);

        mViewPager.setPagingEnabled(false); // 禁止左右滑动
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        /*findViewById(R.id.tv_mainmenu_talker).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(0, false);
                        mSlidingMenu.toggle();
                    }
                });
        findViewById(R.id.tv_mainmenu_worker).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(1, false);
                        mSlidingMenu.toggle();
                    }
                });
        findViewById(R.id.tv_mainmenu_bom).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,
                                BomActivity.class);
                        startActivity(intent);
                    }
                });
        findViewById(R.id.tv_mainmenu_map).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,
                                RouteActivity.class);
                        startActivity(intent);
                    }
                });*/


        mNavigateElv = (ExpandableListView) findViewById(R.id.elv_mainmenu_navigate);
        adapter = new TemplatesAdapter(MainActivity.this);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        mNavigateElv.setAdapter(wrapperAdapter);
        mNavigateElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (v.getTag(R.id.tag_second) != null) {
                    Template temp = (Template) v.getTag(R.id.tag_second);
                    switch (temp.ID) {
                        case -1:
                            mViewPager.setCurrentItem(0, false);
                            mSlidingMenu.toggle();
                            break;
                        case -2:
                            mViewPager.setCurrentItem(1, false);
                            mSlidingMenu.toggle();
                            break;
                        case -3:
                            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                            startActivity(intent);
                            break;
                        case -4:
                            Intent saleIntent = new Intent(MainActivity.this, RetailActivity.class);
                            startActivity(saleIntent);
                            break;
                    }
                }
                return false;
            }
        });

        findViewById(R.id.tv_mainmenu_logout).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.showLogoutDialog(MainActivity.this);
                    }
                });

        getMyWindowManager();
    }

    private boolean isA = false;
    private boolean isB = false;

    private void loadingNavigate() {
        API.TemplateAPI.LoadTemplates(new RequestCallback<Template>(
                Template.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onParseSuccess(List<Template> respList) {
                super.onParseSuccess(respList);
                templates = respList;
                isA = true;
                if (isB && isA) {
                    adapter.setData(templates, navigations);
                    adapter.notifyDataSetChanged();
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
                super.onParseSuccess(respList);
                navigations = respList;
                isB = true;
                if (isB && isA) {
                    adapter.setData(templates, navigations);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_MAIN.equals(action) && mSlidingMenu != null) {
                mSlidingMenu.showMenu();
            }
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
            return 2;
        }
    }

    private Fragment getFragment(int position) {
        Fragment fragment = fragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new TalkerFragment();
                    break;
                case 1:
                    fragment = new WorkerFragment();
                    break;
            }
            fragmentMap.put(position, fragment);
        }
        return fragment;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (mSlidingMenu.isMenuShowing() || mSlidingMenu.isSecondaryMenuShowing()) {
            mSlidingMenu.toggle();
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.main_back_tips,
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            onDestroy();
            CIMPushManager.destory(this);
            AppManager.AppExit(this);
        }
    }

    private void loadingServer() {
        // 连接聊天服务器
        API.Message.AssignEndPoint(new RequestCallback<Connections>(
                Connections.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.w("px", "获取消息服务器IP与端口失败");
            }

            // 连接聊天服务器成功
            @Override
            public void onParseSuccess(final Connections respInfo) {
                Log.i("px", "获取消息服务器IP与端口成功+" + respInfo.getHostName()
                        + respInfo.getPort());
                CIMPushManager.init(MainActivity.this, respInfo.getHostName(),
                        respInfo.getPort());
                CIMListenerManager.registerMessageListener(MainActivity.this,
                        MainActivity.this);

            }
        });
    }

    @Override
    public void onCIMConnectionSucceed() {

        LoginResp msg = PrefsUtil.readLoginCookie();
        IdentityInfo idinfo = new IdentityInfo();
        idinfo.setCompanyCode(msg.c);
        idinfo.setKey(msg.s);
        idinfo.setPublicKey(msg.p);
        idinfo.setUserID(msg.u);

        String contents = new Gson().toJson(idinfo);
        SendMessage newmsg = new SendMessage();
        newmsg.setContent(contents);
        newmsg.setSenderID(100);
        newmsg.setType(1);

        Log.i("px", "onCIMConnectionSucceed!验证体：" + new Gson().toJson(newmsg));

        CIMPushManager.sendRequest(this, new Gson().toJson(newmsg));

    }

    @Override
    public void onMessageReceived(String message) {
        // {"SenderID":1,"CompanyCode":"emw","Type":1}‘
        // message.substring(0, message.indexOf("}"));
        try {
            // view = null;
            Message msg = new Gson().fromJson(message, Message.class);
            if (msg != null) {
                switch (msg.getType()) {
                    // 普通消息推送
                    case MessageType.Message:
                    case MessageType.Attach:
                    case MessageType.Image:
                    case MessageType.Audio:
                    case MessageType.Share:
                        if (!isInBackground(MainActivity.this)) {
                            if (view != null) {
                                TextView TV_NAME = (TextView) view
                                        .findViewById(R.id.name_pop);
                                TextView TV_CONTENT = (TextView) view
                                        .findViewById(R.id.content_pop);
                                TV_NAME.setText(EMWApplication.personMap.get(msg
                                        .getSenderID()).Name + ":");
                                TV_CONTENT.setText(msg.getContent());
                                view.setTag(msg);
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case MessageType.Task:// 任务推送
                        if (!isInBackground(MainActivity.this)) {
                            if (view != null) {
                                Task task = new Gson().fromJson(msg.getContent(),
                                        Task.class);
                                TextView TV_NAME = (TextView) view
                                        .findViewById(R.id.name_pop);
                                TextView TV_CONTENT = (TextView) view
                                        .findViewById(R.id.content_pop);
                                TV_NAME.setText("新任务！");
                                TV_CONTENT.setText(task.getTitle());
                                view.setTag(msg);
                                view.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
                if (view != null) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            view.setVisibility(View.GONE);
                        }
                    }, TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReplyReceived(String replybody) {

    }

    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {
//        ToastUtil.showToast(this, getString(R.string.network_bad));
    }

    @Override
    public void onConnectionStatus(boolean isConnected) {

    }

    @Override
    public void onCIMConnectionClosed() {

    }

    private String login_admin_str = "app@zkbr.cc";
    private String login_pwd_str = "Zkbr2015";

    /**
     * 登录云之讯平台
     */
    private void loginYZX() {
        if (isLoginYZX) {
            UCSManager.connect("4854442b8e088772482fc1c8d318f7a8", "b713552c86641feb8b89f54e58b75304", PrefsUtil.readUserInfo().VoipCode, PrefsUtil.readUserInfo().VoipPwd, new ILoginListener() {
                @Override
                public void onLogin(UcsReason ucsReason) {
                    if (ucsReason.getReason() == UcsErrorCode.NET_ERROR_CONNECTOK) {
                        //登陆成功
                        Toast.makeText(MainActivity.this, "电话服务登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        //登陆失败
                        Toast.makeText(MainActivity.this, "电话服务登录失败，" + ucsReason.getReason() + ucsReason.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // 接收连接回调后发送的广播
                    if (intent.getAction().equals(
                            UIDfineAction.ACTION_TCP_LOGIN_RESPONSE)) {
                        // 登入第一步后收到的回调广播，获得子帐户信息，即将在对话框中显示出来
                        int result = intent.getIntExtra(UIDfineAction.RESULT_KEY, 1);
                        if (result == 0) {
                            Toast.makeText(MainActivity.this, "电话服务登录成功",
                                    Toast.LENGTH_SHORT).show();
                            if (DataTools.istest == true) {
                                login_admin_str = "*#*" + login_admin_str;
                            }

                            LoginConfig.saveCurrentSidAndToken(MainActivity.this,
                                    login_admin_str, login_pwd_str);
                            LoginConfig.saveCurrentSid(MainActivity.this,
                                    login_admin_str);
                            Config.initProperties(MainActivity.this);

                            LoginConfig.saveCurrentClientId(MainActivity.this, "");
                            sendBroadcast(new Intent(UIDfineAction.ACTION_LOGIN)
                                    .putExtra("cliend_id",
                                            PrefsUtil.readUserInfo().VoipCode)
                                    .putExtra("cliend_pwd",
                                            PrefsUtil.readUserInfo().VoipPwd)
                                    .putExtra("sid", Config.getMain_account())
                                    .putExtra("sid_pwd", Config.getMain_token()));
                            Toast.makeText(MainActivity.this, "正在登入子账号,请稍等！",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String str = "";
                            switch (result) {
                                case 10:
                                    str = "JSON错误";
                                    break;
                                case 11:
                                    str = "没有SD卡或内存不足";
                                    break;
                                case 12:
                                    str = "IO错误";
                                    break;
                                case 101111:
                                    str = "用户名和密码错误";
                                    break;
                                default:
                                    str = result + "";
                                    break;
                            }
                            Toast.makeText(MainActivity.this, "电话服务登录失败:" + str,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (intent.getAction().equals(
                            UIDfineAction.ACTION_TCP_LOGIN_CLIENT_RESPONSE)) {
                        // 登入第二步后收到的回调广播，子账户登入成功，即将进入能力展示界面
                        if (intent.getIntExtra(UIDfineAction.RESULT_KEY, 1) == 0) {
                            Toast.makeText(MainActivity.this, "子账号登陆成功",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "子账号登陆失败:"
                                            + intent.getIntExtra(
                                            UIDfineAction.REASON_KEY, 1),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            IntentFilter ift = new IntentFilter();
            ift.addAction(UIDfineAction.ACTION_TCP_LOGIN_RESPONSE);
            ift.addAction(UIDfineAction.ACTION_TCP_LOGIN_CLIENT_RESPONSE);
            registerReceiver(br, ift);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JsonReqClient client = new JsonReqClient();
                    String json = client.login(login_admin_str, login_pwd_str);
                    CustomLog.v(DfineAction.TAG_TCP, "RESULT:" + json);
                    Config.parseConfig(json, MainActivity.this);
                }
            }).start();*/
        }
    }

    @SuppressWarnings("deprecation")
    private void getMyWindowManager() {

        WindowManager windowManger = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        lp.x = 0;
        lp.y = DisplayUtil.dip2px(this, 45);
        lp.width = getWindowManager().getDefaultDisplay().getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER | Gravity.TOP;

        lp.format = PixelFormat.TRANSPARENT;

        if (view == null) {
            view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.pop_msg, null);
            view.setVisibility(View.GONE);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Message message = (Message) v.getTag();
                    if (message.getType() == MessageType.Task) {
                        Task task = new Gson().fromJson(message.getContent(),
                                Task.class);
                        Intent intent = new Intent(MainActivity.this,
                                TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.TASK_ID,
                                task.getID());
                        startActivity(intent);

                    } else if (message.getType() == MessageType.Message) {
                        Intent intent = new Intent(MainActivity.this,
                                ChatActivity.class);
                        intent.putExtra("SenderID", message.getSenderID());
                        intent.putExtra("name", EMWApplication.personMap
                                .get(message.getSenderID()).Name);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        view.setVisibility(View.GONE);
                    }

                }
            });
            windowManger.addView(view, lp);
        }
    }

    private boolean isInBackground(Context context) {
        @SuppressWarnings("deprecation")
        List<RunningTaskInfo> tasksInfo = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (tasksInfo.size() > 0) {

            if (context.getPackageName().equals(
                    tasksInfo.get(0).topActivity.getPackageName())) {

                return false;
            }
        }
        return true;
    }

}
