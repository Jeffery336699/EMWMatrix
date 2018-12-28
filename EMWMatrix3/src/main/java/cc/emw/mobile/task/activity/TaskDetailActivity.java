package cc.emw.mobile.task.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
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
import com.mingle.headsUp.HeadsUpManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.RelationFileActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.contact.adapter.RelationFileListAdapter;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.map.AMapUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.task.adapter.TaskDetailChildAdapter;
import cc.emw.mobile.task.adapter.TaskDetailLogAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.util.StringUtil;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyListView;

/**
 * 任务详情
 * Created by chengyong.liu on 2016/6/22.
 */
@ContentView(R.layout.activity_task_detail_3)
public class TaskDetailActivity extends BaseActivity implements ITaskModifyView {
    @ViewInject(R.id.sv_task_detail)
    private ScrollView mScrollView;
    @ViewInject(R.id.ll_task_detail_container)
    private LinearLayout mLlTotalContainer;
    @ViewInject(R.id.tv_task_detail_title)
    private TextView mTvTaskTitle;
    @ViewInject(R.id.tv_task_detail_desc)
    private TextView mTvTaskDesc;
    @ViewInject(R.id.tv_task_detail_time)
    private TextView mTvTaskTime;
    @ViewInject(R.id.tv_task_detail_process)
    private TextView mTvTaskProcess;
    @ViewInject(R.id.tv_task_detail_emergency)
    private TextView mTvTaskEmergency;
    @ViewInject(R.id.ll_task_detail_mainuser_total_container)
    private LinearLayout mMainUserTotalContainer;
    @ViewInject(R.id.ll_task_detail_mainuserr_container)
    private LinearLayout mMainUserContainer;//负责人头像容器
    @ViewInject(R.id.tv_task_detail_head_number_charge)
    private TextView mTvChargeNum;//负责人数量
    @ViewInject(R.id.tv_task_detail_relation_to_project)
    private TextView mTvTaskRelationToProject;
    @ViewInject(R.id.tv_task_detail_table)
    private TextView mTvTaskTab;
    @ViewInject(R.id.exlistview_task_detail_child_tasks)
    private ExListView mExListViewChildTasks;
    @ViewInject(R.id.ll_task_detail_moreuser_container)
    private LinearLayout mMoreUserContainer;//执行人的头像容器
    @ViewInject(R.id.ll_task_detail_moreuser_total_container)
    private LinearLayout mMoreUserTotalContainer;
    @ViewInject(R.id.tv_task_detail_attachment_number)
    private TextView mTvTaskAttachmentNumber;
    @ViewInject(R.id.tv_task_detail_comment_number)
    private TextView mTvTaskCommentNum;
    @ViewInject(R.id.view_task_detail_excute_devider)
    private View mExcuteDevider;
    @ViewInject(R.id.cm_header_btn_more)
    private ImageButton mHeaderMoreBtn;//顶部更多按钮//编辑
    @ViewInject(R.id.cm_header_btn_more_two)
    private ImageButton mHeaderMoreTwoBtn;//完成按钮
    @ViewInject(R.id.cm_header_btn_more_create)
    private ImageButton mHeaderCreateBtn;//新建任务按钮
    @ViewInject(R.id.civ_task_detail_emergency)
    private IconTextView mTvCivEmergency;
    @ViewInject(R.id.tv_task_detail_head_number)
    private TextView mTvHeadNumber;//执行人成员头像数量
    @ViewInject(R.id.tv_task_detail_child_task_container)
    private LinearLayout mLlchildTaskContainer;//子孩子容器
    @ViewInject(R.id.view_task_detail_subtask_devider)
    private View mSubTaskDevider;
    @ViewInject(R.id.exlistview_task_detail_log)
    private ExListView mExListViewLog;//展示日志的listview
    @ViewInject(R.id.itv_task_delete)
    private IconTextView mItvTaskDelete;
    @ViewInject(R.id.iv_task_title)
    private ImageView mIvTaskTitle;
    @ViewInject(R.id.map_calendar_rail)
    private TextureMapView mapView;    //地图围栏
    @ViewInject(R.id.file_relation)
    private CollapseView mFileLayout;// 知识库
    private RelationFileListAdapter adapter;
    private List<ApiEntity.Files> mDataList = new ArrayList<>();

