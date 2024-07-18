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
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.MiguDJService;
import io.swagger.annotations.Api;
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
import java.util.List;
import java.util.Map;

/**
 * @auth sunxiaolei
 * @date 2024/7/16 11:16
 */
@Controller
@RequestMapping("/2024/07/migudj")
@Api(value = "咪咕视频短剧版", tags = {"咪咕视频短剧版"})
public class MiguDJController {

    @Resource
    private MiguDJService miguDJService;

    @Resource
    private CommonService commonService;
    @Resource
    private HttpServletResponse resp;

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
                object.put(Constant.MSG, "login");
            } else {
                 object = miguDJService.initializeUser(mobile, secToken, channelId, actId,ditch);
            }
        }
        return object;
    }

    /**
     *业务办理
     * @param secToken 用户sectoken
     * @param channelId 渠道
     * @param actId  活动标识
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/getActGift")
    public @ResponseBody
    JSONObject getActGift(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String randCode,String wtAcId, String wtAc,String ditch) throws IOException {
        return  miguDJService.getActGift(secToken, channelId, actId,randCode, wtAcId,wtAc,ditch);
    }

    /**
     * 视频展示数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/videoList")
    public @ResponseBody
    JSONObject videoList( @ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
       // resp.getWriter().write(JSON.toJSONString(miguDJService.selectVideoList(secToken, channelId, actId)));
      return  miguDJService.selectVideoList(secToken, channelId, actId);
    }

    /**
     * 视频展示数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/videoListNew")
    public @ResponseBody
    Map<Integer, List<ActivityConfiguration>> videoListNew(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        return  miguDJService.selectVideoListNew(secToken, channelId, actId);
    }

    /**
     * 短信下发接口
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
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = miguDJService.sendMessage5956(mobile, secToken, channelId, actId);
                object.put("data", object1);
            }
        }
        return object;
    }
}
