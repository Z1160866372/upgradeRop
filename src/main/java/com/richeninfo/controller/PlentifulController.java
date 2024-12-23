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
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.mapper.master.PlentifulMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlentifulService;
import com.richeninfo.service.PlentifulService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
 * @auth sunxiaolei
 * @date 2024/11/21 14:32
 */

@Controller
@RequestMapping("/2024/12/feedback")
@Api(value = "欢乐扭蛋", tags = {"欢乐扭蛋&&权益超市五周年回馈抽奖"})
public class PlentifulController {


    @Resource
    private CommonService commonService;
    @Resource
    private PlentifulService PlentifulService;
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
                user = PlentifulService.insertUser(user);
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
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JSON.toJSONString(PlentifulService.getConfiguration()));
    }

    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.PlentifulService.submit(secToken, actId, channelId,ditch);
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
        return this.PlentifulService.getMyReward(secToken,channelId,actId);
    }


    @ApiOperation("用户点击办理")
    @PostMapping("/transaction")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked
            ,@ApiParam(name = "wtAcId", value = "wtAcId", required = true) String wtAcId, @ApiParam(name = "wtAc", value = "wtAc", required = true) String wtAc, @ApiParam(name = "randCode", value = "二次短信验证码", required = true) String randCode, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.PlentifulService.transaction(secToken, actId, unlocked, channelId,wtAcId,wtAc,randCode,ditch);
    }

}
