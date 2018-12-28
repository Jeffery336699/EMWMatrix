package cc.emw.mobile.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipy.sdk.pay.demo.H5PayDemoActivity;
import com.alipy.sdk.pay.demo.Key;
import com.alipy.sdk.pay.demo.PayResult;
import com.alipy.sdk.pay.demo.SignUtils;
import com.google.gson.Gson;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.Util;
import net.sourceforge.simcpux.WXPayActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.AlipayInfo;
import cc.emw.mobile.entity.SaleList;
import cc.emw.mobile.entity.SaleOrder;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.net.RequestTestParam;
import cc.emw.mobile.sale.RetailOrderActivity;
import cc.emw.mobile.util.MathExtend;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.retail_pay)
public class WXPayEntryActivity extends BaseActivity implements
        IWXAPIEventHandler {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.retailpay_tv_orderno)
    private TextView mOrderNoTv; // 订单号
    @ViewInject(R.id.retailpay_tv_ordertotal)
    private TextView mOrderTotalTv; // 订单金额
    @ViewInject(R.id.retailpay_btn_cash)
    private Button mCashBtn; // 现金支付

    @ViewInject(R.id.retailpay_tv_errormsg)
    private TextView mErrorMsgTv;

    private ArrayList<SaleList> mDataList; // 商品列表
    private Dialog mLoadingDialog; // 加载框

    private String orderNo, orderTotal; // 订单号、总金额

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "01";

    // 支付宝
    // 商户PID
    public static final String PARTNER = Key.PARTNER;
    // 商户收款账号
    public static final String SELLER = Key.SELLER;
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = Key.RSA_PRIVATE;
    // 支付宝公钥
    public static final String RSA_PUBLIC = Key.RSA_PUBLIC;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    // we chat
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderNo = getIntent().getStringExtra("order_no");
        orderTotal = getIntent().getStringExtra("order_total");
        mDataList = new ArrayList<SaleList>();
        initView();
        getSaleList(orderNo);
    }

    private void initView() {
        // we chat
        api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1", false);
        api.handleIntent(getIntent(), this);

        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("支付");
        mHeaderMoreBtn.setImageResource(R.drawable.more_top_btn);
        mHeaderMoreBtn.setVisibility(View.GONE);

        mOrderNoTv.setText(orderNo);
        mOrderTotalTv.setText(MathExtend.round(orderTotal, 2));
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                // HelpUtil.hideSoftInput(this, mContentEt);
                finish();
                break;
            case R.id.cm_header_tv_right:

                break;
        }
    }

    @Event(value = {R.id.retailpay_btn_cash, R.id.retailpay_btn_alipay,
            R.id.retailpay_btn_weixin, R.id.retailpay_btn_unionpay})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.retailpay_btn_cash: // 现金支付
                SaleOrder order = new SaleOrder();
                order.OrderNo = orderNo;
                order.State = 1;
                order.PayType = 1;
                SimpleDateFormat format = new SimpleDateFormat(
                        getString(R.string.timeformat4), Locale.getDefault());
                order.MoneyTime = format.format(new Date());
                updateSaleOrder(order);
                break;
            case R.id.retailpay_btn_alipay: // 支付宝支付
                // Intent intent = new Intent(this, ScannerPayActivity.class);
                // startActivityForResult(intent, 1);
                pay();
                break;
            case R.id.retailpay_btn_weixin: // 微信支付
                wxpay("123456789"); //正式
