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
public interface GsmShareMapper {

   /**
    * 查询用户信息
    * @param userId
    * @return
    */
   @Select("select * from wt_gsmshare_user where userId = #{userId} ")
   ActivityUser findUserInfo(@Param("userId") String userId);

   /**
    * 初始化用户信息
    * @param user
    */
   @Insert("INSERT INTO wt_gsmshare_user (userId,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken,ditch,userType,unlocked) value (#{userId},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken},#{ditch},#{userType},#{unlocked})")
   void saveUser(ActivityUser user);

   /**
    * 查询是否有当月获取记录
    * @param userId
    * @return
    */
   @Select("select * from wt_gsmshare_history where userId = #{userId} and unlocked=6 and status=3 ")
   ActivityUserHistory findCurYwHistory(@Param("userId") String userId);


    /**
     * 查询是否有当月获取记录
     * @param userId
     * @return
     */
    @Select("select * from wt_gsmshare_history where userId = #{userId} and unlocked=6 ")
    ActivityUserHistory findSaveYwHistory(@Param("userId") String userId);

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
   @Update("update  wt_gsmshare_user set secToken=#{secToken}  where userId=#{userId} ")
   int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);


   /**
    * 更新用户secToken
    * @param userId
    * @return
    */
   @Update("update  wt_gsmshare_user set playNum=playNum-1  where userId=#{userId} ")
   int lostPlayNum(@Param("userId") String userId);
   /**
    * 新增用户获取奖品
    * @param history
    * @return
    */
   @Insert("INSERT INTO wt_gsmshare_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark,activityId,itemId,ditch) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark},#{activityId},#{itemId},#{ditch})")
   int saveHistory(ActivityUserHistory history);

   /**
    * 更新用户award
    * @param userId
    * @return
    */
   @Update("update  wt_gsmshare_user set award=1 where userId=#{userId} ")
   int updateUserAward(@Param("userId") String userId);

   /**
    * 多媒体展示
    * @param actId
    * @return
    */
   @Select("select * from activity_configuration where typeId=5 and actId=#{actId}")
   List<ActivityConfiguration> findGiftListByTypeId(String actId);


   /**
    * 查询活动用户白名单
    * @param actId
    * @return
    */
   @Select("select * from wt_gsmshare_roster where userId=#{userId} ")
   ActivityRoster findActWhiteList(String actId);





   /**
    * 多个奖品查询
    * @param actId
    * @return
    */
   @Select("select * from activity_configuration where actId=#{actId}")
  List<ActivityConfiguration>  findGiftListByActId(@Param("actId") String actId);

   @Select("select * from wt_gsmshare_bind where typeId=#{typeId} and userId is null and status=0")
   List<ActivityCardList>  findBindUserIdIsNullByTypeId(@Param("typeId") int typeId);

   @Update("update activity_configuration set AllAmount=AllAmount-1 where id=#{id}")
   int lostGiftAllAmount(int id);

   @Update("update activity_configuration set amount=amount-1 where id=#{id}")
   int lostGiftAmount(int id);

   @Update("update wt_gsmshare_bind set userId=#{userId},status=1 where id=#{id}")
   int updateBindUserId(@Param("userId") String userId,@Param("id")int id);
    @Update("update wt_gsmshare_user set nickName=#{nickName} where userId=#{userId}")
    int updateUserNickName(@Param("nickName") String nickName,@Param("userId")String userId);

    @Update("update wt_gsmshare_user set mark=#{mark} where userId=#{userId}")
    int updateCurMark(@Param("mark") String nickName,@Param("userId")String userId);


    @Update("update wt_gsmshare_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Update(" update  wt_gsmshare_user set unlocked=1 where userId=#{userId} and unlocked =0")
    int updateUnlocked(String userId);






}
