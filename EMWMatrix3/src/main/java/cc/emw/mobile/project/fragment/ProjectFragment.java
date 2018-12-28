package cc.emw.mobile.project.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.project.base.BaseViewPagerFragment;
import cc.emw.mobile.project.view.TimeTrackingMGActivity;

/**
 * 项目外层碎片
 * Created by jven.wu on 2016/6/24.
 */
public class ProjectFragment extends BaseViewPagerFragment {
    @Override
    protected int setPageLimit() {
        return 2;
    }

    @Override
    protected FragmentPagerAdapter getPageAdapter() {
        return super.getPageAdapter();
    }

    @Override
    protected String[] getPagerTitle() {
        return new String[]{"项目", "状态", "人员"};
    }

    @Override
    protected Fragment getPagerFragment(int position) {
        switch (position) {
            case 0:
                return new ProjectListFragment();
            case 1:
                return new ProjectStateListFragment();
            case 2:
                return new ProjectMemberListFragment2();
            default:
                return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                showActionDialog();
                break;
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
//                Intent noticeIntent = new Intent(getActivity(), TestActivity.class);
//                getActivity().startActivity(noticeIntent);
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
            case R.id.cm_header_btn_right1:
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
}
