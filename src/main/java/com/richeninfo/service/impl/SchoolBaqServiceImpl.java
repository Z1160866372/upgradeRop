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
import com.richeninfo.entity.mapper.mapper.master.NewCallMapper;
import com.richeninfo.entity.mapper.mapper.master.SchoolBaqMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.NewCallService;
import com.richeninfo.service.SchoolBaqService;
import com.richeninfo.util.IPUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RSAUtils;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
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
public class SchoolBaqServiceImpl implements SchoolBaqService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private SchoolBaqMapper schoolBaqMapper;
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
        ActivityUser select_user = schoolBaqMapper.selectUserByCreateDate(user.getUserId());
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setCreateDate(month.format(new Date()));
            List<ActivityRoster> selectRoster = schoolBaqMapper.selectRoster(user.getUserId());
            if(!CollectionUtils.isEmpty(selectRoster)){
                for (ActivityRoster roster:selectRoster) {
                    if(roster.getUserType()==1){//营业厅成员
                        new_user.setUserType(1);
                        break;
                    }else{
                        new_user.setUserType(2);
                    }
                }
            }else{//既不是营业厅白名单成员 也不是黑名单成员 可直接领取
                new_user.setUserType(0);
            }
            schoolBaqMapper.insertUser(new_user);
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
            if (secToken!=null||!secToken.isEmpty()) {
                mobile= commonService.getMobile(secToken,channelId);
            }
            for (ActivityConfiguration config : pro_config) {
                userHistory=schoolBaqMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked());
                if(userHistory!=null){//已领取
                    config.setStatus(2);
                }else{//去领取
                    config.setStatus(0);
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String mobilePhone) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        ActivityConfiguration config =null;
        ActivityUser user = new ActivityUser();
        if (secToken!=null||!secToken.isEmpty()) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        if (mobilePhone!=null||!mobilePhone.isEmpty()) {//代领取
            mobilePhone = rsaUtils.decryptByPriKey(mobilePhone).trim();
            user.setUserId(mobilePhone);
        }else{//自领取
            user.setUserId(mobile);
        }
        user.setActId(actId);
        user.setChannelId(channelId);
        user=insertUser(user);
        if(user.getUserType()==0||user.getUserType()==1){//可领取
            config = commonMapper.selectActivityConfiguration(actId,unlocked);
            ActivityUserHistory userHistory =schoolBaqMapper.selectActivityUserHistoryByUnlocked(user.getUserId(),unlocked);
            if(userHistory==null){
                if(saveHistory(actId,channelId,user.getUserId(),config)){
                    schoolBaqMapper.updateActivityUser(user);
                    object.put(Constant.MSG,Constant.SUCCESS);
                }else{
                    object.put(Constant.MSG,Constant.FAILURE);
                }
            }else{
                object.put(Constant.MSG,"ylq");
            }
        }else  if(user.getUserType()==2){
            object.put(Constant.MSG,"blackList");
        }
        return object;
    }


    private boolean saveHistory(String actId, String channelId, String mobile, ActivityConfiguration activityConfiguration) throws Exception {
        boolean result_status=false;
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
        schoolBaqMapper.insertActivityUserHistory(newHistory);
        ActivityUserHistory oldHistory = schoolBaqMapper.selectActivityUserHistoryByUnlocked(mobile, activityConfiguration.getUnlocked());
        Packet packet = packetHelper.CardVoucherIssued("CH5",activityConfiguration.getActivityId(),mobile);
        try {
            String result = ropService.executes(packet,mobile,actId);
            String code = JSONObject.parseObject(result).getString("code");
            //String code="200";
            if(code.equals("200")){
                oldHistory.setStatus(Constant.STATUS_RECEIVED);
                result_status=true;
            }else{
                oldHistory.setStatus(Constant.STATUS_RECEIVED_ERROR);
                result_status=false;
            }
            oldHistory.setCode(JSONArray.fromObject(packet).toString());
            oldHistory.setMessage(result);
            //oldHistory.setMessage("测试数据～");
            schoolBaqMapper.updateHistory(oldHistory);
        }catch (Exception exception){
            result_status=false;
            exception.printStackTrace();
        }
        return result_status;
    }
}
