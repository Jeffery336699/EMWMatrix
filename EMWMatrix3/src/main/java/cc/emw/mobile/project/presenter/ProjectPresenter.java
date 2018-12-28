package cc.emw.mobile.project.presenter;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.ChatterGroup;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.GroupViewBean;
import cc.emw.mobile.project.bean.MemberProject;
import cc.emw.mobile.project.bean.ProjectViewBean;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.project.model.IProjectModel;
import cc.emw.mobile.project.model.ProjectModel;
import cc.emw.mobile.project.view.IBaseProjectView;
import cc.emw.mobile.project.view.INewProjectView;
import cc.emw.mobile.project.view.INewTeamView;
import cc.emw.mobile.project.view.IObserveProjectActivity;
import cc.emw.mobile.project.view.IProjectListView;
import cc.emw.mobile.project.view.IProjectMemberView;
import cc.emw.mobile.project.view.IProjectStateView;
import cc.emw.mobile.project.view.ITeamListView;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by jven.wu on 2016/6/25.
 */
public class ProjectPresenter implements IProjectPresenter {
    private final String TAG = this.getClass().getSimpleName();

    private IProjectModel mProjectModel; //模型层接口
    private IBaseProjectView mProjectView; //页面接口

    public ProjectPresenter(IBaseProjectView view) {
        this.mProjectModel = new ProjectModel();
        this.mProjectView = view;
    }

    @Override
    public void onError(Throwable ex) {
        mProjectView.onError(ex);
    }

    /**
     * 获取项目
     * @param isWithGroup 是否要同时获取团队
     * @param isForMemberList 是否供成员列表使用
     */
    @Override
    public void getProjects(boolean isWithGroup,boolean isForMemberList) {
        mProjectModel.getProjects(this, isWithGroup,isForMemberList);
    }


    /**
     * 获取项目和团队成功回调
     * @param projects
     * @param groups
     */
    @Override
    public void onGetProjectAndGroupSuccess(
            List<UserProject> projects, List<ChatterGroup> groups) {
        ArrayList<GroupProject> groupProjects = getGroupProjects(projects, groups);

        Logger.d(TAG, "onGetProjectAndGroupSuccess()->count: " + groupProjects.size());

        ((IProjectListView) mProjectView).renderProjectListView(groupProjects);
    }

    /**
     * 获取项目和团队
     * @param projects
     * @param groups
     * @return
     */
    @NonNull
    private ArrayList<GroupProject> getGroupProjects(List<UserProject> projects, List<ChatterGroup> groups) {
        ArrayList<GroupProject> groupProjects = new ArrayList<>();

        for (int i = 0; i < groups.size(); i++) {
            GroupProject groupProject = new GroupProject();
            ChatterGroup group = groups.get(i);
            groupProject.GroupId = group.ID;
            groupProject.GroupCreator = group.CreateUser;
            groupProject.GroupName = group.Name;
            groupProject.GroupImg = group.Image;
            groupProject.group = group;

            if (group.MainUserInfo != null && group.MainUserInfo.size() > 0){
                for(int k = 0;k<group.MainUserInfo.size();k++){
                    groupProject.UsersId.add(group.MainUserInfo.get(k).ID);
                }
            }

            if (group.Users != null && group.Users.size() > 0) {
                for (int j = 0; j < group.Users.size(); j++) {
                    groupProject.UsersId.add(group.Users.get(j).ID);
                }
            }

            for (int k = 0; k < projects.size(); k++) {
                UserProject project = projects.get(k);
                boolean isBelongProject;
                isBelongProject = filterVisibleProject(project);
                if(!isBelongProject){
                    continue;
                }
                if (project.TeamId == group.ID) {
                    ProjectViewBean bean = new ProjectViewBean();
                    bean.Project = project;
                    bean.ProjectId = project.ID;
                    bean.ProjectName = project.Name;
                    bean.ProjectColor = project.Color;
//                    bean.TaskNum += project.Tasks.size();
                    groupProject.projectViews.add(bean);
                }
            }

            groupProjects.add(groupProject);
        }
        return groupProjects;
    }

