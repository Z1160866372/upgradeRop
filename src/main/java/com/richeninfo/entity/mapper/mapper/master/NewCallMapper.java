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
public interface NewCallMapper {


    @Select("select * from wt_newCall_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_newCall_user(userId,channelId,secToken,createDate,createTime,actId)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_newCall_history where userId = #{userId} and unlocked =#{unlocked}")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Insert("insert into wt_newCall_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_newCall_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Insert("insert into wt_newCall_operationLog(actId,name,address,userId,instructions,createTime)values(#{actId},#{name},#{address},#{userId},#{instructions},now())")
    int insertOperationLog(OperationLog log);//添加用户操作记录

    @Insert("insert into wt_newCall_share(actId,channelId,userId,createTime)values(#{actId},#{channelId},#{userId},now())")
    int insertShareUser(ActivityShare shareUser);//保存用户分享记录
}
