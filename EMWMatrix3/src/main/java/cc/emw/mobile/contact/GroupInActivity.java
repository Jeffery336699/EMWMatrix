package cc.emw.mobile.contact;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.androidcrash.util.AppManager;
import com.mingle.headsUp.HeadsUpManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.RelationFileActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.adapter.GroupEditAdapter;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.PhotoActivity;
import cc.emw.mobile.map.GroupPersonsActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedColorPickerDialog;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyGridView;
import cc.emw.mobile.view.SegmentedGroup;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_group_in)
public class GroupInActivity extends BaseActivity {
    //    @ViewInject(R.id.cm_header_tv_title)
//    private TextView mTitle;
    @ViewInject(R.id.rb_suggest)
    private RadioButton mPublic;
    @ViewInject(R.id.rb_bug)
    private RadioButton mPrivate;
    @ViewInject(R.id.cm_header_btn_more)
    private ImageButton mEditBtn;
    @ViewInject(R.id.cIv_group_img)
    private CircleImageView gCircleImg;
    @ViewInject(R.id.tv_group_name)
    private EditText gName;
    @ViewInject(R.id.tv_exit_group)
    private TextView exit;  // 退出群组
    @ViewInject(R.id.btn_del_group)
    private TextView btnDelGroup; // 解散群组
    @ViewInject(R.id.tv_contact_group_member_num)
    private TextView memberNum;    //成员的数量
    @ViewInject(R.id.group_grid_view_member)
    private MyGridView gridView;
    @ViewInject(R.id.iv_group_member)
    private MyGridView managerGridView;
    @ViewInject(R.id.iv_group_creator)
    private CircleImageView ownCircleImg;
    @ViewInject(R.id.tv_group_manager_name)
    private TextView ownName;
    @ViewInject(R.id.ll_group_in_manager)
    private LinearLayout lLayoutGManager;
    @ViewInject(R.id.tv_person_chat)
    private LinearLayout tvChatBtn; //聊天按钮
    @ViewInject(R.id.ll_group_in_relation_file)
    private LinearLayout lLGroupRelationFile;
    @ViewInject(R.id.segmented_plan_type)
    private SegmentedGroup mRadioGroup; // 类型RadioGroup
    @ViewInject(R.id.cm_header_tv_right9)
    private IconTextView rightIcon;
    @ViewInject(R.id.ll_calendar_color)
    private LinearLayout mColorLayout;
    @ViewInject(R.id.img_calendar_color_select)
    private ImageView mTvColorSelect;    //颜色
    @ViewInject(R.id.ll_group_in_nearby)
    private LinearLayout nearbyLayout;
    @ViewInject(R.id.root_view)
    private LinearLayout mRootView;
    @ViewInject(R.id.itv_group_head_img_select)
    private IconTextView tvCamera;

