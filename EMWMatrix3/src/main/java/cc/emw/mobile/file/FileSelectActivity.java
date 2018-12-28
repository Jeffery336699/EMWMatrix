package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
import java.util.Stack;

import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.file.adapter.FileSelectAdapter3;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.ListDialog;

/**
 * 选择知识库文件(所有，包含文件夹)
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_select3)
public class FileSelectActivity extends BaseActivity implements OnItemClickListener {
	
	@ViewInject(R.id.cm_header_btn_left9)
	private IconTextView mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right9)
	private TextView mHeaderOkTv; // 顶部条确定

	@ViewInject(R.id.swipe_refresh)
	private SwipeRefreshLayout mSwipeRefreshLayout;
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 文件列表
	@ViewInject(R.id.ll_fileselect_search)
	private LinearLayout mSearchLayout;
	@ViewInject(R.id.et_filelist_search)
	private EditText mSearchEt; // 搜索框
	@ViewInject(R.id.ll_file_blank)
	private LinearLayout mBlankLayout; // 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout; //无网络
	@ViewInject(R.id.itv_fileselect_add)
	private IconTextView mAddItv;

	/** value : ApiEntity.Files的ArrayList集合*/
	public static final String EXTRA_SELECT_LIST = "select_list";
	/** value : [fid1, fid2, fid3]*/
	public static final String EXTRA_SELECT_IDS = "select_ids";
	
	private Dialog mLoadingDialog; //加载框
	private FileSelectAdapter3 mFileAdapter; // 文件adapter
	private ArrayList<Files> mDataList; // 文件列表数据
	
	
	private static final int CHOSE_FILE_CODE = 1;
	private int page = PAGE_FIRST; //第几页
	private static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 10; //页数
	private String selectIds; //传值
	private ArrayList<Files> selectList; // 已选中文件列表数据
	
	private Stack<String> mNameBackStack = new Stack<String>(); //文件夹名称缓存，返回
	private Stack<ArrayList<Files>> mDataListBackStack = new Stack<ArrayList<Files>>(); //列表数据缓存，快速返回
	private int fileID = -10; //当前文件夹ID
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectList = (ArrayList<Files>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
		selectIds = getIntent().getStringExtra(EXTRA_SELECT_IDS);

		initView();
		initAddDialog();

		if (fileID == -10) {
			initData();
    	}

		mAddItv.setVisibility(View.GONE);
		mAddItv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddDialog.show();
			}
		});

	}

	@Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		requestFirstPage();
	}

	private void initView() {
		mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderOkTv.setVisibility(View.VISIBLE);
        mHeaderOkTv.setText(R.string.ok);
        mHeaderTitleTv.setText(R.string.fileselect);

		mSearchLayout.setVisibility(View.GONE);
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
					requestFirstPage();
                }
            }
		});
        
		mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					requestFirstPage();
					return true;
				}
				return false;
			}
		});

		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow, R.color.ptr_red);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				page = PAGE_FIRST;
				String keyword = mSearchEt.getText().toString().trim();

				if (fileID == -10) {
					initData();
				} else if (fileID == -1) {
					getOtherFileList(keyword, 4);
				} else if (fileID == -2) {
					getOtherFileList(keyword, 6);
				} else if (fileID == -3) {
					getOtherFileList(keyword, 5);
				} else {
					getFileList(keyword, 3, fileID);
				}
			}
		});
        
        mDataList = new ArrayList<Files>();
        mFileAdapter = new FileSelectAdapter3(this, mDataList);
		//设置选中
        if (selectList != null && selectList.size() > 0) {
        	mFileAdapter.setSelectList(selectList);
        } else if (!TextUtils.isEmpty(selectIds)) {
        	try {
    			/*Type type = new TypeToken<List<Integer>>() {}.getType();
    			List<Integer> idList = new Gson().fromJson(selectIds, type);*/
				/*String[] idList = selectIds.split(",");
    			for (String id : idList) {
    				mFileAdapter.getSelectMap().put(Integer.valueOf(id), true);
    				Files file = new Files();
    				file.ID = Integer.valueOf(id);
    				mFileAdapter.getSelectList().add(file);
    			}*/
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
		mListView.setAdapter(mFileAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void initData() {
		mDataList.clear();
		Files file1 = new Files();
		file1.ID = 0;
		file1.Type = 1;
		file1.Name = getString(R.string.file_myfile);
		Files file2 = new Files();
		file2.ID = -1;
		file2.Type = 1;
		file2.Name = getString(R.string.file_sharefile);
		Files file3 = new Files();
		file3.ID = -2;
		file3.Type = 1;
		file3.Name = getString(R.string.file_mysharefile);
		Files file4 = new Files();
		file4.ID = -3;
		file4.Type = 1;
		file4.Name = getString(R.string.file_cancelfile);
		mDataList.add(file1);
		mDataList.add(file2);
		mDataList.add(file3);
		mDataList.add(file4);
		mFileAdapter.notifyDataSetChanged();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	protected void onDestroy() {
		if (mFileAdapter != null && mFileAdapter.getSelectMap() != null) {
			mFileAdapter.getSelectMap().clear();
		}
		super.onDestroy();
	}
	
	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				HelpUtil.hideSoftInput(this, mSearchEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				/*if (mFileAdapter.getSelectList().size() == 0) {
					ToastUtil.showToast(this, "请选择！");
					return;
				}*/
				Intent data = new Intent();
				ArrayList<Files> mmfiles = new ArrayList<>();
				mmfiles.addAll(mFileAdapter.getSelectList());
//				data.putParcelableArrayListExtra("select_list", (ArrayList<? extends Parcelable>) mFileAdapter.getSelectList());
				data.putExtra("select_list",mmfiles);
				setResult(Activity.RESULT_OK, data);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
					requestFirstPage();
					break;
			}
    	}
    }
	@Override
	public void onBackPressed() {
		if (mDataListBackStack.size() > 0) { //返回，加载缓存数据
			if (mDataListBackStack.size() == 1) {
				mSearchLayout.setVisibility(View.GONE);
				mAddItv.setVisibility(View.GONE);
			}
			mBlankLayout.setVisibility(View.GONE);
			mDataList.clear();
			mDataList.addAll(mDataListBackStack.pop());
			mFileAdapter.notifyDataSetChanged();
			mHeaderTitleTv.setText(mNameBackStack.pop());
			if (mDataList.size() > 0) {
				fileID = mDataList.get(0).ParentID;
			}
		} else {
			super.onBackPressed();
		}
	}

	// 请求第一页数据
	private void requestFirstPage() {
		mSwipeRefreshLayout.setRefreshing(true);
		page = PAGE_FIRST;
		String keyword = mSearchEt.getText().toString().trim();

		if (fileID == -10) {
			initData();
		} else if (fileID == -1) {
			getOtherFileList(keyword, 4);
		} else if (fileID == -2) {
			getOtherFileList(keyword, 6);
		} else if (fileID == -3) {
			getOtherFileList(keyword, 5);
		} else {
			getFileList(keyword, 3, fileID);
		}
	}

    /**
     * 获取文件列表
     * @param keyword
     */
    private void getFileList(String keyword, int type, int foldId) {
		Log.e("Const","---FileSelectActivity-getFileList--");
    	API.UserData.GetFileInfoByFolderId(keyword, type, foldId, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mSwipeRefreshLayout.setRefreshing(false);
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_list_error);
				}
				if (mDataListBackStack.size() > 0) {
					mDataListBackStack.pop();
				}
				if (mNameBackStack.size() > 0) {
					mHeaderTitleTv.setText(mNameBackStack.pop());
				}
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileSelectActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mSwipeRefreshLayout.setRefreshing(false);
				mNetworkTipsLayout.setVisibility(View.GONE);
				mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
                if (page == PAGE_FIRST)
                	mDataList.clear();
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
    }

    /**
     * 获取文件列表(共享给我的文件/作废的文件)
     * @param keyword
     */
    private void getOtherFileList(String keyword, int type) {
		Log.e("Const","---FileSelectActivity-getOtherFileList--");
    	API.UserData.GetFilesList(keyword, type, 0, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mSwipeRefreshLayout.setRefreshing(false);
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_list_error);
				}
				if (mDataListBackStack.size() > 0) {
					mDataListBackStack.pop();
				}
				if (mNameBackStack.size() > 0) {
					mHeaderTitleTv.setText(mNameBackStack.pop());
				}
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileSelectActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mSwipeRefreshLayout.setRefreshing(false);
				mNetworkTipsLayout.setVisibility(View.GONE);
				mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
                if (page == PAGE_FIRST)
                	mDataList.clear();
				mDataList.addAll(respList);
				mFileAdapter.notifyDataSetChanged();
			}
		});
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
						Intent intent = new Intent(FileSelectActivity.this, FileCreateActivity.class);
						intent.putExtra("start_anim", false);
						startActivity(intent);
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
		LogLongUtil.e("Const","----FileSelectActivity2--------");
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
    	noteFile.ParentID = fileID > 0 ? fileID : 0;
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
				ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_upload_error);
			}
			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
					requestFirstPage();
				} else {
					ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_upload_error);
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
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!mSwipeRefreshLayout.isRefreshing()) {
			mSearchLayout.setVisibility(View.VISIBLE);
			Files file = (Files) view.getTag(R.id.tag_second);
			if (file.Type == 1) { //点击文件夹
				ArrayList<Files> dataList = new ArrayList<Files>();
				dataList.addAll(mDataList);
				mDataListBackStack.push(dataList);
				mNameBackStack.push(mHeaderTitleTv.getText().toString());
				mHeaderTitleTv.setText(file.Name);
				fileID = file.ID;
				requestFirstPage();
				mAddItv.setVisibility(fileID < 0 ? View.GONE : View.VISIBLE);
			} else { //选中或取消文件
				List<Files> selectList = mFileAdapter.getSelectList();
				SparseBooleanArray selectMap = mFileAdapter.getSelectMap();
				Boolean isSelect = selectMap.get(file.ID);
				if (isSelect != null) {
					boolean curSelect = !isSelect;
					selectMap.put(file.ID, curSelect);
					if (curSelect) {
						selectList.add(file);
					} else {
						for (int i = 0, size = selectList.size(); i < size; i++) {
							if (file.ID == selectList.get(i).ID) {
								selectList.remove(i);
								break;
							}
						}
					}
				} else {
					selectMap.put(file.ID, true);
					selectList.add(file);
				}
				mFileAdapter.notifyDataSetChanged();
			}
		}
	}
}
