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

    @Select("select * from activity_roll where userId=#{userId} and user_type=#{user_type}")
    ActivityRoll selectRoll(@Param("userId") String userId, @Param("user_type") Integer user_type);//查询wap｜测试白名单

    @Select("select * from activity_list where actId = #{actId}")
    ActivityList selectActivityByActId(String actId);//查询活动信息

    @Select("select * from activity_configuration where actId = #{actId}")
    List<ActivityConfiguration> selectActivityConfigurationByActId(String actId);//查询活动奖励配置

    @Select("select * from activity_configuration where actId = #{actId} and module=#{module}")
    List<ActivityConfiguration> selectActivityConfigurationByActIdAndModule(@Param("actId") String actId, @Param("module") Integer module);//查询活动奖励配置

    @Update("update activity_configuration set AllAmount= AllAmount-1 where id = #{id} and AllAmount > 0")
    int updateAmount(int id);//更新奖励数量

    @Select("select * from wt_#{keyword}_user where userId = #{userId} and actId =#{actId}")
    ActivityUser selectUser(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查找用户记录

    @Insert("insert into wt_#{keyword}_user(userId,belongFlag,userType,channelId,secToken,createDate,createTime,actId)values(#{userId},#{belongFlag},#{userType},#{channelId},#{secToken},curdate(),now(),#{actId})")
    int insertUser(ActivityUser user, @Param("keyword") String keyword);//初始化用户

    @Select("select * from activity_configuration where actId = #{actId} and unlocked = #{unlocked}")
    ActivityConfiguration selectActivitySomeConfiguration(@Param("actId") String actId, @Param("unlocked") Integer unlocked);//查询某个活动某个奖励配置

    @Select("select * from wt_#{keyword}_user_history where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId}")
    ActivityUserHistory selectHistoryByUnlocked(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户当前奖励是否已领取

    @Update("update wt_#{keyword}_user_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Select("select * from wt_#{keyword}_user_history where userId = #{userId} and actId =#{actId}")
    List<ActivityUserHistory> selectHistory(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户领取记录

    @Insert("insert into wt_#{keyword}_user_share")
    int insertShareUser(ActivityShare shareUser, @Param("keyword") String keyword);//保存用户分享记录

    @Insert("insert into activity_openapiLog(address,userId,code,message,apiCode,appCode,actId,createTime)values(#{address},#{userId},#{code},#{message},#{apiCode},#{appCode},#{actId},now())")
    int insertOpenapiLog(OpenapiLog log);//添加用户业务办理记录

    @Insert("insert into wt_#{keyword}_operationLog(actId,name,address,userId,instructions,createTime)values(#{actId},#{name},#{address},#{userId},#{instructions},now())")
    int insertOperationLog(OperationLog log, @Param("keyword") String keyword);//添加用户操作记录

    @Select("select * from wt_#{keyword}_roster where userId=#{userId} and actId=#{actId}")
    List<ActivityRoster> selectRoster(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户名单列表
}
