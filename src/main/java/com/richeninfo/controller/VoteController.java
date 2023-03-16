package com.richeninfo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.service.VoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/27 13:55
 */
@Controller
@Api(value = "集运中心投票接口文档", tags = {"集运中心投票接口文档"})
@RequestMapping("/vote")
@Slf4j
public class VoteController {

    @Resource
    private VoteService voteService;

    @PostMapping("/getClassifyInfo")
    @ResponseBody
    @ApiOperation("获取分类内容")
    public JSONObject getClassifyInfo(){
        return this.voteService.getClassifyInfo();
    }
    @PostMapping("/getCode")
    @ResponseBody
    @ApiOperation("获取该类内容下用户分配的登录码")
    public JSONObject getCode(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId,@ApiParam(name = "iid", value = "分类内容ID", required = true) String iid){
        return this.voteService.getCode(secToken,channelId,iid);
    }
    @PostMapping("/getTopicList")
    @ResponseBody
    @ApiOperation("获取用户身份对应的题库")
    public JSONObject getTopicList(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId,@ApiParam(name = "yid", value = "编码ID", required = true) String yid,@ApiParam(name = "iid", value = "分类内容ID", required = true) String iid){
        return this.voteService.getTopicList(secToken,channelId,yid,iid);
    }
    @PostMapping("/saveAnswerLog")
    @ResponseBody
    @ApiOperation("保存用户答题记录")
    public JSONObject saveAnswerLog(@ApiParam(name = "answerLog", value = "答题记录JSON", required = true) String answerLog){
        return this.voteService.saveAnswerLog(answerLog);
    }
    @PostMapping("/saveVoteCode")
    @ResponseBody
    @ApiOperation("批量生成用户登录码")
    public JSONObject saveVoteCode(){
        return this.voteService.saveVoteCode();
    }

    @PostMapping("/scoreStatistics")
    @ResponseBody
    @ApiOperation("根据岗位内容查询评分结果")
    public JSONObject scoreStatistics(@ApiParam(name = "rid", value = "岗位编号", required = true) String rid){
        return this.voteService.scoreStatistics(rid);
    }
}
