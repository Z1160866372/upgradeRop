/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/21 16:18
 */
@Data
@NoArgsConstructor
@ApiModel(value = "卡券奖励列表")
public class ActivityCardList {
    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "券码")
    private String couponCode;
    @ApiModelProperty(value = "密码")
    private String cardPwd;
    @ApiModelProperty(value = "用户号码")
    private String userId;
    @ApiModelProperty(value = "奖励标识")
    private int unlocked;
    @ApiModelProperty(value = "奖励类型")
    private int typeId;
    @ApiModelProperty(value = "奖励状态")
    private int status;
    @ApiModelProperty(value = "奖励配置时间")
    private String createDate;
    @ApiModelProperty(value = "具体领取日期")
    private String updateTime;
    @ApiModelProperty(value = "IP地址")
    private String ip;
    @ApiModelProperty(value = "IP段")
    private String ipScanner;
    @ApiModelProperty(value = "活动编号")
    private String actId;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
