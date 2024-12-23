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
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.PlodAdvise;
import com.richeninfo.entity.mapper.entity.PlodLoginUser;
import com.richeninfo.entity.mapper.entity.PlodPubUser;
import com.richeninfo.entity.mapper.mapper.master.PlodMapper;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.Result;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlodService;
import com.richeninfo.util.CommonUtil;
import com.richeninfo.util.DateUtil;
import com.richeninfo.util.PacketHelper;
import com.richeninfo.util.RopServiceManager;
import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 16:55
 */
@Log4j
@Service
public class PlodServiceImpl implements PlodService {


    @Resource
    PlodMapper plodMapper;
    @Resource
    CommonService commonService;
    @Resource
    CommonUtil commonUtil;

    private static String basePath = "/home/weihu/";

    private static String filePath = "/home/weihu/";

    @Resource
    private PacketHelper packetHelper;

    @Resource
    private RopServiceManager ropService;

    @Override
    public JSONObject initializeUser(String userId, String secToken, String channelId, String actId,String ditch) {
        JSONObject jsonObject = new JSONObject();
        ActivityUser activityUser = plodMapper.findUserInfo(userId);
        if (activityUser == null) {
            activityUser = new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setPlayNum(2);
            activityUser.setAward(0);
            activityUser.setChannelId(channelId);
            activityUser.setDitch(ditch);
            plodMapper.saveUser(activityUser);
        }
            activityUser.setSecToken(secToken);
            jsonObject.put("user", activityUser);
            return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param fileType
     * @param file
     * @return
     */
    @Override
    public JSONObject addPicture(@RequestParam("fileType") String fileType,
                                 @RequestBody MultipartFile file) {
        log.info("上传文件类型===" + fileType);
        JSONObject result = new JSONObject();
        try {
            String path = "imgs";
            if ("videos".equals(fileType)) {
                path = "videos";
            }
            String fileSuffix = StringUtils.substring(file.getOriginalFilename(),
                    file.getOriginalFilename().lastIndexOf(".") + 1);
            log.info("上传文件类型===" + fileSuffix);
            if (!StringUtils.equalsIgnoreCase(fileSuffix, "jpg") && !StringUtils.equalsIgnoreCase(fileSuffix, "jpeg")
                    && !StringUtils.equalsIgnoreCase(fileSuffix, "bmp") && !StringUtils.equalsIgnoreCase(fileSuffix, "gif")
                    && !StringUtils.equalsIgnoreCase(fileSuffix, "png") && !StringUtils.equalsIgnoreCase(fileSuffix, "mp4")
                    && !StringUtils.equalsIgnoreCase(fileSuffix, "mov")
                    && !StringUtils.equalsIgnoreCase(fileSuffix, "avi")) {
                log.error("上传文件的格式错误=" + fileSuffix);
                result.put("msg", "上传文件的格式错误");
                return result;
            }
            String fileName = file.getOriginalFilename();// 获取文件名加后缀
            if (fileName != null && fileName != "") {
                String fileF = fileName.substring(fileName.lastIndexOf("."), fileName.length());// 文件后缀
                String dateStr = DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmsszzz");
                String fileAdd = dateStr + "_" + 1;
                // 获取文件夹路径
                File file1 = new File(basePath + File.separator + path + File.separator);
                // 如果文件夹不存在则创建
                if (!file1.exists() && !file1.isDirectory()) {
                    file1.mkdir();
                }
                log.info(basePath + File.separator + path + File.separator);
                // 将图片存入文件夹
                fileName = fileAdd + fileF;
                log.info("file1==="+file1);
                log.info("fileName==="+fileName);
                File targetFile = new File(file1, fileName);
                try {
                    // 将上传的文件写到服务器上指定的文件。
                    file.transferTo(targetFile);
                } catch (Exception e) {
                    result.put("msg", "error");
                }
            }
            result.put("fileName", fileName);
            result.put("msg", "success");
            return result;
        } catch (Exception e) {
            result.put("msg", e.getMessage());
            return result;
        }
    }

    @Override
    public JSONObject saveAdvise(String userId, String title, String msgText, String path, String videoPath, String raceType, String raceContent) throws IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            PlodPubUser pub = plodMapper.findPubUserByUserId(userId);
            PlodAdvise advise = new PlodAdvise();
            advise.setCreateTime(new Date());
            advise.setDepartName(pub.getDepartName());
            advise.setMessage("");
            advise.setUserName(pub.getUserName());
            advise.setUserId(userId);
            advise.setMsgText(msgText);
            advise.setUploadFile(1);
            advise.setTitle(title);
            advise.setAdviseScore(record(raceContent));
            advise.setStatus(0);//
            advise.setRaceType(raceType);
            advise.setRaceContent(raceContent);
            advise.setApprover("");
            if (StringUtil.isNullOrEmpty(videoPath)) {
                advise.setVideoPath("");
            } else {
                advise.setVideoPath(videoPath);
            }
            advise.setFileUrl(path);
            plodMapper.savePlodAdvise(advise);
            jsonObject.put("msg", "success");
                String message=pub.getUserName()+"领导，"+DateUtil.convertDateTimeToString(new Date()) +"提交了："+advise.getMsgText()+"建议";
                List<PlodPubUser> listPub=  plodMapper.findPubUserByDepartName("短信发送");
                log.info(listPub.toString());
                for(PlodPubUser pubPhone :listPub){
                    log.info(pubPhone);
                    log.info(message);
                    sendNote(pubPhone.getUserId(),message);
                }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg", "error");
        }
        return jsonObject;
    }

    public void sendNote(String userId,String message) {
        log.info("短信内容："+message);
        try {
            Packet packet = packetHelper.getCommitPacket1638(userId,message);
            Result result = JSON.parseObject(ropService.execute(packet, userId, "gsmshare"), Result.class);
            log.info("短信返回："+result.getResponse().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject myRecord(String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<PlodAdvise> plodAdvises = plodMapper.findUserAdviseList(userId);
            jsonObject.put("plodAdvises", plodAdvises);
            PlodPubUser plodPubUser = plodMapper.findPubUserByUserId(userId);
            jsonObject.put("myScore", plodPubUser.getTotalScore());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public JSONObject checkLogin(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        PlodLoginUser adminUser = plodMapper.findLoginUser(username, password);
        if (adminUser == null) {
            jsonObject.put("msg", "账号密码错误");
        } else {
            jsonObject.put("msg", "success");
            jsonObject.put("loginUser", adminUser);
        }
        return jsonObject;
    }

    @Override
    public JSONObject AllUserAdvise() {
        log.info("AllUserAdvise=====");
        JSONObject jsonObject = new JSONObject();
        List<PlodAdvise> allAdvise = plodMapper.findAllAdvise();
        jsonObject.put("allAdvise", allAdvise);
        return jsonObject;
    }

    @Override
    public JSONObject playAdvise(String id, String loginUserId, String endRaceType, String endRaceContent, String sort, String message) {
        JSONObject jsonObject = new JSONObject();
        PlodLoginUser loginUse = plodMapper.findLoginUserByUserId(loginUserId);// 审核员信息
        PlodAdvise plodAdvise = plodMapper.findAdviseById(Integer.valueOf(id));// 被审核建议
        if (plodAdvise.getStatus() == 0) {
            if (sort.equals("accept")) {
                plodAdvise.setEndRaceType(endRaceType);// 建议类型
                plodAdvise.setEndRaceContent(endRaceContent);// 建议内容
                plodAdvise.setApproverScore(record(endRaceContent));// 最新分数
                plodAdvise.setStatus(1);
                plodAdvise.setApprover(loginUse.getUserName());
                plodMapper.updatePlodAdviseById(plodAdvise);
                PlodPubUser user = plodMapper.findPubUserByUserId(plodAdvise.getUserId());
                int total = user.getTotalScore() + record(endRaceContent);
                plodMapper.updatePubUserScore(plodAdvise.getUserId(), total);// 更新用户成绩
            } else {
                plodAdvise.setApprover(loginUse.getUserName());
                plodAdvise.setMessage(message);
                plodAdvise.setStatus(2);

                plodMapper.updatePlodAdviseById(plodAdvise);
            }
            jsonObject.put("msg", "success");
            jsonObject.put("plodAdvise", plodAdvise);
        } else {
            jsonObject.put("msg", "该建议已经审批，请勿重复操作。");
        }
        return jsonObject;
    }

    public int record(String raceContent) {
        int score = 0;
        if (raceContent.equals("页面规范类")) {
            score = 30;
        } else if (raceContent.equals("文字差错类")) {
            score = 20;
        } else if (raceContent.equals("页面加载类")) {
            score = 10;
        } else if (raceContent.equals("流程改善类")) {
            score = 30;
        }
        return score;

    }

}