package cc.emw.mobile.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatInformationAdapter;
import cc.emw.mobile.chat.adapter.ChatPoiAdapter;
import cc.emw.mobile.chat.view.EmoticonsEditText;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.autoload.PullLoadMoreRecyclerView;

/**
 * Created by sunny.du on 2017/5/24.
 */
@ContentView(R.layout.activity_chat_poi)
public class ChatPOIActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener, PullLoadMoreRecyclerView.PullLoadMoreListener {
    @ViewInject(R.id.iv_chat_poi_find)
    private ImageView mIvChatPoiFind;
    @ViewInject(R.id.eet_chat_poi_text)
    private EmoticonsEditText mEetChatPoiText;
    @ViewInject(R.id.srv_chat_poi_list)
    private PullLoadMoreRecyclerView mSrvChatPoiList;
    @ViewInject(R.id.but_chat_poi_back)
    private ImageButton mButChatPoiBack;
    @ViewInject(R.id.but_chat_poi_next)
    private ImageButton mButChatPoiNext;
    @ViewInject(R.id.chat_poi_find_close)
    private ImageView mChatPoiFindClose;
    @ViewInject(R.id.ll_chat_poi_root)
    private LinearLayout llChatPoiRoot;
    private double longitude;
    private double latitude;
    private String city;
    private String findCondition;
    private ChatPoiAdapter adapter;
    private RecyclerView mRecyclerView;
    private int currentPage;
    private List<PoiItem> mDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initEvent();
    }

    private void initView() {
        initRecyclerView();
        if(TextUtils.isEmpty(findCondition)) {
            doSearchQuery("餐厅", currentPage);
        }else{
            doSearchQuery(findCondition, currentPage);
        }
    }

    private void initRecyclerView() {
        mRecyclerView = mSrvChatPoiList.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mSrvChatPoiList.setRefreshing(true);
        //设置是否可以上拉刷新
        //mSrvChatPoiList.setPushRefreshEnable(false);
        //显示/隐藏下拉刷新
        mSrvChatPoiList.setRefreshing(false);
        //设置是否可以下拉刷新
        mSrvChatPoiList.setPullRefreshEnable(false);
        //设置上拉刷新文字
        mSrvChatPoiList.setFooterViewText("正在加载...");
        //设置上拉刷新文字颜色
        //mSrvChatPoiList.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mSrvChatPoiList.setFooterViewBackgroundColor(R.color.colorBackground);
        mSrvChatPoiList.setLinearLayout();
        mSrvChatPoiList.setOnPullLoadMoreListener(this);
        adapter = new ChatPoiAdapter(this);
        mSrvChatPoiList.setAdapter(adapter);
        adapter.setOnSelectListener(new ChatInformationAdapter.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                mButChatPoiNext.setImageResource(R.drawable.btn_next2);
            }
        });
    }

    private void initEvent() {
        mButChatPoiNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoiItem chatPoiBean = adapter.getChatPoiBean();
                if (chatPoiBean != null) {
                    Intent startIntent = new Intent(ChatPOIActivity.this, ChatDynamicCreateActivity.class);
                    startIntent.putExtra("chat_poi_bean", chatPoiBean);
                    startActivity(startIntent);
                    finish();
                }
            }
        });
        mEetChatPoiText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!("".equals(mEetChatPoiText.getText().toString().trim()))) {
                        doSearchQuery(mEetChatPoiText.getText().toString().trim(), currentPage);
                        KeyBoardUtil.closeKeyboard(ChatPOIActivity.this);
                    }
                    return true;
                }
                return false;
            }
        });
        mEetChatPoiText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEetChatPoiText.setFocusable(true);
                mEetChatPoiText.setFocusableInTouchMode(true);//EditText与软键盘建立连接关系
                return false;
            }
        });
        mButChatPoiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvChatPoiFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!("".equals(mEetChatPoiText.getText().toString().trim()))) {
                    doSearchQuery(mEetChatPoiText.getText().toString().trim(), currentPage);
                    KeyBoardUtil.closeKeyboard(ChatPOIActivity.this);
                }
            }
        });
    }

    private void initData() {
        longitude = getIntent().getDoubleExtra("longitude", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        city = getIntent().getStringExtra("cityCode");
        findCondition = getIntent().getStringExtra("find_condition");
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyWord, int currentPage) {
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", city);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 5000, true));//
        poiSearch.searchPOIAsyn();// 异步搜索

    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        mSrvChatPoiList.setPullLoadMoreCompleted();
        if (currentPage == 0) {
            mDataList.clear();
        }
        if (i == 1000) {
            ArrayList<PoiItem> pois = poiResult.getPois();
            if (pois != null) {
                if (pois.size() != 0) {
                    mDataList.addAll(pois);
                    adapter.setDate(mDataList);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.setDate(mDataList);
                    adapter.notifyDataSetChanged();
                    ToastUtil.showToast(this, "查找不到相关信息");
                }
            }
        } else {
            if (i == 1806)
                Toast.makeText(this, "请检查你的网络", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        doSearchQuery(TextUtils.isEmpty(mEetChatPoiText.getText().toString()) ? "餐厅" : mEetChatPoiText.getText().toString(), currentPage);
    }
}