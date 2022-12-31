package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantCovert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
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
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    StoreMapper storeMapper;

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;

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
        if (StringUtils.isBlank(merchantDTO.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //密码非空校验
        if (StringUtils.isBlank(merchantDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }

        //校验手机号的唯一性
        //根据手机号查询商户表,如果存在记录则说明手机号已存在
        LambdaQueryWrapper<Merchant> lambdaQryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile,merchantDTO.getMobile());
        Integer count = merchantMapper.selectCount(lambdaQryWrapper);
        if(count>0){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //调用SaaS接口
        //2.添加租户 和账号 并绑定关系
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        //表示该租户类型是商户
        createTenantRequestDTO.setTenantTypeCode("shanju-merchant");
        //设置租户套餐为初始化套餐餐
        createTenantRequestDTO.setBundleCode("shanju-merchant");
        //租户的账号信息
        createTenantRequestDTO.setUsername(merchantDTO.getUsername());
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        //新增租户并设置为管理员
        createTenantRequestDTO.setName(merchantDTO.getUsername());

        log.info("商户中心调用统一账号服务，新增租户和账号");
        TenantDTO tenantDTO = tenantService.createTenantAndAccount(createTenantRequestDTO);

        if (tenantDTO == null || tenantDTO.getId() == null){
            throw new BusinessException(CommonErrorCode.E_200012);
        }
        Merchant merchant1 = merchantMapper.selectOne(new QueryWrapper<Merchant>().lambda().eq(Merchant::getTenantId, tenantDTO.getId()));
        if (merchant1 != null && merchant1.getId() != null){
            throw new BusinessException(CommonErrorCode.E_200017);
        }

//        Merchant merchant = new Merchant();
        //写入属性
//        merchant.setMobile(merchantDTO.getMobile());

        //3. 设置商户所属租户
        //使用MapStruct进行对象转换，将dto装成entity
        Merchant merchant = MerchantCovert.INSTANCE.dto2entity(merchantDTO);

        //设置审核状态0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        merchant.setAuditStatus("0");
        //设置租户id
        merchant.setTenantId(tenantDTO.getId());

        //调用mapper向数据库写入记录
        merchantMapper.insert(merchant);

        //4.新增门店，创建根门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreName("根门店");
        storeDTO.setMerchantId(merchant.getId());//商户id
//        storeDTO.setStoreStatus(true);
        storeDTO = createStore(storeDTO);
        log.info("门店信息：{}" + JSON.toJSONString(storeDTO));

        //5.新增员工，并设置归属门店
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMerchantId(merchant.getId());//商户id
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
        staffDTO.setStoreId(storeDTO.getId());//所属门店id
//        staffDTO.setStaffStatus(true);
        staffDTO = createStaff(staffDTO);

        //6.为门店设置管理员
        bindStaffToStore(storeDTO.getId(),staffDTO.getId());

        //将dto中写入新增商户的id
//        merchantDTO.setId(merchant.getId());
        //将entity转成 dto
        return MerchantCovert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 资质申请
     * @param merchantId 商户id
     * @param merchantDTO 资质申请信息
     * @throws BusinessException
     */
    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        //接收资质申请信息，更新到商户表
        if(merchantDTO == null || merchantId == null){
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        //根据id查询商户，校验merchantId的合法性
        Merchant merchant = merchantMapper.selectById(merchantId);
        if(merchant == null){
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        //将dto转换为entity
        Merchant entity = MerchantCovert.INSTANCE.dto2entity(merchantDTO);
        //将必要的参数设置到entity
        entity.setId(merchant.getId());
        entity.setMobile(merchant.getMobile());//因为资质申请的时候手机号不让改，所以还是使用数据库中的手机号
        entity.setAuditStatus("1");//已申请待审核
        entity.setTenantId(merchant.getTenantId());

        //更新商户列表
        merchantMapper.updateById(entity);
    }

    /**
     * @description: 新增门店
     * @param storeDTO
     * @return: com.shanjupay.merchant.api.dto.StoreDTO
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("商户下新增门店"+ JSON.toJSONString(store));
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }

    /**
     * 商户新增员工
     *
     * @param staffDTO
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {
        //1.校验手机号格式及是否存在
        String mobile = staffDTO.getMobile();
        if(staffDTO == null || StringUtils.isBlank(mobile)
            || staffDTO.getStoreId() == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //根据商户id和手机号校验唯一性
        if(isExistStaffByMobile(mobile, staffDTO.getMerchantId())){
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        //2.校验用户名是否为空
        String username = staffDTO.getUsername();
        if(StringUtils.isBlank(username)){
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //根据商户id和账号校验唯一性
        if(isExistStaffByUserName(username, staffDTO.getMerchantId())){
            throw new BusinessException(CommonErrorCode.E_100114);
        }
        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        log.info("商户下新增员工");
        staffMapper.insert(entity);

        return StaffConvert.INSTANCE.entity2dto(entity);

    }

    /**
     * 为门店设置管理员
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStoreId(storeId);//门店id
        storeStaff.setStaffId(staffId);//员工id
        storeStaffMapper.insert(storeStaff);
    }

    /**
     * 根据手机号判断员工是否已在指定商户存在
     * @param mobile 手机号
     * @return
     */
    private boolean isExistStaffByMobile(String mobile, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }
    /**
     * 根据账号判断员工是否已在指定商户存在
     * @param userName
     * @param merchantId
     * @return
     */
    private boolean isExistStaffByUserName(String userName, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getUsername, userName).eq(Staff::getMerchantId,
                merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }
}
