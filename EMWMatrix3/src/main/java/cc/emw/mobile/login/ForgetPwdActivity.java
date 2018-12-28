package cc.emw.mobile.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

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
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.MaterialEditText;

/**
 * 忘记密码·邮箱验证码
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_forget_pwd)
public class ForgetPwdActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.tv_forgetpwd_next)
    private TextView mNextTv;
    @ViewInject(R.id.et_forgetpwd_email)
    private MaterialEditText mEmailEt;
    @ViewInject(R.id.et_forgetpwd_authcode)
    private EditText mAuthCodeEt;
    @ViewInject(R.id.btn_forgetpwd_getcode)
    private Button mGetCodeBtn;
//    @ViewInject(R.id.et_forgetpwd_newpwd)
//    private EditText mNewPwdEt;
    @ViewInject(R.id.tv_forgetpwd_tips)
    private TextView mTipsTv;

    private Dialog mLoadingDialog; //加载框
    private CountDownTimer countDownTimer;
    private boolean isStartTimer;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        SpannableString spanStr = new SpannableString(mTipsTv.getText());
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#56B9F6"));
        spanStr.setSpan(colorSpan1, spanStr.length()-7, spanStr.length()-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mTipsTv.setText(spanStr);

        mEmailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mEmailEt.getText()) && !hasFocus) {
                    mEmailEt.validateWith(new RegexpValidator("邮箱地址格式错误", StringUtils.EMAIL_REGEX));
                }
            }
        });
        mEmailEt.addTextChangedListener(this);
        mAuthCodeEt.addTextChangedListener(this);
//        mNewPwdEt.addTextChangedListener(this);

        countDownTimer = new CountDownTimer(60*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(!isFinishing()){
                    isStartTimer = true;
                    mGetCodeBtn.setEnabled(false);
                    mGetCodeBtn.setText("已发送(" + millisUntilFinished / 1000 + "s)");
                }
            }

            public void onFinish() {
                if(!isFinishing()){
                    isStartTimer = false;
                    mGetCodeBtn.setEnabled(true);
                    mGetCodeBtn.setText("获取验证码");
                }
            }
        };
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String email = mEmailEt.getText().toString().trim();
        String code = mAuthCodeEt.getText().toString().trim();
//        String password = mNewPwdEt.getText().toString().trim();
//        if (!TextUtils.isEmpty(email) && StringUtils.isEmail(email) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(password)) {
        if (!TextUtils.isEmpty(email) && StringUtils.isEmail(email) && !TextUtils.isEmpty(code)) {
            mNextTv.setVisibility(View.VISIBLE);
        } else {
            mNextTv.setVisibility(View.GONE);
        }

        if (!isStartTimer) {
            if (!TextUtils.isEmpty(email) && StringUtils.isEmail(email)) {
                mGetCodeBtn.setEnabled(true);
            } else {
                mGetCodeBtn.setEnabled(false);
            }
        }
    }
    @Override
    public void afterTextChanged(Editable s) {}

    @Event(value = {R.id.itv_forgetpwd_cancel, R.id.btn_forgetpwd_getcode, R.id.btn_forgetpwd_next, R.id.tv_forgetpwd_next})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.itv_forgetpwd_cancel:
                HelpUtil.hideSoftInput(this, mEmailEt);
                onBackPressed();
                break;
            case R.id.btn_forgetpwd_getcode:
                HelpUtil.hideSoftInput(this, mEmailEt);
                sendVerifyCode(mEmailEt.getText().toString().trim());
                break;
            case R.id.btn_forgetpwd_next:
            case R.id.tv_forgetpwd_next:
                verifyCode(mAuthCodeEt.getText().toString().trim());
                break;
        }
    }

    private void sendVerifyCode(final String email) {
        API.UserPubAPI.SendVerifyCode(email, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (ex instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(ForgetPwdActivity.this);
                } else {
//                    ToastUtil.showToast(ForgetPwdActivity.this, "获取验证码失败！");
                    new MaterialDialog.Builder(ForgetPwdActivity.this)
                            .content("获取验证码失败！")
                            .contentGravity(GravityEnum.CENTER)
                            .negativeText(R.string.cancel)
                            .positiveText("重新获取")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendVerifyCode(email);
                                }
                            })
                            .show();
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
                if (!TextUtils.isEmpty(result)) {
                    if (Integer.valueOf(result) > 0) {
                        countDownTimer.start();
//                        ToastUtil.showToast(ForgetPwdActivity.this, "验证码已成功发到您的邮箱！");
                        new MaterialDialog.Builder(ForgetPwdActivity.this)
                                .content("验证码已发送至您的邮箱，请查收")
                                .contentGravity(GravityEnum.CENTER)
                                .positiveText("好的")
                                .show();
                    } else if (Integer.valueOf(result) == -1){
//                        ToastUtil.showToast(ForgetPwdActivity.this, "输入的邮箱还未注册！");
                        mEmailEt.setError("输入的邮箱还未注册");
                        mEmailEt.postInvalidate();
                    } else {
//                        ToastUtil.showToast(ForgetPwdActivity.this, "获取验证码失败！");
                        new MaterialDialog.Builder(ForgetPwdActivity.this)
                                .content("获取验证码失败！")
                                .contentGravity(GravityEnum.CENTER)
                                .negativeText(R.string.cancel)
                                .positiveText("重新获取")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        sendVerifyCode(email);
                                    }
                                })
                                .show();
                    }
                } else {
//                    ToastUtil.showToast(ForgetPwdActivity.this, "获取验证码失败！");
                    new MaterialDialog.Builder(ForgetPwdActivity.this)
                            .content("获取验证码失败！")
                            .contentGravity(GravityEnum.CENTER)
                            .negativeText(R.string.cancel)
                            .positiveText("重新获取")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendVerifyCode(email);
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void verifyCode(final String code) {
        API.UserPubAPI.VerifyCode(code, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (ex instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(ForgetPwdActivity.this);
                } else {
//                    ToastUtil.showToast(ForgetPwdActivity.this, "验证码错误！");
                    mAuthCodeEt.setError("验证码错误");
                    mAuthCodeEt.postInvalidate();
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
                    Intent intent = new Intent(ForgetPwdActivity.this, ResetPwdActivity.class);
                    intent.putExtra("auth_code", code);
                    startActivity(intent);
                    finish();
                } else {
//                    ToastUtil.showToast(ForgetPwdActivity.this, "验证码错误！");
                    mAuthCodeEt.setError("验证码错误");
                    mAuthCodeEt.postInvalidate();
                }
            }
        });
    }
}
