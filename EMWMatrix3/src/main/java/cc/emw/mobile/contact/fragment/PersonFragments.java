package cc.emw.mobile.contact.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.AddFriendActivity;
import cc.emw.mobile.contact.ContactFrament;
import cc.emw.mobile.contact.SaoYiSaoActivity;
import cc.emw.mobile.contact.adapter.ConcernAdapter;
import cc.emw.mobile.contact.adapter.PersonAdapters;
import cc.emw.mobile.contact.decoration.TitleItemDecoration;
import cc.emw.mobile.contact.widget.IndexBar;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SwipeBackRecyclerView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_persons)
public class PersonFragments extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.rv)
    private SwipeBackRecyclerView mRv;
    //    private AutoLoadRecyclerView mRv;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mLvSearch;
    @ViewInject(R.id.tvSideBarHint)
    private TextView mTvSideBarHint;
    @ViewInject(R.id.indexBar)
    private IndexBar mIndexBar;
    @ViewInject(R.id.ic_tv_add_chat)
    private FloatingActionButton mImgAdd;

    private LinearLayoutManager mManager;
    private PersonAdapters mAdapter;
    private ConcernAdapter mSearchAdapter;
    private TitleItemDecoration mDecoration;
    private List<UserInfo> mAllDataList = new ArrayList<>();
    private List<UserInfo> mSearchList = new ArrayList<>();
    private List<UserInfo> mConcernList = new ArrayList<>();
    private EditText mSearchEt; // 搜索框
    public static final String ACTION_REFRESH_CONTACT_LIST = "cc.emw.mobile.refresh_follow_list"; // 刷新的action
    public static final String ACTION_PERSON_IS_BOTTOM = "person_is_scroll_bottom";
    private MyBroadcastReceive mReceive;
    private CharacterParser characterParser = CharacterParser.getInstance();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 114:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    class MyBroadcastReceive extends BroadcastReceiver {
        // TODO
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CONTACT_LIST.equals(action)) {
                getPersonList();
            } else if (ACTION_PERSON_IS_BOTTOM.equals(action)) {
                if (intent.hasExtra("enable")) {
                    mRv.setEnableGesture(false);
                } else {
                    mRv.setEnableGesture(true);
                    //mRv.updateSwipeBackState();
                }
            } else if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String s = intent.getStringExtra("keyword");
                if (currentItem == 2 && !TextUtils.isEmpty(s.trim())) {
                    mSearchList.clear();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mAllDataList.size(); i++) {
                        UserInfo userInfo = mAllDataList.get(i);
                        String name = userInfo.Name;
                        name = name.replaceAll(" ", "").trim();
                        if (name != null) {
                            String selling = characterParser.getSelling(name.toLowerCase());
                            sb.delete(0, sb.length());
                            for (int j = 0; j < name.length(); j++) {
                                String substring = name.substring(j, j + 1);
                                substring = characterParser.convert(substring);
                                if (substring != null && substring.length() >= 1) {
                                    substring = substring.substring(0, 1);
                                    sb.append(substring);
                                }
                            }
                            if (name.contains(s.toString().trim()) || selling.contains(s.toString().trim().toLowerCase())
                                    || sb.toString().contains(s.toString().trim().toLowerCase())) {
                                mSearchList.add(mAllDataList.get(i));
                            }
                        }
                    }
                    mRv.setVisibility(View.GONE);
                    mLvSearch.setVisibility(View.VISIBLE);
                    mSearchAdapter.setDataList((ArrayList<UserInfo>) mSearchList);
                    mSearchAdapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH_BACK)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String s = intent.getStringExtra("keyword");
                if (currentItem == 2 && TextUtils.isEmpty(s)) {
                    mLvSearch.setVisibility(View.GONE);
                    mRv.setVisibility(View.VISIBLE);
                    mSearchList.clear();
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        mSearchEt = (EditText) getParentFragment().getView().findViewById(R.id.et_search_keywords);
        mSearchEt = (EditText) getActivity().findViewById(R.id.et_search_keywords);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_CONTACT_LIST);
        intentFilter.addAction(ACTION_PERSON_IS_BOTTOM);
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH);
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH_BACK);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
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

        mRv.setEnableGesture(false);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(getActivity()));
        mAdapter = new PersonAdapters(getActivity(), mAllDataList);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new TitleItemDecoration(getActivity(), mAllDataList));
