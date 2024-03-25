/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * @Author : zhouxiaohu
 * @create 2022/12/14 15:18
 * 定义接口恶意请求多次注解
 */
@Retention(RUNTIME)//表示它在运行时
@Target(METHOD) //表示它只能放在方法上
public @interface AccessLimit {
    int seconds();//规定几秒

    int maxCount();//最大请求数
}
