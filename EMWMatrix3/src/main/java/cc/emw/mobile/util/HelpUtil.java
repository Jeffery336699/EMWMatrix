package cc.emw.mobile.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.view.IconTextView;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallSession;
import io.rong.imlib.model.Conversation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class HelpUtil {

    /**
     * @param i
     * @param count
     * @return
     */
    public static String getPercent(int i, int count) {
        DecimalFormat format = new DecimalFormat("#");
        return format.format((i * 100) / count) + "%";
    }

    public static CharSequence getImageString(Context context, int imgResId, int strResId) {
        //		return getImageString(context, imgResId, context.getString(strResId));
        return null;
    }

    /**
     * Md5加密
     *
     * @param string
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 弹出软键盘
     *
     * @param context
     * @param view
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//隐藏软键盘
    }

    public static int getTaskResId(int yxj) {
        int resId = R.drawable.shape_ico_blue;
        switch (yxj) {
            case 1:
                resId = R.drawable.shape_ico_blue;
                break;
            case 2:
                resId = R.drawable.shape_ico_orange;
                break;
            case 3:
                resId = R.drawable.shape_ico_red;
                break;
        }
        return resId;
    }

	/*public static int getScheduleResId(int color) {
        int resId = R.drawable.round_cal_bg_0;
		switch (color) {
			case 1:
				resId = R.drawable.round_cal_bg_1;
				break;
			case 2:
				resId = R.drawable.round_cal_bg_2;
				break;
			case 3:
				resId = R.drawable.round_cal_bg_3;
				break;
			case 4:
				resId = R.drawable.round_cal_bg_4;
				break;
		}
		return resId;
	}*/

    /**
     * C#时间转为Java的字符串
     *
     * @param template     为目标时间的格式
     * @param milliseconds 时间long字符串
     */
    public static String time2String(String template, String milliseconds) {
        try {
            milliseconds = milliseconds.substring(6, milliseconds.length() - 7);
            Date date = new Date(Long.parseLong(milliseconds));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat(template,
                    Locale.getDefault());
            return dateFormat1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return milliseconds;
        }
    }

    /**
     * @param template     为目标时间的格式
     * @param curtemplate  为当前的时间格式
     * @param milliseconds 时间字符串
     */
    public static String time2String(String template, String curtemplate, String milliseconds) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(template, Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(curtemplate, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat2.parse(milliseconds);
            return dateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * C#时间转为Date
     *
     * @param milliseconds 为目标时间的格式
     * @return
     */
    public static Date string2Time(String milliseconds) {
        try {
            milliseconds = milliseconds.substring(6, milliseconds.length() - 7);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(milliseconds));
            return calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 转为C#日期
     *
     * @param date
     * @return
     */
    public static String time2CSharp(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("Z");
        String zone = format.format(date);
        return "/Date(" + date.getTime() + zone + ")/";
    }

    // 将字符串转为时间戳
    public static long getTime(String user_time) {
        long re_time = 0;
        String pattern = "yyyy-MM-dd";
        if (user_time.length() > 10) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d;
        try {
            d = sdf.parse(user_time);
            re_time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * UserInfo 转换成 TalkerUserInfo
     *
     * @param user
     * @return
     */
    public static ApiEntity.TalkerUserInfo userInfo2TalkerUserInfo(ApiEntity.UserInfo user) {
        ApiEntity.TalkerUserInfo talkerUser = new ApiEntity.TalkerUserInfo();
        if (user != null) {
            talkerUser.ID = user.ID;
            talkerUser.Name = user.Name;
            talkerUser.Image = user.Image;
            talkerUser.Job = user.Job;
            talkerUser.IsFollow = user.IsFollow;
            talkerUser.IsOnline = user.IsOnline;
            talkerUser.CompanyCode = user.CompanyCode;
        }
        return talkerUser;
    }

    /**
     * Files 转换成 UserNoteFile
     *
     * @param files
     * @return
     */
    public static UserNote.UserNoteFile files2UserNoteFile(ApiEntity.Files files) {
        UserNote.UserNoteFile file = new UserNote.UserNoteFile();
        if (files != null) {
            file.FileId = files.ID;
            file.Url = files.Url;
            file.FileName = files.Name;
            file.Length = files.Length;
            file.CreateUser = files.Creator;
            file.CompanyCode = files.CompanyCode;
        }
        return file;
    }

    public static GroupInfo ChatterGroup2GroupInfo(ApiEntity.ChatterGroup chatterGroup) {
        GroupInfo groupInfo = new GroupInfo();
        if (chatterGroup != null) {
            groupInfo.ID = chatterGroup.ID;
            groupInfo.Name = chatterGroup.Name;
            groupInfo.Users = chatterGroup.Users;
            groupInfo.Color = chatterGroup.Color;
            groupInfo.Image = chatterGroup.Image;
            groupInfo.CreateUser = chatterGroup.CreateUser;
            groupInfo.IsAddIn = chatterGroup.IsAddIn;
            groupInfo.Type = chatterGroup.Type;
        }
        return groupInfo;
    }

    /**
     * 把知识库集合id转成以逗号分隔的id字符串
     *
     * @param files
     * @return
     */
    public static String files2StrID(ArrayList<ApiEntity.Files> files) {
        if (files == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        //        sb.append("[");
        for (int i = 0; i < files.size(); i++) {
            sb.append(files.get(i).ID);
            if (i != files.size() - 1) {
                sb.append(",");
            }
        }
        //        sb.append("]");
        return sb.toString().trim();
    }

    /**
     * 设置图标
     *
     * @param context
     * @param textView
     * @param iconCode 图标代码
     */
    public static void setTextIcon(Context context, TextView textView, String iconCode) {
        StringBuilder str = new StringBuilder();
        if (!TextUtils.isEmpty(iconCode)) {
            str.append("&#x").append(iconCode).append(";");
        } else {
            str.append("&#xe94e;");
        }
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont/EMW-matrix.ttf");
        textView.setTypeface(typeface);
        textView.setText(Html.fromHtml(str.toString())); // "&#xe94e;"
    }

    public static String getFilePowerText(String power) {
        StringBuilder powerText = new StringBuilder();
        if (!TextUtils.isEmpty(power) && TextUtils.isDigitsOnly(power)) {
            if (Integer.valueOf(power) == 0) {
                powerText.append("无");
            } else {
                char[] chars = power.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1') {
                        if (i == 0) {
                            powerText.append("查看");
                        } else if (i == 1) {
                            if (!TextUtils.isEmpty(powerText))
                                powerText.append("、");
                            powerText.append("编辑");
                        } else if (i == 2) {
                            if (!TextUtils.isEmpty(powerText))
                                powerText.append("、");
                            powerText.append("下载");
                        } else if (i == 3) {
                            if (!TextUtils.isEmpty(powerText))
                                powerText.append("、");
                            powerText.append("共享");
                        } else if (i == 4) {
                            if (!TextUtils.isEmpty(powerText))
                                powerText.append("、");
                            powerText.append("删除");
                        }
                    }
                }
            }
        }
        return powerText.toString();
    }

    public static String getFilePowerNumber(String power) {
        String powerText = "";
        if (!TextUtils.isEmpty(power) && TextUtils.isDigitsOnly(power)) {
            if (Integer.valueOf(power) == 0) {
                powerText = "无权限";
            } else {
                int count = 0;
                char[] chars = power.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1') {
                        count++;
                    }
                }
                if (count > 0) {
                    powerText = count + "权限";
                }
            }
        }
        return powerText;
    }

    /**
     * 设置动态类型图标
     *
     * @param un
     * @param iconTextView
     */
    public static void setDynamicType(UserNote un, ImageView iconTextView) {
        int resID = R.drawable.dynamictype_ic_all;
        if (un.info != null) {
            switch (un.Type) {
                case ApiEnum.UserNoteAddTypes.Normal: //分享
                case ApiEnum.UserNoteAddTypes.Notice: //公告
                    if (ApiEnum.UserNoteAddTypes.Image == un.AddType) { //图片
                        resID = R.drawable.dynamictype_ic_image;
                    } else if (ApiEnum.UserNoteAddTypes.File == un.AddType) { //文件
                        resID = R.drawable.dynamictype_ic_file;
                    } else if (ApiEnum.UserNoteAddTypes.Link == un.AddType) { //链接
                        resID = R.drawable.dynamictype_ic_link;
                    } else if (ApiEnum.UserNoteAddTypes.Vote == un.AddType) { //投票
                        resID = R.drawable.dynamictype_ic_vote;
                    } else if (ApiEnum.UserNoteAddTypes.Video == un.AddType) { //视频
                        resID = R.drawable.dynamictype_ic_video;
                    } else { //普通
                        resID = R.drawable.dynamictype_ic_dynamic;
                    }
                    break;
                case ApiEnum.UserNoteAddTypes.Schedule: //日程
                    resID = R.drawable.dynamictype_ic_schedule;
                    break;
                case ApiEnum.UserNoteAddTypes.Task: //工作分派
                    resID = R.drawable.dynamictype_ic_task;
                    break;
                case ApiEnum.UserNoteAddTypes.Plan: //工作计划
                    resID = R.drawable.dynamictype_ic_plan;
                    break;
                case ApiEnum.UserNoteAddTypes.Share: //转发
                    resID = R.drawable.dynamictype_ic_dynamic; //TODO
                    break;
                case ApiEnum.UserNoteAddTypes.Appoint: //约会
                    resID = R.drawable.dynamictype_ic_date;
                    break;
                case ApiEnum.UserNoteAddTypes.Phone: //电话
                    resID = R.drawable.dynamictype_ic_phone;
                    break;
                case ApiEnum.UserNoteAddTypes.Email: //邮件
                    resID = R.drawable.dynamictype_ic_mail;
                    break;
                case ApiEnum.UserNoteAddTypes.SeviceActive: //服务活动
                    resID = R.drawable.dynamictype_ic_activity;
                    break;
                default:
                    break;
            }
        }
        if (iconTextView != null) {
            iconTextView.setBackgroundResource(resID);
        }
    }

    /**
     * 设置动态类型图标
     *
     * @param un
     * @param iconTextView
     */
    public static void setDynamicType(UserNote un, IconTextView iconTextView) {
        String iconCode = "ea51";
        int resID = R.drawable.dynamic_circleshare_bg;
        if (un.info != null) {
            switch (un.Type) {
                case ApiEnum.UserNoteAddTypes.Normal: //分享
                case ApiEnum.UserNoteAddTypes.Notice: //公告
                    if (ApiEnum.UserNoteAddTypes.Image == un.AddType) { //图片
                        iconCode = "ec00";
                        resID = R.drawable.dynamic_circleimage_bg;
                    } else if (ApiEnum.UserNoteAddTypes.File == un.AddType) { //文件
                        iconCode = "eb05";
                        resID = R.drawable.dynamic_circlefile_bg;
                    } else if (ApiEnum.UserNoteAddTypes.Link == un.AddType) { //链接
                        iconCode = "eb36";
                        resID = R.drawable.dynamic_circlelink_bg;
                    } else if (ApiEnum.UserNoteAddTypes.Vote == un.AddType) { //投票
                        iconCode = "eb31";
                        resID = R.drawable.dynamic_circlevote_bg;
                    } else if (ApiEnum.UserNoteAddTypes.Video == un.AddType) { //视频
                        iconCode = "ea06";
                        resID = R.drawable.dynamic_circlevideo_bg;
                    } else { //普通

                    }
                    break;
                case ApiEnum.UserNoteAddTypes.Schedule: //日程
                    iconCode = "e916";
                    resID = R.drawable.dynamic_circlecalendar_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Task: //工作分派
                    iconCode = "e924";
                    resID = R.drawable.dynamic_circletask_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Plan: //工作计划
                    iconCode = "e92c";
                    resID = R.drawable.dynamic_circleplan_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Share: //转发
                    iconCode = "ecea";
                    resID = R.drawable.dynamic_circleshareto_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Appoint: //约会
                    iconCode = "ecc4";
                    resID = R.drawable.dynamic_circlefile_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Phone: //电话
                    iconCode = "eca4";
                    resID = R.drawable.dynamic_circleshareto_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.Email: //邮件
                    iconCode = "ea73";
                    resID = R.drawable.dynamic_circleimage_bg;
                    break;
                case ApiEnum.UserNoteAddTypes.SeviceActive: //服务活动
                    iconCode = "e9bc";
                    resID = R.drawable.dynamic_circlecalendar_bg;
                    break;
                default:
                    break;
            }
        }
        if (iconTextView != null) {
            iconTextView.setIconText(iconCode);
            iconTextView.setBackgroundResource(resID);
        }
    }

    /**
     * 设置动态筛选类型图标
     *
     * @param position
     * @param iconTextView
     */
    public static void setDynamicType(int position, ImageView iconTextView) {
        int resID = R.drawable.dynamictype_ic_all;
        switch (position) {
            case 0: //全部
                break;
            case 1: //动态
                resID = R.drawable.dynamictype_ic_dynamic;
                break;
            case 2: //图片
                resID = R.drawable.dynamictype_ic_image;
                break;
            case 3: //视频
                resID = R.drawable.dynamictype_ic_video;
                break;
            case 4: //文件
                resID = R.drawable.dynamictype_ic_file;
                break;
            case 5: //链接
                resID = R.drawable.dynamictype_ic_link;
                break;
            case 6: //投票
                resID = R.drawable.dynamictype_ic_vote;
                break;
            case 7: //日程
                resID = R.drawable.dynamictype_ic_schedule;
                break;
            case 8: //工作分派
                resID = R.drawable.dynamictype_ic_task;
                break;
            case 9: //工作计划
                resID = R.drawable.dynamictype_ic_plan;
                break;
            case 10: //约会
                resID = R.drawable.dynamictype_ic_date;
                break;
            case 11: //服务活动
                resID = R.drawable.dynamictype_ic_activity;
                break;
            case 12: //邮件
                resID = R.drawable.dynamictype_ic_mail;
                break;
            case 13: //电话
                resID = R.drawable.dynamictype_ic_phone;
                break;
            default:
                break;
        }
        if (iconTextView != null) {
            iconTextView.setBackgroundResource(resID);
            iconTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置动态筛选类型图标
     *
     * @param position
     * @param iconTextView
     */
    public static void setDynamicType(int position, IconTextView iconTextView) {
        String iconCode = "ec69";
        int resID = R.drawable.dynamic_circleall_bg;
        switch (position) {
            case 0: //全部
                break;
            case 1: //文件
                iconCode = "eb05";
                resID = R.drawable.dynamic_circlefile_bg;
                break;
            case 2: //图片
                iconCode = "ec00";
                resID = R.drawable.dynamic_circleimage_bg;
                break;
            case 3: //链接
                iconCode = "eb36";
                resID = R.drawable.dynamic_circlelink_bg;
                break;
            case 4: //投票
                iconCode = "eb31";
                resID = R.drawable.dynamic_circlevote_bg;
                break;
            case 5: //视频
                iconCode = "ea06";
                resID = R.drawable.dynamic_circlevideo_bg;
                break;
            case 6: //工作分派
                iconCode = "e924";
                resID = R.drawable.dynamic_circletask_bg;
                break;
            case 7: //工作计划
                iconCode = "e92c";
                resID = R.drawable.dynamic_circleplan_bg;
                break;
            case 8: //日程
                iconCode = "e916";
                resID = R.drawable.dynamic_circlecalendar_bg;
                break;
            case 9: //约会
                iconCode = "ecc4";
                resID = R.drawable.dynamic_circlefile_bg;
                break;
            case 10: //电话
                iconCode = "eca4";
                resID = R.drawable.dynamic_circleshareto_bg;
                break;
            case 11: //邮件
                iconCode = "ea73";
                resID = R.drawable.dynamic_circleimage_bg;
                break;
            case 12: //服务活动
                iconCode = "e9bc";
                resID = R.drawable.dynamic_circlecalendar_bg;
                break;
            case 13: //转发
                iconCode = "ecea";
                resID = R.drawable.dynamic_circleshareto_bg;
                break;
            default:
                break;
        }
        if (iconTextView != null) {
            iconTextView.setIconText(iconCode);
            iconTextView.setBackgroundResource(resID);
            iconTextView.setVisibility(View.VISIBLE);
        }
    }

    public static String getDynamicTypeText(int type) {
        String text = "";
        switch (type) {
            case ApiEnum.UserNoteAddTypes.Normal:
                //text = "显示全部";
                text = "筛选";
                break;
            case ApiEnum.UserNoteAddTypes.File:
                text = "文件";
                break;
            case ApiEnum.UserNoteAddTypes.Image:
                text = "图片";
                break;
            case ApiEnum.UserNoteAddTypes.Link:
                text = "链接";
                break;
            case ApiEnum.UserNoteAddTypes.Vote:
                text = "投票";
                break;
            case ApiEnum.UserNoteAddTypes.Video:
                text = "视频";
                break;
            case ApiEnum.UserNoteAddTypes.Task:
                text = "任务";
                break;
            case ApiEnum.UserNoteAddTypes.Plan:
                text = "计划";
                break;
            case ApiEnum.UserNoteAddTypes.Schedule:
                text = "日程";
                break;
            case ApiEnum.UserNoteAddTypes.Appoint:
                text = "约会";
                break;
            case ApiEnum.UserNoteAddTypes.Phone:
                text = "电话";
                break;
            case ApiEnum.UserNoteAddTypes.Email:
                text = "邮件";
                break;
            case ApiEnum.UserNoteAddTypes.SeviceActive:
                text = "活动";
                break;
            case ApiEnum.UserNoteAddTypes.Share:
                text = "转发";
                break;
            case ApiEnum.UserNoteAddTypes.Action:
                text = "动态";
                break;
        }
        return text;
    }

    public static String getFileURL(UserNote.UserNoteFile noteFile) {
        if (noteFile == null)
            return "";
        String fileUrl;
        if (noteFile.Url != null && noteFile.Url.contains("UserImage")) {
            fileUrl = Const.BASE_URL + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("/")) {
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("\\")) {
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url.replaceAll("\\\\", "/");
        } else {
            fileUrl = String.format(Const.DOWN_FILE_URL,
                    TextUtils.isEmpty(noteFile.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : noteFile.CompanyCode,
                    noteFile.Url);
        }
        return fileUrl;
    }

    public static String getFileURL(UserNote.UserNoteVote noteFile) {
        if (noteFile == null)
            return "";
        String fileUrl;
        if (noteFile.Url != null && noteFile.Url.startsWith("/")) { // /emw/UserFile/8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("\\")) { // \emw\UserFile\8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url.replaceAll("\\\\", "/");
        } else {
            fileUrl = String.format(Const.DOWN_FILE_URL,
                    TextUtils.isEmpty(noteFile.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : noteFile.CompanyCode,
                    noteFile.Url);
        }
        return fileUrl;
    }

    public static String getFileURL(ApiEntity.Files noteFile) {
        if (noteFile == null)
            return "";
        String fileUrl;
        if (noteFile.Url != null && noteFile.Url.startsWith("/")) { // /emw/UserFile/8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("\\")) { // \emw\UserFile\8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url.replaceAll("\\\\", "/");
        } else {
            /*fileUrl = String.format(Const.DOWN_FILE_URL,
                    TextUtils.isEmpty(noteFile.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : noteFile.CompanyCode,
					noteFile.Url);*/
            fileUrl = String.format(Const.DOWN_FILE_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    noteFile.Url);
        }
        return fileUrl;
    }

    public static String getFileImage(ApiEntity.Files noteFile) {
        if (noteFile == null)
            return "";
        String fileUrl;
        if (noteFile.Url != null && noteFile.Url.startsWith("/")) { // /emw/UserFile/8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("\\")) { // \emw\UserFile\8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url.replaceAll("\\\\", "/");
        } else {
            fileUrl = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    noteFile.Url);
        }
        return fileUrl;
    }

    public static String getFileVideo(ApiEntity.Files noteFile) {
        if (noteFile == null)
            return "";
        String fileUrl;
        if (noteFile.Url != null && noteFile.Url.startsWith("/")) { // /emw/UserFile/8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url;
        } else if (noteFile.Url != null && noteFile.Url.startsWith("\\")) { // \emw\UserFile\8CC9C7674C7B4AB196CCC5A8894362E6.png
            fileUrl = Const.DOWN_FILE_URL2 + noteFile.Url.replaceAll("\\\\", "/");
        } else {
            fileUrl = Const.BASE_URL + "/" + noteFile.Url;
        }
        return fileUrl;
    }

    public static String getFileURL(String url) {
        if (url == null)
            return "";
        String fileUrl;
        if (url.startsWith("/")) {
            fileUrl = Const.DOWN_FILE_URL2 + url;
        } else if (url.startsWith("\\")) {
            fileUrl = Const.DOWN_FILE_URL2 + url.replaceAll("\\\\", "/");
        } else {
            fileUrl = String.format(Const.DOWN_FILE_URL, PrefsUtil.readUserInfo().CompanyCode, url);
        }
        return fileUrl;
    }

    public static boolean isInBackground(Context context) {
        List<ActivityManager.RunningTaskInfo> tasksInfo = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (tasksInfo.size() > 0) {

            if (context.getPackageName().equals(
                    tasksInfo.get(0).topActivity.getPackageName())) {

                return false;
            }
        }
        return true;
    }

    /**
     * 判断最后listView中最后一个item是否完全显示出来
     * listView 是集合的那个ListView
     *
     * @return true完全显示出来，否则false
     */
    public static boolean isLastItemVisible(ListView listView) {
        final Adapter adapter = listView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = listView.getLastVisiblePosition();

        /**
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - listView.getFirstVisiblePosition();
            final int childCount = listView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = listView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= listView.getBottom();
            }
        }

        return false;
    }

    public static Badge bindBadgeTarget(Context context, View targetView) {
        return new QBadgeView(context)
                .bindTarget(targetView)
                .setBadgeBackgroundColor(Color.parseColor("#08AE43"))
                .setBadgeTextSize(6, true)
                .setBadgePadding(2, true)
                .setGravityOffset(0, false);
    }

    //CallKit start 2
    public static boolean startVoice(Context context, UserInfo userInfo) {
        if (TextUtils.isEmpty(PrefsUtil.readUserInfo().RongYunToken)) {
            ToastUtil.showToast(context, "你暂未开通语音通话服务，请联系管理员申请开通。");
            return false;
        }

        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(context, context.getString(R.string.rc_voip_call_start_fail), Toast.LENGTH_SHORT).show();
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(context, context.getString(R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userInfo != null && !TextUtils.isEmpty(userInfo.RongYunToken)) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            intent.putExtra("targetId", String.valueOf(userInfo.ID));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
            return true;
        } else {
            ToastUtil.showToast(context, "对方暂未开通语音通话服务，请联系管理员申请开通。");
            return false;
        }
    }

    public static boolean startVideo(Context context, UserInfo userInfo) {
        if (TextUtils.isEmpty(PrefsUtil.readUserInfo().RongYunToken)) {
            ToastUtil.showToast(context, "你暂未开通语音通话服务，请联系管理员申请开通。");
            return false;
        }

        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(context, context.getString(R.string.rc_voip_call_start_fail), Toast.LENGTH_SHORT).show();
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(context, context.getString(R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userInfo != null && !TextUtils.isEmpty(userInfo.RongYunToken)) {
            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
            intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            intent.putExtra("targetId", String.valueOf(userInfo.ID));
            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
            return true;
        } else {
            ToastUtil.showToast(context, "对方暂未开通视频通话服务，请联系管理员申请开通。");
            return false;
        }
    }
    //CallKit end 2

    public static int getColorForPosition(int position) {
        int color = R.color.icon_color1;
        position = position > 9 ? position % 10 : position;
        switch (position) {
            case 0:
                color = R.color.icon_color1;
                break;
            case 1:
                color = R.color.icon_color2;
                break;
            case 2:
                color = R.color.icon_color3;
                break;
            case 3:
                color = R.color.icon_color4;
                break;
            case 4:
                color = R.color.icon_color5;
                break;
            case 5:
                color = R.color.icon_color6;
                break;
            case 6:
                color = R.color.icon_color7;
                break;
            case 7:
                color = R.color.icon_color8;
                break;
            case 8:
                color = R.color.icon_color9;
                break;
            case 9:
                color = R.color.icon_color10;
                break;
        }
        return color;
    }

    public static boolean isPkgInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

	/*public static UserBean userInfo2UserBean(UserInfo userInfo) {
        UserBean userBean = new UserBean();
		userBean.ID = Long.valueOf(userInfo.ID+"");
		userBean.IdentityID = userInfo.IdentityID;
		userBean.Name = userInfo.Name;
		userBean.Image = userInfo.Image;
		userBean.BackImage = userInfo.BackImage;
		userBean.Job = userInfo.Job;
		userBean.Sex = userInfo.Sex;
		userBean.Birthday = userInfo.Birthday;
		userBean.Age = userInfo.Age;
		userBean.DeptName = userInfo.DeptName;
		userBean.IsFollow = userInfo.IsFollow;
		userBean.DeptID = userInfo.DeptID;
		userBean.IsOnline = userInfo.IsOnline;
		userBean.Code = userInfo.Code;
		userBean.Phone = userInfo.Phone;
		userBean.Password = userInfo.Password;
		userBean.UserType = userInfo.UserType; //UserTypes
		userBean.CompanyCode = userInfo.CompanyCode;
		userBean.Email = userInfo.Email;
		userBean.VoipCode = userInfo.VoipCode;
		userBean.VoipPwd = userInfo.VoipPwd;
		userBean.DeviceToken = userInfo.DeviceToken;
		userBean.EmailSignText = userInfo.EmailSignText;
		userBean.Axis = userInfo.Axis;
		userBean.InitState = userInfo.InitState; //UserInitState
		userBean.JobExperience = userInfo.JobExperience;
		userBean.ClassSkill = userInfo.ClassSkill;
		userBean.College = userInfo.College;
		userBean.HighSchool = userInfo.HighSchool;
		return userBean;
	}

	public static UserInfo userBean2UserInfo(UserBean userInfo) {
		UserInfo userBean = new UserInfo();
		userBean.ID = Integer.valueOf(userInfo.ID+"");
		userBean.IdentityID = userInfo.IdentityID;
		userBean.Name = userInfo.Name;
		userBean.Image = userInfo.Image;
		userBean.BackImage = userInfo.BackImage;
		userBean.Job = userInfo.Job;
		userBean.Sex = userInfo.Sex;
		userBean.Birthday = userInfo.Birthday;
		userBean.Age = userInfo.Age;
		userBean.DeptName = userInfo.DeptName;
		userBean.IsFollow = userInfo.IsFollow;
		userBean.DeptID = userInfo.DeptID;
		userBean.IsOnline = userInfo.IsOnline;
		userBean.Code = userInfo.Code;
		userBean.Phone = userInfo.Phone;
		userBean.Password = userInfo.Password;
		userBean.UserType = userInfo.UserType; //UserTypes
		userBean.CompanyCode = userInfo.CompanyCode;
		userBean.Email = userInfo.Email;
		userBean.VoipCode = userInfo.VoipCode;
		userBean.VoipPwd = userInfo.VoipPwd;
		userBean.DeviceToken = userInfo.DeviceToken;
		userBean.EmailSignText = userInfo.EmailSignText;
		userBean.Axis = userInfo.Axis;
		userBean.InitState = userInfo.InitState; //UserInitState
		userBean.JobExperience = userInfo.JobExperience;
		userBean.ClassSkill = userInfo.ClassSkill;
		userBean.College = userInfo.College;
		userBean.HighSchool = userInfo.HighSchool;
		return userBean;
	}*/
}
