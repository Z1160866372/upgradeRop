package com.richeninfo.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/20 15:00
 */
@Data
@NoArgsConstructor
@ApiModel(value = "金融联调接口参数")
public class ComEntry {
    @ApiModelProperty("渠道")
    private String channel;
    @ApiModelProperty("时间戳")
    private String timestamp;
    @ApiModelProperty("签名")
    private String sign;
    @ApiModelProperty("uuid")
    private String uuid;
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("验证码")
    private String authcode;
    @ApiModelProperty("流水号")
    private String applyserial;
    @ApiModelProperty("调用地址")
    private String urlCoding;
}
