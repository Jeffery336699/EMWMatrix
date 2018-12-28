package cc.emw.mobile.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.RelationFileActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;


/**
 * Created by sunny.du on 2016/10/15.
 * 群组聊天功能选择提示框
 */
public class ChatTeamHintActivity extends BaseActivity {
    TextView mTvTeamInfo;
    TextView mTvTeamAdd;
    TextView mTvTeamFile;
    TextView mTvTeamDelete;
    TextView mTvTeamNull;
    private GroupInfo groupInfo;    //群信息实体类
    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> noteRoles;
    private List<Integer> userids;//群组用户ID保存
    private ArrayList<UserInfo> sUsers;
    private ArrayList<Message> msgList;
    private int selectTeamUserPosition;

    /***********
     * 初始化
     ***************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_chat_team_hint);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("GroupInfo");
        msgList = (ArrayList<Message>) getIntent().getSerializableExtra("msg_player");
        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();
        getGroupMember(groupInfo.ID);

    }

    private void initView() {
        mTvTeamInfo = (TextView) findViewById(R.id.tv_team_info);
        mTvTeamAdd = (TextView) findViewById(R.id.tv_team_add);
        mTvTeamFile = (TextView) findViewById(R.id.tv_team_file);
        mTvTeamDelete = (TextView) findViewById(R.id.tv_team_delete);
        mTvTeamNull = (TextView) findViewById(R.id.tv_team_null);
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
    }

    /************************
     * 事件监听
     *******************************************************************************************************/
    private void initEvent() {
        mTvTeamInfo.setOnClickListener(new View.OnClickListener() {//查看群信息
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatTeamHintActivity.this, ChatTeamInfoActivity3.class);
                intent.putExtra("groupInfo", groupInfo);
                intent.putExtra("msg_player", msgList);
                intent.putExtra("start_anim", false);
                startActivity(intent);
                finish();
            }
        });
        mTvTeamAdd.setOnClickListener(new View.OnClickListener() {//添加成员
            @Override
            public void onClick(View v) {//添加成员
                Intent intent = new Intent(ChatTeamHintActivity.this, ContactSelectActivity.class);
                intent.putExtra("select_list", noteRoles);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                ChatTeamHintActivity.this.startActivityForResult(intent, 1);
                finish();
            }
        });
        mTvTeamFile.setOnClickListener(new View.OnClickListener() {//查看附件
            @Override
            public void onClick(View v) {
                Intent relationIntent = new Intent(ChatTeamHintActivity.this, RelationFileActivity.class);
                relationIntent.putExtra("group_info", groupInfo);
                startActivity(relationIntent);
                finish();
            }
        });
        mTvTeamDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {//群管理员解散群并且退出
                    if (sUsers.size() != 1) {
                        Intent selectUserIntent = new Intent(ChatTeamHintActivity.this, ChatTeamPersonSelectActivity.class);
                        selectUserIntent.putExtra("userinfo", sUsers);
                        selectUserIntent.putExtra("groupinfo", groupInfo);
                        startActivityForResult(selectUserIntent, 2);
                    } else {
                        delGroup(groupInfo.ID);
                        finish();
                    }

                } else {//普通群成员退出群
                    new AlertDialog(ChatTeamHintActivity.this).builder().setMsg("确认退出群" + groupInfo.Name).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delGroupRoles(groupInfo.ID, PrefsUtil.readUserInfo().ID);
                            finish();
                        }
                    }).show();
                }
            }
        });
        mTvTeamNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 返回人员选择
         */
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            sUsers.clear();
            sUsers = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
            if (sUsers.size() > 0) {
                userids.clear();
                for (int i = 0; i < sUsers.size(); i++) {
                    userids.add(sUsers.get(i).ID);
                }
                saveGroupInfo();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            selectTeamUserPosition = data.getIntExtra("count", -1);
            if (selectTeamUserPosition != -1) {
                new AlertDialog(ChatTeamHintActivity.this).builder().setMsg("确定要转让群管理权和退出群吗？").setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        outChatterGroupByCreator(groupInfo.ID, sUsers.get(selectTeamUserPosition).ID);
                        finish();
                    }
                }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();

            }
        }
    }

    /*************************
     * 请求网络接口
     ************************************************************************************************************/
    /**
     * 管理员转让
     *
     * @param gid   群ID
     * @param actor 选择的管理员id
     */
    private void outChatterGroupByCreator(int gid, int actor) {
        API.TalkerAPI.OutChatterGroupByCreator(gid, actor, new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && result.equals("true")) {
                    ToastUtil.showToast(ChatTeamHintActivity.this, "转让管理员成功", R.drawable.tishi_ico_gougou);
                    //TODO

                    delGroupRoles(groupInfo.ID, PrefsUtil.readUserInfo().ID);
                } else {
                    ToastUtil.showToast(ChatTeamHintActivity.this, "操作失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ChatTeamHintActivity.this, "操作失败,服务器异常");
            }
        });
    }


    /**
     * 退出群组
     *
     * @param gid
     * @param userid
     */
    private void delGroupRoles(int gid, int userid) {
        API.TalkerAPI.DelGroupUser(gid, userid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_exit_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    delete(groupInfo.ID);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_exit_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_exit_error);
                }
            }
        });
    }

    private void delete(int sendID) {
        API.Message.RemoveChatRecord(sendID, 2, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
            }

            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 人员变动提交后台
     */
    private void saveGroupInfo() {
        API.TalkerAPI.SaveChatterGroup(groupInfo, userids, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(ChatTeamHintActivity.this, "编辑失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ChatTeamHintActivity.this, "编辑成功", R.drawable.tishi_ico_gougou);
                    //刷新缓存
                    EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    // 刷群主列表
                    Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    getGroupMember(groupInfo.ID);
                } else {
                    ToastUtil.showToast(ChatTeamHintActivity.this, "编辑失败,请稍候再试");
                }
            }
        });
    }

    /**
     * 获取群成员信息
     *
     * @param gid
     */
    private void getGroupMember(int gid) {
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<UserInfo>(UserInfo.class, true) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (respList != null && respList.size() > 0) {
                    noteRoles.clear();
                    if (groupInfo.Users != null) {
                        groupInfo.Users.clear();
                        groupInfo.Users.addAll(respList);
                    }
                    noteRoles.addAll(respList);
                    sUsers = noteRoles;
                    if (sUsers.size() > 0) {
                        userids.clear();
                        for (int i = 0; i < sUsers.size(); i++) {
                            userids.add(sUsers.get(i).ID);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onStarted() {
            }
        });
    }


    /**
     * //     * 解散群
     */
    private void delGroup(int gid) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_delete_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    delete(groupInfo.ID);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_delete_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtil.showToast(ChatTeamHintActivity.this, R.string.groupinto_delete_error);
                }
            }
        });
    }

}
