package cc.emw.mobile.dynamic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.calendar.CalendarCreateActivitys;
import cc.emw.mobile.dynamic.DateActivity;
import cc.emw.mobile.dynamic.MailCreateActivity;
import cc.emw.mobile.dynamic.PhoneActivity;
import cc.emw.mobile.dynamic.PlanActivity;
import cc.emw.mobile.dynamic.ServiceCreateActivity;
import cc.emw.mobile.dynamic.ShareActivity2;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.main.fragment.TalkerFragment;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.util.CircularAnim;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.FloatingActionButton;
import cc.emw.mobile.view.FloatingActionMenu;
import cc.emw.mobile.view.SearchEditText;
import q.rorbin.badgeview.Badge;

/**
 * 动态Fragment
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.fragment_dynamic)
public class DynamicFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderMenuBtn; //顶部条左菜单按钮
    @ViewInject(R.id.cm_header_civ_head)
    private CircleImageView mHeaderIconIv;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_rg_switch)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.cm_header_rb_left)
    private RadioButton mNewRb; //最新
    @ViewInject(R.id.cm_header_rb_middle)
    private RadioButton mConcernRb; //关注
    @ViewInject(R.id.cm_header_btn_more)
    private ImageButton mHeaderMoreBtn; //筛选
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; //顶部条右通知按钮
    @ViewInject(R.id.menu_item1)
    private FloatingActionButton mMenuItem1;
    @ViewInject(R.id.menu_item2)
    private FloatingActionButton mMenuItem2;
    @ViewInject(R.id.menu_item3)
    private FloatingActionButton mMenuItem3;
    @ViewInject(R.id.menu_item4)
    private FloatingActionButton mMenuItem4;
    @ViewInject(R.id.menu_item5)
    private FloatingActionButton mMenuItem5;
    @ViewInject(R.id.menu_item6)
    private FloatingActionButton mMenuItem6;
    @ViewInject(R.id.menu_item7)
    private FloatingActionButton mMenuItem7;
    @ViewInject(R.id.menu_item8)
    private FloatingActionButton mMenuItem8;

    @ViewInject(R.id.et_search_keyword)
    private EditText mSearchEt;
    @ViewInject(R.id.viewpager)
    private ViewPager mViewPager;

    @ViewInject(R.id.cm_header_et_search)
    private SearchEditText mHeaderSearchEt;
    @ViewInject(R.id.cm_header_tv_cancel)
    private TextView mSearchCancelTv;
    @ViewInject(R.id.cm_header_btn_notice)
    private ImageButton mHeaderNoticeIb;

    private PageAdapter pageAdapter;
    private SparseArray<Fragment> fragmentMap;
    private String headTitle, headType;

    public static final String ACTION_REFRESH_HOME_LIST = "cc.emw.mobile.refresh_home_list"; // 刷新的action
    public static final String ACTION_SHOW_TYPE_DIALOG = "cc.emw.mobile.show_type_dialog"; // 显示筛选类型弹窗action
    private MyBroadcastReceive mReceive;
    private Badge mBadgeView;
    private DisplayImageOptions options;

    public static FloatingActionMenu mActionMenu;
    private MyReceiver receiver;

    public static DynamicFragment newInstance(String title) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString("header_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentMap = new SparseArray<>();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionMenu = (FloatingActionMenu) view.findViewById(R.id.menu);
        mActionMenu.setClosedOnTouchOutside(true);
        mActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuStart(boolean opening) {  //展开与关闭菜单之前调用
                getActivity().sendBroadcast(new Intent(TalkerFragment.ACTION_FLOATING_MENU).putExtra("opened", opening));
            }

            @Override
            public void onMenuToggle(boolean opened) {
                /*if (opened) {
                    getActivity().sendBroadcast(new Intent(TalkerFragment.ACTION_HIDE_TOP_AND_BOTTOM));
                }*/
            }
        });
        initView();
//        initListDialog();

        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mHeaderNoticeIb);

        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_REFRESH_HOME_LIST);
//        intentFilter.addAction(ACTION_SHOW_TYPE_DIALOG);
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        intentFilter.addAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听

