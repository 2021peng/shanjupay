package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = new Merchant();
        //写入属性
        //设置审核状态0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        merchant.setAuditStatus("0");
        merchant.setMobile(merchantDTO.getMobile());

        //调用mapper向数据库写入记录
        merchantMapper.insert(merchant);
        //将dto中写入新增商户的id
        merchantDTO.setId(merchant.getId());

        return merchantDTO;
    }
}
