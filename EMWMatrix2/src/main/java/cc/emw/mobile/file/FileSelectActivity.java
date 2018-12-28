package cc.emw.mobile.file;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
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
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.file.adapter.FileSelectAdapter;
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
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 选择知识库文件
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_select)
public class FileSelectActivity extends BaseActivity implements OnItemClickListener {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv; // 顶部条确定
	
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
	
	/** value : ApiEntity.Files的ArrayList集合*/
	public static final String EXTRA_SELECT_LIST = "select_list";
	/** value : [fid1, fid2, fid3]*/
	public static final String EXTRA_SELECT_IDS = "select_ids";
	
	private Dialog mLoadingDialog; //加载框
	private FileSelectAdapter mFileAdapter; // 文件adapter
	private ArrayList<Files> mDataList; // 文件列表数据
	
	
	private static final int CHOSE_FILE_CODE = 1;
	private int page = PAGE_FIRST; //第几页，(page-1)*PAGE_COUNT+1
	private static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 10; //页数
	private int mType; //0:所有文件；1:我的文件；2:共享给我的文件；3:作废的文件
	private Files noteFile;
	private String selectIds;
	private ArrayList<Files> selectList; // 已选中文件列表数据
	
	private Stack<String> mNameBackStack = new Stack<String>(); //文件夹名称缓存，返回
	private Stack<ArrayList<Files>> mDataListBackStack = new Stack<ArrayList<Files>>(); //列表数据缓存，快速返回
	private int fileID = -10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = getIntent().getIntExtra("file_type", 3);
		noteFile = (Files) getIntent().getSerializableExtra("note_file");
		selectList = (ArrayList<Files>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
		selectIds = getIntent().getStringExtra(EXTRA_SELECT_IDS);

		initView();
		initMenu();
		
		if (fileID == -10) {
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
    		file3.Name = getString(R.string.file_cancelfile);
    		mDataList.add(file1);
    		mDataList.add(file2);
    		mDataList.add(file3);
    		mFileAdapter.notifyDataSetChanged();
    		mPtrFrameLayout.refreshComplete();
    	} 
		/*mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);*/
	}
	private void initView() {
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
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
        
        mPtrFrameLayout.setPinContent(true);
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
				
				/*if (mType == 3) {
					getFileList(noteFile != null ? noteFile.ID : 0, mType);
				} else {
					getOtherFileList(keyword, mType);
				}
				getOtherFileList(keyword, mType);*/
            	if (fileID == -10) {
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
            		file3.Name = getString(R.string.file_cancelfile);
            		mDataList.add(file1);
            		mDataList.add(file2);
            		mDataList.add(file3);
            		mFileAdapter.notifyDataSetChanged();
            		mPtrFrameLayout.refreshComplete();
            	} else if (fileID == -1) {
    				getOtherFileList(keyword, 4);
    			} else if (fileID == -2) {
    				getOtherFileList(keyword, 5);
    			} else {
    				getFileList(keyword, 3, fileID);
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
        mPtrFrameLayout.setEnabled(false);
        /*loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);*/
        
        mDataList = new ArrayList<Files>();
        mFileAdapter = new FileSelectAdapter(this, mDataList);
        if (selectList != null && selectList.size() > 0) {
        	mFileAdapter.setSelectList(selectList);
        } else if (!TextUtils.isEmpty(selectIds)) {
        	try {
    			Type type = new TypeToken<List<Integer>>() {}.getType();
    			List<Integer> idList = new Gson().fromJson(selectIds, type);
    			for (Integer id : idList) {
    				mFileAdapter.getSelectMap().put(id, true);
    				Files file = new Files();
    				file.ID = id;
    				mFileAdapter.getSelectList().add(file);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
		mListView.setAdapter(mFileAdapter);
		mListView.setOnItemClickListener(this);
		/*loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            	page++;
            	String keyword = mSearchEt.getText().toString().trim();
				getFileList(noteFile.ID, mType);
            }
        });*/
		
	}
	
	@Override
	protected void onDestroy() {
		if (mFileAdapter != null && mFileAdapter.getSelectMap() != null) {
			mFileAdapter.getSelectMap().clear();
		}
		super.onDestroy();
	}
	
	@Event({R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				HelpUtil.hideSoftInput(this, mSearchEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right:
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
	@Override
	public void onBackPressed() {
		if (mDataListBackStack.size() > 0) {
			mBlankLayout.setVisibility(View.GONE);
			mDataList.clear();
			mDataList.addAll(mDataListBackStack.pop());
			mFileAdapter.notifyDataSetChanged();
			mHeaderTitleTv.setText(mNameBackStack.pop());
		} else {
			super.onBackPressed();
		}
	}
	
	private RightMenu mMenu;
    private void initMenu() {  
        mMenu = new RightMenu(this);  
        mMenu.addItem("新建文件夹", 1);  
        mMenu.addItem("上传文件", 2);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {  
            @Override  
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
	                case 1:
//						setStartAnim(false);
	                	Intent intent = new Intent(FileSelectActivity.this, FileCreateActivitys.class);
	    				intent.putExtra("note_file", noteFile);
	    				startActivityForResult(intent, 10);
	                    break;
                    case 2:
						setStartAnim(false);
                    	choseFileFromSystemFile();
                        break;  
                }
            }  
        });
    }
    
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
				ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_list_error);
				mDataListBackStack.pop();
				mHeaderTitleTv.setText(mNameBackStack.pop());
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileSelectActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
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
    	API.UserData.GetFilesList(keyword, type, new RequestCallback<Files>(Files.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mPtrFrameLayout.refreshComplete();
				mBlankLayout.setVisibility(View.GONE);
				ToastUtil.showToast(FileSelectActivity.this, R.string.filelist_list_error);
				mDataListBackStack.pop();
				mHeaderTitleTv.setText(mNameBackStack.pop());
			}
			@Override
			public void onStarted() {
				HelpUtil.hideSoftInput(FileSelectActivity.this, mSearchEt);
			}
			@Override
			public void onParseSuccess(List<Files> respList) {
				mPtrFrameLayout.refreshComplete();
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
    	mLoadingDialog = createLoadingDialog("正在上传...");
		mLoadingDialog.show();
    	Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException cex) {
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileSelectActivity.this, "上传文件失败！");
			}
			@Override
			public void onFinished() {
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileSelectActivity.this, "上传文件成功！", R.drawable.tishi_ico_gougou);
					mPtrFrameLayout.autoRefresh(false);
				} else {
					ToastUtil.showToast(FileSelectActivity.this, "上传文件失败！");
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
            startActivityForResult(Intent.createChooser(intent, "请选择文件!"),
            		CHOSE_FILE_CODE);  
        } catch (android.content.ActivityNotFoundException ex) {  
            ToastUtil.showToast(this, "请安装文件管理器！");
        }  
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!mPtrFrameLayout.isRefreshing()) {
			Files file = (Files) view.getTag(R.id.tag_second);
			if (file.Type == 1) {
				ArrayList<Files> dataList = new ArrayList<Files>();
				dataList.addAll(mDataList);
				mDataListBackStack.push(dataList);
				mNameBackStack.push(mHeaderTitleTv.getText().toString());
				mHeaderTitleTv.setText(file.Name);
				fileID = file.ID;
				mPtrFrameLayout.autoRefresh(false);
			} else {
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
