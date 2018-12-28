package cc.emw.mobile.task.view;

/**
 * Task视图顶层接口
 * @author chengyong.liu
 *
 */
public interface ITaskBaseView {
	/**
	 * 获取数据完毕
	 */
	void completeFresh();
	/**
	 * 解析数据失败
	 */
	void onError(Throwable ex, boolean isOnCallback);
}
