package cc.emw.mobile.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.AboutActivity;
import cc.emw.mobile.me.CollectActivity;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.me.PersonInfoEditActivity;
import cc.emw.mobile.me.ReleaseActivity;
import cc.emw.mobile.me.TwoStepActivity;
import cc.emw.mobile.me.WaitHandleActivity;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.view.MyInfoView;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * @author zrjt
 * @version 2016-3-9 下午4:44:30
 */
@ContentView(R.layout.fragment_me6)
public class MyInfoFragment6 extends BaseFragment implements MyInfoView {

//    @ViewInject(R.id.iv_me_head)
//    private ImageView mHeadIv; // 头像
//    @ViewInject(R.id.tv_me_name)
//    private TextView mNameTv; // 名称
//    @ViewInject(R.id.tv_me_releasemun)
//    private TextView mReleaseNumTv; // 发布数量
//    @ViewInject(R.id.tv_me_favoritenum)
//    private TextView mFavoriteNumTv; // 收藏数量
//    @ViewInject(R.id.tv_me_fans)
//    private TextView mFenSiNum;
//    @ViewInject(R.id.tv_me_depart_name)
//    private TextView mDepartName;
//    @ViewInject(R.id.tabstrip)
//    private PagerSlidingTabStrip pStrip;
//    @ViewInject(R.id.viewpager)
//    private ViewPager mViewPager;

    public MyInfoFragment6() {
    }

    private MyInfoPresenter presenter;
    public static final String ACTION_REFRESH_ME_LIST = "cc.emw.mobile.refresh_me_list";
    private MyBroadcastReceive mReceive;

    @Override
    public void showFailureInfo(String tips) {
    }

    @Override
    public void showFollowList(List<UserInfo> simpleUsers) {
    }

    @Override
    public void finishRefresh() {
    }

    @Override
    public void getMyInfoCount(List<Integer> lists) {
        if (lists != null && lists.size() > 0) {
//            mReleaseNumTv.setText(lists.get(0) > 0 ? (lists.get(0) + "") : (0 + ""));
//            mFavoriteNumTv.setText(lists.get(1) > 0 ? (lists.get(1) + "") : (0 + ""));
//            mFenSiNum.setText(lists.get(4) > 0 ? (lists.get(4) + "") : (0 + ""));
        }
    }

    @Override
    public void getMyReleaseInfoList(List<UserNote> userNotes) {
    }

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_ME_LIST.equals(action)) {
//                init();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        init();
    }

    @Override
    public void onFirstUserVisible() {
        if (EMWApplication.personMap != null
                && EMWApplication.personMap
                .get(PrefsUtil.readUserInfo().ID) != null) {
            UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                    .readUserInfo().ID);
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    simPleUser.Image);
//            Picasso.with(getActivity())
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80))
//                    .centerCrop()
//                    .config(Bitmap.Config.RGB_565)
//                    .placeholder(R.drawable.cm_img_head)
//                    .error(R.drawable.cm_img_head)
//                    .into(mHeadIv);
        }
    }

//    private void init() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_REFRESH_ME_LIST);
//        mReceive = new MyBroadcastReceive();
//        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
//        if (PrefsUtil.readUserInfo() != null) {
//            mNameTv.setText(PrefsUtil.readUserInfo().Name);
//            mDepartName.setText(!TextUtils.isEmpty(PrefsUtil.readUserInfo().DeptName) ? PrefsUtil.readUserInfo().DeptName : "暂无部门");
//        } else {
//            mDepartName.setText("暂无部门");
//        }
//        presenter = new MyInfoPresenter(this);
//        presenter.getCount();
//        mViewPager.setOffscreenPageLimit(3);
//        mViewPager.setAdapter(new PageAdapter(getChildFragmentManager()));
////        mCoordinatorTabLayout.setupWithViewPager(mViewPager);
//        pStrip.setTabWeightOne(true);
//        pStrip.setTextSize((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//        pStrip.setTextColor(getResources().getColorStateList(R.color.tabstrip_file_textcolor));
//        mViewPager.setPageMargin(DisplayUtil.dip2px(getActivity(), 5));
//        pStrip.setViewPager(mViewPager);
//    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[]{"日程", "回复", "分享"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragment == null) {
                if (position == 0) {
                    fragment = new MyCalendarsFragment();
                } else if (position == 1) {
                    fragment = new MyReplysFragment();
                } else if (position == 2) {
                    fragment = new MySharesFragment();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }

    @Event(value = {R.id.ll_me_info, R.id.iv_me_head, R.id.ll_me_release, R.id.ll_me_favorite, R.id.ll_me_fans, R.id.item_person_setting,
            R.id.itemProgress_daiban, R.id.itemProgress_about, R.id.itemProgress_twostep})
    private void onItemClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_me_head:
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID) != null) {
                    Intent intentPersonInfo = new Intent(getActivity(), PersonInfoActivity.class);
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intentPersonInfo.putExtra("UserInfo", simPleUser);
                    intentPersonInfo.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intentPersonInfo.putExtra("click_pos_y", location[1]);
                    startActivity(intentPersonInfo);
                }
                break;
            case R.id.ll_me_release: // 发布
                intent = new Intent(getActivity(), ReleaseActivity.class);
                break;
            case R.id.ll_me_favorite: // 收藏
                intent = new Intent(getActivity(), CollectActivity.class);
                break;
            case R.id.ll_me_fans:   //粉丝
                intent = new Intent(getActivity(), ConcernActivity.class);
                break;
            case R.id.item_person_setting:  //个人设置
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap
                        .get(PrefsUtil.readUserInfo().ID) != null) {
                    Intent intentPersonInfoEdit = new Intent(getActivity(), PersonInfoEditActivity.class);
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intentPersonInfoEdit.putExtra("UserInfo", simPleUser);
                    intentPersonInfoEdit.putExtra("start_anim", false);
                    startActivity(intentPersonInfoEdit);
                }
                break;
            case R.id.itemProgress_daiban: // 代办工作
                intent = new Intent(getActivity(), WaitHandleActivity.class);
                break;
            case R.id.itemProgress_about: // 关于我
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.itemProgress_twostep: // 两步验证
                intent = new Intent(getActivity(), TwoStepActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

    }
}
