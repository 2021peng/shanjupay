package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.QRCodeUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Author: admin
 * @Date: 2023/2/5 21:11
 * @Description:
 */

@Api(value = "商户平台‐门店管理", tags = "商户平台‐门店管理", description = "商户平台‐门店的增删改查")
@RestController
public class StoreController {

    @Reference
    MerchantService merchantService;

    @Reference
    TransactionService transactionService;

    @ApiOperation("分页条件查询商户下门店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query")
    })
    @PostMapping("/my/stores/merchants/page")
    public PageVO<StoreDTO> queryStoreByPage(
            @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        //获取商户id
        Long merchantId = SecurityUtil.getMerchantId();
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchantId);
        //分页查询
        PageVO<StoreDTO> stores = merchantService.queryStoreByPage(storeDTO, pageNo, pageSize);
        return stores;
    }

    /**
     * 门店二维码订单标题
     */
    @Value("${shanjupay.c2b.subject}")
    private String subject;
    /**
     * 门店二维码订单内容
     */
    @Value("${shanjupay.c2b.body}")
    private String body;

    @ApiOperation("生成商户应用门店二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "商户应用id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "storeId", value = "商户门店id", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping(value = "/my/apps/{appId}/stores/{storeId}/app-store-qrcode")
    public String createCScanBStoreQRCode(@PathVariable String appId, @PathVariable Long storeId) throws BusinessException {
        //商户id
        Long merchantId = SecurityUtil.getMerchantId();
        //生成二维码链接
        QRCodeDto qrCodeDto = new QRCodeDto();
        qrCodeDto.setMerchantId(merchantId);
        qrCodeDto.setAppId(appId);
        qrCodeDto.setStoreId(storeId);
        //标题
        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);
        //"%s 商品"
        qrCodeDto.setSubject(String.format(subject,merchantDTO.getMerchantName()));
        //内容,格式："向%s 付款"
        qrCodeDto.setBody(String.format(body,merchantDTO.getMerchantName()));
        String storeQRCodeUrl = transactionService.createStoreQRCode(qrCodeDto);
//        log.info("[merchantId:{},appId:{},storeId:{}]createCScanBStoreQRCode is {}",merchantId,appId,storeId,storeQRCodeUrl);
        try {
            //根据返回url，调用生成二维码工具类，生成二维码base64返回
            return QRCodeUtil.createQRCode(storeQRCodeUrl, 200, 200);
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.E_200007);
        }
    }

}
