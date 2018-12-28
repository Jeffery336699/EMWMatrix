package cc.emw.mobile.project.fragment;

import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.project.adapter.StateProjectAdapter;
import cc.emw.mobile.project.base.BaseExpandableListFragment;
import cc.emw.mobile.project.base.BaseListAdapter1;
import cc.emw.mobile.project.bean.StateTask;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectStateView;

/**
 * 项目主列表碎片
 * Created by jven.wu on 2016/6/22.
 */
public class ProjectStateListFragment extends BaseExpandableListFragment<StateTask> implements IProjectStateView {
    public static final String TAG = "ProjectListFragment";
    private ProjectPresenter presenter; //MVP模式中的主导器

    public ProjectStateListFragment() {
        presenter = new ProjectPresenter(this);
    }

    @Override
    protected void requestData() {
        super.requestData();
        presenter = new ProjectPresenter(this);
        presenter.getTaskByState(0);
    }

    @Override
    protected BaseListAdapter1<StateTask> getListAdapter() {
        return new StateProjectAdapter(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 网络请求数据后在页面上的回调
     * @param groupProjects
     */
    @Override
    public void renderProjectStateView(ArrayList<StateTask> groupProjects) {
        if(groupProjects != null && groupProjects.size()>=0) {
            mPageBean.setItems(groupProjects);
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
        tv.setText(String.format(getString(R.string.task_state_unstart_num),20));
    }
}
