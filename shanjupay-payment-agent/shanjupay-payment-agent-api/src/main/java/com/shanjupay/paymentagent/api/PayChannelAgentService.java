package com.shanjupay.paymentagent.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;

/**
 * @Author: admin
 * @Date: 2023/2/9 15:07
 * @Description:
 */
public interface PayChannelAgentService {
    /**
     * 调用支付宝手机WAP下单接口
     * @param aliConfigParam 支付渠道参数
     * @param alipayBean 请求支付参数（商户订单号，订单标题等）
     * @return
     * @throws BusinessException
     */
    PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException;

    /**
     * 支付宝交易状态查询
     * @param aliConfigParam 支付渠道参数
     * @param outTradeNo 闪聚平台订单号
     * @return
     */
    PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam, String outTradeNo);

}
