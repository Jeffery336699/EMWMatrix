package cc.emw.mobile.calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.view.HintPopupWindow;
import cc.emw.mobile.contact.adapter.RelationFileListAdapter;
import cc.emw.mobile.entity.Content;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

@ContentView(R.layout.activity_relations)
public class RelationFileActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 我相关的列表

    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mLlBlank;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络
    @ViewInject(R.id.img_relation_blank_tag)
    private ImageView imgBlankTips;
    @ViewInject(R.id.tv_relation_blank_tag)
    private TextView tvBlankTips;


    private ArrayList<Files> mDataList; // 列表数据
    private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数
    private RelationFileListAdapter adapter; // 文件列表的Adapter
    // private int mType; //0:所有文件；1:我的文件；2:共享给我的文件；3:作废的文件
    private UserInfo userInfo;
    private GroupInfo groupInfo;

    public static final String CALENDER_FILE = "calender_file";// 接收到日程界面的传过来的附件ID
    private String mStringFiles;
    private TextView cm_header_tv_right;
    private int type = 0;//文件类型判断，0全部，1仅选中的发件人，2附件，3视频，4图片

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<Files>();
        if (getIntent().hasExtra("user_info")) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        } else if (getIntent().hasExtra("group_info")) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra(
                    "group_info");
        } else if (getIntent().hasExtra(CALENDER_FILE)) {
            mStringFiles = StringUtils.replaceBlank(getIntent().getStringExtra(CALENDER_FILE));
        }

        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    private void initView() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.relationfile);

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
                page = PAGE_FIRST;
                if (getIntent().hasExtra(CALENDER_FILE)) {
                    getFileByLineStr(mStringFiles);
                } else {
                    if (userInfo != null) {
                        getFileByUserId(userInfo.ID);
                    } else if (groupInfo != null) {
                        if (type == 0) {
                            getGroupRelationFile(groupInfo.ID + "", page, 50);
                        } else if (type == 2) {
                            getGroupRelationFile(groupInfo.ID + "", page, 50);
                        } else if (type == 3) {
                            getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                        } else if (type == 4) {
                            getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                        }
                    }
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

        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);

        adapter = new RelationFileListAdapter(this, mDataList);
        mListView.setAdapter(adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 请求下一页数据
                page++;
                if (type == 0) {
                    getGroupRelationFile(groupInfo.ID + "", page, 50);
                } else if (type == 2) {
                    getGroupRelationFile(groupInfo.ID + "", page, 50);
                } else if (type == 3) {
                    getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                } else if (type == 4) {
                    getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                }
            }
        });
        cm_header_tv_right = (TextView) findViewById(R.id.cm_header_tv_right);
        cm_header_tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> pupItemName = new ArrayList<>();
                List<View.OnClickListener> pupItemEvent = new ArrayList<>();
                //pupItemName.add("发送人");
                pupItemName.add("附件");
                pupItemName.add("视频");
                pupItemName.add("图片");
                /*View.OnClickListener SenderListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        page = 1;
                        //etGroupRelationFileToType(groupInfo.ID + "", page, 10, 1);
                    }
                };
                pupItemEvent.add(SenderListener);*/
                View.OnClickListener EnclosureListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        page = 1;
                        type = 2;
                        getGroupRelationFile(groupInfo.ID + "", page, 50);
                    }
                };
                pupItemEvent.add(EnclosureListener);
                View.OnClickListener videoListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page = 1;
                        type = 3;
                        getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                    }
                };
                pupItemEvent.add(videoListener);
                View.OnClickListener pictureListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page = 1;
                        type = 4;
                        getGroupVideoAndPic(groupInfo.ID + "", page, 50);
                    }
                };
                pupItemEvent.add(pictureListener);
                HintPopupWindow pop = new HintPopupWindow(RelationFileActivity.this, pupItemName, pupItemEvent);
                pop.showPopupWindow(cm_header_tv_right);
                pupItemName.clear();
                pupItemEvent.clear();
            }
        });

        //        refresh();
    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (getIntent().hasExtra(CALENDER_FILE)) {
                    getFileByLineStr(mStringFiles);
                } else {
                    if (userInfo != null) {
                        getFileByUserId(userInfo.ID);
                    } else if (groupInfo != null) {
                        getGroupRelationFile(groupInfo.ID + "", page, 100);
                    }
                }
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                break;
        }
    }

    private void getFileByLineStr(String ids) {
        API.UserData.GetFileListByIds(ids, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                if (page > 0) {
                    page--;
                }
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
                }
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<Files> files) {
                // 空视图展示
                mNetworkTipsLayout.setVisibility(View.GONE);
                mPtrFrameLayout.refreshComplete();
                if (files.size() == 0) {
                    mLlBlank.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                } else {
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mDataList.clear();
                mDataList.addAll(files);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getFileByUserId(int uid) {
        API.UserData.GetFilesListByUserId(uid, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }

            @Override
            public void onParseSuccess(List<Files> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    /**
     * 获取群组相关文件
     */
    private void getGroupRelationFile(String groupId, int pages, int size) {
        API.Message.GetGroupFileMessages(groupId, pages, size, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                mPtrFrameLayout.refreshComplete();
                //无数据
                if (page == PAGE_FIRST && respList.size() == 0) {
                    mLlBlank.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                } else {
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    if (respList.size() < PAGE_COUNT) {
                        loadMoreListViewContainer.loadMoreFinish(false, false);// load more
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }
                    if (page == PAGE_FIRST)
                        mDataList.clear();
                    try {
                        Gson gson = new Gson();
                        for (Files files : respList) {
                            Content content = gson.fromJson(files.Content, Content.class);
                            files.Name = content.Name;
                            files.Url = content.Url;
                            files.ID = content.ID;
                            files.Length = content.Length;
                        }
                    } catch (Exception e) {
                        ToastUtil.showToast(RelationFileActivity.this, "文件解析失败");
                    }
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }
        });
    }

    /**
     * 获取群组相关图片、视频
     */
    private void getGroupVideoAndPic(String groupId, int pages, int size) {
        API.Message.GetGroupVideoAndPicMessages(groupId, pages, size, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {

            @Override
            public void onStarted() {
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                mPtrFrameLayout.refreshComplete();
                //无数据
                if (page == PAGE_FIRST && respList.size() == 0) {
                    mLlBlank.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                } else {
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    if (respList.size() < PAGE_COUNT) {
                        loadMoreListViewContainer.loadMoreFinish(false, false);// load more
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }
                    List<ApiEntity.Files> videoList = new ArrayList<ApiEntity.Files>();
                    List<ApiEntity.Files> imageList = new ArrayList<ApiEntity.Files>();
                    if (page == PAGE_FIRST)
                        mDataList.clear();
                    try {
                        Gson gson = new Gson();
                        for (Files files : respList) {
                            Content content = gson.fromJson(files.Content, Content.class);
                            String name = "";
                            if (content.Name == null || content.Name.equals("")) {
                                name = getExtensionName(content.Url);
                            } else {
                                name = getExtensionName(content.Name);
                            }
                            if (name.equals("avi") || name.equals("3gp") || name.equals("mp3") || name.equals("mp4")) {
                                Files file = new Files();
                                file.Name = content.Name;
                                file.Url = content.Url;
                                file.ID = content.ID;
                                file.Length = content.Length;
                                videoList.add(file);
                            } else if (name.equals("jpg") || name.equals("png") || name.equals("gif") || name.equals("jpeg")) {
                                Files file = new Files();
                                file.Name = content.Name;
                                file.Url = content.Url;
                                file.ID = content.ID;
                                file.Length = content.Length;
                                imageList.add(file);
                            }
                        }
                    } catch (Exception e) {
                        ToastUtil.showToast(RelationFileActivity.this, "文件解析失败");
                    }
                    if (type == 3) {
                        mDataList.addAll(videoList);
                    } else if (type == 4) {
                        mDataList.addAll(imageList);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }
        });
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    private void GetFileByGroupId(int gid) {
        API.TalkerAPI.GetFileListByGroupId(gid, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }

            @Override
            public void onParseSuccess(List<Files> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    /**
     * 将字符串[1，2，3]变成1，2，3
     *
     * @param files
     * @return
     */
    private String getIds(String files) {
        if (files != null && !files.equals("") && !files.equals("[]")) {
            return files.substring(1, files.length() - 1);
        } else {
            return "";
        }
    }
}
