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
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityShare;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OperationLog;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FinanceService;
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
    SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "初始化用户", httpMethod = "POST")
    @PostMapping(value = "/initialize")
    public @ResponseBody
    void initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws IOException {
        ActivityUser user = new ActivityUser();
        JSONObject object = new JSONObject();
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
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
        resp.getWriter().write(object.toJSONString());
    }

    @ApiOperation("获取奖励列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId) throws Exception {
        CommonController.getActId(request, financeService.getConfiguration(secToken, actId, channelId), resp, secToken);
    }


    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
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

}
