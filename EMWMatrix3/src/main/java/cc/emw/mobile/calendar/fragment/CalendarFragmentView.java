package cc.emw.mobile.calendar.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.jeek.calendar.widget.calendar.MyTwoDirectScrollViews;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleState;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.calendar.EmployeeSelectActivity;
import cc.emw.mobile.calendar.adapter.ScalingListViewMonthAdapter;
import cc.emw.mobile.calendar.adapter.TestAdapter;
import cc.emw.mobile.calendar.component.MutiComponent;
import cc.emw.mobile.calendar.view.ObservableScrollViewHor;
import cc.emw.mobile.calendar.view.ObservableScrollViewVer;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import q.rorbin.badgeview.Badge;

@ContentView(R.layout.fragment_calendar_fragment_view)
public class CalendarFragmentView extends BaseFragment implements OnCalendarClickListener {

    @ViewInject(R.id.cal_header_btn_left)
    private CircleImageView mHeadImg;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.show_year_view)
    private TextView tvCurrentTime;
    @ViewInject(R.id.itv_cal_switch_person)
    private IconTextView mUnderUserBtn;     //选择下属按钮
    @ViewInject(R.id.cal_header_btn_notice)
    private ImageButton mHeaderNoticeIb;
    @ViewInject(R.id.slSchedule)
    private ScheduleLayout slSchedule;  //月/周视图容器
    @ViewInject(R.id.ll_calendar_month_week)
    private LinearLayout mRefreshLayout;
    @ViewInject(R.id.mcvCalendar)
    private MonthCalendarView mMonthCalendarView;   //月视图
    @ViewInject(R.id.wcvCalendar)
    private WeekCalendarView mWeekCalendarView; //周视图
    @ViewInject(R.id.ll_cal_text_layout)
    private LinearLayout mCalTextLayout;    //列表布局
    @ViewInject(R.id.ll_cal_pic_layout)
    private LinearLayout mCalPicLayout;     //图形布局
    @ViewInject(R.id.tv_calendar_month_num)
    private TextView clickNum;  //所选天日程的数量
    @ViewInject(R.id.my_content_view)
    private LinearLayout mContentView;  //日程日视图容器
    @ViewInject(R.id.mlv_slide_time)
    private MyListView mSlideTimeLv;    //侧边时间
    @ViewInject(R.id.ll_person_layout)
    private ObservableScrollViewHor mPersonLayout; //人员Layout
    @ViewInject(R.id.ll_person_contain)
    private LinearLayout llPersonContain;   //人员容器
    @ViewInject(R.id.two_scroll_view)
    private MyTwoDirectScrollViews twoWayNestedScrollView;
    @ViewInject(R.id.slide_time_layout)
    private ObservableScrollViewVer mSlideTimeLayout;
    private Badge mBadgeView;

    private String[] sildeTimes = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00",
            "21:00", "22:00", "23:00"};
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat time2 = new SimpleDateFormat("yyyy-MM-dd");
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private MyBroadcastReceive mReceive;
    private ScalingListViewMonthAdapter calendarFragmentAdapter;
    //    @ViewInject(R.id.scroll_cal_contain)
