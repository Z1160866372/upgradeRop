package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/17 09:51
 */
@Data
@NoArgsConstructor
@ApiModel(value = "分类内容")
public class VoteClassifyInfo {
    @ApiModelProperty("内容ID")
    private int iid;
    @ApiModelProperty("类ID")
    private int cid;
    @ApiModelProperty("内容名称")
    private String name;
    @ApiModelProperty("内容背景图")
    private String bgUrl;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("开始时间")
    private String startTime;
    @ApiModelProperty("结束时间")
    private String endTime;

}
