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
import com.richeninfo.util.Des3SSL;
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
            List<ActivityRoster> selectRoster = commonMapper.selectRoster(user.getUserId(), "meliorist", "wt_meliorist_roster", 1);
            if (!CollectionUtils.isEmpty(selectRoster)) {
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
    public List<ActivityUserHistory> getActivityUserList(String secToken, String actId, String channelId, int page, int limit, int typeId, String ip) {
        List<ActivityUserHistory> userList = null;
        if (typeId == 0 || typeId == 1) {//查询所有提交列表||查询当前用户提交列表
            String mobile = "";
            if (!StringUtils.isEmpty(secToken)) {
                mobile = commonService.getMobile(secToken, channelId);
            }
            if (typeId == 0) {
                userList = foodieMapper.selectUserList(mobile, (page - 1) * limit, limit);
            } else {
                userList = foodieMapper.selectUserListByUserId(mobile, (page - 1) * limit, limit);
            }
            if (userList.size() > 0) {
                ActivityUserHistory userHistory = null;
                String key = commonMapper.selectTheDayKey().getSecretKey();
                for (ActivityUserHistory userFor : userList) {
                    userHistory = foodieMapper.selectActivity(mobile, userFor.getId() + "", 1);
                    if (userHistory != null) {//当前用户已点赞
                        userFor.setUserType(1);
                    }
                    userHistory = foodieMapper.selectActivity(mobile, userFor.getId() + "", 2);
                    if (userHistory != null) {//当前用户已评论
                        userFor.setStatus(1);
                    }
                    userFor.setUserId(userFor.getUserId().substring(0, 3) + "****" + userFor.getUserId().substring(7));
                    userFor.setSecToken(Des3SSL.encodeDC(userFor.getUserId(), key));
                }
            }
        }
        if (typeId == 2) {//查询当前提交内容的评论列表
            userList = foodieMapper.selectUserListByTypeId(ip, (page - 1) * limit, limit);
            if (userList.size() > 0) {
                for (ActivityUserHistory userFor : userList) {
                    userFor.setUserId(userFor.getUserId().substring(0, 3) + "****" + userFor.getUserId().substring(7));
                }
            }
        }
        return userList;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int typeId, String channelId, String code, String message, String remark, String ditch, String ip) throws Exception {
        JSONObject object = new JSONObject();
        String mobile = "";
        ActivityConfiguration config = null;
        if (!StringUtils.isEmpty(secToken)) {
            mobile = commonService.getMobile(secToken, channelId);
        }
        ActivityUser select_user = foodieMapper.selectUserByCreateDate(mobile);
        if (select_user != null) {
            if (select_user.getUserType() != 1) {
                object.put(Constant.MSG, "blackList");
                return object;
            }
            if (("1").equals(select_user.getBelongFlag())) {
                object.put(Constant.MSG, "noShYd");
                return object;
            }
            if (select_user.getBelongFlag() != null && select_user.getBelongFlag().isEmpty()) {
                /*if(!commonService.checkUserIsChinaMobile(mobile,actId)){
                    object.put(Constant.MSG,"noShYd");
                    return object;
                }*/
            }
        }
        object = saveHistory(object, actId, channelId, mobile, typeId, code, message, remark, ditch, ip);
        return object;
    }

    private JSONObject saveHistory(JSONObject object, String actId, String channelId, String mobile, int typeId, String code, String message, String remark, String ditch, String ip) {
        boolean result = false;
        ActivityUserHistory newHistory = new ActivityUserHistory();
        newHistory.setUserId(mobile);
        newHistory.setChannelId(channelId);
        newHistory.setCreateDate(day.format(new Date()));
        newHistory.setCreateTime(df.format(new Date()));
        newHistory.setTypeId(typeId);
        newHistory.setActId(actId);
        newHistory.setDitch(ditch);
        if (typeId == 0) {//提交信息
            newHistory.setCode(code);
            newHistory.setMessage(message);
            newHistory.setRemark(remark);
            result = true;
        }
        if (typeId == 1) {//点赞
            ActivityUserHistory userHistory = foodieMapper.selectActivity(mobile, ip, 1);
            if (userHistory == null) {//当前用户未点赞
                result = true;
                newHistory.setIp(ip);
                foodieMapper.updateHistoryModule(new Integer(ip));
            }
        }
        if (typeId == 2) {//评论
            ActivityUserHistory userHistory = foodieMapper.selectActivity(mobile, ip, 2);
            if (userHistory == null) {//当前用户位评论
                result = true;
                newHistory.setIp(ip);
                newHistory.setRemark(remark);
                foodieMapper.updateHistoryUnlocked(new Integer(ip));
            }
        }
        if (result) {
            object.put(Constant.MSG, "success");
            if(typeId == 0){
                if(newHistory.getMessage().contains("_")){
                    for (int i = 0; i < newHistory.getMessage().split("_").length; i++) {
                        newHistory.setMessage(message.split("_")[i]);
                        newHistory.setRemark(remark.split("_")[i]);
                        foodieMapper.insertActivityUserHistory(newHistory);
                    }
                }else{
                    foodieMapper.insertActivityUserHistory(newHistory);
                }
            }else{
                foodieMapper.insertActivityUserHistory(newHistory);
            }
        } else {
            object.put(Constant.MSG, "error");
        }
        return object;
    }
}
