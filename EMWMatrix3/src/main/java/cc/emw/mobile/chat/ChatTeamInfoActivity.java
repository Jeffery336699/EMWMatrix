package cc.emw.mobile.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brucetoo.imagebrowse.widget.PhotoView;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatTeamUserInfoAdapter;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.chat.utils.NativePicUtil;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SwitchButton;
import cc.emw.mobile.view.VideoPlayerView;

import static cc.emw.mobile.chat.base.ChatContent.REQUEST_CAPTURE_PHOTO;

/**
 * Created by sunny.du on 2016/10/15.
 * 展示群组详细信息的页面
 */
public class ChatTeamInfoActivity extends BaseActivity {
    private PhotoView mIvGroupImage;
    private IconTextView mTvGroupBg;
    private TextView mTvGroupText;
    private IconTextView mItvGroupEdit;
    private TextView mTvGriyoSaveUser;
    private EditText mEtGroupName;
    private View mViewLine;
    private IconTextView mItvGroupCamera;
    private RelativeLayout mRlGroupSeekbarBut;
    private SwitchButton mSbGroupShow;
    private TextView mTvGtoupPalyerCount;
    private TextView mTvGroupUserCount;
    private LinearLayout mItvGroupAddUsers;
    private ListView mLvGroupUser;
    private GroupInfo groupInfo;    //群信息实体类
    private ImageView mIvGroupNewImage2;
    private Dialog mLoadingDialog; // 加载框
    private ArrayList<UserInfo> noteRoles;
    private ChatTeamUserInfoAdapter mChatTeamUserInfoAdapter;
    private List<Integer> userids;//群组用户ID保存
    private ArrayList<UserInfo> sUsers;
    private List<ApiEntity.Files> playerList;//储存多媒体信息实体
    private GridView mGvChatPlayer;
    private boolean isCreateUser;
    private GrideAdapter mGrideAdapter;
    private ArrayList<Message> msgList;
    private LinearLayout mLlChatTeamInfoShow;
    private RelativeLayout mRlChatTeamPlayerShow;

    /***********
     * 初始化
     *****************************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_team_info);
        initData();
        initView();
        initEvent();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
        /**
         * 初始化媒体数据
         */
        msgList = (ArrayList<Message>) getIntent().getSerializableExtra("msg_player");


