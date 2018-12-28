package cc.emw.mobile.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.map.GeoFenceActivity;
import cc.emw.mobile.me.widget.ListPopup;
import cc.emw.mobile.me.widget.OnScalePopupItemClickListener;
import cc.emw.mobile.me.widget.ScalePopup;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.SwipeBackScrollView;
import cc.emw.mobile.view.SwitchButton;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * @author zrjt 日程创建
 * @version 创建时间：2016-4-20 下午7:31:23
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_create)
public class CalendarCreateActivitys extends BaseActivity {
    @ViewInject(R.id.cm_input_et_content)
    private EditText mContentEt; // 内容 发表一下
    @ViewInject(R.id.et_calendarcreate_name)
    private EditText mNameEt; // 名称 日程名称
    @ViewInject(R.id.et_calendarcreate_desc)
    private EditText mDescEt; // 描述 日程描素
    @ViewInject(R.id.ll_schedule_startdate)
    private LinearLayout mStartDateLayout;
    @ViewInject(R.id.schedule_btn_startdate)
    private TextView mStartDateTv; // 开始日期按钮
    @ViewInject(R.id.ll_schedule_enddate)
    private LinearLayout mEndDateLayout;
    @ViewInject(R.id.schedule_btn_enddate)
    private TextView mEndDateTv; // 结束日期按钮
    @ViewInject(R.id.tv_calendar_file_number)
    private TextView tvFileNumber;
    @ViewInject(R.id.schedule_ll_allday)
    private LinearLayout mAllDayLayout; // 全天事件Layout
    @ViewInject(R.id.schedule_sb_allday)
    private SwitchButton mAllDaySb; // 全天事件开关按钮
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView txTextView; // 提醒的详情
    @ViewInject(R.id.schedule_tv_hintbefore_end)
    private TextView txEndTextView; // 提醒的详情
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mSelectRootLayout; // 选择分享人员根Layout
    @ViewInject(R.id.cm_select_tv_name)
    private TextView mSelectTv; // 分享范围
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout
    @ViewInject(R.id.cm_ll_worker)
    private LinearLayout mWorkerLayout; // 同步到工作台根Layout
    @ViewInject(R.id.cm_sb_worker)
    private SwitchButton mWorkerSb; // 同步到工作台开关
    @ViewInject(R.id.ll_calendar_tixing_ends)
    private LinearLayout mEndTxLayout;  // 截止提醒Layout
    @ViewInject(R.id.main_calendar_create)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.tv_task_modify_location)
    private TextView mLocationTv;
    @ViewInject(R.id.ll_repeat_end)
    private LinearLayout mLlRepeatEnd;
    @ViewInject(R.id.tv_repeat)
    private TextView mTvRepeat;
    @ViewInject(R.id.tv_repeat_end)
    private TextView mTvRepeatEnd;
    @ViewInject(R.id.tv_remind)
    private TextView mTvRemind;
    @ViewInject(R.id.tv_tag)
    private TextView mTvTag;

    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private ArrayList<ApiEntity.Files> fileRets = new ArrayList<>();
    private Date mStartDate, mEndDate; // 当前、开始、结束日期
    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    private List<ApiEntity.UserLabel> mLables = new ArrayList<>();  //用户标签集合
    ///////////////////////////////////////////////////////////
    private int colorWhat = -1;  // 日程的颜色(标签)
    private int enterFlag;
    private Calendar mClickDate;
    private SimpleDateFormat format, format2, format3, format4;
    public static final int ZHISHIKU = 144;
    //////////////////////////////////////////////////////////////
    private DatePickerDialog dpdStart, dpdEnd, dpdRepeatEnd; //开始/结束时间弹框
    private ScalePopup mPopupRepeat, mPopupRepeatEnd, mPopupRemind; //重复提醒弹框
    private ListPopup mListPopupTag;    //标签弹框
    private List<String> mListRepeat, mListRepeatEnd, mListRemind;  //重复提醒数据
    private String mStrStartTime, mStrEndTime, mStrRepeatEnd;
    public static final int REQUESTCODE_REPEAT = 999; //选择重复数据requestCode
    /////////////////////////////////////////////////////////////

    private int groupID, projectID; //列表传值，来自TimeTracking中的动态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupID = getIntent().getIntExtra("group_id", 0);
        projectID = getIntent().getIntExtra("project_id", 0);
        init();
    }

    private void init() {
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        enterFlag = getIntent().getIntExtra("enter_flag", 0);
        mClickDate = (Calendar) getIntent().getSerializableExtra("createDate");
        mDescEt.setText(getIntent().getStringExtra("chat_desc"));
        mContentEt.setVisibility(enterFlag == 1 ? View.VISIBLE : View.GONE);
        if (mContentEt.getVisibility() == View.VISIBLE) {
            mContentEt.requestFocus();
        }
        String startDay = getIntent().getStringExtra("start_date");
        String endDay = getIntent().getStringExtra("end_date");

        initTagList();  //初始化系统自带标签

        mLoadingDialog = createLoadingDialog(R.string.excuseing);
        mSelectRootLayout.setVisibility(enterFlag == 1 ? View.VISIBLE
                : View.GONE);
//        mWorkerLayout.setVisibility(enterFlag == 1 ? View.VISIBLE : View.GONE);
        initRepeatRemindData();     //初始化重复提醒数据
        mWorkerLayout.setVisibility(View.GONE);

        format = new SimpleDateFormat(
                getString(R.string.timeformat6));
        format2 = new SimpleDateFormat(
                getString(R.string.timeformat5));
        format3 = new SimpleDateFormat(
                getString(R.string.timeformat13));
        format4 = new SimpleDateFormat(
                getString(R.string.timeformat9));

        mStartDate = new Date();
        if (mClickDate != null) {
            mStartDate = mClickDate.getTime();
        }
        String startTime = format.format(mStartDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mStartDate);
        calendar.add(Calendar.HOUR, 1);
        mEndDate = calendar.getTime();
        String endTime = format.format(mEndDate);

        if (startDay != null && !TextUtils.isEmpty(startDay)) {
            startTime = startDay;
            endTime = endDay;
            mAllDaySb.setChecked(true);
        }
        setAllDayChecked(mAllDaySb.isChecked());   //全天事件处理
        mStartDateTv.setTag(startTime);
        mStartDateTv.setText(startTime);
        mEndDateTv.setTag(endTime);
        mEndDateTv.setText(endTime);
        initTime(); //选择时间监听
        getCalendarTag();   //获取用户自定义标签
    }

    @Event(value = {R.id.cm_header_tv_right9, R.id.cm_header_btn_left9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                if (mNameEt.isFocusable())
                    KeyBoardUtil.hideSoftInput(mNameEt, this);
                else
                    KeyBoardUtil.hideSoftInput(mDescEt, this);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String content = mNameEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(this, R.string.empty_content_tips);
                } else {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTimeInMillis(getLongTime(mStartDateTv.getTag()
                            .toString()));
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTimeInMillis(getLongTime(mEndDateTv.getTag()
                            .toString()));
                    if (startCalendar.getTimeInMillis() > endCalendar
                            .getTimeInMillis()) {
                        Toast.makeText(this,
                                R.string.endingtime_notlessthan_starttime,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        send();
                    }
                }
                break;
        }
    }

    @Event(value = {R.id.cm_select_ll_select, R.id.ll_calendar_modify_location_container, R.id.file_relation,
            R.id.ll_repeat, R.id.ll_repeat_end, R.id.ll_remind, R.id.ll_tag_layout})
    private void onSelectClick(View v) {
        switch (v.getId()) {
            case R.id.ll_repeat:  //选择重复
                mPopupRepeat = new ScalePopup(this, mListRepeat);
                mPopupRepeat.showPopupWindow();
                initRRListener(0);
                break;
            case R.id.ll_repeat_end:  //选择重复截止
                mPopupRepeatEnd = new ScalePopup(this, mListRepeatEnd);
                mPopupRepeatEnd.showPopupWindow();
                initRRListener(1);
                break;
            case R.id.ll_remind:  //选择提醒
                mPopupRemind = new ScalePopup(this, mListRemind);
                mPopupRemind.showPopupWindow();
                initRRListener(2);
                break;
            case R.id.ll_tag_layout:    //选择标签
                ListPopup.Builder builder = new ListPopup.Builder(this);
                for (int i = 0; i < mLables.size(); i++) {
                    builder.addItem(mLables.get(i).Name);
                }
                mListPopupTag = builder.build();
                mListPopupTag.showPopupWindow();
                mListPopupTag.setOnListPopupItemClickListener(new ListPopup.OnListPopupItemClickListener() {
                    @Override
                    public void onItemClick(int what) {
                        colorWhat = what - 1;
                        mTvTag.setText(mLables.get(what).Name);
                        mListPopupTag.dismiss();
                    }
                });
                break;
            case R.id.cm_select_ll_select:  //谁能看见
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                intent.putExtra("has_oneself", false);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, 2);
                break;
            case R.id.file_relation:
                Intent repositoryIntent = new Intent(this, FileSelectActivity.class);
                repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, cInfo.Line_File);
                repositoryIntent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                repositoryIntent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(repositoryIntent, ZHISHIKU);
                break;
            case R.id.ll_calendar_modify_location_container:
                Intent locationIntent = new Intent(this, GeoFenceActivity.class);
                startActivityForResult(locationIntent, 143);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_REPEAT:    //自定义重复回传数据
                    mTvRepeat.setText("自定");
                    int type = data.getIntExtra("type", 0);
                    int hz = data.getIntExtra("hz", 0);
                    cInfo.REPEATTYPE = type;
                    cInfo.REPEATHZ = hz;
                    if (type == 2) {
                        String weekStr = data.getStringExtra("weekStr");
                        if (weekStr != null && !TextUtils.isEmpty(weekStr)) {
                            cInfo.REPEATWEEKVAL = weekStr;
                        }
                    }
                    break;
                case 143:
                    ApiEntity.UserRail userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
                    int id = data.getIntExtra("UserRailId", 0);
                    if (userRail != null && id != 0) {
                        cInfo.RailID = id;
                        cInfo.Rail = userRail;
                        mLocationTv.setText(userRail.Address);
                    }
                    break;
                case CalendarCreateActivitys.ZHISHIKU:
                    fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    cInfo.Line_File = HelpUtil.files2StrID(fileRets);
                    if (fileRets == null || fileRets.size() == 0) {
                        tvFileNumber.setVisibility(View.INVISIBLE);
                    } else {
                        tvFileNumber.setVisibility(View.VISIBLE);
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < fileRets.size(); i++) {
                            if (i < 2) {
                                ApiEntity.Files user = fileRets.get(i);
                                if (i != 0) {
                                    builder.append("、");
                                }
                                builder.append(user.Name);
                            } else {
                                builder.append("等" + fileRets.size() + "个");
                                break;
                            }
                        }
                        tvFileNumber.setText(builder);
                    }
                    break;
                case 2:
                    mSelectFlowLayout.removeAllViews();
                    selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < selectList.size(); i++) {
                        if (i < 3) {
                            ApiEntity.UserInfo user = selectList.get(i);
                            if (i != 0) {
                                builder.append("、");
                            }
                            builder.append(user.Name);
                        } else {
                            builder.append("等" + selectList.size() + "人");
                            break;
                        }
                    }
                    mSelectTv.setText(builder);
                    mSelectTv.setHint(selectList.size() > 0 ? "" : "私有");
                    break;
            }
        }
    }

    /**
     * 代提交的日程对象
     *
     * @return
     */
    private CalendarInfo getCInfo() {
        if (groupID > 0) {
            cInfo.Line_Group = Integer.toString(groupID);
        }
        if (projectID > 0) {
            cInfo.Line_Project = Integer.toString(projectID);
        }
        ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
        if (selectList != null && selectList.size() > 0) {
            for (int i = 0, size = selectList.size(); i < size; i++) {
                NoteRole role = new NoteRole();
                role.ID = selectList.get(i).ID;
                role.Name = selectList.get(i).Name;
                role.Image = selectList.get(i).Image;
                role.Type = NoteRoleTypes.User;
                nrList.add(role);
            }
        }
        Gson gson = new Gson();
        cInfo.NoteRoles = nrList;
        cInfo.NoteContent = mContentEt.getText().toString();
        cInfo.IsJobSync = 1;
        cInfo.Title = mNameEt.getText().toString();
        cInfo.Remark = mDescEt.getText().toString();
        cInfo.StartTime = mStartDateTv.getTag().toString();
        cInfo.OverTime = mEndDateTv.getTag().toString();
        cInfo.Allday = mAllDaySb.isChecked() ? 1 : 0;
//        if (mAllDaySb.isChecked()) {
//            cInfo.StartTime = mStartDateTv.getTag().toString() + " 00:00:00";
//            cInfo.OverTime = mEndDateTv.getTag().toString() + " 23:59:59";
//        }

        cInfo.Color = colorWhat;

        cInfo.Line_Project = "[]";
        cInfo.Line_Task = "[]";
        cInfo.Line_File = HelpUtil.files2StrID(fileRets);
//        if (cInfo.Allday == 1) {
//            String endTime = mEndDateTv.getTag().toString();
//            endTime = endTime + "23:59:59";
//            cInfo.OverTime = endTime;
//        }
        cInfo.Priority = 1;
        cInfo.State = 1;
        cInfo.Type = 7;

        // 提醒开始的小时分钟
        if (cInfo.ISCALL == 1) {
            String repeatTimeVals = "09:00";
            if (cInfo.Allday == 0) {
                repeatTimeVals = cInfo.StartTime.substring(11,
                        cInfo.StartTime.length());
            }
            cInfo.RepeatTimeVal = repeatTimeVals;
        }
        cInfo.NotePriority = "[" + gson.toJson(cInfo) + "]";
        return cInfo;
    }

    /**
     * 发布日程
     */
    private void send() {
        cInfo = getCInfo();
        API.TalkerAPI.AddCalendar(cInfo, new RequestCallback<ApiEntity.APIResult>(
                        ApiEntity.APIResult.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (!isFinishing())
                            mLoadingDialog.dismiss();
                        Toast.makeText(CalendarCreateActivitys.this,
                                getString(R.string.service_excep) + arg0.toString(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onParseSuccess(ApiEntity.APIResult respInfo) {
                        if (!isFinishing())
                            mLoadingDialog.dismiss();
                        if (respInfo.State == 1) {
                            ToastUtil.showToast(CalendarCreateActivitys.this,
                                    getString(R.string.groupcreate_success),
                                    R.drawable.tishi_ico_gougou);
                            Intent intent = new Intent(
                                    CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                            intent.putExtra("edit", "edit");
                            sendBroadcast(intent); // 刷新日程月视图列表
                            intent = new Intent(
                                    DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                            if (groupID > 0) {
                                intent = new Intent(TimeTrackingWebFragment.ACTION_REFRESH_TIMEDYNAMIC);
                            }
                            sendBroadcast(intent); // 刷新动态列表
                            finish();
                        } else {
                            Toast.makeText(CalendarCreateActivitys.this,
                                    respInfo.Msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

//    /**
//     * 显示选择的分享人员
//     */
//    private void addPersonItem(UserInfo user) {
//        PersonTextView childView = new PersonTextView(this);
//        childView.setTag(user);
//        childView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSelectFlowLayout.removeView(v);
//                selectList.remove((UserInfo) v.getTag());
//                if (selectList.size() == 0) {
//                    mSelectTv.setHint(R.string.share_range_hint);
//                }
//            }
//        });
//        childView.setText(user.Name);
//        mSelectFlowLayout.addView(childView);
//    }

    // 将字符串转为时间戳
    private long getLongTime(String user_time) {
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

    /**
     * 初始化日程标签
     */
    private void initTagList() {
        ApiEntity.UserLabel tag0 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag1 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag2 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag3 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag4 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag5 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag6 = new ApiEntity.UserLabel();
        ApiEntity.UserLabel tag7 = new ApiEntity.UserLabel();
        tag0.Name = "无";
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

    /**
     * 获取日程标签
     */
    private void getCalendarTag() {
        API.TalkerAPI.GetUserLabel(PrefsUtil.readUserInfo().ID, new RequestCallback<ApiEntity.UserLabel>(ApiEntity.UserLabel.class) {

            @Override
            public void onParseSuccess(List<ApiEntity.UserLabel> respList) {
                if (respList != null && respList.size() > 0) {
                    mLables.clear();
                    initTagList();
                    mLables.addAll(respList);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(CalendarCreateActivitys.this, "获取用户自定义标签失败", Toast.LENGTH_SHORT).show();
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

    /**
     * 时间选择监听
     */
    private void initTime() {
        mStartDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar now = Calendar.getInstance();
                dpdStart = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStrStartTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                                if (mAllDaySb.isChecked()) {
                                    mStartDateTv.setTag(mStrStartTime);
                                    mStartDateTv.setText(mStrStartTime);
                                } else {
                                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                    mStrStartTime = mStrStartTime + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                    mStartDateTv.setTag(mStrStartTime);
                                                    mStartDateTv.setText(mStrStartTime);
                                                }
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            true
                                    );
                                    tpd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                                    tpd.show(getFragmentManager(), "Timepickerdialog");
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
                dpdStart.setAccentColor(getResources().getColor(R.color.cm_main_text));
                dpdStart.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mEndDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar now = Calendar.getInstance();
                dpdEnd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStrEndTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                                if (mAllDaySb.isChecked()) {
                                    mEndDateTv.setTag(mStrEndTime);
                                    mEndDateTv.setText(mStrEndTime);
                                } else {
                                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                    mStrEndTime = mStrEndTime + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                    mEndDateTv.setTag(mStrEndTime);
                                                    mEndDateTv.setText(mStrEndTime);
                                                }
                                            },
                                            now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            true
                                    );
                                    tpd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                                    tpd.show(getFragmentManager(), "Timepickerdialog");
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpdEnd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpdEnd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                dpdEnd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    /**
     * 重复提醒监听
     */
    private void initRRListener(int tag) {
        if (tag == 0) {
            mPopupRepeat.setOnScaleItemClickListener(new OnScalePopupItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        if (position != 7)
                            mTvRepeat.setText(mListRepeat.get(position));
                        mLlRepeatEnd.setVisibility(position == 1 ? View.GONE : View.VISIBLE);
                        switch (position) {
                            case 1:
                                cInfo.REPEATTYPE = 0;
                                cInfo.REPEATHZ = 0;
                                break;
                            case 2:
                                cInfo.REPEATTYPE = 1;
                                cInfo.REPEATHZ = 1;
                                break;
                            case 3:
                                cInfo.REPEATTYPE = 2;
                                cInfo.REPEATHZ = 1;
                                break;
                            case 4:
                                cInfo.REPEATTYPE = 2;
                                cInfo.REPEATHZ = 2;
                                break;
                            case 5:
                                cInfo.REPEATTYPE = 3;
                                cInfo.REPEATHZ = 1;
                                break;
                            case 6:
                                cInfo.REPEATTYPE = 4;
                                cInfo.REPEATHZ = 1;
                                break;
                            case 7:
                                Intent intent = new Intent(CalendarCreateActivitys.this, FreeRepeatActivity.class);
                                intent.putExtra("start_anim", false);
                                if (mTvRepeat.getText().toString().equals("自定")) {
                                    intent.putExtra("type", cInfo.REPEATTYPE);
                                    intent.putExtra("hz", cInfo.REPEATHZ);
                                    intent.putExtra("weekStr", cInfo.REPEATWEEKVAL);
                                }
                                startActivityForResult(intent, REQUESTCODE_REPEAT);
                                break;
                        }
                    }
                    mPopupRepeat.dismiss();
                }
            });
        } else if (tag == 1) {
            mPopupRepeatEnd.setOnScaleItemClickListener(new OnScalePopupItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 1) {
                        mTvRepeatEnd.setText(mListRepeatEnd.get(position));
                        cInfo.REPEATENDTIME = "";
                    } else if (position == 2) {
                        final Calendar now = Calendar.getInstance();
                        dpdRepeatEnd = DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                        mStrRepeatEnd = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                                new TimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                        mStrRepeatEnd = mStrRepeatEnd + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                        mTvRepeatEnd.setTag(mStrRepeatEnd);
                                                        mTvRepeatEnd.setText(mStrRepeatEnd);
                                                        cInfo.REPEATENDTIME = mStrRepeatEnd;
                                                    }
                                                },
                                                now.get(Calendar.HOUR_OF_DAY),
                                                now.get(Calendar.MINUTE),
                                                true
                                        );
                                        tpd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                                        tpd.show(getFragmentManager(), "Timepickerdialog");
                                    }
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpdRepeatEnd.setVersion(DatePickerDialog.Version.VERSION_2);
                        dpdRepeatEnd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                        dpdRepeatEnd.show(getFragmentManager(), "Datepickerdialog");
                    }
                    mPopupRepeatEnd.dismiss();
                }
            });
        } else if (tag == 2) {
            mPopupRemind.setOnScaleItemClickListener(new OnScalePopupItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        if (position != mListRemind.size()) {
                            mTvRemind.setText(mListRemind.get(position));
                            switch (position) {
                                case 1:
                                    cInfo.ISCALL = 0;
                                    break;
                                case 2:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 0;
                                    break;
                                case 3:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 5;
                                    break;
                                case 4:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 15;
                                    break;
                                case 5:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 60;
                                    break;
                                case 6:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 1440;
                                    break;
                                case 7:
                                    cInfo.ISCALL = 1;
                                    cInfo.AHEAD_MINUTE = 10080;
                                    break;
                            }
                        }
                    }
                    mPopupRemind.dismiss();
                }
            });
        }
    }

    /**
     * 初始化重复提醒数据
     */
    private void initRepeatRemindData() {

        mListRepeat = new ArrayList<>();
        mListRepeatEnd = new ArrayList<>();
        mListRemind = new ArrayList<>();

        mListRepeat.add("重复");
        mListRepeat.add("无");
        mListRepeat.add("每天");
        mListRepeat.add("每周");
        mListRepeat.add("每2周");
        mListRepeat.add("每月");
        mListRepeat.add("每年");
        mListRepeat.add("自定");

        mListRemind.add("提醒");
        mListRemind.add("无");
        mListRemind.add("准时");
        mListRemind.add("5分钟前");
        mListRemind.add("15分钟前");
        mListRemind.add("1小时前");
        mListRemind.add("1天前");
        mListRemind.add("1周前");

        mListRepeatEnd.add("重复截止");
        mListRepeatEnd.add("永不");
        mListRepeatEnd.add("于日期");
    }

    /**
     * 全天事件选择状态处理
     *
     * @param isChecked
     */
    private void setAllDayChecked(boolean isChecked) {

        // 全天时间开关
        mAllDaySb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                try {
                    setAllDayChecked(isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mAllDayLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mAllDaySb.toggle();
            }
        });

        try {
            if (isChecked) {
                String startTime = format2.format(mStartDate);
                mStartDateTv.setTag(startTime);
                mStartDateTv.setText(startTime);

                String endTime = format2.format(mEndDate);
                mEndDateTv.setTag(endTime);
                mEndDateTv.setText(endTime);
            } else {
                String startTime = format.format(mStartDate);
                mStartDateTv.setTag(startTime);
                mStartDateTv.setText(startTime);

                String endTime = format.format(mEndDate);
                mEndDateTv.setTag(endTime);
                mEndDateTv.setText(endTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理时间格式
     *
     * @param num
     * @return
     */
    private String checkNum(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
            /*getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
            getSwipeBackLayout().scrollToFinishActivity();*/
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNameEt.clearFocus();
        mDescEt.clearFocus();
        mContentEt.clearFocus();
    }
}
