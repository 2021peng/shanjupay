package com.shanjupay.merchant.api;

import com.shanjupay.merchant.api.dto.MerchantDTO;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.api
 * @Classname: MerchantService
 * @Author: admin
 * @Date: 2022/12/8 15:33
 * @Description:
 */
public interface MerchantService {
    /**
     * 根据ID查询详细信息
     * @param merchantId
     * @return
     * @throws BusinessException
     */
    MerchantDTO queryMerchantById(Long merchantId);

    /**
     * @date: 2022/12/9 15:33
     * @description: 商户注册的接口，接收账号密码和手机号，为了可扩展性使用merchantDto接收数据
     * @Param merchantDTO:  merchantDTO商户注册的信息
     * @return: com.shanjupay.merchant.api.dto.MerchantDTO 注册成功的商户信息
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO);
}
