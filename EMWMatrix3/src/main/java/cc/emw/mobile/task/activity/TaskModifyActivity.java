package cc.emw.mobile.task.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
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
import java.util.Locale;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.map.GeoFenceActivity;
import cc.emw.mobile.me.widget.ListPopup;
import cc.emw.mobile.me.widget.OnScalePopupItemClickListener;
import cc.emw.mobile.me.widget.ScalePopup;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.adapter.TaskTagAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.task.entity.UserLabelBean;
import cc.emw.mobile.task.factory.TaskTimePickerViewFactory;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskLableView;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by chengyong.liu on 2016/6/23.implements ITaskModifyView,ITaskLableView
 */
@ContentView(R.layout.activity_task_modify_3)
public class TaskModifyActivity extends BaseActivity implements ITaskModifyView, ITaskLableView {
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    @ViewInject(R.id.rl_task_modify_project_container)
    private RelativeLayout mLlProjectContainer;
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mLlWhoCanSeeContainer;//谁能看见条目
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_bar)
    private LinearLayout cm_header_bar;
    @ViewInject(R.id.et_taskmodify_name)
    private EditText mEtTitle;//项目名称
    @ViewInject(R.id.et_taskmodify_des)
    private EditText mEtDes;//项目描述
    @ViewInject(R.id.civ_task_modify_head_image)
    private CircleImageView mCivHeadImage;//头像
    @ViewInject(R.id.tv_task_modify_charge)
    private TextView mTvCharge;
    @ViewInject(R.id.task_modify_start_time)
    private TextView mTvStartTime;//开始时间
    @ViewInject(R.id.tv_task_modify_finish_time)
    private TextView mTvFinishTime;//结束时间
    @ViewInject(R.id.tv_task_modify_emergency)
    private TextView mTvEmergency;
    @ViewInject(R.id.tv_task_modify_project)
    private TextView mTvProject;//关联项目
    @ViewInject(R.id.v_task_modify_project)
    private View mModifyProjectLine;
    @ViewInject(R.id.tv_task_modify_attachment)
    private TextView mTvAttachment;
    @ViewInject(R.id.tv_task_modify_more_user)
    private TextView mTvMoreUser;
    @ViewInject(R.id.ll_task_modify_head_image_container)
    private LinearLayout mLlHeadImageContainer;//执行人头像
    @ViewInject(R.id.tv_task_modify_attachment_number)
    private TextView mTvAttachmentNum;
    @ViewInject(R.id.tv_task_modify_more_user_number)
    private TextView mTvMoreUserNum;
    @ViewInject(R.id.tv_task_modify_charge_number)
    private TextView mTvChargeNum;
    @ViewInject(R.id.tv_task_modify_location)
    private TextView tvMPostition;
    @ViewInject(R.id.tv_tag)
    private TextView mTvTag;
    @ViewInject(R.id.itv_task_view)
    private IconTextView itvTaskView;
    public static final int REQUESTCODE_ATTACHMENT = 1;
    public static final int REQUESTCODE_CHARGE = 2;
    public static final int REQUESTCODE_EXCUTOR = 3;
    public static final int REQUESTCODE_WORK_PROJECT = 6;
    public static final int REQUESTCODE_TASK_LABEL = 7;
    public static final String ACTION_MODIFY_TASK = "cc.emw.mobile.modify_task";// 修改任务发送广播频段
    public static final String IS_CHILD_MODIFY = "is_child_modify";
    public static final String ACTION_REFRESH_CALENDAR_TAG = "action_refresh_calendar_tag2";

    private List<UserLabelBean> mLables = new ArrayList<>();
    private ApiEntity.UserProject project;
    private ApiEntity.UserFenPai mUserFenPai;
    private SimpleDateFormat simpleDateFormat;
    private Dialog mLoadingDialog;
    private TaskPresenter mTaskPresenter;
    private boolean mIsChildModify;//是否是子任务的编辑界面  默认false
    private TimePickerView mStartPopupWindow;// 时间表弹窗
    private int date_type = 0;// 时间类型 0表示开始时间 1表示结束时间。
    private boolean mCanSee;
    private String tagNameNew;
    private OptionsPickerView<String> mOptionsPickerView;
    private MyBroadCastReceicer receicer;
    private TaskTagAdapter adapter;

    /////////////////////////////////////////////////////////////////////////////
    private DatePickerDialog dpdStart, dpdEnd; //开始/结束时间弹框
    private ScalePopup mPopupJJCD;  //紧急程度
    private List<String> mListJJCD;  //紧急程度数据
    private String mStrStartTime, mStrEndTime;
    private ListPopup mListPopupTag;    //标签弹框
    /////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        initData();
        initView();
        initOpionSelector();
        initTimeSelector();
        initCollapseView();
    }

    private void initData() {
        initJJCDData();
        initList();
        mTaskPresenter = new TaskPresenter(this);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.timeformat6), Locale.CHINA);
        mUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
        mIsChildModify = getIntent().getBooleanExtra(IS_CHILD_MODIFY, false);

        //绑定项目接收项目信息
        mCanSee = getIntent().getBooleanExtra(TaskCreateActivity.WHO_CAN_SEE, true);
        project = (ApiEntity.UserProject) getIntent().getSerializableExtra(TaskCreateActivity.TEAM_USERPROJECT);
        if (mUserFenPai != null && mUserFenPai.Rail != null && !("".equals(mUserFenPai.Rail.Address)) && mUserFenPai.RailID != 0) {
            tvMPostition.setText(mUserFenPai.Rail.Address);
        } else {
            tvMPostition.setHint("电子围栏");
        }

        getCalendarTag();
    }

    private void initCollapseView() {
        IntentFilter filter = new IntentFilter();
        receicer = new MyBroadCastReceicer();
        filter.addAction(ACTION_REFRESH_CALENDAR_TAG);
        registerReceiver(receicer, filter);

        /*if (mUserFenPai != null && !TextUtils.isEmpty(mUserFenPai.TaskLabel)) {
            tagNameNew = mUserFenPai.TaskLabel;
            taskTag.setTitle(mUserFenPai.TaskLabel);
        } else {
            taskTag.setTitle("#无");
        }
        mLables = new ArrayList<>();
        mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
        adapter = new TaskTagAdapter(this);
        adapter.setData(mLables);
        taskTag.setContent(R.layout.activity_calendar_tag2);
        MyListView myListView = (MyListView) taskTag.findViewById(R.id.lv_calendar_tag);
        LinearLayout tagNew = (LinearLayout) taskTag.findViewById(R.id.tv_calendar_new_tag);
        taskTag.setTagNameVis("e91a", "标签");
        myListView.setAdapter(adapter);
        adapter.setCollaspeView(taskTag);
        tagNew.setOnClickListener(new View.OnClickListener() {//新建任务
            @Override
            public void onClick(View v) {
                Intent labelCreate = new Intent(TaskModifyActivity.this, TaskCreateLabelActivity.class);
                startActivityForResult(labelCreate, REQUESTCODE_TASK_LABEL);
                overridePendingTransition(R.anim.popup_show, R.anim.activity_out);
            }
        });*/
    }


    private class MyBroadCastReceicer extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
            String tag = intent.getStringExtra("tag");
            if (tag != null) {
//                taskTag.setTitle(tag);
                mTvTag.setText(tag);
            }
