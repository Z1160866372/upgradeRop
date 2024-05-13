/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:54
 */
public interface PlodService {


    /**
     * 初始化用户信息
     * @param mobile
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    JSONObject initializeUser(String mobile, String secToken, String channelId, String actId);

    JSONObject addPicture(@RequestParam("fileType") String fileType,
                          @RequestBody MultipartFile file);

    /**
     * 保存员工提出
     * @param userId
     * @param title
     * @param msgText
     * @param path
     * @param videoPath
     * @param raceType
     * @param raceContent
     * @return
     * @throws IOException
     */
    JSONObject saveAdvise(String userId, String title, String msgText, String path, String videoPath, String raceType, String raceContent) throws IOException;

    /**
     * 查询用户advise
     * @param mobile
     * @return
     */
    JSONObject myRecord(String mobile);

    /**
     * 管理用户登录
     * @return
     */
    JSONObject checkLogin(String userName,String password);

    /**
     * 所有建议
     * @return
     */
    JSONObject AllUserAdvise();

    /**
     * 审批
     * @param id
     * @param loginUserId
     * @param endRaceType
     * @param endRaceContent
     * @param sort
     * @param message
     * @return
     */
    JSONObject playAdvise(String id, String loginUserId, String endRaceType, String endRaceContent,String sort, String message);
}
