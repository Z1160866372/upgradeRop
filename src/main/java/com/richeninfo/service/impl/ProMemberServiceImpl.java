package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityRoster;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.ProMemberMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ProMemberService;
import com.richeninfo.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/17 14:03
 */
@Service("proMemberService")
@Slf4j
public class ProMemberServiceImpl implements ProMemberService {
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private CommonUtil commonUtil;
    @Resource
    private ProMemberMapper proMemberMapper;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private HttpSession session;

    DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    Date current_time = new Date();

    /**
     * 获取奖励列表
     *
     * @param secToken
     * @param actId
     * @return
     */
    public List<ActivityConfiguration> getConfiguration(String secToken, String actId,String channelId) {
        List<ActivityConfiguration> last_pro_config = new ArrayList<ActivityConfiguration>();
        try {
            List<ActivityConfiguration> pro_config = commonMapper.selectActivityConfigurationByActId(actId);
            if (secToken.isEmpty()) {
                last_pro_config = pro_config;
            } else {
                String mobilePhone= commonService.getMobile(secToken,channelId);
                ActivityUser user = proMemberMapper.selectUser(mobilePhone, actId);
                if (user == null) {
                    last_pro_config = pro_config;
                    return last_pro_config;
                }
                for (ActivityConfiguration config : pro_config) {
                    config.setUserType(user.getUserType());
                    ActivityUserHistory history = proMemberMapper.selectHistoryByUnlocked(mobilePhone, config.getUnlocked(), actId);
                    if (history != null) {
                        config.setStatus(1);
                    } else {
                        if ((config.getTypeId() == 1 || config.getTypeId() == 3) && config.getAllAmount() == 0) {//限量奖励
                            config.setStatus(2);
                        }
                    }
                    if (config.getTypeId() == 2) {//时间限制
                        if (commonUtil.getDate(df.parse(config.getStartTime()))) {
                            config.setStatus(3);
                        }
                        if (commonUtil.getDate(df.parse(config.getEndTime()))) {
                            config.setStatus(4);
                        }
                    }
                    last_pro_config.add(config);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return last_pro_config;
    }

    /**
     * 用户点击领取
     *
     * @param secToken
     * @param actId
     * @param unlocked
     * @param session
     * @return
     * @throws Exception
     */
    public JSONObject submit(String secToken, String actId, int unlocked, HttpSession session, String channelId) {
        JSONObject object = new JSONObject();
        try {
            if (commonService.verityActive(actId,secToken, false, channelId).getString(Constant.MSG).equals("success")) {
               String mobilePhone = commonService.getMobile(secToken,channelId);
                ActivityUser user = proMemberMapper.selectUser(mobilePhone, actId);
                ActivityConfiguration config = commonMapper.selectActivitySomeConfiguration(actId, unlocked);
                ActivityUserHistory history;
                if (user.getUserType() <= 0) {
                    if (config.getTypeId() == 3 || config.getTypeId() == 4) {
                        if (config.getTypeId() == 3) {
                            if (config.getAllAmount() <= 0) {
                                object.put(Constant.MSG, "noNum");
                                return object;
                            }
                        }
                        history = proMemberMapper.selectHistoryByUnlocked(mobilePhone, Constant.white_figure, actId);
                        if (history == null) {
                            List<ActivityConfiguration> conf_list = commonMapper.selectActivityConfigurationByActIdAndModule(actId, Constant.wap_figure);
                            insertHistory(conf_list, user, channelId);
                        }
                    }
                }
                history = proMemberMapper.selectHistoryByUnlocked(mobilePhone, config.getUnlocked(), actId);
                object = verityHistory(object, config, history, user, channelId);

            } else {
                return commonService.verityActive(actId,secToken, false, channelId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private JSONObject verityHistory(JSONObject object, ActivityConfiguration config, ActivityUserHistory history, ActivityUser user, String channelId) throws Exception {
        String msg = "";
        boolean flog = false;
        if (history == null) {
            List<ActivityConfiguration> conf_list = new ArrayList<ActivityConfiguration>();
            if (config.getTypeId() == 1 || config.getTypeId() == 3) {//限量
                if (config.getAllAmount() > 0) {
                    int update_result = commonMapper.updateAmount(config.getId());//更新数量
                    if (update_result > 0) {
                        conf_list.add(config);
                        flog = insertHistory(conf_list, user, channelId);
                        if (flog) {
                            msg = "success";
                        } else {
                            msg = "error";
                        }
                    } else {
                        msg = "noNum";
                    }
                } else {
                    msg = "noNum";
                }
            } else {
                conf_list.add(config);
                flog = insertHistory(conf_list, user, channelId);
                if (flog) {
                    msg = "success";
                } else {
                    msg = "error";
                }
            }
            object.put(Constant.MSG, msg);
        } else {
            object.put(Constant.MSG, "ylq");
        }
        return object;
    }

    private boolean insertHistory(List<ActivityConfiguration> conf_list, ActivityUser user, String channelId) throws Exception {
        String mqMsg;
        for (ActivityConfiguration config : conf_list) {
            ActivityUserHistory history = new ActivityUserHistory();
            history.setSecToken(user.getSecToken());
            history.setUserId(user.getUserId());
            history.setChannelId(channelId);
            history.setRewardName(config.getName());
            history.setTypeId(config.getTypeId());
            history.setUnlocked(config.getUnlocked());
            history.setActId(config.getActId());
            history.setActivityId(config.getActivityId());
            history.setItemId(config.getItemId());
            history.setKeyword(commonMapper.selectActivityByActId(config.getActId()).getKeyword());
            proMemberMapper.insertUserHistory(history);
            if (config.getTypeId() == 0) {//4147礼包接口
                mqMsg = commonService.issueReward(history);
                log.info("4147请求信息：" + mqMsg);
                jmsMessagingTemplate.convertAndSend("commonQueue",mqMsg);
            }
        }
        return true;
    }
}
