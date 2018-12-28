package cc.emw.mobile.task.model;

import java.util.List;

import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.ApiEntity.UserSprint;
import cc.emw.mobile.net.RequestCallback;

/**
 * Task数据实现接口，用于处理Task中的各种数据逻辑
 * 
 * @author chengyong.liu
 * 
 */
public class TaskModel implements ITaskModel {

	protected static final String TAG = "TaskModel";

	/**
	 * 根据taskID获取任务评论信息
	 */
	@Override
	public void getTaskReply(int taskID, final OnTaskListener listener) {
		API.TalkerAPI.GetTaskReplyByTaskId(taskID,
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
		API.TalkerAPI.DoTaskReply(reply, new RequestCallback<String>(
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
				listener.onSaveReplySuccess(arg0);
			}

		}

		);
	}

	/**
	 * 删除评论数据
	 */
	@Override
	public void deleteTaskReply(int ID, final OnTaskListener listener) {
		API.TalkerAPI.DelTaskReply(ID,
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
	 * 根据任务状态获取项目
	 */
	@Override
	public void getProjectsByTaskState(int state, final OnTaskListener listener) {
		API.TalkerAPI.GetMobileProjectByTaskState(state,
				new RequestCallback<UserProject>(UserProject.class) {
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
					public void onParseSuccess(List<UserProject> projectList) {
						listener.onProjectGetByStateSuccess(projectList);
						listener.onCompleteFresh();
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
		API.TalkerAPI.AddFenPai(userFenPai, new RequestCallback<String>(
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
				listener.onCreateTaskSuccess(arg0);
			}
		});
	}

	/**
	 * 获取所有项目
	 */
	@Override
	public void getAllProjects(final OnTaskListener listener) {
		API.TalkerAPI.GetProject(new RequestCallback<UserProject>(
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

	/**
	 * 获取所有项目冲刺
	 */
	@Override
	public void getAllSprints(final OnTaskListener listener) {
		API.TalkerAPI.GetSprint(0, new RequestCallback<UserSprint>(
				UserSprint.class) {
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
			public void onParseSuccess(List<UserSprint> sprints) {
				listener.onGetAllSprintsSuccess(sprints);
				listener.onCompleteFresh();
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
	 * 添加任务冲刺
	 */
	@Override
	public void doSprintTask(List<UserSprint> usList, UserSprint us,
			int taskID, final OnTaskListener listener) {
		// API.TalkerAPI.AddSprintTask(usList, taskID,
		// new RequestCallback<String>(String.class) {
		// @Override
		// public void onCancelled(CancelledException arg0) {
		// }
		//
		// @Override
		// public void onError(Throwable ex, boolean isOnCallback) {
		// listener.onError(ex, isOnCallback);
		// }
		//
		// @Override
		// public void onFinished() {
		// }
		//
		// @Override
		// public void onSuccess(String arg0) {
		// listener.onAddSprintTaskSuccess(arg0);
		// }
		// });

		API.TalkerAPI.DoSprintTask(usList, us, taskID,
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
						listener.onDoSprintTaskSuccess(arg0);
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

}
