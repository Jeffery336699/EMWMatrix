package cc.emw.mobile.project.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.ArrangeScheduleAdapter;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.CustomHorizontalScrollView;
import cc.emw.mobile.project.view.CustomListView;
import cc.emw.mobile.project.view.IScheduleView;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.util.DisplayUtil;

/**
 * 多项目排期页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.fragment_schedule)
public class ScheduleFragment extends BaseFragment implements IScheduleView {
	public static final int UNIT_WIDTH = 64;//时间条单位长度
	private static final String TAG = "ScheduleFragment";
    public static final String PROJECT_REFRESH = "cc.emw.mobile.project.fragment.schedule";

	@ViewInject(R.id.tv_project_time)
	private TextView mTime; //年月时间文本
	@ViewInject(R.id.chs_project_horizontalScroll)
	private CustomHorizontalScrollView mHorizontalScroll;
	@ViewInject(R.id.ll_project_timeBar)
	private LinearLayout mTimeBar; //时间条
	@ViewInject(R.id.lv_project_content)
	private CustomListView mProjectListView; //项目排期列表
	@ViewInject(R.id.lv_project)
	private ExpandableListView mListView; //项目信息列表

	private ArrangeScheduleAdapter adapter;
	private ProjectAdapter projectAdapter;
	private Map<Integer, Integer> monthDayMap;
	private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
	private int oldHashCode;
	private Handler handler;
    public ArrayList<UserProject> projectArrayList;
    private boolean isSingleProjects;
    private boolean flag;//标记是否第一次进入该界面
    private int scrollDay;
    private Date minTime = new Date(); //所有项目中最小的日期
    private int locationX = 0;
    private int minProjectPos;
    private int maxProjectPos;
    private ProjectUpdateBroadcastReceiver refreshReceive;

    public ScheduleFragment() {
        this.projectArrayList = ProjectPresenter.getGlobalProjects();
	}

    public void setProjects(ArrayList<UserProject> prjs){
        this.projectArrayList = prjs;
        isSingleProjects = true;
    }

	public static ScheduleFragment newInstance(String content) {
		ScheduleFragment fragment = new ScheduleFragment();
		return fragment;
	}

    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		monthDayMap = new HashMap<Integer, Integer>();
		initView();
        if(isSingleProjects) {
            refreshDisplay();
        }
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
	}

	@Override
	public void onFirstUserVisible() {
        refreshDisplay();
	}

    private void initView() {
        mHorizontalScroll.setOnScrollListener(new CustomHorizontalScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollX) {
                setListItemNavScroll(scrollX);
            }
        });
		handler = new Handler();
		adapter = new ArrangeScheduleAdapter(getContext());
        adapter.setScrollView(this.mHorizontalScroll);
		projectAdapter = new ProjectAdapter(getContext());
		mProjectListView.setAdapter(adapter);
        mProjectListView.setOnScrollYListener(new CustomListView.OnScrollYListener() {
            @Override
            public void onScroll(int scrollY) {setListItemNavScroll(locationX);

            }
        });
		mListView.setAdapter(projectAdapter);
		mTimeBar.measure(0, 0);
		mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(getActivity(),TaskDetailActivity.class);
				intent.putExtra(TaskDetailActivity.TASK_DETAIL, 
						(UserFenPai)projectAdapter.getChild(groupPosition, childPosition));
				startActivity(intent);
				return true;
			}
		});

        //设置Group点击事件
//		mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//			@Override
//			public boolean onGroupClick(ExpandableListView parent, View v,
//					int groupPosition, long id) {
//				if(projectAdapter.getGroup(groupPosition) instanceof UserProject){
//					if(((UserProject)projectAdapter.getGroup(groupPosition)).ID == -1){
//						return true;
//					}
//					Intent intent = new Intent(getActivity(),ProjectDetailsActivity.class);
//					intent.putExtra(ProjectDetailsActivity.DETAILS_PROJECT,
//							(Serializable)projectAdapter.getGroup(groupPosition));
//					startActivity(intent);
//				}
//				return true;
//			}
//		});

//		scrollView滑动距离计算
//		当手势缓慢滑动时scrollView.getScrollY()即可计算出滑动距离.
//		当手势快速滑动时,手势已ACTION_UP,但scrollView仍会继续滑动,
//		此时就要通过隔一段时候计算一下scrollView.getScrollY(),当它不变时才表示不再滑动
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						setTextByViewScrollX(mHorizontalScroll);
						handler.postDelayed(this, 100);
					}
				});
			}
		}).start();
		mHorizontalScroll.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setTextByViewScrollX(v);
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROJECT_REFRESH);
        refreshReceive = new ProjectUpdateBroadcastReceiver();
        getActivity().registerReceiver(refreshReceive,intentFilter);
	}

    /**
     * 初始化显示时间条
     */
	private void initTimeBar() {
		mTimeBar.removeAllViews();
		Calendar minDate = Calendar.getInstance();
        Calendar minDateTemp = Calendar.getInstance();
        minTime = getMinTime(projectArrayList);
        adapter.projectsMinTime = minTime;
        //先设置最所有项目最小日期
		minDate.setTime(minTime);
        //设置临时日期，用于存放最小日期月份的1号
        minDateTemp.setTime(minTime);
        minDateTemp.set(Calendar.DAY_OF_MONTH,1);
        minTime = minDateTemp.getTime();
		mTime.setText(format.format(minDate.getTime()));
		Calendar maxDate = Calendar.getInstance();
		maxDate.setTime(getMaxTime(projectArrayList));
		if(!flag){
			//计算滚动的日期
			scrollDay = minDate.get(Calendar.DAY_OF_MONTH) - 1;
            Log.d(TAG, "flag: " + flag + " scrollDay: " + scrollDay);
			//设置scrollView滚动到项目开始日期处
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHorizontalScroll.smoothScrollTo(
                            DisplayUtil.dip2px(getContext(), UNIT_WIDTH) * scrollDay, 0);
                    mProjectListView.setSelection(minProjectPos);
                    //当刚进入排期时滚动到日期为最小的项目，并设置导航图标为不可见
                    if(mProjectListView.getFirstVisiblePosition() == minProjectPos){
                        Log.d(TAG,"run()->getChildCount: " + mProjectListView.getChildCount());
                        View view = mProjectListView.getChildAt(0);
                        ImageView navImg = (ImageView)view.findViewById(R.id.nav_icon);
                        navImg.setVisibility(View.INVISIBLE);
                    }
                }
            }, 1000);
            flag = true;
		}
		int minYear = minDate.get(Calendar.YEAR);
		int maxYear = maxDate.get(Calendar.YEAR);
		for(int year = minYear;year <= maxYear;year++){
			int starMonth = 0;
			int endMonth = 12;
			if(minYear == maxYear){
				starMonth = minDate.get(Calendar.MONTH);
				endMonth = maxDate.get(Calendar.MONTH);
			}else{
				if(year == minYear){
					starMonth = minDate.get(Calendar.MONTH);
				}else if(year  == maxYear){
					endMonth = maxDate.get(Calendar.MONTH);
				}
			}
			Calendar cal = Calendar.getInstance();
			for(int m = starMonth ;m< endMonth+1;m++){
				cal.clear();
				cal.set(Calendar.YEAR,year);
				cal.set(Calendar.MONTH, m);
				int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				Log.d(TAG, "day of"+m+" has "+days);
				for (int i = 1; i < days+1; i++) {
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							DisplayUtil.dip2px(getContext(), UNIT_WIDTH),
							LinearLayout.LayoutParams.WRAP_CONTENT);
					TextView textView = new TextView(getContext());

					textView.setText(i + "");
					textView.setTextSize(14);
					textView.setTextColor(getResources().getColor(R.color.blue_20));
					textView.setLayoutParams(lp);
					textView.setGravity(Gravity.CENTER);
					Log.d(TAG, "textview size" + textView.getMeasuredWidth());
					mTimeBar.addView(textView, lp);
				}
			}
		}
	}

    /**
     * 计算项目中最小时间
     * @param projects 要计算的项目集合
     * @return Date
     */
	private Date getMinTime(ArrayList<UserProject> projects){
		Date d1 = new Date();
        int n;
		try{
            minProjectPos = 0;
			d1 = f.parse(projects.get(0).BeginTime);
            Log.d(TAG,"project size " + projects.size());
			for(int i = 1;i<projects.size();i++){
				Date d2 = f.parse(projects.get(i).BeginTime);
				if(d1.getTime()>d2.getTime()){
                    minProjectPos = i;
                    d1.setTime(d2.getTime());
				}
			}
		}catch(Exception e){
		}
		return d1;
	}

    /**
     * 计算项目中最大时间
     * @param projects
     * @return Date
     */
	private Date getMaxTime(ArrayList<UserProject> projects){
		Date d1 = new Date();
		try{
            maxProjectPos = 0;
			d1 = f.parse(projects.get(0).EndTime);
			for(int i = 1;i<projects.size();i++){
				Date d2 = f.parse(projects.get(i).EndTime);
				if(d1.getTime()<d2.getTime()){
                    maxProjectPos = i;
					d1 = d2;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return d1;
	}
	
	private void refreshDisplay(){
		ArrayList<UserProject> projects = projectArrayList;
		adapter.setArrayProjects(projects);
		adapter.notifyDataSetChanged();
		if(isSingleProjects){
            adapter.setSingleProjectFlag(true);
			projectAdapter.setArrayProjects(projects);
			projectAdapter.notifyDataSetChanged();
			mListView.setVisibility(View.VISIBLE);
		}else{
            mListView.setVisibility(View.INVISIBLE);
        }
        initTimeBar();
	}

    /**
     * 通过滚动的X值设置相应文本
     * @param v 产生滚的view
     */
	private void setTextByViewScrollX(View v){
		if(getActivity() == null){
			return;
		}
		int scrollX = v.getScrollX();
		String date = CommonUtil.getOffsetDate(
				minTime,
				scrollX/DisplayUtil.dip2px(getActivity(), UNIT_WIDTH));
		mTime.setText(date);
	}

    /**
     * 设置listitem中导航图标的点击滑动事件
     * @param x 滚动的值
     */
    private void setListItemNavScroll(int x){
        int firstPosition ;
        int lastPosition ;
        firstPosition = mProjectListView.getFirstVisiblePosition();
        lastPosition = mProjectListView.getLastVisiblePosition();
        locationX = x;
        adapter.locationX = x;
        for(int i = 0;i<mProjectListView.getChildCount();i++){
            final UserProject itemProject = projectArrayList.get(firstPosition + i);
            Date itemMinTime = new Date();
            Date itemMaxTime = new Date();
            try{
                itemMinTime = f.parse(itemProject.BeginTime);
                itemMaxTime = f.parse(itemProject.EndTime);
            }catch (Exception e){

            }
            final int itemScrollDay = CommonUtil.compareDate(minTime, itemMinTime);
            final int itemScrollEndDay = CommonUtil.compareDate(minTime, itemMaxTime);
            View view = mProjectListView.getChildAt(i);
            ImageView navView = (ImageView)view.findViewById(R.id.nav_icon);
            int unitWidth = ScheduleFragment.UNIT_WIDTH;
            if(locationX>DisplayUtil.dip2px(getContext(), unitWidth) * (itemScrollDay + 0.5) - DisplayUtil.getDisplayWidth(getContext())
                    && locationX<DisplayUtil.dip2px(getContext(), unitWidth) * (itemScrollEndDay + 0.5) ){
                navView.setVisibility(View.INVISIBLE);
            }else{
                navView.setVisibility(View.VISIBLE);
            }
            navView.layout(locationX + 26, navView.getTop(), navView.getWidth() + locationX + 26, navView.getBottom());
            navView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.nav_icon:
                            mHorizontalScroll.smoothScrollTo(
                                    DisplayUtil.dip2px(getContext(), UNIT_WIDTH) * itemScrollDay, 0);
                            break;
                    }
                }
            });
        }
    }

    /**
     * 设置listView滚动到指定的位置
     * @param lv
     * @param pos 第一个传0，最后一个传mListView.getCount()-1
     */
    private void setListViewScrollPos(ListView lv,int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            lv.smoothScrollToPosition(pos);
        } else {
            lv.setSelection(pos);
        }
    }

    @Override
	public void onNetworkError() {
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(refreshReceive);
    }

    class ProjectUpdateBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive()->");
            String actionStr = intent.getAction();
            if(PROJECT_REFRESH.equals(actionStr)) {
                refreshDisplay();
            }
        }
    }
}