//        mRv.setOnPauseListenerParams(mAdapter.getImageLoader(), true, true);
        //搜索
        mSearchAdapter = new ConcernAdapter(getActivity());
        mLvSearch.setAdapter(mSearchAdapter);
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
//        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        refresh();

        if (EMWApplication.personMap != null && EMWApplication.personMap.size() > 0) {
            dealDatas(EMWApplication.personSortList);
        } else {
            getPersonList();
        }

        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSheetDialog dialog = new ActionSheetDialog(
                        getActivity()).builder();
                dialog.addSheetItem("选择联系人", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                                intent.putExtra("start_anim", false);
                                startActivity(intent);
                            }
                        });
                dialog.addSheetItem("扫一扫", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(getActivity(), SaoYiSaoActivity.class);
                                intent.putExtra("start_anim", false);
                                intent.putExtra("mSelector", 1);
                                startActivity(intent);
                            }
                        });
                dialog.show();
            }
        });
        mImgAdd.attachToRecyclerView(mRv);
    }

    @Override
    public void onFirstUserVisible() {

    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mRv.getVisibility() == View.VISIBLE ? mRv : mLvSearch, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getPersonList();
            }
        });
    }

    /**
     * 得到全部联系人列表
     */
    public void getPersonList() {
        API.UserAPI.SearchUser("", 0, false,
                new RequestCallback<UserInfo>(UserInfo.class, true) {

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        mPtrFrameLayout.refreshComplete();
                        ToastUtil.showToast(getActivity(), "获取人员列表失败");
                    }

                    @Override
                    public void onParseSuccess(final List<UserInfo> respList) {
                        mPtrFrameLayout.refreshComplete();
                        mSearchEt.setText("");
                        mSearchEt.clearFocus();
                        getOnlineList();
                        dealDatas(respList);
                    }
                });
    }

    /**
     * 获取在线人员列表
     */
    private void getOnlineList() {
        API.Message.GetOnlines(new RequestCallback<Integer>(Integer.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
//                ToastUtil.showToast(getActivity(), "获取在线人员失败");
            }

            @Override
            public void onParseSuccess(List<Integer> respInfo) {
                mPtrFrameLayout.refreshComplete();
                if (respInfo != null) {
                    mAdapter.setOnLineList(respInfo);
                    mSearchAdapter.setOnLineList(respInfo);
                    mAdapter.notifyDataSetChanged();
                    mSearchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 处理人员数据
     *
     * @param respList
     */
    private void dealDatas(final List<UserInfo> respList) {
        new Thread() {
            @Override
            public void run() {
                if (respList != null && respList.size() > 0) {
                    mAllDataList.clear();
                    mConcernList.clear();
                    for (int i = 0; i < respList.size(); i++) {
                        if (respList.get(i).IsFollow && respList.get(i).ID != PrefsUtil.readUserInfo().ID) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.ID = respList.get(i).ID;
                            userInfo.Sex = respList.get(i).Sex;
                            userInfo.Name = respList.get(i).Name;
                            userInfo.Image = respList.get(i).Image;
                            userInfo.IsFollow = respList.get(i).IsFollow;
                            userInfo.CompanyCode = respList.get(i).CompanyCode;
                            userInfo.Email = respList.get(i).Email;
                            userInfo.Phone = respList.get(i).Phone;
                            userInfo.Job = respList.get(i).Job;
                            userInfo.Axis = respList.get(i).Axis;
                            userInfo.setSortLetters("我关注的");
                            mConcernList.add(userInfo);
                        }
                    }

                    if (mConcernList.size() > 0) {
                        mAllDataList.addAll(mConcernList);
                    }

                    mAllDataList.addAll(respList);

                    mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                            .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                            .setNeedRealIndex(true)//设置需要真实的索引
                            .setmSourceDatas(mAllDataList);//设置数据源
                    mHandler.sendEmptyMessage(114);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive);
    }
}
