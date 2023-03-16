package com.richeninfo.entity.mapper.mapper.cluster;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/13 15:07
 */
@Repository
@Mapper
public interface SmsMapper {

    @Insert("INSERT INTO SMSINFO (TYPE, JB, TJSJ, KHH, TEL, MSG, FSBZ, BY1) VALUES ('15', '1', #{timeStamp}, 'zgyzm', #{phone}, #{content}, '0', '12001')")
    void sendSms(String timeStamp, String phone, String content);

    @Insert("INSERT INTO SMSINFO (TYPE, JB, TJSJ, KHH, TEL, MSG, FSBZ, BY1) VALUES ('15', '1', #{timeStamp}, '资管营销短信', #{phone}, #{content}, '0', '12001')")
    void sendInviteMsg(String timeStamp, String phone, String content);
}