//            taskTag.rotateArrow();
        }
    }

    @Override
    public void getUserLable(List<UserLabelBean> labels) {
        mLables.clear();
        initList();
        mLables.addAll(labels);
        if (tagNameNew != null) {
            for (int i = 0; i < mLables.size(); i++) {
                if (tagNameNew.equals(mLables.get(i).Name)) {
                    adapter.getLastPosition(i);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addUserLable(String s) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if (Integer.parseInt(s) > 1) {
            ToastUtil.showToast(this, "添加标签成功!", R.drawable.tishi_ico_gougou);
            mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
//            taskTag.rotateArrow();
        } else {
            ToastUtil.showToast(this, "添加标签失败!");
        }
    }

    @Override
    public void modifyUserLable(String s) {
    }

    @Override
    public void delUserLable(String s) {
    }

    /************************************************************************************************************/
    private void initView() {
        /*cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
        mHeaderTitleTv.setText("编辑任务");*/
        itvTaskView.setVisibility(View.GONE);
        mLlWhoCanSeeContainer.setVisibility(View.GONE);//隐藏谁能看见

        //设置项目名称、项目描述
        mEtTitle.setText(mUserFenPai.Title);
        mEtTitle.setSelection(mUserFenPai.Title.length());
        mEtDes.setText(mUserFenPai.Mark);
        mEtDes.setSelection(mUserFenPai.Mark.length());
//        mTvEmergency.setTextColor(getResources().getColor(R.color.task_modify_charge));
        //设置负责人
        mCivHeadImage.setVisibility(View.VISIBLE);
        String mainUser = mUserFenPai.MainUser;
        List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
        if (mainUsers != null && mainUsers.size() != 0) {
            ApiEntity.UserInfo userInfo = mainUsers.get(0);
            mCivHeadImage.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
            TaskUtils.setCivImageView(userInfo.Image, mCivHeadImage);
            /*mTvCharge.setText(userInfo.Name);
            mTvCharge.setTextColor(getResources().getColor(R.color.task_modify_charge));
            mTvChargeNum.setVisibility(View.VISIBLE);
            mTvChargeNum.setText(mainUsers.size() + "");*/
        }
        //设置开始时间
//        mTvStartTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
        mTvStartTime.setText(mUserFenPai.StartTime);
        //设置结束时间
//        mTvFinishTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
        mTvFinishTime.setText(mUserFenPai.FinishTime);
        //设置紧急程度
        int yxj = mUserFenPai.Yxj;
        if (yxj > 0) {
            mTvEmergency.setText(TaskDetailActivity.colorStr[yxj - 1]);
        }
        //设置关联项目是否展示 子任务的编辑界面不需要选择关联项目
        if (mIsChildModify) {
            mLlProjectContainer.setVisibility(View.GONE);
            mModifyProjectLine.setVisibility(View.GONE);
        }
        //设置关联项目
        mTvProject.setText(TaskDetailActivity.mRelationToProjectName);
        mTvProject.setBackgroundResource(R.drawable.task_bg);
        //初始化附件
        mTvAttachment.setTextColor(getResources().getColor(R.color.task_modify_charge));
        int atachmentNum = TaskUtils.getStringID(mUserFenPai.Files).length;
        mTvAttachmentNum.setText(atachmentNum + "");
        mTvAttachmentNum.setVisibility(atachmentNum == 0 ? View.INVISIBLE : View.VISIBLE);
        //初始化执行人
        showMoreUser();
    }

    private void showMoreUser() {
//        mTvMoreUser.setVisibility(View.GONE);
        mLlHeadImageContainer.setVisibility(View.VISIBLE);
        String moreUser = mUserFenPai.MoreUser;
        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers(moreUser);
        mLlHeadImageContainer.removeAllViews();
        if (moreUsers != null && moreUsers.size() != 0) {
            mTvMoreUserNum.setVisibility(View.VISIBLE);
            mTvMoreUserNum.setText(moreUsers.size() + "");
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                CircleImageView circleImageView = new CircleImageView(this);
                circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                TaskUtils.setCivImageView(userInfo.Image, circleImageView);
                params.leftMargin = DisplayUtil.dip2px(this, 5);
                params.width = DisplayUtil.dip2px(this, 30);
                params.height = DisplayUtil.dip2px(this, 30);
                mLlHeadImageContainer.addView(circleImageView, params);
                if (i == 4) {
                    break;
                }
            }
            mTvMoreUserNum.setVisibility(moreUsers.size() > 5 ? View.VISIBLE : View.GONE);
            mTvMoreUserNum.setText(moreUsers.size() + "");
        } else {
            mTvMoreUserNum.setVisibility(View.GONE);
            mLlHeadImageContainer.setVisibility(View.GONE);
            /*mTvMoreUser.setVisibility(View.VISIBLE);
            mTvMoreUser.setTextColor(getResources().getColor(R.color.task_modify_charge));*/
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event({R.id.ll_tag_layout, R.id.rl_task_modify_charge_container, R.id.ll_task_modify_moreuser_container, R.id.ll_task_modify_emergency_container,
            R.id.ll_task_modify_attachment_container, R.id.ll_task_modify_location_container, R.id.cm_header_btn_left9,
            R.id.ll_task_modify_start_time_container, R.id.ll_task_modify_finish_time_container, R.id.cm_header_tv_right9,})
    private void onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_tag_layout:
                ListPopup.Builder builder = new ListPopup.Builder(this);
                for (int i = 0; i < mLables.size(); i++) {
                    builder.addItem(mLables.get(i).Name);
                }
                mListPopupTag = builder.build();
                mListPopupTag.showPopupWindow();
                mListPopupTag.setOnListPopupItemClickListener(new ListPopup.OnListPopupItemClickListener() {
                    @Override
                    public void onItemClick(int what) {
                        mUserFenPai.TaskLabel = mLables.get(what).Name;
                        mTvTag.setText(mLables.get(what).Name);
                        mListPopupTag.dismiss();
                    }
                });
                break;
            case R.id.ll_task_modify_location_container:
                Intent locationIntent = new Intent(this, GeoFenceActivity.class);
                if (mUserFenPai.Rail != null && !("".equals(mUserFenPai.Rail)) && mUserFenPai.RailID != 0) {
                    locationIntent.putExtra(GeoFenceActivity.USER_RAIL, mUserFenPai.Rail);
                }
                startActivityForResult(locationIntent, 144);
                break;
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.rl_task_modify_charge_container:
                //跳转到负责人界面
                Intent charge = new Intent(this, TaskMemberActivity.class);
                charge.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
                charge.putExtra(TaskMemberActivity.MEMBERTYPE, TaskMemberActivity.CHARGE);
                charge.putExtra(TaskMemberActivity.TASK_MEMBER_REQUEST_TYPE, TaskMemberActivity.Member_new);
                if (!mCanSee) {
                    charge.putExtra(TaskMemberActivity.FITER_TEAM_LIST, getFiterTeamList());
                }
                charge.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                charge.putExtra("click_pos_y", location[1]);
                startActivityForResult(charge, REQUESTCODE_CHARGE);
                break;
            case R.id.ll_task_modify_moreuser_container:
                //跳转到执行人界面
                Intent excutor = new Intent(this, TaskMemberActivity.class);
                excutor.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
                excutor.putExtra(TaskMemberActivity.MEMBERTYPE, TaskMemberActivity.EXCUTOR);
                excutor.putExtra(TaskMemberActivity.TASK_MEMBER_REQUEST_TYPE, TaskMemberActivity.Member_new);
                if (!mCanSee) {
                    excutor.putExtra(TaskMemberActivity.FITER_TEAM_LIST, getFiterTeamList());
                }
                excutor.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                excutor.putExtra("click_pos_y", location2[1]);
                startActivityForResult(excutor, REQUESTCODE_EXCUTOR);
                break;
            case R.id.ll_task_modify_emergency_container:
                //打开紧急程度
                HelpUtil.hideSoftInput(this, mEtDes);
//                mOptionsPickerView.show();
                mPopupJJCD = new ScalePopup(this, mListJJCD);
                mPopupJJCD.showPopupWindow();
                mPopupJJCD.setOnScaleItemClickListener(new OnScalePopupItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            mUserFenPai.Yxj = position;
                            // 并且显示在紧急控件中
                            mTvEmergency.setText(mListJJCD.get(position));
                            mPopupJJCD.dismiss();
                        }
                    }
                });
                break;
            case R.id.ll_task_modify_attachment_container:
                Intent repositoryIntent = new Intent(this, FileSelectActivity.class);
                StringBuilder strFile = new StringBuilder();
                strFile.append("[").append(mUserFenPai.Files).append("]");
                repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, strFile.toString());
                repositoryIntent.putExtra("start_anim", false);
                int[] location3 = new int[2];
                v.getLocationOnScreen(location3);
                repositoryIntent.putExtra("click_pos_y", location3[1]);
                startActivityForResult(repositoryIntent, REQUESTCODE_ATTACHMENT);
                /**
                 * 跳转附件逻辑变更   将原有的AttachmentActivity替换成FileSelectActivity类。废弃原来逻辑
                 */
