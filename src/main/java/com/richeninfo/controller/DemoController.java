package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.interceptor.AccessLimit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author : zhouxiaohu
 * @create 2022/12/6 11:28
 */
@Controller
@Api(value = "测试缓存接口", tags = {"测试缓存接口"})
@RequestMapping("/demo")
@Slf4j
public class DemoController {
    /**
     * 获取项目端口
     */
    @Value("${server.port}")
    private String port;

    @Value("${nengKai.ip}")
    private String ip;

    @Value("${nengKai.appCode}")
    private String appCode;
    @Resource
    private HttpSession session;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;

    /**
     * 将信息存放在session中
     */
    @AccessLimit(seconds = 60, maxCount = 1) //60秒内 允许请求1次
    @GetMapping("/set")
    public @ResponseBody Object set(HttpServletRequest request, HttpServletResponse resp) {
        JSONObject resultObj = new JSONObject();
        session.setAttribute("user", "hello world~~~");
//        Cookie cookie = new Cookie("user", "helloWorld~~~");
//        resp.addCookie(cookie);
        resultObj.put("port：",port);
        return resultObj;
    }

    /**
     * 从session中获取信息
     */
    @GetMapping("/get")
    public @ResponseBody Object get(HttpServletRequest request) {
        JSONObject resultObj = new JSONObject();
        log.info(session.getAttribute("user")+"");
        resultObj.put("session:",session.getAttribute("user"));
//        Cookie[] cookies = request.getCookies();
//        resultObj.put("session:",request.getCookies());
        return resultObj;
    }

    @GetMapping("/method")
    public String method() {
        StringBuffer sb = new StringBuffer();
        sb.append("获取到的系统参数 port 的值为：" + port).append("\r\n")
                .append("获取到的自定义参数 name 的值为：" + ip).append("\r\n")
                .append("获取到的自定义参数 website 的值为：" + appCode);
        System.out.println(sb.toString());
        return sb.toString();
    }
}
