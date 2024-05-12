/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.FinanceMapper;
import com.richeninfo.entity.mapper.mapper.master.NewCallMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FinanceService;
import com.richeninfo.service.NewCallService;
import com.richeninfo.util.IPUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/30 14:04
 */
@Service
@Slf4j
public class NewCallServiceImpl implements NewCallService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private NewCallMapper newCallMapper;
    @Resource
    private PacketHelper packetHelper;
    @Resource
    private RopServiceManager ropService;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private CommonService commonService;
    @Resource
    private HttpServletRequest request;
    SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 初始化用户
     *
     * @param user
     * @return
     */
    public ActivityUser insertUser(ActivityUser user) {
        ActivityUser select_user = newCallMapper.selectUserByCreateDate(user.getUserId());
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            newCallMapper.insertUser(new_user);
            user = new_user;
        } else {
            select_user.setSecToken(user.getSecToken());
            user = select_user;
        }
        return user;
    }

    @Override
    public List<ActivityConfiguration>  getConfiguration(String secToken, String actId, String channelId) {
        List<ActivityConfiguration> pro_config = commonMapper.selectActivityConfigurationByActId(actId);
        String mobile="";
        ActivityUserHistory userHistory = null;
        if(pro_config.size()>0){
            if (!secToken.isEmpty()) {
                mobile= commonService.getMobile(secToken,channelId);
            }
            for (ActivityConfiguration config : pro_config) {
                if(config.getTypeId()==4){
                    userHistory=newCallMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked());
                    if(userHistory!=null&&userHistory.getStatus()==3){//已办理
                        config.setStatus(2);
                    }else{//去办理
                        config.setStatus(0);
                    }
                }else{//权益 跳转
                    config.setStatus(0);
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        ActivityConfiguration config =null;
        if (!secToken.isEmpty()) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        ActivityUserHistory userHistory  =newCallMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
        if(userHistory!=null){
            if(userHistory.getStatus()==3){//已办理
                object.put(Constant.MSG,"ybl");
            }else{
                config = commonMapper.selectActivityConfiguration(actId,unlocked);
                object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc);
            }
        }else{
            config = commonMapper.selectActivityConfiguration(actId,unlocked);
            saveHistory(actId,channelId,mobile,config);
            userHistory  =newCallMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
            object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc);
        }
        return object;
    }

    public JSONObject transact3066Business(ActivityUserHistory history,ActivityConfiguration config,String randCode,String channelId,String wtAcId, String wtAc) {
        JSONObject object = new JSONObject();
        boolean transact_result = false;
        Result result = new Result();
        try {
            List<VasOfferInfo> offerList = new ArrayList<VasOfferInfo>();
            if(config.getActivityId().contains(",")){
                for (int i = 0; i < config.getActivityId().split(",").length; i++) {
                    VasOfferInfo vasOfferInfo = new VasOfferInfo();
                    vasOfferInfo.setOfferId(config.getActivityId().split(",")[i]);
                    vasOfferInfo.setEffectiveType("0");
                    vasOfferInfo.setOperType("0");
                    offerList.add(vasOfferInfo);
                }
            }else{
                VasOfferInfo vasOfferInfo = new VasOfferInfo();
                vasOfferInfo.setOfferId(config.getActivityId());
                vasOfferInfo.setEffectiveType("0");
                vasOfferInfo.setOperType("0");
                offerList.add(vasOfferInfo);
            }
            Packet packet = packetHelper.getCommitPacket306602(history.getUserId(),randCode, offerList, channelId);
           /* String message = ropService.execute(packet,history.getUserId());
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message,Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");
            if(Constant.SUCCESS_CODE.equals(res)){
                transact_result = true;
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
            }else{
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }*/
            if (true) {
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }
            history.setMessage("");
            history.setCode(JSON.toJSONString(packet));
            object.put("res", "0000");
            object.put("DoneCode", "12343242343A");
            object.put("update_history", JSON.toJSONString(history));
            newCallMapper.updateHistory(history);
            Packet new_packet = packetHelper.orderReporting(config,packet,wtAcId,wtAc);
            System.out.println(new_packet.toString());
          /*  String result_String =ropService.execute(new_packet, history.getUserId());*/
            ActivityOrder order = new ActivityOrder();
            order.setName(commonMapper.selectActivityByActId(config.getActId()).getName());
            String packetThirdTradeId= packet.getPost().getPubInfo().getTransactionId();
            order.setThirdTradeId(packetThirdTradeId);
            order.setOrderItemId("JYRZ"+packetThirdTradeId.substring(packetThirdTradeId.length()-21));
            order.setBossId(config.getActivityId());
            order.setCommodityName(config.getName());
            order.setUserId(history.getUserId());
            order.setCode(JSONArray.fromObject(new_packet).toString());
           /* order.setMessage(result_String);*/
            order.setChannelId(channelId);
            commonMapper.insertActivityOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }

    private void saveHistory(String actId, String channelId, String mobile, ActivityConfiguration activityConfiguration) {
        ActivityUserHistory newHistory = new ActivityUserHistory();
        newHistory.setUserId(mobile);
        newHistory.setChannelId(channelId);
        newHistory.setRewardName(activityConfiguration.getName());
        newHistory.setTypeId(activityConfiguration.getTypeId());
        newHistory.setUnlocked(activityConfiguration.getUnlocked());
        newHistory.setCreateDate(day.format(new Date()));
        newHistory.setCreateTime(df.format(new Date()));
        newHistory.setValue(activityConfiguration.getValue());
        newHistory.setActId(actId);
        newCallMapper.insertActivityUserHistory(newHistory);
    }

    /**
     * 保存用户操作记录
     * @param operationLog
     */
    @Override
    public JSONObject insertOperationLog(OperationLog operationLog) {
        JSONObject object = new JSONObject();
        if (!operationLog.getSecToken().isEmpty()) {
            operationLog.setUserId(commonService.getMobile(operationLog.getSecToken(), operationLog.getChannelId()));
            operationLog.setAddress(IPUtil.getRealRequestIp(request));
            operationLog.setName(commonMapper.selectActivityByActId(operationLog.getActId()).getName());
            newCallMapper.insertOperationLog(operationLog);
            object.put("operationLog",operationLog);
            object.put(Constant.MSG,Constant.SUCCESS);
        }else{
            object.put(Constant.MSG,"noMobile");
        }
        return object;
    }
}
