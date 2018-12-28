package cc.emw.mobile.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.constant.TaskConstant;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.task.fragment.taskdetail.AttachmentFragment;
import cc.emw.mobile.task.fragment.taskdetail.CommentFragment;
import cc.emw.mobile.task.fragment.taskdetail.SummarizeFragment;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.PagerSlidingTabStrip;

/**
 * 任务条目点击进入任务详情界面
 * 
 * @author chengyong.liu
 * 
 */
@ContentView(R.layout.activity_task_detail)
public class TaskDetailActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderRightBtn; // 顶部条右菜单按钮
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderRightTv; // 顶部右标题

	@ViewInject(R.id.tabstrip_task_detail)
	private PagerSlidingTabStrip mPagerSlidingTabStrip;// 导航指示栏
	@ViewInject(R.id.viewpager_task_detail)
	private ViewPager mViewPager;// 详情界面的ViewPager
	private TaskDetailActivityAdapter taskDetailActivityAdapter;
	private Map<Integer, Fragment> mFragmentMap;
	private final String[] TITLES = new String[] {
			TaskConstant.TaskDetail.SUMMARIZE, TaskConstant.TaskDetail.COMMENT,
			TaskConstant.TaskDetail.ATTACHMENT };
	private UserFenPai mUserFenPai;
	private InputMethodManager manager;

	private Dialog mLoadingDialog; // 加载框

	public static final String TASK_DETAIL = "userFenpai";// 用于接收跳转到任务详情的实体
	public static final String TASK_ID = "taskid";// 用于接收跳转到任务详情的任务ID
	private int mTaskID;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragmentMap = new HashMap<Integer, Fragment>();
		mUserFenPai = (UserFenPai) getIntent()
				.getSerializableExtra(TASK_DETAIL);
		mTaskID = getIntent().getIntExtra(TASK_ID, -1);
		// 通过任务ID获取任务实体
		if (mTaskID != -1) {
			mHeaderTitleTv.setText(R.string.task_detail);
			mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
			mLoadingDialog.show();
			getTaskById(mTaskID);
		} else {
			initView();
			initOnclik();
		}

		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// manager = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// }
		// }, 1000);
	}

	/**
	 * 顶部后退键点击事件
	 */
	private void initOnclik() {
		mHeaderBackBtn.setOnClickListener(this);
		mHeaderRightTv.setOnClickListener(this);
	}

	private void initView() {
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.task_detail);
		// 初始化视图进行分流页面
		if (taskDetailActivityAdapter == null) {
			taskDetailActivityAdapter = new TaskDetailActivityAdapter(
					getSupportFragmentManager());
		}
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(taskDetailActivityAdapter);
		mPagerSlidingTabStrip.setTabWeightOne(true);
		mPagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 15, getResources()
						.getDisplayMetrics()));
		mPagerSlidingTabStrip.setTextColor(getResources().getColorStateList(
				R.color.vpi__dark_theme));
		mPagerSlidingTabStrip.setViewPager(mViewPager);
	}

	/**
	 * 初始化任务详情概览界面数据
	 */
	private class TaskDetailActivityAdapter extends FragmentPagerAdapter {
		public TaskDetailActivityAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return getFragment(position);
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

	}

	public Fragment getFragment(int position) {
		Fragment fragment = mFragmentMap.get(position);
		if (fragment == null) {
			switch (position) {
			case 0:
				fragment = new SummarizeFragment();
				break;
			case 1:
				fragment = new CommentFragment();
				break;
			case 2:
				fragment = new AttachmentFragment();
				break;
			default:
				break;
			}
			mFragmentMap.put(position, fragment);
		}

		return fragment;
	}

	/**
	 * 定义获取Activity中任务实例的方法
	 */
	public UserFenPai getUserFenPai() {
		return mUserFenPai;
	}

	/**
	 * 获取任务详情界面的中的任务ID
	 */
	public int getTaskID() {
		return mUserFenPai.ID;
	}

	/**
	 * 获取软键盘管理对象
	 */
	public InputMethodManager getInputMethodManager() {
		return manager;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	/**
	 * 通过任务ID获取任务实体对象
	 * 
	 * @param taskID
	 *            任务ID
	 * @return
	 */
	private void getTaskById(int taskID) {
		API.TalkerAPI.GetTaskByIds("" + taskID,
				new RequestCallback<UserFenPai>(UserFenPai.class) {

					@Override
					public void onCancelled(CancelledException cex) {
					}

					@Override
					public void onError(Throwable ex, boolean isOnCallback) {
						if (mLoadingDialog != null)
							mLoadingDialog.dismiss();
//						ToastUtil.showToast(TaskDetailActivity.this,
//								R.string.task_detail_failed);
						mUserFenPai = new UserFenPai();
						AlertDialog dialog = new AlertDialog(TaskDetailActivity.this).builder();
						dialog.setCancelable(false).setMsg(getString(R.string.task_detail_failed));
						dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								scrollToFinishActivity();
							}
						}).show();
					}

					@Override
					public void onFinished() {
					}

					@Override
					public void onParseSuccess(List<UserFenPai> tasks) {
						if (mLoadingDialog != null)
							mLoadingDialog.dismiss();
						mUserFenPai = tasks.get(0);
						String mainUser = mUserFenPai.MainUser;
						if (mainUser != null && !mainUser.equals("")) {

							initView();
							initOnclik();
						} else {
							AlertDialog dialog = new AlertDialog(TaskDetailActivity.this).builder();
							dialog.setCancelable(false).setMsg(getString(R.string.task_detail_failed));
							dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									scrollToFinishActivity();
								}
							}).show();
						}
					}
				});
	}

	@Override
	public void onBackPressed() {
		CommentFragment fragment = (CommentFragment) mFragmentMap.get(1);
		if(fragment.getmEtReply().getHint().toString().startsWith("回复")){
			PrefsUtil.setChildReply(false);
			fragment.getmEtReply().setHint(R.string.dynamicdetail_edittext_hint);
		}else{
			scrollToFinishActivity();
		}
	}
}
