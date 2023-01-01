package com.shanjupay.transaction.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: admin
 * @Date: 2023/1/1 16:02
 * @Description: 支付宝接口对接的测试类
 */

@Slf4j
@Controller
public class PayTestController {
    //应用id
    String APP_ID = "2021000121696908";
    //应用私钥
    String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCM7xJcCkwP9B+e/NErQCO2vwFUiVO+DTXyqwZXLbhOss6CndCbpb1rzZkXhbf+4654c2k+9DhZ+NoEnfwNEYcqgJaxUAaVL8/N1v793sSRaZ4phsrh+ghTLhjhsv1ul0IXOCC/IDsyQ1EiPQxl7+1ifjEbAGj2Lsabcu4PGYnDm0y0S+E0bc2wTu/2lUcRcQJ9HUp4/BzTilCW09SGft8zETXe8w0UbRx7z+9d/3n7nqKJektDgVK1rehdqPTGT4hFFIZc0pczJMPtFu6vajYsUrRE56OR2j/8+Z9N/nFz2OAtJIFdyz/M/kYc8H/nwqesJwCtknn+8YGBXzt0UINJAgMBAAECggEAHbRB5V5SO8aTZ5oXqAsDu/+0ExJCsTrtlPERDAzZ3NR5DDShBD1BmFgkHT4U0sdAPajtlNQ6kFQP94so1cRM/dtOZ57bVFRYcyUOZ4mtWoytRklft5NQb9+2ZNOp8MY2qtgsprvvXh+sGRBO7alNGkZIqU1D0id3usnVfR8/1xcwirDQFg9t9Owvs7seKDkNxQhP13EeMZOP6nNw97VjTU9N/omm/dJkB/+fV0u2Lo5c0Vsq/9nY0oR/bzDqSLCGBsDAKlwcUyMUu/jGRWEeIq5z+8wO54OhOch95FsrN8v1xj0h0PKf8SQ40I0yM0EzDbhIbEpgfcPhbfj6YyVaUQKBgQD3UUUzQ0VO8AtOkqEEXkU/FOx/tQ0INI+OujQtP6EWqCuACYdifnaBoIUFntQ0vhXaJ6IPXhC4bEbTwHGASRkq9CF8Om6wxy8uSBfer7x68lJppFw6xZXd5R0MnMtnTuuao2b9gPO7ZXW/w55wNeHnsORTs+03NvRw6pVXOTCB/QKBgQCR4bHDoNgPeITuGeYHcVY934sWpSahqjJZExFY5ZjgYcd+1HHiO37PAIOLYHtJy2o25rp7QtmxPiDlXC1KPZdat2fIOl3+au62Cij2MpUSk9uoTuLfleYu88hXOMTiwv5ngX6mNA0eEWy5yG1iSgwTMoU52rxbe8EbJ9wHZ6DSPQKBgHKqAmx3Bv5HOubOAstUrcRGfjPjjEw+mkRxPlmwQ46On8R4JoXa3jTRaHsnnXlqnBUOBQwc76GhyeDJO5ZfzcRlddTz/ngFkXFiyXZcggV6rgz+wCgbjZVYcXR0ibW3GHMUyV5FWDuNGd+z4ZRi1v8lu2a3aAcOepyTlnPfNwc9AoGACJy3XuI2/yrRRjE3OEdDnSF+V5bmYfLnSATtKD9duBltqpomIwH6KYRRP8AxZxjgK1+RsMcwjNsrG/9GGTTro4piURrJA8USVKLi6i3wSQl7lUMa97Ikxa7Uq/mI/g4e8MNfU7aM87J5pbokYwIWFmgMH28/YXM74juHLNS8VM0CgYAdziAzp1rOESIFtCLFBS79kcFLQxtGq9yhmamKVqDCuCrfN3wmI4vtwPCJsq2i5TCPev2Jj7UuB7sB5JU26EMCx05/e6xrDoCZ7UMxt68cuxf/q6vmxlEUT/bGwhqL1rVL3sxmmxW1LF7P44qUpWnQY3Fdm6QPy1sM9v9ZwM3R/g==";
    //支付宝公钥
    String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnfjtzhRfeJU013owyCjkKiwnp9z80Ym3mZjUDvup+/iPbLJNliDUFP0bnhchwgb+4xDr4eIGPzlg373+dWSd/QDpb6cieU05Z/SeQb73XAzhv3/oo+mKu2+6cJKkcY9NzirQEU1DH+Hfla2ckL72j9UPvfFOodZ2mZicl5MsA5g2MzykfWWKSMCFEiEerrB5E46Fc8AIm/Kez2z4UpxyF99lq0Gq+uSymIV5f03ckYZQcwkoY7c15naEN3fkyB6z3/PYrbBFDe55YyQgyxTcPtMkGuMxdu/5LkyomI9u/3isCZqF4N9601Gs7jhj3EA1a6Vxb9L84USnOQCj0XH+vwIDAQAB";
    String CHARSET = "utf-8";
    //支付宝接口的网关地址,正式"https://openapi.alipay.com/gateway.do"
    String serverUrl = "https://openapi.alipaydev.com/gateway.do";
    //签名算法类型
    String sign_type = "RSA2";

    @GetMapping("/alipaytest")
    public void alipaytest(HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws ServletException, IOException {
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, APP_ID,
                APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\"20150320010101002\"," +
                "    \"total_amount\":0.01," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");//填充业务参数

        String form = null; //调用SDK生成表单
        try {
            //请求支付宝下单接口,发起http请求
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
    }

}
