package cc.emw.mobile.entity;

import java.io.Serializable;

/**
 * 支付宝支付响应信息
 * @author shaobo.zhuang
 *
 */
public class AlipayInfo implements Serializable {
	
	public PayResp alipay_trade_pay_response;

	public static class PayResp {
		public String code;
		public String msg;
		public String sub_code;
		public String sub_msg;
	}
}
