package cc.emw.mobile.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.BusinessCardFragment;
import cc.emw.mobile.contact.fragment.SaoyiSaoFragment;
import cc.emw.mobile.view.SegmentedGroup;

@ContentView(R.layout.activity_sao_yi_sao)
public class SaoYiSaoActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.view_pager_add_friend)
    private ViewPager mViewPager;
    @ViewInject(R.id.segmented_plan_type)
    private SegmentedGroup mRadioGroup; // 类型RadioGroup
    @ViewInject(R.id.rb_business_card)
    private RadioButton mRbCard;
    @ViewInject(R.id.rb_sao_yi_sao)
    private RadioButton mRbSao;
    @ViewInject(R.id.rl_sao_yi_sao)
    private RelativeLayout mRlTitle;

    private PageAdapter pageAdapter;
    private int mSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(pageAdapter);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mSelector = getIntent().getIntExtra("mSelector", 0);
        if (mSelector == 1) {
            mRbSao.setChecked(true);
            mViewPager.setCurrentItem(1);
        }
    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onClicks(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left:
                finish();
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
                mRbCard.setChecked(true);
                break;
            case 1:
                mRbSao.setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_business_card:
                mViewPager.setCurrentItem(0);
                mRlTitle.setBackgroundColor(getResources().getColor(R.color.white));
                mRlTitle.setBackgroundResource(R.drawable.head_bg);
                break;
            case R.id.rb_sao_yi_sao:
                mViewPager.setCurrentItem(1);
                mRlTitle.setBackgroundColor(getResources().getColor(R.color.black));
                break;
        }
    }


    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"我的名片", "扫一扫"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 0) {
                    fragment = new BusinessCardFragment();
                } else if (position == 1) {
                    fragment = new SaoyiSaoFragment();
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

}