//    private MyScrollView mScrollContain;
    @ViewInject(R.id.rv_calendar_list)
    private ListView mListView;     //列表数据
    private int x, y;
    private List<ApiEntity.UserLabel> mLableList = new ArrayList<>();   //用户标签集合
    private Map<Integer, List<WeekViewEvent>> mCalendarViewMaps = new HashMap<>();  //视图map
    private Map<Integer, List<CalendarInfo>> mCalendarListMaps = new HashMap<>(); //列表map
    private Map<Integer, List<String>> mCalendarDotsMaps = new HashMap<>();    //事件小圆点map
    private List<Integer> mDots = new ArrayList<>();    //日程小圆点集合
    private List<Integer> mDotsWeek = new ArrayList<>();
    private List<String> mDotStrs = new ArrayList<>();  //精确到年月日事件
    private List<CalendarInfo> mSelectDataList = new ArrayList<>(); //选择人员之后日程列表的数据集合
    private List<CalendarInfo> mDataList = new ArrayList<>();   //所选择天的数据集合
    public static Calendar mClickDate = Calendar.getInstance(); //所点击的天
    public static Calendar mClickDate2 = Calendar.getInstance(); //所点击的天
    private List<UserInfo> mEmployList = new ArrayList<>(); //所选人员的集合
    private List<UserInfo> mUserInfos = new ArrayList<>(); //所有的人员集合
    private List<WeekViewEvent> events = new ArrayList<>();    //日程视图模块数据
    public static Guide guide;
    private int weeks = 0;// 用来全局控制 上一周，本周，下一周的周数变化

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 114:   //整体刷新
                    mPtrFrameLayout.refreshComplete();
                    tvCurrentTime.setVisibility(View.VISIBLE);
                    if (mDataList != null && mDataList.size() > 0)
                        clickNum.setText(mDataList.size() + " 个日程");
                    else
                        clickNum.setText("本日暂无事件");
                    for (int i = 0; i < mContentView.getChildCount(); i++) {
                        ((WeekView) mContentView.getChildAt(i)).notifyDatasetChanged();
                    }
                    if (mMonthCalendarView != null && mMonthCalendarView.getCurrentMonthView() != null) {   //绘制小圆点
                        mMonthCalendarView.getCurrentMonthView().setTaskHintList(mDots);
                        mWeekCalendarView.getCurrentWeekView().setTaskHintList(mDots);
                    }
                    calendarFragmentAdapter.notifyDataSetChanged();
                    break;
                case 115:
                    if (mMonthCalendarView != null && mMonthCalendarView.getCurrentMonthView() != null) {   //绘制小圆点
                        mMonthCalendarView.getCurrentMonthView().setTaskHintList(mDots);
                        mWeekCalendarView.getCurrentWeekView().setTaskHintList(mDotsWeek);
                    }
                    break;
                case 116:   //点击天 刷新日程列表
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            calendarFragmentAdapter.notifyDataSetChanged();
                            if (mDataList != null && mDataList.size() > 0)
                                clickNum.setText(mDataList.size() + " 个日程");
                            else
                                clickNum.setText("本日暂无事件");
                        }
                    }, 400);
                    break;
            }
        }
    };
    private MyReceiver receiver;

    @Override
    public void onClickDate(final int year, final int month, final int day) {
        tvCurrentTime.setText(year + "年" + (month + 1) + "月");
        final String selectTime = year + "-" + (month + 1) + "-" + day;
        try {

            //底部日程日视图切换跳转
            mClickDate.setTimeInMillis(time2.parse(selectTime).getTime());
            for (int i = 0; i < mContentView.getChildCount(); i++) {
                ((WeekView) mContentView.getChildAt(i)).goToDate(mClickDate);
            }

            mDots.clear();
            mDotsWeek.clear();
            final Calendar mWeekFirst = Calendar.getInstance();
            final Calendar mWeekEnd = Calendar.getInstance();
            int m = mClickDate.get(Calendar.DAY_OF_WEEK);
            mWeekFirst.setTimeInMillis(mClickDate.getTimeInMillis());
            mWeekEnd.setTimeInMillis(mClickDate.getTimeInMillis());
            mWeekFirst.add(Calendar.DAY_OF_WEEK, -(m - 1));
            mWeekEnd.add(Calendar.DAY_OF_WEEK, 7 - m);

            //是否是本月判断
            if (mClickDate2.get(Calendar.MONTH) == month && mClickDate2.get(Calendar.YEAR) == year) {
                //处理日程选中天的事件过滤
                sortEvent(mSelectDataList);
                calendarFragmentAdapter.notifyDataSetChanged();
                if (mDataList != null && mDataList.size() > 0)
                    clickNum.setText(mDataList.size() + " 个日程");
                else
                    clickNum.setText("本日暂无事件");

                //绘制小圆点
                if (mDotStrs != null && mDotStrs.size() > 0) {
                    for (int i = 0; i < mDotStrs.size(); i++) {
                        String[] arr = mDotStrs.get(i).split("-");
                        if (arr != null && arr.length == 3) {
                            Calendar clickCalendar = Calendar.getInstance();
                            clickCalendar.setTimeInMillis(time2.parse(arr[0] + "-" + arr[1] + "-" + arr[2]).getTime());
                            if (arr[0].equals(year + "") && arr[1].equals((month + 1) + "")) {
                                mDots.add(Integer.valueOf(arr[2]));
                            }
                            if (clickCalendar.getTimeInMillis() >= mWeekFirst.getTimeInMillis() &&
                                    clickCalendar.getTimeInMillis() <= mWeekEnd.getTimeInMillis())
                                mDotsWeek.add(Integer.valueOf(arr[2]));
                        }
                    }
                }
                if (mMonthCalendarView != null && mMonthCalendarView.getCurrentMonthView() != null) {   //绘制小圆点
                    mMonthCalendarView.getCurrentMonthView().setTaskHintList(mDots);
                    mWeekCalendarView.getCurrentWeekView().setTaskHintList(mDotsWeek);
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //处理日程选中天的事件过滤
                            sortEvent(mSelectDataList);

                            //绘制小圆点
                            if (mDotStrs != null && mDotStrs.size() > 0) {
                                for (int i = 0; i < mDotStrs.size(); i++) {
                                    String[] arr = mDotStrs.get(i).split("-");
                                    if (arr != null && arr.length == 3) {
                                        Calendar clickCalendar = Calendar.getInstance();
                                        clickCalendar.setTimeInMillis(time2.parse(arr[0] + "-" + arr[1] + "-" + arr[2]).getTime());
                                        if (arr[0].equals(year + "") && arr[1].equals((month + 1) + "")) {
                                            mDots.add(Integer.valueOf(arr[2]));
                                        }
                                        if (clickCalendar.getTimeInMillis() >= mWeekFirst.getTimeInMillis() &&
                                                clickCalendar.getTimeInMillis() <= mWeekEnd.getTimeInMillis())
                                            mDotsWeek.add(Integer.valueOf(arr[2]));
                                    }
                                }
                                mHandler.sendEmptyMessage(116);
                                mHandler.sendEmptyMessage(115);
                            }
                        } catch (Exception e) {
                        }
                    }
                }).start();
            }
            mClickDate2.setTimeInMillis(time2.parse(selectTime).getTime());
        } catch (Exception e) {
            Log.d("zrjtsss", e.getMessage());
        }
    }

    @Override
    public void onScrollDate(int year, int month, int minDay, int maxDay) {
        String minDayStr, maxDayStr;
        if (minDay < 10) {
            minDayStr = "0" + minDay;
        } else {
            minDayStr = minDay + "";
        }
        if (maxDay < 10) {
            maxDayStr = "0" + maxDay;
        } else {
            maxDayStr = maxDay + "";
        }
        Intent intent = new Intent(getActivity(), CalendarCreateActivitys.class);
        intent.putExtra("start_date", year + "-" + (month + 1) + "-" + minDayStr);
        intent.putExtra("end_date", year + "-" + (month + 1) + "-" + maxDayStr);
        intent.putExtra("start_anim", false);
        getActivity().startActivity(intent);
    }

    // 刷新的action
    public static final String ACTION_REFRESH_SCHEDULE_MONTH_LIST = "cc.emw.mobile.refresh_schedule_month_list";

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_REFRESH_SCHEDULE_MONTH_LIST.equals(action)) {
                    if (intent.hasExtra("person")) {
                        mEmployList.clear();
                        mEmployList = (List<UserInfo>) intent.getSerializableExtra(EmployeeSelectActivity.EXTRA_SELECT_LIST);
                        LinearLayout.LayoutParams paramWeekDayView;
                        HorizontalScrollView.LayoutParams paramTopBar;
                        llPersonContain.removeAllViews();
                        mContentView.removeAllViews();
                        if (mEmployList.size() > 1) {
                            mSelectDataList.clear();
                            mDots.clear();
                            mDotStrs.clear();
                            for (int i = 0; i < mEmployList.size(); i++) {
                                if (mCalendarListMaps.get(mEmployList.get(i).ID) != null)
                                    mSelectDataList.addAll(mCalendarListMaps.get(mEmployList.get(i).ID));
                                if (mCalendarDotsMaps.get(mEmployList.get(i).ID) != null)
                                    mDotStrs.addAll(mCalendarDotsMaps.get(mEmployList.get(i).ID));
                            }
                            if (mDotStrs != null && mDotStrs.size() > 0) {
                                for (int i = 0; i < mDotStrs.size(); i++) {
                                    String[] arr = mDotStrs.get(i).split("-");
                                    if (arr != null && arr.length == 3) {
                                        if (arr[0].equals(mClickDate.get(Calendar.YEAR) + "") && arr[1].equals((mClickDate.get(Calendar.MONTH) + 1) + "")) {
                                            mDots.add(Integer.valueOf(arr[2]));
                                        }
                                    }
                                }
                            }
                            mMonthCalendarView.getCurrentMonthView().setTaskHintList(mDots);
                            mWeekCalendarView.getCurrentWeekView().setTaskHintList(mDots);

                            Collections.sort(mSelectDataList);
                            sortEvent(mSelectDataList);

                            FrameLayout.LayoutParams params;
                            if (mEmployList.size() == 2) {
                                paramWeekDayView = new LinearLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                        - DisplayUtil.dip2px(getActivity(), 40)) / 2,
                                        DisplayUtil.dip2px(getActivity(), 1212));
                                paramTopBar = new HorizontalScrollView.LayoutParams(
                                        (DisplayUtil.getDisplayWidth(getActivity())
                                                - DisplayUtil.dip2px(getActivity(), 40)) / 2,
                                        DisplayUtil.dip2px(getActivity(), 40));
                                params = new FrameLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                        - DisplayUtil.dip2px(getActivity(), 40)) / 2
                                        * mEmployList.size(),
                                        DisplayUtil.dip2px(getActivity(), 1212));
                            } else {
                                paramWeekDayView = new LinearLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                        - DisplayUtil.dip2px(getActivity(), 100)) / 2,
                                        DisplayUtil.dip2px(getActivity(), 1212));
                                paramTopBar = new HorizontalScrollView.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                        - DisplayUtil.dip2px(getActivity(), 100)) / 2,
                                        DisplayUtil.dip2px(getActivity(), 40));
                                params = new FrameLayout.LayoutParams(((DisplayUtil.getDisplayWidth(getActivity())
                                        - DisplayUtil.dip2px(getActivity(), 100)) / 2)
                                        * mEmployList.size(),
                                        DisplayUtil.dip2px(getActivity(), 1212));
                            }
                            mContentView.setLayoutParams(params);
                            for (int i = 0; i < mEmployList.size(); i++) {
                                initWeekItem(paramWeekDayView, mEmployList.get(i).ID);
                                initPersonItem(paramTopBar, mEmployList, i);
                            }
                        } else {
                            mDots.clear();
                            mDotStrs.clear();
                            if (mCalendarDotsMaps.get(PrefsUtil.readUserInfo().ID) != null) {
                                mDotStrs.addAll(mCalendarDotsMaps.get(PrefsUtil.readUserInfo().ID));
                                if (mDotStrs != null && mDotStrs.size() > 0) {
                                    for (int i = 0; i < mDotStrs.size(); i++) {
                                        String[] arr = mDotStrs.get(i).split("-");
                                        if (arr != null && arr.length == 3) {
                                            if (arr[0].equals(mClickDate.get(Calendar.YEAR) + "") && arr[1].equals((mClickDate.get(Calendar.MONTH) + 1) + "")) {
                                                mDots.add(Integer.valueOf(arr[2]));
                                            }
                                        }
                                    }
                                }
                                mMonthCalendarView.getCurrentMonthView().setTaskHintList(mDots);
                                mWeekCalendarView.getCurrentWeekView().setTaskHintList(mDots);
                            }
                            mSelectDataList.clear();
                            mSelectDataList.addAll(mCalendarListMaps.get(PrefsUtil.readUserInfo().ID));
                            Collections.sort(mSelectDataList);
                            sortEvent(mSelectDataList);
                            // 底部weekdayView
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 40),
                                    DisplayUtil.dip2px(getActivity(), 1212));
                            mContentView.setLayoutParams(params);
                            LinearLayout.LayoutParams paramWeekDayViews = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 40),
                                    DisplayUtil.dip2px(getActivity(), 1212));
                            initWeekItem(paramWeekDayViews, PrefsUtil.readUserInfo().ID);

                            // 头部人员View
                            HorizontalScrollView.LayoutParams paramTopBars = new HorizontalScrollView.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 40),
                                    DisplayUtil.dip2px(getActivity(), 40));
                            initPersonItem(paramTopBars, mEmployList, 0);
                        }
                        if (mDataList != null && mDataList.size() > 0)
                            clickNum.setText(mDataList.size() + " 个日程");
                        else
                            clickNum.setText("本日暂无事件");
                        calendarFragmentAdapter.notifyDataSetChanged();
                    } else if (intent.hasExtra("edit")) {
                        List<UserInfo> mUserInfo = new ArrayList<>();
                        mUserInfo.add(PrefsUtil.readUserInfo());
                        getPersonsCalendar(PrefsUtil.readUserInfo().ID + "", mUserInfo);
                    }
                } else if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                    int count = intent.getIntExtra("unread_count", 0);
                    mBadgeView.setBadgeNumber(count);
                } else if (DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
                    mHeadImg.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
                    String uri = String.format(Const.DOWN_ICON_URL,
                            PrefsUtil.readUserInfo().CompanyCode,
                            PrefsUtil.readUserInfo().Image);
                    ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeadImg), options,
                            new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);
