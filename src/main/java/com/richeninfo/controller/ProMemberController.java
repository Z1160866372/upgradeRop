/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Post;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ProMemberService;
import com.richeninfo.util.RedisUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.soap.Addressing;
import java.io.IOException;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/14 17:03
 */
@Controller
@RequestMapping(value = "/proMember")
@Api(value = "PRO会员日活动接口", tags = {"PRO会员日活动接口"})
public class
ProMemberController {
    @Resource
    private ProMemberService proMemberService;
    @Resource
    private HttpSession session;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;

    @ApiOperation("获取奖励列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "actId", value = "活动编号", required = true) String actId,@ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId) throws IOException {
        CommonController.getActId(request, proMemberService.getConfiguration(secToken, actId,channelId), resp, secToken);
    }

    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.proMemberService.submit(secToken, actId, unlocked, session, channelId);
    }


}
