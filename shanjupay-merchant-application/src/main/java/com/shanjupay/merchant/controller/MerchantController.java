package com.shanjupay.merchant.controller;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantRegisterConvert;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant.controller
 * @Classname: MerchantController
 * @Author: admin
 * @Date: 2022/12/8 15:49
 * @Description:
 */

@RestController
@Api(value = "商户平台应用接口", tags = "商户平台应用接口", description = "商户平台应用接口")
public class MerchantController {

    @Reference //注入远程调用的接口
    private MerchantService merchantService;

    @Autowired //将本地的bean注入进来
    SmsService smsService;

    @ApiOperation(value = "根据id查询商户")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id){
        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }

    /*
    @ApiOperation("测试")
    @GetMapping(path = "/hello")
    public String hello(){
        return "hello";
    }

    @ApiOperation("测试")
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string")
    @PostMapping(value = "/hi")
    public String hi(String name) {
        return "hi,"+name;
    }
     */


    @ApiOperation("获取手机验证码")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String",
            paramType = "query")
    @GetMapping("/sms")
    public String getSMSCode(@RequestParam String phone) {
        //向手机发送验证码
        return smsService.sendMsg(phone);
    }

    @ApiOperation("注册商户")
    @ApiImplicitParam(name = "merchantRegister", value = "注册信息", required = true, dataType =
            "MerchantRegisterVO", paramType = "body")
    @PostMapping("/merchants/register")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegister)
    {
        //测试系统异常
//        int i = 1/0;
        //校验验证码
        smsService.checkVerifiyCode(merchantRegister.getVerifiykey(),
                merchantRegister.getVerifiyCode());
        //调用Dubbo接口，注册商户
//        MerchantDTO merchantDTO = new MerchantDTO();
        //向dto写入商户注册的信息
//        merchantDTO.setUsername(merchantRegister.getUsername());
//        merchantDTO.setMobile(merchantRegister.getMobile());

        //使用MapStruct转换对象
        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegister);
        merchantService.createMerchant(merchantDTO);
        return merchantRegister;
    }
}
