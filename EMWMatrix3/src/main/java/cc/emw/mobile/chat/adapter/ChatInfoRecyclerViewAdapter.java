package cc.emw.mobile.chat.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.ChatTeamInfoActivity3;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.map.GroupPersonsActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedColorPickerDialog;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.MyGridView;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SwitchButton;

/**
 * Created by sunny.du on 2016/10/25.
 */
public class ChatInfoRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private GroupInfo mGroupInfo;
    private ArrayList<Message> mMsgList;
    private Dialog mLoadingDialog; // 加载框
    private ChatTeamUserInfoAdapter mChatTeamUserInfoAdapter;
    private ChatPlayerAdapter mGrideAdapter;
    private ArrayList<UserInfo> noteRoles;
    private List<Integer> userids;//群组用户ID保存
    private List<Integer> olderUserIds;
    private ArrayList<UserInfo> sUsers;
    private MyViewHolder myViewHolder;
    private DisplayImageOptions option;
    private int[] colors;   // 群组颜色集合
    private int colorWhat;
    private int isUserHide = -1;//0没屏蔽   1  屏蔽群
    private UserInfo user;//当前用户

    public ChatInfoRecyclerViewAdapter(Context context, GroupInfo groupInfo, ArrayList<Message> msgList, Dialog loadingDialog) {
        this.mContext = context;
        this.mLoadingDialog = loadingDialog;
        this.mGroupInfo = groupInfo;
        this.mMsgList = msgList;
        user = PrefsUtil.readUserInfo();
        for (int userId : mGroupInfo.MsgHideList) {
            if (userId == user.ID) {
                isUserHide = 1;
            } else {
                isUserHide = 0;
            }
        }
        /**
         * 初始化数据集合
         */
        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();
        olderUserIds = new ArrayList<>();

        if (groupInfo != null) {
            for (int i = 0; i < groupInfo.Users.size(); i++) {
                olderUserIds.add(groupInfo.Users.get(i).ID);
            }
        }

        option = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head)
