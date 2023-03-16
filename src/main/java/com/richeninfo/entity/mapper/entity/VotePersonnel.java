package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/17 10:35
 */
@Data
@NoArgsConstructor
@ApiModel(value = "人员")
public class VotePersonnel {
    @ApiModelProperty("人员ID")
    private int pid;
    @ApiModelProperty("所属部门ID")
    private int did;
    @ApiModelProperty("所属岗位ID")
    private int rid;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("创建时间")
    private String createdDate;

}
