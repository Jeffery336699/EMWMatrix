package cc.emw.mobile.me.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.me.adapter.WaitTaskAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我·待办工作》任务Fragment
 *
 * @author zrjt
 */
@ContentView(R.layout.fragment_task_handle)
public class WaitHandleTaskFragment extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 未处理工作列表
    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mBlankLayout;
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout llNetWork;

    public static EditText mSearchEt;
    private WaitTaskAdapter adapter; // 未处理工作adapter
    private ArrayList<UserFenPai> mDataList; // 未处理工作列表数据
    private List<ApiEntity.UserFenPai> gSearchList = new ArrayList<>();
    private int page = PAGE_FIRST; //
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        mSearchEt = (EditText) getActivity().findViewById(R.id.et_search_keyword);
    }


    private void initView() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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
                page = PAGE_FIRST;
                getWaitList(1, page, 10);
            }
        });

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

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(true);

        mDataList = new ArrayList<UserFenPai>();
        adapter = new WaitTaskAdapter(getActivity(), true);
        adapter.setData(mDataList);
        mListView.setAdapter(adapter);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                page++;
                getWaitList(1, page, 10);
            }
        });

        llNetWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPtrFrameLayout.autoRefresh(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                gSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mDataList.size(); i++) {
                    UserFenPai userInfo = mDataList.get(i);
                    if (userInfo.Title != null && userInfo.Title.length() > 1) {
                        String name = userInfo.Title.trim();
                        name = name.replace(" ", "");
                        CharacterParser characterParser = CharacterParser.getInstance();
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
                        if (name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            gSearchList.add(mDataList.get(i));
                        }
                    }
                }
                if (gSearchList != null) {
//                    Set<UserInfo> set = new HashSet<>();
//                    set.addAll(mSearchList);
//                    mSearchList.clear();
//                    mSearchList.addAll(set);
                    adapter.setData((ArrayList<UserFenPai>) gSearchList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int before, int count) {
                // TODO Auto-generated method stub
                adapter.setData(mDataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * @param state 0表示所有，1表示未完成，2表示已完成
     */
    private void getWaitList(int state, int s, int size) {
        API.TalkerAPI.GetTaskByStateAndPage("", state, s, size, new RequestCallback<UserFenPai>(
                UserFenPai.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                if (page > 0) {
                    page--;
                }
                if (ex instanceof ConnectException) {
                    llNetWork.setVisibility(View.VISIBLE);
                    ToastUtil.showToast(getActivity(), "网络连接异常");
                }
            }

            @Override
            public void onParseSuccess(List<UserFenPai> respList) {
                mPtrFrameLayout.refreshComplete();
                llNetWork.setVisibility(View.GONE);
                if (respList != null && respList.size() > 0) {
                    mBlankLayout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    if (respList.size() < PAGE_COUNT) {
                        loadMoreListViewContainer.loadMoreFinish(false, false);// load more
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }
                    if (page == PAGE_FIRST)
                        mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.setData(mDataList);
                    adapter.notifyDataSetChanged();
                } else {
                    if (page == 1) {
                        mBlankLayout.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(false, false);
                    }
                }
            }
        });
    }

}
