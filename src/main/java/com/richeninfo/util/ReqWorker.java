/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import com.alibaba.fastjson.JSON;
import com.richeninfo.pojo.PacketQueue;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.ResultHandle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 10:38
 */
@Component
public class ReqWorker implements Runnable {
    @Resource
    private RopServiceManager ropServiceManager;

    @Override
    public void run() {
        while (true) {
            PacketQueue packetQueue = null;
            try {
                packetQueue = ReqQueue.in.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebApplicationContext wac = null;
            try {
                wac = ContextLoader.getCurrentWebApplicationContext();
                String message = "";
                String openapi = "y";
                if ("y".equals(openapi)) {
                    message = ropServiceManager.execute(packetQueue.getPacket(), "","");
                } else {
                    message = "{\"Response\":{\"ErrorInfo\":{\"Message\":\"成功\",\"Hint\":\"成功\",\"Code\":\"0000\"},\"RetInfo\":{\"CheckResultInfo\":[{\"Result\":\"1\",\"OfferId\":\"380000187851\",\"Code\":\"0000\",\"Reason\":\"成功\"}]}}}";
                }
                message = replaceMessage(message);
                Result result = JSON.parseObject(message, Result.class);
                ResultHandle resultHandle = (ResultHandle) wac.getBean(packetQueue.getHandleCode());
                resultHandle.handle(packetQueue.getPacket(), result);

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    ReqQueue.in.put(packetQueue);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String replaceMessage(String message) {
        message = message.replaceAll("用户前项限制:", "");
        message = message.replaceAll("亲爱的用户，您未开通移动数据流量策划组,请先开通移动数据流量策划组再申请10元闲时包特惠包！", "对不起，您不符合参加此次活动的资格，感谢您的关注！");
        message = message.replaceAll("亲爱的用户，您未开通移动数据流量策划组,请先开通移动数据流量策划组再申请移动数据10元闲时流量包！", "对不起，您不符合参加此次活动的资格，感谢您的关注！");
        return message;
    }

}
