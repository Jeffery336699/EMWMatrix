package cc.emw.mobile.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.MaterialEditText;

/**
 * 注册
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.tv_register_next)
    private TextView mNextTv;
    @ViewInject(R.id.et_register_phone)
    private MaterialEditText mPhoneEt;
    @ViewInject(R.id.et_register_code)
    private EditText mCodeEt;
    @ViewInject(R.id.tv_register_tips)
    private TextView mTipsTv;
    @ViewInject(R.id.btn_register_getcode)
    private Button mGetCodeBtn;

    private Dialog mLoadingDialog; //加载框
    private CountDownTimer countDownTimer;
    private boolean isStartTimer;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        SpannableString spanStr = new SpannableString(mTipsTv.getText());
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cm_main_text));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cm_main_text));
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.cm_main_text));
        spanStr.setSpan(colorSpan1, 8, 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spanStr.setSpan(colorSpan2, 13, 17, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spanStr.setSpan(colorSpan3, 21, 34, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mTipsTv.setText(spanStr);

        mPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneEt.getText()) && !hasFocus) {
                    mPhoneEt.validateWith(new RegexpValidator("手机号码错误", StringUtils.PHONE_REGEX));
                }
            }
        });
        mPhoneEt.addTextChangedListener(this);
        mCodeEt.addTextChangedListener(this);

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
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String phone = mPhoneEt.getText().toString().trim();
        String code = mCodeEt.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && StringUtils.isPhone(phone) && !TextUtils.isEmpty(code)) {
            mNextTv.setVisibility(View.VISIBLE);
        } else {
            mNextTv.setVisibility(View.GONE);
        }

        if (!isStartTimer) {
            if (!TextUtils.isEmpty(mPhoneEt.getText().toString().trim())) {
                mGetCodeBtn.setEnabled(true);
            } else {
                mGetCodeBtn.setEnabled(false);
            }
        }
    }
    @Override
    public void afterTextChanged(Editable s) {}

    @Event(value = {R.id.itv_register_cancel, R.id.tv_register_next, R.id.btn_register_getcode})
    private void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.itv_register_cancel:
                /*HelpUtil.hideSoftInput(this, mPhoneEt);
                Intent intent1 = new Intent(this,WelcomeActivity.class);
                startActivity(intent1);*/
                onBackPressed();
                break;
            case R.id.tv_register_next:
                checkRegisterSMS(mPhoneEt.getText().toString().trim(), mCodeEt.getText().toString().trim());
                break;
            case R.id.btn_register_getcode:
                HelpUtil.hideSoftInput(this, mPhoneEt);
                final String phone = mPhoneEt.getText().toString().trim();
                if (StringUtils.isPhone(phone)) {
                    new MaterialDialog.Builder(RegisterActivity.this)
                            .title("确认手机号码")
                            .titleGravity(GravityEnum.CENTER)
                            .content("我们将发送验证码短信到这个号码：\n"+phone)
                            .contentGravity(GravityEnum.CENTER)
                            .negativeText(R.string.cancel)
                            .positiveText("好的")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendRegisterSMS(phone);
                                }
                            })
                            .show();
                    /*new AlertDialog(this).builder().setTitle("确认手机号码")
                            .setMsg("我们将发送验证码短信到这个号码：\n"+phone)
                            .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendRegisterSMS(phone);
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();*/
                } else {
                    new MaterialDialog.Builder(RegisterActivity.this)
                            .title("手机号码错误")
                            .titleGravity(GravityEnum.CENTER)
                            .content("你输入的是一个无效的手机号码")
                            .contentGravity(GravityEnum.CENTER)
                            .positiveText("重新输入")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mPhoneEt.requestFocusFromTouch();
                                    HelpUtil.showSoftInput(RegisterActivity.this, mPhoneEt);
                                }
                            })
                            .show();
                    /*new AlertDialog(this).builder().setTitle("手机号码错误")
                            .setMsg("你输入的是一个无效的手机号码")
                            .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                    }).show();*/
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10:
                    finish();
                    break;
            }
        }
    }

    private void checkRegisterSMS(final String phone, String code) {
        API.UserAPI.CheckRegisterSMS(phone, code, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (throwable instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(RegisterActivity.this);
                } else {
                    mCodeEt.setError("验证码错误");
                    mCodeEt.postInvalidate();
                    /*new AlertDialog(RegisterActivity.this).builder().setTitle("验证码不正确，请重新输入 ")
                            .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();*/
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.d("authcode", "check:"+result);
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) == 1) {
                    Intent intent = new Intent(RegisterActivity.this, RegisterUserActivity.class);
                    intent.putExtra("reg_phone", phone);
                    startActivity(intent);
                    finish();
                } else {
                    onError(null, false);
                }
            }
        });
    }

    private void sendRegisterSMS(final String phone) {
        API.UserAPI.SendRegisterSMS(phone, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (throwable instanceof ConnectException) {
                    DialogUtil.showNetworkSetDialog(RegisterActivity.this);
                } else {
                    new MaterialDialog.Builder(RegisterActivity.this)
                            .content("获取验证码失败！")
                            .contentGravity(GravityEnum.CENTER)
                            .negativeText(R.string.cancel)
                            .positiveText("重新获取")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendRegisterSMS(phone);
                                }
                            })
                            .show();
                    /*new AlertDialog(RegisterActivity.this).builder()
                            .setMsg("获取验证码失败！")
                            .setPositiveButton("重新获取", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendRegisterSMS(mPhoneEt.getText().toString().trim());
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();*/
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.d("authcode", "SMS:"+result);
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                try {
                    Gson gson = new Gson();
                    String resp = gson.fromJson(result, String.class);
                    AuthCodeResp respInfo = gson.fromJson(resp, AuthCodeResp.class);
                    if (respInfo.resp != null && "000000".equals(respInfo.resp.respCode)) {
                        /*Intent intent = new Intent(RegisterActivity.this, AuthCodeActivity.class);
                        intent.putExtra("reg_phone", phone);
                        startActivityForResult(intent, 10);*/
                        countDownTimer.start();
                    } else {
                        new MaterialDialog.Builder(RegisterActivity.this)
                                .content("获取验证码失败！")
                                .contentGravity(GravityEnum.CENTER)
                                .negativeText(R.string.cancel)
                                .positiveText("重新获取")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        sendRegisterSMS(phone);
                                    }
                                })
                                .show();
                        /*new AlertDialog(RegisterActivity.this).builder()
                                .setMsg("获取验证码失败！")
                                .setPositiveButton("重新获取", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendRegisterSMS(mPhoneEt.getText().toString().trim());
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();*/
                    }
                } catch (JsonParseException e) {
                    onError(null, false);
                }
            }

        });
    }

    class AuthCodeResp {
        public AuthCode resp;
    }

    class AuthCode {
        public String respCode;
        public TemplateSMS templateSMS;
    }

    class TemplateSMS {
        public String createDate;
        public String smsId;
    }
}
