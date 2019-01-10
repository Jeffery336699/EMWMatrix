package cc.emw.mobile.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.contact.CiclerTalkFragment;
import cc.emw.mobile.contact.ContactFrament;
import cc.emw.mobile.dynamic.fragment.DynamicFragment;
import cc.emw.mobile.file.fragment.FileFragment2;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.view.ExViewPager;
import cc.emw.mobile.view.IconTextView;

public class TalkerFragment extends BaseFragment {
    private static final String TAG = "TalkerFragment";

    @ViewInject(R.id.ll_maintab_root)
    private LinearLayout mTabRootLayout;
    @ViewInject(R.id.itv_maintab_talker)
    private IconTextView mTabTalkerItv;
    @ViewInject(R.id.itv_maintab_calendar)
    private IconTextView mTabCalendarItv;
    @ViewInject(R.id.itv_maintab_time)
    private IconTextView mTabTimeItv;
    @ViewInject(R.id.itv_maintab_contact)
    private IconTextView mTabContactItv;
    @ViewInject(R.id.itv_maintab_file)
    private IconTextView mTabFileItv;
    @ViewInject(R.id.tv_maintab_talker)
    private TextView mTabTalkerTv;
    @ViewInject(R.id.tv_maintab_calendar)
    private TextView mTabCalendarTv;
    @ViewInject(R.id.tv_maintab_time)
    private TextView mTabTimeTv;
    @ViewInject(R.id.tv_maintab_contact)
    private TextView mTabContactTv;
    @ViewInject(R.id.tv_maintab_file)
    private TextView mTabFileTv;

    private ExViewPager mViewPager;
    @ViewInject(R.id.bottom_scrim)
    private View mBottomScrimView;

