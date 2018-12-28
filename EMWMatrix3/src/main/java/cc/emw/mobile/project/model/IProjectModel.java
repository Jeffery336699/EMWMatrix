package cc.emw.mobile.project.model;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.ChatterGroup;
import cc.emw.mobile.project.presenter.IProjectPresenter;

/**
 * MVP模式中-Model层接口
 * Created by jven.wu on 2016/6/25.
 */
public interface IProjectModel {
    /**
     * 获取项目
     * @param presenter
     * @param isWithGroup
     * @param isForMemberList
     */
    void getProjects(IProjectPresenter presenter,boolean isWithGroup,boolean isForMemberList);

    /**
     * 获取项目组
     * @param presenter
     * @param isWithProject
     * @param isForMemberList
     */
    void loadGroups(IProjectPresenter presenter,boolean isWithProject,final boolean isForMemberList);

    /**
     * 创建团队
     * @param group
     * @param userids
     * @param presenter
     */
    void createTeam(ChatterGroup group, List<Integer> userids,IProjectPresenter presenter);

    /**
     * 创建项目
     * @param project
     * @param presenter
     */
    void createProject(ApiEntity.UserProject project,IProjectPresenter presenter);

    /**
     * 根据项目获得任务
     * @param ids
     * @param presenter
     */
    void getTaskOfProject(String ids, IProjectPresenter presenter);

    /**
     * 根据状态获取任务
     * @param presenter
     * @param state
     */
    void getTaskByState(IProjectPresenter presenter,int state);

    /**
     * 根据用户id获取任务
     * @param presenter
     * @param id
     */
    void getTaskByUserId(IProjectPresenter presenter,int id);
}
