/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FinanceService;
import com.richeninfo.service.JourneyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 15:04
 */
@Controller
@Api(value = "智享5G新风尚-年中狂欢GO", tags = {"5G新风尚"})
@RequestMapping("/2024/06/journey")
@Slf4j
public class JourneyController {
    @Resource
    private CommonService commonService;
    @Resource
    private JourneyService journeyService;
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
                user.setCreateDate(day.format(new Date()));
                user.setDitch(ditch);
                user = journeyService.insertUser(user);
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
        CommonController.getActId(request, journeyService.getConfiguration(secToken, actId, channelId), resp, secToken);
    }

    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.journeyService.submit(secToken, actId, unlocked, channelId,ditch);
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
        return this.journeyService.getMyReward(secToken,channelId,actId);
    }

    @ApiOperation("用户点击办理")
    @PostMapping("/transaction")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked
            ,@ApiParam(name = "wtAcId", value = "wtAcId", required = true) String wtAcId, @ApiParam(name = "wtAc", value = "wtAc", required = true) String wtAc, @ApiParam(name = "randCode", value = "二次短信验证码", required = true) String randCode, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.journeyService.transaction(secToken, actId, unlocked, channelId,wtAcId,wtAc,randCode,ditch);
    }
}
