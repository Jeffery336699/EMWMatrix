package cc.emw.mobile.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.presenter.LoginPresenter;
import cc.emw.mobile.login.view.LoginView;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DialogUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import io.socket.client.Socket;

/**
 * 登录界面，旧的
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
    @ViewInject(R.id.tv_login_back)
    private TextView mBackTv;
    @ViewInject(R.id.btn_login)
    private Button mLoginBtn;
    @ViewInject(R.id.rl_login_save)
    private RelativeLayout mSaveLayout;
    @ViewInject(R.id.iv_dynamic_head)
    private CircleImageView mHeadIv;
    @ViewInject(R.id.font)
    private FrameLayout fontLayout;
    @ViewInject(R.id.back)
    private FrameLayout backLayout;

    private Dialog mLoadingDialog; //加载框
    private LoginPresenter loginPresenter;
    private String userName, password, comCode;
    private boolean isLoginYZX; // 是否可以登录云之讯

    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        int delay = 0;
        /*if (PrefsUtil.isFirst()) { // 第一次启动APP，进入引导界面
            delay = 1000;
        	PrefsUtil.setFirst(false);
        	Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }*/

        setSwipeBackEnable(false);

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

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        if (PrefsUtil.readUserInfo() != null && PrefsUtil.isLoginSave()) {
//            mUserNameEt.setVisibility(View.GONE);
//            mComCodeEt.setVisibility(View.GONE);
            backLayout.setVisibility(View.VISIBLE);
            fontLayout.setVisibility(View.GONE);
            mPasswordEt.setVisibility(View.VISIBLE);
            mSaveLayout.setVisibility(View.VISIBLE);
//            mLoginBtn.setText("登录");
            String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
            ImageLoader.getInstance().displayImage(uri, mHeadIv, options);
        }

        mBackTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fontLayout.setVisibility(View.VISIBLE);
                mUserNameEt.setVisibility(View.VISIBLE);
                mComCodeEt.setVisibility(View.VISIBLE);
