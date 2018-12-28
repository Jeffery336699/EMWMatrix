package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ShowPhotoActivity;
import cc.emw.mobile.dynamic.fragment.DynamicDiscussFragment;
import cc.emw.mobile.dynamic.fragment.DynamicFavourFragment;
import cc.emw.mobile.dynamic.fragment.DynamicShareFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.imagepicker.ImagePicker;
import cc.emw.mobile.me.imagepicker.bean.ImageItem;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.ExViewPager;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.PagerSlidingTabStrip;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 动态评论
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_dynamic_discuss3)
public class DynamicDiscussActivity extends BaseActivity {

	public static final int CHOSE_FILE_CODE = 10001; //选择文件requestCode
	public static final int CHOSE_IMG_CODE = 10002; //选择图片requestCode

	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题

	@ViewInject(R.id.tabstrip_dynamic_discuss) private PagerSlidingTabStrip pStrip; //
	@ViewInject(R.id.viewpager_dynamic_discuss) private ExViewPager viewPager; //
	private PageAdapter mPageAdapter;
	private int enterFlag; //列表传值
	private UserNote note; //列表传值
	private ApiEntity.UserNote revNote; //列表传值

    @Override
    public void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra("isHideKeyboard", false);
        super.onCreate(savedInstanceState);
//		Eyes.setStatusBarLightMode(this, Color.WHITE);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
		enterFlag = getIntent().getIntExtra("enter_flag", 0);
        note = (UserNote) getIntent().getSerializableExtra("user_note");
		revNote = (ApiEntity.UserNote)getIntent().getSerializableExtra("rev_note");

		mHeaderBackBtn.setIconText("eb68");
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText("评论");
		mHeaderTitleTv.setVisibility(View.GONE);
		findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);

		pStrip.setTabWeightOne(true);
		pStrip.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
		pStrip.setTextColor(getResources().getColorStateList(R.color.tabstrip_text_color));
		mPageAdapter = new PageAdapter(getSupportFragmentManager());
		viewPager.setAdapter(mPageAdapter);
		viewPager.setPagingEnabled(false); //禁止左右滑动
		viewPager.setCurrentItem(1);
		viewPager.setOffscreenPageLimit(2);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageSelected(int position) {
				if (DynamicDiscussFragment.mContentEt != null) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							HelpUtil.hideSoftInput(DynamicDiscussActivity.this, DynamicDiscussFragment.mContentEt);
						}
					}, 300);
				}
			}
			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		pStrip.setViewPager(viewPager);
    }

	class PageAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = new String[] { "赞 " + note.EnjoyCount, "评论 " + note.RevCount, "分享 " + note.ShareCount };

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return DynamicFavourFragment.newInstance(note);
			} else if (position == 1) {
				return DynamicDiscussFragment.newInstance(note, revNote, enterFlag);
			} else {
				return DynamicShareFragment.newInstance(note);
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

	@Event({R.id.cm_header_btn_left, R.id.cm_header_btn_left9, R.id.cm_header_btn_right})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left:
			case R.id.cm_header_btn_left9:
				super.onBackPressed();
				break;
			case R.id.cm_header_btn_right:

				break;
		}
	}

	@Override
    public void onBackPressed() {
    	if (viewPager.getCurrentItem() == 1 && DynamicDiscussFragment.mContentEt.getHint().toString().startsWith("回复")) {
			DynamicDiscussFragment.mContentEt.setTag(null);
			DynamicDiscussFragment.mContentEt.setHint(R.string.dynamicdetail_edittext_hint);
    	} else {
			super.onBackPressed();
		}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case CHOSE_IMG_CODE: //选择的图片
					ArrayList<ApiEntity.Files> imgList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
					Intent intent = new Intent(DynamicDiscussFragment.ACTION_REFRESH_DISCUSS_LIST);
					intent.putExtra("img_list", imgList);
					sendBroadcast(intent);
					break;
				case CHOSE_FILE_CODE: //选择的文件
					ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
					Intent fileIntent = new Intent(DynamicDiscussFragment.ACTION_REFRESH_DISCUSS_LIST);
					fileIntent.putExtra("file_list", fileList);
					sendBroadcast(fileIntent);
					break;
			}
		}

		if (resultCode == Activity.RESULT_OK && requestCode == 10000) {
			String uri = data.getStringExtra("send_photo_uri");
			Intent imgIntent = new Intent(DynamicDiscussFragment.ACTION_REFRESH_DISCUSS_LIST);
			imgIntent.putExtra("img_path", uri);
			sendBroadcast(imgIntent);
		}
		if (requestCode == 100 && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
			//noinspection unchecked
			ArrayList<ImageItem> imageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
			if (imageList != null && imageList.size() > 0 && imageList.get(0) != null) {
				String pathStr = imageList.get(0).path;
				Intent intentImage = new Intent(this, ShowPhotoActivity.class);
				intentImage.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
				intentImage.putExtra("photo_uri", pathStr);
				startActivityForResult(intentImage, 10000);
			}
		}
		if (resultCode == Activity.RESULT_OK && requestCode == 101) {
			String uri = data.getStringExtra(CameraActivity.RESULT_DATA);
			Intent intentImage = new Intent(this, ShowPhotoActivity.class);
			intentImage.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
			intentImage.putExtra("photo_uri", uri);
			startActivityForResult(intentImage, 10000);
		}
	}
}
