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
 * @create 2023/3/23 09:53
 */
@Data
@NoArgsConstructor
@ApiModel(value = "类内容对应人员和人员码")
public class VoteClassifyInfoPersonnelCode {
    @ApiModelProperty("类内容ID")
    private int iid;
    @ApiModelProperty("人员ID")
    private int pid;
    @ApiModelProperty("登录码ID")
    private int yid;
}
