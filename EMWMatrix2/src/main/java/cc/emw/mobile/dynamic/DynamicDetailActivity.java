package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.dynamic.adapter.DynamicDetailAdapter;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 动态详情
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_dynamic_detail)
public class DynamicDetailActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 顶部条更多按钮
	
	@ViewInject(R.id.load_more_list_view_ptr_frame)
	private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
	@ViewInject(R.id.load_more_list_view_container)
	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
	@ViewInject(R.id.load_more_small_image_list_view)
	private ListView mListView; // 列表
	@ViewInject(R.id.et_dynamicdetail_content)
	private EditText mContentEt; // 内容
	@ViewInject(R.id.btn_dynamicdetail_send)
	private Button mSendBtn; // 发布按钮
	
	private Dialog mLoadingDialog; //加载框
	private DynamicDetailAdapter mDynamicDetailAdapter; // 讨论adapter
	private ArrayList<UserNote> mDataList; // 回复列表数据
	private UserNote note; // 列表传值
	private int noteID; // 
	
	private int page = PAGE_FIRST; // 第几页，第1页为0，每下一页+1再乘页数
	private static final int PAGE_FIRST = 1; //第1页
	private static final int PAGE_COUNT = 10; //页数
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        note = (UserNote) getIntent().getSerializableExtra("user_note");
        noteID = getIntent().getIntExtra("note_id", 0);
        
		initView();
		initMenu();
        
		if (note != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        } else if (noteID != 0) {
        	note = new UserNote();
        	note.ID = noteID;
        	getDetailByID();
        }
    }
    
    private void initView() {
    	mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.dynamicdetail);
		if (note != null && note.UserID == PrefsUtil.readUserInfo().ID) {
			mHeaderMoreBtn.setVisibility(View.VISIBLE);
		} else {
			mHeaderMoreBtn.setVisibility(View.GONE);
		}

        mPtrFrameLayout.setPinContent(false);
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
            	getRevsList();
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

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);

        mDataList = new ArrayList<UserNote>();
        mDynamicDetailAdapter = new DynamicDetailAdapter(this, mDataList, mContentEt);
        if (note != null) {
        	mDynamicDetailAdapter.setTopData(note);
            mDynamicDetailAdapter.getVoteMap().put(note.ID, true); // 在动态列表已处理好投票逻辑，所以无需再次请求处理
        }
		mListView.setAdapter(mDynamicDetailAdapter);
