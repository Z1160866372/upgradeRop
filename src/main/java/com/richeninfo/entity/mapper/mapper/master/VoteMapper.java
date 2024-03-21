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
public interface VoteMapper {
    /**
     * 获取类内容下用户对应编码
     */
    @Select({"<script> select co.loginCode,co.yid" +
            " from 202303_wt_vote_personnel pe " +
            " left join 202303_wt_vote_ci_pe_co ci on pe.pid=ci.pid " +
            " left join 202303_wt_vote_code co on co.yid=ci.yid" +
            " where ci.iid=#{iid}" +
            " and pe.mobile =#{mobile}" +
            "</script>"
    })
    VoteCode selectCode(@Param("mobile") String mobile, @Param("iid") int iid);//获取用户类别下编码

    /**
     * 查询角色下对应题库
     *
     * @param mobile
     * @param did
     * @param iid
     * @return
     */
    @Select({"<script> select *" +
            " from 202303_wt_vote_topic_department td " +
            " LEFT JOIN 202303_wt_vote_topic tt on tt.tid=td.tid" +
            " LEFT JOIN 202303_wt_vote_topic_rank tr on tt.tid=tr.tid" +
            " LEFT JOIN 202303_wt_vote_personnel pe on pe.rid=tr.rid" +
            " where pe.mobile =#{mobile} " +
            " and tr.tid=td.tid" +
            " and tt.iid=#{iid}" +
            " and td.did=#{did} ORDER BY td.tid ASC" +
            "</script>"
    })
    List<VoteTopic> selectTopicList(@Param("mobile") String mobile, @Param("did") int did, @Param("iid") int iid);

    /**
     * 查询用户所属部门
     *
     * @param mobile
     * @return
     */
    @Select("select did from 202303_wt_vote_personnel where mobile=#{mobile}")
    int selectDid(@Param("mobile") String mobile);

    /**
     * 查询用户某道题的答题记录
     *
     * @param yid
     * @param did
     * @param tid
     * @return
     */
    @Select("SELECT * from 202303_wt_vote_log where yid=#{yid} and did=#{did} and tid=#{tid}")
    VoteLog selectLog(@Param("yid") String yid, @Param("did") int did, @Param("tid") int tid);

    /**
     * 查询当前分类下当前部门是否已答题
     *
     * @param yid
     * @param did
     * @param iid
     * @return
     */
    @Select("SELECT count(*) from 202303_wt_vote_log where yid=#{yid} and did=#{did} and iid=#{iid}")
    int selectLogByYaD(@Param("yid") int yid, @Param("did") int did, @Param("iid") int iid);

    /**
     * 查询当前分类下当前部门是否已答题
     *
     * @param yid
     * @param did
     * @param iid
     * @return
     */
    @Select("SELECT count(*) from 202303_wt_vote_log where yid=#{yid} and did=#{did} and iid=#{iid} and tid=#{tid}")
    int selectLogByYaDAnT(@Param("yid") int yid, @Param("did") int did, @Param("iid") int iid, @Param("tid") int tid);

    /**
     * 保存答题记录
     *
     * @param log
     */
    @Insert("insert into 202303_wt_vote_log (yid,tid,did,iid,score,content,createdDate)values(#{yid},#{tid},#{did},#{iid},#{score},#{content},#{createdDate})")
    void insertVoteLog(VoteLog log);//保存单部门单道题的记录

    /**
     * 保存打分记录
     *
     * @param result
     */
    @Insert("insert into 202303_wt_vote_result(yid,iid,did,score,createdDate)values(#{yid},#{iid},#{did},#{score},#{createdDate})")
    void insertVoteResult(VoteResult result);//保存单部门总记录

