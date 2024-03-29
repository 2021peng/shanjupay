package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.AmountUtil;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.PaymentUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import com.shanjupay.transaction.convert.PayOrderConvert;
import com.shanjupay.transaction.entity.PayOrder;
import com.shanjupay.transaction.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @Autowired
    PayOrderMapper payOrderMapper;

    @Reference
    PayChannelAgentService payChannelAgentService;

    @Autowired
    PayChannelService payChannelService;

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
     * @param payOrderDTO
     * @description: 支付宝订单保存.
     * 1.保存订单到闪聚平台；2.调用支付渠道代理服务调用支付宝的接口
     * @Param payOrderDTO:
     * @return: com.shanjupay.paymentagent.api.dto.PaymentResponseDTO
     */
    @Override
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException {
        //保存订单到闪聚平台
        payOrderDTO.setPayChannel("ALIPAY_WAP");
        //保存订单
        payOrderDTO = save(payOrderDTO);
        //调用支付代理服务请求第三方支付系统
        PaymentResponseDTO paymentResponseDTO = alipayH5(payOrderDTO.getTradeNo());
        return paymentResponseDTO;
    }

    /**
     * 根据订单号查询订单信息
     * @param tradeNo
     * @return
     */
    @Override
    public PayOrderDTO queryPayOrder(String tradeNo) {
        PayOrder payOrder = payOrderMapper
                .selectOne(new QueryWrapper<PayOrder>().lambda().eq(PayOrder::getTradeNo, tradeNo));
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    /**
     * 更新订单支付状态
     *
     * @param tradeNo           闪聚平台订单号
     * @param payChannelTradeNo 支付宝或微信的交易流水号
     * @param state             订单状态 交易状态支付状态,0‐订单生成,1‐支付中(目前未使用),2‐支付成功,4‐关闭 5‐‐失败
     */
    @Override
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) {
        final LambdaUpdateWrapper<PayOrder> lambda = new UpdateWrapper<PayOrder>().lambda();
        lambda.eq(PayOrder::getTradeNo, tradeNo).set(PayOrder::getPayChannelTradeNo, payChannelTradeNo)
                .set(PayOrder::getTradeState, state);
        if (state != null && "2".equals(state)) {
            lambda.set(PayOrder::getPaySuccessTime, LocalDateTime.now());
        }
        payOrderMapper.update(null, lambda);
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

    /**
     * 保存订单到闪聚平台
     *
     * @param payOrderDTO
     * @return
     */
    private PayOrderDTO save(PayOrderDTO payOrderDTO) throws BusinessException {
        PayOrder entity = PayOrderConvert.INSTANCE.dto2entity(payOrderDTO);
        //订单号，采用雪花片算法
        entity.setTradeNo(PaymentUtil.genUniquePayOrderNo());
        //订单创建时间
        entity.setCreateTime(LocalDateTime.now());
        //设置过期时间，30分钟
        entity.setExpireTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));
        entity.setCurrency("CNY");//设置支付币种
        entity.setTradeState("0");//订单状态
        int insert = payOrderMapper.insert(entity);
        return PayOrderConvert.INSTANCE.entity2dto(entity);
    }

    //调用支付宝下单接口
    private PaymentResponseDTO alipayH5(String tradeNo) {
        //构建支付实体
        AlipayBean alipayBean = new AlipayBean();
        //根据订单号查询订单详情
        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);
        alipayBean.setOutTradeNo(tradeNo);
        alipayBean.setSubject(payOrderDTO.getSubject());
        String totalAmount = null;//支付宝那边入参是元
        try {
            //将分转成元
            totalAmount = AmountUtil.changeF2Y(payOrderDTO.getTotalAmount().toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_300006);
        }
        alipayBean.setTotalAmount(totalAmount);
        alipayBean.setBody(payOrderDTO.getBody());
        alipayBean.setStoreId(payOrderDTO.getStoreId());
        alipayBean.setExpireTime("30m");
        //根据应用、服务类型、支付渠道查询支付渠道参数
        PayChannelParamDTO payChannelParamDTO =
                payChannelService.queryParamByAppPlatformAndPayChannel(payOrderDTO.getAppId(),
//                        payOrderDTO.getChannel(), "ALIPAY_WAP");
                        "shanju_c2b", "ALIPAY_WAP");
        if (payChannelParamDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300007);
        }
        //支付宝渠道参数
        AliConfigParam aliConfigParam = JSON.parseObject(
                payChannelParamDTO.getParam(), AliConfigParam.class);
        //字符编码
        aliConfigParam.setCharest("utf-8");
        PaymentResponseDTO payOrderResponse = payChannelAgentService
                .createPayOrderByAliWAP(aliConfigParam, alipayBean);
        log.info("支付宝H5支付响应Content:" + payOrderResponse.getContent());

        return payOrderResponse;
    }
}
