package cc.emw.mobile.project.model;

import android.text.TextUtils;
import android.util.Log;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.entities.UserSprint;

/**
 * 项目数据处理-M层
 * @author jven.wu
 *
 */
public class ProjectModel implements IProjectModel {
	private static final String TAG = "ProjectModel";
	private ArrayList<UserProject> userProjects = new ArrayList<UserProject>();
	private ArrayList<UserSprint> userSprints = new ArrayList<UserSprint>();
	private ArrayList<UserFenPai> userTasks = new ArrayList<UserFenPai>();
	private ArrayList<CalendarInfo> shcedules = new ArrayList<CalendarInfo>();

	@Override
	public void getProjects(final OnProjectListener listener) {
		API.TalkerAPI.GetProject(new RequestCallback<UserProject>(UserProject.class) {
			@Override
			public void onCancelled(CancelledException cex) {
				Log.d(TAG, "getProjects onCancelled");
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(ex instanceof ConnectException){
					listener.onNetworkError();
					return;
				}
				listener.onGetProjectsError();
			}

			@Override
			public void onFinished() {
				Log.d(TAG, "getProjects onFinished");
			}
			
			@Override
			public void onParseSuccess(List<UserProject> projects) {
				userProjects.clear();
				userProjects.addAll(projects);
				getSprints(listener);
			}
		});
//		RequestParams params = new RequestParams(HttpConstant.GET_PROJECT);
//		params.addQueryStringParameter("userid", PrefsUtil.readUserInfo().getID()+"");
//		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<UserProject>(UserProject.class) {
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				Log.d(TAG, "getProjects onCancelled");
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				Log.d(TAG, "getProjects onError"+ex.getMessage());
//				listener.onGetProjectsError();
//			}
//
//			@Override
//			public void onFinished() {
//				Log.d(TAG, "getProjects onFinished");
//			}
//
//			@Override
//			public void onParseSuccess(List<UserProject> projects) {
//				userProjects.clear();
//				userProjects.addAll(projects);
//				Log.d(TAG,"project count: "+userProjects.size());
//				getSprints(listener);
//			}
//		});
	}

    @Override
    public void GetProjectByPage(final int PageNo, int PageSize, final OnProjectListener listener) {
        API.TalkerAPI.GetProjectByPage(PageNo,PageSize,new RequestCallback<UserProject>(UserProject.class){
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(ex instanceof ConnectException){
                    listener.onNetworkError();
                    return;
                }
                listener.onGetProjectsError();
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "getProjects onFinished");
            }

            @Override
            public void onParseSuccess(List<UserProject> projects) {
                if(PageNo == 1) {
                    userProjects.clear();
                }
                userProjects.addAll(projects);
                getSprints(listener);
            }
        });
    }

    @Override
	public UserFenPai getTaskByProjectId(String id) {
		return null;
	}

	@Override
	public void getSprints(final OnProjectListener listener) {
		API.TalkerAPI.GetSprint(0, new RequestCallback<UserSprint>(UserSprint.class) {
			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				listener.onGetSprintsError();
			}

			@Override
			public void onFinished() {
			}
			
			@Override
			public void onParseSuccess(List<UserSprint> sprints) {
				userSprints.clear();
				for(int i = 0;i<sprints.size();i++){
					UserSprint temp = sprints.get(i);
					int j;
					for(j = i - 1;j>=0;j--){
						SimpleDateFormat dateFormat = 
								new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date d1 = new Date();
						Date d2 = new Date();
						try{
							d1 = dateFormat.parse(sprints.get(j).CreateTime);
							d2 = dateFormat.parse(temp.CreateTime);
						}catch(Exception e){
							e.printStackTrace();
						}
						
						if(d1.getTime()>d2.getTime()){
							sprints.set(j+1, sprints.get(j));
						}else{
							break;
						}
					}
					sprints.set(j+1, temp);
				}
				userSprints.addAll(sprints);
				getTasksByIds(connectTasksId(userSprints), listener);
			}
		});
