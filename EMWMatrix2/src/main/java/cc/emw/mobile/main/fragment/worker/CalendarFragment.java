package cc.emw.mobile.main.fragment.worker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Calendar;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.calendar.fragment.CalendarDayFragment;
import cc.emw.mobile.calendar.fragment.CalendarDayFragments;
import cc.emw.mobile.calendar.fragment.CalendarMonthFragments;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * @author zrjt
 * @version 2016-3-8 下午2:12:10
 */
@ContentView(R.layout.fragment_calendar)
public class CalendarFragment extends BaseFragment {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮

    private PagerSlidingTabStrip mTabStrip;// tab
    private ViewPager mViewPager;

    public static final String CREATEDATE = "createDate";
    private PageAdapter pageAdapter;
    private Calendar mClickDate = Calendar.getInstance();
    private SparseArray<Fragment> fragmentMap;
    private String headerTitle;

    public static CalendarFragment newInstance(String title) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString("header_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentMap = new SparseArray<Fragment>();
        headerTitle = getArguments().getString("header_title");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
                Intent intent = new Intent(getActivity(),
                        CalendarCreateActivitys.class);
                mClickDate = CalendarMonthFragments.mClickDate;
                intent.putExtra(CREATEDATE, mClickDate);
                startActivity(intent);
                break;
        }
    }

    private void initView(View view) {
        mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(headerTitle);
        mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);
        mTabStrip = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabstrip_calendar);
        mViewPager = (ViewPager) view
                .findViewById(R.id.viewpager_calendar);

        mTabStrip.setTabWeightOne(true);
        mTabStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getActivity().getResources()
                        .getDisplayMetrics()));
        mTabStrip.setTextColor(getResources().getColorStateList(
                R.color.vpi__dark_theme));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageAdapter = new PageAdapter(getChildFragmentManager());
                mViewPager.setAdapter(pageAdapter);
                mTabStrip.setViewPager(mViewPager);
            }
        }, 4000);
    }

    @Override
    public void onFirstUserVisible() {
        /*pageAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mTabStrip.setViewPager(mViewPager);*/
    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{
                "日程",
                "月"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                if (position == 0) {
                    fragment = new CalendarDayFragments();
                } else {
                    fragment = new CalendarMonthFragments();
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

}
