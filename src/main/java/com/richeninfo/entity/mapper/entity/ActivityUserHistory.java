/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/22 17:12
 */
@Data
@NoArgsConstructor
@ApiModel(value = "用户活动奖励")
public class ActivityUserHistory {
    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "IP段")
    private String ipScanner;

    @ApiModelProperty(value = "用户号码")
    private String userId;

    @ApiModelProperty(value = "异网标识")
    private String belongFlag;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "加密手机号")
    private String secToken;

    @ApiModelProperty(value = "名单标识")
    private int userType;

    @ApiModelProperty(value = "奖励标识")
    private int unlocked;

    @ApiModelProperty(value = "奖励类型")
    private int typeId;

    @ApiModelProperty(value = "奖励名称")
    private String rewardName;

    @ApiModelProperty(value = "接口响应状态")
    private int status;

    @ApiModelProperty(value = "接口入参")
    private String code;

    @ApiModelProperty(value = "接口出参")
    private String message;

    @ApiModelProperty(value = "渠道")
    private String channelId;

    @ApiModelProperty(value = "触点")
    private String ditch;

    @ApiModelProperty(value = "参与日期")
    private Date createDate;

    @ApiModelProperty(value = "参与时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "活动编号")
    private String actId;

    @ApiModelProperty(value = "奖品说明")
    private String remark;

    @ApiModelProperty(value = "跳转地址")
    private String winSrc;

    @ApiModelProperty(value = "活动关键字")
    private String keyword;

}
