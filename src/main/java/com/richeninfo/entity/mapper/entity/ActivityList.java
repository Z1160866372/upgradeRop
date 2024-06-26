/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/21 13:50
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动列表")
public class ActivityList {

    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "活动名称")
    private String name;
    @ApiModelProperty(value = "活动编号")
    private String actId;
    @ApiModelProperty(value = "是否需要白名单")
    private int isWhiteList;
    @ApiModelProperty(value = "当前参与人数")
    private int partNum;
    @ApiModelProperty(value = "活动地址")
    private String address;
    @ApiModelProperty(value = "活动标识")
    private int unlocked;
    @ApiModelProperty(value = "活动类型")
    private int typeId;
    @ApiModelProperty(value = "活动状态0未开始1进行中2已结束")
    private int status;
    @ApiModelProperty(value = "判断当前活动是按日周月年")
    private int code;
    @ApiModelProperty(value = "上线时间")
    private String startTime;
    @ApiModelProperty(value = "下线时间")
    private String endTime;
    @ApiModelProperty(value = "密钥文件地址")
    private String keyUrl;
    @ApiModelProperty(value = "活动负责人")
    private String principal;
    @ApiModelProperty(value = "活动备注（活动修改）")
    private String content;
    @ApiModelProperty(value = "活动关键字")
    private String keyword;
    @ApiModelProperty(value = "活动规则")
    private String rulesText;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
