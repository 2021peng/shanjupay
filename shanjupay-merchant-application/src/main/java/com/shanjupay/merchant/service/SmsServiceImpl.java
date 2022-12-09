package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.service
 * @Classname: SmsServiceImpl
 * @Author: admin
 * @Date: 2022/12/9 14:26
 * @Description:
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService{

    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.effectiveTime}")
    private String effectiveTime;

    @Autowired
    private RestTemplate restTemplate;
    /**
     * @description: 获取短信验证码
     * @date: 2022/12/9 14:31
     * @Param phone:  
     * @return: java.lang.String
     */
    @Override
    public String sendMsg(String phone) {
        String url = smsUrl + "/generate?name=sms&effectiveTime=" + effectiveTime;
        //验证码过期时间为600秒 10分钟

        log.info("调用短信微服务发送验证码：url:{}",url);

        //请求体
        Map<String,Object> body = new HashMap<>();
        body.put("mobile",phone);
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置数据格式为json
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //封装请求参数
        HttpEntity entity = new HttpEntity(body, httpHeaders);

        Map responseMap = null;

        try {
            //向url发送post请求
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("调用短信微服务发送验证码: 返回值:{}", JSON.toJSONString(exchange));
            //获取响应
            responseMap = exchange.getBody();
        }catch (Exception e){
            log.info(e.getMessage(),e);
            e.printStackTrace();
            throw new RuntimeException("发送验证码失败！！！");
        }

        if (responseMap == null || responseMap.get("result") == null) {
            throw new RuntimeException("发送验证码失败！！！");
        }

        Map resultMap = (Map) responseMap.get("result");
        String key = resultMap.get("key").toString();
        log.info("得到发送验证码对应的的key：{}",key);
        return key;

    }

    @Override
    public void checkVerifiyCode(String verifiyKey, String verifiyCode) {
        String url = smsUrl+"/verify?name=sms&verificationCode="+verifiyCode+"&verificationKey="+verifiyKey;

        Map responseMap = null;

        try {
            //请求校验验证码
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            log.info("校验验证码，响应内容：{} ", JSON.toJSONString(exchange));
            //获取响应
            responseMap = exchange.getBody();
        }catch (Exception e){
            log.info(e.getMessage(),e);
            e.printStackTrace();
            throw new RuntimeException("校验验证码失败！！！");
        }
        if(responseMap == null || responseMap.get("result")==null ||
                !(Boolean) responseMap.get("result")){
            throw new RuntimeException("校验验证码失败！！！");
        }
    }
}
