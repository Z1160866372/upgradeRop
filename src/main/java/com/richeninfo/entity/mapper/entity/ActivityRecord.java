/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.entity;

import java.sql.Timestamp;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  sunxiaolei
 * @Data 2024-04-22
 */
@Data
public class ActivityRecord {
	private int id;
	@ApiModelProperty(value = "手机号")
	private String userId;
	@ApiModelProperty(value = "操作步骤")
	private String caozuo;
	@ApiModelProperty(value = "唯一标识")
	private int typeId;
	@ApiModelProperty(value = "状态")
	private int    status;
	@ApiModelProperty(value = "渠道")
	private String channel_id;
	@ApiModelProperty(value = "详细时间")
	private Timestamp createTime;
	@ApiModelProperty(value = "时间")
	private Date createDate;
	@ApiModelProperty(value = "预留")
	private  long time;
}
