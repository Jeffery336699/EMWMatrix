package cc.emw.mobile.contact.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.ContactFrament;
import cc.emw.mobile.contact.GroupsCreateActivitys;
import cc.emw.mobile.contact.adapter.GroupAdapters;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CacheUtils;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PinyinComparatorGroups;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SwipeBackGridView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.　圈子协同fragment
 */
@ContentView(R.layout.fragment_group)
public class GroupFragment extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.group_grid_view_member)
    private SwipeBackGridView mListView;
    //    private GridView mListView;
    @ViewInject(R.id.iv_add_group)
    private FloatingActionButton imageView;
    private GroupAdapters adapter;
    public static final String ACTION_REFRESH_GROUP = "cc.emw.mobile.refresh.group";
    public static final String ACTION_GROUP_IS_BOTTOM = "group_is_scroll_bottom";
    private MyBroadcastReceiver receicer;
    private List<GroupInfo> mDataList = new ArrayList<>();
    private List<GroupInfo> mGroupInfos = new ArrayList<>();
    private List<GroupInfo> uGroupInfos = new ArrayList<>();
    private List<GroupInfo> gSearchList = new ArrayList<>();
    private EditText mSearchEt; // 搜索框
    private boolean flag = true;
    private CharacterParser characterParser = CharacterParser.getInstance();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 10) {
                flag = false;
            }
            if (flag) {
                flag = false;
                mPtrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.autoRefresh(false);
                    }
                }, 100);
            }
            switch (msg.what) {
                case 1:
                    if (msg.obj != null) {
                        mDataList.clear();
                        mDataList.addAll((Collection<? extends GroupInfo>) msg.obj);
                        adapter.setData(mDataList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 114:
                    adapter.setData(mDataList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(ACTION_REFRESH_GROUP)) {
                getAllGroup();
            } else if (arg1.getAction().equals(ACTION_GROUP_IS_BOTTOM)) {
                if (arg1.hasExtra("enable")) {
                    mListView.setEnableGesture(false);
                } else {
                    mListView.setEnableGesture(true);
                    mListView.updateSwipeBackState();
                }
            } else if (arg1.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH)) {
                int currentItem = arg1.getIntExtra("currentItem", -1);
                String s = arg1.getStringExtra("keyword");
                if (!TextUtils.isEmpty(s) && currentItem == 1) {
                    gSearchList.clear();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mDataList.size(); i++) {
                        GroupInfo userInfo = mDataList.get(i);
                        if (userInfo.Name != null && userInfo.Name.length() > 1) {
                            String name = userInfo.Name.trim();
                            name = name.replace(" ", "");
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
                                gSearchList.add(mDataList.get(i));
                            }
                        }
                    }
                    adapter.setData(gSearchList);
                    adapter.notifyDataSetChanged();
                }
            } else if (arg1.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH_BACK)) {//群组消息
                int currentItem = arg1.getIntExtra("currentItem", -1);
                String s = arg1.getStringExtra("keyword");
                if (currentItem == 1 && TextUtils.isEmpty(s)) {
                    adapter.setData(mDataList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public GroupFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAllGroup();
            }
        });
    }

    @Override
    public void onFirstUserVisible() {
    }

    private void init() {
        mSearchEt = (EditText) getActivity().findViewById(R.id.et_search_keywords);
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

        mListView.setEnableGesture(false);
        adapter = new GroupAdapters(getActivity());
        mListView.setAdapter(adapter);

//        mListView.setOnScrollListener(new PauseOnScrollListener(com.nostra13.universalimageloader.core.ImageLoader.getInstance(), true, true));

        refresh();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupsCreateActivitys.class);
                intent.putExtra("start_anim", false);
                startActivity(intent);
            }
        });
        imageView.attachToListView(mListView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ListViewFragment", "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ListViewFragment", "onScroll()");
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_GROUP);
        intentFilter.addAction(ACTION_GROUP_IS_BOTTOM);
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH);
        intentFilter.addAction(ContactFrament.ACTION_CONTACT_SEARCH_BACK);
        receicer = new MyBroadcastReceiver();
        getActivity().registerReceiver(receicer, intentFilter); // 注册监听

//        mListView.setOnScrollListener(new PauseOnScrollListener(adapter.getImageLoader(), true, true));

        readCache();
    }

    /**
     * 读取缓存
     */
    private void readCache() {
        new Thread() {
            @Override
            public void run() {
                List<GroupInfo> mDataLists = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "mDataList",
                        new TypeToken<List<GroupInfo>>() {
                        }.getType());
                Message message = mHandler.obtainMessage();
                if (mDataLists != null && mDataLists.size() > 0) {
                    message.arg1 = 10;
                }
                message.what = 1;
                message.obj = mDataLists;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private void getAllGroup() {

        API.TalkerAPI.LoadGroups("", true, 0, false, new RequestCallback<GroupInfo>(
                GroupInfo.class) {

            @Override
            public void onStarted() {
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(getActivity(), "加载群组列表失败");
            }

            @Override
            public void onParseSuccess(final List<GroupInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                mSearchEt.setText("");
                mSearchEt.clearFocus();
                new Thread() {
                    @Override
                    public void run() {
                        if (respList != null && respList.size() > 0) {
                            mGroupInfos.clear();
                            uGroupInfos.clear();
                            characterParser = CharacterParser.getInstance();
                            sortData((ArrayList<GroupInfo>) respList);
                            PinyinComparatorGroups pinyinComparator = new PinyinComparatorGroups();
                            Collections.sort(respList, pinyinComparator);// 根据a-z进行排序源数据
                            mDataList.clear();
                            for (GroupInfo groupInfo : respList) {
                                if (groupInfo.IsAddIn) {
                                    boolean flag = true;
                                    for (int i = 0; i < groupInfo.Users.size(); i++) {
                                        if (groupInfo.Users.get(i).ID == groupInfo.CreateUser) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (EMWApplication.personMap != null && EMWApplication.personMap.get(groupInfo.CreateUser) != null && flag) {
                                        groupInfo.Users.add(EMWApplication.personMap.get(groupInfo.CreateUser));
                                        mGroupInfos.add(groupInfo);
                                    } else {
                                        mGroupInfos.add(groupInfo);
                                    }
                                } else {
                                    uGroupInfos.add(groupInfo);
                                }
                            }
                            mDataList.addAll(mGroupInfos);
                            mDataList.addAll(uGroupInfos);
                            mHandler.sendEmptyMessage(114);
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "mDataList", mDataList);
                        }
                    }
                }.start();
            }
        });
    }

    private void sortData(ArrayList<GroupInfo> respList) {
        for (int i = 0; i < respList.size(); i++) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(respList.get(i).Name);
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    respList.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    respList.get(i).setSortLetters("#");
                }
            } else {
                respList.get(i).setSortLetters("#");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (receicer != null)
            getActivity().unregisterReceiver(receicer); // 取消监听
        super.onDestroy();
    }
}