//                Picasso.with(getActivity())
//                        .load(uri)
//                        .resize(DisplayUtil.dip2px(getActivity(), 28), DisplayUtil.dip2px(getActivity(), 28))
//                        .centerCrop()
//                        .config(Bitmap.Config.ALPHA_8)
//                        .placeholder(R.drawable.cm_img_head)
//                        .error(R.drawable.cm_img_head)
//                        .into(mHeadImg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public CalendarFragmentView() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (DisplayUtil.navigationBarExist2(getActivity())) {
            int height = DisplayUtil.getNavigationBarHeight(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(slSchedule.getLayoutParams());
            lp.setMargins(0, 0, 0, -height);
            slSchedule.setLayoutParams(lp);
        }
        mHeadImg.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode,
                PrefsUtil.readUserInfo().Image);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeadImg), options,
                new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);
//        Picasso.with(getActivity())
//                .load(uri)
//                .resize(DisplayUtil.dip2px(getActivity(), 28), DisplayUtil.dip2px(getActivity(), 28))
//                .centerCrop()
//                .config(Bitmap.Config.ALPHA_8)
//                .placeholder(R.drawable.cm_img_head)
//                .error(R.drawable.cm_img_head)
//                .into(mHeadImg);
        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mHeaderNoticeIb);
        initRefreshLayout();
        initData();
        initWeekView();
        initCrash();
        receiver = new MyReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh.head");
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onFirstUserVisible() {
        if (PrefsUtil.isCalFirst()) {   //初始化新手引导
            PrefsUtil.setCalGuide(false);
            getActivity().getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                getActivity().getWindow()
                                        .getDecorView()
                                        .getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            } else {
                                getActivity().getWindow()
                                        .getDecorView()
                                        .getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showGuideView();
                                }
                            }, 300);
                        }
                    });
        }
    }

    /**
     * 新手引导
     */
    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(tvCurrentTime)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
            }
        });

        builder.addComponent(new MutiComponent());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(getActivity());
    }

    private void initData() {
        mEmployList.add(PrefsUtil.readUserInfo());
        mCalPicLayout.setTag("1");
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_SCHEDULE_MONTH_LIST);
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        slSchedule.setOnCalendarClickListener(this);
        tvCurrentTime.setText(mClickDate.get(Calendar.YEAR) + "年"
                + (mClickDate.get(Calendar.MONTH) + 1) + "月");

