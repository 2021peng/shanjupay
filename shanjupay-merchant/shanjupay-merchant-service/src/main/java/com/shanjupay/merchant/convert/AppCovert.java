package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.entity.App;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: admin
 * @Date: 2022/12/21 10:52
 * @Description:
 */
@Mapper
public interface AppCovert {
    AppCovert INSTANCE = Mappers.getMapper(AppCovert.class);
    AppDTO entity2dto(App entity);
    App dto2entity(AppDTO dto);
    List<AppDTO> listentity2dto(List<App> app);
}
