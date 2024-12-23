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
import com.richeninfo.service.FortuneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 14:32
 */

@Controller

@RequestMapping("/2024/12/purchase")
@Api(value = "购机彩蛋礼", tags = {"适老产品调查问卷活动&&购机彩蛋礼"})
public class FortuneController {

    @Resource
    private FortuneService FortuneService;
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
    JSONObject initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String ditch) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = FortuneService.initializeUser(mobile, secToken, channelId, actId,ditch);
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
    @PostMapping(value = "/draw")
    public @ResponseBody
    JSONObject getActGift(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, String ditch) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = FortuneService.draw(mobile, secToken, channelId, actId,ditch);
            }
        }
        return object;
    }

    /**
     * 用户操作记录
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/actRecord")
    @ResponseBody
    public JSONObject actRecord(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, String caozuo) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String userId = commonService.getMobile(secToken, channelId);
            if (userId.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                FortuneService.actRecord(caozuo, actId, userId);
                object.put(Constant.MSG, "success");
            }
        }
        return object;
    }

    /**
     * 用户操作记录
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/userReward")
    @ResponseBody
    public JSONObject userReward(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String userId = commonService.getMobile(secToken, channelId);
            if (userId.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=FortuneService.userReward(userId,actId,channelId);
            }
        }
        return object;
    }

}
