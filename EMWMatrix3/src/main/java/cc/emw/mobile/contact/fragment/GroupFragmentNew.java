package cc.emw.mobile.contact.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.adapter.FragmentAdapter;

/**
 * A simple {@link Fragment} subclass.　圈子协同fragment
 */
@ContentView(R.layout.fragment_group)
public class GroupFragmentNew extends BaseFragment {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpage)
    ViewPager viewpage;
    @BindView(R.id.refresh)
    SwipeRefreshLayout swipeRefreshView;
    @BindView(R.id.iv_add_group)
    FloatingActionButton ivAddGroup;
    Unbinder unbinder;

    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private Fragment mFragment;
    private FragmentAdapter mFragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_new, container, false);
        unbinder = ButterKnife.bind(this, view);
        swipeRefreshView.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        initView();
        initRefresh();
        return view;
    }

    private void initRefresh() {
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 加载完数据设置为不加载状态，将加载进度收起来
                        swipeRefreshView.setRefreshing(false);
                    }
                }, 1200);
            }
        });

    }

    private void initView() {
        mFragmentList = new ArrayList<>();
        mTitleList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    mTitleList.add("个人喜欢");
                    mFragment = new PersonalLikeFragment();
                    break;
                case 1:
                    mTitleList.add("公共频道");
                    mFragment = new PublicChannelFragment();
                    break;
                case 2:
                    mTitleList.add("私有频道");
                    mFragment = new PrivateChannelFragment();
                    break;
                case 3:
                    mTitleList.add("茶水闲聊");
                    mFragment = new TeaWaterFragment();
                    break;
                case 4:
                    mTitleList.add("已读频道");
                    mFragment = new HasReadFragment();
                    break;
            }

            mFragmentList.add(mFragment);
        }

        mFragmentAdapter = new FragmentAdapter(getChildFragmentManager(), mFragmentList, mTitleList);
        viewpage.setAdapter(mFragmentAdapter);
        tabs.setupWithViewPager(viewpage);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
