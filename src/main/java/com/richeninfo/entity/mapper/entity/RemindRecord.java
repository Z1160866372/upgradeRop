package com.richeninfo.entity.mapper.entity;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;
@Data
public class RemindRecord {
	private int id;
	private String userId;//手机号
	private String caozuo;//caozuo
	private int typeId;//  
	private int    status;// 0  还没出结果，1已出结果
	private String channel_id;//渠道
	private Timestamp createTime;
	private Date createDate;
	private  long time;
}
