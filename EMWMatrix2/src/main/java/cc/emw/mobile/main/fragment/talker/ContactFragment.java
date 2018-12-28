package cc.emw.mobile.main.fragment.talker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.GroupsCreateActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.contact.fragment.PersonFragment;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * @author zrjt
 * @version 2016-3-8 下午2:12:10
 */
@ContentView(R.layout.fragment_contact)
public class ContactFragment extends BaseFragment implements
		OnPageChangeListener {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮

	private PagerSlidingTabStrip mTabStrip;// 通讯录tab
	private ViewPager mViewPager;

	private PageAdapter pageAdapter;
	private SparseArray<Fragment> fragmentMap;
	private String headerTitle;

	public static ContactFragment newInstance(String title) {
		ContactFragment fragment = new ContactFragment();
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

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_btn_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
			break;
		case R.id.cm_header_btn_right:
			Intent intent = new Intent(getActivity(), GroupsCreateActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	private void initView(View view) {
		mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(headerTitle);
		mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
		mHeaderMoreBtn.setVisibility(View.INVISIBLE);
		
		mTabStrip = (PagerSlidingTabStrip) view
				.findViewById(R.id.tabstrip_contact);
		mViewPager = (ViewPager) view
				.findViewById(R.id.viewpager_contact);

		mTabStrip.setTabWeightOne(true);
		mTabStrip.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 13, getActivity().getResources()
						.getDisplayMetrics()));
		mTabStrip.setTextColor(getResources().getColorStateList(
				R.color.vpi__dark_theme));

	}

	@Override
	public void onFirstUserVisible() {
		pageAdapter = new PageAdapter(getChildFragmentManager());
		mViewPager.setAdapter(pageAdapter);
		mViewPager.addOnPageChangeListener(this);
		mTabStrip.setViewPager(mViewPager);
	}


	class PageAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = new String[] { "人员", "群组" };

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragmentMap.get(position);
			if (fragment == null) {
				if (position == 0) {
					fragment = new PersonFragments();
				} else {
					fragment = new GroupFragment();
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
		switch (arg0) {
		case 0:
			mHeaderMoreBtn.setVisibility(View.GONE);
			break;
		case 1:
			mHeaderMoreBtn.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

}
