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
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FoodietService;
import com.richeninfo.service.ProtectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@Api(value = "“向上 向善 大家谈”主题活动", tags = {"“向上 向善 大家谈”主题活动"})
@RequestMapping("2024/08/meliorist")
@Slf4j
public class FoodieController {
    @Resource
    private CommonService commonService;
    @Resource
    private FoodietService foodietService;
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
    void initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId
            , @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws IOException {
        ActivityUser user = new ActivityUser();
        JSONObject object = new JSONObject();
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            }else{
                user.setUserId(mobile);
                user.setSecToken(secToken);
                user.setChannelId(channelId);
                user.setActId(actId);
                user.setDitch(ditch);
                user.setCreateDate(day.format(new Date()));
                user = foodietService.insertUser(user);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("user", user);
            }
        }
        resp.getWriter().write(object.toJSONString());
    }

    @ApiOperation("获取展示列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@RequestParam(defaultValue = "1") Integer page,
                 @RequestParam(defaultValue = "10") Integer limit, @ApiParam(name = "typeId", value = "提交|评论标识", required = true) Integer typeId,@ApiParam(name = "ip", value = "被点评用户标识", required = true) String ip
            ,@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId) throws IOException {
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        resp.getWriter().write(JSON.toJSONString(foodietService.getActivityUserList(secToken, actId, channelId,page,limit,typeId,ip)));
    }


    @ApiOperation("用户点击提交评论点赞")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "ip", value = "被点评用户标识", required = true) String ip, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "typeId", value = "提交点赞评论标识", required = true) Integer typeId,
                        @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "code", value = "code", required = true) String code, @ApiParam(name = "message", value = "message", required = true) String message, @ApiParam(name = "remark", value = "提交内容", required = true) String remark, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws Exception {
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        ditch = request.getParameter("ditch") == null ? "" : request.getParameter("ditch");
        code = request.getParameter("code") == null ? "" : request.getParameter("code");
        message = request.getParameter("message") == null ? "" : request.getParameter("message");
        remark = request.getParameter("remark") == null ? "" : request.getParameter("remark");
        return this.foodietService.submit(secToken, actId, typeId,channelId,code,message,remark,ditch,ip);
    }

}
