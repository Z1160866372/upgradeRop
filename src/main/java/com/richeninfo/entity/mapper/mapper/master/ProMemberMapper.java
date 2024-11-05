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
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2022/11/15 14:04
 */
@Repository
@Mapper
public interface ProMemberMapper {
    @Select("select * from wt_turntable_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_turntable_user(userId,channelId,secToken,createDate,createTime,actId,ditch,userType,playNum)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{ditch},#{userType},#{playNum})")
    int insertUser(ActivityUser user);//初始化用户

    @Update("update wt_turntable_user set playNum=playNum-1,answerNum=answerNum+1 where id=#{id} and playNum>0")
    int updateUser(ActivityUser user);//更新接口状态

    @Select("select * from wt_turntable_history where userId = #{userId} and unlocked =#{unlocked} and module=1")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Select("select * from wt_turntable_history where userId = #{userId} and userType =#{userType} and module=#{module}")
    ActivityUserHistory selectActivityUserHistoryByUserType(@Param("userId")String userId, @Param("userType")int userType, @Param("module")int module);

    @Select("select * from wt_turntable_history where userId = #{userId}")
    List<ActivityUserHistory> selectActivityUserHistoryList(@Param("userId")String userId,@Param("actId") String actId);

    @Select("select * from wt_turntable_history order by createTime desc limit 20")
    List<ActivityUserHistory> selectActivityHistoryList();

    @Select("select * FROM activity_configuration WHERE actId='term_title' and  startTime < NOW() and  endTime >NOW()")
    List<ActivityConfiguration> selectActivityConfigurationTitle();

    @Insert("insert into wt_turntable_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch,activityId,itemId,module,remark,winSrc,imgSrc,ipScanner,ip)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch},#{activityId},#{itemId},#{module},#{remark},#{winSrc},#{imgSrc},#{ipScanner},#{ip})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_turntable_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and userType=#{userType}")
    List<ActivityConfiguration> selectActivityConfigurationList(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("userType") Integer userType);

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and module=#{module}")
    ActivityConfiguration selectActivityConfigurationByModule(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("module") Integer module);

    @Select("select * from ${keyword} where userId=#{userId} and actId=#{actId} order by userType desc")
    List<ActivityRoster> selectRoster(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户名单列表

}