//                .showImageForEmptyUri(R.drawable.cm_img_head)
//                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(388))
                .cacheOnDisk(true).build();

        colors = new int[]{mContext.getResources().getColor(R.color.cal_color0), mContext.getResources().getColor(R.color.cal_color1),
                mContext.getResources().getColor(R.color.cal_color2), mContext.getResources().getColor(R.color.cal_color3),
                mContext.getResources().getColor(R.color.cal_color4), mContext.getResources().getColor(R.color.cal_color5),
                mContext.getResources().getColor(R.color.cal_color6)};

        colorWhat = groupInfo.Color;
    }

    public ChatTeamUserInfoAdapter getChatTeamUserInfoAdapter() {
        return mChatTeamUserInfoAdapter;
    }

    public TextView getMemberNumTv() {
        return myViewHolder.mTvGroupUserCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.itme_chat_info_recyclerview, null);
        myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public void setNoteRoles(ArrayList<UserInfo> noteRoles) {
        this.noteRoles = noteRoles;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        initView(holder);//绑定数据展示视图
    }

    private void saveGroupMsgHide(final MyViewHolder holder) {
        API.TalkerAPI.DoChatterGroupMsgHideByGid(mGroupInfo.ID, isUserHide == 1 ? 0 : 1,new RequestCallback<GroupInfo>(GroupInfo.class) {
            @Override
            public void onParseSuccess(GroupInfo respInfo) {
                EMWApplication.groupMap.put(respInfo.ID,respInfo);
                holder.mSbGroupMessageHide.toggle();
                if(holder.mSbGroupMessageHide.isChecked()){
                    isUserHide=1;
                }else{
                    isUserHide=0;
                }
                holder.mPbGroupMessageHide.setVisibility(View.GONE);
                isOverChange=true;
            }


            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d("sunny----->","="+throwable.toString());
                throwable.printStackTrace();
                holder.mPbGroupMessageHide.setVisibility(View.GONE);
                ToastUtil.showToast(mContext,"网络异常，请稍后重试");
            }

            @Override
            public void onStarted() {
                holder.mPbGroupMessageHide.setVisibility(View.VISIBLE);
            }
        });
    }
    boolean isOverChange=true;
    private void initView(final MyViewHolder holder) {
        holder.mSbGroupShow.setChecked(mGroupInfo.Type == 0 ? true : false);
        holder.mSbGroupMessageHide.setChecked(isUserHide == 1 ? true : false);
        holder.mRlGroupMessageHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOverChange) {
                    isOverChange=false;
                    saveGroupMsgHide(holder);
                }
            }
        });
        holder.mRlGroupSeekbarBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
                    holder.mSbGroupShow.toggle();
                    saveGroupInfo(holder.mSbGroupShow.isChecked(), olderUserIds);
                }
            }
        });

        holder.mNearlyLayout.setOnClickListener(new View.OnClickListener() {    //附近的人
            @Override
            public void onClick(View v) {
                Intent nearbyInent = new Intent(mContext, GroupPersonsActivity.class);
                nearbyInent.putExtra("userList", (Serializable) mGroupInfo.Users);
                mContext.startActivity(nearbyInent);
            }
        });

        holder.mGroupCreateImg.setTextBg(EMWApplication.getIconColor(mGroupInfo.CreateUser), mGroupInfo.CreateUserName, 40);
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, mGroupInfo.CreateUserImage);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(holder.mGroupCreateImg), option,
                new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
        holder.mGroupCreateTv.setText(mGroupInfo.CreateUserName);

        if (EMWApplication.personMap != null && EMWApplication.personMap.get(mGroupInfo.CreateUser) != null) {
            final UserInfo userInfo = EMWApplication.personMap.get(mGroupInfo.CreateUser);
            holder.mGroupCreateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo != null) {
                        Intent intent = new Intent(mContext, PersonInfoActivity.class);
                        intent.putExtra("UserInfo", userInfo);
                        intent.putExtra("intoTag", 1);
                        intent.putExtra("start_anim", false);
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        intent.putExtra("click_pos_y", location[1]);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        switch (colorWhat) {
            case 0:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle1_nor);
                break;
            case 1:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle2_nor);
                break;
            case 2:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle3_nor);
                break;
            case 3:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle4_nor);
                break;
            case 4:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle5_nor);
                break;
            case 5:
                holder.mGroupColorInfo.setImageResource(R.drawable.share_circle6_nor);
                break;
        }

        holder.mGroupColorLayout.setVisibility(mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);

        holder.mGroupColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AnimatedColorPickerDialog.Builder(mContext).setTitle("选择一种颜色").setColors(colors).setOnColorClickListener(new AnimatedColorPickerDialog.ColorClickListener() {
                    @Override
                    public void onColorClick(int color) {
                        if (color == mContext.getResources().getColor(R.color.cal_color0)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle1_nor);
                            colorWhat = 0;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color1)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle2_nor);
                            colorWhat = 1;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color2)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle3_nor);
                            colorWhat = 2;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color3)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle4_nor);
                            colorWhat = 3;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color4)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle5_nor);
                            colorWhat = 4;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color5)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle6_nor);
                            colorWhat = 5;
                        } else if (color == mContext.getResources().getColor(R.color.cal_color6)) {
                            holder.mGroupColorInfo.setImageResource(R.drawable.share_circle7_nor);
                            colorWhat = 6;
                        }
                        saveGroupInfo(holder.mSbGroupShow.isChecked(), olderUserIds);
                    }
                }).create().show();
            }
        });

        /**
         * 绑定多媒体信息
         */
        if (mMsgList != null && mMsgList.size() > 0) {
            holder.mRLayoutMedia.setVisibility(View.VISIBLE);
            mGrideAdapter = new ChatPlayerAdapter(mContext, mMsgList);
            holder.mGvChatPlayer.setAdapter(mGrideAdapter);
            if (mMsgList.size() > 4) {
                holder.mTvGtoupPalyerCount.setText("+" + (mMsgList.size() - 4));
            }
        } else {
            holder.mRLayoutMedia.setVisibility(View.GONE);
        }

        /**
         * 获取群组成员已经绑定listView
         */
        /**
         * 添加成员
         */
