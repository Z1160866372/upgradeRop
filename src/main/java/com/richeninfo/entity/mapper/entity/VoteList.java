package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/28 14:33
 */
@Data
public class VoteList {
    @ApiModelProperty("总分")
    private int totalScore;
    @ApiModelProperty("人数")
    private int number;
    @ApiModelProperty("平均分")
    private double averageScore;
    @ApiModelProperty("被评部门名称")
    private String name;
}
