package com.shanjupay.merchant.controller;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.controller
 * @Classname: MerchantController
 * @Author: admin
 * @Date: 2022/12/8 15:49
 * @Description:
 */

@RestController
public class MerchantController {

    @Reference
    private MerchantService merchantService;

    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id){
        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }
}
