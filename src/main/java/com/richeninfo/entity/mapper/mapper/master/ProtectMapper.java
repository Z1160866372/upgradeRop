/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.ActivityShare;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import com.richeninfo.entity.mapper.entity.OperationLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author : zhouxiaohu
 * @create 2024/5/10 17:47
 */
@Repository
@Mapper
public interface ProtectMapper {


    @Select("select * from wt_trade_user where userId = #{userId} and actId=#{actId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId,@Param("actId") String actId);//查找用户记录

    @Insert("insert into wt_trade_user(userId,channelId,secToken,createDate,createTime,actId,belongFlag,answerTitle,userType)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{belongFlag},#{answerTitle},#{userType})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_trade_history where userId = #{userId} and unlocked =#{unlocked} and actId=#{actId}")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked,@Param("actId") String actId);

    @Insert("insert into wt_trade_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,activityId)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{activityId})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_trade_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Update("update activity_configuration set Amount= Amount-1 where id = #{id} and Amount > 0")
    int updateActivityConfigurationAmount(int id);

}
