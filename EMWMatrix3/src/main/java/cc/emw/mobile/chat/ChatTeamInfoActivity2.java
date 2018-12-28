package cc.emw.mobile.chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.brucetoo.imagebrowse.widget.PhotoView;
import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.adapter.ChatInfoRecyclerViewAdapter;
import cc.emw.mobile.chat.adapter.ChatPlayerAdapter;
import cc.emw.mobile.chat.adapter.ChatTeamUserInfoAdapter;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.chat.utils.NativePicUtil;
import cc.emw.mobile.chat.view.MyCollapsingToolbarLayout;
import cc.emw.mobile.chat.view.MyRecyclerView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.FloatingActionButton;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.MyGridView;
import cc.emw.mobile.view.MyListView;


/**
 * Created by sunny.du on 2016/10/15.
 * 展示群组详细信息的页面
 */

public class ChatTeamInfoActivity2 extends AppCompatActivity {
    private PhotoView mIvGroupImage;
    private IconTextView mTvGroupBg;
    private TextView mTvGroupText;
    private IconTextView mItvGroupEdit;
    private TextView mTvGriyoSaveUser;
    private View mViewLine;
    private FloatingActionButton mItvGroupCamera;
    private GroupInfo groupInfo;    //群信息实体类
    private ImageView mIvGroupNewImage2;
    private Dialog mLoadingDialog; // 加载框
    private boolean isCreateUser;
    private ArrayList<Message> msgList;
    private MyRecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private MyCollapsingToolbarLayout mtoolBarlayout;
    private EditText mEtName;
    private LinearLayoutManager mManager;
    private ScrollView lLMedit;
    private ChatTeamUserInfoAdapter mChatTeamUserInfoAdapter;
    private ChatPlayerAdapter mGrideAdapter;
    private MyGridView mGvChatPlayer;//多媒体展示控件;
    private MyListView mLvGroupUser;//展示群组人员列表
    private View headerView;
    private TextView mTvGroupUserCount;//展示群组人数控件;
    private ArrayList<UserInfo> noteRoles;
    private List<Integer> userids;//群组用户ID保存
    private ArrayList<UserInfo> sUsers;
    private AppBarLayout appBarLayout;
    private int x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /****************/
        AppManager.addActivity(this);
        /****************/
        setContentView(R.layout.activity_chat_team_info2);
        initData();
        initView();
        initToolBar();
        boundView();
        initEvent();
        //TODO 设置标题字段
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
        groupInfo = (GroupInfo) getIntent().getSerializableExtra("groupInfo");
        /**
         * 初始化媒体数据
         */
        msgList = (ArrayList<Message>) getIntent().getSerializableExtra("msg_player");

        if (msgList != null)
            Collections.reverse(msgList);

        /**
         * 初始化数据集合
         */
        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();

