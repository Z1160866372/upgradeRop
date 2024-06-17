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
import com.richeninfo.util.FileUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RSAUtils;
import com.richeninfo.util.RopServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static String filePath = "/data/actCSV/csv/";
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


    @Override
    @Transactional
    public Integer importCSVData(String fileName, String actId,Integer userType) throws IOException {
        String tableName="wt_"+actId+"_roster";
        String csvFile = filePath+fileName;
        String line = "";
        String csvSplitBy = ","; // CSV分隔符，根据实际情况修改
        int resultNum = 0;
        boolean firstLineSkipped = false;
        List<String> list =new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                // 使用逗号作为分隔符
                String[] data = line.split(csvSplitBy);
                for (String dataElement : data) {
                    if(dataElement.contains("“")){
                        list.add(dataElement.substring(1, dataElement.length() - 1));
                    }else{
                        list.add(dataElement);
                    }
                }
            }
            // 读取后根据表名插入表数据
            resultNum = insert(tableName, list,userType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultNum;
    }


    @Transactional
    public Integer insert(String tableName, List<String> list,Integer userType) {
        Integer number =0;
        try{
            // 每次插入的数量
            int batchSize = 200000;
            // 计算需要分多少批插入数据库
            int batch = list.size() / batchSize;
            // 计算最后一批的大小
            int lastSize = list.size() % batchSize;
            long start = System.currentTimeMillis();
            // 将筛选出的结果分批次添加到表中
            for (int i = batchSize; i <= batch * batchSize; i = i + batchSize) {
                // 截取本次要添加的数据
                List<String> insertList = list.subList(i - batchSize, i);
                // 添加本批次数据到数据库中
                number+=fileListMapper.batchDataList(tableName, insertList,userType);
            }
            long end = System.currentTimeMillis();
            System.out.println("耗时："+( end - start ) + "ms");
            // 最后一批元素的大小是否为0
            if (lastSize != 0) {
                // 如果元素有剩余则将所有元素作为一个子列表一次性插入
                List<String> lastList = list.subList(batchSize * batch, list.size());
                // 添加集合到数据库中
                number+=fileListMapper.batchDataList(tableName, lastList,userType);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return number;
    }
}
