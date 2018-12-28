package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.adapter.StateBoardProjectAdapter;
import cc.emw.mobile.project.base.BaseListAdapter;
import cc.emw.mobile.project.base.BaseListFragment;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IObserveProjectActivity;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.task.constant.TaskConstant;

/**
 * Created by jven.wu on 2016/11/21.
 */
public class StateBoardProjectFragment extends BaseListFragment<ApiEntity.UserFenPai> implements IObserveProjectActivity {
    private static final String top_template = "%s • %s";
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
        presenter = new ProjectPresenter(this);
        if(!"".equals(TaskSpectacularActivity.currentProjectId)) {
            presenter.getTasksOfProject(TaskSpectacularActivity.currentProjectId + "");
        }else{
            presenter.getTasksOfProject(String.valueOf(0));
        }
    }

    @Override
    protected BaseListAdapter<ApiEntity.UserFenPai> getListAdapter() {
        return new StateBoardProjectAdapter(getActivity());
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    public void renderView(ArrayList<ApiEntity.UserFenPai> stateTasks) {
        if(stateTasks != null && stateTasks.size()>=0) {
            ArrayList<ApiEntity.UserFenPai> temp = new ArrayList<>();
            for(ApiEntity.UserFenPai fenPai : stateTasks){
                if(state == fenPai.State){
                    temp.add(fenPai);
                }
            }
            mPageBean.setItems(temp);
            setListData(mPageBean);
            setTopContentLayout();
        } else {
            setFooterType(TYPE_NO_MORE);
            //mRefreshLayout.setNoMoreData();
        }
        onRequestFinish();
    }

    @Override
    public void onError(Throwable ex) {
        onRequestFinish();
    }

    @Override
    protected int getTopContentLayoutId() {
        return R.layout.list_top_content2;
    }

    @Override
    protected void setTopContentLayout() {
        TextView tv = (TextView)mTopLayout.findViewById(R.id.task_state_num);
        tv.setText(String.format(top_template, CommonUtil.getTaskState(state),mPageBean.getItems().size()));
        tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent taskDetailIntent = new Intent(getActivity(), TaskDetailActivity.class);
        taskDetailIntent.putExtra(TaskDetailActivity.TASK_DETAIL, mPageBean.getItems().get(position));
        taskDetailIntent.putExtra("start_anim", false);
        startActivity(taskDetailIntent);
    }
}
