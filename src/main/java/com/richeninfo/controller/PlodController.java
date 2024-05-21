/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.PlodLoginUser;
import com.richeninfo.entity.mapper.entity.PlodPubUser;
import com.richeninfo.entity.mapper.mapper.master.PlodMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.PlodService;
import com.richeninfo.util.RSAUtils;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private PlodMapper plodMapper;

    @Resource
    private RSAUtils rsaUtils;

    private static String basePath = "/home/weihu/";

    private static String filePath = "/home/weihu/";


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
    JSONObject initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String ditch) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                JSONObject object1 = plodService.initializeUser(mobile, secToken, channelId, actId,ditch);
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
                          @RequestBody @RequestParam("file") MultipartFile file, String secToken) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, "weiting");
            if (StringUtils.isEmpty(mobile)) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                PlodPubUser user = plodMapper.findPubUserByUserId(mobile);
                if ((user != null)) {
                    JSONObject object1 = plodService.addPicture(fileType, file);
                    object.put(Constant.MSG, Constant.SUCCESS);
                    object.put("data", object1);
                } else {
                    object.put(Constant.MSG, "NoPower");
                }
            }
        }
        return object;
    }

    /**
     * 获取图片输出流
     *
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/getImg")
    public @ResponseBody void getImg(@RequestParam("imgId") String imgId, @RequestParam("type") String
            type, HttpServletResponse response, String secToken, String channelId) throws Exception {
        response.setContentType("image/jpeg");
        String path = "imgs";
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = "";
            if (channelId.equals("h5")) {
                mobile = rsaUtils.decryptByPriKey(secToken).trim();
            } else {
                mobile = commonService.getMobile(secToken, "weiting");
            }
            if (StringUtils.isEmpty(mobile)) {
                object.put(Constant.MSG, "userId_isNULL");
            } else {
                PlodPubUser user = plodMapper.findPubUserByUserId(mobile);
                PlodLoginUser user1 = plodMapper.findLoginUserByUserId(mobile);
                if ((user != null) || (user1 != null)) {
                    if (type.equals("videos")) {
                        path = "videos";
                        response.setContentType("video/mpeg4");
                    }
                    System.out.println(basePath + File.separator + path + File.separator + imgId);
                    File file = new File(basePath + File.separator + path + File.separator + imgId);
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
                } else {
                    object.put(Constant.MSG, "NoPower");
                }
            }
            PrintWriter out = response.getWriter();
            out.println(object);
            out.flush();
            out.close();
        }
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
    JSONObject saveAdvise(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, String
            channelId, String title, String msgText, String path, String videoPath, String raceType, String raceContent) throws
            IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
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
    JSONObject myRecord(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, String
            channelId) throws IOException {
        JSONObject object = new JSONObject();
        if (StringUtils.isEmpty(secToken)) {
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
    JSONObject checkLogin(String userName, String password) throws IOException {
        JSONObject object = new JSONObject();
        object.put(Constant.MSG, "success");
        JSONObject object1 = plodService.checkLogin(userName, password);
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
     * @param channelId 渠道
     * @return JSONObject
     * @throws IOException ioe异常
     */
    @PostMapping(value = "/playAdvise")
    public @ResponseBody
    JSONObject playAdvise(@ApiParam(name = "loginUserId", value = "用户标识", required = true) String
                                  loginUserId, String channelId, String id, String endRaceType, String endRaceContent, String sort, String
                                  message) throws IOException {
        JSONObject object = new JSONObject();
        object.put(Constant.MSG, "userId_isNULL");
        object.put(Constant.MSG, "success");
        JSONObject object1 = plodService.playAdvise(id, loginUserId, endRaceType, endRaceContent, sort, message);
        object.put("data", object1);
        return object;
    }

}
