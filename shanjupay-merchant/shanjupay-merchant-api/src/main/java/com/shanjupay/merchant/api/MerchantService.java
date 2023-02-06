package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.api
 * @Classname: MerchantService
 * @Author: admin
 * @Date: 2022/12/8 15:33
 * @Description:
 */
public interface MerchantService {
    /**
     * 根据ID查询详细信息
     * @param merchantId
     * @return
     * @throws BusinessException
     */
    MerchantDTO queryMerchantById(Long merchantId);

    /**
     * 查询租户下的商户
     * @param tenantId
     * @return
     */
    MerchantDTO queryMerchantByTenantId(Long tenantId) throws BusinessException;

    /**
     * @date: 2022/12/9 15:33
     * @description: 商户注册的接口，接收账号密码和手机号，为了可扩展性使用merchantDto接收数据
     * @Param merchantDTO:  merchantDTO商户注册的信息
     * @return: com.shanjupay.merchant.api.dto.MerchantDTO 注册成功的商户信息
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请
     * @param merchantId 商户id
     * @param merchantDTO 资质申请信息
     * @throws BusinessException
     */
    public void applyMerchant(Long merchantId,MerchantDTO merchantDTO) throws BusinessException;

    /**
     * @description: 新增门店
     * @Param storeDTO:
     * @return: com.shanjupay.merchant.api.dto.StoreDTO
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    /**
     * 商户新增员工
     * @param staffDTO
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    /**
     * 为门店设置管理员
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;

    /**
     * 分页条件查询商户下门店
     * @param storeDTO 商户id
     * @param pageNo 页码
     * @param pageSize 每页的记录数
     * @return
     */
    PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize);

}
