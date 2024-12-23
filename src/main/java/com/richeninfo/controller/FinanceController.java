/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FinanceService;
import com.richeninfo.util.RSAUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 15:04
 */
@Controller
@Api(value = "中国移动云盘财运日", tags = {"财运日秒杀"})
@RequestMapping("/finance")
@Slf4j
public class FinanceController {
    @Resource
    private CommonService commonService;
    @Resource
    private FinanceService financeService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private RSAUtils rsaUtils;
    SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "初始化用户", httpMethod = "POST")
    @PostMapping(value = "/initialize")
    public @ResponseBody
    void initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        ActivityUser user = new ActivityUser();
        JSONObject object = new JSONObject();
        Instant start = Instant.now(); // 开始时间
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile;
             if(channelId.equals("leadeonypSession")){
                 mobile = rsaUtils.decryptByPriKey(secToken).trim();
             }else{
                 mobile= commonService.getMobile(secToken, channelId);
             }
            if (mobile==null||mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            }else{
                user.setUserId(mobile);
                user.setSecToken(secToken);
                user.setChannelId(channelId);
                user.setActId(actId);
                user.setCreateDate(month.format(new Date()));
                user.setDitch(ditch);
                user = financeService.insertUser(user);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("user", user);
            }
        }
        Instant end = Instant.now(); // 结束时间
        long duration = Duration.between(start, end).toMillis(); // 计算耗时
        System.out.println("接口耗时: " + duration + " 毫秒");
        saveOpenapiLog("接口耗时: " + duration + " 毫秒","initializeUser", "",  "", "finance");//保存用户调用记录
        resp.getWriter().write(object.toJSONString());
    }


    @ApiOperation("获取奖励列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId) throws Exception {
        Instant start = Instant.now(); // 开始时间
        CommonController.getActId(request, financeService.getConfiguration(secToken, actId, channelId), resp, secToken);
        Instant end = Instant.now(); // 结束时间
        long duration = Duration.between(start, end).toMillis(); // 计算耗时
        System.out.println("接口耗时: " + duration + " 毫秒");
        saveOpenapiLog("接口耗时: " + duration + " 毫秒","getConf", "",  "", "finance");//保存用户调
    }


    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        Instant start = Instant.now(); // 开始时间
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        Instant end = Instant.now(); // 结束时间
        long duration = Duration.between(start, end).toMillis(); // 计算耗时
        System.out.println("接口耗时: " + duration + " 毫秒");
        saveOpenapiLog("接口耗时: " + duration + " 毫秒","userDraw", "",  "", "finance");//保存用户调
        return this.financeService.submit(secToken, actId, unlocked, channelId,ditch);
    }

    /**
     * 我的奖励
     * @param actId
     * @param channelId
     * @return
     */
    @ApiOperation("我的奖励")
    @PostMapping(value = "/getMyReward")
    public @ResponseBody
    Object getMyReward(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "actId", value = "活动标识", required = true) String actId, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId){
        return this.financeService.getMyReward(secToken,channelId,actId);
    }

    /**
     *
     * @param appCode 编码
     * @param message 入参
     * @param response 出参
     * @param userId  用户标识
     * @param actId  活动标识
     */
    public void saveOpenapiLog( String appCode, String message, String response, String userId, String actId) {
        OpenapiLog log = new OpenapiLog();
        log.setAppCode(appCode);
        log.setCode(message);
        log.setMessage(response);
        log.setUserId(userId);
        log.setActId(actId);
        commonMapper.insertOpenapiLog(log);
    }

}
