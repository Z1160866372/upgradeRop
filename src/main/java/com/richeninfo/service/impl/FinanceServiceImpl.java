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
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FinanceService;
import com.richeninfo.util.IPUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.text.DateFormat;
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
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private FinanceMapper financeMapper;
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
        ActivityUser select_user = financeMapper.selectUserByCreateDate(user.getUserId(), user.getActId(),month.format(new Date()));
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setCreateDate(month.format(new Date()));
            financeMapper.insertUser(new_user);
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
                if(config.getTypeId()==0||config.getTypeId()==1){
                    if(config.getTypeId()==1){
                        userHistory=financeMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked(),month.format(new Date()));
                    }else{
                        userHistory=financeMapper.selectActivityUserHistoryByTypeId(mobile,config.getTypeId(),month.format(new Date()));
                    }
                    if(userHistory==null){
                        if(config.getAmount()>0){//马上抢
                            config.setStatus(0);
                        }else{//已抢光
                            config.setStatus(1);
                        }
                    }else{//已抢
                        config.setStatus(2);
                    }
                }else{//权益 限时不限量 查询当月是否已领取过固定卡券奖励
                    userHistory=financeMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked(),month.format(new Date()));
                    if(userHistory==null){
                        config.setStatus(0);
                    }else{
                        config.setStatus(2);
                    }
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        if (!StringUtils.isEmpty(secToken)) {
            mobile= commonService.getMobile(secToken,channelId);
        }else{
            object.put(Constant.MSG,"login");
            return object;
        }
        if(unlocked==6){//盲盒奖励 流量/话费
            ActivityUserHistory userHistory  = financeMapper.selectActivityUserHistoryByTypeId(mobile,0,month.format(new Date()));
            if(userHistory==null){
                ActivityConfiguration activityConfiguration =  financeMapper.selectActivityConfigurationByUnlocked(actId,5);
                if(activityConfiguration.getAmount()>0){
                    int mash = financeMapper.updateActivityConfigurationAmount(activityConfiguration.getId());
                    if(mash>0){
                        saveHistory(actId, channelId, object, mobile, activityConfiguration);
                    }else{
                        activityConfiguration =  financeMapper.selectActivityConfigurationByUnlocked(actId,0);
                        if(activityConfiguration.getAmount()>0){
                            saveHistory(actId, channelId, object, mobile, activityConfiguration);
                        }else{//您来晚了
                            object.put(Constant.MSG,"noDate");
                        }
                    }
                }else{
                    activityConfiguration =  financeMapper.selectActivityConfigurationByUnlocked(actId,0);
                    if(activityConfiguration.getAmount()>0){
                        saveHistory(actId, channelId, object, mobile, activityConfiguration);
                    }else{//您来晚了
                        object.put(Constant.MSG,"noDate");
                    }
                }
            }else{
                object.put(Constant.MSG,"ylq");
            }
        }else if(unlocked>6&&unlocked<9){//洗车券奖励
            ActivityUserHistory userHistory=financeMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked,month.format(new Date()));
            if(userHistory==null){
                ActivityConfiguration activityConfiguration =  financeMapper.selectActivityConfigurationByUnlocked(actId,unlocked);
                if(activityConfiguration.getAmount()>0){
                    List<ActivityCardList> activityCardLists = financeMapper.selectActivityCardList(actId,unlocked,day.format(new Date()));
                    log.info("size=="+activityCardLists.size());
                    if(activityCardLists.size()>0){
                        ActivityCardList activityCardList = financeMapper.selectActivityCardListByUnlocked(actId,unlocked,day.format(new Date()));
                        int result = financeMapper.updateActivityCardList(mobile,activityCardList.getId());
                        log.info("result=="+result);
                        if(result>0){
                            activityConfiguration.setValue(activityCardList.getCouponCode());
                            saveHistory(actId, channelId, object, mobile, activityConfiguration);
                            String content="";
                            if(activityConfiguration.getUnlocked()==7){
                                content="尊敬的客户，您好！恭喜您在“移动云盘周三财运日”活动中获赠标准小车洗车券一张，券码为"+activityCardList.getCouponCode()+"，请您尽快登陆车点点微信公众号或APP-“个人中心”-“兑换码”激活使用，激活后券码有效期为激活之日起1个月有效，请尽快使用。【中国移动】";
                            }
                            if(activityConfiguration.getUnlocked()==8){
                                content="尊敬的客户，您好！恭喜您在“移动云盘周三财运日”活动中获赠标准10元洗车代金券一张，券码为"+activityCardList.getCouponCode()+"，请您尽快登陆车点点微信公众号或APP-“个人中心”-“兑换码”激活使用，激活后券码有效期为激活之日起1个月有效，请尽快使用。【中国移动】";
                            }
                            Packet packet = packetHelper.getCommitPacket1638(mobile, content);
                            JSON.parseObject(ropService.execute(packet, mobile,actId), Result.class);
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
        }else{//权益奖励
            ActivityUserHistory userHistory=financeMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked,month.format(new Date()));
            if(userHistory==null) {
                ActivityConfiguration activityConfiguration = financeMapper.selectActivityConfigurationByUnlocked(actId, unlocked);
                saveHistory(actId, channelId, object, mobile, activityConfiguration);
            }else{
                object.put(Constant.MSG,"ylq");
            }
        }
        return object;
    }

    private void saveHistory(String actId, String channelId, JSONObject object, String mobile, ActivityConfiguration activityConfiguration) {
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
        newHistory.setImgSrc(activityConfiguration.getImgSrc());
        financeMapper.insertActivityUserHistory(newHistory);
        if(activityConfiguration.getUnlocked()==0||activityConfiguration.getUnlocked()==5){
            financeMapper.updateActivityConfigurationAmount(financeMapper.selectActivityConfigurationByUnlocked(actId,6).getId());
            String mqMsg = commonService.issueReward(activityConfiguration, newHistory);
            log.info("4147请求信息：" + mqMsg);
            jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
        }else{
            financeMapper.updateActivityConfigurationAmount(financeMapper.selectActivityConfigurationByUnlocked(actId,activityConfiguration.getUnlocked()).getId());
        }
        object.put("gift",activityConfiguration);
        object.put(Constant.MSG,Constant.SUCCESS);
    }

    /**
     * 我的奖励
     * @param channelId
     * @param actId
     * @return
     */
    @Override
    public JSONObject getMyReward(String secToken,String channelId, String actId) {
        JSONObject object = new JSONObject();
        String mobile="";
        if (!StringUtils.isEmpty(secToken)) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        List<ActivityUserHistory> historyList = financeMapper.selectHistory(mobile,actId,month.format(new Date()));
        object.put(Constant.ObjectList,historyList);
        return object;
    }

}
