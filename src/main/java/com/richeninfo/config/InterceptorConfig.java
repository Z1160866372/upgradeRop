package com.richeninfo.config;
import com.richeninfo.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @Author : zhouxiaohu
 * @create 2022/12/14 16:03
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Resource
    LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //排除swagger拦截
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/doc.html/**","/static/**");
    }
}

