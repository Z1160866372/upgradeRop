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
 * @create 2022/9/21 14:17
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动密钥")
public class ActivitySecretKey {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "活动密钥")
    private String secretKey;

    @ApiModelProperty(value = "上线时间")
    private String startTime;

    @ApiModelProperty(value = "下线时间")
    private String endTime;
}
