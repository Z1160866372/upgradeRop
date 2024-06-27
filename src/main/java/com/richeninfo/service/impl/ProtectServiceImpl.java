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
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityOrder;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.ProtectMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ProtectService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.ReqWorker;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class ProtectServiceImpl implements ProtectService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private ProtectMapper protectMapper;
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
        ActivityUser select_user = protectMapper.selectUserByCreateDate(user.getUserId(),user.getActId());
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setDitch(user.getDitch());
            new_user.setBelongFlag(user.getBelongFlag());
            new_user.setCreateDate(day.format(new Date()));
            protectMapper.insertUser(new_user);
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
                userHistory=protectMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked(),actId);
                if(userHistory!=null){//已报名
                    config.setValue(userHistory.getValue());
                    config.setStatus(2);

                }else{//去报名
                    if(config.getAmount()>0){
                        config.setStatus(0);
                    }else{//已抢完
                        config.setStatus(1);
                    }
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String name,String ditch) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        ActivityConfiguration config =null;
        if (!StringUtils.isEmpty(secToken)) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        ActivityUser select_user = protectMapper.selectUserByCreateDate(mobile,actId);
        if(select_user.getBelongFlag().equals("1")){
            object.put(Constant.MSG,"noShYd");
            return object;
        }
        if(select_user.getBelongFlag().isEmpty()){
            if(!commonService.checkUserIsChinaMobile(mobile,actId)){
                object.put(Constant.MSG,"noShYd");
                return object;
            }
        }
        ActivityUserHistory userHistory  =protectMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked,actId);
        if(userHistory!=null){
            object.put("history",userHistory);
            object.put(Constant.MSG,"ylq");
        }else{
            config = commonMapper.selectActivityConfiguration(actId,unlocked);
            boolean result =  saveHistory(actId,channelId,mobile,config,ditch,name);
            if(result){
                userHistory  =protectMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked,actId);
                userHistory.setUserId(userHistory.getUserId().substring(0, 3) + "****" + userHistory.getUserId().substring(7));
                object.put("history",userHistory);
                object.put(Constant.MSG,Constant.SUCCESS);
            }else{
                object.put(Constant.MSG,"noNum");
            }

        }
        return object;
    }

    private boolean saveHistory(String actId, String channelId, String mobile, ActivityConfiguration activityConfiguration,String ditch,String name) {
        boolean result = false;
        if (activityConfiguration.getAmount() > 0) {
            int updateNum = protectMapper.updateActivityConfigurationAmount(activityConfiguration.getId());
            if (updateNum > 0) {
                result = true;
                ActivityUserHistory newHistory = new ActivityUserHistory();
                newHistory.setUserId(mobile);
                newHistory.setChannelId(channelId);
                newHistory.setRewardName(activityConfiguration.getName());
                newHistory.setTypeId(activityConfiguration.getTypeId());
                newHistory.setUnlocked(activityConfiguration.getUnlocked());
                newHistory.setCreateDate(day.format(new Date()));
                newHistory.setCreateTime(df.format(new Date()));
                newHistory.setValue(name);
                newHistory.setActId(actId);
                newHistory.setDitch(ditch);
                protectMapper.insertActivityUserHistory(newHistory);
            }

        }
        return result;
    }

}
