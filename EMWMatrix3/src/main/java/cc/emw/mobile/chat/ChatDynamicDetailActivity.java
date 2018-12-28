package cc.emw.mobile.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.LayoutRipple2;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by sunny.du on 2017/5/20.
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_chat_dyanmic_detail)
public class ChatDynamicDetailActivity extends BaseActivity {
    //    退出
    @ViewInject(R.id.itv_chat_dynamic_close)
    private IconTextView mItvChatDynamicCloseActivity;
    //    标题
    @ViewInject(R.id.tv_chat_dynamic_title)
    private TextView mTvChatDynamicTitle;
    //    内容
    @ViewInject(R.id.tv_chat_dynamic_desc)
    private TextView mTvChatDynamicDesc;
    //    地址
    @ViewInject(R.id.tv_chat_dynamic_address)
    private TextView mTvChatDynamicAddress;
    //    负责人
    @ViewInject(R.id.tv_chat_dynamic_mainuser)
    private TextView mTvChatDynamicMainUser;
    //    参与人
    @ViewInject(R.id.tv_chat_dynamic_calluser)
    private TextView mTvChatDynamicCallUser;
    //    开始时间
    @ViewInject(R.id.tv_chat_dynamic_startdate)
    private TextView mTvChatDynamicStartDate;
    //    结束时间
    @ViewInject(R.id.tv_chat_dynamic_enddate)
    private TextView mTvChatDynamicEndAte;
    @ViewInject(R.id.rg_select_pay_type)
    private TextView mRgSelectPayType;
    @ViewInject(R.id.ll_chat_dynamic_pay_number)
    private LayoutRipple2 mLlChatDynamicPayNumber;
    @ViewInject(R.id.et_chat_dynamic_pay_money)
    private TextView mEtChatDynamicPayMoney;

    private ApiEntity.UserSchedule userSchedule;
    private StringBuilder mainUserName;
    private StringBuilder moreUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        bindEvent();
    }

    private void initData() {
        Intent intentData = getIntent();
//        int userScheduleId = intentData.getIntExtra("userNoteID", -1);
        userSchedule = (ApiEntity.UserSchedule) intentData.getSerializableExtra("userNote");
        mainUserName = new StringBuilder();
        moreUserName = new StringBuilder();
        List<ApiEntity.NoteRole> userMain = new ArrayList<>();
        List<ApiEntity.UserInfo> userMore;
        if (userSchedule != null) {
            userMain.addAll(userSchedule.NoteRoles);
            try {
                userMore = TaskUtils.getUsers(userSchedule.MustActor);
                if (userMain != null && userMain.size() != 0) {
                    for (int i = 0; i < userMain.size(); i++) {
                        mainUserName.append(userMain.get(i).Name + " ");
                    }
                }
                if (userMore != null && userMore.size() != 0) {
                    for (int i = 0; i < userMore.size(); i++) {
                        moreUserName.append(userMore.get(i).Name + " ");
                    }
                }
                bindView();
            } catch (Exception x) {
                dialogShow();
            }
        }else {
            dialogShow();
        }
    }
    private void dialogShow(){
        AlertDialog dialog = new AlertDialog(ChatDynamicDetailActivity.this).builder();
        dialog.setCancelable(false).setMsg("消息查看失败,请稍候再试!");
        dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).show();
    }
    private void bindView() {
        if (userSchedule != null) {
            mTvChatDynamicTitle.setText(userSchedule.Title);
            mTvChatDynamicDesc.setText(userSchedule.Remark);
            mTvChatDynamicAddress.setText(userSchedule.Place);
            mTvChatDynamicMainUser.setText(mainUserName);
            mTvChatDynamicCallUser.setText(moreUserName);
            mTvChatDynamicStartDate.setText(userSchedule.StartTime);
            mTvChatDynamicEndAte.setText(userSchedule.OverTime);
            if (userSchedule.AppointPayType == 0) {
                mRgSelectPayType.setText("请客");
                mLlChatDynamicPayNumber.setVisibility(View.GONE);
            } else if (userSchedule.AppointPayType == 1) {
                mRgSelectPayType.setText("AA制");
                mLlChatDynamicPayNumber.setVisibility(View.VISIBLE);
                mEtChatDynamicPayMoney.setText(userSchedule.AppointPayVal);
            }
        }
    }
    private void bindEvent() {
        mItvChatDynamicCloseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    /**
//     * 根据ID获取日程详情  TODO   接口获取到的实例有问题
//     */
//    private void getCalendarById(int id) {
//        API.TalkerAPI.GetCalenderById(id, new RequestCallback<ApiEntity.UserSchedule>(ApiEntity.UserSchedule.class) {
//            @Override
//            public void onStarted() {
//            }
//
//            @Override
//            public void onError(Throwable arg0, boolean arg1) {
//                AlertDialog dialog = new AlertDialog(ChatDynamicDetailActivity.this).builder();
//                dialog.setCancelable(false).setMsg("消息查看失败,请稍候再试!");
//                dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                }).show();
//            }
//
//            @Override
//            public void onFinished() {
//            }
//
//            @Override
//            public void onParseSuccess(List<ApiEntity.UserSchedule> respList) {
//                if (respList.size() > 0) {
//                    ApiEntity.UserSchedule userSchedule = respList.get(0);
//                    if (userSchedule != null) {
//                        mainUserName = new StringBuilder();
//                        moreUserName = new StringBuilder();
//                        List<ApiEntity.NoteRole> userMain = new ArrayList<>();
//                        List<ApiEntity.UserInfo> userMore ;
//                        if (userSchedule != null) {
//                            userMain.addAll(userSchedule.NoteRoles);
//                            Log.d("sunny----->","userSchedule.MustActor="+userSchedule.MustActor);
//                            try {
//                                userMore = TaskUtils.getUsers(userSchedule.MustActor);
//                                if (userMain != null && userMain.size() != 0) {
//                                    for (int i = 0; i < userMain.size(); i++) {
//                                        mainUserName.append(userMain.get(i).Name + " ");
//                                    }
//                                }
//                                if (userMore != null && userMore.size() != 0) {
//                                    for (int i = 0; i < userMore.size(); i++) {
//                                        moreUserName.append(userMore.get(i).Name + " ");
//                                    }
//                                }
//                            } catch (Exception x) {
//                                AlertDialog dialog = new AlertDialog(ChatDynamicDetailActivity.this).builder();
//                                dialog.setCancelable(false).setMsg("消息查看失败,请稍候再试!");
//                                dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        finish();
//                                    }
//                                }).show();
//                            }
//                        }
//
//                        mTvChatDynamicTitle.setText(userSchedule.Title);
//                        mTvChatDynamicDesc.setText(userSchedule.Remark);
//                        mTvChatDynamicAddress.setText(userSchedule.Place);
//                        mTvChatDynamicMainUser.setText(mainUserName);
//                        mTvChatDynamicCallUser.setText(moreUserName);
//                        mTvChatDynamicStartDate.setText(userSchedule.StartTime);
//                        mTvChatDynamicEndAte.setText(userSchedule.OverTime);
//                        if (userSchedule.AppointPayType == 0) {
//                            mRgSelectPayType.setText("请客");
//                            mLlChatDynamicPayNumber.setVisibility(View.GONE);
//                        } else if (userSchedule.AppointPayType == 1) {
//                            mRgSelectPayType.setText("AA制");
//                            mLlChatDynamicPayNumber.setVisibility(View.VISIBLE);
//                            mEtChatDynamicPayMoney.setText(userSchedule.AppointPayVal);
//                        }
//                    }
//                }
//            }
//        });
//    }
}
