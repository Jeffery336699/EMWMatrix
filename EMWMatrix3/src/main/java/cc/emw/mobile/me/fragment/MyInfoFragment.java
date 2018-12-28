package cc.emw.mobile.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.AboutActivity;
import cc.emw.mobile.me.CollectActivity;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.me.PersonInfoEditActivity;
import cc.emw.mobile.me.ReleaseActivity;
import cc.emw.mobile.me.SettingActivity;
import cc.emw.mobile.me.TwoStepActivity;
import cc.emw.mobile.me.WaitHandleActivity;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.view.MyInfoView;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import q.rorbin.badgeview.Badge;

/**
 * @author zrjt
 * @version 2016-3-9 下午4:44:30
 */
@ContentView(R.layout.fragment_me)
public class MyInfoFragment extends BaseFragment implements MyInfoView {

    @ViewInject(R.id.cm_header_btn_left)
    private CircleImageView mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_notice)
    private ImageButton mHeaderNoticeIb;
    @ViewInject(R.id.ll_me_info)
    private LinearLayout mLinearLayout;
    @ViewInject(R.id.iv_me_head)
    private ImageView mHeadIv; // 头像
    @ViewInject(R.id.tv_me_name)
    private TextView mNameTv; // 名称
    @ViewInject(R.id.tv_me_releasemun)
    private TextView mReleaseNumTv; // 发布数量
    @ViewInject(R.id.tv_me_favoritenum)
    private TextView mFavoriteNumTv; // 收藏数量
    @ViewInject(R.id.tv_me_fans)
    private TextView mFenSiNum;
    @ViewInject(R.id.tv_me_depart_name)
    private TextView mDepartName;
    //    @ViewInject(R.id.tv_me_concern_num)
