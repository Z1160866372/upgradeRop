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
public class ProMemberController {

    @Resource
    private CommonService commonService;
    @Resource
    private ProMemberService proMemberService;
    @Resource
    private HttpSession session;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;
    @Resource
    private RedisUtil redisUtil;


    @ApiOperation(value = "初始化用户", httpMethod = "POST")
    @PostMapping(value = "/initialize")
    public @ResponseBody
    void initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        ActivityUser user = new ActivityUser();
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                user.setUserId(mobile);
                user.setSecToken(secToken);
                user.setChannelId(channelId);
                user.setActId(actId);
                user = proMemberService.insertUser(user);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("user", user);
            }
        }
        resp.getWriter().write(object.toJSONString());
    }

    @ApiOperation("获取奖励列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        String mobilePhone = redisUtil.get(Constant.KEY_MOBILE) == null ? "" : (String)redisUtil.get(Constant.KEY_MOBILE);
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        List<ActivityConfiguration> config = proMemberService.getConfiguration(mobilePhone, actId);
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(JSON.toJSONString(config));
    }

    @ApiOperation("用户点击领取")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked) {
        String mobilePhone = redisUtil.get(Constant.KEY_MOBILE) == null ? "" : (String)redisUtil.get(Constant.KEY_MOBILE);
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        unlocked = request.getParameter("unlocked") == null ? 0 : Integer.parseInt(request.getParameter("unlocked"));
        return this.proMemberService.submit(mobilePhone, actId, unlocked, session, channelId);
    }


}