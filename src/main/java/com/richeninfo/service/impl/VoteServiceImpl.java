package com.richeninfo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.entity.mapper.entity.*;
import com.richeninfo.entity.mapper.mapper.master.VoteMapper;
import com.richeninfo.pojo.Constant;
import com.richeninfo.service.CommonService;
import com.richeninfo.service.VoteService;
import com.richeninfo.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/27 15:06
 */
@Service("voteService")
@Slf4j
public class VoteServiceImpl implements VoteService {
    @Resource
    private VoteMapper voteMapper;
    @Resource
    private CommonUtil commonUtil;
    @Resource
    private CommonService commonService;
    Map<String,String> map = new HashMap<>();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public JSONObject getClassifyInfo(){
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        Map<String,String> map_qy = new HashMap<>();
        try {
            List<VoteClassify> classifyList =  voteMapper.selectVoteClassify(map);
            if(classifyList.isEmpty()){
                object.put(Constant.MSG,Constant.ERROR);
            }else{
                object.put(Constant.MSG,Constant.SUCCESS);
                for (VoteClassify voteClassify:classifyList){
                    JSONObject new_object = new JSONObject();
                    map_qy.put("cid",""+voteClassify.getCid()+"");
                    new_object.put("cid",voteClassify.getCid());
                    new_object.put("name",voteClassify.getName());
                    List<VoteClassifyInfo> classifyInfoList =  voteMapper.selectVoteClassifyInfo(map_qy);
                    for (VoteClassifyInfo voteClassifyInfo: classifyInfoList){
                        if("NotStarted".equals(commonUtil.timeVerify(voteClassifyInfo.getStartTime(),voteClassifyInfo.getEndTime()))){
                            voteClassifyInfo.setStatus(0);
                        }
                        if("underway".equals(commonUtil.timeVerify(voteClassifyInfo.getStartTime(),voteClassifyInfo.getEndTime()))){
                            voteClassifyInfo.setStatus(1);
                        }
                        if("over".equals(commonUtil.timeVerify(voteClassifyInfo.getStartTime(),voteClassifyInfo.getEndTime()))){
                            voteClassifyInfo.setStatus(2);
                        }
                    }
                    new_object.put("VoteClassifyInfo",classifyInfoList);
                    jsonArray.add(new_object);
                }
                object.put(Constant.DATA,jsonArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public JSONObject getCode(String secToken,String channelId,String iid) {
        JSONObject object = new JSONObject();
        if(secToken.isEmpty()||channelId.isEmpty()){
            object.put(Constant.MSG,"login");
        }else{
            String mobile = commonService.getMobile(secToken,channelId);
            VoteCode voteCode = voteMapper.selectCode(mobile,new Integer(iid));
            if(null==voteCode){
                object.put(Constant.MSG,"noAccess");
            }else{
                object.put(Constant.MSG,Constant.SUCCESS);
                object.put("loginCode",voteCode);
            }

        }
        return object;
    }

    @Override
    public JSONObject getTopicList(String secToken,String channelId,String yid,String iid) {
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(secToken.isEmpty()||channelId.isEmpty()){
            object.put(Constant.MSG,"login");
        }else{
            String mobile = commonService.getMobile(secToken,channelId);
            List<VoteDepartment> departmentList =voteMapper.selectVoteDepartment(map);
            for (VoteDepartment voteDepartment:departmentList){
                JSONObject new_object = new JSONObject();
                new_object.put("did",voteDepartment.getDid());
                new_object.put("name",voteDepartment.getName());
                getStatus(yid, mobile, voteDepartment, new_object,iid);
                List<VoteTopic> topicList = null;
                if(new Integer(iid)==1){//投票
                    topicList = voteMapper.selectTopicList(mobile,voteDepartment.getDid(),new Integer(iid));
                }else{
                    topicList = voteMapper.selectTopicByDid(voteDepartment.getDid(),new Integer(iid));
                }
                for (VoteTopic topic:topicList){
                    VoteLog voteLog = voteMapper.selectLog(yid,voteDepartment.getDid(),topic.getTid());
                    if(null!=voteLog){
                        topic.setContent(voteLog.getContent());
                        topic.setFetchScore(voteLog.getScore());
                        topic.setStatus(1);
                    }
                }
                new_object.put("VoteTopic",topicList);
                jsonArray.add(new_object);
            }
            object.put(Constant.DATA,jsonArray);
            object.put(Constant.MSG,Constant.SUCCESS);
        }
        return object;
    }

    private void getStatus(String yid, String mobile, VoteDepartment voteDepartment, JSONObject new_object,String iid) {
        if (voteMapper.selectDid(mobile)==voteDepartment.getDid()){
            new_object.put("status",1);
        }else{
            int answer_num = voteMapper.selectLogByYaD(new Integer(yid),voteDepartment.getDid(),new Integer(iid));
            if(answer_num>0){
                new_object.put("status",2);
            }else{
                new_object.put("status",0);
            }
        }
    }

    @Override
    public JSONObject saveAnswerLog(String answerLog) {
        JSONObject object = new JSONObject();
        try {
            JSONObject jsonObject = JSONObject.parseObject(answerLog);
            int result_score =0;
            int yid = Integer.parseInt((String) jsonObject.get("yid"));
            int iid =Integer.parseInt((String) jsonObject.get("iid"));
            String data = (String)jsonObject.get("data");
            if(!data.isEmpty()){
                String [] resultList=data.split(",");
                int answer_num = voteMapper.selectLogByYaD(yid,new Integer(resultList[0].split("_")[0]),new Integer(iid));
                if(answer_num>0){
                    object.put(Constant.MSG,Constant.YLQ);
                }else{
                    for (int i = 0; i < resultList.length; i++) {
                        VoteLog log = new VoteLog();
                        log.setYid(yid);
                        log.setDid(new Integer(resultList[i].split("_")[0]));
                        log.setTid(new Integer(resultList[i].split("_")[1]));
                        if(iid==1){
                            log.setScore(new Integer(resultList[i].split("_")[2]));
                            if(resultList[i].split("_").length>3){
                                log.setContent(resultList[i].split("_")[3]);
                            }
                            result_score+=new Integer(resultList[i].split("_")[2]);
                        }
                        log.setIid(iid);
                        log.setCreatedDate(df.format(new Date()));
                        voteMapper.insertVoteLog(log);
                    }
                    VoteResult result = new VoteResult();
                    result.setYid(yid);
                    result.setIid(iid);
                    if(iid==1){
                        result.setDid(new Integer(resultList[0].split("_")[0]));
                        result.setScore(result_score);
                    }
                    result.setCreatedDate(df.format(new Date()));
                    voteMapper.insertVoteResult(result);
                    object.put(Constant.MSG,Constant.SUCCESS);
                }
            }else{
                object.put(Constant.MSG,Constant.ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            object.put(Constant.MSG,Constant.ERROR);
        }
        return object;
    }

    @Override
    public JSONObject saveVoteCode() {
        List<VoteDepartment> departmentList =voteMapper.selectVoteDepartment(map);
        for (VoteDepartment department:departmentList){
            for (int i = 0; i < department.getNumber(); i++) {
                VoteCode code = new VoteCode();
                code.setLoginCode(commonUtil.getRandomChar(7));
                code.setDid(department.getDid());
                code.setCreateDate(df.format(new Date()));
                voteMapper.insertVoteCode(code);
            }
        }
        return null;
    }

    @Override
    public JSONObject scoreStatistics(String rid) {
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(!rid.isEmpty()){
            if(new Integer(rid)<3){
                List<VoteDepartment> departmentList =voteMapper.selectVoteDepartment(map);
                for (VoteDepartment department:departmentList){
                    JSONObject new_object = new JSONObject();
                    Map<String,String> new_map = new HashMap<>();
                    new_map.put("did",department.getDid()+"");
                    new_map.put("rid",rid);
                    new_object.put("name",department.getName());
                    List<VoteList> voteLists = voteMapper.selectVoteList(new_map);
                    new_object.put("voteLists",voteLists);
                    jsonArray.add(new_object);
                }
            }else{
                List<VoteCode> voteCodes = voteMapper.selectVoteCode(0);
                for (VoteCode voteCode:voteCodes){
                    JSONObject new_object = new JSONObject();
                    Map<String,String> new_map = new HashMap<>();
                    new_map.put("loginCode",voteCode.getLoginCode());
                    new_map.put("rid",rid);
                    new_object.put("loginCode",voteCode.getLoginCode());
                    List<VoteList> voteLists = voteMapper.selectVoteListBy(new_map);
                    if(!voteLists.isEmpty()){
                        new_object.put("voteLists",voteLists);
                        jsonArray.add(new_object);
                    }
                }
            }
            object.put(Constant.DATA,jsonArray);
        }

        return object;
    }
}
