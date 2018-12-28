package cc.emw.mobile.me;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.x;

import java.io.File;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.factory.ImageLoadFactory;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ListDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by sunny.du on 2017/7/11.
 * 二维码展示界面
 * 考虑到安全问题，本地不缓存二维码照片
 */
public class PaySettingActivity extends BaseActivity {
    DisplayImageOptions optionesMsgImage = ImageLoadFactory.getChatApdaterImage2();
    private UserInfo user;
    private ImageView mIvHeanderBtnLeft;//返回按钮
    private TextView mIvHeanderTvTitle;//toolbar标题
    private RelativeLayout mRlChatWeiPay;//微信根布局
    private RelativeLayout mRlChatAliPay;//应用宝根布局
    private RelativeLayout mItemRlChatWeiPay;//微信 无二维码的情况下展示
    private RelativeLayout mItemRlChatAliPay;//应用宝 无二维码的情况下展示

    private ImageView mIvChatWeipayRQCode;//二维码
    private ImageView mIvChatAliPayRQCode;//二维码
    private ProgressBar WeiProgressBar;
    private ProgressBar AliProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_setting);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        user = PrefsUtil.readUserInfo();
        initView();
        initEvent();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        mIvHeanderBtnLeft = (ImageView) findViewById(R.id.cm_header_btn_left);
        mIvHeanderTvTitle = (TextView) findViewById(R.id.cm_header_tv_title);
        mIvHeanderTvTitle.setText("我的钱包");
        mRlChatWeiPay = (RelativeLayout) findViewById(R.id.rl_chat_wei_pay);
        mRlChatAliPay = (RelativeLayout) findViewById(R.id.rl_chat_ali_pay);
        mIvChatWeipayRQCode = (ImageView) findViewById(R.id.iv_chat_wei_pay_rq_code);
        mItemRlChatWeiPay = (RelativeLayout) findViewById(R.id.rl_chat_wei_pay_item);
        mItemRlChatAliPay = (RelativeLayout) findViewById(R.id.rl_chat_ali_pay_item);
        mIvChatAliPayRQCode = (ImageView) findViewById(R.id.iv_chat_ali_pay_rq_code);
        WeiProgressBar = (ProgressBar) findViewById(R.id.progress_bar_wei);
        AliProgressBar = (ProgressBar) findViewById(R.id.progress_bar_ali);
        if (!TextUtils.isEmpty(user.WxpayRQCode)) {
            String uri = String.format(Const.DOWN_RQCODE_URL, PrefsUtil.readUserInfo().CompanyCode, subStringBeginAndEnd(user.WxpayRQCode));
            mItemRlChatWeiPay.setVisibility(View.GONE);
            mIvChatWeipayRQCode.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(uri, mIvChatWeipayRQCode, optionesMsgImage);
        }
        if (!TextUtils.isEmpty(user.AlipayRQCode)) {
            String uri = String.format(Const.DOWN_RQCODE_URL, PrefsUtil.readUserInfo().CompanyCode, subStringBeginAndEnd(user.AlipayRQCode));
            mItemRlChatAliPay.setVisibility(View.GONE);
            mIvChatAliPayRQCode.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(uri, mIvChatAliPayRQCode, optionesMsgImage);
        }
    }

    /**
     * 绑定事件
     */
    private void initEvent() {
        mRlChatWeiPay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ListDialog mAddDialog = new ListDialog(PaySettingActivity.this, false);
                mAddDialog.addItem("更改二维码", 0);
                mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                    @Override
                    public void selected(View view, ListDialog.Item item, int position) {
                        switch (item.id) {
                            case 0://直播电话
                                Intent intent;
                                if (Build.VERSION.SDK_INT < 19) {
                                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                } else {
                                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                }
                                startActivityForResult(intent, 1346);
                                break;
                        }
                    }
                });
                mAddDialog.show();
                return false;
            }
        });
        mRlChatAliPay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ListDialog mAddDialog = new ListDialog(PaySettingActivity.this, false);
                mAddDialog.addItem("更改二维码", 0);
                mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                    @Override
                    public void selected(View view, ListDialog.Item item, int position) {
                        switch (item.id) {
                            case 0://直播电话
                                Intent intent;
                                if (Build.VERSION.SDK_INT < 19) {
                                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                } else {
                                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                }
                                startActivityForResult(intent, 1347);
                                break;
                        }
                    }
                });
                mAddDialog.show();
                return false;
            }
        });
        mIvHeanderBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mItemRlChatWeiPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, 1346);
            }
        });
        mItemRlChatAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, 1347);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String pathString = uri2Path(selectedImage, this);
                if(pathString != null) {
                    switch (requestCode) {
                        case 1346://微信二维码返回
                            uploadRQCode(0, pathString);
                            break;
                        case 1347://支付宝二维码返回
                            uploadRQCode(1, pathString);
                            break;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传二维码==========》》》》上传后后台会更新UserInfo的二维码字段。存入链接名
     *
     * @param rqType 0 微信  1支付宝
     * @param path   file路径
     */
    public void uploadRQCode(final int rqType, String path) {
        RequestParam params = new RequestParam(Const.UPLOAD_CQ_CODE_URL);
        params.setMultipart(true);
        final File file = new File(path);
        params.addBodyParameter("RqType", rqType + "");
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (rqType == 0) {
                    WeiProgressBar.setVisibility(View.GONE);
                } else if (rqType == 1) {
                    AliProgressBar.setVisibility(View.GONE);
                }
                ToastUtil.showToast(PaySettingActivity.this, "上传失败,请检查网络环境");
            }

            @Override
            public void onStarted() {
                if (rqType == 0) {
                    WeiProgressBar.setVisibility(View.VISIBLE);
                } else if (rqType == 1) {
                    AliProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSuccess(String result) {
                String substring = subStringBeginAndEnd(result);
                String uri = String.format(Const.DOWN_RQCODE_URL, PrefsUtil.readUserInfo().CompanyCode, substring);
                if (rqType == 0) {
                    user.WxpayRQCode = result;
                    PrefsUtil.saveUserInfo(user);
                    if (mItemRlChatWeiPay.getVisibility() == View.VISIBLE) {
                        mItemRlChatWeiPay.setVisibility(View.GONE);
                        mIvChatWeipayRQCode.setVisibility(View.VISIBLE);
                    }
                    WeiProgressBar.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(uri, mIvChatWeipayRQCode, optionesMsgImage);
                } else if (rqType == 1) {
                    user.AlipayRQCode = result;
                    PrefsUtil.saveUserInfo(user);
                    if (mItemRlChatAliPay.getVisibility() == View.VISIBLE) {
                        mItemRlChatAliPay.setVisibility(View.GONE);
                        mIvChatAliPayRQCode.setVisibility(View.VISIBLE);
                    }
                    AliProgressBar.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(uri, mIvChatAliPayRQCode, optionesMsgImage);
                }
                ToastUtil.showToast(PaySettingActivity.this, "上传成功");
            }
        });
    }

    private String subStringBeginAndEnd(String str) {
        return str.substring(1, str.length() - 1);
    }

    /**
     * 通过uri得到path
     */
    private String uri2Path(Uri uri, Context context) {
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumns, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

}
