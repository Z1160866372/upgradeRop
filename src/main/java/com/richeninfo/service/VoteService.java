package com.richeninfo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.VoteCode;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

/**
* @Author : zhouxiaohu
* @create 2023/3/27 14:17
*/
@Service("voteService")
public interface VoteService {

    /**
     * 获取分类列表
     * @return
     */
    JSONObject getClassifyInfo();

    /**
     * 获取用户类内容下登录码
     * @param secToken
     * @param iid
     * @return
     */
    JSONObject getCode(String secToken,String iid,String channelId);

    /**
     * 根据用户角色获取答题内容
     * @param secToken
     * @return
     */
    JSONObject getTopicList(String secToken,String channelId,String yid,String iid);

    /**
     * 保存用户答题记录
     * @param answerLog
     * @return
     */
    JSONObject saveAnswerLog(String answerLog);

    /**
     * 批量生成Code
     * @param
     * @return
     */
    JSONObject saveVoteCode();

    /**
     * 得分统计
     * @return
     */
    JSONObject scoreStatistics(String rid);


}
