package cc.emw.mobile.chat;

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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.dynamic.DateDetailActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.dynamic.MailDetailActivity;
import cc.emw.mobile.dynamic.PhoneDetailActivity;
import cc.emw.mobile.dynamic.PlanDetailActivity;
import cc.emw.mobile.dynamic.ServiceDetailActivity;
import cc.emw.mobile.entity.CalendarInfo;
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
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FloatingActionMenu;
import cc.emw.mobile.view.ProgressWebView;

import static cc.emw.mobile.R.id.cm_header_tv_right9;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.chat
 * @data on 2018/8/20  15:08
 * @describe TODO
 */
@ContentView(R.layout.addtask_web)
public class AddTasksWebActivity extends BaseActivity {

    @ViewInject(R.id.webview)
    private ProgressWebView webView;

    public static final String ACTION_REFRESH_TIMEDYNAMIC = "cc.emw.mobile.refresh_timedynamic"; //
    public static final String ACTION_TIME_DYNAMICDISCUSS = "cc.emw.mobile.time_dynamicdiscuss"; //
    public static final String ACTION_TIME_DELETEDISCUSS = "cc.emw.mobile.time_deletediscuss"; //

    private static String url = Const.BASE_URL + "/Web_App/new_task/www/task_action.html";
    private Handler handler;
    private SimpleDateFormat format, format2;
    private MyBroadcastReceive mReceive;
    private String jsFuncation, jsParam, type, selectType;
    public static String landPortChange = "landPortChange";
    public static String jsPagePosStr = "www/schedule.html";
    public static String jsPageUserCalPosStr = "www/members.html";
    private String jsPagePos = "unKnow";

    public static FloatingActionMenu mActionMenu;

