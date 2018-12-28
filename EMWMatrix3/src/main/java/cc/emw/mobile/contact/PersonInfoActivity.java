package cc.emw.mobile.contact;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.map.route.RouteActivity;
import cc.emw.mobile.me.adapter.WaitPlanAdapter2;
import cc.emw.mobile.me.adapter.WaitScheduleAdapter2;
import cc.emw.mobile.me.adapter.WaitTaskAdapter2;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import cc.emw.mobile.view.autoload.PullLoadMoreRecyclerView;
import cc.emw.mobile.view.expandablelayout.ExpandableLoadLayout;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_person_info)
public class PersonInfoActivity extends BaseActivity {

    @ViewInject(R.id.cIv_me_head_img)
    private CircleImageView mHeadImg;
    @ViewInject(R.id.cm_header_tv_right)
    private IconTextView mIcTvLocation;
    @ViewInject(R.id.tv_person_name)
    private TextView mName;
    @ViewInject(R.id.tv_person_small_name)
    private EditText mSmallName;
    @ViewInject(R.id.ic_tv_save_bz_name)
    private IconTextView mIcTvSaveBzName;   //编辑备注名按钮
    @ViewInject(R.id.tv_person_depart_name)
    private TextView mDepartName;
    @ViewInject(R.id.tv_person_company_name)
    private TextView mCompanyName;
    @ViewInject(R.id.tv_person_job_name)
    private TextView mJobName;  //职位名称
    @ViewInject(R.id.tv_person_mail)
    private TextView mMail;
    @ViewInject(R.id.tv_person_phone)
    private TextView mPhone;
    @ViewInject(R.id.tv_follow_tips)
    private TextView mFollow;
    @ViewInject(R.id.tv_person_chat)
    private LinearLayout tvChatBtn;
    @ViewInject(R.id.relation_calendar_num)
    private TextView tvCalendarNum;
    @ViewInject(R.id.relation_task_num)
    private TextView tvTaskNum;
    @ViewInject(R.id.relation_project_num)
    private TextView tvProject;

    public static final String ACTION_REFRESH_BZ = "cc.emw.mobile.refresh_bz";
    private DisplayImageOptions options;
    private UserInfo userInfo;
    private List<ApiEntity.UserMark> userMarks;
    private ApiEntity.UserMark um = new ApiEntity.UserMark(); // 用户备注对象
    public static final int REQUESTCODE = 1;
    private Dialog mLoadingDialog;
    private int intoTag;
    private InputMethodManager manager;

    @ViewInject(R.id.expandable_layout)
    private ExpandableLoadLayout expandableLayout;
    @ViewInject(R.id.swipeback_scrollview)
    private ScrollView scrollView;
    private ArrayList<ApiEntity.UserProject> projectList = new ArrayList<>();
    private int projectPage;

    @ViewInject(R.id.expandable_task)
    private ExpandableLoadLayout taskExpandLayout;
    private ArrayList<ApiEntity.UserFenPai> taskList = new ArrayList<>();
    private int taskPage;

