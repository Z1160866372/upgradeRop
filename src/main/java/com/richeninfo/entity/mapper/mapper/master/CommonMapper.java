/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * 互动营销活动公共接口
 * @create 2022/9/20 15:59
 */
@Repository
@Mapper
public interface CommonMapper {

    @Select("select * from activity_secretKey where endTime>=now() and startTime<=now()")
    ActivitySecretKey selectTheDayKey();//查找活动当天密钥

    @Select("select * from activity_roll where userId=#{userId} and userType=#{userType}")
    ActivityRoll selectRoll(@Param("userId") String userId, @Param("userType") Integer userType);//查询wap｜测试白名单

    @Select("select * from activity_list where actId = #{actId}")
    ActivityList selectActivityByActId(String actId);//查询活动信息

    @Select("select * from activity_configuration where actId = #{actId} and  NOW() BETWEEN startTime and endTime")
    List<ActivityConfiguration> selectActivityConfigurationByActId(String actId);//查询活动奖励配置

    @Select("select * from activity_configuration where actId = #{actId} and module=#{module}")
    List<ActivityConfiguration> selectActivityConfigurationByActIdAndModule(@Param("actId") String actId, @Param("module") Integer module);//查询活动奖励配置

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked}")
    ActivityConfiguration selectActivityConfiguration(@Param("actId") String actId, @Param("unlocked") Integer unlocked);//查询当前奖励配置

    @Update("update activity_configuration set Amount= Amount-1 where id = #{id} and Amount > 0")
    int updateAmount(int id);//更新奖励数量

    @Select("select * from ${keyword} where userId = #{userId} and actId =#{actId}")
    ActivityUser selectUser(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查找用户记录

    @Select("select * from ${keyword} where userId = #{userId} and actId =#{actId} and createDate=#{createDate}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword, @Param("createDate") String createDate);//查找用户记录

    @Insert("insert into ${keyword}(userId,belongFlag,userType,channelId,secToken,createDate,createTime,actId)values(#{userId},#{belongFlag},#{userType},#{channelId},#{secToken},curdate(),now(),#{actId})")
    int insertUser(ActivityUser user, @Param("keyword") String keyword);//初始化用户

    @Select("select * from activity_configuration where actId = #{actId} and unlocked = #{unlocked}")
    ActivityConfiguration selectActivitySomeConfiguration(@Param("actId") String actId, @Param("unlocked") Integer unlocked);//查询某个活动某个奖励配置

    @Select("select * from ${keyword} where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId} and status=0")
    ActivityUserHistory selectHistoryByUnlocked(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户当前奖励是否已领取

    @Select("select * from ${keyword} where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId} order by createTime desc")
    List<ActivityUserHistory> selectHistoryByUnlockedList(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户当前奖励是否已领取

    @Select("select * from ${keyword} where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId} and createDate=#{createDate}")
    ActivityUserHistory selectHistoryByUnlockedByCreateDate(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId, @Param("keyword") String keyword, @Param("createDate") String createDate);//查询用户当前奖励是否已领取

    @Update("update ${keyword} set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(@Param("status") int status,@Param("code") String code,@Param("message") String message, @Param("id") int id, @Param("keyword") String keyword);//更新接口状态

    @Select("select * from ${keyword} where userId = #{userId} and actId =#{actId}")
    List<ActivityUserHistory> selectHistory(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户领取记录

    @Select("select * from ${keyword} where userId = #{userId} and actId =#{actId} and createDate=#{createDate}")
    List<ActivityUserHistory> selectHistoryByCreateDate(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword, @Param("createDate") String createDate);//查询用户领取记录

    @Insert("insert into activity_openapiLog(address,userId,code,message,apiCode,appCode,actId,createTime)values(#{address},#{userId},#{code},#{message},#{apiCode},#{appCode},#{actId},now())")
    int insertOpenapiLog(OpenapiLog log);//添加用户业务办理记录

    @Insert("insert into ${keyword}(actId,name,address,userId,instructions,createTime)values(#{log.actId},#{log.name},#{log.address},#{log.userId},#{log.instructions},now())")
    int insertOperationLog(OperationLog log, @Param("keyword") String keyword);//添加用户操作记录

    @Select("select * from ${keyword} where userId=#{userId} and actId=#{actId}")
    List<ActivityRoster> selectRoster(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户名单列表

    @Insert("insert into activity_order(name,actId,userId,thirdTradeId,orderItemId,commodityName,bossId,code,message,channelId,createTime)values(#{name},#{actId},#{userId},#{thirdTradeId},#{orderItemId},#{commodityName},#{bossId},#{code},#{message},#{channelId},now())")
    int insertActivityOrder(ActivityOrder activityOrder);//初始化用户

    @Insert("insert into activity_smsLog(address,userId,code,message,apiCode,appCode,actId,createTime)values(#{address},#{userId},#{code},#{message},#{apiCode},#{appCode},#{actId},now())")
    int insertSMSLog(OpenapiLog log);//添加用户业务办理记录

    @Insert("insert into ${keyword}(actId,userId,secToken,channelId,createTime)values(#{log.actId},#{log.userId},#{log.secToken},#{log.channelId},now())")
    int insertActivityShare(ActivityShare log, @Param("keyword") String keyword);//添加用户操作记录

    @Select("select * from ${keyword} where (`status` !=3 or status is null) and (message not like '%地市%' or message is null) and createTime < #{createTime} and createTime>'2024-05-26 00:00:00' and typeId = 0")
    List<ActivityUserHistory> selectActivityUserHistory(@Param("createTime") String createTime, @Param("keyword") String keyword);

}