//                //跳转到附件界面
//                Intent attachment = new Intent(this, AttachmentActivity.class);
//                attachment.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
//                attachment.putExtra(AttachmentActivity.ATTACHMENT_NEW_OR_MODIFY, AttachmentActivity.attachment_modify);
//                startActivityForResult(attachment, REQUESTCODE_ATTACHMENT);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
                break;
            case R.id.ll_task_modify_start_time_container:
                // 开始时间的点击事件，底部弹出时间表
                date_type = 0;
//                mStartPopupWindow.show();
                HelpUtil.hideSoftInput(TaskModifyActivity.this, mTvStartTime);
                final Calendar now = Calendar.getInstance();
                dpdStart = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStrStartTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                mStrStartTime = mStrStartTime + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                mTvStartTime.setText(mStrStartTime);
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
                dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
                dpdStart.setAccentColor(getResources().getColor(R.color.cm_main_text));
                dpdStart.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.ll_task_modify_finish_time_container:
                //跳转到时间选择界面
                date_type = 1;
//                mStartPopupWindow.show();
                HelpUtil.hideSoftInput(TaskModifyActivity.this, mTvFinishTime);
                final Calendar nowEnd = Calendar.getInstance();
                dpdEnd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStrEndTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);

                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                mStrEndTime = mStrEndTime + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                mTvFinishTime.setText(mStrEndTime);
                                            }
                                        },
                                        nowEnd.get(Calendar.HOUR_OF_DAY),
                                        nowEnd.get(Calendar.MINUTE),
                                        true
                                );
                                tpd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                                tpd.show(getFragmentManager(), "Timepickerdialog");
                            }
                        },
                        nowEnd.get(Calendar.YEAR),
                        nowEnd.get(Calendar.MONTH),
                        nowEnd.get(Calendar.DAY_OF_MONTH)
                );
                dpdEnd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpdEnd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                dpdEnd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.cm_header_tv_right9:
                //修改完成按钮
                //获取各种数据 比如title 描述等
                if (TextUtils.isEmpty(mEtTitle.getText().toString().trim())) {
                    ToastUtil.showToast(this, getString(R.string.allot_empty_taskname));
                    return;
                }
                mUserFenPai.Title = mEtTitle.getText().toString().trim();
                mUserFenPai.Mark = mEtDes.getText().toString().trim();
                //开始时间
                String statTime = mTvStartTime.getText().toString().trim();
                if ("开始日期".equals(statTime)) {
                    ToastUtil.showToast(this, R.string.task_modify_beginTime);
                    return;
                }
                mUserFenPai.StartTime = statTime;
                String endTime = mTvFinishTime.getText().toString().trim();
                if ("结束日期".equals(endTime)) {
                    ToastUtil.showToast(this, R.string.task_modify_endtime);
                    return;
                }
                mUserFenPai.FinishTime = endTime;
                if ("紧急程度".equals(mTvEmergency.getText())) {
                    ToastUtil.showToast(this, "请选择紧急程度!");
                    return;
                }
                mUserFenPai.TaskLabel = mTvTag.getText().toString();
                //默认设置标签为无，如果没有选择标签的话
                if (mUserFenPai.TaskLabel == null) {
                    mUserFenPai.TaskLabel = "无";
                }
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
                mTaskPresenter.modifyTask(mUserFenPai);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 144:
                    ApiEntity.UserRail userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
                    int id = data.getIntExtra("UserRailId", 0);
                    if (userRail != null && id != 0) {
                        if (mUserFenPai.RailID == 0)
                            mUserFenPai.RailID = id;
                        mUserFenPai.Rail = userRail;
                        tvMPostition.setText(userRail.Address);
                    }
                    break;
                case REQUESTCODE_ATTACHMENT:
                    ArrayList<ApiEntity.Files> fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    mUserFenPai.Files = TaskUtils.getRepositoryArray(fileRets);
                    if (fileRets != null) {
                        mTvAttachmentNum.setVisibility(fileRets.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                    }
                    mTvAttachmentNum.setText(fileRets.size() + "");
                    mTvAttachment.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    /**
                     * 返回文件逻辑变更。废弃原有的逻辑
                     */
//                    ArrayList<ApiEntity.Files> list = (ArrayList<ApiEntity.Files>) data .getSerializableExtra("attachment_result");
//                    mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
//                    if (list != null) {
//                        mTvAttachmentNum.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
//                    }
//                    mTvAttachmentNum.setText(list.size() + "");
//                    mTvAttachment.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    break;
                case REQUESTCODE_CHARGE:
                    ArrayList<ApiEntity.UserInfo> list1 = (ArrayList<ApiEntity.UserInfo>) data
                            .getSerializableExtra("member_result");
                    /*mTvChargeNum.setVisibility(View.VISIBLE);
                    mTvChargeNum.setText(list1.size() + "");*/
                    //返回来的数据至少有一条
                    //头像
                    mCivHeadImage.setVisibility(View.VISIBLE);
                    mCivHeadImage.setTextBg(EMWApplication.getIconColor(list1.get(0).ID), list1.get(0).Name, 30);
                    TaskUtils.setCivImageView(list1.get(0).Image, mCivHeadImage);
                    mTvCharge.setText(list1.get(0).Name);
                    mTvCharge.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    mUserFenPai.MainUser = TaskUtils.members2string(list1);
                    break;
                case REQUESTCODE_EXCUTOR:
                    ArrayList<ApiEntity.UserInfo> list2 = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("member_result");
                    if (list2 != null) {
                        mTvMoreUserNum.setVisibility(list2.size() == 0 ? View.GONE : View.VISIBLE);
                    }
                    mUserFenPai.MoreUser = TaskUtils.members2string(list2);
                    showMoreUser();
                    break;
                case REQUESTCODE_WORK_PROJECT:
                    //获取到回传的项目
                    ApiEntity.UserProject userProject = (ApiEntity.UserProject) data
                            .getSerializableExtra(WorkProjectActivity.WORK_PROJECT);
                    //UI展示
                    mTvProject.setText(userProject.Name);
                    mUserFenPai.ProjectId = userProject.ID;
                    mUserFenPai.ProjectElem.Name = userProject.Name;
                    break;
                case REQUESTCODE_TASK_LABEL:
                    tagNameNew = (String) data.getSerializableExtra(TaskCreateLabelActivity.TASK_LABEL_NAME);
                    UserLabelBean userlabel = new UserLabelBean();
                    userlabel.Name = tagNameNew;
                    mTvTag.setText(tagNameNew);
                    mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                    mLoadingDialog.show();
                    mTaskPresenter.addUserLable(userlabel);
                    break;
            }
        }
    }

    /**
     * 获取关联项目的相关人员集合
     *
     * @return
     */
    private ArrayList<ApiEntity.UserInfo> getFiterTeamList() {
        ArrayList<ApiEntity.UserInfo> users = new ArrayList<>();
        if (project.ID != 0) {
            users.addAll(TaskUtils.getUsers(project.MainUser));
            users.addAll(TaskUtils.getUsers(project.Users));
        }
        return users;
    }

    private void initOpionSelector() {
        // 紧急程度滚轴
        ArrayList<String> sorts = new ArrayList<>();
        sorts.add(TaskConstant.TaskEmergencyState.NORMAL);
        sorts.add(TaskConstant.TaskEmergencyState.EMERGENCY);
        sorts.add(TaskConstant.TaskEmergencyState.VERY_EMERGENCY);
        mOptionsPickerView = new OptionsPickerView<>(this);
        mOptionsPickerView.setPicker(sorts);
        mOptionsPickerView.setTitle(getString(R.string.task_modify_emergenct));
        mOptionsPickerView.setCancelable(true);
        mOptionsPickerView.setCyclic(false);// 无限循环

        if (mUserFenPai.Yxj > 0) {
            mOptionsPickerView.setSelectOptions(mUserFenPai.Yxj - 1);
        }
        mOptionsPickerView
                .setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        // 一级联动
                        // 将当前的条目信息返回
                        mUserFenPai.Yxj = options1 + 1;
                        // 并且显示在紧急控件中
                        mTvEmergency.setText(TaskDetailActivity.colorStr[options1]);
//                        mTvEmergency.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    }
                });
    }

    private void initTimeSelector() {
        mStartPopupWindow = TaskTimePickerViewFactory.createTimeSelector(this);
        mStartPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                updateDiaplay(date);
            }
        });
    }

    private void updateDiaplay(Date date) {
        try {
            if (date_type == 0) {
                String endTime = mTvFinishTime.getText().toString().trim();
                if ("结束日期".equals(endTime)) {
                    mTvStartTime.setText(simpleDateFormat.format(date));
                } else {
                    Date d = simpleDateFormat.parse(endTime);
                    if (d.getTime() > date.getTime()) {
                        mTvStartTime.setText(simpleDateFormat.format(date));
                    } else {
                        ToastUtil.showToast(this, R.string.task_modify_endtime_compare_starttime);
                    }
                }
            } else {
                String startTime = mTvStartTime.getText().toString().trim();
                if (getString(R.string.plan_time_hint).equals(startTime)) {
                    mTvFinishTime.setText(simpleDateFormat.format(date));
                } else {
                    Date d = simpleDateFormat.parse(mTvStartTime.getText().toString());
                    if (d.getTime() < date.getTime()) {
                        mTvFinishTime.setText(simpleDateFormat.format(date));
                    } else {
                        ToastUtil.showToast(this, R.string.task_modify_endtime_compare_starttime);
                    }
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化紧急程度数据
     */
    private void initJJCDData() {
        mListJJCD = new ArrayList<>();
        mListJJCD.add("紧急程度");
        mListJJCD.add("普通");
        mListJJCD.add("紧急");
        mListJJCD.add("非常紧急");
    }

    private void initList() {
        UserLabelBean tag0 = new UserLabelBean();
        UserLabelBean tag1 = new UserLabelBean();
        UserLabelBean tag2 = new UserLabelBean();
        UserLabelBean tag3 = new UserLabelBean();
        UserLabelBean tag4 = new UserLabelBean();
        UserLabelBean tag5 = new UserLabelBean();
        UserLabelBean tag6 = new UserLabelBean();
        UserLabelBean tag7 = new UserLabelBean();
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
        API.TalkerAPI.GetUserLabel(PrefsUtil.readUserInfo().ID, new RequestCallback<UserLabelBean>(UserLabelBean.class) {

            @Override
            public void onParseSuccess(List<UserLabelBean> respList) {
                if (respList != null && respList.size() > 0) {
                    mLables.clear();
                    initList();
                    mLables.addAll(respList);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(TaskModifyActivity.this, "获取用户自定义标签失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void modifyTask(String s) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if ("1".equals(s)) {
            ToastUtil.showToast(this, R.string.task_modify_edit_task_success, R.drawable.tishi_ico_gougou);
            Intent intent = new Intent();
            intent.setAction(ACTION_MODIFY_TASK);
            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
            sendBroadcast(intent);
            finish();
        } else {
            ToastUtil.showToast(this, R.string.task_modify_edit_task_error);
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        ToastUtil.showToast(this, R.string.task_modify_edit_task_error);
    }

    @Override
    public void createTask(String respInfo) {
    }

    @Override
    public void getFileList(List<ApiEntity.Files> files) {
    }

    @Override
    public void completeFresh() {
    }

    /****************************
     * 生命周期方法
     *********************************/
    @Override
    protected void onPause() {
        super.onPause();
        HelpUtil.hideSoftInput(this, mEtTitle);
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
}
