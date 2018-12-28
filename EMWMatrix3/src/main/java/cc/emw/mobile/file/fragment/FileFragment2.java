package cc.emw.mobile.file.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.inter.FileOtherCodeCallBack;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.PagerSlidingTabStrip;
import q.rorbin.badgeview.Badge;

/**
 * 知识库Fragment
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.fragment_file2)
public class FileFragment2 extends BaseFragment implements FileOtherCodeCallBack{

    @ViewInject(R.id.cm_header_civ_head)
    private CircleImageView mHeaderIconIv;
    @ViewInject(R.id.cm_header_btn_notice)
    private ImageButton mHeaderNoticeIb;

    @ViewInject(R.id.tabstrip_file) private PagerSlidingTabStrip pStrip; //
    @ViewInject(R.id.viewpager_file) private ViewPager viewPager; //
    private PageAdapter mPageAdapter;
    private MyBroadcastReceive mReceive;
    private Badge mBadgeView;
    private DisplayImageOptions options;
    private MyReceiver receiver;

    public static FileFragment2 newInstance(String title) {
        FileFragment2 fragment = new FileFragment2();
        Bundle args = new Bundle();
        args.putString("header_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        mHeaderIconIv.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeaderIconIv), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);

        pStrip.setTabWeightOne(true);
        pStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        pStrip.setTextColor(getResources().getColorStateList(R.color.tabstrip_file_textcolor));
        mPageAdapter = new PageAdapter(getChildFragmentManager());
        viewPager.setAdapter(mPageAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageMargin(DisplayUtil.dip2px(getActivity(), 5));
        pStrip.setViewPager(viewPager);

        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mHeaderNoticeIb);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        receiver = new MyReceiver();//广播接受者实例
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("refresh.head");
        getActivity().registerReceiver(receiver, intentFilter2);
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.cm_header_civ_head, R.id.cm_header_btn_notice})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
            case R.id.cm_header_civ_head:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
            case R.id.cm_header_btn_notice:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
        }
    }

    @Override
    public int getFileCurrenNu() {
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return -1;
    }

    class PageAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[] { "所有", "文档", "图片", "视频", "其他"};

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return FileFragment.newInstance("");
            } else {
                return FileOtherFragment.newInstance(position,FileFragment2.this);
            }
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

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                mBadgeView.setBadgeNumber(count);
            } else if (DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
                mHeaderIconIv.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
                ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeaderIconIv), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);
            }
        }
    }
    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }

    }
    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            params.width = intent.getIntExtra("positionOffset", 0);
            params.height = intent.getIntExtra("positionOffset", 0);
            mHeaderIconIv.setLayoutParams(params);
        }

    }
}
