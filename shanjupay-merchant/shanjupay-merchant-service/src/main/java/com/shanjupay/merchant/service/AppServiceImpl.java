package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppCovert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * @Author: admin
 * @Date: 2022/12/21 10:46
 * @Description:
 */

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    AppMapper appMapper;

    @Autowired
    MerchantMapper merchantMapper;

    /**
     * @description:创建应用
     * @Param merchantId: 商户的id
     * @Param appDTO: 应用信息
     * @return: com.shanjupay.merchant.api.dto.AppDTO 创建成功的信息
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO appDTO) throws BusinessException {

        if(merchantId == null || appDTO == null || StringUtil.isBlank(appDTO.getAppName())){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //1)校验商户是否通过资质审核
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        if (!"2".equals(merchant.getAuditStatus())) {
            throw new BusinessException(CommonErrorCode.E_200003);
        }

        //3）保存商户应用信息
        //   应用名称需要校验唯一性。
        if (isExistAppName(appDTO.getAppName())){
            throw new BusinessException(CommonErrorCode.E_200004);
        }

        //2）生成应用ID
        String appId = UUID.randomUUID().toString();

        //保存应用信息
        App entity = AppCovert.INSTANCE.dto2entity(appDTO);
        entity.setAppId(appId);
        entity.setMerchantId(merchant.getId());
        //调用appMapper向app表插入数据
        appMapper.insert(entity);

        return AppCovert.INSTANCE.entity2dto(entity);
    }

    /**
     * 查询商户下的应用列表
     *
     * @param merchantId
     * @return
     */
    @Override
    public List<AppDTO> queryAppByMerchant(Long merchantId) throws BusinessException {
        List<App> apps = appMapper.selectList(new QueryWrapper<App>().lambda().eq(App::getMerchantId, merchantId));
        List<AppDTO> appDTOS = AppCovert.INSTANCE.listentity2dto(apps);
        return appDTOS;
    }

    /**
     * 根据业务id查询应用
     *
     * @param id
     * @return
     */
    @Override
    public AppDTO getAppById(String id) throws BusinessException {
        App app = appMapper.selectOne(new QueryWrapper<App>().lambda().eq(App::getAppId, id));
        return AppCovert.INSTANCE.entity2dto(app);
    }

    /**
     * @description:校验应用名是否已被使用
     * @Param appName:
     * @return: java.lang.Boolean
     */
    public Boolean isExistAppName(String appName) {
        Integer count = appMapper.selectCount(new QueryWrapper<App>
                ().lambda().eq(App::getAppName, appName));
        return count.intValue() > 0;
    }
}
