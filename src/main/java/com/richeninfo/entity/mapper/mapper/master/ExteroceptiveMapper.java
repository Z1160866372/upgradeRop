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
public interface ExteroceptiveMapper {
    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from wt_proem_user where userId = #{userId}")
    ActivityUser findUserInfoByUserId(String userId);


    /**
     * 查询一期用户信息
     * @param userId
     * @return
     */
    @Select("select * from wt_experience_user where userId = #{userId}")
    ActivityUser findOldUserInfoByUserId(String userId);

    /**
     * 新增用户经验值
     * @param record
     */
    @Insert("INSERT INTO wt_proem_record (userId,status,actionName,typeId,channel_id,createTime,createDate) value (#{userId},#{status},#{actionName},#{typeId},#{channel_id},now(),curdate())")
    void saveUserRecord(ActivityRecord record);

    /**
     * 初始化用户信息
     * @param user
     */
    @Insert("INSERT INTO wt_proem_user (userId,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken) value (#{userId},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken})")
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
    @Select("select * from wt_proem_history where userId=#{userId} order by createTime desc")
    List<ActivityUserHistory> findUserRecived(String userId);

    /**
     * 查询奖励 BY Id
     * @param id
     * @return
     */
    @Select("select * from wt_proem_history where id=#{id}")
    ActivityUserHistory findHistoryById(int id);

    /**
     * 更新用户答题信息
     * @param userId
     * @return
     */
    @Update("update  wt_proem_user set answerNum=answerNum-1  where userId=#{userId}  and answerNum >0")
    int lostAnswerNum(String userId);

    /**
     * 添加标记
     * @param userId
     * @param mark
     * @return
     */
    @Update("update  wt_proem_user set mark=#{mark} where userId=#{userId}")
    int addUserMark(@Param("userId") String userId, @Param("mark") int mark);

    /**
     * 更新用户今日答题内容
     * @param userId
     * @param answer
     * @param answerTitle
     * @return
     */
    @Update("update  wt_proem_user set answer=#{answer},answerTitle=#{answerTitle} where userId=#{userId}")
    int updateAnswer(@Param("userId") String userId, @Param("answer") String answer, @Param("answerTitle") String answerTitle);

    /**
     * 更新吹泡泡机会
     * @param userId
     * @return
     */
    @Update("update  wt_proem_user set blowNum=blowNum-1  where userId=#{userId}  and blowNum >0")
    int lostBlowNum(@Param("userId") String userId);

    /**
     * 经验值明细
     * @param userId
     * @return
     */
    @Select("select * from wt_proem_record where userId=#{userId} ORDER BY createTime desc")
    List<ActivityRecord> findUserRecord(String userId);

    /**
     * 更新用户等级
     * @param userId
     * @param grade
     */
    @Update("update  wt_proem_user set grade=#{grade}  where userId=#{userId}  and grade <#{grade}")
    void updateUserGrade(@Param("userId") String userId, @Param("grade") int grade);

    /**
     * 扣减用户游戏机会
     * @param id
     * @return
     */
    @Update("update  wt_proem_user set playNum=playNum-1  where id=#{id} and playNum >0")
    int LostPlayNum(int id);

    /**
     * 更新用户获得首次奖励
     * @param userId
     * @return
     */
    @Update("update  wt_proem_user set award=award+1 where userId=#{userId} and award=0")
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
     * 查询每月配置奖品
     * @param module
     * @param actId
     * @return
     */
    @Select(" select * from activity_configuration where module=#{module} and actId=#{actId}")
    List<ActivityConfiguration> findGiftListByObtain(int module, String actId);

    /**
     * 查询每月配置权益
     * @param month
     * @param actId
     * @return
     */
    @Select(" select * from activity_configuration where unlocked >0 and value >0 and actId=#{actId} or(value =0 and module =#{month} and actId=#{actId} )")
    List<ActivityConfiguration> findGiftList(int month, String actId);

    /**
     * 查询奖励配置数量
     * @param typeId
     * @param month
     * @return
     */
    @Select(" select * from activity_configuration where unlocked=#{typeId} and   value=#{month}")
    ActivityConfiguration findExperienceGiftList(@Param("typeId") int typeId, @Param("month") String month);

    /**
     * 更新每月奖品数量
     * @param unlocked
     * @param month
     * @return
     */
    @Update("update  activity_configuration set amount=amount-1  where unlocked=#{unlocked} and   value=#{month} and amount >0")
    int lostGiftListCount(@Param("unlocked") int unlocked, @Param("month") String month);

    /**
     * 新增用户获取奖品
     * @param history
     * @return
     */
    @Insert("INSERT INTO wt_proem_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark,value) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark},#{value})")
    int saveHistory(ActivityUserHistory history);

    /**
     * 用户操作记录
     * @param ActivityRecord
     * @return
     */
    @Insert(" INSERT INTO wt_proem_access (userId,status,actionName,typeId,channel_id,createTime,createDate) value (#{userId},#{status},#{actionName},#{typeId},#{channel_id},now(),curdate())")
    int saveUserAccess(ActivityRecord ActivityRecord);

    /**
     * 分享记录
     * @param share
     */
    @Insert(" INSERT INTO wt_proem_share_history (userId,channelId,actId,secToken,createTime,createDate) value (#{userId},#{channel_id},#{actId},#{secToken},now(),curdate())")
    void saveShareHistory(ActivityShare share);

    @Update("update  wt_proem_user set answerNum=1 , answer=0,blowNum=1,createTime=now()  where userId=#{userId} ")
    int updateUserCurInfo(@Param("userId") String userId);

    /**
     * mq更新奖品发放状态
     * @param history
     * @return
     */
    @Update("update wt_proem_history set status=#{status},code=#{code},message=#{message},couponCode=#{couponCode} where id = #{id}")
    int updateHistory(ActivityUserHistory history);

    /**
     * 更新用户secToken
     * @param userId
     * @param secToken
     * @return
     */
    @Update("update  wt_proem_user set secToken=#{secToken}  where userId=#{userId} ")
    int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);

    /**
     * 更新用户周期性游戏机会
     * @param userId
     * @param playNum
     */
    @Update("update  wt_proem_user set playNum=#{playNum} ,weekTime=now()  where userId=#{userId} ")
    void updateUserBaoXiangPlayNum(@Param("userId") String userId, @Param("playNum") int playNum);




}
