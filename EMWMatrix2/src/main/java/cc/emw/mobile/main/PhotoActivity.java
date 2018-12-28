package cc.emw.mobile.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.main.fragment.PhotoFragment;
import cc.emw.mobile.view.ViewPagerChildView;

/**
 * 图片浏览器
 * 
 * @author zhuangshaobo
 * @Create at 2014-8-4 上午11:47:58
 * @Version 1.0
 */
public class PhotoActivity extends BaseActivity {

	/** 显示第几个图片，从0开始 */
	public static final String INTENT_EXTRA_POSITION = "extra_position";
	/** 多个url以英文逗号','分隔 */
	public static final String INTENT_EXTRA_IMGURLS = "extra_urls";
	/** 是否格式化url */
	public static final String INTENT_EXTRA_ISFORMAT = "extra_isformat";

	private ViewPagerChildView mViewPager;
	private TextView mPositionTv;

	private MyPagerAdapter pagerAdapter;
	private int postion;
	private String urlStr;
	private String[] imgUrls;
	private boolean isFormat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		setSwipeBackEnable(false);
		postion = getIntent().getIntExtra(INTENT_EXTRA_POSITION, 0);
		urlStr = getIntent().getStringExtra(INTENT_EXTRA_IMGURLS);
		isFormat = getIntent().getBooleanExtra(INTENT_EXTRA_ISFORMAT, true);
		if (urlStr != null) {
			imgUrls = urlStr.split(",");
		}
		mViewPager = (ViewPagerChildView) findViewById(R.id.pager);
		mPositionTv = (TextView) findViewById(R.id.photo_tv_position);

		if (imgUrls != null && imgUrls.length > 0) {
			mPositionTv.setText(mViewPager.getCurrentItem() + 1 + "/"
					+ imgUrls.length);
		} else {
			mPositionTv.setText(View.INVISIBLE);
		}

		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mPositionTv.setText(mViewPager.getCurrentItem() + 1 + "/"
						+ pagerAdapter.getCount());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		mViewPager.setCurrentItem(postion, false);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return imgUrls != null ? imgUrls.length : 0;
		}

		@Override
		public Fragment getItem(int position) {
			return PhotoFragment.newInstance(position, imgUrls[position],
					isFormat);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}

	}

}
