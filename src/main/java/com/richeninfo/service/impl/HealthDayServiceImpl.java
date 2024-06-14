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
import com.richeninfo.entity.mapper.mapper.master.HealthDayMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.HealthDayService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RSAUtils;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/30 14:04
 */
@Service
@Slf4j
public class HealthDayServiceImpl implements HealthDayService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private HealthDayMapper healthDayMapper;
    @Resource
    private PacketHelper packetHelper;
    @Resource
    private RopServiceManager ropService;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private CommonService commonService;
    @Resource
    private RSAUtils rsaUtils;
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
        ActivityUser select_user = healthDayMapper.selectUserByCreateDate(user.getUserId(), user.getActId(),month.format(new Date()));
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setCreateDate(month.format(new Date()));
            new_user.setDitch(user.getDitch());
            List<ActivityRoster> selectRoster = healthDayMapper.selectRoster(user.getUserId());
            if(!CollectionUtils.isEmpty(selectRoster)){
                new_user.setUserType(0);
            }else{
                new_user.setUserType(1);
            }
            healthDayMapper.insertUser(new_user);
            user = new_user;
        } else {
            select_user.setSecToken(user.getSecToken());
            user = select_user;
        }
        return user;
    }

    @Override
    public List<ActivityConfiguration>  getConfiguration(String secToken, String actId, String channelId) throws Exception {
        List<ActivityConfiguration> pro_config = commonMapper.selectActivityConfigurationByActId(actId);
        String mobile="";
        ActivityUserHistory userHistory = null;
        if(pro_config.size()>0){
            if (!StringUtils.isEmpty(secToken)) {
                mobile= commonService.getMobile(secToken,channelId);
            }
            for (ActivityConfiguration config : pro_config) {
                userHistory=healthDayMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked(),month.format(new Date()));
                if(userHistory==null){
                    if(config.getAmount()>0){//马上抢
                        config.setStatus(0);
                    }else{//已抢光
                        config.setStatus(1);
                    }
                }else{//已抢
                    config.setValue(userHistory.getValue());
                    config.setStatus(2);
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String ditch) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        if (!StringUtils.isEmpty(secToken)) {
            try {
                mobile= commonService.getMobile(secToken,channelId);
            }catch (Exception e){
                object.put(Constant.MSG,"loginError");
                return object;
            }
        }else{
            object.put(Constant.MSG,"login");
            return object;
        }
        ActivityUser select_user = healthDayMapper.selectUserByCreateDate(mobile, actId,month.format(new Date()));
        if(select_user.getUserType()>0){
            object.put(Constant.MSG,"blackList");
            return object;
        }
        ActivityUserHistory userHistory=healthDayMapper.selectActivityUserHistoryByTypeId(mobile,4,month.format(new Date()));
        if(userHistory==null){
            ActivityConfiguration activityConfiguration =  healthDayMapper.selectActivityConfigurationByUnlocked(actId,unlocked);
            if(activityConfiguration.getAmount()>0){
                List<ActivityCardList> activityCardLists = healthDayMapper.selectActivityCardList(actId,unlocked,month.format(new Date()));
                log.info("size=="+activityCardLists.size());
                if(activityCardLists.size()>0){
                    ActivityCardList activityCardList = healthDayMapper.selectActivityCardListByUnlocked(actId,unlocked,month.format(new Date()));
                    int result = healthDayMapper.updateActivityCardList(mobile,activityCardList.getId());
                    log.info("result=="+result);
                    if(result>0){
                        activityConfiguration.setValue(activityCardList.getCouponCode());
                        saveHistory(actId, channelId, object, mobile, activityConfiguration,ditch);
                        String content=activityConfiguration.getRemark().replace("code",activityCardList.getCouponCode());
                        log.info("content=="+content);
                       /* Packet packet = packetHelper.getCommitPacket1638(mobile, content);
                        JSON.parseObject(ropService.execute(packet, mobile,actId), Result.class);*/
                    }else{
                        object.put(Constant.MSG,"noDate");
                    }
                }else{
                    object.put(Constant.MSG,"noDate");
                }
            }else{//您来晚了
                object.put(Constant.MSG,"noDate");
            }
        }else{
            object.put(Constant.MSG,"ylq");
        }
        return object;
    }

    private void saveHistory(String actId, String channelId, JSONObject object, String mobile, ActivityConfiguration activityConfiguration,String ditch) {
        ActivityUserHistory newHistory = new ActivityUserHistory();
        newHistory.setUserId(mobile);
        newHistory.setChannelId(channelId);
        newHistory.setRewardName(activityConfiguration.getName());
        newHistory.setTypeId(activityConfiguration.getTypeId());
        newHistory.setUnlocked(activityConfiguration.getUnlocked());
        newHistory.setCreateDate(month.format(new Date()));
        newHistory.setCreateTime(df.format(new Date()));
        newHistory.setValue(activityConfiguration.getValue());
        newHistory.setActId(actId);
        newHistory.setDitch(ditch);
        newHistory.setActivityId(activityConfiguration.getActivityId());
        newHistory.setItemId(activityConfiguration.getItemId());
        newHistory.setImgSrc(activityConfiguration.getImgSrc());
        healthDayMapper.insertActivityUserHistory(newHistory);
        healthDayMapper.updateActivityConfigurationAmount(healthDayMapper.selectActivityConfigurationByUnlocked(actId,activityConfiguration.getUnlocked()).getId());
        object.put("gift",activityConfiguration);
        object.put(Constant.MSG,Constant.SUCCESS);
    }
}
