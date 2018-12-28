package cc.emw.mobile.me;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMPushManager;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.LoginActivity2;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.Prefs;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 我·系统设置》修改密码
 */
@ContentView(R.layout.activity_change_pw)
public class PwChangeActivity extends BaseActivity {

    @ViewInject(R.id.et_change_one)
    private EditText mPwOneEt; // 新密码
    @ViewInject(R.id.et_change_two)
    private EditText mPwTwoEt; // 确认新密码

    private Dialog mLoadingDialog; // 加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                String pwOne = mPwOneEt.getText().toString();
                String pwTwo = mPwTwoEt.getText().toString();
                if (validate(pwOne, pwTwo)) {
                    if (pwOne.length() < 6) {
                        ToastUtil.showToast(this, "密码长度不能少于6位");
                    } else {
                        changePw();
                    }
                }
                break;
        }
    }

    /**
     * 非空验证
     *
     * @param pwOne
     * @param pwTwo
     * @return
     */
    private boolean validate(String pwOne, String pwTwo) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(pwOne)) {
            tip = getString(R.string.mechange_empty_pwone);
        } else if (TextUtils.isEmpty(pwTwo)) {
            tip = getString(R.string.mechange_empty_pwtwo);
        } else if (!pwOne.equals(pwTwo)) {
            tip = getString(R.string.mechange_password_inconformity);
        } else {
            isSuccess = true;
        }
        if (!isSuccess) {
            new AlertDialog(this).builder()
                    .setMsg(tip)
                    .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }

        return isSuccess;
    }

    private void changePw() {
        API.UserPubAPI.ModifyPassWord(Prefs.getString("password", ""), mPwOneEt
                        .getText().toString(),
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        Toast.makeText(PwChangeActivity.this, ex.toString(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onStarted() {
                        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        CIMPushManager.stop(PwChangeActivity.this);
                        PrefsUtil.setSwitch(true);
                        PrefsUtil.cleanUserInfo();
                        PrefsUtil.saveLoginUser("", "", "", false);
                        new AlertDialog(PwChangeActivity.this).builder()
                                .setMsg("密码修改成功，请重新登录。")
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(PwChangeActivity.this, LoginActivity2.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                    }
                });
    }

}
