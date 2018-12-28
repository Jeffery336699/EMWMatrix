package cc.emw.mobile.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.main.fragment.worker.CalendarFragment;
import cc.emw.mobile.main.fragment.worker.FileFragment;
import cc.emw.mobile.main.fragment.worker.ProjectFragment;
import cc.emw.mobile.main.fragment.worker.TaskFragment;
import cc.emw.mobile.view.ExViewPager;
import cc.emw.mobile.view.TabView;

public class WorkerFragment extends BaseFragment {

	private String[] titles;
	private int[] iconNormals = { R.drawable.tab_btn_nor_xiangmu, R.drawable.tab_btn_nor_renwu, 
			R.drawable.tab_btn_nor_richeng, R.drawable.tab_btn_nor_zhishiku };
	private int[] iconSelects = { R.drawable.tab_btn_sel_xiangmu, R.drawable.tab_btn_sel_renwu, 
			R.drawable.tab_btn_sel_richeng, R.drawable.tab_btn_sel_zhishiku };

	private ExViewPager mViewPager;
	private TabView mTabView;

	public static final String ACTION_TALKER_ITEM = "cc.emw.mobile.talker_item"; //
	private MyBroadcastReceive mReceive;
	private SparseArray<Fragment> fragmentMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titles = getResources().getStringArray(R.array.main_worker_titles);
		fragmentMap = new SparseArray<Fragment>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_main, container, false);
		mViewPager = (ExViewPager) rootView.findViewById(R.id.tab_pager);
		mTabView = (TabView) rootView.findViewById(R.id.tab_view);
		mViewPager.setPagingEnabled(false); // 禁止左右滑动
		mViewPager.setOffscreenPageLimit(4);
		mViewPager.setAdapter(new PageAdapter(getChildFragmentManager()));
		mTabView.setViewPager(mViewPager);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_TALKER_ITEM);
		mReceive = new MyBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
		return rootView;
	}

	@Override
	public void onDestroy() {
		if (mReceive != null)
			getActivity().unregisterReceiver(mReceive); // 取消监听
		super.onDestroy();
	}

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_TALKER_ITEM.equals(action)) {
				int item = intent.getIntExtra("item", 0);
				mViewPager.setCurrentItem(item);
			}
		}
	}

	private Fragment getFragment(int position) {
		Fragment fragment = fragmentMap.get(position);
		if (fragment == null) {
			switch (position) {
			case 0:
				fragment = ProjectFragment.newInstance(titles[position]);
				break;
			case 1:
				fragment = TaskFragment.newInstance(titles[position]);
				break;
			case 2:
				fragment = CalendarFragment.newInstance(titles[position]);
				break;
			case 3:
				fragment = FileFragment.newInstance(titles[position]);
				break;
			}
			fragmentMap.put(position, fragment);
		}
		return fragment;
	}

	class PageAdapter extends FragmentPagerAdapter implements
			TabView.OnItemIconTextSelectListener {

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return getFragment(position);
		}

		@Override
		public int[] onIconSelect(int position) {
			int icon[] = new int[2];
			icon[0] = iconSelects[position];
			icon[1] = iconNormals[position];
			return icon;
		}

		@Override
		public String onTextSelect(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}
	}
}