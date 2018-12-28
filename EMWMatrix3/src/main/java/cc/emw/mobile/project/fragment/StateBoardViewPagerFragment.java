package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.project.base.BaseViewPagerFragment;
import cc.emw.mobile.project.view.ObserveProjectActivity;
import cc.emw.mobile.project.view.TaskSpectacularActivity;
import cc.emw.mobile.project.view.TimeTrackingMGActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by jven.wu on 2016/11/18.
 */
public class StateBoardViewPagerFragment extends BaseViewPagerFragment {
    private String currentState = TaskConstant.TaskStateString.UNSTART; //当前状态，用于指引viewPager设置当前状态的Fragment

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        currentState = TaskSpectacularActivity.currentState;
        super.onViewCreated(view, savedInstanceState);
        mHeaderMemberProjectBtn.setVisibility(View.GONE);
    }

    @Override
    protected FragmentPagerAdapter getPageAdapter() {
        return super.getPageAdapter();
    }

    @Override
    protected String[] getPagerTitle() {
        return new String[]{TaskConstant.TaskStateString.UNSTART,
                TaskConstant.TaskStateString.PROCESSING,
                TaskConstant.TaskStateString.FINISHED,
                TaskConstant.TaskStateString.DELAY};
    }

    @Override
    protected Fragment getPagerFragment(int position) {
        String type = TaskSpectacularActivity.currentType;
        int state = TaskConstant.TaskState.UNSTART;
        switch (position){
            case 0:
                state = TaskConstant.TaskState.UNSTART;
                break;
            case 1:
                state = TaskConstant.TaskState.PROCESSING;
                break;
            case 2:
                state = TaskConstant.TaskState.FINISHED;
                break;
            case 3:
                state = TaskConstant.TaskState.DELAY;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        if(type.equals(TaskSpectacularActivity.GROUP_TASK)) {
            StateBoardGroupFragment sbgf = new StateBoardGroupFragment();
            sbgf.setArguments(bundle);
            return sbgf;
        }else{
            StateBoardProjectFragment sbpf = new StateBoardProjectFragment();
            sbpf.setArguments(bundle);
            return sbpf;
        }
    }

    @Override
    protected int setPageLimit() {
        return 3;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                showActionDialog();
                break;
            case R.id.cm_header_btn_left:
                getActivity().onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent timeTrackingIntent = new Intent(getActivity(), TimeTrackingMGActivity.class);
                getActivity().startActivity(timeTrackingIntent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void setLeftBtn(IconTextView btn) {
        super.setLeftBtn(btn);
        btn.setIconText("eb68");
    }

    @Override
    protected void setHeaderTitle(TextView titleTv) {
        super.setHeaderTitle(titleTv);
        titleTv.setText("任务看板");
    }
}
