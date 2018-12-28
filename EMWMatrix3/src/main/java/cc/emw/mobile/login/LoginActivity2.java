package cc.emw.mobile.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.githang.androidcrash.util.AppManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.MaterialEditText;
import io.socket.client.Socket;

/**
 * 登录界面，在用
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_login2)
public class LoginActivity2 extends BaseActivity implements TextWatcher, LoginView {

    @ViewInject(R.id.et_login_username)
    private MaterialEditText mUserNameEt;
    @ViewInject(R.id.et_login_password)
    private EditText mPasswordEt;
    @ViewInject(R.id.iv_login_visibility)
    private ImageView mVisibilityIv;
    @ViewInject(R.id.et_login_comcode)
    private EditText mComCodeEt;
    @ViewInject(R.id.btn_login)
    private Button mLoginBtn;

    private Dialog mLoadingDialog; //加载框
    private LoginPresenter loginPresenter;
    private String userName, password, comCode;
    private boolean isLoginRY; // 是否可以登录融云
    private boolean showPwd = true;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Log.d("login_info", "login2 onCreate");
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        PrefsUtil.cleanLoginCookie();
        mUserNameEt.addTextChangedListener(this);
        mUserNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mUserNameEt.getText()) && !hasFocus
                        && !StringUtils.isEmail(mUserNameEt.getText().toString().trim())) {
                    mUserNameEt.setError("邮件账号格式错误");
                    mUserNameEt.postInvalidate();
                }
            }
        });
        mUserNameEt.addTextChangedListener(this);
        mPasswordEt.addTextChangedListener(this);

        loginPresenter = new LoginPresenter(this);
        /*try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            loginPresenter.checkVersion(packInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        // 读取本地的信息，判断是否选取保存信息，有则自动填写，无则手动填写
        PrefsUtil.readLoginUser(mUserNameEt, mPasswordEt, mComCodeEt, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("login_info", "login2 finish");
    }

    @Override
    public void onBackPressed() {
        AppManager.AppExit(this);
    }

    @Event(value = {R.id.tv_login_cancel, R.id.tv_login_twostep, R.id.iv_login_visibility, R.id.tv_login_forgetpwd, R.id.btn_login, R.id.ll_login_register})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_cancel:
                /*HelpUtil.hideSoftInput(this, mUserNameEt);
                Intent intent1 = new Intent(this,WelcomeActivity.class);
                startActivity(intent1);
                finish();*/
                onBackPressed();
                break;
            case R.id.tv_login_twostep:
                Intent intent = new Intent(this, TwoStepActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_login_visibility:
                if (showPwd){
                    //设置EditText文本为可见的
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mVisibilityIv.setImageResource(R.drawable.ic_visibility_black);
                    showPwd = false;
                } else {
                    //设置EditText文本为隐藏的
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mVisibilityIv.setImageResource(R.drawable.ic_visibility_off_black);
                    showPwd = true;
                }
                if (mPasswordEt.hasFocus()) {
                    //有焦点时把光标移到最后
                    CharSequence text = mPasswordEt.getText();
                    if (text instanceof Spannable) {
                        Spannable spanText = (Spannable)text;
                        Selection.setSelection(spanText, text.length());
                    }
                }
                break;
            case R.id.tv_login_forgetpwd:
                Intent forgetIntent = new Intent(this, ForgetPwdActivity.class);
                startActivity(forgetIntent);
                break;
            case R.id.btn_login:
                if (HelpUtil.isNetworkAvailable(this)) {
                    userName = mUserNameEt.getText().toString().toLowerCase().trim();
                    password = mPasswordEt.getText().toString().trim();
                    comCode = mComCodeEt.getText().toString().toLowerCase().trim();
                    mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips1);
                    mLoadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            loginPresenter.cancel();
                        }
                    });
                    mLoadingDialog.show();
                    loginPresenter.login(userName, password, comCode);
                } else {
                    DialogUtil.showNetworkSetDialog(this);
                }
                break;
            case R.id.ll_login_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(mUserNameEt.getText().toString().trim()) && !TextUtils.isEmpty(mPasswordEt.getText().toString())) {
            mLoginBtn.setEnabled(true);
        } else {
            mLoginBtn.setEnabled(false);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void saveLoginInfo(LoginResp respInfo) {
        Logger.d("MainActivity", "saveLoginInfo----------LoginActivity save" );
        PrefsUtil.cleanLoginCookie();
        PrefsUtil.cleanUserInfo();
        PrefsUtil.saveLoginDate();
        PrefsUtil.saveLoginCookie(respInfo);
        PrefsUtil.saveUserInfo(respInfo.User);

        PrefsUtil.saveLoginUser(userName, password, comCode, true);

        loginPresenter.getPersonList();

        String ryToken = PrefsUtil.readUserInfo().RongYunToken;
        if (!TextUtils.isEmpty(ryToken)) {
            isLoginRY = true;
        }

    }

    @Override
    public void navigateToIndex() {
        if (isFinishing())
            return;
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLoginRY", isLoginRY);
        intent.putExtra("isRelogin", false);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                sendBroadcast(new Intent(WelcomeActivity.ACTION_WELCOME_FINISH));
                finish();
            }
        }, 1000);

    }

    @Override
    public void showTipDialog(String tip) {
        if (isFinishing())
            return;
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        DialogUtil.showTipDialog(this, tip);
    }

    @Override
    public void showTipToast(String tip) {

    }

    @Override
    public void showVersionDialog(Version ver) {
        if (isFinishing())
            return;
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        DialogUtil.showVersionDialog(this, ver);
    }

    @Override
    public Socket getIOSocket() {
        EMWApplication emw = (EMWApplication) getApplication();
        Socket mSocket = emw.getAppIOSocket();
        return mSocket;
    }


}