//        mListView = slSchedule.getSchedulerRecyclerView();

        calendarFragmentAdapter = new ScalingListViewMonthAdapter(mDataList, getActivity());
        mListView.setAdapter(calendarFragmentAdapter);
        tvCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMonthCalendarView.setTodayToView();
            }
        });
    }

    /**
     * 初始化底部weekView,人员头部View
     */
    private void initWeekView() {
        List<String> mTimes = new ArrayList<>();
        for (int i = 0; i < sildeTimes.length; i++) {
            mTimes.add(sildeTimes[i]);
        }
        mSlideTimeLv.setAdapter(new TestAdapter(getActivity(), mTimes));

        inflater = LayoutInflater.from(getActivity());
        llPersonContain.removeAllViews();
        mContentView.removeAllViews();
        // 底部weekdayView
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity()) - DisplayUtil.dip2px(getActivity(), 40),
                DisplayUtil.dip2px(getActivity(), 1212));
        mContentView.setLayoutParams(params);
        LinearLayout.LayoutParams paramWeekDayView = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity()) - DisplayUtil.dip2px(getActivity(), 40),
                DisplayUtil.dip2px(getActivity(), 1212));
        initWeekItem(paramWeekDayView, PrefsUtil.readUserInfo().ID);

        // 头部人员View
        HorizontalScrollView.LayoutParams paramTopBar = new HorizontalScrollView.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                - DisplayUtil.dip2px(getActivity(), 40),
                DisplayUtil.dip2px(getActivity(), 40));
        initPersonItem(paramTopBar, mEmployList, 0);
        /**
         * 联动
         */
        mSlideTimeLayout.setScrollView(twoWayNestedScrollView);
        mPersonLayout.setScrollView(twoWayNestedScrollView);
        twoWayNestedScrollView.setScrollView(mSlideTimeLayout);
        twoWayNestedScrollView.setScrollView2(mPersonLayout);
    }

    private void initWeekItem(LinearLayout.LayoutParams paramWeekDayView, final int userIds) {
        final WeekView weekView = new WeekView(getActivity());
        weekView.setNumberOfVisibleDays(1);
        weekView.setDayBackgroundColor(Color.WHITE);
        weekView.setHourHeight(DisplayUtil.dip2px(getActivity(), 50));
        weekView.setColumnGap((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                        .getDisplayMetrics()));
        weekView.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources()
                        .getDisplayMetrics()));
        weekView.setEventTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources()
                        .getDisplayMetrics()));
        mContentView.addView(weekView, paramWeekDayView);
        weekView.goToDate(mClickDate);
        weekView.setMonthChangeListener(new WeekView.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> viewEventList = mCalendarViewMaps.get(userIds);
                return viewEventList != null ? viewEventList : events;
            }
        });
        weekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                for (CalendarInfo uwh : mSelectDataList) {
                    if (uwh.ID == event.getId()) {
                        Intent intent = new Intent(getActivity(),
                                CalendarInfoActivity2.class);
                        intent.putExtra("calendarInfo", uwh);
                        intent.putExtra("start_anim", false);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initPersonItem(HorizontalScrollView.LayoutParams paramTopBar, List<UserInfo> mEmployList, int position) {
        View view = inflater.inflate(R.layout.cal_listitem_person, null);
        CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.cal_listitem_person_head);
        TextView textView = (TextView) view.findViewById(R.id.cal_listitem_person_name);
        circleImageView.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), mEmployList.get(position).Name, 28);
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, mEmployList.get(position).Image);
        imageLoader.displayImage(uri, new ImageViewAware(circleImageView), options,
                new ImageSize(DisplayUtil.dip2px(getActivity(), 20), DisplayUtil.dip2px(getActivity(), 20)), null, null);
        //当前用户姓名
        textView.setText(mEmployList.get(position).Name);
        llPersonContain.addView(view, paramTopBar);
    }

    private void initRefreshLayout() {
        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setToggleMenu(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(true);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mRefreshLayout, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                tvCurrentTime.setVisibility(View.INVISIBLE);
                getCalendarTag();
                getUserUnderling();
            }
        });
    }

    /**
     * 滑动冲突
     */
    private void initCrash() {

        /**
         * 隐藏显示底部toolbar(暂时不用)
         */
//        slSchedule.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                if (slSchedule.getmState() == ScheduleState.OPEN)
////                    toggleTopAndBottom("cc.emw.mobile.hide_top_and_bottom");
////                else if (slSchedule.getmState() == ScheduleState.CLOSE)
////                    toggleTopAndBottom("cc.emw.mobile.show_top_and_bottom");
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        y = (int) event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int currentY = (int) event.getRawY();
//                        if (currentY - y > 0) {
//                            toggleTopAndBottom("cc.emw.mobile.show_top_and_bottom");
//                        } else {
//                            toggleTopAndBottom("cc.emw.mobile.hide_top_and_bottom");
//                        }
//                        y = currentY;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return false;
//            }
//        });

        slSchedule.setListView(mListView);

        //日程日视图

        twoWayNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (slSchedule.getmState() == ScheduleState.OPEN) {
                    if (twoWayNestedScrollView.getScrollY() == 0) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                        return true;
                    } else if (twoWayNestedScrollView.getScrollY() ==
                            twoWayNestedScrollView.getChildAt(0).getHeight() - twoWayNestedScrollView.getHeight()) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                    } else {
                        slSchedule.requestDisallowInterceptTouchEvent(true);
                    }
                } else if (slSchedule.getmState() == ScheduleState.CLOSE) {
                    if (twoWayNestedScrollView.getScrollY() == 0) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                        if (twoWayNestedScrollView.getScrollY() ==
                                twoWayNestedScrollView.getChildAt(0).getHeight() - twoWayNestedScrollView.getHeight()) {
                            slSchedule.requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        slSchedule.requestDisallowInterceptTouchEvent(true);
                    }
                }
