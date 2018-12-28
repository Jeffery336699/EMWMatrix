package cc.emw.mobile.project.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.NewTeamAdapter;
import cc.emw.mobile.project.bean.ConstEnum;
import cc.emw.mobile.project.bean.ConstMap;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/23.
 * 新建/修改团队协作
 */
@ContentView(R.layout.activity_new_project3)
public class NewProjectActivity extends BaseActivity implements INewProjectView, NewTeamAdapter.OnSetUpdateListener {
    private static final String TAG = "NewProjectActivity";
    public static final int SELECT_TEAM_REQUEST_CODE = 3; //选择圈子
    public static final String BROADCAST_PROJECT_REFRESH = "broadcast_project_refresh";

    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_tv_right7) private IconTextView mHeaderArchiveItv; //顶部归档按钮
    @ViewInject(R.id.cm_header_tv_right8) private IconTextView mHeaderDelItv; //顶部删除按钮

    @ViewInject(R.id.et_team_name) private EditText mTeamNameEt;  //团队名称
    @ViewInject(R.id.et_team_desc) private EditText mTeamDescEt; //团队描述
    @ViewInject(R.id.rl_team_group) private RelativeLayout mGroupLayout; //圈子根Layout
    @ViewInject(R.id.tv_team_group) private TextView mGroupNameTv;    //圈子名称
    @ViewInject(R.id.itv_team_arrow) private IconTextView mGroupArrowItv; //圈子箭头
    @ViewInject(R.id.tv_team_starttime) private TextView mStartTimeTv;   //开始时间
    @ViewInject(R.id.tv_team_endtime) private TextView mEndTimeTv;    //结束时间
    @ViewInject(R.id.iv_team_color) private ImageView mTeamColorIv;  //团队颜色
    @ViewInject(R.id.tv_team_color) private TextView mTeamColorTv; //颜色文本
    @ViewInject(R.id.tv_team_mainuser) private TextView mMainUserTitleTv; //负责人文本
    @ViewInject(R.id.lv_team_mainuser) private ExListView mMainUserGv; //负责人列表
    @ViewInject(R.id.ll_team_user) private LinearLayout mUserTitleLayout; //成员标题Layout
    @ViewInject(R.id.lv_team_user) private ExListView mUserGv; //成员列表

    private Dialog mLoadingDialog; //加载框
    private ListDialog mListDialog; //颜色选项弹出dialog
    private SimpleDateFormat dateFormat;
    private TimePickerView mStartTimePicker, mEndTimePicker;
    private NewTeamAdapter mainUserAdapter;
    private NewTeamAdapter userAdapter;
    private ProjectPresenter presenter = new ProjectPresenter(this);

    private ApiEntity.UserProject project; //编辑传值
    private String groupName; //编辑传值
    private ArrayList<Integer> members;
    private ArrayList<Integer> groupIdList; //圈子人员id集
    private ArrayList<ApiEntity.UserInfo> mainUserList, userList; //负责人集；成员集
    private boolean isModify = false; //是否处于编辑状态


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        dateFormat = new SimpleDateFormat(getString(R.string.timeformat4),Locale.CHINA);
        initView();
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9,R.id.cm_header_tv_right7,R.id.cm_header_tv_right8,R.id.cm_header_tv_right9,
            R.id.itv_team_adduser, R.id.rl_team_group, R.id.rl_team_starttime, R.id.rl_team_endtime,R.id.rl_team_color})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mTeamNameEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right7: //归档
                new AlertDialog(this).builder().setMsg("是否确定将该团队归档？")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                archiveProjectById(project.ID);
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).show();
                break;
            case R.id.cm_header_tv_right8: //删除
                new AlertDialog(this).builder().setMsg("确定删除该项目？")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delProjectById(project.ID);
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).show();
                break;
            case R.id.cm_header_tv_right9: //保存
                if(verifyInput()){
                    project.Name = mTeamNameEt.getText().toString();
                    project.Mark = mTeamDescEt.getText().toString();
                    if(!isModify) {
                        project.KeyInfo = "";
                        project.MainUser = "";
                        project.Users = "";
                        project.Tasks = new ArrayList<>();
                    } else {
                        project.MainUser = getUserIDs(mainUserList);
                        project.MainUserList = mainUserList;
                        project.Users = getUserIDs(userList);
                        project.UsersList = userList;
                    }
                    mLoadingDialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                    presenter.createProject(project);
                    mLoadingDialog.show();
                }
                break;
            case R.id.rl_team_group: //选择所属圈子
                Intent intentSelTeam = new Intent(this,TeamListActivity.class);
                intentSelTeam.putExtra("teamId",project.TeamId);
                intentSelTeam.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intentSelTeam.putExtra("click_pos_y", location[1]);
                startActivityForResult(intentSelTeam,SELECT_TEAM_REQUEST_CODE);
                break;
            case R.id.itv_team_adduser: //添加团队成员
                Intent intent = new Intent(this, ContactSelectActivity.class);
                if (groupIdList != null && groupIdList.size() > 0) {
                    ArrayList<ApiEntity.UserInfo> filterUserInfos = new ArrayList<>();
                    for (int i = 0; i < groupIdList.size(); i++) {
                        if (EMWApplication.personMap.get(groupIdList.get(i)) != null)
                            filterUserInfos.add(EMWApplication.personMap.get(groupIdList.get(i)));
                    }
                    members.clear();
                    for (ApiEntity.UserInfo user : mainUserList) {
                        members.add(user.ID);
                    }
                    for (ApiEntity.UserInfo user : userList) {
                        members.add(user.ID);
                    }
                    if (members.size() > 0) {
                        Iterator<ApiEntity.UserInfo> it = filterUserInfos.iterator();
                        while (it.hasNext()) {
                            if (members.contains(it.next().ID)) { //过滤掉已有的用户
                                it.remove();//注意此处不能用list.remove(it.next());
                            }
                        }
                    }
                    if (filterUserInfos.size() == 0) {
                        ToastUtil.showToast(this, "暂无可加入的成员！");
                        return;
                    }
                    intent.putExtra(ContactSelectActivity.EXTRA_FILTER_LIST, filterUserInfos);
                }
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                intent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(intent, ContactSelectActivity.MULTI_SELECT);
                break;
            case R.id.rl_team_starttime:
                mStartTimePicker.show();
                break;
            case R.id.rl_team_endtime:
                mEndTimePicker.show();
                break;
            case R.id.rl_team_color:
                mListDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ContactSelectActivity.MULTI_SELECT:
                    ArrayList<UserInfo> users = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    if (users != null && users.size() > 0){
                        for (UserInfo userInfo : users) {
                            Gson gson = new Gson();
                            userList.add(gson.fromJson(gson.toJson(userInfo), ApiEntity.UserInfo.class));
                            members.add(userInfo.ID);
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                    break;
                case SELECT_TEAM_REQUEST_CODE: //所属圈子
                    project.TeamId = data.getIntExtra("teamId",-1);
                    mGroupNameTv.setText(data.getStringExtra("teamName"));
                    break;
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        project = (ApiEntity.UserProject)getIntent().getSerializableExtra("project");
        groupName = getIntent().getStringExtra("groupName");
        groupIdList = getIntent().getIntegerArrayListExtra("groupmembers");
        mHeaderArchiveItv.setVisibility(View.INVISIBLE);
        mHeaderDelItv.setVisibility(View.INVISIBLE);

        initListDialog();
        initTimePicker();

        if(project == null){
            mHeaderTitleTv.setText("新增团队协作");
            mGroupLayout.setEnabled(true);
            mGroupArrowItv.setVisibility(View.VISIBLE);
            project = new ApiEntity.UserProject();
            project.TeamId = -1;
        }else{
            isModify = true;
            mHeaderTitleTv.setText("修改团队协作");
            mGroupLayout.setEnabled(false);
            mGroupArrowItv.setVisibility(View.GONE);
            mTeamNameEt.setText(project.Name);
            if (!TextUtils.isEmpty(project.Mark)) {
                mTeamDescEt.setText(Html.fromHtml(project.Mark));
            }
            mGroupNameTv.setText(groupName);
            mStartTimeTv.setText(project.BeginTime);
            mEndTimeTv.setText(project.EndTime);
            mTeamColorIv.setImageResource(ConstMap.getProjectColor(project.Color));
            mTeamColorIv.setVisibility(project.Color == 0 ? View.GONE : View.VISIBLE);
            mTeamColorTv.setText(ConstMap.getColorTxt(project.Color));
            mListDialog.setSelectItem(project.Color);
            mHeaderArchiveItv.setVisibility(project.IsArchive == 1 ? View.GONE : View.VISIBLE);
            mHeaderDelItv.setVisibility(View.VISIBLE);
            mMainUserTitleTv.setVisibility(View.VISIBLE);
            mUserTitleLayout.setVisibility(View.VISIBLE);

            mainUserList = new ArrayList<>();
            if (project.MainUserList != null) {
                mainUserList.addAll(project.MainUserList);
            }
            mainUserAdapter = new NewTeamAdapter(this, mainUserList, 0);
            mainUserAdapter.setOnSetUpdateListener(this);
            mMainUserGv.setAdapter(mainUserAdapter);

            userList = new ArrayList<>();
            if (project.UsersList != null) {
                userList.addAll(project.UsersList);
            }
            userAdapter = new NewTeamAdapter(this, userList, 1);
            userAdapter.setOnSetUpdateListener(this);
            mUserGv.setAdapter(userAdapter);

            members = new ArrayList<>();
        }
    }

    /**
     * 初始化颜色选项弹框
     */
    private void initListDialog() {
        mListDialog = new ListDialog(this, true, false, true);
        mListDialog.addItem("无", ConstEnum.ProjectColor.None);
        mListDialog.addItem("浅蓝", ConstEnum.ProjectColor.LightBlue);
        mListDialog.addItem("橙色", ConstEnum.ProjectColor.Orange);
        mListDialog.addItem("红色", ConstEnum.ProjectColor.Red);
        mListDialog.addItem("深蓝", ConstEnum.ProjectColor.DarkBlue);
        mListDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id){
                    case ConstEnum.ProjectColor.None:
                        break;
                    case ConstEnum.ProjectColor.LightBlue:
                        mTeamColorIv.setImageResource(R.drawable.project_color1);
                        break;
                    case ConstEnum.ProjectColor.Orange:
                        mTeamColorIv.setImageResource(R.drawable.project_color2);
                        break;
                    case ConstEnum.ProjectColor.Red:
                        mTeamColorIv.setImageResource(R.drawable.project_color3);
                        break;
                    case ConstEnum.ProjectColor.DarkBlue:
                        mTeamColorIv.setImageResource(R.drawable.project_color4);
                        break;
                }
                project.Color = item.id;
                mTeamColorIv.setVisibility(item.id == ConstEnum.ProjectColor.None ? View.GONE : View.VISIBLE);
                mTeamColorTv.setText(item.text);
            }
        });
    }

    /**
     * 验证页面数据
     * @return
     */
    private boolean verifyInput(){
        boolean isPass = false;
        if(TextUtils.isEmpty(mTeamNameEt.getText().toString().trim())){
            ToastUtil.showToast(this,"请填写团队名称！");
        } else if (TextUtils.isEmpty(mGroupNameTv.getText().toString())){
            ToastUtil.showToast(this,"请选择所属圈子！");
        } else if (TextUtils.isEmpty(mStartTimeTv.getText().toString())){
            ToastUtil.showToast(this,"请选择开始日期！");
        } else if (TextUtils.isEmpty(mEndTimeTv.getText().toString())){
            ToastUtil.showToast(this,"请选择结束日期！");
        } else {
            isPass = true;
        }
        return isPass;
    }

    @Override
    public void renderView(final boolean isCreateSuccess, final String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
                createTips(isCreateSuccess, msg);
            }
        },1000);
    }

    /**
     * 操作提示信息
     * @param isCreateSuccess
     * @param msg
     */
    private void createTips(boolean isCreateSuccess, String msg) {
        if(isCreateSuccess){
            Intent intentBroadCast = new Intent();
            if(!isModify) {
                ToastUtil.showToast(this,"创建成功！", R.drawable.tishi_ico_gougou);
                intentBroadCast.setAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
            } else {
                ToastUtil.showToast(this, "修改成功！", R.drawable.tishi_ico_gougou);
                intentBroadCast.putExtra("project",this.project);
                intentBroadCast.setAction(this.BROADCAST_PROJECT_REFRESH);
            }
            sendBroadcast(intentBroadCast);
            onBackPressed();
        }else {
            if(!isModify)
                ToastUtil.showToast(this,"创建失败: " + msg);
            else
                ToastUtil.showToast(this,"修改失败: " + msg);
        }
    }

    @Override
    public void onError(Throwable ex) {
        if(ex instanceof ConnectException){
            ToastUtil.showToast(this,"网络错误或无连接");
        }else{
            ToastUtil.showToast(this,ex.getMessage());
        }
    }

    /**
     * 初始化时间选择器
     */
    private void initTimePicker(){
        mStartTimePicker = new TimePickerView(this, TimePickerView.Type.ALL);//时间选择器类型
        mStartTimePicker.setTitle(getResources().getString(R.string.beg_time));
        mStartTimePicker.setCancelable(true);
        mStartTimePicker.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { //时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mStartTimeTv.setText(dateFormat.format(date));
                project.BeginTime = mStartTimeTv.getText().toString();
            }
        });

        mEndTimePicker = new TimePickerView(this, TimePickerView.Type.ALL);//时间选择器类型
        mEndTimePicker.setTitle(getResources().getString(R.string.end_time));
        mEndTimePicker.setCancelable(true);
        mEndTimePicker.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { //时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                try {
                    Date d = dateFormat.parse(mStartTimeTv.getText().toString());
                    if (d.getTime() < date.getTime()) {
                        mEndTimeTv.setText(dateFormat.format(date));
                        project.EndTime = mEndTimeTv.getText().toString();
                    } else {
                        ToastUtil.showToast(NewProjectActivity.this, getResources().getString(R.string.endtime_less_begtime));
                    }
                } catch (ParseException e) {
                    ToastUtil.showToast(NewProjectActivity.this, "请先选择开始时间！");
                }
            }
        });
    }

    /**
     * 团队归档
     * @param pid 团队id
     */
    private void archiveProjectById(int pid) {
        API.TalkerAPI.ArchiveProjectById(pid, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                delErrorMsg(ex.getMessage());
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if(mLoadingDialog != null) mLoadingDialog.dismiss();
                if(!TextUtils.isEmpty(result) && result.equals("1")){
                    ToastUtil.showToast(NewProjectActivity.this,"归档成功! ",R.drawable.tishi_ico_gougou);
                    Intent intentBroadCast = new Intent();
                    intentBroadCast.setAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
                    intentBroadCast.putExtra("state", "archive");
                    sendBroadcast(intentBroadCast);
                    AppManager.finishActivity(ObserveProjectActivity.class);
                    AppManager.finishActivity(StateBoardActivity.class);
                    onBackPressed();
                }else {
                    delErrorMsg("归档失败" + result);
                }
            }
        });
    }

    /**
     * 删除项目
     * @param pid 项目id
     */
    private void delProjectById(int pid) {
        API.TalkerAPI.DelProjectById(pid, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                delErrorMsg(ex.getMessage());
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if(!TextUtils.isEmpty(result) && result.equals("1")){
                    delSuccess("删除团队成功！");
                }else {
                    delErrorMsg("删除团队失败" + result);
                }
            }
        });
    }

    /**
     * 删除项目失败信息
     * @param msg
     */
    private void delErrorMsg(String msg){
        if(mLoadingDialog != null) mLoadingDialog.dismiss();
        ToastUtil.showToast(this,msg);
    }

    /**
     * 删除项目成功信息
     * @param msg
     */
    private void delSuccess(String msg){
        if(mLoadingDialog != null) mLoadingDialog.dismiss();
        ToastUtil.showToast(this,msg,R.drawable.tishi_ico_gougou);
        Intent intentBroadCast = new Intent();
        intentBroadCast.setAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
        intentBroadCast.putExtra("state", "delete");
        sendBroadcast(intentBroadCast);
        AppManager.finishActivity(ObserveProjectActivity.class);
        AppManager.finishActivity(StateBoardActivity.class);
        onBackPressed();
    }

    @Override
    public void onSetUpdate(ApiEntity.UserInfo user, int type) {
        if (type == 0) {
            mainUserList.remove(user);
            mainUserAdapter.notifyDataSetChanged();
            userList.add(user);
            userAdapter.notifyDataSetChanged();
        } else {
            userList.remove(user);
            userAdapter.notifyDataSetChanged();
            mainUserList.add(user);
            mainUserAdapter.notifyDataSetChanged();
        }
    }

    private String getUserIDs(List<ApiEntity.UserInfo> users) {
        StringBuilder ids = new StringBuilder();
        for (ApiEntity.UserInfo user : users) {
            ids.append(user.ID).append(",");
        }
        if (ids.length() > 0) {
            ids.deleteCharAt(ids.length() - 1);
        }
        return ids.toString();
    }
}
