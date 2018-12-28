package cc.emw.mobile.project.fragment;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import cc.emw.mobile.project.adapter.MemberProjectAdapter;
import cc.emw.mobile.project.base.BaseExpandableListFragment;
import cc.emw.mobile.project.base.BaseListAdapter1;
import cc.emw.mobile.project.bean.MemberProject;
import cc.emw.mobile.project.presenter.ProjectPresenter;
import cc.emw.mobile.project.view.IProjectMemberView;

/**
 * 项目按成员展示碎片
 * Created by jven.wu on 2016/6/24.
 */
public class ProjectMemberListFragment extends BaseExpandableListFragment<MemberProject> implements IProjectMemberView {
    private ProjectPresenter presenter; //MVP模式中的主导器

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onFirstUserVisible();
    }

    @Override
    protected boolean isNeedFooter() {
        return false;
    }

    @Override
    protected void requestData() {
        super.requestData();
        presenter = new ProjectPresenter(this);
        presenter.getProjects(true, true);
    }

    @Override
    protected BaseListAdapter1<MemberProject> getListAdapter() {
        return new MemberProjectAdapter(getActivity());
    }

    /**
     * 网络请求数据后在页面上的回调
     * @param memberProjects
     */
    @Override
    public void renderView(ArrayList<MemberProject> memberProjects) {
        if(memberProjects != null && memberProjects.size()>=0) {
            mPageBean.setItems(memberProjects);
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


}
