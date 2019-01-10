package cc.emw.mobile.contact;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.AddFriendFragment;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.contact.fragment.NewFriendFragment;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.contact.fragment.PhoneUserFragment;
import cc.emw.mobile.me.fragment.MyInfoFragment2;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_add_friend)
public class AddFriendActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.tabs_add_friend)
    private TabLayout tabLayout;
    @ViewInject(R.id.viewpager_friend_add)
    private ViewPager mViewPager;

    private PageAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        init();

    }

    private void init() {
        mPageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.addOnPageChangeListener(this);
//        setIndicator(tabLayout, 30, 30);
    }

    @Event(value = {R.id.tv_back})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                sendBroadcast(new Intent(AddFriendFragment.ACTION_ADD_FRIEND_IS_BOTTOM));
                sendBroadcast(new Intent(PhoneUserFragment.ACTION_PHONE_FRIEND_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(NewFriendFragment.ACTION_NEW_FRIEND_IS_BOTTOM).putExtra("enable", false));
                break;
            case 1:
                sendBroadcast(new Intent(PhoneUserFragment.ACTION_PHONE_FRIEND_IS_BOTTOM));
                sendBroadcast(new Intent(AddFriendFragment.ACTION_ADD_FRIEND_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(NewFriendFragment.ACTION_NEW_FRIEND_IS_BOTTOM).putExtra("enable", false));
                break;
            case 2:
                sendBroadcast(new Intent(NewFriendFragment.ACTION_NEW_FRIEND_IS_BOTTOM));
                sendBroadcast(new Intent(AddFriendFragment.ACTION_ADD_FRIEND_IS_BOTTOM).putExtra("enable", false));
                sendBroadcast(new Intent(PhoneUserFragment.ACTION_PHONE_FRIEND_IS_BOTTOM).putExtra("enable", false));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"添加好友", "通讯录", "新的好友"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 0) {
                    fragment = new AddFriendFragment();
                } else if (position == 1) {
                    fragment = new PhoneUserFragment();
                } else if (position == 2) {
                    fragment = new NewFriendFragment();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
