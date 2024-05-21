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
import com.richeninfo.entity.mapper.mapper.master.ExteroceptiveMapper;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ExteroceptiveService;
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
import java.util.*;

/**
 * @auth sunxiaolei
 * @date 2024/3/22 15:43
 */
@Log4j
@Service
public class ExteroceptiveServiceImpl implements ExteroceptiveService {
    @Resource
    ExteroceptiveMapper exteroceptiveMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CommonMapper commonMapper;
    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId,String ditch) {
        JSONObject object = new JSONObject();
        ActivityUser users = findEveryDayUser(userId, secToken);

        if (users == null) {
            users= new ActivityUser();
            //查询第一期的分数和对应等级 导入第二期
            ActivityUser olduser = exteroceptiveMapper.findOldUserInfoByUserId(userId);
            if (olduser == null) {
                users.setGrade(0);
            } else {
                users.setGrade(olduser.getGrade());
                users.setMark(olduser.getMark());
                InsertRecord("2023年心级体验官累积分值", userId, olduser.getMark(), channelId, 0);
            }
            users.setLevel(0);// 第一次进游戏
            users.setAward(0);
            users.setUserId(userId);
            users.setChannelId(channelId);
            users.setPlayNum(2);
            users.setBlowNum(1);
            users.setAnswerNum(1);
            users.setUnlocked(0);
            users.setActId(actId);
            users.setDitch(ditch);
            users.setSecToken(secToken);
            exteroceptiveMapper.saveUser(users);
            object.put("user", users);
        } else {
            users.setSecToken(secToken);
            object.put("user", users);
        }
        return object;
    }

    /**
     * 积分记录
     *
     * @param caozuo
     * @param userId
     * @param fenshu
     * @param channel_id
     */
    public void InsertRecord(String caozuo, String userId, int fenshu, String channel_id, int typeId) {
        ActivityRecord record = new ActivityRecord();
        record.setActionName(caozuo);
        record.setStatus(fenshu);
        record.setUserId(userId);
        record.setTypeId(typeId);
        record.setChannel_id(channel_id);
        try {
            exteroceptiveMapper.saveUserRecord(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
                List<VoteTopic> answerlist = new ArrayList<VoteTopic>();
                if (user.getAnswerNum() > 0) {
                    VoteTopic randAnswer = exteroceptiveMapper.findRandAnswerOne();
                    VoteTopic firstanswer = exteroceptiveMapper.findFirstAnswer();//第一题固定
                    answerlist.add(firstanswer);
                    answerlist.add(randAnswer);
                } else {
                    int oneAnswerType = Integer.valueOf(user.getAnswerTitle().split(",")[0]);
                    int twoAnswerType = Integer.valueOf(user.getAnswerTitle().split(",")[1]);
                    VoteTopic randAnswerone = exteroceptiveMapper.findAnswerByType(oneAnswerType);
                    VoteTopic randAnswertwo = exteroceptiveMapper.findAnswerByType(twoAnswerType);
                    answerlist.add(randAnswerone);
                    answerlist.add(randAnswertwo);
                }
                map.put("answerList", JSON.toJSON(answerlist));
                map.put("user", user);
                map.put("channel_id", channel_id);
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
                    ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
                    if (user.getAnswerNum() > 0) {//时间内，白名单用户
                        int lostAnswerNum = exteroceptiveMapper.lostAnswerNum(userId);
                        if (lostAnswerNum > 0) {
                            //添加成绩 并创建表记录
                            int allMark = user.getMark() + 100;
                            int addmark = exteroceptiveMapper.addUserMark(userId, allMark);
                            if (addmark > 0) {
                                exteroceptiveMapper.updateAnswer(userId, answer, answerTitle);
                                InsertRecord("服务评测", userId, 100, channel_id, 1);
                            }
                        }
                        map.put("msg", "success");
                    } else {
                        map.put("msg", "timeout");
                    }
                    ActivityUser newuser = exteroceptiveMapper.findUserInfoByUserId(userId);
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

    /**
     * 完成吹泡泡
     *
     * @param channel_id
     * @param paopao
     * @param userId
     * @return
     */
    @Override
    public JSONObject play(String channel_id, String paopao, String userId) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        try {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
            log.info("吹泡泡" + userId + "paopao==" + paopao);
            if (StringUtils.isBlank(userId) || userId.length() < 11) {
                map.put("msg", "error");
            } else {
                Object userjedis = redisUtil.get(userId + "play");
                if (Objects.isNull(userjedis)) {
                    redisUtil.set(userId + "play", userId + "play", 1);
                    if (user.getBlowNum() > 0) {//时间内，白名单用户
                        int lostBlowNum = exteroceptiveMapper.lostBlowNum(userId);
                        if (lostBlowNum > 0) {
                            //添加成绩 并创建表记录
                            int newpaopao = Integer.valueOf(paopao);
                            if (newpaopao > 100) {
                                newpaopao = 100;
                            }
                            int allMark = user.getMark() + newpaopao;
                            int addmark = exteroceptiveMapper.addUserMark(userId, allMark);
                            if (addmark > 0) {
                                InsertRecord("趣味游戏", userId, newpaopao, channel_id, 2);
                            }
                            map.put("msg", "success");
                            map.put("paopao", paopao);
                        }
                    } else {
                        map.put("msg", "timeout");
                    }
                } else {
                    map.put("msg", "Repeated request");
                }
                ActivityUser newuser = exteroceptiveMapper.findUserInfoByUserId(userId);
                map.put("user", newuser);
            }
        } catch (Exception e) {
            map.put("msg", "error");
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }

    @Override
    public JSONObject tochoujiang(String channel_id, String userId) {
        System.out.println("tochoujiang");
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            if (userId != null) {
                ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
                int tishi = 0;
                String dengji = "0";
                int gameNum = 0;
                if (user.getMark() >= 4500 && user.getGrade() < 1) {
                    tishi = 1;
                    dengji = "白银";
                    gameNum = 2;
                } else if (user.getMark() >= 9000 && user.getGrade() < 2) {
                    tishi = 2;
                    gameNum = 3;
                    dengji = "黄金";
                } else if (user.getMark() >= 20000 && user.getGrade() < 3) {
                    tishi = 3;
                    gameNum = 5;
                    dengji = "铂金";
                } else if (user.getMark() >= 40000 && user.getGrade() < 3) {
                    tishi = 4;
                    gameNum = 6;
                    dengji = "钻石";
                }
                String dangDate = DateTimeTool.formatDate(new Date());
                String weekDate = DateTimeTool.formatDate(user.getWeekTime());
                if (dangDate.equals(weekDate) && tishi > 0) {
                    map.put("msg", "update");
                }
                exteroceptiveMapper.updateUserGrade(userId, tishi);
                map.put("user", user);
                map.put("tishi", tishi);
                map.put("dengji", dengji);
                map.put("gameNum", gameNum);
                map.put("level", dengji);
                map.put("channel_id", channel_id);
            } else {
                map.put("loginFlag", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }

    @Override
    public JSONObject choujiang(String channel_id, String userId, String actId,String ditch) {
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
                    ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
                    //流量礼包开始发放
                    if (user.getPlayNum() > 0) {//时间内，白名单用户
                        int lostPlayNum = exteroceptiveMapper.LostPlayNum(user.getId());
                        if (lostPlayNum > 0) {
                            ActivityConfiguration gift = new ActivityConfiguration();
                            //用户首次抽奖  500MB流量
                            if (user.getAward() < 1) {
                                int changestatus = exteroceptiveMapper.updateUserAward(userId);
                                if (changestatus > 0) {
                                    gift = exteroceptiveMapper.findGiftByTypeId(0, actId);
                                } else {
                                    gift = randomQYGift(actId);
                                }
                            } else {
                                Calendar calendar = Calendar.getInstance();
                                int month = calendar.get(Calendar.MONTH) + 1;
                                List<ActivityConfiguration> giftList = exteroceptiveMapper.findGiftList(month, actId);
                                gift = randomGiftByUserGrade(giftList, user.getGrade());
                                if (Double.valueOf(gift.getValue()) > 0) {//抽中流量话费
                                    //达80%（剩余/奖励总量 <20%）
                                    String curmonth = DateTimeTool.formatDateMonth(new Date());
                                    ActivityConfiguration monthgift = exteroceptiveMapper.findExperienceGiftList(gift.getUnlocked(), curmonth);
                                    log.info("剩余量/总量：" + (Double.valueOf(monthgift.getAmount()) / Double.valueOf(gift.getAmount())));
                                    if ((Double.valueOf(monthgift.getAmount()) / Double.valueOf(gift.getAmount())) < 0.2) {
                                        Random random = new Random();
                                        int n = random.nextInt(2);
                                        if (n == 1) {//发放权益
                                            gift = randomQYGift(actId);
                                            log.info("达80% 抽权益====" + gift.toString());
                                        }
                                    }
                                    if (Double.valueOf(gift.getValue()) > 0) {
                                        int lostCount = exteroceptiveMapper.lostGiftListCount(gift.getUnlocked(), curmonth);
                                        if (lostCount < 1) {
                                            gift = randomQYGift(actId);
                                            log.info("数量不足 发放权益====" + gift.toString());
                                        }
                                    }
                                }
                            }
                            saveHistory(gift, userId, channel_id,ditch);
                            ActivityUser users = exteroceptiveMapper.findUserInfoByUserId(userId);
                            map.put("gift", gift);
                            map.put("msg", "success");
                            map.put("user", users);
                            int tishi = 0;
                            if (user.getMark() >= 4500 && user.getGrade() < 1) {
                                tishi = 1;
                            } else if (user.getMark() >= 9000 && user.getGrade() < 2) {
                                tishi = 2;
                            } else if (user.getMark() >= 20000 && user.getGrade() < 3) {
                                tishi = 3;
                            } else if (user.getMark() >= 40000 && user.getGrade() < 4) {
                                tishi = 4;
                            }
                            exteroceptiveMapper.updateUserGrade(userId, tishi);
                        } else {
                            map.put("msg", "mjh");
                        }
                    } else {
                        map.put("msg", "timeout");
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
    public void saveHistory(ActivityConfiguration gift, String userId, String channel_id,String ditch) {
        ActivityUserHistory history = new ActivityUserHistory();
        history.setUserId(userId);
        history.setChannelId(channel_id);
        history.setValue(gift.getValue());
        history.setActId(gift.getActId());
        history.setRewardName(gift.getName());
        history.setTypeId(gift.getTypeId());
        history.setUnlocked(gift.getUnlocked());
        history.setRemark(gift.getRemark());
        history.setWinSrc(gift.getWinSrc());
        history.setDitch(ditch);
        history.setActivityId(gift.getActivityId());
        history.setItemId(gift.getItemId());
        int status = exteroceptiveMapper.saveHistory(history);
        try {
            if (status > 0 && Double.valueOf(gift.getValue()) > 0) {
                String mqMsg = commonService.issueReward(history);
                log.info("4147请求信息：" + mqMsg);
                jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
            }
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
        List<ActivityConfiguration> qyGiftList = exteroceptiveMapper.findGiftListByObtain(month, actId);
        ActivityConfiguration gift = randomListGift(qyGiftList);
        return gift;
    }

    /**
     * 用户经验值明细
     *
     * @param channel_id
     * @param userId
     * @return
     */
    @Override
    public JSONObject todetail(String channel_id, String userId) {
        System.out.println("todetail");
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            if (userId != null) {
                ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
                List<ActivityRecord> userRecordList = exteroceptiveMapper.findUserRecord(userId);
                for (ActivityRecord record : userRecordList) {
                    record.setTime(record.getCreateTime().getTime());
                }
                map.put("userRecordList", JSON.toJSON(userRecordList));
                map.put("user", user);
                map.put("channel_id", channel_id);
            } else {
                map.put("loginFlag", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("data", map);
        return jsonObject;
    }

    public static Date[] getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
        }
        Date[] dates = new Date[5];  // new Date[7] 星期日
        for (int i = 0; i < 5; i++) {  // i < 7 星期日
            dates[i] = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static Date getLastWeek5() {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 5 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        calendar2.add(Calendar.DATE, offset2 - 7);
        String lastBeginDate = sdf.format(calendar1.getTime());
        Date lastEndDate = calendar2.getTime();
        log.info("上周更新时间：" + lastEndDate);
        return lastEndDate;
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
    private ActivityConfiguration randomGiftByUserGrade(List<ActivityConfiguration> giftList, int grade) {
        double randomNum = RandomUtils.nextDouble();
        //0-1 随机小数
        log.info("随机数=" + randomNum);
        double startRate = 0;
        double endRate = 0;
        for (int i = 0; i < giftList.size(); i++) {
            log.info("rate=" + giftList.get(i).getProb());//0.33
            startRate = endRate; //0
            log.info("startRate=" + startRate);
            String[] newrate = giftList.get(i).getProb().split("_");
            String rate1 = newrate[(grade)];
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
     * 用户记录操作
     *
     * @param caozuo
     * @param userId
     */
    @Override
    public void changeStatus(String caozuo,String actId, String userId) {
        log.info("userId:" + userId + ",caozuo" + caozuo);
        Map<String, Object> map = new HashMap<>();
        OperationLog record = new OperationLog();
        record.setInstructions(caozuo);
        record.setUserId(userId);
        record.setActId(actId);
        record.setUserId(userId);
        try {
            commonMapper.insertOperationLog(record,"wt_proem_operationLog");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
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
            exteroceptiveMapper.saveShareHistory(share);
        }
    }

    public ActivityUser findEveryDayUser(String userId, String secToken) {
        try {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserId(userId);
            if (user == null) {
                return user;
            } else {
                //更新 secToken
              /*  if (!secToken.equals(user.getSecToken())) {
                    exteroceptiveMapper.updateUserSecToken(userId, secToken);
                }*/
                //每天更新吹泡泡 答题游戏机会
                if (!DateTimeTool.formatDate(user.getCreateTime()).equals(DateTimeTool.formatDate(new Date()))) {
                    //更新机会和时间
                    exteroceptiveMapper.updateUserCurInfo(userId);
                }
                //周五更新抽奖机会
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("本周更新日期：" + dateFormat.format(getWeekDay()[4]));
                String week5Date = dateFormat.format(getWeekDay()[4]);
                String dangDate = DateTimeTool.formatDate(new Date());
                Date week5 = dateFormat.parse(week5Date);
                Date curDate = dateFormat.parse(dangDate);
                Date lastweek5 = dateFormat.parse(dateFormat.format(getLastWeek5()));
                Date userweektime = dateFormat.parse(dateFormat.format(user.getWeekTime()));
                log.info("user.getWeekTime()" + userweektime.getTime());
                log.info("lastweek5" + lastweek5.getTime());
                //当前时间大于本周五  且更新日期小于本周五
                if ((curDate.getTime() >= week5.getTime() && user.getWeekTime().getTime() < week5.getTime()) || userweektime.getTime() < lastweek5.getTime()) {
                    int playNum = 0;
                    if (!DateTimeTool.formatDate(user.getWeekTime()).equals(DateTimeTool.formatDate(new Date()))) {
                        //根据用户等级更新宝箱抽奖机会 //二期是每周只有一次游戏机会了
                        playNum = 1;
                        exteroceptiveMapper.updateUserBaoXiangPlayNum(userId, playNum);
                    }
                }
                ActivityUser newuser = exteroceptiveMapper.findUserInfoByUserId(userId);
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
            List<ActivityUserHistory> list = exteroceptiveMapper.findUserRecived(userId);
            jsonObject.put("list", JSON.toJSON(list));
            jsonObject.put("userId", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
