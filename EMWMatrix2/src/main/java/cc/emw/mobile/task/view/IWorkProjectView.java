package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.UserProject;

/**
 * Task视图顶层接口
 * @author chengyong.liu
 *
 */
public interface IWorkProjectView extends ITaskBaseView{
	void showProjects(List<UserProject> projects);//展示所有项目
}