    /**
     * 根据权限过滤出项目
     * @param project
     * @return
     */
    private boolean filterVisibleProject(UserProject project) {
        boolean isBelongProject = false;
        /*ArrayList<ApiEntity.UserInfo> userList = new ArrayList<>();
        userList.clear();
        userList.addAll(project.MainUserList);
        userList.addAll(project.UsersList);
        for(int j = 0;j<userList.size();j++){
            ApiEntity.UserInfo user = userList.get(j);
            if(user.ID == PrefsUtil.readUserInfo().ID) {
                isBelongProject = true;
                break;
            }
        }*/
        String userIds = project.MainUser + "," + project.Users;
        if (Arrays.asList(userIds.split(",")).contains(Integer.toString(PrefsUtil.readUserInfo().ID))) {
            isBelongProject = true;
        }
        return isBelongProject;
    }

    @Override
    public void onGetProjectSuccess(List<UserProject> projects) {
//        Log.d(TAG, "onGetProjectSuccess()->count: " + projects.size());
//        ArrayList<StateProject> stateProjects = new ArrayList<>();
//
//        for (int i = 0; i < 4; i++) {
//            StateProject stateProject = new StateProject();
//            stateProject.ProjectState = getProjectState(i);
//            stateProjects.add(stateProject);
//        }
//
//        for (int i = 0; i < projects.size(); i++) {
//            UserProject project = projects.get(i);
//            boolean isBelongProject;
//            isBelongProject = filterVisibleProject(project);
//            if(!isBelongProject){
//                continue;
//            }
//            ProjectViewBean projectViewBean = new ProjectViewBean();
//            projectViewBean.Project = project;
//            projectViewBean.ProjectId = project.ID;
//            projectViewBean.ProjectName = project.Name;
//            projectViewBean.ProjectColor = project.Color;
//            projectViewBean.Date = project.CreateTime;
//            projectViewBean.isBelongProject = true;
//            stateProjects.get(project.State - 1).projectViews.add(projectViewBean);
//        }
//
//        ((IProjectStateView) mProjectView).renderView(stateProjects);
    }

    /**
     * 创建团队
     * @param group
     * @param userids
     */
    @Override
    public void createTeam(ChatterGroup group, List<Integer> userids) {
        Logger.d(TAG,"createTeam()->" + new Gson().toJson(group));
        mProjectModel.createTeam(group, userids, this);
    }

    /**
     * 创建团队成功回调
     * @param msg
     */
    @Override
    public void onCreateTeamSuccess(String msg) {
        ((INewTeamView) mProjectView).renderView(true, msg);
    }

    /**
     * 创建团队错误回调
     * @param msg
     */
    @Override
    public void onCreateTeamError(String msg) {
        ((INewTeamView) mProjectView).renderView(false, msg);
    }

    /**
     * 加载团队
     */
    @Override
    public void loadGroups() {
        mProjectModel.loadGroups(this, false,false);
    }

    /**
     * 加载团队成功回调
     * @param groups
     */
    @Override
    public void onLoadGroupsSuccess(List<ChatterGroup> groups) {
        ArrayList<GroupViewBean> listGroups = new ArrayList<>();
        for (ChatterGroup g : groups) {
            GroupViewBean group = new GroupViewBean();
            group.group = g;
            group.GroupId = g.ID;
            group.GroupName = g.Name;
            listGroups.add(group);
        }
        ((ITeamListView) mProjectView).renderView(listGroups);
    }

    /**
     * 创建项目
     * @param project
     */
    @Override
    public void createProject(UserProject project) {
        mProjectModel.createProject(project, this);
    }

    /**
     * 创建项目成功回调
     * @param msg
     */
    @Override
    public void onCreateProjectSuccess(String msg) {
        ((INewProjectView) mProjectView).renderView(true, msg);
    }

    /**
     * 创建项目错误回调
     * @param msg
     */
    @Override
    public void onCreateProjectError(String msg) {
        ((INewProjectView) mProjectView).renderView(false, msg);
    }

