package com.shanjupay.paymentagent.service;

import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: admin
 * @Date: 2023/2/11 13:27
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPayChannelAgentService {

    @Autowired
    PayChannelAgentService payChannelAgentService;

    @Test
    public void testQueryPayOrderByAli(){
//        String APP_ID = "";
//        String APP_PRIVATE_KEY = "";
//        String ALIPAY_PUBLIC_KEY = "";
//        String CHARSET = "UTF‐8";
        //应用id
        String APP_ID = "2021000121696908";
        //应用私钥
        String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCM7xJcCkwP9B+e/NErQCO2vwFUiVO+DTXyqwZXLbhOss6CndCbpb1rzZkXhbf+4654c2k+9DhZ+NoEnfwNEYcqgJaxUAaVL8/N1v793sSRaZ4phsrh+ghTLhjhsv1ul0IXOCC/IDsyQ1EiPQxl7+1ifjEbAGj2Lsabcu4PGYnDm0y0S+E0bc2wTu/2lUcRcQJ9HUp4/BzTilCW09SGft8zETXe8w0UbRx7z+9d/3n7nqKJektDgVK1rehdqPTGT4hFFIZc0pczJMPtFu6vajYsUrRE56OR2j/8+Z9N/nFz2OAtJIFdyz/M/kYc8H/nwqesJwCtknn+8YGBXzt0UINJAgMBAAECggEAHbRB5V5SO8aTZ5oXqAsDu/+0ExJCsTrtlPERDAzZ3NR5DDShBD1BmFgkHT4U0sdAPajtlNQ6kFQP94so1cRM/dtOZ57bVFRYcyUOZ4mtWoytRklft5NQb9+2ZNOp8MY2qtgsprvvXh+sGRBO7alNGkZIqU1D0id3usnVfR8/1xcwirDQFg9t9Owvs7seKDkNxQhP13EeMZOP6nNw97VjTU9N/omm/dJkB/+fV0u2Lo5c0Vsq/9nY0oR/bzDqSLCGBsDAKlwcUyMUu/jGRWEeIq5z+8wO54OhOch95FsrN8v1xj0h0PKf8SQ40I0yM0EzDbhIbEpgfcPhbfj6YyVaUQKBgQD3UUUzQ0VO8AtOkqEEXkU/FOx/tQ0INI+OujQtP6EWqCuACYdifnaBoIUFntQ0vhXaJ6IPXhC4bEbTwHGASRkq9CF8Om6wxy8uSBfer7x68lJppFw6xZXd5R0MnMtnTuuao2b9gPO7ZXW/w55wNeHnsORTs+03NvRw6pVXOTCB/QKBgQCR4bHDoNgPeITuGeYHcVY934sWpSahqjJZExFY5ZjgYcd+1HHiO37PAIOLYHtJy2o25rp7QtmxPiDlXC1KPZdat2fIOl3+au62Cij2MpUSk9uoTuLfleYu88hXOMTiwv5ngX6mNA0eEWy5yG1iSgwTMoU52rxbe8EbJ9wHZ6DSPQKBgHKqAmx3Bv5HOubOAstUrcRGfjPjjEw+mkRxPlmwQ46On8R4JoXa3jTRaHsnnXlqnBUOBQwc76GhyeDJO5ZfzcRlddTz/ngFkXFiyXZcggV6rgz+wCgbjZVYcXR0ibW3GHMUyV5FWDuNGd+z4ZRi1v8lu2a3aAcOepyTlnPfNwc9AoGACJy3XuI2/yrRRjE3OEdDnSF+V5bmYfLnSATtKD9duBltqpomIwH6KYRRP8AxZxjgK1+RsMcwjNsrG/9GGTTro4piURrJA8USVKLi6i3wSQl7lUMa97Ikxa7Uq/mI/g4e8MNfU7aM87J5pbokYwIWFmgMH28/YXM74juHLNS8VM0CgYAdziAzp1rOESIFtCLFBS79kcFLQxtGq9yhmamKVqDCuCrfN3wmI4vtwPCJsq2i5TCPev2Jj7UuB7sB5JU26EMCx05/e6xrDoCZ7UMxt68cuxf/q6vmxlEUT/bGwhqL1rVL3sxmmxW1LF7P44qUpWnQY3Fdm6QPy1sM9v9ZwM3R/g==";
        //支付宝公钥
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnfjtzhRfeJU013owyCjkKiwnp9z80Ym3mZjUDvup+/iPbLJNliDUFP0bnhchwgb+4xDr4eIGPzlg373+dWSd/QDpb6cieU05Z/SeQb73XAzhv3/oo+mKu2+6cJKkcY9NzirQEU1DH+Hfla2ckL72j9UPvfFOodZ2mZicl5MsA5g2MzykfWWKSMCFEiEerrB5E46Fc8AIm/Kez2z4UpxyF99lq0Gq+uSymIV5f03ckYZQcwkoY7c15naEN3fkyB6z3/PYrbBFDe55YyQgyxTcPtMkGuMxdu/5LkyomI9u/3isCZqF4N9601Gs7jhj3EA1a6Vxb9L84USnOQCj0XH+vwIDAQAB";
        String CHARSET = "utf-8";
        //正式"https://openapi.alipay.com/gateway.do"
        String serverUrl = "https://openapi.alipaydev.com/gateway.do";
        //支付渠道参数
        AliConfigParam aliConfigParam = new AliConfigParam();
        aliConfigParam.setUrl(serverUrl);
        aliConfigParam.setCharest(CHARSET);
        aliConfigParam.setAlipayPublicKey(ALIPAY_PUBLIC_KEY);
        aliConfigParam.setRsaPrivateKey(APP_PRIVATE_KEY);
        aliConfigParam.setAppId(APP_ID);
        aliConfigParam.setFormat("json");
        aliConfigParam.setSigntype("RSA2");

        PaymentResponseDTO paymentResponseDTO =
                payChannelAgentService.queryPayOrderByAli(aliConfigParam, "SJ1216325162370383873");
        System.out.println(paymentResponseDTO);
    }
}
