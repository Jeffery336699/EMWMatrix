package cc.emw.mobile.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bigkoo.pickerview.listener.OnShowListener;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.adapter.CalendarTagAdapter;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.dynamic.fragment.DynamicFragment;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.map.GeoFenceActivity;
import cc.emw.mobile.me.fragment.WaitHandleCalendarFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SwipeBackScrollView;
import cc.emw.mobile.view.SwitchButton;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_edit3)
public class CalendarEditActivity extends BaseActivity implements OnClickListener, OnShowListener, OnDismissListener {
    //    @ViewInject(R.id.cm_header_bar)
//    private LinearLayout lLTitle;
//    @ViewInject(R.id.cm_header_btn_left)
//    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
//    @ViewInject(R.id.cm_header_tv_title)
//    private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_tv_right)
//    private TextView mHeaderSendTv; // 顶部条发布
    @ViewInject(R.id.scroll_calendar_edit)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.cm_input_et_content)
    private EditText mNameEt; // 名称
    @ViewInject(R.id.cm_input_et_content_scan)
    private EditText mDescEt; // 描述
    @ViewInject(R.id.work_project_relation)
    private LinearLayout projectLayout; // 相关项目
    @ViewInject(R.id.task_relation)
    private LinearLayout taskLayout; // 相关任务
    @ViewInject(R.id.file_relation)
    private LinearLayout fileLayout;// 知识库
    //    @ViewInject(R.id.relation_file_num1)
//    private TextView tvFileName1; // 相关文件1
//    @ViewInject(R.id.relation_file_num2)
//    private TextView tvFileName2; // 相关文件2
//    @ViewInject(R.id.tv_calendar_relation_file)
//    private TextView tvMoreFileTips;
    @ViewInject(R.id.tv_calendar_file_number)
    private TextView tvFileNumber;
    @ViewInject(R.id.relation_project_num)
    private TextView tvProjectNum; // 相关项目的数量
    @ViewInject(R.id.relation_task_num)
    private TextView tvTaskNum; // 相关任务的的数量
    @ViewInject(R.id.schedule_btn_startdate)
    private Button mStartDateBtn; // 开始日期按钮
    @ViewInject(R.id.schedule_btn_enddate)
    private Button mEndDateBtn; // 结束日期按钮
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mSelectRootLayout; // 选择分享人员Layout
    @ViewInject(R.id.cm_select_tv_name)
    private TextView mSelectTv; // 分享范围
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout
    @ViewInject(R.id.tv_calendar_color_select)
    private TextView mTvColorSelect;    //颜色
    @ViewInject(R.id.schedule_ll_allday)
    private LinearLayout mAllDayLayout; // 全天事件Layout
    @ViewInject(R.id.schedule_sb_allday)
    private SwitchButton mAllDaySb; // 全天事件开关按钮
    @ViewInject(R.id.schedule_ll_repeat)
    private LinearLayout mRepeatLayout; // 重复选择Layout
    @ViewInject(R.id.ll_calendar_tixing)
    private LinearLayout tiXingLayout; // 提醒选择的Layout
    @ViewInject(R.id.schedule_tv_repeat)
    private TextView repeatInfoTv; // 重复的详情
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView txTextView; // 提醒的详情
    @ViewInject(R.id.schedule_tv_hintbefore_end)
    private TextView txEndTextView; // 提醒的结束时间
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView mHintBeforeTv; // 提前
    @ViewInject(R.id.iv_repeat_more)
    private ImageView mIvRepeat;
    @ViewInject(R.id.iv_tixing_more)
    private ImageView mIvTiXing;
    @ViewInject(R.id.iv_end_repeat_more)
    private ImageView mIvEndRepeat;
    @ViewInject(R.id.ll_calendar_tixing_end)
    private LinearLayout mEndTxLayout;  // 截止提醒Layout
    @ViewInject(R.id.tv_task_modify_location)
    private TextView mLocationTv;

    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> mSelectList = new ArrayList<>(); // 分享人员列表数据
    private boolean flagShareMember;    //分享人员标志
    private TimePickerView mStartPopupWindow, mEndPopupWindow, mEndRepeatPopupWindow; // 开始、结束、重复中结束时间popupwindow
    private Date mCurDate, mStartDate, mEndDate; // 当前、开始、结束日期

    // private CalendarInfo note; //
    private int waitID;
    private CalendarInfo schedule = new CalendarInfo(); // 日程的实体类
    private SimpleDateFormat format, format2, format3, format4;
    public static final int TASK = 144;
    public static final int PROJECT = 145;
    public static final int ZHISHIKU = 146;
    private static final String TAG = "CalendarEditActivity";
    private ArrayList<UserFenPai> tasks = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<UserFenPai> taskRets = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<Files> fileRets = new ArrayList<Files>();
    private ArrayList<Files> files = new ArrayList<Files>(); // 相关文件的选择列表
    private ArrayList<UserProject> projects = new ArrayList<UserProject>(); // 相关项目的选择列表

    // private String repeatInfoStr = "";
    public static final int TIXING_REQUEST = 23;
    public static final int TIXING_REQUEST_END = 24;

    private int type = -1; // 重复的类型
    private int fixType = -1; // 固定的类型
    private int pinlv; // 重复的频率
    private int fixPinlv; // 固定重复的频率
    private String repeatInfoStr; // 重复的结果
    private String typeStr; // 类型的字符串
    private String weekNum; // 重复的周数字模式
    private String fixRepeatInfoStr; // 固定的重复时间
    //    private String fixTiXingInfoStr; // 固定的提醒时间
