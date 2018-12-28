package cc.emw.mobile.project.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.NewTeamAdapter;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SegmentedGroup;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/23.
 * 新建/修改圈子与协作
 */
@ContentView(R.layout.activity_new_team3)
public class NewTeamActivity extends BaseActivity implements INewTeamView, NewTeamAdapter.OnSetUpdateListener {

    public static final String TAG = "NewTeamActivity";
    public static final String BROADCAST_TEAM_REFRESH = "broadcast_team_refresh";

    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; //顶部标题
    @ViewInject(R.id.cm_header_tv_right8) private IconTextView cm_header_tv_right8; //顶部删除按钮

    @ViewInject(R.id.et_group_name) private EditText mGroupName;  //圈子名称
    @ViewInject(R.id.segmented_private_type) private SegmentedGroup mRadioGroup; //权限
    @ViewInject(R.id.tv_group_mainuser) private TextView mMainUserTitleTv; //负责人文本
    @ViewInject(R.id.lv_group_mainuser) private ExListView mMainUserGv; //负责人列表
    @ViewInject(R.id.ll_group_user) private LinearLayout mUserTitleLayout; //成员标题Layout
    @ViewInject(R.id.lv_group_user) private ExListView mUserGv; //成员列表

    private Dialog mLoadingDialog; //加载框
    private NewTeamAdapter mainUserAdapter;
    private NewTeamAdapter userAdapter;
    private ProjectPresenter presenter;