    /**
     * 查询部门下loginCode
     *
     * @param did
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_code where 1=1" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "</script>")
    List<VoteCode> selectVoteCode(@Param("did") int did);

    /**
     * 查询选取类题目
     *
     * @param did
     * @param iid
     * @return
     */
    @Select({"<script> SELECT * " +
            " from 202303_wt_vote_topic_department td " +
            " LEFT JOIN 202303_wt_vote_topic tt on tt.tid=td.tid" +
            " where td.did=#{did}" +
            " and tt.iid=#{iid}" +
            "</script>"
    })
    List<VoteTopic> selectTopicByDid(@Param("did") int did, @Param("iid") int iid);

    /**
     * 查询答题日志
     *
     * @param qy
     * @return
     */
    List<VoteLog> selectLog(Map<String, String> qy);

    /**
     * 查询评分结果
     *
     * @param qy
     * @return
     */
    List<VoteResult> selectResult(Map<String, String> qy);

    /**
     * 查询员工和经理的评分
     *
     * @param qy
     * @return
     */
    @Select({"<script> SELECT SUM(vr.score) totalScore,(SELECT MIN(score) FROM 202303_wt_vote_log vl where vr.did=vl.did and vr.yid=vl.yid) minScore,(SELECT MAX(score) FROM 202303_wt_vote_log vl where vr.did=vl.did and vr.yid=vl.yid) maxScore,COUNT(*) number,SUM(vr.score)/COUNT(*) averageScore,vd.name name " +
            " from 202303_wt_vote_result vr " +
            " LEFT JOIN 202303_wt_vote_ci_pe_co vpc on vr.yid=vpc.yid" +
            " LEFT JOIN 202303_wt_vote_personnel vp on vp.pid=vpc.pid" +
            " LEFT JOIN 202303_wt_vote_code vc on vr.yid=vc.yid" +
            " LEFT JOIN 202303_wt_vote_department vd on vr.did=vd.did" +
            " where vr.iid=1 and vc.did=#{did}" +
            "<if test='rid != null and rid !=\"\"'> and vp.rid = #{rid} </if>" +
            " GROUP BY vc.did,vr.did" +
            "</script>"
    })
    List<VoteList> selectVoteList(Map<String, String> qy);

    /**
     * 查询分管领导和总经理的打分记录
     *
     * @param qy
     * @return
     */
    @Select({"<script> SELECT SUM(vr.score) totalScore,COUNT(*) number,SUM(vr.score)/COUNT(*) averageScore,vd.name name " +
            " from 202303_wt_vote_result vr " +
            " LEFT JOIN 202303_wt_vote_ci_pe_co vpc on vr.yid=vpc.yid" +
            " LEFT JOIN 202303_wt_vote_personnel vp on vp.pid=vpc.pid" +
            " LEFT JOIN 202303_wt_vote_code vc on vr.yid=vc.yid" +
            " LEFT JOIN 202303_wt_vote_department vd on vr.did=vd.did" +
            " where vc.loginCode=#{loginCode}" +
            "<if test='rid != null and rid !=\"\"'> and vp.rid = #{rid} </if>" +
            " GROUP BY vc.did,vr.did,vr.yid" +
            "</script>"
    })
    List<VoteList> selectVoteListBy(Map<String, String> qy);
    //部门管理

    /**
     * 查询所有部门
     *
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_department where did>0" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteDepartment> selectVoteDepartment(Map<String, String> qy);

    /**
     * 新增部门
     *
     * @param voteDepartment
     * @return
     */
    @Insert("insert into 202303_wt_vote_department (name,createdDate)values(#{name},#{createdDate})")
    int insertVoteDepartment(VoteDepartment voteDepartment);

    /**
     * 更新部门内容
     *
     * @param voteDepartment
     * @return
     */
    @Update("update 202303_wt_vote_department set name=#{name},status=#{status} where rid=#{rid}")
    int updateVoteDepartment(VoteDepartment voteDepartment);
    //岗位管理

