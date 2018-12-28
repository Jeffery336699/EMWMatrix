package cc.emw.mobile.me;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.GroupsCreateActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.contact.fragment.PersonFragment;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.fragment.WaitHandleCalendarFragment;
import cc.emw.mobile.me.fragment.WaitHandlePlanFragment;
import cc.emw.mobile.me.fragment.WaitHandleTaskFragment;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * 我·待办工作
 * @author zrjt
 * 
 */
@ContentView(R.layout.activity_wait_handle)
public class WaitHandleActivity extends BaseActivity implements
		OnPageChangeListener {

	private String[] titles;

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; //

	private PagerSlidingTabStrip mTabStrip;// tab
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titles = getResources().getStringArray(R.array.wait_handle_titles);
		initView();
	}

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_btn_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_btn_right:
			break;
		}
	}

	private void initView() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.waithandle);
		mHeaderMoreBtn.setVisibility(View.GONE);

		mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabstrip_waithandle);
		mViewPager = (ViewPager) findViewById(R.id.viewpager_waithandle);
		mViewPager.setOffscreenPageLimit(2);

		mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
		mViewPager.addOnPageChangeListener(this);
		mTabStrip.setViewPager(mViewPager);

		mTabStrip.setTabWeightOne(true);
		mTabStrip.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 13, getResources()
						.getDisplayMetrics()));
		mTabStrip.setTextColor(getResources().getColorStateList(
				R.color.vpi__dark_theme));

	}

	class PageAdapter extends FragmentPagerAdapter {

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new WaitHandlePlanFragment();
			} else if (position == 1) {
				return new WaitHandleTaskFragment();
			} else {
				return new WaitHandleCalendarFragment();
			}
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		setSwipeBackEnable(arg0 != 0 ? false : true);
	}
}
