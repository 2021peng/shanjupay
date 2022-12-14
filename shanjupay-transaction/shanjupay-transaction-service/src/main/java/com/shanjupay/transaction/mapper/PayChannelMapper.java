package com.shanjupay.transaction.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.transaction.entity.PayChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface PayChannelMapper extends BaseMapper<PayChannel> {

}
