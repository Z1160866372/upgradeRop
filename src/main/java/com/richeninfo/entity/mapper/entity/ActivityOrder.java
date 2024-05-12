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
@ApiModel(value = "上报订单")
public class ActivityOrder {

    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "活动名称")
    private String name;
    @ApiModelProperty(value = "活动编号")
    private String actId;
    @ApiModelProperty(value = "手机号")
    private String  userId;
    @ApiModelProperty(value = "原始订单编号")
    private String thirdTradeId;
    @ApiModelProperty(value = "订单项ID")
    private String orderItemId;
    @ApiModelProperty(value = "商品名称")
    private String commodityName;
    @ApiModelProperty(value = "业务ID")
    private String bossId;
    @ApiModelProperty(value = "接口请求")
    private String code;
    @ApiModelProperty(value = "接口响应")
    private String message;
    @ApiModelProperty(value = "渠道")
    private String channelId;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