//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem1, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem2, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem3, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem4, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem5, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem6, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem7, this);
//        WaveTouchHelper.bindWaveTouchHelper(mMenuItem8, this);
        //个人用户直接进入新建动态
        if ("PUB".equalsIgnoreCase(PrefsUtil.readUserInfo().CompanyCode)) {
            mActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionMenu.setBackgroundColor(getResources().getColor(R.color.cm_dialog_bg));
                    CircularAnim.fullActivity(getActivity(), v)
                            .colorOrImageRes(R.color.white)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    mActionMenu.setBackgroundColor(Color.TRANSPARENT);
                                    Intent shareIntent = new Intent(getActivity(), ShareActivity2.class);
                                    shareIntent.putExtra("start_anim", false);
                                    startActivity(shareIntent);
                                }
                            });
                }
            });
        }
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_more, R.id.cm_header_btn_right, R.id.cm_header_civ_head, R.id.cm_header_btn_notice, R.id.tv_dynamic_type
            , R.id.menu_item1, R.id.menu_item2, R.id.menu_item3, R.id.menu_item4, R.id.menu_item5, R.id.menu_item6, R.id.menu_item7, R.id.menu_item8})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
            case R.id.cm_header_civ_head:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_more:
            case R.id.tv_dynamic_type:
