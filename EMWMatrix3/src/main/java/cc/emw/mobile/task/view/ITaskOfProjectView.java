package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;


/**
 * 修改任务界面的视图接口
 * @author chengyong.liu
 *
 */
public interface ITaskOfProjectView extends ITaskBaseView {
	void showTaskOfProject(List<ApiEntity.UserFenPai> tasks);//展示项目下所有的子任务
}
