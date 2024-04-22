package com.richeninfo.activeListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.mapper.master.ExteroceptiveMapper;
import com.richeninfo.entity.mapper.mapper.master.ProMemberMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.PacketMq;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log
@Component
//@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable",havingValue = "true")
public class ExteroceptiveListener {
    @Resource
    private ExteroceptiveMapper exteroceptiveMapper;
    @JmsListener(destination="proemMQ")
    public void readActiveQueue(String message) {
        log.info("proemMQ接收信息======" + message);
        PacketMq mq = JSON.parseObject(message, PacketMq.class);
        ActivityUserHistory history = null;
        try {
            if (mq == null) {
                return;
            }
            history = exteroceptiveMapper.findHistoryById(mq.getId());
            if (history == null || history.getStatus() == Constant.STATUS_RECEIVED || mq.getRequest() == null) {
                return;
            }
           /* String response_message = ropServiceManager.execute(mq.getPacket(), history.getUserId());
            Result request = JSON.parseObject(response_message, Result.class);
            String code = request.getResponse().getErrorInfo().getCode();
            String resCode = request.getResponse().getRetInfo().getString("resultCode");
            if (Constant.SUCCESS_CODE.equals(code)) {
                if (Constant.SUCCESS.equals(resCode)) {
                    history.setStatus(Constant.STATUS_RECEIVED);
                } else {
                    history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                }
            } else {
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            }
            history.setMessage(response_message);*/
            history.setCode(JSONObject.toJSONString(mq.getPacket()));
        } catch (Exception e) {
            e.printStackTrace();
            history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            history.setMessage(e.getMessage());
        }
        exteroceptiveMapper.updateHistory(history);//更新用户发放状态
    }
}
