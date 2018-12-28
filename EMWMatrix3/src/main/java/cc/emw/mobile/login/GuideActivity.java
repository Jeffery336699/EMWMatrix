package cc.emw.mobile.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;

import com.githang.androidcrash.util.AppManager;
import com.rd.PageIndicatorView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.fragment.GuideFragment;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 引导界面
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

    private static final int NUM_PAGES = 5;
    private int current = 0;

    @ViewInject(R.id.viewpager_guide)
    private ViewPager mViewPager;
    @ViewInject(R.id.indicator_view)
    private PageIndicatorView pageIndicatorView;

    private PagerAdapter pagerAdapter;
    private boolean isOpaque = true; //是否不透明
    private SparseArray<GuideFragment> fragmentMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EMWApplication.getDaoInstant().getChatMsgBeanDao().deleteAll();
        setSwipeBackEnable(false);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                current = position;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (current == 4) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (current == 4 && AppManager.currentActivity() == GuideActivity.this) {
                                Intent intent = new Intent(GuideActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, 1500);
                }
            }
        });
        pageIndicatorView.setViewPager(mViewPager);
    }

    @Event(value = {R.id.tv_log_in, R.id.tv_sign_up})
    private void onLogClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_log_in:
                if (PrefsUtil.readUserInfo() != null && PrefsUtil.readLoginCookie() != null && !PrefsUtil.isSwitch()) {
                    intent = new Intent(this, WelcomeActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity2.class);
                }
                break;
            case R.id.tv_sign_up:
                intent = new Intent(this, RegisterActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GuideFragment tp = null;
            switch (position) {
                case 0:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide);
                    break;
                case 1:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide1);
                    break;
                case 2:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide2);
                    break;
                case 3:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide3);
                    break;
                case 4:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide4);
                    break;
            }
            fragmentMap.put(position, tp);
            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}