package cc.emw.mobile.me;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.SwitchButton;

@ContentView(R.layout.activity_notification)
public class NotificationActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mRightTv;

    @ViewInject(R.id.sb_setting_message_task)
    private SwitchButton sbTask;
    @ViewInject(R.id.sb_setting_message_calendar)
    private SwitchButton sbCalendar;
    @ViewInject(R.id.sb_setting_message_plan)
    private SwitchButton sbPlan;
    @ViewInject(R.id.sb_setting_message_concern)
    private SwitchButton sbConcern;
    @ViewInject(R.id.sb_setting_message_replay)
    private SwitchButton sbReply;
    @ViewInject(R.id.sb_setting_message_collect)
    private SwitchButton sbCollect;
    @ViewInject(R.id.sb_setting_message_send_other)
    private SwitchButton sbSendOther;

    private Dialog mLoadingDialog;
    private List<ApiEntity.UserSetting> userSettings = new ArrayList<>();
    private boolean follow;
    private boolean reply;
    private boolean collect;
    private boolean transend;
    private boolean task;
    private boolean calendar;
    private boolean plan;
    private boolean tag1;
    private boolean tag2;
    private boolean tag3;
    private boolean tag4;
    private boolean tag5;
    private boolean tag6;
    private boolean tag7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mTitle.setText("通知设置");
        mRightTv.setText("保存");
        mRightTv.setVisibility(View.VISIBLE);
        mLoadingDialog = createLoadingDialog("正在加载...");
    }

    @Override
    protected void startAnimEnd(Bundle savedInstanceState) {
        getNoticeAuthority();
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_setting_receiver_msg_task, R.id.ll_setting_receiver_msg_calendar, R.id.ll_setting_receiver_msg_plan,
            R.id.ll_setting_receiver_msg_concern, R.id.ll_setting_receiver_msg_replay, R.id.ll_setting_receiver_msg_collect, R.id.ll_setting_receiver_msg_send_other, R.id.cm_header_tv_right})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                addNoticeAuthority();
                break;
            case R.id.ll_setting_receiver_msg_task:
                sbTask.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Task")) {
                        tag1 = true;
                        if (sbTask.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag1 && sbTask.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Task";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_calendar:
                sbCalendar.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Schedule")) {
                        tag2 = true;
                        if (sbCalendar.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag2 && sbCalendar.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Schedule";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_plan:
                sbPlan.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Plan")) {
                        tag3 = true;
                        if (sbPlan.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag3 && sbPlan.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Plan";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_concern:
                sbConcern.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Follow")) {
                        tag4 = true;
                        if (sbConcern.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag4 && sbConcern.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Follow";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_replay:
                sbReply.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Reply")) {
                        tag5 = true;
                        if (sbReply.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag5 && sbReply.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Reply";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_collect:
                sbCollect.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Like")) {
                        tag6 = true;
                        if (sbCollect.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag6 && sbCollect.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Like";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
            case R.id.ll_setting_receiver_msg_send_other:
                sbSendOther.toggle();
                for (ApiEntity.UserSetting userSet : userSettings) {
                    if (userSet.Key.equals("Notice_Transpond")) {
                        tag7 = true;
                        if (sbSendOther.isChecked()) {
                            if (!TextUtils.isEmpty(userSet.Value)) {
                                if (!userSet.Value.contains("3")) {
                                    userSet.Value += ",3";
                                }
                            } else {
                                userSet.Value = 3 + "";
                            }
                        } else {
                            if (userSet.Value.contains("3")) {
                                if (userSet.Value.length() > 2)
                                    userSet.Value = userSet.Value.substring(0, userSet.Value.length() - 2);
                                else
                                    userSet.Value = "";
                            }
                        }
                    }
                }
                if (!tag7 && sbSendOther.isChecked()) {
                    ApiEntity.UserSetting userSetting = new ApiEntity.UserSetting();
                    userSetting.Key = "Notice_Transpond";
                    if (TextUtils.isEmpty(userSetting.Value)) {
                        userSetting.Value = 3 + "";
                    } else if (userSetting.Value.contains("3")) {
                        userSetting.Value.substring(0, userSetting.Value.length() - 2);
                    }
                    userSetting.UserID = PrefsUtil.readUserInfo().ID;
                    userSetting.UType = 1;
                    userSettings.add(userSetting);
                }
                break;
        }
    }

    private void getNoticeAuthority() {
        API.UserAPI.GetUserSetting(new RequestCallback<ApiEntity.UserSetting>(ApiEntity.UserSetting.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserSetting> respList) {
                mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    userSettings.clear();
//                    ToastUtil.showToast(NotificationActivity.this, "获取通知设置成功");
                    for (ApiEntity.UserSetting userSetting : respList) {
                        if (userSetting.Key.equals("Notice_Follow")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                follow = true;
                            } else {
                                follow = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Reply")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                reply = true;
                            } else {
                                reply = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Like")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                collect = true;
                            } else {
                                collect = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Transpond")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                transend = true;
                            } else {
                                transend = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Task")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                task = true;
                            } else {
                                task = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Schedule")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                calendar = true;
                            } else {
                                calendar = false;
                            }
                        } else if (userSetting.Key.equals("Notice_Plan")) {
                            userSettings.add(userSetting);
                            if (userSetting.Value.contains("3")) {
                                plan = true;
                            } else {
                                plan = false;
                            }
                        }
                    }
                    sbConcern.setChecked(follow);
                    sbReply.setChecked(reply);
                    sbCollect.setChecked(collect);
                    sbSendOther.setChecked(transend);
                    sbTask.setChecked(task);
                    sbCalendar.setChecked(calendar);
                    sbPlan.setChecked(plan);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(NotificationActivity.this, "获取通知设置失败");
            }
        });
    }

    private void addNoticeAuthority() {
        API.UserAPI.AddUserSetting(userSettings, 1, new RequestCallback<ApiEntity.UserSetting>(ApiEntity.UserSetting.class) {
            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (result != null) {
                    ToastUtil.showToast(NotificationActivity.this, "修改成功", R.drawable.tishi_ico_gougou);
                    onBackPressed();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(NotificationActivity.this, "修改失败");
            }
        });
    }

}
