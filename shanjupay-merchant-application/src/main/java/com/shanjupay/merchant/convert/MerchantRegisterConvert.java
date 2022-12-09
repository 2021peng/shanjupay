package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: admin
 * @Date: 2022/12/9 18:15
 * @Description: 将商户注册vo和dto进行转换
 */
@Mapper
public interface MerchantRegisterConvert {

    MerchantRegisterConvert INSTANCE = Mappers.getMapper(MerchantRegisterConvert.class);

    //将dto转成vo
    MerchantRegisterVO dto2vo(MerchantDTO dto);

    //将vo转成dto
    MerchantDTO vo2dto(MerchantRegisterVO vo);
}
