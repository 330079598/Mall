package com.stone.mall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 使用Nacos作为配置中心统一管理配置
 * （1）、引入依赖（注意新版本的依赖问题）
 * （2）、创建一个bootstrap.properties
 * （3）、给配置中心默认添加一个数据集（Data Id）：mall-coupon.properties。默认规则：应用名.properties
 * （4）给应用名添加配置
 * （5）动态获取配置
 *      @RefreshScope:动态获取并刷新配置
 *      @Value("${配置项的名字}“）：获取到配置
 *      配置中心的的配置优先使用
 *
 */

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class MallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCouponApplication.class, args);
    }
}
