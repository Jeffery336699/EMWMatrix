package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 文件更新
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_update3)
public class FileUpdateActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv; //

	@ViewInject(R.id.iv_fileupdate_curicon)
	private ImageView mCurIconIv; //当前文件图标
	@ViewInject(R.id.tv_fileupdate_curname)
	private TextView mCurNameTv; //当前文件名称
	@ViewInject(R.id.tv_fileupdate_cursize)
	private TextView mCurSizeTv; //当前文件大小
	@ViewInject(R.id.tv_fileupdate_curtime)
	private TextView mCurTimeTv; //当前上传时间

	@ViewInject(R.id.ll_fileupdate_add)
	private LinearLayout mAddLayout; //添加文件
	@ViewInject(R.id.rl_fileupdate_update)
	private RelativeLayout mUpdateLayout; //更新的Layout
	@ViewInject(R.id.iv_fileupdate_icon)
	private ImageView mIconIv; //更新的文件图标
	@ViewInject(R.id.tv_fileupdate_name)
	private TextView mNameTv; //更新的文件名称
	@ViewInject(R.id.tv_fileupdate_size)
	private TextView mSizeTv; //更新的文件大小
	@ViewInject(R.id.tv_fileupdate_time)
	private TextView mUploadTimeTv; //更新的上传时间
	@ViewInject(R.id.et_fileupdate_desc)
	private EditText mUpdateDescEt; //更新的描述

	private Dialog mLoadingDialog; //加载框
	private Files noteFile; //传值
	private String path; //选择上传文件更新的本地路径
	private static final int CHOSE_FILE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");
        
        initView();
        
    }
    
    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderOkTv.setText(R.string.ok);
		mHeaderOkTv.setVisibility(View.VISIBLE);*/
		mHeaderTitleTv.setText("更新");
		((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");

		mCurIconIv.setImageResource(FileUtil.getResIconId(noteFile.Name));
        mCurNameTv.setText(noteFile.Name);
        mCurSizeTv.setText(FileUtil.getReadableFileSize(noteFile.Length));
        mCurTimeTv.setText(noteFile.UpdateTime);

    }

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				HelpUtil.hideSoftInput(this, mUpdateDescEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				if (mUpdateLayout.getVisibility() != View.VISIBLE) {
					ToastUtil.showToast(this, "请添加文件！");
				} else {
					uploadFile(path);
				}
				break;
    	}
    }
    @Event({R.id.ll_fileupdate_add, R.id.itv_fileupdate_del})
    private void onRelationClick(View v) {
    	Intent intent = null;
    	switch (v.getId()) {
			case R.id.ll_fileupdate_add:
				choseFileFromSystemFile();
				break;
			case R.id.itv_fileupdate_del:
				mAddLayout.setVisibility(View.VISIBLE);
				mUpdateLayout.setVisibility(View.GONE);
				break;
    	}
    }

	/**
	 * 上传文件
	 * @param path
	 */
	private void uploadFile(String path) {
		RequestParam params = new RequestParam(Const.BASE_URL + "/UploadFile?path=&n=1&save=0");
		params.setMultipart(true);
		final File file = new File(path);
		params.addBodyParameter("file_"+file.getName(), file);
		mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
		mLoadingDialog.show();
		Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onCancelled(CancelledException cex) {
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileUpdateActivity.this, "更新失败");
			}
			@Override
			public void onFinished() {

			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (!TextUtils.isEmpty(result) && result.startsWith("[")) {
					ApiEntity.UploadResult uploadResult = new Gson().fromJson(result.substring(1, result.length() - 1), ApiEntity.UploadResult.class);
					Files fileInfo = new Files();
					fileInfo.ID = noteFile.ID;
					fileInfo.Name = file.getName();
					fileInfo.Type = 0;
					fileInfo.Length = file.length();
					fileInfo.Url = uploadResult.NewName;
					fileInfo.Content = mUpdateDescEt.getText().toString();
					fileInfo.ParentID = noteFile.ParentID;
					fileInfo.ProjectId = noteFile.ProjectId;
					fileInfo.IsActive = 1;
					saveFileInfo(fileInfo);
				} else {
					ToastUtil.showToast(FileUpdateActivity.this, "更新失败");
				}
			}
		});
	}

	/**
	 * 保存文件记录
	 * @param fileInfo
     */
	private void saveFileInfo(Files fileInfo) {
		API.UserData.SaveFileInfo(fileInfo, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileUpdateActivity.this, "更新失败");
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileUpdateActivity.this, "更新成功", R.drawable.tishi_ico_gougou);
					finish();
				} else {
					ToastUtil.showToast(FileUpdateActivity.this, "更新失败");
				}
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case CHOSE_FILE_CODE:
					path = FileUtil.getPath(this, data.getData());
					mAddLayout.setVisibility(View.GONE);
					File file = new File(path);
					mIconIv.setImageResource(FileUtil.getResIconId(noteFile.Name));
					mNameTv.setText(noteFile.Name);
					mSizeTv.setText(FileUtil.getReadableFileSize(file.length()));
					mUploadTimeTv.setText(StringUtils.getCurTimeStr());
					mUpdateLayout.setVisibility(View.VISIBLE);
					break;
    		}
		}
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
