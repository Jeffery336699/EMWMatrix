package io.rong.callkit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.CircleImageView;
import io.rong.calllib.RongCallCommon;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
//import io.rong.imlib.model.UserInfo;

/**
 * Created by weiqinxiao on 16/3/15.
 */
public class CallSelectMemberActivity extends Activity {
    List<String> selectList;
    ArrayList<String> selectedMember;
    ArrayList<String> allMembers;
    TextView txtvStart;
    ListAdapter mAdapter;
    ListView mList;
    RongCallCommon.CallMediaType mMediaType;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;
    private DisplayImageOptions options;

    public static final String EXTRA_SELECT_TYPE = "select_type";
    public static final int RADIO_SELECT = 1;
    public static final int MULTI_SELECT = 2;
    private int selectType;

    public static final String EXTRA_INTO_FLAG = "into_tag";
    public static final int FLAG_NORMAL = 0; //默认
    public static final int FLAG_CHAT_TEMP = 1; //从聊天发起临时多人语音/视频聊天
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, Color.WHITE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);*/

        setContentView(R.layout.rc_voip_activity_select_member);
//        RongContext.getInstance().getEventBus().register(this);
        selectType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, MULTI_SELECT);
        flag = getIntent().getIntExtra(EXTRA_INTO_FLAG, FLAG_NORMAL);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象

        txtvStart = (TextView) findViewById(R.id.rc_btn_ok);
        txtvStart.setEnabled(false);
        txtvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == FLAG_CHAT_TEMP) {
                    StringBuilder disName = new StringBuilder();
                    final ArrayList<String> userIdList = new ArrayList<>();
                    userIdList.add(Integer.toString(PrefsUtil.readUserInfo().ID));
                    disName.append(PrefsUtil.readUserInfo().Name).append("、");
                    try {
                        for (int i = 0, count = selectList.size(); i < count; i++) {
                            UserInfo userInfo = EMWApplication.personMap.get(Integer.valueOf(selectList.get(i)));
                            userIdList.add(Integer.toString(userInfo.ID));
                            disName.append(userInfo.Name);
                            if (i != count - 1) {
                                disName.append("、");
                            }
                        }
                    } catch (Exception e) {

                    }
                    RongIMClient.getInstance().createDiscussion(disName.toString(), userIdList, new RongIMClient.CreateDiscussionCallback() {
                        @Override
                        public void onSuccess(String s) {
                            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIAUDIO);
                            if (mMediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
                                intent.setAction(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIVIDEO);
                            }
                            intent.putExtra("conversationType", Conversation.ConversationType.DISCUSSION.getName().toLowerCase());
                            intent.putExtra("targetId", s);
                            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
                            intent.putStringArrayListExtra("invitedUsers", userIdList);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage(getPackageName());
                            getApplicationContext().startActivity(intent);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            if (mMediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
                                ToastUtil.showToast(getApplicationContext(), "创建视频聊天失败:" + errorCode.getValue());
                            } else {
                                ToastUtil.showToast(getApplicationContext(), "创建语音聊天失败:" + errorCode.getValue());
                            }
                        }
                    });
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("invited", selectedMember);
                    setResult(RESULT_OK, intent);
                    CallSelectMemberActivity.this.finish();
                }
            }
        });
        findViewById(R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                CallSelectMemberActivity.this.finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        selectedMember = new ArrayList<>();

        Intent intent = getIntent();
        int type = intent.getIntExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
        mMediaType = RongCallCommon.CallMediaType.valueOf(type);
        final ArrayList<String> invitedMembers = intent.getStringArrayListExtra("invitedMembers");
        if (flag == FLAG_CHAT_TEMP) {
            if (EMWApplication.personSortList != null && EMWApplication.personSortList.size() > 0) {
                allMembers = new ArrayList<>();
                for (UserInfo user : EMWApplication.personSortList) {
                    if (user != null) {
                        allMembers.add(Integer.toString(user.ID));
                    }
                }
            }
        } else {
            allMembers = intent.getStringArrayListExtra("allMembers");
        }
        String groupId = intent.getStringExtra("groupId");
        /*RongCallKit.GroupMembersProvider provider = RongCallKit.getGroupMemberProvider();
        if (groupId != null && allMembers == null && provider != null) {
            allMembers = provider.getMemberList(groupId, new RongCallKit.OnGroupMembersResult() {
                @Override
                public void onGotMemberList(ArrayList<String> members) {
                    if (mAdapter != null) {
                        if (members != null && members.size() > 0) {
                            mAdapter.setAllMembers(members);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }*/
        if (groupId != null && allMembers == null) {
            GroupInfo group = null;
            try {
                group = EMWApplication.groupMap.get(Integer.valueOf(groupId));
            } catch (Exception e) {
            }
            allMembers = new ArrayList<>();
            if (group != null && group.Users != null && group.Users.size() > 0) {
                for (ApiEntity.UserInfo user : group.Users){
                    if (user != null) {
                        allMembers.add(Integer.toString(user.ID));
                    }
                }
            }
        }

        if (allMembers == null) {
            allMembers = invitedMembers;
        }

        mList = (ListView) findViewById(R.id.rc_listview_select_member);
        mRecyclerView = (RecyclerView) findViewById(R.id.rc_recyclerview_select_member);
        if (invitedMembers != null && invitedMembers.size() > 0) {
            mAdapter = new ListAdapter(allMembers, invitedMembers);
            mList.setAdapter(mAdapter);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = view.findViewById(R.id.rc_checkbox);
                    String userId = (String) v.getTag();
                    if (!invitedMembers.contains(userId)) {
                        if (mMediaType.equals(RongCallCommon.CallMediaType.VIDEO)
                                && !v.isSelected() && selectedMember.size() + invitedMembers.size() >= 9) {
                            Toast.makeText(CallSelectMemberActivity.this, "您最多只能选择9人", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (mMediaType.equals(RongCallCommon.CallMediaType.AUDIO)
                                && !v.isSelected() && selectedMember.size() + invitedMembers.size() >= 13) {
                            Toast.makeText(CallSelectMemberActivity.this, "您最多只能选择12人", Toast.LENGTH_SHORT).show();
                        }
                        if (selectType == RADIO_SELECT) {
                            if (!selectedMember.contains(userId)) {
                                if (selectedMember.size() > 0) {
                                    String lastId = selectedMember.get(0);
                                    View checkbox = mList.getChildAt(allMembers.indexOf(lastId)).findViewById(R.id.rc_checkbox);
                                    checkbox.setSelected(false);
                                    selectedMember.remove(lastId);
                                    selectList.remove(lastId);
                                    mRecyclerViewAdapter.notifyDataSetChanged();
                                }
                                v.setSelected(true);
                                selectedMember.add(userId);
                                selectList.add(userId);
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (selectedMember.contains(userId)) {
                                selectedMember.remove(userId);
                                int index = selectList.indexOf(userId);
                                selectList.remove(index);
                                mRecyclerViewAdapter.notifyItemRemoved(index);
                            }

                            v.setSelected(!v.isSelected());
                            if (v.isSelected()) {
                                selectedMember.add(userId);
                                selectList.add(userId);
                                mRecyclerViewAdapter.notifyItemInserted(selectList.size() - 1);
                                mRecyclerView.scrollToPosition(selectList.size() - 1);
                            }
                        }
                        if (selectedMember.size() > 0) {
                            txtvStart.setEnabled(true);
                            txtvStart.setTextColor(getResources().getColor(R.color.rc_voip_check_enable));
                        } else {
                            txtvStart.setEnabled(false);
                            txtvStart.setTextColor(getResources().getColor(R.color.rc_voip_check_disable));
                        }
                    }
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
//            mRecyclerView.addItemDecoration(new DynamicAdapter.SpaceItemDecoration(DisplayUtil.dip2px(this, 10)));
            selectList = new ArrayList<>();
            if (invitedMembers != null) {
                selectList.addAll(invitedMembers);
            }
            selectList.addAll(selectedMember);
            mRecyclerViewAdapter = new RecyclerViewAdapter(this, selectList);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }

    }

    @Override
    protected void onDestroy() {
//        RongContext.getInstance().getEventBus().unregister(this);
        super.onDestroy();
    }

    class ListAdapter extends BaseAdapter {
        List<String> allMembers;
        List<String> invitedMembers;
        int selectType;

        public ListAdapter(List<String> allMembers, List<String> invitedMembers) {
            this.allMembers = allMembers;
            this.invitedMembers = invitedMembers;
        }

        public void setSelectType(int selectType) {
            this.selectType = selectType;
        }

        public void setAllMembers(List<String> allMembers) {
            this.allMembers = allMembers;
        }

        @Override
        public int getCount() {
            return allMembers.size();
        }

        @Override
        public Object getItem(int position) {
            return allMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(CallSelectMemberActivity.this).inflate(R.layout.rc_voip_listitem_select_member, null);
                holder.checkbox = (ImageView) convertView.findViewById(R.id.rc_checkbox);
                holder.portrait = (CircleImageView) convertView.findViewById(R.id.rc_user_portrait);
                holder.name = (TextView) convertView.findViewById(R.id.rc_user_name);
                convertView.setTag(holder);
            }

            holder = (ViewHolder)convertView.getTag();
            holder.checkbox.setTag(allMembers.get(position));
            if (invitedMembers.contains(allMembers.get(position))) {
                holder.checkbox.setClickable(false);
                holder.checkbox.setEnabled(false);
                holder.checkbox.setImageResource(R.drawable.rc_voip_icon_checkbox_checked);
            } else {
                if (selectedMember.contains(allMembers.get(position))) {
                    holder.checkbox.setImageResource(R.drawable.rc_voip_checkbox);
                    holder.checkbox.setSelected(true);
                } else {
                    holder.checkbox.setImageResource(R.drawable.rc_voip_checkbox);
                    holder.checkbox.setSelected(false);
                }
                holder.checkbox.setClickable(true);
                holder.checkbox.setEnabled(true);
            }

            /*UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(allMembers.get(position));
            if (userInfo != null) {
                holder.name.setText(userInfo.getName());
                holder.portrait.setAvatar(userInfo.getPortraitUri());

            } else {*/
            int uid = Integer.valueOf(allMembers.get(position));
            UserInfo userInfo = EMWApplication.personMap.get(uid);
            if (userInfo != null) {
                holder.name.setText(userInfo.Name);
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                    url = Const.DOWN_ICON_URL2 + userInfo.Image;
                }
                holder.portrait.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 32);
                ImageLoader.getInstance().displayImage(url, new ImageViewAware(holder.portrait), options, new ImageSize(DisplayUtil.dip2px(CallSelectMemberActivity.this, 40), DisplayUtil.dip2px(CallSelectMemberActivity.this, 40)), null, null);
            } else {
                holder.name.setText(allMembers.get(position));
                holder.portrait.setTextBg(EMWApplication.getIconColor(uid), "", 32);
            }
            return convertView;
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerHolder> {
        private List<String> dataList;
        private Context mContext;
        private RecyclerView recyclerView;

        public RecyclerViewAdapter(Context context, List<String> dataList) {
            mContext = context;
            this.dataList = dataList;
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rc_voip_listitem_select_portrait, parent, false);
            RecyclerHolder holder = new RecyclerHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, final int position) {
            int uid = Integer.valueOf(dataList.get(position));
            UserInfo userInfo = EMWApplication.personMap.get(uid);
            if (userInfo != null) {
                String url = String.format(Const.DOWN_ICON_URL, TextUtils.isEmpty(userInfo.CompanyCode) ? PrefsUtil.readUserInfo().CompanyCode : userInfo.CompanyCode, userInfo.Image);
                if (userInfo.Image != null && userInfo.Image.startsWith("/")) {
                    url = Const.DOWN_ICON_URL2 + userInfo.Image;
                }
                holder.textView.setTextBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 62);
                ImageLoader.getInstance().displayImage(url, holder.textView, options);
            } else {
                holder.textView.setTextBg(EMWApplication.getIconColor(uid), "", 62);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.recyclerView = recyclerView;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class RecyclerHolder extends RecyclerView.ViewHolder {
            CircleImageView textView;

            public RecyclerHolder(View itemView) {
                super(itemView);
                textView = (CircleImageView) itemView.findViewById(R.id.rc_user_portrait);
            }
        }
    }

    /*public void onEventMainThread(UserInfo userInfo) {
        if (mList != null) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;

            while (++index <= last && index >= 0 && index < mAdapter.getCount()) {
                if (mAdapter.getItem(index).equals(userInfo.getUserId())) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount()), mList);
                }
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewHolder {
        ImageView checkbox;
        CircleImageView portrait;
        TextView name;
    }
}
