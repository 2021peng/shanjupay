package com.shanjupay.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: admin
 * @Date: 2022/12/21 15:54
 * @Description:
 */
@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    PlatformChannelMapper platformChannelMapper;

    @Autowired
    AppPlatformChannelMapper appPlatformChannelMapper;

    /**
     * 获取平台服务类型
     * @return
     */
    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() throws BusinessException {
        //查询plateform_channel表的全部信息
        List<PlatformChannel> platformChannels =
                platformChannelMapper.selectList(null);
        //将platformChannels转换成包含dto的list
        List<PlatformChannelDTO> platformChannelDTOS =
                PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }

    /**
     * 为某个应用app绑定一个服务类型
     * @param appId                应用id
     * @param platformChannelCodes 平台服务类型列表
     */
    @Override
    @Transactional
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException {
        //根据appId和平台服务类型code查询app_platform_channel
        AppPlatformChannel appPlatformChannel =
                appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                        .eq(AppPlatformChannel::getAppId, appId)
                        .eq(AppPlatformChannel::getPlatformChannel,platformChannelCodes));
        //如果没有绑定则绑定,向app_plateform_channel插入记录
        if(appPlatformChannel == null){
            appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(platformChannelCodes);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }

    }

    /**
     * 应用是否已经绑定了某个服务类型
     *
     * @param appId
     * @param platformChannel
     * @return 已绑定返回1，否则 返回0
     */
    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannel) throws BusinessException {
        int count = appPlatformChannelMapper.selectCount(new QueryWrapper<AppPlatformChannel>().lambda().
                eq(AppPlatformChannel::getAppId, appId).
                eq(AppPlatformChannel::getPlatformChannel, platformChannel));

        if (count > 0){
            return 1;
        }else {
            return 0;
        }

    }
}
