package cc.emw.mobile.me;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMPushManager;
import com.gc.materialdesign.views.ScrollView;
import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.LoginActivity2;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.widget.MenuPopup;
import cc.emw.mobile.me.widget.OnMenuItemClickListener;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CollapseView;

/**
 * 我·系统设置
 *
 * @author zrjt
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.view_dialog_bg)
    private View view;
    @ViewInject(R.id.tv_timeout_info)
    private TextView mTimeOutInfo;
    @ViewInject(R.id.sv_setting)
    private ScrollView mainScrollView;
    @ViewInject(R.id.cv_self_setting)
    private CollapseView mCvSelfSetting;
    private MenuPopup mMenuPopup;
    private TextView mContactPrivacy, mConCernPrivacy, mCollectPrivacy, mSendPrivacy, mWorkPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        init();
        getUserSetting();
    }

    private void init() {
        mMenuPopup = new MenuPopup(this, view);
        mMenuPopup.setDismissWhenTouchOuside(false);
        mCvSelfSetting.setScrollView(mainScrollView);
        mCvSelfSetting.setContent(R.layout.layout_user_privacy);
        mCvSelfSetting.setTagNameVis("", "隐私设置");
        mCvSelfSetting.setTagNameStyle(15, "#DE000000");
        mCvSelfSetting.findViewById(R.id.ll_contact_privacy).setOnClickListener(this);
        mCvSelfSetting.findViewById(R.id.ll_concern_privacy).setOnClickListener(this);
        mCvSelfSetting.findViewById(R.id.ll_collect_privacy).setOnClickListener(this);
        mCvSelfSetting.findViewById(R.id.ll_send_privacy).setOnClickListener(this);
        mCvSelfSetting.findViewById(R.id.ll_work_privacy).setOnClickListener(this);
        mContactPrivacy = (TextView) mCvSelfSetting.findViewById(R.id.tv_contact_privacy);
        mConCernPrivacy = (TextView) mCvSelfSetting.findViewById(R.id.tv_concern_privacy);
        mCollectPrivacy = (TextView) mCvSelfSetting.findViewById(R.id.tv_collect_privacy);
        mSendPrivacy = (TextView) mCvSelfSetting.findViewById(R.id.tv_send_privacy);
        mWorkPrivacy = (TextView) mCvSelfSetting.findViewById(R.id.tv_work_privacy);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                mMenuPopup.dismiss();
            }
        });

        mMenuPopup.setOnMenuClickListener(new OnMenuItemClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tx_1:
                        mMenuPopup.getmText().setText("所有人");
                        break;
                    case R.id.tx_2:
                        mMenuPopup.getmText().setText("关注者");
                        break;
                    case R.id.tx_3:
                        mMenuPopup.getmText().setText("组内人");
                        break;
                    case R.id.tx_4:
                        mMenuPopup.getmText().setText("自己");
                        break;
                    default:
                        break;
                }
                mMenuPopup.dismiss();
                List<ApiEntity.UserSetting> mDataList = checkPrivacy(mContactPrivacy, mConCernPrivacy, mCollectPrivacy, mSendPrivacy, mWorkPrivacy);
                modifyPrivacy(mDataList);
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.ll_me_relation, R.id.tv_me_self,
            R.id.tv_me_preference, R.id.tv_me_notification, R.id.tv_me_navigation, R.id.tv_exit, R.id.ll_pay_setting
            , R.id.itemProgress_daiban, R.id.itemProgress_about, R.id.itemProgress_twostep})
    private void onClicks(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.ll_pay_setting://支付设置  TODO     暂时关闭支付接口
               // intent = new Intent(this, PaySettingActivity.class);
               //intent = new Intent(this, IOSocketTestActivity.class);
                break;
            case R.id.ll_me_relation:
                intent = new Intent(this, PersonRelationActivity.class);
                break;
            case R.id.tv_me_self:
                intent = new Intent(this, PersonIntimateActivity.class);
                break;
            case R.id.tv_me_preference:
                Intent intentTimeOut = new Intent(this, PreferenceActivity2.class);
                intentTimeOut.putExtra("start_anim", false);
                startActivityForResult(intentTimeOut, 1000);
                break;
            case R.id.tv_me_notification:
                intent = new Intent(this, NotificationActivity.class);
                break;
            case R.id.tv_me_navigation:
                intent = new Intent(this, NavigationActivity.class);
                break;
            case R.id.tv_exit:
                ActionSheetDialog dialog = new ActionSheetDialog(
                        SettingActivity.this).builder();
                dialog.addSheetItem("退出后不会删除任何历史数据，下次登录依然可以使用本账号。", null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                            }
                        });
                dialog.addSheetItem("退出登录", null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (manager != null)
                                    manager.cancelAll();
                                sendBroadcast(new Intent(MainActivity.ACTION_FINISH_ACTIVITY));
                                CIMPushManager.destory(SettingActivity.this);
                                EMWApplication.mChatHistory.clear();
//                                clearCache(SettingActivity.this);
//                                clearWebViewCache();
                                PrefsUtil.cleanLoginCookie();
                                PrefsUtil.cleanLoginPwd();
                                PrefsUtil.setSwitch(true);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity2.class);
                                startActivity(intent);
                                AppManager.finishActivity(MainActivity.class);
                                finish();
                            }
                        });
                dialog.show_1();
                break;
            case R.id.itemProgress_daiban: // 代办工作
                intent = new Intent(this, WaitHandleActivity.class);
                break;
            case R.id.itemProgress_about: // 关于我
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.itemProgress_twostep: // 两步验证
                intent = new Intent(this, TwoStepActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("start_anim", false);
            int[] location = new int[2];
            v.getLocationInWindow(location);
            intent.putExtra("click_pos_y", location[1]);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                ApiEntity.UserSetting userSetting = (ApiEntity.UserSetting) data.getSerializableExtra("timeout");
                if (userSetting != null) {
                    if (userSetting.Key.equals("Login_TimeOut")) {
                        if ("5".equals(userSetting.Value)) {
                            mTimeOutInfo.setText("无操作5分钟后跳出登录窗口");
                        } else if ("10".equals(userSetting.Value)) {
                            mTimeOutInfo.setText("无操作10分钟后跳出登录窗口");
                        } else if ("15".equals(userSetting.Value)) {
                            mTimeOutInfo.setText("无操作15分钟后跳出登录窗口");
                        } else if ("20".equals(userSetting.Value)) {
                            mTimeOutInfo.setText("无操作20分钟后跳出登录窗口");
                        } else if ("-1".equals(userSetting.Value)) {
                            mTimeOutInfo.setText("从不超时");
                        } else {
                            mTimeOutInfo.setText("无操作15分钟后跳出登录窗口");
                        }
                    }
                }
            }
        }
    }

    private void setPrivacy(TextView mTv, String value) {
        if (value.equals("3")) {
            mTv.setText("自己");
        } else if (value.equals("2")) {
            mTv.setText("组内人");
        } else if (value.equals("1")) {
            mTv.setText("关注者");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_contact_privacy:
                mMenuPopup.showPopupWindow(v);
                mMenuPopup.setText(mContactPrivacy);
                break;
            case R.id.ll_concern_privacy:
                mMenuPopup.showPopupWindow(v);
                mMenuPopup.setText(mConCernPrivacy);
                break;
            case R.id.ll_collect_privacy:
                mMenuPopup.showPopupWindow(v);
                mMenuPopup.setText(mCollectPrivacy);
                break;
            case R.id.ll_send_privacy:
                mMenuPopup.showPopupWindow(v);
                mMenuPopup.setText(mSendPrivacy);
                break;
            case R.id.ll_work_privacy:
                mMenuPopup.showPopupWindow(v);
                mMenuPopup.setText(mWorkPrivacy);
                break;
        }
    }

    /**
     * 获取用户隐私设置
     */
    private void getUserSetting() {
        API.UserPubAPI.GetUserSetting(new RequestCallback<ApiEntity.UserSetting>(ApiEntity.UserSetting.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserSetting> respList) {
                if (respList != null && respList.size() > 0) {
                    for (ApiEntity.UserSetting userSetting : respList) {
                        if (userSetting.Key.equals("Login_TimeOut")) {
                            if ("5".equals(userSetting.Value)) {
                                mTimeOutInfo.setText("无操作5分钟后跳出登录窗口");
                            } else if ("10".equals(userSetting.Value)) {
                                mTimeOutInfo.setText("无操作10分钟后跳出登录窗口");
                            } else if ("15".equals(userSetting.Value)) {
                                mTimeOutInfo.setText("无操作15分钟后跳出登录窗口");
                            } else if ("20".equals(userSetting.Value)) {
                                mTimeOutInfo.setText("无操作20分钟后跳出登录窗口");
                            } else if ("-1".equals(userSetting.Value)) {
                                mTimeOutInfo.setText("从不超时");
                            } else {
                                mTimeOutInfo.setText("无操作15分钟后跳出登录窗口");
                            }
                        } else if (userSetting.Key.equals("Privacy_Follower")) {
                            setPrivacy(mConCernPrivacy, userSetting.Value);
                        } else if (userSetting.Key.equals("Privacy_Like")) {
                            setPrivacy(mCollectPrivacy, userSetting.Value);
                        } else if (userSetting.Key.equals("Privacy_Publish")) {
                            setPrivacy(mSendPrivacy, userSetting.Value);
                        } else if (userSetting.Key.equals("Privacy_Job")) { //职位相关
                            setPrivacy(mWorkPrivacy, userSetting.Value);
                        } else if (userSetting.Key.equals("Privacy_Contact")) {
                            setPrivacy(mContactPrivacy, userSetting.Value);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(SettingActivity.this, "获取数据失败！");
            }
        });
    }

    /**
     * 修改用户隐私设置
     *
     * @param mDatas
     */
    private void modifyPrivacy(List<ApiEntity.UserSetting> mDatas) {
        API.UserPubAPI.AddUserSetting(mDatas, 2, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {

                } else {
                    onError(null, false);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(SettingActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<ApiEntity.UserSetting> checkPrivacy(TextView mContactPrivacy, TextView mConCernPrivacy, TextView mCollectPrivacy,
                                                     TextView mSendPrivacy, TextView mWorkPrivacy) {
        List<ApiEntity.UserSetting> mDataLists = new ArrayList<>();
        ApiEntity.UserSetting mContactUserSetting = new ApiEntity.UserSetting();
        ApiEntity.UserSetting mConcernUserSetting = new ApiEntity.UserSetting();
        ApiEntity.UserSetting mCollectUserSetting = new ApiEntity.UserSetting();
        ApiEntity.UserSetting mSendUserSetting = new ApiEntity.UserSetting();
        ApiEntity.UserSetting mWorkUserSetting = new ApiEntity.UserSetting();

        //联系人
        if (mContactPrivacy.getText().toString().equals("自己")) {
            mContactUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mContactUserSetting.Key = "Privacy_Contact";
            mContactUserSetting.UType = 2;
            mContactUserSetting.Value = "3";
            mDataLists.add(mContactUserSetting);
        } else if (mContactPrivacy.getText().toString().equals("组内人")) {
            mContactUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mContactUserSetting.Key = "Privacy_Contact";
            mContactUserSetting.UType = 2;
            mContactUserSetting.Value = "2";
            mDataLists.add(mContactUserSetting);
        } else if (mContactPrivacy.getText().toString().equals("关注者")) {
            mContactUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mContactUserSetting.Key = "Privacy_Contact";
            mContactUserSetting.UType = 2;
            mContactUserSetting.Value = "1";
            mDataLists.add(mContactUserSetting);
        }

        //关注
        if (mConCernPrivacy.getText().toString().equals("自己")) {
            mConcernUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mConcernUserSetting.Key = "Privacy_Follower";
            mConcernUserSetting.UType = 2;
            mConcernUserSetting.Value = "3";
            mDataLists.add(mConcernUserSetting);
        } else if (mConCernPrivacy.getText().toString().equals("组内人")) {
            mConcernUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mConcernUserSetting.Key = "Privacy_Follower";
            mConcernUserSetting.UType = 2;
            mConcernUserSetting.Value = "2";
            mDataLists.add(mConcernUserSetting);
        } else if (mConCernPrivacy.getText().toString().equals("关注者")) {
            mConcernUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mConcernUserSetting.Key = "Privacy_Follower";
            mConcernUserSetting.UType = 2;
            mConcernUserSetting.Value = "1";
            mDataLists.add(mConcernUserSetting);
        }

        //收藏
        if (mCollectPrivacy.getText().toString().equals("自己")) {
            mCollectUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mCollectUserSetting.Key = "Privacy_Like";
            mCollectUserSetting.UType = 2;
            mCollectUserSetting.Value = "3";
            mDataLists.add(mCollectUserSetting);
        } else if (mCollectPrivacy.getText().toString().equals("组内人")) {
            mCollectUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mCollectUserSetting.Key = "Privacy_Like";
            mCollectUserSetting.UType = 2;
            mCollectUserSetting.Value = "2";
            mDataLists.add(mCollectUserSetting);
        } else if (mCollectPrivacy.getText().toString().equals("关注者")) {
            mCollectUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mCollectUserSetting.Key = "Privacy_Like";
            mCollectUserSetting.UType = 2;
            mCollectUserSetting.Value = "1";
            mDataLists.add(mCollectUserSetting);
        }

        //发布
        if (mSendPrivacy.getText().toString().equals("自己")) {
            mSendUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mSendUserSetting.Key = "Privacy_Publish";
            mSendUserSetting.UType = 2;
            mSendUserSetting.Value = "3";
            mDataLists.add(mSendUserSetting);
        } else if (mSendPrivacy.getText().toString().equals("组内人")) {
            mSendUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mSendUserSetting.Key = "Privacy_Publish";
            mSendUserSetting.UType = 2;
            mSendUserSetting.Value = "2";
            mDataLists.add(mSendUserSetting);
        } else if (mSendPrivacy.getText().toString().equals("关注者")) {
            mSendUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mSendUserSetting.Key = "Privacy_Publish";
            mSendUserSetting.UType = 2;
            mSendUserSetting.Value = "1";
            mDataLists.add(mSendUserSetting);
        }

        //职位
        if (mWorkPrivacy.getText().toString().equals("自己")) {
            mWorkUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mWorkUserSetting.Key = "Privacy_Job";
            mWorkUserSetting.UType = 2;
            mWorkUserSetting.Value = "3";
            mDataLists.add(mWorkUserSetting);
        } else if (mWorkPrivacy.getText().toString().equals("组内人")) {
            mWorkUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mWorkUserSetting.Key = "Privacy_Job";
            mWorkUserSetting.UType = 2;
            mWorkUserSetting.Value = "2";
            mDataLists.add(mWorkUserSetting);
        } else if (mWorkPrivacy.getText().toString().equals("关注者")) {
            mWorkUserSetting.UserID = PrefsUtil.readUserInfo().ID;
            mWorkUserSetting.Key = "Privacy_Job";
            mWorkUserSetting.UType = 2;
            mWorkUserSetting.Value = "1";
            mDataLists.add(mWorkUserSetting);
        }
        return mDataLists;
    }

    /**
     * WebView清空缓存
     */
    public void clearCache(Context context) {
        /*CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
        context.deleteDatabase("webviewCookiesChromium.db");
        context.deleteDatabase("webviewCookiesChromiumPrivate.db");
        //WebView 缓存文件
        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()+"/webviewCacheChromium");
        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }*/
        CookieManager cm = CookieManager.getInstance();
        cm.removeSessionCookie();
        cm.removeAllCookie();
    }

    private static final String APP_CACAHE_DIRNAME = "/webcache";
    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache(){
        //清理Webview缓存数据库
        try {
            boolean del1 = deleteDatabase("webview.db");
            boolean del2 = deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
//        File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME);
        File appCacheDir = new File("/data/data/cc.emw.mobile/files/"+APP_CACAHE_DIRNAME);
//        Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());

//        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
        File webviewCacheDir = new File("/data/data/cc.emw.mobile/cache/webviewCache");
//        Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }
    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
//        Log.i(TAG, "delete file path=" + file.getAbsolutePath());
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
//        else {
//            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
//        }
    }

}
