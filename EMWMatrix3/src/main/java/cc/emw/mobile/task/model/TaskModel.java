package cc.emw.mobile.task.model;

import java.util.List;

import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.entity.UserLabelBean;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Task数据实现接口，用于处理Task中的各种数据逻辑
 *
 * @author chengyong.liu
 */
public class TaskModel implements ITaskModel {

    protected static final String TAG = "TaskModel";


    @Override
    public void getTaskByState(int type, final OnTaskListener listener) {
        API.TalkerAPI.GetTaskByState(type,
                new RequestCallback<UserFenPai>(UserFenPai.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onParseSuccess(List<UserFenPai> taskList) {
                        listener.onGetTaskByStateSuccess(taskList);
                        listener.onCompleteFresh();
                    }
                });
    }

    /**
     * 根据taskID获取任务评论信息
     */
    @Override
    public void getTaskReply(int taskID, final OnTaskListener listener) {
        API.TalkerAPI.GetTaskReplyByTaskId(taskID, 1,
                new RequestCallback<TaskReply>(TaskReply.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onParseSuccess(List<TaskReply> replyList) {
                        listener.onTaskCommentSuccess(replyList);
                        listener.onCompleteFresh();
                    }
                });
    }

    /**
     * 保存评论数据
     */
    @Override
    public void saveTaskReply(TaskReply reply, final OnTaskListener listener) {
        API.TalkerAPI.DoTaskReply(reply, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {
                    }

//                    @Override
//                    public void onSuccess(String arg0) {
//                        listener.onSaveReplySuccess(arg0);
//                    }
                    @Override
                    public void onParseSuccess(ApiEntity.APIResult respInfo) {
                        listener.onSaveReplySuccess(respInfo);
                    }

                }

        );
    }

    /**
     * 删除评论数据
     */
    @Override
    public void deleteTaskReply(int ID, int taskId, final OnTaskListener listener) {
        API.TalkerAPI.DelTaskReply(ID, taskId,
                new RequestCallback<String>(String.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        listener.onDelReplySuccess(arg0);
                    }
                });
    }

    /**
     * 根据项目ID串获取项目下的所有任务
     *
     * @param ids
     * @param listener
     */
    @Override
    public void getTaskOfProject(String ids, final OnTaskListener listener) {
        API.TalkerAPI.GetTaskByProjectId(ids,
                new RequestCallback<UserFenPai>(UserFenPai.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onParseSuccess(List<UserFenPai> tasks) {
                        listener.onGetTaskOfProjectSuccess(tasks);
                        listener.onCompleteFresh();
                    }
                });
    }

    /**
     * 获取所有标签的回调
     *
     * @param userId
     * @param listener
     */
    @Override
    public void getUserLabel(int userId, final OnTaskListener listener) {
        API.TalkerAPI.GetUserLabel(userId,
                new RequestCallback<UserLabelBean>(UserLabelBean.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex, isOnCallback);
                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onParseSuccess(List<UserLabelBean> labels) {
                        listener.onGetUserLabelSuccess(labels);
                        listener.onCompleteFresh();
                    }
                });
    }

    /**
     * 添加标签的回调
     *
     * @param ul
     * @param listener
     */
    @Override
    public void addUsdrLabel(UserLabelBean ul, final OnTaskListener listener) {
        API.TalkerAPI.AddUserLabel(ul, new RequestCallback<String>(
                String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                listener.onAddUsdrLabelSuccess(arg0);
            }
        });
    }

    /**
     * 修改标签的回调
     *
     * @param ul
     * @param listener
     */
    @Override
    public void modifyUserLabel(UserLabelBean ul, final OnTaskListener listener) {
        API.TalkerAPI.ModifyUserLabel(ul, new RequestCallback<String>(
                String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                listener.onModifyUserLabelSuccess(arg0);
            }
        });
    }

    /**
     * 删除标签的回调
     *
     * @param id       要删除标签的id
     * @param listener
     */
    @Override
    public void delUserLabel(int id, final OnTaskListener listener) {
        API.TalkerAPI.DelUserLabel(id, new RequestCallback<String>(
                String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                listener.onDelUserLabelSuccess(arg0);
            }
        });
    }

    /**
     * 修改任务实例
     */
    @Override
    public void modifyTask(UserFenPai userFenPai, final OnTaskListener listener) {
        API.TalkerAPI.ModifyTask(userFenPai, new RequestCallback<String>(
                String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                listener.onModifyTaskSuccess(arg0);
            }
        });
    }

    /**
     * 创建任务实例
     */
    @Override
    public void createTask(UserFenPai userFenPai, final OnTaskListener listener) {
        API.TalkerAPI.AddFenPai(userFenPai, new RequestCallback<String>(String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                listener.onCreateTaskSuccess(result);
            }

        });
    }


    /**
     * 分享附件
     */
    @Override
    public void shareAttachment(List<UserFilePower> list, int fileID,
                                final OnTaskListener listener) {
        API.UserData.DoFilePower(list, fileID, new RequestCallback<String>(
                String.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                listener.onShareAttachmentSuccess(arg0);
            }
        });
    }

    /**
     * 通过附件字符串ID获取附件实体
     */
    @Override
    public void getFileListByIds(String ids, final OnTaskListener listener) {
        API.UserData.GetFileListByIds(ids, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onParseSuccess(List<Files> files) {
                listener.onGetFileListByIdsSuccess(files);
                listener.onCompleteFresh();
            }
        });
    }

    /**
     * 获取所有项目
     */
    @Override
    public void getAllProjects(final OnTaskListener listener) {
        API.TalkerAPI.GetProjectByUserId(PrefsUtil.readUserInfo().ID, new RequestCallback<UserProject>(
                UserProject.class) {
            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError(ex, isOnCallback);
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onParseSuccess(List<UserProject> projects) {
                listener.onGetAllProjectsSuccess(projects);
                listener.onCompleteFresh();
            }
        });
    }
}
