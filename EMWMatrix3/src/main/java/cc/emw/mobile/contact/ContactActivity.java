package cc.emw.mobile.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.view.SlidingUpPanelLayout;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.contact.fragment.ContactFriendFragment;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.contact.fragment.PersonFriendFragment;
import cc.emw.mobile.contact.util.InformationRight;
import cc.emw.mobile.form.FormWebActivity;
import cc.emw.mobile.login.ScannerLoginActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.adapter.TemplatesAdapter;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.me.MyInfoActivity;
import cc.emw.mobile.me.SettingActivity;
import cc.emw.mobile.me.fragment.MyInfoFragment2;
import cc.emw.mobile.me.fragment.MyInfoFragment4;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_contact)
public class ContactActivity extends BaseActivity implements ViewPager.OnPageChangeListener, CIMEventListener {

    @ViewInject(R.id.contact_viewPager)
    private ViewPager mViewPager;
    @ViewInject(R.id.cm_bottom_bar)
    private RadioGroup mBottomGroup;
    @ViewInject(R.id.ic_tv_chat)
    private RadioButton mRbChat;
    @ViewInject(R.id.ic_tv_group)
    private RadioButton mRbGroup;
    @ViewInject(R.id.ic_tv_people)
    private RadioButton mRbPeople;
    @ViewInject(R.id.ic_tv_phone)
    private RadioButton mRbMe;
    @ViewInject(R.id.et_search_keywords)
    private EditText mSearchEt;
    @ViewInject(R.id.ic_et_del)
    private IconTextView mIcEtDel;

    public static final String ACTION_CONTACT_SEARCH = "action_contact_search";
    public static final String ACTION_CONTACT_SEARCH_BACK = "action_contact_search_back";
    public static final String ACTION_CONTACT_SEARCH_USERINFO = "action_contact_search_user_info";
    private PageAdapter pageAdapter;
    ////////////////////////////////////////////////////////////////////////////////////////////左右菜单
    private SlidingMenu mSlidingMenu;
    private ExpandableListView mNavigateElv;
    private TemplatesAdapter adapter; //左侧导航adapter
    private List<ApiEntity.Template> templates; //导航分组数据
    private List<ApiEntity.Navigation> navigations; //导航组中数据
    private DisplayImageOptions options;
    private InformationRight informationRight;
    ////////////////////////////////////////////////////////////////
    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(MainActivity.MESSAGE_ID, 0);
            int tag = intent.getIntExtra(MainActivity.MESSAGE_TYPE, 0);
            if (tag == 0) {
                informationRight.getChatList(true);
            } else if (tag == 1) {
                RemoveAllMessageByID(id, tag);
            } else if (tag == 2) {
                RemoveAllMessageByID(id, tag);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra("statusbar_white", false);
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 100);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIcEtDel.setVisibility(!TextUtils.isEmpty(mSearchEt.getText().toString()) ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(s)) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONTACT_SEARCH);
                    intent.putExtra("currentItem", mViewPager.getCurrentItem());
                    intent.putExtra("keyword", s.toString());
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONTACT_SEARCH_BACK);
                    intent.putExtra("currentItem", mViewPager.getCurrentItem());
                    intent.putExtra("keyword", s.toString());
                    sendBroadcast(intent);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    if (!TextUtils.isEmpty(mSearchEt.getText().toString())) {
                        HelpUtil.hideSoftInput(ContactActivity.this, mSearchEt);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CONTACT_SEARCH_USERINFO);
                        intent.putExtra("keyword", mSearchEt.getText().toString());
                        sendBroadcast(intent);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void startActivity(Intent intent) {
        intent.putExtra("scale_anim", true);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("scale_anim", true);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_RIGHT_REFRESH);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); // 注册监听
        CIMListenerManager.registerMessageListener(this, this);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(0);
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.main_left_menu2);
        mSlidingMenu.setSecondaryMenu(R.layout.right_information);
        mSlidingMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                onPageSelected(mViewPager.getCurrentItem());
            }
        });
        mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                getSwipeBackLayout().setEnableGesture(false);
            }
        });

        initLeftView();

        //初始化右侧消息通知
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                informationRight = new InformationRight(ContactActivity.this, mSlidingMenu, false);
//                informationRight.initRightMenu();
//            }
//        }, 50);
        informationRight = new InformationRight(ContactActivity.this, mSlidingMenu, false);
        informationRight.initRightMenu();

        mBottomGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.ic_tv_chat:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.ic_tv_phone:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.ic_tv_people:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.ic_tv_group:
                        mViewPager.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    private void initLeftView() {
        mNavigateElv = (ExpandableListView) findViewById(R.id.elv_mainmenu_navigate);
        adapter = new TemplatesAdapter(ContactActivity.this);
        if (MainActivity.templates != null && MainActivity.navigations != null)
            adapter.setData(MainActivity.templates, MainActivity.navigations);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        mNavigateElv.setAdapter(wrapperAdapter);
        try {
            final SlidingUpPanelLayout slidLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            final CircleImageView headView = (CircleImageView) findViewById(R.id.civ_mainmenu_head);
            final TextView nameView = (TextView) findViewById(R.id.tv_mainmenu_name);
            final View menuLayout = findViewById(R.id.ll_mainmenu_root);
            final View arrowView = findViewById(R.id.itv_mainmenu_arrow);
            headView.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 60);
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
            ImageLoader.getInstance().displayImage(uri, headView, options);
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slidLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        slidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
//                        Intent meIntent = new Intent(ContactActivity.this, MyInfoActivity.class);
//                        startActivity(meIntent); //个人中心
                        sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 5));
                        mSlidingMenu.toggle();
                        finish();
                    }
                }
            });
            nameView.setText(PrefsUtil.readUserInfo().Name);

            final int minWH = DisplayUtil.dip2px(ContactActivity.this, 30);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            slidLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    params.width = (int) ((1 - slideOffset) * minWH) + minWH;
                    params.height = (int) ((1 - slideOffset) * minWH) + minWH;
                    headView.setLayoutParams(params);

                    nameView.animate().setDuration(0).scaleX(1 - slideOffset).scaleY(1 - slideOffset);
                    nameView.animate().setDuration(0).alpha(1 - slideOffset * 2);
                    menuLayout.animate().setDuration(0).alpha(1 - slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        arrowView.animate().setDuration(300).rotation(180);
                    } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        arrowView.animate().setDuration(300).rotation(360);
                    }
                }
            });
            findViewById(R.id.ll_mainmenu_talker).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 0));
                    ContactActivity.this.finish();
                }
            });
            findViewById(R.id.ll_mainmenu_calendar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 1));
                    ContactActivity.this.finish();
                }
            });
            findViewById(R.id.ll_mainmenu_file).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 4));
                    ContactActivity.this.finish();
                }
            });
            findViewById(R.id.ll_mainmenu_group).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSlidingMenu.toggle();
                }
            });
            findViewById(R.id.ll_mainmenu_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 3));
                    Intent intent = new Intent(ContactActivity.this, FormWebActivity.class);
                    intent.putExtra("url", "/Web_App/new_task/index.html");
                    intent.putExtra(FormWebActivity.HAS_PROGRESSBAR, false);
                    startActivity(intent);
                    ContactActivity.this.finish();
                }
            });
            findViewById(R.id.ll_mainmenu_scanner).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent scanIntent = new Intent(ContactActivity.this, ScannerLoginActivity.class);
                    startActivity(scanIntent);
                }
            });
            findViewById(R.id.ll_mainmenu_me).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent meIntent = new Intent(ContactActivity.this, MyInfoActivity.class);
                    startActivity(meIntent); //个人中心
                }
            });
            findViewById(R.id.ll_mainmenu_setting).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(ContactActivity.this, SettingActivity.class);
                    startActivity(setIntent); //设置
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mRbChat.setChecked(true);
//                mRbGroup.setChecked(false);
//                mRbPeople.setChecked(false);
//                mRbMe.setChecked(false);
                sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM));
                sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
            case 3:
