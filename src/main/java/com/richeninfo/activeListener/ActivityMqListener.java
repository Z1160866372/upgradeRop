/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.activeListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.PacketMq;
import com.richeninfo.pojo.Result;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/3/20 09:32
 */

@Log
@Component
public class ActivityMqListener {
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private RopServiceManager ropServiceManager;

    DateFormat df = new SimpleDateFormat("yyyy-MM");

    @JmsListener(destination="commonQueue" , containerFactory="queueListener")
    public void readActiveQueue(String message) {
        log.info("接收信息" + message);
        PacketMq mq = JSON.parseObject(message, PacketMq.class);
        ActivityUserHistory history = null;
        String keyword ="";
        try {
            if (mq == null) {
                return;
            }
            keyword = "wt_"+mq.getHistory().getActId()+"_history";
            if(mq.getHistory().getActId().equals("finance")){
                history = commonMapper.selectHistoryByUnlockedByCreateDate(mq.getHistory().getUserId(), mq.getHistory().getUnlocked(), mq.getHistory().getActId(),keyword, df.format(new Date()));
            }else if(mq.getHistory().getActId().equals("turntable")){
                history = commonMapper.selectActivityUserHistoryByUserType(mq.getHistory().getUserId(), mq.getHistory().getUnlocked(), mq.getHistory().getActId(),keyword,mq.getHistory().getUserType(),mq.getHistory().getModule());
            }else{
                history = commonMapper.selectHistoryByUnlocked(mq.getHistory().getUserId(), mq.getHistory().getUnlocked(), mq.getHistory().getActId(),keyword);
            }
            if (history == null || history.getStatus() == Constant.STATUS_RECEIVED || mq.getPacket()== null) {
                return;
            }
            String response_message = ropServiceManager.execute(mq.getPacket(), history.getUserId(),history.getActId());
            Result request;
            String code ="";
            String resCode="";
            request = JSON.parseObject(response_message, Result.class);
            code = request.getResponse().getErrorInfo().getCode();
            if(mq.getPacket().getApiCode().equals("ZTXD1000")){
                resCode = JSON.parseObject(request.getResponse().getRetInfo().getString("data")).getString("retCode");
                if(resCode.equals("000000")){
                    resCode="SUCCESS";
                }
            }else{
                resCode = request.getResponse().getRetInfo().getString("resultCode");
            }
            if (Constant.SUCCESS_CODE.equals(code)) {
                if (resCode.equals("SUCCESS")) {
                    history.setStatus(Constant.STATUS_RECEIVED);
                } else {
                    history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                }
            } else {
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            }
            history.setMessage(response_message);
            //history.setStatus(Constant.STATUS_RECEIVED);
            //history.setMessage("测试数据～");
            history.setCode(JSONObject.toJSONString(mq.getPacket()));
        } catch (Exception e) {
            e.printStackTrace();
            history.setCode(JSONObject.toJSONString(mq.getPacket()));
            history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            history.setMessage(e.getMessage());
        }
        commonMapper.updateHistory(history.getStatus(),history.getCode(),history.getMessage(),history.getId(),keyword);//更新用户发放状态
    }
}
