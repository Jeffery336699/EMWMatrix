package cc.emw.mobile.main.fragment.worker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.project.fragment.ScheduleFragment;
import cc.emw.mobile.project.fragment.SummaryFragment;
import cc.emw.mobile.project.view.AddSprintActivity;
import cc.emw.mobile.project.view.ModifyProjectActivity;
import cc.emw.mobile.project.view.ProjectDetailsActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.view.PagerSlidingTabStrip;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;

/**
 * 项目Fragment
 * @author jven.wu
 *
 */
@ContentView(R.layout.fragment_project)
public class ProjectFragment extends BaseFragment {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮
	
	@ViewInject(R.id.tabstrip_project)
	private PagerSlidingTabStrip mTabStrip;
	@ViewInject(R.id.viewpager_project)
	private ViewPager mViewPager;
    private RightMenu mMenu;

	private PageAdapter pageAdapter;
	private ProjectScheduleBroadcastReceive mReceiver;
	private SparseArray<Fragment> fragmentMap;
	private String headerTitle;

	public static ProjectFragment newInstance(String title) {
		ProjectFragment fragment = new ProjectFragment();
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
		initMenu();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ProjectDetailsActivity.ACTION_PROJECT_SCHEDULE);
		mReceiver = new ProjectScheduleBroadcastReceive();
		getActivity().registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null)
			getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private void initView(){
		mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
		mHeaderMenuBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(headerTitle);
		mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_share);
		mHeaderMoreBtn.setVisibility(View.VISIBLE);
		
        mTabStrip.setTabWeightOne(true);
        mTabStrip.setTextSize((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getActivity().getResources().getDisplayMetrics()));
        mTabStrip.setTextColor(getResources().getColorStateList(R.color.vpi__dark_theme));
        mHeaderMenuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
			}
		});
        mHeaderMoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMenu.showAsDropDown(v);
			}
		});
        
        //mViewPager.setCurrentItem(1);
	}

	@Override
	public void onFirstUserVisible() {
		pageAdapter = new PageAdapter(getChildFragmentManager());
		mViewPager.setAdapter(pageAdapter);
		mTabStrip.setViewPager(mViewPager);
	}

	//初始化弹出模框菜单
    private void initMenu() {  
        mMenu = new RightMenu(getContext());  
        mMenu.addItem(getResources().getString(R.string.add_project), 1);  
        mMenu.addItem(getResources().getString(R.string.add_task), 2);
        mMenu.addItem(getResources().getString(R.string.add_sprint), 3);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override  
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
	                case 1:
						setStartAnim(true);
	                	Intent intent = new Intent(getActivity(),ModifyProjectActivity.class);
	    				startActivity(intent);
	                    break;
                    case 2:
						setStartAnim(true);
                    	Intent intent2 = new Intent(getActivity(),TaskModifyActivity.class);
                    	intent2.putExtra(TaskConstant.TASK_FLAG, TaskConstant.CREATE_TASK);
                    	startActivity(intent2);
                    	break;
                    case 3:
						setStartAnim(false);
                    	Intent intent3 = new Intent(getActivity(),AddSprintActivity.class);
                    	startActivity(intent3);
                        break;
                }
            }  
        });  
    }
	
	class PageAdapter extends FragmentPagerAdapter {
	    	
	    	private final String[] TITLES = new String[] { 
	    			getResources().getString(R.string.summarize),
	    			getResources().getString(R.string.arrange_date) };
	    	
			public PageAdapter(FragmentManager fm) {
				super(fm);
			}
			
			@Override
			public Fragment getItem(int position) {
				Fragment fragment = fragmentMap.get(position);
				if(fragment == null){
					if (position == 0) {
						fragment = new SummaryFragment();
					} else {
						fragment = new ScheduleFragment();
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
		
	class ProjectScheduleBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if (ProjectDetailsActivity.ACTION_PROJECT_SCHEDULE.equals(actionStr)){
		        mViewPager.setCurrentItem(1);
			}
		}
	}
	
}
