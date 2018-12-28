package cc.emw.mobile.contact;

import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.PersonFragment;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

import com.zf.iosdialog.widget.AlertDialog;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_person_more_info)

public class PersonMoreInfoActivity extends BaseActivity implements ContactView {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn;

    @ViewInject(R.id.contact_moreinfo_item_name)
    private LinearLayout nameLayout; // 备注名条目

    @ViewInject(R.id.contact_other_name)
    private TextView tvName; // 备注名

    @ViewInject(R.id.btn_click)
    private Button btnCancel; // 取消关注

    private ContactPresenter presenter;
    private static UserInfo sUser;
    private String name;
    private static int bzUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.personmore);
        presenter = new ContactPresenter(this);
        if (getIntent().getSerializableExtra("sUser") != null) {
            sUser = (UserInfo) getIntent().getSerializableExtra("sUser");
            if (getIntent().getStringExtra("userMark") != null) {
                name = getIntent().getStringExtra("userMark");
                tvName.setText(name);
                if (name.equals("")) {
                    tvName.setText("添加");
                }
            }
            bzUserId = sUser.ID;
        }

        if (bzUserId != 0) {
            if (PrefsUtil.readUserInfo().ID == bzUserId) {
                btnCancel.setVisibility(View.GONE);
            } else {
                btnCancel.setVisibility(View.VISIBLE);
            }
        }

        if (getIntent().getStringExtra("bzName") != null) {
            tvName.setText(getIntent().getStringExtra("bzName"));
        }

        isShowBtn();

    }

    private void isShowBtn() {
        if (sUser != null && sUser.IsFollow == false
                || sUser.ID == PrefsUtil.readUserInfo().ID) {
            btnCancel.setVisibility(View.GONE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Event(value = {R.id.contact_moreinfo_item_name, R.id.btn_click,
            R.id.cm_header_btn_left})
    private void onContactMoreInfoClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                Intent intent = new Intent(this, PersonActivity.class);
                intent.putExtra("sUser", sUser);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.contact_moreinfo_item_name:
                Intent intent1 = new Intent(this, BzmActivity.class);
                intent1.putExtra("userId", bzUserId);
                startActivity(intent1);
                finish();
                break;
            case R.id.btn_click:
                String msg = getString(R.string.cancelfollow_tips);
                new AlertDialog(PersonMoreInfoActivity.this).builder().setMsg(msg)
                        .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // if (sUser.isIsFollow() == true) {
                                // cPresenter.delFollow(sUser);
                                // } else {
                                // cPresenter.addFollow(sUser);
                                // }
                                presenter.delFollow(sUser);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void disProgressDialog() {
        // TODO Auto-generated method stub
    }

    @Override
    public void refreshComplete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showProgressDialog() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showFollowResult(String responseInfo) {
        if (responseInfo.toString() != null && "1".equals(responseInfo)) {
            if (sUser.IsFollow) {
                ToastUtil.showToast(this, R.string.person_cancelfollow_success);
                sUser.IsFollow = false;
                isShowBtn();
                Intent intent = new Intent(
                        PersonFragment.ACTION_REFRESH_CONTACT_LIST);
                sendBroadcast(intent); // 刷新人员列表
            } else {
                ToastUtil.showToast(this, R.string.person_follow_success);
                sUser.IsFollow = true;
                isShowBtn();
            }
            Intent intent = new Intent();
            intent.putExtra("sUser", sUser);
            setResult(RESULT_OK, intent);
            finish();
        } else if (responseInfo.toString() != null && "0".equals(responseInfo)) {
            if (sUser.IsFollow) {
                ToastUtil.showToast(this, R.string.person_cancelfollow_error);
            } else {
                ToastUtil.showToast(this, R.string.person_follow_error);
            }
        } else {
            Toast.makeText(this, "超时！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showTipDialog(String tips) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showUserInfo(List<UserInfo> simpleUsers) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showGroupInfo(List<GroupInfo> groupInfos) {
        // TODO Auto-generated method stub
    }
}
