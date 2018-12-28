package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.Files;


/**
 * 修改任务界面的视图接口
 * @author chengyong.liu
 *
 */
public interface ITaskModifyView extends ITaskBaseView {
	void createTask(String respInfo);//创建任务
	void modifyTask(String s);//修改任务
	void getFileList(List<Files> files);//通过ID字符串获取所有的附件
}
