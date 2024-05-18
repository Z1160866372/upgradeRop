/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2024/5/18 09:20
 */
@Data
@NoArgsConstructor
@ApiModel(value = "白名单列表")
public class ActivityFileList {
    @ApiModelProperty(value = "ID")
    private int id;
    @ApiModelProperty(value = "白名单名称")
    private String name;
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @ApiModelProperty(value = "数量")
    private int number;
    @ApiModelProperty(value = "活动负责人")
    private String principal;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
