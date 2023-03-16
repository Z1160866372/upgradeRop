package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/19 10:18
 */
@Data
@NoArgsConstructor
@ApiModel(value = "能开平台接口调用记录")
public class OpenapiLog {

    @ApiModelProperty(value = "ID")
    private long id;

    @ApiModelProperty(value = "接口调用地址")
    private String address;

    @ApiModelProperty(value = "用户号码")
    private String userId;

    @ApiModelProperty(value = "接口入参")
    private String code;

    @ApiModelProperty(value = "接口出参")
    private String message;

    @ApiModelProperty(value = "调用时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "能开ApiCode")
    private String apiCode;

    @ApiModelProperty(value = "能开AppCode")
    private String appCode;

    @ApiModelProperty(value = "活动编号")
    private String actId;
}