//                mRbChat.setChecked(false);
//                mRbGroup.setChecked(false);
//                mRbPeople.setChecked(false);
                mRbMe.setChecked(true);
                sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM));
                break;
            case 1:
//                mRbChat.setChecked(false);
                mRbGroup.setChecked(true);
//                mRbPeople.setChecked(false);
//                mRbMe.setChecked(false);
                sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM));
                sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
            case 2:
//                mRbChat.setChecked(false);
//                mRbGroup.setChecked(false);
                mRbPeople.setChecked(true);
//                mRbMe.setChecked(false);
                sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM));
                sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Event(value = {R.id.ic_tv_chat, R.id.ic_tv_phone, R.id.im_contact_play, R.id.ic_tv_people, R.id.ic_tv_group, R.id.ic_tv_back, R.id.ic_et_del})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ic_et_del:
                mSearchEt.setText("");
                mSearchEt.clearFocus();
                break;
            case R.id.ic_tv_back:
                onBackPressed();
                break;
            case R.id.im_contact_play:  //拍照视频
//                Intent intent = new Intent(ContactActivity.this, CameraActivity.class);
//                intent.putExtra("start_anim", false);
//                intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CONTACT);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_up_in,
//                        R.anim.popup_hide);
//                CircularAnim.fullActivity(this, v)
//                        .colorOrImageRes(R.color.white)
//                        .go(new CircularAnim.OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd() {
//                                Intent intent = new Intent(ContactActivity.this, CameraActivity2.class);
//                                intent.putExtra("start_anim", false);
//                                intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CONTACT);
//                                startActivity(intent);
//                            }
//                        });
                Intent intent = new Intent(ContactActivity.this, CameraActivity.class);
                intent.putExtra("start_anim", false);
                intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CONTACT);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,
                        R.anim.popup_hide);
                break;
//            case R.id.ic_tv_chat:
//                mViewPager.setCurrentItem(0);
//                break;
//            case R.id.ic_tv_phone:
//                mViewPager.setCurrentItem(3);
//                break;
//            case R.id.ic_tv_people:
//                mViewPager.setCurrentItem(2);
//                break;
//            case R.id.ic_tv_group:
//                mViewPager.setCurrentItem(1);
//                break;
        }
    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"会话", "电话", "人员", "团队"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 0) {
                    fragment = new ChatHistoriesFragment();
                } else if (position == 1) {
                    fragment = new GroupFragment();
                } else if (position == 2) {
                    fragment = new ContactFriendFragment();
                } else {
                    fragment = new MyInfoFragment4();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceive != null)
            unregisterReceiver(mReceive);
        CIMListenerManager.removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            Message msg = new Gson().fromJson(message, Message.class);

            switch (msg.getBusType()) {
                case 0:
                    Intent mIntent = new Intent();
                    mIntent.setAction(ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                    sendBroadcast(mIntent);
                    if (informationRight != null)
                        informationRight.getChatList(true);
                    break;
                case 1:
                    if (informationRight != null)
                        informationRight.getMessageWork(true);
                    break;
                case 2:
                    if (informationRight != null)
                        informationRight.getMessageNotice(true);
                    break;
            }
        } catch (Exception e) {

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
                    if (type == 1)
                        informationRight.getMessageWork(true);
                    else if (type == 2)
                        informationRight.getMessageNotice(true);
                    Intent intent = new Intent(MainActivity.ACTION_REFRESH_COUNT);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(ContactActivity.this, "消息标记失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
