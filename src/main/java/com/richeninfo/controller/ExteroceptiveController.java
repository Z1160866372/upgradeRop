package com.richeninfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.mapper.master.ExteroceptiveMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.ExteroceptiveService;
import com.richeninfo.util.RedisUtil;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @auth lei
 * @date 2024/3/22 15:40
 */

@Controller
@RequestMapping(value = "/proem")
public class ExteroceptiveController {

    @Resource
    private CommonService commonService;
    @Resource
    private ExteroceptiveService exteroceptiveService;
    @Resource
    private ExteroceptiveMapper exteroceptiveMapper;
    @Resource
    private HttpSession session;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse resp;
    @Resource
    private RedisUtil redisUtil;

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
        ActivityUser user = new ActivityUser();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            String mobile = commonService.getMobile(secToken, channelId);
            if (mobile.isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                JSONObject object1 = exteroceptiveService.initializeUser(mobile, secToken, channelId, actId);
                object.put(Constant.MSG, Constant.SUCCESS);
                object.put("data", object1);
            }
        }
        return object;
    }

    /**
     * 去答题
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/toanswer")
    @ResponseBody
    public JSONObject toanswer(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
               object=exteroceptiveService.toAnswer(channelId,user.getUserId());
            }
        }
        return object;
    }
    /**
     * 提交评测内容
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/answer")
    @ResponseBody
    public JSONObject answer(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String answer,String answerTitle ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.answer(channelId,answer,answerTitle,user.getUserId());
            }
        }
        return object;
    }

    /**
     * 吹泡泡游戏
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/play")
    @ResponseBody
    public JSONObject play(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String paopao ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.play(channelId,paopao,user.getUserId());
            }
        }
        return object;
    }

    /**
     * 心级好礼
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/tochoujiang")
    @ResponseBody
    public JSONObject tochoujiang(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.tochoujiang(channelId,user.getUserId());
            }
        }
        return object;
    }


    /**
     * 抽奖
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/choujiang")
    @ResponseBody
    public JSONObject choujiang(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.choujiang(channelId,user.getUserId(),actId);
            }
        }
        return object;
    }

    /**
     * 个人经验值明细
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/todetail")
    @ResponseBody
    public JSONObject todetail(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.todetail(channelId,user.getUserId());
            }
        }
        return object;
    }

    /**
     * 用户操作记录
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/actRecord")
    @ResponseBody
    public JSONObject actRecord(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String caozuo,int type ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                exteroceptiveService.changeStatus(caozuo,channelId,type,user.getUserId());
                object.put(Constant.MSG,"success");
            }
        }
        return object;
    }


    /**
     * 我的奖励
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/received")
    @ResponseBody
    public JSONObject received(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
            ActivityUser user = exteroceptiveMapper.findUserInfoByUserSecToken(secToken);
            if (user.getUserId().isEmpty()) {
                object.put(Constant.MSG, "channelId_error");
            } else {
                object=exteroceptiveService.myReceived(channelId,user.getUserId(),actId);
                object.put(Constant.MSG,"success");
            }
        }
        return object;
    }

    /**
     * 活动时间
     * @param secToken
     * @param channelId
     * @param actId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/ifActDate")
    @ResponseBody
    public JSONObject ifDate(@ApiParam(name = "secToken", value = "用户标识", required = true) String secToken, @ApiParam(name = "channelId", value = "参与渠道", required = true) String channelId, @ApiParam(name = "actId", value = "活动编号", required = true) String actId,String caozuo,int type ) throws IOException {
        JSONObject object = new JSONObject();
        if (secToken.isEmpty()) {
            object.put(Constant.MSG, "login");
        } else {
           String msg= exteroceptiveService.selectTime(actId);
            object.put(Constant.MSG,msg);
        }
        return object;
    }
}