    public String content;
    public int contentType;
    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    private ArrayList<ApiEntity.Files> fileRets = new ArrayList<>();
    public String fileBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("formweb", "url:" + url);
        content = getIntent().getStringExtra("content");
        contentType = getIntent().getIntExtra("type", 1);
        format = new SimpleDateFormat(getString(R.string.timeformat6), Locale.getDefault());
        format2 = new SimpleDateFormat(getString(R.string.timeformat5), Locale.getDefault());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        onBackPressed();
                        break;
                    case 2:
                        Intent intent = new Intent(AddTasksWebActivity.this, FormWebActivity.class);
                        intent.putExtra("url", msg.obj.toString());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent photoIntent = new Intent(AddTasksWebActivity.this, PhotoActivity.class);
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
                                                tpd.show(AddTasksWebActivity.this.getFragmentManager(), "Timepickerdialog");
                                            }
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
                            dpdStart.setAccentColor(getResources().getColor(R.color.cm_main_text));
                            dpdStart.show(AddTasksWebActivity.this.getFragmentManager(), "Datepickerdialog");
                        } catch (Exception e) {

                        }
                        break;
                    case 5:
                        try {
                            switch (msg.arg1) {
                                case ApiEnum.UserNoteAddTypes.Schedule: //日程
                                    Intent scheduleIntent = new Intent(AddTasksWebActivity.this, CalendarInfoActivity2.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        scheduleIntent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    scheduleIntent.putExtra("start_anim", false);
                                    startActivity(scheduleIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Task: //工作分派
                                    Intent taskIntent = new Intent(AddTasksWebActivity.this, TaskDetailActivity.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        taskIntent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    taskIntent.putExtra("start_anim", false);
                                    startActivity(taskIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Plan: //工作计划
                                    Intent planIntent = new Intent(AddTasksWebActivity.this, PlanDetailActivity.class);
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
                                        otherIntent.setClass(AddTasksWebActivity.this, DateDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Phone) {
                                        otherIntent.setClass(AddTasksWebActivity.this, PhoneDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Email) {
                                        otherIntent.setClass(AddTasksWebActivity.this, MailDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.SeviceActive) {
                                        otherIntent.setClass(AddTasksWebActivity.this, ServiceDetailActivity.class);
                                    }
                                    otherIntent.putExtra("start_anim", false);
                                    try {
                                        startActivity(otherIntent);
                                    } catch (ActivityNotFoundException e) {

                                    }
                                    break;
                                default:
                                    Intent dynamicIntent = new Intent(AddTasksWebActivity.this, DynamicDetailActivity.class);
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
                            AddTasksWebActivity.this.sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                        } else if (msg.arg1 == 2) {
                            AddTasksWebActivity.this.sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                        }
                        break;
                    case 7:
                        boolean isInstalled = HelpUtil.isPkgInstalled("com.autonavi.minimap", AddTasksWebActivity.this); //1.判断用户手机是否安装高德地图APP
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
                        } else if (HelpUtil.isPkgInstalled("com.baidu.BaiduMap", AddTasksWebActivity.this)) {
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
                        AddTasksWebActivity.this.sendBroadcast(keyboardIntent);
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
                        Intent discussIntent = new Intent(AddTasksWebActivity.this, DynamicDiscussActivity.class);
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
                            Intent imgIntent = new Intent(AddTasksWebActivity.this, FileSelectActivity2.class);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, "1".equals(selectType) ? FileSelectActivity2.RADIO_SELECT : FileSelectActivity2.MULTI_SELECT);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
                            startActivityForResult(imgIntent, "1".equals(selectType) ? 10100 : 10101);
                        } else {
                            Intent fileIntent = new Intent(AddTasksWebActivity.this, FileSelectActivity.class);
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
                        Intent locationIntent = new Intent(AddTasksWebActivity.this, GeoFenceActivity.class);
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
                        Intent scannerIntent = new Intent(AddTasksWebActivity.this, ScannerLoginActivity.class);
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

        mActionMenu = (FloatingActionMenu) findViewById(R.id.menu);
        mActionMenu.setClosedOnTouchOutside(true);
        initView();
        syncCookie(this, Const.BASE_URL);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(ACTION_REFRESH_TIMEDYNAMIC);
        intentFilter.addAction(ACTION_TIME_DYNAMICDISCUSS);
        intentFilter.addAction(ACTION_TIME_DELETEDISCUSS);
        mReceive = new MyBroadcastReceive();
        this.registerReceiver(mReceive, intentFilter); // 注册监听
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
            AddTasksWebActivity.this.unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.loadUrl("javascript:SetNoteNumber('" + MainActivity.count + "')");
        }
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
        webView.addJavascriptInterface(new AndroidForJs(AddTasksWebActivity.this), "JsCallNative");
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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView arg0, String url, String message, JsResult result) {
                /**
                 * 这里写你自己的处理方式
                 */
                ToastUtil.showToast(AddTasksWebActivity.this, message);
                result.cancel();
                return true;
                // return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:SetNoteNumber('" + MainActivity.count + "')");
                webView.loadUrl("javascript:init1('" + "" + "','" + content + "')");//参数1为任务标题，此处为空，参数2为任务描述
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

    public String getFileUrl(String url) {
        return null;
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
            webView.loadUrl("javascript:" + jsFuncation + "('" + json + "' ,'" + jsParam + "')");
            //webView.loadUrl("javascript:showFilesView('" + json + "')");
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
                String json = new Gson().toJson(userRail);
                webView.loadUrl("javascript:" + jsFuncation + "('" + json + "' ,'" + id + "','" + jsParam + "')");
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 144) {
            fileRets = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
            cInfo.Line_File = HelpUtil.files2StrID(fileRets);
            if (fileRets == null || fileRets.size() == 0) {

            } else {

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < fileRets.size(); i++) {
                    if (i < 2) {
                        ApiEntity.Files user = fileRets.get(i);
                        if (i != 0) {
                            builder.append("、");
                        }
                        builder.append(user.Name);
                    } else {
                        builder.append("等" + fileRets.size() + "个");
                        break;
                    }
                }
                fileBuilder = builder.toString();
                Toast.makeText(this, "选择的文件名为：" + fileBuilder, Toast.LENGTH_SHORT).show();
            }
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
            Message msg = handler.obtainMessage();
            msg.what = 14;
            handler.sendMessage(msg);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Message msg = handler.obtainMessage();
            msg.what = 15;
            handler.sendMessage(msg);
        }
    }

    @Event(value = {cm_header_tv_right9, R.id.cm_header_btn_left9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case cm_header_tv_right9:
                /*Toast.makeText(this, "测试选择附件", Toast.LENGTH_SHORT).show();
                Intent repositoryIntent = new Intent(this, FileSelectActivity.class);
                repositoryIntent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS, cInfo.Line_File);
                repositoryIntent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                repositoryIntent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(repositoryIntent, 144);*/
                //webView.loadUrl("javascript:" + landPortChange + "('" + 1 + "','" + jsPagePos + "')");
                webView.loadUrl("javascript:" + "SaveTask()");
                break;
        }
    }
}
