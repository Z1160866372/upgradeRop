/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *//*

package com.richeninfo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

*/
/**
 * @Author : zhouxiaohu
 * @create 2022/9/22 14:18
 *//*

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    */
/**
     * 接口文档地址和测试调用试例
     * swagger2原生UI：http://localhost:8080/swagger-ui.html
     * swagger2优化版UI：http://localhost:8080/doc.html
     * 【IP和端口按照实际情况修改】
     *//*

    @Bean
    public Docket createRestApi() {
        //添加head参数start
      */
/*  List<Parameter> params = new ArrayList();
        ParameterBuilder tokenParam = new ParameterBuilder();
        tokenParam.name("x-auth-token").description("用户令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        params.add(tokenParam.build());*//*

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径,控制器类包
                .apis(RequestHandlerSelectors.basePackage("com.richeninfo"))
                .paths(PathSelectors.any())
                .build()
                .globalResponseMessage(RequestMethod.GET,
                        newArrayList(new ResponseMessageBuilder()
                                        .code(500)
                                        .message("系统繁忙！")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("请求地址不正确!")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("权限不足")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("请求成功!")
                                        .build()))
                .globalResponseMessage(RequestMethod.POST,
                        newArrayList(new ResponseMessageBuilder()
                                        .code(500)
                                        .message("系统繁忙！")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("请求地址不正确!")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("权限不足")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("请求成功!")
                                        .build()));
    }

    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("互动营销活动接口文档")
                //创建人
                .contact(new Contact("xiaohu.zhou", "https://rop.richeninfo.com/", "xiaohu.zhou@richeninfo.com"))
                //版本号
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }
}
*/