//		RequestParams params = new RequestParams(HttpConstant.GET_SPRINT);
//		params.addQueryStringParameter("userid", PrefsUtil.readUserInfo().getID()+"");
//		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<UserSprint>(UserSprint.class) {
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				listener.onGetSprintsError();
//			}
//
//			@Override
//			public void onFinished() {
//			}
//			
//			@Override
//			public void onParseSuccess(List<UserSprint> sprints) {
//				userSprints.clear();
//				userSprints.addAll(sprints);
//				Log.d(TAG,"sprint count: "+userSprints.size());
//				getTasksByIds(connectTasksId(userSprints), listener);
////				listener.onGetProjectsSuccess(userProjects,userSprints);
//			}
//		});
	}

	@Override
	public void getAllTasks(final OnProjectListener listener) {
		API.TalkerAPI.GetTaskByState(0, new RequestCallback<UserFenPai>(UserFenPai.class) {
			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Log.d(TAG,"getAllTasks error");
				if(ex instanceof ConnectException){
					listener.onNetworkError();
					return;
				}
				listener.onGetAllTasksError(ex.getMessage());
			}

			@Override
			public void onFinished() {
				Log.d(TAG,"getAllTasks finish");
			}
			
			@Override
			public void onParseSuccess(List<UserFenPai> tasks) {
				userTasks.clear();
				userTasks.addAll(tasks);
				listener.onGetAllTasksSuccess(userTasks);
			}
		});
		
//		RequestParams params = new RequestParams(HttpConstant.TASK_GET_BYSTATE);
//		params.addQueryStringParameter("state", "0");
//		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<UserFenPai>(UserFenPai.class) {
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				Log.d(TAG,"getAllTasks error");
////				listener.onGetAllTasksError();
//			}
//
//			@Override
//			public void onFinished() {
//				Log.d(TAG,"getAllTasks finish");
//			}
//			
//			@Override
//			public void onParseSuccess(List<UserFenPai> tasks) {
//				userTasks.clear();
//				userTasks.addAll(tasks);
//				Log.d("jvenwu","tasks count: "+userTasks.size());
//				listener.onGetAllTasksSuccess(userTasks);
//			}
//		});
	}

	@Override
	public void modifyProject(UserProject project,
			final OnProjectListener listener) {
		API.TalkerAPI.DoProject(project, new RequestCallback<String>(String.class) {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				listener.onModifyProjectSuccess(arg0);
			}
		});
		
//		RequestParams params = new RequestParams(HttpConstant.PROJECT_DO_PROJECT);
//		try {
//			StringBody stringBody = new StringBody("up="
//					+ new Gson().toJson(project), "UTF-8");
//			Log.d(TAG, new Gson().toJson(project));
//			stringBody.setContentType("application/x-www-form-urlencoded");
//			params.setRequestBody(stringBody);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		Callback.Cancelable cancelable = x.http().post(params, new CommonCallback<String>() {
//
//			@Override
//			public void onCancelled(CancelledException arg0) {
//				
//			}
//
//			@Override
//			public void onError(Throwable arg0, boolean arg1) {
//				
//			}
//
//			@Override
//			public void onFinished() {
//				
//			}
//
//			@Override
//			public void onSuccess(String arg0) {
//				listener.onModifyProjectSuccess(arg0);
//			}
//		});
	}

	@Override
	public void getAllSchedules(final OnProjectListener listener) {
		API.TalkerAPI.GetAllCalenderList(new RequestCallback<CalendarInfo>(CalendarInfo.class) {
			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(ex instanceof ConnectException){
					listener.onNetworkError();
					return;
				}
				listener.onGetAllSchedulesError(ex.getMessage());
			}

			@Override
			public void onFinished() {
				Log.d(TAG,"getAllSchedules finish");
			}
			
			@Override
			public void onParseSuccess(List<CalendarInfo> calendars) {
				shcedules.clear();
				shcedules.addAll(calendars);
				listener.onGetAllSchedulesSuccess(shcedules);
			}
		});
