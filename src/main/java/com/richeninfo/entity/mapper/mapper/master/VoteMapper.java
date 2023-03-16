package com.richeninfo.entity.mapper.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * 查询分类
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_classify where 1=1"+
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassify> selectVoteClassify(Map<String,String> qy);//获取页面分类内容

    /**
     * 查询分类内容
     * @param qy
     * @return
     */
    @Select("<script> Select * from 202303_wt_vote_classifyInfo where 1=1"+
            "<if test='cid != null and cid !=\"\"'> and cid = #{cid} </if>" +
            "<if test='iid != null and iid !=\"\"'> and iid = #{iid} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteClassifyInfo> selectVoteClassifyInfo(Map<String,String> qy);//获取页面分类内容
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
    VoteCode selectCode(@Param("mobile")String mobile,@Param("iid")int iid);//获取用户类别下编码

    /**
     * 查询角色下对应题库
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
    List<VoteTopic> selectTopicList(@Param("mobile")String mobile,@Param("did")int did,@Param("iid")int iid);

    /**
     * 查询用户所属部门
     * @param mobile
     * @return
     */
    @Select("select did from 202303_wt_vote_personnel where mobile=#{mobile}")
    int selectDid(@Param("mobile")String mobile);

    /**
     * 查询用户某道题的答题记录
     * @param yid
     * @param did
     * @param tid
     * @return
     */
    @Select("SELECT * from 202303_wt_vote_log where yid=#{yid} and did=#{did} and tid=#{tid}")
    VoteLog selectLog(@Param("yid")String yid,@Param("did")int did,@Param("tid")int tid);

    /**
     * 查询当前分类下当前部门是否已答题
     * @param yid
     * @param did
     * @param iid
     * @return
     */
    @Select("SELECT count(*) from 202303_wt_vote_log where yid=#{yid} and did=#{did} and iid=#{iid}")
    int selectLogByYaD(@Param("yid")int yid,@Param("did")int did,@Param("iid")int iid);

    /**
     * 保存答题记录
     * @param log
     */
    @Insert("insert into 202303_wt_vote_log (yid,tid,did,iid,score,content,createdDate)values(#{yid},#{tid},#{did},#{iid},#{score},#{content},#{createdDate})")
    void insertVoteLog(VoteLog log);//保存单部门单道题的记录

    /**
     * 保存打分记录
     * @param result
     */
    @Insert("insert into 202303_wt_vote_result(yid,iid,did,score,createdDate)values(#{yid},#{iid},#{did},#{score},#{createdDate})")
    void insertVoteResult(VoteResult result);//保存单部门总记录

    /**
     * 查询所有部门
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_department where did>0"+
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "<if test='name != null and name !=\"\"'> and name = #{name} </if>" +
            "</script>")
    List<VoteDepartment> selectVoteDepartment(Map<String,String> qy);

    /**
     * 查询部门下loginCode
     * @param did
     * @return
     */
    @Select("<script> select * from 202303_wt_vote_code where 1=1"+
            "<if test='did != null and did !=\"\"'> and did = #{did} </if>" +
            "</script>")
    List<VoteCode> selectVoteCode(@Param("did")int did);

    /**
     * 查询选取类题目
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
    List<VoteTopic> selectTopicByDid(@Param("did")int did,@Param("iid")int iid);

    /**
     * 查询答题日志
     * @param qy
     * @return
     */
    List<VoteLog> selectLog(Map<String,String> qy);

    /**
     * 查询评分结果
     * @param qy
     * @return
     */
    List<VoteResult> selectResult(Map<String,String> qy);

    /**
     * 批量插入
     * @param code
     * @return
     */
    @Insert("insert into 202303_wt_vote_code (did,loginCode,createDate)values(#{did},#{loginCode},#{createDate})")
    int insertVoteCode(VoteCode code);
    @Select({"<script> SELECT SUM(vr.score) totalScore,COUNT(*) number,SUM(vr.score)/COUNT(*) averageScore,vd.name name " +
            " from 202303_wt_vote_result vr " +
            " LEFT JOIN 202303_wt_vote_ci_pe_co vpc on vr.yid=vpc.yid" +
            " LEFT JOIN 202303_wt_vote_personnel vp on vp.pid=vpc.pid" +
            " LEFT JOIN 202303_wt_vote_code vc on vr.yid=vc.yid" +
            " LEFT JOIN 202303_wt_vote_department vd on vr.did=vd.did" +
            " where vc.did=#{did}" +
            "<if test='rid != null and rid !=\"\"'> and vp.rid = #{rid} </if>" +
            " GROUP BY vc.did,vr.did" +
            "</script>"
    })
    List<VoteList> selectVoteList(Map<String,String> qy);
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
    List<VoteList> selectVoteListBy(Map<String,String> qy);

}
