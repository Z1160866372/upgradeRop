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
 * @date 2024/4/25 13:29
 */
@Mapper
public interface PlodMapper {

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from wt_plod_user where userId = #{userId} ")
    ActivityUser findUserInfo(@Param("userId") String userId);

    /**
     * 初始化用户信息
     * @param user
     */
    @Insert("INSERT INTO wt_plod_user (userId,level,award,playNum,channelId,createTime,createDate) value (#{userId},#{level},#{award},#{playNum},#{channelId},now(),curdate())")
    void saveUser(ActivityUser user);

    /**
     * 查询是否有当月获取记录
     * @param userId
     * @return
     */
    @Select("select * from wt_plod_history where userId = #{userId}")
    ActivityUserHistory findCurMonthHistory(@Param("userId") String userId);

    /**
     * 查询奖品
     * @param unlocked
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where unlocked=#{unlocked} and actId=#{actId} limit 1")
    ActivityConfiguration findGiftByUnlocked(int unlocked, String actId);

    /**
     * 查询奖品
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where   actId=#{actId}")
    List<ActivityConfiguration> findGiftList(String actId);

    /**
     * 更新用户secToken
     * @param userId
     * @param secToken
     * @return
     */
    @Update("update  wt_plod_user set secToken=#{secToken}  where userId=#{userId} ")
    int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);

    /**
     * 更新用户answer
     * @param userId
     * @param answer
     * @return
     */
    @Update("update  wt_plod_user set answer=#{answer}  where userId=#{userId} ")
    int updateUserAnswer(@Param("userId") String userId, @Param("answer") String answer);
    /**
     * 新增用户获取奖品
     * @param history
     * @return
     */
    @Insert("INSERT INTO wt_plod_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark})")
    int saveHistory(ActivityUserHistory history);

    /**
     * 更新用户award
     * @param userId
     * @return
     */
    @Update("update  wt_plod_user set award=award+1 where userId=#{userId} ")
    int updateUserAward(@Param("userId") String userId);

    /**
     * 查询员工信息
     * @return
     */
    @Select("select * from wt_plod_pubUser where userId=#{userId} ")
    PlodPubUser findPubUserByUserId(String userId);

    /**
     * 新增advice
     * @return
     */
    @Insert("INSERT INTO wt_plod_advise (userId,title,msgText,uploadFile,message,fileUrl,status,adviseScore,departName,userName,videoPath,raceType,raceContent,createTime) value (#{userId},#{title},#{msgText},#{uploadFile},#{message},#{fileUrl},#{status},#{adviseScore},#{departName},#{userName},#{videoPath},#{raceType},#{raceContent},#{createTime})")
    int savePlodAdvise(PlodAdvise advise);

    /**
     * 查询用户建议
     */
    @Select("select * from wt_plod_advise where userId=#{userId}")
    List<PlodAdvise> findUserAdviseList(String usreId);

    /**
     * 管理用户信息
     */
    @Select("select * from wt_plod_login where  userName=#{userName} and password=#{password}")
    PlodLoginUser findLoginUser(@Param("userName") String userName,@Param("password") String password);

    /**
     * 后台管理 查询所有建议
     */
    @Select("select * from wt_plod_advise order by createTime desc")
    List<PlodAdvise> findAllAdvise();

    /**
     * 管理用户信息 byUserId
     */
    @Select("select * from wt_plod_login where  userId=#{userId} ")
    PlodLoginUser findLoginUserByUserId(@Param("userId") String userId);

    /**
     * 查询advise ById
     */
    @Select("select * from wt_plod_advise where  id=#{id} ")
    PlodAdvise findAdviseById(@Param("id") int id);

    /**
     * 更新用户advise
     * @return
     */
    @Update("update  wt_plod_advise set endRaceType=#{endRaceType},endRaceContent=#{endRaceContent},approverScore=#{approverScore},status=#{status},approver=#{approver},message=#{message} where id=#{id}")
    int updatePlodAdviseById(PlodAdvise plodAdvise);


    /**
     * 更新用户成绩
     * @return
     */
    @Update("update  wt_plod_pubuser set totalScore=#{score} where userId=#{userId}")
    int updatePubUserScore(@Param("userId")String userId,@Param("score")int score);

}


