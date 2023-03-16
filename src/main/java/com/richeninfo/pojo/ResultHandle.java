package com.richeninfo.pojo;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 10:48
 */
public interface ResultHandle {
    void handle(Packet packet, Result result);
}
