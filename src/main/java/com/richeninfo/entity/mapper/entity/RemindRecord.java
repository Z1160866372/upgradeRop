package com.richeninfo.entity.mapper.entity;

import java.sql.Timestamp;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class RemindRecord {
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
	private Timestamp createTime;
	private Date createDate;
	private  long time;
}