//        View headerView = View.inflate(mContext, R.layout.item_chat_team_add_user, null);
//        if (mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
//            holder.mLvGroupUser.addHeaderView(headerView);
//        }
        holder.mGroupMemberEdit.setVisibility(mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
        holder.mGroupMemberEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 添加群组成员事件监听  跳转人员选择列表
                 */
                Intent intent = new Intent(mContext, ContactSelectActivity.class);
                intent.putExtra("select_list", noteRoles);
                intent.putExtra(ContactSelectActivity.EXTRA_CLICKABLE_UID, PrefsUtil.readUserInfo().ID);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                ((Activity) mContext).startActivityForResult(intent, 1);
            }
        });
        mChatTeamUserInfoAdapter = new ChatTeamUserInfoAdapter(mContext);
        holder.mLvGroupUser.setAdapter(mChatTeamUserInfoAdapter);

        /**
         * 添加解散圈子/退出圈子
         */
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_del_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_group_del);
        if (mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
            textView.setText("解散圈子");
        } else {
            textView.setText("退出圈子");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder.mLvGroupUser.getFooterViewsCount() == 0)
                    holder.mLvGroupUser.addFooterView(view);
            }
        }, 1500);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog(mContext).builder().setMsg(mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID ? mContext.getString(R.string.deletegroup_tips) :
                        "是否退出圈子")
                        .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(mContext.getString(R.string.confirm),
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (mGroupInfo.CreateUser == PrefsUtil.readUserInfo().ID)
                                            delGroup(mGroupInfo.ID, mContext);
                                        else
                                            delGroupRoles(mGroupInfo.ID, PrefsUtil.readUserInfo().ID, mContext);
                                    }
                                })
                        .setNegativeButton(mContext.getString(R.string.cancel),
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
            }
        });

        getGroupMember(mGroupInfo.ID, holder);

    }

    @Override
    public int getItemCount() {
        return 1;
    }


