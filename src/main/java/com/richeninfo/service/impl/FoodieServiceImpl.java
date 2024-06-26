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
import com.richeninfo.entity.mapper.mapper.master.FoodieMapper;
import com.richeninfo.entity.mapper.mapper.master.ProtectMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FoodietService;
import com.richeninfo.service.ProtectService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class FoodieServiceImpl implements FoodietService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private FoodieMapper foodieMapper;
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
        ActivityUser select_user = foodieMapper.selectUserByCreateDate(user.getUserId());
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setDitch(user.getDitch());
            new_user.setCreateDate(day.format(new Date()));
            List<ActivityRoster> selectRoster = commonMapper.selectRoster(user.getUserId(),"foodie","wt_foodie_roster",1);
            if(!CollectionUtils.isEmpty(selectRoster)){
                new_user.setUserType(1);
            }
            foodieMapper.insertUser(new_user);
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
            if (!StringUtils.isEmpty(secToken)) {
                mobile= commonService.getMobile(secToken,channelId);
            }
            for (ActivityConfiguration config : pro_config) {
                if(config.getTypeId()==1){
                    ActivityUser select_user = foodieMapper.selectUserByCreateDate(mobile);
                    if(select_user!=null){
                        if(select_user.getUserType()>0){//黑名单
                            config.setStatus(3);
                        }else{
                            userHistory=foodieMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked());
                            if(userHistory!=null&&userHistory.getStatus()==3){//已办理
                                config.setStatus(2);
                            }else{//去办理
                                if(config.getAmount()>0){
                                    config.setStatus(0);
                                }else{//已抢完
                                    config.setStatus(1);
                                }
                            }
                        }
                    }

                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode,String ditch) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        ActivityConfiguration config =null;
        if (!StringUtils.isEmpty(secToken)) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        ActivityUser select_user = foodieMapper.selectUserByCreateDate(mobile);
        if(select_user!=null){
            if(select_user.getUserType()>0){
                object.put(Constant.MSG,"blackList");
                return object;
            }
        }
       /* if(!commonService.checkUserIsChinaMobile(mobile,actId)){
            object.put(Constant.MSG,"noShYd");
            return object;
        }*/
        ActivityUserHistory userHistory  =foodieMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
        if(userHistory!=null){
            if(userHistory.getStatus()==3){//已办理
                object.put(Constant.MSG,"ybl");
            }else{
                config = commonMapper.selectActivityConfiguration(actId,unlocked);
                object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc,ditch);
            }
        }else{
            config = commonMapper.selectActivityConfiguration(actId,unlocked);
            boolean result =  saveHistory(actId,channelId,mobile,config,ditch);
            if(result){
                userHistory  =foodieMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
                object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc,ditch);
            }else{
                object.put(Constant.MSG,"noNum");
            }
        }
        return object;
    }

    public JSONObject transact3066Business(ActivityUserHistory history,ActivityConfiguration config,String randCode,String channelId,String wtAcId, String wtAc,String ditch) {
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
            Packet packet = packetHelper.getCommitPacket306602(history.getUserId(),randCode, offerList, channelId,ditch);
            /*String message = ropService.execute(packet,history.getUserId(),history.getActId());
            message = ReqWorker.replaceMessage(message);
            result = JSON.parseObject(message,Result.class);
            String res = result.getResponse().getErrorInfo().getCode();
            String DoneCode = result.getResponse().getRetInfo().getString("DoneCode");*/
            String res = "1234";
            if(Constant.SUCCESS_CODE.equals(res)){
                transact_result = true;
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
            }else{
                transact_result = false;
                history.setStatus(Constant.STATUS_RECEIVED_ERROR);
                object.put(Constant.MSG, Constant.FAILURE);
            }
            history.setMessage(JSON.toJSONString(result));
            history.setCode(JSON.toJSONString(packet));
           /* object.put("res", res);
            object.put("DoneCode", DoneCode);*/
            object.put("res", "1234");
            object.put("DoneCode", "9999");
            object.put("update_history", JSON.toJSONString(history));
            foodieMapper.updateHistory(history);
            if (transact_result) {
                //业务办理成功 接口上报
                Packet new_packet = packetHelper.orderReporting(config,packet,wtAcId,wtAc);
                System.out.println(new_packet.toString());
                String result_String="";
               /* try {
                    result_String =ropService.executes(new_packet, history.getUserId(),history.getActId());
                }catch (Exception e){
                    result_String="ERROR";
                }*/
                ActivityOrder order = new ActivityOrder();
                order.setName(commonMapper.selectActivityByActId(config.getActId()).getName());
                String packetThirdTradeId= packet.getPost().getPubInfo().getTransactionId();
                order.setThirdTradeId(packetThirdTradeId);
                order.setOrderItemId("JYRZ"+packetThirdTradeId.substring(packetThirdTradeId.length()-21));
                order.setBossId(config.getActivityId());
                order.setCommodityName(config.getName());
                order.setUserId(history.getUserId());
                order.setCode(JSON.toJSONString(new_packet));
                order.setMessage(result_String);
               // order.setMessage("");
                order.setChannelId(channelId);
                commonMapper.insertActivityOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }

    private boolean saveHistory(String actId, String channelId, String mobile, ActivityConfiguration activityConfiguration,String ditch) {
        boolean result=false;
        if(activityConfiguration.getAmount()>0){
            int updateNum = foodieMapper.updateActivityConfigurationAmount(activityConfiguration.getId());
            if(updateNum>0) {
                result=true;
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
                newHistory.setDitch(ditch);
                newHistory.setActivityId(activityConfiguration.getActivityId());
                foodieMapper.insertActivityUserHistory(newHistory);
            }
        }

        return result;
    }


}
