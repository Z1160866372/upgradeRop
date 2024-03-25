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

/**
 * @Author : zhouxiaohu
 * @create 2022/11/16 14:16
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动名单_黑｜白")
public class ActivityRoster {
    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "用户号码")
    private String userId;
    @ApiModelProperty(value = "名单标识")
    private int user_type;
    @ApiModelProperty(value = "活动编号")
    private String actId;
}