//    private String tixingEndStr; // 提醒结束时间的字符串
    //private int[] colors = new int[]{0x00B0CB5E, 0x00007EFF, 0x006DC53C, 0x00E0C400, 0x00D8745C, 0x00795AF9};
    private int[] colors;   // 日程颜色集合
    private int colorWhat = -2;  // 编辑日程之后的颜色
    private ApiEntity.UserLabel userLabel;
    private int calendarId; // 传递过来的日程id
    public static final String CALENDARID = "calendarId";

    // 更改代码begin
    private boolean mIsClick = false;// 顶部右上角是编辑展示 true展示的是取消
    // 更改代码end

    // 日程标签
    private CollapseView repeats;   // 重复Layout
    private LinearLayout fixHzRepeatLayout;
    private LinearLayout freeRepeatSwLayout;
    private SwitchButton freeRepeatSw;  //自定义重复的开关
    private LinearLayout freeRepeatSLayout; //自定义重复的Content
    private CollapseView tiXingCV;  //提醒Layout
    private CollapseView tiXingEndLayout; // 重复截止Layout
    private CollapseView mTagLayout;
    private String endRepeatTime; // 重复截止的时间
    private RelativeLayout mTagContent;
    private RelativeLayout mRepeatContent;
    private List<ApiEntity.UserLabel> mLables;
    private CalendarTagAdapter adapter;
    private ApiEntity.UserLabel tagSelect;
    private int select; //选中标签的下标
    //自定义重复
    private int fixHeight;
    private ArrayList<String> tags = new ArrayList<String>(); // 标签的时间滚轴
    private int n = 7; // 数量的时间滚轴
    private boolean isFree;
    private boolean isShowRr = true;
    private ScrollView freeScrollView;
    private LinearLayout llrepeatSelect;
    private TextView plTextViews;
    private TextView rrTextView;
    private WheelView optionsType;
    private LinearLayout weekLayout; // 周选择视图
    private CheckBox checkBox7; // 星期日
    private CheckBox checkBox6;
    private CheckBox checkBox5;
    private CheckBox checkBox4;
    private CheckBox checkBox3;
    private CheckBox checkBox2;
    private CheckBox checkBox1;
    private LinearLayout llrepeatHz;
    private LinearLayout llrepeatHzSelect;
    private WheelView optionsNum;
    private MyBroadCastReceicer receicer;
    public static final String ACTION_REFRESH_CALENDAR_TAG = "action_refresh_calendar_tag";

    private String mTagNameNew;

    ////////////////

    private class MyBroadCastReceicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra("tag");
            if (tag != null) {
                mTagNameNew = tag;
                mTagLayout.setTitle(tag);
            }
            mTagLayout.rotateArrow();
            getCalendarTag();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        schedule = (CalendarInfo) getIntent().getSerializableExtra(
                "calendarInfo");
        calendarId = getIntent().getIntExtra(CALENDARID, 0);

        // 初始化collaspeView
        initCollapseView();
        initTxEndTime();
        initWheel();

        mLoadingDialog = createLoadingDialog(getString(R.string.excuseing));
        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1),
                getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3),
                getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5)};

        if (schedule != null) {
            showCalendarInfo(schedule);
        }

        if (calendarId != 0) {
            getCalendarById();
        }

        mStartPopupWindow.setOnShowListener(this);
        mStartPopupWindow.setOnDismissListener(this);
        mEndRepeatPopupWindow.setOnShowListener(this);
        mEndRepeatPopupWindow.setOnDismissListener(this);
        mEndPopupWindow.setOnShowListener(this);
        mEndPopupWindow.setOnDismissListener(this);

    }

    /**
     * 初始化collapseView
     */
    private void initCollapseView() {
        // 标签页面
        IntentFilter filter = new IntentFilter();
        receicer = new MyBroadCastReceicer();
        filter.addAction(ACTION_REFRESH_CALENDAR_TAG);
        registerReceiver(receicer, filter);

        // data
        mLables = new ArrayList<>();
        adapter = new CalendarTagAdapter(this);
        adapter.setData(mLables);

        // view
        mTagLayout = (CollapseView) findViewById(R.id.schedule_ll_tagss);
        mTagLayout.setSwipeScrollView(mainScrollView);
        mTagLayout.setContent(R.layout.activity_calendar_tag2);
        MyListView myListView = (MyListView) mTagLayout.findViewById(R.id.lv_calendar_tag);
        LinearLayout tagNew = (LinearLayout) mTagLayout.findViewById(R.id.tv_calendar_new_tag);
        mTagContent = (RelativeLayout) findViewById(R.id.contentRelativeLayout);
        mTagLayout.setTagNameVis("e91a", "标签");

        myListView.setAdapter(adapter);
        adapter.setCollaspeView(mTagLayout);
        adapter.setTagContentLayout(mTagContent);
        if (schedule != null && !TextUtils.isEmpty(schedule.Label)) {
            mTagNameNew = schedule.Label;
            mTagLayout.setTitle(schedule.Label);
        } else {
            mTagLayout.setTitle("#无");
        }

        getCalendarTag();
        tagNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartAnim(false);
                Intent newTagIntent = new Intent(CalendarEditActivity.this, NewCalendarTagActivity.class);
                newTagIntent.putExtra("start_anim", false);
                startActivityForResult(newTagIntent, 111);
                setStartAnim(true);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////
        // 重复页面
        repeats = (CollapseView) findViewById(R.id.schedule_ll_repeat);
        repeats.setSwipeScrollView(mainScrollView);
        repeats.setContent(R.layout.activity_calendar_repeat2);
        mRepeatContent = (RelativeLayout) repeats.findViewById(R.id.contentRelativeLayout);
        freeRepeatSw = (SwitchButton) repeats.findViewById(R.id.sb_calendar_repeat_free);
        freeScrollView = (ScrollView) repeats.findViewById(R.id.free_calendar_scroll);
        fixHzRepeatLayout = (LinearLayout) repeats.findViewById(R.id.ll_calendar_fit_hz_repeat);
        freeRepeatSLayout = (LinearLayout) repeats.findViewById(R.id.scroll_calendar_free);
        freeRepeatSwLayout = (LinearLayout) repeats.findViewById(R.id.ll_calendar_free_switch);
        llrepeatHz = (LinearLayout) repeats.findViewById(R.id.ll_calendar_repeat_result);
        llrepeatSelect = (LinearLayout) repeats.findViewById(R.id.optionspicker_pinlv);
        llrepeatHzSelect = (LinearLayout) repeats.findViewById(R.id.optionspicker_result);
        plTextViews = (TextView) repeats.findViewById(R.id.tv_pl_tags);
        optionsType = (WheelView) repeats.findViewById(R.id.options1_type);
        optionsNum = (WheelView) repeats.findViewById(R.id.options1_num);
        rrTextView = (TextView) repeats.findViewById(R.id.tv_rr_tag);
        weekLayout = (LinearLayout) repeats.findViewById(R.id.ll_week_select_day);
        checkBox7 = (CheckBox) repeats.findViewById(R.id.cb_week_select7);
        checkBox6 = (CheckBox) repeats.findViewById(R.id.cb_week_select6);
        checkBox5 = (CheckBox) repeats.findViewById(R.id.cb_week_select5);
        checkBox4 = (CheckBox) repeats.findViewById(R.id.cb_week_select4);
        checkBox3 = (CheckBox) repeats.findViewById(R.id.cb_week_select3);
        checkBox2 = (CheckBox) repeats.findViewById(R.id.cb_week_select2);
        checkBox1 = (CheckBox) repeats.findViewById(R.id.cb_week_select1);
        repeats.setTitle("无");
        repeats.setTagNameVis("e9c3", "重复");

        TextView repeatNo = (TextView) repeats.findViewById(R.id.ll_never_repeat);
        TextView repeat1 = (TextView) repeats.findViewById(R.id.ll_day_repeat);
        TextView repeat2 = (TextView) repeats.findViewById(R.id.ll_week_repeat);
        TextView repeat3 = (TextView) repeats.findViewById(R.id.ll_week_twice_repeat);
        TextView repeat4 = (TextView) repeats.findViewById(R.id.ll_month_repeat);
        TextView repeat5 = (TextView) repeats.findViewById(R.id.ll_year_repeat);
        repeatNo.setOnClickListener(this);
        repeat1.setOnClickListener(this);
        repeat2.setOnClickListener(this);
        repeat3.setOnClickListener(this);
        repeat4.setOnClickListener(this);
        repeat5.setOnClickListener(this);
        freeRepeatSwLayout.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 提醒页面
        tiXingCV = (CollapseView) findViewById(R.id.ll_calendar_tixing);
        tiXingCV.setSwipeScrollView(mainScrollView);
        tiXingCV.setContent(R.layout.activity_calendar_tixing2);
//        mTagContent = (RelativeLayout) findViewById(R.id.contentRelativeLayout);
        tiXingCV.setTitle("无");
        tiXingCV.setTagNameVis("e908", "提醒");
        LinearLayout none = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_none);
        LinearLayout tx0 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_0);
        LinearLayout tx1 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_1);
        LinearLayout tx2 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_2);
        LinearLayout tx3 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_3);
        LinearLayout tx4 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_4);
        LinearLayout tx5 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_5);
        LinearLayout tx6 = (LinearLayout) tiXingCV.findViewById(R.id.ll_calendar_tx_6);
        none.setOnClickListener(this);
        tx0.setOnClickListener(this);
        tx1.setOnClickListener(this);
        tx2.setOnClickListener(this);
        tx3.setOnClickListener(this);
        tx4.setOnClickListener(this);
        tx5.setOnClickListener(this);
        tx6.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 截止重复页面
        tiXingEndLayout = (CollapseView) findViewById(R.id.ll_calendar_tixing_end);
        tiXingEndLayout.setSwipeScrollView(mainScrollView);
        tiXingEndLayout.setContent(R.layout.activity_calendar_tixing_end2);
