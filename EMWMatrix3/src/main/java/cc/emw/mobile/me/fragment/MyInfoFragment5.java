package cc.emw.mobile.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.AboutActivity;
import cc.emw.mobile.me.CollectActivity;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.me.PersonInfoEditActivity;
import cc.emw.mobile.me.ReleaseActivity;
import cc.emw.mobile.me.TwoStepActivity;
import cc.emw.mobile.me.WaitHandleActivity;
import cc.emw.mobile.me.easybehavior.behavior.AppBarLayoutOverScrollViewBehavior;
import cc.emw.mobile.me.easybehavior.fragment.MyFragmentPagerAdapter;
import cc.emw.mobile.me.easybehavior.fragment.dummy.TabEntity;
import cc.emw.mobile.me.easybehavior.widget.CircleImageView;
import cc.emw.mobile.me.easybehavior.widget.RoundProgressBar;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.tablayout.CommonTabLayout;
import cc.emw.mobile.me.tablayout.listener.CustomTabEntity;
import cc.emw.mobile.me.tablayout.listener.OnTabSelectListener;
import cc.emw.mobile.me.view.MyInfoView;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.IconTextView;
import q.rorbin.badgeview.Badge;

@ContentView(R.layout.fragment_my_info_fragment5)
public class MyInfoFragment5 extends BaseFragment implements MyInfoView {

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mTvBack;
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mTvNotic;
    @ViewInject(R.id.civ_me_title_user_img)
    private CircleImageView mCivTitle;
    @ViewInject(R.id.tv_me_title_name)
    private TextView mTitleName;
    @ViewInject(R.id.tv_me_name)
    private TextView mNameTv; // 名称
    @ViewInject(R.id.tv_me_releasemun)
    private TextView mReleaseNumTv; // 发布数量
    @ViewInject(R.id.tv_me_favoritenum)
    private TextView mFavoriteNumTv; // 收藏数量
    @ViewInject(R.id.tv_me_fans)
    private TextView mFenSiNum;
    @ViewInject(R.id.tv_me_depart_name)
    private TextView mDepartName;

    private ImageView mZoomIv;
    private Toolbar mToolBar;
    private ViewGroup titleContainer;
    private AppBarLayout mAppBarLayout;
    private ViewGroup titleCenterLayout;
    private RoundProgressBar progressBar;
    //    private ImageView mMsgIv;
    private CircleImageView mAvater;
    private CommonTabLayout mTablayout;
    private ViewPager mViewPager;

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private List<Fragment> fragments;
    private int lastState = 1;
    private Badge mBadgeView;
    private DisplayImageOptions option;

    public MyInfoFragment5() {
    }

    private MyInfoPresenter presenter;
    public static final String ACTION_REFRESH_ME_LIST = "cc.emw.mobile.refresh_me_list";
    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver { // TODO

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_ME_LIST.equals(action)) {
                initHeadImg();
                init();
            } else if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                mBadgeView.setBadgeNumber(count);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_ME_LIST);
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        findId(view);
        init();
        initListener();
        initTab();
        initStatus();
    }

    @Override
    public void onFirstUserVisible() {
        initHeadImg();
    }

    private void initHeadImg() {
        if (EMWApplication.personMap != null
                && EMWApplication.personMap
                .get(PrefsUtil.readUserInfo().ID) != null) {
            UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                    .readUserInfo().ID);
            mAvater.setTvBg(EMWApplication.getIconColor(simPleUser.ID), simPleUser.Name, 80);
            mCivTitle.setTvBg(EMWApplication.getIconColor(simPleUser.ID), simPleUser.Name, 80);
            option = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head)
//                .showImageForEmptyUri(R.drawable.cm_img_head)
//                .showImageOnFail(R.drawable.cm_img_head)
                    .cacheInMemory(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheOnDisk(true).build();

            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    simPleUser.Image);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mAvater), option,
                    new ImageSize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80)), null, null);
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mCivTitle), option,
                    new ImageSize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80)), null, null);
