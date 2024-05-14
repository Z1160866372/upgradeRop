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
 * @create 2024/4/30 14:42
 */
@Repository
@Mapper
public interface FinanceMapper {


    @Select("select * from wt_finance_user where userId = #{userId} and actId =#{actId} and createDate=#{createDate}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId, @Param("actId") String actId, @Param("createDate") String createDate);//查找用户记录

    @Insert("insert into wt_finance_user(userId,channelId,secToken,createDate,createTime,actId)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_finance_history where userId = #{userId} and unlocked =#{unlocked} and createDate =#{createDate}")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId,@Param("unlocked")int unlocked,@Param("createDate")String createDate);

    @Select("select * from wt_finance_history where userId = #{userId} and typeId =#{typeId} and createDate =#{createDate}")
    ActivityUserHistory selectActivityUserHistoryByTypeId(@Param("userId")String userId,@Param("typeId")int typeId,@Param("createDate")String createDate);

    @Select("select * from wt_finance_history where userId = #{userId} and actId =#{actId} and createDate =#{createDate}")
    List<ActivityUserHistory> selectHistory(@Param("userId") String userId, @Param("actId") String actId,@Param("createDate")String createDate);//查询用户领取记录

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked}")
    ActivityConfiguration selectActivityConfigurationByUnlocked(@Param("actId")String actId,@Param("unlocked")int unlocked);

    @Update("update activity_configuration set Amount= Amount-1 where id = #{id} and Amount > 0")
    int updateActivityConfigurationAmount(int id);

    @Insert("insert into wt_finance_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Select("select * from activity_card where actId = #{actId} and unlocked =#{unlocked} and createDate =#{createDate}")
    List<ActivityCardList> selectActivityCardList(@Param("actId")String actId,@Param("unlocked")int unlocked,@Param("createDate")String createDate);

    @Select("select * from activity_card where actId = #{actId} and unlocked =#{unlocked} and createDate =#{createDate} limit 1")
    ActivityCardList selectActivityCardListByUnlocked(@Param("actId")String actId,@Param("unlocked")int unlocked,@Param("createDate")String createDate);

    @Update("update activity_card set userId= #{userId},status=1 where id = #{id} and status = 0")
    int updateActivityCardList(@Param("userId")String userId,@Param("id")int id);

    @Insert("insert into wt_finance_operationLog(actId,name,address,userId,instructions,createTime)values(#{actId},#{name},#{address},#{userId},#{instructions},now())")
    int insertOperationLog(OperationLog log);//添加用户操作记录

}
