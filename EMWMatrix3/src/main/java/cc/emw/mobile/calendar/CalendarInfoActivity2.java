package cc.emw.mobile.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.RelationFileListAdapter;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.map.AMapUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SwipeBackScrollView;

/**
 * @author zrjt
 */
@SuppressLint("SimpleDateFormat")
@ContentView(R.layout.activity_calendar_info3)
public class CalendarInfoActivity2 extends BaseActivity implements LocationSource, View.OnClickListener, AMap.OnMapClickListener {
    @ViewInject(R.id.scroll_calendar_edit)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.cm_header_bar)
    private LinearLayout lLTitle;
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSendTv; // 顶部条发布
    @ViewInject(R.id.cm_input_et_content)
    private EditText mNameEt; // 名称
    @ViewInject(R.id.cm_input_et_content_scan)
    private EditText mDescEt; // 描述
    @ViewInject(R.id.file_relation)
    private CollapseView mFileLayout;// 知识库
    @ViewInject(R.id.view_file_line)
    private View mFileLine;
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout
    @ViewInject(R.id.tv_calendar_tag)
    private TextView mTvTag;
    @ViewInject(R.id.schedule_ll_repeat)
    private LinearLayout mRepeatLayout; // 重复选择Layout
    @ViewInject(R.id.ll_calendar_tixing)
    private LinearLayout tiXingLayout; // 提醒选择的Layout
    @ViewInject(R.id.schedule_tv_repeat)
    private TextView repeatInfoTv; // 重复的详情
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView txTextView; // 提醒的详情
    @ViewInject(R.id.schedule_tv_hintbefore)
    private TextView mHintBeforeTv; // 提前
    @ViewInject(R.id.iv_repeat_more)
    private ImageView mIvRepeat;
    @ViewInject(R.id.iv_tixing_more)
    private ImageView mIvTiXing;
    @ViewInject(R.id.cm_select_itv_select)
    private IconTextView mIvShare;
    @ViewInject(R.id.cm_header_tv_edit)
    private IconTextView editText;
    @ViewInject(R.id.tv_calendar_start_time)
    private TextView mTvStartTime;  //开始时间文本
    @ViewInject(R.id.tv_calendar_end_time)
    private TextView mTvEndTime;    //结束时间文本
    @ViewInject(R.id.map_calendar_rail)
    private TextureMapView mapView;    //地图围栏

    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch mSearch;
    private GeocodeAddress address;
    final int REQ_GEO_FENCE = 0x13;
    private int mRadius = 100;//记录围栏选择半径 默认100米
    private LatLng latLngCircle;
    private LatLng centerLatLng;
    final String ACTION_GEO_FENCE = "geo fence action";
    String loactionCity;    //定位的城市

    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> mSelectList = new ArrayList<>(); // 分享人员列表数据
    private CalendarInfo schedule = new CalendarInfo(); // 日程的实体类
    private SimpleDateFormat format, format2, format3, format4;
    public static final int TASK = 44;
    public static final int PROJECT = 45;
    public static final int ZHISHIKU = 46;
    private static final String TAG = "CalendarInfoActivity";
    private ArrayList<UserFenPai> tasks = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<UserFenPai> taskRets = new ArrayList<UserFenPai>(); // 相关任务的选择列表
    private ArrayList<Files> fileRets = new ArrayList<Files>();
    private ArrayList<Files> files = new ArrayList<Files>(); // 相关文件的选择列表
    private ArrayList<UserProject> projects = new ArrayList<UserProject>(); // 相关项目的选择列表

    private CalendarInfo cInfo = new CalendarInfo(); // 传给服务器的对象
    public static final int TIXING_REQUEST = 23;
    public static final int TIXING_REQUEST_END = 24;

    private int type = -1; // 重复的类型
    private int fixType = -1; // 固定的类型
    private int pinlv; // 重复的频率
    private int fixPinlv; // 固定重复的频率
    private String repeatInfoStr; // 重复的结果
    private String typeStr; // 类型的字符串
    private String weekStr; // 重复的周中文模式
    private String weekNum; // 重复的周数字模式
    private String fixRepeatInfoStr; // 固定的重复时间
    private String fixTiXingInfoStr; // 固定的提醒时间
    private String tixingEndStr; // 提醒结束时间的字符串
    //private int[] colors = new int[]{0x00B0CB5E, 0x00007EFF, 0x006DC53C, 0x00E0C400, 0x00D8745C, 0x00795AF9};
    private int[] colors;   // 日程颜色集合
    private int calendarId; // 传递过来的日程id
    public static final String CALENDARID = "calendarId";
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private RelationFileListAdapter adapter;
    private List<Files> mDataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        schedule = (CalendarInfo) getIntent().getSerializableExtra(
                "calendarInfo");
        calendarId = getIntent().getIntExtra(CALENDARID, 0);
        /**
         * 初始化地图控件
         */
        mapView.onCreate(savedInstanceState);
        //show my location
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        lLTitle.setVisibility(View.GONE);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.calendar_introduction);
        mHeaderSendTv.setText(R.string.edit);
        mLoadingDialog = createLoadingDialog(getString(R.string.excuseing));
        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1), getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3), getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5)};

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mainScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    mainScrollView.requestDisallowInterceptTouchEvent(true);
                }
            }
        });

        if (schedule != null) {
            showCalendarInfo(schedule);
        }

        if (calendarId != 0) {
            getCalendarById();
        }

        mNameEt.setEnabled(false);
        mDescEt.setEnabled(false);

        // view
        mFileLayout.setContent(R.layout.activity_calendar_tag2);
        LinearLayout tagNew = (LinearLayout) mFileLayout.findViewById(R.id.tv_calendar_new_tag);
        tagNew.setVisibility(View.GONE);
        MyListView myListView = (MyListView) mFileLayout.findViewById(R.id.lv_calendar_tag);
        mFileLayout.setTagNameVis("eb05", "附件");
        mFileLayout.setSwipeScrollView(mainScrollView);
        adapter = new RelationFileListAdapter(this, (ArrayList<Files>) mDataList);
        myListView.setAdapter(adapter);
        if (schedule != null && !TextUtils.isEmpty(schedule.Line_File)
                && schedule.Line_File.length() != 2) {
            String str = schedule.Line_File.substring(1,
                    schedule.Line_File.length() - 1);
            String[] arr = str.split(",");
            mFileLayout.setTitle(arr.length + "个附件");
            getFileByLineStr(StringUtils.replaceBlank(schedule.Line_File));
        } else {
            mFileLayout.setTitle("暂无相关附件");
            mFileLayout.setEnabled(false);
            mFileLayout.setVisibility(View.GONE);
            mFileLine.setVisibility(View.GONE);
        }
    }

    /**
     * 显示围栏地址
     */
    private void showRailInfo(final CalendarInfo calendarInfo) {

        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
//        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mSearch = new GeocodeSearch(CalendarInfoActivity2.this);
        if (calendarInfo.Rail != null && !TextUtils.isEmpty(calendarInfo.Rail.Address))
            mSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                    if (geocodeResult == null) {
                        ToastUtil.showToast(CalendarInfoActivity2.this, "抱歉,该位置不存在!");
                    } else {
                        if (geocodeResult.getGeocodeAddressList().size() > 0)
                            address = geocodeResult.getGeocodeAddressList().get(0);
                        else
                            ToastUtil.showToast(CalendarInfoActivity2.this, "抱歉,该位置无效");
                    }
                    String lola = AMapUtil.convertToLatLng(address.getLatLonPoint()).latitude + ","
                            + AMapUtil.convertToLatLng(address.getLatLonPoint()).longitude;
                    if (calendarInfo.Rail != null) {
                        calendarInfo.Rail.Axts = lola;
                        calendarInfo.Rail.Address = address.getFormatAddress();
                        calendarInfo.Rail.Radius = mRadius;
                    }
//                100 * 16.5f / mRadius
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            AMapUtil.convertToLatLng(address.getLatLonPoint()), 16.5f));
                    aMap.addMarker(new MarkerOptions()
                            .position(AMapUtil.convertToLatLng(address.getLatLonPoint())).title(address.getFormatAddress())
                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(true));

                    Intent intent = new Intent(ACTION_GEO_FENCE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(CalendarInfoActivity2.this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //500:是围栏半径;-1：是超时时间（单位：ms，-1代表永不超时）
                    AMapLocationClient client = new AMapLocationClient(CalendarInfoActivity2.this);
                    client.addGeoFenceAlert("fenceId", AMapUtil.convertToLatLng(address.getLatLonPoint()).latitude,
                            AMapUtil.convertToLatLng(address.getLatLonPoint()).longitude, 100, -1, pendingIntent);
                    latLngCircle = AMapUtil.convertToLatLng(address.getLatLonPoint());
                    addCircle(latLngCircle, mRadius);
                }
            });

        if (calendarInfo.Rail != null) {
            //是否初始化围栏坐标在地图上的展示 TODO ？
            String axts = calendarInfo.Rail.Axts;
            if (axts != null) {
                String[] strs = axts.split(",");
                if (strs.length > 1) {
                    LatLng latLng = new LatLng(Double.valueOf(strs[0]), Double.valueOf(strs[1]));
                    addCircle(latLng, calendarInfo.Rail.Radius);
                }
            }
            GeocodeQuery query = new GeocodeQuery(calendarInfo.Rail.Address, loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            mSearch.getFromLocationNameAsyn(query);
        }
    }

    /**
     * 调用该方法后会在地图上展示围栏效果。
     * 目前调用一次添加一次围栏圈
     *
     * @param latLng
     * @param radius
     */
    public void addCircle(LatLng latLng, int radius) {
        aMap.clear();
//        ToastUtil.showToast(this, "画圈圈");
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(3);
        circleOptions.strokeColor(getResources().getColor(R.color.map_wai));
        circleOptions.fillColor(getResources().getColor(R.color.map_nei));
        aMap.addCircle(circleOptions);
    }


    /**
     * 显示日程的详细信息
     */
    private void showCalendarInfo(CalendarInfo schedule) {

        mainScrollView.setVerticalScrollBarEnabled(false);

        if (schedule.Rail != null && !TextUtils.isEmpty(schedule.Rail.Address)) {
            mapView.setVisibility(View.VISIBLE);
        } else {
            mapView.setVisibility(View.GONE);
        }

        showRailInfo(schedule);

        if (schedule.UserID == PrefsUtil.readUserInfo().ID) {
            editText.setVisibility(View.VISIBLE);
        } else {
            editText.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(schedule.Line_File)
//                && schedule.Line_File.length() != 2) {
//            tvFileNum.setVisibility(View.VISIBLE);
//            String str = schedule.Line_File.substring(1,
//                    schedule.Line_File.length() - 1);
//            String[] arr = str.split(",");
//            tvFileNum.setText(arr.length + "个附件");
//        }
//        if (schedule.FileName == null) {
//            tvFileNumber.setText("暂无相关附件");
//        } else {
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < schedule.FileName.size(); i++) {
//                if (i < 2) {
//                    String user = schedule.FileName.get(i);
//                    if (i != 0) {
//                        builder.append("、");
//                    }
//                    builder.append(user);
//                } else {
//                    builder.append("等" + schedule.FileName.size() + "个");
//                    break;
//                }
//            }
//            tvFileNumber.setText(builder);
//        }

        String startTime = schedule.StartTime;
        String endTime = schedule.OverTime;

        mTvStartTime.setText(startTime);

        mTvEndTime.setText(endTime);

        mNameEt.setHint(R.string.share_range_hint);

        mNameEt.setText(schedule.Title);

        //显示日程颜色
        showEventColor(schedule);

        String start = schedule.StartTime;
        if (start != null && start.startsWith("/Date("))
            start = HelpUtil.time2String(getString(R.string.timeformat6),
                    start);
        String end = schedule.OverTime;
        if (end != null && end.startsWith("/Date("))
            end = HelpUtil.time2String(getString(R.string.timeformat6),
                    end);
        if (schedule.Remark != null && !TextUtils.isEmpty(schedule.Remark)) {
            mDescEt.setText(schedule.Remark);
        } else {
            mDescEt.setText(getResources().getString(R.string.nope));
        }

        // Calendar重复提醒
        showRepeatInfo(schedule);
    }

    /**
     * 重复提醒赋值
     */
    private void showRepeatInfo(CalendarInfo schedule) {
        String repeatTypeStr = ""; // 重复类型字符串
        pinlv = schedule.REPEATHZ; // 频率
        int repeatType = schedule.REPEATTYPE; // 重复的类型
        String repeatWeekStr = schedule.REPEATWEEKVAL; // 重复周的提示
        int tiXing = schedule.AHEAD_MINUTE; // 提醒的时间
        String tiXingEndTimeStr = schedule.REPEATENDTIME; // 重复提醒的截止时间
        switch (repeatType) {
            case 1:
                repeatTypeStr = "天";
                break;
            case 2:
                repeatTypeStr = "周";
                break;
            case 3:
                repeatTypeStr = "月";
                break;
            case 4:
                repeatTypeStr = "年";
                break;
        }
        // 星期的信息
        StringBuilder weekInfoScan = new StringBuilder();
        if (repeatWeekStr != null && repeatWeekStr.length() > 1) {
            String[] weekArr = repeatWeekStr.split(",");
            if (weekArr != null) {
                for (int i = 0; i < weekArr.length; i++) {
                    if (TextUtils.isDigitsOnly(weekArr[i])) {
                        switch (Integer.valueOf(weekArr[i])) {
                            case 0:
                                weekInfoScan.append("一/");
                                break;
                            case 1:
                                weekInfoScan.append("二/");
                                break;
                            case 2:
                                weekInfoScan.append("三/");
                                break;
                            case 3:
                                weekInfoScan.append("四/");
                                break;
                            case 4:
                                weekInfoScan.append("五/");
                                break;
                            case 5:
                                weekInfoScan.append("六/");
                                break;
                            case 6:
                                weekInfoScan.append("日/");
                                break;
                        }
                    }
                }
            }
            if (weekInfoScan != null && weekInfoScan.length() > 2) {
                weekInfoScan = weekInfoScan
                        .deleteCharAt(weekInfoScan.length() - 1);
            }
        } else if (repeatWeekStr != null && repeatWeekStr.length() == 1) {
            switch (Integer.valueOf(repeatWeekStr)) {
                case 0:
                    weekInfoScan.append("一");
                    break;
                case 1:
                    weekInfoScan.append("二");
                    break;
                case 2:
                    weekInfoScan.append("三");
                    break;
                case 3:
                    weekInfoScan.append("四");
                    break;
                case 4:
                    weekInfoScan.append("五");
                    break;
                case 5:
                    weekInfoScan.append("六");
                    break;
                case 6:
                    weekInfoScan.append("日");
                    break;
            }
        }

        String tiXingStr = ""; // 提醒提前的时间

        if (schedule.ISCALL == 1) {
            switch (tiXing) {
                case 0:
                    tiXingStr = getString(R.string.ontime);
                    break;
                case 5:
                    tiXingStr = getString(R.string.five_minute_per);
                    break;
                case 60:
                    tiXingStr = getString(R.string.one_hours);
                    break;
                case 180:
                    tiXingStr = getString(R.string.three_hours);
                    break;
                case 1440:
                    tiXingStr = getString(R.string.one_day);
                    break;
                case 4320:
                    tiXingStr = getString(R.string.three_day);
                    break;
                case 10080:
                    tiXingStr = getString(R.string.one_week);
                    break;
            }
        } else {
            tiXingStr = getString(R.string.nope);
        }

        repeatInfoStr = pinlv + repeatTypeStr + " " + weekInfoScan;

        if (tiXingStr != null && !TextUtils.isEmpty(tiXingStr)) {
            txTextView.setText(tiXingStr);
        } else {
            txTextView.setText(getString(R.string.nope));
        }

        if (pinlv != 0) {
            repeatInfoTv.setText("每" + repeatInfoStr);
        }
    }

//    /**
//     * 相关事件的点击、提醒重复的点击
//     *
//     * @param view
//     */
//    @Event(value = {R.id.work_project_relation, R.id.task_relation,
//            R.id.file_relation})
//    private void relationClick(View view) {
//        if (schedule != null) {
//            switch (view.getId()) {
////                case R.id.work_project_relation:
////                    if (mIsClick) {
////                        Intent intent = new Intent(this,
////                                WorkProjectMutiActivity.class);
////                        intent.putExtra(WorkProjectMutiActivity.WORK_PROJECT_IDS,
////                                schedule.Line_Project);
////                        // ToastUtil.showToast(this, schedule.Line_Project);
////                        startActivityForResult(intent, PROJECT);
////                    } else {
////                        // 展示工作项目
////                        String line_Project = schedule.Line_Project;//
////                        Intent intent = new Intent(this,
////                                RelationProjectActivity.class);
////                        // 将项目id字符串传递到相关项目界面
////                        intent.putExtra(
////                                RelationProjectActivity.CALENDER_WORK_PORJECT,
////                                line_Project);
////                        startActivity(intent);
////                    }
////                    break;
////                case R.id.task_relation:
////                    if (mIsClick) {
////                        Intent taskIntent = new Intent(this,
////                                RelativeTaskActivity.class);
////                        String infos = schedule.Line_Task;
////                        if (infos != null && infos.length() > 0) {
////                            infos = infos.substring(1, infos.length() - 1);
////                        }
////                        taskIntent.putExtra("task_ids", infos);
////                        // ToastUtil.showToast(this, schedule.Line_Task);
////                        taskIntent.putExtra("from",
////                                RelativeTaskActivity.FROM_SCHEDULE);
////                        startActivityForResult(taskIntent, TASK);
////                    } else {
////                        // 展示相关任务
////                        String line_Task = schedule.Line_Task;
////                        Intent intent = new Intent(this, RelationTaskActivity.class);
////                        intent.putExtra(RelationTaskActivity.CALENDER_TASK,
////                                line_Task);
////                        startActivity(intent);
////                    }
////                    break;
////                case R.id.file_relation:
////                    // 展示知识库详情
////                    String line_File = schedule.Line_File;
////                    Intent intent = new Intent(this, RelationFileActivity.class);
////                    intent.putExtra(RelationFileActivity.CALENDER_FILE,
////                            line_File);
////                    startActivity(intent);
////                    break;
//
//            }
//        }
//    }


    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_left9, R.id.cm_header_tv_edit})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_edit:
                if (schedule != null) {
                    Intent intent = new Intent(CalendarInfoActivity2.this, CalendarEditActivity.class);
                    intent.putExtra("calendarInfo", schedule);
                    intent.putExtra("start_anim", false);
                    startActivityForResult(intent, 100);
                }
                break;
        }
    }

    //显示日程的颜色
    private void showEventColor(CalendarInfo schedule) {
        if (!TextUtils.isEmpty(schedule.Label)) {
            if (schedule.Label.contains("#")) {
                mTvTag.setText(schedule.Label);
            } else {
                mTvTag.setText("#" + schedule.Label);
            }
        } else {
            mTvTag.setText("#无");
        }
    }

