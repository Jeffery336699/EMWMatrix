package cc.emw.mobile.entity;

import java.io.Serializable;

/**
 * 商品订单
 * @author shaobo.zhuang
 *
 */
public class SaleOrder implements Serializable {
	
	public int ID;
	public String OrderNo; // 订单号
	public String CreateTime; // 创建时间
	public String MoneyTime; // 付款时间
	public int State; // 0：未支付；1：已支付
	public String OrderInfo; // 订单信息
	public String TotalMoney; // 总金额
	public int Creator; // 创建用户ID
	public int PayType; // 1：现金支付；2：支付宝支付； 3：微信支付；4：银联支付
}
