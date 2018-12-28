package cc.emw.mobile.contact;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.contact.adapter.GroupEditAdapter;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_group_into)
public class GroupIntoActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn;
    @ViewInject(R.id.img_group_contain)
    private CircleImageView mCircleImageView;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeadRight;
    @ViewInject(R.id.btn_addchat_group)
    private Button btnChat; // 发起群聊
    @ViewInject(R.id.tv_group_create_person)
    private TextView tvCreate; // 创建者
    @ViewInject(R.id.tv_group_name)
    private TextView tvName;
    @ViewInject(R.id.tv_group_member)
    private TextView tvNumber;
    @ViewInject(R.id.group_grid_view)
    private GridView gridView;// 人员头像列表
    @ViewInject(R.id.ll_group_layout)
    private LinearLayout countLay;
    @ViewInject(R.id.btn_del_group)
    private Button btnDelGroup; // 解散群组
    @ViewInject(R.id.btn_exit_group)
    private Button btnExitGroup; // 退出群组
    // 群组相关数目
    @ViewInject(R.id.relation_project_num)
    private TextView projectNum;
    @ViewInject(R.id.relation_task_num)
    private TextView taskNum;
    @ViewInject(R.id.relation_calendar_num)
    private TextView calendarNum;
    @ViewInject(R.id.relation_file_num)
    private TextView fileNum;

    private GroupInfo info = new GroupInfo();
    private Dialog mLoadingDialog; // 加载框
    private GroupEditAdapter adapter;
    private boolean IsFromChat;
    private ArrayList<UserInfo> sUsers = new ArrayList<UserInfo>();
    public static final String ACTION_REFRESH_COUNT_GROUP = "cc.emw.mobile.refresh.count";
    private ArrayList<UserInfo> noteRoles = new ArrayList<UserInfo>();
    private MyBroadCastReceiver receiver;
    private static final int REQUEST_CAPTURE_PHOTO = 10001;
    private DisplayImageOptions options;
    private String mUploadImg;
    private List<Integer> userids = new ArrayList<Integer>();

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            getGroupMember(info.ID);
            adapter.notifyDataSetChanged();
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.cm_img_grouphead)
                    .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                    .showImageOnFail(R.drawable.cm_img_grouphead)
                    .cacheInMemory(true).cacheOnDisk(true).build();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
        if (getIntent().hasExtra("IsFromChat")) {
            IsFromChat = getIntent().getBooleanExtra("IsFromChat", false);
        }
        if (IsFromChat) {
            btnChat.setVisibility(View.INVISIBLE);
        } else {
            btnChat.setVisibility(View.VISIBLE);
        }
        info = (GroupInfo) getIntent().getSerializableExtra("group_info");
        ImageLoader.getInstance().displayImage(Const.BASE_URL + info.Image,
                mCircleImageView, options);
        getGroupMember(info.ID);
        getRelationCount();
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        if (info.CreateUser == PrefsUtil.readUserInfo().ID) {
            mHeadRight.setVisibility(View.VISIBLE);
        }

        IntentFilter filter = new IntentFilter();
        receiver = new MyBroadCastReceiver();
        filter.addAction(ACTION_REFRESH_COUNT_GROUP);
        registerReceiver(receiver, filter);

        mHeaderTitleTv.setText(R.string.groupinto);
        mHeadRight.setText(R.string.edit);
        mHeadRight.setTag(0);
        tvName.setText(info.Name);
        if (info.CreateUser == PrefsUtil.readUserInfo().ID) {
            btnExitGroup.setVisibility(View.GONE);
        }
        if (info.Type == 0) {
            tvCreate.setText("创建人： " + info.CreateUserName + "  公开群组");
        } else if (info.Type == 1) {
            tvCreate.setText("创建人： " + info.CreateUserName + "  私有群组");
        }
        adapter = new GroupEditAdapter(GroupIntoActivity.this, mLoadingDialog);
        adapter.setGroupInfo(info);
        gridView.setAdapter(adapter);

    }

    @Event(value = {R.id.cm_header_btn_left, R.id.btn_addchat_group,
            R.id.cm_header_tv_right, R.id.ll_group_layout, R.id.file_relation,
            R.id.task_relation, R.id.btn_exit_group,
            R.id.work_project_relation, R.id.calendar_relation,
            R.id.img_group_contain})
    private void onContentClick(View view) {
        Intent relationIntent = null;
        switch (view.getId()) {
            case R.id.img_group_contain:
                if (mHeadRight.getTag().equals(1)) {
                    ActionSheetDialog dialog = new ActionSheetDialog(this)
                            .builder();
                    dialog.addSheetItem(getString(R.string.actionsheet_photo),
                            null, new OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                            .fromFile(new File(
                                                    EMWApplication.tempPath
                                                            + "tempraw.png")));
                                    startActivityForResult(intent,
                                            REQUEST_CAPTURE_PHOTO);
                                }
                            });
                    dialog.addSheetItem(getString(R.string.actionsheet_pick), null,
                            new OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Crop.pickImage(GroupIntoActivity.this);
                                }
                            });
                    dialog.show();
                }
                break;
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.btn_addchat_group:// 群组聊天
                Intent intent1 = new Intent(this, ChatActivity.class);
                intent1.putExtra("name", info.Name);// 群组名字
                intent1.putExtra("type", 2);
                intent1.putExtra("GroupID", info.ID);// 群组id
                intent1.putExtra("SenderID", PrefsUtil.readUserInfo().ID);// 当前用户id
                startActivity(intent1);
                break;
            case R.id.cm_header_tv_right:
                if (mHeadRight.getTag().equals(0)) {
                    adapter.setEditEnabled(true);
                    adapter.notifyDataSetChanged();
                    mHeadRight.setText(R.string.finish);
                    mHeadRight.setTag(1);
                    // 完成创建群组
                    btnDelGroup.setVisibility(View.VISIBLE);
                    btnDelGroup.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog(GroupIntoActivity.this)
                                    .builder()
                                    .setMsg(getString(R.string.deletegroup_tips))
                                    .setPositiveButton(getString(R.string.ok),
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    delGroup(info.ID);

                                                }
                                            })
                                    .setNegativeButton(getString(R.string.cancel),
                                            new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();
                        }
                    });
                } else {
                    mHeadRight.setTag(0);
                    adapter.setEditEnabled(false);
                    adapter.notifyDataSetChanged();
                    mHeadRight.setText(R.string.edit);
                    btnDelGroup.setVisibility(View.GONE);
                    if (mUploadImg != null) {
                        addGroup();
                    }
                }
                break;
            case R.id.ll_group_layout:
                Intent intent = new Intent(this, GroupMemberListActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("group_info", info);
                intent.putExtras(args);
                startActivity(intent);
                break;
            case R.id.btn_exit_group:
                new AlertDialog(this)
                        .builder()
                        .setMsg("确认退出群" + info.Name)
                        .setNegativeButton(getString(R.string.cancel),
                                new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                        .setPositiveButton(getString(R.string.ok),
                                new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        delGroupRoles(info.ID,
                                                PrefsUtil.readUserInfo().ID);
                                    }
                                }).show();
                break;
            case R.id.file_relation:
                relationIntent = new Intent(this, RelationFileActivity.class);
                break;
            case R.id.task_relation:
                relationIntent = new Intent(this, RelationTaskActivity.class);
                break;
            case R.id.work_project_relation:
                relationIntent = new Intent(this, RelationProjectActivity.class);
                break;
            case R.id.calendar_relation:
                relationIntent = new Intent(this, RelationCalendarActivity.class);

                break;
            default:
                break;
        }
        if (relationIntent != null) {
            relationIntent.putExtra("group_info", info);
            startActivity(relationIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            sUsers = (ArrayList<UserInfo>) data
                    .getSerializableExtra("select_list");
            // sUser = (UserInfo) data.getSerializableExtra("select_user");
            for (int i = 0; i < sUsers.size(); i++) {
                userids.add(sUsers.get(i).ID);
            }
            // int id = sUser.ID;
            boolean isAddIn = false;
            for (int i = 0; i < noteRoles.size(); i++) {
                for (int j = 0; j < sUsers.size(); j++) {
                    if (noteRoles.get(i).ID == sUsers.get(j).ID) {
                        ToastUtil.showToast(this,
                                R.string.groupinto_joinexist_tips);
                        // Toast.makeText(this, "成员已加入",
                        // Toast.LENGTH_SHORT).show();
                        isAddIn = false;
                        // break;
                        return;
                    } else {
                        isAddIn = true;
                    }
                }
            }
            if (isAddIn) {
                getGroupInfo(userids);
            }
        }
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "zkbr.png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            uploadImage(uri.getPath(), uri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据群组ID获取群组相关的数量
     */
    private void getRelationCount() {
        API.TalkerAPI.GetMyTalkerCountByGroupId(info.ID,
                new RequestCallback<Integer>(Integer.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(GroupIntoActivity.this,
                                R.string.groupinto_relationcount_error);
                    }

                    @Override
                    public void onParseSuccess(List<Integer> respList) {
                        if (respList != null && respList.size() >= 4) {
                            if (respList.get(0) > 0) {
                                projectNum.setVisibility(View.VISIBLE);
                                projectNum.setText(respList.get(0) + "");
                            }
                            if (respList.get(1) > 0) {
                                taskNum.setVisibility(View.VISIBLE);
                                taskNum.setText(respList.get(1) + "");
                            }
                            if (respList.get(2) > 0) {
                                calendarNum.setVisibility(View.VISIBLE);
                                calendarNum.setText(respList.get(2) + "");
                            }
                            if (respList.get(3) > 0) {
                                fileNum.setVisibility(View.VISIBLE);
                                fileNum.setText(respList.get(3) - 1 + "");
                            }
                        } else {
                            ToastUtil.showToast(GroupIntoActivity.this,
                                    R.string.groupinto_relationcount_error);
                        }
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
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(GroupIntoActivity.this,
                        R.string.groupinto_exit_error);
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(info.ID);
                    ToastUtil.showToast(GroupIntoActivity.this,
                            R.string.groupinto_exit_success);
                    for (int i = 0, size = noteRoles.size(); i < size; i++) {
                        if (noteRoles.get(i).ID == PrefsUtil.readUserInfo().ID) {
                            noteRoles.remove(i);
                            break;
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction(GroupIntoActivity.ACTION_REFRESH_COUNT_GROUP);
                    GroupIntoActivity.this.sendBroadcast(intent);
                    Intent intentExit = new Intent();
                    intentExit.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intentExit);
                    adapter.notifyDataSetChanged();
                    onBackPressed();
                } else {
                    ToastUtil.showToast(GroupIntoActivity.this,
                            R.string.groupinto_exit_error);
                }
            }
        });
    }

    private void delGroup(int gid) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(GroupIntoActivity.this,
                        R.string.groupinto_delete_error);
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(info.ID);
                    ToastUtil.showToast(GroupIntoActivity.this,
                            R.string.groupinto_delete_success);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    onBackPressed();
                } else {
                    ToastUtil.showToast(GroupIntoActivity.this,
                            R.string.groupinto_delete_error);
                }
            }
        });

    }

    private void delete(int sendID) {
        API.Message.RemoveChatRecord(sendID, new RequestCallback<String>(
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
     * 获取群成员信息
     *
     * @param gid
     */
    private void getGroupMember(int gid) {
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
            }

            @Override
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                try {
                    Gson gson = new Gson();
                    noteRoles = new ArrayList<UserInfo>();
                    JsonArray array = new JsonParser().parse(result)
                            .getAsJsonArray();
                    for (JsonElement jsonElement : array) {
                        UserInfo noteRole = gson.fromJson(jsonElement,
                                UserInfo.class);
                        noteRoles.add(noteRole);
                    }
                    adapter.setData(noteRoles);
                    adapter.notifyDataSetChanged();
                    tvNumber.setText("群组成员（" + adapter.getMemberCount() + "）");
                } catch (Exception e) {
                    Log.d("zrjt", e.toString());
                }
            }
        });
    }

    /**
     * 加入群组
     *
     * @param userids
     */
    private void getGroupInfo(List<Integer> userids) {
        API.TalkerAPI.AddGroupUsers(info.ID, userids,
                new RequestCallback<String>(String.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(GroupIntoActivity.this,
                                R.string.groupinto_join_error);
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isDigitsOnly(result)
                                && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(GroupIntoActivity.this,
                                    R.string.groupinto_join_success);
                            getGroupMember(info.ID);
                            Intent intent = new Intent(
                                    GroupFragment.ACTION_REFRESH_GROUP);
                            sendBroadcast(intent); // 刷新群组列表
                        } else {
                            ToastUtil.showToast(GroupIntoActivity.this,
                                    R.string.groupinto_join_error);
                        }
                    }
                });
    }

    /**
     * 群组头像上传
     *
     * @param path
     * @param uri
     */
    private void uploadImage(String path, final Uri uri) {
        final Dialog mDialog = createLoadingDialog(R.string.loading_dialog_tips4);
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                ToastUtil.showToast(GroupIntoActivity.this,
                        R.string.headupload_error);
            }

            @Override
            public void onStarted() {
                if (mDialog != null) {
                    mDialog.show();
                }
            }

            @Override
            public void onParseSuccess(List<String> result) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                if (result != null && result.size() > 0) {
                    mUploadImg = result.get(0);
                    mCircleImageView.setImageBitmap(BitmapFactory
                            .decodeFile(new File(uri.getPath()).getPath()));
                } else {
                    ToastUtil.showToast(GroupIntoActivity.this,
                            R.string.headupload_error);
                }
            }
        });
    }

    private void addGroup() {
        info.Image = mUploadImg;
        List<Integer> groupUsers = new ArrayList<>();
        ArrayList<ApiEntity.UserInfo> userInfos = new ArrayList<>();
        userInfos = (ArrayList<ApiEntity.UserInfo>) info.Users;
        for (int i = 0; i < userInfos.size(); i++) {
            groupUsers.add(Integer.valueOf(userInfos.get(i).ID));
        }
        if (userids != null && userids.size() != 0) {
            groupUsers.addAll(userids);
        }
        API.TalkerAPI.SaveChatterGroup(info, groupUsers,
                new RequestCallback<String>(String.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        ToastUtil.showToast(GroupIntoActivity.this,
                                R.string.modify_fail);
                    }

                    @Override
                    public void onStarted() {
                        if (mLoadingDialog != null)
                            mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isDigitsOnly(result)
                                && Integer.valueOf(result) > 0) {
                            Intent intent = new Intent(
                                    GroupFragment.ACTION_REFRESH_GROUP);
                            sendBroadcast(intent); // 刷群主列表
                            GroupIntoActivity.this.finish();
                        } else {
                            ToastUtil.showToast(GroupIntoActivity.this,
                                    R.string.modify_fail);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
