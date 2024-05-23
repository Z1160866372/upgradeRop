/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.entity.mapper.mapper.master.FileListMapper;
import com.richeninfo.entity.mapper.mapper.master.FinanceMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.FileListService;
import com.richeninfo.service.FinanceService;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RSAUtils;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/30 14:04
 */
@Service
@Slf4j
public class FileListServiceImpl implements FileListService {

    @Resource
    private CommonMapper commonMapper;
    @Resource
    private FileListMapper fileListMapper;
    @Resource
    private PacketHelper packetHelper;
    @Resource
    private RopServiceManager ropService;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Resource
    private CommonService commonService;
    @Resource
    private RSAUtils rsaUtils;
    @Resource
    private HttpServletRequest request;
    SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public JSONObject insertActivityFileList(ActivityFileList activityFileList) {
        JSONObject object = new JSONObject();
        int insertFile = fileListMapper.insertActivityFileList(activityFileList);
        if(insertFile>0){
            object.put(Constant.MSG,Constant.SUCCESS);
        }else{
            object.put(Constant.MSG,Constant.FAILURE);
        }
        return object;
    }

    @Override
    public List<ActivityFileList> getActivityFileList(String principal) {
        List<ActivityFileList> activityFileLists=null;
        if(principal.equals("admin")){
            principal="";
        };
        activityFileLists=fileListMapper.selectActivityFileList(principal);
        return activityFileLists;
    }
}
