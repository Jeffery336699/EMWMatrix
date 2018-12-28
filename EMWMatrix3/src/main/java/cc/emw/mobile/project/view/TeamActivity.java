package cc.emw.mobile.project.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.base.BaseHolder;
import cc.emw.mobile.base.MyBaseAdapter;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.model.bean.GroupMessage;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.adapter.TeamMemberAdapter;
import cc.emw.mobile.project.adapter.TeamMemberAdapter2;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/23.
 * 查看圈子信息
 */
@ContentView(R.layout.activity_team3)
public class TeamActivity extends BaseActivity {
    private final String TAG = this.getClass().getSimpleName();

    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_invite) private IconTextView mHeaderInviteItv; // 顶部条申请信息按钮
    @ViewInject(R.id.cm_header_btn_more) private ImageButton mHeaderMoreBtn; // 顶部条编辑按钮

    @ViewInject(R.id.civ_team_head) private CircleImageView mTeamHeadIv; //圈子头像
    @ViewInject(R.id.tv_team_name) private TextView mTeamName;  //团队名称
    @ViewInject(R.id.tv_team_power) private TextView mTeamPowerTv; //权限(私密/公开)
    @ViewInject(R.id.civ_team_creatorhead) private CircleImageView mCreatorHeadIv; //创建者头像
    @ViewInject(R.id.tv_team_creatorname) private TextView mCreatorNameTv; //创建者姓名
    @ViewInject(R.id.ll_team_member) private LinearLayout mMemberLayout;
    @ViewInject(R.id.tv_team_membernum) private TextView mMemberNumTv; //成员数量
    @ViewInject(R.id.itv_team_memberarrow) private IconTextView mMemberArrowItv; //箭头
    @ViewInject(R.id.gv_team_member) private GridView mMemberGridView;// 团队成员头像列表

    private DisplayImageOptions options;
    private TeamMemberAdapter2 adapter;
    private String groupName;
    private String groupImg;
    private ArrayList<Integer> members; //成员id集合
    private ArrayList<ApiEntity.UserInfo> memberList = new ArrayList<>();
    private ApiEntity.ChatterGroup group; //团队
    private MyBroadcastReceive receive;
    private ArrayList<ApiEntity.Message> messages; //申请消息
    private ViewGroup listViewGroup;
    private InvitInfoAdapter invitInfoAdapter;

    private DrawerLayout mDrawerLayout;//侧滑抽屉

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_btn_invite, R.id.cm_header_btn_more, R.id.ll_team_member, R.id.ll_team_chat})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left9:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.cm_header_btn_invite:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.cm_header_btn_more:
                Intent modifyIntent = new Intent(this,NewTeamActivity.class);
                modifyIntent.putExtra("group",group);
                modifyIntent.putIntegerArrayListExtra("members",members);
                modifyIntent.putExtra("start_anim", false);
                startActivity(modifyIntent);
                break;
            case R.id.ll_team_member:
                Intent intent = new Intent(this,TeamMemberActivity.class);
