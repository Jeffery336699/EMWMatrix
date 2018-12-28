package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.RelationFileListAdapter;
import cc.emw.mobile.entity.BusDataInfo;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SwitchButton;

/**
 * 动态·服务详情
 *
 * @author zrjt
 * @version 创建时间：2016-4-20 下午7:31:23
 */
@ContentView(R.layout.activity_service_detail)
public class ServiceDetailActivity extends BaseActivity {

    @ViewInject(R.id.et_phone_name)
    private TextView mTitleEt; //主题
    @ViewInject(R.id.et_phone_desc)
    private TextView mDescEt; //描述
    @ViewInject(R.id.sb_phone_allday)
    private SwitchButton mAllDaySb; //全天事件开关按钮
    @ViewInject(R.id.tv_phone_mainuser)
    private TextView mMainUserTv; //负责人文本
    @ViewInject(R.id.ll_phone_mainuser_head)
    private LinearLayout mMainUserHeadLayout;//负责人头像Layout
    @ViewInject(R.id.tv_phone_mainuser_num)
    private TextView mMainUserNum; //负责人数量
    @ViewInject(R.id.btn_phone_startdate)
    private TextView mStartDateTv; //开始日期文本
    @ViewInject(R.id.btn_phone_enddate)
    private TextView mEndDateTv; //结束日期文本
    @ViewInject(R.id.tv_phone_emergency)
    private TextView mEmergencyTv; //优先级文本
    @ViewInject(R.id.file_relation)
    private CollapseView mFileLayout;//附件
    @ViewInject(R.id.ll_date_nobusdata)
    private LinearLayout mNoBusDataLayout; //无业务数据Layout
    @ViewInject(R.id.ll_date_busdata)
    private LinearLayout mBusDataLayout; //业务数据Layout
    @ViewInject(R.id.tv_date_busname)
    private TextView mBusNameTv; //业务数据名称

