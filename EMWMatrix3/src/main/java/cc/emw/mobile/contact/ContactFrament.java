package cc.emw.mobile.contact;


import android.app.Activity;
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
import android.util.Log;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.view.SlidingUpPanelLayout;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.contact.fragment.ContactFriendFragment;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.contact.util.InformationRight;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.adapter.TemplatesAdapter;
import cc.emw.mobile.main.fragment.TalkerFragment;
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
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/*
**圈子原生Frament界面
 */
@ContentView(R.layout.activity_contact)
public class ContactFrament extends BaseFragment implements ViewPager.OnPageChangeListener, CIMEventListener, SwipeBackActivityBase {
    @ViewInject(R.id.ic_civ_head)
    private CircleImageView mHeaderIconIv;
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
    private ExpandableListView mNavigateElv;
    private TemplatesAdapter adapter; //左侧导航adapter
    private List<ApiEntity.Template> templates; //导航分组数据
    private List<ApiEntity.Navigation> navigations; //导航组中数据
    private DisplayImageOptions options;
    private InformationRight informationRight;
    ////////////////////////////////////////////////////////////////
    private MyBroadcastReceive mReceive;
    private Activity activity;
    private SwipeBackActivityHelper mHelper;
    private MyReceiver receiver;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        activity.getIntent().putExtra("statusbar_white", false);
        mHelper = new SwipeBackActivityHelper(activity);
        mHelper.onActivityCreate();
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
                    activity.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONTACT_SEARCH_BACK);
                    intent.putExtra("currentItem", mViewPager.getCurrentItem());
                    intent.putExtra("keyword", s.toString());
                    activity.sendBroadcast(intent);
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
                        HelpUtil.hideSoftInput(activity, mSearchEt);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CONTACT_SEARCH_USERINFO);
                        intent.putExtra("keyword", mSearchEt.getText().toString());
                        activity.sendBroadcast(intent);
                    }
                    return true;
                }
                return false;
            }
        });
        receiver = new MyReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh.head");
        getActivity().registerReceiver(receiver, intentFilter);
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

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_RIGHT_REFRESH);
        mReceive = new MyBroadcastReceive();
        activity.registerReceiver(mReceive, intentFilter); // 注册监听
        CIMListenerManager.registerMessageListener(this, activity);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        pageAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(0);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        mHeaderIconIv.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeaderIconIv), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);

        //initLeftView();
//        SlidingMenu mSlidingMenu = new SlidingMenu(activity);
//        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
//        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
//        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
//        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        mSlidingMenu.setFadeDegree(0.35f);
//        mSlidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
//        mSlidingMenu.setMenu(R.layout.main_left_menu2);
//        mSlidingMenu.setSecondaryMenu(R.layout.right_information);
//        mSlidingMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
//            @Override
//            public void onClosed() {
//                onPageSelected(mViewPager.getCurrentItem());
//            }
//        });
//        mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
//            @Override
//            public void onOpened() {
//                getSwipeBackLayout().setEnableGesture(false);
//            }
//        });
//        informationRight = new InformationRight(activity, mSlidingMenu, false);
//        informationRight.initRightMenu();
//        informationRight = MainActivity.informationRight;
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
        mNavigateElv = (ExpandableListView) activity.findViewById(R.id.elv_mainmenu_navigate);
        adapter = new TemplatesAdapter(activity);
        if (MainActivity.templates != null && MainActivity.navigations != null)
            adapter.setData(MainActivity.templates, MainActivity.navigations);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        mNavigateElv.setAdapter(wrapperAdapter);
        try {
            final SlidingUpPanelLayout slidLayout = (SlidingUpPanelLayout) activity.findViewById(R.id.sliding_layout);
            final CircleImageView headView = (CircleImageView) activity.findViewById(R.id.civ_mainmenu_head);
            final TextView nameView = (TextView) activity.findViewById(R.id.tv_mainmenu_name);
            final View menuLayout = activity.findViewById(R.id.ll_mainmenu_root);
            final View arrowView = activity.findViewById(R.id.itv_mainmenu_arrow);
            headView.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 60);
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
            ImageLoader.getInstance().displayImage(uri, headView, options);
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ContactActivity", "-----headView click-------");
                    if (slidLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        Log.e("ContactActivity", "-----headView click--SlidingUpPanelLayout.PanelState.EXPANDED-----");
                        slidLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    } else {
                        Log.e("ContactActivity", "-----headView click- finish----");
//                        Intent meIntent = new Intent(activity, MyInfoActivity.class);
//                        startActivity(meIntent); //个人中心
                        activity.sendBroadcast(new Intent(TalkerFragment.ACTION_TALKER_ITEM).putExtra("item", 5));
                    }
                }
            });
            nameView.setText(PrefsUtil.readUserInfo().Name);

            final int minWH = DisplayUtil.dip2px(activity, 30);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            slidLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    Log.e("ContactActivity", "-----onPanelSlide----");
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
                activity.sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM));
                activity.sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
            case 3:
//                mRbChat.setChecked(false);
//                mRbGroup.setChecked(false);
//                mRbPeople.setChecked(false);
                mRbMe.setChecked(true);
                activity.sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM));
                break;
            case 1:
//                mRbChat.setChecked(false);
                mRbGroup.setChecked(true);
//                mRbPeople.setChecked(false);
//                mRbMe.setChecked(false);
                activity.sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM));
                activity.sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
            case 2:
//                mRbChat.setChecked(false);
//                mRbGroup.setChecked(false);
                mRbPeople.setChecked(true);
//                mRbMe.setChecked(false);
                activity.sendBroadcast(new Intent(PersonFragments.ACTION_PERSON_IS_BOTTOM));
                activity.sendBroadcast(new Intent(GroupFragment.ACTION_GROUP_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(ChatHistoriesFragment.ACTION_CHAT_IS_BOTTOM).putExtra("enable", false));
                activity.sendBroadcast(new Intent(MyInfoFragment2.ACTION_ME_IS_BOTTOM).putExtra("enable", false));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Event(value = {R.id.ic_tv_chat, R.id.ic_tv_phone, R.id.im_contact_play, R.id.ic_tv_people, R.id.ic_tv_group, R.id.ic_civ_head, R.id.ic_et_del})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ic_et_del:
                mSearchEt.setText("");
                mSearchEt.clearFocus();
                break;
            case R.id.ic_civ_head:
                activity.sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.im_contact_play:  //拍照视频
//                Intent intent = new Intent(activity, CameraActivity.class);
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
//                                Intent intent = new Intent(activity, CameraActivity2.class);
//                                intent.putExtra("start_anim", false);
//                                intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CONTACT);
//                                startActivity(intent);
//                            }
//                        });
                Intent intent = new Intent(activity, CameraActivity.class);
                intent.putExtra("start_anim", false);
                intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CONTACT);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.push_up_in,
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
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null)
            activity.unregisterReceiver(mReceive);
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
                    activity.sendBroadcast(mIntent);
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
                    activity.sendBroadcast(intent);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(activity, "消息标记失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(activity);
        getSwipeBackLayout().scrollToFinishActivity();
    }
    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }

    }
    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            params.width = intent.getIntExtra("positionOffset", 0);
            params.height = intent.getIntExtra("positionOffset", 0);
            mHeaderIconIv.setLayoutParams(params);
        }

    }

}