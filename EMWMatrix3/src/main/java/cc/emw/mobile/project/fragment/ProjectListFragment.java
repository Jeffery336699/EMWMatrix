package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.project.adapter.HomeProjectAdapter;
import cc.emw.mobile.project.base.BaseExpandableListFragment;
import cc.emw.mobile.project.base.BaseListAdapter1;
import cc.emw.mobile.project.bean.GroupProject;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectListView;
import cc.emw.mobile.project.view.IProjectStateView;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.Logger;

/**
 * 项目主列表碎片
 * Created by jven.wu on 2016/6/22.
 */
public class ProjectListFragment extends BaseExpandableListFragment<GroupProject> implements IProjectListView,IProjectStateView {
    public static final String TAG = "ProjectListFragment";
    private ProjectPresenter presenter; //MVP模式中的主导器
    private ArrayList<StateTask> mStateTasks;
    private Handler mHandler;

    public ProjectListFragment() {
        presenter = new ProjectPresenter(this);
        mHandler = new Handler();
    }

    @Override
    protected void requestData() {
        super.requestData();
        presenter = new ProjectPresenter(this);
        presenter.getProjects(true, false);
        presenter.getTaskByState(0);
    }

    @Override
    protected BaseListAdapter1<GroupProject> getListAdapter() {
        return new HomeProjectAdapter(getActivity());
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 网络请求数据后在页面上的回调
     *
     * @param groupProjects
     */
    @Override
    public void renderProjectListView(ArrayList<GroupProject> groupProjects) {
        if (groupProjects != null && groupProjects.size() >= 0) {
            mPageBean.setItems(groupProjects);
            setListData(mPageBean);
            setTopContentLayout();
        } else {
            setFooterType(TYPE_NO_MORE);
            //mRefreshLayout.setNoMoreData();
        }
        onRequestFinish();
    }

    /**
     * 网络请求数据后在页面上的回调
     * @param stateTasks
     */
    @Override
    public void renderProjectStateView(ArrayList<StateTask> stateTasks) {
        if(stateTasks != null && stateTasks.size() != 0) {
            mStateTasks = new ArrayList<>();
            mStateTasks.addAll(stateTasks);
            setTaskNum(R.id.unstart_num, R.id.processing_num,
                    R.id.delay_num, R.id.finished_num);
        }
    }

    private void setTaskNum(int... viewIds) {
        for(int i = 0;i<viewIds.length;i++) {
            final TextView numTv = (TextView) mTopLayout.findViewById(viewIds[i]);
            final int finalI = i;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    numTv.setText(String.valueOf(mStateTasks.get(finalI).TaskViewBean.size()));
                }
            });
        }
    }

    @Override
    public void onError(Throwable ex) {
        onRequestFinish();
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.d(TAG,"onItemClick");
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        Logger.d(TAG,"onGroupClick");
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Logger.d(TAG,"onChildClick");

        TaskSpectacularActivity.currentType = TaskSpectacularActivity.PROJECT_TASK;
        TaskSpectacularActivity.currentProjectId =
                mPageBean.getItems().get(groupPosition).projectViews.get(childPosition).ProjectId + "";
        Intent spectacularIntent = new Intent(getActivity(),TaskSpectacularActivity.class);
        startActivity(spectacularIntent);
        return false;
    }

    @Override
    protected int getTopContentLayoutId() {
        return R.layout.list_top_content1;
    }

    @Override
    protected void setTopContentLayout() {
        setClickListenerToTopView(
                R.id.unstart_text, R.id.processing_text,
                R.id.finished_text, R.id.delay_text);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String fragmentFlag = TaskConstant.TaskStateString.UNSTART;
            switch (v.getId()) {
                case R.id.unstart_text:
                    fragmentFlag = TaskConstant.TaskStateString.UNSTART;
                    break;
                case R.id.processing_text:
                    fragmentFlag = TaskConstant.TaskStateString.PROCESSING;
                    break;
                case R.id.finished_text:
                    fragmentFlag = TaskConstant.TaskStateString.FINISHED;
                    break;
                case R.id.delay_text:
                    fragmentFlag = TaskConstant.TaskStateString.DELAY;
                    break;
            }
            Intent spectacularIntent = new Intent(getActivity(), TaskSpectacularActivity.class);
            TaskSpectacularActivity.currentType = TaskSpectacularActivity.GROUP_TASK;
            TaskSpectacularActivity.currentState = fragmentFlag;
            startActivity(spectacularIntent);
        }
    };

    /**
     * 为topView中的控件设置clickListener
     *
     * @param viewIds
     */
    private void setClickListenerToTopView(int... viewIds) {
        for (int id : viewIds) {
            mTopLayout.findViewById(id).setOnClickListener(onClickListener);
        }
    }
}