//            Picasso.with(getActivity())
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80))
//                    .centerCrop()
//                    .config(Bitmap.Config.RGB_565)
//                    .placeholder(R.drawable.cm_img_head)
//                    .error(R.drawable.cm_img_head)
//                    .into(mAvater);
//            Picasso.with(getActivity())
//                    .load(uri)
//                    .resize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80))
//                    .centerCrop()
//                    .config(Bitmap.Config.RGB_565)
//                    .placeholder(R.drawable.cm_img_head)
//                    .error(R.drawable.cm_img_head)
//                    .into(mCivTitle);
        }
    }

    private void init() {
        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mTvNotic);
        if (PrefsUtil.readUserInfo() != null) {
            mNameTv.setText(PrefsUtil.readUserInfo().Name);
            mTitleName.setText(PrefsUtil.readUserInfo().Name);
            mDepartName.setText(!TextUtils.isEmpty(PrefsUtil.readUserInfo().DeptName) ? PrefsUtil.readUserInfo().DeptName : "暂无部门");
        } else {
            mDepartName.setText("暂无部门");
        }
        presenter = new MyInfoPresenter(this);
        presenter.getCount();
    }

    /**
     * 初始化id
     */
    private void findId(View view) {
        mZoomIv = (ImageView) view.findViewById(R.id.uc_zoomiv);
        mToolBar = (Toolbar) view.findViewById(R.id.toolbar);
        titleContainer = (ViewGroup) view.findViewById(R.id.title_layout);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        titleCenterLayout = (ViewGroup) view.findViewById(R.id.title_center_layout);
        progressBar = (RoundProgressBar) view.findViewById(R.id.uc_progressbar);
        mAvater = (CircleImageView) view.findViewById(R.id.uc_avater);
        mTablayout = (CommonTabLayout) view.findViewById(R.id.uc_tablayout);
        mViewPager = (ViewPager) view.findViewById(R.id.uc_viewpager);
        mAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        fragments = getFragments();
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments, getNames());

        mTablayout.setTabData(mTabEntities);
        mViewPager.setAdapter(myFragmentPagerAdapter);
    }

    /**
     * 绑定事件
     */
    private void initListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
                if (titleCenterLayout != null && mAvater != null) {
                    titleCenterLayout.setAlpha(percent);
//                    StatusBarUtil.setTranslucentForImageView(MainActivity.this, (int) (255f * percent), null);
                    if (percent == 0) {
                        groupChange(1f, 1);
                    } else if (percent == 1) {
                        if (mAvater.getVisibility() != View.GONE) {
                            mAvater.setVisibility(View.GONE);
                        }
                        groupChange(1f, 2);
                    } else {
                        if (mAvater.getVisibility() != View.VISIBLE) {
                            mAvater.setVisibility(View.VISIBLE);
                        }
                        groupChange(percent, 0);
                    }

                }
            }
        });
        AppBarLayoutOverScrollViewBehavior myAppBarLayoutBehavoir = (AppBarLayoutOverScrollViewBehavior)
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        myAppBarLayoutBehavoir.setOnProgressChangeListener(new AppBarLayoutOverScrollViewBehavior.onProgressChangeListener() {
            @Override
            public void onProgressChange(float progress, boolean isRelease) {
                progressBar.setProgress((int) (progress * 360));
                if (!isRelease) {
                    mTvNotic.setVisibility(View.GONE);
                } else if (progress == 0 && !progressBar.isSpinning) {
                    mTvNotic.setVisibility(View.VISIBLE);
                }
                if (progress == 1 && !progressBar.isSpinning && isRelease) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().sendBroadcast(new Intent(MyCalendarsFragment.ACTION_REFRESH_CALENDAR_LIST));
//                            getActivity().sendBroadcast(new Intent(MyReplysFragment.ACTION_REFRESH_REPLY_LIST));
//                            getActivity().sendBroadcast(new Intent(MySharesFragment.ACTION_REFRESH_SHARE_LIST));
//                        }
//                    }, 200);
                }
            }
        });
        mTablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTablayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化状态栏位置
     */
    private void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
//            StatusBarUtil.setTransparentForImageView(MainActivity.this, null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(getActivity());
            CollapsingToolbarLayout.LayoutParams lp1 = (CollapsingToolbarLayout.LayoutParams) titleContainer.getLayoutParams();
//            lp1.topMargin = statusBarHeight;
            titleContainer.setLayoutParams(lp1);
            CollapsingToolbarLayout.LayoutParams lp2 = (CollapsingToolbarLayout.LayoutParams) mToolBar.getLayoutParams();
//            lp2.topMargin = statusBarHeight;
            mToolBar.setLayoutParams(lp2);
        }
    }

    /**
     * @param alpha
     * @param state 0-正在变化 1展开 2 关闭
     */
    public void groupChange(float alpha, int state) {
        lastState = state;
        mTvBack.setAlpha(alpha);
        mTvNotic.setAlpha(alpha);
        switch (state) {
            case 1://完全展开 显示白色
                mTvBack.setTextColor(getResources().getColor(R.color.white));
                mTvNotic.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2://完全关闭 显示黑色
                mTvBack.setTextColor(Color.parseColor("#FF4C0B"));
                mTvNotic.setTextColor(Color.parseColor("#FF4C0B"));
                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
                    mTvBack.setTextColor(Color.parseColor("#FF4C0B"));
                    mTvNotic.setTextColor(Color.parseColor("#FF4C0B"));
                }
                break;
        }
    }

    /**
     * 获取状态栏高度
     * ！！这个方法来自StatusBarUtil,因为作者将之设为private，所以直接copy出来
     * * @param context context
     *
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 假数据
     *
     * @return
     */
    public String[] getNames() {
        String[] mNames = new String[]{"日程", "回复", "分享"};
        for (String str : mNames) {
//            mTabEntities.add(new TabEntity(String.valueOf(new Random().nextInt(200)), str));
            mTabEntities.add(new TabEntity("", str));
        }
        return mNames;
    }

    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MyCalendarsFragment());
        fragments.add(new MyReplysFragment());
        fragments.add(new MySharesFragment());
        return fragments;
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
            intent.putExtra("start_anim", false);
            int[] location = new int[2];
            v.getLocationInWindow(location);
            intent.putExtra("click_pos_y", location[1]);
            startActivity(intent);
        }

    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
//                getActivity().onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
        }
    }

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
            mReleaseNumTv.setText(lists.get(0) > 0 ? (lists.get(0) + "") : (0 + ""));
            mFavoriteNumTv.setText(lists.get(1) > 0 ? (lists.get(1) + "") : (0 + ""));
            mFenSiNum.setText(lists.get(4) > 0 ? (lists.get(4) + "") : (0 + ""));
        }
    }

    @Override
    public void getMyReleaseInfoList(List<UserNote> userNotes) {
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }
}
