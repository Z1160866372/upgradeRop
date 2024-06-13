/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/30 14:42
 */
@Repository
@Mapper
public interface FileListMapper {

    @Select({"<script> select * from activity_fileList where 1=1 " +
            " <if test=\"principal != null and principal != ''\">  " +
            " and principal = #{principal} " +
            " </if>" +
            "</script>"
    })
    List<ActivityFileList> selectActivityFileList(String principal);

    @Insert("insert into activity_fileList(name,fileName,number,principal,createTime)values(#{name},#{fileName},#{number},#{principal},now())")
    int insertActivityFileList(ActivityFileList fileList);//初始化用户

    @Insert({"<script> insert into ${tableName} (userId,userType) values " +
            " <foreach item=\"item\" index=\"index\" collection=\"oneList\" separator=\",\">\n" +
            " (#{item},#{userType})\n" +
            " </foreach>  " +
            "</script>"
    })
    Integer batchDataList(@Param("tableName") String tableName, @Param("oneList") List<String> dataOneList,@Param("userType") Integer userType);
}
