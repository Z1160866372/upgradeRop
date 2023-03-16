package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.pojo.ComEntry;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/20 16:07
 * 金融大转盘
 */
public interface FinancialService {
    /**
     * 发送验证码接口
     * 注册接口
     * @return
     */
    JSONObject sendMsgCodeAndRegister(ComEntry comEntry, HttpSession session);

    /**
     * 初始化用户
     * @param user
     * @return
     */
    ActivityUser insertUser(@ModelAttribute ActivityUser user);

    /**
     * 立即抽奖
     * @param user
     * @return
     */
    JSONObject instantDraw(@ModelAttribute ActivityUser user);

    /**
     * 保存用户调用记录
     * @param log
     */
    void recordLog(@ModelAttribute OpenapiLog log);

    /**
     * 根据日期发放奖励
     * @param createDate
     * @return
     */
    JSONObject sendAwardMsg(String activityName,String createDate);

}
