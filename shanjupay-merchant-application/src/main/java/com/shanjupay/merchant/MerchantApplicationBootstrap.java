package com.shanjupay.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant
 * @Classname: MerchantApplicationBootstrap
 * @Author: admin
 * @Date: 2022/12/7 15:23
 * @Description:
 */

@SpringBootApplication
@EnableDiscoveryClient
public class  MerchantApplicationBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplicationBootstrap.class,args);
    }
}
