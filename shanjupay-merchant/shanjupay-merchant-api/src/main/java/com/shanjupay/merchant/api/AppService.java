package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.AppDTO;

import java.util.List;

/**
 * @Author: admin
 * @Date: 2022/12/21 10:42
 * @Description: 应用管理相关的接口
 */
public interface AppService {
    /**
     * @description:创建应用
     * @Param merchantId: 商户的id
     * @Param appDTO: 应用信息
     * @return: com.shanjupay.merchant.api.dto.AppDTO 创建成功的信息
     */
    AppDTO createApp(Long merchantId,AppDTO appDTO) throws BusinessException;

    /**
     * 查询商户下的应用列表
     * @param merchantId
     * @return
     */
    List<AppDTO> queryAppByMerchant(Long merchantId) throws BusinessException;

    /**
     * 根据业务id查询应用
     * @param id
     * @return
     */
    AppDTO getAppById(String id) throws BusinessException;
}
