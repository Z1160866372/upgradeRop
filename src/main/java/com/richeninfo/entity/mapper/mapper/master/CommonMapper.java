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

    @Select("select * from activity_list")
    List<ActivityList> selectActivityList();//查询活动列表

    @Select("select * from activity_list where actId = #{actId}")
    ActivityList selectActivityByActId(String actId);//查询活动信息

    @Insert("insert into activity_openapiLog(address,userId,code,message,apiCode,appCode,actId,createTime)values(#{address},#{userId},#{code},#{message},#{apiCode},#{appCode},#{actId},now())")
    int insertOpenapiLog(OpenapiLog log);//添加用户业务办理记录

    @Insert("insert into activity_operationLog(actId,name,address,userId,instructions,createTime)values(#{actId},#{name},#{address},#{userId},#{instructions},now())")
    int insertOperationLog(OperationLog log);//添加用户操作记录

    @Select("select * from activity_configuration where actId = #{actId}")
    List<ActivityConfiguration> selectActivityConfigurationByActId(String actId);//查询活动奖励配置

    @Select("select * from activity_configuration where actId = #{actId} and module=#{module}")
    List<ActivityConfiguration> selectActivityConfigurationByActIdAndModule(@Param("actId") String actId, @Param("module") Integer module);//查询活动奖励配置

    @Update("update activity_configuration set AllAmount= AllAmount-1 where id = #{id} and AllAmount > 0")
    int updateAmount(int id);

    @Select("select * from activity_configuration where actId = #{actId} and unlocked = #{unlocked}")
    ActivityConfiguration selectActivitySomeConfiguration(@Param("actId") String actId, @Param("unlocked") Integer unlocked);//查询某个活动某个奖励配置

    @Insert("insert into activity_share")
    int insertShareUser(ActivityShare shareUser);//保存用户分享记录
}
