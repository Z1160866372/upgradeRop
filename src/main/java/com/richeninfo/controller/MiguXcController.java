/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MiguXcService;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @auth sunxiaolei
 * @date 2024/5/11 11:16
 */
@Controller
@RequestMapping("/miguxc")
public class MiguXcController {

    @Resource
    private MiguXcService miguXcService;

    @Resource
    private CommonService commonService;

    /**
     * 初始化用户数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/initialize")
    public @ResponseBody
    JSONObject initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = miguXcService.initializeUser(mobile, secToken, channelId, actId);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     *领取活动礼包
     * @param secToken 用户sectoken
     * @param channelId 渠道
     * @param actId  活动标识
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/getActGift")
    public @ResponseBody
    JSONObject getActGift(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String randCode) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = miguXcService.getActGift(mobile, secToken, channelId, actId,randCode);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 初始化用户数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/videoList")
    public @ResponseBody
    JSONObject videoList(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = miguXcService.selectVideoList(mobile, secToken, channelId, actId);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 初始化用户数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/sendMessage5956")
    public @ResponseBody
    JSONObject sendMessage5956(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = miguXcService.sendMessage5956(mobile, secToken, channelId, actId);
                object.put("data", object1);
            }
        }
        return object;
    }
}
