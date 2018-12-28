package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.dynamic.adapter.BusDataAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 业务数据1级
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_bus_data)
public class BusDataActivity extends BaseActivity implements OnItemClickListener{

//	@ViewInject(R.id.cm_header_btn_left) private ImageButton mHeaderCancelBtn; // 顶部条取消
//	@ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//	@ViewInject(R.id.cm_header_tv_right) private TextView mHeaderSendTv; // 顶部条发布

	@ViewInject(R.id.load_more_list_view_ptr_frame) private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//	@ViewInject(R.id.load_more_list_view_container)	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view) private ListView mListView; // 近期动态列表

	private Dialog mLoadingDialog; //加载框
	private ArrayList<Navigation> dataList; // 列表数据
	private BusDataAdapter busDataAdapter; // adapter

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
		initView();

		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
	}
	private void initView() {
		/*mHeaderCancelBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText("业务数据");
		mHeaderSendTv.setText(R.string.publish);
		mHeaderSendTv.setVisibility(View.GONE);*/
		((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
		findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);

		mPtrFrameLayout.setToggleMenu(true);
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
											 View content, View header) {
				// here check list view, not content.
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						mListView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				// 请求第一页数据
				getUserGridNavigation();
			}
		});

		// header
		final MaterialHeader header = new MaterialHeader(this);
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
		header.setPtrFrameLayout(mPtrFrameLayout);

		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setDurationToCloseHeader(1500);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);

//		loadMoreListViewContainer.useDefaultFooter();
//		loadMoreListViewContainer.setAutoLoadMore(true);

		// binding view and data
		dataList = new ArrayList<>();
		busDataAdapter = new BusDataAdapter(this);
		mListView.setAdapter(busDataAdapter);
		mListView.setOnItemClickListener(this);

	}

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9, R.id.cm_header_btn_left})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:

				break;
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case DateActivity.REQUESTCODE_BUSDATA:
					Intent intent = new Intent();
					intent.putExtra("bus_info", data.getSerializableExtra("bus_info"));
					setResult(Activity.RESULT_OK, intent);
					finish();
					break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (view.getTag(R.id.tag_second) != null) {
			Navigation nav = (Navigation) view.getTag(R.id.tag_second);
			Intent intent = new Intent(this, BusDataActivity2.class);
			intent.putExtra("nav_id", nav.PAGEID);
			intent.putExtra("nav_name", nav.Name);
			intent.putExtra("start_anim", false);
			startActivityForResult(intent, DateActivity.REQUESTCODE_BUSDATA);
		}
	}

	private void getUserGridNavigation() {
		API.TemplateAPI.GetUserGridNavigation(new RequestCallback<Navigation>(Navigation.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				ToastUtil.showToast(BusDataActivity.this, R.string.dynamic_list_error);
			}
			@Override
			public void onParseSuccess(List<Navigation> respList) {
				mPtrFrameLayout.refreshComplete();
				if (respList != null && respList.size() > 0) {
					dataList.clear();
					dataList.addAll(respList);
					busDataAdapter.setData(dataList);
					busDataAdapter.notifyDataSetChanged();
				}
			}
		});
	}

}