//                mPasswordEt.setVisibility(View.GONE);
//                mSaveLayout.setVisibility(View.GONE);
//                mLoginBtn.setText("下一步");
                mHeadIv.setImageResource(R.drawable.cm_img_head);
                doHideAndShowAnimation(backLayout, fontLayout);
            }
        });
    }

    //设置setCameraDistance ，影响到rotateY 是Y 轴的长度。
    public void init() {
        float scale = getResources().getDisplayMetrics().density * 10000;
        fontLayout.setCameraDistance(scale);
        backLayout.setCameraDistance(scale);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event({R.id.btn_login, R.id.btn_logins})
    private void onLoginClick(View v) {
        backLayout.setVisibility(View.VISIBLE);
        fontLayout.setVisibility(View.VISIBLE);
        if (HelpUtil.isNetworkAvailable(this)) {
            userName = mUserNameEt.getText().toString().toLowerCase().trim();
            password = mPasswordEt.getText().toString().trim();
            comCode = mComCodeEt.getText().toString().toLowerCase().trim();
            if ("下一步".equals(((Button) v).getText().toString())) {
                if (PrefsUtil.readUserInfo() != null && userName.equalsIgnoreCase(PrefsUtil.readUserInfo().Code) && comCode.equalsIgnoreCase(PrefsUtil.readUserInfo().CompanyCode)) {
                    doHideAndShowAnimation(fontLayout, backLayout);
                    String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
                    ImageLoader.getInstance().displayImage(uri, mHeadIv, options);
                } else {
                    if (validate(userName, comCode)) {
                        RequestParams params = new RequestParams(Const.BASE_URL + "/UserAPI/GetUserInfoByEmail");
                        params.addQueryStringParameter("email", userName);
                        x.http().get(params, new RequestCallback<UserInfo>(UserInfo.class) {
                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                mLoadingDialog.dismiss();
                                //							showTipDialog("用户名或企业代码错误！");
                                //                            mUserNameEt.setVisibility(View.GONE);
                                //                            mComCodeEt.setVisibility(View.GONE);
                                mPasswordEt.setVisibility(View.VISIBLE);
                                mSaveLayout.setVisibility(View.VISIBLE);
                                //                            mLoginBtn.setText("登录");
                                mPasswordEt.setText("");
                                doHideAndShowAnimation(fontLayout, backLayout);
                            }

                            @Override
                            public void onStarted() {
                                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                                mLoadingDialog.show();
                            }

                            @Override
                            public void onParseSuccess(UserInfo respInfo) {
                                mLoadingDialog.dismiss();
                                //                            mUserNameEt.setVisibility(View.GONE);
                                //                            mComCodeEt.setVisibility(View.GONE);
                                mPasswordEt.setVisibility(View.VISIBLE);
                                mSaveLayout.setVisibility(View.VISIBLE);
                                //                            mLoginBtn.setText("登录");
                                mPasswordEt.setText("");
                                String uri = String.format(Const.DOWN_ICON_URL, respInfo.CompanyCode, respInfo.Image);
                                ImageLoader.getInstance().displayImage(uri, mHeadIv, options);
                                doHideAndShowAnimation(fontLayout, backLayout);
                            }
                        });
                    }
                }
            } else {
                if (validatePwd(password)) {
                    backLayout.setVisibility(View.VISIBLE);
                    fontLayout.setVisibility(View.GONE);
                    mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips1);
                    mLoadingDialog.show();
                    loginPresenter.login(userName, password, comCode);
                }
            }
        } else {
            showNetworkSetDialog();
        }
    }

    @Override
    public void saveLoginInfo(LoginResp respInfo) {
        PrefsUtil.cleanLoginCookie();
        PrefsUtil.cleanUserInfo();
        PrefsUtil.saveLoginDate();
        PrefsUtil.saveLoginCookie(respInfo);
        PrefsUtil.saveUserInfo(respInfo.User);

        if (mSaveCb.isChecked()) {
            PrefsUtil.saveLoginUser(userName, password, comCode, true);
        } else {
            PrefsUtil.saveLoginUser("", "", "", false);
        }

//		new ContactModelImple().getGroupList(null); // 需要在保存好Cookie再调用请求
        loginPresenter.getPersonList();

        /*String voipCode = PrefsUtil.readUserInfo().VoipCode;
        String voipPwd = PrefsUtil.readUserInfo().VoipPwd;
        if (!TextUtils.isEmpty(voipCode) && !TextUtils.isEmpty(voipPwd)) {
//				&& android.os.Build.VERSION.SDK_INT < 23) { //安卓6.0以上不可用
            isLoginYZX = true;
            // 启动云之讯Service
            startService(new Intent(LoginActivity.this, ConnectionService.class));
        }*/

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
    public void showTipToast(String tip) {

    }

    @Override
    public void showVersionDialog(Version ver) {
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

    /**
     * 非空验证
     *
     * @param username
     * @param comcode
     * @return
     */
    private boolean validate(String username, String comcode) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(username)) {
            tip = getString(R.string.login_tip_name);
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

    private boolean validatePwd(String password) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(password)) {
            tip = getString(R.string.login_tip_psw);
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

    //对两个Layout 作属性动画，font 布局旋转90度时，back 布局从-90度开始旋转到0度。注意设置旋转动画时的可点击性。
    public void doHideAndShowAnimation(final View fontView, final View backView) {
        fontView.animate().withStartAction(new Runnable() {
            @Override
            public void run() {
                fontView.setClickable(false);
                backView.setClickable(false);
                fontView.setRotationY(0);
                fontView.setAlpha(1);
                backView.setAlpha(0);
                backView.setRotationY(-90);
            }
        }).rotationY(90).withEndAction(new Runnable() {
            @Override
            public void run() {
                fontView.setAlpha(0);
                backView.setAlpha(1);
                backView.animate().rotationY(0).setDuration(1000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        backView.setClickable(true);
                    }
                }).start();
            }
        }).setDuration(1000).start();
    }

}