/*    @Event(R.id.cm_select_ll_select)
    private void onSelectClick(View v) {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE,
                ContactSelectActivity.MULTI_SELECT);
        intent.putExtra("select_list", mSelectList);
        startActivityForResult(intent, 2);
    }*/

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // 分享范围
                case 2:
                    mSelectFlowLayout.removeAllViews();
                    mSelectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mSelectList.size(); i++) {
                        if (i < 3) {
                            ApiEntity.UserInfo user = mSelectList.get(i);
                            if (i != 0) {
                                builder.append("、");
                            }
                            builder.append(user.Name);
                        } else {
                            builder.append("等" + mSelectList.size() + "人");
                            break;
                        }
                    }
                    break;
                case 100:
                    finish();
                    break;
            }
        }
        if (requestCode == TIXING_REQUEST && resultCode == RESULT_OK) {
            fixTiXingInfoStr = data.getStringExtra("tixing");
            if (!fixTiXingInfoStr.equals("null")) {
                txTextView.setText(fixTiXingInfoStr);
            }
        }
    }

    /**
     * 显示选择的分享人员
     */
    private void addPersonItem(UserInfo user) {
//        PersonTextView childView = new PersonTextView(this, R.drawable.persontext_bg, false);
//        childView.setTag(user);
//        childView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (flagShareMember) {
//                    mSelectFlowLayout.removeView(v);
//                    mSelectList.remove((UserInfo) v.getTag());
//                    if (mSelectList.size() == 0) {
//                        mSelectTv.setHint(R.string.share_range_hint);
//                    }
//                }
//            }
//        });
//        childView.setText(user.Name);
//        mSelectFlowLayout.addView(childView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        type = intent.getIntExtra("type", -1);
        pinlv = intent.getIntExtra("pinlv", 0);
        weekStr = intent.getStringExtra("week"); // 重复周的详细描述
        weekNum = intent.getStringExtra("weekNum"); // 重复周的数字描述
        fixRepeatInfoStr = intent.getStringExtra("fixRepeat"); // 固定重复的详细描述
        fixPinlv = intent.getIntExtra("fixpv", 0);
        fixType = intent.getIntExtra("fixType", -1);

        switch (type) {
            case 0:
                typeStr = "天";
                break;
            case 1:
                typeStr = "周";
                break;
            case 2:
                typeStr = "月";
                break;
            case 3:
                typeStr = "年";
                break;
        }
        switch (fixType) {
            case 0:
                typeStr = "天";
                break;
            case 1:
                typeStr = "周";
                break;
            case 2:
                typeStr = "月";
                break;
            case 3:
                typeStr = "年";
                break;
        }

        repeatInfoStr = pinlv + typeStr + " " + weekStr;

        // 重复提醒的判断
        if (type != -1) {
            repeatInfoTv.setText("每" + repeatInfoStr);
        }
        if (fixRepeatInfoStr != null) {
            repeatInfoTv.setText(fixRepeatInfoStr);
        }
    }


    /**
     * 过滤掉已经存在容器集合中的文件数据 如若原集合中有时不添加
     *
     * @param content 原集合内容
     * @param rets    新增的返回内容
     * @return 返回过滤后的集合
     */
    public static ArrayList<UserFenPai> filterFiles(
            ArrayList<UserFenPai> content, ArrayList<UserFenPai> rets) {
        for (int i = 0; i < rets.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < content.size(); j++) {
                if (rets.get(i).ID == content.get(j).ID) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                content.add(rets.get(i));
            }
        }
        return content;
    }

    // 将字符串转为时间戳
    private long getTime(String user_time) {
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

    // 把项目集合id转成id字符串
    private String project2string(ArrayList<UserProject> projects) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < projects.size(); i++) {
            sb.append(projects.get(i).ID);
            if (i != projects.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString().trim();
    }

    // 把任务集合id转成id字符串
    private String tasks2string(ArrayList<UserFenPai> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(tasks.get(i).ID);
            if (i != tasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString().trim();
    }

    // 把知识库集合id转成id字符串
    private String files2string(ArrayList<Files> files) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < files.size(); i++) {
            sb.append(files.get(i).ID);
            if (i != files.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        Log.d(TAG, "av" + sb.toString());
        return sb.toString().trim();
    }

    // 将以字符串按","分割成数组
    private String[] string2array(String str) {
        String[] strings;
        str = str.substring(1, str.length() - 1);
        strings = str.split(",");
        return strings;
    }

    /**
     * 根据ID获取日程详情
     */
    private void getCalendarById() {
        API.TalkerAPI.GetCalenderById(calendarId,
                new RequestCallback<CalendarInfo>(CalendarInfo.class) {

                    @Override
                    public void onStarted() {
//                        mLoadingDialog.show();
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
//                        mLoadingDialog.dismiss();
                        if (!isFinishing()) {
                            AlertDialog dialog = new AlertDialog(CalendarInfoActivity2.this).builder();
                            dialog.setCancelable(false).setMsg("日程查看失败,请稍候再试!");
                            dialog.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onFinished() {
//                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onParseSuccess(List<CalendarInfo> respList) {
                        if (respList.size() > 0) {
                            schedule = respList.get(0);
                            showCalendarInfo(schedule);
                            if (schedule != null && !TextUtils.isEmpty(schedule.Line_File)
                                    && schedule.Line_File.length() != 2) {
                                String str = schedule.Line_File.substring(1,
                                        schedule.Line_File.length() - 1);
                                String[] arr = str.split(",");
                                mFileLayout.setTitle(arr.length + "个附件");
                                getFileByLineStr(StringUtils.replaceBlank(schedule.Line_File));
                            } else {
                                mFileLayout.setTitle("暂无相关附件");
                                mFileLayout.setEnabled(false);
                            }
                        } else {
                            AlertDialog dialog = new AlertDialog(CalendarInfoActivity2.this).builder();
                            dialog.setCancelable(false).setMsg("该日程可能已被删除!");
                            dialog.setPositiveButton(getString(R.string.ok), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                        }
                    }
                });
    }

    //将view转换为bitmap
    public Bitmap convertViewToBitmap(int id) {
        ImageView view = new ImageView(this);
        if (id == 1) {
            view.setBackgroundResource(R.drawable.map_location);
        } else {
            view.setBackgroundResource(R.drawable.map_people);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    //关闭定位
    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 获取相关附件实体
     *
     * @param ids
     */
    private void getFileByLineStr(String ids) {
        API.UserData.GetFileListByIds(ids, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mFileLayout.setEnabled(false);
                ToastUtil.showToast(CalendarInfoActivity2.this, "获取附件失败,请检查网络");
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<Files> files) {
                if (files != null && files.size() > 0) {
                    mFileLayout.setVisibility(View.VISIBLE);
                    mFileLine.setVisibility(View.VISIBLE);
                    mFileLayout.setEnabled(true);
                    mDataList.clear();
                    mDataList.addAll(files);
                    adapter.notifyDataSetChanged();
                } else {
                    mFileLayout.setEnabled(false);
                }
            }
        });
    }
}
