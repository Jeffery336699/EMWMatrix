package cc.emw.mobile.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.farsunset.cim.client.model.Message;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatInfoRecyclerViewAdapter;
import cc.emw.mobile.chat.adapter.ChatTeamUserInfoAdapter;
import cc.emw.mobile.chat.view.MyCollapsingToolbarLayout;
import cc.emw.mobile.chat.view.MyRecyclerView;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;

public class ChatTeamInfoActivity3 extends BaseActivity {

    private static final int REQUEST_CAPTURE_PHOTO = 1000;
    private MyRecyclerView myRecyclerView;
    private MyCollapsingToolbarLayout mtoolBarlayout;
    private Toolbar mToolbar;
    private ImageView imageView;
    private IconTextView exit, edit;
    private EditText editText;
    private View underLine;
    private TextView tvSave;
    private GroupInfo groupInfo;
    private ArrayList<Message> msgList;
    private View dialogBg;
    private DisplayImageOptions optionsBg;
    private Uri uri;
    private String imgName;
    private ChatInfoRecyclerViewAdapter chatInfoRecyclerViewAdapter;
    private List<UserInfo> sUsers;
    private List<ApiEntity.UserInfo> userInfos = new ArrayList<>();
    private List<Integer> userids = new ArrayList<Integer>();
    private boolean isOpenKeyBoard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_chat_team_info3);
        initData();
        init();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
        /**
         * 初始化媒体数据
         */
        msgList = (ArrayList<Message>) getIntent().getSerializableExtra("msg_player");

        if (msgList != null) {
            Collections.reverse(msgList);
        }

        sUsers = new ArrayList<>();

        if (groupInfo != null && groupInfo.Users != null) {
            userInfos = groupInfo.Users;
            for (int i = 0; i < groupInfo.Users.size(); i++) {
                userids.add(groupInfo.Users.get(i).ID);
            }
        }

        optionsBg = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray_1) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.color.gray_1) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.color.gray_1) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

    }

    private void init() {
        myRecyclerView = (MyRecyclerView) findViewById(R.id.recyclerView);
        mtoolBarlayout = (MyCollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        exit = (IconTextView) findViewById(R.id.tv_group_exit);
        edit = (IconTextView) findViewById(R.id.itv_group_edit);
        tvSave = (TextView) findViewById(R.id.tv_group_save);
        editText = (EditText) findViewById(R.id.etv_group_name);
        underLine = findViewById(R.id.view_group_name_line);
        dialogBg = findViewById(R.id.view_dialog_bg);

        String uriHead;
        if (groupInfo.Image != null && !TextUtils.isEmpty(groupInfo.Image)) {
            uriHead = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                    groupInfo.Image);
        } else {
            switch (groupInfo.BackImageIndex) {
                case 1:
                    uriHead = "drawable://" + R.drawable.group1;
                    break;
                case 2:
                    uriHead = "drawable://" + R.drawable.group2;
                    break;
                case 3:
                    uriHead = "drawable://" + R.drawable.group3;
                    break;
                case 4:
                    uriHead = "drawable://" + R.drawable.group4;
                    break;
                case 5:
                    uriHead = "drawable://" + R.drawable.group5;
                    break;
                case 6:
                    uriHead = "drawable://" + R.drawable.group6;
                    break;
                case 7:
                    uriHead = "drawable://" + R.drawable.group7;
                    break;
                case 8:
                    uriHead = "drawable://" + R.drawable.group8;
                    break;
                case 9:
                    uriHead = "drawable://" + R.drawable.group9;
                    break;
                case 10:
                    uriHead = "drawable://" + R.drawable.group10;
                    break;
                case 11:
                    uriHead = "drawable://" + R.drawable.group11;
                    break;
                case 12:
                    uriHead = "drawable://" + R.drawable.group12;
                    break;
                case 13:
                    uriHead = "drawable://" + R.drawable.group13;
                    break;
                case 14:
                    uriHead = "drawable://" + R.drawable.group14;
                    break;
                case 15:
                    uriHead = "drawable://" + R.drawable.group15;
                    break;
                default:
                    uriHead = "drawable://";
                    break;
            }
        }
        ImageLoader.getInstance().displayImage(uriHead, imageView, optionsBg);
        mtoolBarlayout.setTitle(groupInfo.Name);// 设置标题
        editText.setText(groupInfo.Name);
        mtoolBarlayout.setExpandedTitleColor(Color.BLACK);// 设置还没收缩时状态下字体颜色
        mtoolBarlayout.setCollapsedTitleTextColor(Color.BLACK);// 设置收缩后Toolbar上字体的颜色

        edit.setTag(0);
        edit.setVisibility(groupInfo.CreateUser == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
        edit.setTag(groupInfo.CreateUser == PrefsUtil.readUserInfo().ID ? 1 : 0);

        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getGroupInfo();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getTag().equals(1) && dialogBg.getVisibility() == View.VISIBLE) {
                    ActionSheetDialog dialog = new ActionSheetDialog(
                            ChatTeamInfoActivity3.this).builder();
                    dialog.addSheetItem(getString(R.string.actionsheet_photo), null,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(
                                                    EMWApplication.tempPath
                                                            + "tempraw.png")));
                                    startActivityForResult(intent,
                                            REQUEST_CAPTURE_PHOTO);
                                }
                            });
                    dialog.addSheetItem(getString(R.string.actionsheet_pick), null,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Crop.pickImage(ChatTeamInfoActivity3.this);
                                }
                            });
                    dialog.show();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setTag(1);
                mtoolBarlayout.setTitle("");
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                editText.setSelection(editText.getText().toString().length());
                edit.setVisibility(View.GONE);
                tvSave.setVisibility(View.VISIBLE);
                underLine.setVisibility(View.VISIBLE);
                dialogBg.setVisibility(View.VISIBLE);
                KeyBoardUtil.openOrCloseSoftInput(ChatTeamInfoActivity3.this);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtoolBarlayout.setTitle(groupInfo.Name);
                editText.setVisibility(View.GONE);
                tvSave.setVisibility(View.GONE);
                underLine.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                dialogBg.setVisibility(View.GONE);
                String newName = editText.getText().toString();
                if (groupInfo.Name.equals(newName) && uri == null) {
                    ToastUtil.showToast(ChatTeamInfoActivity3.this,
                            "编辑成功", R.drawable.tishi_ico_gougou);
                } else if (uri != null) {
                    uploadImage(uri.getPath(), uri);
                } else {
                    saveGroupInfo(groupInfo.Image, userids);
                }
                KeyBoardUtil.closeKeyboard(ChatTeamInfoActivity3.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            sUsers.clear();
            sUsers = (ArrayList<UserInfo>) data
                    .getSerializableExtra("select_list");
            if (sUsers.size() > 0) {
                userids.clear();
                for (int i = 0; i < sUsers.size(); i++) {
                    userids.add(sUsers.get(i).ID);
                }
                ChatTeamUserInfoAdapter chatTeamUserInfoAdapter = chatInfoRecyclerViewAdapter.getChatTeamUserInfoAdapter();
                chatTeamUserInfoAdapter.setData(sUsers);
                chatTeamUserInfoAdapter.notifyDataSetChanged();
                chatInfoRecyclerViewAdapter.getMemberNumTv().setText("成员(" + sUsers.size() + "人)");
                chatInfoRecyclerViewAdapter.setNoteRoles((ArrayList<UserInfo>) sUsers);
                chatInfoRecyclerViewAdapter.notifyDataSetChanged();
                saveGroupInfo(groupInfo.Image, userids);
            }
        }
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData()); // 开始裁剪
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data); // 裁剪完成
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped.png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            uri = Crop.getOutput(result);
            imageView.setImageURI(uri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil.showToast(this, Crop.getError(result).getMessage());
        }
    }

    /**
     * 保存群组
     */
    private void saveGroupInfo(final String image, List<Integer> userIds) {
        groupInfo.Name = editText.getText().toString();
        groupInfo.Image = image;
        groupInfo.Users = userInfos;
        if (!TextUtils.isEmpty(groupInfo.Name)) {
            API.TalkerAPI.SaveChatterGroup(groupInfo, userIds,
                    new RequestCallback<String>(String.class) {

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            ToastUtil.showToast(ChatTeamInfoActivity3.this, "编辑失败,服务器异常");
                        }

                        @Override
                        public void onSuccess(String result) {
                            if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                                ToastUtil.showToast(ChatTeamInfoActivity3.this,
                                        "编辑成功", R.drawable.tishi_ico_gougou);
                                Intent intent = new Intent(
                                        GroupFragment.ACTION_REFRESH_GROUP);
                                sendBroadcast(intent); // 刷群主列表
                                mtoolBarlayout.setTitle(editText.getText().toString());
                                groupInfo.Name = editText.getText().toString();
                                groupInfo.Image = image;
                                EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                            } else {
                                ToastUtil.showToast(ChatTeamInfoActivity3.this, "编辑失败,请稍候再试");
                            }
                        }
                    });
        } else {
            ToastUtil.showToast(ChatTeamInfoActivity3.this, "圈子名称不能为空");
        }
    }

    /**
     * 群组头像上传
     *
     * @param path
     * @param uri
     */
    private void uploadImage(String path, final Uri uri) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(ChatTeamInfoActivity3.this,
                        R.string.headupload_error);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onParseSuccess(List<String> result) {
                if (result != null && result.size() > 0) {
                    String imgTargetStr = result.toString();
                    int i = imgTargetStr.lastIndexOf("/");
                    imgName = imgTargetStr.substring(i + 1,
                            imgTargetStr.length() - 1);
                    saveGroupInfo(imgName, userids);
                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity3.this,
                            R.string.headupload_error);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dialogBg.getVisibility() == View.VISIBLE) {
            mtoolBarlayout.setTitle(groupInfo.Name);
            editText.setVisibility(View.GONE);
            tvSave.setVisibility(View.GONE);
            underLine.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            dialogBg.setVisibility(View.GONE);
            KeyBoardUtil.closeKeyboard(ChatTeamInfoActivity3.this);
        } else {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /**
     * 获取群组相关文件
     */
    private void getGroupRelationFile(String groupId, int pages, int size) {
        API.Message.GetGroupFileMessages(groupId, pages, size, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                //无数据
                if (respList == null || respList.size() == 0) {

                } else {
                    for (ApiEntity.Files file : respList) {
                        if (file.Type == 7 || file.Type == 9) {  //视频图片
                            Message message = new Message();
                            message.setContent(file.Content);
                            message.setType(file.Type);
                            msgList.add(message);
                        }
                    }
                    if (msgList.size() > 0)
                        chatInfoRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ChatTeamInfoActivity3.this, R.string.relationfile_list_error);
            }
        });
    }

    public void getGroupInfo() {
        API.TalkerAPI.GetChatterGroupById(groupInfo.ID, new RequestCallback<GroupInfo>(GroupInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ChatTeamInfoActivity3.this, "获取群消息失败，请稍后重试");
            }

            @Override
            public void onParseSuccess(GroupInfo respInfo) {
                if (respInfo != null) {
                    chatInfoRecyclerViewAdapter = new ChatInfoRecyclerViewAdapter(ChatTeamInfoActivity3.this, respInfo, msgList, null);
                    myRecyclerView.setAdapter(chatInfoRecyclerViewAdapter);
                    if (msgList == null) {
                        msgList = new ArrayList<>();
                        getGroupRelationFile(groupInfo.ID + "", 1, 100);
                    }
                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity3.this, "获取群消息失败，请稍后重试");
                }
            }
        });
    }
}
