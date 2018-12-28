package cc.emw.mobile.main.fragment.talker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.AboutActivity;
import cc.emw.mobile.me.CollectActivity;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.me.MentionMeActivity;
import cc.emw.mobile.me.ReleaseActivity;
import cc.emw.mobile.me.SettingActivity;
import cc.emw.mobile.me.WaitHandleActivity;
import cc.emw.mobile.me.presenter.MyInfoPresenter;
import cc.emw.mobile.me.view.MyInfoView;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;

/**
 * @author zrjt
 * @version 2016-3-9 下午4:44:30
 */
@ContentView(R.layout.fragment_me)
public class MyInfoFragment extends BaseFragment implements MyInfoView {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderMenuBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条右菜单按钮

    @ViewInject(R.id.iv_me_head)
    private ImageView mHeadIv; // 头像
    @ViewInject(R.id.tv_me_name)
    private TextView mNameTv; // 名称

    @ViewInject(R.id.tv_me_releasemun)
    private TextView mReleaseNumTv; // 发布数量
    @ViewInject(R.id.tv_me_attentionnum)
    private TextView mAttentionNumTv; // 关注数量
    @ViewInject(R.id.tv_me_favoritenum)
    private TextView mFavoriteNumTv; // 收藏数量

    @ViewInject(R.id.tv_me_waithandle)
    private TextView mWaitHandleTv; // 未处理工作
    @ViewInject(R.id.ll_me_at)
    private LinearLayout mAtmeLayout; // 相关到我
    @ViewInject(R.id.tv_me_relationnum)
    private TextView mRelationNumTv; // 相关到我数量
    @ViewInject(R.id.tv_me_setting)
    private TextView mSettingTv; // 系统设置
    @ViewInject(R.id.tv_me_about)
    private TextView mAboutTv; // 关于

    private DisplayImageOptions options;
    private MyInfoPresenter mPresenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        mHeaderMenuBtn.setImageResource(R.drawable.nav_btn_menu);
        mHeaderMenuBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("");
        mHeaderMoreBtn.setImageResource(R.drawable.nav_btn_gerenziliao);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        if (PrefsUtil.readUserInfo() != null) {
            int id = PrefsUtil.readUserInfo().ID;
            if (EMWApplication.personMap.get(id) != null) {
                mNameTv.setText(EMWApplication.personMap.get(id).Name);
            }
        }
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                getActivity().sendBroadcast(
                        new Intent(MainActivity.ACTION_REFRESH_MAIN));
                break;
            case R.id.cm_header_btn_right:
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap
                        .get(PrefsUtil.readUserInfo().ID) != null) {
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intent.putExtra("simple_user", simPleUser);
                    startActivityForResult(intent, 44);
                }
                break;
        }
    }

    @Event(value = {R.id.iv_me_head, R.id.ll_me_release, R.id.ll_me_attention, R.id.ll_me_favorite,
            R.id.tv_me_waithandle, R.id.ll_me_at, R.id.tv_me_setting, R.id.tv_me_about})
    private void onItemClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_me_head:
                intent = new Intent(getActivity(), PersonActivity.class);
                if (EMWApplication.personMap != null
                        && EMWApplication.personMap
                        .get(PrefsUtil.readUserInfo().ID) != null) {
                    UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                            .readUserInfo().ID);
                    simPleUser.IsFollow = true;
                    intent.putExtra("simple_user", simPleUser);
                    startActivityForResult(intent, 44);
                }
                break;
            case R.id.ll_me_release: // 发布
                intent = new Intent(getActivity(), ReleaseActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_attention: // 关注
                intent = new Intent(getActivity(), ConcernActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_favorite: // 收藏
                intent = new Intent(getActivity(), CollectActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_me_waithandle: // 未处理工作
                intent = new Intent(getActivity(), WaitHandleActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_at: // 相关到我
                intent = new Intent(getActivity(), MentionMeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_me_setting: // 系统设置
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_me_about: // 关于我
                intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void init() {
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode,
                PrefsUtil.readUserInfo().Image);
        ImageLoader.getInstance().displayImage(uri, mHeadIv, options);

        mPresenter = new MyInfoPresenter(this);
        mPresenter.getFollow();
        mPresenter.getCount();
    }

    @Override
    public void onFirstUserVisible() {
        init();
    }

    @Override
    public void onUserVisible() {
        init();
    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();
    }

    @Override
    public void showFailureInfo(String tips) {
    }

    @Override
    public void showFollowList(List<UserInfo> simpleUsers) {
        // tv_attention.setText(simpleUsers.size() + "");
    }

    @Override
    public void finishRefresh() {

    }

    @Override
    public void getMyInfoCount(List<Integer> lists) {
        if (lists.size() > 0 && lists != null) {
            mReleaseNumTv.setText(lists.get(0) + "");
            mFavoriteNumTv.setText(lists.get(1) + "");
            if (lists.get(2) != 0) {
                mRelationNumTv.setVisibility(View.VISIBLE);
                mRelationNumTv.setText(lists.get(2) + "");
            }
            mAttentionNumTv.setText(lists.get(3) + "");
        }

    }

    @Override
    public void getMyReleaseInfoList(List<UserNote> userNotes) {
        // TODO Auto-generated method stub
    }

}
