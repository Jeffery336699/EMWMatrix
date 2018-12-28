package cc.emw.mobile.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.zf.iosdialog.widget.AlertDialog;

import org.json.JSONObject;
import org.xutils.common.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.BusDataInfo;
import cc.emw.mobile.entity.ErrorInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.NoteInfo;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.login.LoginActivity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.net.ApiEntity.UserSchedule;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;

/**
 * 请求回调
 *
 * @param <T>
 * @author shaobo.zhuang
 */
public abstract class RequestCallback<T> implements Callback.ProgressCallback<String> {

    private Class<T> mResponeClass;
    private CharacterParser characterParser;// 汉字转换成拼音的类
    private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类
    private boolean isAutoSort;// ContactActivity公司人员是否根据a-z进行排序源数据
    private boolean isConvert = true;// 是否将UserNote中的Content(JSON)转为给对象info(NoteInfo)
    private static AlertDialog mSessionDialog;// 会话超时弹出框
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public RequestCallback() {
        mResponeClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public RequestCallback(Class<T> responeClass) {
        mResponeClass = responeClass;
    }

    public RequestCallback(Class<T> responeClass, boolean isAutoSort) {
        mResponeClass = responeClass;
        this.isAutoSort = isAutoSort;
    }

    public RequestCallback(Class<T> responeClass, boolean isAutoSort,
                           boolean isConvert) {
        mResponeClass = responeClass;
        this.isAutoSort = isAutoSort;
        this.isConvert = isConvert;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    final String info = (String) msg.obj;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSessionDialog(info);
                        }
                    }, 1000);
                    break;
                case 2:
                    ErrorInfo error = (ErrorInfo) msg.obj;
                    // HttpException exception = new
                    // HttpException(error.getErrorCode());
                    // onFailure(exception, error.getError());
                    onError(new Throwable(error.Error), false);
                    break;
                case 3:
                    onParseSuccess((T) msg.obj);
                    break;
                case 4:
                    List<T> respList = (ArrayList<T>) msg.obj;
                    onParseSuccess(respList);
                    break;
                case 5:
                    ToastUtil.showToast(AppManager.currentActivity(), "返回的数据格式错误！");
                    break;
                case 6:
                    String respStr = (String) msg.obj;
                    onParseStrSuccess(respStr);
                    break;
            }
        }

    };

    @Override
    public void onSuccess(final String result) {
        // System.out.println("-----result ====="+result);
        Log.d("result", result);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    String resultStr = StringUtils.replaceBlank(result);
                    if (resultStr.startsWith("{")) {
                        ErrorInfo error = gson.fromJson(resultStr, ErrorInfo.class);
                        if (error != null && error.Error != null) {
                            if (error.ErrorCode == 1) { // 登录超时
                                Message msg = mHandler.obtainMessage();
                                msg.what = 1;
                                msg.obj = error.Error;
                                mHandler.sendMessage(msg);
                            } else { // 其他错误
                                Message msg = mHandler.obtainMessage();
                                msg.what = 2;
                                msg.obj = error;
                                mHandler.sendMessage(msg);
                            }
                        } else {
                            Message msg = mHandler.obtainMessage();
                            if (resultStr.startsWith("{\"Data\":[[{")) {
                                msg.what = 6;
                                msg.obj = resultStr;
                            } else {
                                msg.what = 3;
                                msg.obj = gson.fromJson(resultStr, mResponeClass);
                            }


                            if (isConvert && mResponeClass.equals(UserNote.class)) {
                                UserNote un = (UserNote) msg.obj;
                                convertUserNote(un);
                            }
                            mHandler.sendMessage(msg);
                        }
                    } else if (resultStr.startsWith("[")) {
                        ArrayList respList = new ArrayList<T>();
                        if (!resultStr.equals("[]") && !resultStr.equals("[null]")) {
                            JsonArray array = new JsonParser().parse(resultStr).getAsJsonArray();
                            for (final JsonElement elem : array) {
                                respList.add(gson.fromJson(elem, mResponeClass));
                            }
                            if (isAutoSort && mResponeClass.equals(UserInfo.class)) {
                                characterParser = CharacterParser.getInstance();
                                sortData(respList);
                                pinyinComparator = new PinyinComparator();
                                Collections.sort(respList, pinyinComparator);// 根据a-z进行排序源数据
                            }
                            if (isConvert && mResponeClass.equals(UserNote.class)) {
                                for (int i = 0; i < respList.size(); i++) {
                                    UserNote un = (UserNote) respList.get(i);
                                    convertUserNote(un);
                                }
                            }
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = 4;
                        msg.obj = respList;
                        mHandler.sendMessage(msg);
                    } else {
                        // mHandler.sendEmptyMessage(5);
                        sendError(resultStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendError("返回的数据格式错误");
                }
            }
        });
    }

    public void onParseSuccess(T respInfo) {
    }

    public void onParseSuccess(List<T> respList) {
    }

    public void onParseStrSuccess(String respStr) {
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
    }

    @Override
    public void onStarted() {
    }

    @Override
    public void onWaiting() {
    }

    @Override
    public void onCancelled(CancelledException cex) {
    }

    @Override
    public void onFinished() {
    }

    /**
     * 弹出会话超时对话框
     *
     * @param msg
     */
    private void showSessionDialog(String msg) {
        if (mSessionDialog == null) {
            final Context context = AppManager.currentActivity();
            mSessionDialog = new AlertDialog(context)
                    .builder()
                    .setTitle(context.getString(R.string.warm_tips))
                    .setCancelable(false)
                    .setMsg(msg)
                    .setPositiveButton(context.getString(R.string.login),
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context
                                            .getApplicationContext(),
                                            LoginActivity.class);
                                    PendingIntent restartIntent = PendingIntent.getActivity(
                                            context.getApplicationContext(), 0,
                                            intent,
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AlarmManager mgr = (AlarmManager) context
                                            .getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC,
                                            System.currentTimeMillis() + 0,
                                            restartIntent); // 0毫秒钟后重启应用
                                    AppManager.AppExit(context);// 退出程序

                                }
                            })
                    .setNegativeButton(context.getString(R.string.exit),
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppManager.AppExit(context);
                                }
                            });
            mSessionDialog.show();
        } else {
            if (!mSessionDialog.isShowing())
                mSessionDialog.show();
        }
    }

    private void sendError(String errmsg) {
        ErrorInfo error = new ErrorInfo();
        error.Error = errmsg;
        Message msg = mHandler.obtainMessage();
        msg.what = 2;
        msg.obj = error;
        mHandler.sendMessage(msg);
    }

    private void sortData(ArrayList<UserInfo> respList) {
        for (int i = 0; i < respList.size(); i++) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(respList.get(i).Name);
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    respList.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    respList.get(i).setSortLetters("#");
                }
            } else {
                respList.get(i).setSortLetters("#");
            }
        }
    }

    /**
     * 将UserNote中Property的JSON值转为NoteInfo对象所对应的字段
     *
     * @param un
     */
    public static void convertUserNote(UserNote un) {
        try {
            Gson gson = new Gson();
            un.info = new NoteInfo();
            switch (un.Type) {
                case UserNoteAddTypes.Normal: // 分享
                case UserNoteAddTypes.Notice: // 公告
                    if (un.AddType == UserNoteAddTypes.Image) { // 图片
                        Type type = new TypeToken<List<UserNoteFile>>() {
                        }.getType();
                        un.info.File = gson.fromJson(un.AddProperty, type);
                    } else if (un.AddType == UserNoteAddTypes.File) { // 文件
                        Type type = new TypeToken<List<UserNoteFile>>() {
                        }.getType();
                        un.info.File = gson.fromJson(un.AddProperty, type);
                    } else if (un.AddType == UserNoteAddTypes.Link) { // 链接
                        un.info.link = gson.fromJson(un.AddProperty, UserNoteLink.class);
                    } else if (un.AddType == UserNoteAddTypes.Vote) { // 投票
                        Type type = new TypeToken<List<UserRootVote>>() {
                        }.getType();
                        un.info.vote = gson.fromJson(un.AddProperty, type);
                    } else if (un.AddType == UserNoteAddTypes.Video) { // 视频
                        Type type = new TypeToken<List<UserNoteFile>>() {
                        }.getType();
                        un.info.File = gson.fromJson(un.AddProperty, type);
                    } else { // 普通

                    }
                    break;
                case UserNoteAddTypes.Schedule: // 日程
                    Type scheduleType = new TypeToken<List<UserSchedule>>() {
                    }.getType();
                    un.info.schedule = gson.fromJson(un.Property, scheduleType);
                    break;
                case UserNoteAddTypes.Task: // 工作分派
                    Type taskType = new TypeToken<List<UserFenPai>>() {
                    }.getType();
                    un.info.task = gson.fromJson(un.Property, taskType);

                    if (un.AddType == UserNoteAddTypes.File) { // 附件
                        Type type = new TypeToken<List<UserNoteFile>>() {
                        }.getType();
                        un.info.File = gson.fromJson(un.AddProperty, type);
                    }
                    break;
                case UserNoteAddTypes.Plan: // 工作计划
                    Type planType = new TypeToken<List<UserPlan>>() {
                    }.getType();
                    un.info.log = gson.fromJson(un.Property, planType);
                    break;
                case UserNoteAddTypes.Share: // 转发
                    if (un.ShareInfo != null && un.ShareInfo.size() > 0 && un.ShareInfo.get(0) != null) {
                        un.info.shareNote = gson.fromJson(gson.toJson(un.ShareInfo.get(0)), UserNote.class);
                        convertUserNote(un.info.shareNote);
                    } else {
                        Type shareToType = new TypeToken<List<UserNoteShareTo>>() {
                        }.getType();
                        un.info.shareTo = gson.fromJson(un.Property, shareToType);
                    }
                    break;
                case UserNoteAddTypes.Appoint: // 约会
                case UserNoteAddTypes.Phone: // 电话
                case UserNoteAddTypes.Email: // 邮件
                case UserNoteAddTypes.SeviceActive: // 服务活动
                    Type appointType = new TypeToken<List<UserSchedule>>() {
                    }.getType();
                    un.info.schedule = gson.fromJson(un.Property, appointType);

                    if (un.AddType == UserNoteAddTypes.File) { // 文件
                        Type type = new TypeToken<List<ApiEntity.Files>>() {
                        }.getType();
                        ArrayList<ApiEntity.Files> fileList = gson.fromJson(un.AddProperty, type);
                        un.info.File = new ArrayList<>();
                        for (int i = 0; i < fileList.size(); i++) {
                            UserNoteFile file = HelpUtil.files2UserNoteFile(fileList.get(i));
                            un.info.File.add(file);
                        }
                    } else if (un.AddType == 6) { // 业务数据
                        un.info.busData = gson.fromJson(un.AddProperty, BusDataInfo.class);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RequestCallback", "error:nid:" + un.ID);
        }
    }
}
