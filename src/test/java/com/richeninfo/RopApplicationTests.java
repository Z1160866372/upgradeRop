package com.richeninfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.pojo.Packet;
import com.richeninfo.service.CommonService;
import com.richeninfo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = RopApplication.class)
@Slf4j
class RopApplicationTests {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommonService commonService;
    @Resource
    private CommonUtil commonUtil;
    @Resource
    private CreateCSVFile createCSVFile;
    //声明一个队列
    @Test
    public void createQueue() throws Exception {
       /* ActivityConfiguration config =new ActivityConfiguration();
        config.setActivityId("1234");
        config.setItemId("5678");
        ActivityUserHistory history = new ActivityUserHistory();
        history.setUserId("13817513783");
        String  mqMsg = commonService.issueReward(config,history);
        rabbitTemplate.convertAndSend("ProMemberQueue", mqMsg);*/
        /*int key_num=0;
        Calendar calOne = Calendar.getInstance();
        calOne.setTime(new Date());
        int dayOfYear = calOne.getActualMaximum(Calendar.DAY_OF_YEAR);
        String startTime = commonUtil.formatDateTime(commonUtil.todayStart(commonUtil.getCurrentFirstOfYear()));
        String endTime ="";
        if(dayOfYear%3==0){
            key_num=dayOfYear/3;
        }else{
            key_num=dayOfYear/3+1;
        }
        List<Object> exportData = new ArrayList<Object>();//列表信息
        exportData.add("id");
        exportData.add("secretKey");
        exportData.add("startTime");
        exportData.add("endTime");
        List<List<Object>> datalist = new ArrayList<List<Object>>();
        for (int i = 1; i <= key_num; i++) {
            List<Object> data=new ArrayList<Object>();//列表内容
            data.add(i);
            data.add(commonUtil.getRandomChar(24));
            data.add(startTime+" 00:00:00");
            endTime=commonUtil.formatDateTime(commonUtil.addDayEnd(3,commonUtil.todayStart(commonUtil.parseDateTime(startTime))));
            data.add(endTime+" 23:59:59");
            startTime=commonUtil.formatDateTime(commonUtil.addDayEnd(2,commonUtil.todayStart(commonUtil.parseDateTime(endTime))));
            datalist.add(data);
        }
        String path = "/Users/zhouxiaohu/Desktop/";
        String fileName = calOne.get(Calendar.YEAR)+"年密钥文件";
        File file =createCSVFile.createCSVFile(exportData, datalist, path, fileName);*/
      /*  String text="{'code':'SYSERROR','msg':'签名异常'}";
        String result="B7A59FB808AEC29139967C6F3AC3EA3A611993EC906078EF65F850975D7AB6976255A6C06366A9CFD20D4E176C8E6696";
        String uuid="60b21d2b-392c-4a";
        String  iv ="0102030405060708";
        log.info(AESCBC.encryptAES(text,uuid));
        log.info(AESCBC.decryptAES(result,uuid));
*/
        /*String test = "Tdu5DwfyBSaqoXF0HBcVSA==";
        System.out.println(new Date().getTime());*/
        System.out.println(RSAUtils.decryptByPriKey("Zs80rLKwzBoeK3AeuQP9swPl9zAHIr/hxamKoiKTizfaZPIy2w3cxolNTBlkFOZEv0jTlifGHNlJ0nRLy/ytnQ4bHRGqhgMSpI8c4LIkCSMDVaSdqJwVPP7ymClEHznliiv0I56L40x/KrWHYxOFXV+ZUFAszQtpPUGkpG3WJFI="));

    }
}
