package cc.emw.mobile.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
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
import cc.emw.mobile.calendar.fragment.CalendarDayFragment;
import cc.emw.mobile.calendar.fragment.CalendarMonthFragment;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.RelationFileActivity;
import cc.emw.mobile.contact.RelationProjectActivity;
import cc.emw.mobile.contact.RelationTaskActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.me.fragment.WaitHandleCalendarFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.view.RelativeTaskActivity;
import cc.emw.mobile.task.WorkProjectMutiActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedColorPickerDialog;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;
import cc.emw.mobile.view.SwitchButton;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_edit)
public class CalendarEditActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSendTv; // 顶部条发布
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
    @ViewInject(R.id.relation_file_num)
    private TextView tvFileNum; // 相关文件的数量
    @ViewInject(R.id.relation_project_num)
    private TextView tvProjectNum; // 相关项目的数量
    @ViewInject(R.id.relation_task_num)
    private TextView tvTaskNum; // 相关任务的的数量
    /*
     * @ViewInject(R.id.schedule_ll_date) private LinearLayout mDateLayout; //
     * 日期Layout
     */
    @ViewInject(R.id.schedule_btn_startdate)
    private Button mStartDateBtn; // 开始日期按钮
    @ViewInject(R.id.schedule_btn_enddate)
    private Button mEndDateBtn; // 结束日期按钮
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mSelectRootLayout; // 选择分享人员Layout
    @ViewInject(R.id.cm_select_tv_select)
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
    @ViewInject(R.id.ll_calendar_tixing_end)
    private LinearLayout tiXingEndLayout; // 提醒截止Layout
    @ViewInject(R.id.schedule_tv_repeat)
    private TextView repeatInfoTv; // 重复的详情
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView txTextView; // 提醒的详情
    @ViewInject(R.id.schedule_tv_hintbefore_end)
    private TextView txEndTextView; // 提醒的结束时间
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView mHintBeforeTv; // 提前
    @ViewInject(R.id.ll_calendar_color)
    private LinearLayout llCalendarColor;   //颜色Layout
    @ViewInject(R.id.iv_color_more)
    private ImageView mIvColor; // 更多颜色的提示箭头
    @ViewInject(R.id.iv_repeat_more)
    private ImageView mIvRepeat;
    @ViewInject(R.id.iv_tixing_more)
    private ImageView mIvTiXing;
    @ViewInject(R.id.iv_end_repeat_more)
    private ImageView mIvEndRepeat;
    @ViewInject(R.id.iv_share_more)
    private ImageView mIvShare;

    //    private RadioButton checkedRb;
    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> mSelectList = new ArrayList<>(); // 分享人员列表数据
    private boolean flagShareMember;    //分享人员标志
    private TimePickerView mStartPopupWindow, mEndPopupWindow; // 开始、结束、重复中结束时间popupwindow
    private Date mCurDate, mStartDate, mEndDate; // 当前、开始、结束日期

    // private CalendarInfo note; //
    private int waitID;
    private CalendarInfo schedule = new CalendarInfo(); // 日程的实体类
    private SimpleDateFormat format, format2, format3, format4;
    public static final int TASK = 44;
    public static final int PROJECT = 45;
    public static final int ZHISHIKU = 46;
    private static final String TAG = "CalendarEditActivity";
    private ArrayList<UserFenPai> tasks = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<UserFenPai> taskRets = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<Files> fileRets = new ArrayList<Files>();
    private ArrayList<Files> files = new ArrayList<Files>(); // 相关文件的选择列表
    private ArrayList<UserProject> projects = new ArrayList<UserProject>(); // 相关项目的选择列表

    // private String repeatInfoStr = "";
    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    public static final int TIXING_REQUEST = 23;
    public static final int TIXING_REQUEST_END = 24;

    private int type = -1; // 重复的类型
    private int fixType = -1; // 固定的类型
    private int pinlv; // 重复的频率
    private int fixPinlv; // 固定重复的频率
    private String repeatInfoStr; // 重复的结果
    private String typeStr; // 类型的字符串
    private String weekStr; // 重复的周中文模式
    private String weekNum; // 重复的周数字模式
    private String fixRepeatInfoStr; // 固定的重复时间
    private String fixTiXingInfoStr; // 固定的提醒时间
    private String tixingEndStr; // 提醒结束时间的字符串
    //private int[] colors = new int[]{0x00B0CB5E, 0x00007EFF, 0x006DC53C, 0x00E0C400, 0x00D8745C, 0x00795AF9};
    private int[] colors;   // 日程颜色集合
    private int colorWhat = -1;  // 编辑日程之后的颜色
    private int calendarId; // 传递过来的日程id
    public static final String CALENDARID = "calendarId";

    // 更改代码begin
    @ViewInject(R.id.schedule_btn_container)
    private LinearLayout mBtnContainer;// 底部完成 保存按钮容器
    private boolean mIsClick = false;// 顶部右上角是编辑展示 true展示的是取消
    // 更改代码end

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        schedule = (CalendarInfo) getIntent().getSerializableExtra(
                "calendarInfo");
        calendarId = getIntent().getIntExtra(CALENDARID, 0);

        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.calendar_introduction);
        mHeaderSendTv.setVisibility(View.VISIBLE);// 初始化的时候显示顶部右边编辑文本
        mHeaderSendTv.setText(R.string.edit);
        mLoadingDialog = createLoadingDialog(getString(R.string.excuseing));
        mBtnContainer.setVisibility(View.GONE);
        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1), getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3), getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5)};

        if (schedule != null) {
            showCalendarInfo(schedule);
        }

        if (calendarId != 0) {
            getCalendarById();
        }
        mNameEt.setEnabled(false);
        mDescEt.setEnabled(false);
        mStartDateBtn.setEnabled(false);
        mEndDateBtn.setEnabled(false);
        mAllDayLayout.setEnabled(false);
        mRepeatLayout.setEnabled(false);
        tiXingLayout.setEnabled(false);
        tiXingEndLayout.setEnabled(false);
        mSelectRootLayout.setEnabled(false);
        llCalendarColor.setEnabled(false);
        mIvColor.setVisibility(View.GONE);
        mIvRepeat.setVisibility(View.GONE);
        mIvTiXing.setVisibility(View.GONE);
        mIvEndRepeat.setVisibility(View.GONE);
        mIvShare.setVisibility(View.GONE);
    }

    /**
     * 显示日程的详细信息
     */
    private void showCalendarInfo(CalendarInfo schedule) {
        if (!TextUtils.isEmpty(schedule.Line_File)
                && schedule.Line_File.length() != 2) {
            tvFileNum.setVisibility(View.VISIBLE);
            String str = schedule.Line_File.substring(1,
                    schedule.Line_File.length() - 1);
            String[] arr = str.split(",");
            tvFileNum.setText(arr.length + "");
        }
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

        mNameEt.setHint(R.string.share_range_hint);

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
                String startTime = mAllDaySb.isChecked() ? format2
                        .format(date) : format.format(date);
                mStartDateBtn.setTag(startTime);
                mStartDateBtn.setText(startTime);
            }
        });
        mStartDateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartPopupWindow.show();
                HelpUtil.hideSoftInput(CalendarEditActivity.this, mNameEt);
            }
        });

        mEndPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mEndPopupWindow.setTitle(getString(R.string.ending_time));
        mEndPopupWindow.setCancelable(true);
