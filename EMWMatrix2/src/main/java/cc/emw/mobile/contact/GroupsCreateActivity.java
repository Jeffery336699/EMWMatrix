package cc.emw.mobile.contact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.SwitchButton;

import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_create_group)
public class GroupsCreateActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeadRight;
	@ViewInject(R.id.group_create_et_name)
	private EditText mNameEt; // 小组名称
	@ViewInject(R.id.group_create_sb_apply)
	private SwitchButton mPrivate; // 需要申请
	@ViewInject(R.id.group_create_et_memo)
	private EditText mMemoEt; // 圈子描述
	@ViewInject(R.id.group_create_iv_head)
	private CircleImageView mCircleImageView;
	private Dialog mLoadingDialog;
	private String mUploadImg;
	private List<Integer> userids = new ArrayList<Integer>();

	private static final int REQUEST_CAPTURE_PHOTO = 1010;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeadRight.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.groupcreate);
		mHeadRight.setText(R.string.finish);
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.cm_header_tv_right,
			R.id.group_create_ll_upload, R.id.group_create_ll_apply,
			R.id.cm_header_btn_right })
	private void onFollowClick(View view) {
		switch (view.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_tv_right:
			String name = mNameEt.getText().toString().trim();
			if (TextUtils.isEmpty(name)) {
				ToastUtil.showToast(this, R.string.groupcreate_empty_name);
			} else {
				addGroup(name);
			}
			break;
		case R.id.group_create_ll_apply:
			mPrivate.toggle();
			break;
		case R.id.group_create_ll_upload:
			ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
			dialog.addSheetItem(getString(R.string.actionsheet_photo), null,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
									.fromFile(new File(EMWApplication.tempPath
											+ "tempraw.png")));
							startActivityForResult(intent,
									REQUEST_CAPTURE_PHOTO);
						}
					});
			dialog.addSheetItem(getString(R.string.actionsheet_pick), null,
					new OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							Crop.pickImage(GroupsCreateActivity.this);
						}
					});
			dialog.show();
			break;
		}
	}

	// 创建圈子
	private void addGroup(String name) {
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.Name = name;
		groupInfo.Memo = mMemoEt.getText().toString().trim();
		groupInfo.Type = mPrivate.isChecked() ? 1 : 0;
		groupInfo.ID = 0;
		groupInfo.Image = mUploadImg;
		API.TalkerAPI.SaveChatterGroup(groupInfo, userids,
				new RequestCallback<String>(String.class) {
					@Override
					public void onError(Throwable arg0, boolean arg1) {
						if (mLoadingDialog != null)
							mLoadingDialog.dismiss();
						ToastUtil.showToast(GroupsCreateActivity.this,
								R.string.groupcreate_error);
					}

					@Override
					public void onStarted() {
						mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips6);
						mLoadingDialog.show();
					}

					@Override
					public void onSuccess(String result) {
						if (mLoadingDialog != null)
							mLoadingDialog.dismiss();
						if (!TextUtils.isEmpty(result)
								&& TextUtils.isDigitsOnly(result)
								&& Integer.valueOf(result) > 0) {
							ToastUtil.showToast(GroupsCreateActivity.this,
									R.string.groupcreate_success,
									R.drawable.tishi_ico_gougou);
							Intent intent = new Intent(
									GroupFragment.ACTION_REFRESH_GROUP);
							sendBroadcast(intent); // 刷群主列表
							GroupsCreateActivity.this.finish();
						} else {
							ToastUtil.showToast(GroupsCreateActivity.this,
									R.string.groupcreate_error);
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
			Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
					+ "tempraw.png"));
			Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
					+ "tempcrop.png"));
			Crop.of(inputUri, outputUri).asSquare().start(this);
		} else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
			beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, data);
		}

	}

	private void beginCrop(Uri source) {
		Uri destination = Uri.fromFile(new File(getCacheDir(), "zkbr.png"));
		Crop.of(source, destination).asSquare().start(this);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			Uri uri = Crop.getOutput(result);
			uploadImage(uri.getPath(), uri);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 群组头像上传
	 * 
	 * @param path
	 * @param uri
	 */
	private void uploadImage(String path, final Uri uri) {
		final Dialog mDialog = createLoadingDialog(R.string.loading_dialog_tips4);
		RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
		params.setMultipart(true);
		File file = new File(path);
		params.addBodyParameter(file.getName(), file);
		x.http().post(params, new RequestCallback<String>(String.class) {

			@Override
			public void onStarted() {
				super.onStarted();
				if (mDialog != null) {
					mDialog.show();
				}
			}

			@Override
			public void onCancelled(CancelledException cex) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				ToastUtil.showToast(GroupsCreateActivity.this,
						R.string.headupload_error);
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onParseSuccess(List<String> result) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				mUploadImg = result.get(0);
				mCircleImageView.setImageBitmap(BitmapFactory
						.decodeFile(new File(uri.getPath()).getPath()));
			}
		});
	}
}
