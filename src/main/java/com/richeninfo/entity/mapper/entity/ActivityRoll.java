package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * 活动验证名单
 * @create 2022/9/21 13:39
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活动验证名单")
public class ActivityRoll {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "用户号码")
    private String userId;

    @ApiModelProperty(value = "类型，0代表wap20用户，1代表测试白名单")
    private int user_type;
}
