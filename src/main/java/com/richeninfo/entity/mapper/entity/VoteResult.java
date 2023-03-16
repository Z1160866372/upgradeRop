package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/23 11:01
 */
@Data
@NoArgsConstructor
@ApiModel(value = "投票结果")
public class VoteResult {
    @ApiModelProperty("结果ID")
    private int lid;
    @ApiModelProperty("答题码ID")
    private int yid;
    @ApiModelProperty("评比部门ID")
    private int did;
    @ApiModelProperty("类内容ID")
    private int iid;
    @ApiModelProperty("所打分值")
    private int score;
    @ApiModelProperty("创建时间")
    private String createdDate;
}
