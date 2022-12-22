package com.shanjupay.common.domain;


/**
 * 异常编码
 */
public enum CommonErrorCode implements ErrorCode {

	////////////////////////////////////公用异常编码 //////////////////////////
	E_100101(100101,"传入参数与接口不匹配"),
	E_100102(100102,"验证码错误"),
	E_100103(100103,"验证码为空"),
	E_100104(100104,"查询结果为空"),
	E_100105(100105,"ID格式不正确或超出Long存储范围"),
	E_100106(100106,"上传错误"),
	E_100107(100107,"发送验证码错误"),
	E_100108(100108,"传入对象为空"),
	E_100109(100109,"手机号格式不正确"),
	E_100110(100110,"用户名为空"),
	E_100111(100111,"密码为空"),
	E_100112(100112,"手机号为空"),
	E_100113(100113,"手机号已存在"),
	E_100114(100114,"用户名已存在"),
	E_100115(100115,"密码不正确"),

	////////////////////////////////////SAAS服务异常编码110 //////////////////////////
	E_110001(110001,"账号不存在"),
	E_110002(110002,"角色编码在同一租户中已存在，不可重复"),
	E_110003(110003,"角色为空"),
	E_110004(110004,"角色已绑定账号，被使用中不可删除"),
	E_110005(110005,"权限集合为空"),
	E_110006(110006,"参数为空"),
	E_110007(110007,"未查询到租户关联的角色"),
	E_110008(110008,"账号被其他租户使用，不可删除"),

	////////////////////////////////////商户服务异常编码200//////////////////////////
	E_200001(200001,"企业名称不能为空"),
	E_200002(200002,"商户不存在"),
	E_200003(200003,"商户还未通过认证审核，不能创建应用"),
	E_200004(200004,"应用名称已经存在，请使用其他名称"),
	E_200005(200005,"应用不属于当前商户"),
	E_200006(200006,"门店不属于当前商户"),
	E_200007(200007,"二维码生成失败"),
	E_200008(200008,"授权码为空"),
	E_200009(200009,"订单标题为空"),
	E_200010(200010,"订单金额为空"),
	E_200011(200011,"授权码格式有误"),
	E_200012(200012,"租户不存在"),
	E_200013(200013,"员工不存在"),
	E_200014(200014,"商户下未设置根门店"),
	E_200015(200015,"未查询到该门店"),
	E_200016(200016,"资质申请已通过，无需重复申请"),
	E_200017(200017,"商户在当前租户下已经注册，不可重复注册"),

	////////////////////////////////////交易服务异常编码300//////////////////////////
	E_300001(300001,"支付金额为空"),
	E_300002(300002,"openId为空"),
	E_300003(300003,"appId为空"),
	E_300004(300004,"商户id为空"),
	E_300005(300005,"服务类型编码为空"),
	E_300006(300006,"订单金额转换异常"),
	E_300007(300007,"原始支付渠道为空"),
	E_300008(300008,"已存在相同的支付参数，不可重复配置"),
	E_300009(300009,"传入对象为空或者缺少必要的参数"),
	E_300010(300010,"应用未绑定该服务类型,不可进行支付渠道参数配置"),


	E_300110(300110,"交易单号不能为空"),


	////////////////////////////////////支付渠道代理服务异常编码400//////////////////
	E_400001(400001,"微信确认支付失败"),

	////////////////////////////////////运营服务异常编码500//////////////////

	////////////////////////////////////特殊异常编码/////////////////////////////////////
    E_999991(999991,"调用微服务-授权服务 被熔断"),
    E_999992(999992,"调用微服务-用户服务 被熔断"),
    E_999993(999993,"调用微服务-资源服务 被熔断"),
    E_999994(999994,"调用微服务-同步服务 被熔断"),

    E_999910(999910,"调用微服务-没有传tenantId租户Id"),
	E_999911(999911,"调用微服务-没有json-token令牌"),
	E_999912(999912,"调用微服务-json-token令牌解析有误"),
	E_999913(999913,"调用微服务-json-token令牌有误-没有当前租户信息"),
	E_999914(999914,"调用微服务-json-token令牌有误-该租户下没有权限信息"),

	E_NO_AUTHORITY(999997,"没有访问权限"),
	CUSTOM(999998,"自定义异常"),
	/**
	 * 未知错误
	 */
	UNKNOWN(999999,"未知错误");


	private int code;
	private String desc;

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	private CommonErrorCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	public static CommonErrorCode setErrorCode(int code) {
       for (CommonErrorCode errorCode : CommonErrorCode.values()) {
           if (errorCode.getCode()==code) {
               return errorCode;
           }
       }
	       return null;
	}
}