        /**
         * 判断是否是管理员
         */
        if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
            isCreateUser = true;
        } else {
            isCreateUser = false;
        }
        /**
         * 初始化数据集合
         */
        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();
        playerList = new ArrayList<>();
    }


    private void initView() {
        mIvGroupImage = (PhotoView) findViewById(R.id.iv_group_new_image);
        mTvGroupBg = (IconTextView) findViewById(R.id.tv_group_bg);
        mTvGroupText = (TextView) findViewById(R.id.tv_group_new_text);
        mItvGroupEdit = (IconTextView) findViewById(R.id.itv_group_new_edit);
        mTvGriyoSaveUser = (TextView) findViewById(R.id.tv_group_new_save);
        mViewLine = findViewById(R.id.view_group_new_line);
        mItvGroupCamera = (IconTextView) findViewById(R.id.itv_group_new_camera);
        mRlGroupSeekbarBut = (RelativeLayout) findViewById(R.id.rl_group_new_seebar_but);
        mSbGroupShow = (SwitchButton) findViewById(R.id.sb_group_new_but);
        mTvGtoupPalyerCount = (TextView) findViewById(R.id.tv_group_new_palyer_count);
        mTvGroupUserCount = (TextView) findViewById(R.id.tv_group_new_user_count);
        mLvGroupUser = (ListView) findViewById(R.id.lv_group_user);
        mEtGroupName = (EditText) findViewById(R.id.tv_group_name_show);
        mGvChatPlayer = (GridView) findViewById(R.id.gd_chat_player);
        mLlChatTeamInfoShow = (LinearLayout) findViewById(R.id.ll_chat_team_info_show);
        mRlChatTeamPlayerShow = (RelativeLayout) findViewById(R.id.rl_team_player_show);
        mIvGroupNewImage2 = (ImageView) findViewById(R.id.iv_group_new_image2);
        //初始化页面  隐藏编辑页面
        mTvGroupText.setVisibility(View.GONE);
        mTvGriyoSaveUser.setVisibility(View.GONE);
        mItvGroupCamera.setVisibility(View.GONE);
        mViewLine.setVisibility(View.GONE);
        mEtGroupName.setEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /**
         * 添加成员
         */
        View headerView = View.inflate(this, R.layout.item_chat_team_add_user, null);
        mLvGroupUser.addHeaderView(headerView);
        mItvGroupAddUsers = (LinearLayout) headerView.findViewById(R.id.itv_group_add_user);
        if (isCreateUser) {
            mItvGroupAddUsers.setVisibility(View.VISIBLE);
            mItvGroupEdit.setVisibility(View.VISIBLE);
        } else {
            mItvGroupAddUsers.setVisibility(View.GONE);
            mItvGroupEdit.setVisibility(View.GONE);
        }
        /**
         * 数据绑定视图
         */
        boundView();
    }

    /**
     * 数据绑定视图
     */
    private void boundView() {
        DisplayImageOptions optiones = ImageLoadFactory.getChatPlayerOption();//图片load
        DisplayImageOptions optiones2 = ImageLoadFactory.getChatTeamTitleOptiones();//图片load
        mIvGroupImage.disenable();
        mIvGroupImage.setTag(0);
        if (groupInfo.Image != null && !("".equals(groupInfo.Image))) {
            String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, groupInfo.Image);
            mIvGroupNewImage2.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(imageuri, mIvGroupImage, optiones);
        } else if (groupInfo.BackImageIndex != 0) {
            mIvGroupImage.setVisibility(View.INVISIBLE);
            ImageLoader.getInstance().displayImage(NativePicUtil.getPic(groupInfo.BackImageIndex), mIvGroupNewImage2, optiones2);
        }
        mEtGroupName.setText(groupInfo.Name);
        /**
         * 设置是否公开
         */
        if (groupInfo.Type == 0) {
            if (!mSbGroupShow.isChecked()) {
                mSbGroupShow.toggle();
            }
        }
        /**
         * 获取群组成员已经绑定listView
         */
        mChatTeamUserInfoAdapter = new ChatTeamUserInfoAdapter(this);
        mLvGroupUser.setAdapter(mChatTeamUserInfoAdapter);
        getGroupMember(groupInfo.ID);
        /**
         * 绑定多媒体信息
         */
        if (msgList == null || msgList.size() == 0) {
        } else {
            mGrideAdapter = new GrideAdapter(msgList);
            mGvChatPlayer.setAdapter(mGrideAdapter);
            if (playerList.size() > 4) {
                mTvGtoupPalyerCount.setText("+" + (msgList.size() - 4));
            }
        }
    }

    /**
     * 显示软键盘
     */
    protected void showInputMethod() {
        View view = this.getCurrentFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    protected void hideInputMethod() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initEvent() {
        /**
         * 进入编辑模式
         */
        mItvGroupEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.argb(80, 0, 0, 0); // 第一个值为透明度
                mLlChatTeamInfoShow.setBackgroundColor(color);
                mItvGroupEdit.setVisibility(View.GONE);
                mTvGroupText.setVisibility(View.VISIBLE);
                mTvGriyoSaveUser.setVisibility(View.VISIBLE);
                mItvGroupCamera.setVisibility(View.VISIBLE);
                mViewLine.setVisibility(View.VISIBLE);
                mEtGroupName.setEnabled(true);
                mEtGroupName.requestFocus();
                mEtGroupName.setSelection(groupInfo.Name.length());
                showInputMethod();
            }
        });
        /**
         * 保存编辑模式
         */
        mTvGriyoSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvGriyoSaveUser.setVisibility(View.GONE);
                mTvGroupText.setVisibility(View.GONE);
                mItvGroupCamera.setVisibility(View.GONE);
                mViewLine.setVisibility(View.GONE);
                mItvGroupEdit.setVisibility(View.VISIBLE);
                if (null == mEtGroupName.getText() || "".equals(mEtGroupName.getText().toString())) {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "圈子名称不能为空");
                } else {
                    int color = Color.argb(0, 255, 0, 255); // 第一个值为透明度
                    mLlChatTeamInfoShow.setBackgroundColor(color);
                    mEtGroupName.setEnabled(false);
                    groupInfo.Name = mEtGroupName.getText().toString();
                    amendGroupInfo();
                    hideInputMethod();
                }
            }
        });
        /**
         * 是否公开群组
         */
        mRlGroupSeekbarBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCreateUser) {
                    int type = groupInfo.Type;
                    if (type == 0) {
                        groupInfo.Type = 1;
                    } else if (type == 1) {
                        groupInfo.Type = 0;
                    }
                    amendGroupisSee();
                }
            }
        });
        /**
         * 成员列表详细信息查看监听
         */
        mLvGroupUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    /**
                     * 添加群组成员事件监听  跳转人员选择列表
                     */
                    Intent intent = new Intent(ChatTeamInfoActivity.this, ContactSelectActivity.class);
                    intent.putExtra("select_list", (Serializable) noteRoles);
                    intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    intent.putExtra("click_pos_y", location[1]);
                    ChatTeamInfoActivity.this.startActivityForResult(intent, 1);
                } else {
                    if (noteRoles.get(position - 1) != null) {
                        Intent intent = new Intent(ChatTeamInfoActivity.this, PersonInfoActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("UserInfo", noteRoles.get(position - 1));
                        intent.putExtra("intoTag", 1);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        view.getLocationInWindow(location);
                        intent.putExtra("click_pos_y", location[1]);
                        intent.putExtras(args);
                        ChatTeamInfoActivity.this.startActivity(intent);
                    }
                }
            }
        });
        /**
         * 设置群组图片监听
         */
        mItvGroupCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSheetDialog dialog = new ActionSheetDialog(ChatTeamInfoActivity.this).builder();
                dialog.addSheetItem(getString(R.string.actionsheet_photo), null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(EMWApplication.tempPath + "tempraw.png")));
                        startActivityForResult(intent, ChatContent.REQUEST_CAPTURE_PHOTO);
                    }
                });
                dialog.addSheetItem(getString(R.string.actionsheet_pick), null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Crop.pickImage(ChatTeamInfoActivity.this);
                            }
                        });
                dialog.show();
            }
        });
        mTvGroupBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputMethod();
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
                mTvGroupUserCount.setText("成员(" + userids.size() + "人)");
                mChatTeamUserInfoAdapter.setData(sUsers);
                mChatTeamUserInfoAdapter.notifyDataSetChanged();
            }
        }
        /**
         * 返回图片选择
         */
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath + "tempcrop.png"));
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
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    /**********
     * 底层接口
     *****************************************************************************************************************************/
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
                ToastUtil.showToast(ChatTeamInfoActivity.this, R.string.headupload_error);
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
                    /**
                     * 设置图片  平铺模式
                     */
                    if (groupInfo.Image != null && !("".equals(groupInfo.Image))) {
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(uri.getPath()).getPath());
                        BitmapDrawable bd = new BitmapDrawable(bitmap);
                        bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                        bd.setDither(true);
                        mIvGroupImage.setImageBitmap(bitmap);
                    }
                    String imgTargetStr = result.toString();
                    int i = imgTargetStr.lastIndexOf("/");
                    String imgName = imgTargetStr.substring(i + 1, imgTargetStr.length() - 1);
                    groupInfo.Image = imgName;
                    //刷新缓存
                    EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    /**
                     * 发送广播
                     */
                    //刷新对话列表信息
