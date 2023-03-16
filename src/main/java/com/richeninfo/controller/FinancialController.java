package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.OpenapiLog;
import com.richeninfo.entity.mapper.mapper.cluster.SmsMapper;
import com.richeninfo.entity.mapper.mapper.master.FinancialMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.FinancialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/21 10:02
 */
@Controller
@Api(value = "金融新人福利活动接口", tags = {"金融新人福利活动接口"})
@RequestMapping("/financial")
@Slf4j
public class FinancialController {

    @Resource
    private FinancialService financialService;
    @Resource
    private FinancialMapper financialMapper;
    @Resource
    private HttpSession session;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;
/*
    @PostMapping("/sedMsgCode")
    @ResponseBody
    public JSONObject sedMsgCode(ComEntry comEntry){
        JSONObject resultObj = new JSONObject();
        comEntry.setUrlCoding("mobileSend");
        if(session.getAttribute(Constant.SMS_CODE_MAP) != null){
            Map<String, Object> smsCodeMap = (Map<String, Object>) session.getAttribute(Constant.SMS_CODE_MAP);
            if(smsCodeMap != null && smsCodeMap.get("date")!=null){
                long nowDate = new Date().getTime();
                if(nowDate - Long.parseLong(smsCodeMap.get("date").toString()) <= Constant.SEPARATION_MILLISECOND){
                    log.info("当前手机号 {} 发送短信间隔不足 {}毫秒 ,短信验证码{}， 无法重复发送",comEntry.toString());
                    resultObj.put(Constant.MSG,Constant.SEND_MSG_SEPARATION_NOT_ENOUGH);
                    return resultObj;
                }
            }
        }
        return this.financialService.sendMsgCodeAndRegister(comEntry,session);
    }
    @PostMapping("/registerInfo")
    @ResponseBody
    public JSONObject registerInfo(ComEntry comEntry){
        comEntry.setUrlCoding(" register");
        JSONObject resultObj = financialService.sendMsgCodeAndRegister(comEntry,session);
        if(resultObj.getString(Constant.MSG).equals(Constant.SUCCESS)){
            ActivityUser user = new ActivityUser();
            user.setUserId(comEntry.getMobile());
            user.setBelongFlag(JSONObject.parseObject(resultObj.getString(Constant.DATA)).getString("isregister"));
            user = financialService.insertUser(user);
        }
        return resultObj;
    }*/

    @PostMapping("/initializeUser")
    @ResponseBody
    @ApiOperation("初始化用户获取用户基本信息")
    public JSONObject initializeUser(@ApiParam(name = "userId", value = "用户标识", required = true) String userId,@ApiParam(name = "belongFlag", value = "是否注册", required = true) String belongFlag,@ApiParam(name = "actId", value = "活动编号", required = true) String actId){
        JSONObject resultObj = new JSONObject();
        if(userId.isEmpty()){
            resultObj.put(Constant.MSG,"login");
        }else{
            ActivityUser user =new ActivityUser();
            user.setUserId(userId);
            user.setBelongFlag(belongFlag);
            user.setActId(actId);
            resultObj.put(Constant.MSG,Constant.SUCCESS);
            resultObj.put("user", financialService.insertUser(user));
        }
        return resultObj;
    }

    @PostMapping("/instantDraw")
    @ResponseBody
    @ApiOperation("立即抽奖")
    public JSONObject instantDraw(@ApiParam(name = "userId", value = "用户标识", required = true) String userId,@ApiParam(name = "belongFlag", value = "是否注册", required = true) String belongFlag,@ApiParam(name = "actId", value = "活动编号", required = true) String actId){
        ActivityUser user =new ActivityUser();
        user.setUserId(userId);
        user.setBelongFlag(belongFlag);
        user.setActId(actId);
        return this.financialService.instantDraw(user);
    }

    @PostMapping("/recordLog")
    @ResponseBody
    @ApiOperation("记录日志")
    public void recordLog(@ApiParam(name = "userId", value = "用户标识", required = true) String userId,@ApiParam(name = "code", value = "入参信息", required = true) String code,@ApiParam(name = "message", value = "出参信息", required = true) String message,@ApiParam(name = "actId", value = "活动编号", required = true) String actId) {
      try {
          if(userId.isEmpty()){}else{
              OpenapiLog log =new OpenapiLog();
              log.setUserId(userId);
              log.setCode(code);
              log.setMessage(message);
              log.setActId(actId);
              if(code.isEmpty()){}else{
                  this.financialService.recordLog(log);
              }
          }
      }catch (Exception e){
          e.printStackTrace();
      }
    }

    @PostMapping("/sendAwardMsg")
    @ResponseBody
    @ApiOperation("发放奖励短信")
    public JSONObject sendAwardMsg(@ApiParam(name = "activityName", value = "活动名称", required = true) String activityName, @ApiParam(name = "timeStamp", value = "当前时间戳", required = true) String timeStamp){
        return this.financialService.sendAwardMsg(activityName,timeStamp);
    }
}
