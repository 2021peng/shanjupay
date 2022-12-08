package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.service
 * @Classname: MerchantServiceImpl
 * @Author: admin
 * @Date: 2022/12/8 15:43
 * @Description:
 */

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    //根据ID查询详细信息
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setMerchantName(merchant.getMerchantName());
//设置其它属性...
        return merchantDTO;
    }
}
