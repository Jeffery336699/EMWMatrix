package cc.emw.mobile.main.fragment.worker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.task.fragment.TaskChildFragment;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * 任务Fragment
 * 
 * @author chengyong.liu
 * 
 */

@ContentView(R.layout.fragment_task)
public class TaskFragment extends BaseFragment {

	private static final String TAG = "TaskFragment";

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮

	@ViewInject(R.id.tabstrip_task)
	private PagerSlidingTabStrip mTabStrip;
	@ViewInject(R.id.viewpager_task)
	private ViewPager mViewPager;

	private final String[] TITLES = new String[] {
			TaskConstant.TaskStateString.PROCESSING,
			TaskConstant.TaskStateString.UNSTART,
			TaskConstant.TaskStateString.FINISHED };
	private TaskFragmentAdapter taskFragmentAdapter;
	private SparseArray<Fragment> fragmentMap;
	private String headerTitle;

	public static TaskFragment newInstance(String title) {
		TaskFragment fragment = new TaskFragment();
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
		initView();
	}

	@Event({ R.id.cm_header_btn_left, R.id.cm_header_btn_right })
	private void onHeaderClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			getActivity().sendBroadcast(
					new Intent(MainActivity.ACTION_REFRESH_MAIN));
			break;
		case R.id.cm_header_btn_right:
			Intent intent = new Intent(getActivity(), TaskModifyActivity.class);
			intent.putExtra(TaskConstant.TASK_FLAG, TaskConstant.CREATE_TASK);
			startActivity(intent);
			break;
		}
	}

	private void initView() {
		mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(headerTitle);
		mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
		mHeaderMoreBtn.setVisibility(View.VISIBLE);

		Log.d(TAG, "new Task FINISHED");
		mViewPager.setOffscreenPageLimit(2);

		mTabStrip.setTabWeightOne(true);
		mTabStrip.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 15, getActivity().getResources()
						.getDisplayMetrics()));
		mTabStrip.setTextColor(getResources().getColorStateList(
				R.color.vpi__dark_theme));
	}

	@Override
	public void onFirstUserVisible() {
		taskFragmentAdapter = new TaskFragmentAdapter(getChildFragmentManager());
		mViewPager.setAdapter(taskFragmentAdapter);
		mTabStrip.setViewPager(mViewPager);
	}

	private class TaskFragmentAdapter extends FragmentPagerAdapter {

		public TaskFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragmentMap.get(position);
			if (fragment == null) {
				switch (position) {
				case 0:
					fragment = TaskChildFragment
							.newInstance(TaskConstant.TaskState.PROCESSING);// 进行中的Fragment
					// Log.d(TAG, "new Task PROCESSING");
					break;
				case 1:
					fragment = TaskChildFragment
							.newInstance(TaskConstant.TaskState.UNSTART);// 未开始的Fragment
					// Log.d(TAG, "new Task UNSTART");
					break;
				case 2:
					fragment = TaskChildFragment
							.newInstance(TaskConstant.TaskState.FINISHED);// 已完成的Fragment
					// Log.d(TAG, "new Task FINISHED");
					break;
				default:
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
}