//                WXPay();  //测试
                break;
            case R.id.retailpay_btn_unionpay: // 银联支付
                if (UPPayAssistEx.checkInstalled(this)) {
                    String txnAmt = String.valueOf((int) (0.01 * 100));
                    unionpay(txnAmt);
                } else {
                    // http://mobile.unionpay.com/getclient?platform=android&type=securepayplugin
                    UPPayAssistEx.installUPPayPlugin(this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    String authCode = data.getStringExtra("auth_code");
                    alipay1(authCode);
                    break;
                case 10: // 处理银联手机支付控件返回的支付结果
                    if (data == null) {
                        return;
                    }
                    String msg = "";
                /*
                 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
				 */
                    String str = data.getExtras().getString("pay_result");
                    if (str.equalsIgnoreCase("success")) {
                        // 支付成功后，extra中如果存在result_data，取出校验
                        // result_data结构见c）result_data参数说明
                        if (data.hasExtra("result_data")) {
                            String result = data.getExtras().getString(
                                    "result_data");
                            try {
                                JSONObject resultJson = new JSONObject(result);
                                String sign = resultJson.getString("sign");
                                String dataOrg = resultJson.getString("data");
                                // 验签证书同后台验签证书
                                // 此处的verify，商户需送去商户后台做验签
                                // boolean ret = RSAUtil.verify(dataOrg, sign,
                                // mMode);
                                boolean ret = true;
                                if (ret) {
                                    // 验证通过后，显示支付结果
                                    msg = "支付成功！";
                                } else {
                                    // 验证不通过后的处理
                                    // 建议通过商户后台查询支付结果
                                    msg = "支付失败！";
                                }
                            } catch (JSONException e) {
                            }
                        } else {
                            // 未收到签名信息
                            // 建议通过商户后台查询支付结果
                            msg = "支付成功！";
                        }
                        SaleOrder order = new SaleOrder();
                        order.OrderNo = orderNo;
                        order.State = 1;
                        order.PayType = 4;
                        SimpleDateFormat format = new SimpleDateFormat(
                                getString(R.string.timeformat4), Locale.getDefault());
                        order.MoneyTime = format.format(new Date());
                        updateSaleOrder(order);
                    } else if (str.equalsIgnoreCase("fail")) {
                        msg = "支付失败！";
                    } else if (str.equalsIgnoreCase("cancel")) {
                        msg = "用户取消了支付";
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 通过订单号获取商品清单
     */
    private void getSaleList(String orderNo) {
        RequestTestParam params = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=GetSaleList");
        params.setStringParams(orderNo);
        x.http().post(params, new RequestCallback<SaleList>(SaleList.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }

            @Override
            public void onParseSuccess(List<SaleList> respList) {
                mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    mDataList.addAll(respList);
                }
            }
        });
    }

    /**
     * 更新订单状态
     */
    private void updateSaleOrder(SaleOrder order) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=UpdateSaleOrder");
        param.setStringParams(order);
        x.http().post(param, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String responseInfo) {
                mLoadingDialog.dismiss();
                if (responseInfo != null
                        && !"0".equals(responseInfo)) {
                    Toast.makeText(WXPayEntryActivity.this, "订单支付成功",
                            Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent(
                            RetailOrderActivity.ACTION_REFRESH_RETAIL_ORDER)); // 刷新
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(WXPayEntryActivity.this, "订单支付失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }
        });
    }

    private void alipay1(String authCode) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Server.NewPayServer&m=ZFBRequest");
        param.setStringParams(authCode);
        x.http().post(param, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String responseInfo) {
                mLoadingDialog.dismiss();
                Gson gson = new Gson();
                String jsonStr = gson.fromJson(responseInfo, String.class);
                AlipayInfo alipayInfo = gson.fromJson(jsonStr, AlipayInfo.class);
                if (alipayInfo != null) {
                    AlipayInfo.PayResp payResp = alipayInfo.alipay_trade_pay_response;
                    if (payResp != null && "10000".equals(payResp.code)) {
                        SaleOrder order = new SaleOrder();
                        order.OrderNo = orderNo;
                        order.State = 1;
                        order.PayType = 2;
                        SimpleDateFormat format = new SimpleDateFormat(
                                getString(R.string.timeformat4), Locale
                                .getDefault());
                        order.MoneyTime = format.format(new Date());
                        updateSaleOrder(order);
                    } else {
                        Toast.makeText(WXPayEntryActivity.this,
                                "订单支付失败", Toast.LENGTH_SHORT).show();
                        mErrorMsgTv.setText("[" + payResp.code + "] "
                                + payResp.sub_msg);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }
        });
    }

    /**
     * 银联支付
     *
     * @param txnAmt 交易金额，单位分
     */
    private void unionpay(String txnAmt) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Server.NewPayServer&m=UnionPay");
        param.setStringParams(txnAmt);
        x.http().post(param, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String respInfo) {
                mLoadingDialog.dismiss();
                Gson gson = new Gson();
                String jsonStr = gson.fromJson(respInfo, String.class);
                if (jsonStr != null && TextUtils.isDigitsOnly(jsonStr)) {
                    String tn = jsonStr;
                    // 启动银联支付手机控件
                    UPPayAssistEx.startPay(WXPayEntryActivity.this, null, null, tn, mMode);
                } else {
                    mErrorMsgTv.setText(respInfo);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }
        });
    }

    // //////////////////////////////支付宝/////////////////////////////////////////

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(WXPayEntryActivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                        SaleOrder order = new SaleOrder();
                        order.OrderNo = orderNo;
                        order.State = 1;
                        order.PayType = 2;
                        SimpleDateFormat format = new SimpleDateFormat(
                                getString(R.string.timeformat4),
                                Locale.getDefault());
                        order.MoneyTime = format.format(new Date());
                        updateSaleOrder(order);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(WXPayEntryActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(WXPayEntryActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(WXPayEntryActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    private void pay() {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }
        String orderInfo = getOrderInfo("测试的商品", "中天博日", "0.01");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(WXPayEntryActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(WXPayEntryActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
     *
     * @param v
     */
    public void h5Pay(View v) {
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        /**
         * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
         * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
         * 商户可以根据自己的需求来实现
         */
        String url = "http://m.taobao.com";

        // url可以是一号店或者美团等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);

    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    // ///////////////////////////////////////////////微信/////////////////////////////////////////////////

    private void wxpay(String authCode) {
        API.NewPayServer.WXAPPZFRequest(authCode, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String responseInfo) {
                mLoadingDialog.dismiss();
                try {
                    Gson gson = new Gson();
                    String jsonStr = gson.fromJson(responseInfo,
                            String.class);
                    JSONObject json = new JSONObject(jsonStr);
                    if (null != json && !json.has("retcode")) {
                        PayReq req = new PayReq();
                        // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                        req.appId = json.getString("appid");
                        req.partnerId = json.getString("mch_id");
                        req.prepayId = json.getString("prepay_id");
                        req.nonceStr = json.getString("nonce_str");
                        req.timeStamp = json.getString("timestamp");
                        req.sign = json.getString("sign1");
                        req.packageValue = "Sign=WXPay";
                        req.extData = "app data"; // optional
                        Toast.makeText(WXPayEntryActivity.this, "正常调起支付",
                                Toast.LENGTH_SHORT).show();
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

                        // 将该app注册到微信
                        api.registerApp(req.appId);

                        api.sendReq(req);
                    } else {
                        Log.d("PAY_GET",
                                "返回错误" + json.getString("retmsg"));
                        Toast.makeText(WXPayEntryActivity.this,
                                "返回错误" + json.getString("retmsg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }
        });
       /* RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Server.NewPayServer&m=WXAPPZFRequest");
        param.setStringParams(authCode);
        x.http().post(param, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String responseInfo) {
                mLoadingDialog.dismiss();
                try {
                    Gson gson = new Gson();
                    String jsonStr = gson.fromJson(responseInfo,
                            String.class);
                    JSONObject json = new JSONObject(jsonStr);
                    if (null != json && !json.has("retcode")) {
                        PayReq req = new PayReq();
                        // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                        req.appId = json.getString("appid");
                        req.partnerId = json.getString("mch_id");
                        req.prepayId = json.getString("prepay_id");
                        req.nonceStr = json.getString("nonce_str");
                        req.timeStamp = json.getString("timestamp");
                        req.sign = json.getString("sign1");
                        req.packageValue = "Sign=WXPay";
                        req.extData = "app data"; // optional
                        Toast.makeText(WXPayEntryActivity.this, "正常调起支付",
                                Toast.LENGTH_SHORT).show();
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

                        // 将该app注册到微信
                        api.registerApp(req.appId);

                        api.sendReq(req);
                    } else {
                        Log.d("PAY_GET",
                                "返回错误" + json.getString("retmsg"));
                        Toast.makeText(WXPayEntryActivity.this,
                                "返回错误" + json.getString("retmsg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(WXPayEntryActivity.this, throwable.getMessage());
            }
        });*/
    }

    private void WXPay() {
        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        // Button payBtn = (Button) findViewById(R.id.appay_btn);
        // payBtn.setEnabled(false);
        Toast.makeText(WXPayEntryActivity.this, "获取订单中...", Toast.LENGTH_SHORT)
                .show();
        try {
            byte[] buf = Util.httpGet(url);
            if (buf != null && buf.length > 0) {
                String content = new String(buf);
                Log.e("get server pay params:", content);
                JSONObject json = new JSONObject(content);
                if (null != json && !json.has("retcode")) {
                    PayReq req = new PayReq();
                    api.registerApp(Constants.APP_ID);
                    // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
                    req.extData = "app data"; // optional
                    Toast.makeText(WXPayEntryActivity.this, "正常调起支付",
                            Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                    Toast.makeText(WXPayEntryActivity.this,
                            "返回错误" + json.getString("retmsg"),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(WXPayEntryActivity.this, "服务器请求错误",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(WXPayEntryActivity.this, "异常：" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        // payBtn.setEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        // Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("微信支付结果");
			builder.setMessage(getString(R.string.pay_result_callback_msg,
					String.valueOf(resp.errCode)));
			builder.show();*/
            if (resp.errCode == 0) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                SaleOrder order = new SaleOrder();
                order.OrderNo = orderNo;
                order.State = 1;
                order.PayType = 3;
                SimpleDateFormat format = new SimpleDateFormat(
                        getString(R.string.timeformat4), Locale.getDefault());
                order.MoneyTime = format.format(new Date());
                updateSaleOrder(order);
            } else if (resp.errCode == -1) {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "用户取消了支付", Toast.LENGTH_SHORT).show();
            }
        }
    }
}