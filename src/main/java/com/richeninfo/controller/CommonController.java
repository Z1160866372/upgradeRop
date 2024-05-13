/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityShare;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OperationLog;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.util.Des3SSL;
import com.richeninfo.util.ImageUtil;
import com.richeninfo.util.RSAUtils;
import com.richeninfo.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/21 11:21
 */
@Controller
@Api(value = "互动营销活动公共接口", tags = {"互动营销活动公共接口"})
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private CommonService commonService;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;
    @Resource
    private ImageUtil imageUtil;
    @Resource
    private RSAUtils rsaUtils;
    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/img-verify-code")
    @ResponseBody
    @ApiOperation("获取图形验证码")
    protected JSONObject images() throws Exception {
        JSONObject jsonRESTResult = new JSONObject();
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = imageUtil.createImage();
        //将验证码存入redis
        redisUtil.set(""+objs[0]+"", objs[0]);
        //将图片转正base64
        BufferedImage image = (BufferedImage) objs[1];
        //转base64
       // BASE64Encoder encoder = new BASE64Encoder();
        Base64.Encoder encoder = Base64.getEncoder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        ImageIO.write(image, "png", baos);//写入流中
        byte[] bytes = baos.toByteArray();//转换成字节
        String png_base64 = encoder.encodeToString(bytes).trim();//转换成base64串
        //删除 \r\n
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");
        Map map = new HashMap<>();
        map.put("base64", "data:image/png;base64," + png_base64);
        map.put("validateCode", objs[0]);
        jsonRESTResult.put("map", map);
        return jsonRESTResult;

    }

    @PostMapping(value = "/newSendMsg")
    @ApiOperation("获取短信验证码")
    public @ResponseBody
    Object sendMsg(@ApiParam(name = "smsRandom", value = "图形验证码", required = true) String smsRandom, @ApiParam(name = "mobilePhone", value = "用户号码", required = true) String mobilePhone) throws Exception {
        log.info("获取：" + redisUtil.get(smsRandom));
        Cookie[] cookies = request.getCookies();
        smsRandom = request.getParameter(Constant.SMS_RANDOM) == null ? "" : request.getParameter(Constant.SMS_RANDOM);
        JSONObject resultObj = new JSONObject();
        resultObj.put("cookie", cookies);
        //校验图形参数是否正确 不区分大小写
        if (redisUtil.get(smsRandom) == null || !smsRandom.toLowerCase().equals(redisUtil.get(smsRandom).toString().toLowerCase())) {
            resultObj.put(Constant.MSG, Constant.YZM_ERROR);
            return resultObj;
        }
        mobilePhone = request.getParameter(Constant.KEY_MOBILE) == null ? "" : request.getParameter(Constant.KEY_MOBILE);
        if (!mobilePhone.isEmpty()) {
            mobilePhone = rsaUtils.decryptByPriKey(mobilePhone).trim();
        }
        if (!commonService.checkUserIsChinaMobile(mobilePhone)) {
            resultObj.put(Constant.MSG, Constant.ERROR);
            return resultObj;
        }
        return this.commonService.sendMsgCode(mobilePhone);
    }

    @PostMapping(value = "/login_check")
    @ApiOperation("H5登录验证")
    public @ResponseBody
    Object validMsgNoVal(@ApiParam(name = "keyCode", value = "短信验证码", required = true) String keyCode, @ApiParam(name = "mobilePhone", value = "用户号码", required = true) String mobilePhone) throws Exception {
        JSONObject resultObj = new JSONObject();
        mobilePhone = request.getParameter(Constant.KEY_MOBILE) == null ? "" : request.getParameter(Constant.KEY_MOBILE);
        keyCode = request.getParameter(Constant.KEY_CODE) == null ? "" : request.getParameter(Constant.KEY_CODE);
        if (mobilePhone.isEmpty() || keyCode.isEmpty()) {
            resultObj.put(Constant.MSG, Constant.SMS_MOBIEL_OR_CODE_IS_NULL);
            return resultObj;
        }
        if (!mobilePhone.isEmpty()) {
            mobilePhone = rsaUtils.decryptByPriKey(mobilePhone).trim();
        }
        //校验验证码
        boolean isMatched = false;
        isMatched = this.commonService.valSendMsgCode(mobilePhone, keyCode);
        if (isMatched) {
            redisUtil.set(Constant.KEY_MOBILE,mobilePhone);
            String key = commonMapper.selectTheDayKey().getSecretKey();
            String secToken = Des3SSL.encodeDC(mobilePhone, key);
            log.info("生成secToken:" + secToken);
            resultObj.put("secToken", secToken);
            resultObj.put(Constant.MSG, Constant.SUCCESS);
        } else {
            resultObj.put(Constant.MSG, Constant.SMS_CODE_NOt_MATCHED);
        }
        return resultObj;
    }

    /**
     * 活动校验
     *
     * @param actId
     * @return
     * @throws Exception
     */
    @ApiOperation("活动校验(时间||白名单||WAP20用户)")
    @PostMapping(value = "/verityActive")
    public @ResponseBody
    Object getActiveStatus(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "actId", value = "活动标识", required = true) String actId, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId, @ApiParam(name = "isTestWhite", value = "是否加白名单验证", required = true) boolean isTestWhite) throws Exception {
        return this.commonService.verityActive(secToken,actId, isTestWhite, channelId);
    }


    @ApiOperation(value = "二次短信下发", httpMethod = "POST")
    @PostMapping(value = "/sendSms5956")
    public @ResponseBody
    Object sendSms5956(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) int unlocked) throws IOException {
        Object object=  commonObject(secToken,channelId,actId).getString(Constant.MSG);
        if(object.equals(Constant.SUCCESS)){
            return  commonService.sendSms5956(commonObject(secToken,channelId,actId).getString(Constant.KEY_MOBILE),actId,unlocked);
        }
        return  object;
    }

    /**
     * 提取公共内容
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    public  JSONObject commonObject(String secToken, String channelId, String actId){
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
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put(Constant.KEY_MOBILE, mobile);
            }
        }
        return object;
    }


    static void getActId(HttpServletRequest request, List<ActivityConfiguration> configuration, HttpServletResponse resp, @ApiParam(name = "secToken", value = "用户标识", required = true) String secToken) throws IOException {
        String actId;
        String channelId;
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        List<ActivityConfiguration> config = configuration;
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(JSON.toJSONString(config));
    }

    static void getParameter(HttpServletRequest request,String actId,String channelId,int unlocked){
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        unlocked = request.getParameter("unlocked") == null ? 0 : Integer.parseInt(request.getParameter("unlocked"));
    }

}
