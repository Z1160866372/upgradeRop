package com.richeninfo.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * @Author : zhouxiaohu
 * @create 2022/12/14 15:41
 */
@Component
@Slf4j
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public RedisUtil() {
    }

    public void set(String key, String value) throws Exception {
        this.stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setObject(String key, Object value) throws Exception {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void setexObject(String key, Object value, long time) throws Exception {
        this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    public Object getObject(String key) throws Exception {
        return this.redisTemplate.opsForValue().get(key);
    }

    public String get(String key)  {
        return (String)this.stringRedisTemplate.opsForValue().get(key);
    }

    public void setex(String key, String value, long time) throws Exception {
        this.stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    public void hset(String key, String field, String value) throws Exception {
        this.redisTemplate.opsForHash().put(key, field, value);
    }

    public String hget(String key, String field) throws Exception {
        return (String)this.redisTemplate.opsForHash().get(key, field);
    }

    public Long incr(String key) throws Exception {
        return this.redisTemplate.opsForValue().increment(key, 1L);
    }

    public boolean hasKey(String key) throws Exception {
        return this.redisTemplate.hasKey(key);
    }

    public void setex(String key, long value) throws Exception {
        this.redisTemplate.opsForValue().increment(key, value);
    }

    public void expire(String key, Integer seconds) throws Exception {
        this.redisTemplate.expire(key, (long)seconds, TimeUnit.SECONDS);
    }

    public List<String> smembers(String key) throws Exception {
        List<String> list = (List)this.redisTemplate.opsForValue().get(key);
        return list;
    }

    public void setValToList(String key, String val) throws Exception {
        this.redisTemplate.opsForSet().add(key, new Object[]{val});
    }

    public void setValsToList(String key, String[] val) throws Exception {
        this.redisTemplate.opsForSet().add(key, val);
    }

    public Set<Object> getSetmembers(String key) throws Exception {
        return this.redisTemplate.opsForSet().members(key);
    }

    public void delRedisByKey(String key)  {
        this.redisTemplate.delete(key);
    }

    public Set<String> keys(String val) throws Exception {
        return this.redisTemplate.keys(val);
    }

    public boolean keyExist(String key) {
        Long size = this.redisTemplate.opsForList().size(key);
        return size >= 1L;
    }

    public void setValToListLeftOne(String key, String value) {
        this.redisTemplate.opsForList().leftPush(key, value);
    }

    public void getValToListLeftOne(String key) {
        this.redisTemplate.opsForList().leftPop(key, 2L, TimeUnit.SECONDS);
    }

    /**
     * 设置List类型对象专用
     * @param key
     * @param value
     * @param <T>
     */
    public <T> Long setValToListLeft(String key, List<T> value) {
        return this.redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 获得List类型对象专用
     * @param key
     * @return
     */
    public Object getValToListLeft(String key) {
        return this.redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 设置List类型对象专用
     * @param key
     * @param value
     * @param <T>
     */
    public <T> Long setValToListRight(String key, List<T> value) {
        return this.redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 获得List类型对象专用
     * @param key
     * @return
     */
    public Object getValToListRight(String key) {
        return this.redisTemplate.opsForList().rightPop(key);
    }
}