//                    Intent intent = new Intent(ChatHistoryFragments.ACTION_CHAT);
//                    sendBroadcast(intent);
                    //刷新对话框页面数据
                    Intent intentChat = new Intent(ChatContent.REFRESH_CHAT_TEAM_INFO);
                    sendBroadcast(intentChat);
                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, R.string.headupload_error);
                }
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
                ToastUtil.showToast(ChatTeamInfoActivity.this, "编辑失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "编辑成功", R.drawable.tishi_ico_gougou);
                    //刷新缓存
                    EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    // 刷群主列表
                    Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    getGroupMember(groupInfo.ID);
                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "编辑失败,请稍候再试");
                }
            }
        });
    }

    /**
     * 更改群可见状态
     */
    private void amendGroupisSee() {
        API.TalkerAPI.SaveChatterGroup(groupInfo, userids, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(ChatTeamInfoActivity.this, "更改群状态失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "群状态设置成功", R.drawable.tishi_ico_gougou);
                    //刷新缓存
                    EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    //刷新对话列表信息
//                    Intent intent = new Intent(ChatHistoryFragments.ACTION_CHAT);
//                    sendBroadcast(intent);
                    //刷新对话框页面数据
                    Intent intentChat = new Intent(ChatContent.REFRESH_CHAT_TEAM_INFO);
                    sendBroadcast(intentChat);
                    mSbGroupShow.toggle();


                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "更改群状态失败,请稍候再试");
                }
            }
        });
    }

    /**
     * 群信息编辑
     */
    private void amendGroupInfo() {
        API.TalkerAPI.SaveChatterGroup(groupInfo, userids, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(ChatTeamInfoActivity.this, "更改群信息失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "更改群信息成功", R.drawable.tishi_ico_gougou);
                    //刷新缓存
                    EMWApplication.groupMap.put(groupInfo.ID, groupInfo);
                    //刷新对话列表信息
//                    Intent intent = new Intent(ChatHistoryFragments.ACTION_CHAT);
//                    sendBroadcast(intent);
                    //刷新对话框页面数据
                    Intent intentChat = new Intent(ChatContent.REFRESH_CHAT_TEAM_INFO);
                    sendBroadcast(intentChat);

                } else {
                    ToastUtil.showToast(ChatTeamInfoActivity.this, "更改群信息失败,请稍候再试");
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
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
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
                    mChatTeamUserInfoAdapter.setData(sUsers);
                    mChatTeamUserInfoAdapter.notifyDataSetChanged();
                    mTvGroupUserCount.setText("成员(" + respList.size() + "人)");
                } else {
                    mTvGroupUserCount.setText("成员(" + 0 + "人)");
                }
            }

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
        });
    }


    /**********
     * / * 设置多媒体信息适配器
     * /
     *****************************************************************************************************************************/
    public class GrideAdapter extends BaseAdapter {
        ArrayList<Message> imagelist;
        boolean flag = false;
        int widthPixels = DisplayUtil.getDisplayWidth(ChatTeamInfoActivity.this);
        int meanWidth = widthPixels / 4;

        public GrideAdapter(ArrayList<Message> imagelist) {
            this.imagelist = imagelist;
            if (imagelist != null && imagelist.size() != 0) {
                flag = true;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(ChatTeamInfoActivity.this, R.layout.item_chat_team_player, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ViewGroup.LayoutParams params = holder.ivChatPlayerImage.getLayoutParams();
            params.width = meanWidth;
            holder.ivChatPlayerImage.setLayoutParams(params);
            ViewGroup.LayoutParams params2 = holder.vpvChatPlayer.getLayoutParams();
            params2.width = meanWidth;
            holder.vpvChatPlayer.setLayoutParams(params2);
            if (flag && position < 4) {
                if (imagelist.get(position).getType() == 7) {//过滤信息  设置图片
                    final Files image = new Gson().fromJson(imagelist.get(position).getContent(), Files.class);
                    holder.ivChatPlayerImage.setVisibility(View.VISIBLE);
                    holder.rlChatPlayer.setVisibility(View.GONE);
                    String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image.Url);
                    DisplayImageOptions optiones = ImageLoadFactory.getChatPlayerOption();//图片load
                    ImageLoader.getInstance().displayImage(imageuri, holder.ivChatPlayerImage, optiones);
                    holder.ivChatPlayerImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatTeamInfoActivity.this, ChatTeamPlayerViewPagerActivity.class);
                            intent.putExtra("videos", imagelist);
                            intent.putExtra("count", position);
                            ChatTeamInfoActivity.this.startActivity(intent);
                        }
                    });
                } else if (imagelist.get(position).getType() == 9) {

                    final Files video = new Gson().fromJson(imagelist.get(position).getContent(), Files.class);
                    holder.ivChatPlayerImage.setVisibility(View.GONE);
                    holder.rlChatPlayer.setVisibility(View.VISIBLE);
                    //跳转视频播放页面
                    holder.rlChatPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO   错误逻辑     viewpager传值需要修改
                            Intent intent = new Intent(ChatTeamInfoActivity.this, ChatTeamPlayerViewPagerActivity.class);
                            intent.putExtra("videos", imagelist);
                            intent.putExtra("count", position);
                            ChatTeamInfoActivity.this.startActivity(intent);
                        }
                    });
                }
            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (imagelist.size() >= 4) {
                return 4;
            } else {
                return imagelist.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (imagelist.size() != 0 && imagelist != null) {
                if (position < 4) {
                    return imagelist.get(position);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (position < 4) {
                return position;
            }
            return 0;
        }
    }

    public class ViewHolder {
        public PhotoView ivChatPlayerImage;
        public RelativeLayout rlChatPlayer;
        public VideoPlayerView vpvChatPlayer;
        public ImageView ivChatPlayerButton;

        public ViewHolder(View view) {
            ivChatPlayerImage = (PhotoView) view.findViewById(R.id.iv_group_new_image);
            rlChatPlayer = (RelativeLayout) view.findViewById(R.id.rl_chat_team_player);
//            vpvChatPlayer = (VideoPlayerView) view.findViewById(R.id.vpv_chat_team_player);
            ivChatPlayerButton = (ImageView) view.findViewById(R.id.iv_chat_team_player_but);
        }
    }
}