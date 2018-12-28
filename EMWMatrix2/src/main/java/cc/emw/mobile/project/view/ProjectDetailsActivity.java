package cc.emw.mobile.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.adapter.ResponserAdapter;
import cc.emw.mobile.project.adapter.TaskAdapter;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.TaskModifyActivity;

/**
 * 项目详情页面
 * @author jven.wu
 *
 */
@ContentView(R.layout.activity_project_details)
public class ProjectDetailsActivity extends BaseActivity implements
		IProjectDetailsView {
	public static final String DETAILS_PROJECT = "project";
	public static final String ACTION_PROJECT_SCHEDULE = "cc.emw.mobile.project_schedule";
	private static final String TAG = "ProjectDetailsActivity";
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;	//顶部返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;	//顶部标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderEditBtn;	//顶部编缉按钮
	@ViewInject(R.id.tv_projectdetail_name)
	private TextView mName;
	@ViewInject(R.id.tv_projectdetail_description)
	private TextView mDescription;
	@ViewInject(R.id.tv_projectdetail_state)
	private TextView mState;
	@ViewInject(R.id.tv_projectdetail_progress)
	private TextView mProgressPercent;
	@ViewInject(R.id.pb_projectdetail_progress)
	private ProgressBar mProgressBar;
	@ViewInject(R.id.tv_projectdetail_startTime)
	private TextView mStartTime;
	@ViewInject(R.id.tv_projectdetail_endTime)
	private TextView mEndTime;
	@ViewInject(R.id.lv_projectdetail_responser)
	private ListView mResponserListView;
	@ViewInject(R.id.lv_projectdetail_task)
	private ListView mTaskListView;
	@ViewInject(R.id.ll_projectdetail_addTask)
	private LinearLayout mAddTaskBtn;
    @ViewInject(R.id.new_task_divider)
    private View newTaskDivider;
	@ViewInject(R.id.ll_projectdetail_shcedule)
	private LinearLayout mSeeSchedule;

	private UserProject project;
	private ProjectPresenter presenter;
	private TaskAdapter taskAdapter;
	private ResponserAdapter responserAdapter;
	private ArrayList<UserProject> arrayProjects;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = new ProjectPresenter(this);
		initView();
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ModifyProjectActivity.EDIT_PROJECT_CODE && resultCode == RESULT_OK){  
            project = (UserProject)data.getSerializableExtra(
            		ModifyProjectActivity.EDIT_PROJECT);
            showProject();
        }  
    }  

	private void initView() {
		mHandler = new Handler();
		taskAdapter = new TaskAdapter(this);
		responserAdapter = new ResponserAdapter(this);
		project = (UserProject) getIntent().getSerializableExtra(
				DETAILS_PROJECT);
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.cm_header_btn_left:
					onBackPressed();
					break;
				case R.id.cm_header_tv_right:
					Log.d(TAG, project.Name);
					final Intent intent = new Intent(ProjectDetailsActivity.this,
							ModifyProjectActivity.class);
					intent.putExtra(ModifyProjectActivity.EDIT_PROJECT, project);
					startActivityForResult(intent, ModifyProjectActivity.EDIT_PROJECT_CODE);
					break;
				case R.id.ll_projectdetail_addTask:
					Intent intent2 = new Intent(ProjectDetailsActivity.this,TaskModifyActivity.class);
                	intent2.putExtra(TaskConstant.TASK_FLAG, TaskConstant.CREATE_TASK);
                	intent2.putExtra(TaskModifyActivity.PROJECT_TO_TASK, project.ID);
                	startActivity(intent2);
					break;
				case R.id.ll_projectdetail_shcedule:
                    arrayProjects = new ArrayList<UserProject>();
                    arrayProjects.add(project);
                    Intent intent1 = new Intent(ProjectDetailsActivity.this,
                            SingleScheduleActivity.class);
                    intent1.putExtra("projects",arrayProjects);
                    startActivity(intent1);
					break;
				default:
					break;
				}
			}
		};
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(getResources().getString(R.string.project_detail));
		mHeaderEditBtn.setText(getResources().getString(R.string.edit));
		mHeaderEditBtn.setVisibility(View.VISIBLE);
		// presenter.showProjectsDetails(this);
		mHeaderBackBtn.setOnClickListener(onClickListener);
		mHeaderEditBtn.setOnClickListener(onClickListener);
		mAddTaskBtn.setOnClickListener(onClickListener);
		mSeeSchedule.setOnClickListener(onClickListener);
		mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ProjectDetailsActivity.this,TaskDetailActivity.class);
				intent.putExtra(TaskDetailActivity.TASK_DETAIL, 
						(UserFenPai)taskAdapter.getItem(position));
				startActivity(intent);				
			}
		});
		showProject();
	}

	public void showProject() {
		int prog = CommonUtil.getProgress(
				project.BeginTime, 
				project.EndTime, 
				getResources().getString(R.string.timeformat4));
		mName.setText(project.Name);
		mDescription.setText(project.Mark);
		switch (project.Color) {
		case 0:
			mState.setText("普通");
			break;
		case 1:
			mState.setText("紧急");
			break;
		case 2:
			mState.setText("非常紧急");
			break;

		default:
			mState.setText(project.Color+"");
			break;
		}
		mProgressPercent.setText(prog + "%");
		mProgressBar.setProgress(prog);
		mStartTime.setText(project.BeginTime.substring(0,10));
		mEndTime.setText(project.EndTime.substring(0, 10));
		taskAdapter.setArrayTasks(project.Tasks);
        //当有任务时显示任务列表与新增任务按钮间的分隔条
        if(project.Tasks.size()>0){
            newTaskDivider.setVisibility(View.VISIBLE);
        }
		if(mTaskListView.getAdapter() == null){
			mTaskListView.setAdapter(taskAdapter);
		}else{
			taskAdapter.notifyDataSetChanged();
		}
		setListViewHeightBasedOnChildren(mTaskListView);
		showResponser(project.MainUser);
	}

	@Override
	public void loadProjectList(ProjectAdapter adapter) {
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	private void showResponser(String userString){
		String[] strings;
		List<UserInfo> users = new ArrayList<UserInfo>();
		strings = userString.split(",");
		for(int i = 0;i<strings.length;i++){
			if(!strings[i].equals("")){
				Log.d(TAG, "strings"+strings[i]);
				users.add(EMWApplication.personMap.get(Integer.valueOf(strings[i])));
			}
		}
		responserAdapter.setArrayUser(users,false);
		mResponserListView.setAdapter(responserAdapter);
		setListViewHeightBasedOnChildren(mResponserListView);
	}

	@Override
	public void onNetworkError() {
	}
}
