package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @Author : zhouxiaohu
 * 用户操作日志
 * @create 2022/9/21 14:41
 */
@Data
@NoArgsConstructor
@ApiModel(value = "用户操作记录")
public class OperationLog {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "活动编号")
    private String actId;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "用户访问IP")
    private String address;

    @ApiModelProperty(value = "操作用户")
    private String userId;

    @ApiModelProperty(value = "加密手机号")
    private String secToken;

    @ApiModelProperty(value = "操作说明")
    private String instructions;

    @ApiModelProperty(value = "操作时间")
    private Timestamp createTime;
}
