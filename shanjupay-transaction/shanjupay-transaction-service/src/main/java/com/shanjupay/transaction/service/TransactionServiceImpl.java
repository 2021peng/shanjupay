package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author: admin
 * @Date: 2023/2/6 19:08
 * @Description:
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Reference
    AppService appService;

    @Reference
    MerchantService merchantService;

    /**
     * 支付入口url
     */
    @Value("${shanjupay.payurl}")
    private String payurl;

    /**
     * 生成门店二维码的url
     *
     * @param qrCodeDto ，传入merchantId,appId、storeid、channel、subject、body
     * @return 支付入口URL，将二维码的参数组成json并用base64编码
     * @throws BusinessException
     */
    @Override
    public String createStoreQRCode(QRCodeDto qrCodeDto) throws BusinessException {

        //校验商户id和应用id和门店id的合法性
        verifyAppAndStore(qrCodeDto.getMerchantId(),qrCodeDto.getAppId(),qrCodeDto.getStoreId());

        //组装url所需要的数据
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setMerchantId(qrCodeDto.getMerchantId());
        payOrderDTO.setAppId(qrCodeDto.getAppId());
        payOrderDTO.setStoreId(qrCodeDto.getStoreId());
        payOrderDTO.setSubject(qrCodeDto.getSubject());//显示订单标题
        payOrderDTO.setChannel("shanju_c2b");//服务类型
        payOrderDTO.setBody(qrCodeDto.getBody());//订单内容
        //装成json
        String jsonString = JSON.toJSONString(payOrderDTO);
        log.info("transaction service createStoreQRCode,JsonString is {}",jsonString);

        //将支付信息保存到票据当中，base64编码
        String ticket = EncryptUtil.encodeUTF8StringBase64(jsonString);
        //支付入口
        String payEntryUrl = payurl +ticket;
        log.info("transaction service createStoreQRCode,pay‐entry is {}",payEntryUrl);
        return payEntryUrl;
    }

    /**
     * 校验应用和门店是否属于当前登录商户
     * @param merchantId
     * @param appId
     * @param storeId
     */
    private void verifyAppAndStore(Long merchantId,String appId,Long storeId) {
        //判断应用是否属于当前商户
        Boolean contains = appService.queryAppInMerchant(appId, merchantId);
        if (!contains) {
            throw new BusinessException(CommonErrorCode.E_200005);
        }
        //判断门店是否属于当前商户
        Boolean containsStore = merchantService.queryStoreInMerchant(storeId, merchantId);
        if (!containsStore) {
            throw new BusinessException(CommonErrorCode.E_200006);
        }
    }
}
