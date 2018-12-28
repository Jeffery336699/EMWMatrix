package cc.emw.mobile.project.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.project.adapter.TeamMemberAdapter;
import cc.emw.mobile.project.adapter.TeamMemberAdapter2;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/23.
 * 查看团队成员列表
 */
@ContentView(R.layout.activity_team_member3)
public class TeamMemberActivity extends BaseActivity implements INewTeamView ,INewProjectView{
    private final String TAG = this.getClass().getSimpleName();

    @ViewInject(R.id.cm_header_bar)
    private LinearLayout bar;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; // 顶部条消息右菜单按钮
    @ViewInject(R.id.invite_member_ll)
    private LinearLayout inviteBtn; //邀请按钮
    @ViewInject(R.id.member_num_tv)
    private TextView memberNum; //成员数量
    @ViewInject(R.id.team_member_lv)
    private ListView mListView; //团队成员头像列表

    private TeamMemberAdapter2 adapter;
    private ArrayList<Integer> members; //成员id集合
    private ArrayList<ApiEntity.UserInfo> memberList;
    private String groupName;
    private ProjectPresenter presenter = new ProjectPresenter(this);
    private ApiEntity.ChatterGroup group;
    private Dialog dialog;
    private ArrayList<Integer> tempMembers = new ArrayList<>();
    private boolean isDelete = false;
    private ApiEntity.UserProject project; //项目
    private boolean isFromProject = false;
    private ArrayList<Integer> groupmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();

        try {
            findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
            findViewById(R.id.cm_header_btn_left9).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {

        }
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.dialog_content, R.id.cm_header_btn_right, R.id.invite_member_ll})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_content:
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
            case R.id.invite_member_ll:
                Intent intent = new Intent(this, ContactSelectActivity.class);

                ArrayList<ApiEntity.UserInfo> selUserInfos = new ArrayList<>();
                for (int i = 0; i < members.size(); i++) {
                    selUserInfos.add(EMWApplication.personMap.get(members.get(i)));
                }
                intent.putExtra("select_list", selUserInfos);

                ArrayList<ApiEntity.UserInfo> filterUserInfos = new ArrayList<>();
                if (groupmembers != null && groupmembers.size() > 0) {
                    for (int i = 0; i < groupmembers.size(); i++) {
                        filterUserInfos.add(EMWApplication.personMap.get(groupmembers.get(i)));
                    }
                    intent.putExtra(ContactSelectActivity.EXTRA_FILTER_LIST, filterUserInfos);
                }
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactSelectActivity.MULTI_SELECT && resultCode == RESULT_OK) {
            ArrayList<UserInfo> users = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
            isDelete = false;
            tempMembers.clear();
            tempMembers.addAll(members);
            members.clear();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i) != null)
                    members.add(users.get(i).ID);
            }
            dialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
            if(isFromProject){
                ArrayList<Integer> mainUserList = new ArrayList<>();
                if(project.MainUserList!= null) {
                    for (int j = 0; j < project.MainUserList.size(); j++) {
                        mainUserList.add(project.MainUserList.get(j).ID);
                    }
                }
                StringBuilder builder = new StringBuilder();
                ArrayList<ApiEntity.Role> roles = new ArrayList<>();
                for(int i = 0;i<members.size();i++) {
                    Logger.d(TAG,"member " + i + ": " + members.get(i));
                    ApiEntity.UserInfo userInfo = EMWApplication.personMap.get(members.get(i));
                    if(userInfo != null) {
                        Logger.d(TAG,"userInfo: " + i);
                        ApiEntity.Role role = new ApiEntity.Role();
                        role.ID = userInfo.ID;
                        role.Name = userInfo.Name;
                        role.Image = userInfo.Image;
                        role.Email = userInfo.Email;
                        role.Job = userInfo.Job;
                        role.DeptId = userInfo.DeptID;
                        role.DeptName = userInfo.DeptName;
                        role.Type = userInfo.UserType;
                        roles.add(role);
                    }
                    if(mainUserList.contains(members.get(i))) continue;
                    builder.append(members.get(i)).append(",");
                }
                project.Users = builder.toString().substring(0,builder.length() - 1);
//                project.UsersList.clear();
//                project.UsersList.addAll(roles);
                presenter.createProject(project);
            }else {
                presenter.createTeam(group, members);
            }
            dialog.show();
