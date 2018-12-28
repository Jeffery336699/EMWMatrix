package cc.emw.mobile.calendar;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.fragment.CalendarDayFragment;
import cc.emw.mobile.calendar.fragment.CalendarMonthFragment;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.main.fragment.worker.CalendarFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.view.AnimatedColorPickerDialog;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.PersonTextView;
import cc.emw.mobile.view.SwitchButton;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.google.gson.Gson;

/**
 * @author zrjt
 * @version 创建时间：2016-4-20 下午7:31:23
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_create)
public class CalendarCreateActivitys extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_left)
    private TextView mHeaderCancelTv; // 顶部条取消
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSendTv; // 顶部条发布

    @ViewInject(R.id.cm_input_et_content)
    private EditText mContentEt; // 内容
    @ViewInject(R.id.et_calendarcreate_name)
    private EditText mNameEt; // 名称
    @ViewInject(R.id.et_calendarcreate_desc)
    private EditText mDescEt; // 描述
    @ViewInject(R.id.schedule_btn_startdate)
    private Button mStartDateBtn; // 开始日期按钮
    @ViewInject(R.id.schedule_btn_enddate)
    private Button mEndDateBtn; // 结束日期按钮
    @ViewInject(R.id.ll_calendar_color)
    private LinearLayout mLlColor; // 颜色Layout
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
    private TextView txEndTextView; // 提醒的详情

    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mSelectRootLayout; // 选择分享人员根Layout
    @ViewInject(R.id.cm_select_tv_select)
    private TextView mSelectTv; // 分享范围
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout
    @ViewInject(R.id.cm_ll_worker)
    private LinearLayout mWorkerLayout; // 同步到工作台根Layout
    @ViewInject(R.id.cm_sb_worker)
    private SwitchButton mWorkerSb; // 同步到工作台开关

    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private TimePickerView mStartPopupWindow, mEndPopupWindow;// 开始、结束时间popupwindow
    private Date mCurDate, mStartDate, mEndDate; // 当前、开始、结束日期
    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    public static final int TIXING_REQUEST = 21;
    public static final int TIXING_REQUEST_END = 22;

    private int type = -1; // 重复的类型
    private int fixType = -1; // 固定的类型
    private int pinlv; // 重复的频率
    private int fixPinlv; // 固定重复的频率
    private String typeStr; // 类型的字符串
    private String repeatInfoStr; // 重复的结果
    private String weekStr; // 重复的周中文模式
    private String weekNum; // 重复的周数字模式
    private String fixRepeatInfoStr; // 固定的重复时间
    private String fixTiXingInfoStr; // 固定的提醒时间
    private String tixingEndStr;
    private int[] colors;   // 日程颜色集合
    private int colorWhat = 0;  // 日程的颜色
    private int enterFlag;
    private Calendar mClickDate;
    private SimpleDateFormat format, format2, format3, format4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterFlag = getIntent().getIntExtra("enter_flag", 0);
        mClickDate = (Calendar) getIntent().getSerializableExtra(CalendarFragment.CREATEDATE);
        init();
    }

    private void init() {
        mHeaderCancelTv.setVisibility(View.VISIBLE);
        mHeaderCancelTv.setText(R.string.cancel);
        mHeaderTitleTv.setText(R.string.dynamic_more_schedule);
        mHeaderSendTv.setText(R.string.publish);
        mHeaderSendTv.setVisibility(View.VISIBLE);
        mLoadingDialog = createLoadingDialog(R.string.excuseing);

        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1), getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3), getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5)};

        mContentEt.setVisibility(enterFlag == 1 ? View.VISIBLE : View.GONE);
        mSelectRootLayout.setVisibility(enterFlag == 1 ? View.VISIBLE
                : View.GONE);
        mWorkerLayout.setVisibility(enterFlag == 1 ? View.VISIBLE : View.GONE);

        // typeStr = getIntent().getStringExtra("type");
        format = new SimpleDateFormat(
                getString(R.string.timeformat6));
        format2 = new SimpleDateFormat(
                getString(R.string.timeformat5));
        format3 = new SimpleDateFormat(
                getString(R.string.timeformat13));
        format4 = new SimpleDateFormat(
                getString(R.string.timeformat9));
        mCurDate = new Date();
        mStartDate = mCurDate;
        if (mClickDate != null) {
            mCurDate = mClickDate.getTime();
            mStartDate = mClickDate.getTime();
        }
        String time = format.format(mCurDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurDate);
        calendar.add(Calendar.HOUR, 1);
        mEndDate = calendar.getTime();
        String endtime = format.format(mEndDate);
        mStartDateBtn.setTag(time);
        mStartDateBtn.setText(time);
        mEndDateBtn.setTag(endtime);
        mEndDateBtn.setText(endtime);

        mStartPopupWindow = new TimePickerView(this, Type.ALL);// 时间选择器
        mStartPopupWindow.setTitle(getString(R.string.beg_time));
        mStartPopupWindow.setCancelable(true);
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
        mEndDateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndPopupWindow.show();
            }
        });

        setAllDayChecked(mAllDaySb.isChecked());

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
        mWorkerLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mWorkerSb.toggle();
            }
        });
        Drawable left = getResources().getDrawable(R.drawable.share_circle1_nor);
        left.setBounds(0, 0, DisplayUtil.dip2px(this, 25), DisplayUtil.dip2px(this, 25));
        mTvColorSelect.setCompoundDrawables(left, null, null, null);
        mLlColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AnimatedColorPickerDialog.Builder(CalendarCreateActivitys.this).setTitle("选择一种颜色").setColors(colors).setOnColorClickListener(new AnimatedColorPickerDialog.ColorClickListener() {
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
                            left.setBounds(0, 0, DisplayUtil.dip2px(CalendarCreateActivitys.this, 25), DisplayUtil.dip2px(CalendarCreateActivitys.this, 25));
                            mTvColorSelect.setCompoundDrawables(left, null, null, null);
                        }
                    }
                }).create().show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mStartPopupWindow != null && mStartPopupWindow.isShowing()) {
            mStartPopupWindow.dismiss();
            // mDateLayout.setBackgroundResource(R.drawable.schedule_date_bg1);
        } else if (mEndPopupWindow != null && mEndPopupWindow.isShowing()) {
            mEndPopupWindow.dismiss();
            // mDateLayout.setBackgroundResource(R.drawable.schedule_date_bg1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNameEt.clearFocus();
        mDescEt.clearFocus();
        mContentEt.clearFocus();
    }

    // else if (mRepeatSb.isChecked() && mRepeatTimeTv.getTag() == null) {
    // Toast.makeText(this, "请先选择结束日期！", Toast.LENGTH_SHORT).show();
    // }
    @Event(value = {R.id.cm_header_tv_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_tv_left:
                HelpUtil.hideSoftInput(this, mContentEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
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

    @Event(value = {R.id.cm_select_ll_select, R.id.schedule_ll_repeat,
            R.id.ll_calendar_tixing, R.id.ll_calendar_tixing_end})
    private void onSelectClick(View v) {

        switch (v.getId()) {
            case R.id.cm_select_ll_select:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
                        ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                startActivityForResult(intent, 2);
                break;
            case R.id.schedule_ll_repeat:
                Intent reIntent = new Intent(this, CalendarRepeatActivity.class);
                startActivity(reIntent);
                break;
            case R.id.ll_calendar_tixing:
                Intent txIntent = new Intent(this, CalendarTxActivity.class);
                startActivityForResult(txIntent, TIXING_REQUEST);
                break;
            case R.id.ll_calendar_tixing_end:
                Intent txEndIntent = new Intent(this, CalendarTxEndActivity.class);
                startActivityForResult(txEndIntent, TIXING_REQUEST_END);
                break;
        }

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

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    mSelectFlowLayout.removeAllViews();
                    selectList = (ArrayList<UserInfo>) data
                            .getSerializableExtra("select_list");
                    for (UserInfo user : selectList) {
                        addPersonItem(user);
                    }
                    if (selectList.size() > 0) {
                        mSelectTv.setHint("");
                    } else {
                        mSelectTv.setHint(R.string.share_range_hint);
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
     * 代提交的日程对象
     *
     * @return
     */
    private CalendarInfo getCInfo() {
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
        if (enterFlag == 1) {
            cInfo.IsJobSync = mWorkerSb.isChecked() ? 1 : 0;
        } else {
            cInfo.IsJobSync = 1;
        }

        cInfo.Title = mNameEt.getText().toString();
        cInfo.Remark = mDescEt.getText().toString();
        cInfo.StartTime = mStartDateBtn.getTag().toString();
        cInfo.OverTime = mEndDateBtn.getTag().toString();
        cInfo.Color = colorWhat;
        cInfo.Allday = mAllDaySb.isChecked() ? 1 : 0;
        cInfo.Line_Project = "[]";
        cInfo.Line_Task = "[]";
        cInfo.Line_File = "[]";
        if (cInfo.Allday == 1) {
            String endTime = mEndDateBtn.getTag().toString();
            endTime = endTime + "23:59:59";
            cInfo.OverTime = endTime;
        }
        cInfo.Priority = 1;
        cInfo.State = 1;
        cInfo.Type = 1;

        // 重复的类型
        if (type != -1) {
            cInfo.REPEATTYPE = type + 1;
        }
        if (fixType != -1) {
            cInfo.REPEATTYPE = fixType + 1;
        }

        // 重复的频率
        if (pinlv != 0) {
            cInfo.REPEATHZ = pinlv;
        }
        if (fixPinlv != 0) {
            cInfo.REPEATHZ = fixPinlv;
        }

        // 重复的星期值
        if (weekNum != null) {
            cInfo.REPEATWEEKVAL = weekNum;
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
            cInfo.ISCALL = 0;
        }

        // 提醒的终止时间
        if (tixingEndStr != null
                && !tixingEndStr.equals(getString(R.string.never))) {
            cInfo.REPEATENDTIME = tixingEndStr;
        } else {
            cInfo.REPEATENDTIME = null;
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
        return cInfo;
    }

    /**
     * 发布日程
     */
    private void send() {
        cInfo = getCInfo();
        API.TalkerAPI.AddCalendar(cInfo, new RequestCallback<CalendarInfo>(
                CalendarInfo.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                mLoadingDialog.dismiss();
                Toast.makeText(CalendarCreateActivitys.this,
                        getString(R.string.service_excep) + arg0.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String arg0) {
                mLoadingDialog.dismiss();
                if (Integer.valueOf(arg0) > 0) {
                    Toast.makeText(CalendarCreateActivitys.this,
                            getString(R.string.groupcreate_success),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(
                            CalendarDayFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    sendBroadcast(intent); // 刷新日程列表
                    intent = new Intent(
                            CalendarMonthFragment.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    sendBroadcast(intent); // 刷新日程月视图列表
                    intent = new Intent(
                            DynamicFragment.ACTION_REFRESH_HOME_LIST);
                    sendBroadcast(intent); // 刷新动态列表
                    onBackPressed();
                } else {
                    Toast.makeText(CalendarCreateActivitys.this,
                            getString(R.string.groupcreate_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
        PersonTextView childView = new PersonTextView(this);
        childView.setTag(user);
        childView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectFlowLayout.removeView(v);
                selectList.remove((UserInfo) v.getTag());
                if (selectList.size() == 0) {
                    mSelectTv.setHint(R.string.share_range_hint);
                }
            }
        });
        childView.setText(user.Name);
        mSelectFlowLayout.addView(childView);
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

}
