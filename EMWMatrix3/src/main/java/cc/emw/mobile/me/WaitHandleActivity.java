package cc.emw.mobile.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.me.fragment.WaitHandleCalendarFragment;
import cc.emw.mobile.me.fragment.WaitHandlePlanFragment;
import cc.emw.mobile.me.fragment.WaitHandleTaskFragment;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 我·待办工作
 *
 * @author zrjt
 */
@ContentView(R.layout.activity_wait_handle)
public class WaitHandleActivity extends BaseActivity implements
        OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    @ViewInject(R.id.vp_me_wait_handle)
    private ViewPager mViewPager;
    @ViewInject(R.id.cm_header_rg_switch)
    private RadioGroup radioGroup;
    @ViewInject(R.id.cm_header_rb_left)
    private RadioButton rgLeft;
    @ViewInject(R.id.cm_header_rb_middle)
    private RadioButton rgMiddle;
    @ViewInject(R.id.cm_header_rb_right)
    private RadioButton rgRight;
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView notice;
    @ViewInject(R.id.et_search_keyword)
    private EditText mSearchEt;

    //状态栏沉浸模式使用
    /*statusbar view*/
    private ViewGroup view;
    /*沉浸颜色*/
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        //沉浸模式功能代码
//        initStatusbar(this, R.color.cm_header_bg);
        initView();
    }

    @Event({R.id.tv_cm_header_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cm_header_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                this.startActivity(noticeIntent);
                break;
        }
    }

    private void initView() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cm_header_rb_left:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.cm_header_rb_middle:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.cm_header_rb_right:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    class PageAdapter extends FragmentPagerAdapter {

        private String[] titles = {"日程", "任务", "计划"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new WaitHandleCalendarFragment();
            } else if (position == 1) {
                return new WaitHandleTaskFragment();
            } else {
                return new WaitHandlePlanFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 0:
                rgLeft.setChecked(true);
                break;
            case 1:
                rgMiddle.setChecked(true);
                break;
            case 2:
                rgRight.setChecked(true);
                break;
        }
    }

//    /**
//     * 沉浸模式状态栏初始化
//     *
//     * @param context      上下文
//     * @param statusbar_bg 沉浸颜色
//     * @return
//     */
//    @SuppressLint("NewApi")
//    public void initStatusbar(Context context, int statusbar_bg) {
//        //4.4版本及以上可用
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 状态栏沉浸效果
//            Window window = ((Activity) context).getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            //decorview实际上就是activity的外层容器，是一层framlayout
//            view = (ViewGroup) ((Activity) context).getWindow().getDecorView();
//            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
//            //textview是实际添加的状态栏view，颜色可以设置成纯色，也可以加上shape，添加gradient属性设置渐变色
//            textView = new TextView(this);
//            textView.setBackgroundResource(statusbar_bg);
//            textView.setLayoutParams(lParams);
//            view.addView(textView);
//        }
//    }
//
//    /**
//     * 获取状态栏高度
//     *
//     * @return
//     */
//    public int getStatusBarHeight() {
//        Class<?> c = null;
//        Object obj = null;
//        Field field = null;
//        int x = 0, statusBarHeight = 0;
//        try {
//            c = Class.forName("com.android.internal.R$dimen");
//            obj = c.newInstance();
//            field = c.getField("status_bar_height");
//            x = Integer.parseInt(field.get(obj).toString());
//            statusBarHeight = getResources().getDimensionPixelSize(x);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return statusBarHeight;
//    }
//
//    /**
//     * 如果项目中用到了slidingmenu,根据slidingmenu滑动百分比设置statusbar颜色：渐变色效果
//     *
//     * @param alpha
//     */
//    @SuppressLint("NewApi")
//    public void changeStatusBarColor(float alpha) {
//        //textview是slidingmenu关闭时显示的颜色
//        //textview2是slidingmenu打开时显示的颜色
//        textView.setAlpha(1 - alpha);
//
//    }
}
