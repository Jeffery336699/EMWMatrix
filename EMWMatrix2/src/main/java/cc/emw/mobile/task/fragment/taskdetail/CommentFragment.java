package cc.emw.mobile.task.fragment.taskdetail;

import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.task.adapter.CommentAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskCommentView;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.fragment_task_detail_comment)
public class CommentFragment extends BaseFragment implements ITaskCommentView,
		OnClickListener {
	// private static final String TAG = "CommentFragment";
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.lv_task_comment_reply)
	private ListView mLvReply; // 任务详情评论列表

	@ViewInject(R.id.et_task_comment_reply)
	private EditText mEtReply;// 文本输入框
	@ViewInject(R.id.btn_task_comment_send)
	private Button mBtnSend;// 发送按钮

	@ViewInject(R.id.ll_task_blank)
	private LinearLayout mBlankLayout;// 空视图
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout;// 无网络

	private TextView mHeaderRightTv;
	private ImageButton mHeaderRightBtn;
	private TaskPresenter mTaskPresenter;
	private CommentAdapter mCommentAdapter;

	private Dialog mLoadingDialog; // 加载框

	private static final int SAVE_REPLY = 1;// 保存评论
	private static final int DEL_REPLY_ = 2;// 删除评论
	private static final int GET_REPLY = 3;// 获取评论
	private int mReplyType = SAVE_REPLY;// 默认是添加评论

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTaskPresenter = new TaskPresenter(this);
		mHeaderRightTv = (TextView) getActivity().findViewById(
				R.id.cm_header_tv_right);
		mHeaderRightBtn = (ImageButton) getActivity().findViewById(
				R.id.cm_header_btn_right);
		initView();
		initClick();
	}

	private void initView() {
		mCommentAdapter = new CommentAdapter(getActivity(), mEtReply);
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				// 获取评论数据列表 根据任务ID
				mReplyType = GET_REPLY;
				mTaskPresenter
						.getTaskReply(((TaskDetailActivity) getActivity())
								.getTaskID());
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
											 View content, View header) {
				// here check list view, not content.
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						mLvReply, header);
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
	}

	private void initClick() {
		mBtnSend.setOnClickListener(this);
	}

	@Override
	public void onFirstUserVisible() {
		mHeaderRightBtn.setVisibility(View.GONE);
		mHeaderRightTv.setVisibility(View.GONE);
		mPtrFrameLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh(false);// 获取评论数据
			}
		}, 100);
	}


	@Override
	public void onUserVisible() {
		mHeaderRightBtn.setVisibility(View.GONE);
		mHeaderRightTv.setVisibility(View.GONE);
		// 来回切换的时候,设置为根回复
		PrefsUtil.setChildReply(false);
		mEtReply.requestFocus();

	}

	@Override
	public void onUserInvisible() {
		mEtReply.setHint(getString(R.string.dynamicdetail_edittext_hint));
		HelpUtil.hideSoftInput(getActivity(), mEtReply);
	}

	@Override
	public void onClick(View v) {
		String createTime = getCreateTime();
		int creator = PrefsUtil.readUserInfo().ID;
		String reply = mEtReply.getText().toString().trim();
		// 非空判断
		if (TextUtils.isEmpty(reply)) {
			Toast.makeText(getActivity(),getString(R.string.empty_content_tips), Toast.LENGTH_SHORT).show();
			return;
		}
		HelpUtil.hideSoftInput(getActivity(), mEtReply);

		// 创建评论实体
		TaskReply addReply = new TaskReply();
		// 判断是对任务进行评论 还是通过点击回复进行子评论创建
		if (PrefsUtil.isChildReply()) {
			// 如果是子评论
			// 获取Adapter中评论对象，并进行赋值
			// addReply.setTaskId(taskId);//任务ID在fragment中拿,其他字段在Adapter已经设置
			// addReply.setContent(content)//内容在Fragment中拿,其他字段在Adapter已经设置
			// addReply.setCreator(creator)//内容在Fragment中拿,其他字段在Adapter已经设置
			// addReply.setCreateTime(createTime)//在fragment中设置,其他字段在Adapter已经设置
			// addReply.setReplys(replys)//默认为空;
			TaskReply childReply = mCommentAdapter.getChildReply();
			childReply.TaskId = ((TaskDetailActivity) getActivity())
					.getTaskID();
			childReply.Content = reply;
			childReply.CreateTime = createTime;
			childReply.Creator = creator;
			addReply = childReply;
		} else {
			// 如果不是子评论
			// 添加评论对象
			addReply.TaskId = ((TaskDetailActivity) getActivity()).getTaskID();
			addReply.ID = 0;
			addReply.CreateTime = createTime;
			addReply.Creator = creator;
			addReply.Content = reply;
			addReply.ParentID = 0;
		}
		mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
		mLoadingDialog.show();
		// 发送给服务器
		mReplyType = SAVE_REPLY;
		mTaskPresenter.saveTaskReply(addReply);
	}

	@Override
	public void showFinish() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public void showReply(List<TaskReply> replyList) {
		mNetworkTipsLayout.setVisibility(View.GONE);
		if (replyList.size() == 0) {
			mBlankLayout.setVisibility(View.VISIBLE);
			mLvReply.setVisibility(View.GONE);
		} else {
			mBlankLayout.setVisibility(View.GONE);
			mLvReply.setVisibility(View.VISIBLE);
		}
		mCommentAdapter.setData(replyList);
		mCommentAdapter.notifyDataSetChanged();
		mLvReply.setAdapter(mCommentAdapter);
		// 置空类型
		mReplyType = -1;
	}

	@Override
	public void saveReply(String s) {
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		// 评论成功返回的是评论值的ID
		// 将是否是子评论置为初始值 false
		PrefsUtil.setChildReply(false);
		int rePlyID = Integer.parseInt(s);
		if (rePlyID > 0) {
			ToastUtil.showToast(getActivity(), getString(R.string.dynamicdetail_comment_success),
					R.drawable.tishi_ico_gougou);
			mPtrFrameLayout.autoRefresh(false);
			// 清空EditText内容
			mEtReply.setText("");
			mEtReply.setHint(getString(R.string.dynamicdetail_edittext_hint));
		} else {
			ToastUtil.showToast(getActivity(), getString(R.string.dynamicdetail_comment_error));
		}
		// 置空类型
		mReplyType = -1;
	}

	@Override
	public void delReply(String s) {
		// TODO Auto-generated method stub
		// 置空类型
		mReplyType = -1;
	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {
		mPtrFrameLayout.refreshComplete();
		if (mLoadingDialog != null)
			mLoadingDialog.dismiss();
		switch (mReplyType) {
		case SAVE_REPLY:
			ToastUtil.showToast(getActivity(), getString(R.string.dynamicdetail_comment_error));
			break;
		case DEL_REPLY_:
			ToastUtil.showToast(getActivity(), "sorry,删除评论失败！");
			break;
		case GET_REPLY:
			if (ex instanceof ConnectException) {
				mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
			}
			break;
		default:
			break;
		}
		// 置空类型
		mReplyType = -1;

	}

	private String getCreateTime() {
		Date date = new Date();
//		String pattern = "yyyy/MM/dd HH:mm:ss";
		String pattern =getString(R.string.timeformat2);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.CHINA);
		String createTime = simpleDateFormat.format(date);
		return createTime;
	}

	public EditText getmEtReply(){
		return mEtReply;
	}

}