    @ViewInject(R.id.expandable_schedule)
    private ExpandableLoadLayout scheduleExpandLayout;
    private ArrayList<CalendarInfo> scheduleList = new ArrayList<>();
    private int schedulePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        init();
        initAddDialog();

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!TextUtils.isEmpty(mMail.getText().toString())) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                    intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {mMail.getText().toString()});// 收件人
//                    //当无法确认发送类型的时候使用如下语句
//                    //intent.setType(“*/*”);
//                    //当没有附件,纯文本发送时使用如下语句
//                    intent.setType("plain/text");
//                    startActivity(Intent.createChooser(intent, "请选择邮件"));
//                }
            }
        });
    }

    private ListDialog mAddDialog;

    private void initAddDialog() {
        mAddDialog = new ListDialog(this, false);
        mAddDialog.addItem("语音通话", 1);
        mAddDialog.addItem("视频通话", 2);
        mAddDialog.addItem("拨打电话", 3);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case 1:
                        /*Intent intentVoice = new Intent(PersonInfoActivity.this, AudioConverseActivity.class);
                        intentVoice.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVoice.putExtra("userName", userInfo.Name);
                        intentVoice.putExtra("userId", userInfo.VoipCode);
                        intentVoice.putExtra("call_phone", userInfo.Phone);
                        intentVoice.putExtra("call_type", 4);//1:免费电话 2:直拨 4:智能
                        intentVoice.putExtra("call_head", userInfo.Image);
                        startActivity(intentVoice);*/
                        HelpUtil.startVoice(PersonInfoActivity.this, userInfo);
                        break;
                    case 2:
                        /*Intent intentVideo = new Intent(PersonInfoActivity.this, VideoConverseActivity.class);
                        intentVideo.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intentVideo.putExtra("userName", userInfo.Name);
                        intentVideo.putExtra("userId", userInfo.VoipCode);
                        intentVideo.putExtra("call_phone", userInfo.Phone);
                        intentVideo.putExtra("call_position", "");
                        startActivity(intentVideo);*/
                        HelpUtil.startVideo(PersonInfoActivity.this, userInfo);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone.getText().toString()));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void init() {
        mSmallName.setEnabled(false);
        mSmallName.setTag("0");
//        receiver = new MyBroadcastReceiver();
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_REFRESH_BZ);
//        registerReceiver(receiver, filter);
        mLoadingDialog = createLoadingDialog("正在处理");
        userInfo = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        intoTag = getIntent().getIntExtra("intoTag", 0);
        if (intoTag != 0 && PrefsUtil.readUserInfo().ID != userInfo.ID) {
            tvChatBtn.setVisibility(View.VISIBLE);
        } else {
            tvChatBtn.setVisibility(View.GONE);
        }
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        if (userInfo != null) {
            isFollowTip(userInfo);
            if (userInfo.IsFollow) {
                mFollow.setTag(1);
                mFollow.setText("取消关注");
            } else {
                mFollow.setTag(0);
                mFollow.setText("关注");
            }
//            mIcTvLocation.setVisibility(!TextUtils.isEmpty(userInfo.Axis) ? View.VISIBLE : View.GONE);
            mIcTvLocation.setVisibility(View.VISIBLE);
//            Log.d("zzzz", userInfo.Axis);
            mHeadImg.setTvBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 60);
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    userInfo.Image);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeadImg), options,
                    new ImageSize(DisplayUtil.dip2px(this, 60), DisplayUtil.dip2px(this, 60)), null, null);
            String sex;
            if (userInfo.Sex == 0)
                sex = "男";
            else
                sex = "女";
            mName.setText("姓名：" + userInfo.Name + "    " + sex);
            mDepartName.setText("部门：" + userInfo.DeptName);
            mJobName.setText(userInfo.Job);
            if (TextUtils.isEmpty(userInfo.DeptName))
                mDepartName.setText("其他");
            mPhone.setText(userInfo.Phone);
            mMail.setText(userInfo.Email);
            mCompanyName.setText(userInfo.CompanyCode);