//                mListDialog.show();
                break;
            case R.id.cm_header_btn_right:
            case R.id.cm_header_btn_notice:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
            case R.id.menu_item1: //新建活动
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent serviceIntent = new Intent(getActivity(), ServiceCreateActivity.class);
                                serviceIntent.putExtra("start_anim", false);
                                getActivity().startActivity(serviceIntent);
                            }
                        });
                break;
            case R.id.menu_item2: //新建邮件
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent mailIntent = new Intent(getActivity(), MailCreateActivity.class);
                                mailIntent.putExtra("start_anim", false);
                                getActivity().startActivity(mailIntent);
                            }
                        });
                break;
            case R.id.menu_item3: //新建电话
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent phoneIntent = new Intent(getActivity(), PhoneActivity.class);
                                phoneIntent.putExtra("start_anim", false);
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.menu_item4: //新建约会
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent dateIntent = new Intent(getActivity(), DateActivity.class);
                                dateIntent.putExtra("start_anim", false);
                                startActivity(dateIntent);
                            }
                        });
                break;
            case R.id.menu_item5: //新建日程
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent scheduleIntent = new Intent(getActivity(), CalendarCreateActivitys.class);
                                scheduleIntent.putExtra("enter_flag", 1);
                                scheduleIntent.putExtra("start_anim", false);
                                startActivity(scheduleIntent);
                            }
                        });
                break;
            case R.id.menu_item6: //新建计划
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent planIntent = new Intent(getActivity(), PlanActivity.class);
                                planIntent.putExtra("start_anim", false);
                                startActivity(planIntent);
                            }
                        });
                break;
            case R.id.menu_item7: //新建任务
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent taskIntent = new Intent(getActivity(), TaskCreateActivity.class);
                                taskIntent.putExtra(TaskCreateActivity.WHO_CAN_SEE, true);
                                taskIntent.putExtra("start_anim", false);
                                startActivity(taskIntent);
                            }
                        });
                break;
            case R.id.menu_item8: //新建动态
                CircularAnim.fullActivity(getActivity(), v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                mActionMenu.close(true);
                                Intent shareIntent = new Intent(getActivity(), ShareActivity2.class);
                                shareIntent.putExtra("start_anim", false);
                                startActivity(shareIntent);
                            }
                        });
                break;
        }
    }

    private void initView() {
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        headTitle = "最新";
        mHeaderTitleTv.setText(headTitle);
        mNewRb.setButtonDrawable(R.drawable.nav_rb_new);
        mNewRb.setChecked(true);
        mConcernRb.setButtonDrawable(R.drawable.nav_rb_concern);
        mConcernRb.setVisibility(View.VISIBLE);
        mRadioGroup.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_sort);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.VISIBLE);

        pageAdapter = new PageAdapter(getFragmentManager());
        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager.addOnPageChangeListener(this);

        /*mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    HelpUtil.hideSoftInput(getActivity(), mSearchEt);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("keyword", "");
                    getActivity().sendBroadcast(intent);
                }
            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    HelpUtil.hideSoftInput(getActivity(), mSearchEt);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("keyword", mSearchEt.getText().toString());
                    getActivity().sendBroadcast(intent);
                    return true;
                }
                return false;
            }
        });*/

        mHeaderSearchEt.setCancelView(mSearchCancelTv);
        mHeaderSearchEt.setNoticeView(mHeaderNoticeIb);
        mHeaderSearchEt.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && start == 0 && before > 0 && count == 0) {
                    HelpUtil.hideSoftInput(getActivity(), mHeaderSearchEt);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("keyword", "");
                    getActivity().sendBroadcast(intent);
                }
            }

            @Override
            public void onSearchClick(View view) {
                Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                intent.putExtra("keyword", mHeaderSearchEt.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        mHeaderIconIv.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeaderIconIv), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);
        receiver = new MyReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh.head");
        getActivity().registerReceiver(receiver, intentFilter);
    }

    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }

    }

    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            params.width = intent.getIntExtra("positionOffset", 0);
            params.height = intent.getIntExtra("positionOffset", 0);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            mHeaderIconIv.setLayoutParams(params);
        }

    }

    @Override
    public void onFirstUserVisible() {
        mViewPager.setAdapter(pageAdapter);
    }

    @Override
    public void onDestroy() {
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive); // 取消监听
        super.onDestroy();
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*if (ACTION_REFRESH_HOME_LIST.equals(action)) {
                mHeaderTitleTv.setText(mViewPager.getCurrentItem() == 0 ? "最新" : "关注");
                mListDialog.setSelectItem(0);
            } else if (ACTION_SHOW_TYPE_DIALOG.equals(action)) {
                mListDialog.show();
            }  else*/
            if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                mBadgeView.setBadgeNumber(count);
            } else if (DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST.equals(action)) {
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
                mHeaderIconIv.setTextBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), PrefsUtil.readUserInfo().Name, 35);
                ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mHeaderIconIv), options, new ImageSize(DisplayUtil.dip2px(getActivity(), 35), DisplayUtil.dip2px(getActivity(), 35)), null, null);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cm_header_rb_left:
                mViewPager.setCurrentItem(0);
                headTitle = "最新";
                mHeaderTitleTv.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));
                break;
            case R.id.cm_header_rb_middle:
                mViewPager.setCurrentItem(1);
                headTitle = "关注";
                mHeaderTitleTv.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        mRadioGroup.check(position == 0 ? R.id.cm_header_rb_left : R.id.cm_header_rb_middle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                fragment = DynamicChildFragment.newInstance(position);
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
//            return 2;
            return 1;
        }

    }

    /*private ListDialog mListDialog;

    private void initListDialog() {
        mListDialog = new ListDialog(getActivity(), true, true, false);
        mListDialog.addItem(R.string.dynamic_more_all, ApiEnum.UserNoteAddTypes.Normal);
        mListDialog.addItem("文件", ApiEnum.UserNoteAddTypes.File);
        mListDialog.addItem("图片", ApiEnum.UserNoteAddTypes.Image);
        mListDialog.addItem("链接", ApiEnum.UserNoteAddTypes.Link);
        mListDialog.addItem("投票", ApiEnum.UserNoteAddTypes.Vote);
        mListDialog.addItem("视频", ApiEnum.UserNoteAddTypes.Video);
        mListDialog.addItem(R.string.dynamic_more_allot, ApiEnum.UserNoteAddTypes.Task);
        mListDialog.addItem(R.string.dynamic_more_plan, ApiEnum.UserNoteAddTypes.Plan);
        mListDialog.addItem(R.string.dynamic_more_schedule, ApiEnum.UserNoteAddTypes.Schedule);
        mListDialog.addItem("约会", ApiEnum.UserNoteAddTypes.Appoint);
        mListDialog.addItem("电话", ApiEnum.UserNoteAddTypes.Phone);
        mListDialog.addItem("邮件", ApiEnum.UserNoteAddTypes.Email);
        mListDialog.addItem("活动", ApiEnum.UserNoteAddTypes.SeviceActive);
        mListDialog.addItem("转发", ApiEnum.UserNoteAddTypes.Share);
        mListDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                if (item.id == ApiEnum.UserNoteAddTypes.Normal) {
                    headType = "";
                    mDynamicTypeTv.setText("显示全部");
                } else {
                    headType = item.text;
                    mDynamicTypeTv.setText(headType);
                }
                mHeaderTitleTv.setText(headTitle + (!TextUtils.isEmpty(headType) ? "·" + headType : ""));

                Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                intent.putExtra("type_id", item.id);
                getActivity().sendBroadcast(intent);
            }
        });
    }*/

}
