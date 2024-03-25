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
 * @create 2023/3/17 09:45
 */
@Data
@NoArgsConstructor
@ApiModel(value = "分类")
public class VoteClassify {
    @ApiModelProperty("类ID")
    private int cid;
    @ApiModelProperty("类名称")
    private String name;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("创建时间")
    private String createdDate;
}