//            mCompanyName.setText("深圳市中天博日科技有限公司");
//            mNowAddress.setText(userInfo.Adr);
        }
        getUserBz();
        getNumOfRelation();

        mHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonInfoActivity.this, PhotoActivity.class);
                String uri = String.format(Const.DOWN_ICON_URL,
                        PrefsUtil.readUserInfo().CompanyCode,
                        userInfo.Image);
                intent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, uri);
                intent.putExtra(PhotoActivity.INTENT_EXTRA_ISFORMAT, false);
                intent.putExtra("start_anim", false);
                startActivity(intent);
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.tv_person_small_name, R.id.tv_follow_tips,
            R.id.cm_header_tv_right, R.id.task_relation, R.id.work_project_relation,
            R.id.calendar_relation, R.id.tv_person_chat, R.id.ic_tv_save_bz_name, R.id.ll_person_phone})
    private void onHeadClick(View view) {
        Intent relation = null;
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.ic_tv_save_bz_name:
                if (mSmallName.getTag().equals("0")) {
                    mSmallName.setEnabled(true);
                    if (mSmallName.getText().toString().equals("暂无备注"))
                        mSmallName.setText("");
                    mSmallName.setSelection(mSmallName.getText().length());
                    mSmallName.setTag("1");
                    mIcTvSaveBzName.setIconText("e9a8");
                    mIcTvLocation.setIconText("e931");
                    KeyBoardUtil.openOrCloseSoftInput(this);
                }
                break;
            case R.id.tv_person_small_name:
//                mMenu.showAsDropDown(view);
//                setStartAnim(false);
//                Intent intentBzm = new Intent(PersonInfoActivity.this,
//                        BzmActivity.class);
//                intentBzm.putExtra("sUser", userInfo.ID);
//                if (userMarkz != null) {
//                    intentBzm.putExtra("userMark", userMarkz.Name);
//                }
//                startActivityForResult(intentBzm, REQUESTCODE);
//                overridePendingTransition(R.anim.popup_show, R.anim.activity_out);
//                setStartAnim(true);
//                Log.d("zrjt","------>true");
                break;
            case R.id.ll_person_phone:
                if (!TextUtils.isEmpty(mPhone.getText().toString()) && TextUtils.isDigitsOnly(userInfo.Phone.toString())) {
                    if (userInfo != null && !TextUtils.isEmpty(userInfo.Name) && !TextUtils.isEmpty(userInfo.VoipCode)) {
                        mAddDialog.show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone.getText().toString()));
                        startActivity(intent);
                    }
                } else {
                    ToastUtil.showToast(PersonInfoActivity.this, "号码无效");
                }
                break;
            case R.id.tv_follow_tips:
                String msg;
                if (mFollow.getTag() != null && mFollow.getTag().equals(1))
                    msg = "确认取消关注";
                else
                    msg = getString(R.string.follow_tips);
                new AlertDialog(PersonInfoActivity.this).builder().setMsg(msg)
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mFollow.getTag() != null && mFollow.getTag().equals(1))
                                    delFollow(userInfo);
                                else
                                    doFollow(userInfo);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.work_project_relation:
