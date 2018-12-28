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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.calendar.EmployeeSelectActivity;
import cc.emw.mobile.calendar.abs.RefreshListener;
import cc.emw.mobile.calendar.adapter.MonthCalendarAdpter;
import cc.emw.mobile.calendar.adapter.ScalingListViewMonthAdapter;
import cc.emw.mobile.calendar.adapter.TestAdapter;
import cc.emw.mobile.calendar.adapter.WeekCalendarAdpter;
import cc.emw.mobile.calendar.component.MutiComponent;
import cc.emw.mobile.calendar.utils.DateUtils;
import cc.emw.mobile.calendar.view.MyTwoDirectScrollView;
import cc.emw.mobile.calendar.view.ObservableScrollViewHor;
import cc.emw.mobile.calendar.view.ObservableScrollViewVer;
import cc.emw.mobile.calendar.widget.HandMoveLayout;
import cc.emw.mobile.calendar.widget.HasTwoAdapterViewpager;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import cc.emw.mobile.view.MyListView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我的日程
 */
@ContentView(R.layout.fragment_my_calenders)
public class MyCalendarFragmentss extends BaseFragment implements RefreshListener {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.show_year_view)
    private TextView tvCurrentTime;
    @ViewInject(R.id.my_content_view)
    private LinearLayout mContentView;  //日程日视图容器
    @ViewInject(R.id.ll_person_layout)
    private ObservableScrollViewHor mPersonLayout; //人员Layout
    @ViewInject(R.id.ll_person_contain)
    private LinearLayout llPersonContain;   //人员容器
    @ViewInject(R.id.two_scroll_view)
    private MyTwoDirectScrollView twoWayNestedScrollView;
    @ViewInject(R.id.slide_time_layout)
    private ObservableScrollViewVer mSlideTimeLayout;
    @ViewInject(R.id.mlv_slide_time)
    private MyListView mSlideTimeLv;
    @ViewInject(R.id.tv_calendar_month_time)
    private TextView clickTime; //点击的时间
    @ViewInject(R.id.tv_calendar_month_num)
    private TextView clickNum;  //当前天日程的数量
    @ViewInject(R.id.list)
    private ListView mListView;
    @ViewInject(R.id.ll_cal_text_layout)
    private LinearLayout mCalTextLayout;
    @ViewInject(R.id.ll_cal_pic_layout)
    private LinearLayout mCalPicLayout;
    @ViewInject(R.id.ll_calendar_month_week)
    private LinearLayout mRefreshLayout;
    @ViewInject(R.id.itv_cal_switch_person)
    private IconTextView mUnderUserBtn;     //选择下属按钮

    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat time2 = new SimpleDateFormat("yyyy-MM-dd");
    private HasTwoAdapterViewpager viewPager;
    private HasTwoAdapterViewpager viewpagerWeek;
    private List<View> views;
    private List<View> viewss;
    private HandMoveLayout handMoveLayout;
    public static Calendar mClickDate = Calendar.getInstance(); //所点击的天
    private String[] sildeTimes = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00",
            "21:00", "22:00", "23:00"};
    private WeekCalendarAdpter weekCalendarAdpter;
    private LayoutInflater inflater;
    private MonthCalendarAdpter adpter;
    //    private Dialog mLoadingDialog;
    // 更新日程数据
    private MyBroadcastReceive mReceive;
    // 刷新的action
    public static final String ACTION_REFRESH_SCHEDULE_MONTH_LIST = "cc.emw.mobile.refresh_schedule_month_list";
    /**
     * 用于接收上面日期改变的消息
     */
    public static final int change = 90;
    public static final int change2 = 91;
    public static final int pagerNext = 101;
    public static final int pagerLast = 102;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ListDialog mListDialog;
    private List<ApiEntity.UserLabel> mLableList = new ArrayList<>();
    private List<UserInfo> mUserInfos = new ArrayList<>();
    private List<UserInfo> mEmployList = new ArrayList<>();
    private List<String> mHotEvents = new ArrayList<>();
    private Map<Integer, List<WeekViewEvent>> mCalendarViewMaps = new HashMap<>();  //视图map
    private Map<Integer, List<CalendarInfo>> mCalendarListMaps = new HashMap<>(); //列表map
    private Map<Integer, List<String>> hotTimesStrMaps = new HashMap<>();    //事件小圆点map
    private List<CalendarInfo> mSelectDataList = new ArrayList<>(); //选择人员之后日程列表的数据集合
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();    //日程视图模块数据
    private List<CalendarInfo> mDataList = new ArrayList<>();   //所选择天的数据集合
    private ScalingListViewMonthAdapter scalingListViewMonthAdapter;
    private Guide guide;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
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
                        mHotEvents.clear();

                        for (int i = 0; i < mEmployList.size(); i++) {
                            if (mCalendarListMaps.get(mEmployList.get(i).ID) != null)
                                mSelectDataList.addAll(mCalendarListMaps.get(mEmployList.get(i).ID));
                            if (hotTimesStrMaps.get(mEmployList.get(i).ID) != null)
                                mHotEvents.addAll(hotTimesStrMaps.get(mEmployList.get(i).ID));
                        }
                        mHotEvents = duplicateRemova(mHotEvents); //list去重
                        refreshListener(viewPager);
                        refreshListener(viewpagerWeek);
                        weekCalendarAdpter.notifyDataSetChanged();
                        adpter.notifyDataSetChanged();
                        Collections.sort(mSelectDataList);
                        sortEvent(mSelectDataList);
                        scalingListViewMonthAdapter.notifyDataSetChanged();

                        FrameLayout.LayoutParams params;
                        if (mEmployList.size() == 2) {
                            paramWeekDayView = new LinearLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 40)) / 2,
                                    DisplayUtil.dip2px(getActivity(), 1200));
                            paramTopBar = new HorizontalScrollView.LayoutParams(
                                    (DisplayUtil.getDisplayWidth(getActivity())
                                            - DisplayUtil.dip2px(getActivity(), 40)) / 2,
                                    DisplayUtil.dip2px(getActivity(), 40));
                            params = new FrameLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 40)) / 2
                                    * mEmployList.size(),
                                    DisplayUtil.dip2px(getActivity(), 1200));
                        } else {
                            paramWeekDayView = new LinearLayout.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 100)) / 2,
                                    DisplayUtil.dip2px(getActivity(), 1200));
                            paramTopBar = new HorizontalScrollView.LayoutParams((DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 100)) / 2,
                                    DisplayUtil.dip2px(getActivity(), 40));
                            params = new FrameLayout.LayoutParams(((DisplayUtil.getDisplayWidth(getActivity())
                                    - DisplayUtil.dip2px(getActivity(), 100)) / 2)
                                    * mEmployList.size(),
                                    DisplayUtil.dip2px(getActivity(), 1200));
                        }
                        mContentView.setLayoutParams(params);
                        for (int i = 0; i < mEmployList.size(); i++) {
                            initWeekItem(paramWeekDayView, mEmployList.get(i).ID);
                            initPersonItem(paramTopBar, mEmployList, i);
                        }
                    } else {
                        mHotEvents.clear();
                        if (hotTimesStrMaps.get(PrefsUtil.readUserInfo().ID) != null)
                            mHotEvents.addAll(hotTimesStrMaps.get(PrefsUtil.readUserInfo().ID));
                        refreshListener(viewPager);
                        refreshListener(viewpagerWeek);
                        weekCalendarAdpter.notifyDataSetChanged();
                        adpter.notifyDataSetChanged();
                        mSelectDataList.clear();
                        mSelectDataList.addAll(mCalendarListMaps.get(PrefsUtil.readUserInfo().ID));
                        Collections.sort(mSelectDataList);
                        sortEvent(mSelectDataList);
                        scalingListViewMonthAdapter.notifyDataSetChanged();
                        // 底部weekdayView
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                - DisplayUtil.dip2px(getActivity(), 40),
                                DisplayUtil.dip2px(getActivity(), 1200));
                        mContentView.setLayoutParams(params);
                        LinearLayout.LayoutParams paramWeekDayViews = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                - DisplayUtil.dip2px(getActivity(), 40),
                                DisplayUtil.dip2px(getActivity(), 1200));
                        initWeekItem(paramWeekDayViews, PrefsUtil.readUserInfo().ID);

                        // 头部人员View
                        HorizontalScrollView.LayoutParams paramTopBars = new HorizontalScrollView.LayoutParams(DisplayUtil.getDisplayWidth(getActivity())
                                - DisplayUtil.dip2px(getActivity(), 40),
                                DisplayUtil.dip2px(getActivity(), 40));
                        initPersonItem(paramTopBars, mEmployList, 0);
                    }
                } else if (intent.hasExtra("edit")) {
                    List<UserInfo> mUserInfo = new ArrayList<>();
                    mUserInfo.add(PrefsUtil.readUserInfo());
                    getPersonsCalendar(PrefsUtil.readUserInfo().ID + "", mUserInfo);
                }
            }
        }
    }

    public MyCalendarFragmentss() {
        super();
    }

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
                    refreshListener(viewPager);
                    refreshListener(viewpagerWeek);
                    weekCalendarAdpter.notifyDataSetChanged();
                    adpter.notifyDataSetChanged();
                    scalingListViewMonthAdapter.notifyDataSetChanged();
                    for (int i = 0; i < mContentView.getChildCount(); i++) {
                        ((WeekView) mContentView.getChildAt(i)).notifyDatasetChanged();
                    }
                    break;
            }
        }
    };

    View rootView;

    private int y;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
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
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
                tvCurrentTime.setVisibility(View.INVISIBLE);
            }
        }, 100);
        mCalPicLayout.setTag("1");
        mEmployList.add(PrefsUtil.readUserInfo());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_SCHEDULE_MONTH_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        initCalendar();

        clickTime.setText(adpter.getSelectTime());

        scalingListViewMonthAdapter = new ScalingListViewMonthAdapter(mDataList, getActivity());
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setAdapter(scalingListViewMonthAdapter);

        try {
            mClickDate.setTimeInMillis(time2.parse(adpter.getSelectTime()).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        handMoveLayout = (HandMoveLayout) rootView.findViewById(R.id.hand_move_layout);
        handMoveLayout.setHandler(os);

        initWeekView();
        /**
         * 滑动冲突
         */
        twoWayNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handMoveLayout.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int currentY = (int) event.getRawY();
                        if (currentY - y > 0) {
                            toggleTopAndBottom("cc.emw.mobile.show_top_and_bottom");
                        } else {
                            toggleTopAndBottom("cc.emw.mobile.hide_top_and_bottom");
                        }
                        y = currentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        mSlideTimeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handMoveLayout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mSlideTimeLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handMoveLayout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handMoveLayout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        tvCurrentTime.setText(mClickDate.get(Calendar.YEAR) + "年" + (mClickDate.get(Calendar.MONTH) + 1) + "月");
        refresh();
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

    private void refresh() {

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
                            showGuideView();
                        }
                    });
        }
    }

    /**
     * 初始化底部weekView
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
                DisplayUtil.dip2px(getActivity(), 1200));
        mContentView.setLayoutParams(params);
        LinearLayout.LayoutParams paramWeekDayView = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(getActivity()) - DisplayUtil.dip2px(getActivity(), 40),
                DisplayUtil.dip2px(getActivity(), 1200));
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
                for (CalendarInfo uwh : mDataList) {
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
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, mEmployList.get(position).Image);
        imageLoader.displayImage(uri, new ImageViewAware(circleImageView), options,
                new ImageSize(DisplayUtil.dip2px(getActivity(), 20), DisplayUtil.dip2px(getActivity(), 20)), null, null);
        //当前用户姓名
        textView.setText(mEmployList.get(position).Name);
        llPersonContain.addView(view, paramTopBar);
    }

    Handler os = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 90) {
                // 选中的时间监听
                try {
                    mClickDate.setTimeInMillis(time2.parse(adpter.getSelectTime()).getTime());
                    tvCurrentTime.setText(mClickDate.get(Calendar.YEAR) + "年" + (mClickDate.get(Calendar.MONTH) + 1) + "月");
                    scalingListViewMonthAdapter.setMClickDate(mClickDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sortEvent(mSelectDataList);
                if (mDataList != null && mDataList.size() > 0)
                    clickNum.setText(mDataList.size() + " 个日程");
                else
                    clickNum.setText("本日暂无事件");
                clickTime.setText(adpter.getSelectTime());
                sortEvent(mSelectDataList);
                scalingListViewMonthAdapter.notifyDataSetChanged();
                for (int i = 0; i < mContentView.getChildCount(); i++) {
                    ((WeekView) mContentView.getChildAt(i)).goToDate(mClickDate);
                }
            } else if (msg.what == change2) {
                handMoveLayout.setRowNum((Integer) msg.obj);
            } else if (msg.what == pagerNext) {
                pagerNext();
            } else if (msg.what == pagerLast) {
                pagerLast();
            }
        }
    };

    int currentItem = 0;

    @Override
    public void refreshListener(final ViewPager viewPager) {
        //得到这个selecttime对应的currentItem
        currentItem = 0;
        if (viewPager.getAdapter() instanceof MonthCalendarAdpter) {
            tvCurrentTime.setText(mClickDate.get(Calendar.YEAR) + "年" + (mClickDate.get(Calendar.MONTH) + 1) + "月");
            adpter.getTimeList((ArrayList<String>) mHotEvents);
            //月视图
            currentItem = getMonthCurrentItem();
            int odl = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem, false);
            //刷新已经存在的3个视图view
            if (Math.abs(odl - currentItem) <= 1) {
                adpter.instantiateItem(viewPager, viewPager.getCurrentItem() - 1);

                adpter.instantiateItem(viewPager, viewPager.getCurrentItem());

                adpter.instantiateItem(viewPager, viewPager.getCurrentItem() + 1);
            }
            ViewGroup day = adpter.getDay();
            adpter.notifyDataSetChanged();
        } else {
            //周视图
            currentItem = getWeekCurrentItem();
            //如果是周日，就是下一周，+1
            if (DateUtils.getWeekStr(DateUtils.stringToDate(adpter.getSelectTime())).equals("星期日")) {
                currentItem++;
            }
            weekCalendarAdpter.getTimeList((ArrayList<String>) mHotEvents);
            int odl = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem, false);
            //刷新已经存在的3个视图view
            if (Math.abs(odl - currentItem) <= 1) {
                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem() - 1);

                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem());

                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem() + 1);
            }
            weekCalendarAdpter.notifyDataSetChanged();
        }

    }

    //得到月视图选中日期后的CurrentItem
    private int getMonthCurrentItem() {
//        //此刻
//        Calendar today = new GregorianCalendar();
//        today.setTimeInMillis(System.currentTimeMillis());
//        //选中时间
//        String time = adpter.getSelectTime();
//        Date date = DateUtils.stringToDate(time);
//        Calendar select = new GregorianCalendar();
//        select.setTimeInMillis(date.getTime());
        //选中时间的(MONTH)-此刻(MONTH)=月数
//        int aa = select.get(Calendar.MONTH) - today.get(Calendar.MONTH);
//        int aa = Math.abs(Integer.valueOf(CalendarUtil.getTwoDay(getTimeString(time2, today), getTimeString(time2, select))));
//        if (today.getTimeInMillis() > select.getTimeInMillis()) {
//            return (int) (adpter.getCount() / 2 - Math.ceil(aa / 30.0));
//        } else {
//            return (int) (adpter.getCount() / 2 + Math.round(aa / 30.0));
//        }
        String nowTimeStr = "";
        Calendar toDay = Calendar.getInstance();
        Calendar selectDay = Calendar.getInstance();
        selectDay.setTimeInMillis(DateUtils.stringToDate(adpter.getSelectTime()).getTime());
        int month = toDay.get(Calendar.MONTH) + 1;
        if (month < 10)
            nowTimeStr = toDay.get(Calendar.YEAR) + "-0" + month;
        else
            nowTimeStr = toDay.get(Calendar.YEAR) + "-" + month;
        String selectTimeStr = adpter.getSelectTime().substring(0, 7);
        if (toDay.getTimeInMillis() > selectDay.getTimeInMillis()) {
            return adpter.getCount() / 2 - getBetweenMonth(selectTimeStr, nowTimeStr);
        } else {
            int aa = getBetweenMonth(nowTimeStr, selectTimeStr);
            if (aa == 0)
                return adpter.getCount() / 2;
            else
                return adpter.getCount() / 2 + aa;
        }
    }

    //得到周视图选中日期后的CurrentItem
    public int getWeekCurrentItem() {
        //此刻
//        Calendar today = new GregorianCalendar();
//        today.setTimeInMillis(System.currentTimeMillis());
//        //转为本周一
//        int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
//        if (day_of_week == 0) {
//            day_of_week = 7;
//        }
//        today.add(Calendar.DATE, -day_of_week);
        Calendar today = Calendar.getInstance();
        String nowTimeStr = getMondayOFWeek(today.getTime());
        today.setTimeInMillis(DateUtils.stringToDate(nowTimeStr).getTime());
        //选中时间
        String time = weekCalendarAdpter.getSelectTime();
        Date date = DateUtils.stringToDate(time);
        Calendar select = new GregorianCalendar();
        select.setTimeInMillis(date.getTime());
//        String selectTimeStr = getMondayOFWeek(date);
//        select.setTimeInMillis(DateUtils.stringToDate(selectTimeStr).getTime());

        //选中时间的(day of yeay)-此刻(day of yeay)=天数
//        int aa = ((int) (select.getTime().getTime() / 1000) - (int) (today.getTime().getTime() / 1000)) / 3600 / 24;
//        int aa2 = 0;
//        if (Math.abs(aa) % 7 == 0) {
//            aa2 = Math.abs(aa) / 7;
//        } else {
//            aa2 = Math.abs(aa) / 7;
//        }
//        if (aa >= 0) {
//            return weekCalendarAdpter.getCount() / 2 + aa2;
//        } else {
//            return weekCalendarAdpter.getCount() / 2 - aa2 - 1;
//        }
        int aa = Math.abs(Integer.valueOf(CalendarUtil.getTwoDay(getTimeString(time2, today), getTimeString(time2, select))));
        if (today.getTimeInMillis() > select.getTimeInMillis()) {
            if (aa % 7 == 0) {
                return weekCalendarAdpter.getCount() / 2 - (aa / 7) - 1;
            } else {
                return (int) (weekCalendarAdpter.getCount() / 2 - Math.ceil(aa / 7.0));
            }
        } else {
            if (aa % 7 == 0) {
                return weekCalendarAdpter.getCount() / 2 + aa / 7 - 1;
            } else {
                return weekCalendarAdpter.getCount() / 2 + aa / 7;
            }
        }
    }

    // 点击事件
    @Event(value = {R.id.iv_add_calendar, R.id.cal_header_btn_left, R.id.cal_header_btn_notice, R.id.itv_cal_switch_person, R.id.itv_cal_switch_style})
    private void topClick(View view) {
        switch (view.getId()) {
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
            case R.id.itv_cal_switch_style:
                if (mCalPicLayout.getTag().equals("1")) {
                    mCalPicLayout.setVisibility(View.GONE);
                    mCalTextLayout.setVisibility(View.VISIBLE);
                    mCalPicLayout.setTag("0");
                } else {
                    mCalPicLayout.setVisibility(View.VISIBLE);
                    mCalTextLayout.setVisibility(View.GONE);
                    mCalPicLayout.setTag("1");
                }
                break;
            case R.id.cal_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cal_header_btn_notice:
                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
                getActivity().startActivity(noticeIntent);
                break;
            case R.id.itv_cal_switch_person:
//                ActionSheetDialog dialog = new ActionSheetDialog(getActivity())
//                        .builder();
//                dialog.addSheetItem("人员",
//                        null, new ActionSheetDialog.OnSheetItemClickListener() {
//                            @Override
//                            public void onClick(int which) {
                if (mUserInfos != null && mUserInfos.size() > 0) {
                    Intent personIntent = new Intent(getActivity(), EmployeeSelectActivity.class);
                    personIntent.putExtra(EmployeeSelectActivity.EXTRA_USER_LIST, (ArrayList) mUserInfos);
                    personIntent.putExtra(EmployeeSelectActivity.EXTRA_SELECT_LIST, (ArrayList) mEmployList);
                    personIntent.putExtra("start_anim", false);
                    getActivity().startActivity(personIntent);
                } else {
                    ToastUtil.showToast(getActivity(), "获取下属成员失败");
                }
//                            }
//                        });
//                dialog.addSheetItem("标签", null,
//                        new ActionSheetDialog.OnSheetItemClickListener() {
//                            @Override
//                            public void onClick(int which) {
//                                if (mListDialog != null)
//                                    mListDialog.show();
//                            }
//                        });
//                dialog.show();
                break;
        }
    }


    /**
     * 调整到下个月
     */
    public void pagerNext() {
        if (viewPager != null) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    /**
     * 调整到上个月
     */
    public void pagerLast() {
        if (viewPager != null) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * 初始化日程月视图
     */
    public void initCalendar() {
        viewPager = (HasTwoAdapterViewpager) rootView.findViewById(R.id.calendar_viewpager);
        viewpagerWeek = (HasTwoAdapterViewpager) rootView.findViewById(R.id.calendar_viewpager_week);
        viewPager.setListener(this);
        viewpagerWeek.setListener(this);
        //制造月视图所需view
        views = new ArrayList<>();
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout1 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout2 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout3 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        views.add(layout);
        views.add(layout1);
        views.add(layout2);
        views.add(layout3);
        adpter = new MonthCalendarAdpter(views, getActivity(), (ArrayList<String>) mHotEvents);
        adpter.setHandler(os);

        //制造日试图所需view
        viewss = new ArrayList();
        LinearLayout layoutri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout1ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout2ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout3ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        viewss.add(layoutri);
        viewss.add(layout1ri);
        viewss.add(layout2ri);
        viewss.add(layout3ri);
        weekCalendarAdpter = new WeekCalendarAdpter(viewss, getActivity(), (ArrayList<String>) mHotEvents);
        weekCalendarAdpter.setHandler(os);

        viewPager.setAdapter(adpter);
        viewPager.setCurrentItem(1200, true);

        viewpagerWeek.setAdapter(weekCalendarAdpter);
        viewpagerWeek.setCurrentItem(weekCalendarAdpter.getCount() / 2, true);

        //如果是周日，就翻到下一页
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("position", Integer.toString(position));

            }

            @Override
            public void onPageSelected(int position) {
                Calendar today = new GregorianCalendar();
                today.setTimeInMillis(System.currentTimeMillis());
                //距离当前时间的月数(按月算)
                int month = adpter.getCount() / 2 - position;
                today.add(Calendar.MONTH, -month);
                int months = today.get(Calendar.MONTH) + 1;
                tvCurrentTime.setText(today.get(Calendar.YEAR) + "年" + months + "月");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpagerWeek.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar today = new GregorianCalendar();
                today.setTimeInMillis(System.currentTimeMillis());

                int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
                if (day_of_week == 0) {
                    day_of_week = 7;
                }
                today.add(Calendar.DATE, -day_of_week);
                //距离当前时间的周数(按周算)
                int week = weekCalendarAdpter.getCount() / 2 - position;
                today.add(Calendar.DATE, -week * 7);
//                setBarTitle(getTopTitleTime(today));
                //刷新本页
                int months = today.get(Calendar.MONTH) + 1;
                tvCurrentTime.setText(today.get(Calendar.YEAR) + "年" + months + "月");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                if (hotTimesStrMaps.get(PrefsUtil.readUserInfo().ID) != null)
                    hotTimesStrMaps.get(PrefsUtil.readUserInfo().ID).clear();
            } else {
                mCalendarListMaps.clear();
                mCalendarViewMaps.clear();
                hotTimesStrMaps.clear();
            }
            mHotEvents.clear();
            Collections.sort(respList);
            for (int m = 0; m < mUserInfos.size(); m++) {
                List<WeekViewEvent> weekViewEvents = new ArrayList<>();
                List<CalendarInfo> mPersonCalendarList = new ArrayList<>();
                List<String> hotTimesStrs = new ArrayList<>();  //有事件的天数字符串集合
                for (int i = 0; i < respList.size(); i++) {
                    CalendarInfo waitInfo = respList.get(i);
                    if (mUserInfos.get(m).ID == waitInfo.UserID) {
                        String start = waitInfo.StartTime;
                        String end = waitInfo.OverTime;
                        if (waitInfo.Allday == 1) {
                            end = end.substring(0, 11);
                            end = end + "23:59:59";
                        }
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

                            if (startTime.getTimeInMillis() < endTime.getTimeInMillis()) {
                                weekViewEvents.add(event);
                                mPersonCalendarList.add(waitInfo);
                            }

                            int year = duringCal.get(Calendar.YEAR);
                            int month = duringCal.get(Calendar.MONTH) + 1;
                            int day = duringCal.get(Calendar.DAY_OF_MONTH);
                            String months = month + "";
                            String days = day + "";
                            if (month < 10)
                                months = "0" + months;
                            if (day < 10)
                                days = "0" + days;
                            // 有事件的天集合
//                            if (CalendarFragment.tag == waitInfo.Color) {
                            hotTimesStrs.add(year + "-" + months + "-" + days);
//                            } else if (CalendarFragment.tag == -1) {
//                                hotTimesStrs.add(year + "-" + months + "-" + days);
//                            }
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
                                        months = month + "";
                                        days = day + "";
                                        if (month < 10)
                                            months = "0" + months;
                                        if (day < 10)
                                            days = "0" + days;
                                        if (CalendarFragment.tag == waitInfo.Color) {
                                            hotTimesStrs.add(year + "-" + months + "-" + days);
                                        } else if (CalendarFragment.tag == -1) {
                                            hotTimesStrs.add(year + "-" + months + "-" + days);
                                        }
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mCalendarViewMaps.put(mUserInfos.get(m).ID, weekViewEvents);
                mCalendarListMaps.put(mUserInfos.get(m).ID, mPersonCalendarList);
                hotTimesStrMaps.put(mUserInfos.get(m).ID, hotTimesStrs);
            }
            mHotEvents.addAll(hotTimesStrMaps.get(PrefsUtil.readUserInfo().ID));
            mHotEvents = duplicateRemova(mHotEvents);
            mSelectDataList.clear();
            for (int i = 0; i < mEmployList.size(); i++) {
                Log.d("zzzz", mEmployList.get(i).Name + mEmployList.get(i).ID);
            }
            Log.d("zzzz", PrefsUtil.readUserInfo().ID + "current");
            if (mEmployList.size() == 1)
                mSelectDataList.addAll(mCalendarListMaps.get(PrefsUtil.readUserInfo().ID));
            else {
                for (int i = 0; i < mEmployList.size(); i++) {
                    mSelectDataList.addAll(mCalendarListMaps.get(mEmployList.get(i).ID));
                }
            }
            Collections.sort(mSelectDataList);
            sortEvent(mSelectDataList);
            Message message = Message.obtain();
            message.what = 114;
            mHandler.sendMessage(message);
        }
    }

    private void sortEvent(List<CalendarInfo> cInfos) {
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
        int cDays = 0;
        StringBuilder nowTimeLast = new StringBuilder();
        StringBuilder nowTimeFirst = new StringBuilder();
        nowTimeLast.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("23:59:59");
        nowTimeFirst.append(nowYear).append("-").append(nowMonth).append("-").append(nowDay).append(" ").append("00:00:00");
        for (int i = 0; i < cInfos.size(); i++) {
            final CalendarInfo waitInfo = cInfos.get(i);
            try {
                // 当前的日期
                Date nowDateLast = null;
                nowDateLast = time.parse(nowTimeLast.toString());
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
//                    if (waitInfo.Color == tag) {
//                        mDataList.add(waitInfo);
//                    } else if (tag == -1) {
//                        mDataList.add(waitInfo);
//                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
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
                mDataList.clear();
                if (respList != null && respList.size() > 0) {
                    mLableList.addAll(respList);
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
        API.UserAPI.GetUserByParent(PrefsUtil.readUserInfo().ID, new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("position", "My throwable = "+throwable.getMessage());
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
     * 获取当前用户日程
     *
     * @param userId
     */
    private void getScheduleList(final int userId) {
        API.TalkerAPI.GetAllCalenderListByUserId(userId, new RequestCallback<CalendarInfo>(
                        CalendarInfo.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        ToastUtil.showToast(getActivity(), "获取用户日程失败!");
                    }

                    @Override
                    public void onParseSuccess(final List<CalendarInfo> respList) {
                        new Thread() {
                            @Override
                            public void run() {
                            }
                        }.start();
                    }
                }
        );
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
        if (mLableList != null && mLableList.size() > 0) {
            for (int i = 0; i < mLableList.size(); i++) {
                mListDialog.addItem(mLableList.get(i).Name, i + 7);
            }
        }
        mListDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                Intent intent = new Intent(
                        WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                getActivity().sendBroadcast(intent); // 刷新日程列表
                intent = new Intent(ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                intent.putExtra("tag", "tag");
                getActivity().sendBroadcast(intent); // 刷新日程月视图列表
            }
        });
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
        guide.setShouldCheckLocInWindow(true);
        guide.show(getActivity());
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    /**
     * 获取指点格式时间字符串
     *
     * @param time2      时间格式
     * @param mClickDate 时间
     * @return
     */
    public String getTimeString(SimpleDateFormat time2, Calendar mClickDate) {
        String hehe = time2.format(mClickDate.getTime());
        return hehe;
    }

    /**
     * 获取两个时间之间间隔几个月
     *
     * @param time
     * @param time2
     * @return
     */
    private int getBetweenMonth(String time, String time2) {
        int months = 0;
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time));
            c2.setTime(df.parse(time2));
            while (c1.getTimeInMillis() < c2.getTimeInMillis()) {
                ++months;
                c1.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
        }
        return months;
    }

    public String getMondayOFWeek(Date date) {
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTime(date);
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        String preMonday = time2.format(monday);
        return preMonday;
    }

    /**
     * 获得当前日期与本周日相差的天数
     *
     * @return
     */
    private int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK); // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    /**
     * 去重无序
     *
     * @param list
     * @return
     */
    public List<String> duplicateRemova(List<String> list) {
        List<String> listRes = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>(list);
        listRes.addAll(set);
        return listRes;
    }
}