//                if (twoWayNestedScrollView.getChildAt(0).getHeight() - twoWayNestedScrollView.getHeight()
//                        == twoWayNestedScrollView.getScrollY()) {   //滑动到底部
//                }
                return false;
            }
        });

        //日程侧边时间列表
        mSlideTimeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (slSchedule.getmState() == ScheduleState.OPEN) {
                    if (mSlideTimeLayout.getScrollY() == 0) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                        return true;
                    } else if (mSlideTimeLayout.getScrollY() ==
                            mSlideTimeLayout.getChildAt(0).getHeight() - mSlideTimeLayout.getHeight()) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                    } else {
                        slSchedule.requestDisallowInterceptTouchEvent(true);
                    }
                } else if (slSchedule.getmState() == ScheduleState.CLOSE) {
                    if (mSlideTimeLayout.getScrollY() == 0) {
                        slSchedule.requestDisallowInterceptTouchEvent(false);
                        if (mSlideTimeLayout.getScrollY() ==
                                mSlideTimeLayout.getChildAt(0).getHeight() - mSlideTimeLayout.getHeight()) {
                            slSchedule.requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        slSchedule.requestDisallowInterceptTouchEvent(true);
                    }
                }
//                if (mSlideTimeLayout.getChildAt(0).getHeight() - mSlideTimeLayout.getHeight()
//                        == mSlideTimeLayout.getScrollY()) {   //滑动到底部
//                }
                return false;
            }
        });
    }

    /**
     * 显示隐藏底部
     *
     * @param action
     */
    public void toggleTopAndBottom(String action) {
        Intent intentBroadCast = new Intent();
        intentBroadCast.setAction(action);
        getActivity().sendBroadcast(intentBroadCast);
    }

    /**
     * 获取用户的直属下属
     */
    private void getUserUnderling() {
        API.UserAPI.GetUserByParent(PrefsUtil.readUserInfo().ID, new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                tvCurrentTime.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "获取员工列表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                mUserInfos.clear();
                if (respList == null || respList.size() == 0) {
                    mUnderUserBtn.setVisibility(View.INVISIBLE);
                } else {
                    mUnderUserBtn.setVisibility(View.VISIBLE);
                }
                mUserInfos.add(PrefsUtil.readUserInfo());
                mUserInfos.addAll(respList);
                StringBuilder mEmployIds = new StringBuilder();
                for (int i = 0; i < mUserInfos.size(); i++) {
                    mEmployIds.append(mUserInfos.get(i).ID);
                    if (i != mUserInfos.size() - 1)
                        mEmployIds.append(",");
                }
                getPersonsCalendar(mEmployIds.toString(), mUserInfos);
            }
        });
    }

    /**
     * 获取当前用户及其下属日程
     *
     * @param sUserIds
     */
    private void getPersonsCalendar(String sUserIds, final List<UserInfo> mUserInfos) {
        API.TalkerAPI.GetAllNewCalenderListByUsers(sUserIds, false, new RequestCallback<CalendarInfo>(CalendarInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("position", "throwable = " + throwable.getMessage());
                mPtrFrameLayout.refreshComplete();
                tvCurrentTime.setVisibility(View.VISIBLE);
                ToastUtil.showToast(getActivity(), "获取用户日程失败!");
            }

            @Override
            public void onParseSuccess(final List<CalendarInfo> respList) {
                new Thread() {
                    @Override
                    public void run() {
                        dealDatas(respList, mUserInfos);
                    }
                }.start();
            }
        });
    }

    /**
     * 处理获取的日程数据
     *
     * @param respList
     */
    private void dealDatas(List<CalendarInfo> respList, List<UserInfo> mUserInfos) {
        if (respList != null && respList.size() > 0) {
            if (mUserInfos.size() == 1) {
                if (mCalendarListMaps.get(PrefsUtil.readUserInfo().ID) != null)
                    mCalendarListMaps.get(PrefsUtil.readUserInfo().ID).clear();
                if (mCalendarViewMaps.get(PrefsUtil.readUserInfo().ID) != null)
                    mCalendarViewMaps.get(PrefsUtil.readUserInfo().ID).clear();
                if (mCalendarDotsMaps.get(PrefsUtil.readUserInfo().ID) != null)
                    mCalendarDotsMaps.get(PrefsUtil.readUserInfo().ID).clear();
            } else {
                mCalendarListMaps.clear();
                mCalendarViewMaps.clear();
                mCalendarDotsMaps.clear();
            }
            mDots.clear();
            mDotStrs.clear();
            for (int m = 0; m < mUserInfos.size(); m++) {
                List<WeekViewEvent> weekViewEvents = new ArrayList<>();
                List<CalendarInfo> mPersonCalendarList = new ArrayList<>();
                List<String> temporaryDots = new ArrayList<>();
                for (int i = 0; i < respList.size(); i++) {
                    CalendarInfo waitInfo = respList.get(i);
                    if (mUserInfos.get(m).ID == waitInfo.UserID) {
                        String start = waitInfo.StartTime;
                        String end = waitInfo.OverTime;
                        if (waitInfo.Allday == 1) {
                            end = end.substring(0, 11);
                            end = end + "23:59:59";
                        }
                        // 实例化weekView
                        WeekViewEvent event;
                        try {
                            Calendar startTime = Calendar.getInstance();
                            Calendar endTime = Calendar.getInstance();
                            Calendar duringCal = Calendar.getInstance();
                            startTime.setTimeInMillis(time.parse(start)
                                    .getTime());
                            endTime.setTimeInMillis(time.parse(end)
                                    .getTime());
                            duringCal.setTimeInMillis(time.parse(start)
                                    .getTime());
                            event = new WeekViewEvent(waitInfo.ID, waitInfo.Title, startTime, endTime);
                            int colorId = waitInfo.Color;
                            int color = 0;
                            switch (colorId) {
                                case -1:
                                    color = R.color.cal_tag;
                                    break;
                                case 0:
                                    color = R.color.cal_tag1;
                                    break;
                                case 1:
                                    color = R.color.cal_tag2;
                                    break;
                                case 2:
                                    color = R.color.cal_tag3;
                                    break;
                                case 3:
                                    color = R.color.cal_tag4;
                                    break;
                                case 4:
                                    color = R.color.cal_tag5;
                                    break;
                                case 5:
                                    color = R.color.cal_tag6;
                                    break;
                                case 6:
                                    color = R.color.cal_tag7;
                                    break;
                            }
                            if (isAdded()) {
                                if (color == 0)
                                    event.setColor(getResources().getColor(R.color.cal_tag));
                                else
                                    event.setColor(getResources().getColor(color));
                            }
                            event.setColorId(colorId);
                            // 添加数据
                            if (startTime.getTimeInMillis() < endTime.getTimeInMillis()) {
                                weekViewEvents.add(event);
                                mPersonCalendarList.add(waitInfo);
                            }
                            int year = duringCal.get(Calendar.YEAR);
                            int month = duringCal.get(Calendar.MONTH) + 1;
                            int day = duringCal.get(Calendar.DAY_OF_MONTH);
                            temporaryDots.add(year + "-" + month + "-" + day);
                            if (startTime.getTimeInMillis() < endTime.getTimeInMillis()) {
                                // 绘制有事件的天的小圆点
                                StringBuilder startBuilder = new StringBuilder();
                                StringBuilder endBuilder = new StringBuilder();
                                startBuilder.append(startTime.get(Calendar.YEAR)).append("-").append(startTime.get(Calendar.MONTH) + 1).append("-").append(startTime.get(Calendar.DAY_OF_MONTH));
                                endBuilder.append(endTime.get(Calendar.YEAR)).append("-").append(endTime.get(Calendar.MONTH) + 1).append("-").append(endTime.get(Calendar.DAY_OF_MONTH));
                                int between = Integer.valueOf(CalendarUtil.getTwoDay(endBuilder.toString(), startBuilder.toString()));
                                if (between != 0) {
                                    for (int j = 0; j < between; j++) {
                                        duringCal.add(Calendar.DAY_OF_MONTH, 1);
                                        year = duringCal.get(Calendar.YEAR);
                                        month = duringCal.get(Calendar.MONTH) + 1;
                                        day = duringCal.get(Calendar.DAY_OF_MONTH);
                                        temporaryDots.add(year + "-" + month + "-" + day);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d("zrjtsss", e.getMessage());
                        }
                    }
                }
                mCalendarDotsMaps.put(mUserInfos.get(m).ID, temporaryDots);
                mCalendarViewMaps.put(mUserInfos.get(m).ID, weekViewEvents);
                mCalendarListMaps.put(mUserInfos.get(m).ID, mPersonCalendarList);
            }
            mSelectDataList.clear();
            if (mEmployList.size() == 1) {
                mSelectDataList.addAll(mCalendarListMaps.get(PrefsUtil.readUserInfo().ID));
                mDotStrs.addAll(mCalendarDotsMaps.get(PrefsUtil.readUserInfo().ID));
            } else {
                for (int i = 0; i < mEmployList.size(); i++) {
                    mSelectDataList.addAll(mCalendarListMaps.get(mEmployList.get(i).ID));
                    mDotStrs.addAll(mCalendarDotsMaps.get(mEmployList.get(i).ID));
                }
            }
            for (int i = 0; i < mDotStrs.size(); i++) {
                String[] arr = mDotStrs.get(i).split("-");
                if (arr != null && arr.length == 3) {
                    if (arr[0].equals(mClickDate.get(Calendar.YEAR) + "") && arr[1].equals((mClickDate.get(Calendar.MONTH) + 1) + "")) {
                        mDots.add(Integer.valueOf(arr[2]));
                    }
                }
            }
            Collections.sort(mSelectDataList);
            sortEvent(mSelectDataList);
        }
        Message message = Message.obtain();
        message.what = 114;
        mHandler.sendMessage(message);
    }

    /**
     * 所选中的天
     *
     * @param mSelectDataList
     */
    private void sortEvent(List<CalendarInfo> mSelectDataList) {
        mDataList.clear();
        //日程的时间
        Calendar sCal;
        Calendar eCal;
        //点击的时间
        final Calendar clickCalLast = Calendar.getInstance();
        final Calendar clickCalFirst = Calendar.getInstance();
        //现在的时间
        final Calendar nowCalFirst = Calendar.getInstance();
        final Calendar nowCalLast = Calendar.getInstance();
        int nowYear = nowCalFirst.get(Calendar.YEAR);
        int nowMonth = nowCalFirst.get(Calendar.MONTH) + 1;
        int nowDay = nowCalFirst.get(Calendar.DAY_OF_MONTH);
        // 点击的天
        int cDays;
        StringBuilder nowTimeLast = new StringBuilder();
        StringBuilder nowTimeFirst = new StringBuilder();
        nowTimeLast.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("23:59:59");
        nowTimeFirst.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("00:00:00");
        for (int i = 0; i < mSelectDataList.size(); i++) {
            final CalendarInfo waitInfo = mSelectDataList.get(i);
            try {
                // 当前的日期
                Date nowDateLast = time.parse(nowTimeLast.toString());
                Date nowDateFirst = time.parse(nowTimeFirst.toString());
                // 当前的时间
                nowCalLast.setTimeInMillis(nowDateLast.getTime());
                nowCalFirst.setTimeInMillis(nowDateFirst.getTime());
                //日程的时间
                sCal = Calendar.getInstance();
                eCal = Calendar.getInstance();
                String startTimes = waitInfo.StartTime;
                String endTimes = waitInfo.OverTime;
                //日程开始
                Date dayStart = time.parse(startTimes);
                sCal.setTimeInMillis(dayStart.getTime());
                //日程结束
                Date dayEnd = time.parse(endTimes);
                eCal.setTimeInMillis(dayEnd.getTime());
                if (mClickDate != null) {
                    int cYear = mClickDate.get(Calendar.YEAR);
                    int cMonth = mClickDate.get(Calendar.MONTH) + 1;
                    cDays = mClickDate.get(Calendar.DAY_OF_MONTH);
                    String clickTimeFirst = cYear + "-" + cMonth + "-" + cDays
                            + " " + "00:00:00";
                    String clickTimeLast = cYear + "-" + cMonth + "-" + cDays
                            + " " + "23:59:59";
                    Date clickDateFirst = time.parse(clickTimeFirst);
                    Date clickDateLast = time.parse(clickTimeLast);
                    clickCalFirst.setTimeInMillis(clickDateFirst.getTime());
                    clickCalLast.setTimeInMillis(clickDateLast.getTime());
                }
                // 判断是否是点击天的日程
                if (clickCalLast.getTimeInMillis() >= sCal
                        .getTimeInMillis()
                        && clickCalFirst.getTimeInMillis() <= eCal
                        .getTimeInMillis()) {
                    mDataList.add(waitInfo);
                }
            } catch (Exception e) {
                Log.w("zrjtsss", e.getMessage());
            }
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
                if (respList != null && respList.size() > 0) {
                    mLableList.addAll(respList);
                } else if (respList.size() == 0) {
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    // 点击事件
    @Event(value = {R.id.iv_add_calendar, R.id.cal_header_btn_left, R.id.itv_cal_switch_style, R.id.itv_cal_switch_person, R.id.cal_header_btn_notice})
    private void topClick(View view) {
        switch (view.getId()) {
            case R.id.itv_cal_switch_style:
                if (mCalPicLayout.getTag().equals("1")) {
                    mCalPicLayout.setVisibility(View.GONE);
                    mCalTextLayout.setVisibility(View.VISIBLE);
                    mCalPicLayout.setTag("0");
                } else {
//                    mListView.setSelection(0);
                    mCalPicLayout.setVisibility(View.VISIBLE);
                    mCalTextLayout.setVisibility(View.GONE);
                    mCalPicLayout.setTag("1");
                }
                break;
            case R.id.iv_add_calendar:
                Intent intent = new Intent(getActivity(),
                        CalendarCreateActivitys.class);
                if (Calendar.getInstance().getTimeInMillis() - mClickDate.getTimeInMillis() <= 60 * 60 * 24 * 1000) {
                    intent.putExtra("createDate", mClickDate);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(getActivity(), "开始时间不能小于当前时间");
                }
                break;
            case R.id.cal_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cal_header_btn_notice:
//                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
//                getActivity().startActivity(noticeIntent);
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
            case R.id.itv_cal_switch_person:
                if (mUserInfos != null && mUserInfos.size() > 0) {
                    Intent personIntent = new Intent(getActivity(), EmployeeSelectActivity.class);
                    personIntent.putExtra(EmployeeSelectActivity.EXTRA_USER_LIST, (ArrayList) mUserInfos);
                    personIntent.putExtra(EmployeeSelectActivity.EXTRA_SELECT_LIST, (ArrayList) mEmployList);
                    personIntent.putExtra("start_anim", false);
                    getActivity().startActivity(personIntent);
                } else {
                    ToastUtil.showToast(getActivity(), "获取下属成员失败");
                }
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获得某日期本周星期日的日期
     *
     * @return
     */
    public String getSunDay(Date date) {
        int mondayPlus = this.getMondayPlus(date);
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTime(date);
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = df.format(monday);
        return preMonday;
    }

    /**
     * 获得某日期与本周日相差的天数
     *
     * @return
     */
    private int getMondayPlus(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    /**
     * 获得相应周的周六的日期
     *
     * @return
     */
    public String getSaturday(Date date) {
        int mondayPlus = this.getMondayPlus(date);
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = df.format(monday);
        return preMonday;
    }

    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null) {
            getActivity().unregisterReceiver(mReceive);
        }
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
            mHeadImg.setLayoutParams(params);

        }

    }
}
