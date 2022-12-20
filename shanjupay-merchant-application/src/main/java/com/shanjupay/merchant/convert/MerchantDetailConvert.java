package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: admin
 * @Date: 2022/12/20 17:23
 * @Description:
 */
@Mapper
public interface MerchantDetailConvert {
    MerchantDetailConvert INSTANCE = Mappers.getMapper(MerchantDetailConvert.class);
    MerchantDTO vo2dto(MerchantDetailVO vo);
    MerchantDetailVO dto2vo(MerchantDTO dto);
}