    /**
     * 查询所有岗位
     *
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_rank where 1=1" +
            "<if test='rid != null and rid !=\"\"'> and rid = #{did} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteRank> selectVoteRank(Map<String, String> qy);

    /**
     * 新增岗位
     *
     * @param voteRank
     * @return
     */
    @Insert("insert into 202303_wt_vote_rank (name,createdDate)values(#{name},#{createdDate})")
    int insertVoteRank(VoteRank voteRank);

    /**
     * 更新岗位内容
     *
     * @param voteRank
     * @return
     */
    @Update("update 202303_wt_vote_rank set name=#{name},status=#{status} where did=#{did}")
    int updateVoteRank(VoteRank voteRank);
    //登录码管理

    /**
     * 查询所有登录码
     *
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_code where 1=1" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='loginCode != null and loginCode !=\"\"'> and loginCode = #{loginCode} </if>" +
            "</script>")
    List<VoteCode> selectVoteCodes(Map<String, String> qy);

    /**
     * 新增登录码
     *
     * @param voteCode
     * @return
     */
    @Insert("insert into 202303_wt_vote_code (did,loginCode,createDate)values(#{did},#{loginCode},#{createDate})")
    int insertVoteCode(VoteCode voteCode);

    /**
     * 更新登录码内容
     *
     * @param voteCode
     * @return
     */
    @Update("update 202303_wt_vote_code set did=#{did},loginCode=#{loginCode},status=#{status} where yid=#{yid}")
    int updateVoteCode(VoteCode voteCode);
    //人员管理

    /**
     * 查询所有人员
     *
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_personnel where 1=1" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='rid != null and rid !=\"\"'> and rid = #{rid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "<if test='mobile != null and mobile !=\"\"'> and mobile = #{mobile} </if>" +
            "</script>")
    List<VotePersonnel> selectVotePersonnel(Map<String, String> qy);

    /**
     * 新增人员
     *
     * @param votePersonnel
     * @return
     */
    @Insert("insert into 202303_wt_vote_personnel (did,rid,name,#{mobile},createDate)values(#{did},#{rid},#{name},#{mobile},#{createDate})")
    int insertVotePersonnel(VotePersonnel votePersonnel);

    /**
     * 更新人员信息
     *
     * @param votePersonnel
     * @return
     */
    @Update("update 202303_wt_vote_personnel set did=#{did},rid=#{rid},name=#{name},mobile=#{mobile} where pid=#{pid}")
    int updateVotePersonnel(VotePersonnel votePersonnel);
    //分类管理

    /**
     * 查询分类
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_classify where 1=1" +
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassify> selectVoteClassify(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增分类
     *
     * @param voteClassify
     * @return
     */
    @Insert("insert into 202303_wt_vote_classify (name,createDate)values(#{name},#{createDate})")
    int insertVoteClassify(VoteClassify voteClassify);

    /**
     * 更新分类内容
     *
     * @param voteClassify
     * @return
     */
    @Update("update 202303_wt_vote_classify set name=#{name},status=#{status} where cid=#{cid}")
    int updateVoteClassify(VoteClassify voteClassify);
    //分类内容管理

    /**
     * 查询分类内容
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_classifyInfo where 1=1" +
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassifyInfo> selectVoteClassifyInfo(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增分类
     *
     * @param voteClassify
     * @return
     */
    @Insert("insert into 202303_wt_vote_classifyInfo (cid,name,bgUrl,startTime,endTime)values(#{cid},#{name},#{bgUrl},#{startTime},#{endTime})")
    int insertVoteClassifyInfo(VoteClassifyInfo voteClassify);

    /**
     * 更新分类内容
     *
     * @param voteClassify
     * @return
     */
    @Update("update 202303_wt_vote_classifyInfo set cid=#{cid},name=#{name},bgUrl=#{bgUrl},status=#{status},startTime=#{startTime},endTime=#{endTime} where cid=#{cid}")
    int updateVoteClassifyInfo(VoteClassifyInfo voteClassify);
    //题库管理

