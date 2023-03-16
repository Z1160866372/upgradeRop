package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/22 15:56
 */
@Data
@NoArgsConstructor
@ApiModel(value = "用户活动信息")
public class ActivityUser {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "用户号码")
    private String userId;

    @ApiModelProperty(value = "异网标识")
    private String belongFlag;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "加密手机号")
    private String secToken;

    @ApiModelProperty(value = "名单标识")
    private int userType;

    @ApiModelProperty(value = "标识")
    private int unlocked;

    @ApiModelProperty(value = "领取状态")
    private int claimStatus;

    @ApiModelProperty(value = "备用参数")
    private String note;

    @ApiModelProperty(value = "渠道")
    private String channelId;

    @ApiModelProperty(value = "触点")
    private String ditch;

    @ApiModelProperty(value = "参与日期")
    private Date createDate;

    @ApiModelProperty(value = "参与时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "活动编号")
    private String  actId;
}