        /**
         * 判断是否是管理员
         */
        if (groupInfo.CreateUser == PrefsUtil.readUserInfo().ID) {
            isCreateUser = true;
        } else {
            isCreateUser = false;
        }
    }

    private void initView() {
        mRecyclerView = (MyRecyclerView) this.findViewById(R.id.recyclerView);//滑动列表
        lLMedit = (ScrollView) findViewById(R.id.ll_my_edit_info);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);//title
        mIvGroupImage = (PhotoView) findViewById(R.id.iv_group_new_image);//title群头像
        mIvGroupNewImage2 = (ImageView) findViewById(R.id.iv_group_new_image2);
        mTvGroupBg = (IconTextView) findViewById(R.id.tv_group_bg);//返回
        mTvGroupText = (TextView) findViewById(R.id.tv_group_new_text);//title字体
        mItvGroupEdit = (IconTextView) findViewById(R.id.itv_group_new_edit);//编辑按钮
        mTvGriyoSaveUser = (TextView) findViewById(R.id.tv_group_new_save);//保存
        mViewLine = findViewById(R.id.view_group_new_line);//下标线
        mItvGroupCamera = (FloatingActionButton) findViewById(R.id.fab_photo);//相机 选择群头像图片
        mtoolBarlayout = (MyCollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mEtName = (EditText) findViewById(R.id.tv_group_name_show);
        mGvChatPlayer = (MyGridView) findViewById(R.id.gd_chat_player);
        mLvGroupUser = (MyListView) findViewById(R.id.lv_group_user2);
        headerView = View.inflate(this, R.layout.item_chat_team_add_user, null);
        mTvGroupUserCount = (TextView) findViewById(R.id.tv_group_new_user_count);
        appBarLayout = (AppBarLayout) findViewById(R.id.abl_main);


        mRecyclerView.setLayoutManager(mManager = new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new ChatInfoRecyclerViewAdapter(this, groupInfo, msgList, mLoadingDialog));

        //初始化页面  隐藏编辑页面
        mEtName.setText(groupInfo.Name);
        mTvGroupText.setVisibility(View.GONE);
        mTvGriyoSaveUser.setVisibility(View.GONE);
        mItvGroupCamera.setVisibility(View.GONE);
        mViewLine.setVisibility(View.GONE);

        if (isCreateUser) {
            mItvGroupEdit.setVisibility(View.VISIBLE);
        } else {
            mItvGroupEdit.setVisibility(View.GONE);
        }
        mItvGroupEdit.setTag(0);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下y
                        x = (int) ev.getX();
                        y = (int) ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int new_x = (int) ev.getX();
                        int new_y = (int) ev.getY();

                        //判断有上下滑动的意向（用于字VIew是上下，parent是水平的）
                        int move_x = new_x - x;//x轴滑动的距离
                        int move_y = new_y - y;//y轴滑动的距离
                        if (move_y > (move_x + 10)) {//10的偏移量
                            mTvGroupBg.setTextColor(Color.WHITE);
                            mItvGroupEdit.setTextColor(Color.WHITE);
                        } else if (move_y > -(move_x + 10)) {
                            mTvGroupBg.setTextColor(Color.BLACK);
                            mItvGroupEdit.setTextColor(Color.BLACK);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void boundView() {
        mtoolBarlayout.setTitle(groupInfo.Name);
        DisplayImageOptions optiones = ImageLoadFactory.getChatPlayerOption();//图片load
        DisplayImageOptions optiones2 = ImageLoadFactory.getChatTeamTitleOptiones();//图片load
        mIvGroupImage.disenable();
        mIvGroupImage.setTag(0);
        if (groupInfo.Image != null && !("".equals(groupInfo.Image))) {
            String imageuri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, groupInfo.Image);
            mIvGroupNewImage2.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(imageuri, mIvGroupImage, optiones);
        } else if (groupInfo.BackImageIndex != 0) {
            mIvGroupImage.setVisibility(View.INVISIBLE);
            ImageLoader.getInstance().displayImage(NativePicUtil.getPic(groupInfo.BackImageIndex), mIvGroupNewImage2, optiones2);
        }
    }

    /**
     * 初始化title
     */
    private void initToolBar() {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        //通过CollapsingToolbarLayout修改字体颜色
        mtoolBarlayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mtoolBarlayout.setCollapsedTitleTextColor(Color.BLACK);//设置收缩后Toolbar上字体的颜色
        mToolbar.setNavigationIcon(null);//设置左侧按钮为空
        getSupportActionBar().setHomeButtonEnabled(false);//取消左侧按钮监听事件
    }

    private void initEvent() {
        mTvGroupBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /**
         * 编辑
         */
        mItvGroupEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRecyclerView.setVisibility(View.GONE);
//                lLMedit.setVisibility(View.VISIBLE);
                mItvGroupEdit.setTag(1);
                mtoolBarlayout.setTitle("");
                mEtName.setFocusable(true);
                mEtName.setVisibility(View.VISIBLE);
                mEtName.setFocusableInTouchMode(true);
                mItvGroupEdit.setVisibility(View.GONE);
                mTvGriyoSaveUser.setVisibility(View.VISIBLE);
                mItvGroupCamera.setVisibility(View.VISIBLE);
                mViewLine.setVisibility(View.VISIBLE);
//                if (msgList != null && msgList.size() > 0) {
//                    mGrideAdapter = new ChatPlayerAdapter(ChatTeamInfoActivity2.this, msgList);
//                    mGvChatPlayer.setAdapter(mGrideAdapter);
//                }
//                if (PrefsUtil.readUserInfo().ID == groupInfo.CreateUser)
//                    mLvGroupUser.addHeaderView(headerView);
//                mChatTeamUserInfoAdapter = new ChatTeamUserInfoAdapter(ChatTeamInfoActivity2.this);
//                mLvGroupUser.setAdapter(mChatTeamUserInfoAdapter);
//                getGroupMember(groupInfo.ID);
            }
        });
        /**
         * 保存
         */
        mTvGriyoSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRecyclerView.setVisibility(View.VISIBLE);
//                lLMedit.setVisibility(View.GONE);
                mItvGroupEdit.setTag(0);
                mtoolBarlayout.setTitle(groupInfo.Name);
                mItvGroupEdit.setVisibility(View.VISIBLE);
                mTvGriyoSaveUser.setVisibility(View.GONE);
                mTvGroupText.setText("");
                mViewLine.setVisibility(View.GONE);
                mEtName.setVisibility(View.GONE);
                mItvGroupCamera.setVisibility(View.GONE);
                mIvGroupImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isStartAnim = true; //是否跳转动画

    public void setStartAnim(boolean isStartAnim) {
        this.isStartAnim = isStartAnim;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (isStartAnim) {
            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (isStartAnim) {
            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        }
    }

    @Override
    protected void onDestroy() {
        isStartAnim = true;
        super.onDestroy();
        AppManager.finishActivity(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /**
     * 显示软键盘
     */
    protected void showInputMethod() {
        View view = this.getCurrentFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    protected void hideInputMethod() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 加载对话框
     */
    public Dialog createLoadingDialog(int resId) {
        return createLoadingDialog(getString(resId));
    }

    /**
     * 加载对话框
     */
    public Dialog createLoadingDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    /**
     * 获取群成员信息
     *
     * @param gid
     */
    private void getGroupMember(int gid) {
        API.TalkerAPI.LoadGroupUsersByGid(gid, new RequestCallback<UserInfo>(UserInfo.class) {
            @Override
            public void onParseSuccess(List<UserInfo> respList) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    noteRoles.clear();
                    if (groupInfo.Users != null) {
                        groupInfo.Users.clear();
                        groupInfo.Users.addAll(respList);
                    }
                    noteRoles.addAll(respList);
                    sUsers = noteRoles;
                    if (sUsers.size() > 0) {
                        userids.clear();
                        for (int i = 0; i < sUsers.size(); i++) {
                            userids.add(sUsers.get(i).ID);
                        }
                    }
                    mChatTeamUserInfoAdapter.setData(sUsers);
                    mChatTeamUserInfoAdapter.notifyDataSetChanged();
                    mTvGroupUserCount.setText("成员(" + respList.size() + "人)");
                } else {
                    mTvGroupUserCount.setText("成员(" + 0 + "人)");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
            }

            @Override
            public void onStarted() {
                if (mLoadingDialog != null)
                    mLoadingDialog.show();
            }
        });
    }
}