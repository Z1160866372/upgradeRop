package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2022/9/21 14:53
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动奖励配置")
public class ActivityConfiguration {
    @ApiModelProperty(value = "主键ID")
    private int id;

    @ApiModelProperty(value = "活动编号")
    private String  actId;

    @ApiModelProperty(value = "奖励名称")
    private String name;

    @ApiModelProperty(value = "奖励值例如50(MB)、0.5(元)、10(元)、1024(MB)")
    private String value;

    @ApiModelProperty(value = "楼层模块")
    private int module;

    @ApiModelProperty(value = "奖励类型0—默认、1-限量、2-时间限制权益、3-需验证&限量、4、只需验证")
    private int typeId;

    @ApiModelProperty(value = "奖励标识")
    private int unlocked;

    @ApiModelProperty(value = "接口编号")
    private String activityId;

    @ApiModelProperty(value = "接口自定义ID")
    private String itemId;

    @ApiModelProperty(value = "奖励数量")
    private int amount;

    @ApiModelProperty(value = "概率")
    private String prob;

    @ApiModelProperty(value = "跳转地址")
    private String winSrc;

    @ApiModelProperty(value = "图片地址")
    private String imgSrc;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "状态")
    private int status;

    @ApiModelProperty(value = "名单状态")
    private int userType;

    @ApiModelProperty(value = "总量")
    private int AllAmount;

    @ApiModelProperty(value = "按钮状态")
    private String buttonStatus;

    @ApiModelProperty(value = "非PRO会员提示")
    private String noProContent;

    @ApiModelProperty(value = "PRO会员提示")
    private String proContent;

    @ApiModelProperty(value = "显示编号")
    private int showNum;

    @ApiModelProperty(value = "渠道")
    private String channelId;

    @ApiModelProperty(value = "插码")
    private String wtEvent;
}
