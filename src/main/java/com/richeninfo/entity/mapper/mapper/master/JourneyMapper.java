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
public interface JourneyMapper {

    @Select("select * from wt_journey_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_journey_user(userId,channelId,secToken,createDate,createTime,actId,ditch)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{ditch})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_journey_history where userId = #{userId} and unlocked =#{unlocked} and module=1")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Select("select * from wt_journey_history where userId = #{userId} and module=1")
    List<ActivityUserHistory> selectActivityUserHistoryList(@Param("userId")String userId,@Param("actId") String actId);

    @Insert("insert into wt_journey_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch,activityId,itemId,module,remark)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch},#{activityId},#{itemId},#{module},#{remark})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_journey_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and userType=#{userType}")
    List<ActivityConfiguration> selectActivityConfigurationList(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("userType") Integer userType);
}
