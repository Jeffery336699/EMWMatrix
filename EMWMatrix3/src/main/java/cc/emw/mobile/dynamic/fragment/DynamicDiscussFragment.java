package cc.emw.mobile.dynamic.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.ShowPhotoActivity;
import cc.emw.mobile.chat.model.bean.EmojiBean;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.dynamic.adapter.DynamicDiscussAdapter2;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.contral.CommentContral;
import cc.emw.mobile.me.imagepicker.ImagePicker;
import cc.emw.mobile.me.imagepicker.bean.ImageItem;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.project.fragment.TimeTrackingWebFragment;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.DiscussFileLayout;
import cc.emw.mobile.view.DiscussImgLayout;
import cc.emw.mobile.view.DynamicEmoticonsKeyBoard;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * 动态·讨论Fragment
 * 
 * @author shaobo.zhuang
 * 
 */
@ContentView(R.layout.fragment_dynamic_discuss)
public class DynamicDiscussFragment extends BaseFragment {

	private static final String TAG = DynamicDiscussFragment.class.getSimpleName();

	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 近期动态列表
	@ViewInject(R.id.ll_dynamic_blank)
	private LinearLayout mBlankLayout;
	@ViewInject(R.id.ll_network_tips)
	private LinearLayout mNetworkTipsLayout; //无网络

//	@ViewInject(R.id.et_dynamicbottom_content)
	public static EditText mContentEt; // 内容
	@ViewInject(R.id.ek_bar)
	private DynamicEmoticonsKeyBoard ekBar;
	private Dialog mLoadingDialog; //加载框
	private DynamicDiscussAdapter2 mDynamicDiscussAdapter; // 讨论adapter
	private ArrayList<ApiEntity.UserNote> mDataList; // 回复列表数据
	private int enterFlag;
	private UserNote note; //列表传值
	private ApiEntity.UserNote revNote; //列表传值

	private CommentContral mCommentContral;

	public static final String ACTION_REFRESH_DISCUSS_LIST = "cc.emw.mobile.refresh_discuss_list"; // 刷新的action
	private MyBroadcastReceive mReceive;
	private DiscussImgLayout imgLayout; //发图片区域
	private DiscussFileLayout fileLayout; //发文件区域

