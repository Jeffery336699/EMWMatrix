package cc.emw.mobile.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.project.adapter.TeamListAdapter;
import cc.emw.mobile.project.bean.GroupViewBean;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/6/29.
 * 所属圈子-选择页面
 */
@ContentView(R.layout.activity_team_list3)
public class TeamListActivity extends BaseActivity implements ITeamListView {
    private static final String TAG = "TeamListActivity";

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.team_lv)
    private ListView mListView;
    @ViewInject(R.id.et_contact_search)
    private EditText mSearchEt;

    private TeamListAdapter adapter;
    private ProjectPresenter presenter = new ProjectPresenter(this);
    private int tempPosition = -1;
    private ArrayList<GroupViewBean> mDataList = new ArrayList<>(); //
    private ArrayList<GroupViewBean> mSearchList = new ArrayList<>(); // 搜索后的数据集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9,R.id.cm_header_tv_right9, R.id.cm_header_tv_right})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mSearchEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
            case R.id.cm_header_tv_right:
                Log.d(TAG, "onClick()->position: " + tempPosition);
                if (adapter.getSelPosition() >= 0) {
                    GroupViewBean group = ((GroupViewBean) adapter.getItem(adapter.getSelPosition()));
                    Intent intent = getIntent();
                    intent.putExtra("teamId", group.GroupId);
                    intent.putExtra("teamName", group.GroupName);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }
                break;
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        setRefresh();
        mHeaderTitleTv.setText("选择所属圈子");
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");
//        mHeaderFinishBtn.setVisibility(View.VISIBLE);
//        mHeaderFinishBtn.setText("完成");
        adapter = new TeamListAdapter(this);
        adapter.setSelTeamById(getIntent().getIntExtra("teamId", -1));
        mListView.setAdapter(adapter);

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 300);

        //人员搜索框
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mDataList.size(); i++) {
                    GroupViewBean userInfo = mDataList.get(i);
                    if (userInfo.GroupName != null) {
                        CharacterParser characterParser = CharacterParser.getInstance();
                        String selling = characterParser.getSelling(userInfo.GroupName.toLowerCase());
                        sb.delete(0, sb.length());
                        for (int j = 0; j < userInfo.GroupName.length(); j++) {
                            String substring = userInfo.GroupName.substring(j, j + 1);
                            substring = characterParser.convert(substring);
                            substring = substring.substring(0, 1);
                            sb.append(substring);
                        }
                        if (userInfo.GroupName.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            mSearchList.add(mDataList.get(i));
                        }
                    }
                }
                if (mSearchList != null) {
                    adapter.setData(mSearchList);
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
                // HelpUtil.hideSoftInput(getActivity(), mSearchEt);
            }
        });
    }

    /**
     * 设置【下拉刷新】和【加载更多】功能
     */
    private void setRefresh() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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
//        loadMoreListViewContainer.useDefaultFooter();
//        loadMoreListViewContainer.setAutoLoadMore(true);
//        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
//            @Override
//            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
//
//            }
//        });
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.loadGroups();
            }
        });
    }

    @Override
    public void renderView(ArrayList<GroupViewBean> groups) {
        mDataList.clear();
        for(int i = 0;i<groups.size();i++){
            if(PrefsUtil.readUserInfo().ID == groups.get(i).group.CreateUser)
                mDataList.add(groups.get(i));
        }

        adapter.setData(mDataList);
        adapter.notifyDataSetChanged();
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onError(Throwable ex) {
        if(ex instanceof ConnectException){
            ToastUtil.showToast(this,"网络错误或无连接");
        }else{
            ToastUtil.showToast(this,ex.getMessage());
        }
        mPtrFrameLayout.refreshComplete();
    }

}
