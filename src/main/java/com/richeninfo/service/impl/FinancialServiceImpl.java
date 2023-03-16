package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.cluster.SmsMapper;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.FinancialMapper;
import com.richeninfo.pojo.ComEntry;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.FinancialService;
import com.richeninfo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/20 16:15
 */
@Slf4j
@Service("financialService")
public class FinancialServiceImpl implements FinancialService {

    @Value("${jinrong.url}")
    private String url;
    @Resource
    private FinancialMapper financialMapper;
    @Resource
    private SmsMapper smsMapper;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private HttpServletRequest request;
    @Resource
    private PacketHelper packetHelper;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 短信验证码
     * 注册接口
     * @param comEntry
     * @return
     */
    @Override
    public JSONObject  sendMsgCodeAndRegister(ComEntry comEntry,HttpSession session){
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        String result="";
        try {
            url = url+"/"+comEntry.getUrlCoding();
            jsonObject.put("channel",comEntry.getChannel());
            jsonObject.put("timestamp",comEntry.getTimestamp());
            jsonObject.put("sign",comEntry.getSign());
            jsonObject.put("uuid",comEntry.getUuid());
            jsonObject.put("mobile",comEntry.getMobile());
            if(comEntry.getUrlCoding().equals("register")){
                jsonObject.put("authcode",comEntry.getAuthcode());
                jsonObject.put("applyserial",comEntry.getApplyserial());
            }
            result = HttpClientTool.doPostString(url,jsonObject.toJSONString());
            log.info("接口返回结果"+result);
            result= AESCBC.decryptAES(result,comEntry.getUuid());
            log.info("解密后数据"+result);
            if(JSONObject.parseObject(result).getString("code").equals("0000")){
                Map<String, Object> smsCodeMap = new HashMap<>(5);
                smsCodeMap.put("userId", comEntry.getMobile());
                smsCodeMap.put("applyserial",JSONObject.parseObject(result).getString("applyserial"));
                smsCodeMap.put("date", new Date().getTime());
                session.setAttribute(Constant.SMS_CODE_MAP,smsCodeMap);
                object.put(Constant.MSG,Constant.SUCCESS);
            }else{
                object.put(Constant.MSG,Constant.ERROR_CODE);
            }
            object.put(Constant.DATA,result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public ActivityUser insertUser(ActivityUser user) {
        try {
            user.setUserId(AES.desEncrypt(user.getUserId()));
            ActivityUser select_user = financialMapper.selectUser(user.getUserId());
            if(select_user==null){
                ActivityUser new_user = new ActivityUser();
                new_user.setUserId(user.getUserId());
                new_user.setBelongFlag(user.getBelongFlag());
                new_user.setActId(user.getActId());
                List<ActivityRoster> roster = financialMapper.selectRoster(user.getUserId());
                if(CollectionUtils.isEmpty(roster)){
                    new_user.setUserType(0);
                }else{
                    new_user.setUserType(1);
                }
                financialMapper.insertUser(new_user);
                user=new_user;
            }else{
                user=select_user;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public JSONObject instantDraw(ActivityUser user) {
        JSONObject resultObj = new JSONObject();
        if(user.getUserId().isEmpty()){
            resultObj.put(Constant.MSG,Constant.LOGIN);
        }else{
            user = this.insertUser(user);
            if(user.getUserType()>0){
                resultObj.put(Constant.MSG,Constant.NO_RIGHTS);
            }else{
                if(user.getClaimStatus()>0){//判断用户是否已抽过奖
                    resultObj.put(Constant.MSG,Constant.YLQ);
                }else{
                    financialMapper.updateClaimStatus(user.getId());//更新用户标识 已抽奖
                    String ip = IPUtil.getRealRequestIp(request);
                    //获得第一个点的位置
                    int index = ip.indexOf(".");
                    index = ip.indexOf(".", index + 3);
                    String ipScanner = ip.substring(0,index+3);
                    List<ActivityCardList> ip_list= financialMapper.selectCardListByIp(ipScanner,df.format(new Date()));
                    ActivityUserHistory user_history = new ActivityUserHistory();
                    user_history.setIp(ip);
                    user_history.setUserId(user.getUserId());
                    if(ip_list.size()>50){//该IP段当天中奖人数超过五十 则直接认定为不中奖
                        user_history.setUnlocked(0);
                        user_history.setRewardName("不中奖");
                        user.setUnlocked(0);
                        user.setNote("不中奖");
                        resultObj.put(Constant.MSG,Constant.NO_DATA);
                    }else{
                        ActivityUserHistory history = financialMapper.selectHistoryByUserId(user.getUserId());
                        if(history==null){
                            ActivityCardList card_user =  financialMapper.selectCardByUserId(user.getUserId());
                            if(card_user==null){//判断用户是否已抽中过奖
                                //List<ActivityConfiguration> giftList = financialMapper.selectConfiguration();
                                List<ActivityConfiguration> giftList =commonMapper.selectActivityConfigurationByActId(user.getActId());
                                ActivityConfiguration qy_gift =CommonUtil.randomGift(giftList);
                                if(qy_gift.getUnlocked()==0){//判断是否抽中 不中奖
                                    user_history.setUnlocked(qy_gift.getUnlocked());
                                    user_history.setRewardName(qy_gift.getName());
                                    user.setUnlocked(qy_gift.getUnlocked());
                                    user.setNote(qy_gift.getName());
                                    resultObj.put(Constant.MSG,Constant.NO_DATA);
                                }else{
                                    if(qy_gift.getAllAmount()>0){//判断奖品剩余总量
                                        List<ActivityCardList> wfb_card =financialMapper.selectCardList(qy_gift.getUnlocked());
                                        if(wfb_card.isEmpty()){//查找未分配奖励
                                            user_history.setUnlocked(0);
                                            user_history.setRewardName("不中奖");
                                            user.setUnlocked(0);
                                            user.setNote("不中奖");
                                            resultObj.put(Constant.MSG,Constant.NO_DATA);
                                        }else{
                                            ActivityCardList fb_card = financialMapper.selectCard(qy_gift.getUnlocked());
                                            int up_result = financialMapper.updateCardStatus(user.getUserId(),ip,ipScanner,fb_card.getId());
                                            if(up_result>0){//分配成功
                                               // financialMapper.updateAmount(qy_gift.getId());//更新奖励数量
                                                commonMapper.updateAmount(qy_gift.getId());//更新奖励数量
                                                user_history.setUnlocked(qy_gift.getUnlocked());
                                                user_history.setRewardName(qy_gift.getName());
                                                user.setUnlocked(qy_gift.getUnlocked());
                                                user.setNote(qy_gift.getName());
                                                resultObj.put(Constant.MSG,Constant.SUCCESS);
                                                resultObj.put("gift",qy_gift);
                                            }else{
                                                user_history.setUnlocked(0);
                                                user_history.setRewardName("不中奖");
                                                user.setUnlocked(0);
                                                user.setNote("不中奖");
                                                resultObj.put(Constant.MSG,Constant.NO_DATA);
                                            }
                                        }
                                    }else{
                                        user_history.setUnlocked(0);
                                        user_history.setRewardName("不中奖");
                                        user.setUnlocked(0);
                                        user.setNote("不中奖");
                                        resultObj.put(Constant.MSG,Constant.NO_DATA);
                                    }
                                }
                            }else{
                                resultObj.put(Constant.MSG,Constant.YLQ);
                            }
                        }else {
                            resultObj.put(Constant.MSG,Constant.YLQ);
                        }
                    }
                    financialMapper.insertUserHistory(user_history);
                    financialMapper.updateUserInfo(user);
                }
            }
        }

        return resultObj;
    }

    @Override
    public void recordLog(OpenapiLog openapiLog) {
        try {
            openapiLog.setAddress(IPUtil.getRealRequestIp(request));
            openapiLog.setUserId(AES.desEncrypt(openapiLog.getUserId()));
            commonMapper.insertOpenapiLog(openapiLog);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public JSONObject sendAwardMsg(String activityName,String createDate) {
        JSONObject object = new JSONObject();
        if(activityName.isEmpty()||createDate.isEmpty()){
            object.put(Constant.MSG,Constant.ERROR);
        }else{
            createDate = df.format(new Date(Long.parseLong(createDate)));
            String timeStamp = Long.toString(new Date().getTime());
            String channelId="";
           /* String[] params=new String[2];
            params[0]=activityName;*/
            List<ActivityCardList> cardList = financialMapper.selectCardListByStatus(createDate);
            if(cardList.isEmpty()){
                object.put(Constant.MSG,Constant.NO_DATA);
            }else{
                for (ActivityCardList card:cardList) {
                   /* params[1]=card.getCouponCode();
                    packetHelper.sendDSMS(card.getUserId(),params);*/
                    if(card.getTypeId()==1){//京东
                        channelId="京东";
                    }else if(card.getTypeId()==2){//天猫
                        channelId="天猫";
                    }
                    String params="您已获得"+card.getName()+"，兑换卡号："+card.getCouponCode()+"密码："+card.getCardPwd()+"请登录注册"+channelId+"APP，尽快绑定卡号密码使用。点击下载国泰君安资管APP：https://www.baidu.com/获得您的专业理财基金交易顾问。";
                    smsMapper.sendInviteMsg(timeStamp,card.getUserId(),params);
                    financialMapper.updateCardCodeStatus(card.getId());//更新发放标识
                }
                object.put(Constant.MSG,Constant.SUCCESS);
            }
        }
        return object;
    }
}