	public static DynamicDiscussFragment newInstance(UserNote un, ApiEntity.UserNote revNote, int enterFlag) {
		DynamicDiscussFragment fragment = new DynamicDiscussFragment();
		Bundle args = new Bundle();
		args.putSerializable("user_note", un);
		args.putSerializable("rev_note", revNote);
		args.putInt("enter_flag", enterFlag);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enterFlag = getArguments().getInt("enter_flag", 0);
		note = (UserNote)getArguments().getSerializable("user_note");
		revNote = (ApiEntity.UserNote)getArguments().getSerializable("rev_note");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_REFRESH_DISCUSS_LIST);
		mReceive = new MyBroadcastReceive();
		getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
	}

	@Override
	public void onResume() {
		super.onResume();
		if (imgLayout != null && imgLayout.getPreSurfaceView() != null) {
			imgLayout.getPreSurfaceView().openCamera();
		}
	}

	class MyBroadcastReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_REFRESH_DISCUSS_LIST.equals(intent.getAction())) {
				if (intent.hasExtra("img_list")) { //选择的图片列表
					ArrayList<ApiEntity.Files> imgList = (ArrayList<ApiEntity.Files>) intent.getSerializableExtra("img_list");
					imgLayout.setData(imgList);
				} else if (intent.hasExtra("file_list")) { //选择的文件列表
					ArrayList<ApiEntity.Files> imgList = (ArrayList<ApiEntity.Files>) intent.getSerializableExtra("file_list");
					fileLayout.setData(imgList);
				} else if (intent.hasExtra("img_path")) {
					String path = intent.getStringExtra("img_path");
					uploadImage(path);
				}
			}
		}
	}

	@Event(R.id.ll_network_tips)
	private void onNetworkTipsClick(View v) {
		mPtrFrameLayout.autoRefresh(false);
	}

	/*@Event(R.id.itv_dynamicbottom_send)
	private void onSendClick(View v) {
		String content = mContentEt.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			ToastUtil.showToast(getActivity(), R.string.empty_content_tips);
		} else {
			reply(content);
		}
	}*/

	private void initView() {
		mPtrFrameLayout.setEnabled(false);
		mPtrFrameLayout.setPinContent(false);
		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				// here check list view, not content.
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						mListView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				// 请求第一页数据

			}
		});

		// header
		final MaterialHeader header = new MaterialHeader(getActivity());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
		header.setPtrFrameLayout(mPtrFrameLayout);

		mPtrFrameLayout.setLoadingMinTime(1000);
		mPtrFrameLayout.setDurationToCloseHeader(1500);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);

		initEmoticonsKeyBoardBar();
		// binding view and data
		mDataList = new ArrayList<ApiEntity.UserNote>();
		for (ApiEntity.UserNote userNote : note.RevInfo) {
			if (userNote.PID == note.ID) {
				mDataList.add(userNote);
			}
		}
		mDynamicDiscussAdapter = new DynamicDiscussAdapter2(getActivity(), mDataList, mContentEt, enterFlag);
		mDynamicDiscussAdapter.setAllData(note.RevInfo);
		mListView.setAdapter(mDynamicDiscussAdapter);

		/*View commentLayout = getActivity().findViewById(R.id.ll_dynamicbottom_discuss);
		EditText commentContentEt = (EditText) getActivity().findViewById(R.id.et_dynamicbottom_content);
		View commentSendItv = getActivity().findViewById(R.id.itv_dynamicbottom_send);
		mCommentContral = new CommentContral(getActivity(), commentLayout, commentContentEt, commentSendItv);
		mCommentContral.setListView(mListView);
		mHomeAdapter.setCommentContral(mCommentContral);

		setViewTreeObserver();*/
	}

	private void initEmoticonsKeyBoardBar() {
		mContentEt = ekBar.getEtChat();
		if (revNote != null) {
			mContentEt.setTag(revNote);
			mContentEt.setHint("回复 " + (revNote.UserIdInfo != null ? revNote.UserIdInfo.Name : ""));
		}
		ekBar.getEtChat().requestFocusFromTouch();
		ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
		ekBar.setAdapter(ChatUtils.getCommonAdapter(getActivity(), emoticonClickListener));
//		ekBar.addOnFuncKeyBoardListener(this);
		imgLayout = new DiscussImgLayout(getActivity());
		ekBar.addFuncView(DynamicEmoticonsKeyBoard.FUNC_TYPE_IMG, imgLayout);
		fileLayout = new DiscussFileLayout(getActivity());
		ekBar.addFuncView(DynamicEmoticonsKeyBoard.FUNC_TYPE_FILE, fileLayout);

		ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
//				scrollToBottom();
			}
		});
		ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = mContentEt.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					ToastUtil.showToast(getActivity(), R.string.empty_content_tips);
				} else {
					reply(content);
				}
			}
		});
	}
	EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
		@Override
		public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

			if (isDelBtn) {
				ChatUtils.delClick(ekBar.getEtChat());
			} else {
				if (o == null) {
					return;
				}
				if (actionType == 2) {
					if (o instanceof EmoticonEntity) {
//						OnSendImage(((EmoticonEntity) o).getIconUri());
					}
				} else {
					String content = null;
					if (o instanceof EmojiBean) {
						content = ((EmojiBean) o).emoji;
					} else if (o instanceof EmoticonEntity) {
						content = ((EmoticonEntity) o).getContent();
					}
					if (TextUtils.isEmpty(content)) {
						return;
					}
					int index = ekBar.getEtChat().getSelectionStart();
					Editable editable = ekBar.getEtChat().getText();
					editable.insert(index, content);
				}
			}
		}
	};

	private void setViewTreeObserver() {

		final ViewTreeObserver swipeRefreshLayoutVTO = mListView.getViewTreeObserver();
		swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				mListView.getWindowVisibleDisplayFrame(r);
				int screenH = mListView.getRootView().getHeight();
				int keyH = screenH - (r.bottom - r.top);
				if (keyH == EMWApplication.mKeyBoardH) {//有变化时才处理，否则会陷入死循环
					return;
				}
				EMWApplication.mKeyBoardH = keyH;
				mCommentContral.setScreenHeight(screenH);//应用屏幕的高度
				if (mCommentContral != null) {
					mCommentContral.handleListViewScroll();
				}
			}
		});
	}

	@Override
	public void onFirstUserVisible() {
		/*if (mPtrFrameLayout != null) {
			mPtrFrameLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPtrFrameLayout.autoRefresh(false);
				}
			}, 100);
		}*/
	}

	@Override
	public void onDestroy() {
		if (mReceive != null)
			getActivity().unregisterReceiver(mReceive); // 取消监听
		super.onDestroy();
	}

	/**
	 * 回复
	 * @param content
	 */
	private void reply(String content) {
		HelpUtil.hideSoftInput(getActivity(), mContentEt);
		final UserNote rev = new UserNote();
		rev.ID = 0;
		rev.Content = content;
		rev.PID = note.ID;
		rev.TopId = note.ID;
		if (mContentEt != null && mContentEt.getTag() instanceof ApiEntity.UserNote) { //是否回复对方
			ApiEntity.UserNote subrev = (ApiEntity.UserNote) mContentEt.getTag();
			rev.PID = subrev.ToUserId > 0 ? subrev.PID : subrev.ID;
			rev.ToUserId = subrev.UserID;

			rev.ToUserIdInfo = new ApiEntity.TalkerUserInfo(); //回复的对方信息
			rev.ToUserIdInfo.ID = subrev.UserID;
			rev.ToUserIdInfo.Name = subrev.UserIdInfo != null ? subrev.UserIdInfo.Name : "";
		}
		if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_IMG) { //带图片
			rev.AddType = ApiEnum.UserNoteAddTypes.Image;
			rev.AddProperty = new Gson().toJson(imgLayout.getData());
		} else if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_FILE) { //带文件
			rev.AddType = ApiEnum.UserNoteAddTypes.File;
			rev.AddProperty = new Gson().toJson(fileLayout.getData());
		}

		API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Logger.d(TAG, "onError:"+(ex != null ? ex.toString():""));
				if (mLoadingDialog != null && getActivity() != null &&
						!getActivity().isFinishing())
					mLoadingDialog.dismiss();
				ToastUtil.showToast(getActivity(), R.string.dynamicdetail_comment_error);
			}

			@Override
			public void onStarted() {
				if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
					mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
					mLoadingDialog.show();
				}
			}

			@Override
			public void onSuccess(String result) {
				Logger.d(TAG, "onSuccess:"+result);
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(getActivity(), R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
					if (enterFlag == 1) {
						intent = new Intent(TimeTrackingWebFragment.ACTION_TIME_DYNAMICDISCUSS);
					}
					intent.putExtra("note_id", note.ID);
					rev.ID = Integer.valueOf(result);
					rev.UserID = PrefsUtil.readUserInfo().ID;
					rev.UserIdInfo = HelpUtil.userInfo2TalkerUserInfo(PrefsUtil.readUserInfo());
					rev.CreateTime = StringUtils.getCurTimeStr();
					Gson gson = new Gson();
					intent.putExtra("rev_note", gson.fromJson(gson.toJson(rev), ApiEntity.UserNote.class));
					getActivity().sendBroadcast(intent); //刷新Talker列表
					mDataList.clear();
					mDynamicDiscussAdapter.getAllData().add(rev);
					for (ApiEntity.UserNote userNote : mDynamicDiscussAdapter.getAllData()) {
						if (userNote.PID == note.ID) { //找出第一级数据
							mDataList.add(userNote);
						}
					}
					mDynamicDiscussAdapter.setData(mDataList);
					mDynamicDiscussAdapter.notifyDataSetChanged();

					mContentEt.setText("");
					mContentEt.setTag(null);
					mContentEt.setHint(R.string.dynamicdetail_edittext_hint);
					ekBar.reset();
					imgLayout.clearData();
					fileLayout.clearData();
				} else {
					ToastUtil.showToast(getActivity(), R.string.dynamicdetail_comment_error);
				}
			}
		});
	}

	private void uploadImage(String path) {
		RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
		params.setMultipart(true);
		final File file = new File(path);
		params.addBodyParameter(file.getName(), file);
		x.http().post(params, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null && getActivity() != null &&
						!getActivity().isFinishing())
					mLoadingDialog.dismiss();
				ToastUtil.showToast(getActivity(), "上传图片失败！");
			}

			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}

			@Override
			public void onParseSuccess(List<String> result) {
				// 获取服务器返回的图片的url发送消息
				if (result != null && result.size() > 0) {
					UserNote.UserNoteFile image = new UserNote.UserNoteFile();
					image.FileId = 0;
					image.Url = result.get(0);
					image.FileName = file.getName();
					image.Length = file.length();
					image.CreateUser = PrefsUtil.readUserInfo().ID;
					image.CompanyCode = PrefsUtil.readUserInfo().CompanyCode;
					imgLayout.getData().clear();
					imgLayout.getData().add(image);
					reply("");
				}
			}
		});
	}

}
