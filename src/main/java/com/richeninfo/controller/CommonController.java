/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.CommonMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.pojo.Packet;
import com.richeninfo.pojo.PacketMq;
import com.richeninfo.pojo.Result;
import com.richeninfo.service.CommonService;
import com.richeninfo.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/21 11:21
 */
@Controller
@Api(value = "互动营销活动公共接口", tags = {"互动营销活动公共接口"})
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private CommonService commonService;
    @Resource
    private CommonMapper commonMapper;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;
    @Resource
    private ImageUtil imageUtil;
    @Resource
    private RSAUtils rsaUtils;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    HttpServletResponse response;
    @Resource
    private FileUtil fileUtil;

    @Resource
    private RopServiceManager ropServiceManager;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private PacketHelper packetHelper;
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${context}")
    private String context;
    SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping(value = "wtFree")
    protected String wtFree(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,
                            @ApiParam(name = "actId", value = "活动标识", required = true) String actId,
                            @ApiParam(name = "ditch", value = "渠道", required = true) String ditch,
                            @ApiParam(name = "openid", value = "渠道", required = true) String openid,
                            @ApiParam(name = "belongFlag", value = "异网标识", required = true) String belongFlag) throws ServletException, IOException {
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        ditch = request.getParameter("ditch") == null ? "" : request.getParameter("ditch");
        openid = request.getParameter("openid") == null ? "" : "1";
        belongFlag = request.getParameter("belongFlag") == null ? "" : request.getParameter("belongFlag");
        log.info("微厅免登录接收secToken==" + secToken);
        log.info("微厅免登录接收belongFlag==" + belongFlag);
        log.info("微厅免登录接收openid==" + openid);
        String url = "";
        ActivityList activity = commonMapper.selectActivityByActId(actId);
        url = activity.getAddress() + "?secToken=" + secToken + "&ditch=" + ditch + "&belongFlag=" + belongFlag+ "&openid=" + openid;
       /* if(actId.equals("newcall")||actId.equals("finance")||actId.equals("schoolbaq")||actId.equals("consult")||actId.equals("fortune")
        ||actId.equals("migumonth")||actId.equals("miguxc")||actId.equals("proem")||actId.equals("plod")||actId.equals("miguflow")){
            url="https://activity.sh.10086.cn/"+actId+"/index.html?secToken="+secToken+"&ditch="+ditch+"&belongFlag="+belongFlag;
        }else{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以需要+1
            String formattedMonth="";
            if(month<10){
                formattedMonth = String.format("%02d", month);
            }
            url="https://activity.sh.10086.cn/"+context+"/"+year+"/"+formattedMonth+"/"+actId+"/index.html?secToken="+secToken+"&ditch="+ditch+"&belongFlag="+belongFlag;
       }*/
        return "redirect:" + url + "";
    }

    @GetMapping("/img-verify-code")
    @ResponseBody
    @ApiOperation("获取图形验证码")
    protected JSONObject images() throws Exception {
        JSONObject jsonRESTResult = new JSONObject();
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = imageUtil.createImage();
        //将验证码存入redis
        redisUtil.set("" + objs[0] + "", objs[0]);
        //将图片转正base64
        BufferedImage image = (BufferedImage) objs[1];
        //转base64
        // BASE64Encoder encoder = new BASE64Encoder();
        Base64.Encoder encoder = Base64.getEncoder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
        ImageIO.write(image, "png", baos);//写入流中
        byte[] bytes = baos.toByteArray();//转换成字节
        String png_base64 = encoder.encodeToString(bytes).trim();//转换成base64串
        //删除 \r\n
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");
        Map map = new HashMap<>();
        map.put("base64", "data:image/png;base64," + png_base64);
        map.put("validateCode", objs[0]);
        jsonRESTResult.put("map", map);
        return jsonRESTResult;
    }

    @PostMapping(value = "/newSendMsg")
    @ApiOperation("获取短信验证码")
    public @ResponseBody
    Object sendMsg(@ApiParam(name = "smsRandom", value = "图形验证码", required = true) String smsRandom, @ApiParam(name = "mobilePhone", value = "用户号码", required = true) String mobilePhone, @ApiParam(name = "actId", value = "活动标识", required = true) String actId) throws Exception {
        log.info("获取：" + redisUtil.get(smsRandom));
        Cookie[] cookies = request.getCookies();
        smsRandom = request.getParameter(Constant.SMS_RANDOM) == null ? "" : request.getParameter(Constant.SMS_RANDOM);
        JSONObject resultObj = new JSONObject();
        resultObj.put("cookie", cookies);
        //校验图形参数是否正确 不区分大小写
        if (redisUtil.get(smsRandom) == null || !smsRandom.toLowerCase().equals(redisUtil.get(smsRandom).toString().toLowerCase())) {
            resultObj.put(Constant.MSG, Constant.YZM_ERROR);
            return resultObj;
        }
        mobilePhone = request.getParameter(Constant.KEY_MOBILE) == null ? "" : request.getParameter(Constant.KEY_MOBILE);
        if (!mobilePhone.isEmpty()) {
            mobilePhone = rsaUtils.decryptByPriKey(mobilePhone).trim();
        }
        if (!commonService.checkUserIsChinaMobile(mobilePhone, actId)) {
            resultObj.put(Constant.MSG, Constant.ERROR);
            return resultObj;
        }
        return this.commonService.sendMsgCode(mobilePhone, actId);
    }

    @PostMapping(value = "/login_check")
    @ApiOperation("H5登录验证")
    public @ResponseBody
    Object validMsgNoVal(@ApiParam(name = "keyCode", value = "短信验证码", required = true) String keyCode, @ApiParam(name = "mobilePhone", value = "用户号码", required = true) String mobilePhone) throws Exception {
        JSONObject resultObj = new JSONObject();
        mobilePhone = request.getParameter(Constant.KEY_MOBILE) == null ? "" : request.getParameter(Constant.KEY_MOBILE);
        keyCode = request.getParameter(Constant.KEY_CODE) == null ? "" : request.getParameter(Constant.KEY_CODE);
        if (mobilePhone.isEmpty() || keyCode.isEmpty()) {
            resultObj.put(Constant.MSG, Constant.SMS_MOBIEL_OR_CODE_IS_NULL);
            return resultObj;
        }
        if (!mobilePhone.isEmpty()) {
            log.info("解析mobilePhone==="+mobilePhone);
            mobilePhone = rsaUtils.decryptByPriKey(mobilePhone).trim();
        }
        //校验验证码
        boolean isMatched = false;
        isMatched = this.commonService.valSendMsgCode(mobilePhone, keyCode);
        if (isMatched) {
            redisUtil.set(Constant.KEY_MOBILE, mobilePhone);
            String key = commonMapper.selectTheDayKey().getSecretKey();
            String secToken = Des3SSL.encodeDC(mobilePhone, key);
            log.info("生成secToken:" + secToken);
            resultObj.put("secToken", secToken);
            resultObj.put(Constant.MSG, Constant.SUCCESS);
        } else {
            resultObj.put(Constant.MSG, Constant.SMS_CODE_NOt_MATCHED);
        }
        return resultObj;
    }

    /**
     * 活动校验
     *
     * @param actId
     * @return
     * @throws Exception
     */
    @ApiOperation("活动校验(时间||白名单||WAP20用户)")
    @PostMapping(value = "/verityActive")
    public @ResponseBody
    Object getActiveStatus(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动标识", required = true) String actId, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId, @ApiParam(name = "isTestWhite", value = "是否加白名单验证", required = true) boolean isTestWhite) throws Exception {
        return this.commonService.verityActive(secToken, actId, isTestWhite, channelId);
    }

    /**
     * 上海移动手机号校验
     *
     * @param actId
     * @return
     * @throws Exception
     */
    @ApiOperation("异网用户校验")
    @PostMapping(value = "/verityFlag")
    public @ResponseBody
    Object verityFlag(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动标识", required = true) String actId, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId, @ApiParam(name = "isTestWhite", value = "是否加白名单验证", required = true) boolean isTestWhite) throws Exception {
        String userId=commonService.getMobile(secToken, channelId);
        log.info("异网用户校验userId="+userId);
        return this.commonService.checkUserIsChinaMobile(userId, actId);
    }



    @ApiOperation(value = "二次短信下发", httpMethod = "POST")
    @PostMapping(value = "/sendSms5956")
    public @ResponseBody
    Object sendSms5956(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) int unlocked) throws IOException {
        Object object = commonObject(secToken, channelId, actId).getString(Constant.MSG);
        if (object.equals(Constant.SUCCESS)) {
            return commonService.sendSms5956(commonObject(secToken, channelId, actId).getString(Constant.KEY_MOBILE), actId, unlocked);
        }
        return object;
    }

    /**
     * 活动校验
     *
     * @param secToken
     * @return
     * @throws Exception
     */
    @ApiOperation("活动校验(OA用户)")
    @PostMapping(value = "/verityOa")
    public @ResponseBody
    Object verityOa(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId) throws Exception {
        return this.commonService.verityOa(secToken, channelId);
    }


    /**
     * 提取公共内容
     *
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     */
    public JSONObject commonObject(String secToken, String channelId, String actId) {
        JSONObject object = new JSONObject();
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put(Constant.KEY_MOBILE, mobile);
            }
        }
        return object;
    }


    static void getActId(HttpServletRequest request, List<ActivityConfiguration> configuration, HttpServletResponse resp, @ApiParam(name = "secToken", value = "用户标识", required = true) String secToken) throws IOException {
        String actId;
        String channelId;
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        List<ActivityConfiguration> config = configuration;
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(JSON.toJSONString(config));
    }

    static void getParameter(HttpServletRequest request, String actId, String channelId, int unlocked, String ditch) {
        actId = request.getParameter("actId") == null ? "" : request.getParameter("actId");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        unlocked = request.getParameter("unlocked") == null ? 0 : Integer.parseInt(request.getParameter("unlocked"));
        actId = request.getParameter("ditch") == null ? "" : request.getParameter("ditch");
    }

    /**
     * 保存用户操作记录
     *
     * @return
     */
    @ApiOperation("保存用户操作记录")
    @PostMapping(value = "/insertOperationLog")
    public @ResponseBody
    Object insertOperationLog(@ModelAttribute OperationLog log) {
        return this.commonService.insertOperationLog(log);
    }

    /**
     * 保存用户分享记录
     *
     * @return
     */
    @ApiOperation("保存用户分享记录")
    @PostMapping(value = "/insertActivityShare")
    public @ResponseBody
    Object insertActivityShare(@ModelAttribute ActivityShare activityShare) {
        return this.commonService.insertActivityShare(activityShare);
    }


    @ApiOperation("数据补发接口)")
    @PostMapping(value = "/dataReissue")
    public @ResponseBody
    Object dataReissue(@ApiParam(name = "createTime", value = "截止日期", required = true) String createTime, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws Exception {
        JSONObject object = new JSONObject();
        String keyword = "wt_" + actId + "_history";
        List<ActivityUserHistory> activityUserHistoryList = commonMapper.selectActivityUserHistory(createTime, keyword);
        if (!activityUserHistoryList.isEmpty()) {
            for (ActivityUserHistory history : activityUserHistoryList) {
                if (history.getActivityId() != null && history.getUserId() != null) {
                    String mqMsg = commonService.issueReward(history);
                    log.info("4147请求信息：" + mqMsg);
                    jmsMessagingTemplate.convertAndSend("commonQueue", mqMsg);
                }
            }
            object.put("objectList", activityUserHistoryList);
            object.put(Constant.MSG, "yesData");
        } else {
            object.put(Constant.MSG, "noData");
        }
        return object;
    }



    @ApiOperation("41417礼包定向发放接口)")
    @PostMapping(value = "/data4147")
    public @ResponseBody
    Object dataReissue(@ApiParam(name = "userId", value = "手机号", required = true) String userId,@ApiParam(name = "activityId", value = "活动促销项", required = true) String activityId, @ApiParam(name = "itemId", value = "活动编号", required = true) String itemId) throws Exception {
        JSONObject object = new JSONObject();
        ActivityUserHistory activityUserHistory=new ActivityUserHistory();
        activityUserHistory.setUserId(userId);
        activityUserHistory.setActivityId(activityId);
        activityUserHistory.setItemId(itemId);
        if (activityUserHistory.getActivityId() != null && activityUserHistory.getUserId() != null) {
            PacketMq mq = new PacketMq();
            String out_order_id = commonUtil.getRandomCode(14, 0);
            Packet packet = packetHelper.getCommitPacket4147(userId, activityId, itemId, out_order_id);
            String response_message = ropServiceManager.execute(packet, userId,"data4147");
            Result request = JSON.parseObject(response_message, Result.class);
            String code = request.getResponse().getErrorInfo().getCode();
            String resCode = request.getResponse().getRetInfo().getString("resultCode");
            object.put("code", code);
        }
        object.put("objectList", activityUserHistory);
        object.put(Constant.MSG, "yesData");
        return object;
    }
    /**
     * 附件上传(仅支持CSV)
     *
     * @param file
     */
    @ResponseBody
    @RequestMapping("attachmentUpload")
    public void attachmentUpload(@RequestParam(value = "file", required = false) MultipartFile file) {
        fileUtil.attachmentUpload(request, response, file);
    }

    @ApiOperation(value = "初始化用户", httpMethod = "POST")
    @PostMapping(value = "/initialize")
    public @ResponseBody
    void initializeUser(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "ditch", value = "触点", required = true) String ditch) throws IOException {
        ActivityUser user = new ActivityUser();
        JSONObject object = new JSONObject();
        secToken = request.getParameter("secToken") == null ? "" : request.getParameter("secToken");
        channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId");
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile == null || mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                user.setUserId(mobile);
                user.setSecToken(secToken);
                user.setChannelId(channelId);
                user.setActId(actId);
                user.setCreateDate(day.format(new Date()));
                user.setDitch(ditch);
                user = commonService.insertUser(user);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("user", user);
            }
        }
        resp.getWriter().write(object.toJSONString());
    }

    @ApiOperation("获取奖励列表")
    @PostMapping("/getConf")
    public @ResponseBody
    void getConf(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId) throws Exception {
        CommonController.getActId(request, commonService.getConfiguration(secToken, actId, channelId), resp, secToken);
    }

    @ApiOperation("用户点击领取｜游戏结束得分")
    @PostMapping("/draw")
    public @ResponseBody
    JSONObject userDraw(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId, @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked, @ApiParam(name = "ditch", value = "触点", required = true) String ditch
            , @ApiParam(name = "grade", value = "得分", required = true) Integer grade) throws Exception {
        CommonController.getParameter(request, actId, channelId,unlocked,ditch);
        return this.commonService.submit(secToken, actId, unlocked, channelId,ditch,grade);
    }

    /**
     * 我的奖励｜排行榜
     * @param actId
     * @param channelId
     * @return
     */
    @ApiOperation("我的奖励｜排行榜")
    @PostMapping(value = "/getMyReward")
    public @ResponseBody
    Object getMyReward(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken,@ApiParam(name = "actId", value = "活动标识", required = true) String actId, @ApiParam(name = "channelId", value = "渠道", required = true) String channelId
            , @ApiParam(name = "unlocked", value = "奖励标识", required = true) Integer unlocked){
        return this.commonService.getMyReward(secToken,channelId,actId,unlocked);
    }


    @ApiOperation("集团卡券领取")
    @PostMapping("/jtGetCommitPacket1000")
    public @ResponseBody
    String jtGetCommitPacket1000(@ApiParam(name = "batchID", value = "批次id，券批次的唯一标示", required = true) String batchID,  @ApiParam(name = "actId", value = "活动编号", required = true) String actId
            , @ApiParam(name = "loginNo", value = "用户手机号", required = true) String loginNo) throws Exception {
        return this.commonService.jtGetCommitPacket1000(batchID, actId, loginNo);
    }
}
