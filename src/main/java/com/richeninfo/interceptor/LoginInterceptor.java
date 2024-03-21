package com.richeninfo.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.util.RedisUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * @Author : zhouxiaohu
 * @create 2022/12/14 15:22
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private final Log log = LogFactory.getLog(LoginInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //获取方法中的注解,看是否有该注解,用于防止接口被恶意调用多次
        AccessLimit accessLimit = ((HandlerMethod) handler).getMethodAnnotation(AccessLimit.class);
        // log.info("========================accessLimit==============================>" + accessLimit);
        if (accessLimit == null) {
            return true;
        }
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        String ip = request.getRemoteAddr();
        String key = request.getServletPath() + ":" + ip;
        String count = (String)redisUtil.get(key);
        if (null == count) {
            redisUtil.set(key, "1", seconds);
            String count1 = (String)redisUtil.get(key);
            return true;
        }
        if (Integer.parseInt(count) < maxCount) {
            count = String.valueOf(Integer.parseInt(count) + 1);
            redisUtil.set(key, count, seconds);
            return true;
        }
        if (Integer.parseInt(count) >= maxCount) {
            //response 返回 json 请求过于频繁请稍后再试
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(401);
            JSONObject object = new JSONObject();
            object.put("msg", "操作频繁，请稍后重试");
            response.getWriter().write(object.toString());
            return false;
        }
        return false;
    }
}
