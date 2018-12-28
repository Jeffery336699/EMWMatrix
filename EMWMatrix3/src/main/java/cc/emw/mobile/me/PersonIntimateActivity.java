package cc.emw.mobile.me;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

@ContentView(R.layout.activity_person_intimate)
public class PersonIntimateActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mRightTv;

    @ViewInject(R.id.tv_intimate_concern)
    private TextView tvConcern;
    @ViewInject(R.id.tv_intimate_collect)
    private TextView tvCollect;
    @ViewInject(R.id.tv_intimate_send)
    private TextView tvSend;
    @ViewInject(R.id.tv_intimate_work)
    private TextView tvWork;

    private List<ApiEntity.UserSetting> userSettings = new ArrayList<>();
    private Dialog mLoadingDialog;
    private int concern;
    private int collect;
    private int send;
    private int work;
    private int value;
    private boolean tag1;
    private boolean tag2;
    private boolean tag3;
    private boolean tag4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        init();
    }

    private void init() {
        mTitle.setText("隐私设置");
        getUserIntimate();
        mRightTv.setVisibility(View.VISIBLE);
        mLoadingDialog = createLoadingDialog("正在加载...");
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_my_concern_self,
            R.id.ll_my_collect_self, R.id.ll_my_send_self, R.id.ll_my_work_relation, R.id.cm_header_tv_right})
    private void onHeadClick(View view) {
        Intent intent = new Intent(this, IntimateActivity.class);
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                API.UserAPI.AddUserSetting(userSettings, 2, new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        mLoadingDialog.dismiss();
                        if (result != null) {
                            ToastUtil.showToast(PersonIntimateActivity.this, "修改成功", R.drawable.tishi_ico_gougou);
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonIntimateActivity.this, "修改失败");
                    }
                });
                break;
            case R.id.ll_my_concern_self:
                intent.putExtra("intimate", concern);
                startActivityForResult(intent, 102);
                break;
            case R.id.ll_my_collect_self:
                intent.putExtra("intimate", collect);
                startActivityForResult(intent, 103);
                break;
            case R.id.ll_my_send_self:
                intent.putExtra("intimate", send);
                startActivityForResult(intent, 104);
                break;
            case R.id.ll_my_work_relation:
                intent.putExtra("intimate", work);
                startActivityForResult(intent, 105);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 102:
                    value = data.getIntExtra("value", -1);
                    if (value != -1) {
                        if (userSettings != null) {
                            for (ApiEntity.UserSetting userSet : userSettings) {
                                if (userSet.Key.equals("Privacy_Follower")) {
                                    tag1 = true;
                                    userSet.Value = value + "";
                                }
                            }
                        }
                        if (!tag1) {
                            ApiEntity.UserSetting userSettingConcern = new ApiEntity.UserSetting();
                            userSettingConcern.Key = "Privacy_Follower";
                            userSettingConcern.UType = 2;
                            userSettingConcern.UserID = PrefsUtil.readUserInfo().ID;
                            userSettingConcern.Value = value + "";
                            userSettings.add(userSettingConcern);
                        }
                    }
                    switch (value) {
                        case 0:
                            tvConcern.setText("所有人");
                            break;
                        case 1:
                            tvConcern.setText("关注的");
                            break;
                        case 2:
                            tvConcern.setText("组内人");
                            break;
                        case 3:
                            tvConcern.setText("自己");
                            break;
                    }
                    break;
                case 103:
                    value = data.getIntExtra("value", -1);
                    if (value != -1) {
                        if (userSettings != null) {
                            for (ApiEntity.UserSetting userSet : userSettings) {
                                if (userSet.Key.equals("Privacy_Like")) {
                                    tag2 = true;
                                    userSet.Value = value + "";
                                }
                            }
                        }

                        if (!tag2) {
                            ApiEntity.UserSetting userSettingCollect = new ApiEntity.UserSetting();
                            userSettingCollect.Key = "Privacy_Like";
                            userSettingCollect.UType = 2;
                            userSettingCollect.UserID = PrefsUtil.readUserInfo().ID;
                            userSettingCollect.Value = value + "";
                            userSettings.add(userSettingCollect);
                        }
                    }
                    switch (value) {
                        case 0:
                            tvCollect.setText("所有人");
                            break;
                        case 1:
                            tvCollect.setText("关注的");
                            break;
                        case 2:
                            tvCollect.setText("组内人");
                            break;
                        case 3:
                            tvCollect.setText("自己");
                            break;
                    }
                    break;
                case 104:
                    value = data.getIntExtra("value", -1);
                    if (value != -1) {
                        if (userSettings != null) {
                            for (ApiEntity.UserSetting userSet : userSettings) {
                                if (userSet.Key.equals("Privacy_Publish")) {
                                    tag3 = true;
                                    userSet.Value = value + "";
                                }
                            }
                        }

                        if (!tag3) {
                            ApiEntity.UserSetting userSettingSend = new ApiEntity.UserSetting();
                            userSettingSend.Key = "Privacy_Publish";
                            userSettingSend.UType = 2;
                            userSettingSend.UserID = PrefsUtil.readUserInfo().ID;
                            userSettingSend.Value = value + "";
                            userSettings.add(userSettingSend);
                        }
                    }
                    switch (value) {
                        case 0:
                            tvSend.setText("所有人");
                            break;
                        case 1:
                            tvSend.setText("关注的");
                            break;
                        case 2:
                            tvSend.setText("组内人");
                            break;
                        case 3:
                            tvSend.setText("自己");
                            break;
                    }
                    break;
                case 105:
                    value = data.getIntExtra("value", -1);
                    if (value != -1) {
                        if (userSettings != null) {
                            for (ApiEntity.UserSetting userSet : userSettings) {
                                if (userSet.Key.equals("Privacy_Job")) {
                                    tag4 = true;
                                    userSet.Value = value + "";
                                }
                            }
                        }

                        if (!tag4) {
                            ApiEntity.UserSetting userSettingWork = new ApiEntity.UserSetting();
                            userSettingWork.Key = "Privacy_Job";
                            userSettingWork.UType = 2;
                            userSettingWork.UserID = PrefsUtil.readUserInfo().ID;
                            userSettingWork.Value = value + "";
                            userSettings.add(userSettingWork);
                        }
                    }
                    switch (value) {
                        case 0:
                            tvWork.setText("所有人");
                            break;
                        case 1:
                            tvWork.setText("关注的");
                            break;
                        case 2:
                            tvWork.setText("组内人");
                            break;
                        case 3:
                            tvWork.setText("自己");
                            break;
                    }
                    break;
            }
        }
    }

    private void getUserIntimate() {
        API.UserAPI.GetUserSetting(new RequestCallback<ApiEntity.UserSetting>(ApiEntity.UserSetting.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserSetting> respList) {
                mLoadingDialog.dismiss();
                userSettings.clear();
                if (respList != null && respList.size() > 0) {
                    for (ApiEntity.UserSetting userSetting : respList) {
                        if (userSetting.Key.equals("Privacy_Follower") && !TextUtils.isEmpty(userSetting.Value)) {
                            userSettings.add(userSetting);
                            concern = Integer.valueOf(userSetting.Value);
                            switch (concern) {
                                case 0:
                                    tvConcern.setText("所有人");
                                    break;
                                case 1:
                                    tvConcern.setText("关注的");
                                    break;
                                case 2:
                                    tvConcern.setText("组内人");
                                    break;
                                case 3:
                                    tvConcern.setText("自己");
                                    break;
                            }
                        } else if (userSetting.Key.equals("Privacy_Like") && !TextUtils.isEmpty(userSetting.Value)) {
                            userSettings.add(userSetting);
                            collect = Integer.valueOf(userSetting.Value);
                            switch (collect) {
                                case 0:
                                    tvCollect.setText("所有人");
                                    break;
                                case 1:
                                    tvCollect.setText("关注的");
                                    break;
                                case 2:
                                    tvCollect.setText("组内人");
                                    break;
                                case 3:
                                    tvCollect.setText("自己");
                                    break;
                            }
                        } else if (userSetting.Key.equals("Privacy_Publish") && !TextUtils.isEmpty(userSetting.Value)) {
                            userSettings.add(userSetting);
                            send = Integer.valueOf(userSetting.Value);
                            switch (send) {
                                case 0:
                                    tvSend.setText("所有人");
                                    break;
                                case 1:
                                    tvSend.setText("关注的");
                                    break;
                                case 2:
                                    tvSend.setText("组内人");
                                    break;
                                case 3:
                                    tvSend.setText("自己");
                                    break;
                            }
                        } else if (userSetting.Key.equals("Privacy_Job") && !TextUtils.isEmpty(userSetting.Value)) {
                            userSettings.add(userSetting);
                            work = Integer.valueOf(userSetting.Value);
                            switch (work) {
                                case 0:
                                    tvWork.setText("所有人");
                                    break;
                                case 1:
                                    tvWork.setText("关注的");
                                    break;
                                case 2:
                                    tvWork.setText("组内人");
                                    break;
                                case 3:
                                    tvWork.setText("自己");
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(PersonIntimateActivity.this, "获取隐私设置失败");
            }
        });
    }
}
