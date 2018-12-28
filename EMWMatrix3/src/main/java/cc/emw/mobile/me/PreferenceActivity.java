package cc.emw.mobile.me;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.CollapseView;

/**
 * 偏好设置
 */
@ContentView(R.layout.activity_preference)
public class PreferenceActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mRightTv;

    private CollapseView mTimeOutCV;  //
    private Dialog mLoadingDialog;
    private ApiEntity.UserSetting userSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        init();
        userSetting = new ApiEntity.UserSetting();
        userSetting.Key = "Login_TimeOut";
        userSetting.UType = ApiEnum.UserSettingTypes.Perference;

        getTimeOutSetting();
    }

    private void init() {
        mTitle.setText("偏好设置");
        mRightTv.setText("保存");
        mRightTv.setVisibility(View.VISIBLE);

        mTimeOutCV = (CollapseView) findViewById(R.id.cv_phone_tixing);
        mTimeOutCV.setContent(R.layout.activity_preference_timeout2);
        mTimeOutCV.setTitle("15分钟");
        mTimeOutCV.setTagNameVis("", "PC登录超时时间");
        TextView tx5 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_5);
        TextView tx10 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_10);
        TextView tx15 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_15);
        TextView tx20 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_20);
        TextView tx30 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_30);
        TextView tx45 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_45);
        TextView tx60 = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_60);
        TextView tvNone = (TextView) mTimeOutCV.findViewById(R.id.tv_timeout_none);
        tx5.setOnClickListener(this);
        tx10.setOnClickListener(this);
        tx15.setOnClickListener(this);
        tx20.setOnClickListener(this);
        tx30.setOnClickListener(this);
        tx45.setOnClickListener(this);
        tx60.setOnClickListener(this);
        tvNone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tv_timeout_5:
                mTimeOutCV.setTitle("5分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "5";
                break;
            case R.id.tv_timeout_10:
                mTimeOutCV.setTitle("10分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "10";
                break;
            case R.id.tv_timeout_15:
                mTimeOutCV.setTitle("15分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "15";
                break;
            case R.id.tv_timeout_20:
                mTimeOutCV.setTitle("20分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "20";
                break;
            case R.id.tv_timeout_30:
                mTimeOutCV.setTitle("30分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "30";
                break;
            case R.id.tv_timeout_45:
                mTimeOutCV.setTitle("45分钟");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "45";
                break;
            case R.id.tv_timeout_60:
                mTimeOutCV.setTitle("1个小时");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "60";
                break;
            case R.id.tv_timeout_none:
                mTimeOutCV.setTitle("从不超时");
                mTimeOutCV.rotateArrow();
                userSetting.Value = "-1";
                break;
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                ArrayList<ApiEntity.UserSetting> setList = new ArrayList<>();
                setList.add(userSetting);
                API.UserAPI.AddUserSetting(setList, 5, new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(PreferenceActivity.this, "保存成功！", R.drawable.tishi_ico_gougou);
                            onBackPressed();
                        } else {
                            onError(null, false);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        ToastUtil.showToast(PreferenceActivity.this, "保存失败！");
                    }
                });
                break;
        }
    }

    private void getTimeOutSetting() {
        API.UserAPI.GetUserSetting(new RequestCallback<ApiEntity.UserSetting>(ApiEntity.UserSetting.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserSetting> respList) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    for (ApiEntity.UserSetting userSetting : respList) {
                        if (userSetting.Key.equals("Login_TimeOut")) {
                            if ("5".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("5分钟");
                            } else if ("10".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("10分钟");
                            } else if ("15".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("15分钟");
                            } else if ("20".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("20分钟");
                            } else if ("30".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("30分钟");
                            } else if ("45".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("45分钟");
                            } else if ("60".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("1个小时");
                            } else if ("-1".equals(userSetting.Value)) {
                                mTimeOutCV.setTitle("从不超时");
                            } else {
                                mTimeOutCV.setTitle("15分钟");
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(PreferenceActivity.this, "获取数据失败！");
            }
        });
    }
}
