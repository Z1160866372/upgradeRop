package com.richeninfo.service;


import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @auth lei
 * @date 2024/3/22 15:43
 */
public interface ExteroceptiveService {


    JSONObject initializeUser(String mobile, String secToken, String channelId, String actId);

    JSONObject toAnswer(String channelId, String userId);

    JSONObject answer(String channelId, String answer, String answerTitle, String userId);

    JSONObject play(String channelId, String paopao, String userId);


    JSONObject tochoujiang(String channelId, String userId);

    JSONObject choujiang(String channelId, String userId,String actId);

    JSONObject todetail(String channelId, String userId);

    void changeStatus(String caozuo, String channelId, int type, String userId);

    String  selectTime(String actId);

    JSONObject myReceived(String channelId, String userId,String actId);


}