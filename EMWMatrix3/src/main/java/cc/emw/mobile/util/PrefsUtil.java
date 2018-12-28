package cc.emw.mobile.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;

import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserInfo;

/**
 * SharePreference工具
 *
 * @author shaobo.zhuang
 */
public class PrefsUtil {

    public static final String KEY_USERNAME = "user_name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_COMCODE = "com_code";
    public static final String KEY_ISSAVE = "is_save";
    public static final String KEY_COOKIE = "cookie";
    public static final String KEY_ISCHECKVER = "is_checkver";
    public static final String KEY_LOGINDATE = "login_date";
    public static final String KEY_USERINFO = "user_info";
    public static final String KEY_ISFIRST = "is_first";
    public static final String KEY_CALGUIDE = "cal_guide";
    public static final String KEY_ISNOTICE = "is_notice";
    public static final String KEY_AUDIO_DEVICE = "audio_device";
    public static final String KEY_ISSWITCH = "is_switch";
    public static final String KEY_ISFORMWEB = "is_formweb";

    public static String KEY_IMGBGURL = "bgurl";
    public static final String KEY_ISCHILDREPLY = "is child_reply"; // 记录是否是子评论
    private static Context mContext;
    private static UserInfo mUser;
    private static LoginResp mCookie;

    public static void init(Context context) {
        mContext = context;
        // Initialize the Prefs class
        new Prefs.Builder().setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true).build();
    }

    /**
     * 保存这次用户登录信息
     *
     * @param userName
     * @param password
     * @param comCode
     * @param isSave
     */
    public static void saveLoginUser(String userName, String password,
                                     String comCode, boolean isSave) {
        Prefs.putString(KEY_USERNAME, userName);
        Prefs.putString(KEY_PASSWORD, password);
        Prefs.putString(KEY_COMCODE, comCode);
        Prefs.putBoolean(KEY_ISSAVE, isSave);
    }

    /**
     * 获取上次用户登录信息
     *
     * @param userNameEt
     * @param passwordEt
     * @param comCodeEt
     * @param saveCb
     */
    public static void readLoginUser(EditText userNameEt, EditText passwordEt,
                                     EditText comCodeEt, CheckBox saveCb) {
        if (isLoginSave()) {
            userNameEt.setText(Prefs.getString(KEY_USERNAME, ""));
            passwordEt.setText(Prefs.getString(KEY_PASSWORD, ""));
            comCodeEt.setText(Prefs.getString(KEY_COMCODE, ""));
            if (saveCb != null)
                saveCb.setChecked(Prefs.getBoolean(KEY_ISSAVE, false));
        }
    }

    /**
     * 清除当前用户密码
     */
    public static void cleanLoginPwd() {
        Prefs.putString(KEY_PASSWORD, "");
    }

    /**
     * 设置是否每次登录询问更新
     *
     * @param isCheck
     */
    public static void setLoginCheckver(boolean isCheck) {
        Prefs.putBoolean(KEY_ISCHECKVER, isCheck);
    }

    /**
     * 优先级：设置每次登录询问更新 > 不在同一天询问更新
     *
     * @return 是否检查新版本
     */
    public static boolean isLoginCheckver() {
        String lastDate = readLoginDate();
        String curDate = DateFormat.getDateInstance().format(new Date());
        return Prefs.getBoolean(KEY_ISCHECKVER, true)
                || !lastDate.equals(curDate);
    }

    /**
     * 保存这次登录日期
     */
    public static void saveLoginDate() {
        String loginDate = DateFormat.getDateInstance().format(new Date());
        Prefs.putString(KEY_LOGINDATE, loginDate);
    }

    /**
     * 获取上次登录日期
     *
     * @return
     */
    public static String readLoginDate() {
        return Prefs.getString(KEY_LOGINDATE, "");
    }

    /**
     * 保存当前用户信息
     *
     * @param user
     */
    public static void saveUserInfo(UserInfo user) {
        Prefs.putString(KEY_USERINFO, new Gson().toJson(user));
        mUser = null;
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static UserInfo readUserInfo() {
        if (mUser == null) {
            mUser = new Gson().fromJson(Prefs.getString(KEY_USERINFO, ""),
                    UserInfo.class);
        }
        return mUser;
    }

    /**
     * 清除当前用户信息
     */
    public static void cleanUserInfo() {
        mUser = null;
    }

    /**
     * 保存登录的cookie
     *
     * @param cookie
     */
    public static void saveLoginCookie(LoginResp cookie) {
        Prefs.putString(KEY_COOKIE, new Gson().toJson(cookie));
        mCookie = null;
    }

    /**
     * 获取登录的cookie
     *
     * @return
     */
    public static LoginResp readLoginCookie() {
        if (mCookie == null) {
            mCookie = new Gson().fromJson(Prefs.getString(KEY_COOKIE, ""),
                    LoginResp.class);
        }
        return mCookie;
    }

    /**
     * 清除当前用户信息
     */
    public static void cleanLoginCookie() {
        Prefs.putString(KEY_COOKIE, "");
        mCookie = null;
    }

    public static boolean isLoginSave() {
        return Prefs.getBoolean(KEY_ISSAVE, false);
    }

    /**
     * 设置是否第一次进入APP
     *
     * @param isFirst
     */
    public static void setFirst(boolean isFirst) {
        Prefs.putBoolean(KEY_ISFIRST, isFirst);
    }

    /**
     * 设置是否第一次进入日程
     *
     * @param isFirst
     */
    public static void setCalGuide(boolean isFirst) {
        Prefs.putBoolean(KEY_CALGUIDE, isFirst);
    }

    /**
     * 是否第一次进入APP
     *
     * @return
     */
    public static boolean isFirst() {
        return Prefs.getBoolean(KEY_ISFIRST, true);
    }

    /**
     * 是否第一次进入日程
     *
     * @return
     */
    public static boolean isCalFirst() {
        return Prefs.getBoolean(KEY_CALGUIDE, true);
    }

    /**
     * 设置是否通知栏显示信息通知
     *
     * @param isNotice
     */
    public static void setNotice(boolean isNotice) {
        Prefs.putBoolean(KEY_ISNOTICE, isNotice);
    }

    /**
     * 是否通知栏显示信息通知
     *
     * @return
     */
    public static boolean isNotice() {
        return Prefs.getBoolean(KEY_ISNOTICE, true);
    }

    /**
     * 设置该评论是否是子评论
     *
     * @param isChildReply
     */
    public static void setChildReply(boolean isChildReply) {
        Prefs.putBoolean(KEY_ISCHILDREPLY, isChildReply);
    }

    /**
     * 是否是子评论
     *
     * @return 是 返回true,不是返回false
     */
    public static boolean isChildReply() {
        return Prefs.getBoolean(KEY_ISCHILDREPLY, false);
    }

    public static void setKeyImgbgurl(String uri) {
        Prefs.putString(KEY_IMGBGURL, uri);
    }

    public static String getKeyImgbgurl() {
        return Prefs.getString(KEY_IMGBGURL, "");
    }

    public static void setAudioDevice(int device) {
        Prefs.putInt(KEY_AUDIO_DEVICE, device);
    }

    public static int getAudioDevice() {
        return Prefs.getInt(KEY_AUDIO_DEVICE, -1);
    }

    /**
     * 设置是否切换用户
     *
     * @param isSwitch
     */
    public static void setSwitch(boolean isSwitch) {
        Prefs.putBoolean(KEY_ISSWITCH, isSwitch);
    }

    /**
     * 是否切换用户
     *
     * @return
     */
    public static boolean isSwitch() {
        return Prefs.getBoolean(KEY_ISSWITCH, false);
    }

    /**
     * 设置是否使用Web表单（可在设置》导航设置》长按标题切换）
     * @param isFormWeb true:web表单；false:原生表单
     */
    public static void setFormWeb(boolean isFormWeb) {
        Prefs.putBoolean(KEY_ISFORMWEB, isFormWeb);
    }

    /**
     * 是否使用Web表单
     * @return
     */
    public static boolean isFormWeb() {
        return Prefs.getBoolean(KEY_ISFORMWEB, true);
    }
}
