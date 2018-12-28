package cc.emw.mobile.form;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
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

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.dynamic.DateDetailActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.dynamic.MailDetailActivity;
import cc.emw.mobile.dynamic.PhoneDetailActivity;
import cc.emw.mobile.dynamic.PlanDetailActivity;
import cc.emw.mobile.dynamic.ServiceDetailActivity;
import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.file.FileSelectActivity2;
import cc.emw.mobile.login.ScannerLoginActivity;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.ProgressWebView;

/**
 * 表单Web
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_form_web)
public class FormWebActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener {

    public static final String PAGE_ID = "page_id";
    public static final String ROW_ID = "row_id";
    public static final String HAS_PROGRESSBAR = "has_progressbar";

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; // 顶部条更多按钮
    private ProgressWebView webView;
    @ViewInject(R.id.tv_info)
    private TextView mInfoTv;

    private GeocodeSearch geocoderSearch;
    private static String url = "http://10.0.10.61:8082/test.aspx";//http://10.0.10.61:8081
    private int pageID, rowID;
    private boolean hasProgressbar;
    private Handler handler;
    private SimpleDateFormat format, format2;
    private String jsFuncation, jsParam, type, selectType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setSwipeBackEnable(false);
        pageID = getIntent().getIntExtra(PAGE_ID, 0);
        rowID = getIntent().getIntExtra(ROW_ID, 0);
        hasProgressbar = getIntent().getBooleanExtra(HAS_PROGRESSBAR, true);
        format = new SimpleDateFormat(
                getString(R.string.timeformat6));
        format2 = new SimpleDateFormat(
                getString(R.string.timeformat5));
        url = Const.BASE_URL + "/mobile/index.html#/index?pageid=" + pageID; //10.0.10.52:2080  10.1.1.121:2080
        if (rowID > 0) {
            url = url + "&rid=" + rowID;
        }
        if (getIntent().hasExtra("absolute_url")) { //绝对url
            url = getIntent().getStringExtra("absolute_url");
        } else if (getIntent().hasExtra("url")) { //相对url
            url = Const.BASE_URL + getIntent().getStringExtra("url");
        }
        Log.e("formweb", "url:" + url);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        onBackPressed();
                        break;
                    case 2:
                        Intent intent = new Intent(FormWebActivity.this, FormWebActivity.class);
                        intent.putExtra("url", msg.obj.toString());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent photoIntent = new Intent(FormWebActivity.this, PhotoActivity.class);
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
                                                tpd.show(getFragmentManager(), "Timepickerdialog");
                                            }
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
                            dpdStart.setAccentColor(getResources().getColor(R.color.cm_main_text));
                            dpdStart.show(getFragmentManager(), "Datepickerdialog");
                        } catch (Exception e) {

                        }
                        break;
                    case 5:
                        try {
                            switch (msg.arg1) {
                                case ApiEnum.UserNoteAddTypes.Schedule: //日程
                                    Intent scheduleIntent = new Intent(FormWebActivity.this, CalendarInfoActivity2.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        scheduleIntent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    scheduleIntent.putExtra("start_anim", false);
                                    startActivity(scheduleIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Task: //工作分派
                                    Intent taskIntent = new Intent(FormWebActivity.this, TaskDetailActivity.class);
                                    if (TextUtils.isDigitsOnly(msg.obj.toString())) {
                                        taskIntent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(msg.obj.toString()));
                                    }
                                    taskIntent.putExtra("start_anim", false);
                                    startActivity(taskIntent);
                                    break;
                                case ApiEnum.UserNoteAddTypes.Plan: //工作计划
                                    Intent planIntent = new Intent(FormWebActivity.this, PlanDetailActivity.class);
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
                                        otherIntent.setClass(FormWebActivity.this, DateDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Phone) {
                                        otherIntent.setClass(FormWebActivity.this, PhoneDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.Email) {
                                        otherIntent.setClass(FormWebActivity.this, MailDetailActivity.class);
                                    } else if (msg.arg1 == ApiEnum.UserNoteAddTypes.SeviceActive) {
                                        otherIntent.setClass(FormWebActivity.this, ServiceDetailActivity.class);
                                    }
                                    otherIntent.putExtra("start_anim", false);
                                    try {
                                        startActivity(otherIntent);
                                    } catch (ActivityNotFoundException e) {

                                    }
                                    break;
                                default:
                                    Intent dynamicIntent = new Intent(FormWebActivity.this, DynamicDetailActivity.class);
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
                    case 7:
                        boolean isInstalled = HelpUtil.isPkgInstalled("com.autonavi.minimap", FormWebActivity.this); //1.判断用户手机是否安装高德地图APP
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
                        } else if (HelpUtil.isPkgInstalled("com.baidu.BaiduMap", FormWebActivity.this)){
                            Intent i1 = new Intent();// 驾车导航
                            i1.setData(Uri.parse("baidumap://map/navi?query="+msg.toString()));
                            startActivity(i1);
                        } else {

                        }
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
                            Intent imgIntent = new Intent(FormWebActivity.this, FileSelectActivity2.class);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, "1".equals(selectType)?FileSelectActivity2.RADIO_SELECT:FileSelectActivity2.MULTI_SELECT);
                            imgIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
                            startActivityForResult(imgIntent, "1".equals(selectType)?10100:10101);
                        } else {
                            Intent fileIntent = new Intent(FormWebActivity.this, FileSelectActivity.class);
                            fileIntent.putExtra("start_anim", false);
                            startActivityForResult(fileIntent, 10101);
                        }
                        break;
                    case 13:
                        String[] scanner = msg.obj.toString().split(",");
                        if (scanner.length > 0) {
                            jsFuncation = scanner[0];
                            if (scanner.length > 1) {
                                jsParam = scanner[1];
                            }
                        }
                        Intent scannerIntent = new Intent(FormWebActivity.this, ScannerLoginActivity.class);
                        startActivityForResult(scannerIntent, 10102);
                        break;
                }
            }
        };
            /*AlertDialog dialog = new AlertDialog(this).builder();
            dialog.setCancelable(false).setMsg("打开失败！");
			dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					scrollToFinishActivity();
				}
			}).show();*/
        initData();
