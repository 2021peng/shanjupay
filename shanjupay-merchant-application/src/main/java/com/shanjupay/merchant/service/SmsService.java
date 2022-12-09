package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

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

    /**
     * 校验验证码，抛出异常则校验无效
     * @param verifiyKey 验证码key
     * @param verifiyCode 验证码
     */
    void checkVerifiyCode(String verifiyKey,String verifiyCode) throws BusinessException;
}
