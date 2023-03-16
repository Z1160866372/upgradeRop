package com.richeninfo.util;

import com.richeninfo.pojo.PacketQueue;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 10:44
 */
@Component
public class ReqQueue {

    public static BlockingQueue<PacketQueue> in = new LinkedBlockingQueue<PacketQueue>();

}
