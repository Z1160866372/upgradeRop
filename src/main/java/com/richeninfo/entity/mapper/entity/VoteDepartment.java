package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/17 10:02
 */
@Data
@NoArgsConstructor
@ApiModel(value = "部门")
public class VoteDepartment {
    @ApiModelProperty("部门ID")
    private int did;
    @ApiModelProperty("部门名称")
    private String name;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("人数")
    private int number;
    @ApiModelProperty("创建时间")
    private String createdDate;

}
