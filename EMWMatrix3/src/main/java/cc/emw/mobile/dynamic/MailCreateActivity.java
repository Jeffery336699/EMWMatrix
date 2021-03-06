package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
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
import cc.emw.mobile.calendar.FreeRepeatActivity;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.calendar.fragment.WeekFragment;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.BusDataInfo;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.me.widget.OnScalePopupItemClickListener;
import cc.emw.mobile.me.widget.ScalePopup;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.SwipeBackScrollView;
import cc.emw.mobile.view.SwitchButton;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 动态·新建邮件
 * Created by sunny.du on 2016/8/24.
 */
@ContentView(R.layout.activity_mail_create3)
public class MailCreateActivity extends BaseActivity {
//    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_tv_right) private TextView mHeaderSendTv; // 顶部条发布

    @ViewInject(R.id.et_mail_name)
    private EditText mTitleEt; //邮件主题
    @ViewInject(R.id.et_mail_desc)
    private EditText mDescEt; //描述
    @ViewInject(R.id.ll_mail_allday)
    private LinearLayout mAllDayLayout; // 全天事件Layout
    @ViewInject(R.id.sb_mail_allday)
    private SwitchButton mAllDaySb; // 全天事件开关按钮
    @ViewInject(R.id.ll_mail_calluser_head)
    private LinearLayout mCallUserHeadLayout; //收件人头像Layout
    @ViewInject(R.id.ll_mail_answeruser_head)
    private LinearLayout mAnswerUserHeadLayout; //抄送人头像Layout
    @ViewInject(R.id.tv_mail_answeruser_num)
    private TextView mAnswerUserNum; //抄送人数量
    @ViewInject(R.id.ll_mail_starttime)
    private LinearLayout mStartDateLayout; //开始日期Layout
    @ViewInject(R.id.tv_mail_starttime)
    private TextView mStartDateTv; //开始日期文本
    @ViewInject(R.id.ll_mail_endtime)
    private LinearLayout mEndDateLayout; //结束日期Layout
    @ViewInject(R.id.tv_mail_endtime)
    private TextView mEndDateTv; //开始日期文本
    @ViewInject(R.id.ll_mail_mainuser_head)
    private LinearLayout mMainUserHeadLayout; //主负责人头像Layout
    @ViewInject(R.id.tv_mail_emergency)
    private TextView mEmergencyTv; //紧急程度文本
    @ViewInject(R.id.tv_mail_file_num)
    private TextView mFileNumTv; //选择附件的名称
    @ViewInject(R.id.tv_mail_busdata)
    private TextView mBusDataTv; //业务数据
    @ViewInject(R.id.ll_mail_busdata)
    private LinearLayout mBusDataLayout; //已选业务数据Layout
    @ViewInject(R.id.tv_mail_busname)
    private TextView mBusNameTv; //已选业务数据名称