//        mTagContent = (RelativeLayout) findViewById(R.id.contentRelativeLayout);
        tiXingEndLayout.setTitle("无");
        tiXingEndLayout.setTagNameVis("e90a", "结束重复");
        LinearLayout neverEnd = (LinearLayout) tiXingEndLayout.findViewById(R.id.ll_calendar_tx_none_end);
        LinearLayout endTime = (LinearLayout) tiXingEndLayout.findViewById(R.id.ll_calendar_tx_0_end);
        neverEnd.setOnClickListener(this);
        endTime.setOnClickListener(this);
    }

    private void initWheel() {
        // 分类的时间滚轴
        ArrayList<String> sorts = new ArrayList<String>();
        sorts.add("天");
        sorts.add("周");
        sorts.add("月");
        sorts.add("年");
        ArrayWheelAdapter<String> adapterSort = new ArrayWheelAdapter<String>(
                sorts);
        optionsType.setAdapter(adapterSort);
        optionsType.setCyclic(true);
        optionsType.setEnabled(true);
        optionsType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                tags.clear();
                switch (index) {
                    case 0:
                        tags.add("天");
                        plTextViews.setText("天");
                        n = 10;
                        weekLayout.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        plTextViews.setText("周");
                        tags.add("周");
                        weekLayout.setVisibility(View.VISIBLE);
                        n = 8;
                        break;
                    case 2:
                        plTextViews.setText("月");
                        tags.add("月");
                        n = 12;
                        weekLayout.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        plTextViews.setText("年");
                        tags.add("年");
                        n = 3;
                        weekLayout.setVisibility(View.INVISIBLE);
                        break;
                }
                // 数量的时间滚轴
                NumericWheelAdapter adapter1 = new NumericWheelAdapter(1, n);
                optionsNum.setAdapter(adapter1);
            }
        });
        // 数量的时间滚轴
        NumericWheelAdapter adapter1 = new NumericWheelAdapter(1, n);
        optionsNum.setAdapter(adapter1);
        optionsNum.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int i = optionsNum.getCurrentItem() + 1;
                rrTextView.setText(i + "");
            }
        });
        // 标签的时间滚轴
        tags = new ArrayList<>();
        tags.add("天");
        // 设置默认值
        optionsType.setCurrentItem(0);
        optionsNum.setCurrentItem(0);
        thingsCrash();
    }

    private void initTxEndTime() {
        final SimpleDateFormat format = new SimpleDateFormat(
                getString(R.string.timeformat6));
        mEndRepeatPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mEndRepeatPopupWindow.setTitle(getString(R.string.repeat_end));
        mEndRepeatPopupWindow.setCancelable(true);

        mEndRepeatPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                endRepeatTime = format.format(date);
                tiXingEndLayout.setTitle(endRepeatTime);
                tiXingEndLayout.rotateArrow();
                schedule.REPEATENDTIME = endRepeatTime;
            }
        });

    }

    /**
     * 显示日程的详细信息
     */
    private void showCalendarInfo(CalendarInfo schedule) {
        if (schedule.Rail != null && !TextUtils.isEmpty(schedule.Rail.Address))
            mLocationTv.setText(schedule.Rail.Address);
        //附件
        if (schedule.FileName == null || schedule.FileName.size() == 0) {
            tvFileNumber.setVisibility(View.INVISIBLE);
        } else {
            tvFileNumber.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < schedule.FileName.size(); i++) {
                if (i < 2) {
                    String name = schedule.FileName.get(i);
                    if (i != 0) {
                        builder.append("、");
                    }
                    builder.append(name);
                } else {
                    builder.append("等" + schedule.FileName.size() + "个");
                    break;
                }
            }
            tvFileNumber.setText(builder);
        }
