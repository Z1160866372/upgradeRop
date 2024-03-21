package com.richeninfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
@EnableConfigurationProperties // 允许自定义配置，也就是让MQConfig生效
@EnableJms //启动消息队列
public class RopApplication {

    public static void main(String[] args) {
        SpringApplication.run(RopApplication.class, args);
    }

}
