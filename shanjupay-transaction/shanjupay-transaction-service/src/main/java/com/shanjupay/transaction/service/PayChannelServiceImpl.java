package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.dubbo.common.utils.StringUtils;
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

    @Autowired
    PayChannelParamMapper payChannelParamMapper;

    @Autowired
    Cache cache;

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

    /**
     * 根据平台服务类型获取支付渠道列表
     * @param platformChannelCode
     * @return
     */
    @Override
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) throws BusinessException {
        //调用mapper查询数据库platform_pay_channel，pay_channel，platform_channel
        return platformChannelMapper.selectPayChannelByPlatformChannel(platformChannelCode);

    }

    /**
     * 保存支付渠道参数
     *
     * @param payChannelParam 商户原始支付渠道参数
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParam) throws BusinessException {
        if(payChannelParam == null || StringUtils.isBlank(payChannelParam.getAppId())
                ||
                StringUtils.isBlank(payChannelParam.getPlatformChannelCode())
                ||
                StringUtils.isBlank(payChannelParam.getPayChannel())){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //根据appid和服务类型查询应用与服务类型绑定id
        Long appPlatformChannelId = selectIdByAppPlatformChannel(payChannelParam.getAppId(), payChannelParam.getPlatformChannelCode());
        if(appPlatformChannelId == null){
            //应用未绑定该服务类型不可进行支付渠道参数配置
            throw new BusinessException(CommonErrorCode.E_300007);
        }
        //根据应用与服务类型绑定id和支付渠道查询参数信息
        PayChannelParam entity = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId)
                .eq(PayChannelParam::getPayChannel, payChannelParam.getPayChannel()));

        //更新已有配置
        if(entity != null){
            entity.setChannelName(payChannelParam.getChannelName());
            entity.setParam(payChannelParam.getParam());
            payChannelParamMapper.updateById(entity);
        }else {
            //添加新配置
            PayChannelParam entity1 = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParam);
            entity1.setId(null);
            //应用与服务类型绑定id
            entity1.setAppPlatformChannelId(appPlatformChannelId);
            payChannelParamMapper.insert(entity1);
        }

        //保存到redis
        updateCache(payChannelParam.getAppId(),payChannelParam.getPlatformChannelCode());
    }

    /**
     * 获取指定应用指定服务类型下所包含的原始支付渠道参数列表
     *
     * @param appId           应用id
     * @param platformChannel 服务类型
     * @return
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannel) throws BusinessException {
        /*
         *查询渠道参数，先从Redis查询，如果Redis存在则返回渠道参数，
         * 否则从数据库查询同时将查询到的渠道参数存储在Redis中。
         */
        //从缓存查询
        //1.key的构建 如：SJ_PAY_PARAM:b910da455bc84514b324656e1088320b:shanju_c2b
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        //是否有缓存
        Boolean exists = cache.exists(redisKey);
        if (exists){
            //从redis获取key对应的value
            String value = cache.get(redisKey);
            //将value转成对象
            List<PayChannelParamDTO> paramDTOS = JSONObject.parseArray(value, PayChannelParamDTO.class);
            return paramDTOS;
        }

        //查出应用id和服务类型代码在app_platform_channel主键
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
        if (appPlatformChannelId == null){
            return null;
        }
        //根据appPlatformChannelId从pay_channel_param查询所有支付参数
        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
        List<PayChannelParamDTO> paramDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
        //存入缓存
        updateCache(appId, platformChannel);
        return paramDTOS;
    }

    /**
     * 获取指定应用指定服务类型下所包含的某个原始支付参数
     *
     * @param appId
     * @param platformChannel
     * @param payChannel
     * @return
     * @throws BusinessException
     */
    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel, String payChannel) throws BusinessException {
        List<PayChannelParamDTO> payChannelParamDTOS = queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        for (PayChannelParamDTO payChannelParam : payChannelParamDTOS){
            if(payChannelParam.getPayChannel().equals(payChannel)){
                return payChannelParam;
            }
        }
        return null;
    }

    /**
     * 根据appid和服务类型查询应用与服务类型绑定id
     * @param appId
     * @param platformChannelCode
     * @return
     */
    private Long selectIdByAppPlatformChannel(String appId,String platformChannelCode){
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper
                .selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                        .eq(AppPlatformChannel::getAppId, appId)
                        .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));

        if (appPlatformChannel != null){
            return appPlatformChannel.getId();
        }
        return null;
    }

    /**
     * @description:根据应用id和服务类型code查询支付渠道参数列表，将其写入redis
     * @Param appId: 应用id
     * @Param platformChannel: 服务类型code
     * @return: void
     */
    private void updateCache(String appId, String platformChannel){
        //根据应用id和服务类型code查询支付渠道参数列表，将支付渠道参数列表写入redis

        //处理redis缓存
        //1.key的构建 如：SJ_PAY_PARAM:b910da455bc84514b324656e1088320b:shanju_c2b
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        //2.查询redis,检查key是否存在
        Boolean exists = cache.exists(redisKey);
        if (exists){//存在，则清除
            //删除原有缓存
            cache.del(redisKey);
        }
        //3.从数据库查询应用的服务类型对应的实际支付参数，并重新存入缓存
        //查出应用id和服务类型代码在app_platform_channel主键
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
        if (appPlatformChannelId != null){
            //根据appPlatformChannelId从pay_channel_param查询所有支付参数
            List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
            List<PayChannelParamDTO> paramDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
            //存入缓存
            cache.set(redisKey, JSON.toJSON(paramDTOS).toString());
        }

    }
}
