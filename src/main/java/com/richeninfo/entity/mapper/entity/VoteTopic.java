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
 * @create 2023/3/17 10:00
 */
@Data
@NoArgsConstructor
@ApiModel(value = "题库")
public class VoteTopic {
    @ApiModelProperty("题ID")
    private int tid;
    @ApiModelProperty("题类型ID")
    private int iid;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("选项内容")
    private String options;
    @ApiModelProperty("分值")
    private int score;
    @ApiModelProperty("打取分值")
    private int fetchScore;
    @ApiModelProperty("备注")
    private String content;
    @ApiModelProperty("当前题库状态")
    private int status;
    @ApiModelProperty("创建时间")
    private String createdDate;

}
