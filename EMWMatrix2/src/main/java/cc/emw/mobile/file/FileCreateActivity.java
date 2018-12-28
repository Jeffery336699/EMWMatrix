package cc.emw.mobile.file;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;

/**
 * 新建文件夹·对话框
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_create)
public class FileCreateActivity extends BaseActivity {
	
	@ViewInject(R.id.et_filecreate_name)
	private EditText mNameEt; // 
	@ViewInject(R.id.et_filecreate_desc)
	private EditText mDescEt; // 
	@ViewInject(R.id.btn_filecreate_cancel)
	private Button mCancelBtn; // 
	@ViewInject(R.id.btn_filecreate_ok)
	private Button mOkBtn; // 
	
	private Dialog mLoadingDialog; //加载框
	private Files noteFile;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setSwipeBackEnable(false);
        noteFile = (Files) getIntent().getSerializableExtra("note_file");
        
    }
    
    @Override
    public void onBackPressed() {
    	finish();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Event({R.id.btn_filecreate_cancel, R.id.btn_filecreate_ok})
    private void onFooterClick(View v) {
    	switch (v.getId()) {
			case R.id.btn_filecreate_cancel:
				onBackPressed();
				break;
			case R.id.btn_filecreate_ok:
				String name = mNameEt.getText().toString();
		    	String desc = mDescEt.getText().toString();
		    	if (!TextUtils.isEmpty(name)) {
					saveFolder(noteFile != null ? noteFile.ID : 0, name, desc);
		    	} else {
		    		ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_empty_folder);
		    	}
				break;
    	}
    }
    
    
    /**
     * 新建文件夹
     */
    private void saveFolder(int pid, String name, String desc) {
		Files file = new Files();
		file.ParentID = pid;
		file.Name = name;
		file.Content = desc;
    	
    	API.UserData.SaveFoldInfo(file, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips6);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_success, R.drawable.tishi_ico_gougou);
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_error);
				} 
			}
		});
    }
    
}
