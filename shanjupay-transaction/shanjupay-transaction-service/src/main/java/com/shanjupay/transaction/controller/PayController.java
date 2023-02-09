package com.shanjupay.transaction.controller;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.ParseURLPairUtil;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: admin
 * @Date: 2023/2/8 20:18
 * @Description:
 */

@Slf4j
@Controller
public class PayController {
    /**
     * @description: 支付入口
     * @Param ticket: 对json数据及进行base64编码
     * @Param request:
     * @return: java.lang.String
     */
    @RequestMapping("/pay-entry/{ticket}")
    public String payEntry(@PathVariable("ticket") String ticket, HttpServletRequest request) throws Exception {

        //将对象转成url格式
//        String params = ParseURLPairUtil.parseURLPair(order);
//        return "forward:/pay‐page?" + params;
//        return "forward:/pay-page";
        try {
            //将ticket的base64还原
            String ticketStr = EncryptUtil.decodeUTF8StringBase64(ticket);
            //将ticket（json）转成对象
            PayOrderDTO order = JSON.parseObject(ticketStr, PayOrderDTO.class);
            //将对象转成url格式
            // String url = toParamsString(order);
            BrowserType browserType = BrowserType.valueOfUserAgent(request.getHeader("user-agent"));
            switch (browserType) {
                case ALIPAY: //直接跳转收银台pay.html
                    return "forward:/pay-page?" + ParseURLPairUtil.parseURLPair(order);
                case WECHAT: //获取授权码(待实现)
                    return "forward:/pay-page?" + ParseURLPairUtil.parseURLPair(order);
                default:
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        return "forward:/pay-page-error";

    }
}
