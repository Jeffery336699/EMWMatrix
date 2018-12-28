package cc.emw.mobile.task.presenter;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserFenPai;

import cc.emw.mobile.task.entity.UserLabelBean;
import cc.emw.mobile.task.model.OnTaskListener;
import cc.emw.mobile.task.model.TaskModel;
import cc.emw.mobile.task.view.ITaskBaseView;
import cc.emw.mobile.task.view.ITaskCommentView;
import cc.emw.mobile.task.view.ITaskLableView;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.task.view.ITaskOfProjectView;
import cc.emw.mobile.task.view.ITaskView;
import cc.emw.mobile.task.view.IWorkProjectView;
import cc.emw.mobile.task.view.ItaskShareAttachmentView;


/**
 * 连接Model与View 1、获取数据 2、调用view的方法 展示数据
 *
 * @author chengyong.liu
 */
public class TaskPresenter implements OnTaskListener {

    private ITaskBaseView mITaskBaseView;
    private TaskModel mITaskModel;

    public TaskPresenter(ITaskBaseView view) {
        mITaskBaseView = view;
        mITaskModel = new TaskModel();
    }


    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mITaskBaseView.onError(ex, isOnCallback);
    }

    @Override
    public void onCompleteFresh() {
        mITaskBaseView.completeFresh();
    }

    /**
     * 根据任务状态获取任务
     *
     * @param type 0为获取所有的任务
     */
    public void getTaskByState(int type) {
        mITaskModel.getTaskByState(type, this);
    }

    @Override
    public void onGetTaskByStateSuccess(List<UserFenPai> taskList) {
        ((ITaskView) mITaskBaseView).showTask(taskList);
    }

    /**
     * 获取任务评论列表
     *
     * @param taskID 任务ID
     */
    public void getTaskReply(int taskID) {
        // 获取任务评论
        mITaskModel.getTaskReply(taskID, this);
    }

    @Override
    public void onTaskCommentSuccess(List<ApiEntity.TaskReply> replyList) {
        // 获取任务评论成功的回调
        ((ITaskCommentView) mITaskBaseView).showReply(replyList);
    }

    /**
     * 添加评论
     *
     * @param reply
     */
    public void saveTaskReply(ApiEntity.TaskReply reply) {
        mITaskModel.saveTaskReply(reply, this);
    }

    @Override
    public void onSaveReplySuccess(ApiEntity.APIResult respInfo) {
        // 保存评论成功
        ((ITaskCommentView) mITaskBaseView).saveReply(respInfo);
    }

    /**
     * 删除评论
     *
     * @param ID
     */
    public void deleteTaskReply(int ID, int taskId) {
        // 删除评论
        mITaskModel.deleteTaskReply(ID, taskId, this);
    }

    @Override
    public void onDelReplySuccess(String s) {
        ((ITaskCommentView) mITaskBaseView).delReply(s);
    }

    /**
     * 创建任务
     *
     * @param userFenPai 任务实体
     */
    public void createTask(UserFenPai userFenPai) {
        mITaskModel.createTask(userFenPai, this);
    }


    @Override
    public void onCreateTaskSuccess(String respInfo) {
        ((ITaskModifyView) mITaskBaseView).createTask(respInfo);
    }
    /**
     * 修改任务
     *
     * @param userFenPai 任务实体
     */
    public void modifyTask(UserFenPai userFenPai) {
        mITaskModel.modifyTask(userFenPai, this);
    }

    @Override
    public void onModifyTaskSuccess(String s) {
        ((ITaskModifyView) mITaskBaseView).modifyTask(s);
    }

    /**
     * 附件分享
     *
     * @param list   分享给用户的需求
     * @param fileID 分享的附件ID
     */
    public void shareAttachment(List<ApiEntity.UserFilePower> list, int fileID) {
        mITaskModel.shareAttachment(list, fileID, this);
    }

    @Override
    public void onShareAttachmentSuccess(String s) {
        ((ItaskShareAttachmentView) mITaskBaseView).shareAttachmentSuccess(s);

    }

    /**
     * 通过Ids字符串获取所有的附件
     *
     * @param ids
     */
    public void getFileListByIds(String ids) {
        mITaskModel.getFileListByIds(ids, this);
    }

    @Override
    public void onGetFileListByIdsSuccess(List<ApiEntity.Files> files) {
        ((ITaskModifyView) mITaskBaseView).getFileList(files);

    }

    /**
     * 通过项目Ids获取所有的任务
     *
     * @param ids
     */
    public void getTaskOfProject(String ids) {
        mITaskModel.getTaskOfProject(ids, this);
    }

    @Override
    public void onGetTaskOfProjectSuccess(List<UserFenPai> tasks) {
        ((ITaskOfProjectView) mITaskBaseView).showTaskOfProject(tasks);
    }

    /**
     * 获取所有的项目
     */
    public void getAllProjects() {
        mITaskModel.getAllProjects(this);
    }

    @Override
    public void onGetAllProjectsSuccess(List<ApiEntity.UserProject> projects) {
        // 获取所有的项目实例对象
        ((IWorkProjectView) mITaskBaseView).showProjects(projects);
    }

    /**
     * 获取所有的标签
     *
     * @param userId
     */
    public void getUserLable(int userId) {
        mITaskModel.getUserLabel(userId, this);
    }

    @Override
    public void onGetUserLabelSuccess(List<UserLabelBean> labels) {
        ((ITaskLableView) mITaskBaseView).getUserLable(labels);
    }

    /**
     * 添加任务标签
     *
     * @param ul
     */
    public void addUserLable(UserLabelBean ul) {
        mITaskModel.addUsdrLabel(ul, this);
    }

    @Override
    public void onAddUsdrLabelSuccess(String s) {
        ((ITaskLableView) mITaskBaseView).addUserLable(s);
    }

    /**
     * 修改任务标签
     *
     * @param ul
     */
    public void modifyUserLable(UserLabelBean ul) {
        mITaskModel.modifyUserLabel(ul, this);
    }

    @Override
    public void onModifyUserLabelSuccess(String s) {
        ((ITaskLableView) mITaskBaseView).modifyUserLable(s);
    }

    /**
     * 删除任务标签
     *
     * @param labelId
     */
    public void delUserLable(int labelId) {
        mITaskModel.delUserLabel(labelId, this);
    }

    @Override
    public void onDelUserLabelSuccess(String s) {
        ((ITaskLableView) mITaskBaseView).delUserLable(s);
    }
}
