package com.shanjupay.paymentagent.service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.TradeStatus;
import com.shanjupay.paymentagent.common.consant.AliCodeConstants;
import com.shanjupay.paymentagent.message.PayProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: admin
 * @Date: 2023/2/9 15:44
 * @Description:
 */
@Service
@Slf4j
public class PayChannelAgentServiceImpl implements PayChannelAgentService {

    @Autowired
    PayProducer payProducer;

    /**
     * 调用支付宝手机WAP下单接口
     *
     * @param aliConfigParam 支付渠道参数
     * @param alipayBean     请求支付参数（商户订单号，订单标题等）
     * @return
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException {

        log.info("支付宝请求参数", alipayBean.toString());
        //支付宝渠道参数
        String gateway = aliConfigParam.getUrl();//支付宝下单接口地址
        String appId = aliConfigParam.getAppId();//appid
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey();//私钥
        String format = aliConfigParam.getFormat();//数据格式json
        String charest = aliConfigParam.getCharest();//字符编码
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey(); //公钥
        String signtype = aliConfigParam.getSigntype();//签名算法类型
        String notifyUrl = aliConfigParam.getNotifyUrl();//支付结果通知地址
        String returnUrl = aliConfigParam.getReturnUrl();//支付完成返回商户地址
        //支付宝sdk客户端
        AlipayClient client = new DefaultAlipayClient(gateway, appId, rsaPrivateKey,
                format, charest, alipayPublicKey, signtype);
        // 封装请求支付信息
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(alipayBean.getOutTradeNo());//闪聚平台订单
        model.setSubject(alipayBean.getSubject());//订单标题
        model.setTotalAmount(alipayBean.getTotalAmount());//订单金额
        model.setBody(alipayBean.getBody());//订单内容
        model.setTimeoutExpress(alipayBean.getExpireTime());//订单过期时间
//        model.setProductCode(alipayBean.getProductCode());//商户与支付宝签定的产品码，固定为QUICK_WAP_WAY
        model.setProductCode("QUICK_WAP_WAY");//商户与支付宝签定的产品码，固定为QUICK_WAP_WAY
        alipayRequest.setBizModel(model);//请求参数集合
        String jsonString = JSON.toJSONString(alipayBean);
        log.info("createPayOrderByAliWAP..alipayRequest:{}",jsonString);
        // 设置异步通知地址
        alipayRequest.setNotifyUrl(notifyUrl);
        // 设置同步地址,支付成功的url
        alipayRequest.setReturnUrl(returnUrl);
        try {
            //发送支付结果查询延迟消息
            PaymentResponseDTO<AliConfigParam> notice = new PaymentResponseDTO<AliConfigParam>();
            notice.setOutTradeNo(alipayBean.getOutTradeNo());
            notice.setContent(aliConfigParam);
            notice.setMsg("ALIPAY_WAP");
            payProducer.payOrderNotice(notice);

            // 调用SDK提交表单
            AlipayTradeWapPayResponse response = client.pageExecute(alipayRequest);
            log.info("支付宝手机网站支付预支付订单信息" + response);
            PaymentResponseDTO res = new PaymentResponseDTO();
            res.setContent(response.getBody());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_400002);//支付宝确认支付失败
        }

    }

    /**
     * 支付宝交易状态查询
     *
     * @param aliConfigParam 支付渠道参数
     * @param outTradeNo     闪聚平台订单号
     * @return
     */
    @Override
    public PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam, String outTradeNo) {
        String gateway = aliConfigParam.getUrl();//支付接口网关地址
        String appId = aliConfigParam.getAppId();//appid
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey(); //私钥
        String format = aliConfigParam.getFormat();//json格式
        String charest = aliConfigParam.getCharest();//编码 utf‐8
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey(); //公钥
        String signtype = aliConfigParam.getSigntype();//签名算法类型
        log.info("C扫B请求支付宝查询订单，参数：{}", JSON.toJSONString(aliConfigParam));
        //构建sdk客户端
        AlipayClient client = new DefaultAlipayClient(gateway, appId, rsaPrivateKey, format,
                charest, alipayPublicKey, signtype);
        AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
        AlipayTradePayModel model = new AlipayTradePayModel();
        //闪聚平台订单号
        model.setOutTradeNo(outTradeNo);
        //封装请求参数
        queryRequest.setBizModel(model);
        PaymentResponseDTO dto;
        try {
            //请求支付宝接口
            AlipayTradeQueryResponse qr = client.execute(queryRequest);
            //接口调用成功
            if (AliCodeConstants.SUCCESSCODE.equals(qr.getCode())) {
                //将支付宝响应的状态转换为闪聚平台的状态
                TradeStatus tradeStatus = covertAliTradeStatusToShanjuCode(qr.getTradeStatus());
                dto = PaymentResponseDTO.success(qr.getTradeNo(), qr.getOutTradeNo(), tradeStatus,
                        qr.getMsg() + " " + qr.getSubMsg());
                log.info("‐‐‐‐查询支付宝H5支付结果" + JSON.toJSONString(dto));
                return dto;
            }
        } catch (AlipayApiException e) {
            log.warn(e.getMessage(), e);
        }
        dto = PaymentResponseDTO.fail("查询支付宝支付结果异常", outTradeNo, TradeStatus.UNKNOWN);
        return dto;
    }

    /**
     * 将支付宝查询时订单状态trade_status 转换为 闪聚订单状态
     * @param aliTradeStatus 支付宝交易状态
     * WAIT_BUYER_PAY（交易创建，等待买家付款）
     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
     * TRADE_SUCCESS（交易支付成功）
     * TRADE_FINISHED（交易结束，不可退款）
     * @return
     */
    private TradeStatus covertAliTradeStatusToShanjuCode(String aliTradeStatus) {
        switch (aliTradeStatus) {
            case AliCodeConstants.WAIT_BUYER_PAY:
                return TradeStatus.USERPAYING;
            case AliCodeConstants.TRADE_SUCCESS:
            case AliCodeConstants.TRADE_FINISHED:
                return TradeStatus.SUCCESS;
            case AliCodeConstants.TRADE_CLOSED:
                return TradeStatus.REVOKED;
            default:
                return TradeStatus.FAILED;
        }
    }


}
