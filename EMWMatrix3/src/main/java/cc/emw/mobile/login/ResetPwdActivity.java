package cc.emw.mobile.login;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;

/**
 * 忘记密码·重置密码
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_reset_pwd)
public class ResetPwdActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.et_resetpwd_newpwd)
    private EditText mNewPwdEt;
    @ViewInject(R.id.et_resetpwd_repeatpwd)
    private EditText mRepeatPwdEt;
    @ViewInject(R.id.btn_reset_submit)
    private Button mSubmitBtn;

    private Dialog mLoadingDialog; //加载框
    private String authCode;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        authCode = getIntent().getStringExtra("auth_code");
        mNewPwdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mNewPwdEt.length() < 8 && !hasFocus) {
                    mNewPwdEt.setError("密码长度至少为8个字符");
                    mNewPwdEt.postInvalidate();
                }
            }
        });
        mRepeatPwdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mRepeatPwdEt.length() < 8 && !hasFocus) {
                    mRepeatPwdEt.setError("密码长度至少为8个字符");
                    mRepeatPwdEt.postInvalidate();
                }
            }
        });
        mNewPwdEt.addTextChangedListener(this);
        mRepeatPwdEt.addTextChangedListener(this);

    }


    @Event(value = {R.id.itv_resetpwd_cancel, R.id.btn_reset_submit})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.itv_resetpwd_cancel:
                HelpUtil.hideSoftInput(this, mNewPwdEt);
                onBackPressed();
                break;
            case R.id.btn_reset_submit:
                if (TextUtils.equals(mNewPwdEt.getText(), mRepeatPwdEt.getText())) {
                    updatePassword(authCode, mNewPwdEt.getText().toString().trim());
                } else {
                    new MaterialDialog.Builder(ResetPwdActivity.this)
                            .content("两次密码不一致，请重新输入。")
                            .contentGravity(GravityEnum.CENTER)
                            .positiveText("好的")
                            .show();
                    /*AlertDialog dialog = new AlertDialog(this).builder();
                    dialog.setMsg("两次密码不一致，请重新输入。");
                    dialog.setPositiveButton("确认", null).show();*/
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(mNewPwdEt.getText().toString().trim()) && mNewPwdEt.length() >= 8
                && !TextUtils.isEmpty(mRepeatPwdEt.getText().toString().trim()) && mRepeatPwdEt.length() >= 8) {
            mSubmitBtn.setEnabled(true);
        } else {
            mSubmitBtn.setEnabled(false);
        }
    }
    @Override
    public void afterTextChanged(Editable s) {}

    private void updatePassword(final String code, String password) {
        API.UserPubAPI.UpdatePassword(code, password, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (ex instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(ResetPwdActivity.this);
                } else {
                    ToastUtil.showToast(ResetPwdActivity.this, "重置密码失败！");
                }
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                Logger.w("forget", result);
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    /*AlertDialog dialog = new AlertDialog(ResetPwdActivity.this).builder();
                    dialog.setCancelable(false).setMsg("密码修改成功，请使用新密码登录。");
                    dialog.setPositiveButton("现在登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ResetPwdActivity.this, WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).show();*/
                    ToastUtil.showToast(ResetPwdActivity.this, "重置密码成功！");
                    finish();
                } else {
                    ToastUtil.showToast(ResetPwdActivity.this, "重置密码失败！");
                }
            }
        });
    }
}
