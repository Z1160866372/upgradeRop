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
 * @date 2024/4/26 11:20
 */
@Mapper
public interface MiguDJMapper {

   /**
    * 查询用户信息
    * @param userId
    * @return
    */
   @Select("select * from wt_research_user where userId = #{userId} ")
   ActivityUser findCurMonthUserInfo(@Param("userId") String userId);

   /**
    * 初始化用户信息
    * @param user
    */
   @Insert("INSERT INTO wt_research_user (userId,nickName,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken,ditch,userType) value (#{userId},#{nickName},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken},#{ditch},#{userType})")
   void saveUser(ActivityUser user);

   /**
    * 查询是否有当月获取记录
    * @param userId
    * @return
    */
   @Select("select * from wt_research_history where userId = #{userId} and unlocked=0  ")
   ActivityUserHistory findCurYwHistory(@Param("userId") String userId);

   /**
    * 查询是否在参与白名单
    * @param userId
    * @return
    */
   @Select("select * from wt_research_roster where userId = #{userId} and userType=#{userType}  ")
   ActivityRoster findActivityRoster(@Param("userId") String userId,@Param("userType") int userType);

   /**
    * 查询奖品
    * @param unlocked
    * @param actId
    * @return
    */
   @Select("select * from activity_configuration where unlocked=#{unlocked} and actId=#{actId} limit 1")
   ActivityConfiguration findGiftByUnlocked(int unlocked, String actId);

   /**
    * 更新用户secToken
    * @param userId
    * @param secToken
    * @return
    */
   @Update("update  wt_research_user set secToken=#{secToken}  where userId=#{userId} ")
   int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);


   /**
    * 新增用户获取奖品
    * @param history
    * @return
    */
   @Insert("INSERT INTO wt_research_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark,activityId,itemId,ditch) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark},#{activityId},#{itemId},#{ditch})")
   int saveHistory(ActivityUserHistory history);

   /**
    * 更新用户award
    * @param userId
    * @return
    */
   @Update("update  wt_research_user set award=1 where userId=#{userId} ")
   int updateUserAward(@Param("userId") String userId);

   /**
    * 更新用户答题状态
    * @param userId
    * @return
    */
   @Update("update  wt_research_user set answer=#{answer} ,mark=#{mark} where userId=#{userId}")
   int updateUserMarkAndAnswer(@Param("answer") String answer,@Param("mark") int mark,@Param("userId") String userId );

   /**
    * 多媒体展示
    * @param actId
    * @return
    */
   @Select("select * from activity_configuration where typeId=5 and actId=#{actId}")
   List<ActivityConfiguration> findGiftByTypeId(String actId);

   @Update("update wt_research_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
   int updateHistory(ActivityUserHistory history);//更新接口状态

   @Select("SELECT `userId` FROM  `wt_flowmigu_black` WHERE userId=#{userId} ")
   List<String > findIsBlack(String userId);
}
