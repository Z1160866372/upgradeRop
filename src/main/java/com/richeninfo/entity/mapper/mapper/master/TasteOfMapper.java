/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */
package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/3/22 16:15
 */
@Mapper
public interface TasteOfMapper {
    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from wt_tasteof_user where userId = #{userId}")
    ActivityUser findUserInfoByUserId(String userId);




    /**
     * 初始化用户信息
     * @param user
     */
    @Insert("INSERT INTO wt_tasteof_user (userId,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken,ditch,unlocked) value (#{userId},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken},#{ditch},#{unlocked})")
    void saveUser(ActivityUser user);

    /**
     * 随机第二题目
     * @return
     */
    @Select(" SELECT * FROM `202303_wt_vote_topic` where status >0 ORDER BY RAND() LIMIT 1")
    VoteTopic findRandAnswerOne();

    /**
     * 固定第一题
     * @return
     */
    @Select("select * from 202303_wt_vote_topic where status=0 and iid=3")
    VoteTopic findFirstAnswer();

    /**
     * 根据status 查询题目
     * @param answerType
     * @return
     */
    @Select("select * from 202303_wt_vote_topic where status=#{status} and iid=3")
    VoteTopic findAnswerByType(int answerType);

    /**
     * 查询用户获取奖品记录
     * @param userId
     * @return
     */
    @Select("select * from wt_tasteof_history where userId=#{userId} order by createTime desc")
    List<ActivityUserHistory> findUserRecived(String userId);

    /**
     * 查询奖励 BY Id
     * @param id
     * @return
     */
    @Select("select * from wt_tasteof_history where id=#{id}")
    ActivityUserHistory findHistoryById(int id);

    /**
     * 更新用户答题信息
     * @param userId
     * @return
     */
    @Update("update  wt_tasteof_user set answerNum=answerNum-1  where userId=#{userId}  and answerNum >0")
    int lostAnswerNum(String userId);

    /**
     * 更新用户游戏机会
     * @param userId
     * @return
     */
    @Update("update  wt_tasteof_user set playNum=1,grade=1  where userId=#{userId}  and playNum =0")
    int updateUserPlayNum(String userId);

    /**
     * 更新分享标识
     * @param userId
     * @return
     */
    @Update("update  wt_tasteof_user set helpNum=1  where userId=#{userId}")
    int updateUserHelpNum(String userId);

    /**
     * 添加标记
     * @param userId
     * @param mark
     * @return
     */
    @Update("update  wt_tasteof_user set mark=#{mark} where userId=#{userId}")
    int addUserMark(@Param("userId") String userId, @Param("mark") int mark);

    /**
     * 更新用户今日答题内容
     * @param userId
     * @param answer
     * @param answerTitle
     * @return
     */
    @Update("update  wt_tasteof_user set answer=#{answer},answerTitle=#{answerTitle} where userId=#{userId}")
    int updateAnswer(@Param("userId") String userId, @Param("answer") String answer, @Param("answerTitle") String answerTitle);




    /**
     * 扣减用户游戏机会
     * @param id
     * @return
     */
    @Update("update  wt_tasteof_user set playNum=playNum-1  where id=#{id} and playNum >0")
    int LostPlayNum(int id);

    /**
     * 更新用户获得首次奖励
     * @param userId
     * @return
     */
    @Update("update  wt_tasteof_user set award=award+1 where userId=#{userId} and award=0")
    int updateUserAward(String userId);

    /**
     * 查询奖品
     * @param unlocked
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where unlocked=#{unlocked} and actId=#{actId} limit 1")
    ActivityConfiguration findGiftByTypeId(int unlocked, String actId);


    /**
     * 查询对应节日奖品
     * @param unlocked
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where unlocked=#{unlocked} and  module=1 and actId=#{actId} ")
    ActivityConfiguration findJRWeekGift(int unlocked, String actId);

    /**
     * 查询活动周期
     * @return
     */
    @Select("select * from activity_configuration where actId='tasteOfWeek' and   NOW() BETWEEN startTime and endTime ")
    ActivityConfiguration findActWeekList();



    /**
     * 查询每月配置奖品
     * @param module
     * @param actId
     * @return
     */
    @Select(" select * from activity_configuration where module=#{module} and actId=#{actId}")
    List<ActivityConfiguration> findGiftListByObtain(int module, String actId);


    /**
     * 查询元旦期间奖励
     * @param actId
     * @return
     */
    @Select(" select * from activity_configuration where module=1 and  unlocked=1  and actId=#{actId}")
    ActivityUserHistory findFirstWeekHistory( String actId);


    /**
     * 查询每月配置权益
     * @param month
     * @param actId
     * @return
     */
    @Select(" select * from activity_configuration where unlocked >0 and value >0 and  module=0 and actId=#{actId} or(value =0 and module =#{month} and actId=#{actId} )")
    List<ActivityConfiguration> findGiftList(int month, String actId);


    /**
     * 新增用户获取奖品
     * @param history
     * @return
     */
    @Insert("INSERT INTO wt_tasteof_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark,value,activityId,itemId,ditch,module) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark},#{value},#{activityId},#{itemId},#{ditch},#{module})")
    int saveHistory(ActivityUserHistory history);


    /**
     * 分享记录
     * @param share
     */
    @Insert(" INSERT INTO wt_tasteof_share_history (userId,channelId,actId,secToken,createTime,createDate) value (#{userId},#{channel_id},#{actId},#{secToken},now(),curdate())")
    void saveShareHistory(ActivityShare share);


    /**
     * 每周期更新用户数据
     * @param userId
     * @param unlocked 周期标识
     * @return
     */
    @Update("update  wt_tasteof_user set  playNum=1, answerNum=1 , unlocked= #{unlocked} ,grade=0 where userId=#{userId} ")
    int updateUserWeekInfo(@Param("userId") String userId,@Param("unlocked") int unlocked);

    /**
     * mq更新奖品发放状态
     * @param history
     * @return
     */
    @Update("update wt_tasteof_history set status=#{status},code=#{code},message=#{message},couponCode=#{couponCode} where id = #{id}")
    int updateHistory(ActivityUserHistory history);


    /**
     * 更新用户成为心级体验官的状态
     * @param userId
     * @return
     */
    @Update("update  wt_tasteof_user set level=1 where userId=#{userId} ")
    int changeLevel(String userId);



}
