package cc.emw.mobile.contact.fragment;

import cc.emw.mobile.util.CharacterParser;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.adapter.PersonnelAdapter;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PersonnelComparator;

/**
 * @author zrjt
 * @version 2016-3-8 下午3:42:37
 */
@SuppressLint("DefaultLocale")
@ContentView(R.layout.fragment_person)
public class PersonFragment extends BaseFragment implements ContactView {

    // @ViewInject(R.id.load_more_list_view_ptr_frame)
    // private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 小组列表
    @ViewInject(R.id.contact_sidebar)
    private LinearLayout sideLayout;

    @ViewInject(R.id.tv_sidebar_tag)
    private TextView sidebarTag;
    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt; // 搜索框
    private ContactPresenter contactPresenter;
    private ArrayList<UserInfo> mDataList; // 按部门分类后的数据集合
    private ArrayList<UserInfo> mSearchList = new ArrayList<UserInfo>(); // 搜索后的数据集合
    private PersonnelAdapter personnelAdapter;
    public static final String ACTION_REFRESH_CONTACT_LIST = "cc.emw.mobile.refresh_follow_list"; // 刷新的action
    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver {
        // TODO
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_CONTACT_LIST.equals(action)) {
                mPtrFrameLayout.autoRefresh(false);
            }
        }
    }

    @Override
    public void onUserInvisible() {
        sidebarTag.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // View view = inflater.inflate(R.layout.fragment_person, null);
    // init(view);
    // return view;
    // }

    private void init(View view) {
        mPtrFrameLayout = (PtrFrameLayout) view
                .findViewById(R.id.load_more_list_view_ptr_frame);
        mDataList = new ArrayList<UserInfo>();
        contactPresenter = new ContactPresenter(this);
        IntentFilter intentFilter = new IntentFilter(
                ACTION_REFRESH_CONTACT_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        personnelAdapter = new PersonnelAdapter(getActivity());

        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);

        refresh();

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                for (int i = 0; i < mDataList.size(); i++) {
                    String convert = CharacterParser.getInstance().convert(mDataList.get(i).Name);
                    if (mDataList.get(i).Name.contains(s) || mDataList.get(i).getSortLetters().equals(s)) {
                        mSearchList.add(mDataList.get(i));
                    }
                }
                if (mSearchList != null) {
                    personnelAdapter.setData(mSearchList);
                    personnelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                // TODO Auto-generated method stub
                personnelAdapter.setData(mDataList);
                personnelAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });

    }

    @Override
    public void showTipDialog(String tip) {
    }

    @Override
    public void showUserInfo(List<UserInfo> simpleUsers) {
        mSearchEt.setVisibility(View.VISIBLE);
        mDataList.clear();
        ArrayList<UserInfo> followLists = new ArrayList<UserInfo>();
        ArrayList<UserInfo> allUsers = new ArrayList<UserInfo>();
        followLists.clear();
        allUsers.clear();
        mDataList.clear();
        // Collections.addAll(mDataList, new UserInfo[simpleUsers.size() *
        // 2]);
        // Collections.copy(mDataList, simpleUsers);
        // Collections.addAll(allUsers, new UserInfo[simpleUsers.size()]);
        // Collections.copy(allUsers, simpleUsers);
        allUsers.addAll(simpleUsers);
        mDataList.addAll(simpleUsers);
        for (UserInfo followUser : allUsers) {
            if (followUser.IsFollow == true) {
                UserInfo UserInfo = new UserInfo();
                UserInfo.DeptID = -1;
                UserInfo.DeptName = "我关注的";
                UserInfo.Email = followUser.Email;
                UserInfo.IsOnline = followUser.IsOnline;
                UserInfo.ID = followUser.ID;
                UserInfo.Image = followUser.Image;
                UserInfo.Name = followUser.Name;
                UserInfo.Phone = followUser.Phone;
                UserInfo.IsFollow = true;
                followLists.add(UserInfo);
            }
        }
        mDataList.addAll(followLists);
        // textView.setVisibility(View.VISIBLE);
        // textView.setText(mDataList.get(0).toString());
        PersonnelComparator<UserInfo> personnelComparator = new PersonnelComparator<UserInfo>();
        Collections.sort(mDataList, personnelComparator);
        personnelAdapter.setData(mDataList);
        personnelAdapter.setType(0);
        personnelAdapter.notifyDataSetChanged();
        mListView.setAdapter(personnelAdapter);
        sideLayout.removeAllViews();
        for (int i = 0; i < mDataList.size(); i++) {
            final UserInfo user = mDataList.get(i);
            if (i > 0 && mDataList.get(i - 1).DeptID == user.DeptID) {
            } else {
                TextView tvSide = new TextView(getActivity());
                if (user.DeptName.length() > 1) {
                    tvSide.setText(user.DeptName.substring(0, 1));
                } else {
                    tvSide.setText("其");
                }

                tvSide.setTextColor(getResources().getColor(
                        R.color.main_bottom_tab_textcolor_normal));
                tvSide.setPadding(5, 5, 5, 5);
                tvSide.setTag(i);
                tvSide.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent arg1) {
                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_UP:
                                sidebarTag.setVisibility(View.GONE);
                                return true;
                            case MotionEvent.ACTION_DOWN:
                                mListView.setSelection(Integer.valueOf(v.getTag()
                                        + ""));
                                sidebarTag.setVisibility(View.VISIBLE);
                                if (user.DeptName.length() > 1) {
                                    sidebarTag.setText(user.DeptName
                                            .substring(0, 1));
                                } else {
                                    sidebarTag.setText("其");
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                break;
                        }
                        return false;
                    }
                });
                sideLayout.addView(tvSide);
            }
        }
        sideLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });
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
                contactPresenter.getPersonList("");
            }
        });
    }

    @Override
    public void onFirstUserVisible() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    @Override
    public void showProgressDialog() {
        // dialog = createLoadingDialog("正在加载");
        // dialog.show();
    }

    @Override
    public void disProgressDialog() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void showFollowResult(String result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showGroupInfo(List<GroupInfo> groupInfos) {
        // TODO Auto-generated method stub

    }
}
