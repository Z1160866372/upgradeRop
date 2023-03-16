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

    @Select("select * from wt_2022_11_proMember where userId = #{userId} and actId =#{actId}")
    ActivityUser selectUser(@Param("userId") String userId, @Param("actId") String actId);//查找用户记录

    @Insert("insert into wt_2022_11_proMember(userId,belongFlag,userType,channelId,secToken,createDate,createTime,actId)values(#{userId},#{belongFlag},#{userType},#{channelId},#{secToken},curdate(),now(),#{actId})")
    int insertUser(ActivityUser user);//初始化用户

    @Insert("insert into wt_2022_11_proMember_history(userId,belongFlag,userType,rewardName,unlocked,channelId,secToken,createDate,createTime,actId)values(#{userId},#{belongFlag},#{userType},#{rewardName},#{unlocked},#{channelId},#{secToken},curdate(),now(),#{actId})")
    int insertUserHistory(ActivityUserHistory history);//保存用户记录

    @Select("select * from wt_2022_11_proMember_history where userId = #{userId} and actId =#{actId}")
    List<ActivityUserHistory> selectHistory(@Param("userId") String userId, @Param("actId") String actId);//查询用户领取记录

    @Select("select * from wt_2022_11_proMember_history where userId = #{userId} and unlocked =#{unlocked} and actId =#{actId}")
    ActivityUserHistory selectHistoryByUnlocked(@Param("userId") String userId, @Param("unlocked") int unlocked, @Param("actId") String actId);//查询用户当前奖励是否已领取

    @Update("update wt_2022_11_proMember_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Update("update wt_2022_11_proMember set userType = 1 where id = #{id}")
    int updateUser_type(int id);//更新用户标识(PRO会员标识 1_yes;0_no)

    @Select("select * from wt_2022_11_proMember_roster")
    List<ActivityRoster> selectRoster(String userId);//查询用户名单列表
}