    public static final String ACTION_REFRESH_COUNT_GROUP = "cc.emw.mobile.refresh.count";
    private static final int REQUEST_CAPTURE_PHOTO = 1002;
    private GroupInfo groupInfo;
    private String mUploadImg;
    private DisplayImageOptions options;
    private DisplayImageOptions option;
    private GroupEditAdapter adapter;
    private Dialog mLoadingDialog; // 加载框
    private List<Integer> userids = new ArrayList<Integer>();
    private ArrayList<UserInfo> sUsers = new ArrayList<UserInfo>();
    private ArrayList<UserInfo> noteRoles = new ArrayList<>();
    private MyBroadCastReceiver receiver;
    private int intoTag;
    private int GroupID;
    private int[] colors;   // 群组颜色集合
    private int colorWhat;
    private List<ApiEntity.UserInfo> userInfos = new ArrayList<>();
    private String uriHead; //圈子头像URL
    private int msgID; //传值，通知栏推送点击进入

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            getGroupMember(groupInfo.ID);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
        GroupID = getIntent().getIntExtra("GroupID", 0);
        if (GroupID == 0) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra("GroupInfo");
            isNull();
        } else {
            if (EMWApplication.groupMap != null && EMWApplication.groupMap.get(GroupID) != null &&
                    EMWApplication.groupMap.get(GroupID).Users != null) {
                groupInfo = EMWApplication.groupMap.get(GroupID);
                init();
            } else
                getGroupsByID(GroupID);
        }
        mEditBtn.setTag(0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //            view.getTag(R.id.tag_first) != null && view.getTag().equals("lastItem")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mEditBtn.getTag().equals(1)) {
                    if (position == adapter.getCount() - 1) {
                        Intent intent = new Intent(GroupInActivity.this, ContactSelectActivity.class);
                        intent.putExtra("select_list", (Serializable) sUsers);
                        intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        intent.putExtra("click_pos_y", location[1]);
                        startActivityForResult(intent, 1);
                    } else if (view.getTag(R.id.tag_first) != null && (boolean) view.getTag(R.id.tag_first)) {
                        new AlertDialog(GroupInActivity.this).builder().setMsg("确认要删除该成员？")
                                .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                                .setPositiveButton(getString(R.string.confirm),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                userids.remove((Integer) sUsers.get(position).ID);
                                                sUsers.remove(position);
                                                memberNum.setText(userids.size() + "人");
                                                adapter.setData(sUsers);
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton(getString(R.string.cancel),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        }).show();
                    }
                } else {
                    if (sUsers.get(position) != null) {
                        Intent intent = new Intent(GroupInActivity.this,
                                PersonInfoActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("UserInfo", sUsers.get(position));
                        intent.putExtra("intoTag", 1);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        view.getLocationInWindow(location);
                        intent.putExtra("click_pos_y", location[1]);
                        intent.putExtras(args);
                        GroupInActivity.this.startActivity(intent);
                    }
                }
            }
        });

        msgID = getIntent().getIntExtra("msg_id", 0);
        if (msgID > 0) {
            Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
            intent.putExtra(MainActivity.MESSAGE_ID, msgID);
            intent.putExtra(MainActivity.MESSAGE_TYPE, 2);
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

    private void isNull() {
        if (groupInfo != null)
            init();
        else {
            mRootView.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog(GroupInActivity.this).builder();
            dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
            dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
    }

    private void init() {
        userInfos = groupInfo.Users;
        intoTag = getIntent().getIntExtra("intoTag", 0);
        mRootView.setVisibility(View.VISIBLE);
        colorWhat = groupInfo.Color;
        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1),
                getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3),
                getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5),
                getResources().getColor(R.color.cal_color6)};
        mRadioGroup.setEnabled(false);
        mPrivate.setEnabled(false);
        mPublic.setEnabled(false);
        gName.setEnabled(false);
        mColorLayout.setVisibility(View.GONE);
        if (groupInfo.IsAddIn) {
            nearbyLayout.setVisibility(View.VISIBLE);
            lLGroupRelationFile.setVisibility(View.VISIBLE);
            if (intoTag == 0) {
                tvChatBtn.setVisibility(View.VISIBLE);
            } else {
                tvChatBtn.setVisibility(View.GONE);
            }
            gridView.setVisibility(View.VISIBLE);
            exit.setText("退出群组");
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog(GroupInActivity.this).builder().setMsg("确认退出群" + groupInfo.Name)
                            .setNegativeButton(getString(R.string.cancel),
                                    new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                        }
                                    })
                            .setPositiveButton(getString(R.string.confirm),
                                    new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            delGroupRoles(groupInfo.ID,
                                                    PrefsUtil.readUserInfo().ID);
                                        }
                                    }).show();
                }
            });
        } else {
            tvChatBtn.setVisibility(View.GONE);
            nearbyLayout.setVisibility(View.GONE);
            lLGroupRelationFile.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            exit.setText("申请加入");
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog(GroupInActivity.this).builder().setMsg("确认申请加入群" + groupInfo.Name)
                            .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                }
                            }).setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (groupInfo.Type == 0)
                                getGroupInfo(PrefsUtil.readUserInfo().ID);
                            else
                                askForGroup(PrefsUtil.readUserInfo().ID);
                        }
                    }).show();
                }
            });
        }

        IntentFilter filter = new IntentFilter();
        receiver = new MyBroadCastReceiver();
        filter.addAction(ACTION_REFRESH_COUNT_GROUP);
        registerReceiver(receiver, filter);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_grouphead) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_grouphead) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(388))
                .build(); // 创建配置过得DisplayImageOption对象

        option = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head)
