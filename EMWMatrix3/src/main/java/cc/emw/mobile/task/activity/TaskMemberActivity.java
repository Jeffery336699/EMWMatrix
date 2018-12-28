package cc.emw.mobile.task.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.adapter.TaskMemberAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by chengyong.liu on 2016/6/24.
 * 查看任务负责人与参与人已选的人员
 */
@ContentView(R.layout.activity_task_team_member_3)
public class TaskMemberActivity extends BaseActivity implements ITaskModifyView {
    @ViewInject(R.id.invite_member_ll)
    private LinearLayout mLlInviteMember;
    @ViewInject(R.id.view_team_memeber_devider)
    private View mInviteDevider;//邀请人员分割线
    @ViewInject(R.id.member_num_tv)
    private TextView mTvMemberNum;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_bar)
    private LinearLayout cm_header_bar;
    @ViewInject(R.id.cm_header_tv_right9)
    private IconTextView mTvRight9;
    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mLlBlank;// 空视图
    @ViewInject(R.id.team_member_lv)
    private ListView mLvMember;

    public static final String ACTION_TASK_MEMBER = "cc.emw.mobile.task_memeber";
    public static final String FITER_TEAM_LIST = "fiter_team_list";//用于传送新建任务界面选择关联项目的相关人员列表
    public static final String TASK_MEMBER_REQUEST_TYPE = "task_member_request_type";
    public static final String MEMBERTYPE = "membertype";
    public static final int Member_new = 21;
    public static final int Member_modify = 22;
    public static final int CHARGE = 1;//负责人
    public static final int EXCUTOR = 2;//执行人
    public static final int ADDMEMBER = 11;
    public static final int DELETEMEMBER = 12;

    private int memberType;
    private int mRequestType;//用于控制人员界面是临时储存还是实时请求修改等动作
    private int mOperateMember;// 用于标记对附件操作的类型
    private boolean isShow = false;//是否展示邀请和删除功能
    private boolean limit = false;//负责人权限 当前用户属于负责人，具有权限 为true

    private List<ApiEntity.UserInfo> moreUsers;
    private ArrayList<ApiEntity.UserInfo> fiterTeamList;
    private ApiEntity.UserFenPai mFailureUserFenPai;//记录修改前的任务对象
    private ApiEntity.UserFenPai mUserFenPai;
    private ApiEntity.UserInfo mTempUserInfo;//用于记录要删除的人员
    private ApiEntity.UserInfo mCurrentUser;//当前用户

    private TaskMemberAdapter mMemberAdapter;
    private TaskPresenter mTaskPresenter;
    private Dialog mLoadingDialog;// 加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initData();
        initPawer();
        initAdapter();
        initView();
    }

    private void initPawer() {
        //设置标题
        String moreUser = null;
        if (memberType == CHARGE) {
            moreUser = mUserFenPai.MainUser;
//            mHeaderTitleTv.setText("负责人");
        } else if (memberType == EXCUTOR) {
            moreUser = mUserFenPai.MoreUser;
//            mHeaderTitleTv.setText("执行人");
        }
        moreUsers = TaskUtils.getUsers(moreUser);
        if (mRequestType != -1) {
            //新建或者修改界面进来的
            isShow = true;
        } else {
//            mTvRight9.setVisibility(View.GONE);
            findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
            isShow = false;
        }
        //是否是负责人权限
        mCurrentUser = EMWApplication.personMap .get(PrefsUtil.readUserInfo().ID);
        String mainUser = mUserFenPai.MainUser;
        List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
        if (mainUsers.contains(mCurrentUser) || mUserFenPai.Creator == PrefsUtil.readUserInfo().ID) {
            limit = true;
        } else {
            limit = false;
        }
    }

    private void initAdapter() {
        //是否展示邀请
        if (isShow || limit) {
            mMemberAdapter = new TaskMemberAdapter(this, true);
            mLlInviteMember.setVisibility(View.VISIBLE);
            mInviteDevider.setVisibility(View.VISIBLE);
        } else {
            mMemberAdapter = new TaskMemberAdapter(this, false);
            mLlInviteMember.setVisibility(View.GONE);
            mInviteDevider.setVisibility(View.GONE);
        }
        mMemberAdapter.setRequestType(mRequestType);
        mLvMember.setAdapter(mMemberAdapter);
    }

    private void initData() {
        mFailureUserFenPai = new ApiEntity.UserFenPai();
        mTaskPresenter = new TaskPresenter(this);
        mUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
        memberType = getIntent().getIntExtra(MEMBERTYPE, 0);
        //任务详情界面进来请求类型为-1;新建或者修改界面进来不为-1
        mRequestType = getIntent().getIntExtra(TASK_MEMBER_REQUEST_TYPE, -1);
        //获取关联项目相关人员
        fiterTeamList = (ArrayList<ApiEntity.UserInfo>) getIntent().getSerializableExtra(FITER_TEAM_LIST);
//        cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
    }

    private void initView() {
        mTvMemberNum.setText(moreUsers.size() + "人");
        if (moreUsers.size() != 0) {
            //展示更多负责人
            mLlBlank.setVisibility(View.GONE);
            mLvMember.setVisibility(View.VISIBLE);
        } else {
            //展示空视图
            mLlBlank.setVisibility(View.VISIBLE);
            mLvMember.setVisibility(View.GONE);
        }
        mMemberAdapter.setData(moreUsers);
        mMemberAdapter.notifyDataSetChanged();
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.invite_member_ll,R.id.cm_header_btn_left9,R.id.cm_header_tv_right9})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_member_ll:
                //跳转到邀请人界面
                Intent addMember = new Intent(TaskMemberActivity.this, ContactSelectActivity.class);
                addMember.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                addMember.putExtra("select_list", (ArrayList<ApiEntity.UserInfo>) mMemberAdapter.getData());
                if(fiterTeamList != null &&fiterTeamList.size() != 0) {
                    addMember.putExtra("filter_list", fiterTeamList);
                }
                addMember.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                addMember.putExtra("click_pos_y", location[1]);
                startActivityForResult(addMember, 1);
                break;
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                //将人员信息传递到请求新建或者修改界面中
                Intent result = new Intent();
                ArrayList<ApiEntity.UserInfo> data = (ArrayList<ApiEntity.UserInfo>) mMemberAdapter.getData();
                if (memberType == CHARGE) {
                    if (data.size() == 0) {
                        ToastUtil.showToast(this, "必须选择一个负责人!");
                        return;
                    }
                }
                result.putExtra("member_result", data);
                setResult(Activity.RESULT_OK, result);
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mOperateMember = ADDMEMBER;
            ArrayList<ApiEntity.UserInfo> userRets = (ArrayList<ApiEntity.UserInfo>) data .getSerializableExtra("select_list");
            if (userRets != null) {
                moreUsers = userRets;
            }
            //添加联系人
            if (memberType == CHARGE) {
                mFailureUserFenPai.MainUser = mUserFenPai.MainUser;//记录修改前的任务实体的字段
                mUserFenPai.MainUser = TaskUtils.members2string(moreUsers);
            } else if (memberType == EXCUTOR) {
                mFailureUserFenPai.MoreUser = mUserFenPai.MoreUser;
                mUserFenPai.MoreUser = TaskUtils.members2string(moreUsers);
            }
            if (mRequestType == -1) {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
                mTaskPresenter.modifyTask(mUserFenPai);
            } else {
                initView();
            }
        }
    }
    /*********************************调用底层方法网络传输回调*************************************************/
    @Override
    public void createTask(String respInfo) {
    }
    @Override
    public void modifyTask(String s) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if ("1".equals(s)) {
            switch (mOperateMember) {
                case ADDMEMBER:
                    ToastUtil.showToast(this, "邀请人员成功", R.drawable.tishi_ico_gougou);
                    break;
                case DELETEMEMBER:
                    ToastUtil.showToast(this, "删除人员成功", R.drawable.tishi_ico_gougou);
                    break;
            }
            initView();
            //发送广播
            Intent intent = new Intent();
            intent.setAction(ACTION_TASK_MEMBER);
            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
            sendBroadcast(intent);
        } else {
            switch (mOperateMember) {
                case ADDMEMBER:
                    ToastUtil.showToast(this, "邀请人员失败");
                    break;
                case DELETEMEMBER:
                    ToastUtil.showToast(this, "删除人员失败");
                    break;
            }
            //删除或者添加失败还原原来的字段
            String moreUser = null;
            if (memberType == CHARGE) {
                mUserFenPai.MainUser = mFailureUserFenPai.MainUser;
                moreUser = mUserFenPai.MainUser;
            } else if (memberType == EXCUTOR) {
                mUserFenPai.MoreUser = mFailureUserFenPai.MoreUser;
                moreUser = mUserFenPai.MoreUser;
            }
            moreUsers = TaskUtils.getUsers(moreUser);
        }
        mOperateMember = 0;
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
        switch (mOperateMember) {
            case ADDMEMBER:
                ToastUtil.showToast(this, "邀请人员失败");
                break;
            case DELETEMEMBER:
                ToastUtil.showToast(this, "删除人员失败");
                break;
        }
        String moreUser = null;
        if (memberType == CHARGE) {
            mUserFenPai.MainUser = mFailureUserFenPai.MainUser;
            moreUser = mUserFenPai.MainUser;
        } else if (memberType == EXCUTOR) {
            mUserFenPai.MoreUser = mFailureUserFenPai.MoreUser;
            moreUser = mUserFenPai.MoreUser;
        }
        moreUsers = TaskUtils.getUsers(moreUser);
        mOperateMember = 0;
    }

    public int getMemberType() {
        return memberType;
    }

    public TextView getTvMemberNum() {
        return mTvMemberNum;
    }

    public LinearLayout getMLlBlank() {
        return mLlBlank;
    }

    public ListView getListView() {
        return mLvMember;
    }
    /**
     * 添加不重复人员
     *
     * @param userRets 添加的人员列表
     * @param users    目的人员列表
     * @return 返回添加后的目的人员列表
     */
    private List<ApiEntity.UserInfo> checkCopyUsers(List<ApiEntity.UserInfo> userRets,
                                                    List<ApiEntity.UserInfo> users) {
        for (int i = 0; i < userRets.size(); i++) {
            boolean haveFlag = false;// 返回列表中是否有重复
            for (int j = 0; j < users.size(); j++) {
                if (userRets.get(i).ID == users.get(j).ID) {
                    haveFlag = true;
                    break;
                }
            }
            if (!haveFlag) {
                users.add(userRets.get(i));
            }
        }
        return users;
    }
    /**
     * 删除人员
     *
     * @param userInfo 要删除的人员
     * @param list     删除后剩下的人员
     */
    public void DeleteMember(ApiEntity.UserInfo userInfo, List<ApiEntity.UserInfo> list) {
        mOperateMember = DELETEMEMBER;
        mTempUserInfo = userInfo;
        moreUsers = list;
        if (memberType == CHARGE) {
            mFailureUserFenPai.MainUser = mUserFenPai.MainUser;//记录修改前的任务实体的字段
            mUserFenPai.MainUser = TaskUtils.members2string(list);
        } else if (memberType == EXCUTOR) {
            mFailureUserFenPai.MoreUser = mUserFenPai.MoreUser;
            mUserFenPai.MoreUser = TaskUtils.members2string(list);
        }
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
        mLoadingDialog.show();
        mTaskPresenter.modifyTask(mUserFenPai);
    }

}
