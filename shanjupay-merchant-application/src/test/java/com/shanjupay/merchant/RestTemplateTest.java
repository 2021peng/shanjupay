package com.shanjupay.merchant;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @BelongsProject: shanjupay
 * @BelongsPackage: com.shanjupay.merchant
 * @Classname: RestTemplateTest
 * @Author: admin
 * @Date: 2022/12/9 11:12
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RestTemplateTest {

    @Autowired
    RestTemplate restTemplate;

    //获取网页内容
    @Test
    public void gethtml(){
        String url = "http://www.baidu.com/";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String body = forEntity.getBody();
        System.out.println(body);
    }
}
