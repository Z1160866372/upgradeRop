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

/**
 * @Author : zhouxiaohu
 * @create 2022/11/15 15:12
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动分享用户")
public class ActivityShare {

    @ApiModelProperty(value = "主键ID")
    private int id;

    @ApiModelProperty(value = "分享用户")
    private String userId;

    @ApiModelProperty(value = "加密手机号")
    private String secToken;

    @ApiModelProperty(value = "分享渠道")
    private String channelId;

    @ApiModelProperty(value = "分享时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "活动编号")
    private int actId;
}
