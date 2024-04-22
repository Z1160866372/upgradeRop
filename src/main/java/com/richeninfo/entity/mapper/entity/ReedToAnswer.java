package com.richeninfo.entity.mapper.entity;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

@Data
public class ReedToAnswer {

		private  int id;
		private String 	answerName;//题目
		private String 	answerNum;// 数量
		private String 	answerOptain;//选项
		private String  answerKey;//答案 
		private int    	answerType;//类型
		private Date   	createDate;//日期
}
