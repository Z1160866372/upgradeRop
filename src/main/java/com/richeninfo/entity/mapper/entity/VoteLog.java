/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.sql.DataSourceDefinitions;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/23 10:10
 */
@Data
public class VoteLog {
    @ApiModelProperty("日志ID")
    private int lid;
    @ApiModelProperty("答题码ID")
    private int yid;
    @ApiModelProperty("所答题ID")
    private int tid;
    @ApiModelProperty("评比部门ID")
    private int did;
    @ApiModelProperty("所属分类内容")
    private int iid;
    @ApiModelProperty("所打分值")
    private int score;
    @ApiModelProperty("备注")
    private String content;
    @ApiModelProperty("创建时间")
    private String createdDate;
}
