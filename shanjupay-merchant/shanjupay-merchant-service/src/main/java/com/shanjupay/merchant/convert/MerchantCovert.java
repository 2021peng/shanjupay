package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: admin
 * @Date: 2022/12/9 17:07
 * @Description: 定义dto和entity之间的转换规则
 */

@Mapper //对象属性的映射
public interface MerchantCovert {
    //创建转换类实例
    MerchantCovert INSTANCE = Mappers.getMapper(MerchantCovert.class);

    //entity转换成dto
    MerchantDTO entity2dto(Merchant entity);

    //dto转换成entity
    Merchant dto2entity(MerchantDTO dto);

    //list之间的转换
    List<MerchantDTO> listentity2dto(List<Merchant> list);

    public static void main(String[] args) {
        //dto转entity
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setUsername("测试");
        merchantDTO.setMobile("123456");
        Merchant entity = MerchantCovert.INSTANCE.dto2entity(merchantDTO);

        //entity转dto
        entity.setMobile("123444554");
        MerchantDTO merchantDTO1 = MerchantCovert.INSTANCE.entity2dto(entity);
        System.out.println(merchantDTO1);

        //测试list之间的转换
        List<Merchant> list_entity = new ArrayList<>();
        list_entity.add(entity);
        List<MerchantDTO> merchantDTOS = MerchantCovert.INSTANCE.listentity2dto(list_entity);
        System.out.println(merchantDTOS);
    }
}