    @ViewInject(R.id.schedule_tv_repeat)
    private TextView repeatInfoTv; // 重复的详情
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView txTextView; // 提醒的详情

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    private ApiEntity.UserSchedule userSchedule;
    private RelationFileListAdapter adapter;
    private ArrayList<ApiEntity.Files> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSchedule = (ApiEntity.UserSchedule) getIntent().getSerializableExtra("user_schedule");
        initView();
    }

    private void initView() {
        findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
        if (userSchedule != null) {
            if (userSchedule.UserID == PrefsUtil.readUserInfo().ID) {
                findViewById(R.id.cm_header_btn_edit).setVisibility(View.VISIBLE);
            }
            mTitleEt.setText(userSchedule.Title);
            mDescEt.setText(userSchedule.Remark);
            mAllDaySb.setChecked(userSchedule.Allday == 1);
            if (!TextUtils.isEmpty(userSchedule.MainUser) && TextUtils.isDigitsOnly(userSchedule.MainUser.split(",")[0])) {
                int uid = Integer.valueOf(userSchedule.MainUser.split(",")[0]);
                showMainUser(uid);
            }
            mStartDateTv.setText(userSchedule.StartTime);
            mEndDateTv.setText(userSchedule.OverTime);
            if (userSchedule.Priority > 0 && userSchedule.Priority - 1 < TaskDetailActivity.colorStr.length) {
                mEmergencyTv.setText(TaskDetailActivity.colorStr[userSchedule.Priority - 1]);
            } else {
                mEmergencyTv.setText(TaskDetailActivity.colorStr[0]);
            }
            initFileView(userSchedule);
            if (userSchedule.NoteAddType == 6) {
                try {
                    BusDataInfo busDataInfo = new Gson().fromJson(userSchedule.NoteAddPriority, BusDataInfo.class);
                    mBusNameTv.setText(busDataInfo != null? busDataInfo.Text : "");
                    mNoBusDataLayout.setVisibility(View.GONE);
                    mBusDataLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
            } else {
                mNoBusDataLayout.setVisibility(View.VISIBLE);
                mBusDataLayout.setVisibility(View.GONE);
            }
            showRepeatInfo(userSchedule);
        }
    }

    private void initFileView(ApiEntity.UserSchedule schedule) {
        mFileLayout.setContent(R.layout.activity_calendar_tag2);
        LinearLayout tagNew = (LinearLayout) mFileLayout.findViewById(R.id.tv_calendar_new_tag);
        tagNew.setVisibility(View.GONE);
        MyListView myListView = (MyListView) mFileLayout.findViewById(R.id.lv_calendar_tag);
        mFileLayout.setTagNameVis("eb05", "附件");
        try {
            Type type = new TypeToken<List<ApiEntity.Files>>() {}.getType();
            mDataList = new Gson().fromJson(schedule.NoteAddPriority, type);
        } catch (Exception e) {

        }
        adapter = new RelationFileListAdapter(this, mDataList);
        myListView.setAdapter(adapter);
        if (mDataList != null && mDataList.size() > 0) {
            mFileLayout.setEnabled(true);
            mFileLayout.setTitle(mDataList.size() + "个附件");
        } else {
            mFileLayout.setTitle("无");
            mFileLayout.setEnabled(false);
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right9, R.id.cm_header_btn_left9, R.id.cm_header_btn_edit})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:

                break;
            case R.id.cm_header_btn_edit:
                Intent intent = new Intent(this, ServiceCreateActivity.class);
                Gson gson = new Gson();
                CalendarInfo calendarInfo = gson.fromJson(gson.toJson(userSchedule), CalendarInfo.class);
                intent.putExtra("calendar_info", calendarInfo);
                startActivityForResult(intent, 11111);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 11111:
                    finish();
                    break;
            }
        }
    }

    private void showMainUser(int uid) {
//        mMainUserTv.setVisibility(View.GONE);
        mMainUserNum.setVisibility(View.GONE);
        mMainUserHeadLayout.setVisibility(View.VISIBLE);
        mMainUserHeadLayout.removeAllViews();
        ApiEntity.UserInfo userInfo = EMWApplication.personMap.get(uid);
        if (userInfo != null) {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
            TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mMainUserHeadLayout.addView(circleImageView, params);
        } else {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(uid), "", 30);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mMainUserHeadLayout.addView(circleImageView, params);
//            mMainUserHeadLayout.setVisibility(View.GONE);
//            mMainUserTv.setVisibility(View.VISIBLE);
            mMainUserNum.setVisibility(View.GONE);
        }
    }

    private void showMainUser(ArrayList<ApiEntity.UserInfo> moreUsers) {
//        mMainUserTv.setVisibility(View.GONE);
        mMainUserHeadLayout.setVisibility(View.VISIBLE);
        mMainUserHeadLayout.removeAllViews();
        if (moreUsers != null && moreUsers.size() > 0) {
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                if (userInfo != null) {
                    CircleImageView circleImageView = new CircleImageView(this);
                    circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                    TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mMainUserHeadLayout.addView(circleImageView, params);
                }
                if (i == 5) {
                    break;
                }
            }
//            mMainUserNum.setVisibility(View.VISIBLE);
            mMainUserNum.setText(moreUsers.size() + "");
        } else {
            mMainUserHeadLayout.setVisibility(View.GONE);
//            mMainUserTv.setVisibility(View.VISIBLE);
            mMainUserNum.setVisibility(View.GONE);
        }
    }


    /**
     * 重复提醒赋值
     */
    private void showRepeatInfo(ApiEntity.UserSchedule schedule) {
        String repeatTypeStr = ""; // 重复类型字符串
        int pinlv = schedule.REPEATHZ; // 频率
        int repeatType = schedule.REPEATTYPE; // 重复的类型
        String repeatWeekStr = TextUtils.isEmpty(schedule.REPEATWEEKVAL)? "" : schedule.REPEATWEEKVAL; // 重复周的提示
        int tiXing = schedule.AHEAD_MINUTE; // 提醒的时间
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

        String repeatInfoStr = pinlv + repeatTypeStr + " " + weekInfoScan;  // 重复的结果

        if (tiXingStr != null && !TextUtils.isEmpty(tiXingStr)) {
            txTextView.setText(tiXingStr);
        } else {
            txTextView.setText(getString(R.string.nope));
        }

        if (pinlv != 0) {
            repeatInfoTv.setText("每" + repeatInfoStr);
        }
    }
}
