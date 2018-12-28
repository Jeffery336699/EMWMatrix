package cc.emw.mobile.contact.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.main.MainActivity;

/**
 * @author zrjt
 * @version 2016-3-8 下午2:12:10
 */
@ContentView(R.layout.fragment_contact)
public class ContactFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @ViewInject(R.id.cm_header_rg_switch)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton iBLeft;
    @ViewInject(R.id.viewpager_contact)
    private ViewPager mViewPager;
    @ViewInject(R.id.cm_header_rb_left)
    private RadioButton mRbPerson;
    @ViewInject(R.id.cm_header_rb_middle)
    private RadioButton mRbMiddle;
    @ViewInject(R.id.cm_header_rb_right)
    private RadioButton mRbGroup;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton imageButton;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mTitle;

    private PageAdapter pageAdapter;
    private MyBroadcastReceive mReceive;
    // 刷新的action
    public static final String ACTION_GO_TO_CHAT = "cc.emw.mobile.go_to_chat";

    class MyBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_GO_TO_CHAT.equals(action)) {
                mViewPager.setCurrentItem(0);
            }
        }
    }

    public ContactFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GO_TO_CHAT);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听

        mRadioGroup.setVisibility(View.VISIBLE);
        iBLeft.setVisibility(View.VISIBLE);
        mRbPerson.setButtonDrawable(R.drawable.contact_chat_selector);
        mRbPerson.setChecked(true);
        mRbMiddle.setVisibility(View.VISIBLE);
        mRbMiddle.setButtonDrawable(R.drawable.contact_people_selector);
        mRbGroup.setVisibility(View.VISIBLE);
        mRbGroup.setButtonDrawable(R.drawable.contact_group_selector);
        imageButton.setImageResource(R.drawable.nav_btn_notice);
        imageButton.setVisibility(View.VISIBLE);
        pageAdapter = new PageAdapter(getFragmentManager());
        mTitle.setText("会话");
        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(pageAdapter);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
                getActivity().startActivity(noticeIntent);
                break;
        }
    }

    @Override
    public void onFirstUserVisible() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.cm_header_rb_left:
                mViewPager.setCurrentItem(0);
                mTitle.setText("会话");
                break;
            case R.id.cm_header_rb_middle:
                mViewPager.setCurrentItem(1);
                mTitle.setText("人员");
                break;
            case R.id.cm_header_rb_right:
                mViewPager.setCurrentItem(2);
                mTitle.setText("团队");
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:
                mRbPerson.setChecked(true);
                break;
            case 1:
                mRbMiddle.setChecked(true);
                break;
            case 2:
                mRbGroup.setChecked(true);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"会话", "人员", "团队"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 0) {
                    fragment = new ChatHistoriesFragment();
//                    fragment = new ChatHistoryFragments();
                } else if (position == 1) {
                    fragment = new PersonFragments();
                } else {
                    fragment = new GroupFragment();
//                    fragment = new TestFragment();
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceive);
    }
}
