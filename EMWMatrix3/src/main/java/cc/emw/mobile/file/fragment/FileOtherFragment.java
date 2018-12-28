package cc.emw.mobile.file.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.melnykov.fab.FloatingActionButton;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.asr.AsrTxtActivity;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.decoration.DividerItemDecoration;
import cc.emw.mobile.contact.inter.FileOtherCodeCallBack;
import cc.emw.mobile.file.FileDetailActivity;
import cc.emw.mobile.file.adapter.FileOtherAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;

/**
 * 知识库Fragment
 * 知识库文档 图片 视频 其他请求界面显示
 * @author shaobo.zhuang
 */
@SuppressLint("ValidFragment")
@ContentView(R.layout.fragment_file_other)
public class FileOtherFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.swipe_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout; // 下拉刷新
    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView; // 文件列表
    @ViewInject(R.id.ll_file_blank)
    private LinearLayout mBlankLayout; // 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; //无网络

    private FileOtherAdapter mFileAdapter; // 文件adapter
    private ArrayList<ApiEntity.Files> mDataList; // 文件列表数据
    private int fileType; //1：文档；2：图片；3：视频；4：其他

    public final static String UPDATE_FILE = "cc.emw.mobile.file.updateOtherFile";
    private UpdateBroadCast updateBroadCast;
    private FloatingActionButton mTvAddChat;

    private FileOtherCodeCallBack fileCode;
    public static FileOtherFragment newInstance(int fileType,FileOtherCodeCallBack fileCode) {
        FileOtherFragment fragment = new FileOtherFragment(fileCode);
        Bundle args = new Bundle();
        args.putInt("file_type", fileType);
        fragment.setArguments(args);
        return fragment;
    }

   public FileOtherFragment (FileOtherCodeCallBack fileCode){
        this.fileCode = fileCode;
   }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fileType = getArguments().getInt("file_type", 1);
        initView();
        mTvAddChat = (FloatingActionButton) view.findViewById(R.id.ic_tv_add_chat);
        mTvAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getActivity(), AsrTxtActivity.class);
                startActivity(scanIntent);
            }
        });
        initBroadcast();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(updateBroadCast != null){
            getActivity().unregisterReceiver(updateBroadCast);
        }

    }

    private void initView() {
        mDataList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mFileAdapter = new FileOtherAdapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mFileAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow, R.color.ptr_red);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOtherFileList("", 3);
            }
        });
    }
    private void initBroadcast(){
        updateBroadCast = new UpdateBroadCast();
        IntentFilter updateIF = new IntentFilter();
        updateIF.addAction(UPDATE_FILE);
        getActivity().registerReceiver(updateBroadCast,updateIF);
    }

    class UpdateBroadCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            int code = fileCode.getFileCurrenNu();
            if (code == fileType) {
                getOtherFileList("", 3);
            }
        }
    }

    @Override
    public void onFirstUserVisible() {
        mSwipeRefreshLayout.setRefreshing(true);
        getOtherFileList("", 3);
    }

    /**
     * 获取相应类型文件列表
     *
     * @param keyword
     */
    private void getOtherFileList(String keyword, int type) {
        Log.e("Const","---FileOtherFragment--getOtherFileList-");
        API.UserData.GetFilesList(keyword, type, fileType, new RequestCallback<ApiEntity.Files>(ApiEntity.Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mSwipeRefreshLayout.setRefreshing(false);
                mBlankLayout.setVisibility(View.GONE);
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.showToast(getActivity(), R.string.filelist_list_error);
                }
            }

            @Override
            public void onParseSuccess(List<ApiEntity.Files> respList) {
                mSwipeRefreshLayout.setRefreshing(false);
                mNetworkTipsLayout.setVisibility(View.GONE);
                mBlankLayout.setVisibility(respList.size() == 0 ? View.VISIBLE : View.GONE);
                mDataList.clear();
                mDataList.addAll(respList);
                mFileAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (v.getTag(R.id.tag_second) != null) {
            ApiEntity.Files noteFile = (ApiEntity.Files) v.getTag(R.id.tag_second);
            Intent intent = new Intent(getActivity(), FileDetailActivity.class);
            intent.putExtra("file_info", noteFile);
            intent.putExtra("start_anim", false);
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            intent.putExtra("click_pos_y", location[1]);
            startActivityForResult(intent, 10);
        }
    }
}
