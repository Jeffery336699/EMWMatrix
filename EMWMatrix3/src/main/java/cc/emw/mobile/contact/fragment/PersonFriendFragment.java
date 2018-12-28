package cc.emw.mobile.contact.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.adapter.PersonalUserAdapter;
import cc.emw.mobile.contact.base.RecyclerViewData;
import cc.emw.mobile.contact.listener.OnRecyclerViewListener;
import cc.emw.mobile.entity.GroupElem;
import cc.emw.mobile.entity.PersonalGroup;
import cc.emw.mobile.entity.PersonalGroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_person_friend)
public class PersonFriendFragment extends BaseFragment implements OnRecyclerViewListener.OnItemClickListener, OnRecyclerViewListener.OnItemLongClickListener {

    @ViewInject(R.id.rv_person_friend)
    private RecyclerView mRv;

    public static final String ACTION_REFRESH_FRIEND_LIST = "action_refresh_friend_list";
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    private List<RecyclerViewData> mDatas = new ArrayList<>();
    private PersonalUserAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<GroupElem> mGroups = new ArrayList<>();
    private List<PersonalGroup> personalGroups = new ArrayList<>();
    private MyBroadcastReceive mReceive;

    public PersonFriendFragment() {
    }

    @Override
    public void onFirstUserVisible() {
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRv, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getPersonalGroupNameByUser();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initRefreshHead();
        init();
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getPersonalGroupNameByUser();
        }
    }

    private void init() {
        mReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_FRIEND_LIST);
        getActivity().registerReceiver(mReceive, intentFilter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(linearLayoutManager);
//        mRv.addItemDecoration(new SimplePaddingDecoration(getActivity()));

        mAdapter = new PersonalUserAdapter(getActivity(), mDatas);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        mRv.setAdapter(mAdapter);

        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        ImageLoader.getInstance().resume();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ImageLoader.getInstance().pause();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ImageLoader.getInstance().pause();
                        break;
                }
            }
        });
    }

    private void initRefreshHead() {
        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setToggleMenu(true);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
    }

    /**
     * 获取分组
     */
    private void getPersonalGroupNameByUser() {
        API.UserPubAPI.GetPubConGroupsByUserId(PrefsUtil.readUserInfo().ID, new RequestCallback<GroupElem>(GroupElem.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                Toast.makeText(getActivity(), "获取个人联系人分组失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(final List<GroupElem> respList) {
                if (respList != null && respList.size() > 0) {
                    mGroups.clear();
                    mGroups.addAll(respList);
                } else {
                    Toast.makeText(getActivity(), "暂无个人分组", Toast.LENGTH_SHORT).show();
                }
                getPersonList();
            }

        });
    }

    /**
     * 获取个人联系人
     */
    private void getPersonList() {
        API.UserPubAPI.GetPubUserContactsListByUserId(PrefsUtil.readUserInfo().ID, new RequestCallback<PersonalGroupInfo>(PersonalGroupInfo.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                Toast.makeText(getActivity(), "获取个人联系人失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<PersonalGroupInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {

                    personalGroups.clear();
                    mDatas.clear();

                    //没有分组
                    PersonalGroup personalUnGroup = new PersonalGroup();
                    List<UserInfo> userInfosUn = new ArrayList<>();
                    for (int i = 0; i < respList.size(); i++) {
                        if (respList.get(i).GroupID == 0 || respList.get(i).GroupID == -1) {
                            userInfosUn.add(respList.get(i).ConUser);
                        }
                    }
                    personalUnGroup.GroupMembers = new ArrayList<>();
                    personalUnGroup.GroupMembers.addAll(userInfosUn);
                    personalUnGroup.GroupName = "默认分组" + "( " + personalUnGroup.GroupMembers.size() + "人 )";
                    personalGroups.add(personalUnGroup);
                    //有分组
                    for (int i = 0; i < mGroups.size(); i++) {
                        PersonalGroup personalGroup = new PersonalGroup();
                        personalGroup.GroupId = mGroups.get(i).ID;
                        String groupName = mGroups.get(i).Name;
                        List<UserInfo> userInfos = new ArrayList<>();
                        for (int j = 0; j < respList.size(); j++) {
                            if (mGroups.get(i).ID == respList.get(j).GroupID) {
                                userInfos.add(respList.get(j).ConUser);
                            }
                        }
                        personalGroup.GroupMembers = new ArrayList<>();
                        personalGroup.GroupMembers.addAll(userInfos);
                        personalGroup.GroupName = groupName + "( " + personalGroup.GroupMembers.size() + "人 )";
                        personalGroups.add(personalGroup);
                    }

                    for (int i = 0; i < personalGroups.size(); i++) {
                        mDatas.add(new RecyclerViewData(personalGroups.get(i).GroupName, personalGroups.get(i).GroupMembers, true));
                    }
                    mAdapter.setAllDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                } else {

                }
            }
        });
    }


    @Override
    public void onGroupItemClick(int position, int groupPosition, View view) {

    }

    @Override
    public void onChildItemClick(int position, int groupPosition, int childPosition, View view) {
        Intent intent = new Intent(getActivity(), PersonInfoActivity.class);
        intent.putExtra("UserInfo", personalGroups.get(groupPosition).GroupMembers.get(childPosition));
        intent.putExtra("intoTag", 1);
        intent.putExtra("start_anim", false);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        intent.putExtra("click_pos_y", location[1]);
        startActivity(intent);
    }

    @Override
    public void onGroupItemLongClick(int position, int groupPosition, View view) {

    }

    @Override
    public void onChildItemLongClick(int position, int groupPosition, int childPosition, View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive);
    }
}
