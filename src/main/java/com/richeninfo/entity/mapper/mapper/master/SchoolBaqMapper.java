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
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/5/10 17:47
 */
@Repository
@Mapper
public interface SchoolBaqMapper {


    @Select("select * from wt_schoolBaq_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Select("select * from wt_schoolBaq_roster where userId=#{userId}")
    List<ActivityRoster> selectRoster(@Param("userId") String userId);//查询用户名单列表

    @Insert("insert into wt_schoolBaq_user(userId,channelId,secToken,createDate,createTime,actId,userType,ditch)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{userType},#{ditch})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_schoolBaq_history where userId = #{userId} and unlocked =#{unlocked}")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);
    @Select("select * from wt_schoolBaq_history where userId = #{userId} and unlocked =#{unlocked} and status=3")
    ActivityUserHistory selectActivityUserHistoryByUnlockedAnStatus(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Insert("insert into wt_schoolBaq_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Insert("insert into wt_schoolBaq_operationLog(actId,name,address,userId,instructions,createTime)values(#{actId},#{name},#{address},#{userId},#{instructions},now())")
    int insertOperationLog(OperationLog log);//添加用户操作记录

    @Insert("insert into wt_schoolBaq_share(actId,channelId,userId,createTime)values(#{actId},#{channelId},#{userId},now())")
    int insertShareUser(ActivityShare shareUser);//保存用户分享记录

    @Update("update wt_schoolBaq_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Update("update wt_schoolBaq_user set level=1 where id=#{id}")
    int updateActivityUser(ActivityUser user);//更新接口状态


}