//        if (schedule.FileName == null || schedule.FileName.size() == 0) {
//            tvFileName1.setVisibility(View.VISIBLE);
//            tvFileName2.setVisibility(View.GONE);
//            tvFileName1.setTextSize(16);
//            tvFileName1.setText("添加附件");
//            tvMoreFileTips.setText("");
//        } else if (schedule.FileName.size() >= 2) {
//            tvFileName1.setVisibility(View.VISIBLE);
//            tvFileName2.setVisibility(View.VISIBLE);
//            tvFileName1.setTextSize(14);
//            tvFileName1.setText(schedule.FileName.get(0));
//            tvFileName2.setText(schedule.FileName.get(1));
//            tvMoreFileTips.setText("查看更多");
//        } else {
//            tvFileName1.setTextSize(14);
//            tvFileName1.setVisibility(View.VISIBLE);
//            tvFileName2.setVisibility(View.GONE);
//            tvFileName1.setText(schedule.FileName.get(0));
//            tvMoreFileTips.setText("");
//        }
        if (!TextUtils.isEmpty(schedule.Line_Project)
                && schedule.Line_Project.length() != 2) {
            tvProjectNum.setVisibility(View.VISIBLE);
            String str = schedule.Line_Project.substring(1,
                    schedule.Line_Project.length() - 1);
            String[] arr = str.split(",");
            tvProjectNum.setText(arr.length + "");
        }
        if (!TextUtils.isEmpty(schedule.Line_Task)
                && schedule.Line_Task.length() != 2) {
            tvTaskNum.setVisibility(View.VISIBLE);
            String str = schedule.Line_Task.substring(1,
                    schedule.Line_Task.length() - 1);
            String[] arr = str.split(",");
            tvTaskNum.setText(arr.length + "");
        }
        waitID = getIntent().getIntExtra("wait_id", 0);

        String startTime = schedule.StartTime;
        String endTime = schedule.OverTime;
        try {
            if (startTime.length() > 10) {
                startTime = startTime.substring(0, 16);
                endTime = endTime.substring(0, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mStartDateBtn.setTag(startTime);
        mEndDateBtn.setTag(endTime);
        mStartDateBtn.setText(startTime);
        mEndDateBtn.setText(endTime);

        format = new SimpleDateFormat(getString(R.string.timeformat6));
        format2 = new SimpleDateFormat(getString(R.string.timeformat5));
        format3 = new SimpleDateFormat(getString(R.string.timeformat13));
        format4 = new SimpleDateFormat(getString(R.string.timeformat9));

        mCurDate = new Date();
        mStartPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mStartPopupWindow
                .setTitle(getString(R.string.task_summarize_startTime));
        mStartPopupWindow.setCancelable(true);
//        try {
//            mStartPopupWindow.setTime(mAllDaySb.isChecked() ? format2.parse(startTime) : format.parse(startTime));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        mStartPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mStartDate = date;
                Calendar nowCal = Calendar.getInstance();
                int year = nowCal.get(Calendar.YEAR);
                int month = nowCal.get(Calendar.MONTH) + 1;
                int day = nowCal.get(Calendar.DAY_OF_MONTH);
                String months = month + "";
                String days = day + "";
                if (month < 10)
                    months = 0 + months;
                if (day < 10)
                    days = 0 + days;
                String nowTimeStr = year + "-" + months + "-" + days;
                String startTime = mAllDaySb.isChecked() ? format2
                        .format(date) : format.format(date);
                try {
                    if ((mAllDaySb.isChecked() ? format2.parse(startTime).getTime() : format.parse(startTime).getTime()) < format2.parse(nowTimeStr).getTime()) {
                        ToastUtil.showToast(CalendarEditActivity.this, "开始时间不能小于当前天");
                    } else {
                        mStartDateBtn.setTag(startTime);
                        mStartDateBtn.setText(startTime);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        mEndPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mEndPopupWindow.setTitle(getString(R.string.ending_time));
        mEndPopupWindow.setCancelable(true);
        mEndPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mEndDate = date;
                String endTime = mAllDaySb.isChecked() ? format2
                        .format(date) : format.format(date);
                mEndDateBtn.setTag(endTime);
                mEndDateBtn.setText(endTime);
            }
        });

        mAllDaySb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                setAllDayChecked(isChecked);
            }
        });

        mNameEt.setText(schedule.Title);
        mNameEt.setSelection(schedule.Title.length());

        String start = schedule.StartTime;
        if (start != null && start.startsWith("/Date("))
            start = HelpUtil.time2String(getString(R.string.timeformat6),
                    start);
        String end = schedule.OverTime;
        if (end != null && end.startsWith("/Date("))
            end = HelpUtil.time2String(getString(R.string.timeformat6),
                    end);
        if (schedule.Allday == 1) {
            start += " 00:00";
            end += " 23:59";
            mStartDate = new Date(getTime(start));
            mEndDate = new Date(getTime(end));
            mAllDaySb.setChecked(true);
            setAllDayChecked(true);
        } else {
            mStartDate = new Date(getTime(start.substring(0, 16)));
            mEndDate = new Date(getTime(end.substring(0, 16)));
            mAllDaySb.setChecked(false);
            setAllDayChecked(false);
        }
        if (schedule.Remark != null && !TextUtils.isEmpty(schedule.Remark)) {
            mDescEt.setText(schedule.Remark);
            mDescEt.setSelection(schedule.Remark.length());
        } else {
            mDescEt.setText(getResources().getString(R.string.nope));
        }

        mAllDayLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mAllDaySb.toggle();
            }
        });

        // Calendar重复提醒
        showRepeatInfo(schedule);

        if (schedule.NoteRoles != null) {

            List<ApiEntity.NoteRole> nrList = new ArrayList<>();
            nrList = schedule.NoteRoles;
            for (ApiEntity.NoteRole noteRole : nrList) {
                UserInfo userInfo = new UserInfo();
                userInfo.ID = noteRole.ID;
                userInfo.Name = noteRole.Name;
                mSelectList.add(userInfo);
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mSelectList.size(); i++) {
                if (i < 3) {
                    ApiEntity.UserInfo user = mSelectList.get(i);
                    if (i != 0) {
                        builder.append("、");
                    }
                    builder.append(user.Name);
                } else {
                    builder.append("等" + mSelectList.size() + "人");
                    break;
                }
            }
            mSelectTv.setText(builder);
            mSelectTv.setHint(mSelectList.size() > 0 ? "" : "公开");
        }
    }

    /**
     * 重复提醒赋值
     */
    private void showRepeatInfo(CalendarInfo schedule) {
        String repeatTypeStr = ""; // 重复类型字符串
        pinlv = schedule.REPEATHZ; // 频率
        int repeatType = schedule.REPEATTYPE; // 重复的类型
        String repeatWeekStr = schedule.REPEATWEEKVAL; // 重复周的提示
        int tiXing = schedule.AHEAD_MINUTE; // 提醒的时间
        String tiXingEndTimeStr = schedule.REPEATENDTIME; // 重复提醒的截止时间
        switch (repeatType) {
            case 1:
                repeatTypeStr = "天";
                break;
            case 2:
                repeatTypeStr = "周";
                break;
            case 3:
                repeatTypeStr = "月";
                break;
            case 4:
                repeatTypeStr = "年";
                break;
        }
        // 星期的信息
        StringBuilder weekInfoScan = new StringBuilder();
        if (repeatWeekStr != null && repeatWeekStr.length() > 1) {
            String[] weekArr = repeatWeekStr.split(",");
            if (weekArr != null) {
                for (int i = 0; i < weekArr.length; i++) {
                    if (TextUtils.isDigitsOnly(weekArr[i])) {
                        switch (Integer.valueOf(weekArr[i])) {
                            case 0:
                                weekInfoScan.append("一/");
                                break;
                            case 1:
                                weekInfoScan.append("二/");
                                break;
                            case 2:
                                weekInfoScan.append("三/");
                                break;
                            case 3:
                                weekInfoScan.append("四/");
                                break;
                            case 4:
                                weekInfoScan.append("五/");
                                break;
                            case 5:
                                weekInfoScan.append("六/");
                                break;
                            case 6:
                                weekInfoScan.append("日/");
                                break;
                        }
                    }
                }
            }
            if (weekInfoScan != null && weekInfoScan.length() > 2) {
                weekInfoScan = weekInfoScan
                        .deleteCharAt(weekInfoScan.length() - 1);
            }
        } else if (repeatWeekStr.length() == 1) {
            switch (Integer.valueOf(repeatWeekStr)) {
                case 0:
                    weekInfoScan.append("一");
                    break;
                case 1:
                    weekInfoScan.append("二");
                    break;
                case 2:
                    weekInfoScan.append("三");
                    break;
                case 3:
                    weekInfoScan.append("四");
                    break;
                case 4:
                    weekInfoScan.append("五");
                    break;
                case 5:
                    weekInfoScan.append("六");
                    break;
                case 6:
                    weekInfoScan.append("日");
                    break;
            }
        }

        String tiXingStr = ""; // 提醒提前的时间

        if (schedule.ISCALL == 1) {
            switch (tiXing) {
                case 0:
                    tiXingStr = getString(R.string.ontime);
                    break;
                case 5:
                    tiXingStr = getString(R.string.five_minute_per);
                    break;
                case 60:
                    tiXingStr = getString(R.string.one_hours);
                    break;
                case 180:
                    tiXingStr = getString(R.string.three_hours);
                    break;
                case 1440:
                    tiXingStr = getString(R.string.one_day);
                    break;
                case 4320:
                    tiXingStr = getString(R.string.three_day);
                    break;
                case 10080:
                    tiXingStr = getString(R.string.one_week);
                    break;
            }
        } else {
            tiXingStr = getString(R.string.nope);
        }

//        if ()
        repeatInfoStr = pinlv + repeatTypeStr + " " + weekInfoScan;

        if (tiXingStr != null && !TextUtils.isEmpty(tiXingStr)) {
            tiXingCV.setTitle(tiXingStr);
        } else {
            tiXingCV.setTitle(getString(R.string.nope));
        }

        if (pinlv != 0) {
            tiXingEndLayout.setVisibility(View.VISIBLE);
            repeats.setTitle("每" + repeatInfoStr);
        } else {
            tiXingEndLayout.setVisibility(View.GONE);
        }

        if (tiXingEndTimeStr != null && !TextUtils.isEmpty(tiXingEndTimeStr)) {
            tiXingEndLayout.setTitle(tiXingEndTimeStr);
        }
    }

    /**
     * 相关事件的点击、提醒重复的点击
     *
     * @param view
     */
    @Event(value = {R.id.work_project_relation, R.id.task_relation, R.id.file_relation,
            R.id.ll_calendar_modify_location_container, R.id.schedule_ll_tag, R.id.lr_calendar_edit_start_time, R.id.lr_calendar_edit_end_time})
    private void relationClick(View view) {
        if (schedule != null) {
            switch (view.getId()) {
                case R.id.lr_calendar_edit_start_time:
                    mStartPopupWindow.show();
                    HelpUtil.hideSoftInput(CalendarEditActivity.this, mNameEt);
                    break;
                case R.id.lr_calendar_edit_end_time:
                    mEndPopupWindow.show();
                    HelpUtil.hideSoftInput(CalendarEditActivity.this, mNameEt);
                    break;
                case R.id.ll_calendar_modify_location_container:
                    Intent locationIntent = new Intent(this, GeoFenceActivity.class);
                    if (schedule.Rail != null && !TextUtils.isEmpty(schedule.Rail.Address))
                        locationIntent.putExtra(GeoFenceActivity.USER_RAIL, schedule.Rail);
                    startActivityForResult(locationIntent, 111);
                    break;
                case R.id.work_project_relation:
//                    if (mIsClick) {
//                        Intent intent = new Intent(this,
//                                WorkProjectMutiActivity.class);
//                        intent.putExtra(WorkProjectMutiActivity.WORK_PROJECT_IDS,
//                                schedule.Line_Project);
//                        // ToastUtil.showToast(this, schedule.Line_Project);
//                        startActivityForResult(intent, PROJECT);
//                    } else {
//                        // 展示工作项目
//                        String line_Project = schedule.Line_Project;//
//                        Intent intent = new Intent(this,
//                                RelationProjectActivity.class);
//                        // 将项目id字符串传递到相关项目界面
//                        intent.putExtra(
//                                RelationProjectActivity.CALENDER_WORK_PORJECT,
//                                line_Project);
//                        startActivity(intent);
//                    }
                    break;
                case R.id.task_relation:
//                    if (mIsClick) {
//                        Intent taskIntent = new Intent(this,
//                                RelativeTaskActivity.class);
//                        String infos = schedule.Line_Task;
//                        if (infos != null && infos.length() > 0) {
//                            infos = infos.substring(1, infos.length() - 1);
//                        }
//                        taskIntent.putExtra("task_ids", infos);
//                        // ToastUtil.showToast(this, schedule.Line_Task);
//                        taskIntent.putExtra("from",
//                                RelativeTaskActivity.FROM_SCHEDULE);
//                        startActivityForResult(taskIntent, TASK);
//                    } else {
//                        // 展示相关任务
//                        String line_Task = schedule.Line_Task;
//                        Intent intent = new Intent(this, RelationTaskActivity.class);
//                        intent.putExtra(RelationTaskActivity.CALENDER_TASK,
//                                line_Task);
//                        startActivity(intent);
//                    }
                    break;
                case R.id.file_relation:
//                    if (mIsClick) {
                    Intent repositoryIntent = new Intent(this,
                            FileSelectActivity.class);
                    repositoryIntent.putExtra(
                            FileSelectActivity.EXTRA_SELECT_IDS,
                            schedule.Line_File);
                    repositoryIntent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    repositoryIntent.putExtra("click_pos_y", location[1]);
                    startActivityForResult(repositoryIntent, ZHISHIKU);
//                    } else {
//                        // 展示知识库详情
//                        String line_File = schedule.Line_File;
//                        Intent intent = new Intent(this, RelationFileActivity.class);
//                        intent.putExtra(RelationFileActivity.CALENDER_FILE,
//                                line_File);
//                        startActivity(intent);
//                    }
                    break;
//                case R.id.schedule_ll_tag:
//                    setStartAnim(false);
//                    Intent intentTag = new Intent(this, CalendarTagActivity.class);
//                    startActivityForResult(intentTag, 101);
//                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mStartPopupWindow != null && mStartPopupWindow.isShowing()) {
            mStartPopupWindow.dismiss();
        } else if (mEndPopupWindow != null && mEndPopupWindow.isShowing()) {
            mEndPopupWindow.dismiss();
        } else {
            finish();
        }
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9, R.id.schedule_btn_finish})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                if (mNameEt.isFocusable())
                    KeyBoardUtil.hideSoftInput(mNameEt, this);
                else
                    KeyBoardUtil.hideSoftInput(mDescEt, this);
                onBackPressed();
                break;
            case R.id.schedule_btn_finish:
                new AlertDialog(this).builder().setMsg("确认删除日程？")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.confirm),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finishCalendar(schedule.ID, schedule);
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                break;
            case R.id.cm_header_tv_right9:
                String content = mNameEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, R.string.empty_content_tips,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTimeInMillis(getTime(mStartDateBtn.getTag()
                            .toString()));
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTimeInMillis(getTime(mEndDateBtn.getTag()
                            .toString()));

                    if (startCalendar.getTimeInMillis() > endCalendar
                            .getTimeInMillis()) {
                        Toast.makeText(
                                this,
                                getString(R.string.endingtime_notlessthan_starttime),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        save();
                    }
                }
                break;
        }
    }

    @Event(R.id.cm_select_ll_select)
    private void onSelectClick(View v) {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
        intent.putExtra("select_list", mSelectList);
        intent.putExtra("start_anim", false);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        intent.putExtra("click_pos_y", location[1]);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNameEt.clearFocus();
        mDescEt.clearFocus();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //电子围栏
                case 111:
                    ApiEntity.UserRail userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
                    int id = data.getIntExtra("UserRailId", 0);
                    if (userRail != null && id != 0) {
                        schedule.RailID = id;
                        schedule.Rail = userRail;
                        mLocationTv.setText(userRail.Address);
                    }
                    break;
                // 分享范围
                case 2:
                    mSelectFlowLayout.removeAllViews();
                    mSelectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mSelectList.size(); i++) {
                        if (i < 3) {
                            ApiEntity.UserInfo user = mSelectList.get(i);
                            if (i != 0) {
                                builder.append("、");
                            }
                            builder.append(user.Name);
                        } else {
                            builder.append("等" + mSelectList.size() + "人");
                            break;
                        }
                    }
                    mSelectTv.setText(builder);
                    mSelectTv.setHint(mSelectList.size() > 0 ? "" : "公开");
                    break;
                case CalendarEditActivity.PROJECT:
