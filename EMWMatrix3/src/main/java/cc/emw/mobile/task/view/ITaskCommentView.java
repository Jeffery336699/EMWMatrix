package cc.emw.mobile.task.view;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.TaskReply;

/**
 * 任务详情评论界面的View接口
 * @author chengyong.liu
 *
 */
public interface ITaskCommentView extends ITaskBaseView {
	/**
	 * 任务评论数据的展示
	 * @param replyList
	 */
	void showReply(List<TaskReply> replyList);
	/**
	 * 保存评论
	 * @param respInfo
	 */
	void saveReply(ApiEntity.APIResult respInfo);
	/**
	 * 删除评论
	 * @param s
	 */
	void delReply(String s);//删除评论
}