//                .showImageForEmptyUri(R.drawable.cm_img_head)
//                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(388))
                .cacheOnDisk(true).build();

        if (groupInfo.BackImageIndex == 0) {
            uriHead = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
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
        ImageLoader.getInstance().displayImage(uriHead, new ImageViewAware(gCircleImg), options,
                new ImageSize(DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 40)), null, null);
        gName.setText(groupInfo.Name);
        if (groupInfo.Type == 0) {
            mPublic.setChecked(true);
        } else {
            mPrivate.setChecked(true);
        }
        memberNum.setText(groupInfo.Count + "人");
        if (groupInfo.Users != null) {
            if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
                exit.setVisibility(View.GONE);
                mEditBtn.setVisibility(View.VISIBLE);
                mEditBtn.setTag(0);
            } else {
                mEditBtn.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
            }
            ownCircleImg.setTvBg(EMWApplication.getIconColor(groupInfo.CreateUser), groupInfo.CreateUserName, 40);
            String ownUri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                    groupInfo.CreateUserImage);
            ImageLoader.getInstance().displayImage(ownUri, new ImageViewAware(ownCircleImg), option,
                    new ImageSize(DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 40)), null, null);
            ownName.setText(groupInfo.CreateUserName);
        }

        adapter = new GroupEditAdapter(GroupInActivity.this, mLoadingDialog);
        adapter.setGroupInfo(groupInfo);
        gridView.setAdapter(adapter);
        getGroupMember(groupInfo.ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            boolean flag = true;
            sUsers.clear();
            sUsers = (ArrayList<UserInfo>) data
                    .getSerializableExtra("select_list");
            if (sUsers.size() > 0) {
                userids.clear();
                for (int i = 0; i < sUsers.size(); i++) {
                    userids.add(sUsers.get(i).ID);
                    if (sUsers.get(i).ID == groupInfo.CreateUser)
                        flag = false;
                }
//                if (flag)
//                    userids.add(groupInfo.CreateUser);
                memberNum.setText(userids.size() + "人");
                adapter.setData(sUsers);
                adapter.notifyDataSetChanged();
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

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_left9, R.id.cm_header_btn_more, R.id.cIv_group_img, R.id.ll_group_in_manager,
            R.id.ll_group_in_relation_file, R.id.tv_person_chat, R.id.cm_header_tv_right9, R.id.ll_calendar_color, R.id.ll_group_in_nearby})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
            case R.id.cm_header_btn_left:
                KeyBoardUtil.hideSoftInput(gName, this);
                onBackPressed();
                break;
            case R.id.cIv_group_img:
                if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID && mEditBtn.getTag().equals(1)) {
                    ActionSheetDialog dialog = new ActionSheetDialog(this)
                            .builder();
                    dialog.addSheetItem(getString(R.string.actionsheet_photo),
                            null, new ActionSheetDialog.OnSheetItemClickListener() {
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
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Crop.pickImage(GroupInActivity.this);
                                }
                            });
                    dialog.show();
                } else {
                    Intent intent = new Intent(GroupInActivity.this, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_IMGURLS, uriHead);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_ISFORMAT, false);
                    intent.putExtra(PhotoActivity.INTENT_EXTRA_ISNATIVE_IMG, groupInfo.BackImageIndex);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                }
                break;
            case R.id.cm_header_btn_more:
                mEditBtn.setTag(1);
                tvCamera.setVisibility(View.VISIBLE);
                gName.setEnabled(true);
                gName.setSelection(gName.getText().toString().length());
                KeyBoardUtil.openSoftInput(this);
                mRadioGroup.setEnabled(true);
                mPrivate.setEnabled(true);
                mPublic.setEnabled(true);
                mColorLayout.setVisibility(View.VISIBLE);
                lLayoutGManager.setVisibility(View.GONE);
                lLGroupRelationFile.setVisibility(View.GONE);
                nearbyLayout.setVisibility(View.GONE);
                adapter.setEditEnabled(true);
                adapter.notifyDataSetChanged();
                mEditBtn.setVisibility(View.GONE);
                rightIcon.setVisibility(View.VISIBLE);
                btnDelGroup.setVisibility(View.VISIBLE);
                btnDelGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog(GroupInActivity.this).builder().setMsg(getString(R.string.deletegroup_tips))
                                .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                                .setPositiveButton(getString(R.string.confirm),
                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                delGroup(groupInfo.ID);
                                            }
                                        })
                                .setNegativeButton(getString(R.string.cancel),
                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                            }
                                        }).show();
                    }
                });

                switch (colorWhat) {
                    case 0:
                        mTvColorSelect.setImageResource(R.drawable.share_circle1_nor);
                        break;
                    case 1:
                        mTvColorSelect.setImageResource(R.drawable.share_circle2_nor);
                        break;
                    case 2:
                        mTvColorSelect.setImageResource(R.drawable.share_circle3_nor);
                        break;
                    case 3:
                        mTvColorSelect.setImageResource(R.drawable.share_circle4_nor);
                        break;
                    case 4:
                        mTvColorSelect.setImageResource(R.drawable.share_circle5_nor);
                        break;
                    case 5:
                        mTvColorSelect.setImageResource(R.drawable.share_circle6_nor);
                        break;
                }

                break;
            case R.id.cm_header_tv_right9:
                mEditBtn.setTag(0);
                tvCamera.setVisibility(View.INVISIBLE);
                mEditBtn.setVisibility(View.VISIBLE);
                rightIcon.setVisibility(View.GONE);
                gName.setEnabled(false);
                KeyBoardUtil.hideSoftInput(gName, this);
                mRadioGroup.setEnabled(false);
                mPrivate.setEnabled(false);
                mPublic.setEnabled(false);
                mColorLayout.setVisibility(View.GONE);
                lLayoutGManager.setVisibility(View.VISIBLE);
                lLGroupRelationFile.setVisibility(View.VISIBLE);
                nearbyLayout.setVisibility(View.VISIBLE);
                adapter.setEditEnabled(false);
                adapter.notifyDataSetChanged();
                btnDelGroup.setVisibility(View.GONE);
                saveGroupInfo();
                break;
            case R.id.ll_calendar_color:
                new AnimatedColorPickerDialog.Builder(this).setTitle("选择一种颜色").setColors(colors).setOnColorClickListener(new AnimatedColorPickerDialog.ColorClickListener() {
                    @Override
                    public void onColorClick(int color) {
                        if (color == getResources().getColor(R.color.cal_color0)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle1_nor);
                            colorWhat = 0;
                        } else if (color == getResources().getColor(R.color.cal_color1)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle2_nor);
                            colorWhat = 1;
                        } else if (color == getResources().getColor(R.color.cal_color2)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle3_nor);
                            colorWhat = 2;
                        } else if (color == getResources().getColor(R.color.cal_color3)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle4_nor);
                            colorWhat = 3;
                        } else if (color == getResources().getColor(R.color.cal_color4)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle5_nor);
                            colorWhat = 4;
                        } else if (color == getResources().getColor(R.color.cal_color5)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle6_nor);
                            colorWhat = 5;
                        } else if (color == getResources().getColor(R.color.cal_color6)) {
                            mTvColorSelect.setImageResource(R.drawable.share_circle7_nor);
                            colorWhat = 6;
                        }
                    }
                }).create().show();
                break;
            case R.id.ll_group_in_relation_file:
                Intent relationIntent = new Intent(this, RelationFileActivity.class);
                relationIntent.putExtra("group_info", groupInfo);
                startActivity(relationIntent);
                break;
            case R.id.ll_group_in_manager:
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(groupInfo.CreateUser) != null) {
                    Intent intent = new Intent(this, PersonInfoActivity.class);
                    UserInfo userInfo = EMWApplication.personMap.get(groupInfo.CreateUser);
                    intent.putExtra("UserInfo", userInfo);
                    intent.putExtra("intoTag", 1);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    startActivity(intent);
                }
                break;
            case R.id.tv_person_chat:
                Intent intents = new Intent(this, ChatActivity.class);
                intents.putExtra("GroupID", groupInfo.ID);
                intents.putExtra("name", groupInfo.Name);
                intents.putExtra("type", 2);
                startActivity(intents);
                break;
            case R.id.ll_group_in_nearby:   //附近的人
                Intent nearbyInent = new Intent(this, GroupPersonsActivity.class);
                nearbyInent.putExtra("userList", (Serializable) groupInfo.Users);
                startActivity(nearbyInent);
                break;
        }

    }

    /**
     * 跟据群id获取群
     *
     * @param gids
     */
    private void getGroupsByID(int gids) {
        API.TalkerAPI.GetChatterGroupById(gids, new RequestCallback<GroupInfo>(GroupInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                mRootView.setVisibility(View.GONE);
                AlertDialog dialog = new AlertDialog(GroupInActivity.this).builder();
                dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
                dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
                Log.d("zrjt", throwable.toString());
            }

            @Override
            public void onParseSuccess(GroupInfo respInfo) {
                if (respInfo != null) {
                    groupInfo = respInfo;
                    init();
                } else {
                    mRootView.setVisibility(View.GONE);
                    AlertDialog dialog = new AlertDialog(GroupInActivity.this).builder();
                    dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
                    dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
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
        Log.d("Const","-----GroupInActivity----getGroupMember---");
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<UserInfo>(
                UserInfo.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                if (mLoadingDialog != null)
//                    mLoadingDialog.dismiss();
            }

            @Override
            public void onStarted() {
//                if (mLoadingDialog != null)
//                    mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<UserInfo> respList) {
//                if (mLoadingDialog != null)
//                    mLoadingDialog.dismiss();
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
                    adapter.setData(noteRoles);
                    adapter.notifyDataSetChanged();
                    memberNum.setText(adapter.getMemberCount() + "人");
                }
            }
        });
    }

    /**
     * 私有群组申请加入群组
     *
     * @param id
     */
    private void askForGroup(int id) {
        API.TalkerAPI.JoinToGroup(groupInfo, new RequestCallback<String>(String.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(GroupInActivity.this, "申请失败");
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showToast(GroupInActivity.this,
                            "申请成功,等待审核", R.drawable.tishi_ico_gougou);
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
        API.TalkerAPI.AddGroupUsers(groupInfo.ID, userids,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onStarted() {
                        if (mLoadingDialog != null)
                            mLoadingDialog.show();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(GroupInActivity.this,
                                R.string.groupinto_join_error);
                    }

                    @Override
                    public void onSuccess(String result) {
                        mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isDigitsOnly(result)
                                && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(GroupInActivity.this,
                                    R.string.groupinto_join_success, R.drawable.tishi_ico_gougou);
                            getGroupMember(groupInfo.ID);
                            Intent intent = new Intent(
                                    GroupFragment.ACTION_REFRESH_GROUP);
                            sendBroadcast(intent); // 刷新群组列表
                        } else {
                            ToastUtil.showToast(GroupInActivity.this,
                                    R.string.groupinto_join_error);
                        }
                    }
                });
    }

    // 申请加入群组
    private void getGroupInfo(int uid) {
        API.TalkerAPI.AddGroupUser(groupInfo.ID, uid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(GroupInActivity.this, R.string.groupinto_join_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(GroupInActivity.this, R.string.groupinto_join_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent); // 刷新群组列表
                    finish();
                } else {
                    ToastUtil.showToast(GroupInActivity.this, R.string.groupinto_join_error);
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
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(GroupInActivity.this,
                        R.string.groupinto_exit_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(groupInfo.ID);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(GroupInActivity.this,
                            R.string.groupinto_exit_success, R.drawable.tishi_ico_gougou);
//                    for (int i = 0, size = noteRoles.size(); i < size; i++) {
//                        if (noteRoles.get(i).ID == PrefsUtil.readUserInfo().ID) {
//                            noteRoles.remove(i);
//                            break;
//                        }
//                    }
                    Intent intent = new Intent();
                    intent.setAction(GroupInActivity.ACTION_REFRESH_COUNT_GROUP);
                    GroupInActivity.this.sendBroadcast(intent);
                    Intent intentExit = new Intent();
                    intentExit.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intentExit);
                    adapter.notifyDataSetChanged();
                    finish();
                } else {
                    ToastUtil.showToast(GroupInActivity.this,
                            R.string.groupinto_exit_error);
                }
            }
        });
    }

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
                ToastUtil.showToast(GroupInActivity.this,
                        R.string.groupinto_delete_error);
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(groupInfo.ID);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(GroupInActivity.this,
                            R.string.groupinto_delete_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
                    sendBroadcast(intent);
                    finish();
                } else {
                    ToastUtil.showToast(GroupInActivity.this,
                            R.string.groupinto_delete_error);
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
                ToastUtil.showToast(GroupInActivity.this,
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
                    gCircleImg.setImageBitmap(BitmapFactory
                            .decodeFile(new File(uri.getPath()).getPath()));
                    String imgTargetStr = result.toString();
                    int i = imgTargetStr.lastIndexOf("/");
                    String imgName = imgTargetStr.substring(i + 1,
                            imgTargetStr.length() - 1);
                    groupInfo.Image = imgName;
                } else {
                    ToastUtil.showToast(GroupInActivity.this,
                            R.string.headupload_error);
                }
            }
        });
    }

    private void saveGroupInfo() {
        groupInfo.Name = gName.getText().toString();
        if (mPublic.isChecked())
            groupInfo.Type = 0;
        else
            groupInfo.Type = 1;
        groupInfo.Color = colorWhat;
        groupInfo.Users = userInfos;
        if (!TextUtils.isEmpty(groupInfo.Name)) {
            API.TalkerAPI.SaveChatterGroup(groupInfo, userids,
                    new RequestCallback<String>(String.class) {

                        @Override
                        public void onStarted() {
                            mLoadingDialog.show();
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            mLoadingDialog.dismiss();
                            ToastUtil.showToast(GroupInActivity.this, "编辑失败,服务器异常");
                        }

//                        @Override
//                        public void onParseSuccess(ApiEntity.APIResult respInfo) {
//                            if (respInfo.State == 1) {
//                                ToastUtil.showToast(GroupInActivity.this,
//                                        "编辑成功", R.drawable.tishi_ico_gougou);
//                                Intent intent = new Intent(
//                                        GroupFragment.ACTION_REFRESH_GROUP);
//                                sendBroadcast(intent); // 刷群主列表
//                            } else {
//                                ToastUtil.showToast(GroupInActivity.this, "编辑失败,请稍候再试");
//                            }
//                        }

                        @Override
                        public void onSuccess(String result) {
                            mLoadingDialog.dismiss();
                            if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                                ToastUtil.showToast(GroupInActivity.this,
                                        "编辑成功", R.drawable.tishi_ico_gougou);
                                Intent intent = new Intent(
                                        GroupFragment.ACTION_REFRESH_GROUP);
                                sendBroadcast(intent); // 刷群主列表
                            } else {
                                ToastUtil.showToast(GroupInActivity.this, "编辑失败,请稍候再试");
                            }
                        }
                    });
        } else {
            ToastUtil.showToast(GroupInActivity.this, "圈子名称不能为空");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
    }
}
