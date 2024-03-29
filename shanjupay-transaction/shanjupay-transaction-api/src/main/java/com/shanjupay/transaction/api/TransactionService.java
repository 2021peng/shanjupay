package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;

/**
 * @Author: admin
 * @Date: 2023/2/6 19:04
 * @Description: 交易订单相关服务接口
 */
public interface TransactionService {
    /**
     * 生成门店二维码的url
     * @param qrCodeDto，传入merchantId,appId、storeid、channel、subject、body
     * @return 支付入口URL，将二维码的参数组成json并用base64编码
     * @throws BusinessException
     */
    String createStoreQRCode(QRCodeDto qrCodeDto) throws BusinessException;

    /**
     * @description: 支付宝订单保存.
     * 1.保存订单到闪聚平台；2.调用支付渠道代理服务调用支付宝的接口
     * @Param payOrderDTO:
     * @return: com.shanjupay.paymentagent.api.dto.PaymentResponseDTO
     */
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO)throws BusinessException;

    /**
     * @description: 根据订单号查询订单信息
     * @Param tradeNo:
     * @return: com.shanjupay.transaction.api.dto.PayOrderDTO
     */
    public PayOrderDTO queryPayOrder(String tradeNo);

    /**
     * 更新订单支付状态
     *
     * @param tradeNo 闪聚平台订单号
     * @param payChannelTradeNo 支付宝或微信的交易流水号
     * @param state 订单状态 交易状态支付状态,0‐订单生成,1‐支付中(目前未使用),2‐支付成功,4‐关闭 5‐‐失败
     */
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state);

}
