package com.shanjupay.paymentagent.message;

import com.alibaba.fastjson.JSON;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: admin
 * @Date: 2023/2/11 13:50
 * @Description:
 */

@Slf4j
@Component
public class PayProducer {
    //消息Topic
    private static final String TOPIC_ORDER = "TP_PAYMENT_ORDER";
    private static final String TOPIC_RESULT = "TP_PAYMENT_RESULT";

//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    RocketMQTemplate rocketMQTemplate;


    //发送消息，查询支付宝订单状态
    public void payOrderNotice(PaymentResponseDTO result) {
        log.info("支付通知发送延迟消息:{}", result);
        try {
            //处理消息存储格式
            Message<PaymentResponseDTO> message = MessageBuilder.withPayload(result).build();
            SendResult sendResult = rocketMQTemplate.syncSend(TOPIC_ORDER, message, 1000, 3);
            log.info("支付渠道代理服务向mq发送订单查询消息：{}", JSON.toJSONString(sendResult));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    //发送消息1，查看支付结果
    public void payResultNotice(PaymentResponseDTO result){
        rocketMQTemplate.convertAndSend(TOPIC_RESULT,result);
        log.info("支付渠道代理服务向mq发送支付结果的消息：{}", JSON.toJSONString(result));
    }
}