//		mListView.setOnItemClickListener(this);
		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            	page++;
            	getRevsList();
            }
        });
    }

	private RightMenu mMenu;
	private void initMenu() {
		mMenu = new RightMenu(this);
		mMenu.addItem(R.string.dynamicdetail_more_delete, 1);
		mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
			@Override
			public void selected(View view, PopMenu.Item item, int position) {
				switch (item.id) {
					case 1:
						new AlertDialog(DynamicDetailActivity.this).builder().setMsg(getString(R.string.deletedynamic_tips))
								.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										delete();
									}
								}).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
						break;
				}
			}
		});
	}
    
    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				onBackPressed();
				break;
			case R.id.cm_header_btn_right:
				mMenu.showAsDropDown(v);
				break;
    	}
    }
    
    @Event(R.id.btn_dynamicdetail_send)
    private void onSendClick(View v) {
    	String content = mContentEt.getText().toString().trim();
    	if (TextUtils.isEmpty(content)) {
    		ToastUtil.showToast(DynamicDetailActivity.this, R.string.empty_content_tips);
    	} else {
    		reply(content);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	if (mContentEt.getHint().toString().startsWith("回复")) {
    		mContentEt.setTag(null);
    		mContentEt.setHint(R.string.dynamicdetail_edittext_hint);
    	} else {
    		scrollToFinishActivity();
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
    			case ContactSelectActivity.RADIO_SELECT:
    				UserInfo user = (UserInfo) data.getSerializableExtra("select_user");
    	    		Intent intent = new Intent(this, ChatActivity.class);
    				intent.putExtra("SenderID", user.ID);
    				intent.putExtra("type", 1);
    				intent.putExtra("name", user.Name);
    				UserNoteShareTo share = new UserNoteShareTo();
    				share.NoteID = note.ID;
    				share.UserName = note.UserName;
    				share.Content = note.Content;
    				intent.putExtra("share", new Gson().toJson(share));
    				startActivity(intent);
    				break;
    			case 100:
    				GroupInfo group = (GroupInfo) data.getSerializableExtra(GroupSelectActivity.TargetG);
    	    		Intent intent2 = new Intent(this, ChatActivity.class);
    				intent2.putExtra("GroupID", group.ID);
    				intent2.putExtra("type", 2);
    				intent2.putExtra("name", group.Name);
    				UserNoteShareTo shareto = new UserNoteShareTo();
    				shareto.NoteID = note.ID;
    				shareto.UserName = note.UserName;
    				shareto.Content = note.Content;
    				intent2.putExtra("share", new Gson().toJson(shareto));
    				startActivity(intent2);
    				break;
    		}
    	}
    }
    
    /**
     * 根据动态ID获取详情信息
     */
    private void getDetailByID() {
    	API.TalkerAPI.getTalkerById(noteID, new RequestCallback<UserNote>(UserNote.class, false, true) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
//				ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_info_error);
				AlertDialog dialog = new AlertDialog(DynamicDetailActivity.this).builder();
				dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
				dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								scrollToFinishActivity();
							}
				}).show();
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
				mLoadingDialog.show();
			}
			@Override
			public void onParseSuccess(UserNote respInfo) {
				mPtrFrameLayout.refreshComplete();
				note = respInfo;
				mHeaderMoreBtn.setVisibility(note.UserID == PrefsUtil.readUserInfo().ID ? View.VISIBLE : View.GONE);
				mDynamicDetailAdapter.setTopData(note);
				mDynamicDetailAdapter.notifyDataSetChanged();
				getRevsList();
			}
		});
    }
    
    /**
     * 获取回复列表
     */
    private void getRevsList() {
    	API.TalkerAPI.getTalkerRevByTalkerId(note.ID, new RequestCallback<UserNote>(UserNote.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mPtrFrameLayout.refreshComplete();
				ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_commentlist_error);
				if (page > 0) {
					page--;
				}
			}

			@Override
			public void onParseSuccess(List<UserNote> respList) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				mPtrFrameLayout.refreshComplete();
				if (respList.size() < PAGE_COUNT)
					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
				else
					loadMoreListViewContainer.loadMoreFinish(false, true);
				if (page == 1) {
					mDataList.clear();
					mDynamicDetailAdapter.getAllData().clear();
					note.RevCount = respList.size();
				}
				for (UserNote userNote : respList) {
					if (userNote.PID == note.ID) {
						mDataList.add(userNote);
					}
				}
				mDynamicDetailAdapter.setAllData(respList);
				mDynamicDetailAdapter.notifyDataSetChanged();
			}
		});
    }
    
    /**
     * 回复
     * @param content
     */
    private void reply(String content) {
    	HelpUtil.hideSoftInput(DynamicDetailActivity.this, mContentEt);
    	UserNote rev = new UserNote();
    	rev.ID = 0;
    	rev.Content = content;
    	rev.PID = note.ID;
    	rev.TopId = note.ID;
    	if (mContentEt != null && mContentEt.getTag() instanceof UserNote) {
    		UserNote subrev = (UserNote) mContentEt.getTag();
    		rev.PID = subrev.ID;
    		rev.ToUserId = subrev.UserID;
    	}
    	
    	API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_error);
			}

			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
					mPtrFrameLayout.autoRefresh(false);
					mContentEt.setText("");
					mContentEt.setTag(null);
					mContentEt.setHint(R.string.dynamicdetail_edittext_hint);
				} else {
					ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_error);
				}
			}
		});
    }

	private void delete() {
		API.TalkerAPI.DeleteTalker(note.ID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(DynamicDetailActivity.this, R.string.deletedynamic_error);
			}

			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips5);
				mLoadingDialog.show();
			}

			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(DynamicDetailActivity.this, R.string.deletedynamic_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(DynamicFragment.ACTION_REFRESH_HOME_LIST);
					sendBroadcast(intent); // 刷新动态列表
					finish();
				} else {
					ToastUtil.showToast(DynamicDetailActivity.this, R.string.deletedynamic_error);
				}
			}
		});
	}
}
