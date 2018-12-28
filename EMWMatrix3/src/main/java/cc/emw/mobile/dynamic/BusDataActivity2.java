package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.xlf.nrl.NsRefreshLayout;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.BusDataInfo;
import cc.emw.mobile.form.adapter.RadioTableAdapter;
import cc.emw.mobile.form.entity.DataTable;
import cc.emw.mobile.form.entity.GridControl;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 业务数据2级
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_bus_data2)
public class BusDataActivity2 extends BaseActivity {

	@ViewInject(R.id.load_more_list_view_ptr_frame) private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//	@ViewInject(R.id.load_more_list_view_container) private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.table) private TableFixHeaders tableFixHeaders;
	@ViewInject(R.id.nrl_test) private NsRefreshLayout refreshLayout;

	private Dialog mLoadingDialog; //加载框
	private MyAdapter adapter; // adapter
	private ArrayList<DataTable> mDataList; // 列表数据
	private GridControl gridControl; //表头等数据
	private DataTable mGetListDataTable; //行数据

	private int navID;
	private int page = PAGE_FIRST; //第几页，(page-1)*PAGE_COUNT+1
	public static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 20; //页数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
		navID = getIntent().getIntExtra("nav_id", 0);
		initView();
		getGrid();
	}

	private void initView() {
		((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
											 View content, View header) {
				// here check list view, not content.
				return tableFixHeaders.getActualScrollY() == 0;
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				// 请求第一页数据
				page = PAGE_FIRST;
				getList();
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
		mDataList = new ArrayList<>();
		adapter = new MyAdapter(this);
		tableFixHeaders.setAdapter(adapter);

		refreshLayout.setRefreshLayoutListener(new NsRefreshLayout.NsRefreshLayoutListener() {
			@Override
			public void onRefresh() {

			}

			@Override
			public void onLoadMore() {
				page++;
				getList();
			}
		});
		refreshLayout.setRefreshLayoutController(new NsRefreshLayout.NsRefreshLayoutController() {
			@Override
			public boolean isPullRefreshEnable() {
				return false;
			}

			@Override
			public boolean isPullLoadEnable() {
				if (mGetListDataTable != null && mGetListDataTable.Rows != null) {
					if (mGetListDataTable.Rows.size() < PAGE_COUNT) {
						refreshLayout.mPullLoadText = "没有更多数据了!";
						refreshLayout.hasNoMoreData = false;
						refreshLayout.removeCircleProgress();
					} else {
						refreshLayout.mPullLoadText = null;
						refreshLayout.hasNoMoreData = true;
						refreshLayout.addCircleProgress();
					}
				} else {
					refreshLayout.mPullLoadText = null;
					refreshLayout.hasNoMoreData = true;
					refreshLayout.addCircleProgress();
				}
				return true;
			}
		});
		//自定义表格滑到底部的回调，用于对上拉加载更多的事件进行拦截
		refreshLayout.setUpScrollFlagListener(new NsRefreshLayout.UpScrollFlagListener() {
			@Override
			public boolean onUp() {
				if (tableFixHeaders.getActualScrollY() == DisplayUtil.dip2px(BusDataActivity2.this, adapter.getRowCount() * 40)) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				Intent data = new Intent();
				BusDataInfo busDataInfo = new BusDataInfo();
				busDataInfo.PageID = navID;
				busDataInfo.ID = adapter.getSelectID();
				busDataInfo.Text = adapter.getText();
				busDataInfo.TID = gridControl.TID;
				data.putExtra("bus_info", busDataInfo);
				setResult(Activity.RESULT_OK, data);
				finish();
				break;
		}
	}

	/**
	 * 获取各区域数据
	 */
	private void getGrid() {
		RequestParam params = new RequestParam(Const.BASE_URL + "/mpage/" + navID);
		x.http().post(params, new RequestCallback<GridControl>(GridControl.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
//				ToastUtil.showToast(BusDataActivity2.this, "获取各区域数据失败！");
				AlertDialog dialog = new AlertDialog(BusDataActivity2.this).builder();
				dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
				dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						scrollToFinishActivity();
					}
				}).show();
			}

			@Override
			public void onStarted() {
				/*mLoadingDialog = createLoadingDialog("正在加载...");
				mLoadingDialog.show();*/
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mPtrFrameLayout.autoRefresh(false);
				gridControl = new Gson().fromJson(result, GridControl.class);
				if (gridControl != null) {
					//初始化表格数据
					if (gridControl.Columns != null && gridControl.Columns.size() > 0) {
						mPtrFrameLayout.autoRefresh(false);
					}
				} else {
					AlertDialog dialog = new AlertDialog(BusDataActivity2.this).builder();
					dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
					dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							scrollToFinishActivity();
						}
					}).show();
				}
			}
            /*@Override
            public void onParseSuccess(GridControl respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				titleList = respInfo.Columns;
				mPtrFrameLayout.autoRefresh(false);
			}*/
		});
	}

	public void getList() {
		RequestParam params = new RequestParam(Const.BASE_URL + "/Page/" + navID + "/loaddata");
		String body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"ChartNavigations\":[]}";
		params.setStringBody(body);
		x.http().post(params, new RequestCallback<DataTable>(DataTable.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(BusDataActivity2.this, "获取列表数据失败！");
				mPtrFrameLayout.refreshComplete();
				mGetListDataTable = null;
				refreshLayout.hasNoMoreData = false;
				refreshLayout.finishPullLoad();
			}

			/*@Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }*/
			@Override
			public void onParseSuccess(DataTable respInfo) {
				mGetListDataTable = respInfo;
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mPtrFrameLayout.refreshComplete();
				refreshLayout.finishPullLoad();
				if (page == 1) {
					mDataList.clear();
					adapter.clearSelectID();
				}
				mDataList.add(respInfo);
				adapter.setDataList(mDataList);
				adapter.notifyDataSetChanged();
				if (page == 1) {
					tableFixHeaders.scrollTo(0, 0);
				}
			}
		});
	}

	public class MyAdapter extends RadioTableAdapter {

		private final int width;
		private final int height;

		private ArrayList<DataTable> dtList;

		public MyAdapter(Context context) {
			super(context);

			Resources resources = context.getResources();

			width = resources.getDimensionPixelSize(R.dimen.table_width);
			height = resources.getDimensionPixelSize(R.dimen.table_height);
		}

		public void setDataList(ArrayList<DataTable> dtList) {
			this.dtList = dtList;
		}

		@Override
		public int getRowCount() {
			int count = 0;
			if (dtList != null) {
				for (DataTable dt : dtList) {
					count += dt.Rows.size();
				}
			}
			System.out.println("rowcount:" + count);
			return count;
		}

		@Override
		public int getColumnCount() {
			int count = 0;
			if (dtList != null && dtList.size() > 0) {
				count = dtList.get(0).Columns.size() - 2;
			}
			System.out.println("colcount:" + count);
			return count;
		}

		@Override
		public int getWidth(int column) {
			return width;
		}

		@Override
		public int getHeight(int row) {
			return height;
		}

		@Override
		public String getCellString(int row, int column) {
			if (row == -1) {
				if (gridControl != null && gridControl.Columns != null && gridControl.Columns.size() > 0
						&& column + 1 < gridControl.Columns.size() && gridControl.Columns.get(column + 1) != null) {
					return gridControl.Columns.get(column + 1).Name;
				} else {
					return "";
				}
			} else {
				return dtList != null ? dtList.get(row / PAGE_COUNT).Rows.get(row - row / PAGE_COUNT * PAGE_COUNT).get(column + 2) : "";
			}
		}

		@Override
		public int getLayoutResource(int row, int column) {
			final int layoutResource;
			switch (getItemViewType(row, column)) {
				case 0:
					layoutResource = R.layout.item_table_header;
					break;
				case 1:
					layoutResource = R.layout.item_table;
					break;
				default:
					throw new RuntimeException("wtf?");
			}
			return layoutResource;
		}

		@Override
		public int getItemViewType(int row, int column) {
			if (row < 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
	}
}
