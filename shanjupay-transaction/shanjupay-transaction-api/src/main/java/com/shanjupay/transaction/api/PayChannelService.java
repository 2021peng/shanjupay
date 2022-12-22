package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * @Author: admin
 * @Date: 2022/12/21 15:52
 * @Description:
 */
public interface PayChannelService {

    /**
     * 获取平台服务类型
     * @return
     */
    List<PlatformChannelDTO> queryPlatformChannel() throws BusinessException;

    /**
     * 为某个应用app绑定一个服务类型
     * @param appId 应用id
     * @param platformChannelCodes 平台服务类型列表
     */
    void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException;

    /**
     * 应用是否已经绑定了某个服务类型
     * @param appId
     * @param platformChannel
     * @return 已绑定返回1，否则 返回0
     */
    int queryAppBindPlatformChannel(String appId,String platformChannel) throws BusinessException;
}
