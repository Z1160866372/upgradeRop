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
import com.richeninfo.entity.mapper.mapper.master.HuiPlayMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.pojo.VasOfferInfo;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.HuiPlayService;
import com.richeninfo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Base64;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HuiPlayServiceImpl implements HuiPlayService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private HuiPlayMapper HuiPlayMapper;
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
        ActivityUser select_user = HuiPlayMapper.selectUserByCreateDate(user.getUserId());
        if (select_user == null) {
            ActivityUser new_user = new ActivityUser();
            new_user.setSecToken(user.getSecToken());
            new_user.setUserId(user.getUserId());
            new_user.setActId(user.getActId());
            new_user.setChannelId(user.getChannelId());
            new_user.setCreateDate(day.format(new Date()));
            new_user.setDitch(user.getDitch());
            HuiPlayMapper.insertUser(new_user);
            user = new_user;
        } else {
            select_user.setSecToken(user.getSecToken());
            user = select_user;
        }
        user.setUserId(Base64.getEncoder().encodeToString(user.getUserId().getBytes()));
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
                userHistory=HuiPlayMapper.selectActivityUserHistoryByUnlocked(mobile,config.getUnlocked());
                if(userHistory!=null){
                    config.setName(userHistory.getRewardName());
                    config.setTypeId(userHistory.getTypeId());
                    config.setUnlocked(userHistory.getUnlocked());
                    config.setWinSrc(userHistory.getWinSrc());
                    config.setImgSrc(userHistory.getImgSrc());
                    config.setRemark(userHistory.getRemark());
                    config.setValue(userHistory.getValue());
                    if(userHistory.getTypeId()==1){
                        if(userHistory.getStatus()==3){//已办理
                            config.setStatus(2);
                        }else{//已领取 未办理
                            config.setStatus(1);
                        }
                    }else{//已领取
                        config.setStatus(1);
                    }
                }else{//去领取
                    config.setStatus(0);
                }
            }
        }
        return pro_config;
    }

    @Override
    public JSONObject submit(String secToken, String actId, int unlocked, String channelId,String ditch) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        log.info("submit============"+secToken);
        if(commonService.verityTime(actId).equals("underway")) {
            if (!StringUtils.isEmpty(secToken)) {
                try {
                    mobile = commonService.getMobile(secToken, channelId);
                    if (mobile == null || mobile.isEmpty()) {
                        object.put(Constant.MSG, "login");
                        return object;
                    }
                    if( !commonService.checkUserIsChinaMobile(mobile,actId)){
                        object.put(Constant.MSG,"noShYd");
                        return object;
                    }
                } catch (Exception e) {
                    object.put(Constant.MSG, "loginError");
                    return object;
                }
            } else {
                object.put(Constant.MSG, "login");
                return object;
            }
            ActivityUserHistory userHistory = HuiPlayMapper.selectActivityUserHistoryByUnlocked(mobile, unlocked);
            if (userHistory == null) {
                ActivityConfiguration config = null;
                String keyword = "wt_" + actId + "_roster";
                log.info("keyword============"+keyword);
                if (unlocked == 4) {
                    List<ActivityRoster> selectRoster = commonMapper.selectRoster(mobile, actId, keyword, unlocked);
                    log.info("selectRoster============"+selectRoster.size());
                    if (!CollectionUtils.isEmpty(selectRoster)) {
                        object.put(Constant.MSG, "blackList");
                        return object;
                    }
                }
                config = HuiPlayMapper.selectActivityConfigurationByModule(actId, unlocked, 0);
                saveHistory(actId, channelId, object, mobile, config, ditch);
                object.put("config", config);
                object.put(Constant.MSG, Constant.SUCCESS);
            } else {
                ActivityConfiguration config = new ActivityConfiguration();
                config.setName(userHistory.getRewardName());
                config.setTypeId(userHistory.getTypeId());
                config.setUnlocked(userHistory.getUnlocked());
                config.setWinSrc(userHistory.getWinSrc());
                config.setImgSrc(userHistory.getImgSrc());
                object.put("config", config);
                object.put(Constant.MSG, Constant.YLQ);
            }
        }else {
            object.put(Constant.MSG, "ActError");
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
        newHistory.setCreateDate(day.format(new Date()));
        newHistory.setCreateTime(df.format(new Date()));
        newHistory.setValue(activityConfiguration.getValue());
        newHistory.setActId(actId);
        newHistory.setDitch(ditch);
        newHistory.setIpScanner(activityConfiguration.getNoProContent());
        newHistory.setActivityId(activityConfiguration.getActivityId());
        newHistory.setItemId(activityConfiguration.getItemId());
        newHistory.setImgSrc(activityConfiguration.getImgSrc());
        newHistory.setWinSrc(activityConfiguration.getWinSrc());
        newHistory.setModule(activityConfiguration.getModule());
        HuiPlayMapper.insertActivityUserHistory(newHistory);
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
            String message = ropService.execute(packet,history.getUserId(),history.getActId());
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
            }
            history.setMessage(JSON.toJSONString(result));
            history.setCode(JSON.toJSONString(packet));
            object.put("res", res);
            object.put("DoneCode", DoneCode);
            /*if(true){
                object.put("res", "0000");
                object.put("DoneCode", "9999");
                history.setStatus(Constant.STATUS_RECEIVED);
                object.put(Constant.MSG, Constant.SUCCESS);
                transact_result=true;
            }*/
            object.put("update_history", JSON.toJSONString(history));
            HuiPlayMapper.updateHistory(history);
            if (transact_result) {
                if(config.getAmount()>0){
                    List<ActivityCardList> activityCardLists = HuiPlayMapper.selectActivityCardList(history.getActId(),history.getUnlocked(),month.format(new Date()));
                    if(activityCardLists.size()>0){
                        ActivityCardList activityCardList = HuiPlayMapper.selectActivityCardListByUnlocked(history.getActId(),history.getUnlocked(),month.format(new Date()));
                        int card_result = HuiPlayMapper.updateActivityCardList(history.getUserId(),activityCardList.getId());
                        if(card_result>0){
                            commonMapper.updateAmount(config.getId());//更新卡券配置数量
                            history.setRemark(activityCardList.getCouponCode());
                            HuiPlayMapper.updateHistoryRemark(history);
                            String content=config.getRemark().replace("code",activityCardList.getCouponCode());
                            log.info("content:"+content);
                             Packet card_packet = packetHelper.getCommitPacket1638(history.getUserId(), content);
                            JSON.parseObject(ropService.execute(packet, history.getUserId(),history.getActId()), Result.class);
                        }else{
                            object.put(Constant.MSG,"noDate");
                        }
                    }else{
                        object.put(Constant.MSG,"noDate");
                    }
                }else{
                    object.put(Constant.MSG,"noDate");
                }
                //业务办理成功 接口上报
                Packet new_packet = packetHelper.orderReporting(config,packet,wtAcId,wtAc);
                String result_String="";
                try {
                    result_String =ropService.executes(new_packet, history.getUserId(),history.getActId());
                }catch (Exception e){
                    result_String="ERROR";
                }
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
                order.setChannelId(channelId);
                commonMapper.insertActivityOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        object.put("transact_result", transact_result);
        return object;
    }

    @Override
    public JSONObject transaction(String secToken, String actId, int unlocked, String channelId,String wtAcId, String wtAc,String randCode,String ditch) throws Exception {
        JSONObject object = new JSONObject();
        String mobile="";
        ActivityConfiguration config =null;
        if (!StringUtils.isEmpty(secToken)) {
            mobile= commonService.getMobile(secToken,channelId);
        }
        if(!commonService.checkUserIsChinaMobile(mobile,actId)){//非上海移动
            object.put(Constant.MSG,"noShYd");
            return object;
        }
        ActivityUserHistory userHistory  =HuiPlayMapper.selectActivityUserHistoryByUnlocked(mobile,unlocked);
        if(userHistory!=null){
            if(userHistory.getTypeId()==1){
                if(userHistory.getStatus()==3){//已办理
                    object.put(Constant.MSG,"ybl");
                }else{
                    config = commonMapper.selectActivitySomeConfigurationByTYpeId(actId,unlocked);
                    object = transact3066Business(userHistory,config,randCode,channelId,wtAcId,wtAc,ditch);
                }
            }else{//不能办理
                object.put(Constant.MSG,"noTransaction");
            }
        }else{//没有历史数据
            object.put(Constant.MSG,"noData");
        }
        return object;
    }
}
