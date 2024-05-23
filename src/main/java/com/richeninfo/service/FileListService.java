/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityFileList;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/29 14:54
 */
public interface FileListService {

    /**
     * 保存文件内容
     * @return
     */
    JSONObject insertActivityFileList(@ModelAttribute ActivityFileList activityFileList);

    /**
     * 获取上传列表
     * @param principal
     * @return
     */
    List<ActivityFileList> getActivityFileList(String principal);
}
