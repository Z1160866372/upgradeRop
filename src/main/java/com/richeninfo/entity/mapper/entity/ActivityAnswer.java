package com.richeninfo.entity.mapper.entity;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class ActivityAnswer {
    private int id;
    @ApiModelProperty(value = "题目")
    private String answerName;
    @ApiModelProperty(value = "数量")
    private String answerNum;
    @ApiModelProperty(value = "选项")
    private String answerOptain;
    @ApiModelProperty(value = "题目")
    private String answerKey;
    @ApiModelProperty(value = "类型")
    private int answerType;
    @ApiModelProperty(value = "日期")
    private Date createDate;
}
