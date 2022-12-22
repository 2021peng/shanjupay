package com.shanjupay.transaction.config;

import com.shanjupay.common.cache.Cache;
import com.shanjupay.transaction.common.util.RedisCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: admin
 * @Date: 2022/12/22 19:48
 * @Description:redis 的配置类
 */

@Configuration
public class RedisConfig {

    @Bean
    public Cache cache(StringRedisTemplate redisTemplate){
        return new RedisCache(redisTemplate);
    }
}
