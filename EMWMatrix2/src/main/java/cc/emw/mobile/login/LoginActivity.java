package cc.emw.mobile.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.yzx.service.ConnectionService;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.model.ContactModelImple;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 登录界面
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements LoginView {

    @ViewInject(R.id.et_login_username)
    private EditText mUserNameEt;
    @ViewInject(R.id.et_login_password)
    private EditText mPasswordEt;
    @ViewInject(R.id.et_login_comcode)
    private EditText mComCodeEt;
    @ViewInject(R.id.cb_login_save)
    private CheckBox mSaveCb;

    private Dialog mLoadingDialog; //加载框
    private LoginPresenter loginPresenter;
    private String userName, password, comCode;
    private boolean isLoginYZX; // 是否可以登录云之讯

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int delay = 0;
        if (PrefsUtil.isFirst()) { // 第一次启动APP，进入引导界面
            delay = 1000;
            PrefsUtil.setFirst(false);
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                setSwipeBackEnable(false);
                loginPresenter = new LoginPresenter(LoginActivity.this);
                try {
                    PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    loginPresenter.checkVersion(packInfo.versionCode);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                // 读取本地的信息，判断是否选取保存信息，有则自动填写，无则手动填写
                PrefsUtil.readLoginUser(mUserNameEt, mPasswordEt, mComCodeEt, mSaveCb);
            }
        }, delay);


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(R.id.btn_login)
    private void onLoginClick(View v) {
        if (HelpUtil.isNetworkAvailable(this)) {
            userName = mUserNameEt.getText().toString().toLowerCase().trim();
            password = mPasswordEt.getText().toString().trim();
            comCode = mComCodeEt.getText().toString().toLowerCase().trim();
            if (validate(userName, password, comCode)) {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips1);
                mLoadingDialog.show();
                loginPresenter.login(userName, password, comCode);
            }
        } else {
            showNetworkSetDialog();
        }
    }

    @Override
    public void saveLoginInfo(LoginResp respInfo) {
        PrefsUtil.saveLoginDate();
        PrefsUtil.saveLoginCookie(respInfo);
        PrefsUtil.saveUserInfo(respInfo.User);

        if (mSaveCb.isChecked()) {
            PrefsUtil.saveLoginUser(userName, password, comCode, true);
        } else {
            PrefsUtil.saveLoginUser("", "", "", false);
        }

        new ContactModelImple().getGroupList(null); // 需要在保存好Cookie再调用请求
        loginPresenter.getPersonList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String voipCode = PrefsUtil.readUserInfo().VoipCode;
                String voipPwd = PrefsUtil.readUserInfo().VoipPwd;
                if (!TextUtils.isEmpty(voipCode) && !TextUtils.isEmpty(voipPwd)) {
//				&& android.os.Build.VERSION.SDK_INT < 23) { //安卓6.0以上不可用
                    isLoginYZX = true;
                    // 启动云之讯Service
                    startService(new Intent(LoginActivity.this, ConnectionService.class));
                }
            }
        }, 2000);

    }

    @Override
    public void navigateToIndex() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLoginYZX", isLoginYZX);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 1000);

    }

    @Override
    public void showTipDialog(String tip) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        DialogUtil.showTipDialog(this, tip);
    }

    @Override
    public void showVersionDialog(Version ver) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        DialogUtil.showVersionDialog(this, ver);
    }

    /**
     * 非空验证
     *
     * @param username
     * @param password
     * @param comcode
     * @return
     */
    private boolean validate(String username, String password, String comcode) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(username)) {
            tip = getString(R.string.login_tip_name);
        } else if (TextUtils.isEmpty(password)) {
            tip = getString(R.string.login_tip_psw);
        } else if (TextUtils.isEmpty(comcode)) {
            tip = getString(R.string.login_tip_ccode);
        } else {
            isSuccess = true;
        }
        if (!isSuccess) {
            DialogUtil.showTipDialog(this, tip);
        }

        return isSuccess;
    }

    /**
     * 弹出网络设置对话框
     */
    private void showNetworkSetDialog() {
        new AlertDialog(this).builder().setTitle(getString(R.string.warm_tips))
                .setMsg(getString(R.string.net_not_open))
                .setPositiveButton(getString(R.string.setting), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterNetworkSetting();
                    }
                }).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 进入无线网络配置界面
     */
    private void enterNetworkSetting() {
        Intent i = null;
        if (android.os.Build.VERSION.SDK_INT > 10) {
            // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTING打开到wifi界面
            i = new Intent(Settings.ACTION_SETTINGS);
        } else {
            i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