    final String ACTION_GEO_FENCE = "geo fence action";
    String loactionCity;    //定位的城市
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch mSearch;
    private GeocodeAddress address;
    final int REQ_GEO_FENCE = 0x13;
    private int mRadius = 100;//记录围栏选择半径 默认100米
    private LatLng latLngCircle;
    private LatLng centerLatLng;

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    public static final String[] colorStr = {
            TaskConstant.TaskEmergencyState.NORMAL,
            TaskConstant.TaskEmergencyState.EMERGENCY,
            TaskConstant.TaskEmergencyState.VERY_EMERGENCY};

    public static final String TASK_ID = "taskid";// 用于接收跳转到任务详情的任务ID
    public static final String TASK_DETAIL = "userFenpai";// 用于接收跳转到任务详情的实体
    public static final String ACTION_DETAIL_TASK = "cc.emw.mobile.detail_task";
    public static final String IS_SHOW_CHILD_TASKS = "is_show_child_tasks";
    public static final String PARENT_STATE = "parent_state";

    public static String mRelationToProjectName;//用来记录第一层任务的关联项目名称

    private boolean mIsShowChildTasks = true;//用来显示是否展示子任务以及创建子任务按钮
    private boolean limit = false;//负责人权限 当前用户属于负责人，具有权限 为true
    private boolean subTasks = false;//是否创建子任务，默认不创建
    private boolean isStateModify = false;//判断当前页面是否进行了状态数据更改，若更改则在关闭页面的时候默认刷新上一个页面
    private boolean mCanSee;

    private int mTaskID;
    private int mParentState;
    private int msgID; //传值，通知栏推送点击进入

    private ArrayList<ApiEntity.TaskReply> mLogList;
    private ApiEntity.UserFenPai mBeforeModifyTask;//修改之前的任务
    private ApiEntity.UserFenPai mUserFenPai;//当前任务
    private ApiEntity.UserInfo mCurrentUser;//当前用户
    private ApiEntity.UserProject project;