//TODO   后续优化提取请求底层接口公共类

    /**
     * 获取群成员信息
     *
     * @param gid
     */
    private void getGroupMember(int gid, final MyViewHolder holder2) {
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    noteRoles.clear();
                    if (mGroupInfo.Users != null) {
                        mGroupInfo.Users.clear();
                        mGroupInfo.Users.addAll(respList);
                    }
                    noteRoles.addAll(respList);
                    sUsers.clear();
                    sUsers.addAll(noteRoles);
                    if (sUsers.size() > 0) {
                        userids.clear();
                        for (int i = 0; i < sUsers.size(); i++) {
                            userids.add(sUsers.get(i).ID);
                        }
                    }
                    mChatTeamUserInfoAdapter.setData(sUsers);
                    mChatTeamUserInfoAdapter.notifyDataSetChanged();
                    holder2.mTvGroupUserCount.setText("成员(" + respList.size() + "人)");
                } else {
                    holder2.mTvGroupUserCount.setText("成员(" + 0 + "人)");
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

    /**
     * 解散圈子
     *
     * @param gid
     */
    private void delGroup(final int gid, final Context mContext) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(mContext,
                        R.string.groupinto_delete_error);
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(gid);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(mContext,
                            R.string.groupinto_delete_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent();
                    intent.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    mContext.sendBroadcast(intent);
                    intent = new Intent();
                    intent.setAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
                    mContext.sendBroadcast(intent);
                    ((ChatTeamInfoActivity3) mContext).finish();
                } else {
                    ToastUtil.showToast(mContext,
                            R.string.groupinto_delete_error);
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
    private void delGroupRoles(final int gid, int userid, final Context mContext) {
        API.TalkerAPI.DelGroupUser(gid, userid, new RequestCallback<String>(
                String.class) {

            @Override
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(mContext,
                        R.string.groupinto_exit_error);
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(result)
                        && TextUtils.isDigitsOnly(result)
                        && Integer.valueOf(result) > 0) {
                    delete(gid);
                    AppManager.finishActivity(ChatActivity.class);
                    ToastUtil.showToast(mContext,
                            R.string.groupinto_exit_success, R.drawable.tishi_ico_gougou);
//                    for (int i = 0, size = noteRoles.size(); i < size; i++) {
//                        if (noteRoles.get(i).ID == PrefsUtil.readUserInfo().ID) {
//                            noteRoles.remove(i);
//                            break;
//                        }
//                    }
                    Intent intent = new Intent();
                    intent.setAction(GroupInActivity.ACTION_REFRESH_COUNT_GROUP);
                    mContext.sendBroadcast(intent);
                    Intent intentExit = new Intent();
                    intentExit.setAction(GroupFragment.ACTION_REFRESH_GROUP);
                    mContext.sendBroadcast(intentExit);
                    try {
                        AppManager.finishActivity(ChatTeamInfoActivity3.class);
                    } catch (Exception e) {
                    }
                } else {
                    ToastUtil.showToast(mContext,
                            R.string.groupinto_exit_error);
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
     * 保存群组
     */
    private void saveGroupInfo(boolean flag, List<Integer> oldeUserIds) {
        if (flag)
            mGroupInfo.Type = 0;
        else
            mGroupInfo.Type = 1;
        mGroupInfo.Color = colorWhat;
        API.TalkerAPI.SaveChatterGroup(mGroupInfo, oldeUserIds,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        ToastUtil.showToast(mContext, "编辑失败,服务器异常");
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(mContext,
                                    "编辑成功", R.drawable.tishi_ico_gougou);
                            Intent intent = new Intent(
                                    GroupFragment.ACTION_REFRESH_GROUP);
                            mContext.sendBroadcast(intent);
                            EMWApplication.groupMap.put(mGroupInfo.ID, mGroupInfo);
                        } else {
                            ToastUtil.showToast(mContext, "编辑失败,请稍候再试");
                        }
                    }
                });
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {
    LinearLayout mRlGroupSeekbarBut;//是否可见点击事件条
    LinearLayout mNearlyLayout; //附近的人
    SwitchButton mSbGroupShow;//是否可见开关
    RelativeLayout mRLayoutMedia;   //分享媒体的容器
    MyGridView mGvChatPlayer;//多媒体展示控件
    TextView mTvGtoupPalyerCount;//总共有多媒体数量-4
    TextView mTvGroupUserCount;//展示群组人数控件
    MyListView mLvGroupUser;//展示群组人员列表
    CircleImageView mGroupCreateImg; //群主头像
    TextView mGroupCreateTv;    //群主姓名,解散群组
    LinearLayout mGroupColorLayout; //圈子颜色选项
    LinearLayout mGroupCreateLayout;   //圈子创建者
    ImageView mGroupColorInfo;//圈子颜色标示
    LinearLayout mGroupMemberEdit;//圈子成员编辑
    LinearLayout mRlGroupMessageHide;
    SwitchButton mSbGroupMessageHide;
    ProgressBar mPbGroupMessageHide;

    public MyViewHolder(View itemView) {
        super(itemView);
        mRlGroupSeekbarBut = (LinearLayout) itemView.findViewById(R.id.rl_group_new_seebar_but);
        mSbGroupShow = (SwitchButton) itemView.findViewById(R.id.sb_group_new_but);
        mGvChatPlayer = (MyGridView) itemView.findViewById(R.id.gd_chat_player);
        mTvGtoupPalyerCount = (TextView) itemView.findViewById(R.id.tv_group_new_palyer_count);
        mTvGroupUserCount = (TextView) itemView.findViewById(R.id.tv_group_new_user_count);
        mLvGroupUser = (MyListView) itemView.findViewById(R.id.lv_group_user2);
        mRLayoutMedia = (RelativeLayout) itemView.findViewById(R.id.rl_media_layout);
        mNearlyLayout = (LinearLayout) itemView.findViewById(R.id.ll_group_in_nearby);
        mGroupCreateImg = (CircleImageView) itemView.findViewById(R.id.civ_chat_create_image);
        mGroupCreateTv = (TextView) itemView.findViewById(R.id.tv_chat_team_create_name);
        mGroupColorLayout = (LinearLayout) itemView.findViewById(R.id.ll_calendar_color);
        mGroupColorInfo = (ImageView) itemView.findViewById(R.id.img_calendar_color_select);
        mGroupCreateLayout = (LinearLayout) itemView.findViewById(R.id.ll_group_create);
        mGroupMemberEdit = (LinearLayout) itemView.findViewById(R.id.ll_group_member_edit);
        mRlGroupMessageHide = (LinearLayout) itemView.findViewById(R.id.rl_group_message_hide);
        mSbGroupMessageHide = (SwitchButton) itemView.findViewById(R.id.sb_group_message_hide);
        mPbGroupMessageHide = (ProgressBar) itemView.findViewById(R.id.pb_group_message_hide);
    }
}