//                    projects = (ArrayList<UserProject>) data
//                            .getSerializableExtra(WorkProjectMutiActivity.WORK_PROJECTS);
//                    schedule.Line_Project = project2string(projects);
//                    if (schedule.Line_Project.length() == 2) {
//                        tvProjectNum.setVisibility(View.GONE);
//                    }
//                    if (projects != null && projects.size() > 0) {
//                        tvProjectNum.setVisibility(View.VISIBLE);
//                        tvProjectNum.setText(projects.size() + "");
//                    }
                    break;
                case CalendarEditActivity.TASK:
                    taskRets = (ArrayList<UserFenPai>) data
                            .getSerializableExtra("select_list");
                    filterFiles(tasks, taskRets);
                    schedule.Line_Task = tasks2string(tasks);
                    if (schedule.Line_Task.length() == 2) {
                        tvTaskNum.setVisibility(View.GONE);
                    }
                    if (tasks != null && tasks.size() > 0) {
                        tvTaskNum.setVisibility(View.VISIBLE);
                        tvTaskNum.setText(tasks.size() + "");
                    }
                    break;
                case CalendarEditActivity.ZHISHIKU:
                    fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    schedule.Line_File = HelpUtil.files2StrID(fileRets);
                    if (fileRets == null || fileRets.size() == 0) {
                        tvFileNumber.setVisibility(View.INVISIBLE);
                    } else {
                        tvFileNumber.setVisibility(View.VISIBLE);
                        StringBuilder builders = new StringBuilder();
                        for (int i = 0; i < fileRets.size(); i++) {
                            if (i < 2) {
                                ApiEntity.Files user = fileRets.get(i);
                                if (i != 0) {
                                    builders.append("、");
                                }
                                builders.append(user.Name);
                            } else {
                                builders.append("等" + fileRets.size() + "个");
                                break;
                            }
                        }
                        tvFileNumber.setText(builders);
                    }
                    break;
            }
        }
