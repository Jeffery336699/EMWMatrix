package cc.emw.mobile.project.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.dynamic.DateActivity;
import cc.emw.mobile.dynamic.DateDetailActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.dynamic.MailCreateActivity;
import cc.emw.mobile.dynamic.MailDetailActivity;
import cc.emw.mobile.dynamic.PhoneActivity;
import cc.emw.mobile.dynamic.PhoneDetailActivity;
import cc.emw.mobile.dynamic.PlanActivity;
import cc.emw.mobile.dynamic.PlanDetailActivity;
import cc.emw.mobile.dynamic.ServiceCreateActivity;
import cc.emw.mobile.dynamic.ServiceDetailActivity;
import cc.emw.mobile.dynamic.ShareActivity2;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.file.FileSelectActivity2;
import cc.emw.mobile.form.FormWebActivity;
import cc.emw.mobile.login.ScannerLoginActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.map.GeoFenceActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.CircularAnim;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.FloatingActionMenu;
import cc.emw.mobile.view.ProgressWebView;

/**
 * TimeTracking首页 TimeTracking web
 * Created by shaobo.zhuang on 2016/11/30.
 */
@ContentView(R.layout.fragment_time_tracking_web)
public class TimeTrackingWebFragment extends BaseFragment {

    @ViewInject(R.id.webview)
    private ProgressWebView webView;

    public static final String ACTION_REFRESH_TIMEDYNAMIC = "cc.emw.mobile.refresh_timedynamic"; //
    public static final String ACTION_TIME_DYNAMICDISCUSS = "cc.emw.mobile.time_dynamicdiscuss"; //
    public static final String ACTION_TIME_DELETEDISCUSS = "cc.emw.mobile.time_deletediscuss"; //

    private static String url = Const.BASE_URL + "/Web_App/new_task/index.html";
    private Handler handler;
    private SimpleDateFormat format, format2;
    private MyBroadcastReceive mReceive;
    private String jsFuncation, jsParam, type, selectType;
    public static String landPortChange = "landPortChange";
    public static String jsPagePosStr = "www/schedule.html";
    public static String jsPageUserCalPosStr = "www/members.html";
    private String jsPagePos = "unKnow";

