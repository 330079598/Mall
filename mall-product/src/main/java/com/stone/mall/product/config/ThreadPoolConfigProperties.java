package com.stone.mall.product.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "mall.thread")
@Component
public class ThreadPoolConfigProperties {

	private Integer coreSize;
	private Integer maxSize;

	private Integer keepAliveTime;
}
