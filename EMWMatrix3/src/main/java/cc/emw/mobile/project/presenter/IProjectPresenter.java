package cc.emw.mobile.project.presenter;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.ChatterGroup;
import cc.emw.mobile.net.ApiEntity.UserProject;

/**
 * Created by jven.wu on 2016/6/25.
 */
public interface IProjectPresenter {
    void onError(Throwable ex);

    void getProjects(boolean isWithGroup,boolean isForMemberList);
    void onGetProjectAndGroupSuccess(List<UserProject> projects, List<ChatterGroup> groups);
    void onGetProjectSuccess(List<UserProject> projects);

    void createTeam(ChatterGroup group, List<Integer> userids);
    void onCreateTeamSuccess(String msg);
    void onCreateTeamError(String msg);

    void loadGroups();
    void onLoadGroupsSuccess(List<ChatterGroup> groups);

    void createProject(UserProject project);
    void onCreateProjectSuccess(String msg);
    void onCreateProjectError(String msg);

    void onGetProjectsForMemberListSuccess( List<UserProject> projects, List<ChatterGroup> groups);

    void getTasksOfProject(String ids);
    void onGetTaskOfProjectSuccess(List<ApiEntity.UserFenPai> tasks);

    void getTaskByState(int state);
    void onGetTaskByStateSuccess(List<ApiEntity.UserFenPai> tasks);
    void getTaskByUserId(int id);
    void onGetTaskByUserIdSuccess(List<ApiEntity.UserFenPai> tasks);
}
