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

import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.file.adapter.FileSelectAdapter2;
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

/**
 * 选择知识库文件(分类型，不包含文件夹)
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_select3)
public class FileSelectActivity2 extends BaseActivity implements OnItemClickListener {
	
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

	public static final String EXTRA_SELECT_TYPE = "select_type";
	/** 单选 */
	public static final int RADIO_SELECT = 1;
	/** 多选 */
	public static final int MULTI_SELECT = 2;
	/** value : ApiEntity.Files的ArrayList集合*/
	public static final String EXTRA_SELECT_LIST = "select_list";
	/** value : [fid1, fid2, fid3]*/
	public static final String EXTRA_SELECT_IDS = "select_ids";
	public static final String EXTRA_FILE_TYPE = "file_type";
	public static final String EXTRA_POSITION = "position";
	
	private Dialog mLoadingDialog; //加载框
	private FileSelectAdapter2 mFileAdapter; // 文件adapter
	private ArrayList<Files> mDataList; // 文件列表数据
	
	
	private static final int CHOSE_FILE_CODE = 1;
	private int page = PAGE_FIRST; //第几页
	private static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 10; //页数
	private String selectIds; //传值
	private ArrayList<Files> selectList; // 已选中文件列表数据
	private int fileType; //1：文档；2：图片；3：视频；4：其他
	private int position;
	private int selectType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectType = getIntent().getIntExtra(EXTRA_SELECT_TYPE, RADIO_SELECT);
		selectList = (ArrayList<Files>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
		selectIds = getIntent().getStringExtra(EXTRA_SELECT_IDS);
		fileType = getIntent().getIntExtra(EXTRA_FILE_TYPE, 2);
		position = getIntent().getIntExtra(EXTRA_POSITION, 0);

		initView();

		mAddItv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choseFileFromSystemFile();
			}
		});

		requestFirstPage();
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
        
        mDataList = new ArrayList<Files>();
        mFileAdapter = new FileSelectAdapter2(this, mDataList);
		mFileAdapter.setSelectType(selectType);
		mFileAdapter.setFileType(fileType);
		mListView.setAdapter(mFileAdapter);
		mListView.setOnItemClickListener(this);

		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow, R.color.ptr_red);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				page = PAGE_FIRST;
				String keyword = mSearchEt.getText().toString().trim();

				getOtherFileList(keyword, 2);
			}
		});
		
	}

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				HelpUtil.hideSoftInput(this, mSearchEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				if (selectType == RADIO_SELECT && mFileAdapter.getSelectFile() == null) {
					ToastUtil.showToast(this, "请选择！");
					return;
				}
				Intent data = new Intent();
				if (selectType == RADIO_SELECT) {
					data.putExtra("select_file", mFileAdapter.getSelectFile());
				} else {
					data.putExtra("select_list", mFileAdapter.getSelectList());
				}
				data.putExtra(EXTRA_POSITION, position);
				setResult(Activity.RESULT_OK, data);
				finish();
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

	// 请求第一页数据
	private void requestFirstPage() {
		mSwipeRefreshLayout.setRefreshing(true);
		page = PAGE_FIRST;
		String keyword = mSearchEt.getText().toString().trim();

		getOtherFileList(keyword, 2);
	}

    /**
     * 获取视频文件列表(filetype=3)
     * @param keyword
     */
    private void getOtherFileList(String keyword, int type) {
		Log.e("Const","---FileSelectActivity2--getOtherFileList-");
    	API.UserData.GetFilesList(keyword, type, fileType, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mSwipeRefreshLayout.setRefreshing(false);
				mBlankLayout.setVisibility(View.GONE);
				if (ex instanceof ConnectException) {
					mNetworkTipsLayout.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.showToast(FileSelectActivity2.this, R.string.filelist_list_error);
				}
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileSelectActivity2.this, mSearchEt);
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
    	noteFile.ParentID = 0;
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
				ToastUtil.showToast(FileSelectActivity2.this, R.string.filelist_upload_error);
			}
			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileSelectActivity2.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
					requestFirstPage();
				} else {
					ToastUtil.showToast(FileSelectActivity2.this, R.string.filelist_upload_error);
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
			Files file = (Files) view.getTag(R.id.tag_second);
			switch (selectType) {
				case RADIO_SELECT:
					mFileAdapter.setSelectFile(file);
					break;
				case MULTI_SELECT:
					SparseBooleanArray selectMap = mFileAdapter.getSelectMap();
					Boolean isSelect = selectMap.get(file.ID);
					if (isSelect != null) {
						boolean curSelect = !isSelect;
						selectMap.put(file.ID, curSelect);
						if (curSelect) {
							mFileAdapter.getSelectList().add(file);
						} else {
							for (int i = 0, size = mFileAdapter.getSelectList().size(); i < size; i++) {
								if (file.ID == mFileAdapter.getSelectList().get(i).ID) {
									mFileAdapter.getSelectList().remove(i);
									break;
								}
							}
						}
					} else {
						selectMap.put(file.ID, true);
						mFileAdapter.getSelectList().add(file);
					}
					break;
			}
			mFileAdapter.notifyDataSetChanged();
		}
	}
}
