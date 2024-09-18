/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.ActivityUser;
import com.richeninfo.entity.mapper.entity.ActivityUserHistory;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/5/10 17:47
 */
@Repository
@Mapper
public interface FoodieMapper {


    @Select("select * from wt_meliorist_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_meliorist_user(userId,channelId,secToken,createDate,createTime,actId,belongFlag,userType)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{belongFlag},#{userType})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_meliorist_history where typeId=0  and userId !=#{userId}")
    List<ActivityUserHistory> selectUserListAll(@Param("userId") String userId);//查找提交用户集合

    @Select("select * from wt_meliorist_history where typeId=0  and userId !=#{userId} order by createTime desc limit #{pageBegin},#{pageSize}")
    List<ActivityUserHistory> selectUserList(@Param("userId") String userId,@Param("pageBegin")int pageBegin,@Param("pageSize")int pageSize);//查找提交用户集合

    @Select("select * from wt_meliorist_history where userId=#{userId} and typeId=0 order by createTime desc limit #{pageBegin},#{pageSize}")
    List<ActivityUserHistory> selectUserListByUserId(@Param("userId") String userId,@Param("pageBegin")int pageBegin,@Param("pageSize")int pageSize);//查找提交用户集合

    @Select("select * from wt_meliorist_history where typeId=2 and ip=#{ip} order by createTime desc limit #{pageBegin},#{pageSize}")
    List<ActivityUserHistory> selectUserListByTypeId(@Param("ip") String ip,@Param("pageBegin")int pageBegin,@Param("pageSize")int pageSize);//查找提交用户集合

    @Select("select * from wt_meliorist_history where typeId=#{typeId} and userId = #{userId} and ip=#{ip}")
    ActivityUserHistory selectActivity(@Param("userId") String userId,@Param("ip") String ip,@Param("typeId")int typeId);

    @Select("select * from wt_meliorist_history where userId = #{userId} and unlocked =#{unlocked}")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Insert("insert into wt_meliorist_history(userId,typeId,ip,code,message,remark,channelId,createDate,createTime,actId)values(#{userId},#{typeId},#{ip},#{code},#{message},#{remark},#{channelId},#{createDate},#{createTime},#{actId})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_meliorist_history set module= module+1 where id = #{id}")
    int updateHistoryModule(int id);

    @Update("update wt_meliorist_history set unlocked= unlocked+1 where id = #{id}")
    int updateHistoryUnlocked(int id);


}
