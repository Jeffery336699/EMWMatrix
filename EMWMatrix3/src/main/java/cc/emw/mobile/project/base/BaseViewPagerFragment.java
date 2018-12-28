package cc.emw.mobile.project.base;

import android.content.Intent;
import android.graphics.Color;
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

import com.rd.PageIndicatorView;
import com.rd.animation.AnimationType;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.project.bean.ConstEnum;
import cc.emw.mobile.project.view.NewProjectActivity;
import cc.emw.mobile.project.view.NewTeamActivity;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * 项目外层碎片
 * Created by jven.wu on 2016/6/24.
 */
@ContentView(R.layout.fragment_project)
public abstract class BaseViewPagerFragment extends BaseFragment
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    @ViewInject(R.id.top_header)
    private LinearLayout top_header;
    @ViewInject(R.id.rl_content)
    private RelativeLayout rl_content;
    @ViewInject(R.id.cm_header_bar)
    private RelativeLayout mHeadContain;
    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_btn_right)
    protected IconTextView mHeaderMemberProjectBtn; //项目人员列表
    @ViewInject(R.id.cm_header_btn_right1)
    private IconTextView mHeaderNoticeBtn; // 顶部条消息右菜单按钮
    @ViewInject(R.id.project_vp)
    private ViewPager mViewPager;        //主体viewPager
    @ViewInject(R.id.pageIndicatorView)
    private PageIndicatorView mIndicatorView;
    @ViewInject(R.id.btn_create)
    private IconTextView createBtn;
    @ViewInject(R.id.empty_ll)
    private LinearLayout emptyPage; //空数据显示

    private FragmentPagerAdapter pageAdapter;
    protected SparseArray<Fragment> fragmentMap;
    private ListDialog mAddDialog;
    public static EditText mEtSearch;//搜索框

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentMap = new SparseArray<Fragment>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtSearch = (EditText) view.findViewById(R.id.et_search_keyword);
        initView();
    }

    /**
     * 初始化视图娄据
     */
    private void initView() {
        setTopBar();
        pageAdapter = getPageAdapter();
        mViewPager.setOffscreenPageLimit(setPageLimit());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(pageAdapter);
        mIndicatorView.setViewPager(mViewPager);
        mIndicatorView.setRadius(3);
        mIndicatorView.setInteractiveAnimation(true);
        mIndicatorView.setUnselectedColor(Color.parseColor("#33354052"));
        mIndicatorView.setSelectedColor(Color.parseColor("#80354052"));
        mIndicatorView.setAnimationType(AnimationType.WORM);

        switch (TaskSpectacularActivity.currentState) {
            case TaskConstant.TaskStateString.UNSTART:
                mViewPager.setCurrentItem(0);
                break;
            case TaskConstant.TaskStateString.PROCESSING:
                mViewPager.setCurrentItem(1);
                break;
            case TaskConstant.TaskStateString.FINISHED:
                mViewPager.setCurrentItem(2);
                break;
            case TaskConstant.TaskStateString.DELAY:
                mViewPager.setCurrentItem(3);
                break;
        }

        /*mAddDialog = new ListDialog(getActivity(), false);
        initAddDialog(mAddDialog);*/
    }

    private void setTopBar() {
        mHeadContain.setBackground(getResources().getDrawable(R.drawable.form_top_meun_white_bg));
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        setHeaderTitle(mHeaderTitleTv);
        setLeftBtn(mHeaderMenuBtn);
        mHeaderMemberProjectBtn.setIconText("ecdc");
        mHeaderNoticeBtn.setIconText("ecd5");
        setTopIcon(mHeaderMenuBtn, mHeaderMemberProjectBtn);
        mHeaderMenuBtn.setOnClickListener(this);
        mHeaderMemberProjectBtn.setOnClickListener(this);
        mHeaderNoticeBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
    }

    /**
     * 设置头部标题栏按钮外观参数
     *
     * @param ivs
     */
    private void setTopIcon(IconTextView... ivs) {
        for (IconTextView iv : ivs) {
            iv.setTextColor(getResources().getColor(R.color.cm_text));
            iv.setTextSize(14);
            iv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化快捷操作按钮
     */
    private void initAddDialog(ListDialog listDialog) {
        listDialog.addItem("新增圈子与协作", ConstEnum.Create.CreateTeam);
        listDialog.addItem("新增团队协作", ConstEnum.Create.CreateProject);
        listDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
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

    /**
     * 弹出弹框
     */
    protected void showActionDialog() {
//        mAddDialog.show();
        Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
        intent.putExtra("start_anim", false);
        startActivity(intent);
    }

    /**
     * 设置可缓存的fragment个数
     *
     * @return
     */
    protected abstract int setPageLimit();

    /**
     * 设置ViewPager适配器, 使用默认PageAdapter时, 必须重写
     * {@link #getPageAdapter()} 和 {@link #getPagerTitle()}
     *
     * @return
     */
    protected FragmentPagerAdapter getPageAdapter() {
        return new PageAdapter(getFragmentManager());
    }

    /**
     * 设置ViewPager的title数组，可以不重写该方法，而只重写{@link #getPageAdapter()}
     *
     * @return
     */
    protected String[] getPagerTitle() {
        return new String[]{""};
    }

    /**
     * 设置ViewPager要切换的fragment，可以不重写该方法，而只重写{@link #getPageAdapter()}
     *
     * @param position
     * @return
     */
    protected Fragment getPagerFragment(int position) {
        return null;
    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = getPagerTitle();

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                fragment = getPagerFragment(position);
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
     * 设置顶部左菜单按钮
     *
     * @param btn
     */
    protected void setLeftBtn(IconTextView btn) {
        btn.setIconText("ec7c");
        btn.setTextColor(getResources().getColor(R.color.cm_text));
        btn.setPadding(DisplayUtil.dip2px(getActivity(), 12), 0, 0, 0);
    }

    /**
     * 设置页面标题
     *
     * @param titleTv
     */
    protected void setHeaderTitle(TextView titleTv) {
        titleTv.setText(R.string.time_tracking);
        titleTv.setTextColor(getResources().getColor(R.color.cm_text));
        mHeaderTitleTv.setTextSize(17);
    }
}

