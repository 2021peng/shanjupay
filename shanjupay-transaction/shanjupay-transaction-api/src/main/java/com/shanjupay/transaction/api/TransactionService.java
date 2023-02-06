package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
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
}
