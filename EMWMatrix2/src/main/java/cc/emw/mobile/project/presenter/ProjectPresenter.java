package cc.emw.mobile.project.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.adapter.ProjectAdapter;
import cc.emw.mobile.project.adapter.RelativeScheduleAdapter;
import cc.emw.mobile.project.adapter.RelativeTaskAdapter;
import cc.emw.mobile.project.entities.UserSprint;
import cc.emw.mobile.project.model.IProjectModel;
import cc.emw.mobile.project.model.OnProjectListener;
import cc.emw.mobile.project.model.ProjectModel;
import cc.emw.mobile.project.view.IModifyProjectView;
import cc.emw.mobile.project.view.IProjectBaseView;
import cc.emw.mobile.project.view.IRelativeScheduleView;
import cc.emw.mobile.project.view.IRelativeTaskView;
import cc.emw.mobile.project.view.ISummaryView;

/**
 * 项目主导器-P层
 * @author jven.wu
 *
 */
public class ProjectPresenter implements OnProjectListener {
	private static final String TAG = "ProjectPresenter";
	private IProjectModel mProjectModel;
	private IProjectBaseView mProjectView;
	private ProjectAdapter projectAdapter;
	private RelativeTaskAdapter taskAdapter;
	private RelativeScheduleAdapter scheduleAdapter;
	private Context mContext;
	private static ArrayList<UserProject> globalProjects = new ArrayList<UserProject>();
	
	public ProjectPresenter(IProjectBaseView view){
		this.mProjectModel = new ProjectModel();
		this.mProjectView = view;
	}

	/**
	 * 显示项目列表
	 * @param context
	 */
	public void showListProjects(int pageNo,Context context){
		mContext = context;
//		mProjectModel.getProjects(this);
        mProjectModel.GetProjectByPage(pageNo,20,this);
	}
	
	/**
	 * 显示项目详情
	 * @param context
	 */
	public void showProjectsDetails(Context context){
		
	}
	
	/**
	 * 显示所有任务
	 * @param context
	 */
	public void showAllTasks(Context context){
		mContext = context;
		mProjectModel.getAllTasks(this);
	}
	
	/**
	 * 显示所有日程
	 * @param context
	 */
	public void showAllSchedule(Context context){
		mContext = context;
		mProjectModel.getAllSchedules(this);
	}
	
	/**
	 * 添加新项目
	 * @param project
	 */
	public void addProject(UserProject project){
		mProjectModel.modifyProject(project,this);
	}
	
	@Override
	public void onGetProjectsSuccess(List<UserProject> projects,List<UserSprint> sprints) {
		projectAdapter = new ProjectAdapter(mContext);
		projectAdapter.setArrayProjects(projects);
		ProjectPresenter.getGlobalProjects().clear();
		ProjectPresenter.globalProjects.addAll(projects);
		projectAdapter.setArraySrpints(sprints);
		mProjectView.loadProjectList(projectAdapter);
		((ISummaryView)mProjectView).refreshComplete();
	}

	@Override
	public void onGetSprintsSuccess(List<UserSprint> sprints) {
	}
	
	@Override
	public void onGetAllTasksSuccess(List<UserFenPai> tasks) {
		taskAdapter = new RelativeTaskAdapter(mContext);
		taskAdapter.setArrayTasks(tasks);
		((IRelativeTaskView)mProjectView).setListView(taskAdapter);
		((IRelativeTaskView)mProjectView).refreshComplete();
	}
	
	
	@Override
	public void onModifyProjectSuccess(String ret) {
		if(!ret.equals("0")){
			((IModifyProjectView)mProjectView).showAddSucessTip();
		}else{
			((IModifyProjectView)mProjectView).showAddFaildTip();
		}
	}

	@Override
	public void onGetAllSchedulesSuccess(List<CalendarInfo> schedules) {
		scheduleAdapter = new RelativeScheduleAdapter(mContext);
		scheduleAdapter.setArraySchedules(schedules);
		((IRelativeScheduleView)mProjectView).setListView(scheduleAdapter);
		((IRelativeScheduleView)mProjectView).refreshComplete("");
	}
	
	/**
	 * 获取全局项目集合
	 * @return 项目集合
	 */
	public static ArrayList<UserProject> getGlobalProjects() {
		return globalProjects;
	}
	
	/**
	 * 设置全局项目集合
	 * @param globalProjects 项目集合
	 */
	public static void setGlobalProjects(ArrayList<UserProject> globalProjects) {
		ProjectPresenter.globalProjects.clear();
		ProjectPresenter.globalProjects.addAll(globalProjects);
	}
	
	@Override
	public void onGetProjectsError() {
        ((ISummaryView)mProjectView).onGetProjectsError();
//		((ISummaryView)mProjectView).refreshComplete();
	}

	@Override
	public void onGetSprintsError() {
		((ISummaryView)mProjectView).refreshComplete();
	}
	
	@Override
	public void onGetAllTasksError(String errMsg) {
		((IRelativeTaskView)mProjectView).displayError(errMsg);
	}
	
	@Override
	public void onGetAllSchedulesError(String errMsg) {
		((IRelativeScheduleView)mProjectView).refreshComplete(errMsg);
	}
	@Override
	public void onGetTasksByIdSuccess(List<UserFenPai> tasks) {
		
	}
	@Override
	public void onGetTasksByIdError() {
		
	}
	@Override
	public void onNetworkError() {
		mProjectView.onNetworkError();
	}
	
}