//                intent.putIntegerArrayListExtra("members",members);
                intent.putExtra("member_list", memberList);
                intent.putExtra("group",group);
                intent.putExtra("groupName",groupName);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivity(intent);
                break;
            case R.id.ll_team_chat:
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra("GroupID", group.ID);
                chatIntent.putExtra("name", group.Name);
                chatIntent.putExtra("type", 2);
                startActivity(chatIntent);
                break;
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        groupName = getIntent().getStringExtra("groupName");
        groupImg = getIntent().getStringExtra("groupImg");
        members = getIntent().getIntegerArrayListExtra("members");
        group = (ApiEntity.ChatterGroup) getIntent().getSerializableExtra("group");
        if(PrefsUtil.readUserInfo().ID == group.CreateUser) {
            mHeaderInviteItv.setVisibility(View.VISIBLE);
            mHeaderMoreBtn.setVisibility(View.VISIBLE);
            mMemberArrowItv.setVisibility(View.VISIBLE);
            mMemberLayout.setClickable(true);
            //获取申请加入信息
            getInviteMsg(group.ID + "");
        }else {
            mHeaderInviteItv.setVisibility(View.GONE);
            mHeaderMoreBtn.setVisibility(View.GONE);
            mMemberArrowItv.setVisibility(View.GONE);
            mMemberLayout.setClickable(false);
        }

        mHeaderTitleTv.setText(groupName);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        DisplayImageOptions groupHeadOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_grouphead) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_grouphead) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, groupImg);
        ImageLoader.getInstance().displayImage(uri, mTeamHeadIv, groupHeadOptions);
        Log.d(TAG,"initView()->groupImg: " + groupImg);
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(group.CreateUser) != null) {
            UserInfo user = EMWApplication.personMap.get(group.CreateUser);
            mCreatorNameTv.setText(user.Name);
            String uri2 = String.format(Const.DOWN_ICON_URL, user.CompanyCode, user.Image);
            ImageLoader.getInstance().displayImage(uri2, mCreatorHeadIv, options);
        }

        mMemberNumTv.setText(members.size() + "人");
        mTeamName.setText(groupName);
        mTeamPowerTv.setText(group.Type == 0 ? "公开":"私有");
        /*adapter = new TeamMemberAdapter(this,TeamMemberAdapter.TYPE_GRID);
        adapter.setData(members);*/
        adapter = new TeamMemberAdapter2(this,TeamMemberAdapter.TYPE_GRID);
        if (group.MainUserInfo != null) {
            memberList.addAll(group.MainUserInfo);
        }
        if (group.Users != null) {
            memberList.addAll(group.Users);
        }
        adapter.setData(memberList);
        mMemberGridView.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewTeamActivity.BROADCAST_TEAM_REFRESH);
        receive = new MyBroadcastReceive();
        registerReceiver(receive,intentFilter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        ListView listView = (ListView) findViewById(R.id.invite_info_lv);
        invitInfoAdapter = new InvitInfoAdapter(this);
        listView.setAdapter(invitInfoAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receive != null)
            unregisterReceiver(receive);
    }

    //接收广播
    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();

            if (NewTeamActivity.BROADCAST_TEAM_REFRESH.equals(actionStr)) {

                ApiEntity.ChatterGroup tempGroup = (ApiEntity.ChatterGroup)intent.getSerializableExtra("group");
                if(tempGroup != null){
                    group = tempGroup;
                    mTeamName.setText(group.Name);
                    mTeamPowerTv.setText(group.Type == 0 ? "公开":"私有");
                    memberList.clear();
                    if (group.MainUserInfo != null) {
                        memberList.addAll(group.MainUserInfo);
                    }
                    if (group.Users != null) {
                        memberList.addAll(group.Users);
                    }
                    adapter.setData(memberList);
                    adapter.notifyDataSetChanged();
                    mMemberNumTv.setText(memberList.size() + "人");
                }

                /*ArrayList<Integer> temp = intent.getIntegerArrayListExtra("members");
                if(temp != null) {
                    members.clear();
                    members.addAll(temp);
                    mMemberNumTv.setText(members.size() + "人");
                    adapter.setData(members);
                    adapter.notifyDataSetChanged();
                }*/
            }
        }
    }

    /***
     * 获取申请加入信息
     * @param groupId 圈子id
     */
    private void getInviteMsg(String groupId){
        API.Message.GetGroupApplyMessages(groupId, new RequestCallback<ApiEntity.Message>(
                ApiEntity.Message.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Logger.d(TAG,"getInviteMsg()->onError()->msg: " + ex.getMessage() + " toString: " + ex.toString());
                messages = new ArrayList<>();
                ApiEntity.Message invMsg = new ApiEntity.Message();
                invMsg.Content = "加入申请";
                invMsg.ID = -1;
                messages.add(invMsg);
                ApiEntity.Message msg2 = new ApiEntity.Message();
                msg2.Content = "已批准申请";
                msg2.ID = -1;
                messages.add(msg2);
                ApiEntity.Message msg3 = new ApiEntity.Message();
                msg3.Content = "已拒绝申请";
                msg3.ID = -1;
                messages.add(msg3);
                invitInfoAdapter.setData(messages);
                invitInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onParseSuccess(List<ApiEntity.Message> retMsg) {
                Logger.d(TAG,"onParseSuccess()->size: " + retMsg.size());
                if(retMsg != null){
                    messages = new ArrayList<>();
                    addMessageBuInviteState(retMsg,InviteState.APPLY_FOR);
                    addMessageBuInviteState(retMsg,InviteState.ALLOW);
                    addMessageBuInviteState(retMsg,InviteState.DISALLOW);
                    invitInfoAdapter.setData(messages);
                    invitInfoAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 申请状态常量
     */
    class InviteState{
        public static  final int APPLY_FOR = 1;
        public static  final int ALLOW = 0;
        public static  final int DISALLOW = -1;


    }

    /**
     * 通过申请状态加入消息
     * @param retMsg
     * @param inviteState
     */
    public void addMessageBuInviteState(List<ApiEntity.Message> retMsg, int inviteState) {
        ApiEntity.Message invMsg = new ApiEntity.Message();
        if(inviteState == InviteState.APPLY_FOR) {
            invMsg.Content = "加入申请";
        }else if(inviteState == InviteState.ALLOW){
            invMsg.Content = "已批准申请";
        }else if(inviteState == InviteState.DISALLOW){
            invMsg.Content = "已拒绝申请";
        }
        invMsg.ID = -1;     // 为分类标题时id设为-1
        messages.add(invMsg);
        for(ApiEntity.Message msg : retMsg){
            if(msg.IsNew == inviteState)
                messages.add(msg);
        }
    }

    //申请消息适配器
    class InvitInfoAdapter extends MyBaseAdapter<ApiEntity.Message>{
        private Context mContext;
        private ArrayList<ApiEntity.Message> datas = new ArrayList<>();
        public InvitInfoAdapter(Context context){
            mContext = context;
            this.mDatas = datas;
        }

        @Override
        public void setData(List<ApiEntity.Message> dataList) {
            super.setData(dataList);
            if(dataList != null){
                datas.clear();
                datas.addAll(dataList);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView == null){
                vh = new ViewHolder();
                convertView = vh.mHolderView;
                listViewGroup = parent;
            }else {
                vh = (ViewHolder)convertView.getTag();
            }
            ApiEntity.Message msg = messages.get(position);
            vh.setDataAndRefreshHolderView(position,msg);
            return convertView;
        }
    }

    class ViewHolder extends BaseHolder<ApiEntity.Message>{
        TextView title;
        LinearLayout content;
        LinearLayout apply_for_ll;
        CircleImageView portrait;
        TextView name;
        TextView groupName;
        TextView agree_tv;
        TextView agree_btn;
        TextView disagree_btn;

        @Override
        public void refreshHolderView(int position, final ApiEntity.Message data) {
            if(data.ID == -1){
                title.setVisibility(View.VISIBLE);
                content.setVisibility(View.INVISIBLE);
                title.setText(data.Content);
            }else {
                Type types = new TypeToken<GroupMessage>() {
                }.getType();
                GroupMessage groupMessage = new Gson().fromJson(data.Content, types);
                Log.d(TAG,"refreshHolderView()->msg: " + data.Content);
                title.setVisibility(View.INVISIBLE);
                content.setVisibility(View.VISIBLE);
                if(data.IsNew == InviteState.APPLY_FOR){
                    apply_for_ll.setVisibility(View.VISIBLE);
                    agree_tv.setVisibility(View.GONE);
                    agree_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            processInvite(data,1);
                        }
                    });
                    disagree_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            processInvite(data,2);
                        }
                    });
                }else{
                    apply_for_ll.setVisibility(View.GONE);
                    agree_tv.setVisibility(View.VISIBLE);
                }
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(groupMessage.UserID) != null) {
                    String image = EMWApplication.personMap.get(groupMessage.UserID).Image;
                    String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, image);
                    ImageLoader.getInstance().displayImage(uri, portrait, options);
                }
                name.setText(groupMessage.UserName);
                groupName.setText(groupMessage.GroupName);
            }
        }

        @Override
        public View initHolderViewAndFindViews() {
            View view = LayoutInflater.from(TeamActivity.this).inflate(R.layout.listitem_invite,listViewGroup,false);
            title = (TextView)view.findViewById(R.id.title);
            content = (LinearLayout)view.findViewById(R.id.content_ll);
            apply_for_ll = (LinearLayout)view.findViewById(R.id.apply_for_ll);
            portrait = (CircleImageView)view.findViewById(R.id.portrait);
            name = (TextView)view.findViewById(R.id.name);
            groupName = (TextView)view.findViewById(R.id.group_name);
            agree_tv = (TextView)view.findViewById(R.id.agree_tv);
            agree_btn = (TextView)view.findViewById(R.id.agree_btn);
            disagree_btn = (TextView)view.findViewById(R.id.disagree_btn);
            return view;
        }
    }

    /**
     * 处理加入申请
     * @param data
     * @param processType 1表示同意，2表示拒绝
     */
    private void processInvite(ApiEntity.Message data,int processType){
        Type types = new TypeToken<GroupMessage>() {
        }.getType();
        GroupMessage groupMessage = new Gson().fromJson(data.Content, types);
        API.TalkerAPI.DoJoinGroupUser(data.GroupID,groupMessage.UserID,processType,data.Content,data.ID,groupMessage.GroupName, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(TeamActivity.this,"网络错误，请稍后重试！");
                Logger.d(TAG,"onError()->msg" + ex.getMessage());
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                if("1".equals(result)){
                    getInviteMsg(group.ID + "");
                }else{
                    ToastUtil.showToast(TeamActivity.this,"网络错误，请稍后重试！");
                }
            }
        });
    }
}
