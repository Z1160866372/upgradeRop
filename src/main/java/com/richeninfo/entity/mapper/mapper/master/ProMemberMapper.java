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


    @Select("select * from wt_proMember_user where userId = #{userId} and actId =#{actId}")
    ActivityUser selectUser(@Param("userId") String userId, @Param("actId") String actId);//查找用户记录

    @Insert("insert into wt_proMember_user_history(userId,belongFlag,userType,rewardName,unlocked,channelId,secToken,createDate,createTime,actId,activityId,itemId)values(#{userId},#{belongFlag},#{userType},#{rewardName},#{unlocked},#{channelId},#{secToken},curdate(),now(),#{actId},#{activityId},#{itemId})")
    int insertUserHistory(ActivityUserHistory history);//保存用户记录

    @Select("select * from wt_proMember_user_history where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId}")
    ActivityUserHistory selectHistoryByUnlocked(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId);//查询用户当前奖励是否已领取

    @Update("update wt_proMember_user set userType = 1 where id = #{id}")
    int updateUser_type(int id);//更新用户标识(PRO会员标识 1_yes;0_no)

}
