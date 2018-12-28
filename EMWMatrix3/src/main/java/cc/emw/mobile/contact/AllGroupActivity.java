package cc.emw.mobile.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.GroupAllAdapter;
import cc.emw.mobile.contact.adapter.PersonAdapter;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PinyinComparatorGroups;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SideBar;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

@ContentView(R.layout.activity_all_group)
public class AllGroupActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.contact_elv_person)
    private ExpandableListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    private GroupAllAdapter adapter;
    CharacterParser characterParser;

    private MyBroadcastReceiver receicer;
    public static final String ACTION_REFRESH_GROUP = "cc.emw.mobile.refresh.group";

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction() == ACTION_REFRESH_GROUP) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void init() {
        mTitle.setText("查找团队");
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAddInGroup();
            }
        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        adapter = new GroupAllAdapter(this);
        mListView.setAdapter(adapter);
        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelectedGroup(position);
                }
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }

    private void getAddInGroup() {

        API.TalkerAPI.LoadGroups("", true, 0,false, new RequestCallback<GroupInfo>(
                GroupInfo.class) {

            @Override
            public void onStarted() {
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(AllGroupActivity.this, "加载群组列表失败");
            }

            @Override
            public void onParseSuccess(List<GroupInfo> respList) {
                mPtrFrameLayout.refreshComplete();
                if (respList != null && respList.size() > 0) {
                    characterParser = CharacterParser.getInstance();
                    sortData((ArrayList<GroupInfo>) respList);
                    PinyinComparatorGroups pinyinComparator = new PinyinComparatorGroups();
                    Collections.sort(respList, pinyinComparator);// 根据a-z进行排序源数据
                    List<GroupInfo> addInGroups = new ArrayList<>();
                    if (EMWApplication.groupMap == null) {
                        EMWApplication.groupMap = new SparseArray<>();
                    }
                    EMWApplication.groupMap.clear();
                    for (GroupInfo groupInfo : respList) {
                        EMWApplication.groupMap.put(groupInfo.ID,
                                groupInfo);
                        addInGroups.add(groupInfo);
                    }
                    if (addInGroups != null && addInGroups.size() > 0) {
                        adapter.setDataList((ArrayList<GroupInfo>) addInGroups);
                        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
                            mListView.expandGroup(i);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
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
            unregisterReceiver(receicer); // 取消监听
        super.onDestroy();
    }

}