//            adapter.setData(members);
//            adapter.notifyDataSetChanged();
//            memberNum.setText(members.size() + "人");
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
//        bar.setBackgroundResource(R.drawable.trans_bg);

        Intent intent = getIntent();
        isFromProject = intent.getBooleanExtra("isFromProject", false);
//        members = intent.getIntegerArrayListExtra("members");
        memberList = (ArrayList<ApiEntity.UserInfo>) intent.getSerializableExtra("member_list");
        groupmembers = intent.getIntegerArrayListExtra("groupmembers");
        if (members == null) {
            members = new ArrayList<Integer>();
            for (ApiEntity.UserInfo user : memberList) {
                members.add(user.ID);
            }
        }
        memberNum.setText((members == null ? 0 : members.size()) + "人");
        groupName = intent.getStringExtra("groupName");
        group = (ApiEntity.ChatterGroup) intent.getSerializableExtra("group");
        project = (ApiEntity.UserProject) intent.getSerializableExtra("project");
        mHeaderTitleTv.setText(isFromProject ? project.Name : groupName);
        int creator = isFromProject ? project.Creator : group.CreateUser;
        if (PrefsUtil.readUserInfo().ID == creator) {
            inviteBtn.setVisibility(View.VISIBLE);
        }
//        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
//        mHeaderNoticeBtn.setVisibility(View.VISIBLE);

        adapter = new TeamMemberAdapter2(this, TeamMemberAdapter.TYPE_LIST);
        adapter.setCreatorId(isFromProject ? project.Creator : group.CreateUser);
        adapter.setData(memberList);
        mListView.setAdapter(adapter);
        adapter.setOnDelClickListener(new TeamMemberAdapter2.OnDelClickListener() {
            @Override
            public void delClick(final int position) {
                new AlertDialog(TeamMemberActivity.this).builder().setMsg("确定删除该成员?")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(TeamMemberActivity.this.getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isDelete = true;
                                tempMembers.clear();
                                //保存要删除的临时成员
                                tempMembers.add(members.get(position));
                                members.remove(position);
                                dialog = createLoadingDialog(getResources().getString(R.string.progress_tip));
                                presenter.createTeam(group, members);
                                dialog.show();
                            }
                        })
                        .setNegativeButton(TeamMemberActivity.this.getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
            }
        });
    }

    @Override
    public void renderView(final boolean isCreateSuccess, final String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                createTips(isCreateSuccess, msg);
            }
        }, 1000);
    }

    @Override
    public void onError(Throwable ex) {
        if (ex instanceof ConnectException) {
            ToastUtil.showToast(this, "网络错误或无连接");
        } else {
            ToastUtil.showToast(this, ex.getMessage());
        }
    }

    /**
     * 操作信息提示
     * @param isCreateSuccess
     * @param msg
     */
    private void createTips(boolean isCreateSuccess, String msg) {
        if (isCreateSuccess) {
            adapter.setData(memberList);
            adapter.notifyDataSetChanged();
            memberNum.setText(members.size() + "人");
            if (isDelete) {
                ToastUtil.showToast(this, "删除成功！", R.drawable.tishi_ico_gougou);
            } else {
                ToastUtil.showToast(this, "修改人员成功！", R.drawable.tishi_ico_gougou);
            }
            Intent intentBroadCast = new Intent();
            if(isFromProject){
                intentBroadCast.setAction(NewProjectActivity.BROADCAST_PROJECT_REFRESH);
                Logger.d(TAG,"BROADCAST_PROJECT_REFRESH");
            }else {
                intentBroadCast.setAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
                Logger.d(TAG,"BROADCAST_TEAM_REFRESH");
            }
            intentBroadCast.putIntegerArrayListExtra("members", members);
            sendBroadcast(intentBroadCast);
//            onBackPressed();
        } else {
            if (isDelete) {
                ToastUtil.showToast(this, "删除失败" + msg);
            } else {
                ToastUtil.showToast(this, "修改人员失败" + msg);
            }
            members.clear();
            members.addAll(tempMembers);
        }
    }
}