//        syncCookie(this, ".emw.cc"); //
        syncCookie(this, Const.BASE_URL);

        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                break;
        }
    }

    private void setMap(String data) {
        geocoderSearch = new GeocodeSearch(getApplicationContext());
        //设置逆地理编码监听
        geocoderSearch.setOnGeocodeSearchListener(this);

        GeocodeQuery query = new GeocodeQuery(data.trim(), "");
        geocoderSearch.getFromLocationNameAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == 1000) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null &&
                    geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                double latitude = geocodeAddress.getLatLonPoint().getLatitude();
                double longitude = geocodeAddress.getLatLonPoint().getLongitude();

            }
        }
    }


    /**
     * 将cookie同步到WebView
     *
     * @param url WebView要加载的url
     * @return true 同步cookie成功，false同步cookie失败
     */
    public static boolean syncCookie(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.removeAllCookie();
        LoginResp loginResp = PrefsUtil.readLoginCookie();
        String cookieStr = "u=" + loginResp.u + "; c=" + loginResp.c + "; s=" + loginResp.s + "; p=" + loginResp.p;
        Log.e("formweb", "cookie:" + cookieStr);
        cookieManager.setCookie(url, "u=" + loginResp.u);
        cookieManager.setCookie(url, "c=" + loginResp.c);
        cookieManager.setCookie(url, "s=" + loginResp.s);
        cookieManager.setCookie(url, "p=" + loginResp.p);
        CookieSyncManager.getInstance().sync();
        String newCookie = cookieManager.getCookie(url);
        Log.e("formweb", "newCookie:" + newCookie);
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

    private void initData() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText("预览");
        mInfoTv.setText(url);
        webView = (ProgressWebView) findViewById(R.id.webview);
        webView.setShowProgressbar(hasProgressbar);
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
        webView.addJavascriptInterface(new AndroidForJs(this), "JsCallNative");
        webView.setOnWebCallBack(new ProgressWebView.OnWebCallBack() {
            @Override
            public void getTitle(String title) {
                mHeaderTitleTv.setText(title);
            }

            @Override
            public void getUrl(String url) {

            }

            @Override
            public void loadFinish() {
//				mInfoTv.setVisibility(View.GONE);
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
        public void openNavi(String addr) {
            Log.e("formweb", "openNavi:addr=" + addr);
            Message msg = handler.obtainMessage();
            msg.what = 7;
            msg.obj = addr != null ? addr : "";
            handler.sendMessage(msg);
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
        public void openScanner(String jsFuncation, String jsParam) {
            Log.e("formweb", "openScanner:jsFuncation=" + jsFuncation + ", id=" + jsParam);
            Message msg = handler.obtainMessage();
            msg.what = 13;
            msg.obj = jsFuncation + "," + jsParam;
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
}