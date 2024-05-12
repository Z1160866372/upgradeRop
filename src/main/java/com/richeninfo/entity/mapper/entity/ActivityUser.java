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
 * @create 2022/9/22 15:56
 */
@Data
@NoArgsConstructor
@ApiModel(value = "用户活动信息")
public class ActivityUser {


    @ApiModelProperty(value = "ID")
    private int id;

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

    @ApiModelProperty(value = "标识")
    private int unlocked;

    @ApiModelProperty(value = "领取状态")
    private int claimStatus;

    @ApiModelProperty(value = "备用参数")
    private String note;

    @ApiModelProperty(value = "渠道")
    private String channelId;

    @ApiModelProperty(value = "触点")
    private String ditch;

    @ApiModelProperty(value = "宝箱机会")
    private int playNum;

    @ApiModelProperty(value = "用户段位")
    private int grade;

    @ApiModelProperty(value = "答题机会")
    private int answerNum;

    @ApiModelProperty(value = "用户分数")
    private int mark;

    @ApiModelProperty(value = "是否获取第一次奖励")
    private int award;

    @ApiModelProperty(value = "吹泡泡游戏机会")
    private int blowNum;

    @ApiModelProperty(value = "选择的题目")
    private String 	answerTitle;

    @ApiModelProperty(value = "选择的答案")
    private String 	answer;

    @ApiModelProperty(value = "首次进入页面 0|1   首次进入|已进过")
    private int level;

    @ApiModelProperty(value = "每周更新时间")
    private Timestamp weekTime;

    @ApiModelProperty(value = "活动编号")
    private String actId;

    @ApiModelProperty(value = "参与日期")
    private String  createDate;

    @ApiModelProperty(value = "参与时间")
    private Timestamp createTime;
}
