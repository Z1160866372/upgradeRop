/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityRoster;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/4/25 13:29
 */
@Mapper
public interface PlentifulMapper {

    @Select("select * from wt_feedback_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_feedback_user(userId,channelId,secToken,createDate,createTime,actId,ditch,userType,playNum,mark)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{ditch},#{userType},#{playNum},#{mark})")
    int insertUser(ActivityUser user);//初始化用户

    @Update("update wt_feedback_user set playNum=playNum-1,answerNum=answerNum+1 where id=#{id} and playNum>0")
    int updateUser(ActivityUser user);//更新接口状态

    @Select("select * from wt_feedback_history where userId = #{userId} and unlocked =#{unlocked} and module=1")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Select("select * from wt_feedback_history where userId = #{userId}  and module=#{module}")
    ActivityUserHistory selectActivityUserHistoryByUserType(@Param("userId")String userId,  @Param("module")int module);

    @Select("select * from wt_feedback_history where userId = #{userId}")
    List<ActivityUserHistory> selectActivityUserHistoryList(@Param("userId")String userId,@Param("actId") String actId);

    @Select("select * from wt_feedback_history order by createTime desc limit 20")
    List<ActivityUserHistory> selectActivityHistoryList();

    @Select("select * FROM activity_configuration WHERE actId='term_title' and  startTime < NOW() and  endTime >NOW()")
    List<ActivityConfiguration> selectActivityConfigurationTitle();

    @Insert("insert into wt_feedback_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch,activityId,itemId,module,remark,winSrc,imgSrc,ipScanner,ip,userType)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch},#{activityId},#{itemId},#{module},#{remark},#{winSrc},#{imgSrc},#{ipScanner},#{ip},#{userType})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_feedback_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and userType=#{userType} and module=#{module}")
    List<ActivityConfiguration> selectActivityConfigurationList(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("userType") Integer userType,@Param("module") Integer module);

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and module=#{module}")
    ActivityConfiguration selectActivityConfigurationByModule(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("module") Integer module);

    @Select("select * from ${keyword} where userId=#{userId} and actId=#{actId} order by userType ")
    List<ActivityRoster> selectRoster(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户名单列表


}
