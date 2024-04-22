/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 16:12
 */
@Component
public class JedisPoolUtils {
    private static JedisPool pool;

    public static final String AUTH = "h1e2j3i4a5o6s7h8o9u0!@#$%^&*";
   // public static final String AUTH = "";

    /**
     * 建立连接池 真实环境，一般把配置参数缺抽取出来。
     */
    private static void createJedisPool() {

        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();

        // 设置最大连接数
        config.setMaxTotal(300);

        // 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxWaitMillis(10000);

        // 设置空间连接
        config.setMaxIdle(30);

        config.setTestOnBorrow(true);

        // 创建连接池
//		pool = new JedisPool(config, "172.16.176.78", 6379, 10000, AUTH);
       // pool = new JedisPool(config, "127.0.0.1", 6379, 10000);
	pool = new JedisPool(config, "121.40.205.162", 6379, 10000, AUTH);


    }

    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (pool == null)
            createJedisPool();
    }

    /**
     * 获取一个jedis 对象
     *
     * @return
     */
    public static Jedis getJedis() {
        if (pool == null)
            poolInit();
        return pool.getResource();
    }

    /**
     * 归还一个连接
     *
     * @param jedis
     */
    public static void returnRes(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static Object unSerializable(byte[] binaryByte) {
        if (binaryByte == null) {
            return null;
        }
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        byteArrayInputStream = new ByteArrayInputStream(binaryByte);
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object obj = objectInputStream.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] getByte = byteArrayOutputStream.toByteArray();
            return getByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