    public static FloatingActionMenu mActionMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("formweb", "url:" + url);
        format = new SimpleDateFormat(getString(R.string.timeformat6), Locale.getDefault());
        format2 = new SimpleDateFormat(getString(R.string.timeformat5), Locale.getDefault());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:

                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), FormWebActivity.class);
                        intent.putExtra("url", msg.obj.toString());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent photoIntent = new Intent(getActivity(), PhotoActivity.class);
                        photoIntent.putExtra(PhotoActivity.INTENT_EXTRA_ISFORMAT, false);
                        photoIntent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, msg.obj.toString());
                        startActivity(photoIntent);
                        break;
                    case 4:
                        try {
                            final String[] arr = (String[]) msg.obj;
                            final Calendar now = Calendar.getInstance();
                            if (!TextUtils.isEmpty(arr[1])) {
                                if (arr[0].equals("0")) {
                                    now.setTimeInMillis(format.parse(arr[1]).getTime());
                                } else {
                                    now.setTimeInMillis(format2.parse(arr[1]).getTime());
                                }
                            }
                            DatePickerDialog dpdStart = DatePickerDialog.newInstance(
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                            final String mStrStartTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                                            if (arr[0].equals("1")) {
                                                //调用JS中的 函数，当然也可以不传参
                                                webView.loadUrl("javascript:" + arr[2] + "('" + mStrStartTime + "','" + arr[3] + "')");
                                            } else {
                                                TimePickerDialog tpd = TimePickerDialog.newInstance(
                                                        new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                                                String mStrStartTimes = mStrStartTime + " " + checkNum(hourOfDay) + ":" + checkNum(minute);
                                                                webView.loadUrl("javascript:" + arr[2] + "('" + mStrStartTimes + "' ,'" + arr[3] + "')");
                                                            }
                                                        },
                                                        now.get(Calendar.HOUR_OF_DAY),
                                                        now.get(Calendar.MINUTE),
                                                        true
                                                );
                                                tpd.setAccentColor(getResources().getColor(R.color.cm_main_text));
                                                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                                            }
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
                            dpdStart.setAccentColor(getResources().getColor(R.color.cm_main_text));
                            dpdStart.show(getActivity().getFragmentManager(), "Datepickerdialog");
                        } catch (Exception e) {

                        }
                        break;
                    case 5:
                        try {
                            switch (msg.arg1) {
                                case ApiEnum.UserNoteAddTypes.Schedule: //日程
                                    Intent scheduleIntent = new Intent(getActivity(), CalendarInfoActivity2.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        scheduleIntent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    scheduleIntent.putExtra("start_anim", false);
                                    startActivity(scheduleIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Task: //工作分派
                                    Intent taskIntent = new Intent(getActivity(), TaskDetailActivity.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        taskIntent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    taskIntent.putExtra("start_anim", false);
                                    startActivity(taskIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Plan: //工作计划
                                    Intent planIntent = new Intent(getActivity(), PlanDetailActivity.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {

                                    } else {
                                        Type planType = new TypeToken<List<ApiEntity.UserPlan>>() {
                                        }.getType();
                                        ArrayList<ApiEntity.UserPlan> planList = new Gson().fromJson(msg.obj.toString(), planType);
                                        planIntent.putExtra("plan_list", planList);
                                    }
                                    planIntent.putExtra("start_anim", false);
                                    startActivity(planIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Appoint: //约会
                                case ApiEnum.UserNoteAddTypes.Phone: //电话
                                case ApiEnum.UserNoteAddTypes.Email: //邮件
                                case ApiEnum.UserNoteAddTypes.SeviceActive: //服务活动
                                    Intent otherIntent = new Intent();
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {

                                    } else {
                                        Type appointType = new TypeToken<List<ApiEntity.UserSchedule>>() {
                                        }.getType();
                                        ArrayList<ApiEntity.UserSchedule> otherList = new Gson().fromJson(msg.obj.toString(), appointType);
                                        if (otherList != null && otherList.size() > 0) {
                                            otherIntent.putExtra("user_schedule", otherList.get(0));
                                        }
                                    }
                                    if (msg.arg1 == ApiEnum.UserNoteAddTypes.Appoint) {
                                        otherIntent.setClass(getActivity(), DateDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Phone) {
                                        otherIntent.setClass(getActivity(), PhoneDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Email) {
                                        otherIntent.setClass(getActivity(), MailDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.SeviceActive) {
                                        otherIntent.setClass(getActivity(), ServiceDetailActivity.class);
                                    }
                                    otherIntent.putExtra("start_anim", false);
                                    try {
                                        startActivity(otherIntent);
                                    } catch (ActivityNotFoundException e) {

                                    }
                                    break;
                                default:
                                    Intent dynamicIntent = new Intent(getActivity(), DynamicDetailActivity.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        dynamicIntent.putExtra("note_id", Integer.valueOf(msg.obj.toString()));
                                    }
                                    dynamicIntent.putExtra("start_anim", false);
                                    startActivity(dynamicIntent);
                                    break;
                            }
                            break;
                        } catch (Exception e) {
                            Log.e("formweb", "error:" + e.getMessage());
                        }
                    case 6:
                        if (msg.arg1 == 1) {
                            getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                        } else if (msg.arg1 == 2) {
                            getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                        }
                        break;
                    case 7:
                        boolean isInstalled = HelpUtil.isPkgInstalled("com.autonavi.minimap", getActivity()); //1.判断用户手机是否安装高德地图APP
                        if (isInstalled) { //2.首选使用高德地图APP完成导航
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("androidamap://keywordNavi?");
                            try {
                                stringBuilder.append("sourceApplication=Talker");//填写应用名称
                                stringBuilder.append("&keyword=" + URLEncoder.encode(msg.toString(), "utf-8"));//导航目的地
                                stringBuilder.append("&style=2");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent mapIntent = new Intent(); //调用高德地图APP
                            mapIntent.setPackage("com.autonavi.minimap");
                            mapIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            mapIntent.setAction(Intent.ACTION_VIEW);
                            mapIntent.setData(Uri.parse(stringBuilder.toString())); //传递组装的数据
                            startActivity(mapIntent);
                        } else if (HelpUtil.isPkgInstalled("com.baidu.BaiduMap", getActivity())) {
                            Intent i1 = new Intent();// 驾车导航
                            i1.setData(Uri.parse("baidumap://map/navi?query=" + msg.toString()));
                            startActivity(i1);
                        } else {

                        }
                        break;
                    case 8:
                        Intent keyboardIntent = new Intent(MainActivity.ACTION_TALKER_COMMENT);
                        keyboardIntent.putExtra("is_show", true);
                        keyboardIntent.putExtra("note_id", msg.arg1);
                        keyboardIntent.putExtra("enter_flag", 1);
                        getActivity().sendBroadcast(keyboardIntent);
                        break;
                    case 9:
                        if (mActionMenu != null) {
                            mActionMenu.setVisibility(msg.arg1 == 0 ? View.GONE : View.VISIBLE);
                            String[] strs = msg.obj.toString().split(",");
                            if (strs.length > 1) {
                                mActionMenu.setTag(R.id.tag_first, Integer.valueOf(strs[0]));
                                mActionMenu.setTag(R.id.tag_second, Integer.valueOf(strs[1]));
                            }
                        }
                        break;
                    case 10:
                        ArrayList list = (ArrayList) msg.obj;
                        Intent discussIntent = new Intent(getActivity(), DynamicDiscussActivity.class);
                        if (list.size() > 0) {
                            discussIntent.putExtra("user_note", (UserNote) list.get(0));
                        }
                        if (list.size() > 1) {
                            discussIntent.putExtra("rev_note", (ApiEntity.UserNote) list.get(1));
                        }
                        discussIntent.putExtra("enter_flag", 1);
                        discussIntent.putExtra("start_anim", false);
                        startActivity(discussIntent);
                        break;
                    case 11:
                        String[] strs = msg.obj.toString().split(",");
                        if (strs.length > 3) {
                            jsFuncation = strs[0];
                            jsParam = strs[1];
                            type = strs[2];
                            selectType = strs[3];
                        }
                        if ("1".equals(type)) {
                            Intent imgIntent = new Intent(getActivity(), FileSelectActivity2.class);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, "1".equals(selectType) ? FileSelectActivity2.RADIO_SELECT : FileSelectActivity2.MULTI_SELECT);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
                            startActivityForResult(imgIntent, "1".equals(selectType) ? 10100 : 10101);
                        } else {
                            Intent fileIntent = new Intent(getActivity(), FileSelectActivity.class);
                            fileIntent.putExtra("start_anim", false);
                            startActivityForResult(fileIntent, 10101);
                        }
                        break;
                    case 12:
                        String[] strLocation = msg.obj.toString().split(",");
                        if (strLocation.length > 1) {
                            jsFuncation = strLocation[0];
                            jsParam = strLocation[1];
                        }
                        Intent locationIntent = new Intent(getActivity(), GeoFenceActivity.class);
                        startActivityForResult(locationIntent, 143);
                        break;
                    case 13:
                        String[] scanner = msg.obj.toString().split(",");
                        if (scanner.length > 0) {
                            jsFuncation = scanner[0];
                            if (scanner.length > 1) {
                                jsParam = scanner[1];
                            }
                        }
                        Intent scannerIntent = new Intent(getActivity(), ScannerLoginActivity.class);
                        startActivityForResult(scannerIntent, 10102);
                        break;
                    case 14:
                        if (jsPagePos.equals(jsPagePosStr) || jsPagePos.equals(jsPageUserCalPosStr)) {
                            webView.loadUrl("javascript:" + landPortChange + "('" + 1 + "','" + jsPagePos + "')");
                        }
                        break;
                    case 15:
                        if (jsPagePos.equals(jsPagePosStr) || jsPagePos.equals(jsPageUserCalPosStr)) {
                            webView.loadUrl("javascript:" + landPortChange + "('" + 0 + "','" + jsPagePos + "')");
                        }
                        break;
                    case 16:
                        jsPagePos = msg.obj.toString();
                        break;
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionMenu = (FloatingActionMenu) view.findViewById(R.id.menu);
        mActionMenu.setClosedOnTouchOutside(true);
        initView();
        syncCookie(getActivity(), Const.BASE_URL);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(ACTION_REFRESH_TIMEDYNAMIC);
        intentFilter.addAction(ACTION_TIME_DYNAMICDISCUSS);
        intentFilter.addAction(ACTION_TIME_DELETEDISCUSS);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                if (webView != null)
                    webView.loadUrl("javascript:SetNoteNumber('" + count + "')");
            } else if (ACTION_REFRESH_TIMEDYNAMIC.equals(action)) {
                if (webView != null)
                    webView.loadUrl("javascript:PageInit()");
            } else if (ACTION_TIME_DYNAMICDISCUSS.equals(action)) {
                int noteID = intent.getIntExtra("note_id", 0);
                ApiEntity.UserNote rev = (ApiEntity.UserNote) intent.getSerializableExtra("rev_note");
                if (webView != null && noteID > 0 && rev != null)
                    webView.loadUrl("javascript:talker_reply('" + new Gson().toJson(rev) + "', " + noteID + ")");
            } else if (ACTION_TIME_DELETEDISCUSS.equals(action)) {
                int noteID = intent.getIntExtra("note_id", 0);
                ApiEntity.UserNote delrev = (ApiEntity.UserNote) intent.getSerializableExtra("delrev_note");
                if (webView != null && noteID > 0 && delrev != null)
                    webView.loadUrl("javascript:ReplayDel(" + noteID + ", " + delrev.ID + ")");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
        if (webView != null){
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onFirstUserVisible() {
        webView.loadUrl(url);
    }

    @Override
    public void onUserVisible() {
        if (webView != null)
            webView.loadUrl("javascript:SetNoteNumber('" + MainActivity.count + "')");
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url WebView要加载的url
     * @return true 同步cookie成功，false同步cookie失败
     */
    public static boolean syncCookie(Context context, String url) {
        LoginResp loginResp = PrefsUtil.readLoginCookie();
        if (loginResp == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.removeAllCookie();
        cookieManager.setCookie(url, "u=" + loginResp.u);
        cookieManager.setCookie(url, "c=" + loginResp.c);
        cookieManager.setCookie(url, "s=" + loginResp.s);
        cookieManager.setCookie(url, "p=" + loginResp.p);
        CookieSyncManager.getInstance().sync();
        String newCookie = cookieManager.getCookie(url);
        Log.e("formweb", "newCookie:" + newCookie);
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

    private void initView() {
//        webView = (ProgressWebView) view.findViewById(R.id.webview);
        webView.setShowProgressbar(false);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAllowFileAccess(true);// 设置允许访问文件数据
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
//		setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
//		setting.setDefaultTextEncodingName("GBK");//设置字符编码
        webView.addJavascriptInterface(new AndroidForJs(getActivity()), "JsCallNative");
        webView.setOnWebCallBack(new ProgressWebView.OnWebCallBack() {
            @Override
            public void getTitle(String title) {

            }

            @Override
            public void getUrl(String url) {

            }

            @Override
            public void loadFinish() {
                webView.loadUrl("javascript:SetNoteNumber('" + MainActivity.count + "')");
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键 时的操作
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
    }

    public class AndroidForJs {

        private Context mContext;

        public AndroidForJs(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface//这句标识必须要写上否则会出问题
        public void close() {
            Log.e("formweb", "close()");
            handler.sendEmptyMessage(1);
        }

        @JavascriptInterface
        public void openUrl(String url) {
            Log.e("formweb", "openUrl:" + url);
            Message msg = handler.obtainMessage();
            msg.what = 2;
            msg.obj = url;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openPhoto(String urls) {
            Log.e("formweb", "openPhoto:" + urls);
            Message msg = handler.obtainMessage();
            msg.what = 3;
            msg.obj = urls;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openTimePicker(String tag, String selectTime, String jsFuncationName, String id) {
            Message msg = handler.obtainMessage();
            msg.what = 4;
            String[] arr = (tag + "," + selectTime + "," + jsFuncationName + "," + id).split(",");
            msg.obj = arr;
            handler.sendMessage(msg);
        }

        /**
         * @param type UserNoteAddTypes
         * @param json
         */
        @JavascriptInterface
        public void openDetail(int type, String json) {
            Log.e("formweb", "openDetail:type=" + type + ", jsonStr=" + json);
            Message msg = handler.obtainMessage();
            msg.what = 5;
            msg.arg1 = type;
            msg.obj = json != null ? json : "";
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openMenu(int type) {
            Log.e("formweb", "openMenu:type=" + type);
            Message msg = handler.obtainMessage();
            msg.what = 6;
            msg.arg1 = type;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openNavi(String addr) {
            Log.e("formweb", "openNavi:addr=" + addr);
            Message msg = handler.obtainMessage();
            msg.what = 7;
            msg.obj = addr != null ? addr : "";
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openKeyboard(int noteID) {
            Log.e("formweb", "openKeyboard:noteID=" + noteID);
            Message msg = handler.obtainMessage();
            msg.what = 8;
            msg.arg1 = noteID;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openActionMenu(int visibility, int groupID, int projectID) {
            Log.e("formweb", "openActionMenu:visibility=" + visibility + ", groupID=" + groupID + ", projectID=" + projectID);
            Message msg = handler.obtainMessage();
            msg.what = 9;
            msg.arg1 = visibility;
            msg.obj = groupID + "," + projectID;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openDynamicDiscuss(String json, String revJson) {
            Log.e("formweb", "openDynamicDiscuss:json=" + json);
            try {
                Message msg = handler.obtainMessage();
                msg.what = 10;
                ArrayList list = new ArrayList();
                Gson gson = new Gson();
                UserNote un = gson.fromJson(json, UserNote.class);
                if (un != null) {
                    list.add(un);
                    ApiEntity.UserNote revNote = gson.fromJson(revJson, ApiEntity.UserNote.class);
                    if (revNote != null) {
                        list.add(revNote);
                    }
                    msg.obj = list;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openFilePicker(String jsFuncation, String jsParam, int type, int selectType) {
            Log.e("formweb", "openFilePicker:jsFuncation=" + jsFuncation + ", id=" + jsParam);
            Message msg = handler.obtainMessage();
            msg.what = 11;
            msg.obj = jsFuncation + "," + jsParam + "," + type + "," + selectType;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openSelectLocation(String jsFuncation, String jsParam) {
            Message msg = handler.obtainMessage();
            msg.what = 12;
            msg.obj = jsFuncation + "," + jsParam;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void openScanner(String jsFuncation, String jsParam) {
            Log.e("formweb", "openScanner:jsFuncation=" + jsFuncation + ", id=" + jsParam);
            Message msg = handler.obtainMessage();
            msg.what = 13;
            msg.obj = jsFuncation + "," + jsParam;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void onCalendarSelect(String jsParam) {
            Message msg = handler.obtainMessage();
            msg.what = 16;
            msg.obj = jsParam;
            handler.sendMessage(msg);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 10100) {
            ApiEntity.Files file = (ApiEntity.Files) data.getSerializableExtra("select_file");
            String json = new Gson().toJson(file);
            Log.e("formweb", "onActivityResult:json=" + json);
            webView.loadUrl("javascript:" + jsFuncation + "('" + json + "' ,'" + jsParam + "')");
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 10101) {
            ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
            for (int i = 0, count = fileList.size(); i < count; i++) {
                ApiEntity.Files files = fileList.get(i);
                files.Url = "";
            }
            String json = new Gson().toJson(fileList);
            Log.e("formweb", "onActivityResult:json=" + json);
            webView.loadUrl("javascript:" + jsFuncation + "('" + json + "' ,'" + jsParam + "')");
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 10102) {
            String result = data.getStringExtra("result");
            Log.e("formweb", "onActivityResult:result=" + result);
            webView.loadUrl("javascript:" + jsFuncation + "('" + result + "' ,'" + jsParam + "')");
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 143) {
            ApiEntity.UserRail userRail = (ApiEntity.UserRail) data.getSerializableExtra("UserRail");
            int id = data.getIntExtra("UserRailId", 0);
            if (userRail != null && id != 0) {
//                cInfo.RailID = id;
//                cInfo.Rail = userRail;
//                mLocationTv.setText(userRail.Address);
                String json = new Gson().toJson(userRail);
                webView.loadUrl("javascript:" + jsFuncation + "('" + json + "' ,'" + id + "','" + jsParam + "')");
            }
        }
    }

    @Event({R.id.menu_item1, R.id.menu_item2, R.id.menu_item3, R.id.menu_item4, R.id.menu_item5, R.id.menu_item6, R.id.menu_item7, R.id.menu_item8})
    private void onActionClick(View v) {
        final int groupID = (int) mActionMenu.getTag(R.id.tag_first);
        final int projectID = (int) mActionMenu.getTag(R.id.tag_second);
        switch (v.getId()) {
            case R.id.menu_item1: //新建活动
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent serviceIntent = new Intent(getActivity(), ServiceCreateActivity.class);
                                serviceIntent.putExtra("group_id", groupID);
                                serviceIntent.putExtra("project_id", projectID);
                                serviceIntent.putExtra("start_anim", false);
                                getActivity().startActivity(serviceIntent);
                            }
                        });
                break;
            case R.id.menu_item2: //新建邮件
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent mailIntent = new Intent(getActivity(), MailCreateActivity.class);
                                mailIntent.putExtra("group_id", groupID);
                                mailIntent.putExtra("project_id", projectID);
                                mailIntent.putExtra("start_anim", false);
                                getActivity().startActivity(mailIntent);
                            }
                        });
                break;
            case R.id.menu_item3: //新建电话
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent phoneIntent = new Intent(getActivity(), PhoneActivity.class);
                                phoneIntent.putExtra("group_id", groupID);
                                phoneIntent.putExtra("project_id", projectID);
                                phoneIntent.putExtra("start_anim", false);
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.menu_item4: //新建约会
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent dateIntent = new Intent(getActivity(), DateActivity.class);
                                dateIntent.putExtra("group_id", groupID);
                                dateIntent.putExtra("project_id", projectID);
                                dateIntent.putExtra("start_anim", false);
                                startActivity(dateIntent);
                            }
                        });
                break;
            case R.id.menu_item5: //新建日程
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent scheduleIntent = new Intent(getActivity(), CalendarCreateActivitys.class);
                                scheduleIntent.putExtra("group_id", groupID);
                                scheduleIntent.putExtra("project_id", projectID);
                                scheduleIntent.putExtra("enter_flag", 1);
                                scheduleIntent.putExtra("start_anim", false);
                                startActivity(scheduleIntent);
                            }
                        });
                break;
            case R.id.menu_item6: //新建计划
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent planIntent = new Intent(getActivity(), PlanActivity.class);
                                planIntent.putExtra("group_id", groupID);
                                planIntent.putExtra("project_id", projectID);
                                planIntent.putExtra("start_anim", false);
                                startActivity(planIntent);
                            }
                        });
                break;
            case R.id.menu_item7: //新建任务
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent taskIntent = new Intent(getActivity(), TaskCreateActivity.class);
                                taskIntent.putExtra("group_id", groupID);
                                taskIntent.putExtra("project_id", projectID);
                                taskIntent.putExtra(TaskCreateActivity.WHO_CAN_SEE, true);
                                taskIntent.putExtra("start_anim", false);
                                startActivity(taskIntent);
                            }
                        });
                break;
            case R.id.menu_item8: //新建动态
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent shareIntent = new Intent(getActivity(), ShareActivity2.class);
                                shareIntent.putExtra("group_id", groupID);
                                shareIntent.putExtra("project_id", projectID);
                                shareIntent.putExtra("start_anim", false);
                                startActivity(shareIntent);
                            }
                        });
                break;
        }
    }

    /**
     * 处理时间格式
     *
     * @param num
     * @return
     */
    private String checkNum(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // TODO
//            Toast.makeText(getActivity(), "land", Toast.LENGTH_SHORT).show();
            Message msg = handler.obtainMessage();
            msg.what = 14;
            handler.sendMessage(msg);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // TODO
//            Toast.makeText(getActivity(), "port", Toast.LENGTH_SHORT).show();
            Message msg = handler.obtainMessage();
            msg.what = 15;
            handler.sendMessage(msg);
        }
    }
}
