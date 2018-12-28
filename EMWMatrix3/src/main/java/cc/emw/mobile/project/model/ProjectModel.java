package cc.emw.mobile.project.model;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.presenter.IProjectPresenter;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by jven.wu on 2016/6/25.
 */
public class ProjectModel implements IProjectModel {
    private final String TAG = this.getClass().getSimpleName();

    private List<ApiEntity.UserProject> projects = new ArrayList<>();
    @Override
    public void getProjects(final IProjectPresenter presenter, final boolean isWithGroup, final boolean isForMemberList) {
        API.TalkerAPI.GetProjectByUserId(PrefsUtil.readUserInfo().ID,new RequestCallback<ApiEntity.UserProject>(ApiEntity.UserProject.class) {
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "getProjects()->onCancelled()->");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "getProjects()->onError()->");
                presenter.onError(ex);
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "getProjects()->onFinished()->");
            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserProject> retProjects) {
                Log.d(TAG, "getProjects()->onParseSuccess()->");
                if(retProjects == null){
                    this.onError(new NullPointerException("请求的项目解析为空"),false);
                    return;
                }
                Log.d(TAG, "getProjects()->onParseSuccess()->count: " + retProjects.size());
                projects.clear();
                for(int i = 0;i<retProjects.size();i++) {
                    ApiEntity.UserProject project = retProjects.get(i);
                    boolean isBelongProject;
                    isBelongProject = filterVisibleProject(project);
                    if (!isBelongProject) {
                        continue;
                    }
                    projects.add(retProjects.get(i));
                }
                if(isWithGroup) {
                    if(isForMemberList){
                        loadGroups(presenter,true,true);
                    }else {
                        loadGroups(presenter, true,false);
                    }
                    return;
                }
                presenter.onGetProjectSuccess(retProjects);
            }
        });
    }

    @Override
    public void loadGroups(final IProjectPresenter presenter, final boolean isWithProject,final boolean isForMemberList) {
        API.TalkerAPI.LoadGroups("",false,1,false,
                new RequestCallback<ApiEntity.ChatterGroup>(ApiEntity.ChatterGroup.class) {
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "loadGroups()->onCancelled()->");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "loadGroups()->onError()->");
                presenter.onError(ex);
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "loadGroups()->onFinished()->");
            }

            @Override
            public void onParseSuccess(List<ApiEntity.ChatterGroup> retGroup) {
                Log.d(TAG, "loadGroups()->onParseSuccess()->");
                Log.d(TAG, "loadGroups()->onParseSuccess()->count: " + retGroup.size());
                if(isWithProject) {
                    if(isForMemberList){
                        presenter.onGetProjectsForMemberListSuccess(projects,retGroup);
                    }else {
                        presenter.onGetProjectAndGroupSuccess(projects, retGroup);
                    }
                    return;
                }
                presenter.onLoadGroupsSuccess(retGroup);
            }
        });
    }

    @Override
    public void createTeam(ApiEntity.ChatterGroup group,
                           List<Integer> userids,final IProjectPresenter presenter) {
        API.TalkerAPI.SaveChatterGroup(group,userids,new RequestCallback<String>(String.class) {
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "loadGroups()->onCancelled()->");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "loadGroups()->onError()->");
                presenter.onCreateTeamError(ex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "loadGroups()->onFinished()->");
            }

//            @Override
//            public void onParseSuccess(ApiEntity.APIResult respInfo) {
//                if(respInfo != null && respInfo.State == 1) {
//                    presenter.onCreateTeamSuccess(respInfo);
//                }else if(respInfo != null) {
//                    presenter.onCreateTeamError(respInfo.Msg);
//                }else{
//                    presenter.onCreateTeamError("空返回！");
//                }
//            }

            @Override
            public void onSuccess(String retData) {
                Log.d(TAG, "loadGroups()->onParseSuccess()->");
                presenter.onCreateTeamSuccess(retData);
            }
        });
    }

    @Override
    public void createProject(ApiEntity.UserProject project,final IProjectPresenter presenter) {
        Logger.d(TAG,"DoProject: " + new Gson().toJson(project));
        API.TalkerAPI.DoProject(project,new RequestCallback<String>(String.class) {
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "loadGroups()->onCancelled()->");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "loadGroups()->onError()->");
                presenter.onCreateProjectError(ex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "loadGroups()->onFinished()->");
            }

//            @Override
//            public void onParseSuccess(ApiEntity.APIResult respInfo) {
//                Log.d(TAG, "onParseSuccess()->onParseSuccess()->");
//                if(respInfo != null && respInfo.State == 1){
//                    presenter.onCreateProjectSuccess(respInfo);
//                }else if(respInfo != null){
//                    presenter.onCreateProjectError(respInfo.Msg);
//                }else{
//                    presenter.onCreateProjectError("空返回！");
//                }
//            }

            @Override
            public void onSuccess(String retData) {
                Log.d(TAG, "loadGroups()->onParseSuccess()->");
                if("0".equals(retData)){
                    presenter.onCreateProjectError("return 0");
                }else {
                    presenter.onCreateProjectSuccess(retData);
                }
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
    public void getTaskOfProject(String ids, final IProjectPresenter listener) {
        API.TalkerAPI.GetTaskByProjectId(ids,
                new RequestCallback<ApiEntity.UserFenPai>(ApiEntity.UserFenPai.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {
                        Log.d(TAG, "getTaskOfProject()->onCancelled()->");
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        listener.onError(ex);
                        Log.d(TAG,"getTaskOfProject()->onError()->");
                    }

                    @Override
                    public void onFinished() {
                        Log.d(TAG,"getTaskOfProject()->onFinished()->");
                    }

                    @Override
                    public void onParseSuccess(List<ApiEntity.UserFenPai> tasks) {
                        listener.onGetTaskOfProjectSuccess(tasks);
                        Log.d(TAG,"getTaskOfProject()->onParseSuccess()->count: " + (tasks == null ? 0: tasks.size()));
                    }
                });
    }

    @Override
    public void getTaskByState(final IProjectPresenter presenter, int state) {
        API.TalkerAPI.GetTaskByState(state,
                new RequestCallback<ApiEntity.UserFenPai>(ApiEntity.UserFenPai.class) {
                    @Override
                    public void onCancelled(CancelledException arg0) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        presenter.onError(ex);
                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onParseSuccess(List<ApiEntity.UserFenPai> taskList) {
                        presenter.onGetTaskByStateSuccess(taskList);
                    }
                });
    }

    @Override
    public void getTaskByUserId(final IProjectPresenter presenter, int id) {
        API.TalkerAPI.GetTaskByUserId(id,new RequestCallback<ApiEntity.UserFenPai>(ApiEntity.UserFenPai.class){

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                presenter.onError(ex);
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserFenPai> taskList) {
                presenter.onGetTaskByUserIdSuccess(taskList);
            }
        });
    }

    private boolean filterVisibleProject(ApiEntity.UserProject project) {
        boolean isBelongProject = false;
        /*ArrayList<ApiEntity.UserInfo> userList = new ArrayList<>();
        userList.clear();
        userList.addAll(project.MainUserList);
        userList.addAll(project.UsersList);
        for(int j = 0;j<userList.size();j++){
            ApiEntity.UserInfo user = userList.get(j);
            if(user.ID == PrefsUtil.readUserInfo().ID) {
                isBelongProject = true;
                break;
            }
        }*/
        String userIds = project.MainUser + "," + project.Users;
        if (Arrays.asList(userIds.split(",")).contains(Integer.toString(PrefsUtil.readUserInfo().ID))) {
            isBelongProject = true;
        }
        return isBelongProject;
    }
}
