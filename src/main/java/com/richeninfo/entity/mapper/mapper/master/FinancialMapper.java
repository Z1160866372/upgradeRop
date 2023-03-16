package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2023/2/21 15:03
 */
@Repository
@Mapper
public interface FinancialMapper {

    @Select("select * from jr_2023_02_financial where userId=#{userId}")
    ActivityUser selectUser(@Param("userId")String userId);//查找用户

    @Insert("insert into jr_2023_02_financial(userId,belongFlag,userType,createDate,createTime,actId)values(#{userId},#{belongFlag},#{userType},curdate(),now(),#{actId})")
    int insertUser(ActivityUser user);//初始化用户

    @Insert("insert into jr_2023_02_financial_history(userId,ip,unlocked,rewardName,createDate,createTime,actId)values(#{userId},#{ip},#{unlocked},#{rewardName},curdate(),now(),#{actId})")
    int insertUserHistory(ActivityUserHistory history);//记录用户抽奖记录

    @Select("select * from jr_2023_02_financial_history where userId=#{userId}")
    ActivityUserHistory selectHistoryByUserId(@Param("userId")String userId);

    @Update("update jr_2023_02_financial set claimStatus=1 where claimStatus=0 and id=#{id}")
    int updateClaimStatus(int id);//更新奖励领取状态

    @Update("update jr_2023_02_financial set note=#{note},unlocked=#{unlocked} where  id=#{id}")
    int updateUserInfo(ActivityUser user);//更新奖励领取状态

   /* @Select("select * from jr_2023_02_financial_configuration")
    List<ActivityConfiguration> selectConfiguration();//查找配置奖励

    @Update("update jr_2023_02_financial_configuration set amount=amount-1 where amount>0")
    int updateAmount(int id);//更新奖品数量
*/
    @Select("select * from jr_2023_02_financial_card where unlocked=#{unlocked} and status=0")
    List<ActivityCardList> selectCardList(@Param("unlocked")int unlocked);//查询未分配券码

    @Select("select * from jr_2023_02_financial_card where ipScanner=#{ipScanner} and createDate=#{createDate}")
    List<ActivityCardList> selectCardListByIp(@Param("ipScanner")String ipScanner,@Param("createDate")String createDate);//查询当天IP段获取奖励

    @Select("select * from jr_2023_02_financial_card where userId=#{userId}")
    ActivityCardList selectCardByUserId(@Param("userId")String userId);

    @Select("select * from jr_2023_02_financial_card where unlocked=#{unlocked} and status=0 limit 1")
    ActivityCardList selectCard(@Param("unlocked")int unlocked);//查询未分配券码 status=0

    @Update("update jr_2023_02_financial_card set userId=#{userId},ip=#{ip},ipScanner=#{ipScanner},status=1,createDate=curdate(),createDateTime=now() where id=#{id} and status=0")
    int updateCardStatus(@Param("userId")String userId,@Param("ip")String ip,@Param("ipScanner")String ipScanner,@Param("id")int id);//用户绑定卡券 status=1 未发放短信

    @Select("select * from jr_2023_02_financial_card where status=1 and createDate=#{createDate}")
    List<ActivityCardList> selectCardListByStatus(@Param("createDate")String createDate);

    @Update("update jr_2023_02_financial_card set status=2 where id=#{id} and status=1")
    int updateCardCodeStatus(@Param("id")int id);//status=2 已发放短信

    @Select("select * from jr_2023_02_financial_roster where userId=#{userId}")
    List<ActivityRoster> selectRoster(String userId);//查询用户名单列表

}
