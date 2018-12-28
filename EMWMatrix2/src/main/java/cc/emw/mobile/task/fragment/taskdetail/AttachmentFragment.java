package cc.emw.mobile.task.fragment.taskdetail;

import cc.emw.mobile.file.FileSelectActivity;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.TaskModifyActivity;
import cc.emw.mobile.task.adapter.AttachmentAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.task.view.ItaskShareAttachmentView;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.fragment_task_detail_attachment)
public class AttachmentFragment extends BaseFragment implements
		ITaskModifyView, ItaskShareAttachmentView {

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_task_attach)
	private ListView mListView;

	@ViewInject(R.id.ll_task_blank)
	private LinearLayout mBlankLayout;// 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;// 无网络

	private TextView mHeaderRightTv;
	private ImageButton mHeaderRightBtn;
	private AttachmentAdapter mFileListAdapter;
	private UserFenPai mUserFenPai;
	private AttachmentBroadcastReceive mReceive;
	private TaskPresenter mTaskPresenter;

	public static final int SHARE_ATTACHMENT = 1;// 附件分享请求码
	public static final int ADD_ATTACHMENT = 10;// 添加附件的请求码

	public static final int DELFILE = 11;
	public static final int SHAREFILE = 12;
	public static final int ADDFILE = 13;
	public static final int GETFILE = 14;
	private static final String TAG = "AttachmentFragment";

	private int mOperateFile;// 用于标记对附件操作的类型
	private Files mFile;// 用于记录需要移除的file文件
	private ArrayList<Files> mTempFiles = new ArrayList<Files>();
	private Dialog mLoadingDialog; // 加载框
	private String mLastFiles;// 记录每次修改之前的附件信息
	// private UserFenPai mTempUserFenpai = new UserFenPai();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mHeaderRightTv = (TextView) getActivity().findViewById(
				R.id.cm_header_tv_right);
		mHeaderRightBtn = (ImageButton) getActivity().findViewById(
				R.id.cm_header_btn_right);

		TaskDetailActivity ta = (TaskDetailActivity) getActivity();
		mUserFenPai = ta.getUserFenPai();
		mTaskPresenter = new TaskPresenter(this);
		initView();

		// 注册广播用于接收
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TaskModifyActivity.ACTION_MODIFY_TASK);
		mReceive = new AttachmentBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter);

		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);
			}
		}, 100);
	}

	private void initView() {
		mFileListAdapter = new AttachmentAdapter(getActivity(), this);
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				mOperateFile = GETFILE;
				String ids = getIds(mUserFenPai.Files);
				mTaskPresenter.getFileListByIds(ids);
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
											 View content, View header) {
				// here check list view, not content.
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						mListView, header);
			}
		});

		// header
		final MaterialHeader header = new MaterialHeader(getActivity());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
		header.setPtrFrameLayout(mPtrFrameLayout);

		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setDurationToCloseHeader(1500);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);

		loadMoreListViewContainer.useDefaultFooter();
		loadMoreListViewContainer.setAutoLoadMore(false);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Files file = mFileListAdapter.getDataList().get(position);
				String name = file.ID + FileUtil.getExtension(file.Name);
				FileUtil.openFile(getActivity(), EMWApplication.filePath + name);
			}
		});
	}

	private String getIds(String files) {
		if (files != null && !files.equals("") && !files.equals("[]")) {
			return files.substring(1, files.length() - 1);
		} else {
			return "";
		}
	}

	@Override
	public void onFirstUserVisible() {
		mHeaderRightTv.setVisibility(View.GONE);
		if (SummarizeFragment.ChargeLimitFlag) {
			// 如果当前用户是负责人的话，就展示可以添加附件的操作
			mHeaderRightBtn.setImageResource(R.drawable.nav_btn_share);
			mHeaderRightBtn.setVisibility(View.VISIBLE);
		}
		mHeaderRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到附件选择界面
				Intent intent = new Intent(getActivity(),
						FileSelectActivity.class);
				if (mUserFenPai.Files != null && mUserFenPai.Files.equals("")) {
					mUserFenPai.Files = "[]";
				}
				intent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS,
						mUserFenPai.Files);
				startActivityForResult(intent, ADD_ATTACHMENT);
			}
		});
	}

	@Override
	public void onUserVisible() {
		mHeaderRightTv.setVisibility(View.GONE);
		if (SummarizeFragment.ChargeLimitFlag) {
			// 如果当前用户是负责人的话，就展示可以添加附件的操作
			mHeaderRightBtn.setImageResource(R.drawable.nav_btn_share);
			mHeaderRightBtn.setVisibility(View.VISIBLE);
		}
		mHeaderRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到附件选择界面
				Intent intent = new Intent(getActivity(),
						FileSelectActivity.class);
				if (mUserFenPai.Files != null && mUserFenPai.Files.equals("")) {
					mUserFenPai.Files = "[]";
				}
				intent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS,
						mUserFenPai.Files);
				startActivityForResult(intent, ADD_ATTACHMENT);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 返回数据，将其添加到列表中
		switch (requestCode) {
		case ADD_ATTACHMENT:
			if (resultCode == Activity.RESULT_OK) {
				mOperateFile = ADDFILE;
				// 获取返回成功数据
				ArrayList<Files> list = (ArrayList<Files>) data
						.getSerializableExtra("select_list");
				mTempFiles.clear();
				mTempFiles.addAll(list);
				mLastFiles = mUserFenPai.Files;// 记录修改之前的文件信息
				mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
				mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
				mLoadingDialog.show();
				// 发起修改任务
				mTaskPresenter.modifyTask(mUserFenPai);
			}
			break;

		case SHARE_ATTACHMENT:
			if (resultCode == Activity.RESULT_OK) {
				mOperateFile = SHAREFILE;
				int fileID = mFileListAdapter.getFileID();
				ArrayList<UserInfo> userRets = (ArrayList<UserInfo>) data
						.getSerializableExtra("select_list");
				// 用户集合
				ArrayList<UserFilePower> upList = new ArrayList<UserFilePower>();
				for (UserInfo UserInfo : userRets) {
					UserFilePower upData = new UserFilePower();
					upData.ID = UserInfo.ID;
					upData.Power = 1;
					upData.Type = 1;
					upList.add(upData);
				}
				mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
				mLoadingDialog.show();
				mTaskPresenter.shareAttachment(upList, fileID);
			}
			break;

		default:
			break;
		}
	}

	class AttachmentBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TaskModifyActivity.ACTION_MODIFY_TASK
					.equals(intent.getAction())) {
				// 接收广播
				mUserFenPai = (UserFenPai) intent
						.getSerializableExtra(TaskModifyActivity.SEND_USERFENPAI);
				mPtrFrameLayout.autoRefresh();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mReceive != null)
			getActivity().unregisterReceiver(mReceive);
		super.onDestroy();
	}

	@Override
	public void modifyTask(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if ("1".equals(s)) {
			switch (mOperateFile) {
			case DELFILE:
				mFileListAdapter.getDataList().remove(mFile);
				mFileListAdapter.notifyDataSetChanged();
				ToastUtil.showToast(getActivity(), getString(R.string.deletefile_success),
						R.drawable.tishi_ico_gougou);
				break;
			case ADDFILE:
				if (mFileListAdapter.getLastPosition() != -1) {
					mFileListAdapter.setLastPosition();
				}
				mFileListAdapter.setDataList(mTempFiles);
				mFileListAdapter.notifyDataSetChanged();
				ToastUtil.showToast(getActivity(), getString(R.string.task_attachment_add_success),
						R.drawable.tishi_ico_gougou);
				break;
			default:
				break;
			}
			Intent intent = new Intent();
			intent.setAction(TaskModifyActivity.ACTION_MODIFY_TASK);
			intent.putExtra(TaskModifyActivity.SEND_USERFENPAI, mUserFenPai);
			getActivity().sendBroadcast(intent);
		} else {

			switch (mOperateFile) {
			case DELFILE:
				mUserFenPai.Files = mLastFiles;
				ToastUtil.showToast(getActivity(), getString(R.string.deletefile_error));
				break;
			case ADDFILE:
				// 还原请求前的附件信息
				mUserFenPai.Files = mLastFiles;
				ToastUtil.showToast(getActivity(), getString(R.string.task_attachment_add_error));
				break;
			default:
				break;
			}
		}
		// 置空标记
		mOperateFile = 0;
	}

	/**
	 * 修改任务附件属性
	 */
	public void deleteFile(Files file, ArrayList<Files> list) {
		mLastFiles = mUserFenPai.Files;
		mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
		mOperateFile = DELFILE;
		mFile = file;
		mTaskPresenter.modifyTask(mUserFenPai);
	}

	@Override
	public void shareAttachmentSuccess(String s) {
		// 任务分享成功的回调
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		if ("1".equals(s)) {
			ToastUtil.showToast(getActivity(), getString(R.string.file_share_success),
					R.drawable.tishi_ico_gougou);
		} else {
			ToastUtil.showToast(getActivity(), getString(R.string.file_share_error));
		}
	}

	@Override
	public void getFileList(List<Files> files) {
		// StringBuilder sb = new StringBuilder();
		// for (Files files2 : files) {
		// Log.d(TAG, "获取的数据。。。"+files2.Name+files2.ID);
		// sb.append(files2.Name+":"+files2.ID+",");
		// }
		// Toast.makeText(getActivity(), sb.toString(), 0).show();
		mNetworkTipsLayout.setVisibility(View.GONE);
		if (files.size() == 0) {
			mBlankLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			mBlankLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		// 设置数据
		mFileListAdapter.setDataList(files);
		mFileListAdapter.notifyDataSetChanged();
		mListView.setAdapter(mFileListAdapter);
	}

	@Override
	public void showFinish() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mPtrFrameLayout.refreshComplete();
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();

		switch (mOperateFile) {
		case DELFILE:
			mUserFenPai.Files = mLastFiles;
			ToastUtil.showToast(getActivity(), getString(R.string.deletefile_error));
			break;
		case ADDFILE:
			mUserFenPai.Files = mLastFiles;
			ToastUtil.showToast(getActivity(), getString(R.string.task_attachment_add_error));
			break;
		case SHAREFILE:
			ToastUtil.showToast(getActivity(), getString(R.string.file_share_error));
			break;
		case GETFILE:
			if (ex instanceof ConnectException) {
				mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
			}
			break;
		default:
			break;
		}
		// 置空标记
		mOperateFile = 0;
	}

	@Override
	public void createTask(String s) {
		// TODO Auto-generated method stub

	}

}