    @ViewInject(R.id.cm_select_tv_name)
    private TextView mCanSeeTv; //谁能看见文本
    @ViewInject(R.id.swipeback_scrollview)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.ll_repeat_end)
    private LinearLayout mLlRepeatEnd;
    @ViewInject(R.id.tv_repeat)
    private TextView mTvRepeat;
    @ViewInject(R.id.tv_repeat_end)
    private TextView mTvRepeatEnd;
    @ViewInject(R.id.tv_remind)
    private TextView mTvRemind;

    public static final int REQUESTCODE_ACCEPTUSER = 3; //选择接收人requestCode
    public static final int REQUESTCODE_COPYUSER = 4; //选择抄送人requestCode
    public static final int REQUESTCODE_MAINUSER = 1; //选择负责人requestCode
    public static final int REQUESTCODE_FILE = 2; //选择附件requestCode
    public static final int REQUESTCODE_BUSDATA = 145; //选择业务数据requestCode
    public static final int REQUESTCODE_CANSEE = 7; //谁能看见requestCode
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private BusDataInfo busDataInfo; //回传业务数据对象
    private ArrayList<ApiEntity.UserInfo> mAcceptList, mMakeCopyList, mMainuserList;
    private String mMakeCopyUserNumber;//抄送人id集合
    private String mMakeCopyUserName;//抄送人姓名集合
    private ApiEntity.UserInfo mainUser, acceptUser;//负责人、收件人
    private Dialog mLoadingDialog; // 加载框
    private ArrayList<ApiEntity.UserInfo> selectList; //谁能看见列表数据
    private ArrayList<ApiEntity.Files> fileRets = new ArrayList<>(); //选择的附件列表数据
    private ArrayList<ApiEntity.UserInfo> mainUserList = new ArrayList<>(); //负责人列表数据
    private SimpleDateFormat format, format2;
    private Date mStartDate, mEndDate; //开始、结束日期
    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    ///////////////////////////////////////////////////////////////////////////////////////////
    private DatePickerDialog dpdStart, dpdEnd, dpdRepeatEnd; //开始/结束时间弹框
    private ScalePopup mPopupRepeat, mPopupRepeatEnd, mPopupRemind; //重复提醒弹框
    private ScalePopup mPopupJJCD;  //紧急程度
    private List<String> mListRepeat, mListRepeatEnd, mListRemind, mListJJCD;  //重复提醒数据
    private String mStrStartTime, mStrEndTime, mStrRepeatEnd;
    public static final int REQUESTCODE_REPEAT = 999; //选择重复数据requestCode
    ///////////////////////////////////////////////////////////////////////////////////////////

    private int groupID, projectID; //列表传值，来自TimeTracking中的动态
    private boolean isEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        groupID = getIntent().getIntExtra("group_id", 0);
        projectID = getIntent().getIntExtra("project_id", 0);
        if (getIntent().hasExtra("calendar_info")) {
            isEdit = true;
        }
        init();
        if (isEdit) {
            setInitValue();
        }
    }

    private void init() {
        if (isEdit) {
            mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
        } else {
            mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips6);
        }
        mCanSeeTv.setHint("私有");
        initRepeatRemindData();     //初始化重复提醒数据
        format = new SimpleDateFormat(getString(R.string.timeformat6), Locale.getDefault());
        format2 = new SimpleDateFormat(getString(R.string.timeformat5), Locale.getDefault());
        mStartDate = new Date();
        String time = format.format(mStartDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mStartDate);
        calendar.add(Calendar.HOUR, 1);
        mEndDate = calendar.getTime();
        String endtime = format.format(mEndDate);
        mStartDateTv.setTag(time);
        mStartDateTv.setText(time);
        mEndDateTv.setTag(endtime);
        mEndDateTv.setText(endtime);
        setAllDayChecked(mAllDaySb.isChecked());   //全天事件处理
        initTime(); //选择时间监听
    }

    private void setInitValue() {
        ((TextView)findViewById(R.id.cm_header_tv_right9)).setText("保存");
        cInfo = (CalendarInfo) getIntent().getSerializableExtra("calendar_info");
        if (cInfo != null) {
            mTitleEt.setText(cInfo.Title);
            mDescEt.setText(cInfo.Remark);
            mAllDaySb.setChecked(cInfo.Allday == 1);
            showCallUser(cInfo.Receiver);
            mMakeCopyList = (ArrayList<ApiEntity.UserInfo>) TaskUtils.getUsers(cInfo.CopySender);
            showAnswerUser(mMakeCopyList);
            if (!TextUtils.isEmpty(cInfo.MainUser) && TextUtils.isDigitsOnly(cInfo.MainUser.split(",")[0])) {
                int uid = Integer.valueOf(cInfo.MainUser.split(",")[0]);
                showMainUser(uid);
            }
            mStartDateTv.setText(cInfo.StartTime);
            mEndDateTv.setText(cInfo.OverTime);
            try {
                if (cInfo.Allday == 1) {
                    mStartDate = format.parse(cInfo.StartTime + " 00:00");
                    mEndDate = format.parse(cInfo.OverTime + " 23:59");
                } else {
                    mStartDate = format.parse(cInfo.StartTime);
                    mEndDate = format.parse(cInfo.OverTime);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setAllDayChecked(mAllDaySb.isChecked());
            if (cInfo.Priority > 0 && cInfo.Priority - 1 < TaskDetailActivity.colorStr.length) {
                mEmergencyTv.setText(TaskDetailActivity.colorStr[cInfo.Priority - 1]);
            } else {
                mEmergencyTv.setText(TaskDetailActivity.colorStr[0]);
            }
            if (cInfo.NoteAddType == 3) {
                try {
                    Type type = new TypeToken<List<ApiEntity.Files>>() {}.getType();
                    ArrayList<ApiEntity.Files> fileList = new Gson().fromJson(cInfo.NoteAddPriority, type);
                    Intent data = new Intent();
                    data.putExtra("select_list", fileList);
                    onActivityResult(REQUESTCODE_FILE, Activity.RESULT_OK, data);
                } catch (Exception e) {

                }
            } else if (cInfo.NoteAddType == 6) {
                try {
                    BusDataInfo busDataInfo = new Gson().fromJson(cInfo.NoteAddPriority, BusDataInfo.class);
                    Intent data = new Intent();
                    data.putExtra("bus_info", busDataInfo);
                    onActivityResult(REQUESTCODE_BUSDATA, Activity.RESULT_OK, data);
                } catch (Exception e) {

                }
            }

            if (cInfo.REPEATTYPE == 0 && cInfo.REPEATHZ == 0) {
                mTvRepeat.setText("无");
            } else if (cInfo.REPEATTYPE == 1 && cInfo.REPEATHZ == 1) {
                mTvRepeat.setText("每天");
            } else if (cInfo.REPEATTYPE == 2 && cInfo.REPEATHZ == 1 && TextUtils.isEmpty(cInfo.REPEATWEEKVAL)) {
                mTvRepeat.setText("每周");
            } else if (cInfo.REPEATTYPE == 2 && cInfo.REPEATHZ == 2) {
                mTvRepeat.setText("每2周");
            } else if (cInfo.REPEATTYPE == 3 && cInfo.REPEATHZ == 1) {
                mTvRepeat.setText("每月");
            } else if (cInfo.REPEATTYPE == 4 && cInfo.REPEATHZ == 1) {
                mTvRepeat.setText("每年");
            } else {
                mTvRepeat.setText("自定");
            }
            mTvRepeatEnd.setText(cInfo.REPEATENDTIME);
            mLlRepeatEnd.setVisibility(cInfo.REPEATTYPE == 0 ? View.GONE : View.VISIBLE);

            if (cInfo.ISCALL == 0) {
                mTvRemind.setText("无");
            } else if (cInfo.AHEAD_MINUTE == 0) {
                mTvRemind.setText("准时");
            } else if (cInfo.AHEAD_MINUTE == 5) {
                mTvRemind.setText("5分钟前");
            } else if (cInfo.AHEAD_MINUTE == 15) {
                mTvRemind.setText("15分钟前");
            } else if (cInfo.AHEAD_MINUTE == 60) {
                mTvRemind.setText("1小时前");
            } else if (cInfo.AHEAD_MINUTE == 1440) {
                mTvRemind.setText("1天前");
            } else if (cInfo.AHEAD_MINUTE == 10080) {
                mTvRemind.setText("1周前");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right9, R.id.cm_header_btn_left9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
            case R.id.cm_header_btn_left:
                HelpUtil.hideSoftInput(this, mTitleEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String content = mTitleEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(this, "请输入邮件主题！");
                } else if (TextUtils.isEmpty(mDescEt.getText())) {
                    ToastUtil.showToast(this, "邮件内容不能为空！");
                } else {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTimeInMillis(HelpUtil.getTime(mStartDateTv.getTag().toString()));
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTimeInMillis(HelpUtil.getTime(mEndDateTv.getTag().toString()));
                    if (startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()) {
                        Toast.makeText(this, R.string.endingtime_notlessthan_starttime, Toast.LENGTH_SHORT).show();
                    } else {
                        if (acceptUser == null) {
                            ToastUtil.showToast(this, "请选择收件人！");
                        } else if (mainUser == null) {
                            ToastUtil.showToast(this, "请选择负责人！");
                        } else if (cInfo.Priority == 0) {
                            ToastUtil.showToast(this, "请选择紧急程度！");
                        } else if (busDataInfo == null && (fileRets == null || fileRets.size() == 0)) {
                            ToastUtil.showToast(this, "请选择业务数据或附件！");
                        } else {
                            send();
                        }
                    }
                }
                break;
        }
    }

    @Event(value = {R.id.ll_mail_calluser_root, R.id.ll_mail_answeruser_root, R.id.ll_mail_mainuser_root,
            R.id.cm_select_ll_select, R.id.ll_mail_emergency_root, R.id.ll_mail_file_root, R.id.ll_mail_busdata_root
            , R.id.ll_repeat, R.id.ll_repeat_end, R.id.ll_remind})
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
            case R.id.ll_mail_calluser_root: //选择收件人
                Intent acceptListIntent = new Intent(this, ContactSelectActivity.class);
                acceptListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                acceptListIntent.putExtra("select_user", acceptUser);
//                acceptListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
//                acceptListIntent.putExtra("select_list", mAcceptList);
                acceptListIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                acceptListIntent.putExtra("click_pos_y", location[1]);
                startActivityForResult(acceptListIntent, REQUESTCODE_ACCEPTUSER);
                break;
            case R.id.ll_mail_answeruser_root: //选择抄送人
                Intent copyListIntent = new Intent(this, ContactSelectActivity.class);
                copyListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                copyListIntent.putExtra("select_list", mMakeCopyList);
                copyListIntent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                copyListIntent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(copyListIntent, REQUESTCODE_COPYUSER);
                break;
            case R.id.ll_mail_mainuser_root: //选择负责人
                Intent mainNuserListIntent = new Intent(this, ContactSelectActivity.class);
                mainNuserListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                mainNuserListIntent.putExtra("select_user", mainUser);
//                mainNuserListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
//                mainNuserListIntent.putExtra("select_list", mMainuserList);
                mainNuserListIntent.putExtra("start_anim", false);
                int[] location3 = new int[2];
                v.getLocationOnScreen(location3);
                mainNuserListIntent.putExtra("click_pos_y", location3[1]);
                startActivityForResult(mainNuserListIntent, REQUESTCODE_MAINUSER);
                break;
            case R.id.ll_mail_file_root: //选择附件
                Intent repositoryIntent = new Intent(this, FileSelectActivity.class);
                repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, cInfo.Line_File);
                repositoryIntent.putExtra("start_anim", false);
                int[] location4 = new int[2];
                v.getLocationOnScreen(location4);
                repositoryIntent.putExtra("click_pos_y", location4[1]);
                startActivityForResult(repositoryIntent, REQUESTCODE_FILE);
                break;
            case R.id.ll_mail_busdata_root: //选择业务数据
                Intent intent1 = new Intent(this, BusDataActivity.class);
                intent1.putExtra("start_anim", false);
                int[] location5 = new int[2];
                v.getLocationOnScreen(location5);
                intent1.putExtra("click_pos_y", location5[1]);
                startActivityForResult(intent1, REQUESTCODE_BUSDATA);
                break;
            case R.id.cm_select_ll_select: //谁能看见
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                intent.putExtra("has_oneself", false);
                intent.putExtra("start_anim", false);
                int[] location6 = new int[2];
                v.getLocationOnScreen(location6);
                intent.putExtra("click_pos_y", location6[1]);
                startActivityForResult(intent, REQUESTCODE_CANSEE);
                break;
            case R.id.ll_mail_emergency_root: //打开紧急程度
                HelpUtil.hideSoftInput(this, mTitleEt);
                mPopupJJCD = new ScalePopup(this, mListJJCD);
                mPopupJJCD.showPopupWindow();
                mPopupJJCD.setOnScaleItemClickListener(new OnScalePopupItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            mEmergencyTv.setText(mListJJCD.get(position));
                            cInfo.Priority = position;
                            mPopupJJCD.dismiss();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                case REQUESTCODE_ACCEPTUSER: //选择收件人回传的数据
                    acceptUser = (ApiEntity.UserInfo) data.getSerializableExtra("select_user");
                    showCallUser(acceptUser);
                    /*mMailAcceptUser.setVisibility(View.VISIBLE);
                    mMailAcceptUser.setText(acceptUser.Name);
                    mMailAcceptUser.setTextColor(getResources().getColor(R.color.task_modify_charge));*/
                    break;
                case REQUESTCODE_COPYUSER: //选择抄送人回传的数据
                    mMakeCopyList = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("select_list");
                    mMakeCopyUserNumber = TaskUtils.members2string(mMakeCopyList);
                    mMakeCopyUserName = TaskUtils.names2string(mMakeCopyList);
                    showAnswerUser(mMakeCopyList);
                    /*mMailAnswerUser.setVisibility(View.VISIBLE);
                    mMailAnswerUser.setText(mMakeCopyUserName);
                    mMailAnswerUser.setTextColor(getResources().getColor(R.color.task_modify_charge));*/
                    break;
                case REQUESTCODE_MAINUSER: //选择负责人回传的数据
                    mainUser = (ApiEntity.UserInfo) data.getSerializableExtra("select_user");
                    mainUserList.add(mainUser);
                    showMainUser(mainUser);
                    /*mMailMailUser.setVisibility(View.VISIBLE);
                    mMailMailUser.setText(mainUser.Name);
                    mMailMailUser.setTextColor(getResources().getColor(R.color.task_modify_charge));*/
                    break;
                case REQUESTCODE_FILE: //选择附件回传的数据
                    fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    cInfo.Line_File = HelpUtil.files2StrID(fileRets);
                    if (fileRets == null || fileRets.size() == 0) {
                        mFileNumTv.setVisibility(View.INVISIBLE);
                    } else {
                        mFileNumTv.setVisibility(View.VISIBLE);
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
                        mFileNumTv.setText(builder);

                        mBusNameTv.setText("");
                        mBusDataLayout.setVisibility(View.GONE);
                        mBusDataTv.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUESTCODE_CANSEE: //选择人员回传的数据
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
                    mCanSeeTv.setText(builder);
                    if (selectList.size() == 0) {
                        mCanSeeTv.setText("");
                    }
                    break;
                case REQUESTCODE_BUSDATA: //选择业务数据回传的数据
                    busDataInfo = (BusDataInfo) data.getSerializableExtra("bus_info");
                    if (busDataInfo != null) {
                        if (busDataInfo.ID > 0) {
                            mBusNameTv.setText(busDataInfo.Text);
                            mBusDataLayout.setVisibility(View.VISIBLE);
                            mBusDataTv.setVisibility(View.GONE);

                            cInfo.Line_File = "";
                            mFileNumTv.setVisibility(View.INVISIBLE);
                        } else {
                            mBusNameTv.setText("");
                            mBusDataLayout.setVisibility(View.GONE);
                            mBusDataTv.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 发布邮件
     */
    private void send() {
        cInfo = getCInfo();
        API.TalkerAPI.AddCalendar(cInfo, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(MailCreateActivity.this, getString(R.string.service_excep));
                mLoadingDialog.dismiss();
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respInfo != null && respInfo.State == 1) {
                    Intent intent = new Intent(WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    sendBroadcast(intent); // 刷新日程列表
                    intent = new Intent(CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    sendBroadcast(intent); // 刷新日程月视图列表
                    intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    if (groupID > 0) {
                        intent = new Intent(TimeTrackingWebFragment.ACTION_REFRESH_TIMEDYNAMIC);
                    }
                    sendBroadcast(intent); // 刷新动态列表
                    if (isEdit) {
                        ToastUtil.showToast(MailCreateActivity.this, "保存成功！", R.drawable.tishi_ico_gougou);
                        setResult(Activity.RESULT_OK);
                    } else {
                        ToastUtil.showToast(MailCreateActivity.this, getString(R.string.groupcreate_success), R.drawable.tishi_ico_gougou);
                    }
                    finish();
                } else {
                    if (isEdit) {
                        Toast.makeText(MailCreateActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MailCreateActivity.this, getString(R.string.groupcreate_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 待提交的邮件对象
     *
     * @return
     */
    private CalendarInfo getCInfo() {
        cInfo.UserID = PrefsUtil.readUserInfo().ID;
        cInfo.Place = "";
        cInfo.MainUser = mainUser.ID + "";
        cInfo.T_MainUser = mainUser.Name;
        cInfo.Receiver = acceptUser.ID;
        cInfo.T_Receiver = acceptUser.Name;
        cInfo.CopySender = mMakeCopyUserNumber;
        cInfo.T_CopySender = mMakeCopyUserName;
        cInfo.T_MainUser = TaskUtils.getUsers(mainUser.ID + "").get(0).Name;
        cInfo.UserID = PrefsUtil.readUserInfo().ID;
        cInfo.mainUserList = new ArrayList<>();
        ApiEntity.Role mUser = new ApiEntity.Role();
        mUser.Name = mainUser.Name;
        mUser.DeptId = mainUser.DeptID;
        cInfo.mainUserList.add(mUser);
        if (groupID > 0) {
            cInfo.Line_Group = Integer.toString(groupID);
        }
        if (projectID > 0) {
            cInfo.Line_Project = Integer.toString(projectID);
        }
        ArrayList<ApiEntity.NoteRole> nrList = new ArrayList<ApiEntity.NoteRole>();
        if (selectList != null && selectList.size() > 0) {
            for (int i = 0, size = selectList.size(); i < size; i++) {
                if (selectList.get(i).ID == mainUser.ID)
                    continue;
                ApiEntity.NoteRole role = new ApiEntity.NoteRole();
                role.ID = selectList.get(i).ID;
                role.Name = selectList.get(i).Name;
                role.Image = selectList.get(i).Image;
                role.Type = ApiEnum.NoteRoleTypes.User;
                nrList.add(role);
            }
        }
        ApiEntity.NoteRole mainRole = new ApiEntity.NoteRole();
        mainRole.ID = mainUser.ID;
        mainRole.Name = mainUser.Name;
        mainRole.Type = ApiEnum.NoteRoleTypes.User;
        nrList.add(mainRole);
        Gson gson = new Gson();
        cInfo.NoteRoles = nrList;
        cInfo.NoteContent = "";
        cInfo.IsJobSync = 1;
        cInfo.Color = 3;
        cInfo.Type = ApiEnum.UserNoteAddTypes.Email;
        cInfo.NoteType = ApiEnum.UserNoteAddTypes.Email;
        cInfo.Title = mTitleEt.getText().toString();
        cInfo.Remark = mDescEt.getText().toString();
        cInfo.StartTime = mStartDateTv.getTag().toString();
        cInfo.OverTime = mEndDateTv.getTag().toString();
        cInfo.Allday = mAllDaySb.isChecked() ? 1 : 0;
        if (mBusDataLayout.getVisibility() == View.VISIBLE) {
            cInfo.NoteAddPriority = gson.toJson(busDataInfo);
            cInfo.NoteAddType = 6;
            cInfo.HandlerTitle = busDataInfo.Text;
            cInfo.HandlerRemark = "业务数据";
        } else {
            cInfo.NoteAddPriority = gson.toJson(fileRets);
            cInfo.NoteAddType = ApiEnum.UserNoteAddTypes.File;
            cInfo.HandlerRemark = "附件";
        }

        cInfo.Line_Project = "[]";
        cInfo.Line_Task = "[]";
        cInfo.Line_File = HelpUtil.files2StrID(fileRets);
        cInfo.State = 1;
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

    @Override
    protected void onPause() {
        super.onPause();
        mTitleEt.clearFocus();
        mDescEt.clearFocus();

    }

    private void showCallUser(ApiEntity.UserInfo userInfo) {
//        mMailAcceptUser.setVisibility(View.GONE);
//        mCallUserNum.setVisibility(View.GONE);
        mCallUserHeadLayout.setVisibility(View.VISIBLE);
        mCallUserHeadLayout.removeAllViews();
        if (userInfo != null) {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
            TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mCallUserHeadLayout.addView(circleImageView, params);
        } else {
            mCallUserHeadLayout.setVisibility(View.GONE);
//            mMailAcceptUser.setVisibility(View.VISIBLE);
//            mCallUserNum.setVisibility(View.GONE);
        }
    }

    private void showAnswerUser(ArrayList<ApiEntity.UserInfo> moreUsers) {
//        mMailAnswerUser.setVisibility(View.GONE);
        mAnswerUserHeadLayout.setVisibility(View.VISIBLE);
        mAnswerUserHeadLayout.removeAllViews();
        if (moreUsers != null && moreUsers.size() > 0) {
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                CircleImageView circleImageView = new CircleImageView(this);
                circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
                params.leftMargin = DisplayUtil.dip2px(this, 5);
                params.width = DisplayUtil.dip2px(this, 30);
                params.height = DisplayUtil.dip2px(this, 30);
                mAnswerUserHeadLayout.addView(circleImageView, params);
                if (i == 4) {
                    break;
                }
            }
            mAnswerUserNum.setVisibility(moreUsers.size() > 5 ? View.VISIBLE : View.GONE);
            mAnswerUserNum.setText(moreUsers.size() + "");
        } else {
            mAnswerUserHeadLayout.setVisibility(View.GONE);
//            mMailAnswerUser.setVisibility(View.VISIBLE);
            mAnswerUserNum.setVisibility(View.GONE);
        }
    }

    private void showMainUser(ApiEntity.UserInfo userInfo) {
//        mMailMailUser.setVisibility(View.GONE);
//        mMainUserNum.setVisibility(View.GONE);
        mMainUserHeadLayout.setVisibility(View.VISIBLE);
        mMainUserHeadLayout.removeAllViews();
        if (userInfo != null) {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
            TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mMainUserHeadLayout.addView(circleImageView, params);
        } else {
            mMainUserHeadLayout.setVisibility(View.GONE);
//            mMailMailUser.setVisibility(View.VISIBLE);
//            mMainUserNum.setVisibility(View.GONE);
        }
    }

    private void showCallUser(int uid) {
//        mCallUserTv.setVisibility(View.GONE);
        mCallUserHeadLayout.setVisibility(View.VISIBLE);
        mCallUserHeadLayout.removeAllViews();
        ApiEntity.UserInfo userInfo = EMWApplication.personMap.get(uid);
        acceptUser = userInfo;
        if (userInfo != null) {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
            TaskUtils.setCivImageView(this, userInfo.Image, circleImageView);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mCallUserHeadLayout.addView(circleImageView, params);
        } else {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setTextBg(EMWApplication.getIconColor(uid), "", 30);
            params.leftMargin = DisplayUtil.dip2px(this, 5);
            params.width = DisplayUtil.dip2px(this, 30);
            params.height = DisplayUtil.dip2px(this, 30);
            mCallUserHeadLayout.addView(circleImageView, params);
//            mCallUserHeadLayout.setVisibility(View.GONE);
//            mCallUserTv.setVisibility(View.VISIBLE);
        }
    }

    private void showMainUser(int uid) {
//        mMainUserTv.setVisibility(View.GONE);
        mMainUserHeadLayout.setVisibility(View.VISIBLE);
        mMainUserHeadLayout.removeAllViews();
        ApiEntity.UserInfo userInfo = EMWApplication.personMap.get(uid);
        mainUser = userInfo;
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
        }
    }

    /**
     * 时间选择监听
     */
    private void initTime() {
        mStartDateLayout.setOnClickListener(new View.OnClickListener() {
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

        mEndDateLayout.setOnClickListener(new View.OnClickListener() {
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
                                Intent intent = new Intent(MailCreateActivity.this, FreeRepeatActivity.class);
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
        mListJJCD = new ArrayList<>();

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

        mListJJCD.add("紧急程度");
        mListJJCD.add("普通");
        mListJJCD.add("紧急");
        mListJJCD.add("非常紧急");
    }

    /**
     * 全天事件选择状态处理
     *
     * @param isChecked
     */
    private void setAllDayChecked(boolean isChecked) {

        // 全天时间开关
        mAllDaySb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        mAllDayLayout.setOnClickListener(new View.OnClickListener() {

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

}
