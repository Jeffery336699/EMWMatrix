package cc.emw.mobile.contact;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.PersonFragment;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.map.ToastUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;

@ContentView(R.layout.activity_modify_user_info)
public class ModifyUserInfoActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderMoreTv;
    @ViewInject(R.id.cm_input_et_content_mail)
    private EditText edMail;
    @ViewInject(R.id.cm_input_et_content_phone)
    private EditText edPhone;

    private UserInfo sUser;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        sUser = (UserInfo) getIntent().getSerializableExtra("userInfo");
        mHeaderTitleTv.setText("修改资料");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderMoreTv.setVisibility(View.VISIBLE);
        mHeaderMoreTv.setText("修改");
        mDialog = createLoadingDialog("正在提交");
        edMail.setEnabled(false);
        edPhone.setEnabled(false);
        edMail.setText(sUser.Email + "");
        edPhone.setText(sUser.Phone + "");
        if (sUser.Email.equals(""))
            edMail.setText(getResources().getString(R.string.nope));
        if (sUser.Phone.equals(""))
            edPhone.setText(getResources().getString(R.string.nope));
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onTopNavClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_tv_right:
                mHeaderMoreTv.setText("提交");
                mHeaderMoreTv.setTag(1);
                if (mHeaderMoreTv.getTag() != null && mHeaderMoreTv.getTag().equals(1)) {
                    if (sUser.Email.equals(""))
                        edMail.setText("");
                    if (sUser.Phone.equals(""))
                        edPhone.setText("");
                    edPhone.setEnabled(true);
                    edMail.setEnabled(true);
                    mHeaderMoreTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!edMail.getText().toString().equals("") && !edPhone.getText().toString().equals(""))
                                upLoadUserInfo();
                            else
                                ToastUtil.show(ModifyUserInfoActivity.this, "邮箱或手机号不能为空");
                        }
                    });
                }
                break;
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }

    private void upLoadUserInfo() {
//        if (edPhone.getText().toString() != null)
        sUser.Phone = edPhone.getText().toString();
//        if (edMail.getText().toString() != null)
        sUser.Email = edMail.getText().toString();
        API.UserAPI.ModifyUserById(sUser, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                if (mDialog != null) mDialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mDialog != null) mDialog.dismiss();
                ToastUtil.show(ModifyUserInfoActivity.this, "修改失败");
            }

            @Override
            public void onSuccess(String result) {
                if (mDialog != null) mDialog.dismiss();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.show(ModifyUserInfoActivity.this, "修改成功");
                    Intent intent = new Intent();
                    intent.setAction(PersonFragment.ACTION_REFRESH_CONTACT_LIST);
                    sendBroadcast(intent);
                    EMWApplication.personMap.put(sUser.ID, sUser);
                    PrefsUtil.readUserInfo().Email = sUser.Email;
                    PrefsUtil.readUserInfo().Phone = sUser.Phone;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("modifyData", sUser);
                    setResult(RESULT_OK, resultIntent);
                    onBackPressed();
                }
            }
        });
    }
}
