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
 * @create 2023/3/23 16:24
 */
@Data
@NoArgsConstructor
@ApiModel(value = "岗位")
public class VoteTopicRank {
    @ApiModelProperty("岗位ID")
    private int rid;
    @ApiModelProperty("题ID")
    private int tid;
}