//        try {
//            mEndPopupWindow.setTime(mAllDaySb.isChecked() ? format2.parse(endTime) : format.parse(endTime));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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
        mEndDateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndPopupWindow.show();
                HelpUtil.hideSoftInput(CalendarEditActivity.this, mNameEt);
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

        //显示日程颜色
        showEventColor(schedule);

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
            if (mSelectList != null && mSelectList.size() > 0) {
                for (UserInfo user : mSelectList) {
                    addPersonItem(user);
                }
                mSelectTv.setHint("");
            } else {
                mSelectTv.setHint(R.string.share_range_hint);
            }

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
                                weekInfoScan.append("周一/");
                                break;
                            case 1:
                                weekInfoScan.append("周二/");
                                break;
                            case 2:
                                weekInfoScan.append("周三/");
                                break;
                            case 3:
                                weekInfoScan.append("周四/");
                                break;
                            case 4:
                                weekInfoScan.append("周五/");
                                break;
                            case 5:
                                weekInfoScan.append("周六/");
                                break;
                            case 6:
                                weekInfoScan.append("周日/");
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
                    weekInfoScan.append("周一");
                    break;
                case 1:
                    weekInfoScan.append("周二");
                    break;
                case 2:
                    weekInfoScan.append("周三");
                    break;
                case 3:
                    weekInfoScan.append("周四");
                    break;
                case 4:
                    weekInfoScan.append("周五");
                    break;
                case 5:
                    weekInfoScan.append("周六");
                    break;
                case 6:
                    weekInfoScan.append("周日");
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

        repeatInfoStr = pinlv + repeatTypeStr + " " + weekInfoScan;

        if (tiXingStr != null && !TextUtils.isEmpty(tiXingStr)) {
            txTextView.setText(tiXingStr);
        } else {
            txTextView.setText(getString(R.string.nope));
        }

        if (pinlv != 0) {
            tiXingEndLayout.setVisibility(View.VISIBLE);
            repeatInfoTv.setText("每" + repeatInfoStr);
        } else {
            tiXingEndLayout.setVisibility(View.GONE);
        }

        if (tiXingEndTimeStr != null && !TextUtils.isEmpty(tiXingEndTimeStr)) {
            txEndTextView.setText(tiXingEndTimeStr);
        }
    }

    /**
     * 相关事件的点击、提醒重复的点击
     *
     * @param view
     */
    @Event(value = {R.id.work_project_relation, R.id.task_relation,
            R.id.file_relation, R.id.schedule_ll_repeat,
            R.id.ll_calendar_tixing, R.id.ll_calendar_tixing_end})
    private void relationClick(View view) {
        if (schedule != null) {
            switch (view.getId()) {
                case R.id.work_project_relation:
                    if (mIsClick) {
                        Intent intent = new Intent(this,
                                WorkProjectMutiActivity.class);
                        intent.putExtra(WorkProjectMutiActivity.WORK_PROJECT_IDS,
                                schedule.Line_Project);
                        // ToastUtil.showToast(this, schedule.Line_Project);
                        startActivityForResult(intent, PROJECT);
                    } else {
                        // 展示工作项目
                        String line_Project = schedule.Line_Project;//
                        Intent intent = new Intent(this,
                                RelationProjectActivity.class);
                        // 将项目id字符串传递到相关项目界面
                        intent.putExtra(
                                RelationProjectActivity.CALENDER_WORK_PORJECT,
                                line_Project);
                        startActivity(intent);
                    }
                    break;
                case R.id.task_relation:
                    if (mIsClick) {
                        Intent taskIntent = new Intent(this,
                                RelativeTaskActivity.class);
                        String infos = schedule.Line_Task;
                        if (infos != null && infos.length() > 0) {
                            infos = infos.substring(1, infos.length() - 1);
                        }
                        taskIntent.putExtra("task_ids", infos);
                        // ToastUtil.showToast(this, schedule.Line_Task);
                        taskIntent.putExtra("from",
                                RelativeTaskActivity.FROM_SCHEDULE);
                        startActivityForResult(taskIntent, TASK);
                    } else {
                        // 展示相关任务
                        String line_Task = schedule.Line_Task;
                        Intent intent = new Intent(this, RelationTaskActivity.class);
                        intent.putExtra(RelationTaskActivity.CALENDER_TASK,
                                line_Task);
                        startActivity(intent);
                    }
                    break;
                case R.id.file_relation:
                    if (mIsClick) {
                        Intent repositoryIntent = new Intent(this,
                                FileSelectActivity.class);
                        repositoryIntent.putExtra(
                                FileSelectActivity.EXTRA_SELECT_IDS,
                                schedule.Line_File);
                        // ToastUtil.showToast(this, schedule.Line_File);
                        startActivityForResult(repositoryIntent, ZHISHIKU);
                    } else {
                        // 展示知识库详情
                        String line_File = schedule.Line_File;
                        Intent intent = new Intent(this, RelationFileActivity.class);
                        intent.putExtra(RelationFileActivity.CALENDER_FILE,
                                line_File);
                        startActivity(intent);
                    }
                    break;
                case R.id.schedule_ll_repeat:
                    Intent reIntent = new Intent(this, CalendarRepeatActivity.class);
                    reIntent.putExtra("enterTag", 1);
                    startActivity(reIntent);
                    break;
                case R.id.ll_calendar_tixing:
                    Intent txIntent = new Intent(this, CalendarTxActivity.class);
                    txIntent.putExtra("enterTag", 1);
                    startActivityForResult(txIntent, TIXING_REQUEST);
                    break;
                case R.id.ll_calendar_tixing_end:
                    Intent txEndIntent = new Intent(this,
                            CalendarTxEndActivity.class);
                    txEndIntent.putExtra("enterTag", 1);
                    startActivityForResult(txEndIntent, TIXING_REQUEST_END);
                    break;

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
            super.onBackPressed();
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                HelpUtil.hideSoftInput(this, mNameEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                // 点击切换顶部右上角编辑文本的展示
                // false 编辑 Gone
                mIsClick = !mIsClick;
                if (mIsClick) {
                    flagShareMember = true;
                    mHeaderTitleTv.setText("日程编辑");
                    mHeaderSendTv.setText("取消");
                    mNameEt.setEnabled(true);
                    mDescEt.setEnabled(true);
                    mStartDateBtn.setEnabled(true);
                    mEndDateBtn.setEnabled(true);
                    mAllDayLayout.setEnabled(true);
                    mRepeatLayout.setEnabled(true);
                    tiXingLayout.setEnabled(true);
                    tiXingEndLayout.setEnabled(true);
                    mSelectRootLayout.setEnabled(true);
                    mIvColor.setVisibility(View.VISIBLE);
                    mIvRepeat.setVisibility(View.VISIBLE);
                    mIvTiXing.setVisibility(View.VISIBLE);
                    mIvEndRepeat.setVisibility(View.VISIBLE);
                    mIvShare.setVisibility(View.VISIBLE);
                    if (mDescEt.getText().toString().equals(getResources().getString(R.string.nope))) {
                        mDescEt.setText("");
                    }
                    llCalendarColor.setEnabled(true);
                    llCalendarColor.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AnimatedColorPickerDialog.Builder(CalendarEditActivity.this)
                                    .setTitle("选择一种颜色")
                                    .setColors(colors)
                                    .setOnColorClickListener(new AnimatedColorPickerDialog.ColorClickListener() {
                                        @Override
                                        public void onColorClick(int color) {
                                            Drawable left = null;
                                            if (color == getResources().getColor(R.color.cal_color0)) {
                                                left = getResources().getDrawable(R.drawable.share_circle1_nor);
                                                colorWhat = 0;
                                            } else if (color == getResources().getColor(R.color.cal_color1)) {
                                                left = getResources().getDrawable(R.drawable.share_circle2_nor);
                                                colorWhat = 1;
                                            } else if (color == getResources().getColor(R.color.cal_color2)) {
                                                left = getResources().getDrawable(R.drawable.share_circle3_nor);
                                                colorWhat = 2;
                                            } else if (color == getResources().getColor(R.color.cal_color3)) {
                                                left = getResources().getDrawable(R.drawable.share_circle4_nor);
                                                colorWhat = 3;
                                            } else if (color == getResources().getColor(R.color.cal_color4)) {
                                                left = getResources().getDrawable(R.drawable.share_circle5_nor);
                                                colorWhat = 4;
                                            } else if (color == getResources().getColor(R.color.cal_color5)) {
                                                left = getResources().getDrawable(R.drawable.share_circle6_nor);
                                                colorWhat = 5;
                                            }
                                            if (left != null) {
                                                mTvColorSelect.setVisibility(View.VISIBLE);
                                                left.setBounds(0, 0, DisplayUtil.dip2px(CalendarEditActivity.this, 25), DisplayUtil.dip2px(CalendarEditActivity.this, 25));
                                                mTvColorSelect.setCompoundDrawables(left, null, null, null);
                                            }
                                        }
                                    }).create().show();
                        }
                    });
                } else {
                    flagShareMember = false;
                    mHeaderTitleTv.setText("日程详情");
                    mHeaderSendTv.setText("编辑");
                    mNameEt.setEnabled(false);
                    mDescEt.setEnabled(false);
                    mStartDateBtn.setEnabled(false);
                    mEndDateBtn.setEnabled(false);
                    mAllDayLayout.setEnabled(false);
                    mRepeatLayout.setEnabled(false);
                    tiXingLayout.setEnabled(false);
                    tiXingEndLayout.setEnabled(false);
                    mSelectRootLayout.setEnabled(false);
                    mIvColor.setVisibility(View.GONE);
                    mIvRepeat.setVisibility(View.GONE);
                    mIvTiXing.setVisibility(View.GONE);
                    mIvEndRepeat.setVisibility(View.GONE);
                    mIvShare.setVisibility(View.GONE);
                    if (schedule.Remark == null || TextUtils.isEmpty(schedule.Remark)) {
                        mDescEt.setText(getResources().getString(R.string.nope));
                    }
                    llCalendarColor.setEnabled(false);
                }
//                mHeaderSendTv.setText(mIsClick ? getString(R.string.cancel)
//                        : getString(R.string.edit));
                // 底部保存 完成按钮的切换
                mBtnContainer.setVisibility(mIsClick ? View.VISIBLE : View.GONE);
                break;
        }
    }

    //显示日程的颜色
    private void showEventColor(CalendarInfo schedule) {
        Drawable left = null;
        if (0 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle1_nor);
        } else if (1 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle2_nor);
        } else if (2 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle3_nor);
        } else if (3 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle4_nor);
        } else if (4 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle5_nor);
        } else if (5 == schedule.Color) {
            left = getResources().getDrawable(R.drawable.share_circle6_nor);
        }
        if (left != null) {
            mTvColorSelect.setVisibility(View.VISIBLE);
            left.setBounds(0, 0, DisplayUtil.dip2px(CalendarEditActivity.this, 25), DisplayUtil.dip2px(CalendarEditActivity.this, 25));
            mTvColorSelect.setCompoundDrawables(left, null, null, null);
        }
    }

    @Event(value = {R.id.schedule_btn_finish, R.id.schedule_btn_save})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_btn_finish:
                new AlertDialog(this)
                        .builder()
                        .setMsg(getString(R.string.conform_to_do))
                        .setPositiveButton(getString(R.string.ok),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finishCalendar(schedule.ID);
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                break;
            case R.id.schedule_btn_save:
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

                    if (startCalendar.getTimeInMillis() >= endCalendar
                            .getTimeInMillis() && !mAllDaySb.isChecked()) {
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
        intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
                ContactSelectActivity.MULTI_SELECT);
        intent.putExtra("select_list", mSelectList);
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
                // 分享范围
                case 2:
                    mSelectFlowLayout.removeAllViews();
                    mSelectList.clear();
                    mSelectList = (ArrayList<UserInfo>) data
                            .getSerializableExtra("select_list");
                    for (UserInfo user : mSelectList) {
                        addPersonItem(user);
                    }
                    if (mSelectList.size() > 0) {
                        mSelectTv.setHint("");
                    } else {
                        mSelectTv.setHint(R.string.share_range_hint);
                    }
                    break;
                case CalendarEditActivity.PROJECT:
                    projects = (ArrayList<UserProject>) data
                            .getSerializableExtra(WorkProjectMutiActivity.WORK_PROJECTS);
                    schedule.Line_Project = project2string(projects);
                    if (schedule.Line_Project.length() == 2) {
                        tvProjectNum.setVisibility(View.GONE);
                    }
                    if (projects != null && projects.size() > 0) {
                        tvProjectNum.setVisibility(View.VISIBLE);
                        tvProjectNum.setText(projects.size() + "");
                    }
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
                    fileRets = (ArrayList<Files>) data
                            .getSerializableExtra("select_list");
                    CommonUtil.filterFiles(files, fileRets);
                    schedule.Line_File = files2string(files);
                    if (schedule.Line_File.length() == 2) {
                        tvFileNum.setVisibility(View.GONE);
                    }
                    if (files.size() != 0) {
                        tvFileNum.setVisibility(View.VISIBLE);
                        tvFileNum.setText(files.size() + "");
                    }
                    break;
            }
        }
        if (requestCode == TIXING_REQUEST && resultCode == RESULT_OK) {
            fixTiXingInfoStr = data.getStringExtra("tixing");
            if (!fixTiXingInfoStr.equals("null")) {
                txTextView.setText(fixTiXingInfoStr);
            }
        }
        if (requestCode == TIXING_REQUEST_END && resultCode == RESULT_OK) {
            tixingEndStr = data.getStringExtra("tixingEnd");
            if (!tixingEndStr.equals("null")) {
                txEndTextView.setText(tixingEndStr);
            }
        }
    }

    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
        PersonTextView childView = new PersonTextView(this, R.drawable.persontext_bg, false);
        childView.setTag(user);
        childView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagShareMember) {
                    mSelectFlowLayout.removeView(v);
                    mSelectList.remove((UserInfo) v.getTag());
                    if (mSelectList.size() == 0) {
                        mSelectTv.setHint(R.string.share_range_hint);
                    }
                }
            }
        });
        childView.setText(user.Name);
        mSelectFlowLayout.addView(childView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        type = intent.getIntExtra("type", -1);
        pinlv = intent.getIntExtra("pinlv", 0);
        weekStr = intent.getStringExtra("week"); // 重复周的详细描述
        weekNum = intent.getStringExtra("weekNum"); // 重复周的数字描述
        fixRepeatInfoStr = intent.getStringExtra("fixRepeat"); // 固定重复的详细描述
        fixPinlv = intent.getIntExtra("fixpv", 0);
        fixType = intent.getIntExtra("fixType", -1);

        switch (type) {
            case 0:
                typeStr = "天";
                break;
            case 1:
                typeStr = "周";
                break;
            case 2:
                typeStr = "月";
                break;
            case 3:
                typeStr = "年";
                break;
        }
        switch (fixType) {
            case 0:
                typeStr = "天";
                break;
            case 1:
                typeStr = "周";
                break;
            case 2:
                typeStr = "月";
                break;
            case 3:
                typeStr = "年";
                break;
        }

        repeatInfoStr = pinlv + typeStr + " " + weekStr;

        // 重复提醒的判断
        if (type != -1) {
            repeatInfoTv.setText("每" + repeatInfoStr);
        }
        if (fixRepeatInfoStr != null) {
            repeatInfoTv.setText(fixRepeatInfoStr);
        }
        if (pinlv != 0) {
            tiXingEndLayout.setVisibility(View.VISIBLE);
        }
        if (repeatInfoTv.getText().equals(getString(R.string.nope))) {
            tiXingEndLayout.setVisibility(View.GONE);
        } else {
            tiXingEndLayout.setVisibility(View.VISIBLE);
        }
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
            cInfo.NoteRoles = nrList;
            cInfo.ID = schedule.ID;
            cInfo.Title = mNameEt.getText().toString();
            cInfo.Remark = mDescEt.getText().toString();
            cInfo.StartTime = mStartDateBtn.getTag().toString();
            cInfo.OverTime = mEndDateBtn.getTag().toString();
            cInfo.Color = schedule.Color;
            if (colorWhat != -1) {
                cInfo.Color = colorWhat;
            }
            cInfo.Allday = mAllDaySb.isChecked() ? 1 : 0;
            cInfo.Priority = 1;
            cInfo.State = 1;
            cInfo.Type = 1;
            // 相关事件
            if (tasks != null && tasks.size() > 0) {
                cInfo.Line_Task = tasks2string(tasks);
            } else {
                cInfo.Line_Task = schedule.Line_Task;
            }
            if (projects != null && projects.size() > 0) {
                cInfo.Line_Project = project2string(projects);
            } else {
                cInfo.Line_Project = schedule.Line_Project;
            }
            if (files != null && files.size() > 0) {
                cInfo.Line_File = files2string(files);
            } else {
                cInfo.Line_File = schedule.Line_File;
            }

            // 重复的类型
            if (type != -1) {
                cInfo.REPEATTYPE = type + 1;
            }
            if (fixType != -1) {
                cInfo.REPEATTYPE = fixType + 1;
            }
            if (fixType == -1 && type == -1) {
                cInfo.REPEATTYPE = schedule.REPEATTYPE;
            }

            // 重复的频率
            if (pinlv != 0) {
                cInfo.REPEATHZ = pinlv;
            }
            if (fixPinlv != 0) {
                cInfo.REPEATHZ = fixPinlv;
            }
            if (pinlv == 0 && fixPinlv == 0) {
                cInfo.REPEATHZ = schedule.REPEATHZ;
            }

            if (weekNum != null) {
                // String week = "[" + weekNum + "]";
                cInfo.REPEATWEEKVAL = weekNum;
            } else {
                cInfo.REPEATWEEKVAL = schedule.REPEATWEEKVAL;
            }

            // 提醒提前的时间
            if (fixTiXingInfoStr != null) {
                if (fixTiXingInfoStr.equals(getString(R.string.nope))) {
                    cInfo.ISCALL = 0;
                } else {
                    cInfo.ISCALL = 1;
                    if (fixTiXingInfoStr.equals(getString(R.string.ontime))) {
                        cInfo.AHEAD_MINUTE = 0;
                    } else if (fixTiXingInfoStr
                            .equals(getString(R.string.five_minute_per))) {
                        cInfo.AHEAD_MINUTE = 5;
                    } else if (fixTiXingInfoStr
                            .equals(getString(R.string.one_hours))) {
                        cInfo.AHEAD_MINUTE = 60;
                    } else if (fixTiXingInfoStr
                            .equals(getString(R.string.three_hours))) {
                        cInfo.AHEAD_MINUTE = 180;
                    } else if (fixTiXingInfoStr.equals(getString(R.string.one_day))) {
                        cInfo.AHEAD_MINUTE = 1440;
                    } else if (fixTiXingInfoStr
                            .equals(getString(R.string.three_day))) {
                        cInfo.AHEAD_MINUTE = 4320;
                    } else if (fixTiXingInfoStr
                            .equals(getString(R.string.one_week))) {
                        cInfo.AHEAD_MINUTE = 10080;
                    }
                }
            } else {
                cInfo.AHEAD_MINUTE = schedule.AHEAD_MINUTE;
            }

            // 提醒的终止时间
            if (tixingEndStr != null
                    && !tixingEndStr.equals(getString(R.string.never))) {
                cInfo.REPEATENDTIME = tixingEndStr;
            } else {
                cInfo.REPEATENDTIME = schedule.REPEATENDTIME;
            }

            // 提醒开始的小时分钟
            if (cInfo.ISCALL == 1) {
                String repeatTimeVals = "09:00";
                if (cInfo.Allday == 0) {
                    repeatTimeVals = cInfo.StartTime.substring(11,
                            cInfo.StartTime.length());
                }
                cInfo.RepeatTimeVal = repeatTimeVals;
            }

            cInfo.IsJobSync = 1;

            cInfo.NotePriority = "[" + gson.toJson(cInfo) + "]";
        }
        return cInfo;
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
            cInfo = getCInfo(schedule);
        }
        API.TalkerAPI.AddCalendar(cInfo, new RequestCallback<String>(
                String.class) {

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
                Toast.makeText(CalendarEditActivity.this,
                        getString(R.string.service_excep), Toast.LENGTH_SHORT)
                        .show();
                mLoadingDialog.dismiss();
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess(String arg0) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(arg0) && TextUtils.isDigitsOnly(arg0)
                        && Integer.valueOf(arg0) == 1) {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.edit_success),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(CalendarDayFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    sendBroadcast(intent);
                    intent = new Intent(
                            CalendarMonthFragment.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    sendBroadcast(intent); // 刷日程月列表
                    intent = new Intent(
                            CalendarDayActivitys.ACTION_REFRESH_DAY_VIEW);
                    sendBroadcast(intent); // 刷日程日视图列表
                    intent = new Intent(
                            WaitHandleCalendarFragment.ACTION_REFRESH_CALENDAR_HANDLE);
                    sendBroadcast(intent);
                    intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
                    sendBroadcast(intent);
                    onBackPressed();
                } else {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.edit_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void finishCalendar(int cid) {
        API.TalkerAPI.FinishCalendar(cid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Toast.makeText(CalendarEditActivity.this,
                        getString(R.string.service_excep), Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess(String arg0) {
                if (!TextUtils.isEmpty(arg0) && TextUtils.isDigitsOnly(arg0)
                        && Integer.valueOf(arg0) == 1) {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.finish_success),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(CalendarDayFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    sendBroadcast(intent);
                    intent = new Intent(
                            CalendarMonthFragment.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    sendBroadcast(intent); // 刷日程列表
                    intent = new Intent(
                            CalendarDayActivitys.ACTION_REFRESH_DAY_VIEW);
                    sendBroadcast(intent); // 刷新日程日视图列表
                    intent = new Intent(
                            DynamicFragment.ACTION_REFRESH_HOME_LIST);
                    sendBroadcast(intent); // 刷新动态列表
                    onBackPressed();
                } else {
                    Toast.makeText(CalendarEditActivity.this,
                            getString(R.string.finish_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 保存
     *
     * @param content
     */
    // private void send(String content) {
    // HttpUtils http = new HttpUtils();
    // http.configCookieStore(PrefsUtil.readLoginCookie());
    // RequestParam param = new RequestParam();
    // UserNote un = new UserNote();
    // un.ID = note.ID;
    // un.Type = 7;
    // SimpleUser user = new SimpleUser();
    // user.setID(PrefsUtil.readUserInfo().getID());
    // un.User = user;
    // NoteInfo info = new NoteInfo();
    // info.content = content;
    // NoteSchedule schedule = new NoteSchedule();
    // schedule.StartTime = mStartDateBtn.getTag().toString();
    // schedule.endTime = mEndDateBtn.getTag().toString();
    // RadioButton checkedRb = (RadioButton) mTagRadioGroup
    // .findViewById(mTagRadioGroup.getCheckedRadioButtonId());
    // schedule.Color = checkedRb.getTag().toString();
    // schedule.Allday = mAllDaySb.isChecked() ? 1 : 0;
    // StringBuffer week = new StringBuffer();// 0,1,2,3,4,5,6
    // if ("2".equals(mRepeatTypeNs.getTag().toString())) {
    // for (int i = 0, count = mRateWeekLayout.getChildCount(); i < count; i++)
    // {
    // CheckBox checkBox = (CheckBox) mRateWeekLayout.getChildAt(i);
    // if (checkBox.isChecked()) {
    // if (week.length() > 0)
    // week.append(",");
    // week.append(checkBox.getTag().toString());
    // }
    // }
    // }
    //
    // String repeat = "";
    // if (mRepeatSb.isChecked()) {
    // // 重复类型|重复频率值|重复结束时间|星期选择
    // repeat = mRepeatTypeNs.getTag().toString() + "|"
    // + mRateNumEt.getText().toString() + "|"
    // + mRepeatTimeTv.getTag().toString() + "|" + week;
    // }
    // String call = "";
    // if (!"4".equals(mHintUnitNs.getTag().toString())) {
    // // 提醒值|重复类型
    // call = mHintNumEt.getText().toString() + "|"
    // + mHintUnitNs.getTag().toString();
    // }
    // schedule.Repeat = repeat;
    // schedule.Call = call;
    // info.plan = schedule;
    //
    // un.Color = checkedRb.getTag().toString();
    // un.Allday = mAllDaySb.isChecked();
    // un.Repeat = repeat;
    // un.Call = call;
    // PlanInfo planInfo = new PlanInfo();
    // planInfo.StartTime = HelpUtil.time2CSharp(new Date(
    // getTime(schedule.StartTime)));
    // planInfo.EndTime = HelpUtil.time2CSharp(new Date(
    // getTime(schedule.endTime)));
    // un.Plan = planInfo;
    //
    // un.Content = content;
    // ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
    // if (mSelectList != null && mSelectList.size() > 0) {
    // un.SendType = 1; // 0 公共 1 私有
    // for (int i = 0; i < mSelectList.size(); i++) {
    // NoteRole role = new NoteRole();
    // role.setID(mSelectList.get(i).getID());
    // role.setName(mSelectList.get(i).getName());
    // role.setImage(mSelectList.get(i).getImage());
    // role.setType(1);
    // nrList.add(role);
    // }
    // un.Roles = nrList;
    // } else {
    // un.SendType = 0;
    // }
    // Gson gson = new Gson();
    // param.setStringParams(un, waitID, note.ID, gson.toJson(info), content,
    // mStartDateBtn.getTag().toString(), mEndDateBtn.getTag()
    // .toString());
    // http.send(HttpMethod.POST, HttpConst.Url_Client
    // + "?t=Chatter.TalkerAccess&m=SetCalendarById", param,
    // new RequestCallBack<String>() {
    // @Override
    // public void onStart() {
    // HelpUtil.hideSoftInput(ScheduleEditActivity.this,
    // mContentEt);
    // mLoadingDialog = createLoadingDialog("正在处理...");
    // mLoadingDialog.show();
    // }
    //
    // @Override
    // public void onSuccess(ResponseInfo<String> responseInfo) {
    // LogUtils.d("send:" + responseInfo.result);
    // mLoadingDialog.dismiss();
    // if (responseInfo.result != null
    // && !"0".equals(responseInfo.result)) {
    // Toast.makeText(ScheduleEditActivity.this, "保存成功",
    // Toast.LENGTH_SHORT).show();
    // Intent intent = new Intent(
    // HomeFragment.ACTION_REFRESH_HOME_LIST);
    // sendBroadcast(intent); // 刷新首页列表
    // intent = new Intent(
    // ScheduleFragment.ACTION_REFRESH_SCHEDULE_LIST);
    // sendBroadcast(intent); // 刷新日程列表
    // setResult(Activity.RESULT_OK); // 刷新未处理工作列表
    // finish();
    // } else {
    // Toast.makeText(ScheduleEditActivity.this, "保存失败",
    // Toast.LENGTH_SHORT).show();
    // }
    // }
    //
    // @Override
    // public void onFailure(HttpException error, String msg) {
    // mLoadingDialog.dismiss();
    // Toast.makeText(ScheduleEditActivity.this,
    // error.getExceptionCode() + " " + msg,
    // Toast.LENGTH_SHORT).show();
    // }
    // });
    // }

    /**
     * 完成
     */
    // private void finishWait() {
    // HttpUtils http = new HttpUtils();
    // http.configCookieStore(PrefsUtil.readLoginCookie());
    // RequestParam param = new RequestParam();
    // param.setStringParams(note.ID, 2);
    // http.send(HttpMethod.POST, HttpConst.Url_Client
    // + "?t=Server.UserTask&m=ChangeTaskState", param,
    // new RequestCallBack<String>() {
    // @Override
    // public void onStart() {
    // HelpUtil.hideSoftInput(ScheduleEditActivity.this,
    // mContentEt);
    // mLoadingDialog = createLoadingDialog("正在处理...");
    // mLoadingDialog.show();
    // }
    //
    // @Override
    // public void onSuccess(ResponseInfo<String> responseInfo) {
    // LogUtils.d("send:" + responseInfo.result);
    // mLoadingDialog.dismiss();
    // if (responseInfo.result != null
    // && !"0".equals(responseInfo.result)) {
    // Intent intent = new Intent(
    // HomeFragment.ACTION_REFRESH_HOME_LIST);
    // sendBroadcast(intent); // 刷新首页列表
    // intent = new Intent(
    // ScheduleFragment.ACTION_REFRESH_SCHEDULE_LIST);
    // sendBroadcast(intent); // 刷新日程列表
    // setResult(Activity.RESULT_OK); // 刷新未处理工作列表
    // finish();
    // } else {
    // Toast.makeText(ScheduleEditActivity.this, "操作失败",
    // Toast.LENGTH_SHORT).show();
    // }
    // }
    //
    // @Override
    // public void onFailure(HttpException error, String msg) {
    // mLoadingDialog.dismiss();
    // Toast.makeText(ScheduleEditActivity.this,
    // error.getExceptionCode() + " " + msg,
    // Toast.LENGTH_SHORT).show();
    // }
    // });
    // }
    //

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
        sb.append("[");
        for (int i = 0; i < files.size(); i++) {
            sb.append(files.get(i).ID);
            if (i != files.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
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

}
