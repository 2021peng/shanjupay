package com.shanjupay.merchant.service.impl;

import com.shanjupay.merchant.dto.StoreDTO;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanjupay.merchant.service.IStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2022-12-09
 */
@Slf4j
@Service
@Transactional
public class StoreServiceImpl extends ServiceImpl<StoreMapper, StoreDTO> implements IStoreService {

}
