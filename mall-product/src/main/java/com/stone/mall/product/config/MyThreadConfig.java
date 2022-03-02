package com.stone.mall.product.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author: stone
 * @Description: 配置线程池
 * Documention: https://docs.spring.io/spring-boot/docs/1.2.3.RELEASE/reference/htmlsingle/
 **/

// 开启属性配置
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {

	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties) {

		return new ThreadPoolExecutor(threadPoolConfigProperties.getCoreSize(),
				threadPoolConfigProperties.getMaxSize(),
				threadPoolConfigProperties.getKeepAliveTime(), TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());
	}
}
