package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/17 10:48
 */
@Data
@NoArgsConstructor
@ApiModel(value = "岗位")
public class VoteRank {
    @ApiModelProperty("岗位ID")
    private int rid;
    @ApiModelProperty("岗位名称")
    private String name;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("权重")
    private double weight;
    @ApiModelProperty("创建时间")
    private String createdDate;
}
