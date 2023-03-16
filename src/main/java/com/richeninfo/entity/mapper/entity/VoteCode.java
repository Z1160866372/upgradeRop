package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/20 11:04
 */
@Data
@NoArgsConstructor
@ApiModel(value = "登录码")
public class VoteCode {
    @ApiModelProperty("ID")
    private int yid;
    @ApiModelProperty("所属部门ID")
    private int did;
    @ApiModelProperty("登录码")
    private String loginCode;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("创建时间")
    private String createDate;
}
