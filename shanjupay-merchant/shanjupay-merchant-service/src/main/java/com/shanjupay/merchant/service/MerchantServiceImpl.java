package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantCovert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.commons.lang3.StringUtils;
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
        // 1.校验
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        //手机号非空校验
        if (StringUtils.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //校验手机号的合法性
        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //联系人非空校验
//        if (StringUtils.isBlank(merchantDTO.getUsername())) {
//            throw new BusinessException(CommonErrorCode.E_100110);
//        }
        //密码非空校验
//        if (StringUtils.isBlank(merchantDTO.getPassword())) {
//            throw new BusinessException(CommonErrorCode.E_100111);
//        }

        //校验手机号的唯一性
        //根据手机号查询商户表,如果存在记录则说明手机号已存在
        LambdaQueryWrapper<Merchant> lambdaQryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile,merchantDTO.getMobile());
        Integer count = merchantMapper.selectCount(lambdaQryWrapper);
        if(count>0){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

//        Merchant merchant = new Merchant();
        //写入属性
//        merchant.setMobile(merchantDTO.getMobile());

        //使用MapStruct进行对象转换，将dto装成entity
        Merchant merchant = MerchantCovert.INSTANCE.dto2entity(merchantDTO);

        //设置审核状态0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        merchant.setAuditStatus("0");

        //调用mapper向数据库写入记录
        merchantMapper.insert(merchant);
        //将dto中写入新增商户的id
//        merchantDTO.setId(merchant.getId());
        //将entity转成 dto
        return MerchantCovert.INSTANCE.entity2dto(merchant);
    }
}
