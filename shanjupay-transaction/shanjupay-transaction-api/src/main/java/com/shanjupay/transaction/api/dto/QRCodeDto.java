package com.shanjupay.transaction.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: admin
 * @Date: 2023/2/6 19:03
 * @Description:
 */

@Data
@NoArgsConstructor
public class QRCodeDto implements Serializable {
    private Long merchantId;
    private String appId;
    private Long storeId;
    private String subject;//商品标题
    private String body;//订单描述
}
