package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/23 10:18
 */
@Data
@NoArgsConstructor
@ApiModel(value = "题对应部门")
public class VoteDepartmentTopic {
    @ApiModelProperty("题ID")
    private int tid;
    @ApiModelProperty("部门")
    private int did;
}
