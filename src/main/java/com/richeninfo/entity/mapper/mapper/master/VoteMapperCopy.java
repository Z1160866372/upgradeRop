package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author : zhouxiaohu
 * @create 2023/3/17 13:30
 */
@Repository
@Mapper
public interface VoteMapperCopy {
    /*//部门管理
    @Insert("insert into 202303_wt_vote_department (name,createdDate)values(#{name},#{createdDate})")
    int insertVoteDepartment(VoteDepartment voteDepartment);
    @Delete("<script> delete from 202303_wt_vote_department where did in <foreach collection='ids' item='did' open='(' separator=',' close=')'>#{did}</foreach> </script>)")
    int deleteVoteDepartment(List<Integer> ids);
    @Select("<script> Select * from 202303_wt_vote_department "+
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteDepartment> selectVoteDepartment(Map<String,String> qy);
    @Update("update 202303_wt_vote_code set status=#{status} where did=#{did}")
    int updateVoteDepartment(VoteDepartment voteDepartment);
    //岗位管理
    @Insert("insert into 202303_wt_vote_rank (name,weight,createdDate)values(#{name},#{weight},#{createdDate})")
    int insertVoteRank(VoteRank voteRank);
    @Delete("<script> delete from 202303_wt_vote_rank where rid in <foreach collection='ids' item='rid' open='(' separator=',' close=')'>#{rid}</foreach> </script>)")
    int deleteVoteRank(String ids);
    @Select("<script> Select * from 202303_wt_vote_rank "+
            "<if test='rid != null and rid !=\"\"'> and rid = #{rid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteRank> selectVoteRank(Map<String,String> qy);
    @Update("update 202303_wt_vote_code set status=#{status},weight=#{weight} where rid=#{rid}")
    int updateVoteRank(VoteRank voteRank);
    //人员管理
    @Insert("insert into 202303_wt_vote_personnel (did,did,name,mobile,createdDate)values(#{did},#{did},#{name},#{mobile},#{createdDate})")
    int insertVotePersonnel(VotePersonnel votePersonnel);
    @Delete("<script> delete from 202303_wt_vote_personnel where pid in <foreach collection='ids' item='pid' open='(' separator=',' close=')'>#{pid}</foreach> </script>)")
    int deleteVotePersonnel(List<Integer> ids);
    @Select("<script> Select * from 202303_wt_vote_personnel "+
            "<if test='pid != null and pid !=\"\"'> and pid = #{pid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "<if test='mobile != null and mobile !=\"\"'> and mobile = #{mobile} </if>" +
            "</script>")
    List<VotePersonnel> selectVotePersonnel(Map<String,String> qy);
    @Update("update 202303_wt_vote_code set did=#{did},rid=#{rid},name=#{name},mobile=#{mobile} where pid=#{pid}")
    int updateVotePersonnel(VotePersonnel votePersonnel);
    //分类管理
    @Insert("insert into 202303_wt_vote_classify (name,createdDate)values(#{name},#{createdDate})")
    int insertVoteClassify(VoteClassify voteClassify);
    @Delete("<script> delete from 202303_wt_vote_classify where cid in <foreach collection='ids' item='cid' open='(' separator=',' close=')'>#{cid}</foreach> </script>)")
    int deleteVoteClassify(List<Integer> ids);
    @Select("<script> Select * from 202303_wt_vote_classify "+
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassify> selectVoteClassify(Map<String,String> qy);
    @Update("update 202303_wt_vote_classify set name=#{name},status=#{status} where cid=#{cid}")
    int updateVoteClassify(VoteClassify voteClassify);
    //分类内容管理
    @Insert("insert into 202303_wt_vote_classifyInfo (cid,name,bgUrl,status,startTime,endTime)values(#{cid},#{name},#{bgUrl},#{status},#{startTime},#{endTime})")
    int insertVoteClassifyInfo(VoteClassifyInfo voteClassifyInfo);
    @Insert("insert into 202303_wt_vote_ci_pe_co (iid,pid,yid)values(#{iid},#{pid},#{yid})")
    int insertVoteClassifyInfoPersonnelCode(VoteClassifyInfoPersonnelCode voteClassifyInfoPersonnelCode);
    @Delete("<script> delete from 202303_wt_vote_classifyInfo where iid in <foreach collection='ids' item='iid' open='(' separator=',' close=')'>#{iid}</foreach> </script>)")
    int deleteVoteClassifyInfo(List<Integer> ids);
    @Select("<script> Select * from 202303_wt_vote_classifyInfo "+
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='iid != null and iid !=\"\"'> and iid = #{iid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassifyInfo> selectVoteClassifyInfo(Map<String,String> qy);
    @Update("update 202303_wt_vote_classifyInfo set cid=#{cid},name=#{name},bgUrl=#{bgUrl},status=#{status},startTime=#{startTime},endTime=#{endTime} where cid=#{cid}")
    int updateVoteClassifyInfo(VoteClassifyInfo voteClassifyInfo);
    //题库管理
    @Insert("insert into 202303_wt_vote_topic (title,options,score)values(#{title},#{options},#{score})")
    int insertVoteTopic(VoteTopic voteTopic);
    @Insert("insert into 202303_wt_vote_topic_rank(rid,tid)values(#{rid},#{tid})")
    int insertVoteTopicRank(VoteTopicRank voteTopicRank);
    @Insert("insert into 202303_wt_vote_department_topic (did,tid)values(#{did},#{tid})")
    int insertVoteDepartmentTopic(VoteDepartmentTopic voteDepartmentTopic);
    @Delete("<script> delete from 202303_wt_vote_department_topic where tid in <foreach collection='ids' item='tid' open='(' separator=',' close=')'>#{tid}</foreach> </script>)")
    int deleteVoteTopic(List<Integer> ids);
    @Select("<script> Select * from 202303_wt_vote_topic "+
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='iid != null and iid !=\"\"'> and iid = #{iid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteTopic> selectVoteTopic(Map<String,String> qy);
    @Update("update 202303_wt_vote_topic set title=#{title},options=#{options},score=#{score},status=#{status} where tid=#{tid}")
    int updateVoteTopic(VoteTopic voteClassifyInfo);
    @Delete("<script> delete from 202303_wt_vote_topic_rank where tid =#{tid} and " +
            "rid in <foreach collection='rids' item='rid' open='(' separator=',' close=')'>#{rid}</foreach></script>)")
    int deleteVoteTopicRank(String tid,List<Integer> rids);
    @Delete("<script> delete from 202303_wt_vote_department_topic where tid =#{tid} and " +
            "did in <foreach collection='rids' item='did' open='(' separator=',' close=')'>#{did}</foreach></script>)")
    int deleteVoteDepartmentTopic(String tid,List<Integer> rids);
    //登录码管理
    @Insert("insert into 202303_wt_vote_code(did,loginCode,createDate)values(#{did},#{loginCode},#{createDate})")
    int insertVoteCode(VoteCode voteCode);
    @Delete("<script> delete from 202303_wt_vote_code where yid in <foreach collection='ids' item='yid' open='(' separator=',' close=')'>#{yid}</foreach> </script>)")
    int deleteVoteCode(List<Integer> ids);
    @Select("<script>select * from 202303_wt_vote_code "+
            "<if test='yid != null and yid !=\"\"'> and yid = #{yid} </if>" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='loginCode != null and loginCode !=\"\"'> and loginCode = #{loginCode} </if>" +
            "</script>")
    List<VoteCode> selectVoteCode(Map<String,String> qy);
    @Update("update 202303_wt_vote_code set status=#{status} where yid=#{yid}")
    int updateVoteCode(VoteCode voteCode);
    //投票清单查询
    List<VoteLog> selectVoteLog(Map<String,String> qy);
    List<VoteResult> selectVoteResult(Map<String,String> qy);
    *//**
     * 获取类内容下用户对应编码
     *//*
    @Select({"<script> select co.loginCode" +
            " from 202303_wt_vote_personnel pe " +
            " left join 202303_wt_vote_ci_pe_co ci on pe.pid=ci.pid " +
            " left 202303_wt_vote_code co on co.yid=ci.yid" +
            " where pe.mobile =#{mobile} " +
            " and ci.iid = #{iid}" +
            "</script>"
    })
    String selectCode(@Param("mobile")String mobile,@Param("iid")String iid);*/
}
