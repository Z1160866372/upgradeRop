/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @auth sunxiaolei
 * @date 2024/3/22 16:15
 */
@Mapper
public interface ExteroceptiveMapper {

    @Select("select * from wt_proem_user where userId = #{userId}")
    ActivityUser findUserInfoByUserId(String userId);
    @Select("select * from wt_proem_user where secToken = #{secToken}")
    ActivityUser findUserInfoByUserSecToken(String secToken);

    @Select("select * from wt_experience_user where userId = #{userId}")
    ActivityUser findOldUserInfoByUserId(String userId);

    @Insert("INSERT INTO wt_proem_record (userId,status,caozuo,typeId,channel_id,createTime,createDate) value (#{userId},#{status},#{caozuo},#{typeId},#{channel_id},now(),curdate())")
    void saveUserRecord(ActivityRecord record);

    @Insert("INSERT INTO wt_proem_user (userId,level,award,playNum,channelId,grade,answerNum,mark,blowNum,weekTime,createTime,createDate,secToken) value (#{userId},#{level},#{award},#{playNum},#{channelId},#{grade},#{answerNum},#{mark},#{blowNum},now(),now(),curdate(),#{secToken})")
    void saveUser(ActivityUser user);
    @Select(" SELECT * FROM `wt_proem_answer` where answerType >0 ORDER BY RAND() LIMIT 1")
    ActivityAnswer findRandAnswerOne();

    @Select("select * from wt_proem_answer where answerType=0")
    ActivityAnswer findFirstAnswer();

    @Select("select * from wt_proem_answer where answerType=#{answerType}")
    ActivityAnswer findAnswerByType(int answerType);

    @Select("select * from wt_proem_history where userId=#{userId}")
    List<ActivityUserHistory> findUserRecived(String userId);

    @Select("select * from wt_proem_history where id=#{id}")
    ActivityUserHistory findHistoryById(int id);

    @Update("update  wt_proem_user set answerNum=answerNum-1  where userId=#{userId}  and answerNum >0")
    int lostAnswerNum(String userId);

    @Update("update  wt_proem_user set mark=#{mark} where userId=#{userId}")
    int addUserMark(@Param("userId") String userId, @Param("mark") int mark);

    @Update("update  wt_proem_user set answer=#{answer},answerTitle=#{answerTitle} where userId=#{userId}")
    int updateAnswer(@Param("userId") String userId, @Param("answer") String answer, @Param("answerTitle") String answerTitle);

    @Update("update  wt_proem_user set blowNum=blowNum-1  where userId=#{userId}  and blowNum >0")
    int lostBlowNum(@Param("userId") String userId);

    @Select("select * from wt_proem_record where userId=#{userId} ORDER BY createTime desc")
    List<ActivityRecord> findUserRecord(String userId);

    @Update("update  wt_proem_user set grade=#{grade}  where userId=#{userId}  and grade <#{grade}")
    void updateUserGrade(@Param("userId") String userId, @Param("grade") int grade);

    @Update("update  wt_proem_user set playNum=playNum-1  where id=#{id} and playNum >0")
    int LostPlayNum(int id);

    @Update("update  wt_proem_user set award=award+1 where userId=#{userId} and award=0")
    int updateUserAward(String userId);

    @Select("select * from activity_configuration where unlocked=#{unlocked} and actId=#{actId} limit 1")
    ActivityConfiguration findGiftByTypeId(int unlocked, String actId);


    @Select(" select * from activity_configuration where module=#{module} and actId=#{actId}")
    List<ActivityConfiguration> findGiftListByObtain(int module, String actId);

    @Select(" select * from activity_configuration where unlocked >0 and value >0 or(value =0 and module =#{month} and actId=#{actId} )")
    List<ActivityConfiguration> findGiftList(int month, String actId);


    @Select(" select * from activity_configuration where unlocked=#{typeId} and   value=#{month}")
    ActivityConfiguration findExperienceGiftList(@Param("typeId") int typeId, @Param("month") String month);//查询奖励配置数量

    @Update("update  activity_configuration set amount=amount-1  where unlocked=#{unlocked} and   value=#{month} and amount >0")
    int lostGiftListCount(@Param("unlocked") int unlocked, @Param("month") String month);

    @Insert("INSERT INTO wt_proem_history (userId,rewardName,typeId,unlocked,belongFlag,status,code,message,secToken,channelId,createTime,createDate,actId,winSrc,remark) value (#{userId},#{rewardName},#{typeId},#{unlocked},#{belongFlag},#{status},#{code},#{message},#{secToken},#{channelId},now(),curdate(),#{actId},#{winSrc},#{remark})")
    int saveHistory(ActivityUserHistory history);

    @Insert(" INSERT INTO wt_proem_access (userId,status,caozuo,typeId,channel_id,createTime,createDate) value (#{userId},#{status},#{caozuo},#{typeId},#{channel_id},now(),curdate())")
    int saveUserAccess(ActivityRecord ActivityRecord);

    @Insert(" INSERT INTO wt_proem_share_history (userId,channelId,actId,secToken,createTime,createDate) value (#{userId},#{channel_id},#{actId},#{secToken},now(),curdate())")
    void saveShareHistory(ActivityShare share);

    @Update("update  wt_proem_user set answerNum=1 , answer=0,blowNum=1,createTime=now()  where userId=#{userId} ")
    int updateUserCurInfo(@Param("userId") String userId);

    @Update("update wt_proem_history set status=#{status},code=#{code},message=#{message},couponCode=#{couponCode} where id = #{id}")
    int updateHistory(ActivityUserHistory history);

    @Update("update  wt_proem_user set secToken=#{secToken}  where userId=#{userId} ")
    int updateUserSecToken(@Param("userId") String userId, @Param("secToken") String secToken);

    @Update("update  wt_proem_user set playNum=#{playNum} ,weekTime=now()  where userId=#{userId} ")
    void updateUserBaoXiangPlayNum(@Param("userId") String userId, @Param("playNum") int playNum);

    @Select("select * from activity_list where actId=#{actId}")
    ActivityList selectTime(String actId);


}
