/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */
package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.mapper.master.TasteOfMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.TasteOfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @auth sunxiaolei
 * @date 2024/3/22 15:40
 */

@Controller
@Api(value = "2025心级体验官", tags = {"2025心级体验官"})
@RequestMapping(value = "/2025/01/tasteOf")
public class TasteOfController {
    @Resource
    private CommonService commonService;
    @Resource
    private TasteOfService TasteOfService;
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
            System.out.println("客户端传输："+secToken);
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = TasteOfService.initializeUser(mobile, secToken, channelId, actId,ditch);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 去答题
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/toanswer")
    @ResponseBody
    public JSONObject toanswer(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = TasteOfService.toAnswer(channelId,mobile);
            }
        }
        return object;
    }

    /**
     * 提交评测内容
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/answer")
    @ResponseBody
    public JSONObject answer(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, String answer, String answerTitle) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = TasteOfService.answer(channelId, answer, answerTitle, mobile);
            }
        }
        return object;
    }


    /**
     * 抽奖
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/choujiang")
    @ResponseBody
    public JSONObject choujiang(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = TasteOfService.choujiang(channelId, mobile, actId,ditch);
            }
        }
        return object;
    }

    /**
     * 我的奖励
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/received")
    @ResponseBody
    public JSONObject received(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object = TasteOfService.myReceived(channelId, mobile, actId);
            }
        }
        return object;
    }


    /**
     * 成为心级心级体验官level标识
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/changeLevel")
    @ResponseBody
    public void changeLevel(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        TasteOfService.changeLevel(channelId, secToken, actId);
    }
}
