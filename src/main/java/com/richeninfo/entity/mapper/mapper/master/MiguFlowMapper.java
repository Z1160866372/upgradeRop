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
 * @date 2024/4/26 11:20
 */
@Mapper
public interface MiguFlowMapper {

   @Select("select * from wt_luckRotary_user where userId = #{userId}")
   ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

   @Insert("insert into wt_luckRotary_user(userId,channelId,secToken,userType,createDate,createTime,actId,ditch)values(#{userId},#{channelId},#{secToken},#{userType},#{createDate},now(),#{actId},#{ditch})")
   int insertUser(ActivityUser user);//初始化用户

   @Select("select * from wt_luckRotary_history where userId = #{userId} and unlocked =#{unlocked} and module=0")
   ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

   @Select("select * from wt_luckRotary_history where userId = #{userId} and module=0")
   List<ActivityUserHistory> selectActivityUserHistoryList(@Param("userId")String userId,@Param("actId") String actId);

   @Select("select * FROM activity_configuration WHERE actId='term_title' and  startTime < NOW() and  endTime >NOW()")
   List<ActivityConfiguration> selectActivityConfigurationTitle();

   @Insert("insert into wt_luckRotary_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch,activityId,itemId,module,remark,winSrc,imgSrc,ipScanner)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch},#{activityId},#{itemId},#{module},#{remark},#{winSrc},#{imgSrc},#{ipScanner})")
   void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

   @Update("update wt_luckRotary_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
   int updateHistory(ActivityUserHistory history);//更新接口状态

   @Update("update wt_luckRotary_history set remark=#{remark} where id=#{id}")
   int updateHistoryRemark(ActivityUserHistory history);//更新卡券信息

   @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and userType=#{userType}")
   List<ActivityConfiguration> selectActivityConfigurationList(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("userType") Integer userType);

   @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and module=#{module}")
   ActivityConfiguration selectActivityConfigurationByModule(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("module") Integer module);

   @Select("select * from ${keyword} where userId=#{userId} and actId=#{actId}")
   List<ActivityRoster> selectRoster(@Param("userId") String userId, @Param("actId") String actId, @Param("keyword") String keyword);//查询用户名单列表

}
