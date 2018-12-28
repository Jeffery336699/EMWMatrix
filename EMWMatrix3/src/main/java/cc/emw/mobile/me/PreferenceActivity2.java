package cc.emw.mobile.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;

/**
 * 偏好设置
 */
@ContentView(R.layout.activity_preference2)
public class PreferenceActivity2 extends BaseActivity {

    private ApiEntity.UserSetting userSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSetting = new ApiEntity.UserSetting();
        userSetting.Key = "Login_TimeOut";
        userSetting.UType = ApiEnum.UserSettingTypes.Perference;
        findViewById(R.id.ll_out_side).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Event(value = {R.id.tv_timeout_5, R.id.tv_timeout_10, R.id.tv_timeout_15, R.id.tv_timeout_20,
            R.id.tv_timeout_none})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.tv_timeout_5:
                userSetting.Value = "5";
                break;
            case R.id.tv_timeout_10:
                userSetting.Value = "10";
                break;
            case R.id.tv_timeout_15:
                userSetting.Value = "15";
                break;
            case R.id.tv_timeout_20:
                userSetting.Value = "20";
                break;
            case R.id.tv_timeout_none:
                userSetting.Value = "-1";
                break;
        }
        List<ApiEntity.UserSetting> setList = new ArrayList<>();
        setList.add(userSetting);
        doPreference(setList);
    }

    private void doPreference(final List<ApiEntity.UserSetting> setList) {
        API.UserPubAPI.AddUserSetting(setList, 5, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("timeout", setList.get(0));
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    onError(null, false);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(PreferenceActivity2.this, "保存失败！");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade, R.anim.fade);
    }
}
