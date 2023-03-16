package com.richeninfo.rabbitListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.mapper.master.ProMemberMapper;
import com.richeninfo.pojo.*;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Log
public class messageListener {

    @Resource
    private ProMemberMapper proMemberMapper;
    @Resource
    private RopServiceManager ropServiceManager;
    /**
     * PRO会员监听事件
     */
   @RabbitListener(queuesToDeclare = @Queue("ProMemberQueue"))
    public void ReceiveMessageProMember(String  message){
       log.info("接收信息"+message);
       PacketMq mq = JSON.parseObject(message, PacketMq.class);
       ActivityUserHistory history = null;
       try {
            if(mq==null){
                return;
            }
            history =proMemberMapper.selectHistoryByUnlocked(mq.getHistory().getUserId(), mq.getHistory().getUnlocked(), mq.getHistory().getActId());
            if (history==null||history.getStatus()== Constant.STATUS_RECEIVED||mq.getRequest()==null){
                return;
            }
            String response_message= ropServiceManager.execute(mq.getPacket(),history.getUserId());
            Result request = JSON.parseObject(response_message,Result.class);
            String code = request.getResponse().getErrorInfo().getCode();
            String resCode = request.getResponse().getRetInfo().getString("resultCode");
            if(Constant.SUCCESS_CODE.equals(code)){
                if(Constant.SUCCESS.equals(resCode)){
                    history.setStatus(Constant.STATUS_RECEIVED);
                }else{
                    history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                }
            }else{
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            }
            history.setMessage(response_message);
            history.setCode(JSONObject.toJSONString(mq.getPacket()));
       }catch (Exception e){
            e.printStackTrace();
            history.setStatus(Constant.STATUS_RECEIVED_ERROR);
            history.setMessage(e.getMessage());
       }
       proMemberMapper.updateHistory(history);//更新用户发放状态
    }
}
