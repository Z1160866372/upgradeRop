package com.richeninfo.entity.mapper.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/28 09:39
 */
@Data
@NoArgsConstructor
@ApiModel(value = "题目类型")
public class VoteTopicType {
    @ApiModelProperty("ID")
    private int eid;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("创建时间")
    private String createdDate;
}
