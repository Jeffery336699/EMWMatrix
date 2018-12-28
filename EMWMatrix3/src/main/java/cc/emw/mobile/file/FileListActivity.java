package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.file.adapter.FileListAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 知识库列表
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_list)
public class FileListActivity extends BaseActivity implements OnItemClickListener {

	@ViewInject(R.id.cm_header_btn_left)
	private IconTextView mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//	@ViewInject(R.id.load_more_list_view_container)
//	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 文件列表
	@ViewInject(R.id.et_filelist_search)
	private EditText mSearchEt; // 搜索框
	@ViewInject(R.id.ll_file_blank)
	private LinearLayout mBlankLayout; // 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout; //无网络
	@ViewInject(R.id.itv_filelist_add)
	private IconTextView mAddItv;

	private Dialog mLoadingDialog; //加载框
	private FileListAdapter mFileAdapter; // 文件adapter
	private ArrayList<Files> mDataList; // 文件列表数据
	
	private static final int CHOSE_FILE_CODE = 100;
	private int page = PAGE_FIRST; //第几页
	private static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 10; //页数
	private int mType; //3:我的文件；4:共享给我的文件；5:作废的文件
	private Files noteFile; //传值
	private Handler handler;
	private String fileName; //传值
	private MyBroadcastReceive mReceive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Eyes.setStatusBarLightMode(this, Color.WHITE);
		mType = getIntent().getIntExtra("file_type", 3);
		fileName = getIntent().getStringExtra("file_name");
		noteFile = (Files) getIntent().getSerializableExtra("file_info");
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mPtrFrameLayout.autoRefresh(false);
			}
		};
		
		initView();
		initAddDialog();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FileDetailActivity.ACTION_REFRESH_FILE_DOWN);
		intentFilter.addAction(FileDetailActivity.ACTION_REFRESH_FILE_CANCEL);
		mReceive = new MyBroadcastReceive();
		registerReceiver(mReceive, intentFilter); // 注册监听

		mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
	}

	@Override
	protected void onDestroy() {
		if (mReceive != null) {
			unregisterReceiver(mReceive);
		}
		super.onDestroy();
	}

	@Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		mPtrFrameLayout.autoRefresh(false);
	}

	private void initView() {
		mHeaderBackBtn.setIconText("eb68");
		mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(fileName);
		mHeaderTitleTv.setVisibility(View.VISIBLE);
		
        mSearchEt.addTextChangedListener(new TextWatcher() {
        	@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
    		}
    		@Override
    		public void beforeTextChanged(CharSequence s, int start, int count,
    				int after) {
    		}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                	mPtrFrameLayout.autoRefresh(false);
                }
            }
		});
        
		mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					mPtrFrameLayout.autoRefresh(false);
					return true;
				}
				return false;
			}
		});
        
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
            	page = PAGE_FIRST;
            	String keyword = mSearchEt.getText().toString().trim();
