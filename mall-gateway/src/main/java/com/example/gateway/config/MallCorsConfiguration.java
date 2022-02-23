package com.example.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author: stone
 * @Title: MallCorsConfiguration
 * @date: 2022/1/8 16:29
 * @Description: 处理跨域请求
 */

@Configuration
public class MallCorsConfiguration {

    @Bean // 添加过滤器
    public CorsWebFilter corsWebFilter(){
        // 基于URL跨域，选择reactive包
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 配置跨域信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许跨域的头
        corsConfiguration.addAllowedHeader("*");
        // 允许跨域的请求方式
        corsConfiguration.addAllowedMethod("*");
        // 允许跨域请求的来源
//        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOriginPattern("*");
        // 是否允许携带cookie跨域
        corsConfiguration.setAllowCredentials(true);
        // 任意URL都要进行跨域配置
        source.registerCorsConfiguration("/**",corsConfiguration); //所有请求都允许
        return  new CorsWebFilter(source);
    }
}
