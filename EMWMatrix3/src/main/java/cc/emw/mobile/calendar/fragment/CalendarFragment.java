package cc.emw.mobile.calendar.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_calendar)
public class CalendarFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton btnBack;
    @ViewInject(R.id.cm_header_rg_switch)
    private RadioGroup rgCalendar;
    @ViewInject(R.id.vp_calendar)
    private ViewPager mViewPager;
    @ViewInject(R.id.cm_header_rb_left)
    private RadioButton rbLeft;
    @ViewInject(R.id.cm_header_rb_middle)
    private RadioButton rbRight;
    @ViewInject(R.id.iv_add_calendar)
    private IconTextView imageView;
    @ViewInject(R.id.cm_header_btn_more)
    private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; // 顶部条右菜单按钮

    private Calendar mClickDate = Calendar.getInstance();
    private ListDialog mListDialog;
    private ListDialog mListDialogs;
    private String headTitle, headType;
    public static int tag = -1;
    public static int currentId = PrefsUtil.readUserInfo().ID;
    private List<ApiEntity.UserLabel> mDataList = new ArrayList<>();
    private List<ApiEntity.UserInfo> mUnderlings;
    private MyBroadCastReceicer receicer;
    public static final String ACTION_REFRESH_CALENDAR_FRAGMENT = "action_refresh_calendar_fragment";

    private class MyBroadCastReceicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getCalendarTag();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        IntentFilter filter = new IntentFilter();
        receicer = new MyBroadCastReceicer();
        filter.addAction(ACTION_REFRESH_CALENDAR_FRAGMENT);
        getActivity().registerReceiver(receicer, filter);
        getCalendarTag();
        getUserUnderling();
    }

    /**
     * 标签筛选
     */
    private void initListDialog() {
        if (getActivity() == null)
            return;
        mListDialog = new ListDialog(getActivity(), true);
        mListDialog.addItem("全部", -1);
        mListDialog.addItem("会议", 0);
        mListDialog.addItem("拜访", 1);
        mListDialog.addItem("电话", 2);
        mListDialog.addItem("邮件", 3);
        mListDialog.addItem("报告", 4);
        mListDialog.addItem("其他", 5);
        mListDialog.addItem("事件", 6);
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = 0; i < mDataList.size(); i++) {
                mListDialog.addItem(mDataList.get(i).Name, i + 7);
            }
        }
        mListDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                if (item.id == -1) {
                    headType = "";
                } else {
                    headType = item.text;
                }
                mTitle.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));
                tag = item.id;
                Intent intent = new Intent(
                        WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                getActivity().sendBroadcast(intent); // 刷新日程列表
                intent = new Intent(
                        CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                getActivity().sendBroadcast(intent); // 刷新日程月视图列表
            }
        });
    }

    private void initListDialogs() {
        if (getActivity() == null)
            return;
        mListDialogs = new ListDialog(getActivity(), true);
        mListDialogs.addItem(PrefsUtil.readUserInfo().Name, PrefsUtil.readUserInfo().ID);
        if (mUnderlings != null && mUnderlings.size() > 0) {
            for (int i = 0; i < mUnderlings.size(); i++) {
                mListDialogs.addItem(mUnderlings.get(i).Name, mUnderlings.get(i).ID);
            }
        }
        mListDialogs.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                currentId = item.id;
                Intent intent = new Intent(
                        WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                getActivity().sendBroadcast(intent); // 刷新日程列表
                intent = new Intent(
                        CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                getActivity().sendBroadcast(intent); // 刷新日程月视图列表
            }
        });
    }


    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
                getActivity().startActivity(noticeIntent);
                break;
        }
    }

    public CalendarFragment() {
    }

    private void init() {
        btnBack.setVisibility(View.VISIBLE);
        headTitle = "日程";
        mTitle.setText(headTitle);
        rgCalendar.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_sort);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.VISIBLE);
        rbRight.setVisibility(View.VISIBLE);
        rbLeft.setButtonDrawable(R.drawable.calendar_month_selector);
        rbRight.setButtonDrawable(R.drawable.calendar_week_selector);
        final Calendar nowCal = Calendar.getInstance();
        mUnderlings = new ArrayList<>();
        rgCalendar.setOnCheckedChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        CalendarCreateActivitys.class);
                mClickDate = MyCalendarFragmentss.mClickDate;
                if (nowCal.getTimeInMillis() - mClickDate.getTimeInMillis() <= 60 * 60 * 24 * 1000) {
                    intent.putExtra("createDate", mClickDate);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(getActivity(), "开始时间不能小于当前时间");
                }
            }
        });
        mHeaderMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSheetDialog dialog = new ActionSheetDialog(getActivity())
                        .builder();
                dialog.addSheetItem("人员",
                        null, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (mListDialogs != null)
                                    mListDialogs.show();
                            }
                        });
                dialog.addSheetItem("标签", null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (mListDialog != null)
                                    mListDialog.show();
                            }
                        });
                dialog.show();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cm_header_rb_left:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.cm_header_rb_middle:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mTitle.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));
                rbLeft.setChecked(true);
                mViewPager.setCurrentItem(0);
                break;
            case 1:
                mTitle.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));
                rbRight.setChecked(true);
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"日程", "月"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 1) {
                    fragment = new WeekFragment();
                } else {
                    fragment = new MyCalendarFragmentss();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    /**
     * 获取日程标签
     */
    private void getCalendarTag() {
        API.TalkerAPI.GetUserLabel(PrefsUtil.readUserInfo().ID, new RequestCallback<ApiEntity.UserLabel>(ApiEntity.UserLabel.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserLabel> respList) {
                mDataList.clear();
                if (respList != null && respList.size() > 0) {
                    mDataList.addAll(respList);
                    initListDialog();
                } else if (respList.size() == 0) {
                    initListDialog();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    /**
     * 获取用户的直属下属
     */
    private void getUserUnderling() {
        API.UserAPI.GetUserByParent(PrefsUtil.readUserInfo().ID, new RequestCallback<ApiEntity.UserInfo>(ApiEntity.UserInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
//                Toast.makeText(getActivity(), "获取直属下属失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    mUnderlings.addAll(respList);
                    initListDialogs();
                } else if (respList.size() == 0) {
                    initListDialogs();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receicer != null)
            getActivity().unregisterReceiver(receicer);
    }
}
