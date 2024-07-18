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
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface HuiPlayMapper {

    @Select("select * from wt_huiplay_user where userId = #{userId}")
    ActivityUser selectUserByCreateDate(@Param("userId") String userId);//查找用户记录

    @Insert("insert into wt_huiplay_user(userId,channelId,secToken,createDate,createTime,actId,ditch)values(#{userId},#{channelId},#{secToken},#{createDate},now(),#{actId},#{ditch})")
    int insertUser(ActivityUser user);//初始化用户

    @Select("select * from wt_huiplay_history where userId = #{userId} and unlocked =#{unlocked} and module=1")
    ActivityUserHistory selectActivityUserHistoryByUnlocked(@Param("userId")String userId, @Param("unlocked")int unlocked);

    @Select("select * from wt_huiplay_history where userId = #{userId} and module=1")
    List<ActivityUserHistory> selectActivityUserHistoryList(@Param("userId")String userId,@Param("actId") String actId);

    @Insert("insert into wt_huiplay_history(userId,unlocked,typeId,rewardName,value,channelId,createDate,createTime,actId,ditch,activityId,itemId,module,remark,winSrc,imgSrc,ipScanner)values(#{userId},#{unlocked},#{typeId},#{rewardName},#{value},#{channelId},#{createDate},#{createTime},#{actId},#{ditch},#{activityId},#{itemId},#{module},#{remark},#{winSrc},#{imgSrc},#{ipScanner})")
    void insertActivityUserHistory(ActivityUserHistory activityUserHistory);

    @Update("update wt_huiplay_history set status=#{status},code=#{code},message=#{message} where id=#{id}")
    int updateHistory(ActivityUserHistory history);//更新接口状态

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and userType=#{userType}")
    List<ActivityConfiguration> selectActivityConfigurationList(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("userType") Integer userType);

    @Select("select * from activity_configuration where actId = #{actId} and unlocked=#{unlocked} and module=#{module}")
    ActivityConfiguration selectActivityConfigurationByModule(@Param("actId") String actId, @Param("unlocked") Integer unlocked,@Param("module") Integer module);
}
