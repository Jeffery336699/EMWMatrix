package cc.emw.mobile.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnShowListener;
import com.gc.materialdesign.views.LayoutRipple2;
import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.calendar.fragment.WeekFragment;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by sunny.du on 2017/5/20.
 * Chat版约会创建界面
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_chat_create_detail)
public class ChatDynamicCreateActivity extends BaseActivity implements OnShowListener, OnDismissListener {
    private PoiItem chatPoiBean;
    private ArrayList<ApiEntity.UserInfo> mainUser; //选择的主负责人数据
    private ArrayList<ApiEntity.UserInfo> callUserList;
    private Date mStartDate, mEndDate; //开始、结束日期
    private SimpleDateFormat format, format2;
    private ArrayList<ApiEntity.UserInfo> moreUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");
        initData();
        bindView();
        bindEvent();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initData() {
        Intent intentData = getIntent();
        chatPoiBean = intentData.getParcelableExtra("chat_poi_bean");
        format = new SimpleDateFormat(getString(R.string.timeformat6));
        format2 = new SimpleDateFormat(getString(R.string.timeformat5));
        mStartDate = new Date();
        callUserList=new ArrayList<>();
        String time = format.format(mStartDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mStartDate);
        calendar.add(Calendar.HOUR, 1);
        mEndDate = calendar.getTime();
        final String endtime = format.format(mEndDate);
        mBtnChatDyanmicStartDate.setTag(time);
        mBtnChatDyanmicStartDate.setText(time);
        mBtnChatDyanmicEndDate.setTag(endtime);
        mBtnChatDyanmicEndDate.setText(endtime);
    }

    private void bindView() {
        if(chatPoiBean != null) {
            mEtChatDynamicCreateTitle.setText(chatPoiBean.getTitle());
            mEtChatDynamicTelNum.setText(chatPoiBean.getSnippet());
        }

    }

