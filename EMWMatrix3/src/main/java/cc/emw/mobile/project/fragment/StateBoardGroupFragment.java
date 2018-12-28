package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.StateBoardGroupAdapter;
import cc.emw.mobile.project.base.BaseExpandableListFragment;
import cc.emw.mobile.project.base.BaseListAdapter1;
import cc.emw.mobile.project.bean.GroupTask;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.task.constant.TaskConstant;

/**
 * Created by jven.wu on 2016/11/14.
 */
public class StateBoardGroupFragment extends BaseExpandableListFragment<GroupTask> {
    private static final String top_template = "%s • %s"; //顶部文本格式化的占位字符串
    private ProjectPresenter presenter; //MVP模式中的主导器
    private String type = TaskSpectacularActivity.GROUP_TASK;  //type有两种：groupTask | projectTask
    private int state = TaskConstant.TaskState.UNSTART; //state有四种状态

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = TaskSpectacularActivity.currentType;
        state = getArguments().getInt("state", TaskConstant.TaskState.UNSTART);
    }

    @Override
    protected boolean isNeedFooter() {
        return false;
    }

    @Override
    protected void requestData() {
        super.requestData();
        API.TalkerAPI.LoadGroupsTask("", false, 1, state, new RequestCallback<GroupTask>() {
            @Override
            public void onFinished() {

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                onRequestFinish();
            }

            @Override
            public void onParseSuccess(List<GroupTask> respList) {
                ArrayList<GroupTask> gp = new ArrayList<GroupTask>();
                if (respList != null && respList.size() > 0) {
                    for (GroupTask g : respList) {
                        gp.add(g);
                    }
                }
                renderView(gp);
            }
        });
    }

    @Override
    protected BaseListAdapter1<GroupTask> getListAdapter() {
        return new StateBoardGroupAdapter(getActivity());
    }

    @Override
    protected int getTopContentLayoutId() {
        return R.layout.list_top_content2;
    }

    @Override
    protected void setTopContentLayout() {
        TextView tv = (TextView) mTopLayout.findViewById(R.id.task_state_num);
        int taskCount = 0;
        for (GroupTask groupTask : mPageBean.getItems()) {
            if (groupTask.Tasks != null)
                taskCount += groupTask.Tasks.size();
        }
        tv.setText(String.format(top_template, CommonUtil.getTaskState(state), taskCount));
        tv.setVisibility(View.VISIBLE);
    }

    /**
     * 将请求的数据显示到UI上
     *
     * @param stateTasks
     */
    public void renderView(ArrayList<GroupTask> stateTasks) {
        if (stateTasks != null && stateTasks.size() >= 0) {
            mPageBean.setItems(stateTasks);
            setListData(mPageBean);
            setTopContentLayout();
            for (int i = 0; i < mListView.getExpandableListAdapter().getGroupCount(); i++) {
                mListView.expandGroup(i);
            }
        } else {
            setFooterType(TYPE_NO_MORE);
            //mRefreshLayout.setNoMoreData();
        }
        onRequestFinish();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent taskDetailIntent = new Intent(getActivity(), TaskDetailActivity.class);
        taskDetailIntent.putExtra(TaskDetailActivity.TASK_DETAIL, mPageBean.getItems().get(groupPosition).Tasks.get(childPosition));
        taskDetailIntent.putExtra("start_anim", false);
        startActivity(taskDetailIntent);
        return false;
    }
}
