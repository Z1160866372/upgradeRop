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
 * 活动验证名单
 * @create 2022/9/21 13:39
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动验证名单")
public class ActivityRoll {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "用户号码")
    private String userId;

    @ApiModelProperty(value = "类型，0代表wap20用户，1代表测试白名单")
    private int user_type;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
