package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.project.bean.ConstEnum;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.project.view.TimeTrackingMGActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * 项目外层碎片
 * Created by jven.wu on 2016/6/24.
 */
@ContentView(R.layout.fragment_project)
public class ProjectFragment2 extends BaseFragment {
    private final String TAG = this.getClass().getSimpleName();
    @ViewInject(R.id.top_header)
    private LinearLayout top_header;

    @ViewInject(R.id.rl_content)
    private RelativeLayout rl_content;
    @ViewInject(R.id.cm_search_bar)
    private LinearLayout cm_search_bar; //顶部搜索框
    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    //    @ViewInject(R.id.cm_header_rg_switch)
//    private RadioGroup mRadioGroup;     //radioGroup组
//    @ViewInject(R.id.cm_header_rb_left)
//    private RadioButton mProjectListRb;     //项目视图按钮
//    @ViewInject(R.id.cm_header_rb_middle)
//    private RadioButton mProjectStateRb;    //状态视图按钮
//    @ViewInject(R.id.cm_header_rb_right)
//    private RadioButton mProjectMemberRb;   //人员视图按钮
    @ViewInject(R.id.cm_header_btn_right1)
    private IconTextView mHeaderMemberProjectBtn; //项目人员列表
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderNoticeBtn; // 顶部条消息右菜单按钮
    @ViewInject(R.id.project_vp)
    private ViewPager mViewPager;        //主体viewPager
    @ViewInject(R.id.btn_create)
    private IconTextView createBtn;
    @ViewInject(R.id.empty_ll)
    private LinearLayout emptyPage; //空数据显示

    private PageAdapter pageAdapter;
    private SparseArray<Fragment> fragmentMap;
    private ListDialog mAddDialog;
    private String headTitle;

    public static EditText mEtSearch;//搜索框
    //    private MyBroadcastReceive mReceive;
    private int count;
    private LinearLayout.LayoutParams params;
    private int count2;
    private boolean flag1 = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initView();
        fragmentMap = new SparseArray<Fragment>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtSearch = (EditText) view.findViewById(R.id.et_search_keyword);
        initView();
    }

    @Event({R.id.btn_create, R.id.cm_header_btn_left,
            R.id.cm_header_btn_right,R.id.cm_header_btn_right1})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                mAddDialog.show();
                break;
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
                getActivity().startActivity(noticeIntent);
                break;
            case R.id.cm_header_btn_right1:
                Intent timeTrackingIntent = new Intent(getActivity(), TimeTrackingMGActivity.class);
                getActivity().startActivity(timeTrackingIntent);
                break;
        }
    }

    /**
     * 初始化视图娄据
     */
    private void initView() {
        setTopBar();
        pageAdapter = new PageAdapter(getFragmentManager());
        mViewPager.setOffscreenPageLimit(2); //设置最多有三个fragment缓存
        //设置当viewPager改变时更改上端的图标显示
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int resId = R.id.cm_header_rb_left;
                switch (position) {
                    case 0:
                        resId = R.id.cm_header_rb_left;
                        break;
                    case 1:
                        resId = R.id.cm_header_rb_middle;
                        break;
                    case 2:
                        resId = R.id.cm_header_rb_right;
                        break;
                }
//                mRadioGroup.check(resId);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //设置当上端图标点击时更新viewpager的当前展示项
//        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.cm_header_rb_left:
//                        mViewPager.setCurrentItem(0);
//                        headTitle = "项目";
//                        break;
//                    case R.id.cm_header_rb_middle:
//                        mViewPager.setCurrentItem(1);
//                        headTitle = "状态";
//                        break;
//                    case R.id.cm_header_rb_right:
//                        mViewPager.setCurrentItem(2);
//                        headTitle = "人员";
//                        break;
//                }
//                mHeaderTitleTv.setText(headTitle);
//            }
//        });
        initAddDialog();

        //初始化底部的菜单隐藏或展示接收
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(TalkerFragment.ACTION_HIDE_TOP_AND_BOTTOM);
//        intentFilter.addAction(TalkerFragment.ACTION_SHOW_TOP_AND_BOTTOM);
//        mReceive = new MyBroadcastReceive();
//        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
    }

    private void setTopBar() {
        cm_search_bar.setVisibility(View.GONE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.time_tracking);
        mHeaderTitleTv.setTextSize(17);
//        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderMenuBtn.setIconText("ec7c");
        mHeaderMenuBtn.setPadding(DisplayUtil.dip2px(getActivity(), 12), 0, 0, 0);
//        mHeaderFinishBtn.setVisibility(View.VISIBLE);
//        mHeaderFinishBtn.setText(R.string.finish);
        mHeaderMemberProjectBtn.setIconText("ecdc");
        mHeaderNoticeBtn.setIconText("ecd5");
        setTopIcon(mHeaderMenuBtn,mHeaderMemberProjectBtn,mHeaderNoticeBtn);

//        mHeaderNoticeBtn.setPadding(0, 0, DisplayUtil.dip2px(getActivity(), 12), 0);
    }

    /**
     * 设置头部标题栏按钮外观参数
     * @param ivs
     */
    private void setTopIcon(IconTextView... ivs) {
        for(IconTextView iv : ivs) {
            iv.setTextColor(getResources().getColor(R.color.white));
            iv.setTextSize(22);
            iv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化设置viewPager
     */
    @Override
    public void onFirstUserVisible() {
        mViewPager.setAdapter(pageAdapter);
    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{
                "项目", "状态", "人员"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                switch (position) {
                    case 0:
                        fragment = new ProjectListFragment();
                        //将父fragment传给子fragment,用于设置空状态和网络错误时的页面显示
//                        ((ProjectListFragment) fragment).setParentFragment(ProjectFragment.this);
                        break;
                    case 1:
                        fragment = new ProjectStateListFragment();
//                        ((ProjectStateListFragment) fragment).setParentFragment(ProjectFragment.this);
                        break;
                    case 2:
                        fragment = new ProjectMemberListFragment2();
                        break;
                }
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    /**
     * 初始化快捷操作按钮
     */
    private void initAddDialog() {
        mAddDialog = new ListDialog(getActivity(), false);
        mAddDialog.addItem("新增圈子与协作", ConstEnum.Create.CreateTeam);
        mAddDialog.addItem("新增团队协作", ConstEnum.Create.CreateProject);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case ConstEnum.Create.CreateTeam:
                        Intent teamIntent = new Intent(getActivity(), NewTeamActivity.class);
                        teamIntent.putExtra("start_anim", false);
                        startActivity(teamIntent);
                        break;
                    case ConstEnum.Create.CreateProject:
                        Intent projectIntent = new Intent(getActivity(), NewProjectActivity.class);
                        projectIntent.putExtra("start_anim", false);
                        startActivity(projectIntent);
                        break;
                }
            }
        });
    }

//    class MyBroadcastReceive extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (TalkerFragment.ACTION_HIDE_TOP_AND_BOTTOM.equals(action)) {
//                if(flag1){
//                    return;
//                }
//                if(params == null) {
//                    params = (LinearLayout.LayoutParams) rl_content.getLayoutParams();
//                }
//                AnimUtil.setHideShowAnim(-cm_search_bar.getHeight(), 500, top_header, null);
//            } else if (TalkerFragment.ACTION_SHOW_TOP_AND_BOTTOM.equals(action)) {
//                if(flag1){
//                    return;
//                }
//                AnimUtil.setHideShowAnim(0, 500, top_header, null);
//            }
//        }
//    }
}
