package net.sourceforge.simcpux;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.xutils.view.annotation.ContentView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.Const;

@ContentView(R.layout.pay)
public class PayActivity extends BaseActivity {

	private Dialog mLoadingDialog; // 加载框

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, "wxd930ea5d5a258f4f");

		Button appayBtn = (Button) findViewById(R.id.appay_btn);
		appayBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String ownUrl = Const.Url_Client
						+ "?t=Server.NewPayServer&m=WXAPPZFRequest";
//				wxpay("123456789");
				/*
				 * String url =
				 * "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android"
				 * ; Button payBtn = (Button) findViewById(R.id.appay_btn);
				 * payBtn.setEnabled(false); Toast.makeText(PayActivity.this,
				 * "获取订单中...", Toast.LENGTH_SHORT).show(); try{ byte[] buf =
				 * Util.httpGet(url); if (buf != null && buf.length > 0) {
				 * String content = new String(buf);
				 * Log.e("get server pay params:",content); JSONObject json =
				 * new JSONObject(content); if(null != json &&
				 * !json.has("retcode") ){ PayReq req = new PayReq();
				 * //req.appId = "wxf8b4f85f3a794e77"; // 测试用appId req.appId =
				 * json.getString("appid"); req.partnerId =
				 * json.getString("partnerid"); req.prepayId =
				 * json.getString("prepayid"); req.nonceStr =
				 * json.getString("noncestr"); req.timeStamp =
				 * json.getString("timestamp"); req.packageValue =
				 * json.getString("package"); req.sign = json.getString("sign");
				 * req.extData = "app data"; // optional
				 * Toast.makeText(PayActivity.this, "正常调起支付",
				 * Toast.LENGTH_SHORT).show(); //
				 * 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
				 * api.sendReq(req); }else{ Log.d("PAY_GET",
				 * "返回错误"+json.getString("retmsg"));
				 * Toast.makeText(PayActivity.this,
				 * "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
				 * } }else{ Log.d("PAY_GET", "服务器请求错误");
				 * Toast.makeText(PayActivity.this, "服务器请求错误",
				 * Toast.LENGTH_SHORT).show(); } }catch(Exception e){
				 * Log.e("PAY_GET", "异常："+e.getMessage());
				 * Toast.makeText(PayActivity.this, "异常："+e.getMessage(),
				 * Toast.LENGTH_SHORT).show(); }
				 */
				// payBtn.setEnabled(true);

			}
		});
		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
		checkPayBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				Toast.makeText(PayActivity.this,
						String.valueOf(isPaySupported), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

//	private void wxpay(String authCode) {
//		HttpUtils http = new HttpUtils();
//		http.configCookieStore(PrefsUtil.readLoginCookie());
//		RequestParam param = new RequestParam();
//		param.setStringParams(authCode);
//		http.send(HttpMethod.POST, HttpConst.Url_Client
//				+ "?t=Server.NewPayServer&m=WXAPPZFRequest", param,
//				new RequestCallBack<String>() {
//					@Override
//					public void onStart() {
//						mLoadingDialog = createLoadingDialog("正在处理...");
//						mLoadingDialog.show();
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						LogUtils.d("alipay:" + responseInfo.result);
//						mLoadingDialog.dismiss();
//
//						try {
//							Gson gson = new Gson();
//							String jsonStr = gson.fromJson(responseInfo.result,
//									String.class);
//							JSONObject json = new JSONObject(jsonStr);
//							if (null != json && !json.has("retcode")) {
//								PayReq req = new PayReq();
//								// req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
//								//req.appId = json.getString("appid");
//								req.appId = "wxd930ea5d5a258f4f";
//								req.partnerId = json.getString("mch_id");
//								// req.partnerId = "1900000109";
//								req.prepayId = json.getString("prepay_id");
//								req.nonceStr = json.getString("nonce_str");
//								req.timeStamp = System.currentTimeMillis() + "";
//								req.packageValue = "Sign=WXPay";
//								req.sign = json.getString("sign");
//								req.extData = "app data"; // optional
//								Toast.makeText(PayActivity.this, "正常调起支付",
//										Toast.LENGTH_SHORT).show();
//								// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//
//								// 将该app注册到微信
//								api.registerApp(Constants.APP_ID);
//
//								api.sendReq(req);
//							} else {
//								Log.d("PAY_GET",
//										"返回错误" + json.getString("retmsg"));
//								Toast.makeText(PayActivity.this,
//										"返回错误" + json.getString("retmsg"),
//										Toast.LENGTH_SHORT).show();
//							}
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						mLoadingDialog.dismiss();
//						Toast.makeText(PayActivity.this,
//								error.getExceptionCode() + " " + msg,
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//	}

}