//        if (requestCode == TIXING_REQUEST && resultCode == RESULT_OK) {
//            fixTiXingInfoStr = data.getStringExtra("tixing");
//            if (!fixTiXingInfoStr.equals("null")) {
//                txTextView.setText(fixTiXingInfoStr);
//            }
//        }
//        if (requestCode == TIXING_REQUEST_END && resultCode == RESULT_OK) {
//            tixingEndStr = data.getStringExtra("tixingEnd");
//            if (!tixingEndStr.equals("null")) {
//                txEndTextView.setText(tixingEndStr);
//            }
//        }
    }

    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
//        PersonTextView childView = new PersonTextView(this, R.drawable.persontext_bg, false);
//        childView.setTag(user);
//        childView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (flagShareMember) {
//                    mSelectFlowLayout.removeView(v);
//                    mSelectList.remove((UserInfo) v.getTag());
//                    if (mSelectList.size() == 0) {
//                        mSelectTv.setHint(R.string.share_range_hint);
//                    }
//                }
//            }
//        });
//        childView.setText(user.Name);
//        mSelectFlowLayout.addView(childView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        type = intent.getIntExtra("type", -1);
//        pinlv = intent.getIntExtra("pinlv", 0);
//        weekStr = intent.getStringExtra("week"); // 重复周的详细描述
//        weekNum = intent.getStringExtra("weekNum"); // 重复周的数字描述
//        fixRepeatInfoStr = intent.getStringExtra("fixRepeat"); // 固定重复的详细描述
//        fixPinlv = intent.getIntExtra("fixpv", 0);
//        fixType = intent.getIntExtra("fixType", -1);
//
//        switch (type) {
//            case 0:
//                typeStr = "天";
//                break;
//            case 1:
//                typeStr = "周";
//                break;
//            case 2:
//                typeStr = "月";
//                break;
//            case 3:
//                typeStr = "年";
//                break;
//        }
//        switch (fixType) {
//            case 0:
//                typeStr = "天";
//                break;
//            case 1:
//                typeStr = "周";
//                break;
//            case 2:
//                typeStr = "月";
//                break;
//            case 3:
//                typeStr = "年";
//                break;
//        }
//
//        repeatInfoStr = pinlv + typeStr + " " + weekStr;
//
//        // 重复提醒的判断
//        if (type != -1) {
//            repeatInfoTv.setText("每" + repeatInfoStr);
//        }
//        if (fixRepeatInfoStr != null) {
//            repeatInfoTv.setText(fixRepeatInfoStr);
//        }
//        if (pinlv != 0) {
//            tiXingEndLayout.setVisibility(View.VISIBLE);
//        }
//        if (repeatInfoTv.getText().equals(getString(R.string.nope))) {
//            tiXingEndLayout.setVisibility(View.GONE);
//        } else {
//            tiXingEndLayout.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 编辑日程
     */
    private void save() {
        // CalendarInfo cInfo = new CalendarInfo();
        // cInfo.ID = schedule.ID;
        // cInfo.Title = mContentEt.getText().toString();
        // cInfo.StartTime = mStartDateBtn.getTag().toString();
        // cInfo.OverTime = mEndDateBtn.getTag().toString();
        // RadioButton checkedRb = (RadioButton) mTagRadioGroup
        // .findViewById(mTagRadioGroup.getCheckedRadioButtonId());
        // cInfo.Color = (Integer.valueOf(checkedRb.getTag().toString()) - 1);
        // cInfo.Allday = (mAllDaySb.isChecked() ? 1 : 0);
        // cInfo.Priority = (1);
        // cInfo.Remark = ("");
        // cInfo.State = (1);
        // cInfo.Type = (1);
        // cInfo.NotePriority = "[" + new Gson().toJson(cInfo) + "]";
        // if (files2string(files).length() == 2) {
        // cInfo.Line_File = schedule.Line_File;
        // } else {
        // cInfo.Line_File = files2string(files);
        // }
        // if (tasks2string(tasks).length() == 2) {
        // cInfo.Line_Task = schedule.Line_Task;
        // } else {
        // cInfo.Line_Task = tasks2string(tasks);
        // }
        // if (project2string(projects).length() == 2) {
        // cInfo.Line_Project = schedule.Line_Project;
        // } else {
        // cInfo.Line_Project = project2string(projects);
        // }
        if (schedule != null) {
            schedule = getCInfo(schedule);
        }
        API.TalkerAPI.AddCalendar(schedule, new RequestCallback<ApiEntity.APIResult>(
                ApiEntity.APIResult.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onCancelled(CancelledException arg0) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                ToastUtil.showToast(CalendarEditActivity.this,
                        getString(R.string.service_excep));
                mLoadingDialog.dismiss();
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
                mLoadingDialog.dismiss();
                if (respInfo != null && respInfo.State == 1) {
                    ToastUtil.showToast(CalendarEditActivity.this,
                            getString(R.string.edit_success),
                            R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(
                            CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    schedule.Label = respInfo.Msg;
                    intent.putExtra("edit", schedule);
                    sendBroadcast(intent); // 刷日程月列表
                    intent = new Intent(
                            WaitHandleCalendarFragment.ACTION_REFRESH_CALENDAR_HANDLE);
                    sendBroadcast(intent);
                    intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    sendBroadcast(intent);
                    AppManager.finishActivity(CalendarInfoActivity2.class);
                    onBackPressed();
                } else {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.edit_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void finishCalendar(final int cid, final CalendarInfo cInfo) {
        API.TalkerAPI.DelCalenderById(cid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                mLoadingDialog.dismiss();
                Toast.makeText(CalendarEditActivity.this,
                        getString(R.string.service_excep), Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onSuccess(String arg0) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(arg0) && TextUtils.isDigitsOnly(arg0)
                        && Integer.valueOf(arg0) == 1) {
                    ToastUtil.showToast(CalendarEditActivity.this,
                            getString(R.string.finish_success),
                            R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    intent.putExtra("edit", "edit");
                    sendBroadcast(intent); // 刷日程月列表
                    intent = new Intent(
                            WaitHandleCalendarFragment.ACTION_REFRESH_CALENDAR_HANDLE);
                    sendBroadcast(intent);
                    intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
                    sendBroadcast(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.finish_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 全天事件选择状态处理
     *
     * @param isChecked
     */
    private void setAllDayChecked(boolean isChecked) {
        try {
            if (isChecked) {
                mStartPopupWindow.setType(Type.YEAR_MONTH_DAY);
                mEndPopupWindow.setType(Type.YEAR_MONTH_DAY);

                String startTime = format2.format(mStartDate);
                mStartDateBtn.setTag(startTime);
                mStartDateBtn.setText(startTime);
                mStartPopupWindow.setTime(format2.parse(startTime));

                String endTime = format2.format(mEndDate);
                mEndDateBtn.setTag(endTime);
                mEndDateBtn.setText(endTime);
                mEndPopupWindow.setTime(format2.parse(endTime));
            } else {
                mStartPopupWindow.setType(Type.ALL);
                mEndPopupWindow.setType(Type.ALL);

                String startTime = format.format(mStartDate);
                mStartDateBtn.setTag(startTime);
                mStartDateBtn.setText(startTime);
                mStartPopupWindow.setTime(format.parse(startTime));

                String endTime = format.format(mEndDate);
                mEndDateBtn.setTag(endTime);
                mEndDateBtn.setText(endTime);
                mEndPopupWindow.setTime(format.parse(endTime));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤掉已经存在容器集合中的文件数据 如若原集合中有时不添加
     *
     * @param content 原集合内容
     * @param rets    新增的返回内容
     * @return 返回过滤后的集合
     */
    public static ArrayList<UserFenPai> filterFiles(
            ArrayList<UserFenPai> content, ArrayList<UserFenPai> rets) {
        for (int i = 0; i < rets.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < content.size(); j++) {
                if (rets.get(i).ID == content.get(j).ID) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                content.add(rets.get(i));
            }
        }
        return content;
    }

    // 将字符串转为时间戳
    private long getTime(String user_time) {
        long re_time = 0;
        String pattern = "yyyy-MM-dd";
        if (user_time.length() > 10) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d;
        try {
            d = sdf.parse(user_time);
            re_time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    // 把项目集合id转成id字符串
    private String project2string(ArrayList<UserProject> projects) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < projects.size(); i++) {
            sb.append(projects.get(i).ID);
            if (i != projects.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString().trim();
    }

    // 把任务集合id转成id字符串
    private String tasks2string(ArrayList<UserFenPai> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(tasks.get(i).ID);
            if (i != tasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString().trim();
    }

    // 把知识库集合id转成id字符串
    private String files2string(ArrayList<Files> files) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files.size(); i++) {
            sb.append(files.get(i).ID);
            if (i != files.size() - 1) {
                sb.append(",");
            }
        }
        Log.d(TAG, "av" + sb.toString());
        return sb.toString().trim();
    }

    // 将以字符串按","分割成数组
    private String[] string2array(String str) {
        String[] strings;
        str = str.substring(1, str.length() - 1);
        strings = str.split(",");
        return strings;
    }

    /**
     * 初始化日程标签
     */
    private void initList() {
        ApiEntity.UserLabel tag0 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag1 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag2 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag3 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag4 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag5 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag6 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag7 = new ApiEntity.UserLabel();
        tag1.Name = "无";
        tag1.Name = "#会议";
        tag2.Name = "#拜访";
        tag3.Name = "#电话";
        tag4.Name = "#邮件";
        tag5.Name = "#报告";
        tag6.Name = "#其他";
        tag7.Name = "#事件";
        mLables.add(tag0);
        mLables.add(tag1);
        mLables.add(tag2);
        mLables.add(tag3);
        mLables.add(tag4);
        mLables.add(tag5);
        mLables.add(tag6);
        mLables.add(tag7);
    }

    private void getCalendarById() {
        API.TalkerAPI.GetCalenderById(calendarId,
                new RequestCallback<CalendarInfo>(CalendarInfo.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(CalendarEditActivity.this,
                                arg0.toString());
                    }

                    @Override
                    public void onFinished() {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        if (respList.size() > 0) {
                            schedule = respList.get(0);
                            showCalendarInfo(schedule);
                            getCInfo(schedule);
                        } else {
                            ToastUtil.showToast(CalendarEditActivity.this,
                                    "返回数据为空");
                        }
                    }
                });
    }

    /**
     * 获取日程标签
     */
    private void getCalendarTag() {
        //用于关闭
        NewCalendarTagActivity.setOnColseView(new NewCalendarTagActivity.OnColseView() {
            @Override
            public void onClolse() {
                mTagLayout.rotateArrow();
            }
        });
        API.TalkerAPI.GetUserLabel(PrefsUtil.readUserInfo().ID, new RequestCallback<ApiEntity.UserLabel>(ApiEntity.UserLabel.class) {

            @Override
            public void onStarted() {
//                if (!isFinishing())
//                    mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserLabel> respList) {
//                if (!isFinishing())
//                    mLoadingDialog.dismiss();
                mLables.clear();
                if (respList.size() > 0 && respList != null) {
                    initList();
                    mLables.addAll(respList);
                    if (mTagNameNew != null) {
                        for (int i = 0; i < mLables.size(); i++) {
                            if (mTagNameNew.equals(mLables.get(i).Name)) {
                                adapter.getLastPosition(i);
                            }
                        }
                    }
                } else {
                    initList();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                if (!isFinishing())
//                    mLoadingDialog.dismiss();
                Toast.makeText(CalendarEditActivity.this, "获取用户标签失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 代提交的日程对象
     *
     * @return
     */
    private CalendarInfo getCInfo(CalendarInfo schedule) {
        // 分享人员
        ArrayList<ApiEntity.NoteRole> nrList = new ArrayList<>();
        if (mSelectList != null && mSelectList.size() > 0) {
            for (int i = 0, size = mSelectList.size(); i < size; i++) {
                ApiEntity.NoteRole role = new ApiEntity.NoteRole();
                role.ID = mSelectList.get(i).ID;
                role.Name = mSelectList.get(i).Name;
                role.Image = mSelectList.get(i).Image;
                role.Type = ApiEnum.NoteRoleTypes.User;
                nrList.add(role);
            }
        }
        if (schedule != null) {
            Gson gson = new Gson();
            schedule.NoteRoles = nrList;
            schedule.Title = mNameEt.getText().toString();
            schedule.Remark = mDescEt.getText().toString();
            schedule.StartTime = mStartDateBtn.getTag().toString();
            schedule.OverTime = mEndDateBtn.getTag().toString();
            schedule.Label = "";
            colorWhat = adapter.getSelect();

            if (colorWhat != -1) {
                schedule.Color = colorWhat;
            }

            //重复赋值
            if (isFree) {
                schedule.REPEATHZ = optionsNum.getCurrentItem() + 1;
                schedule.REPEATTYPE = optionsType.getCurrentItem() + 1;
                if (optionsType.getCurrentItem() == 1) {
                    weekNum = PackagingUtils.getRepeatWeekInfo(checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7);
                    if (weekNum != null && !TextUtils.isEmpty(weekNum)) {
                        schedule.REPEATWEEKVAL = weekNum;
                    }
                } else {
                    schedule.REPEATWEEKVAL = "";
                }
            } else {
                schedule.REPEATWEEKVAL = "";
            }

            schedule.Allday = mAllDaySb.isChecked() ? 1 : 0;
            // 相关事件
            if (tasks != null && tasks.size() > 0) {
                schedule.Line_Task = tasks2string(tasks);
            } else {
                schedule.Line_Task = schedule.Line_Task;
            }
            if (projects != null && projects.size() > 0) {
                schedule.Line_Project = project2string(projects);
            } else {
                schedule.Line_Project = schedule.Line_Project;
            }
            if (files != null && files.size() > 0) {
                schedule.Line_File = files2string(files);
            } else {
                schedule.Line_File = schedule.Line_File;
            }

            // 提醒开始的小时分钟
            if (schedule.ISCALL == 1) {
                String repeatTimeVals = "09:00";
                if (schedule.Allday == 0) {
                    repeatTimeVals = schedule.StartTime.substring(11,
                            schedule.StartTime.length());
                }
                schedule.RepeatTimeVal = repeatTimeVals;
            }

            schedule.IsJobSync = 1;

            schedule.NotePriority = "[" + gson.toJson(schedule) + "]";
        }
        return schedule;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_calendar_tx_none:
                tiXingCV.setTitle("永不");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 0;
                break;
            case R.id.ll_calendar_tx_0:
                tiXingCV.setTitle("准时");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 0;
                break;
            case R.id.ll_calendar_tx_1:
                tiXingCV.setTitle("5分钟前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 5;
                break;
            case R.id.ll_calendar_tx_2:
                tiXingCV.setTitle("1小时前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 60;
                break;
            case R.id.ll_calendar_tx_3:
                tiXingCV.setTitle("3小时前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 180;
                break;
            case R.id.ll_calendar_tx_4:
                tiXingCV.setTitle("1天前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 1440;
                break;
            case R.id.ll_calendar_tx_5:
                tiXingCV.setTitle("3天前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 4320;
                break;
            case R.id.ll_calendar_tx_6:
                tiXingCV.setTitle("1周前");
                tiXingCV.rotateArrow();
                schedule.ISCALL = 1;
                schedule.AHEAD_MINUTE = 10080;
                break;
            case R.id.ll_never_repeat:
                repeats.setTitle("无");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 0;
                schedule.REPEATHZ = 0;
                mEndTxLayout.setVisibility(View.GONE);
                break;
            case R.id.ll_day_repeat:
                repeats.setTitle("每天");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 1;
                schedule.REPEATHZ = 1;
                mEndTxLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_week_repeat:
                repeats.setTitle("每周");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 2;
                schedule.REPEATHZ = 1;
                mEndTxLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_week_twice_repeat:
                repeats.setTitle("每两周");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 2;
                schedule.REPEATHZ = 2;
                mEndTxLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_month_repeat:
                repeats.setTitle("每月");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 3;
                mEndTxLayout.setVisibility(View.VISIBLE);
                schedule.REPEATHZ = 1;
                break;
            case R.id.ll_year_repeat:
                repeats.setTitle("每年");
                repeats.rotateArrow();
                schedule.REPEATTYPE = 4;
                mEndTxLayout.setVisibility(View.VISIBLE);
                schedule.REPEATHZ = 1;
                break;
            case R.id.ll_calendar_tx_none_end:
                tiXingEndLayout.setTitle("无");
                tiXingEndLayout.rotateArrow();
                break;
            case R.id.ll_calendar_tx_0_end:
                tiXingEndLayout.setTitle("");
                mEndRepeatPopupWindow.show();
                break;
            case R.id.ll_calendar_free_switch:
                freeRepeatSw.toggle();
                if (freeRepeatSw.isChecked()) {
                    isFree = true;
                    repeats.setTitle("自定");
                    mEndTxLayout.setVisibility(View.VISIBLE);
                    freeRepeatSLayout.setVisibility(View.VISIBLE);
                    fixHeight = fixHzRepeatLayout.getMeasuredHeight();
                    PackagingUtils.hideAnimation(fixHzRepeatLayout);
                } else {
                    isFree = false;
                    repeats.setTitle("无");
                    mEndTxLayout.setVisibility(View.GONE);
                    freeRepeatSLayout.setVisibility(View.GONE);
                    PackagingUtils.showAnimation(fixHzRepeatLayout, fixHeight);
                }
                break;
        }

    }

    private void itemCrash(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                }
                return true;
            }
        });
    }

    private void thingsCrash() {

        itemCrash(checkBox1);
        itemCrash(checkBox2);
        itemCrash(checkBox3);
        itemCrash(checkBox4);
        itemCrash(checkBox5);
        itemCrash(checkBox6);
        itemCrash(checkBox7);

        llrepeatHz.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                }
                return true;
            }
        });

        optionsType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                }
                return true;
            }
        });

        optionsNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        freeScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                }
                return true;
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receicer != null)
            unregisterReceiver(receicer);
    }

    //===OnShowListener ↓=== 防止滑动到最底端，弹出选择器后无法滚动选择问题
    @Override
    public void onShow(Object o) {
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    public void onDismiss(Object o) {
        getSwipeBackLayout().setEnableGesture(true);
    }
    //===OnDismissListener ↑===
}
