package cc.emw.mobile.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;


public class DoPayActivity extends BaseActivity {
    private int userNoteId;
    private int messageId;
    private boolean isGroup;
    private ApiEntity.UserSchedule scheduleBean;
    private UserInfo mainUserInfo;
    private ImageView mIvHeanderBtnLeft;//退出
    private TextView mTvChatDoPayMoney;//人均支付金额
    private RelativeLayout mRlChatDoPayTypeWei;//微信支付选择
    private ImageView mIvChatDoPayTypeWeiSelect;//微信支付选中
    private RelativeLayout mRlChatDoPayTypeAli;//支付宝支付选择
    private ImageView mIvChatDoPayTypeAliSelect;//支付宝支付选中
    private RelativeLayout mRlDoPaySubmit;//提交

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_pay);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mIvHeanderBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRlChatDoPayTypeWei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvChatDoPayTypeAliSelect.getVisibility() == View.VISIBLE) {
                    mIvChatDoPayTypeAliSelect.setVisibility(View.GONE);
                }
                mIvChatDoPayTypeWeiSelect.setVisibility(View.VISIBLE);
            }
        });
        mRlChatDoPayTypeAli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvChatDoPayTypeWeiSelect.getVisibility() == View.VISIBLE) {
                    mIvChatDoPayTypeWeiSelect.setVisibility(View.GONE);
                }
                mIvChatDoPayTypeAliSelect.setVisibility(View.VISIBLE);
            }
        });
        mRlDoPaySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvChatDoPayTypeWeiSelect.getVisibility() == View.VISIBLE) {
                    if( mainUserInfo.WxpayRQCode == null || "".equals( mainUserInfo.WxpayRQCode)){
                        ToastUtil.showToast(DoPayActivity.this,"对方未开上传二维码，请选择其他支付方式");
                    }else {
                        loadImage(String.format(Const.DOWN_RQCODE_URL, PrefsUtil.readUserInfo().CompanyCode, mainUserInfo.WxpayRQCode), 1);
                    }

                } else if (mIvChatDoPayTypeAliSelect.getVisibility() == View.VISIBLE) {
                    if( mainUserInfo.AlipayRQCode == null || "".equals( mainUserInfo.AlipayRQCode)){
                        ToastUtil.showToast(DoPayActivity.this,"对方未开上传二维码，请选择其他支付方式");
                    }else {
                        loadImage(String.format(Const.DOWN_RQCODE_URL, PrefsUtil.readUserInfo().CompanyCode, mainUserInfo.AlipayRQCode), 2);
                    }
                }
            }
        });
    }

    private void loadImage(final String iamgePath, final int type) {
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                OutputStream outputStream = null;
                File file=null;
                String path=EMWApplication.filePath;
                try {
                    File dir = new File(EMWApplication.filePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    file = new File(EMWApplication.filePath, System.currentTimeMillis() + ".png");
                    boolean falg = file.createNewFile();
                    if (!falg) {
                        file.delete();
                        file.createNewFile();
                    }
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    Intent SYSTEMINTENT= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri SYTEMURI= Uri.fromFile(new File(path + java.io.File.separator));
                    SYSTEMINTENT.setData(SYTEMURI);
                    sendBroadcast(SYSTEMINTENT);
                    switch (type) {
                        case 1:
                            try {
                                //利用Intent打开微信
                                Uri uri = Uri.parse("weixin://");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                sendBroadcast4Note();
                            } catch (Exception e) {
                                Toast.makeText(DoPayActivity.this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            break;
                        case 2:
                            try {
                                Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                sendBroadcast4Note();
                            } catch (Exception e) {
                                Toast.makeText(DoPayActivity.this, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                            break;
                    }
                } catch (IOException e) {
                    ToastUtil.showToast(DoPayActivity.this,"网络异常,请稍后尝试");
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                            ToastUtil.showToast(DoPayActivity.this,"完成二维码保存:"+file.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                ToastUtil.showToast(DoPayActivity.this,"数据异常，请稍后重试");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        Picasso.with(this).load(iamgePath).into(target);
    }

    private void initView() {
        TextView tvHeanderTvTitle = (TextView) findViewById(R.id.cm_header_tv_title);
        tvHeanderTvTitle.setText("支付");
        mIvHeanderBtnLeft = (ImageView) findViewById(R.id.cm_header_btn_left);
        mTvChatDoPayMoney = (TextView) findViewById(R.id.tv_chat_do_pay_money);
        mTvChatDoPayMoney.setText(scheduleBean.AppointPayVal);
        mRlChatDoPayTypeWei = (RelativeLayout) findViewById(R.id.rl_chat_do_pay_type_wei);
        mIvChatDoPayTypeWeiSelect = (ImageView) findViewById(R.id.iv_chat_do_pay_type_wei_select);
        mRlChatDoPayTypeAli = (RelativeLayout) findViewById(R.id.rl_chat_do_pay_type_ali);
        mIvChatDoPayTypeAliSelect = (ImageView) findViewById(R.id.iv_chat_do_pay_type_ali_select);
        mRlDoPaySubmit = (RelativeLayout) findViewById(R.id.rl_do_pay_submit);
         progressBar= (ProgressBar) findViewById(R.id.pb);
    }
   private ProgressBar progressBar;
    private void initData() {
        Intent intent = getIntent();
        scheduleBean = (ApiEntity.UserSchedule) intent.getSerializableExtra("schedule_bean");
        mainUserInfo = EMWApplication.personMap.get(Integer.parseInt(scheduleBean.MainUser));
        userNoteId = intent.getIntExtra("user_note_id", -1);
        messageId = intent.getIntExtra("mesage_id", -1);
        isGroup = intent.getBooleanExtra("is_group", false);
    }


    public void sendBroadcast4Note() {
        Intent intent = new Intent();
        intent.putExtra("user_note_id", userNoteId);
        intent.putExtra("mesage_id", messageId);
        intent.putExtra("is_group", isGroup);
        sendBroadcast(intent);
    }
}
