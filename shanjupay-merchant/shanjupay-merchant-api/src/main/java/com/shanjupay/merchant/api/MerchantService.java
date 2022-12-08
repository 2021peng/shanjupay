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
}