//            	getFilesListByPage(keyword, mType, noteFile != null ? noteFile.ID : 0);
				if (mType == 3) {
					getFileList(keyword, 3, noteFile != null ? noteFile.ID : 0);
				} else {
					getOtherFileList(keyword, mType);
				}
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

        /*loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);*/
        
        mDataList = new ArrayList<Files>();

        mFileAdapter = new FileListAdapter(this, mDataList, handler, false);
		mListView.setAdapter(mFileAdapter);
		mListView.setOnItemClickListener(this);
		/*loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            	page++;
            	String keyword = mSearchEt.getText().toString().trim();
				getFilesListByPage(keyword, mType, noteFile != null ? noteFile.ID : 0);
            }
        });*/

		mAddItv.setVisibility(mType == 3 ? View.VISIBLE : View.GONE);
		mAddItv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddDialog.show();
			}
		});
	}
	
	@Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				HelpUtil.hideSoftInput(this, mSearchEt);
				onBackPressed();
				break;
			case R.id.cm_header_btn_right:
				Intent noticeIntent = new Intent(this, TestActivity.class);
				startActivity(noticeIntent);
				break;
    	}
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case CHOSE_FILE_CODE:
					String path = FileUtil.getPath(this, data.getData());
					uploadFile(path);
					break;
				case 10:
					mPtrFrameLayout.autoRefresh(false);
					break;
			}
    	}
    }

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int fid = intent.getIntExtra("fid", 0);
			if (FileDetailActivity.ACTION_REFRESH_FILE_DOWN.equals(action) && fid > 0) {
				int progress = intent.getIntExtra("progress", 0);
				int length = intent.getIntExtra("length", 0);
				if (progress == length) {
					mFileAdapter.setProgressMap(fid, 0);
					ToastUtil.showToast(FileListActivity.this, "下载成功", R.drawable.tishi_ico_gougou);
				} else {
					mFileAdapter.setProgressMap(fid, progress);
				}
			} else if (FileDetailActivity.ACTION_REFRESH_FILE_CANCEL.equals(action)) {
				mFileAdapter.setProgressMap(fid, 0);
			}
		}
	}

    /*private void getFilesListByPage(String keyword, int type, int foldId) {
    	API.UserData.GetFilesListByPage(keyword, type, foldId, page, PAGE_COUNT, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileListActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
				if (respList.size() < PAGE_COUNT)
					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
				else
					loadMoreListViewContainer.loadMoreFinish(false, true);
                if (page == PAGE_FIRST)
                	mDataList.clear();
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
    }*/
    
    /**
     * 获取文件列表
     * @param keyword
     */
    private void getFileList(String keyword, int type, int foldId) {
    	API.UserData.GetFileInfoByFolderId(keyword, type, foldId, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(FileListActivity.this, R.string.filelist_list_error);
				}
			}

			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileListActivity.this, mSearchEt);
			}

			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
				mNetworkTipsLayout.setVisibility(View.GONE);
				mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
				if (page == PAGE_FIRST) {
					mDataList.clear();
				}
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
    }

    /**
     * 获取文件列表(共享给我的文件/我分享的文件/作废的文件)
     * @param keyword
     */
    private void getOtherFileList(String keyword, int type) {
    	API.UserData.GetNewFilesList(keyword, type, 0, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(FileListActivity.this, R.string.filelist_list_error);
				}
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileListActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
				mNetworkTipsLayout.setVisibility(View.GONE);
				mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
                if (page == PAGE_FIRST)
                	mDataList.clear();
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (v.getTag(R.id.tag_second) != null) {
			Files noteFile = (Files)v.getTag(R.id.tag_second);
			if (noteFile.Type == 1) { //点击文件夹
				Intent intent = new Intent(FileListActivity.this, FileListActivity.class);
				intent.putExtra("file_type", mType);
				intent.putExtra("file_name", noteFile.Name);
				intent.putExtra("file_info", noteFile);
				startActivityForResult(intent, 10);
			} else {
				Intent intent = new Intent(FileListActivity.this, FileDetailActivity.class);
				intent.putExtra("file_info", noteFile);
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				intent.putExtra("click_pos_y", location[1]);
				startActivityForResult(intent, 10);
			}
			
		}
	}

	private ListDialog mAddDialog;
	private void initAddDialog() {
		mAddDialog = new ListDialog(this, false);
		mAddDialog.addItem(R.string.filelist_more_upload, 1);
		mAddDialog.addItem(R.string.filelist_more_create, 2);
		mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
			@Override
			public void selected(View view, ListDialog.Item item, int position) {
				switch (item.id) {
					case 1:
						setStartAnim(false);
						choseFileFromSystemFile();
						setStartAnim(true);
						break;
					case 2:
						Intent intent = new Intent(FileListActivity.this, FileCreateActivity.class);
						intent.putExtra("file_info", noteFile);
						intent.putExtra("start_anim", false);
						startActivityForResult(intent, 10);
						break;
				}
			}
		});
	}

	/**
     * 上传文件
     * @param path
     */
    private void uploadFile(String path) {
    	RequestParam params = new RequestParam(Const.UPLOAD_FILE_URL);
    	params.setMultipart(true);
    	File file = new File(path);
    	params.addBodyParameter("file_"+file.getName(), file);
    	ArrayList<Files> fileList = new ArrayList<Files>();
    	Files noteFile = new Files();
    	noteFile.Name = file.getName();
    	noteFile.Length = file.length();
    	noteFile.Creator = PrefsUtil.readUserInfo().ID;
    	noteFile.Url = file.getName();
    	noteFile.ParentID = this.noteFile != null ? this.noteFile.ID : 0;
    	fileList.add(noteFile);
    	params.addBodyParameter("UploadData", new Gson().toJson(fileList));
    	mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips4);
		mLoadingDialog.show();
    	Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException cex) {
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileListActivity.this, R.string.filelist_upload_error);
			}
			@Override
			public void onFinished() {
				
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileListActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
					mPtrFrameLayout.autoRefresh(false);
				} else {
					ToastUtil.showToast(FileListActivity.this, R.string.filelist_upload_error);
				} 
			}
		});
    }
    
    /**
     * 从本地文件管理选取文件
     */
    private void choseFileFromSystemFile() {  
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {  
            startActivityForResult(Intent.createChooser(intent, getString(R.string.filelist_choose_title)),
            		CHOSE_FILE_CODE);  
        } catch (android.content.ActivityNotFoundException ex) {  
        	ToastUtil.showToast(this, R.string.filelist_choose_error);
        }  
    }
}
