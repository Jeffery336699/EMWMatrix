package cc.emw.mobile.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.login.fragment.GuideFragment;

/**
 * 引导界面
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

    private static final int NUM_PAGES = 4;

    @ViewInject(R.id.pager)
    private ViewPager mViewPager;

    private PagerAdapter pagerAdapter;
    private boolean isOpaque = true; //是否不透明
    private SparseArray<GuideFragment> fragmentMap = new SparseArray<GuideFragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(true, new CrossfadePageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset, int positionOffsetPixels) {
                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        mViewPager
                                .setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        mViewPager.setBackgroundColor(getResources().getColor(R.color.guide_viewpager_bg));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == NUM_PAGES - 2) {

                } else if (position < NUM_PAGES - 2) {
                    // skip.setVisibility(View.VISIBLE);
                    // next.setVisibility(View.VISIBLE);
                    // done.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    endTutorial();
                }

                int id = 0;
                switch (position) {
                    case 0:
                        id = R.layout.fragment_guide1;
                        break;
                    case 1:
                        id = R.layout.fragment_guide2;
                        break;
                    case 2:
                        id = R.layout.fragment_guide3;
                        break;
                    case 3:
                        id = R.layout.fragment_guide4;
                        break;
                }
                if (position != 4) {
                    fragmentMap.get(position).stopAnim(id);
                    fragmentMap.get(position).showAnim(id);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) {
            mViewPager.clearOnPageChangeListeners();
        }
    }

    private void endTutorial() {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
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
                    tp = GuideFragment.newInstance(R.layout.fragment_guide1);
                    break;
                case 1:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide2);
                    break;
                case 2:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide3);
                    break;
                case 3:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide4);
                    break;
                case 4:
                    tp = GuideFragment.newInstance(R.layout.fragment_guide5);
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

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);

            View welcomeImage01 = page.findViewById(R.id.welcome_01);
            View welcomeImage02 = page.findViewById(R.id.welcome_02);
            View welcomeImage03 = page.findViewById(R.id.welcome_03);
            View welcomeImage04 = page.findViewById(R.id.welcome_04);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {
            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView,
                            1.0f - Math.abs(position));

                }

                if (welcomeImage01 != null) {
                    ViewHelper.setTranslationX(welcomeImage01,
                            (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage01,
                            1.0f - Math.abs(position));
                }

                if (welcomeImage02 != null) {
                    ViewHelper.setTranslationX(welcomeImage02,
                            (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage02,
                            1.0f - Math.abs(position));
                }

                if (welcomeImage03 != null) {
                    ViewHelper.setTranslationX(welcomeImage03,
                            (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage03,
                            1.0f - Math.abs(position));
                }

                if (welcomeImage04 != null) {
                    ViewHelper.setTranslationX(welcomeImage04,
                            (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage04,
                            1.0f - Math.abs(position));
                }
            }
        }
    }
}