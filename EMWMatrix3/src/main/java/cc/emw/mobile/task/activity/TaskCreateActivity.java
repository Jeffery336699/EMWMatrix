package cc.emw.mobile.task.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Locale;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.map.GeoFenceActivity;
import cc.emw.mobile.me.widget.ListPopup;
import cc.emw.mobile.me.widget.OnScalePopupItemClickListener;
import cc.emw.mobile.me.widget.ScalePopup;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.task.adapter.TaskTagAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.task.entity.UserLabelBean;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SwipeBackScrollView;

/**
 * Created by chengyong.liu on 2016/6/24.
 * 任务创建
 */
@ContentView(R.layout.activity_task_create)
public class TaskCreateActivity extends BaseActivity implements ITaskModifyView {
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    @ViewInject(R.id.cm_input_et_content)
    private EditText mContentEt;
    @ViewInject(R.id.rl_task_modify_project_container)
    private RelativeLayout mRlProjectContainer;
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
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mRlCanSeeContainer;
    @ViewInject(R.id.cm_select_tv_name)
    private TextView mTvCanSee;
    @ViewInject(R.id.tv_task_modify_location)
    private TextView tvMPostition;
    @ViewInject(R.id.itv_task_view)
    private IconTextView itvTaskView;
    @ViewInject(R.id.swipeback_scrollview)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.tv_tag)
    private TextView mTvTag;
    public static final int REQUESTCODE_ATTACHMENT = 1;
    public static final int REQUESTCODE_CHARGE = 2;
    public static final int REQUESTCODE_EXCUTOR = 3;
    public static final int REQUESTCODE_WORK_PROJECT = 6;
    public static final int REQUESTCODE_SELECT_PERSON = 7;
    public static final int REQUESTCODE_TASK_LABEL = 8;                                         //新增任务
    public static final String ACTION_TASK_CREATE_SUCCESS = "action_task_create_success"; //新建、修改或删除任务成功action
    public static final String ACTION_REFRESH_CALENDAR_TAG = "action_refresh_calendar_tag";
    public static final String PARENT_USERFENPAI = "parent_userfenpai";
    public static final String TEAM_USERPROJECT = "team_userpoject";
    public static final String ACTION_CREATE_CHILD_TASK = "cc.emw.mobile.create_child_task";//创建子任务发送广播频段
    public static final String WHO_CAN_SEE = "who_can_see";//用于标记是否展示谁能看见
    private boolean mCanSee;//默认不展示谁能看见
    private int date_type = 0;// 时间类型 0表示开始时间 1表示结束时间。
    private String ul;//用于储存新创建的标签名称
    private ArrayList<ApiEntity.UserInfo> selectList;
    private List<UserLabelBean> mLables = new ArrayList<>();
    private ApiEntity.UserProject mUserProject;//团队实体类。(新建）
    private ApiEntity.UserFenPai mUserFenPai;
    private ApiEntity.UserFenPai mParentUserFenPai;//父类任务 用于创建子任务
    private ApiEntity.UserRail userRail;
    private SimpleDateFormat simpleDateFormat;
    private Dialog mLoadingDialog;
    private TaskPresenter mTaskPresenter;
    private ArrayList<ApiEntity.Files> fileRets;
    /////////////////////////////////////////////////////////////////////////////
    private DatePickerDialog dpdStart, dpdEnd; //开始/结束时间弹框
    private ScalePopup mPopupJJCD;  //紧急程度
    private List<String> mListJJCD;  //紧急程度数据
    private String mStrStartTime, mStrEndTime;
    private ListPopup mListPopupTag;    //标签弹框
    /////////////////////////////////////////////////////////////////////////////

    private int groupID, projectID; //列表传值，来自TimeTracking中的动态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        groupID = getIntent().getIntExtra("group_id", 0);
        projectID = getIntent().getIntExtra("project_id", 0);
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        initJJCDData();
        initTagList();
        selectList = new ArrayList<>();
        mTaskPresenter = new TaskPresenter(this);
        simpleDateFormat = new SimpleDateFormat(getString(R.string.timeformat6), Locale.CHINA);
        mUserFenPai = new ApiEntity.UserFenPai();
        mParentUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(PARENT_USERFENPAI);
        mUserProject = (ApiEntity.UserProject) getIntent().getSerializableExtra(TEAM_USERPROJECT);
        mCanSee = getIntent().getBooleanExtra(WHO_CAN_SEE, false);
        //查找项目名并且显示在Ui上
        if (mUserProject != null) {
            if (!mCanSee && mUserProject.Name != null) {
                mTvProject.setText(mUserProject.Name);
                mTvProject.setBackgroundResource(R.drawable.task_bg);
                mUserFenPai.ProjectId = mUserProject.ID;
            }
        }
        getCalendarTag();
    }


    /**
     * 初始化基本视图
     */
    private void initView() {
        /*cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
        mHeaderTitleTv.setText("新任务");*/
        if (mCanSee) {
            mContentEt.setVisibility(View.VISIBLE);
            mRlCanSeeContainer.setVisibility(View.VISIBLE);//看见条目
            mRlProjectContainer.setVisibility(View.GONE);//看不见项目名称
        } else {
            mContentEt.setVisibility(View.GONE);
            mLlWhoCanSeeContainer.setVisibility(View.GONE);
            mRlProjectContainer.setVisibility(View.VISIBLE);
            if (mUserProject != null) {
                itvTaskView.setVisibility(View.GONE);
            } else {
                itvTaskView.setVisibility(View.VISIBLE);
                mTvProject.setBackgroundColor(Color.TRANSPARENT);
                mRlProjectContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TaskCreateActivity.this, WorkProjectActivity.class);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        intent.putExtra("click_pos_y", location[1]);
                        startActivityForResult(intent, REQUESTCODE_WORK_PROJECT);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        /*getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        getSwipeBackLayout().scrollToFinishActivity();*/
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /*********************************************点击事件监听********************************************************/
    @Event({R.id.rl_task_modify_charge_container,
            R.id.ll_task_modify_moreuser_container,
            R.id.ll_task_modify_emergency_container,
            R.id.ll_task_modify_attachment_container,
            R.id.ll_task_modify_location_container,
            R.id.cm_header_btn_left9,
            R.id.ll_task_modify_start_time_container,
            R.id.ll_task_modify_finish_time_container,
            R.id.cm_header_tv_right9,
            R.id.cm_select_ll_select,
            R.id.ll_tag_layout
    })
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
            //取消任务创建
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            //跳转到负责人界面
            case R.id.rl_task_modify_charge_container:
                Intent charge = new Intent(TaskCreateActivity.this, TaskMemberActivity.class);
                charge.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
                charge.putExtra(TaskMemberActivity.MEMBERTYPE, TaskMemberActivity.CHARGE);
                charge.putExtra(TaskMemberActivity.TASK_MEMBER_REQUEST_TYPE, TaskMemberActivity.Member_new);
                if (!mCanSee) {
                    charge.putExtra(TaskMemberActivity.FITER_TEAM_LIST, getFiterTeamList());//传送关联项目的负责人ID
                }
                charge.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                charge.putExtra("click_pos_y", location[1]);
                startActivityForResult(charge, REQUESTCODE_CHARGE);
                break;
            //跳转到执行人界面
            case R.id.ll_task_modify_moreuser_container:
                Intent excutor = new Intent(TaskCreateActivity.this, TaskMemberActivity.class);
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
            //打开紧急程度
            case R.id.ll_task_modify_emergency_container:
                HelpUtil.hideSoftInput(this, mEtDes);
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
            //跳转到附件界面
            case R.id.ll_task_modify_attachment_container:
                Intent repositoryIntent = new Intent(this, FileSelectActivity.class);
                repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, mUserFenPai.Files);
                repositoryIntent.putExtra("start_anim", false);
                int[] location3 = new int[2];
                v.getLocationOnScreen(location3);
                repositoryIntent.putExtra("click_pos_y", location3[1]);
                startActivityForResult(repositoryIntent, REQUESTCODE_ATTACHMENT);
                /**
                 * 废弃原有的跳转逻辑和类   直接跳转到FileSelectActivity
                 */
//                Intent attachment = new Intent(this, AttachmentActivity.class);
//                attachment.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
//                attachment.putExtra(AttachmentActivity.ATTACHMENT_NEW_OR_MODIFY, AttachmentActivity.attachment_new);
//                startActivityForResult(attachment, REQUESTCODE_ATTACHMENT);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
                break;
            //开始时间选择界面
            case R.id.ll_task_modify_start_time_container:
                HelpUtil.hideSoftInput(TaskCreateActivity.this, mTvStartTime);
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
            //结束时间选择界面
            case R.id.ll_task_modify_finish_time_container:
                HelpUtil.hideSoftInput(TaskCreateActivity.this, mTvFinishTime);
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
            //谁能看到选择界面 跳转到人员列表中
            case R.id.cm_select_ll_select:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                intent.putExtra("has_oneself", false);
                intent.putExtra("start_anim", false);
                int[] location4 = new int[2];
                v.getLocationOnScreen(location4);
                intent.putExtra("click_pos_y", location4[1]);
                startActivityForResult(intent, REQUESTCODE_SELECT_PERSON);
                break;
            //提交任务
            case R.id.cm_header_tv_right9:
                String statTime = mTvStartTime.getText().toString().trim();
                String endTime = mTvFinishTime.getText().toString().trim();
                boolean isTrue = validateTask(statTime, endTime);//验证必要数据;
                mUserFenPai.Title = mEtTitle.getText().toString().trim();
                mUserFenPai.Mark = mEtDes.getText().toString().trim();
                //开始时间
                mUserFenPai.StartTime = statTime;
                mUserFenPai.FinishTime = endTime;
                mUserFenPai.ID = 0;//新建任务  任务ID 为0
                mUserFenPai.State = TaskConstant.TaskState.UNSTART;// 新建任务的状态为未完成
                mUserFenPai.FlowState = TaskConstant.FlowState.NORMAL;
                mUserFenPai.Creator = PrefsUtil.readUserInfo().ID;
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
                if (fileRets != null && fileRets.size() > 0) { //附件
                    ArrayList<UserNote.UserNoteFile> fileList = new ArrayList<>();
                    for (ApiEntity.Files file : fileRets) {
                        fileList.add(HelpUtil.files2UserNoteFile(file));
                    }
                    mUserFenPai.NoteAddPriority = new Gson().toJson(fileList);
                    mUserFenPai.NoteAddType = ApiEnum.UserNoteAddTypes.File;
                }
                if (mUserFenPai.TaskLabel == null) {
                    mUserFenPai.TaskLabel = "无";
                }
                if (mUserFenPai.Files == null) {
                    mUserFenPai.Files = "";// 用户新建任务的时候，没有选择任何附件操作
                }
                if (mParentUserFenPai != null) {//创建子任务
                    mUserFenPai.PID = mParentUserFenPai.ID;
                    mUserFenPai.ProjectId = mParentUserFenPai.ProjectId;
                }

                if (mCanSee) {
                    mUserFenPai.NoteContent = mContentEt.getText().toString();
                    addNoteRoles();
                }
                if (userRail != null) {
                    mUserFenPai.Rail = userRail;
                }
                if (groupID > 0) {
                    mUserFenPai.Line_Group = Integer.toString(groupID);
                }
                if (projectID > 0) {
                    mUserFenPai.ProjectId = projectID;
                }
                if (isTrue) {
                    mTaskPresenter.createTask(mUserFenPai);
                } else {
                    if (null != mLoadingDialog) {
                        mLoadingDialog.dismiss();
                    }
                }
                break;
            case R.id.ll_task_modify_location_container:
                Intent locationIntent = new Intent(this, GeoFenceActivity.class);
                startActivityForResult(locationIntent, 143);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 143:
                if (resultCode == Activity.RESULT_OK) {
                    userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
                    int id = data.getIntExtra("UserRailId", 0);
                    mUserFenPai.RailID = id;
                    if (userRail != null)
                        tvMPostition.setText(userRail.Address);
                }
                break;
            case REQUESTCODE_ATTACHMENT:
                if (resultCode == Activity.RESULT_OK) {
                    fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    mUserFenPai.Files = TaskUtils.getRepositoryArray(fileRets);
                    if (fileRets == null || fileRets.size() == 0) {
                        mTvAttachmentNum.setVisibility(View.INVISIBLE);
                    } else {
                        mTvAttachmentNum.setVisibility(View.VISIBLE);
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
                        mTvAttachmentNum.setText(builder);
                    }
                }
                break;
            case REQUESTCODE_CHARGE:    //跳转到负责人界面
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ApiEntity.UserInfo> list = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("member_result");
//                    mTvChargeNum.setVisibility(View.VISIBLE);
                    mTvChargeNum.setText(list.size() + "");
                    //返回来的数据至少有一条
                    //头像
                    mCivHeadImage.setVisibility(View.VISIBLE);
                    mCivHeadImage.setTextBg(EMWApplication.getIconColor(list.get(0).ID), list.get(0).Name, 30);
                    TaskUtils.setCivImageView(list.get(0).Image, mCivHeadImage);
//                    mTvCharge.setText(list.get(0).Name);
//                    mTvCharge.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    mUserFenPai.MainUser = TaskUtils.members2string(list);
                }
                break;
            case REQUESTCODE_EXCUTOR:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ApiEntity.UserInfo> list = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("member_result");
                    mUserFenPai.MoreUser = TaskUtils.members2string(list);
                    showMoreUser();
                }
                break;
            case REQUESTCODE_WORK_PROJECT:
                //获取到回传的项目
                if (resultCode == Activity.RESULT_OK) {
                    ApiEntity.UserProject userProject = (ApiEntity.UserProject) data
                            .getSerializableExtra(WorkProjectActivity.WORK_PROJECT);
                    //UI展示
                    mTvProject.setText(userProject.Name);
                    mTvProject.setBackgroundResource(R.drawable.task_bg);
                    mUserFenPai.ProjectId = userProject.ID;
                    mUserFenPai.ProjectElem = userProject;
                }
                break;
            case REQUESTCODE_SELECT_PERSON:
                //跳转到选择人员列表中
                if (resultCode == Activity.RESULT_OK) {
                    selectList = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("select_list");
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
                    mTvCanSee.setText(builder);
                    if (selectList.size() == 0) {
                        mTvCanSee.setText("");
                    }
                }
                break;
        }
    }

    /*****************************************任务创建业务逻辑方法*******************************************/
    /**
     * @des 根据执行人String名字集合获取到负责人列表，然后取出头像图片添加到显示视图中
     * @cal 选择执行人返回任务创建页面
     */
    private void showMoreUser() {
//        mTvMoreUser.setVisibility(View.GONE);
        mLlHeadImageContainer.setVisibility(View.VISIBLE);
        String moreUser = mUserFenPai.MoreUser;
        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers(moreUser);
        mLlHeadImageContainer.removeAllViews();
        if (moreUsers != null && moreUsers.size() != 0) {
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                if (userInfo != null) {
                    CircleImageView circleImageView = new CircleImageView(this);
                    circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                    TaskUtils.setCivImageView(userInfo.Image, circleImageView);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mLlHeadImageContainer.addView(circleImageView, params);
                }
                if (i == 4) {
                    break;
                }
            }
            mTvMoreUserNum.setVisibility(moreUsers.size() > 5 ? View.VISIBLE : View.GONE);
            mTvMoreUserNum.setText(moreUsers.size() + "");
        } else {
            mLlHeadImageContainer.setVisibility(View.GONE);
            mTvMoreUserNum.setVisibility(View.GONE);
//            mTvMoreUser.setVisibility(View.VISIBLE);
//            mTvMoreUser.setTextColor(getResources().getColor(R.color.task_modify_charge));
        }
    }

    /**
     * @des 设置谁能看到的人员列表实体对象
     * @cal 提交任务
     */
    private void addNoteRoles() {
        ArrayList<ApiEntity.NoteRole> nrList = new ArrayList<>();
        if (selectList != null && selectList.size() > 0) {
            for (int i = 0, size = selectList.size(); i < size; i++) {
                ApiEntity.NoteRole role = new ApiEntity.NoteRole();
                role.ID = selectList.get(i).ID;
                role.Name = selectList.get(i).Name;
                role.Image = selectList.get(i).Image;
                role.Type = ApiEnum.NoteRoleTypes.User;
                nrList.add(role);
            }
        }
        mUserFenPai.NoteRoles = new Gson().toJson(nrList);
        mUserFenPai.NoteContent = mEtTitle.getText().toString().trim();
    }

    /**
     * @des 验证开始时间和结束时间的合法性
     * @cal 选择时间后
     */
    private void updateDiaplay(Date date) {
        try {
            if (CalendarUtil.compareDate(date) < 0) {
                ToastUtil.showToast(this, "不可小于当前日期!");
                return;
            }
            if (date_type == 0) {
                //设置开始时间
                String endTime = mTvFinishTime.getText().toString().trim();
                if (endTime.length() == 0) {
                    mTvStartTime.setText(simpleDateFormat.format(date));
//                    mTvStartTime.setTextColor(getResources().getColor(R.color.task_modify_charge));

                } else {
                    Date d = simpleDateFormat.parse(endTime);
                    if (d.getTime() > date.getTime()) {
                        mTvStartTime.setText(simpleDateFormat.format(date));
//                        mTvStartTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
                    } else {
                        ToastUtil.showToast(this, "开始时间不能大于结束时间");
                    }
                }
            } else {
                //设置结束时间
                String startTime = mTvStartTime.getText().toString().trim();
                if (startTime.length() == 0) {
                    mTvFinishTime.setText(simpleDateFormat.format(date));
//                    mTvFinishTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
                } else {
                    Date d = simpleDateFormat.parse(mTvStartTime.getText().toString());
                    if (d.getTime() < date.getTime()) {
                        mTvFinishTime.setText(simpleDateFormat.format(date));
//                        mTvFinishTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
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
     * @des 验证任务的合法性
     * @cal 提交新建任务
     */
    private boolean validateTask(String statTime, String endTime) {
        if (TextUtils.isEmpty(mEtTitle.getText().toString().trim())) {
            ToastUtil.showToast(this, getString(R.string.allot_empty_taskname));
            return false;
        }
        if ("0".equals(mTvChargeNum.getText())) {
            ToastUtil.showToast(this, "请选择至少一个负责人!");
            return false;
        }
        if ("0".equals(mTvMoreUserNum.getText())) {
            ToastUtil.showToast(this, "请选择至少一个执行人!");
            return false;
        }
        if ("紧急程度".equals(mTvEmergency.getText())) {
            ToastUtil.showToast(this, "请选择紧急程度!");
            return false;
        }
        if ("开始日期".equals(statTime)) {
            ToastUtil.showToast(this, R.string.task_modify_beginTime);
            return false;
        }
        if ("结束日期".equals(endTime)) {
            ToastUtil.showToast(this, R.string.task_modify_endtime);
            return false;
        }
        return true;
    }

    /*******************************************
     * 生命周期方法
     **************************************************/
    @Override
    protected void onPause() {
        super.onPause();
        HelpUtil.hideSoftInput(this, mEtTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @des 提交任务
     */
    @Override
    public void createTask(String respInfo) {
        if (null != mLoadingDialog) {
            mLoadingDialog.dismiss();
        }
        if (respInfo != null && TextUtils.isDigitsOnly(respInfo.trim()) && Integer.valueOf(respInfo) > 0) {
            ToastUtil.showToast(this, R.string.task_modify_create_task_success,
                    R.drawable.tishi_ico_gougou);
            if (mParentUserFenPai != null) {
                Intent intent = new Intent();
                intent.setAction(ACTION_CREATE_CHILD_TASK);
                intent.putExtra(TaskConstant.SEND_USERFENPAI, mParentUserFenPai);
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                if (groupID > 0) {
                    intent = new Intent(TimeTrackingWebFragment.ACTION_REFRESH_TIMEDYNAMIC);
                }
                sendBroadcast(intent);
            }
            if (!mCanSee) {
                Intent loadIntent = new Intent();
                loadIntent.setAction(ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH);
                sendBroadcast(loadIntent);

                sendBroadcast(new Intent(TaskCreateActivity.ACTION_TASK_CREATE_SUCCESS));
            }
            finish();
        } else {
            ToastUtil.showToast(this, R.string.task_modify_create_task_error);
        }
    }

    /**
     * @des 提交任务出错
     */
    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        ToastUtil.showToast(this, R.string.task_modify_create_task_error);
    }

    @Override
    public void modifyTask(String s) {
    }

    @Override
    public void getFileList(List<ApiEntity.Files> files) {
    }

    @Override
    public void completeFresh() {
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

    /**
     * @des 获取关联项目的相关人员集合
     */
    public ArrayList<ApiEntity.UserInfo> getFiterTeamList() {
        ArrayList<ApiEntity.UserInfo> users = new ArrayList<>();
        if (mUserProject != null) {
            if (mUserProject.ID != 0) {
                users.addAll(TaskUtils.getUsers(mUserProject.MainUser));
                users.addAll(TaskUtils.getUsers(mUserProject.Users));
            }
        }
        return users;
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

    /**
     * 初始化日程标签
     */
    private void initTagList() {
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
                    initTagList();
                    mLables.addAll(respList);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(TaskCreateActivity.this, "获取用户自定义标签失败", Toast.LENGTH_SHORT).show();
            }
        });
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


//package cc.emw.mobile.task.activity;
//
//
//        import android.app.Activity;
//        import android.app.Dialog;
//        import android.content.BroadcastReceiver;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.content.IntentFilter;
//        import android.os.Bundle;
//        import android.text.TextUtils;
//        import android.view.View;
//        import android.widget.EditText;
//        import android.widget.LinearLayout;
//        import android.widget.RelativeLayout;
//        import android.widget.TextView;
//
//        import com.bigkoo.pickerview.OptionsPickerView;
//        import com.bigkoo.pickerview.TimePickerView;
//        import com.google.gson.Gson;
//
//        import org.xutils.view.annotation.ContentView;
//        import org.xutils.view.annotation.Event;
//        import org.xutils.view.annotation.ViewInject;
//
//        import java.text.ParseException;
//        import java.text.SimpleDateFormat;
//        import java.util.ArrayList;
//        import java.util.Date;
//        import java.util.List;
//        import java.util.Locale;
//
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.base.BaseActivity;
//        import cc.emw.mobile.contact.ContactSelectActivity;
//        import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
//        import cc.emw.mobile.map.GeoFenceActivity;
//        import cc.emw.mobile.net.ApiEntity;
//        import cc.emw.mobile.net.ApiEnum;
//        import cc.emw.mobile.project.view.ObserveProjectActivity;
//        import cc.emw.mobile.task.adapter.TaskTagAdapter;
//        import cc.emw.mobile.task.constant.TaskConstant;
//        import cc.emw.mobile.task.entity.UserLabelBean;
//        import cc.emw.mobile.task.factory.TaskOptionsPickerViewFactory;
//        import cc.emw.mobile.task.factory.TaskTimePickerViewFactory;
//        import cc.emw.mobile.task.presenter.TaskPresenter;
//        import cc.emw.mobile.task.view.ITaskLableView;
//        import cc.emw.mobile.task.view.ITaskModifyView;
//        import cc.emw.mobile.util.DisplayUtil;
//        import cc.emw.mobile.util.HelpUtil;
//        import cc.emw.mobile.util.PrefsUtil;
//        import cc.emw.mobile.util.TaskUtils;
//        import cc.emw.mobile.util.ToastUtil;
//        import cc.emw.mobile.view.CircleImageView;
//        import cc.emw.mobile.view.CollapseView;
//        import cc.emw.mobile.view.IconTextView;
//        import cc.emw.mobile.view.MyListView;
//
///**
// * Created by chengyong.liu on 2016/6/24.
// */
//@ContentView(R.layout.activity_task_modify_2)
//public class TaskCreateActivity extends BaseActivity implements ITaskModifyView,ITaskLableView {
//    public static final String ACTION_CREATE_CHILD_TASK = "cc.emw.mobile.create_child_task";//创建子任务发送广播频段
//    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT);
//
//    @ViewInject(R.id.rl_task_modify_project_container)
//    private RelativeLayout mRlProjectContainer;
//    @ViewInject(R.id.cm_select_ll_select)
//    private LinearLayout mLlWhoCanSeeContainer;//谁能看见条目
//    @ViewInject(R.id.cm_header_tv_title)
//    private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_bar)
//    private LinearLayout cm_header_bar;
//    @ViewInject(R.id.et_taskmodify_name)
//    private EditText mEtTitle;//项目名称
//    @ViewInject(R.id.et_taskmodify_des)
//    private EditText mEtDes;//项目描述
//    @ViewInject(R.id.civ_task_modify_head_image)
//    private CircleImageView mCivHeadImage;//头像
//    @ViewInject(R.id.tv_task_modify_charge)
//    private TextView mTvCharge;
//    @ViewInject(R.id.task_modify_start_time)
//    private TextView mTvStartTime;//开始时间
//    @ViewInject(R.id.tv_task_modify_finish_time)
//    private TextView mTvFinishTime;//结束时间
//    @ViewInject(R.id.tv_task_modify_emergency)
//    private TextView mTvEmergency;
//    @ViewInject(R.id.tv_task_modify_project)
//    private TextView mTvProject;//关联项目
//    @ViewInject(R.id.tv_task_modify_attachment)
//    private TextView mTvAttachment;
//    @ViewInject(R.id.tv_task_modify_more_user)
//    private TextView mTvMoreUser;
//    @ViewInject(R.id.ll_task_modify_head_image_container)
//    private LinearLayout mLlHeadImageContainer;//执行人头像
//    @ViewInject(R.id.tv_task_modify_attachment_number)
//    private TextView mTvAttachmentNum;
//    @ViewInject(R.id.tv_task_modify_more_user_number)
//    private TextView mTvMoreUserNum;
//    @ViewInject(R.id.tv_task_modify_charge_number)
//    private TextView mTvChargeNum;
//    @ViewInject(R.id.v_task_modify_project)
//    private View mView;
//    @ViewInject(R.id.cm_select_ll_select)
//    private LinearLayout mRlCanSeeContainer;
//    @ViewInject(R.id.cm_select_tv_name)
//    private TextView mTvCanSee;
//    @ViewInject(R.id.tv_task_modify_location)
//    private TextView tvMPostition;
//    @ViewInject(R.id.itv_task_view)
//    private IconTextView itvTaskView;
//    @ViewInject(R.id.clv_task_tag)
//    private CollapseView mTagLayout;
//
//    private ApiEntity.UserFenPai mUserFenPai;
//    private OptionsPickerView<String> mOptionsPickerView;
//
//    public static final int REQUESTCODE_ATTACHMENT = 1;
//    public static final int REQUESTCODE_CHARGE = 2;
//    public static final int REQUESTCODE_EXCUTOR = 3;
//    public static final int REQUESTCODE_SELECT_PERSON = 7;
//    public static final int REQUESTCODE_TASK_LABEL = 8;
//    public static final String PARENT_USERFENPAI = "parent_userfenpai";
//    private ApiEntity.UserFenPai mParentUserFenPai;//父类任务 用于创建子任务
//    public static final String WHO_CAN_SEE = "who_can_see";//用于标记是否展示谁能看见
//    private boolean mCanSee;//默认不展示谁能看见
//    private ApiEntity.UserRail userRail;
//
//    private SimpleDateFormat simpleDateFormat;
//
//
//    private Dialog mLoadingDialog;
//    private TaskPresenter mTaskPresenter;
//
//    private TimePickerView mStartPopupWindow;// 时间表弹窗
//    private int date_type = 0;// 时间类型 0表示开始时间 1表示结束时间。
//    private TaskTagAdapter adapter;
//    private List<UserLabelBean> mLables;
//    private MyBroadCastReceicer receicer;
//    public static final String ACTION_REFRESH_CALENDAR_TAG = "action_refresh_calendar_tag";
//
//    private ApiEntity.UserProject mUserProject;//团队实体类。(新建）
//    public static final String TEAM_USERPROJECT="team_userpoject";
//    private ArrayList<ApiEntity.UserInfo> selectList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTaskPresenter = new TaskPresenter(this);
//        simpleDateFormat = new SimpleDateFormat(getString(R.string.timeformat6), Locale.CHINA);
//        mUserFenPai = new ApiEntity.UserFenPai();
//        mParentUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(PARENT_USERFENPAI);//parent_userfenpai
//        mUserProject =(ApiEntity.UserProject) getIntent().getSerializableExtra(TEAM_USERPROJECT);
//        mCanSee = getIntent().getBooleanExtra(WHO_CAN_SEE, false);
//        initOpionSelector();
//        initTimeSelector();
//        initView();
//        initData();
//        initCollapseView();
//    }
//    /************************************************************************************************************/
//
//    private void initCollapseView(){
//        IntentFilter filter = new IntentFilter();
//        receicer = new MyBroadCastReceicer();
//        filter.addAction(ACTION_REFRESH_CALENDAR_TAG);
//        registerReceiver(receicer, filter);
//
//
//        mLables = new ArrayList<>();
//        mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
//        adapter = new TaskTagAdapter(this);
//        adapter.setData(mLables);
//
//        mTagLayout.setContent(R.layout.activity_calendar_tag2);
//        MyListView myListView = (MyListView) mTagLayout.findViewById(R.id.lv_calendar_tag);
//        TextView tagNew = (TextView) mTagLayout.findViewById(R.id.tv_calendar_new_tag);
//        mTagLayout.setmTagNameVis(true, false, false, false);
//        myListView.setAdapter(adapter);
//        adapter.setCollaspeView(mTagLayout);
//        mTagLayout.setTitle("#无1");
//        tagNew.setOnClickListener(new View.OnClickListener() {//新建任务
//            @Override
//            public void onClick(View v) {
//                Intent labelCreate = new Intent(TaskCreateActivity.this, TaskCreateLabelActivity.class);
//                startActivityForResult(labelCreate, REQUESTCODE_TASK_LABEL);
//                overridePendingTransition(R.anim.popup_show, R.anim.activity_out);
//            }
//        });
//    }
//
//    @Override
//    public void getUserLable(List<UserLabelBean> labels) {
//        mLables.clear();
//        initList();
//        mLables.addAll(labels);
//        adapter.notifyDataSetChanged();
//    }
//    @Override
//    public void addUserLable(String s) {
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        if (Integer.parseInt(s) > 1) {
//            ToastUtil.showToast(this, "添加标签成功!", R.drawable.tishi_ico_gougou);
//            mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
//            mTagLayout.rotateArrow();
//        } else {
//            ToastUtil.showToast(this, "添加标签失败!");
//        }
//
//    }
//    @Override
//    public void modifyUserLable(String s) {
//    }
//    @Override
//    public void delUserLable(String s) {
//    }
//
//    private class MyBroadCastReceicer extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mTaskPresenter.getUserLable(PrefsUtil.readUserInfo().ID);
//            String tag = intent.getStringExtra("tag");
//            if (tag != null) {
//                mTagLayout.setTitle(tag);
//            }
//            mTagLayout.rotateArrow();
//        }
//    }
//
//    /************************************************************************************************************/
//    private void initData() {
//        //查找项目名并且显示在Ui上
//        if(mUserProject != null) {
//            if (!mCanSee && mUserProject.Name != null) {
//                mTvProject.setText(mUserProject.Name);
//                mTvProject.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                mUserFenPai.ProjectId = mUserProject.ID;
//            }
//        }
//    }
//
//    /**
//     * 获取关联项目的相关人员集合
//     *
//     * @return
//     */
//    public ArrayList<ApiEntity.UserInfo> getFiterTeamList() {
//        ArrayList<ApiEntity.UserInfo> users = new ArrayList<>();
//        if(mUserProject != null) {
//            if (mUserProject.ID != 0) {
//                users.addAll(TaskUtils.getUsers(mUserProject.MainUser));
//                users.addAll(TaskUtils.getUsers(mUserProject.Users));
//            }
//        }
//        return users;
//    }
//    private void initView() {
//        cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
//        mHeaderTitleTv.setText("新任务");
//        if (mCanSee) {
//            mRlCanSeeContainer.setVisibility(View.VISIBLE);//看见条目
//            mRlProjectContainer.setVisibility(View.GONE);//看不见项目名称
//            mView.setVisibility(View.GONE);
//        }else{
//            mLlWhoCanSeeContainer.setVisibility(View.GONE);
//            mRlProjectContainer.setVisibility(View.VISIBLE);
//            mView.setVisibility(View.VISIBLE);
//            itvTaskView.setVisibility(View.GONE);
//        }
//    }
//
//    private void showMoreUser() {
//        mTvMoreUser.setVisibility(View.GONE);
//        mLlHeadImageContainer.setVisibility(View.VISIBLE);
//        String moreUser = mUserFenPai.MoreUser;
//        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers(moreUser);
//        mLlHeadImageContainer.removeAllViews();
//        if (moreUsers != null && moreUsers.size() != 0) {
//            for (int i = 0; i < moreUsers.size(); i++) {
//                ApiEntity.UserInfo userInfo = moreUsers.get(i);
//                CircleImageView circleImageView = new CircleImageView(this);
//                if (userInfo.Image != null) {
//                    TaskUtils.setCivImageView(userInfo.Image, circleImageView);
//                    params.leftMargin = DisplayUtil.dip2px(this, 5);
//                    params.width = DisplayUtil.dip2px(this, 30);
//                    params.height = DisplayUtil.dip2px(this, 30);
//                    mLlHeadImageContainer.addView(circleImageView, params);
//                }
//                if (i == 5) {
//                    break;
//                }
//            }
//        } else {
//            mLlHeadImageContainer.setVisibility(View.GONE);
//            mTvMoreUser.setVisibility(View.VISIBLE);
//            mTvMoreUser.setTextColor(getResources().getColor(R.color.task_modify_charge));
//        }
//    }
//
//    @Event({R.id.rl_task_modify_charge_container,
//            R.id.ll_task_modify_moreuser_container,
//            R.id.ll_task_modify_emergency_container,
//            R.id.ll_task_modify_attachment_container,
//            R.id.ll_task_modify_location_container,
//            R.id.cm_header_btn_left9,
//            R.id.ll_task_modify_start_time_container,
//            R.id.ll_task_modify_finish_time_container,
//
//            R.id.cm_header_tv_right9,
//            R.id.cm_select_ll_select,
//            R.id.finish})
//    private void onclick(View v) {
//        switch (v.getId()) {
//            case R.id.cm_header_btn_left9:
//                finish();
//                break;
//            case R.id.rl_task_modify_charge_container:
//                //跳转到负责人界面
//                Intent charge = new Intent(TaskCreateActivity.this, TaskMemberActivity.class);
//                charge.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
//                charge.putExtra(TaskMemberActivity.MEMBERTYPE, TaskMemberActivity.CHARGE);
//                charge.putExtra(TaskMemberActivity.TASK_MEMBER_REQUEST_TYPE, TaskMemberActivity.Member_new);
//                if(!mCanSee) {
//                    charge.putExtra(TaskMemberActivity.FITER_TEAM_LIST, getFiterTeamList());//传送关联项目的负责人ID
//                }
//                startActivityForResult(charge, REQUESTCODE_CHARGE);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
//                break;
//            case R.id.ll_task_modify_moreuser_container:
//                //跳转到执行人界面
//                Intent excutor = new Intent(TaskCreateActivity.this, TaskMemberActivity.class);
//                excutor.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
//                excutor.putExtra(TaskMemberActivity.MEMBERTYPE, TaskMemberActivity.EXCUTOR);
//                excutor.putExtra(TaskMemberActivity.TASK_MEMBER_REQUEST_TYPE, TaskMemberActivity.Member_new);
//                if(!mCanSee) {
//                    excutor.putExtra(TaskMemberActivity.FITER_TEAM_LIST, getFiterTeamList());
//                }
//                startActivityForResult(excutor, REQUESTCODE_EXCUTOR);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
//                break;
//            case R.id.ll_task_modify_emergency_container:
//                //打开紧急程度
//                HelpUtil.hideSoftInput(this, mEtDes);
//                mOptionsPickerView.show();
//                break;
//            case R.id.ll_task_modify_attachment_container:
//                //跳转到附件界面
//                Intent attachment = new Intent(this, AttachmentActivity.class);
//                attachment.putExtra(TaskDetailActivity.TASK_DETAIL, mUserFenPai);
//                attachment.putExtra(AttachmentActivity.ATTACHMENT_NEW_OR_MODIFY, AttachmentActivity.attachment_new);
//                startActivityForResult(attachment, REQUESTCODE_ATTACHMENT);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
//                break;
//            case R.id.ll_task_modify_start_time_container:
//                date_type = 0;
//                mStartPopupWindow.setTitle(getString(R.string.plan_time));
//                mStartPopupWindow.show();
//                HelpUtil.hideSoftInput(TaskCreateActivity.this, mTvStartTime);
//                break;
//            case R.id.ll_task_modify_finish_time_container:
//                date_type = 1;
//                mStartPopupWindow.setTitle(getString(R.string.ending_time));
//                mStartPopupWindow.show();
//                HelpUtil.hideSoftInput(TaskCreateActivity.this, mTvFinishTime);
//                break;
//            case R.id.cm_select_ll_select:
//                //跳转到人员列表中
//                Intent intent = new Intent(this, ContactSelectActivity.class);
//                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
//                intent.putExtra("select_list", selectList);
//                startActivityForResult(intent, REQUESTCODE_SELECT_PERSON);
//                break;
//            case R.id.cm_header_tv_right9:
//                //发布任务
//                if (TextUtils.isEmpty(mEtTitle.getText().toString().trim())) {
//                    ToastUtil.showToast(this, getString(R.string.allot_empty_taskname));
//                    return;
//                }
//                mUserFenPai.Title = mEtTitle.getText().toString().trim();
//                mUserFenPai.Mark = mEtDes.getText().toString().trim();
//                if (!mCanSee) {
//                    if (mParentUserFenPai != null) {
//                        //创建子任务
//                        mUserFenPai.PID = mParentUserFenPai.ID;
//                        mUserFenPai.ProjectId = mParentUserFenPai.ProjectId;
//                    }
//                }
//
//                if ("0".equals(mTvChargeNum.getText())) {
//                    ToastUtil.showToast(this, "请选择至少一个负责人!");
//                    return;
//                }
//                if ("0".equals(mTvMoreUserNum.getText())) {
//                    ToastUtil.showToast(this, "请选择至少一个执行人!");
//                    return;
//                }
//                //开始时间
//                String statTime = mTvStartTime.getText().toString().trim();
//                if ("开始日期".equals(statTime)) {
//                    ToastUtil.showToast(this, R.string.task_modify_beginTime);
//                    return;
//                }
//                mUserFenPai.StartTime = statTime;
//                String endTime = mTvFinishTime.getText().toString().trim();
//                if ("结束日期".equals(endTime)) {
//                    ToastUtil.showToast(this, R.string.task_modify_endtime);
//                    return;
//                }
//                mUserFenPai.FinishTime = endTime;
//                if ("紧急程度".equals(mTvEmergency.getText())) {
//                    ToastUtil.showToast(this, "请选择紧急程度!");
//                    return;
//                }
//
//                if (mUserFenPai.Files == null) {
//                    mUserFenPai.Files = "[]";// 用户新建任务的时候，没有选择任何附件操作
//                }
//
//                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
//                mLoadingDialog.show();
//                mUserFenPai.FlowState = TaskConstant.FlowState.NORMAL;
//                mUserFenPai.Creator = PrefsUtil.readUserInfo().ID;
//
//                mUserFenPai.ID = 0;//新建任务  任务ID 为0
//                mUserFenPai.State = TaskConstant.TaskState.UNSTART;// 新建任务的状态为未完成
//                if (mCanSee) {
//                    addNoteRoles();
//                }
//                //添加子任务
//                if (mParentUserFenPai != null) {
//                    List<ApiEntity.UserFenPai> tasks = mParentUserFenPai.Tasks;
//                    if (tasks != null) {
//                        tasks.add(mUserFenPai);
//                    } else {
//                        tasks = new ArrayList<>();
//                        tasks.add(mUserFenPai);
//                    }
//                }
//                mUserFenPai.TaskLabel=mTagLayout.getTitle();
//                //默认设置标签为无，如果没有选择标签的话
//                if (mUserFenPai.TaskLabel == null) {
//                    mUserFenPai.TaskLabel = "无";
//                }
//                if (userRail != null) {
//                    mUserFenPai.Rail = userRail;
//                }
//                mTaskPresenter.createTask(mUserFenPai);
//                break;
//            case R.id.ll_task_modify_location_container:
//                Intent locationIntent = new Intent(this, GeoFenceActivity.class);
//                startActivityForResult(locationIntent, 143);
//                break;
//        }
//    }
//
//    private void addNoteRoles() {
//        ArrayList<ApiEntity.NoteRole> nrList = new ArrayList<>();
//        if (selectList != null && selectList.size() > 0) {
//            for (int i = 0, size = selectList.size(); i < size; i++) {
//                ApiEntity.NoteRole role = new ApiEntity.NoteRole();
//                role.ID = selectList.get(i).ID;
//                role.Name = selectList.get(i).Name;
//                role.Image = selectList.get(i).Image;
//                role.Type = ApiEnum.NoteRoleTypes.User;
//                nrList.add(role);
//            }
//        }
//        mUserFenPai.NoteRoles = new Gson().toJson(nrList);
//        mUserFenPai.NoteContent = mEtTitle.getText().toString().trim();
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 143:
//                if (resultCode == Activity.RESULT_OK) {
//                    userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
//                    int id = data.getIntExtra("UserRailId", 0);
//                    mUserFenPai.RailID = id;
//                    if (userRail != null)
//                        tvMPostition.setText(userRail.Address);
//                }
//                break;
//            case REQUESTCODE_ATTACHMENT:
//                if (resultCode == Activity.RESULT_OK) {
//                    ArrayList<ApiEntity.Files> list = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("attachment_result");
//                    mTvAttachmentNum.setVisibility(View.VISIBLE);
//                    mTvAttachmentNum.setText(list.size() + "");
//                    mTvAttachment.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                    mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
//                }
//                break;
//            case REQUESTCODE_CHARGE:    //跳转到负责人界面
//                if (resultCode == Activity.RESULT_OK) {
//                    ArrayList<ApiEntity.UserInfo> list = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("member_result");
//                    mTvChargeNum.setVisibility(View.VISIBLE);
//                    mTvChargeNum.setText(list.size() + "");
//                    //返回来的数据至少有一条
//                    //头像
//                    mCivHeadImage.setVisibility(View.VISIBLE);
//                    TaskUtils.setCivImageView(list.get(0).Image, mCivHeadImage);
//                    mTvCharge.setText(list.get(0).Name);
//                    mTvCharge.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                    mUserFenPai.MainUser = TaskUtils.members2string(list);
//                }
//                break;
//            case REQUESTCODE_EXCUTOR:
//                if (resultCode == Activity.RESULT_OK) {
//                    ArrayList<ApiEntity.UserInfo> list = (ArrayList<ApiEntity.UserInfo>) data .getSerializableExtra("member_result");
//                    mTvMoreUserNum.setVisibility(View.VISIBLE);
//                    mTvMoreUserNum.setText(list.size() + "");
//                    mUserFenPai.MoreUser = TaskUtils.members2string(list);
//                    showMoreUser();
//                }
//                break;
//            case REQUESTCODE_SELECT_PERSON:
//                //跳转到选择人员列表中
//                if (resultCode == Activity.RESULT_OK) {
//                    selectList = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("select_list");
//                    StringBuilder builder = new StringBuilder();
//                    for (int i = 0; i < selectList.size(); i++) {
//                        if (i < 3) {
//                            ApiEntity.UserInfo user = selectList.get(i);
//                            if (i != 0) {
//                                builder.append("、");
//                            }
//                            builder.append(user.Name);
//                        } else {
//                            builder.append("等" + selectList.size() + "人");
//                            break;
//                        }
//                    }
//                    mTvCanSee.setText(builder);
//                    if (selectList.size() == 0) {
//                        mTvCanSee.setText("");
//                    }
//                }
//                break;
//            case REQUESTCODE_TASK_LABEL:
//                if (resultCode == Activity.RESULT_OK) {
//                    String ul = (String) data.getSerializableExtra(TaskLableActivity.TASK_LABEL_NAME);
//                    UserLabelBean userlabel = new UserLabelBean();
//                    userlabel.Name = ul;
//                    mTagLayout.setTitle(ul);
//                    mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
//                    mLoadingDialog.show();
//                    mTaskPresenter.addUserLable(userlabel);
//                }
//                break;
//        }
//    }
//
//    private void initOpionSelector() {
//        // 紧急程度滚轴
//        mOptionsPickerView = TaskOptionsPickerViewFactory.createOptionsPickerView(this);
//        if (mUserFenPai.Yxj > 0) {
//            mOptionsPickerView.setSelectOptions(mUserFenPai.Yxj - 1);
//        }
//        mOptionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                // 一级联动
//                // 将当前的条目信息返回
//                mUserFenPai.Yxj = options1 + 1;
//                // 并且显示在紧急控件中
//                mTvEmergency.setText(TaskDetailActivity.colorStr[options1]);
//                mTvEmergency.setTextColor(getResources().getColor(R.color.task_modify_charge));
//            }
//        });
//    }
//
//    private void initTimeSelector() {
//        mStartPopupWindow= TaskTimePickerViewFactory.createTimeSelector(this);
//        mStartPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
//            @Override
//            public void onTimeSelect(Date date) {
//                updateDiaplay(date);
//            }
//        });
//    }
//
//    private void updateDiaplay(Date date) {
//        try {
//            long currentTime = new Date().getTime();
//            if (date.getTime() < currentTime) {
//                ToastUtil.showToast(this, "不可小于当前时间!");
//                return;
//            }
//            if (date_type == 0) {
//                //设置开始时间
//                String endTime = mTvFinishTime.getText().toString().trim();
//                if ("结束日期".equals(endTime)) {
//                    mTvStartTime.setText(simpleDateFormat.format(date));
//                    mTvStartTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
//
//                } else {
//                    Date d = simpleDateFormat.parse(endTime);
//                    if (d.getTime() > date.getTime()) {
//                        mTvStartTime.setText(simpleDateFormat.format(date));
//                        mTvStartTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                    } else {
//                        ToastUtil.showToast(this, R.string.task_modify_endtime_compare_starttime);
//                    }
//                }
//            } else {
//                //设置结束时间
//                String startTime = mTvStartTime.getText().toString().trim();
//                if (getString(R.string.plan_time_hint).equals(startTime)) {
//                    mTvFinishTime.setText(simpleDateFormat.format(date));
//                    mTvFinishTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                } else {
//                    Date d = simpleDateFormat.parse(mTvStartTime.getText().toString());
//                    if (d.getTime() < date.getTime()) {
//                        mTvFinishTime.setText(simpleDateFormat.format(date));
//                        mTvFinishTime.setTextColor(getResources().getColor(R.color.task_modify_charge));
//                    } else {
//                        ToastUtil.showToast(this, R.string.task_modify_endtime_compare_starttime);
//                    }
//                }
//
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void createTask(String respInfo) {
//        if (null != mLoadingDialog) {
//            mLoadingDialog.dismiss();
//        }
//        if (respInfo != null && TextUtils.isDigitsOnly(respInfo.trim()) && Integer.valueOf(respInfo) > 0) {
//            ToastUtil.showToast(this, R.string.task_modify_create_task_success,
//                    R.drawable.tishi_ico_gougou);
//            if (mParentUserFenPai != null) {
//                Intent intent = new Intent();
//                intent.setAction(ACTION_CREATE_CHILD_TASK);
//                intent.putExtra(TaskConstant.SEND_USERFENPAI, mParentUserFenPai);
//                sendBroadcast(intent);
//            } else {
//                Intent intent = new Intent();
//                intent.setAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
//                sendBroadcast(intent);
//            }
//            if(!mCanSee){
//                Intent loadIntent = new Intent();
//                loadIntent.setAction(ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH);
//                sendBroadcast(loadIntent);
//            }
//            finish();
//        } else {
//            ToastUtil.showToast(this, R.string.task_modify_create_task_error);
//        }
//    }
//
//    @Override
//    public void modifyTask(String s) {
//
//    }
//
//    @Override
//    public void getFileList(List<ApiEntity.Files> files) {
//
//    }
//
//    @Override
//    public void completeFresh() {
//
//    }
//
//    @Override
//    public void onError(Throwable ex, boolean isOnCallback) {
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        ToastUtil.showToast(this, R.string.task_modify_create_task_error);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        HelpUtil.hideSoftInput(this, mEtTitle);
//    }
//
//    private void initList(){
//        UserLabelBean tag0 = new UserLabelBean();
//        UserLabelBean tag1 = new UserLabelBean();
//        UserLabelBean tag2 = new UserLabelBean();
//        UserLabelBean tag3 = new UserLabelBean();
//        UserLabelBean tag4 = new UserLabelBean();
//        UserLabelBean tag5 = new UserLabelBean();
//        UserLabelBean tag6 = new UserLabelBean();
//        UserLabelBean tag7 = new UserLabelBean();
//        tag1.Name = "无";
//        tag1.Name = "#会议";
//        tag2.Name = "#拜访";
//        tag3.Name = "#电话";
//        tag4.Name = "#邮件";
//        tag5.Name = "#报告";
//        tag6.Name = "#其他";
//        tag7.Name = "#事件";
//        mLables.add(tag0);
//        mLables.add(tag1);
//        mLables.add(tag2);
//        mLables.add(tag3);
//        mLables.add(tag4);
//        mLables.add(tag5);
//        mLables.add(tag6);
//        mLables.add(tag7);
//    }
//
//}