    private void bindEvent() {
        final TimePickerView mStartPopupWindow = new TimePickerView(this, TimePickerView.Type.ALL);// 时间选择器
        mStartPopupWindow.setTitle(getString(R.string.beg_time));
        mStartPopupWindow.setCancelable(true);
        mStartPopupWindow.setOnShowListener(this);
        mStartPopupWindow.setOnDismissListener(this);
        mStartPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mStartDate = date;
                Calendar nowCal = Calendar.getInstance();
                String startTime = format.format(date);
                mBtnChatDyanmicStartDate.setTag(startTime);
                mBtnChatDyanmicStartDate.setText(startTime);
            }
        });
        mLlChatDyanmicStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartPopupWindow.show();
            }
        });

        final TimePickerView mEndPopupWindow = new TimePickerView(this, TimePickerView.Type.ALL);// 时间选择器
        mEndPopupWindow.setTitle(getString(R.string.ending_time));
        mEndPopupWindow.setCancelable(true);
        mEndPopupWindow.setOnShowListener(this);
        mEndPopupWindow.setOnDismissListener(this);
        mEndPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mEndDate = date;
                String endTime = format.format(date);
                mBtnChatDyanmicEndDate.setTag(endTime);
                mBtnChatDyanmicEndDate.setText(endTime);
            }
        });
        mLlChatDynamicEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndPopupWindow.show();
            }
        });
        mRgSelectPayType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == mRgSelectPayType1.getId()) {
                    payType=0;
                    mLlChatDynamicPayRoot.setVisibility(View.GONE);
                } else if (checkedId == mRgSelectPayType2.getId()) {
                    payType=1;
                    mLlChatDynamicPayRoot.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public void onShow(Object o) {

    }

    @Override
    public void onDismiss(Object o) {

    }
    @Event(value = {R.id.ll_chat_dynamic_mainuser_root, R.id.ll_chat_dynamic_calluser_root,R.id.but_chat_poi_back,R.id.but_chat_poi_next,
            R.id.cm_header_btn_left9,R.id.cm_header_tv_right9})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_chat_poi_next://下一步
            case R.id.cm_header_tv_right9:
                if(TextUtils.isEmpty(mEtChatDynamicCreateTitle.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "约会标题不能为空");
                }else if(TextUtils.isEmpty(mEtChatDynamicTelNum.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "约会地点不能为空");
                }else if(TextUtils.isEmpty(mEtChatDynamicDes.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "约会详情不能为空");
                }else if(TextUtils.isEmpty(mBtnChatDyanmicStartDate.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "开始时间不能为空");
                }else if(TextUtils.isEmpty(mBtnChatDyanmicEndDate.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "结束时间不能为空");
                }else if(TextUtils.isEmpty(mTvChatDyanmicMainUserNum.getText().toString())) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "负责人不能为空");
                }else if (payType==-1){
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "请选择支付类型");
                }else if (payType!=-1 && mLlChatDynamicPayRoot.getVisibility()==View.VISIBLE &&mEtChatDynamicPayMoney.getText().toString() !=null &&"".equals(mEtChatDynamicPayMoney.getText().toString())){
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, "请输入金额");
                }else{
                    send();
                }
                break;
            case R.id.but_chat_poi_back://返回
            case R.id.cm_header_btn_left9:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.ll_chat_dynamic_mainuser_root:
                Intent mainListIntent = new Intent(this, ContactSelectActivity.class);
                mainListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                mainListIntent.putExtra("select_list", mainUser);
                mainListIntent.putExtra("start_anim", false);
                int[] location5 = new int[2];
                v.getLocationOnScreen(location5);
                mainListIntent.putExtra("click_pos_y", location5[1]);
                startActivityForResult(mainListIntent, ChatContent.CHAT_REQUESTCODE_MAINUSER);
                break;
            case R.id.ll_chat_dynamic_calluser_root:
                Intent callListIntent = new Intent(this, ContactSelectActivity.class);
                callListIntent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                callListIntent.putExtra("select_list", callUserList);
                callListIntent.putExtra("start_anim", false);
                int[] location4 = new int[2];
                v.getLocationOnScreen(location4);
                callListIntent.putExtra("click_pos_y", location4[1]);
                startActivityForResult(callListIntent, ChatContent.CHAT_REQUESTCODE_CALLUSER);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ChatContent.CHAT_REQUESTCODE_MAINUSER: //负责人
                    mainUser = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("select_list");
                    showMainUser(mainUser);
                    break;
                case ChatContent.CHAT_REQUESTCODE_CALLUSER://参与人
                    callUserList = (ArrayList<ApiEntity.UserInfo>) data.getSerializableExtra("select_list");
                    showCallUser(callUserList);
                    break;
            }
        }
    }

    private void showMainUser(ArrayList<ApiEntity.UserInfo> userInfo) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < userInfo.size(); i++) {
            sb.append(userInfo.get(i).Name + " ");
        }
        mTvChatDyanmicMainUserNum.setText(sb.toString());
    }

    private void showCallUser(ArrayList<ApiEntity.UserInfo> moreUsers) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < moreUsers.size(); i++) {
            sb.append(moreUsers.get(i).Name + " ");
        }
        mTvChatDyanmicCallUserNum.setText(sb.toString());
    }


    /**
     * 发布日程
     */
    private ApiEntity.UserSchedule cInfo = new ApiEntity.UserSchedule(); //传给服务器的对象
    private void send() {
        cInfo = getCInfo();
        API.TalkerAPI.AddCalendar(cInfo, new RequestCallback<ApiEntity.APIResult>(
                ApiEntity.APIResult.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ChatDynamicCreateActivity.this, getString(R.string.service_excep) + ex.toString(),  Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
                if (respInfo != null && respInfo.State == 1) {
                    ToastUtil.showToast(ChatDynamicCreateActivity.this, getString(R.string.groupcreate_success), R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(
                            WeekFragment.ACTION_REFRESH_SCHEDULE_LIST);
                    sendBroadcast(intent); // 刷新日程列表
                    intent = new Intent(
                            CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                    sendBroadcast(intent); // 刷新日程月视图列表
                    intent = new Intent(
                            DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    sendBroadcast(intent); // 刷新动态列表
                    finish();
                } else {
                    Toast.makeText(ChatDynamicCreateActivity.this, getString(R.string.groupcreate_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 待提交的日程对象
     *
     * @return
     */
    private ApiEntity.UserSchedule getCInfo() {
        cInfo.Place = mEtChatDynamicTelNum.getText().toString();//约会地点
        cInfo.MainUser = mainUser.get(0).ID + "";
        cInfo.T_MainUser = mTvChatDyanmicMainUserNum.getText().toString();
        cInfo.Title = mEtChatDynamicCreateTitle.getText().toString();//标题
        cInfo.Remark = mEtChatDynamicDes.getText().toString();//详情
        cInfo.StartTime = mBtnChatDyanmicStartDate.getTag().toString();//开始时间
        cInfo.OverTime = mBtnChatDyanmicEndDate.getTag().toString();//结束时间
        if(callUserList.size() != 0) {
            cInfo.MustActor = TaskUtils.members2string(callUserList);//参与人
        }
        cInfo.Type = ApiEnum.UserNoteAddTypes.Appoint;
        cInfo.NoteType = ApiEnum.UserNoteAddTypes.Appoint;
        cInfo.T_MainUser = mainUser.get(0).Name;

        ArrayList<ApiEntity.NoteRole> nrList = new ArrayList<>();
        ApiEntity.NoteRole mainRole = new ApiEntity.NoteRole();
        if(mainUser != null && mainUser.size() != 0){
            for (ApiEntity.UserInfo userInfo:mainUser){
                mainRole.ID = userInfo.ID;
                mainRole.Name = userInfo.Name;
                mainRole.Type = ApiEnum.NoteRoleTypes.User;
                mainRole.Image=userInfo.Image;
                nrList.add(mainRole);
            }
        }
        cInfo.NoteRoles = nrList;
        cInfo.NoteContent = "";
        cInfo.Service = 0;
        cInfo.Receiver = 0;
        cInfo.Line_Project = "[]";
        cInfo.Line_Task = "[]";
        cInfo.AppointPayType=payType;
        cInfo.AppointPayVal=mEtChatDynamicPayMoney.getText().toString();
        cInfo.NotePriority = "[" + new Gson().toJson(cInfo) + "]";
        Log.d("sunny----->","[" + new Gson().toJson(cInfo) + "]");
        return cInfo;
    }

    @ViewInject(R.id.itv_chat_dynamic_create_close)
    private IconTextView mItvChatDynamicCreateClose;
    @ViewInject(R.id.et_chat_dynamic_create_title)
    private EditText mEtChatDynamicCreateTitle;
    @ViewInject(R.id.et_chat_dynamic_des)
    private EditText mEtChatDynamicDes;
    @ViewInject(R.id.et_chat_dynamic_telnum)
    private EditText mEtChatDynamicTelNum;
    @ViewInject(R.id.ll_chat_dynamic_mainuser_root)
    private LayoutRipple2 mLlChatDynamicMainuserRoot;
    @ViewInject(R.id.tv_chat_dynamic_mainuser)
    private TextView mTvChatDynamicMainUser;
    @ViewInject(R.id.tv_chat_dynamic_mainuser_num)
    private TextView mTvChatDyanmicMainUserNum;//负责人
    @ViewInject(R.id.ll_chat_dynamic_calluser_root)
    private LayoutRipple2 mLlChatDynamicCallUserRoot;
    @ViewInject(R.id.tv_chat_dynamic_calluser)
    private TextView mTvChatDyanmicCallUser;
    @ViewInject(R.id.tv_chat_dynamic_calluser_num)
    private TextView mTvChatDyanmicCallUserNum;
    @ViewInject(R.id.ll_chat_dynamic_startdate)
    private LayoutRipple2 mLlChatDyanmicStartDate;
    @ViewInject(R.id.btn_chat_dynamic_startdate)
    private TextView mBtnChatDyanmicStartDate;
    @ViewInject(R.id.ll_chat_dynamic_enddate)
    private LayoutRipple2 mLlChatDynamicEndDate;
    @ViewInject(R.id.btn_chat_dynamic_enddate)
    private TextView mBtnChatDyanmicEndDate;

    @ViewInject(R.id.rg_select_pay_type)
    private RadioGroup mRgSelectPayType;
    @ViewInject(R.id.rg_select_pay_type1)
    private RadioButton mRgSelectPayType1;
    @ViewInject(R.id.rg_select_pay_type2)
    private RadioButton mRgSelectPayType2;
    private int payType=-1;//支付类型 ：0 请客，1AA制
    @ViewInject(R.id.ll_chat_dynamic_pay_number)
    private LinearLayout mLlChatDynamicPayRoot;
    @ViewInject(R.id.et_chat_dynamic_pay_money)
    private EditText mEtChatDynamicPayMoney;







//TODO  暂时保留，处理负责人和参与人集合的模板代码
    //        mainUserName = new StringBuilder();
//        moreUserName = new StringBuilder();
//        List<ApiEntity.UserInfo> userMain = new ArrayList<>();
//        List<ApiEntity.UserInfo> userMore = new ArrayList<>();
//        if (userSchedule != null) {
//            try {
//                userMain = TaskUtils.getUsers(userSchedule.MainUser);
//            } catch (Exception x) {
//                Log.d("sunny----->", "数据异常="+x);
//            }
//            try {
//                userMore = TaskUtils.getUsers(userSchedule.MustActor);
//            } catch (Exception x) {
//                Log.d("sunny----->", "数据异常="+x);
//            }
//            if(userMain != null && userMain.size()!=0) {
//                for (int i = 0; i < userMain.size(); i++) {
//                    mainUserName.append(userMain.get(i).Name + " ");
//                }
//            }
//            if(userMain != null && userMore.size()!=0) {
//                for (int i = 0; i < userMore.size(); i++) {
//                    moreUserName.append(userMore.get(i).Name + " ");
//                }
//            }
//        }
}