    private TaskDetailBroadcastReceiver mReceive;
    private TaskPresenter mTaskPresenter;
    private Dialog mLoadingDialog; // 加载框
    private TaskDetailChildAdapter mTaskDetailChildAdapter;
    private TaskDetailLogAdapter mLogAdapter;//日志适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);

    }

    @Override
    protected void startAnimEnd(Bundle savedInstanceState) {
        /**
         * 初始化地图控件
         */
        mapView.onCreate(savedInstanceState);
        //show my location
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
            }
        });

        initData();
        if (mTaskID != -1) {
            getTaskById(mTaskID);
        } else {
            initView();
        }
        initFileList();
        initReceiver();
    }

    private void initFileList() {
        mFileLayout.setContent(R.layout.activity_calendar_tag2);
        LinearLayout tagNew = (LinearLayout) mFileLayout.findViewById(R.id.tv_calendar_new_tag);
        tagNew.setVisibility(View.GONE);
        MyListView myListView = (MyListView) mFileLayout.findViewById(R.id.lv_calendar_tag);
        mFileLayout.setTagNameVis("eb05", "附件");
        adapter = new RelationFileListAdapter(this, (ArrayList<ApiEntity.Files>) mDataList);
        myListView.setAdapter(adapter);
        if (mUserFenPai != null && !TextUtils.isEmpty(mUserFenPai.Files)) {
            mFileLayout.setEnabled(true);
            String[] arr = mUserFenPai.Files.split(",");
            mFileLayout.setTitle(arr.length + "个附件");
            getFileByLineStr(StringUtils.replaceBlank(mUserFenPai.Files));
        } else {
            mFileLayout.setTitle("暂无相关附件");
            mFileLayout.setEnabled(false);
        }
    }

    private void initData() {
        mLogList = new ArrayList<>();
        mBeforeModifyTask = new ApiEntity.UserFenPai();

        mCurrentUser = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
        mTaskPresenter = new TaskPresenter(this);
        //子任务跳进来的话 必须指定是第几层的第几个子任务
        mUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(TASK_DETAIL);
        mIsShowChildTasks = getIntent().getBooleanExtra(IS_SHOW_CHILD_TASKS, true);
        mParentState = getIntent().getIntExtra(PARENT_STATE, -1);
        mCanSee = getIntent().getBooleanExtra(TaskCreateActivity.WHO_CAN_SEE, true);
        project = (ApiEntity.UserProject) getIntent().getSerializableExtra(TaskCreateActivity.TEAM_USERPROJECT);
        mTaskID = getIntent().getIntExtra(TASK_ID, -1);

        msgID = getIntent().getIntExtra("msg_id", 0);
        if (msgID > 0) {
            Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
            intent.putExtra(MainActivity.MESSAGE_ID, msgID);
            intent.putExtra(MainActivity.MESSAGE_TYPE, 1);
            sendBroadcast(intent);//发广播清除未读数
            HeadsUpManager.getInstant(this).cancel(msgID);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(msgID);
                }
            }, 3000);
        }
    }

    private void initView() {
        //设置负责人
        String mainUser = mUserFenPai.MainUser;
        List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
        if (mainUsers.contains(mCurrentUser) || mUserFenPai.Creator == PrefsUtil.readUserInfo().ID) {
            limit = true;
        } else {
            limit = false;
        }
        mLlTotalContainer.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setVisibility(limit ? View.VISIBLE : View.GONE);
        mHeaderMoreTwoBtn.setVisibility(limit ? View.VISIBLE : View.GONE);
        mItvTaskDelete.setVisibility(limit ? View.VISIBLE : View.GONE);
        mHeaderMoreTwoBtn.setBackgroundResource(mUserFenPai.State == TaskConstant.TaskState.FINISHED ? R.drawable.task_wancheng_jihuo : R.drawable.task_wancheng);
        mHeaderCreateBtn.setVisibility(mIsShowChildTasks && limit ? View.VISIBLE : View.GONE);
        mHeaderCreateBtn.setBackgroundResource(subTasks == false ? R.drawable.task_zirenwu : R.drawable.task_zirenwu_dianjiwan);
        mLlchildTaskContainer.setVisibility(subTasks == false ? View.GONE : View.VISIBLE);
        mSubTaskDevider.setVisibility(subTasks == false ? View.GONE : View.VISIBLE);
        if (mUserFenPai.State == TaskConstant.TaskState.FINISHED) {//当前状态：任务完成   更改     任务进行中
            mHeaderMoreTwoBtn.setVisibility(View.GONE);
            mHeaderMoreBtn.setVisibility(View.GONE);
            mHeaderCreateBtn.setVisibility(View.GONE);
            mItvTaskDelete.setVisibility(View.GONE);
            mIvTaskTitle.setVisibility(View.VISIBLE);
        }
        if (mUserFenPai.PID > 0) {
            mHeaderCreateBtn.setVisibility(View.GONE);
        }
        mTvTaskTitle.setText(mUserFenPai.Title);
        mTvTaskDesc.setText(StringUtil.delHTMLTag(mUserFenPai.Mark));
        //设置关联任务
        if (mIsShowChildTasks) {
            //如果是第一层就给关联项目赋值
            if (mUserFenPai.ProjectElem != null && !TextUtils.isEmpty(mUserFenPai.ProjectElem.Name)) {
                mTvTaskRelationToProject.setText(mUserFenPai.ProjectElem.Name);
                mRelationToProjectName = mUserFenPai.ProjectElem.Name;
            } else {
                mTvTaskRelationToProject.setText("其他");
                mRelationToProjectName = "其他";
            }
        } else {
            mTvTaskRelationToProject.setText(TaskDetailActivity.mRelationToProjectName);
        }
        String oldFormat = getResources().getString(R.string.timeformat6);
        String startTime = TaskUtils.parseToStringTime(oldFormat, mUserFenPai.StartTime);
        String finishTime = TaskUtils.parseToStringTime(oldFormat, mUserFenPai.FinishTime);
        mTvTaskTime.setText(startTime + "至" + finishTime);
        ((TextView) findViewById(R.id.tv_taskdetail_starttime)).setText(startTime);
        ((TextView) findViewById(R.id.tv_taskdetail_endtime)).setText(finishTime);

        if (mUserFenPai.TaskLabel == null || TextUtils.isEmpty(mUserFenPai.TaskLabel)) {
            mTvTaskProcess.setText("无");
        } else {
            mTvTaskProcess.setText(mUserFenPai.TaskLabel);
        }
        //设置任务紧急程度
        int yxj = mUserFenPai.Yxj;
        switch (yxj) {
            case TaskConstant.TaskEmergency.EMERGENCY:
                mTvTaskEmergency.setText(TaskConstant.TaskEmergencyState.EMERGENCY);
//                mTvCivEmergency.setTextColor(getResources().getColor(R.color.task_emergency));
                break;
            case TaskConstant.TaskEmergency.VERY_EMERGENCY:
                mTvTaskEmergency.setText(TaskConstant.TaskEmergencyState.VERY_EMERGENCY);
//                mTvCivEmergency.setTextColor(getResources().getColor(R.color.task_very_emergency));
                break;
            case TaskConstant.TaskEmergency.NORMAL:
            default:
                mTvTaskEmergency.setText(TaskConstant.TaskEmergencyState.NORMAL);
//                mTvCivEmergency.setTextColor(getResources().getColor(R.color.task_normal));
                break;
        }
        showMainUsers();
        //设置附件数量
        String[] fileString = TaskUtils.getStringID(mUserFenPai.Files);
        if (fileString.length != 0) {
            mTvTaskAttachmentNumber.setVisibility(View.VISIBLE);
            mTvTaskAttachmentNumber.setText(fileString.length + "");
        } else {
            mTvTaskAttachmentNumber.setVisibility(View.GONE);
        }
        //执行人
        showMoreUsers();
        if (mUserFenPai.Rail != null && !TextUtils.isEmpty(mUserFenPai.Rail.Address))
            mapView.setVisibility(View.VISIBLE);
        else
            mapView.setVisibility(View.GONE);
        showRailInfo(mUserFenPai);
        //子任务
        mExListViewChildTasks.setVisibility(mIsShowChildTasks ? View.VISIBLE : View.GONE);
        mTaskDetailChildAdapter = new TaskDetailChildAdapter(this, project, mCanSee);
        mExListViewChildTasks.setAdapter(mTaskDetailChildAdapter);
        List<ApiEntity.UserFenPai> tasks = mUserFenPai.Tasks;
        if (tasks != null && tasks.size() != 0) {
            mTaskDetailChildAdapter.setData(tasks);
            mTaskDetailChildAdapter.setProjectId(mUserFenPai.ProjectId);
            //将子任务的父类任务的状态传递下去，用于确定是否能修改状态
            mTaskDetailChildAdapter.setParentState(mUserFenPai.State);
        }
        //任务标签
        if (mUserFenPai.TaskLabel == null || TextUtils.isEmpty(mUserFenPai.TaskLabel)) {
            mTvTaskTab.setText("无");
        } else {
            mTvTaskTab.setText(mUserFenPai.TaskLabel);
        }

        //获取任务操作日志
        mLogAdapter = new TaskDetailLogAdapter(this, mLogList);
        mExListViewLog.setAdapter(mLogAdapter);
        getTaskDetailLog(mUserFenPai.ID);
    }

    /**
     * 显示围栏地址
     */
    private void showRailInfo(final ApiEntity.UserFenPai calendarInfo) {

        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
//        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mSearch = new GeocodeSearch(TaskDetailActivity.this);
        if (calendarInfo.Rail != null && !TextUtils.isEmpty(calendarInfo.Rail.Address))
            mSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                    if (geocodeResult == null) {
                        ToastUtil.showToast(TaskDetailActivity.this, "抱歉,该位置不存在!");
                    } else {
                        if (geocodeResult.getGeocodeAddressList().size() > 0)
                            address = geocodeResult.getGeocodeAddressList().get(0);
                        else
                            ToastUtil.showToast(TaskDetailActivity.this, "抱歉,该位置无效");
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
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskDetailActivity.this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //500:是围栏半径;-1：是超时时间（单位：ms，-1代表永不超时）
                    AMapLocationClient client = new AMapLocationClient(TaskDetailActivity.this);
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

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TaskModifyActivity.ACTION_MODIFY_TASK);
        intentFilter.addAction(TaskMemberActivity.ACTION_TASK_MEMBER);
        intentFilter.addAction(AttachmentActivity.ATTACHMENT_BROADCAST);
        intentFilter.addAction(TaskDetailActivity.ACTION_DETAIL_TASK);
        intentFilter.addAction(TaskCreateActivity.ACTION_CREATE_CHILD_TASK);
        mReceive = new TaskDetailBroadcastReceiver();
        registerReceiver(mReceive, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (mReceive != null)
            unregisterReceiver(mReceive);
        super.onDestroy();
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({
            R.id.rl_task_detail_attachment_container,
            R.id.tv_task_detail_child_task_container,
            R.id.tv_task_detail_comment_container,
            R.id.cm_header_btn_left9,
            R.id.cm_header_btn_more,
            R.id.cm_header_btn_right,
            R.id.cm_header_btn_more_two,
            R.id.cm_header_btn_more_create,
            R.id.itv_task_delete})
    private void onclick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                if (isStateModify) {
                    Intent loadIntent = new Intent();
                    loadIntent.setAction(ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH);
                    sendBroadcast(loadIntent);
                    isStateModify = false;
                }
                onBackPressed();
                break;
            case R.id.rl_task_detail_attachment_container:
                //String line_File = mUserFenPai.Files;
                StringBuilder strFile = new StringBuilder();
                strFile.append("[").append(mUserFenPai.Files).append("]");
                Intent fileIntent = new Intent(this, RelationFileActivity.class);
                fileIntent.putExtra(RelationFileActivity.CALENDER_FILE, strFile.toString());
                startActivity(fileIntent);
                /**
                 * 更改文件查看业务逻辑    放弃引用AttachmentActivity类
                 */
//                Intent attachment = new Intent(this, AttachmentActivity.class);
//                attachment.putExtra(TASK_DETAIL, mUserFenPai);
//                startActivity(attachment);
//                overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
                break;
            case R.id.tv_task_detail_child_task_container:
                //创建子任务
                Intent childTask = new Intent(this, TaskCreateActivity.class);
                childTask.putExtra(TaskCreateActivity.PARENT_USERFENPAI, mUserFenPai);
                childTask.putExtra(TaskCreateActivity.WHO_CAN_SEE, mCanSee);
                childTask.putExtra(TaskCreateActivity.TEAM_USERPROJECT, project);
                childTask.putExtra("start_anim", false);
                startActivity(childTask);
                finish();
                break;
            case R.id.tv_task_detail_comment_container:
                Intent intent = new Intent(TaskDetailActivity.this, CommentActivity.class);
                intent.putExtra(TASK_DETAIL, mUserFenPai);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivity(intent);
                break;
            case R.id.cm_header_btn_more:
                if (!limit) {
                    ToastUtil.showToast(this, "你没有修改任务的权限!");
                    return;
                }
                Intent taskModify = new Intent(TaskDetailActivity.this, TaskModifyActivity.class);
                taskModify.putExtra(TASK_DETAIL, mUserFenPai);
                if (!mIsShowChildTasks) {
                    //表示是第2层或者以上的任务详情界面
                    //需要告诉编辑界面这是子任务的编辑界面
                    taskModify.putExtra(TaskModifyActivity.IS_CHILD_MODIFY, true);
                }
                taskModify.putExtra(TaskCreateActivity.WHO_CAN_SEE, mCanSee);
                taskModify.putExtra(TaskCreateActivity.TEAM_USERPROJECT, project);//传送关联项目的负责人ID
                taskModify.putExtra("start_anim", false);
                startActivity(taskModify);
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
            case R.id.cm_header_btn_more_two:
                if (!limit) {
                    ToastUtil.showToast(this, "你没有修改任务状态的权限!");
                    return;
                }
                if (mParentState != -1 && mParentState == TaskConstant.TaskState.UNSTART) {
                    //表示是子任务,并且父类任务的状态为未开始
                    ToastUtil.showToast(this, "父类任务未开始,你不能更改任务状态!");
                    return;
                }
                mBeforeModifyTask.State = mUserFenPai.State;//记录修改任务之前的任务状态
                if (mUserFenPai.State != TaskConstant.TaskState.FINISHED) {//非完成状态的任务改为完成任务
                    mUserFenPai.State = TaskConstant.TaskState.FINISHED;
                    mHeaderMoreTwoBtn.setVisibility(View.GONE);
                    mHeaderMoreBtn.setVisibility(View.GONE);
                    mHeaderCreateBtn.setVisibility(View.GONE);
                    mItvTaskDelete.setVisibility(View.GONE);
                }
                //发起请求
                mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
                mLoadingDialog.show();
                mTaskPresenter.modifyTask(mUserFenPai);
                if (mUserFenPai.Tasks != null && mUserFenPai.Tasks.size() > 0) {
                    for (ApiEntity.UserFenPai userfenpai : mUserFenPai.Tasks) {
                        userfenpai.State = TaskConstant.TaskState.FINISHED;
                        mTaskPresenter.modifyTask(userfenpai);
                    }
                }
                break;
            case R.id.cm_header_btn_more_create:
                if (!limit) {
                    ToastUtil.showToast(this, "你没有创建子任务的权限!");
                    return;
                }
                subTasks = !subTasks;
                mHeaderCreateBtn.setBackgroundResource(subTasks == false ? R.drawable.task_zirenwu : R.drawable.task_zirenwu_dianjiwan);
                mLlchildTaskContainer.setVisibility(subTasks == false ? View.GONE : View.VISIBLE);
                mSubTaskDevider.setVisibility(subTasks == false ? View.GONE : View.VISIBLE);
                break;
            case R.id.itv_task_delete:
                new AlertDialog(this).builder().setMsg("确认删除该任务?")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.confirm),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        delTask(mUserFenPai);
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                break;
        }
    }

    /**
     * 展示执行人
     */
    private void showMoreUsers() {

        String moreUser = mUserFenPai.MoreUser;
        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers(moreUser);
        mMoreUserContainer.removeAllViews();
        if (moreUsers != null && moreUsers.size() != 0) {
            mMoreUserTotalContainer.setVisibility(View.VISIBLE);
            mExcuteDevider.setVisibility(View.VISIBLE);
//            mTvHeadNumber.setVisibility(View.VISIBLE);
            mTvHeadNumber.setText(moreUsers.size() + "");
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                CircleImageView circleImageView = new CircleImageView(TaskDetailActivity.this);
                if (userInfo != null) {
                    circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                    TaskUtils.setCivImageView(userInfo.Image, circleImageView);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mMoreUserContainer.addView(circleImageView, params);
                } else {
                    circleImageView.setTextBg(0, "", 30);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mMoreUserContainer.addView(circleImageView, params);
                }
                if (i == 5) {
                    break;
                }
            }
        } else {
            //展示没有执行人
            mMoreUserTotalContainer.setVisibility(View.GONE);
            mExcuteDevider.setVisibility(View.GONE);
            mTvHeadNumber.setVisibility(View.GONE);
        }
    }

    /**
     * 展示负责人
     */
    private void showMainUsers() {
        String moreUser = mUserFenPai.MainUser;
        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers(moreUser);
        mMainUserContainer.removeAllViews();
        if (moreUsers != null && moreUsers.size() != 0) {
            mMainUserTotalContainer.setVisibility(View.VISIBLE);
            mTvChargeNum.setText(moreUsers.size() + "");
            for (int i = 0; i < moreUsers.size(); i++) {
                ApiEntity.UserInfo userInfo = moreUsers.get(i);
                CircleImageView circleImageView = new CircleImageView(TaskDetailActivity.this);
                if (userInfo != null) {
                    circleImageView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 30);
                    TaskUtils.setCivImageView(userInfo.Image, circleImageView);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mMainUserContainer.addView(circleImageView, params);
                } else {
                    circleImageView.setTextBg(0, "", 30);
                    params.leftMargin = DisplayUtil.dip2px(this, 5);
                    params.width = DisplayUtil.dip2px(this, 30);
                    params.height = DisplayUtil.dip2px(this, 30);
                    mMoreUserContainer.addView(circleImageView, params);
                }
                if (i == 5) {
                    break;
                }
            }
        } else {
            //展示没有执行人
            mMainUserTotalContainer.setVisibility(View.GONE);
        }
    }

    private String getHeadImageUri(String imageUrl) {
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
                .readUserInfo().CompanyCode, imageUrl);
        return uri;
    }

    /**
     * 通过任务ID获取任务实体对象
     *
     * @param taskID 任务ID
     * @return
     */
    private void getTaskById(int taskID) {
        API.TalkerAPI.GetTaskByIds("" + taskID,
                new RequestCallback<ApiEntity.UserFenPai>(ApiEntity.UserFenPai.class) {

                    @Override
                    public void onStarted() {
                        /*if (!isFinishing()) {
                            mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                            mLoadingDialog.show();
                        }*/
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mLoadingDialog != null && !isFinishing())
                            mLoadingDialog.dismiss();
                        mUserFenPai = new ApiEntity.UserFenPai();
                        AlertDialog dialog = new AlertDialog(TaskDetailActivity.this).builder();
                        dialog.setCancelable(false).setMsg(getString(R.string.task_detail_failed));
                        dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollToFinishActivity();
                            }
                        }).show();
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onParseSuccess(List<ApiEntity.UserFenPai> tasks) {
                        String mainUser = null;
                        if (mLoadingDialog != null && !isFinishing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (tasks != null && tasks.size() > 0) {
                            mUserFenPai = tasks.get(0);
                            mainUser = mUserFenPai.MainUser;
                            if (mUserFenPai != null && !TextUtils.isEmpty(mUserFenPai.Files)) {
                                mFileLayout.setEnabled(true);
                                String[] arr = mUserFenPai.Files.split(",");
                                mFileLayout.setTitle(arr.length + "个附件");
                                getFileByLineStr(StringUtils.replaceBlank(mUserFenPai.Files));
                            } else {
                                mFileLayout.setTitle("暂无相关附件");
                                mFileLayout.setEnabled(false);
                            }
                        }
                        if (mainUser != null && !mainUser.equals("")) {
                            initView();
                            getTaskReplyByTaskId(mUserFenPai.ID);
                        } else {
                            AlertDialog dialog = new AlertDialog(TaskDetailActivity.this).builder();
                            dialog.setCancelable(false).setMsg(getString(R.string.task_detail_failed));
                            dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    scrollToFinishActivity();
                                }
                            }).show();
                        }
                    }
                });
    }

    @Override
    public void createTask(String respInfo) {
    }

    @Override
    public void modifyTask(String s) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if ("1".equals(s)) {
            ToastUtil.showToast(this, "修改任务成功!",
                    R.drawable.tishi_ico_gougou);
            mHeaderMoreTwoBtn.setBackgroundResource(mUserFenPai.State == TaskConstant.TaskState.FINISHED ?
                    R.drawable.task_wancheng_jihuo : R.drawable.task_wancheng);
            Intent intent = new Intent();
            intent.setAction(ACTION_DETAIL_TASK);
            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
            sendBroadcast(intent);
            sendBroadcast(new Intent(TaskCreateActivity.ACTION_TASK_CREATE_SUCCESS));
            isStateModify = true;

        } else {
            ToastUtil.showToast(this, "修改任务失败!");
            mUserFenPai.State = mBeforeModifyTask.State;
        }

    }

    @Override
    public void getFileList(List<ApiEntity.Files> files) {

    }


    @Override
    public void completeFresh() {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        mUserFenPai.State = mBeforeModifyTask.State;
        ToastUtil.showToast(this, "修改任务失败!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserFenPai != null) {
            getTaskReplyByTaskId(mUserFenPai.ID);
        }
    }

    private void getTaskReplyByTaskId(int uid) {
        API.TalkerAPI.GetTaskReplyByTaskId(uid, 1, new RequestCallback<ApiEntity.TaskReply>(ApiEntity.TaskReply.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.TaskReply> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0 && respList.get(0).ReplyCount > 0) {
                    mTvTaskCommentNum.setText(String.valueOf(respList.get(0).ReplyCount));
                    mTvTaskCommentNum.setVisibility(View.VISIBLE);
                } else {
                    mTvTaskCommentNum.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 根据任务ID获取当前任务的操作日志
     *
     * @param id 任务ID
     */
    private void getTaskDetailLog(int id) {
        API.TalkerAPI.GetTalkerLogByTaskId(id, 1, new RequestCallback<ApiEntity.TaskReply>(ApiEntity.TaskReply.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
            }

            @Override
            public void onParseSuccess(List<ApiEntity.TaskReply> logs) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mLogList.clear();
                if (logs != null) {
                    mLogList.addAll(logs);
                }
                mLogAdapter.notifyDataSetChanged();
                mScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    /**
     * 根据任务ID删除任务
     *
     * @param task 任务实体
     */
    public void delTask(final ApiEntity.UserFenPai task) {
        API.TalkerAPI.DelTaskById(task.ID, new RequestCallback<String>(
                        String.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                    }

                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(TaskDetailActivity.this, "删除任务失败!");
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        if ("1".equals(arg0)) {
                            ToastUtil.showToast(TaskDetailActivity.this, "删除任务成功!", R.drawable.tishi_ico_gougou);
                            //删除任务
                            mTaskDetailChildAdapter.getDataList().remove(task);
                            mUserFenPai.Tasks = mTaskDetailChildAdapter.getDataList();
                            //发送广播
                            Intent intent = new Intent();
                            intent.setAction(ACTION_DETAIL_TASK);
                            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
                            sendBroadcast(intent);
                            Intent loadIntent = new Intent();
                            loadIntent.setAction(ObserveProjectActivity.BROADCAST_PROJECT_TASK_REFRESH);
                            sendBroadcast(loadIntent);
                            sendBroadcast(new Intent(TaskCreateActivity.ACTION_TASK_CREATE_SUCCESS));
                            finish();

                        } else {
                            ToastUtil.showToast(TaskDetailActivity.this, "删除任务失败!");
                            finish();
                        }
                    }
                }

        );
    }

    private class TaskDetailBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TaskModifyActivity.ACTION_MODIFY_TASK
                    .equals(intent.getAction()) || TaskMemberActivity.ACTION_TASK_MEMBER
                    .equals(intent.getAction()) || AttachmentActivity.ATTACHMENT_BROADCAST
                    .equals(intent.getAction()) || TaskDetailActivity.ACTION_DETAIL_TASK
                    .equals(intent.getAction()) || TaskCreateActivity.ACTION_CREATE_CHILD_TASK
                    .equals(intent.getAction())
                    ) {
                ApiEntity.UserFenPai userFenPai = (ApiEntity.UserFenPai) intent
                        .getSerializableExtra(TaskConstant.SEND_USERFENPAI);
                //遍历userFenPai
                if (userFenPai.ID == mUserFenPai.ID) {
                    mUserFenPai = userFenPai;
                } else {
                    TaskUtils.changeUserFenPai(mUserFenPai, userFenPai);
                }
                subTasks = false;//用于控制当前子任务创建的相关UI展示
                initView();
            }
        }
    }

    /**
     * 获取相关附件实体
     *
     * @param ids
     */
    private void getFileByLineStr(String ids) {
        API.UserData.GetFileListByIds(ids, new RequestCallback<ApiEntity.Files>(
                ApiEntity.Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mFileLayout.setEnabled(false);
                ToastUtil.showToast(TaskDetailActivity.this, "获取附件失败,请检查网络");
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> files) {
                if (files != null && files.size() > 0) {
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
