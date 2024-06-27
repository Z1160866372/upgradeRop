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
import com.richeninfo.service.SimValidateService;
import com.richeninfo.service.UniapiTokenValidateService;
import com.richeninfo.util.RSAUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Api(value = "智能连接", tags = {"智能连接"})
@Controller
@RequestMapping(value = "/china/mobile/")
public class UniapiTokenValidateController {

    @Autowired
    private UniapiTokenValidateService uniapiTokenValidateService;

    @Autowired
    private SimValidateService simValidateService;

    /**
     * 智能连接登录
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping(value = "/uniapiTokenValidate")
    public @ResponseBody Object   UniapiTokenValidate(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        String token = request.getParameter("token") == null ? "" : request.getParameter("token");
        String userInformation = request.getParameter("userInformation") == null ? "" : request.getParameter("userInformation");
        return this.uniapiTokenValidateService.UniapiTokenValidateMethod(Constant.APP_ID,Constant.APP_KEY,"1", token ,userInformation, session);
    }


    @RequestMapping(value = "/sim/login")
    public @ResponseBody Object   simLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        String phone = request.getParameter("phone") == null ? "" : request.getParameter("phone");
        return this.simValidateService.getAuthMsg(phone);
    }


    @RequestMapping(value = "/encryptedRsa")
    public @ResponseBody Object  encryptedRsa(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        JSONObject jsonObject = new JSONObject();
        String sign = request.getParameter("sign") == null ? "" : request.getParameter("sign");
        try {
            String encryptedVal = RSAUtils.sign(sign.getBytes(), Constant.PRIVATE_KEY);
            jsonObject.put(Constant.MSG, Constant.SUCCESS);
            jsonObject.put(Constant.DATA, encryptedVal);
        } catch (Exception e) {
            jsonObject.put(Constant.MSG, Constant.ERROR);
        }
        return jsonObject;
    }


    /**
     * 智能连接退出
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping(value = "/login/out")
    public void  logout(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        session.invalidate();
    }




}