    private ApiEntity.ChatterGroup chatterGroup; //传值
    private ArrayList<Integer> members; //所有圈内人id集
    private ArrayList<ApiEntity.UserInfo> mainUserList, userList; //负责人集；成员集
    private boolean isModify = false; //是否处于编辑状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right8,R.id.cm_header_tv_right9, R.id.itv_group_adduser})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mGroupName);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right8:
                new AlertDialog(this).builder().setMsg("确定解散该圈子？")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delGroupById(chatterGroup.ID);
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).show();
                break;
            case R.id.cm_header_tv_right9:
                if(verifyInput()){
                    chatterGroup.Name = mGroupName.getText().toString();
                    if (isModify) {
                        chatterGroup.MainUser = getUserIDs(mainUserList);
                        chatterGroup.MainUserInfo = mainUserList;
                        chatterGroup.Users = userList;
                        members.clear();
                        for (ApiEntity.UserInfo user : mainUserList) {
                            members.add(user.ID);
                        }
                        for (ApiEntity.UserInfo user : userList) {
                            members.add(user.ID);
                        }
                    }
                    mLoadingDialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                    presenter.createTeam(chatterGroup, members);
                    mLoadingDialog.show();
                }
                break;
            case R.id.itv_group_adduser:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                members.clear();
                for (ApiEntity.UserInfo user : mainUserList) {
                    members.add(user.ID);
                }
                for (ApiEntity.UserInfo user : userList) {
                    members.add(user.ID);
                }
                if (members.size() > 0) {
                    ArrayList<ApiEntity.UserInfo> selUserInfos = new ArrayList<>();
                    selUserInfos.addAll(EMWApplication.personSortList);
                    Iterator<ApiEntity.UserInfo> it = selUserInfos.iterator();
                    while (it.hasNext()) {
                        if (members.contains(it.next().ID)) { //过滤掉已有的用户
                            it.remove();//注意此处不能用list.remove(it.next());
                        }
                    }
                    intent.putExtra(ContactSelectActivity.EXTRA_FILTER_LIST, selUserInfos);
                }
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, ContactSelectActivity.MULTI_SELECT);
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
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        chatterGroup = (ApiEntity.ChatterGroup)getIntent().getSerializableExtra("group");
        members = getIntent().getIntegerArrayListExtra("members");
        if (members == null) {
            members = new ArrayList<>();
        }

        if(chatterGroup == null) {
            mHeaderTitleTv.setText("新增圈子与协作");
            cm_header_tv_right8.setVisibility(View.INVISIBLE);
            chatterGroup = new ApiEntity.ChatterGroup();
            chatterGroup.ID = 0;
            chatterGroup.Memo = "";
            chatterGroup.CreateUser = PrefsUtil.readUserInfo().ID;
            chatterGroup.TeamType = 1;
            chatterGroup.Parent = 1;
            chatterGroup.ProjectId = 0;
            chatterGroup.Image = "";
            chatterGroup.Users = new ArrayList<>();
        }else{
            isModify = true;
            mHeaderTitleTv.setText("修改圈子与协作");
            mGroupName.setText(chatterGroup.Name);
            mGroupName.setSelection(mGroupName.length());
            mRadioGroup.check(chatterGroup.Type == 0 ? R.id.access_public : R.id.access_private);
            cm_header_tv_right8.setVisibility(View.VISIBLE);
            mMainUserTitleTv.setVisibility(View.VISIBLE);
            mUserTitleLayout.setVisibility(View.VISIBLE);

            mainUserList = new ArrayList<>();
            if (chatterGroup.MainUserInfo != null) {
                mainUserList.addAll(chatterGroup.MainUserInfo);
            }
            mainUserAdapter = new NewTeamAdapter(this, mainUserList, 0);
            mainUserAdapter.setOnSetUpdateListener(this);
            mMainUserGv.setAdapter(mainUserAdapter);

            userList = new ArrayList<>();
            if (chatterGroup.Users != null) {
                userList.addAll(chatterGroup.Users);
            }
            userAdapter = new NewTeamAdapter(this, userList, 1);
            userAdapter.setOnSetUpdateListener(this);
            mUserGv.setAdapter(userAdapter);
        }

        presenter = new ProjectPresenter(this);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.access_public:
                        chatterGroup.Type = 0;
                        break;
                    case R.id.access_private:
                        chatterGroup.Type = 1;
                        break;
                }
            }
        });
    }

    /**
     * 验证数据
     * @return
     */
    private boolean verifyInput(){
        boolean isPass = true;
        if(TextUtils.isEmpty(mGroupName.getText().toString()) && isPass){
            isPass = false;
            ToastUtil.showToast(this,"请填写团队名称！");
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
        if (isCreateSuccess) {
            Intent intentBroadCast = new Intent();
            mGroupName.setEnabled(false);
            mRadioGroup.setClickable(false);
            if (!isModify) {
                ToastUtil.showToast(this, "创建成功！", R.drawable.tishi_ico_gougou);
            } else {
                intentBroadCast.putExtra("group", chatterGroup);
                intentBroadCast.putExtra("members", members);
                ToastUtil.showToast(this, "修改成功！", R.drawable.tishi_ico_gougou);
            }
            intentBroadCast.setAction(BROADCAST_TEAM_REFRESH);
            sendBroadcast(intentBroadCast);
            onBackPressed();
        } else {
            if (!isModify)
                ToastUtil.showToast(this, "创建失败: " + msg);
            else
                ToastUtil.showToast(this, "修改失败: " + msg);
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
     * 删除圈子
     * @param gid 圈子id
     */
    private void delGroupById(int gid) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(
                String.class) {
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
                    delSuccess("解散圈子成功！");
                }else {
                    delErrorMsg("解散圈子失败" + result);
                }
            }
        });
    }

    /**
     * 删除失败信息提示
     * @param msg
     */
    private void delErrorMsg(String msg){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog != null) mLoadingDialog.dismiss();
            }
        },1000);
        ToastUtil.showToast(this,msg);
    }

    /**
     * 删除成功信息提示
     * @param msg
     */
    private void delSuccess(String msg){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog != null) mLoadingDialog.dismiss();
            }
        },1000);
        ToastUtil.showToast(this,msg,R.drawable.tishi_ico_gougou);
        Intent intentBroadCast = new Intent();
        intentBroadCast.setAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
        sendBroadcast(intentBroadCast);
        AppManager.finishActivity(TeamActivity.class);
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