//    private TextView mConcernNum;   // 关注的数量
    @ViewInject(R.id.tv_me_setting)
    private TextView mSettingTv; // 系统设置
    @ViewInject(R.id.tv_me_about)
    private TextView mAboutTv; // 关于
    @ViewInject(R.id.iv_me_head_bg_img)
    private ImageView mBackImg;
    private Badge mBadgeView;

    private MyInfoPresenter presenter;
    public static final String ACTION_REFRESH_ME_LIST = "cc.emw.mobile.refresh_me_list";
    private MyBroadcastReceive mReceive;

    class MyBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_ME_LIST.equals(action)) {
                init();
            } else if (MainActivity.ACTION_UNREAD_COUNT.equals(action)) {
                int count = intent.getIntExtra("unread_count", 0);
                mBadgeView.setBadgeNumber(count);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_ME_LIST);
        intentFilter.addAction(MainActivity.ACTION_UNREAD_COUNT);
        mReceive = new MyBroadcastReceive();
        getActivity().registerReceiver(mReceive, intentFilter); // 注册监听
        init();
    }

    @Override
    public void onFirstUserVisible() {
        UserInfo simPleUser = PrefsUtil.readUserInfo();
        String uri = String.format(Const.DOWN_ICON_URL,
                simPleUser.CompanyCode,
                simPleUser.Image);
        mDepartName.setText(TextUtils.isEmpty(simPleUser.Job) ? "暂无职位" : simPleUser.Job);
        if (getActivity() != null) {
            Picasso.with(getActivity())
                    .load(uri)
                    .resize(DisplayUtil.dip2px(getActivity(), 28), DisplayUtil.dip2px(getActivity(), 28))
                    .centerCrop()
                    .config(Bitmap.Config.ALPHA_8)
                    .placeholder(R.drawable.cm_img_head)
                    .error(R.drawable.cm_img_head)
                    .into(mHeaderMenuBtn);
            Picasso.with(getActivity())
                    .load(uri)
                    .resize(DisplayUtil.dip2px(getActivity(), 80), DisplayUtil.dip2px(getActivity(), 80))
                    .centerCrop()
                    .config(Bitmap.Config.ALPHA_8)
                    .placeholder(R.drawable.cm_img_head)
                    .error(R.drawable.cm_img_head)
                    .into(mHeadIv);
        }
    }

    private void init() {
//        String uriBg = String.format(Const.DOWN_ICON_URL,
//                PrefsUtil.readUserInfo().CompanyCode,
//                PrefsUtil.getKeyImgbgurl());
//        Picasso.with(getActivity())
//                .load(uriBg)
//                .resize(mBackImg.getMaxWidth(), DisplayUtil.dip2px(getActivity(), 200))
//                .centerCrop()
//                .config(Bitmap.Config.RGB_565)
//                .placeholder(R.drawable.desert)
//                .error(R.drawable.desert)
//                .into(mBackImg);
        mBadgeView = HelpUtil.bindBadgeTarget(getActivity(), mHeaderNoticeIb);
        if (PrefsUtil.readUserInfo() != null) {
            mNameTv.setText(PrefsUtil.readUserInfo().Name);
        }
        presenter = new MyInfoPresenter(this);
        presenter.getCount();
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_notice})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN));
//                getActivity().onBackPressed();
                break;
            case R.id.cm_header_btn_notice:
                getActivity().sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_MAIN_RIGHT));
                break;
        }
    }

    @Event(value = {R.id.ll_me_info, R.id.iv_me_head, R.id.ll_me_release, R.id.ll_me_favorite, R.id.ll_me_fans, R.id.itemProgress_daiban, R.id.tv_me_sale_list,
            R.id.itemProgress_settings, R.id.itemProgress_about, R.id.itemProgress_twostep})
    private void onItemClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ll_me_info:
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap
                        .get(PrefsUtil.readUserInfo().ID) != null) {
                    Intent intentPersonInfoEdit = new Intent(getActivity(), PersonInfoEditActivity.class);
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intentPersonInfoEdit.putExtra("UserInfo", simPleUser);
                    intentPersonInfoEdit.putExtra("start_anim", false);
                    startActivity(intentPersonInfoEdit);
                }
                break;
            case R.id.iv_me_head:
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID) != null) {
                    Intent intentPersonInfo = new Intent(getActivity(), PersonInfoActivity.class);
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intentPersonInfo.putExtra("UserInfo", simPleUser);
                    intentPersonInfo.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intentPersonInfo.putExtra("click_pos_y", location[1]);
                    startActivity(intentPersonInfo);
                }
                break;
            case R.id.ll_me_release: // 发布
                intent = new Intent(getActivity(), ReleaseActivity.class);
                break;
            case R.id.ll_me_favorite: // 收藏
                intent = new Intent(getActivity(), CollectActivity.class);
                break;
            case R.id.ll_me_fans:
                intent = new Intent(getActivity(), ConcernActivity.class);
                break;
            case R.id.tv_me_sale_list:
                intent = new Intent(getActivity(), MainActivity.class);
                break;
            case R.id.itemProgress_daiban: // 代办工作
                intent = new Intent(getActivity(), WaitHandleActivity.class);
                break;
            case R.id.itemProgress_settings: // 系统设置
                intent = new Intent(getActivity(), SettingActivity.class);
                break;
            case R.id.itemProgress_about: // 关于我
                intent = new Intent(getActivity(), AboutActivity.class);
//                intent = new Intent(getActivity(), NearByFoodActivity.class);
                break;
            case R.id.itemProgress_twostep: // 两步验证
                intent = new Intent(getActivity(), TwoStepActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

    }

    @Override
    public void onUserVisible() {
        presenter.getCount();
    }

    @Override
    public void onUserInvisible() {
    }

    @Override
    public void showFailureInfo(String tips) {
    }

    @Override
    public void showFollowList(List<UserInfo> simpleUsers) {
    }

    @Override
    public void finishRefresh() {
    }

    @Override
    public void getMyInfoCount(List<Integer> lists) {
        if (lists.size() > 0 && lists != null) {
            mReleaseNumTv.setText(lists.get(0) > 0 ? (lists.get(0) + "") : (0 + ""));
            mFavoriteNumTv.setText(lists.get(1) > 0 ? (lists.get(1) + "") : (0 + ""));
            mFenSiNum.setText(lists.get(4) > 0 ? (lists.get(4) + "") : (0 + ""));
//            mConcernNum.setText(lists.get(0) > 0 ? (lists.get(3) + "") : (0 + ""));
//            mReleaseNumTv.setText(lists.get(0) + "");
//            mFavoriteNumTv.setText(lists.get(1) + "");
//            mConcernNum.setText(lists.get(3) + "");
//            if (lists.get(2) != 0) {
//                mRelationNumTv.setVisibility(View.VISIBLE);
//                mRelationNumTv.setText(lists.get(2) + "");
//            }
        }
    }

    @Override
    public void getMyReleaseInfoList(List<UserNote> userNotes) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceive != null)
            getActivity().unregisterReceiver(mReceive);
    }
}
