/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.pojo;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 10:48
 */
public interface ResultHandle {
    void handle(Packet packet, Result result);
}
