

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipy.sdk.pay.demo;

/**
 * 支付宝服务窗环境常量（demo中常量只是参考，需要修改成自己的常量值）
 * 
 * @author taixu.zqq
 * @version $Id: AlipayServiceConstants.java, v 0.1 2014年7月24日 下午4:33:49 taixu.zqq Exp $
 */
public class AlipayServiceEnvConstants {

    /**支付宝公钥-从支付宝服务窗获取*/
    public static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

    /**签名编码-视支付宝服务窗要求*/
    public static final String SIGN_CHARSET      = "GBK";

    /**字符编码-传递给支付宝的数据编码*/
    public static final String CHARSET           = "GBK";

    /**签名类型-视支付宝服务窗要求*/
    public static final String SIGN_TYPE         = "RSA";
    
    /** 商户ID */
    public static final String PARTNER           = "2088911227080000";

    /** 服务窗appId  */
    //TODO !!!! 注：该appId必须设为开发者自己的服务窗id  这里只是个测试id
    public static final String APP_ID            = "2015050800060000";

    //开发者请使用openssl生成的密钥替换此处  请看文档：https://fuwu.alipay.com/platform/doc.htm#2-1接入指南
    //TODO !!!! 注：该私钥为测试账号私钥  开发者必须设置自己的私钥 , 否则会存在安全隐患 
    public static final String PRIVATE_KEY       = "MIICXAIBAAKBgQDDzQrBEUxy4TdcXRGFvPZzM4iE+2b9RjmtWHYmtIX2UIUwgCJJCes3t0MuOT/pxwsXN7/svlzMIqEFFa3PryB0tY2NWgIsrOu68ii7nThjuntALb7mMXVT5syKHci0fZVd6MJLXTc+BTurOqOBliS1gZYrnk3BsFIHzUw/xy4+vwIDAQABAoGAC4/qGKiK2SXPfRbJ2Bnme7sJTsEBvecNtNzKbVKvrnHmy4xleeaqCyo5P1uGGKn2T7KtFK/RDwTkeUZkcyUmR2Np8oDs2560hbCPpUMW+6KExNd1K4tKRRbH86wrDv2Kd8Je8YBF0QrIjMvj+Ziznm+Cg+CtbsZ/94lHKeWJPgECQQD7m7txD/mQJImgaPtU1YI6IC40UAwRBOGEMHBjNOP+thp28cp8oDe6aD4HGvCC16/r32PeryTuDJPwfdUlpBY/AkEAxzfxvIkeGwx92WZ14bSYG6YgYUmcDDWuP1lkNKRGd6God1MXfHvyzfY/yxkSa3bitx94Si7a8bfB1kb/Bmi3gQJAVc878whetaEwasvDA6C5hDdbaq3uH5PJkbqvabYtF75lcjFuPGir+N/6X5P5eLY0oj4SomybLLCPd9KCcg7vIwJAYojiMQ1k7Z4FFSVfgu6PGYXWmCgPXd6jO8QcEwl5rSwxJivb/v9TfdmhffP9r1eTFV4kYhTAyDPKVRCyjNn4gQJBAIzZG23ru5+oCMYE050/Wy3SmptV+RygUlPQ4mg0leKCZRCToqWUpyLxkE6PvnmFehQFdFlpajNvJliP6v4n2Fo=";

    //TODO !!!! 注：该公钥为测试账号公钥  开发者必须设置自己的公钥 ,否则会存在安全隐患
    public static final String PUBLIC_KEY        = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDzQrBEUxy4TdcXRGFvPZzM4iE+2b9RjmtWHYmtIX2UIUwgCJJCes3t0MuOT/pxwsXN7/svlzMIqEFFa3PryB0tY2NWgIsrOu68ii7nThjuntALb7mMXVT5syKHci0fZVd6MJLXTc+BTurOqOBliS1gZYrnk3BsFIHzUw/xy4+vwIDAQAB";

    /**支付宝网关*/
    public static final String ALIPAY_GATEWAY    = "https://openapi.alipay.com/gateway.do";

    /**授权访问令牌的授权类型*/
    public static final String GRANT_TYPE        = "authorization_code";
}