//		RequestParams params = new RequestParams(HttpConstant.PROJECT_GET_ALL_SCHEDULE);
//		params.addQueryStringParameter("userid", PrefsUtil.readUserInfo().getID()+"");
//		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<CalendarInfo>(CalendarInfo.class) {
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				Log.d(TAG,"getAllSchedules error " + ex.getMessage());
//				listener.onGetAllSchedulesError();
//			}
//
//			@Override
//			public void onFinished() {
//				Log.d(TAG,"getAllSchedules finish");
//			}
//			
//			@Override
//			public void onParseSuccess(List<CalendarInfo> calendars) {
//				shcedules.clear();
//				shcedules.addAll(calendars);
//				Log.d(TAG,"shcedules count: "+shcedules.size());
//				listener.onGetAllSchedulesSuccess(shcedules);
//			}
//		});
	}

	@Override
	public void getTasksByIds(String ids, final OnProjectListener listener) {
		API.TalkerAPI.GetTaskByIds(ids, new RequestCallback<UserFenPai>(UserFenPai.class) {
			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				listener.onGetTasksByIdError();
			}

			@Override
			public void onFinished() {
			}
			
			@Override
			public void onParseSuccess(List<UserFenPai> tasks) {
				for(int i = 0;i<userSprints.size();i++){
					userSprints.get(i).setTasks(
							filterTasksById(userSprints.get(i).getContent(), tasks));
				}
				listener.onGetProjectsSuccess(userProjects,userSprints);
			}
		});
//		RequestParams params = new RequestParams(HttpConstant.GET_TASKS_BY_IDS);
//		params.addQueryStringParameter("userid", PrefsUtil.readUserInfo().getID()+"");
//		params.addQueryStringParameter("ids", ids);
//		Callback.Cancelable cancelable = x.http().post(params, new RequestListener<UserFenPai>(UserFenPai.class) {
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				Log.d(TAG, "getProjects onCancelled");
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				Log.d(TAG, "getProjects onError"+ex.getMessage());
//				listener.onGetTasksByIdError();
//			}
//
//			@Override
//			public void onFinished() {
//				Log.d(TAG, "getProjects onFinished");
//			}
//			
//			@Override
//			public void onParseSuccess(List<UserFenPai> tasks) {
//				for(int i = 0;i<userSprints.size();i++){
//					userSprints.get(i).setTasks(
//							filterTasksById(userSprints.get(i).getContent(), tasks));
//				}
//				listener.onGetProjectsSuccess(userProjects,userSprints);
//			}
//		});
	}
	
	/**
	 * 把冲刺集合中的任务拼接起来，用于一次请求冲刺的所有任务
	 * @param sprints 冲刺集合
	 * @return
	 */
	private String connectTasksId(ArrayList<UserSprint> sprints){
		StringBuilder sBuilder =new StringBuilder();
		for(int i = 0;i<sprints.size();i++){
			if(sprints.get(i).getContent().equals("")){
				continue;
			}
			sBuilder.append(sprints.get(i).getContent());
			sBuilder.append(",");
		}
		String idString = sBuilder.toString();
		if(idString.length()>0){
			idString = idString.substring(0,idString.length()-1);
		}
		return idString;
	}
	/**
	 * 根据任务id字符串过滤出相应任务
	 * @param ids 任务id字符串
	 * @param tasks 要被过滤的任务
	 * @return
	 */
	private List<UserFenPai> filterTasksById(String ids,List<UserFenPai> tasks){
		List<UserFenPai> contentTasks = new ArrayList<UserFenPai>();
		String[] strings;
		if(TextUtils.isEmpty(ids) || ids.equals("")){
			return contentTasks;
		}
		strings = ids.split(",");
		for(int i = 0;i<tasks.size();i++){
    		boolean flag = false;
    		for(int j=0;j<strings.length;j++){
    			if (!TextUtils.isEmpty(strings[j]) 
    					&& TextUtils.isDigitsOnly(strings[j]) 
    					&& Integer.valueOf(strings[j]).intValue() == tasks.get(i).ID) {
    				flag = true;
    				break;
    			}
    		}
    		if(flag){
    			contentTasks.add(tasks.get(i));
    		}
    	}
		return contentTasks;
	}
}