    /**
     * 获取人员+项目列表成功回调
     * @param projects
     * @param groups
     */
    @Override
    public void onGetProjectsForMemberListSuccess( List<UserProject> projects, List<ChatterGroup> groups) {
        ArrayList<GroupProject> groupProjects = getGroupProjects(projects, groups);
        Set<Integer> creatorIds = new HashSet<>();
        for(int i = 0;i<groups.size();i++){
            creatorIds.add(groups.get(i).CreateUser);
        }
        ArrayList<MemberProject> memberProjects = new ArrayList<>();
        for(int id:creatorIds){
            MemberProject mb = new MemberProject();
            mb.ProjectNum = 0;
            mb.UserId = id;
            for(int i = 0;i<groupProjects.size();i++){
                if(id == groupProjects.get(i).GroupCreator){
                    mb.groupProjects.add(groupProjects.get(i));
                    mb.ProjectNum += groupProjects.get(i).projectViews.size();
                    for(ProjectViewBean p : groupProjects.get(i).projectViews){
                        mb.TaskNum += p.Project.TaskCount;
                    }
//                    mb.groupProjects.add(groupProjects.get(i));
                }
            }
            memberProjects.add(mb);
        }

        ((IProjectMemberView)mProjectView).renderView(memberProjects);
    }

    /**
     * 根据项目获取任务
     * @param ids
     */
    @Override
    public void getTasksOfProject(String ids) {
        mProjectModel.getTaskOfProject(ids,this);
    }

    /**
     * 根据项目获取任务成功回调
     * @param tasks
     */
    @Override
    public void onGetTaskOfProjectSuccess(List<ApiEntity.UserFenPai> tasks) {
        ArrayList<ApiEntity.UserFenPai> tempTasks = new ArrayList<ApiEntity.UserFenPai>();
        tempTasks.clear();
        tempTasks.addAll(tasks);
        ((IObserveProjectActivity)mProjectView).renderView(tempTasks);
    }

    /**
     * 通过状态获取任务
     * @param state
     */
    @Override
    public void getTaskByState(int state) {
        mProjectModel.getTaskByState(this,state);
    }

    /**
     * 通过状态获取任务成功回调
     * @param tasks
     */
    @Override
    public void onGetTaskByStateSuccess(List<ApiEntity.UserFenPai> tasks) {
        Logger.d(TAG, "onGetProjectSuccess()->count: " + tasks.size());
        ArrayList<StateTask> stateTasks = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            StateTask stateTask = new StateTask();
            stateTask.TaskState = getTaskState(i);
            stateTasks.add(stateTask);
        }

        for (int i = 0; i < tasks.size(); i++) {
            ApiEntity.UserFenPai task = tasks.get(i);
//            boolean isBelongProject;
//            isBelongProject = filterVisibleProject(task);
//            if(!isBelongProject){
//                continue;
//            }
            stateTasks.get(task.State - 1).TaskViewBean.add(task);
        }

        ((IProjectStateView) mProjectView).renderProjectStateView(stateTasks);
    }

    /**
     * 根据用户id获取任务
     * @param id
     */
    @Override
    public void getTaskByUserId(int id) {
        mProjectModel.getTaskByUserId(this,id);
    }

    /**
     * 根据用户id获取任务成回调
     * @param tasks
     */
    @Override
    public void onGetTaskByUserIdSuccess(List<ApiEntity.UserFenPai> tasks) {
        Logger.d(TAG, "onGetProjectSuccess()->count: " + tasks.size());
        ArrayList<StateTask> stateTasks = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            StateTask stateTask = new StateTask();
            stateTask.TaskState = getTaskState(i);
            stateTasks.add(stateTask);
        }

        for (int i = 0; i < tasks.size(); i++) {
            ApiEntity.UserFenPai task = tasks.get(i);
//            boolean isBelongProject;
//            isBelongProject = filterVisibleProject(task);
//            if(!isBelongProject){
//                continue;
//            }
            stateTasks.get(task.State - 1).TaskViewBean.add(task);
        }

        ((IProjectStateView) mProjectView).renderProjectStateView(stateTasks);
    }

    private String getProjectState(int index) {
        switch (index) {
            case 0:
                return "未开始";
            case 1:
                return "进行中";
            case 2:
                return "已完成";
            case 3:
                return "延迟";
            default:
                return "index error";
        }
    }

    /**
     * 获取任务状态
     * @param index
     * @return
     */
    private String getTaskState(int index) {
        switch (index) {
            case TaskConstant.TaskState.PROCESSING:
                return "进行中";
            case TaskConstant.TaskState.UNSTART:
                return "未开始";
            case TaskConstant.TaskState.FINISHED:
                return "已完成";
            case TaskConstant.TaskState.DELAY:
                return "延迟";
            default:
                return "index error";
        }
    }
}
