/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/4/25 13:29
 */
@Mapper
public interface FortuneMapper {

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from wt_fortune_user where userId = #{userId} ")
    ActivityUser findUserInfo(@Param("userId") String userId);

    /**
     * 初始化用户信息
     * @param user
     */
    @Insert("INSERT INTO wt_fortune_user (userId,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken) value (#{userId},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken})")
    void saveUser(ActivityUser user);

    /**
     * 查询是否有当月获取记录
     * @param userId
     * @return
     */
    @Select("select * from wt_fortune_history where userId = #{userId}")
    ActivityUserHistory findCurMonthHistory(@Param("userId") String userId);

    /**
     * 查询奖品
     * @param unlocked
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where unlocked=#{unlocked} and actId=#{actId} limit 1")
    ActivityConfiguration findGiftByUnlocked(int unlocked, String actId);

    /**
     * 查询奖品
     * @param actId
     * @return
     */
    @Select("select * from activity_configuration where   actId=#{actId}")
    List<ActivityConfiguration> findGiftList(String actId);

    /**
     * 更新用户secToken
     * @param userId
     * @param secToken
     * @return
     */
    @Update("update  wt_fortune_user set secToken=#{secToken}  where userId=#{userId} ")
    int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);


    /**
     * 新增用户获取奖品
     * @param history
     * @return
     */
    @Insert("INSERT INTO wt_fortune_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark})")
    int saveHistory(ActivityUserHistory history);

    /**
     * 更新用户award
     * @param userId
     * @return
     */
    @Update("update  wt_fortune_user set award=award+1 where userId=#{userId} ")
    int updateUserAward(@Param("userId") String userId);

    @Update("update  wt_fortune_user set playNum=playNum-1 where userId=#{userId} ")
    int LostUserPlayNum(@Param("userId") String userId);

}
