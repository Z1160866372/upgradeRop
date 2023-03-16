package com.richeninfo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/22 14:18
 */
@Configuration
public class DirectRabbitConfig {

    //队列 起名：ProMemberQueue
    @Bean
    public Queue ProMemberQueue() {
        return new Queue("ProMemberQueue",true);
    }
//    //Direct交换机 起名：TestDirectExchange
//    @Bean
//    DirectExchange TestDirectExchange() {
//        return new DirectExchange("TestDirectExchange");
//    }
//
//    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
//    @Bean
//    Binding bindingDirect() {
//        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
//    }
}
