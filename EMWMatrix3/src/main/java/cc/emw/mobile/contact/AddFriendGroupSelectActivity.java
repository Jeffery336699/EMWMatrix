package cc.emw.mobile.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.PersonFriendFragment;
import cc.emw.mobile.entity.GroupElem;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;

@ContentView(R.layout.activity_add_friend_group_select)
public class AddFriendGroupSelectActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.rv_friend_group)
    private RecyclerView mRv;

    public static final String USER_ID = "user_id";
    private int userId;
    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<GroupElem> mGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        userId = getIntent().getIntExtra(USER_ID, 0);
        mTitle.setText("选择分组");
        getPersonalGroupNameByUser();
        adapter = new MyAdapter();
        adapter.setData(mGroups);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(linearLayoutManager);
        mRv.setAdapter(adapter);
        findViewById(R.id.cm_header_btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取分组
     */
    private void getPersonalGroupNameByUser() {
        API.UserPubAPI.GetPubConGroupsByUserId(PrefsUtil.readUserInfo().ID, new RequestCallback<GroupElem>(GroupElem.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AddFriendGroupSelectActivity.this, "获取个人联系人分组失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(final List<GroupElem> respList) {
                mGroups.clear();
                if (respList != null && respList.size() >= 0) {
                    GroupElem groupElem = new GroupElem();
                    groupElem.Name = "默认分组";
                    groupElem.ID = -1;
                    groupElem.UserID = PrefsUtil.readUserInfo().ID;
                    mGroups.add(groupElem);
                    mGroups.addAll(respList);
                    adapter.setData(mGroups);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddFriendGroupSelectActivity.this, "暂无个人分组", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * 添加好友
     */
    private void addFriend(ApiEntity.PubUserContacts pcObj) {
        API.UserPubAPI.AddPubUserContacts(pcObj, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AddFriendGroupSelectActivity.this, "添加失败,请稍后再试!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                if (result != null && !TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
                    if (Integer.valueOf(result) > 0) {
                        Toast.makeText(AddFriendGroupSelectActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent(PersonFriendFragment.ACTION_REFRESH_FRIEND_LIST));
                        finish();
                    } else {
                        Toast.makeText(AddFriendGroupSelectActivity.this, "添加失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<GroupElem> groupElems;

        public MyAdapter() {
            mInflater = LayoutInflater.from(AddFriendGroupSelectActivity.this);
        }

        public void setData(List<GroupElem> groupElems) {
            this.groupElems = groupElems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.personal_title_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTitle.setText(groupElems.get(position).Name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiEntity.PubUserContacts pcObj = new ApiEntity.PubUserContacts();
                    pcObj.UserID = PrefsUtil.readUserInfo().ID;
                    pcObj.ConID = userId;
                    pcObj.GroupID = groupElems.get(position).ID;
                    pcObj.Mome = "";
                    addFriend(pcObj);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupElems == null ? 0 : groupElems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }

    }

}
