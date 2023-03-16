package com.richeninfo.pojo;

import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/17 16:58
 */
@Data
@NoArgsConstructor
public class PacketMq{
    @ApiModelProperty("编号")
    private int id;
    @ApiModelProperty("获取奖励")
    private ActivityUserHistory history;
    @ApiModelProperty("接口出参")
    private Request request;
    @ApiModelProperty("接口入参")
    private Packet packet;
}
