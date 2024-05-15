/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlodService;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @auth sunxiaolei
 * @date 2024/5/7 14:32
 */

@Controller
@RequestMapping("/plod")
public class PlodController {

    @Resource
    private PlodService plodService;
    @Resource
    private CommonService commonService;

    private static String basePath = "https://activity.sh.10086.cn";

    private static String filePath = "/home/weihu";


    /**
     * 初始化用户数据
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/initialize")
    public @ResponseBody
    JSONObject initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                JSONObject object1 = plodService.initializeUser(mobile, secToken, channelId, actId);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 初始化用户数据
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addPicture")
    public @ResponseBody
    JSONObject addPicture(@RequestParam("fileType") String fileType,
                          @RequestBody  @RequestParam("file") MultipartFile file) throws IOException {
        JSONObject object = new JSONObject();
        JSONObject object1 = plodService.addPicture(fileType, file);
        object.put(Constant.MSG, Constant.SUCCESS);
        object.put("data", object1);
        return object;
    }

    /**
     * 获取图片输出流
     *
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/getImg")
    public @ResponseBody void getImg(@RequestParam("imgId") String imgId, @RequestParam("type") String type, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        String path = "imgs";
        if (type.equals("videos")) {
            path = "videos";
            response.setContentType("video/mpeg4");
        }
        System.out.println(basePath + File.separator + filePath + File.separator + imgId);
        File file = new File(basePath + File.separator + filePath + File.separator + imgId);
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = new FileInputStream(file);
        byte[] bs = new byte[1024];
        int length = inputStream.read(bs);
        while (-1 != length) {
            outputStream.write(bs, 0, length);
            length = inputStream.read(bs);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * 保存advise
     *
     * @param secToken  用户sectoken
     * @param channelId 渠道
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/saveAdvise")
    public @ResponseBody
    JSONObject saveAdvise(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, String channelId, String title, String msgText, String path, String videoPath, String raceType, String raceContent) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                object.put(Constant.MSG, "success");
                JSONObject object1 = plodService.saveAdvise(mobile, title, msgText, path, videoPath, raceType, raceContent);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 保存advise
     *
     * @param secToken  用户sectoken
     * @param channelId 渠道
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/myRecord")
    public @ResponseBody
    JSONObject myRecord(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, String channelId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                object.put(Constant.MSG, "success");
                JSONObject object1 = plodService.myRecord(mobile);
                object.put("data", object1);
            }
        }
        return object;
    }
    /**
     * 用户登录
     *
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/checkLogin")
    public @ResponseBody
    JSONObject checkLogin(String userName,String password) throws IOException {
        JSONObject object = new JSONObject();
        object.put(Constant.MSG, "success");
        JSONObject object1 = plodService.checkLogin(userName,password);
        object.put("data", object1);
        return object;
    }

    /**
     * 所有建议
     *
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/AllUserAdvise")
    public @ResponseBody
    JSONObject AllUserAdvise() throws IOException {
        JSONObject object = new JSONObject();
        object.put(Constant.MSG, "success");
        JSONObject object1 = plodService.AllUserAdvise();
        object.put("data", object1);
        return object;
    }


    /**
     * 所有建议
     *
     * @param secToken  用户sectoken
     * @param channelId 渠道
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/playAdvise")
    public @ResponseBody
    JSONObject playAdvise(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, String channelId,String id, String endRaceType, String endRaceContent,String sort, String message) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                object.put(Constant.MSG, "success");
                JSONObject object1 = plodService.playAdvise(id, mobile, endRaceType,  endRaceContent, sort,  message);
                object.put("data", object1);
            }
        }
        return object;
    }

}