    public static final String ACTION_TALKER_ITEM = "cc.emw.mobile.talker_item"; //
    public static final String ACTION_FLOATING_MENU = "cc.emw.mobile.floating_menu"; //
    public static final String ACTION_HIDE_TOP_AND_BOTTOM = "cc.emw.mobile.hide_top_and_bottom";//
    public static final String ACTION_SHOW_TOP_AND_BOTTOM = "cc.emw.mobile.show_top_and_bottom";//
    private MyBroadcastReceive mReceive;
    private SparseArray<Fragment> fragmentMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentMap = new SparseArray<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_main, container, false);
        mViewPager = (ExViewPager) rootView.findViewById(R.id.tab_pager);
        mViewPager.setPagingEnabled(false); // 禁止左右滑动
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(new PageAdapter(getChildFragmentManager()));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TALKER_ITEM);
        intentFilter.addAction(ACTION_FLOATING_MENU);
        intentFilter.addAction(ACTION_HIDE_TOP_AND_BOTTOM);
        intentFilter.addAction(ACTION_SHOW_TOP_AND_BOTTOM);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive()->");
            String action = intent.getAction();
            if (ACTION_TALKER_ITEM.equals(action)) {
                int item = intent.getIntExtra("item", 0);
                Log.d(TAG, "onReceive()-> item = " + item);
                View v = new View(getActivity());
                switch (item) {
                    case 0:
                        v.setId(R.id.ll_maintab_talker);
                        onSwitchTabClick(v);
                        break;
                    case 1:
                        v.setId(R.id.ll_maintab_calendar);
                        onSwitchTabClick(v);
                        break;
                    case 2:
                        v.setId(R.id.ll_maintab_contact);
                        onSwitchTabClick(v);
                        break;
                    case 3:
                        Log.d(TAG, "onReceive()-> ll_maintab_time ");
                        v.setId(R.id.ll_maintab_time);
                        onSwitchTabClick(v);
                        break;
                    case 4:
                        v.setId(R.id.ll_maintab_file);
                        onSwitchTabClick(v);
                        break;
                    case 5:
                        mViewPager.setCurrentItem(2);
                        break;
                }
                //设置TimeTracking能够横竖切换,其他横屏显示
                getActivity().setRequestedOrientation(item == 3 ? ActivityInfo.SCREEN_ORIENTATION_SENSOR : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }/* else if (ACTION_HIDE_TOP_AND_BOTTOM.equals(action)) {
                mTabRootLayout.setTag(false);
                AnimUtil.setHideShowAnim(mTabRootLayout.getMeasuredHeight(), 500, mTabRootLayout);
            } else if (ACTION_SHOW_TOP_AND_BOTTOM.equals(action)) {
                mTabRootLayout.setTag(true);
                AnimUtil.setHideShowAnim(0, 500, mTabRootLayout);
                mTabRootLayout.setVisibility(View.VISIBLE);
            } */ else if (ACTION_FLOATING_MENU.equals(action)) {
                //底部选项卡显示，才显示底部遮罩
                if (intent.getBooleanExtra("opened", false) && mTabRootLayout.getTag() != null && (Boolean) mTabRootLayout.getTag()) {
                    mBottomScrimView.setVisibility(View.VISIBLE);
                } else {
                    mBottomScrimView.setVisibility(View.GONE);
                }
            }
        }
    }

    private Fragment getFragment(int position) {
        Fragment fragment = fragmentMap.get(position);
        if (fragment == null) {
            fragment = new TestFragment();
            switch (position) {
                case 0://Talker
                    fragment = DynamicFragment.newInstance("");
                    break;
                case 1://日程功能
                    fragment = new CalendarFragmentView();
                    break;
                case 2://圈子功能
//                    fragment = new ContactFragment();
                    // fragment = new ContactFrament();
                    fragment = new CiclerTalkFragment();
                    break;
                case 3://Timer Tracking
                    //项目
//                    fragment = new ProjectFragment();
//                    fragment = new TimeTrackingFragment();
                    Log.d(TAG, "getFragment  TimeTrackingWebFragment");
                    fragment = new TimeTrackingWebFragment();
                    break;
                case 4://知识库功能
//                    fragment = new FileFragment();
                    fragment = new FileFragment2();
                    break;
            }
            fragmentMap.put(position, fragment);
        }
        return fragment;
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
            return 5;
        }
    }

    @Event({R.id.ll_maintab_talker, R.id.ll_maintab_calendar, R.id.ll_maintab_contact, R.id.ll_maintab_time, R.id.ll_maintab_file})
    private void onSwitchTabClick(View v) {
        switch (v.getId()) {
            case R.id.ll_maintab_talker:
                mViewPager.setCurrentItem(0, false);
                mTabTalkerItv.setTextColor(Color.WHITE);
                mTabTalkerTv.setTextColor(Color.WHITE);
                mTabCalendarItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileTv.setTextColor(Color.parseColor("#80FFFFFF"));
                break;
            case R.id.ll_maintab_calendar:
                mViewPager.setCurrentItem(1, false);
                mTabTalkerItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTalkerTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarItv.setTextColor(Color.WHITE);
                mTabCalendarTv.setTextColor(Color.WHITE);
                mTabTimeItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileTv.setTextColor(Color.parseColor("#80FFFFFF"));
                break;
            case R.id.ll_maintab_contact: //圈子
//                Intent intent = new Intent(getActivity(), ContactActivity.class);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.push_up_in,
//                        R.anim.popup_hide);
                mViewPager.setCurrentItem(2, false);
                mTabTalkerItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTalkerTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactItv.setTextColor(Color.WHITE);
                mTabContactTv.setTextColor(Color.WHITE);
                mTabTimeItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileTv.setTextColor(Color.parseColor("#80FFFFFF"));
                break;
            case R.id.ll_maintab_time:
                mViewPager.setCurrentItem(3, false);
                mTabTalkerItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTalkerTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeItv.setTextColor(Color.WHITE);
                mTabTimeTv.setTextColor(Color.WHITE);
                mTabFileItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileTv.setTextColor(Color.parseColor("#80FFFFFF"));
                break;
            case R.id.ll_maintab_file:
                mViewPager.setCurrentItem(4, false);
                mTabTalkerItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTalkerTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabCalendarTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabContactTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeItv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabTimeTv.setTextColor(Color.parseColor("#80FFFFFF"));
                mTabFileItv.setTextColor(Color.WHITE);
                mTabFileTv.setTextColor(Color.WHITE);
                break;
        }
        getActivity().sendBroadcast(new Intent(ACTION_HIDE_TOP_AND_BOTTOM));
    }

}