//                relation = new Intent(this, RelationProjectActivity.class);
                final int[] location1 = new int[2];
                view.getLocationInWindow(location1);
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                    /*view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            expandableLayout.setRefreshing(false);
                            expandableLayout.getRecyclerView().setAdapter(new SimpleAdapter());
                            expandableLayout.updateHeight();
                        }
                    }, 2000);*/
                    if (projectList.size() == 0) {
                        expandableLayout.setRefreshing(true);
                        final WaitPlanAdapter2 adapter = new WaitPlanAdapter2(PersonInfoActivity.this);
                        projectPage = 1;
                        API.TalkerAPI.GetProjectOnPageByUserId(userInfo.ID, projectPage, 10 , new RequestCallback<ApiEntity.UserProject>(
                                ApiEntity.UserProject.class) {
                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                expandableLayout.setRefreshing(false);
                                expandableLayout.collapse();
                                ToastUtil.showToast(PersonInfoActivity.this, R.string.relationproject_list_error);
                            }

                            @Override
                            public void onParseSuccess(List<ApiEntity.UserProject> respList) {
                                expandableLayout.setRefreshing(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (scrollView != null) {
                                            scrollView.smoothScrollBy(0, location1[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                        }
                                    }
                                }, 400);
                                if (respList.size() > 0) {
                                    projectList.addAll(respList);
                                    adapter.setData(projectList);
                                    expandableLayout.getRecyclerView().setAdapter(adapter);
                                    expandableLayout.updateHeight();
                                } else {
                                    expandableLayout.collapse();
                                    ToastUtil.showToast(PersonInfoActivity.this, "暂无项目信息！");
                                }
                            }
                        });
                        expandableLayout.getPullLoadMoreRecyclerView().setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                            @Override
                            public void onRefresh() {

                            }

                            @Override
                            public void onLoadMore() {
                                projectPage++;
                                API.TalkerAPI.GetProjectOnPageByUserId(userInfo.ID, projectPage, 10, new RequestCallback<ApiEntity.UserProject>(
                                        ApiEntity.UserProject.class) {
                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        expandableLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        projectPage--;
                                        ToastUtil.showToast(PersonInfoActivity.this, R.string.relationproject_list_error);
                                    }

                                    @Override
                                    public void onParseSuccess(List<ApiEntity.UserProject> respList) {
                                        expandableLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        if (respList.size() < 10 ) {
                                            expandableLayout.getPullLoadMoreRecyclerView().setHasMore(false);
                                        }
                                        if (respList.size() > 0) {
                                            projectList.addAll(respList);
                                            adapter.setData(projectList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scrollView != null) {
                                    scrollView.smoothScrollBy(0, location1[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                }
                            }
                        }, 400);
                    }
                }
                break;
            case R.id.task_relation:
//                relation = new Intent(this, RelationTaskActivity.class);
                final int[] location2 = new int[2];
                view.getLocationInWindow(location2);
                if (taskExpandLayout.isExpanded()) {
                    taskExpandLayout.collapse();
                } else {
                    taskExpandLayout.expand();
                    if (taskList.size() == 0) {
                        taskExpandLayout.setRefreshing(true);
                        final WaitTaskAdapter2 adapter = new WaitTaskAdapter2(PersonInfoActivity.this);
                        taskPage = 1;
                        API.TalkerAPI.GetTaskOnPageByUserId(userInfo.ID, taskPage, 10, new RequestCallback<ApiEntity.UserFenPai>(
                                ApiEntity.UserFenPai.class) {
                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                taskExpandLayout.setRefreshing(false);
                                taskExpandLayout.collapse();
                                ToastUtil.showToast(PersonInfoActivity.this, R.string.relationtask_list_error);
                            }

                            @Override
                            public void onParseSuccess(List<ApiEntity.UserFenPai> respList) {
                                taskExpandLayout.setRefreshing(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (scrollView != null) {
                                            scrollView.smoothScrollBy(0, location2[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                        }
                                    }
                                }, 400);
                                if (respList.size() > 0) {
                                    taskList.addAll(respList);
                                    adapter.setData(taskList);
                                    taskExpandLayout.getRecyclerView().setAdapter(adapter);
                                    taskExpandLayout.updateHeight();
                                } else {
                                    taskExpandLayout.collapse();
                                    ToastUtil.showToast(PersonInfoActivity.this, "暂无任务信息！");
                                }
                            }
                        });
                        taskExpandLayout.getPullLoadMoreRecyclerView().setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                            @Override
                            public void onRefresh() {

                            }

                            @Override
                            public void onLoadMore() {
                                taskPage++;
                                API.TalkerAPI.GetTaskOnPageByUserId(userInfo.ID, taskPage, 10, new RequestCallback<ApiEntity.UserFenPai>(
                                        ApiEntity.UserFenPai.class) {
                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        taskExpandLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        taskPage--;
                                        ToastUtil.showToast(PersonInfoActivity.this, R.string.relationtask_list_error);
                                    }

                                    @Override
                                    public void onParseSuccess(List<ApiEntity.UserFenPai> respList) {
                                        taskExpandLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        if (respList.size() < 10 ) {
                                            taskExpandLayout.getPullLoadMoreRecyclerView().setHasMore(false);
                                        }
                                        if (respList.size() > 0) {
                                            taskList.addAll(respList);
                                            adapter.setData(taskList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scrollView != null) {
                                    scrollView.smoothScrollBy(0, location2[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                }
                            }
                        }, 400);
                    }
                }
                break;
            case R.id.calendar_relation:
//                relation = new Intent(this, RelationCalendarActivity.class);
                final int[] location3 = new int[2];
                view.getLocationInWindow(location3);
                if (scheduleExpandLayout.isExpanded()) {
                    scheduleExpandLayout.collapse();
                } else {
                    scheduleExpandLayout.expand();
                    if (scheduleList.size() == 0) {
                        scheduleExpandLayout.setRefreshing(true);
                        final WaitScheduleAdapter2 adapter = new WaitScheduleAdapter2(PersonInfoActivity.this);
                        schedulePage = 1;
                        API.TalkerAPI.GetAllCalenderListOnPageByUserId(userInfo.ID, schedulePage, 10, new RequestCallback<CalendarInfo>(
                                CalendarInfo.class) {
                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                scheduleExpandLayout.setRefreshing(false);
                                scheduleExpandLayout.collapse();
                                ToastUtil.showToast(PersonInfoActivity.this, R.string.relationcalendar_list_error);
                            }

                            @Override
                            public void onParseSuccess(List<CalendarInfo> respList) {
                                scheduleExpandLayout.setRefreshing(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (scrollView != null) {
                                            scrollView.smoothScrollBy(0, location3[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                        }
                                    }
                                }, 400);
                                if (respList.size() > 0) {
                                    scheduleList.addAll(respList);
                                    adapter.setData(scheduleList);
                                    scheduleExpandLayout.getRecyclerView().setAdapter(adapter);
                                    scheduleExpandLayout.updateHeight();
                                } else {
                                    scheduleExpandLayout.collapse();
                                    ToastUtil.showToast(PersonInfoActivity.this, "暂无日程信息！");
                                }
                            }
                        });
                        scheduleExpandLayout.getPullLoadMoreRecyclerView().setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                            @Override
                            public void onRefresh() {

                            }

                            @Override
                            public void onLoadMore() {
                                schedulePage++;
                                API.TalkerAPI.GetTaskOnPageByUserId(userInfo.ID, schedulePage, 10, new RequestCallback<CalendarInfo>(
                                        CalendarInfo.class) {
                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        scheduleExpandLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        schedulePage--;
                                        ToastUtil.showToast(PersonInfoActivity.this, R.string.relationcalendar_list_error);
                                    }

                                    @Override
                                    public void onParseSuccess(List<CalendarInfo> respList) {
                                        scheduleExpandLayout.getPullLoadMoreRecyclerView().setPullLoadMoreCompleted();
                                        if (respList.size() < 10 ) {
                                            scheduleExpandLayout.getPullLoadMoreRecyclerView().setHasMore(false);
                                        }
                                        if (respList.size() > 0) {
                                            scheduleList.addAll(respList);
                                            adapter.setData(scheduleList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scrollView != null) {
                                    scrollView.smoothScrollBy(0, location3[1] - DisplayUtil.dip2px(PersonInfoActivity.this, 74));
                                }
                            }
                        }, 400);
                    }
                }
                break;
            case R.id.cm_header_tv_right:
                if (mSmallName.getTag().equals("1")) {
                    mSmallName.setTag("0");
                    mSmallName.setEnabled(false);
                    mIcTvSaveBzName.setIconText("eb3b");
                    mIcTvLocation.setIconText("eab5");
                    submitSuggest();
                } else {
                    Intent intents = new Intent(this, RouteActivity.class);
                    intents.putExtra("ID", userInfo.ID);
                    if (userInfo.Axis != null)
                        intents.putExtra("Axis", userInfo.Axis);
                    startActivity(intents);
                }
                break;
            case R.id.tv_person_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("start_anim", false);
                intent.putExtra("SenderID", userInfo.ID);
                intent.putExtra("name", userInfo.Name);
                startActivity(intent);
                break;
        }
        if (relation != null) {
            relation.putExtra("user_info", userInfo);
            relation.putExtra("start_anim", false);
            int[] location = new int[2];
            view.getLocationInWindow(location);
            relation.putExtra("click_pos_y", location[1]);
            startActivity(relation);
        }
    }

    private void isFollowTip(UserInfo userInfo) {
        if (userInfo.ID == PrefsUtil.readUserInfo().ID)
            mFollow.setVisibility(View.GONE);
        else
            mFollow.setVisibility(View.VISIBLE);
    }

    private void doFollow(final UserInfo sUser) {
        List<Integer> fuids = new ArrayList<Integer>();
        fuids.add(sUser.ID);
        API.UserAPI.DoFollow(fuids, 1,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String responseInfo) {
                        mLoadingDialog.dismiss();
                        if (responseInfo != null && "1".equals(responseInfo)) {
                            ToastUtil.showToast(PersonInfoActivity.this, R.string.person_follow_success, R.drawable.tishi_ico_gougou);
                            mFollow.setTag(1);
                            mFollow.setText("取消关注");
                            sUser.IsFollow = true;
                            if (EMWApplication.personMap != null && EMWApplication.personMap.get(sUser.ID) != null)
                                EMWApplication.personMap.get(sUser.ID).IsFollow = true;
                            Intent intent = new Intent(PersonFragments.ACTION_REFRESH_CONTACT_LIST);
                            sendBroadcast(intent); // 刷新人员列表
                        } else if (responseInfo != null && "0".equals(responseInfo)) {
                            ToastUtil.showToast(PersonInfoActivity.this, R.string.person_follow_error);
                        } else {
                            Toast.makeText(PersonInfoActivity.this, "超时！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonInfoActivity.this, "关注失败");
                    }

                }

        );
    }

    private void delFollow(final UserInfo userInfo) {
        List<Integer> fuids = new ArrayList<Integer>();
        fuids.add(userInfo.ID);
        API.UserAPI.DoFollow(fuids, 2,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        mLoadingDialog.show();
                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonInfoActivity.this, "取消关注失败");
                    }

                    @Override
                    public void onSuccess(String responseInfo) {
                        mLoadingDialog.dismiss();
                        if (responseInfo.toString() != null && "1".equals(responseInfo)) {
                            ToastUtil.showToast(PersonInfoActivity.this, R.string.person_cancelfollow_success, R.drawable.tishi_ico_gougou);
                            mFollow.setTag(0);
                            mFollow.setText("关注");
                            if (EMWApplication.personMap != null && EMWApplication.personMap.get(userInfo.ID) != null)
                                EMWApplication.personMap.get(userInfo.ID).IsFollow = false;
                            Intent intent = new Intent(
                                    PersonFragments.ACTION_REFRESH_CONTACT_LIST);
                            sendBroadcast(intent); // 刷新人员列表
                        } else if (responseInfo.toString() != null && "0".equals(responseInfo)) {
                            ToastUtil.showToast(PersonInfoActivity.this, R.string.person_cancelfollow_error);
                        } else {
                            ToastUtil.showToast(PersonInfoActivity.this, "超时！");
                        }
                    }
                });
    }

    /**
     * 获取人员相关条目的数量
     */
    private void getNumOfRelation() {
        API.TalkerAPI.GetTalkCountByUserId(userInfo.ID,
                new RequestCallback<Integer>(Integer.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(PersonInfoActivity.this, R.string.person_relationcount_error);
                    }

                    @Override
                    public void onParseSuccess(List<Integer> respList) {
                        if (respList != null && respList.size() >= 4) {
                            if (respList.get(0) > 0) {
                                tvProject.setVisibility(View.VISIBLE);
                                tvProject.setText(respList.get(0) + "");
                            }
                            if (respList.get(1) > 0) {
                                tvTaskNum.setVisibility(View.VISIBLE);
                                tvTaskNum.setText(respList.get(1) + "");
                            }
                            if (respList.get(2) > 0) {
                                tvCalendarNum.setVisibility(View.VISIBLE);
                                tvCalendarNum.setText(respList.get(2) + "");
                            }
                        } else {
                            ToastUtil.showToast(PersonInfoActivity.this, R.string.person_relationcount_error);
                        }
                    }
                });
    }

    /**
     * 获取用户备注
     */
    private void getUserBz() {
        if (userInfo != null) {
            um.UserId = userInfo.ID;
        }
        API.UserAPI.GetUserMark(um, new RequestCallback<String>(String.class) {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Toast.makeText(PersonInfoActivity.this, "获取人员备注名失败",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                try {
                    Gson gson = new Gson();
                    userMarks = new ArrayList<>();
                    JsonArray array = new JsonParser().parse(arg0)
                            .getAsJsonArray();
                    for (JsonElement jsonElement : array) {
                        ApiEntity.UserMark userMark = gson.fromJson(jsonElement,
                                ApiEntity.UserMark.class);
                        if (userMark.Creator == PrefsUtil.readUserInfo().ID) {
                            um = userMark;
                        }
                    }
                    if (um != null && !TextUtils.isEmpty(um.Name)) {
                        mSmallName.setText(um.Name);
                    } else {
                        mSmallName.setText("暂无备注");
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    /**
     * 提交用户备注
     */
    private void submitSuggest() {
        um.UserId = userInfo.ID;
        um.Name = mSmallName.getText().toString();
        API.UserAPI.DoUserMark(um, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(PersonInfoActivity.this, R.string.bzm_submit_error);
            }

            @Override
            public void onStarted() {
//                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
//                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    if (um.Name == null || TextUtils.isEmpty(um.Name))
                        mSmallName.setText("暂无备注");
//                    ToastUtil.showToast(PersonInfoActivity.this, R.string.bzm_submit_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(PersonFragments.ACTION_REFRESH_CONTACT_LIST);
                    sendBroadcast(intent);
                } else {
                    ToastUtil.showToast(PersonInfoActivity.this, R.string.bzm_submit_error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (receiver != null)
//            unregisterReceiver(receiver);
    }

}
