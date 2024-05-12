/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.config;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
/**
 * @Author : zhouxiaohu
 * @create 2024/3/19 13:52
 */
@Configuration
public class ActiveMQConfig{
/*
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String userName;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${spring.activemq.queue-name}")
    private String queueName;

    @Value("${spring.activemq.topic-name}")
    private String topicName;

    @Bean
    public Queue queue() {
        System.out.println("注入队列实例：" + queueName);
        return new ActiveMQQueue(queueName);
    }

    @Bean
    public Topic topic() {
        System.out.println("注入广播实例：" + topicName);
        return new ActiveMQTopic(topicName);
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory(userName, password, brokerUrl);
    }

    // 在Queue模式中，对消息的监听需要对containerFactory进行配置
    @Bean("queueListener")
    public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
    }

  *//*  @Bean
    @ConditionalOnMissingBean(
            name = {"queueListener"}
    )
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQConnectionFactory connectionFactory) {
        //如果用自定义bean,高版本需要添加这行,否则会报错
        connectionFactory.setTrustAllPackages(true);
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }
*//*


    @Bean(name = "topicListener")
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory  connectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        //设置广播模式，这样才能接收到topic 发送的消息
        bean.setPubSubDomain(true);
        return bean;
    }*/
}


