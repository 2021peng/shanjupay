package com.shanjupay.merchant.service;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.service
 * @Classname: SmsService
 * @Author: admin
 * @Date: 2022/12/9 11:57
 * @Description: 手机短信服务
 */

public interface SmsService {

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    String sendMsg(String phone);
}