    /**
     * 查询题库
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_topic where 1=1" +
            "<if test='iid != null and iid !=\"\"'> and iid = #{iid} </if>" +
            "<if test='title != null and title !=\"\"'> and title = #{title} </if>" +
            "</script>")
    List<VoteTopic> selectVoteTopic(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增题
     *
     * @param voteTopic
     * @return
     */
    @Insert("insert into 202303_wt_vote_topic (iid,title,options,score,createdDate)values(#{iid},#{title},#{options},#{score},#{createdDate})")
    int insertVoteTopic(VoteTopic voteTopic);

    /**
     * 更新分类内容
     *
     * @param voteTopic
     * @return
     */
    @Update("update 202303_wt_vote_topic set iid=#{iid},title=#{title},options=#{options},status=#{status},score=#{score} where tid=#{tid}")
    int updateVoteTopic(VoteTopic voteTopic);
    //类内容-白名单员工-分配登录码

    /**
     * 查询类内容-员工-登录码内容
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_ci_pe_co where 1=1" +
            "<if test='iid != null and iid !=\"\"'> and iid = #{iid} </if>" +
            "<if test='pid != null and pid !=\"\"'> and pid = #{pid} </if>" +
            "<if test='yid != null and yid !=\"\"'> and yid = #{yid} </if>" +
            "</script>")
    List<VoteClassifyInfoPersonnelCode> selectVoteClassifyInfoPersonnelCode(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增关联关系
     *
     * @param voteClassifyInfoPersonnelCode
     * @return
     */
    @Insert("insert into 202303_wt_vote_ci_pe_co (iid,pid,yid)values(#{iid},#{pid},#{yid})")
    int insertVoteClassifyInfoPersonnelCode(VoteClassifyInfoPersonnelCode voteClassifyInfoPersonnelCode);

    /**
     * 更新关联关系
     *
     * @param voteClassifyInfoPersonnelCode
     * @return
     */
    @Update("update 202303_wt_vote_ci_pe_co set pid=#{pid},yid=#{yid} where iid=#{iid}")
    int updateVoteClassifyInfoPersonnelCode(VoteClassifyInfoPersonnelCode voteClassifyInfoPersonnelCode);
    //题-角色

    /**
     * 查询题-角色
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_topic_rank where 1=1" +
            "<if test='tid != null and tid !=\"\"'> and tid = #{tid} </if>" +
            "<if test='rid != null and rid !=\"\"'> and rid = #{rid} </if>" +
            "</script>")
    List<VoteTopicRank> selectVoteTopicRank(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增关联关系
     *
     * @param voteTopicRank
     * @return
     */
    @Insert("insert into 202303_wt_vote_topic_rank (tid,rid)values(#{tid},#{rid})")
    int insertVoteTopicRank(VoteTopicRank voteTopicRank);

    /**
     * 更新关联关系
     *
     * @param voteTopicRank
     * @return
     */
    @Update("update 202303_wt_vote_topic_rank set rid=#{rid} where tid=#{tid}")
    int updateVoteTopicRank(VoteTopicRank voteTopicRank);
    //题-部门

    /**
     * 查询题-部门
     *
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_topic_rank where 1=1" +
            "<if test='tid != null and tid !=\"\"'> and tid = #{tid} </if>" +
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "</script>")
    List<VoteDepartmentTopic> selectVoteDepartmentTopic(Map<String, String> qy);//获取页面分类内容

    /**
     * 新增关联关系
     *
     * @param voteDepartmentTopic
     * @return
     */
    @Insert("insert into 202303_wt_vote_topic_rank (tid,did)values(#{tid},#{did})")
//    int insertVoteDepartmentTopic(VoteDepar stmentTopic voteDepartmentTopic);

    /**
     * 更新关联关系
     *
     * @param voteDepartmentTopic
     * @return
     */
    @Update("update 202303_wt_vote_topic_rank set did=#{did} where tid=#{tid}")
    int updateVoteDepartmentTopic(VoteDepartmentTopic voteDepartmentTopic);
}
