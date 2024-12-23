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
import com.richeninfo.entity.mapper.mapper.master.TasteOfMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.TasteOfService;
import com.richeninfo.util.DateTimeTool;
import com.richeninfo.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * @auth sunxiaolei
 * @date 2024/3/22 15:43
 */
@Log4j
@Service
public class TasteOfServiceImpl implements TasteOfService {
    @Resource
    TasteOfMapper TasteOfMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommonMapper commonMapper;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId, String ditch) {
        JSONObject object = new JSONObject();
        ActivityUser users = findEveryDayUser(userId, secToken);
        if (users == null) {
            ActivityUser user = new ActivityUser();
            user.setLevel(0);// 第一次进游戏
            user.setAward(0);
            user.setUserId(userId);
            user.setChannelId(channelId);
            user.setPlayNum(0);
            user.setBlowNum(0);
            user.setAnswerNum(1);
            user.setGrade(0);
            //当前周期标识
            user.setUnlocked(curActWeekConfig().getUnlocked());
            user.setActId(actId);
            user.setDitch(ditch);
            user.setSecToken(secToken);
            TasteOfMapper.saveUser(user);
            user.setUserId(Base64.getEncoder().encodeToString(user.getUserId().getBytes()));
            object.put("user", user);
        } else {
            users.setSecToken(secToken);
            users.setUserId(Base64.getEncoder().encodeToString(users.getUserId().getBytes()));
            object.put("user", users);
        }
        return object;
    }

    /**
     * 服务评测
     *
     * @param channel_id
     * @param userId
     * @return
     */
    @Override
    public JSONObject toAnswer(String channel_id, String userId) {
        System.out.println("toanswer");
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        try {
            if (userId != null) {
                ActivityUser user = TasteOfMapper.findUserInfoByUserId(userId);
                List<VoteTopic> answerlist = new ArrayList<VoteTopic>();
                if (user.getAnswerNum() > 0) {
                    VoteTopic randAnswer = TasteOfMapper.findRandAnswerOne();
                    VoteTopic firstanswer = TasteOfMapper.findFirstAnswer();//第一题固定
                    answerlist.add(firstanswer);
                    answerlist.add(randAnswer);
                } else {
                    int oneAnswerType = Integer.valueOf(user.getAnswerTitle().split(",")[0]);
                    int twoAnswerType = Integer.valueOf(user.getAnswerTitle().split(",")[1]);
                    VoteTopic randAnswerone = TasteOfMapper.findAnswerByType(oneAnswerType);
                    VoteTopic randAnswertwo = TasteOfMapper.findAnswerByType(twoAnswerType);
                    answerlist.add(randAnswerone);
                    answerlist.add(randAnswertwo);
                }
                map.put("answerList", JSON.toJSON(answerlist));
            } else {
                map.put("loginFlag", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }


    /**
     * 提交评测内容
     *
     * @param channel_id
     * @param answer
     * @param answerTitle
     * @param userId
     */
    @Override
    public JSONObject answer(String channel_id, String answer, String answerTitle, String userId) {
        JSONObject jsonObject = null;
        Map<String, Object> map = new HashMap<>();
        try {
            jsonObject = new JSONObject();
            if (StringUtils.isBlank(userId) || userId.length() < 11) {
                map.put("msg", "error");
            } else {
                Object userjedis = redisUtil.get(userId + "play");
                if (Objects.isNull(userjedis)) {
                    redisUtil.set(userId + "play", userId + "play", 1);
                    ActivityUser user = TasteOfMapper.findUserInfoByUserId(userId);
                    if (user.getAnswerNum() > 0) {//时间内，白名单用户
                        int lostAnswerNum = TasteOfMapper.lostAnswerNum(userId);
                        if (lostAnswerNum > 0) {
                            //添加成绩 并创建表记录
                            int allMark = user.getMark() + 100;
                            int addmark = TasteOfMapper.addUserMark(userId, allMark);
                            if (addmark > 0) {
                                TasteOfMapper.updateAnswer(userId, answer, answerTitle);
                                //解锁游戏机会
                                TasteOfMapper.updateUserPlayNum(userId);
                            }
                        }
                        map.put("msg", "success");
                    } else {
                        map.put("msg", "timeout");
                    }
                    ActivityUser newuser = TasteOfMapper.findUserInfoByUserId(userId);
                    newuser.setUserId("");
                    map.put("user", newuser);
                } else {
                    map.put("msg", "Repeated request");
                }
            }
        } catch (Exception e) {
            map.put("msg", "error");
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }

    @Override
    public JSONObject choujiang(String channel_id, String userId, String actId, String ditch) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        try {
            log.info("开始抽奖" + userId);
            if (StringUtils.isBlank(userId) || userId.length() < 11) {
                map.put("msg", "error");
            } else {
                Object userjedis = redisUtil.get(userId + "play");
                if (Objects.isNull(userjedis)) {
                    redisUtil.set(userId + "play", userId + "play", 1);
                    ActivityUser user = TasteOfMapper.findUserInfoByUserId(userId);
                    //流量礼包开始发放
                    if (user.getGrade() > 0) {
                        if (user.getPlayNum() > 0) {//时间内，白名单用户
                            int lostPlayNum = TasteOfMapper.LostPlayNum(user.getId());
                            if (lostPlayNum > 0) {
                                ActivityConfiguration activityWeekConfiguration = curActWeekConfig();
                                ActivityConfiguration gift = new ActivityConfiguration();
                                //用户首次抽奖  节日期间会获取对应1GB流量奖励，其他周期首次获得流量奖励，冬季1.8-1.23获得 已经参与过元旦的活动并获得奖励进入奖池
                                if (activityWeekConfiguration.getValue().equals("1")) { //节日奖励
                                    gift = TasteOfMapper.findJRWeekGift(activityWeekConfiguration.getUnlocked(), actId);
                                } else {
                                    //冬季1.8-1.23 需要查看用户是否获得元旦期间奖品
                                    if (user.getAward() < 1) { //还未获得首次1GB奖励
                                        int changestatus = TasteOfMapper.updateUserAward(userId);
                                        if (changestatus > 0) {
                                            if (activityWeekConfiguration.getUnlocked() == 2) {
                                                ActivityUserHistory activityUserHistory = TasteOfMapper.findFirstWeekHistory(actId);
                                                if (activityUserHistory == null) {
                                                    if (changestatus > 0) {
                                                        gift = TasteOfMapper.findJRWeekGift(activityWeekConfiguration.getUnlocked(), actId);
                                                    } else {
                                                        gift = randomQYGift(actId);
                                                    }
                                                }
                                            } else {  //首次1GB奖励
                                                gift = TasteOfMapper.findJRWeekGift(2, actId);
                                            }
                                        } else {
                                            gift = randomQYGift(actId);
                                        }
                                    } else {
                                        Calendar calendar = Calendar.getInstance();
                                        int month = calendar.get(Calendar.MONTH) + 1;
                                        List<ActivityConfiguration> giftList = TasteOfMapper.findGiftList(month, actId);
                                        gift = randomGiftByUserGrade(giftList);
                                    }
                                }
                                saveHistory(gift, userId, channel_id, ditch);
                                ActivityUser users = TasteOfMapper.findUserInfoByUserId(userId);
                                users.setUserId("");
                                map.put("gift", gift);
                                map.put("msg", "success");
                                map.put("user", users);
                            } else {
                                map.put("msg", "mjh");
                            }
                        } else {
                            map.put("msg", "whj");
                        }
                    } else {
                        map.put("msg", "mjh");
                    }

                } else {
                    map.put("msg", "Repeated request");
                }
            }
        } catch (Exception e) {
            map.put("msg", "error");
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }

    //保存中奖记录
    public void saveHistory(ActivityConfiguration gift, String userId, String channel_id, String ditch) {
        ActivityUserHistory history = new ActivityUserHistory();
        history.setUserId(userId);
        history.setChannelId(channel_id);
        history.setValue(gift.getValue());
        history.setActId(gift.getActId());
        history.setModule(gift.getModule());
        history.setRewardName(gift.getName());
        history.setTypeId(gift.getTypeId());
        history.setUnlocked(gift.getUnlocked());
        history.setRemark(gift.getRemark());
        history.setWinSrc(gift.getWinSrc());
        history.setDitch(ditch);
        history.setActivityId(gift.getActivityId());
        history.setItemId(gift.getItemId());
        int status = TasteOfMapper.saveHistory(history);
        try {
          /*  if (status > 0 && Double.valueOf(gift.getValue()) > 0) {
                String mqMsg = commonService.issueReward(history);
                log.info("4147请求信息：" + mqMsg);
                jmsMessagingTemplate.convertAndSend("commonQueue", mqMsg);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 权益随机抽取
     *
     * @return
     */
    public ActivityConfiguration randomQYGift(String actId) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        List<ActivityConfiguration> qyGiftList = TasteOfMapper.findGiftListByObtain(month, actId);
        ActivityConfiguration gift = randomListGift(qyGiftList);
        return gift;
    }


    /**
     * 全部随机抽取
     *
     * @param giftList
     * @return
     */
    public ActivityConfiguration randomListGift(List<ActivityConfiguration> giftList) {
        log.info(giftList.toString());
        Random random = new Random();
        int n = random.nextInt(giftList.size());
        log.info("n的值===" + n);
        ActivityConfiguration zjgift = giftList.get(n);
        System.out.println("随机的奖励===" + zjgift);
        return zjgift;
    }

    /**
     * 按照个人等级来抽奖，
     *
     * @param giftList
     * @return
     */
    private ActivityConfiguration randomGiftByUserGrade(List<ActivityConfiguration> giftList) {
        double randomNum = RandomUtils.nextDouble();
        //0-1 随机小数
        log.info("随机数=" + randomNum);
        double startRate = 0;
        double endRate = 0;
        for (int i = 0; i < giftList.size(); i++) {
            log.info("rate=" + giftList.get(i).getProb());//0.33
            startRate = endRate; //0
            log.info("startRate=" + startRate);
            String rate1 = giftList.get(i).getProb();
            endRate += Double.valueOf(rate1);//0.33
            log.info("endRate=" + endRate);
            if (randomNum >= startRate && randomNum < endRate) {
                log.info("抽中奖励" + giftList.get(i).toString());
                return giftList.get(i);
            }
        }
        return null;
    }


    /**
     * 分享接口
     *
     * @param userId
     * @param channel_id
     * @param event
     */
    public void addShare(String userId, String channel_id, String event) {
        Map<String, Object> map = new HashMap<>();
        log.info("addShare===" + userId + "channel_id===" + channel_id);
        if (userId.equals("") || userId == "") {

        } else {
            ActivityUser user = TasteOfMapper.findUserInfoByUserId(userId);
            if (null == channel_id || channel_id.equals("")) {
                channel_id = "weiting";
            }
            if (null == event || event.equals("")) { // 普通分享 只增加计数
            } else if (event.equals("index") || event.equals("game")) {// 首页分享，

            }
            ActivityShare share = new ActivityShare();
            share.setChannelId(channel_id);
            share.setUserId(userId);
            share.setSecToken(user.getSecToken());
            share.setActId(user.getActId());
            TasteOfMapper.saveShareHistory(share);
        }
    }

    public ActivityUser findEveryDayUser(String userId, String secToken) {
        try {
            ActivityUser user = TasteOfMapper.findUserInfoByUserId(userId);
            if (user == null) {
                return user;
            } else {
                //每周期更新答题机会  初始化分享helpNum标识,unlocked 周期标识，获得周期奖励标识
                if (curActWeekConfig().getUnlocked() != user.getUnlocked()) {
                    TasteOfMapper.updateUserWeekInfo(userId, curActWeekConfig().getUnlocked());
                }
                ActivityUser newuser = TasteOfMapper.findUserInfoByUserId(userId);
                return newuser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject myReceived(String channelId, String userId, String actId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (null == userId || userId.length() < 11 || userId.equals("")) {
                jsonObject.put("msg", "error");
            }
            SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
            List<ActivityUserHistory> list = TasteOfMapper.findUserRecived(userId);
            List<ActivityUserHistory> newList= new ArrayList<ActivityUserHistory>();
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            System.out.println("当前月份是：" + currentMonth);
                for(ActivityUserHistory userHistory :list){
                    Date date = month.parse(userHistory.getCreateTime());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int curmonth = calendar.get(Calendar.MONTH) + 1; // 月份以0为基准，需要加1
                    System.out.println("领取月份：" + curmonth);
                    if(curmonth==currentMonth){
                        userHistory.setKeyword("0");
                    }else{
                        userHistory.setKeyword("1");
                    }
                    userHistory.setUserId("");
                    newList.add(userHistory);
                }
            jsonObject.put("list", JSON.toJSON(newList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void changeLevel(String channelId, String secToken, String actId) {
        String mobile = commonService.getMobile(secToken, channelId);
        TasteOfMapper.changeLevel(mobile);
    }

    public ActivityConfiguration curActWeekConfig() {
        ActivityConfiguration list = TasteOfMapper.findActWeekList();
        return list;
    }

}
